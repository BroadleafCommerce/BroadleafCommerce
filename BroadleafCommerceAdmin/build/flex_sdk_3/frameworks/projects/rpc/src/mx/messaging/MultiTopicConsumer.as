////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging
{

import flash.events.TimerEvent;
import flash.utils.Timer;
import mx.core.mx_internal;
import mx.collections.ArrayCollection;
import mx.events.PropertyChangeEvent;
import mx.events.CollectionEvent;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.events.MessageEvent;
import mx.messaging.errors.MessagingError;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.IMessage;

use namespace mx_internal;

/**
 *  Dispatched when a message is received by the Consumer.
 *
 *  @eventType mx.messaging.events.MessageEvent.MESSAGE
 */
[Event(name="message", type="mx.messaging.events.MessageEvent")]

/**
 *  Like a Consumer, a MultiTopicConsumer subscribes to a destination with a single
 *  clientId and delivers messages to a single event handler.  Unlike a Consumer
 *  it lets you register subscriptions for a list of subtopics and selector expressions
 *  at the same time from a single message handler.  Where Consumer has subtopic and selector properties,
 *  this component has an addSubscription(subtopic, selector) method you use to 
 *  add a new subscription to the existing set of subscriptions.  Alternatively, you can
 *  populate the subscriptions property with a list of SubscriptionInfo instances that
 *  define the subscriptions for this destination.
 *  <p>
 *  Like the regular Consumer, the MultiTopicConsumer sends subscribe and unsubscribe 
 *  messages which generate a MessageAckEvent or MessageFaultEvent depending upon whether the 
 *  operation was successful or not.
 *  Once subscribed, a MultiTopicConsumer dispatches a MessageEvent for each message it receives.</p>
 *  @mxml
 *  <p>
 *  The &lt;mx:MultiTopicConsumer&gt; tag has these properties:
 *  </p>
 *  <pre>
 *   &lt;mx:Consumer
 *    <b>Properties</b>
 *    subscriptions="<i>"an empty ArrayCollection of SubscriptionInfo objects</i>"
 *    resubscribeAttempts="<i>5</i>"
 *    resubscribeInterval="<i>5000</i>"
 *    timestamp="<i>No default.</i>"
 *  /&gt;
 *  </pre> 
 */
public class MultiTopicConsumer extends AbstractConsumer
{
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructs a MultiTopicConsumer.
     * 
     * 
     *  @example
     *  <listing version="3.0">
     *   function initConsumer():void
     *   {
     *       var consumer:Consumer = new MultiTopicConsumer();
     *       consumer.destination = "NASDAQ";
     *       consumer.addEventListener(MessageEvent.MESSAGE, messageHandler);
     *       consumer.addSubscription("myStock1", "operation IN ('BID', 'Ask')");
     *       consumer.addSubscription("myStock2", "operation IN ('BID', 'Ask')");
     *       consumer.subscribe();
     *   }
     *
     *   function messageHandler(event:MessageEvent):void
     *   {
     *       var msg:IMessage = event.message;
     *       var info:Object = msg.body;
     *       trace("-App recieved message: " + msg.toString());
     *   }
     *   </listing>
     */
    public function MultiTopicConsumer()
    {
        super();

        _subscriptions.addEventListener(CollectionEvent.COLLECTION_CHANGE, subscriptionsChangeHandler);
    }
    

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _subscriptions:ArrayCollection = new ArrayCollection();

    /** 
     * This is a map where the keys are string names of the form subtopic + separator + selector
     * registered with the boolean true.  When we generate a subscription message and
     * send it to the server, we add/remove those subscriptions from this list.  Thus this
     * list tracks the subscriptions we have sent to the server.
     */
    private var _currentSubscriptions:Object = {};

    /** 
     * Used when the subscriptions property changes so we batch all changes made in one
     * frame into a single multi-subscription message
     */
    private var _subchangeTimer:Timer = null;

    [Bindable(event="propertyChange")]

    [Inspectable(category="General", verbose="1")]
    /**
     *  Stores an Array of SubscriptionInfo objects.  Each subscription
     *  contains a subtopic and a selector each of which can be null.
     *  A subscription with a non-null subtopic restricts the subscription
     *  to messages delivered with only that subtopic.
     *  If a subtopic is null, it uses the selector with no subtopic.
     *  If the selector and the subtopic is null, the subscription receives
     *  any messages targeted at the destination with no subtopic.
     *  The subtopic can contain a wildcard specification.
     * 
     *  <p>Before a call to the <code>subscribe()</code> method, this property 
     *  can be set with no side effects. 
     *  After the MultiTopicConsumer has subscribed to its destination, changing this 
     *  value has the side effect of updating the MultiTopicConsumer's subscription to 
     *  include any new subscriptions and remove any subscriptions you deleted from
     *  the ArrayCollection.</p>
     * 
     *  <p>The remote destination must understand the value of the selector 
     *  expression.</p>
     */ 
    public function get subscriptions():ArrayCollection
    {
        return _subscriptions;
    }

    /**
     * Provide a new subscriptions array collection.  This should be an ArrayCollection
     * containing SubscriptionInfo instances which define message topics and selectors
     * you want received by this consumer.
     */
    public function set subscriptions(value:ArrayCollection):void
    {
        if (_subscriptions !== value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "subscriptions", _subscriptions, value);

            if (subscribed)
            {
                unsubscribe();
                _shouldBeSubscribed = true;
            }

            if (_subscriptions != null)
                _subscriptions.removeEventListener(CollectionEvent.COLLECTION_CHANGE, subscriptionsChangeHandler);

            _subscriptions = value;

            if (_subscriptions != null)
                _subscriptions.addEventListener(CollectionEvent.COLLECTION_CHANGE, subscriptionsChangeHandler);

            // Update an existing subscription to use the new selector.
            if (_shouldBeSubscribed)
                subscribe(clientId);

            dispatchEvent(event);
        }
    }

    /**
     * This is a convenience method for adding a new subscription.  It just creates
     * a new SubscriptionInfo object and adds it to the subscriptions property.
     * To call this method, you provide the
     * subtopic and selector string for the new subscription.  If the subtopic is null,
     * the subscription applies to messages which do not have a subtopic set in the
     * producer.  If the selector string is null, all messages sent which match the
     * subtopic string are received by this consumer.  
     *
     * @param subtopic The subtopic for the new subscription.
     *
     * @param selector The selector for the new subscription.
     */
    public function addSubscription(subtopic:String = null, selector:String = null):void
    {
        subscriptions.addItem(new SubscriptionInfo(subtopic, selector));
    }

    /**
     * This method removes the subscription specified by the subtopic
     * and selector.
     *
     * @param subtopic The subtopic for the subscription.
     *
     * @param selector The selector for the subscription.
     */
    public function removeSubscription(subtopic:String = null, selector:String = null):void
    {
        var n:int = subscriptions.length;
        for (var i:int = 0; i < n; ++i)
        {
            var si:SubscriptionInfo = SubscriptionInfo(subscriptions.getItemAt(i));
            if (si.subtopic == subtopic && si.selector == selector)
            {
                subscriptions.removeItemAt(i);
                break;
            }
        }
        
        if (n == subscriptions.length)
            throw new MessagingError("Attempt to remove a subscription with subtopic: " + 
                subtopic + " and selector: " + selector + " that this consumer does not have");
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Returns a subscribe message.
     *
     * @return The subscribe CommandMessage.
     */
    override protected function buildSubscribeMessage():CommandMessage
    {
        var msg:CommandMessage = super.buildSubscribeMessage();
        msg.operation = CommandMessage.MULTI_SUBSCRIBE_OPERATION;

        var subs:Object = getCurrentSubscriptions();
        var toAdd:Array = [];
        var toRemove:Array = [];

        for (var s:String in subs)
        {
            if (_currentSubscriptions[s] == null)
                toAdd.push(s);
        }
        for (s in _currentSubscriptions)
        {
            if (subs[s] == null)
                toRemove.push(s);
        }

        if (toAdd.length > 0)
            msg.headers[CommandMessage.ADD_SUBSCRIPTIONS] = toAdd;
        if (toRemove.length > 0)
            msg.headers[CommandMessage.REMOVE_SUBSCRIPTIONS] = toRemove;

        _currentSubscriptions = subs;

        // Tell the ack handler to mark this guy as unsubscribed after the request
        if (_currentSubscriptions.length == 0)
            msg.headers.DSlastUnsub = true;

        return msg;
    }

    /**
     * Returns an unsubscribe mesage.
     *
     * @param preserveDurable When true, durable JMS subscriptions are
     * not destroyed, allowing consumers to later resubscribe and
     * receive missed messages.
     *
     * @return The unsubscribe CommandMessage.
     */
    override protected function buildUnsubscribeMessage(preserveDurable:Boolean):CommandMessage
    {
        var msg:CommandMessage = super.buildUnsubscribeMessage(preserveDurable);
        msg.operation = CommandMessage.MULTI_SUBSCRIBE_OPERATION;

        var toRemove:Array = [];

        for (var s:String in _currentSubscriptions)
        {
            toRemove.push(s);
        }
        _currentSubscriptions = {};

        if (toRemove.length > 0)
            msg.headers[CommandMessage.REMOVE_SUBSCRIPTIONS] = toRemove;

        msg.headers.DSlastUnsub = true;

        return msg;
    }

    /**
     * @private
     */
    override protected function internalSend(message:IMessage, waitForClientId:Boolean = true):void
    {
        // If there is nothing to do with this message, do not send it - instead throw
        // an exception as this was an attempt to subscribe or unsubscribe with no
        // subscriptions.
        if (message.headers[CommandMessage.ADD_SUBSCRIPTIONS] != null ||
            message.headers[CommandMessage.REMOVE_SUBSCRIPTIONS] != null)
            super.internalSend(message, waitForClientId);
        else
        {
            if (channelSet == null)
                initChannelSet(message);
            // If we are disconnected and are trying to subscribe, we still need
            // to send the message to force a reconnect
            if (channelSet != null && !channelSet.connected && 
                message is CommandMessage &&
                CommandMessage(message).operation == CommandMessage.MULTI_SUBSCRIBE_OPERATION)
                super.internalSend(message, waitForClientId);
            if (message.headers.DSlastUnsub != null)
                setSubscribed(false);
            // In this case, if we are not sending the message we can just say we are 
            // subscribed but only if we are connected
            else if (channelSet != null && channelSet.connected)
                setSubscribed(true);
        }
    }
    /**
     * @private
     */
    override protected function setSubscribed(value:Boolean):void
    {
        /* 
         * Whenenver we get marked as being unsubscribed (i.e. the server is
         * disconnected), we clear out the subscriptions on the server.  The
         * client will then resubscribe and send all subscriptions on the next
         * connect.
         */
        if (!value)
            _currentSubscriptions = {};
        super.setSubscribed(value);
    }
            
    //--------------------------------------------------------------------------
    //
    // Private Methods
    // 
    //--------------------------------------------------------------------------

    private function getCurrentSubscriptions():Object
    {
        var subs:Object = {};

        for (var i:int = 0; i < subscriptions.length; i++)
        {
            var si:SubscriptionInfo = SubscriptionInfo(subscriptions.getItemAt(i));
            subs[(si.subtopic == null ? "" : si.subtopic) + 
                  CommandMessage.SUBTOPIC_SEPARATOR + 
                  (si.selector == null ? "" : si.selector)] = true;
        }
        return subs;
    }

    private function subscriptionsChangeHandler(event:CollectionEvent):void
    {
        // process the changes on the next frame to be sure we are done with
        // all subscriptions.  If we get a change event after we've subscribed
        // but before we've gotten the ack, _shouldBeSubscribed is true but
        // subscribed is only set when we have acked. 
        if ((_shouldBeSubscribed || subscribed) && _subchangeTimer == null)
        {
            _subchangeTimer = new Timer(0, 1);
            _subchangeTimer.addEventListener("timer", doResubscribe);
            _subchangeTimer.start();
        }
    }

    private function doResubscribe(event:TimerEvent):void
    {
        _subchangeTimer = null;

        if (subscribed) 
        {
            internalSend(buildSubscribeMessage());
        }
    }


}

}
