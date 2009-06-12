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
    
import flash.utils.Dictionary;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.messaging.events.MessageEvent;

use namespace mx_internal;

/**
 *  @private
 * 
 *  Helper class that listens for MessageEvents dispatched by ChannelSets that Consumers are subscribed over.
 *  This class is necessary because the server maintains queues of messages to push to this Flex client on a
 *  per-endpoint basis but the client may create more than one Channel that connects to a single server endpoint.
 *  In this scenario, messages can be pushed/polled to the client over a different channel instance than the one 
 *  that the target Consumer subscribed over. The server isn't aware of this difference because both channels are 
 *  pointed at the same endpoint. Here's a diagram to illustrate.
 * 
 *  Client:
 *               Consumer 1           Consumer 2    Consumer 3
 *                  |                       |       /
 *               ChannelSet 1            ChannelSet 2
 *                  |                       |
 *               Channel 1               Channel 2  <- The endpoint URIs for these two channels are identical
 *                  |                       |
 *                  \_______________________/
 *  Server:                     |
 *                              |
 *                          Endpoint (that the two channels point to)
 *                              |
 *                  FlexClientOutboundQueue (for this endpoint for this FlexClient)
 *                              \-- Outbound messages for the three Consumer subscriptions
 * 
 *  When the endpoint receives a poll request from Channel 1 it will return queued messages for all three subscriptions
 *  but back on the client when Channel 1 dispatches message events for Consumer 2 and 3's subscriptions they won't see
 *  them because they're directly connected to the separate Channel2/ChannelSet2.
 *  This helper class keeps track of Consumer subscriptions and watches all ChannelSets for message events to 
 *  ensure they're dispatched to the proper Consumer even when the client has been manually (miss)configured as the
 *  diagram illustrates.
 *  
 *  This class is a singleton that maintains a table of all subscribed Consumers and ref-counts the number of active
 *  subscriptions per ChannelSet to determine whether it needs to be listening for message events from a given 
 *  ChannelSet or not; it dispatches message events from these ChannelSets to the proper Consumer instance
 *  by invoking the Consumer's messageHandler() method directly.
 */
public class ConsumerMessageDispatcher
{
    //--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  The sole instance of this singleton class.
     */
	private static var _instance:ConsumerMessageDispatcher;
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Returns the sole instance of this singleton class,
	 *  creating it if it does not already exist.
     */
	public static function getInstance():ConsumerMessageDispatcher
	{
		if (!_instance)
			_instance = new ConsumerMessageDispatcher();

		return _instance;
	}
    
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *  Use getInstance() instead of "new" to create.
	 */
	public function ConsumerMessageDispatcher()
	{
		super();
    }
    
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
    
    /**
     *  Lookup table for subscribed Consumer instances; Object<Consumer clientId, Consumer>
     *  This is used to dispatch pushed/polled messages to the proper Consumer instance.
     */
    private const _consumers:Object = {};
    
    /**
     *  Table of ref-counts per ChannelSet that subscribed Consumer instances are using; Dictionary<ChannelSet, ref-count> (non-weak keys).
     *  The ref-count is the number of subscribed Consumers for the ChannelSet.
     *  When we add a new ChannelSet we need to start listening on it for MessageEvents to redispatch to subscribed Consumers.
     *  When the ref-count drops to zero we need to stop listening on it for MessageEvents and remove it from the table.
     */
    private const _channelSetRefCounts:Dictionary = new Dictionary(/* strong keys */);
       
    /**
     *  Table used to prevent duplicate delivery of messages to a Consumer when multiple ChannelSets are
     *  connected to the same server endpoint over a single, underlying shared Channel.
     */
    private const _consumerDuplicateMessageBarrier:Object = {};       
       
	//--------------------------------------------------------------------------
	//
	//  Public Methods
	//
	//--------------------------------------------------------------------------
      
    /**
     *  Determines whether any subscriptions are using the specified channel.
     */
    public function isChannelUsedForSubscriptions(channel:Channel):Boolean
    {
        var memberOfChannelSets:Array = channel.channelSets;
        var cs:ChannelSet = null;
        var n:int = memberOfChannelSets.length;
        for (var i:int = 0; i < n; i++)
        {
            cs = memberOfChannelSets[i];
            if ((_channelSetRefCounts[cs] != null) && (cs.currentChannel == channel))
                return true;
        }
        return false;
    }  
      
