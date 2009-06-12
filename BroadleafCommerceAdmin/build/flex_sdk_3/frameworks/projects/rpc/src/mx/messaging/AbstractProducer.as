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

import flash.errors.IllegalOperationError;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;
import mx.logging.Log;
import mx.messaging.config.ConfigMap;
import mx.messaging.config.ServerConfig;
import mx.messaging.errors.InvalidDestinationError;
import mx.messaging.errors.MessagingError;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageAckEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  The AbstractProducer is the base class for the Producer and
 *  MultiTopicConsumer classes. 
 *  You use these classes to push messages to the server.
 */
public class AbstractProducer extends MessageAgent
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    public function AbstractProducer()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  A connect message to use for (re)connect attempts which allows the underlying
     *  ChannelSet to de-dupe if multiple reconnects queue up at the channel layer.
     */
    private var _connectMsg:CommandMessage;
    
    /**
     *  @private
     *  This is the current number of reconnect attempts that we've done.
     */
    private var _currentAttempt:int;    
        
    /**
     *  @private
     *  The timer used for reconnect attempts.
     */
    private var _reconnectTimer:Timer;

    /**
     *  @private
     *  Indicates whether this agent should be connected or not.
     */
    protected var _shouldBeConnected:Boolean;

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
    //  autoConnect
    //----------------------------------

    /**
     *  @private
     */
    private var _autoConnect:Boolean = true;
    
    [Bindable(event="propertyChange")]
    
    /**
     *  If <code>true</code> the Producer automatically connects to its destination the
     *  first time the <code>send()</code> method is called.
     *  If <code>false</code> then the <code>connect()</code> method must be called explicitly to 
     *  establish a connection to the destination.
     *  By default this property is <code>true</code>, but applications that need to operate
     *  in an offline mode may set this to <code>false</code> to prevent the <code>send()</code> method
     *  from connecting implicitly.
     */
    public function get autoConnect():Boolean
    {
        return _autoConnect;
    }
    
    /**
     *  @private
     */
    public function set autoConnect(value:Boolean):void
    {
        if (_autoConnect != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "autoConnect", _autoConnect, value);
            _autoConnect = value;
            dispatchEvent(event);
        }
    }

    //----------------------------------
    //  defaultHeaders
    //----------------------------------

    /**
     *  @private
     */
    private var _defaultHeaders:Object;

    [Bindable(event="propertyChange")]

    /**
     *  The default headers to apply to messages sent by the Producer.
     *  Any default headers that do not exist in the message will be created.
     *  If the message already contains a matching header, the value in the 
     *  message takes precedence and the default header value is ignored. 
     */
    public function get defaultHeaders():Object
    {
        return _defaultHeaders;
    }
    
    /**
     *  @private
     */
    public function set defaultHeaders(value:Object):void
    {
        if (_defaultHeaders != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "defaultHeaders", _defaultHeaders, value);
            _defaultHeaders = value;
            dispatchEvent(event);
        }        
    }   
    
    //----------------------------------
    //  reconnectAttempts
    //----------------------------------
    
    /**
     *  @private
     */
    private var _reconnectAttempts:int;

    [Bindable(event="propertyChange")]

    /**
     *  The number of reconnect attempts that the Producer makes in the event
     *  that the destination is unavailable or the connection to the destination closes. 
     *  A value of -1 enables infinite attempts.
     *  A value of zero disables reconnect attempts.
     *  
     *  <p>Reconnect attempts are made at a constant rate according to the reconnect interval
     *  value. When a reconnect attempt is made if the underlying channel for the Producer is not
     *  connected or attempting to connect the channel will start a connect attempt. 
     *  Subsequent Producer reconnect attempts that occur while the underlying
     *  channel connect attempt is outstanding are effectively ignored until
     *  the outstanding channel connect attempt succeeds or fails.</p>
     * 
     *  @see mx.messaging.Producer#reconnectInterval
     */
    public function get reconnectAttempts():int
    {
        return _reconnectAttempts;
    }
    
    /**
     *  @private
     */
    public function set reconnectAttempts(value:int):void
    {
        if (_reconnectAttempts != value)
        {
            if (value == 0)
                stopReconnectTimer();
            
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "reconnectAttempts", _reconnectAttempts, value);
            _reconnectAttempts = value;
            dispatchEvent(event);
        }  
    }
    
    //----------------------------------
    //  reconnectInterval
    //----------------------------------

    /**
     *  @private
     */
    private var _reconnectInterval:int;

    [Bindable(event="propertyChange")]

    /**
     *  The number of milliseconds between reconnect attempts.
     *  If a Producer doesn't receive an acknowledgement for a connect
     *  attempt, it will wait the specified number of milliseconds before 
     *  making a subsequent reconnect attempt. 
     *  Setting the value to zero disables reconnect attempts.
     *  
     *  <p>Reconnect attempts are made at a constant rate according to this
     *  value. When a reconnect attempt is made if the underlying channel for the Producer is not
     *  connected or attempting to connect the channel will start a connect attempt. 
     *  Subsequent Producer reconnect attempts that occur while the underlying
     *  channel connect attempt is outstanding are effectively ignored until
     *  the outstanding channel connect attempt succeeds or fails.</p>
     * 
     *  @see mx.messaging.Producer#reconnectInterval  
     * 
     *  @throws ArgumentError If the assigned value is negative.
     */
    public function get reconnectInterval():int
    {
        return _reconnectInterval;
    } 
     
    /**
     *  @private
     */
    public function set reconnectInterval(value:int):void
    {
        if (_reconnectInterval != value)
        {
            if (value < 0)
            {
                var message:String = resourceManager.getString(
                    "messaging", "reconnectIntervalNegative");
                throw new ArgumentError(message);
            }
            else if (value == 0)
            {
                stopReconnectTimer();
            }    
            else if (_reconnectTimer != null)
            {
                _reconnectTimer.delay = value;
            }
            
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "reconnectInterval", _reconnectInterval, value);
            _reconnectInterval = value;
            dispatchEvent(event);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Overridden Public Methods
    // 
    //--------------------------------------------------------------------------
        
    /**
     *  @private
     *  Custom processing for message acknowledgments. 
     *  Specifically, re/connect acknowledgements.
     * 
     *  @param ackMsg The AcknowledgeMessage.
     * 
     *  @param msg The original message.
     */
    override public function acknowledge(ackMsg:AcknowledgeMessage, msg:IMessage):void
    {        
        // Ignore acks for any outstanding messages that return after disconnect() is invoked.
        if (_disconnectBarrier)
            return;
        
        super.acknowledge(ackMsg, msg);
        if (msg is CommandMessage && CommandMessage(msg).operation == CommandMessage.TRIGGER_CONNECT_OPERATION)
            stopReconnectTimer();
    }
    
    /**
     *  @private
     *  The Producer suppresses ErrorMessage processing if the fault is for a connect
     *  attempt that is being retried.
     * 
     *  @param errMsg The ErrorMessage describing the fault.
     * 
     *  @param msg The original message.
     */
    override public function fault(errMsg:ErrorMessage, msg:IMessage):void
    {
        internalFault(errMsg, msg);
    }

    /**
     *  @private
     *  Custom processing to start up a reconnect timer if our channel is
     *  disconnected when we should be connected.
     * 
     *  @param event The ChannelEvent.
     */
    override public function channelDisconnectHandler(event:ChannelEvent):void
    {
        super.channelDisconnectHandler(event);
        
        if (_shouldBeConnected && !event.rejected)
            startReconnectTimer();
    }    

    /**
     *  @private
     *  Custom processing to start up a reconnect timer if our channel faults
     *  when we should be connected.
     * 
     *  @param event The ChannelFaultEvent.
     */
    override public function channelFaultHandler(event:ChannelFaultEvent):void
    {
        super.channelFaultHandler(event);
        
        if (_shouldBeConnected && !event.rejected && !event.channel.connected)
            startReconnectTimer();
    }
    
    /**
     *  Disconnects the Producer from its remote destination.
     *  This method does not wait for outstanding network operations to complete.
     *  After invoking <code>disconnect()</code>, the Producer will report that it is not
     *  connected and it will not receive any outstanding message acknowledgements or faults.
     *  Disconnecting stops automatic reconnect attempts if they are running.
     */
    override public function disconnect():void
    {
        _shouldBeConnected = false; // Prevent reconnect attempts.
            
        stopReconnectTimer();
            
        super.disconnect();
    }    

    //--------------------------------------------------------------------------
    //
    // Public Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Connects the Producer to its target destination.
     *  When a connection is established the <code>connected</code> property will
     *  change to <code>true</code> and this property is bindable and generates
     *  <code>PropertyChangeEvent</code>s.
     *  The internal TRIGGER_CONNECT_OPERATION CommandMessage that is sent will result
     *  in an acknowledge or fault event depending upon whether the underlying channel
     *  establishes its connection.
     * 
     *  @throws mx.messaging.errors.InvalidDestinationError  If no destination is set.
     * 
     *  @example
     *  <pre>
     *     var producer:Producer = new Producer();
     *     producer.destination = "TestTopic";
     *     producer.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, handleConnect);
     *     producer.connect();
     *  </pre>
     */
    public function connect():void
    {
        if (!connected)
        {
            _shouldBeConnected = true;
            
            if (_connectMsg == null)
                _connectMsg = buildConnectMessage();   
            internalSend(_connectMsg, false);         
        }        
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Sends the specified message to its destination.
     *  If the producer is being used for publish/subscribe messaging, only messages of type AsyncMessage
     *  should be sent unless a custom message type is being used and the 
     *  message destination on the server has been configured to process the
     *  custom message type.
     *
     *  @param message The Message to send.
     * 
     *  @throws mx.messaging.errors.InvalidDestinationError  If no destination is set.
     * 
     *  @example
     *  <pre>
     *     var producer:Producer = new Producer();
     *     producer.destination = "TestTopic";
     *     var msg:AsyncMessage = new AsyncMessage();
     *     msg.body = "test message";
     *     producer.send(msg);
     *  </pre>
     *
     */
    public function send(message:IMessage):void
    {
        if (!connected && autoConnect)
            _shouldBeConnected = true;
        
        if (defaultHeaders != null)
        {
            for (var header:String in defaultHeaders)
            {
                if (!message.headers.hasOwnProperty(header))
                    message.headers[header] = defaultHeaders[header];
            }
        }
        
        if (!connected && !autoConnect)
        {
            _shouldBeConnected = false;
            var errMsg2:ErrorMessage = new ErrorMessage();
            errMsg2.faultCode = "Client.Error.MessageSend";
            errMsg2.faultString = resourceManager.getString(
                "messaging", "producerSendError");
            errMsg2.faultDetail = resourceManager.getString(
                "messaging", "producerSendErrorDetails");
            errMsg2.correlationId = message.messageId;
            internalFault(errMsg2, message, false, true);
        }        
        else 
        {
            if (Log.isInfo())
                _log.info("'{0}' {1} sending message '{2}'", id, _agentType, message.messageId);        
            internalSend(message); 
        }
    }

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The Producer suppresses ErrorMessage processing if the fault is for a connect
     *  attempt that is being retried.
     * 
     *  @param errMsg The ErrorMessage describing the fault.
     * 
     *  @param msg The original message.
     * 
     *  @param routeToStore currently not used.  Previously was a flag used to
     *  indicate if the faulted message shoudl be stored offline to retry.
     * 
     *  @param ignoreDisconnectBarrier If true the message is faulted regardless 
     *  of whether disconnect() has been invoked. Generally a disconnect() will 
     *  suppress pending acks and faults.
     */
    mx_internal function internalFault(errMsg:ErrorMessage, msg:IMessage, 
                                       routeToStore:Boolean = true, 
                                       ignoreDisconnectBarrier:Boolean = false):void
    {
        // Ignore faults for any outstanding messages that return after disconnect() is invoked.
        if (_disconnectBarrier && !ignoreDisconnectBarrier)
            return;
        
        if (msg is CommandMessage && CommandMessage(msg).operation == CommandMessage.TRIGGER_CONNECT_OPERATION)
        {
            if (_reconnectTimer == null)
            {                
                // If this error correlates to our current connect message,
                // we should no longer be connected.
                if ((_connectMsg != null) && (errMsg.correlationId == _connectMsg.messageId))
                {
                    _shouldBeConnected = false;
                    // Improve the messaging.
                    var errMsg2:ErrorMessage = buildConnectErrorMessage();
                    errMsg2.rootCause = errMsg.rootCause;
                    super.fault(errMsg2, msg);
                }
                else
                {
                    super.fault(errMsg, msg);
                }
            }
            // Else, suppress the fault dispatch because the reconnect timer
            // is running and will generate a fault when it runs out of
            // allowed reconnect attempts.
        }
        else
        {
            super.fault(errMsg, msg);
        }
    }    

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Attempt to reconnect.  This can be called directly or
     *  from a Timer's event handler.
     * 
     *  @param event The timer event for reconnect attempts.
     */
    protected function reconnect(event:TimerEvent):void
    {
        // If we're past our limit of attempts, fault out.
        if ((_reconnectAttempts != -1) && 
            (_currentAttempt >= _reconnectAttempts))
        {
            stopReconnectTimer();
            _shouldBeConnected = false;
            fault(buildConnectErrorMessage(), _connectMsg);
            return;
        }
        
        if (Log.isDebug())
            _log.debug("'{0}' {1} trying to reconnect.", id, _agentType);
        
        _reconnectTimer.delay = _reconnectInterval;
        _currentAttempt++;
        if (_connectMsg == null)
            _connectMsg = buildConnectMessage();

        internalSend(_connectMsg, false);
    }    
    
    /**
     *  @private
     *  This method will start a timer which attempts to reconnect 
     *  periodically. 
     */
    protected function startReconnectTimer():void
    {
        if (_shouldBeConnected && (_reconnectTimer == null))
        {
            // If we're configured for reconnect set up the timer.
            if ((_reconnectAttempts != 0) && (_reconnectInterval > 0))
            {
                if (Log.isDebug())
                    _log.debug("'{0}' {1} starting reconnect timer.", id, _agentType);                                     
                /* 
                 * Initially, the timeout is set to 1 so we try to 
                 * reconnect immediately (perhaps to a different channel).
                 * after that, it will poll at the configured time interval.
                 */
                _reconnectTimer = new Timer(1);
                _reconnectTimer.addEventListener(TimerEvent.TIMER, reconnect);
                _reconnectTimer.start();
                _currentAttempt = 0;
            }            
        }
    }
    
    /**
     * @private 
     * Stops a reconnect timer if one is running.
     */
    protected function stopReconnectTimer():void
    {
        if (_reconnectTimer != null)
        {
            if (Log.isDebug())
                _log.debug("'{0}' {1} stopping reconnect timer.", id, _agentType);
                
            _reconnectTimer.removeEventListener(TimerEvent.TIMER, reconnect);
            _reconnectTimer.reset();
            _reconnectTimer = null;
        }
    } 

    //--------------------------------------------------------------------------
    //
    // Private Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Builds an ErrorMessage for a failed connect attempt.
     * 
     *  @return The ErrorMessage.
     */
    private function buildConnectErrorMessage():ErrorMessage
    {
        var errMsg:ErrorMessage = new ErrorMessage();
        errMsg.faultCode = "Client.Error.Connect";
        errMsg.faultString = resourceManager.getString(
            "messaging", "producerConnectError");
        errMsg.faultDetail = resourceManager.getString(
            "messaging", "failedToConnect");
        errMsg.correlationId = _connectMsg.messageId;
        return errMsg;
    }
    
    /**
     *  @private
     *  Builds a 'connect' message to use for a connect attempt.
     *  
     *  @return The 'connect' CommandMessage.
     */
    private function buildConnectMessage():CommandMessage
    {
        var msg:CommandMessage = new CommandMessage();
        msg.operation = CommandMessage.TRIGGER_CONNECT_OPERATION;
        msg.clientId = clientId;
        msg.destination = destination;
        return msg;
    }
    
}

}
