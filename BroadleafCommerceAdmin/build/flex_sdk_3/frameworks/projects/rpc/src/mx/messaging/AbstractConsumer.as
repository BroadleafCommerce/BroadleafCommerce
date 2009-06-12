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
import mx.events.PropertyChangeEvent;
import mx.logging.Log;
import mx.messaging.channels.PollingChannel;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

/**
 *  Dispatched when a message is received by the Consumer.
 *
 *  @eventType mx.messaging.events.MessageEvent.MESSAGE
 */
[Event(name="message", type="mx.messaging.events.MessageEvent")]

[ResourceBundle("messaging")]

/**
 *  The AbstractConsumer is the base class for both the Consumer and
 *  MultiTopicConsumer classes.  You use those classes to receive pushed
 *  messages from the server.
 */
public class AbstractConsumer extends MessageAgent
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructs a Consumer.
     *
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
	public function AbstractConsumer()
	{
		super();
	    _log = Log.getLogger("mx.messaging.Consumer");
		_agentType = "consumer";
	}

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This is the current number of resubscribe attempts that we've done.
     */
    private var _currentAttempt:int;

    /**
     *  @private
     *  The timer used for resubscribe attempts.
     */
    private var _resubscribeTimer:Timer;

	/**
	 *  Flag indicating whether this consumer should be subscribed or not.
	 */
	protected var _shouldBeSubscribed:Boolean;

    /**
     *  @private
     *  Current subscribe message - used for resubscribe attempts.
     */
    private var _subscribeMsg:CommandMessage;

    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
	//  clientId
	//----------------------------------

    /**
     *  @private
     *  If our clientId has changed we may need to unsubscribe() using the
     *  current clientId and then resubscribe using the new clientId.
     *  // TODO - remove this?
     *
     *  @param value The clientId value.
     */
	override mx_internal function setClientId(value:String):void
	{
		if (super.clientId != value)
		{
		    var resetSubscription:Boolean = false;
			if (subscribed)
			{
				unsubscribe();
				resetSubscription = true;
		    }

			super.setClientId(value);

			if (resetSubscription)
				subscribe(value);
		}
	}

	//----------------------------------
	//  destination
	//----------------------------------

    /**
     *  @private
     *  Updates the destination for this Consumer and resubscribes if the
     *  Consumer is currently subscribed.
     */
	override public function set destination(value:String):void
	{
	    if (destination != value)
	    {
	        var resetSubscription:Boolean = false;
            if (subscribed)
            {
                unsubscribe();
                resetSubscription = true;
            }

            super.destination = value;

            if (resetSubscription)
                subscribe();
        }
	}

	//----------------------------------
	//  resubscribeAttempts
	//----------------------------------

    /**
     *  @private
     */
    private var _resubscribeAttempts:int = 5;

    [Bindable(event="propertyChange")]

	/**
	 *  The number of resubscribe attempts that the Consumer makes in the event
	 *  that the destination is unavailable or the connection to the destination fails.
	 *  A value of -1 enables infinite attempts.
	 *  A value of zero disables resubscribe attempts.
	 *  <p>
	 *  Resubscribe attempts are made at a constant rate according to the resubscribe interval
	 *  value. When a resubscribe attempt is made if the underlying channel for the Consumer is not
	 *  connected or attempting to connect the channel will start a connect attempt.
	 *  Subsequent Consumer resubscribe attempts that occur while the underlying
	 *  channel connect attempt is outstanding are effectively ignored until
	 *  the outstanding channel connect attempt succeeds or fails.
	 *  </p>
	 *
	 *  @see mx.messaging.Consumer#resubscribeInterval
	 */
	public function get resubscribeAttempts():int
	{
	    return _resubscribeAttempts;
	}

	/**
	 *  @private
	 */
	public function set resubscribeAttempts(value:int):void
	{
	    if (_resubscribeAttempts != value)
	    {
	        if (value == 0)
	            stopResubscribeTimer();

	        var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "resubscribeAttempts", _resubscribeAttempts, value);
	        _resubscribeAttempts = value;
	        dispatchEvent(event);
	    }
	}

	//----------------------------------
	//  resubscribeInterval
	//----------------------------------

    /**
     *  @private
     */
    private var _resubscribeInterval:int = 5000;

    [Bindable(event="propertyChange")]

	/**
	 *  The number of milliseconds between resubscribe attempts.
	 *  If a Consumer doesn't receive an acknowledgement for a subscription
	 *  request, it will wait the specified number of milliseconds before
	 *  attempting to resubscribe.
	 *  Setting the value to zero disables resubscriptions.
	 *  <p>
	 *  Resubscribe attempts are made at a constant rate according to this
	 *  value. When a resubscribe attempt is made if the underlying channel for the Consumer is not
	 *  connected or attempting to connect the channel will start a connect attempt.
	 *  Subsequent Consumer resubscribe attempts that occur while the underlying
	 *  channel connect attempt is outstanding are effectively ignored until
	 *  the outstanding channel connect attempt succeeds or fails.
	 *  </p>
	 *
	 *  @see mx.messaging.Consumer#resubscribeInterval
	 *
	 *  @throws ArgumentError If the assigned value is negative.
	 */
	public function get resubscribeInterval():int
	{
	    return _resubscribeInterval;
	}

	/**
	 *  @private
	 */
	public function set resubscribeInterval(value:int):void
	{
	    if (_resubscribeInterval != value)
	    {
	        if (value < 0)
			{
				var message:String = resourceManager.getString(
					"messaging", "resubscribeIntervalNegative");
	            throw new ArgumentError(message);
			}
		    else if (value == 0)
			{
		        stopResubscribeTimer();
			}
		    else if (_resubscribeTimer != null)
			{
		        _resubscribeTimer.delay = value;
			}

		    var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "resubscribeInterval", _resubscribeInterval, value);
		    _resubscribeInterval = value;
		    dispatchEvent(event);
		}
	}

	//----------------------------------
	//  subscribed
	//----------------------------------

	/**
	 *  @private
	 */
	private var _subscribed:Boolean;

	[Bindable(event="propertyChange")]

	/**
	 *  Indicates whether the Consumer is currently subscribed. The <code>propertyChange</code>
	 *  event is dispatched when this property changes.
	 */
	public function get subscribed():Boolean
	{
		return _subscribed;
	}

	/**
	 *  @private
	 */
	protected function setSubscribed(value:Boolean):void
	{
	    if (_subscribed != value)
	    {
	        var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "subscribed", _subscribed, value);
	        _subscribed = value;

	        // Register or unregister our subscription state with the ConsumerMessageDispatcher.
	        // This allows the singleton ConsumerMessageDispatcher to start or stop listening for
	        // messages on our behalf.
	        if (_subscribed)
            {
	            ConsumerMessageDispatcher.getInstance().registerSubscription(this);
                if (channelSet != null && channelSet.currentChannel != null && channelSet.currentChannel is PollingChannel)
                    PollingChannel(channelSet.currentChannel).enablePolling();
            }
	        else
            {
    	        ConsumerMessageDispatcher.getInstance().unregisterSubscription(this);
                if (channelSet != null && channelSet.currentChannel != null && channelSet.currentChannel is PollingChannel)
                    PollingChannel(channelSet.currentChannel).disablePolling();
            }

	        dispatchEvent(event);
	    }
	}

	//----------------------------------
	//  timestamp
	//----------------------------------

	/**
	 *  @private
	 */
	private var _timestamp:Number = -1;

    [Bindable(event="propertyChange")]

	/**
	 *  Contains the timestamp of the most recent message this Consumer
	 *  has received.
	 *  This value is passed to the destination in a <code>receive()</code> call
	 *  to request that it deliver messages for the Consumer from the timestamp
	 *  forward.
	 *  All messages with a timestamp value greater than the
	 *  <code>timestamp</code> value will be returned during a poll operation.
	 *  Setting this value to -1 will retrieve all cached messages from the
	 *  destination.
	 */
	public function get timestamp():Number
	{
	    return _timestamp;
	}

	/**
	 *  @private
	 */
	public function set timestamp(value:Number):void
	{
	    if (_timestamp != value)
	    {
	        var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "timestamp", _timestamp, value);
	        _timestamp = value;
	        dispatchEvent(event);
	    }
	}

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
     *  Custom processing for subscribe, unsubscribe and poll message
     *  acknowledgments.
     *
     *  @param ackMsg The AcknowledgeMessage.
     *
     *  @param msg The original subscribe, unsubscribe or poll message.
	 */
	override public function acknowledge(ackMsg:AcknowledgeMessage, msg:IMessage):void
	{
	    // Ignore acks for any outstanding messages that return after disconnect() is invoked.
	    if (_disconnectBarrier)
	        return;

	    // Only run Consumer processing if this isn't an error.
	    if (!ackMsg.headers[AcknowledgeMessage.ERROR_HINT_HEADER] && (msg is CommandMessage))
	    {
	        var command:CommandMessage = msg as CommandMessage;

            var op:int = command.operation;

            // For MultiTopicConsumers, the message gets marked if this is the
            // message completely unsubscribes the client.
            if (op == CommandMessage.MULTI_SUBSCRIBE_OPERATION)
            {
                if (msg.headers.DSlastUnsub != null)
                   op = CommandMessage.UNSUBSCRIBE_OPERATION;
                else
                   op = CommandMessage.SUBSCRIBE_OPERATION;
            }

	        switch (op)
	        {
	            case CommandMessage.UNSUBSCRIBE_OPERATION:
                    if (Log.isInfo())
                        _log.info("'{0}' {1} acknowledge for unsubscribe.", id, _agentType);
                    super.setClientId(null);
                    setSubscribed(false); // Stop listening for messages.
                    ackMsg.clientId = null; // Force the ack's clientId to null as well before ack'ing it.
                    super.acknowledge(ackMsg, msg);
                break;

                case CommandMessage.SUBSCRIBE_OPERATION:
                    stopResubscribeTimer();
                    // NOTE: the -1 in the timestamp assignment below.
                    // This works around a bug where if a Producer sends
                    // a message in the same batch as the subscribe,
                    // it will end up with (likely) the same timestamp
                    // as the consumer.  Because the message is sent
                    // by the client after the subscribe though, it
                    // should still be delivered.
                    // TODO: Improve solution here.
                    if (ackMsg.timestamp > _timestamp)
                        _timestamp = ackMsg.timestamp - 1;

                    if (Log.isInfo())
                        _log.info("'{0}' {1} acknowledge for subscribe. Client id '{2}' new timestamp {3}",
                                    id, _agentType, ackMsg.clientId, _timestamp);
                    super.setClientId(ackMsg.clientId);
                    setSubscribed(true);
                    super.acknowledge(ackMsg, msg);
                break;

                // Handle the result of a receive() invocation (a Consumer instance-specific poll request).
                case CommandMessage.POLL_OPERATION:
                	if ((ackMsg.body != null) && (ackMsg.body is Array))
                	{
			            var messageList:Array = ackMsg.body as Array;
                        for each (var message:IMessage in messageList)
                            messageHandler(MessageEvent.createEvent(MessageEvent.MESSAGE, message));
		            }
		            super.acknowledge(ackMsg, msg);
                break;
            }
	    }
	    else
	    {
	        super.acknowledge(ackMsg, msg);
	    }
	}

	/**
	 *  Disconnects the Consumer from its remote destination.
	 *  This method should be invoked on a Consumer that is no longer
	 *  needed by an application after unsubscribing.
	 *  This method does not wait for outstanding network operations to complete
	 *  and does not send an unsubscribe message to the server.
	 *  After invoking disconnect(), the Consumer will report that it is in an
	 *  disconnected, unsubscribed state because it will not receive any more
	 *  messages until it has reconnected and resubscribed.
	 *  Disconnecting stops automatic resubscription attempts if they are running.
	 */
	override public function disconnect():void
	{
	    // We don't invoke unsubscribe() in this case because a Consumer subscribed to a
	    // JMS destination durably will blow away the durable subscription.
	    _shouldBeSubscribed = false; // Prevent resubscribe attempts.
        stopResubscribeTimer();
        setSubscribed(false);

        super.disconnect();
	}

	/**
	 *  @private
     *  The Consumer supresses ErrorMessage processing if the error is
     *  retryable and it is configured to resubscribe.
     *
     *  @param errMsg The ErrorMessage describing the fault.
     *
     *  @param msg The original message (generally a subscribe).
	 */
	override public function fault(errMsg:ErrorMessage, msg:IMessage):void
    {
        // Ignore faults for any outstanding messages that return after disconnect() is invoked.
        if (_disconnectBarrier)
            return;

        // If this error correlates to our current subscribe message,
        // we should no longer be subscribed.
        if ((_subscribeMsg != null) && (errMsg.correlationId == _subscribeMsg.messageId))
            _shouldBeSubscribed = false;

        // If the error is not retryable, or there is no resubscribe timer running,
        // dispatch a fault event. Otherwise, the resubscribe timer is running and
        // will generate a fault when it runs out of allowed resubscribe attempts.
        if (!errMsg.headers[ErrorMessage.RETRYABLE_HINT_HEADER] || _resubscribeTimer == null)
        {
            // Stop the resubscribe timer in case it is running.
            stopResubscribeTimer();
            super.fault(errMsg, msg);
        }
    }

    /**
     *  @private
     *  Custom processing to warn the user if the consumer is connected over
     *  a non-real channel.
     *
     *  @param event The ChannelEvent.
     */
    override public function channelConnectHandler(event:ChannelEvent):void
    {
        super.channelConnectHandler(event);

        if (connected && channelSet != null && channelSet.currentChannel != null
                && !channelSet.currentChannel.realtime && Log.isWarn())
        {
            _log.warn("'{0}' {1} connected over a non-realtime channel '{2}'"
                + " which means channel is not automatically receiving updates via polling or server push."
                , id, _agentType, channelSet.currentChannel.id);
        }
    }

    /**
     *  @private
     *  Custom processing to start up a resubscribe timer if our channel is
     *  disconnected when we should be subscribed.
     *
     *  @param event The ChannelEvent.
     */
    override public function channelDisconnectHandler(event:ChannelEvent):void
	{
	    setSubscribed(false);

	    super.channelDisconnectHandler(event);

	    if (_shouldBeSubscribed && !event.rejected)
            startResubscribeTimer();
	}

	/**
	 *  @private
	 *  Custom processing to start up a resubscribe timer if our channel faults
	 *  when we should be subscribed.
	 *
	 *  @param event The ChannelFaultEvent.
	 */
	override public function channelFaultHandler(event:ChannelFaultEvent):void
	{
	    if (!event.channel.connected)
	        setSubscribed(false);

	    super.channelFaultHandler(event);

	    if (_shouldBeSubscribed && !event.rejected && !event.channel.connected)
	        startResubscribeTimer();
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  Requests any messages that are queued for this Consumer on the server.
	 *  This method should only be used for Consumers that subscribe over non-realtime,
	 *  non-polling channels.
	 *  This method is a no-op if the Consumer is not subscribed.
	 *
	 *  @param timestamp This argument is deprecated and is ignored.
	 */
	public function receive(timestamp:Number = 0):void
	{
	    if (clientId != null) // We need a clientId to distinguish this from a generic poll request sent by a polling channel.
	    {
    		var msg:CommandMessage = new CommandMessage();
            msg.operation = CommandMessage.POLL_OPERATION;
            msg.destination = destination;
    		internalSend(msg);
        }
	}

	/**
     *  Subscribes to the remote destination.
	 *
	 *  @param clientId The client id to subscribe with. Use null for non-durable Consumers. If the subscription is durable, a consistent
	 *                  value must be supplied every time the Consumer subscribes in order
	 *                  to reconnect to the correct durable subscription in the remote destination.
	 *
	 *  @throws mx.messaging.errors.InvalidDestinationError If no destination is set.
	 */
	public function subscribe(clientId:String = null):void
	{
        // Set a flag to determine whether the passed clientId differs from the
        // current value and should be assigned.
        var resetClientId:Boolean = ((clientId != null) &&
                                (super.clientId != clientId)) ? true : false;

        if (subscribed && resetClientId)
        {
            // We're already subscribed, but we need to resubscribe under
            // the new clientId.
            unsubscribe();
        }

        // Make sure any resubscribe timer is stopped.
        stopResubscribeTimer();

        _shouldBeSubscribed = true;
        if (resetClientId)
            super.setClientId(clientId);
        if (Log.isInfo())
            _log.info("'{0}' {1} subscribe.", id, _agentType);
        _subscribeMsg = buildSubscribeMessage();

        internalSend(_subscribeMsg);
	}

	/**
     *  Unsubscribes from the remote destination. In the case of durable JMS
     *  subscriptions, this will destroy the durable subscription on the JMS server.
     *
     *  @param preserveDurable - when true, durable JMS subscriptions are not destroyed
	 * 		allowing consumers to later resubscribe and receive missed messages
	 */
	public function unsubscribe(preserveDurable:Boolean = false):void
	{
        _shouldBeSubscribed = false;
        if (subscribed)
        {
        	// Stop listening now for any messages as we could be set to a new
        	// channel before the ack comes back, and once the ack returns we
        	// will no longer have a valid client id.
            if (channelSet != null)
		        channelSet.removeEventListener(destination, messageHandler);

		    if (Log.isInfo())
                _log.info("'{0}' {1} unsubscribe.", id, _agentType);

            internalSend(buildUnsubscribeMessage(preserveDurable));
	    }
	    else
	    {
	        stopResubscribeTimer();
        }
	}

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    //
    //--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Consumers subscribe for messages from a destination and this is the handler
	 *  method that is invoked when a message for this Consumer is pushed or polled
	 *  from the server.
	 *
	 *  @param event The MessageEvent.
	 */
	mx_internal function messageHandler(event:MessageEvent):void
	{
	    // NOTE: This method is invoked directly by the ConsumerMessageDispatcher.
	    // The event flow for a pushed message is:
	    // 1. Channel receives a pushed/polled message and dispatches a message event
	    // 2. Any ChannelSets connected to the Channel will handle these events in ChannelSet.messageHandler();
	    //    simply redispatching them.
	    // 3. Consumers that subscribe to a destination trigger the internal use of a shared ConsumerMessageDispatcher
	    //    that listens for message events from any ChannelSets that Consumers have subscribed over and this helper routes pushed messages to the proper Consumer instances.
	    var message:IMessage = event.message;
	    if (message is CommandMessage)
        {
            var command:CommandMessage = message as CommandMessage;
            switch (command.operation)
            {
                case CommandMessage.SUBSCRIPTION_INVALIDATE_OPERATION:
                    // We've been unsubscribed but it wasn't the result of an unsubscribe
                    // message this agent sent. If a polling channel is being used, let it know.
                    if (channelSet.currentChannel is PollingChannel)
                        PollingChannel(channelSet.currentChannel).disablePolling();
                    setSubscribed(false);
                break;
                default:
                    if (Log.isWarn())
                        _log.warn("'{0}' received a CommandMessage '{1}' that could not be handled.", id, CommandMessage.getOperationAsString(command.operation));
            }
            /*
             * Command messages are handled internally by the Consumer and
             * are not dispatched to message listeners via MessageEvents.
             */
            return;
        }

        if (message.timestamp > _timestamp)
            _timestamp = message.timestamp;

		// Server might push out error messages (eg. during MessageClient.invalidate)
		// that need to be dispatched as message fault events.
		if (message is ErrorMessage)
			dispatchEvent(MessageFaultEvent.createEvent(ErrorMessage(message)));
		else
			dispatchEvent(MessageEvent.createEvent(MessageEvent.MESSAGE, message));
	}

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  Returns a subscribe message.
	 *  This method should be overridden by subclasses if they need custom
	 *  subscribe messages.
	 *
	 *  @return The subscribe CommandMessage.
	 */
	protected function buildSubscribeMessage():CommandMessage
	{
        var msg:CommandMessage = new CommandMessage();
        msg.operation = CommandMessage.SUBSCRIBE_OPERATION;
        msg.clientId = clientId;
        msg.destination = destination;
        return msg;
	}

	/**
	 *  Returns an unsubscribe message.
	 *  This method should be overridden by subclasses if they need custom
	 *  unsubscribe messages.
	 *
	 *  @param preserveDurable - when true, durable JMS subscriptions are not destroyed
	 * 			allowing consumers to later resubscribe and receive missed messages
	 *
	 *  @return The unsubscribe CommandMessage.
	 */
	protected function buildUnsubscribeMessage(preserveDurable:Boolean):CommandMessage
	{
        var msg:CommandMessage = new CommandMessage();
        msg.operation = CommandMessage.UNSUBSCRIBE_OPERATION;
        msg.clientId = clientId;
        msg.destination = destination;

        // only include the PRESERVE_DURABLE_HEADER param in the message if
        // its value is true
        if (preserveDurable)
        	msg.headers[CommandMessage.PRESERVE_DURABLE_HEADER] = preserveDurable;

        return msg;
	}

    /**
     *  @private
     *  Attempt to resubscribe.
     *  This can be called directly or from a Timer's event handler.
     *
     *  @param event The timer event for resubscribe attempts.
     */
    protected function resubscribe(event:TimerEvent):void
    {
        // If we're past our limit of attempts, fault out.
        if ((_resubscribeAttempts != -1) &&
            (_currentAttempt >= _resubscribeAttempts))
        {
            stopResubscribeTimer();
            _shouldBeSubscribed = false;
            var errMsg:ErrorMessage = new ErrorMessage();
			errMsg.faultCode = "Client.Error.Subscribe";
            errMsg.faultString = resourceManager.getString(
				"messaging", "consumerSubscribeError");
            errMsg.faultDetail = resourceManager.getString(
				"messaging", "failedToSubscribe");
            errMsg.correlationId = _subscribeMsg.messageId;
            fault(errMsg, _subscribeMsg);
            return;
        }

        if (Log.isDebug())
            _log.debug("'{0}' {1} trying to resubscribe.", id, _agentType);

        _resubscribeTimer.delay = _resubscribeInterval;
        _currentAttempt++;
        // Send the resubscribe message, skipping the MessageAgent's queue that blocks
        // messages until the clientId is set.
        internalSend(_subscribeMsg, false);
    }

	/**
	 *  @private
     *  This method will start a timer which attempts to resubscribe
     *  periodically.
     */
    protected function startResubscribeTimer():void
    {
        if (_shouldBeSubscribed && (_resubscribeTimer == null))
        {
            // If we're configured for resubscribe start up the timer.
            if ((_resubscribeAttempts != 0) && (_resubscribeInterval > 0))
            {
                if (Log.isDebug())
                    _log.debug("'{0}' {1} starting resubscribe timer.", id, _agentType);
	            /*
	             * Initially, the timeout is set to 1 so we try to
	             * reconnect immediately (perhaps to a different channel).
	             * after that, it will poll at the configured time interval.
	             */
	            _resubscribeTimer = new Timer(1);
	            _resubscribeTimer.addEventListener(TimerEvent.TIMER, resubscribe);
	            _resubscribeTimer.start();
	            _currentAttempt = 0;
            }
        }
    }

    /**
     * @private
     * Stops a resubscribe timer if one is running.
     */
    protected function stopResubscribeTimer():void
    {
        if (_resubscribeTimer != null)
        {
            if (Log.isDebug())
                _log.debug("'{0}' {1} stopping resubscribe timer.", id, _agentType);

            _resubscribeTimer.removeEventListener(TimerEvent.TIMER, resubscribe);
            _resubscribeTimer.reset();
            _resubscribeTimer = null;
        }
	}
}

}