    /**
     *  Registers a Consumer subscription.
     *  This will cause the ConsumerMessageDispatcher to start listening for MessageEvents
     *  from the underlying ChannelSet used to subscribe and redispatch messages to Consumers.
     */ 
    public function registerSubscription(consumer:AbstractConsumer):void
    {
        _consumers[consumer.clientId] = consumer;
        if (_channelSetRefCounts[consumer.channelSet] == null)
        {
            // If this is the first time we've seen this ChannelSet start listening for message events
            // and initialize its ref-count.
            consumer.channelSet.addEventListener(MessageEvent.MESSAGE, messageHandler);
            _channelSetRefCounts[consumer.channelSet] = 1;
        }
        else
        {
            // We're already listening for message events; just increment the ref-count.
            _channelSetRefCounts[consumer.channelSet]++;
        }
    }
    
    /**
     *  Unregisters a Consumer subscription.
     *  The ConsumerMessageDispatcher will stop monitoring underlying channels for messages for
     *  this Consumer.
     */
    public function unregisterSubscription(consumer:AbstractConsumer):void
    {
        delete _consumers[consumer.clientId];
        var refCount:int = _channelSetRefCounts[consumer.channelSet];
        if (--refCount == 0)
        {
            // If this was the last Consumer using this ChannelSet stop listening for message events
            // and blow away the ref-count.
            consumer.channelSet.removeEventListener(MessageEvent.MESSAGE, messageHandler);
            _channelSetRefCounts[consumer.channelSet] = null;
            
            // And clean up the duplicate message delivery barrier if necessary.
            if (_consumerDuplicateMessageBarrier[consumer.id] != null)
                delete _consumerDuplicateMessageBarrier[consumer.id];
        }
        else
        {
            // Save the decremented ref-count.
            _channelSetRefCounts[consumer.channelSet] = refCount;
        }        
    }
    
	//--------------------------------------------------------------------------
	//
	//  Private Methods
	//
	//--------------------------------------------------------------------------    

    /**
     *  Handles message events from ChannelSets that Consumers are subscribed over.
     *  We just need to redirect the event to the proper Consumer instance.
     */
    private function messageHandler(event:MessageEvent):void
    {
        var consumer:AbstractConsumer = _consumers[event.message.clientId];
        if (consumer == null)
        {
            if (Log.isDebug())
                Log.getLogger("mx.messaging.Consumer").debug("'{0}' received pushed message for consumer but no longer subscribed: {1}", event.message.clientId, event.message);
            return;
        }
               
        if (event.target.currentChannel.channelSets.length > 1)
        {
            // If two (or more) ChannelSets share an underlying Channel instance and the channel receives
            // a message for a Consumer, it will dispatch a MessageEvent. Both ChannelSets will
            // be listening for this event and each will redispatch it. At this level
            // we need to ensure that a message targeted to a single Consumer is only processed once even
            // though we'll receive more than one MessageEvent (one per ChannelSet instances connected to
            // the same underlying Channel).
            // We do this by recording the most recent message Id received over the currently connected Channel
            // for the target Consumer and blocking repeat processing. When the Consumer moves to an unsubscribed
            // state its corresponding barrier is cleaned up.
            // This (miss?)configuration can be accomplished manually in client code but most clients won't
            // configure things this way in which case we'll skip to the faster code path below.
            var consumerId:String = consumer.id;
            if (_consumerDuplicateMessageBarrier[consumerId] == null)
                _consumerDuplicateMessageBarrier[consumerId] = {};
                
            var channelId:String = event.target.currentChannel.id;
            if (_consumerDuplicateMessageBarrier[consumerId][channelId] != event.messageId)
            {
                _consumerDuplicateMessageBarrier[consumerId][channelId] = event.messageId;
                consumer.messageHandler(event);
            }
        }
        else // Only one ChannelSet so we don't need to worry about this.
        {
            consumer.messageHandler(event);
        }
    }
       
}

}
