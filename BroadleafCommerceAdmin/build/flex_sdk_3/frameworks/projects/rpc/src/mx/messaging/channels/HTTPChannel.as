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

import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.events.TimerEvent;
import flash.net.URLLoader;
import flash.net.URLRequest;

import mx.core.mx_internal;
import mx.messaging.FlexClient;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.channels.amfx.AMFXDecoder;
import mx.messaging.channels.amfx.AMFXEncoder;
import mx.messaging.channels.amfx.AMFXHeader;
import mx.messaging.channels.amfx.AMFXResult;
import mx.messaging.config.ConfigMap;
import mx.messaging.config.ServerConfig;
import mx.messaging.errors.MessageSerializationError;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.HTTPRequestMessage;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.MessagePerformanceInfo;
import mx.messaging.messages.MessagePerformanceUtils;
import mx.utils.ObjectUtil;
import mx.utils.StringUtil;

use namespace mx_internal;

/**
 *  The HTTPChannel class provides the HTTP support for messaging.
 *  You can configure this Channel to poll the server at an interval
 *  to approximate server push.
 *  You can also use this Channel with polling disabled to send RPC messages
 *  to remote destinations to invoke their methods.
 *
 *  <p>
 *  The HTTPChannel relies on network services native to Flash Player and AIR,
 *  and exposed to ActionScript by the URLLoader class.
 *  This channel uses URLLoader exclusively, and creates a new URLLoader
 *  per request.
 *  </p>
 *
 *  <p>
 *  Channels are created within the framework using the
 *  <code>ServerConfig.getChannel()</code> method. Channels can be constructed
 *  directly and assigned to a ChannelSet if desired.
 *  </p>
 *
 *  <p>
 *  Channels represent a physical connection to a remote endpoint.
 *  Channels are shared across destinations by default.
 *  This means that a client targetting different destinations may use
 *  the same Channel to communicate with these destinations.
 *  </p>
 *
 *  <p>
 *  When used in polling mode, this Channel polls the server for new messages
 *  based on the <code>polling-interval-seconds</code> property in the configuration file,
 *  and this can be changed by setting the <code>pollingInterval</code> property.
 *  The default value is 3 seconds.
 *  To enable polling, the channel must be connected and the <code>polling-enabled</code>
 *  property in the configuration file must be set to <code>true</code>, or the
 *  <code>pollingEnabled</code> property of the Channel must be set to <code>true</code>.
 *  </p>
 */
