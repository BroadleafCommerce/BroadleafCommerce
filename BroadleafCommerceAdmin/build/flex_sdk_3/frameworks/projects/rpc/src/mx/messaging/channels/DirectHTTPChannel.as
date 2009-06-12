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
import flash.net.URLRequestHeader;
import flash.net.URLVariables;

import mx.core.mx_internal;
import mx.messaging.Channel;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.errors.ChannelError;
import mx.messaging.errors.InvalidChannelError;
import mx.messaging.errors.MessageSerializationError;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.HTTPRequestMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

[ExcludeClass]

/**
 *  @private
 *  The DirectHTTPChannel class is used to turn an HTTPRequestMessage object into an
 *  HTTP request.
 *  This Channel does not connect to a Flex endpoint.
 */
public class DirectHTTPChannel extends Channel
{
    /**
    *  Constructs an instance of a DirectHTTPChannel.
    *  The parameters are not used.
    */
    public function DirectHTTPChannel(id:String, uri:String="")
    {
        super(id, uri);
        if (uri.length > 0)
		{
			var message:String = resourceManager.getString(
				"messaging", "noURIAllowed");
            throw new InvalidChannelError(message);
		}
        clientId = ("DirectHTTPChannel" + clientCounter++);
    }

    /**
     * @private
     * Used by DirectHTTPMessageResponder to specify a dummy clientId for AcknowledgeMessages.
     * Each instance of this channel gets a new clientId.
     */ 
    mx_internal var clientId:String;

    /**
     *  @private
     */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    /**
     *  Indicates if this channel is connected.
     */
    override public function get connected():Boolean
    {
        return true;
    }
    
    /**
     *  Indicates the protocol used by this channel.
     */
    override public function get protocol():String
    {
        return "http";     
    }
    
	//----------------------------------
	//  realtime
	//----------------------------------
    
    /**
     *  @private
     *  Returns true if the channel supports realtime behavior via server push or client poll.
     */
    override mx_internal function get realtime():Boolean
    {
        return false;
    }    

    /**
     *  @private
     *  Because this channel is always "connected", we ignore any connect timeout
     *  that is reported.
     */
    override protected function connectTimeoutHandler(event:TimerEvent):void
	{
	    // Ignore.
	}

    /**
     *  Returns the appropriate MessageResponder for the Channel.
     *
     *  @param agent The MessageAgent sending the message.
     * 
     *  @param message The IMessage to send.
     * 
     *  @return The MessageResponder to handle the send result or fault.
     */
    override protected function getMessageResponder(agent:MessageAgent, 
                                            message:IMessage):MessageResponder
    {
        return new DirectHTTPMessageResponder(agent, message, this, new URLLoader());
    }

    /**
     *  Because this channel doesn't participate in hunting we will always assume
     *  that we have connected.
     *
     *  @private
     */
    override protected function internalConnect():void
    {
        connectSuccess();
    }

