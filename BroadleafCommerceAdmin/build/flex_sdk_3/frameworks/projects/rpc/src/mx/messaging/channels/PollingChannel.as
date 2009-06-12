////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.channels
{

import flash.events.Event;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.messaging.Channel;
import mx.messaging.ConsumerMessageDispatcher;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  The PollingChannel class provides the polling behavior that all polling channels in the messaging
 *  system require.
 */
public class PollingChannel extends Channel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Creates a new PollingChannel instance with the specified id. Once a PollingChannel is
     *  connected and begins polling, it will issue a poll request once every three seconds
     *  by default.
     *
 	 *  <b>Note</b>: The PollingChannel type should not be constructed directly. Instead
	 *  create instances of protocol specific subclasses such as HTTPChannel or
	 *  AMFChannel that extend it.
     *
	 *  @param id The id of this Channel.
	 *  
	 *  @param uri The uri for this Channel.
     */
    public function PollingChannel(id:String = null, uri:String = null)
    {
        super(id, uri);

        _pollingEnabled = true;
        _shouldPoll = false;

        if (timerRequired())
        {
            // Poll on a 3 second interval by default.
            // The timer is configured to only dispatch one event per run.
            // It is restarted after a poll response is received for the current outstanding poll request.
            _pollingInterval = DEFAULT_POLLING_INTERVAL;
            _timer = new Timer(_pollingInterval, 1);
            _timer.addEventListener(TimerEvent.TIMER, internalPoll);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private 
     *  The base polling interval to use if the server is not triggering adaptive polling
     *  interval waits via its poll responses.
     */
    mx_internal var _pollingInterval:int;

    /**
     *  @private
     *  Indicates whether we should poll but stopped for some reason.
     */
    mx_internal var _shouldPoll:Boolean;

    /**
     *  @private
     *  This reference count allows us to determine when polling is needed and
     *  when it is not.
     */
    private var _pollingRef:int = -1;

   /**
    *  @private
    *  Guard used to avoid issuing poll requests on top of each other. This is 
    *  needed when a poll request is issued manually by calling poll() method.
    */    
    mx_internal var pollOutstanding:Boolean;
    
    /**
     *  @private 
     *  Used for polling the server at a given interval.  
     *  This may be null if channel implementation does not require the use of a 
     *  timer to poll.
     */
    mx_internal var _timer:Timer;

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
	//  piggybackingEnabled
	//----------------------------------   
      
    /**
     *  @private
     */
    private var _piggybackingEnabled:Boolean;
    
    /**
     *  @private
     */
    protected function get internalPiggybackingEnabled():Boolean
    {
        return _piggybackingEnabled;
    }   
    
    /**
     *  @private
     */
    protected function set internalPiggybackingEnabled(value:Boolean):void
    {
        _piggybackingEnabled = value;
    }
       
    //----------------------------------
	//  pollingEnabled
	//----------------------------------
	
    /**
     *  @private
     */
    private var _pollingEnabled:Boolean; 

    /**
     *  @private
     */
    protected function get internalPollingEnabled():Boolean
    {
        return _pollingEnabled;
    }

    /**
     *  @private
     */
    protected function set internalPollingEnabled(value:Boolean):void
    {
        _pollingEnabled = value;
        // If the value is false, we want to stop polling only if the timer is
        // definitely running OR the timer isn't running and the polling interval is 0
        // because if the polling interval is 0 and we're polling, the timer isn't on
        // anyway, so we need to include both cases.
        if (!value && (timerRunning || (!timerRunning && (_pollingInterval == 0))))
        {
            stopPolling();
        }
        else if (value && _shouldPoll && !timerRunning)
        {
            startPolling();
        }
    }

	//----------------------------------
	//  pollingInterval
	//----------------------------------

    /**
     *  @private
     */
    mx_internal function get internalPollingInterval():Number
    {        
        return (_timer == null) ? 0 : _pollingInterval;
    }

    /**
     *  @private
     */
    mx_internal function set internalPollingInterval(value:Number):void
    {
        // We have to be careful here because the timer's delay cannot be set to
        // 0 so if we are setting the polling interval to 0, we need to stop the
        // timer AND hold onto the value in the _pollingInterval variable.
        if (value == 0)
        {
            _pollingInterval = value;
                     
            if (_timer != null)
            {
                _timer.stop();
            }             
            if (_shouldPoll)
            {
                startPolling();
            }
              
        }
        else if (value > 0)
        {
            if (_timer != null)
            {              
                _timer.delay = _pollingInterval = value;
                if (!timerRunning && _shouldPoll)
                {
                    startPolling();
                }
            }
        }
        else
        {
			var message:String = resourceManager.getString(
				"messaging", "pollingIntervalNonPositive");
            throw new ArgumentError(message);
        }
    }

	//----------------------------------
	//  realtime
	//----------------------------------
    
    /**
     *  @private
     *  Returns true if the channel supports realtime behavior via server push or client poll.
     *  Piggybacking does not qualify as real time because no data will arrive from the server
     *  without a message being explicitly sent by the client.
     */
    override mx_internal function get realtime():Boolean
    {
        return _pollingEnabled;
    }    

	//----------------------------------
	//  timerRunning
	//----------------------------------
    
    /**
     *  @private
     */
    mx_internal function get timerRunning():Boolean
    {
        return (_timer != null) && _timer.running;
    }    

    //--------------------------------------------------------------------------
    //
    // Overridden Public Methods
    // 
    //--------------------------------------------------------------------------

    /**
	 *  Sends the specified message to its target destination.
	 *  Subclasses must override the <code>internalSend()</code> method to
	 *  perform the actual send.
	 *  <code>PollingChannel</code> will wrap outbound messages in poll requests if a poll
	 *  is not currently outstanding.
     *
	 *  @param agent The MessageAgent that is sending the message.
	 * 
	 *  @param message The Message to send.
	 * 
	 *  @throws mx.messaging.errors.InvalidDestinationError If neither the MessageAgent nor the
	 *                                  message specify a destination.
	 */
	override public function send(agent:MessageAgent, message:IMessage):void
	{    
	    var piggyback:Boolean = false;
	    if (!pollOutstanding && _piggybackingEnabled && !(message is CommandMessage))
	    {
	        if (_shouldPoll)
	        {
	            piggyback = true;
	        }
	        else
	        {
	            var consumerDispatcher:ConsumerMessageDispatcher = ConsumerMessageDispatcher.getInstance();
	            if (consumerDispatcher.isChannelUsedForSubscriptions(this))
	                piggyback = true;
	        }
	    }
	    if (piggyback)
	        internalPoll();
	    
	    super.send(agent, message);
	    
	    if (piggyback)
	    {
    	    // Manually build and send a terminal poll message to return any pushed messages
    	    // that may result from the sent message above. Invoking internalPoll() again would 
    	    // be a no-op because we now have the initial poll outstanding.
    	    var msg:CommandMessage = new CommandMessage();
            msg.operation = CommandMessage.POLL_OPERATION;

            if (Log.isDebug())
                _log.debug("'{0}' channel sending poll message\n{1}\n", id, msg.toString());
            
            try
            {
                internalSend(new PollCommandMessageResponder(null, msg, this, _log));
            }
            catch(e:Error)
            {
                // If there was a problem stop polling.
                stopPolling();
                throw e;
            }
        }	    
	}

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method prevents polling from continuing when the Channel can not connect.
     * 
     *  @param event The ChannelFaultEvent.
     */
	override protected function connectFailed(event:ChannelFaultEvent):void
	{
	     stopPolling();
	     super.connectFailed(event);   
	}

    /**
     *  @private
     *  If a consumer sends a subscribe message to the server, we need to
     *  track that polling should occur.  In addition, we don't however, want
     *  to begin polling before we actually receive the acknowledgement that
     *  we have successfully subscribed.  This method is used to return a
     *  special message handler that will notify us when we have a successful
     *  subscribe and can safely begin polling.  This case is the reverse for
     *  unsubscribe, we need to track that we successfully unsubscribed and
     *  there are no more consumers attached that need polling.
     * 
     *  In addition to handling this case, this method also returns a special
     *  responder to handle the results or fault for a poll request.
     *
     *  @param agent MessageAgent that requested the message be sent.
     * 
     *  @param msg Message to be sent.
     * 
     *  @return A PollSyncMessageResponder for subscribe/unsubscriber requests or a
     *          PollCommandMessageResponder for poll requests; otherwise the default
     *          message responder.
     */
    final override protected function getMessageResponder(agent:MessageAgent, msg:IMessage):MessageResponder
    {
        var responder:MessageResponder = null;
        if (msg is CommandMessage)
        {
            var cmd:CommandMessage = CommandMessage(msg);
            if ((cmd.operation == CommandMessage.SUBSCRIBE_OPERATION) ||
                (cmd.operation == CommandMessage.UNSUBSCRIBE_OPERATION))
            {
                responder = getPollSyncMessageResponder(agent, cmd);
            }
            else if (cmd.operation == CommandMessage.POLL_OPERATION)
            {
                responder = new PollCommandMessageResponder(agent, msg, this, _log);
            }
        }
        return responder == null ? getDefaultMessageResponder(agent, msg):responder;
    }
    
    /**
     *  @private 
     *  Disconnects from the remote destination.
     */
    override protected function internalDisconnect(rejected:Boolean = false):void
    {
        stopPolling();
        super.internalDisconnect(rejected);
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Enables polling based on the number of times <code>enablePolling()</code>
     *  and <code>disablePolling()</code> have been invoked. If the net result is to enable
     *  polling the channel will poll the server on behalf of connected MessageAgents.
     *  <p>Invoked automatically based upon subscribing or unsubscribing from a remote
     *  destination over a PollingChannel.</p>
     */
    public function enablePolling():void
    {
    	_pollingRef++;
        if (_pollingRef == 0)
            startPolling();
    }

    /**
     *  Disables polling based on the number of times <code>enablePolling()</code>
     *  and <code>disablePolling()</code> have been invoked. If the net result is to disable
     *  polling the channel stops polling.
     *  <p>Invoked automatically based upon subscribing or unsubscribing from a remote
     *  destination over a PollingChannel.</p>
     */
    public function disablePolling():void
    {
    	_pollingRef--;
        if (_pollingRef < 0)
            stopPolling();
    }

    /**
     *  Initiates a poll operation if there are consumers subscribed to this channel, 
     *  and polling is enabled for this channel.
     *
     *  Note that this method will not start a new poll if one is currently in progress.
     */
    public function poll():void
    {
        internalPoll();
    }

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  This method allows a PollCommandMessageResponder to indicate that the 
     *  channel has lost its connectivity.
     * 
     *  @param rejected Channel will be rejected and will not attempt to reconnect if 
     *  this flag is true
     */
    mx_internal function pollFailed(rejected:Boolean = false):void
    {
        internalDisconnect(rejected);   
    }
    
    /**
     *  @private
     *  This method is invoked automatically when <code>disablePolling()</code>
     *  is called and it results in a net negative number of requests to poll.
     *  
     *  mx_internal to allow the poll responder to shut down polling if a general,
     *  fatal error occurs.
     */
    mx_internal function stopPolling():void
    {
        if (Log.isInfo())
            _log.info("'{0}' channel polling stopped.", id);
        
        if (_timer != null)
            _timer.stop();
        
        _pollingRef = -1;
        _shouldPoll = false;
        pollOutstanding = false;        
    }


    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Processes polling related configuration settings.
     *  
     *  @param settings The Channel settings.
     */
    protected function applyPollingSettings(settings:XML):void
    {
        if (settings.properties.length())
        {
            var props:XML = settings.properties[0];
            if (props["polling-enabled"].length())
                internalPollingEnabled = props["polling-enabled"].toString()=="true";
            if (props["polling-interval-millis"].length())
                internalPollingInterval = parseInt(props["polling-interval-millis"].toString());
            else if (props["polling-interval-seconds"].length()) // deprecated
                internalPollingInterval = parseInt(props["polling-interval-seconds"].toString()) * 1000;
           	
           	if (props["piggybacking-enabled"].length())
           	    internalPiggybackingEnabled = props["piggybacking-enabled"].toString()=="true";
           	
           	if (props["login-after-disconnect"].length())
           		_loginAfterDisconnect = props["login-after-disconnect"].toString()=="true";          	
        }
    }
    
    /**
     *  @private
     */
    protected function getPollSyncMessageResponder(agent:MessageAgent, msg:CommandMessage):MessageResponder
    {
        return null;
    }

    /**
     *  @private
     */
    protected function getDefaultMessageResponder(agent:MessageAgent, msg:IMessage):MessageResponder
    {
        return super.getMessageResponder(agent, msg);
    }    

    /**
     *  @private 
     *  Requests the server return any messages queued since the last poll request for this FlexClient.
     *
     *  @param event Event dispatched by the polling Timer.
     */
    protected function internalPoll(event:Event = null):void
    {
        if (!pollOutstanding)
        {
            if (Log.isInfo())
            	_log.info("'{0}' channel requesting queued messages.", id);
            	
            // If this poll is triggered via a direct invocation make sure no
            // concurrent poll Timer is running.
            if (timerRunning)
                _timer.stop();
        
            var msg:CommandMessage = new CommandMessage();
            msg.operation = CommandMessage.POLL_OPERATION;
            // Pass a null clientId - this indicates that we're polling for 
            // any subscriptions for this client as opposed to receive()'ing 
            // messages for a single Consumer instance subscribed to a specific destination.
            if (Log.isDebug())
                _log.debug("'{0}' channel sending poll message\n{1}\n", id, msg.toString());
    
            try
            {
                internalSend(new PollCommandMessageResponder(null, msg, this, _log));
                pollOutstanding = true;
            }
            catch(e:Error)
            {
                // If there was a problem stop polling.
                stopPolling();
                throw e;
            }
        }
        else
        {
            if (Log.isInfo())
                _log.info("'{0}' channel waiting for poll response.", id);
        }
    }

    /**
     *  @private
     *  This method is invoked automatically when <code>enablePolling()</code>
     *  is called and it results in net positive number of requests to poll.
     */
    protected function startPolling():void
    {
        if (_pollingEnabled)
        {
            if (Log.isInfo())
                _log.info("'{0}' channel polling started.", id);

            _shouldPoll = true;

		    poll(); // Poll immediately. Once a result is returned we schedule the next poll invocation.
        }
        // If polling is not enabled, this is a no-op.
    }

    /**
     *  @private
     *  Returns true if this channel requires a timer for polling.
     */
    protected function timerRequired():Boolean
    {
        return true;
    }

    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------

    /**
    * Define the default Polling Interval as 3000ms
    */
    private static const DEFAULT_POLLING_INTERVAL:int = 3000;
}

}

//------------------------------------------------------------------------------
//
// Private Classes
// 
//------------------------------------------------------------------------------

import flash.utils.Timer;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.logging.ILogger;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.channels.PollingChannel;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.MessagePerformanceUtils;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  @private
 *  Used internally to dispatch a batched set of messages returned in the poll
 *  command message.
 */
class PollCommandMessageResponder extends MessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private 
     *  Initializes an instance of the message responder that handles
     *  multiple messages received from a poll request that a Channel makes.
     *
     *  @param channel PollingChannel.
     */
    public function PollCommandMessageResponder(agent:MessageAgent, msg:IMessage, channel:PollingChannel, log:ILogger)
    {
        super(agent, msg, channel);
        _log = log;
    }

	//--------------------------------------------------------------------------
	//
	// Variables
	// 
	//--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Reference to the logger for the associated Channel.
     */
    private var _log:ILogger;    
        
    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handles a poll command result from the server which is either an empty acknowledgement
     *  if there were no messages to deliver or a response containing a list of messages to 
     *  dispatch in its body.
     * 
     *  @param msg The result message.
     */
    override protected function resultHandler(msg:IMessage):void
    {      
        PollingChannel(channel).pollOutstanding = false;
                
        if ((msg is CommandMessage))
        {    
            // Return early if the response is tagged as a no-op poll.
            if (msg.headers[CommandMessage.NO_OP_POLL_HEADER] == true)
                return;
                        	
            if (msg.body != null)
            {
                var messageList:Array = msg.body as Array;
                for each (var message:IMessage in messageList)
                {
                    if (Log.isDebug())
                    {
                        _log.debug("'{0}' channel got message\n{1}\n", channel.id, message.toString());
                        if (channel.mpiEnabled)
                        {
                            try
                            {
                                var mpiutil:MessagePerformanceUtils = new MessagePerformanceUtils(message);
                                _log.debug(mpiutil.prettyPrint());
                            }
                            catch (e:Error)
                            {
                                _log.debug("Could not get message performance information for: " + msg.toString());   
                            }
                        }
                    }
                    channel.dispatchEvent(MessageEvent.createEvent(MessageEvent.MESSAGE, message));
                }
            }
        }
        else if (msg is AcknowledgeMessage)
        {
            // The server returns an empty ack if there are no messages to return. 
            // We don't need to do anything here.            
        }
        else
        {
            // This should never happen.
            // If it does, treat it as a status (error) and return.
        	var errMsg:ErrorMessage = new ErrorMessage();
        	errMsg.faultDetail = resourceManager.getString(
				"messaging", "receivedNull");
        	status(errMsg);
        	return;
       	}
       	
       	// Only set up the next poll if the channel is still connected.
       	// Subscription invalidation commands pushed by the server can cause the channel to disconnect
       	// and it shouldn't issue another poll request in this case.
       	// Also, if the channel is piggybacking but not polling on an interval we don't want to
       	// schedule the next poll.
       	var pollingChannel:PollingChannel = PollingChannel(channel);
       	if (pollingChannel.connected && pollingChannel._shouldPoll)
       	{
           	var adaptivePollWait:int = 0;
           	if (msg.headers[CommandMessage.POLL_WAIT_HEADER] != null)
                adaptivePollWait = msg.headers[CommandMessage.POLL_WAIT_HEADER];
           	
           	// An adaptive polling value of 0 indicates that the channel should use its default
           	// polling interval.           	
           	if (adaptivePollWait == 0)
           	{
               	if (pollingChannel.internalPollingInterval == 0)
                {
                    // No need for a Timer at all if we're polling immediately.
                    pollingChannel.poll();
               	}
               	else if (!pollingChannel.timerRunning)      	
               	{
               	    // Poll at the base rate for this Channel; no adaptive poll wait is defined.
               	    pollingChannel._timer.delay = pollingChannel._pollingInterval;
                   	pollingChannel._timer.start();
                }
            }
            else
            {
                // Use adaptive poll wait.
                pollingChannel._timer.delay = adaptivePollWait;
                pollingChannel._timer.start();
            }
        }
    }

    /**
     *  @private
     *  Handles a fault while attempting to poll.
     * 
     *  @param msg The ErrorMessage from the remote destination.
     */ 
    override protected function statusHandler(msg:IMessage):void
    {        
        var pollingChannel:PollingChannel = PollingChannel(channel);
        pollingChannel.stopPolling(); // Shut down all polling.
        var errMsg:ErrorMessage = msg as ErrorMessage;
        var details:String = (errMsg != null) ? errMsg.faultDetail : "";            
        var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent
            (pollingChannel, false, "Channel.Polling.Error", "error", details);
        faultEvent.rootCause = msg;
        pollingChannel.dispatchEvent(faultEvent);
        
        // Reject this channel if the server does not support polling
        if (errMsg != null && errMsg.faultCode == "Server.PollNotSupported")
        {
        	pollingChannel.pollFailed(true);
        }
        else
        {
        	pollingChannel.pollFailed(false);
        }
    }
}