public class HTTPChannel extends PollingChannel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates an new HTTPChannel instance.
     *
     *  @param id The id of this Channel.
     *  @param uri The uri for this Channel.
     */
    public function HTTPChannel(id:String = null, uri:String = null)
    {
        super(id, uri);

        _encoder = new AMFXEncoder();
        _appendToURL = "";
        _messageQueue = [];
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _appendToURL:String;

    /**
     *  @private
     *  The loader used to ping the server in internalConnect. We need to hang onto a reference
     *  in order to time out a connect attempt.
     */
    private var _connectLoader:ChannelRequestLoader;

    /**
     *  @private
     */
    private var _encoder:AMFXEncoder;

    /**
     *  @private
     *  Records the request that needs to be completed before other
     *  requests can be sent.
     */
    private var _pendingRequest:ChannelRequestLoader = null;

    /**
     *  @private
     *  This queue contains the messages from send requests that
     *  occurred while an authentication attempt is underway.
     */
    private var _messageQueue:Array;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  polling
    //----------------------------------

    /**
     *  Reports whether the channel is actively polling.
     */
    public function get polling():Boolean
    {
        return pollOutstanding;
    }

    //----------------------------------
    //  piggybackingEnabled
    //----------------------------------

    /**
     *  Indicates whether this channel will piggyback poll requests along
     *  with regular outbound messages when an outstanding poll is not in
     *  progress. This allows the server to piggyback data for the client
     *  along with its response to client's message.
     */
    public function get piggybackingEnabled():Boolean
    {
        return internalPiggybackingEnabled;
    }

    /**
     *  @private
     */
    public function set piggybackingEnabled(value:Boolean):void
    {
        internalPiggybackingEnabled = value;
    }

    //----------------------------------
    //  pollingEnabled
    //----------------------------------

    /**
     *  Indicates whether this channel is enabled to poll.
     */
    public function get pollingEnabled():Boolean
    {
        return internalPollingEnabled;
    }

    /**
     *  @private
     */
    public function set pollingEnabled(value:Boolean):void
    {
        internalPollingEnabled = value;
    }

    //----------------------------------
    //  pollingInterval
    //----------------------------------

    /**
     *  Provides access to the polling interval for this Channel.
     *  The value is in milliseconds.
     *  This value determines how often this Channel requests messages from
     *  the server, to approximate server push.
     *
     *  @throws ArgumentError If the pollingInterval is assigned a value of 0 or
     *                        less.
     */
    public function get pollingInterval():Number
    {
        return internalPollingInterval;
    }

    /**
     *  @private
     */
    public function set pollingInterval(value:Number):void
    {
        internalPollingInterval = value;
    }

    //----------------------------------
    //  protocol
    //----------------------------------

    /**
     *  Returns the protocol for this channel (http).
     */
    override public function get protocol():String
    {
        return "http";
    }

    //--------------------------------------------------------------------------
    //
    // Internal Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  appendToURL
    //----------------------------------

    /**
     * @private
     */
    mx_internal function get appendToURL():String
    {
        return _appendToURL;
    }

    /**
     *  @private
     */
    mx_internal function set appendToURL(value:String):void
    {
        if (value && endpoint)
        {
            _appendToURL = value;
       }
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Processes polling related configuration settings.
     */
    override public function applySettings(settings:XML):void
    {
        super.applySettings(settings);
        applyPollingSettings(settings);
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
        _connectLoader.close();
        super.connectTimeoutHandler(event);
    }

    /**
     *  @private
     */
    override protected function getPollSyncMessageResponder(agent:MessageAgent, msg:CommandMessage):MessageResponder
    {
        return new PollSyncHTTPMessageResponder(agent, msg, this);
    }

    /**
     *  @private
     */
    override protected function getDefaultMessageResponder(agent:MessageAgent, msg:IMessage):MessageResponder
    {
        return new HTTPMessageResponder(agent, msg, this);
    }

    /**
     *  @private
     *  Attempts to connect to the remote destination with the current endpoint
     *  specified for this channel.
     *  This will determine if a connection can be established.
     */
    override protected function internalConnect():void
    {
        // Ping the server to make sure that it is reachable.
        var msg:CommandMessage = new CommandMessage();
        if (credentials != null)
        {
            msg.operation = CommandMessage.LOGIN_OPERATION;
            msg.body = credentials;
        }
        else
        {
            msg.operation = CommandMessage.CLIENT_PING_OPERATION;
        }

        // Report the messaging version for this Channel.
        msg.headers[CommandMessage.MESSAGING_VERSION] = messagingVersion;

        // Indicate if requesting the dynamic configuration from the server.
        if (ServerConfig.needsConfig(this))
            msg.headers[CommandMessage.NEEDS_CONFIG_HEADER] = true;

        // Add the FlexClient id header.
        setFlexClientIdOnMessage(msg);

        var urlRequest:URLRequest = createURLRequest(msg);
        _connectLoader = new ChannelRequestLoader();
        _connectLoader.setErrorCallbacks(pingErrorHandler);
        _connectLoader.completeCallback = pingCompleteHandler;
        _connectLoader.load(urlRequest);
    }

    /**
     *  @private
     *  Disconnects from the remote destination.
     */
    override protected function internalDisconnect(rejected:Boolean = false):void
    {
        // Attempt to notify the server of the disconnect.
        if (!rejected)
        {
            var msg:CommandMessage = new CommandMessage();
            msg.operation = CommandMessage.DISCONNECT_OPERATION;
            internalSend(new HTTPFireAndForgetResponder(msg));
        }
        // Shutdown locally.
        setConnected(false);
        super.internalDisconnect(rejected);
        disconnectSuccess(rejected); // make sure to notify everyone that we have disconnected.
    }

    /**
     *  @private
     */
    override protected function internalSend(msgResp:MessageResponder):void
    {
        if (_pendingRequest != null)
        {
            _messageQueue.push(msgResp);
        }
        else
        {
            // Set the global FlexClient Id.
            setFlexClientIdOnMessage(msgResp.message);

            try
            {
                // If MPI is enabled initialize MPI object and stamp it with client send time
                if (mpiEnabled)
                {
                    var mpii:MessagePerformanceInfo = new MessagePerformanceInfo();
                    if (recordMessageTimes)
                        mpii.sendTime = new Date().getTime();
                    msgResp.message.headers[MessagePerformanceUtils.MPI_HEADER_IN] = mpii;
                }

                // Finally, if "Small Messages" are enabled, send this form instead of
                // the normal message where possible.
                /*
                if (useSmallMessages && msgResp.message is ISmallMessage)
                {
                    var smallMessage:IMessage = ISmallMessage(msgResp.message).getSmallMessage();
                    if (smallMessage != null)
                        msgResp.message = smallMessage;
                }
                */

                var urlLoader:ChannelRequestLoader;
                var urlRequest:URLRequest = createURLRequest(msgResp.message);
                if (msgResp is HTTPMessageResponder)
                {
                    var httpMsgResp:HTTPMessageResponder =
                        HTTPMessageResponder(msgResp);
                    urlLoader = httpMsgResp.urlLoader;
                    urlLoader.completeCallback = httpMsgResp.completeHandler;
                    urlLoader.errorCallback = httpMsgResp.errorHandler;
                    urlLoader.ioErrorCallback = httpMsgResp.ioErrorHandler;
                    urlLoader.securityErrorCallback =
                        httpMsgResp.securityErrorHandler;
                }
                else
                {
                    var responderWrapper:HTTPWrapperResponder =
                        new HTTPWrapperResponder(msgResp);
                    urlLoader = new ChannelRequestLoader();
                    urlLoader.completeCallback =
                        responderWrapper.completeHandler;
                    urlLoader.setErrorCallbacks(responderWrapper.errorHandler);
                }
                urlLoader.requestProcessedCallback = requestProcessedHandler;

                // Do not consider poll requests as pending requests to allow
                // clients to send messages while waiting for poll response.
                if (!(msgResp.message is CommandMessage && CommandMessage(msgResp.message).operation == CommandMessage.POLL_OPERATION))
                    _pendingRequest = urlLoader;

                urlLoader.load(urlRequest);
            }
            catch(e:MessageSerializationError)
            {
                msgResp.agent.fault(e.fault, msgResp.message);
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Utility function to handle a connection related ErrorMessage.
     *
     *  @param msg The ErrorMessage returned during a connect attempt.
     */
    mx_internal function connectionError(msg:ErrorMessage):void
    {
        var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent(this, false,
                "Channel.Connect.Failed", "error", msg.faultDetail + " url: '" + endpoint +
                    (_appendToURL != null ? _appendToURL : "") + "'");
        faultEvent.rootCause = msg;
        connectFailed(faultEvent);
    }

    /**
     *  @private
     *  This method will serialize the specified message into a new instance of
     *  a URLRequest and return it.
     *
     *  @param   message Message to serialize
     *  @return  URLRequest
     */
    mx_internal function createURLRequest(message:IMessage):URLRequest
    {
        var result:URLRequest = new URLRequest();
        if (_appendToURL)
            result.url = endpoint + _appendToURL;
        else
            result.url = endpoint;

        result.contentType = HTTPRequestMessage.CONTENT_TYPE_XML;

        var packet:XML = _encoder.encode(message, null);
        result.data = packet.toString();
        result.method = "POST";

        return result;
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    protected function internalPingComplete(msg:AsyncMessage):void
    {
        if (msg != null)
        {
            ServerConfig.updateServerConfigData(msg.body as ConfigMap, endpoint);

            // Set the server assigned FlexClient Id.
            if (FlexClient.getInstance().id == null && msg.headers[AbstractMessage.FLEX_CLIENT_ID_HEADER] != null)
                FlexClient.getInstance().id = msg.headers[AbstractMessage.FLEX_CLIENT_ID_HEADER];
        }

        // Process the features advertised by the server endpoint.
        /*
        if (msg.headers[CommandMessage.MESSAGING_VERSION] != null)
        {
            var serverVersion:Number = msg.headers[CommandMessage.MESSAGING_VERSION] as Number;
            handleServerMessagingVersion(serverVersion);
        }
        */

        connectSuccess();
        if (credentials != null && !(msg is ErrorMessage))
            setAuthenticated(true);
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Special handler for AMFX packet level header "AppendToGatewayUrl".
     *  When we receive this header we assume the server detected that a session
     *  was created but it believed the client could not accept its session
     *  cookie, so we need to decorate the channel endpoint with the session id.
     *
     *  We do not modify the underlying endpoint property, however, as this
     *  session is transient and should not apply if the channel is disconnected
     *  and re-connected at some point in the future.
     */
    private function AppendToGatewayUrl(value:String):void
    {
        if (value != null)
            appendToURL = value;
    }

    private function decodePacket(event:Event):AMFXResult
    {
        var raw:String = String(URLLoader(event.target).data);
        var xmlData:XML = new XML(raw);
        var _decoder:AMFXDecoder = new AMFXDecoder();
        var packet:AMFXResult = _decoder.decode(xmlData);
        return packet;
    }

    /**
     *  @private
     *  Attempts to replicate the packet-level header functionality that AMFChannel
     *  uses for response headers such as AppendToGatewayUrl for session id tracking.
     */
    private function processHeaders(packet:AMFXResult):void
    {
        if (packet.headers != null)
        {
            try
            {
                for (var i:uint = 0; i < packet.headers.length; i++)
                {
                    var header:AMFXHeader = packet.headers[i];
                    if (header != null && header.name == APPEND_TO_URL_HEADER)
                    {
                        AppendToGatewayUrl(String(header.content));
                    }
                }
            }
            catch(e:Error)
            {
            }
        }
    }

    /**
     *  @private
     *  This method indicates that we successfully connected to the endpoint.
     *  Called as a result of the ping operation performed in the
     *  internalConnect() method.
     */
    private function pingCompleteHandler(event:Event):void
    {
        var packet:AMFXResult = decodePacket(event);
        processHeaders(packet);
        var msg:AsyncMessage = packet.result as AsyncMessage;
        if (msg != null && (msg is ErrorMessage) &&
            ErrorMessage(msg).faultCode == "Client.Authentication")
        {
            internalPingComplete(msg);
            var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent(this, false, "Channel.Authentication.Error", "warn");
            faultEvent.rootCause = ErrorMessage(msg);
            dispatchEvent(faultEvent);
        }
        else
        {
            internalPingComplete(msg);
        }
    }

    /**
     *  @private
     *  This method dispatches the appropriate error to any message agents, and
     *  is called as a result of the ping operation performed in the
     *  internalConnect() method.
     */
    private function pingErrorHandler(event:Event):void
    {
        _log.debug("'{0}' fault handler called. {1}", id, event.toString());
        var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent(this, false,
                                                    "Channel.Ping.Failed",
                                                    "error",
                                                    " url: '" + endpoint +
                                                    (_appendToURL == null ? "" : _appendToURL + "'") + "'");
        faultEvent.rootCause = event;
        connectFailed(faultEvent);
    }

    /**
     *  @private
     *  Chains sends for pending messages.
     */
    private function requestProcessedHandler
        (loader:ChannelRequestLoader, event:Event):void
    {
        if (_pendingRequest == loader)
        {
            _pendingRequest = null;
        }
        // TODO: we should do these in a batch for more efficiency and
        // better session maintenance
        while ((_messageQueue.length > 0) && (_pendingRequest == null))
        {
            internalSend(MessageResponder(_messageQueue.shift()));
        }
    }

    //--------------------------------------------------------------------------
    //
    // Static Constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const APPEND_TO_URL_HEADER:String = "AppendToGatewayUrl";
}
}

//------------------------------------------------------------------------------
//
// Private Classes
//
//------------------------------------------------------------------------------

import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.net.URLLoader;
import flash.net.URLRequest;
import mx.core.mx_internal;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.channels.HTTPChannel;
import mx.messaging.channels.amfx.AMFXDecoder;
import mx.messaging.channels.amfx.AMFXHeader;
import mx.messaging.channels.amfx.AMFXResult;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.HTTPRequestMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.StringUtil;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  @private
 *  This responder wraps another MessageResponder with HTTP functionality.
 */
class HTTPWrapperResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructs a HTTPWrappedResponder.
     *
     *  @param wrappedResponder The responder to wrap.
     */
    public function HTTPWrapperResponder(wrappedResponder:MessageResponder)
    {
        super();
        _wrappedResponder = wrappedResponder;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _wrappedResponder:MessageResponder;


    /**
     * @private
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Handles a result returned from the remote destination.
     *
     *  @param event The completion event from the associated URLLoader.
     */
    public function completeHandler(event:Event):void
    {
        var raw:String = String(URLLoader(event.target).data);
        var xmlData:XML = new XML(raw);
        var _decoder:AMFXDecoder = new AMFXDecoder();
        var packet:AMFXResult = _decoder.decode(xmlData);
        if (packet.result is ErrorMessage)
        {
            _wrappedResponder.status(ErrorMessage(packet.result));
        }
        else if (packet.result is AsyncMessage)
        {
            _wrappedResponder.result(AsyncMessage(packet.result));
        }
    }

    /**
     *  @private
     *  Handles an error for an outbound request.
     *
     *  @param event The error event from the associated URLLoader.
     */
    public function errorHandler(event:Event):void
    {
        var msg:ErrorMessage = new ErrorMessage();
        msg.correlationId = _wrappedResponder.message.messageId;
        msg.faultCode = "Server.Error.Request";
        msg.faultString = resourceManager.getString(
            "messaging", "httpRequestError");
        var details:String = event.toString();
        if (_wrappedResponder.message is HTTPRequestMessage)
        {
            details += ". URL: ";
            details += HTTPRequestMessage(_wrappedResponder.message).url;
        }
        msg.faultDetail = resourceManager.getString(
            "messaging", "httpRequestError.details", [ details ]);
        msg.rootCause = event;
        _wrappedResponder.status(msg);
    }
}


[ResourceBundle("messaging")]

/**
 *  @private
 *  This is an adapter for url loader that is used by the HTTPChannel.
 */
class HTTPMessageResponder extends MessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructs an HTTPMessageResponder.
     *
     *  @param agent The associated MessageAgent.
     *
     *  @param msg The message to send.
     *
     *  @param channel The Channel to send the message over.
     */
    public function HTTPMessageResponder
        (agent:MessageAgent, msg:IMessage, channel:HTTPChannel)
    {
        super(agent, msg, channel);
        decoder = new AMFXDecoder();
        urlLoader = new ChannelRequestLoader();
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var decoder:AMFXDecoder;

    /**
     * @private
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  The loader associated with this responder.
     */
    public var urlLoader:ChannelRequestLoader;

    //--------------------------------------------------------------------------
    //
    // Overridden Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function resultHandler(response:IMessage):void
    {
        var errorMsg:ErrorMessage;

        if (response is AsyncMessage)
        {
            if (response is ErrorMessage)
            {
                agent.fault(ErrorMessage(response), message);
            }
            else if (AsyncMessage(response).correlationId == message.messageId)
            {
                agent.acknowledge(AcknowledgeMessage(response), message);
            }
            else
            {
                errorMsg = new ErrorMessage();
                errorMsg.faultCode = "Server.Acknowledge.Failed";
                errorMsg.faultString = resourceManager.getString(
                    "messaging", "ackFailed");
                errorMsg.faultDetail = resourceManager.getString(
                    "messaging", "ackFailed.details",
                    [ message.messageId, AsyncMessage(response).correlationId ]);
                agent.fault(errorMsg, message);
            }
        }
        else if (response != null)
        {
            errorMsg = new ErrorMessage();
            errorMsg.faultCode = "Server.Acknowledge.Failed";
            errorMsg.faultString = resourceManager.getString(
                "messaging", "noAckMessage");
            errorMsg.faultDetail = resourceManager.getString(
                "messaging", "noAckMessage.details",
                [ mx.utils.ObjectUtil.toString(response) ]);
            agent.fault(errorMsg, message);
        }
    }

    /**
     *  @private
     *  Handle a request timeout by closing our associated URLLoader and
     *  faulting the message to the agent.
     */
    override protected function requestTimedOut():void
    {
        urlLoader.close();

        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send the fault
        agent.fault(createRequestTimeoutErrorMessage(), message);
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    final public function completeHandler(event:Event):void
    {
        result(null);

        var raw:String = String(URLLoader(event.target).data);
        var xmlData:XML = new XML(raw);
        var packet:AMFXResult = decoder.decode(xmlData);

        if (packet.result is IMessage)
        {
            resultHandler(IMessage(packet.result));
        }
    }

    /**
     *  @private
     */
    public function errorHandler(event:Event):void
    {
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send fault
        var msg:ErrorMessage = new ErrorMessage();
        msg.correlationId = message.messageId;
        msg.faultCode = "Server.Error.Request";
        msg.faultString = resourceManager.getString(
            "messaging", "httpRequestError");
        var details:String = event.toString();
        if (message is HTTPRequestMessage)
        {
            details += ". URL: ";
            details += HTTPRequestMessage(message).url;
        }
        msg.faultDetail = resourceManager.getString(
            "messaging", "httpRequestError.details", [ details ]);
        msg.rootCause = event;
        agent.fault(msg, message);
    }

    /**
     *  @private
     */
    public function ioErrorHandler(event:Event):void
    {
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send fault
        var msg:ErrorMessage = new ErrorMessage();
        msg.correlationId = message.messageId;
        msg.faultCode = "Server.Error.Request";
        msg.faultString = resourceManager.getString(
            "messaging", "httpRequestError");
        var details:String = event.toString();
        if (message is HTTPRequestMessage)
        {
            details += ". URL: ";
            details += HTTPRequestMessage(message).url;
        }
        msg.faultDetail = resourceManager.getString(
            "messaging", "httpRequestError.details", [ details ]);
        msg.rootCause = event;

        (channel as HTTPChannel).connectionError(msg);
    }

    /**
     *  @private
     */
    public function securityErrorHandler(event:Event):void
    {
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send fault
        var msg:ErrorMessage = new ErrorMessage();
        msg.correlationId = message.messageId;
        msg.faultCode = "Channel.Security.Error";
        msg.faultString = resourceManager.getString(
            "messaging", "securityError");
        msg.faultDetail = resourceManager.getString(
            "messaging", "securityError.details", [ message.destination ]);
        msg.rootCause = event;
        agent.fault(msg, message);
    }
}

/**
 *  @private
 *  Wraps an URLLoader and manages dispatching its events to the proper handlers.
 */
class ChannelRequestLoader
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructs a ChannelRequestLoader.
     */
    public function ChannelRequestLoader()
    {
        super();
        _urlLoader = new URLLoader();
        _urlLoader.addEventListener(ErrorEvent.ERROR, errorHandler);
        _urlLoader.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        _urlLoader.addEventListener
            (SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
        _urlLoader.addEventListener(Event.COMPLETE, completeHandler);
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The wrapped URLLoader.
     */
    private var _urlLoader:URLLoader;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public var errorCallback:Function;

    /**
     *  @private
     */
    public var ioErrorCallback:Function;

    /**
     *  @private
     */
    public var securityErrorCallback:Function;

    /**
     *  @private
     */
    public var completeCallback:Function;

    /**
     *  @private
     */
    public var requestProcessedCallback:Function;

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function load(request:URLRequest):void
    {
        _urlLoader.load(request);
    }

    /**
     *  @private
     */
    public function close():void
    {
        _urlLoader.removeEventListener(ErrorEvent.ERROR, errorHandler);
        _urlLoader.removeEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        _urlLoader.removeEventListener
            (SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
        _urlLoader.removeEventListener(Event.COMPLETE, completeHandler);
        _urlLoader.close();
    }

    /**
     *  @private
     */
    public function setErrorCallbacks(callback:Function):void
    {
        errorCallback = callback;
        ioErrorCallback = callback;
        securityErrorCallback = callback;
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function callRequestProcessedCallback(event:Event):void
    {
        if (requestProcessedCallback != null)
            requestProcessedCallback(this, event);
    }

    /**
     *  @private
     */
    private function callEventCallback(callback:Function, event:Event):void
    {
        if (callback != null)
            callback(event);
    }

    /**
     *  @private
     */
    private function errorHandler(event:Event):void
    {
        callRequestProcessedCallback(event);
        callEventCallback(requestProcessedCallback, event);
    }

    /**
     *  @private
     */
    private function ioErrorHandler(event:Event):void
    {
        callRequestProcessedCallback(event);
        callEventCallback(ioErrorCallback, event);
    }

    /**
     *  @private
     */
    private function securityErrorHandler(event:Event):void
    {
        callRequestProcessedCallback(event);
        callEventCallback(securityErrorCallback, event);
    }

    /**
     *  @private
     */
    private function completeHandler(event:Event):void
    {
        callRequestProcessedCallback(event);
        callEventCallback(completeCallback, event);
    }
}

/**
 *  @private
 *  This class provides a way to synchronize polling with a subscribe or
 *  unsubscribe request.  It is constructed in response to a consumer sending
 *  either a subscribe or unsubscribe command message.  If a successfull
 *  subscribe/unsubscribe is made this responder will inform the channel
 *  appropriately.
 *
 *  See the PollSyncMessageResponder in NetConnectionChannel - the prototype.
 */
class PollSyncHTTPMessageResponder extends HTTPMessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructs a PollSyncHTTPMessageResponder.
     *
     *  @param agent The associated MessageAgent.
     *
     *  @param msg The subscribe or unsubscribe message.
     *
     *  @param channel The Channel used to send the message.
     */
    public function PollSyncHTTPMessageResponder
        (agent:MessageAgent, msg:IMessage, channel:HTTPChannel)
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
     */
    override protected function resultHandler(response:IMessage):void
    {
        super.resultHandler(response);
        if ((response is AsyncMessage) && (AsyncMessage(response).correlationId == message.messageId))
        {
            // notify the channel
            var cmd:CommandMessage = CommandMessage(message);
            switch (cmd.operation)
            {
                case CommandMessage.SUBSCRIBE_OPERATION:
                    HTTPChannel(channel).enablePolling();
                break;

                case CommandMessage.UNSUBSCRIBE_OPERATION:
                    HTTPChannel(channel).disablePolling();
                break;
            }
        }
    }
}

/**
 *  Helper class for sending a fire-and-forget disconnect message.
 */
class HTTPFireAndForgetResponder extends MessageResponder
{
    public function HTTPFireAndForgetResponder(message:IMessage)
    {
        super(null, message, null);
    }
}
