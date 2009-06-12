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

package mx.messaging.channels
{

import flash.events.AsyncErrorEvent;
import flash.events.ErrorEvent;
import flash.events.IOErrorEvent;
import flash.events.NetStatusEvent;
import flash.events.SecurityErrorEvent;
import flash.events.TimerEvent;
import flash.net.NetConnection;
import flash.net.ObjectEncoding;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.ISmallMessage;
import mx.messaging.messages.MessagePerformanceInfo;
import mx.messaging.messages.MessagePerformanceUtils;
import mx.utils.StringUtil;

use namespace mx_internal;

/**
 *  This NetConnectionChannel provides the basic NetConnection support for messaging.
 *  The AMFChannel and RTMPChannel both extend this class.
 */
public class NetConnectionChannel extends PollingChannel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates a new NetConnectionChannel instance.
     *  <p>
     *  The underlying NetConnection's <code>objectEncoding</code>
     *  is set to <code>ObjectEncoding.AMF3</code> by default. It can be
     *  changed manually by accessing the channel's <code>netConnection</code>
     *  property. The global <code>NetConnection.defaultObjectEncoding</code>
     *  setting is not honored by this channel.
     *  </p>
     *
     *  @param id The id of this Channel.
     *
     *  @param uri The uri for this Channel.
     */
    public function NetConnectionChannel(id:String = null, uri:String = null)
    {
        super(id, uri);

        _nc = new NetConnection();
        _nc.objectEncoding = ObjectEncoding.AMF3;
        _nc.client = this;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var _appendToURL:String;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  netConnection
    //----------------------------------

    /**
     *  @private
     */
    protected var _nc:NetConnection;

    /**
     *  Provides access to the associated NetConnection for this Channel.
     */
    public function get netConnection():NetConnection
    {
        return _nc;
    }

    //----------------------------------
    //  useSmallmessages
    //----------------------------------

    /**
     * @private
     * If the ObjectEncoding is set to AMF0 we can't support small messages.
     */
    override public function get useSmallMessages():Boolean
    {
        return (super.useSmallMessages && _nc != null && _nc.objectEncoding >= ObjectEncoding.AMF3);
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function connectTimeoutHandler(event:TimerEvent):void
    {
        shutdownNetConnection();
        super.connectTimeoutHandler(event);
    }

    /**
     *  @private
     */
    override protected function getPollSyncMessageResponder(agent:MessageAgent, msg:CommandMessage):MessageResponder
    {
        return new PollSyncMessageResponder(agent, msg, this);
    }

    /**
     *  @private
     */
    override protected function getDefaultMessageResponder(agent:MessageAgent, msg:IMessage):MessageResponder
    {
        return new NetConnectionMessageResponder(agent, msg, this);
    }

    /**
     *  @private
     */
    override protected function internalDisconnect(rejected:Boolean = false):void
    {
        super.internalDisconnect(rejected);
        shutdownNetConnection();
        disconnectSuccess(rejected); // make sure to notify everyone that we have disconnected.
    }

    /**
     *  @private
     */
    override protected function internalConnect():void
    {
        super.internalConnect();
        var url:String = endpoint;
        if (_appendToURL != null)
            url += _appendToURL;

        // If the NetConnection has a non-null uri the Player will close() it automatically
        // as part of its connect() processing below. Pre-emptively close the connection while suppressing
        // NetStatus event handling to avoid spurious events.
        if (_nc.uri != null && _nc.uri.length > 0 && _nc.connected)
        {
            _nc.removeEventListener(NetStatusEvent.NET_STATUS, statusHandler);
            _nc.close();
        }

        _nc.addEventListener(NetStatusEvent.NET_STATUS, statusHandler);
        _nc.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
        _nc.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        _nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR, asyncErrorHandler);

        try
        {
            _nc.connect(url);
        }
        catch(e:Error)
        {
            // In some cases, this error does not have the URL in it (if it is a malformed
            // URL for example) and in others it does (sandbox violation).  Usually this is
            // a URL problem, so add it all of the time even though this means we'll see it
            // twice for the sandbox violation.
            e.message += "  url: '" + url + "'";
            throw e;
        }
    }

    /**
     *  @private
     */
    override protected function internalSend(msgResp:MessageResponder):void
    {
        // Set the global FlexClient Id.
        setFlexClientIdOnMessage(msgResp.message);

        // If MPI is enabled initialize MPI object and stamp it with client send time
        if (mpiEnabled)
        {
            var mpii:MessagePerformanceInfo = new MessagePerformanceInfo();
            if (recordMessageTimes)
                mpii.sendTime = new Date().getTime();
            msgResp.message.headers[MessagePerformanceUtils.MPI_HEADER_IN] = mpii;
        }

        var message:IMessage = msgResp.message;

        // Finally, if "Small Messages" are enabled, send this form instead of
        // the normal message where possible.
        if (useSmallMessages && message is ISmallMessage)
        {
            var smallMessage:IMessage = ISmallMessage(message).getSmallMessage();
            if (smallMessage != null)
                message = smallMessage;
        }

        _nc.call(null, msgResp, message);
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Special handler for legacy AMF packet level header "AppendToGatewayUrl".
     *  When we receive this header we assume the server detected that a session was
     *  created but it believed the client could not accept its session cookie, so we
     *  need to decorate the channel endpoint with the session id.
     *
     *  We do not modify the underlying endpoint property, however, as this session
     *  is transient and should not apply if the channel is disconnected and re-connected
     *  at some point in the future.
     */
    public function AppendToGatewayUrl(value:String):void
    {
        if (value != null && value != "" && value != _appendToURL)
        {
            if (Log.isDebug())
                _log.debug("'{0}' channel will disconnect and reconnect with with its session identifier '{1}' appended to its endpoint url \n", id, value);
            _appendToURL = value;
        }
    }

    /**
     *  @private
     *  Called by the player when the server pushes a message.
     *  Dispatches a MessageEvent to any MessageAgents that are listening.
     *  Any ...rest args passed via RTMP are ignored.
     *
     *  @param msg The message pushed from the server.
     */
    public function receive(msg:IMessage, ...rest:Array):void
    {
        if (Log.isDebug())
        {
            _log.debug("'{0}' channel got message\n{1}\n", id, msg.toString());

            // If MPI is enabled write a performance summary to log
            if (this.mpiEnabled)
            {
                try
                {
                    var mpiutil:MessagePerformanceUtils = new MessagePerformanceUtils(msg);
                    _log.debug(mpiutil.prettyPrint());
                }
                catch (e:Error)
                {
                    _log.debug("Could not get message performance information for: " + msg.toString());
                }
            }
        }

        dispatchEvent(MessageEvent.createEvent(MessageEvent.MESSAGE, msg));
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Utility method to close a NetConnection; includes retry to deal with instances
     *  that have generated a NetStatus error on the same frame and can't be closed immediately.
     *
    protected function closeNetConnection(nc:NetConnection):void
    {
        var closeHelper:CloseHelper = new CloseHelper(_nc);
        closeHelper.close();
    }
    */

    /**
     *  @private
     *  Shuts down the underlying NetConnection for the channel.
     */
    protected function shutdownNetConnection():void
    {
        _nc.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
        _nc.removeEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        _nc.removeEventListener(NetStatusEvent.NET_STATUS, statusHandler);
        _nc.removeEventListener(AsyncErrorEvent.ASYNC_ERROR, asyncErrorHandler);
        _nc.close();
    }

    /**
     *  @private
     *  Called when a status event occurs on the NetConnection.
     *  Descendants must override this method.
     */
    protected function statusHandler(event:NetStatusEvent):void
    {
    }

    /**
     *  @private
     *  If the player rejects a NetConnection request for security reasons,
     *  such as a security sandbox violation, the NetConnection raises a
     *  securityError event which we dispatch as a channel fault.
     */
    protected function securityErrorHandler(event:SecurityErrorEvent):void
    {
        defaultErrorHandler("Channel.Security.Error", event);
    }

    /**
     *  @private
     *  If there is a network problem, the NetConnection raises an
     *  ioError event which we dispatch as a channel fault.
     */
    protected function ioErrorHandler(event:IOErrorEvent):void
    {
        defaultErrorHandler("Channel.IO.Error", event);
    }

    /**
     *  @private
     *  If a problem arises in the native player code asynchronously, this
     *  error event will be dispatched as a channel fault.
     */
    protected function asyncErrorHandler(event:AsyncErrorEvent):void
    {
        defaultErrorHandler("Channel.Async.Error", event);
    }


    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Utility function to dispatch a ChannelFaultEvent at an "error" level
     *  based upon the passed code and ErrorEvent.
     */
    private function defaultErrorHandler(code:String, event:ErrorEvent):void
    {
        var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent
            (this, false, code, "error", event.text + " url: '" + endpoint + "'");
        faultEvent.rootCause = event;

        if (_connecting)
            connectFailed(faultEvent);
        else
            dispatchEvent(faultEvent);
    }

}

}

//------------------------------------------------------------------------------
//
// Private Classes
//
//------------------------------------------------------------------------------

import flash.utils.Timer;
import flash.net.NetConnection;
import flash.events.TimerEvent;

import mx.core.mx_internal;
import mx.messaging.channels.NetConnectionChannel;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.StringUtil;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  @private
 *  This class provides the responder level interface for dispatching message
 *  results from a remote destination.
 *  The NetConnectionChannel creates this handler to manage
 *  the results of a pending operation started when a message is sent.
 *  The message handler is always associated with a MessageAgent
 *  (the object that sent the message) and calls its <code>fault()</code>,
 *  <code>acknowledge()</code>, or <code>message()</code> method as appopriate.
 */
class NetConnectionMessageResponder extends MessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Initializes this instance of the message responder with the specified
     *  agent.
     *
     *  @param agent MessageAgent that this responder should call back when a
     *            message is received.
     *
     *  @param msg The outbound message.
     *
     *  @param channel The channel this responder is using.
     */
    public function NetConnectionMessageResponder(agent:MessageAgent,
                                    msg:IMessage, channel:NetConnectionChannel)
    {
        super(agent, msg, channel);
        channel.addEventListener(ChannelEvent.DISCONNECT, channelDisconnectHandler);
        channel.addEventListener(ChannelFaultEvent.FAULT, channelFaultHandler);
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Called when the result of sending a message is received.
     *
     *  @param msg NetConnectionChannel-specific message data.
     */
    override protected function resultHandler(msg:IMessage):void
    {
        disconnect();
        if (msg is AsyncMessage)
        {
            if (AsyncMessage(msg).correlationId == message.messageId)
            {
                agent.acknowledge(msg as AcknowledgeMessage, message);
            }
            else
            {
                errorMsg = new ErrorMessage();
                errorMsg.faultCode = "Server.Acknowledge.Failed";
                errorMsg.faultString = resourceManager.getString(
                    "messaging", "ackFailed");
                errorMsg.faultDetail = resourceManager.getString(
                    "messaging", "ackFailed.details",
                    [ message.messageId, AsyncMessage(msg).correlationId ]);
                errorMsg.correlationId = message.messageId;
                agent.fault(errorMsg, message);
                //@TODO: need to add constants here
            }
        }
        else
        {
            var errorMsg:ErrorMessage;
            errorMsg = new ErrorMessage();
            errorMsg.faultCode = "Server.Acknowledge.Failed";
            errorMsg.faultString = resourceManager.getString(
                "messaging", "noAckMessage");
            errorMsg.faultDetail = resourceManager.getString(
                "messaging", "noAckMessage.details",
                [ msg ? msg.toString() : "null" ]);
            errorMsg.correlationId = message.messageId;
            agent.fault(errorMsg, message);
        }
    }

    /**
     *  @private
     *  Called when the current invocation fails.
     *  Passes the fault information on to the associated agent that made
     *  the request.
     *
     *  @param msg NetConnectionMessageResponder status information.
     */
    override protected function statusHandler(msg:IMessage):void
    {
        disconnect();

        // even a fault is still an acknowledgement of a message sent so pass it on...
        if (msg is AsyncMessage)
        {
            if (AsyncMessage(msg).correlationId == message.messageId)
            {
                // pass the ack on...
                var ack:AcknowledgeMessage = new AcknowledgeMessage();
                ack.correlationId = AsyncMessage(msg).correlationId;
                ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // add a hint this is for an error
                agent.acknowledge(ack, message);
                // send the fault on...
                agent.fault(msg as ErrorMessage, message);
            }
            else if (msg is ErrorMessage)
            {
                // we can't find a correlation id but do have some sort of error message so just forward
                agent.fault(msg as ErrorMessage, message);
            }
            else
            {
                var errorMsg:ErrorMessage;
                errorMsg = new ErrorMessage();
                errorMsg.faultCode = "Server.Acknowledge.Failed";
                errorMsg.faultString = resourceManager.getString(
                    "messaging", "noErrorForMessage");
                errorMsg.faultDetail = resourceManager.getString(
                    "messaging", "noErrorForMessage.details",
                    [ message.messageId, AsyncMessage(msg).correlationId ]);
                errorMsg.correlationId = message.messageId;
                agent.fault(errorMsg, message);
            }
        }
        else
        {
            errorMsg = new ErrorMessage();
            errorMsg.faultCode = "Server.Acknowledge.Failed";
            errorMsg.faultString = resourceManager.getString(
                "messaging", "noAckMessage");
            errorMsg.faultDetail = resourceManager.getString(
                "messaging", "noAckMessage.details",
                [ msg ? msg.toString(): "null" ]);
            errorMsg.correlationId = message.messageId;
            agent.fault(errorMsg, message);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handle a request timeout by removing ourselves as a listener on the
     *  NetConnection and faulting the message to the agent.
     */
    override protected function requestTimedOut():void
    {
        disconnect();
        statusHandler(createRequestTimeoutErrorMessage());
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handles a disconnect of the underlying Channel before a response is
     *  returned to the responder.
     *  The sent message is faulted and flagged with the ErrorMessage.MESSAGE_DELIVERY_IN_DOUBT
     *  code.
     *
     *  @param event The DISCONNECT event.
     */
    protected function channelDisconnectHandler(event:ChannelEvent):void
    {
        disconnect();
        var errorMsg:ErrorMessage = new ErrorMessage();
        errorMsg.correlationId = message.messageId;
        errorMsg.faultString = resourceManager.getString(
            "messaging", "deliveryInDoubt");
        errorMsg.faultDetail = resourceManager.getString
            ("messaging", "deliveryInDoubt.details");
        errorMsg.faultCode = ErrorMessage.MESSAGE_DELIVERY_IN_DOUBT;
        agent.fault(errorMsg, message);
    }

    /**
     *  @private
     *  Handles a fault of the underlying Channel before a response is
     *  returned to the responder.
     *  The sent message is faulted and flagged with the ErrorMessage.MESSAGE_DELIVERY_IN_DOUBT
     *  code.
     *
     *  @param event The ChannelFaultEvent.
     */
    protected function channelFaultHandler(event:ChannelFaultEvent):void
    {
        disconnect();
        var errorMsg:ErrorMessage = event.createErrorMessage();
        errorMsg.correlationId = message.messageId;
        // if the channel is no longer connected then we don't really
        // know if the message made it to the server.
        if (!event.channel.connected)
        {
            errorMsg.faultCode = ErrorMessage.MESSAGE_DELIVERY_IN_DOUBT;
        }
        agent.fault(errorMsg, message);
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Disconnects the responder from the underlying Channel.
     */
    private function disconnect():void
    {
        channel.removeEventListener(ChannelEvent.DISCONNECT, channelDisconnectHandler);
        channel.removeEventListener(ChannelFaultEvent.FAULT, channelFaultHandler);
    }
}

/**
 *  @private
 *  This class provides a way to synchronize polling with a subscribe or
 *  unsubscribe request.
 *  It is constructed in response to a Consumer sending either a subscribe or
 *  unsubscribe command message.
 *  If a successfull subscribe/unsubscribe is made this responder will inform the
 *  channel which will trigger it to start/stop polling if necessary.
 *
 */
class PollSyncMessageResponder extends NetConnectionMessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructs a PollSyncMessageResponder.
     *
     *  @param agent The agent to manage polling for.
     *
     *  @param msg The subscribe or unsubscribe message for the agent.
     *
     *  @param channel The underlying Channel for the responder.
     */
    public function PollSyncMessageResponder(agent:MessageAgent, msg:IMessage,
                                                channel:NetConnectionChannel)
    {
        super(agent, msg, channel);
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method is called by the player when the result of sending a message
     *  is received.
     *
     *  @param msg The response to a subscribe or unsubscribe request.
     */
    override protected function resultHandler(msg:IMessage):void
    {
        super.resultHandler(msg);
        if ((msg is AsyncMessage) && (AsyncMessage(msg).correlationId == message.messageId))
        {
            // notify the channel
            var cmd:CommandMessage = CommandMessage(message);
            switch (cmd.operation)
            {
                case CommandMessage.SUBSCRIBE_OPERATION:
                    NetConnectionChannel(channel).enablePolling();
                break;

                case CommandMessage.UNSUBSCRIBE_OPERATION:
                    NetConnectionChannel(channel).disablePolling();
                break;
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handles a disconnect of the underlying Channel before a response is
     *  returned to the responder and supresses resend attempts.
     *
     *  @param event The Channel disconnect event.
     */
    override protected function channelDisconnectHandler(event:ChannelEvent):void {}

    /**
     *  @private
     *  Handles a fault of the underlying Channel before a response is
     *  returned to the responder and supresses resend attempts.
     *
     *  @param event The Channel fault event.
     */
    override protected function channelFaultHandler(event:ChannelFaultEvent):void {}
}