    override protected function internalSend(msgResp:MessageResponder):void
    {
        var httpMsgResp:DirectHTTPMessageResponder = DirectHTTPMessageResponder(msgResp);
        var urlRequest:URLRequest;

        try
        {
            urlRequest = createURLRequest(httpMsgResp.message);
        }
        catch(e: MessageSerializationError)
        {
            httpMsgResp.agent.fault(e.fault, httpMsgResp.message);
            return;
        }

        var urlLoader:URLLoader = httpMsgResp.urlLoader;
        urlLoader.addEventListener(ErrorEvent.ERROR, httpMsgResp.errorHandler);
        urlLoader.addEventListener(IOErrorEvent.IO_ERROR, httpMsgResp.errorHandler);
        urlLoader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, httpMsgResp.securityErrorHandler);
        urlLoader.addEventListener(Event.COMPLETE, httpMsgResp.completeHandler);
        urlLoader.load(urlRequest);
    }

    /**
     * @private
     */
    /*override */mx_internal function createURLRequest(message:IMessage):URLRequest
    {
        var httpMsg:HTTPRequestMessage = HTTPRequestMessage(message);
        var result:URLRequest = new URLRequest();
        var url:String = httpMsg.url;
        var params:String = null;

        result.contentType = httpMsg.contentType;
        
        var contentTypeIsXML:Boolean = 
            result.contentType == HTTPRequestMessage.CONTENT_TYPE_XML 
            || result.contentType == HTTPRequestMessage.CONTENT_TYPE_SOAP_XML;

        var headers:Object = httpMsg.httpHeaders;
        if (headers)
        {
            var requestHeaders:Array = [];
            var header:URLRequestHeader;
            for (var h:String in headers)
            {
                header = new URLRequestHeader(h, headers[h]);
                requestHeaders.push(header);
            }
            result.requestHeaders = requestHeaders;
        }

        if (!contentTypeIsXML)
        {
            var urlVariables:URLVariables = new URLVariables();
            var body:Object = httpMsg.body;
            for (var p:String in body)
                urlVariables[p] = httpMsg.body[p];

            params = urlVariables.toString();
        }

        if (httpMsg.method == HTTPRequestMessage.POST_METHOD || contentTypeIsXML)
        {
            result.method = "POST";
            if (result.contentType == HTTPRequestMessage.CONTENT_TYPE_FORM)
                result.data = params;
            else
            {
                // For XML content, work around bug 196450 by calling 
                // XML.toXMLString() ourselves as URLRequest.data uses
                // XML.toString() hence bug 184950.
                if (httpMsg.body != null && httpMsg.body is XML)
                    result.data = XML(httpMsg.body).toXMLString();
                else
                    result.data = httpMsg.body;
            }
        }
        else
        {
            if (params && params != "")
            {
                url += (url.indexOf("?") > -1) ? '&' : '?';
                url += params;
            }
        }
        result.url = url;

        return result;
    }

    override public function setCredentials(credentials:String, agent:MessageAgent=null, charset:String=null):void
    {
		var message:String = resourceManager.getString(
			"messaging", "authenticationNotSupported");
   		throw new ChannelError(message);
    }

    /**
     * @private
     * Incremented per new instance of the channel to create clientIds.
     */
    private static var clientCounter:uint;
}
}

import flash.events.Event;
import flash.events.ErrorEvent;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.net.URLLoader;
import mx.core.mx_internal;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.messaging.channels.DirectHTTPChannel;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.HTTPRequestMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  @private
 *  This is an adapter for url loader that is used by the HTTPChannel.
 */
class DirectHTTPMessageResponder extends MessageResponder
{
	//--------------------------------------------------------------------------
	//
	// Constructor
	// 
	//--------------------------------------------------------------------------    
    
    /**
     *  Constructs a DirectHTTPMessageResponder.
     */
    public function DirectHTTPMessageResponder(agent:MessageAgent, msg:IMessage, 
                                        channel:DirectHTTPChannel, urlLoader:URLLoader)
    {
        super(agent, msg, channel);
        this.urlLoader = urlLoader;
        clientId = channel.clientId;
    }

    /**
     *  The URLLoader associated with this responder.
     */
    public var urlLoader:URLLoader;
    
    private var clientId:String;
    
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
     */
    public function errorHandler(event:Event):void
    {
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.clientId = clientId;
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send fault
        var msg:ErrorMessage = new ErrorMessage();
        msg.clientId = clientId;
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
    public function securityErrorHandler(event:Event):void
    {
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.clientId = clientId;
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send fault
        var msg:ErrorMessage = new ErrorMessage();
        msg.clientId = clientId;
        msg.correlationId = message.messageId;
        msg.faultCode = "Channel.Security.Error";
        msg.faultString = resourceManager.getString(
			"messaging", "securityError");
        msg.faultDetail = resourceManager.getString(
			"messaging", "securityError.details", [ message.destination ]);
        msg.rootCause = event;
        agent.fault(msg, message);
    }

    /**
     *  @private
     */
    public function completeHandler(event:Event):void
    {
        result(null);
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.clientId = clientId;
        ack.correlationId = message.messageId;
        ack.body = URLLoader(event.target).data;
        agent.acknowledge(ack, message);
    }
    
    /**
     *  Handle a request timeout by closing our associated URLLoader and
     *  faulting the message to the agent.
     */
    override protected function requestTimedOut():void
    {
        urlLoader.removeEventListener(ErrorEvent.ERROR, errorHandler);
        urlLoader.removeEventListener(IOErrorEvent.IO_ERROR, errorHandler);
	    urlLoader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
	    urlLoader.removeEventListener(Event.COMPLETE, completeHandler);
        urlLoader.close();
        
        status(null);
        // send the ack
        var ack:AcknowledgeMessage = new AcknowledgeMessage();
        ack.clientId = clientId;
        ack.correlationId = message.messageId;
        ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER] = true; // hint there was an error
        agent.acknowledge(ack, message);
        // send the fault
        agent.fault(createRequestTimeoutErrorMessage(), message);
    }
}
