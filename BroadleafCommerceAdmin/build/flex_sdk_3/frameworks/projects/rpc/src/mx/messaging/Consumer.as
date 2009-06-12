////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
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
import mx.events.PropertyChangeEvent;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.config.ServerConfig;
import mx.messaging.errors.MessagingError;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;

use namespace mx_internal;

/**
 *  Dispatched when a message is received by the Consumer.
 *
 *  @eventType mx.messaging.events.MessageEvent.MESSAGE
 */
[Event(name="message", type="mx.messaging.events.MessageEvent")]

/**
 *  A Consumer subscribes to a destination to receive messages.
 *  Consumers send subscribe and unsubscribe messages which generate a MessageAckEvent
 *  or MessageFaultEvent depending upon whether the operation was successful or not.
 *  Once subscribed, a Consumer dispatches a MessageEvent for each message it receives.
 *  Consumers provide the ability to filter messages using a selector.
 *  These selectors must be understood by the destination.
 *  @mxml
 *  <p>
 *  The &lt;mx:Consumer&gt; tag inherits all the tag attributes of its superclass, and adds the following tag attributes:
 *  </p>
 *  <pre>
 *   &lt;mx:Consumer
 *    <b>Properties</b>
 *    resubscribeAttempts="<i>5</i>"
 *    resubscribeInterval="<i>5000</i>"
 *    selector="<i>No default.</i>"
 *    timestamp="<i>No default.</i>"
 *  /&gt;
 *  </pre> 
 */
public class Consumer extends AbstractConsumer
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructs a Consumer.
     * 
     *  @param messageType The alias for the message type processed by the service
     *                     hosting the remote destination the Consumer will subscribe to.
     *                     This parameter is deprecated and it is ignored by the
     *                     constructor.
     * 
     *  @example
     *  <listing version="3.0">
     *   function initConsumer():void
     *   {
     *       var consumer:Consumer = new Consumer();
     *       consumer.destination = "NASDAQ";
     *       consumer.selector = "operation IN ('Bid','Ask')";
     *       consumer.addEventListener(MessageEvent.MESSAGE, messageHandler);
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
	public function Consumer(messageType:String="flex.messaging.messages.AsyncMessage")
	{
		super();
	}
	

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  selector
	//----------------------------------

	/**
     *  @private
	 */
	private var _selector:String = "";

    [Bindable(event="propertyChange")]

    [Inspectable(category="General", verbose="1")]
	/**
	 *  The selector for the Consumer. 
	 *  This is an expression that is passed to the destination which uses it
	 *  to filter the messages delivered to the Consumer.
	 * 
	 *  <p>Before a call to the <code>subscribe()</code> method, this property 
	 *  can be set with no side effects. 
	 *  After the Consumer has subscribed to its destination, changing this 
	 *  value has the side effect of updating the Consumer's subscription to 
	 *  use the new selector expression immediately.</p>
	 * 
	 *  <p>The remote destination must understand the value of the selector 
	 *  expression.</p>
	 */	
	public function get selector():String
	{
		return _selector;
	}

    /**
     *  @private
     */
	public function set selector(value:String):void
	{
	    if (_selector !== value)
	    {
	        var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "selector", _selector, value);

            var resetSubscription:Boolean = false;
            if (subscribed)
            {
                unsubscribe();
                resetSubscription = true;
            }

    		_selector = value;

    		// Update an existing subscription to use the new selector.
    		if (resetSubscription)
    			subscribe(clientId);

    	    dispatchEvent(event);
    	}
	}
	
	//----------------------------------
	//  subtopic
	//----------------------------------	

    /**
     *  @private
     */
    private var _subtopic:String = "";
    
    [Bindable(event="propertyChange")]
    
    /**
     *  Provides access to the subtopic for the remote destination that the MessageAgent uses.
     */
    public function get subtopic():String
    {
        return _subtopic;
    }
    
	/**
	 *  Setting the subtopic when the Consumer is connected and
	 *  subscribed has the side effect of unsubscribing and resubscribing
	 *  the Consumer.
	 */
    public function set subtopic(value:String):void
    {
        if (subtopic != value)
	    {
	        var resetSubscription:Boolean = false;
            if (subscribed)
            {
                unsubscribe();
                resetSubscription = true;
            }
                
            _subtopic = value;
            
            if (resetSubscription)
                subscribe();
        }
    }


    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    override protected function internalSend(message:IMessage, waitForClientId:Boolean = true):void
    {
        if (subtopic.length > 0)
            message.headers[AsyncMessage.SUBTOPIC_HEADER] = subtopic;    
        if (_selector.length > 0)
            message.headers[CommandMessage.SELECTOR_HEADER] = _selector;

        super.internalSend(message, waitForClientId);
    }
}

}
