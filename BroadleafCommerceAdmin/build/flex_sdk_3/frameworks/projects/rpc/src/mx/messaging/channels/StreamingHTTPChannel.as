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

package mx.messaging.channels
{

import flash.events.Event;
import flash.events.HTTPStatusEvent;
import flash.events.IOErrorEvent;
import flash.events.ProgressEvent;
import flash.events.SecurityErrorEvent;
import flash.events.StatusEvent;
import flash.utils.ByteArray;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.messaging.FlexClient;
import mx.messaging.config.ConfigMap;
import mx.messaging.config.ServerConfig;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("messaging")]

/**
 *  The StreamingHTTPChannel class provides support for messaging and
 *  offers a different push model than the base HTTPChannel. Rather than
 *  polling for data from the server, the streaming channel opens an internal
 *  HTTP connection to the server that is held open to allow the server to
 *  stream data down to the client with no poll overhead.
 *
 *  <p>
 *  Messages sent by this channel to the server are sent using a URLLoader
 *  which uses an HTTP connection internally for the duration of the operation.
 *  Once the message is sent and an acknowledgement or fault is returned the HTTP connection
 *  used by URLLoader is released by the channel. These client-to-server messages are
 *  not sent over the streaming HTTP connection that the channel holds open to receive
 *  server pushed data.
 *  </p>
 *
 *  <p>
 *  Although this class extends the base HTTPChannel to inherit the regular HTTP
 *  handling, it does not support polling.
 *  </p>
 */
public class StreamingHTTPChannel extends HTTPChannel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates an new StreamingHTTPChannel instance.
     *
     *  @param id The id of this Channel.
     *
     *  @param uri The uri for this Channel.
     */
    public function StreamingHTTPChannel(id:String = null, uri:String = null)
    {
        super(id, uri);

        // Disable polling.
        internalPollingEnabled = false;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

      /**
       * Helper class used by the channel to establish a streaming HTTP connection
       * with the server.
       */
    private var streamingConnectionHandler:StreamingConnectionHandler;

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
    //  pollingEnabled
    //----------------------------------

    /**
     *  @private
     */
    override public function set pollingEnabled(value:Boolean):void
    {
        var message:String = resourceManager.getString(
            "messaging", "pollingNotSupportedHTTP");
        throw new Error(message);
    }

    //----------------------------------
    //  pollingInterval
    //----------------------------------

    /**
     *  @private
     */
    override public function set pollingInterval(value:Number):void
    {
        var message:String = resourceManager.getString(
            "messaging", "pollingNotSupportedHTTP");
        throw new Error(message);
    }

    //----------------------------------
    //  realtime
    //----------------------------------

    /**
     *  @private
     *  Returns true since streaming channels are considered realtime.
     */
    override mx_internal function get realtime():Boolean
    {
        return true;
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Polling is not supported by this channel.
     */
    override public function poll():void
    {
        var message:String = resourceManager.getString(
            "messaging", "pollingNotSupportedHTTP");
        throw new Error(message);
    }

    //--------------------------------------------------------------------------
    //
    // Overriden Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Closes the streaming connection before redispatching the fault event.
     *
     *  @param event The ChannelFaultEvent.
     */
    override protected function connectFailed(event:ChannelFaultEvent):void
    {
        if (streamingConnectionHandler != null)
            streamingConnectionHandler.closeStreamingConnection();

        super.connectFailed(event);
    }

    /**
     *  @private
     *  Closes the streaming connection before disconnecting.
     */
    override protected function internalDisconnect(rejected:Boolean = false):void
    {
        if (streamingConnectionHandler != null)
            streamingConnectionHandler.closeStreamingConnection();

        super.internalDisconnect(rejected);
    }

    /**
     *  @private
     *  This method will be called if the ping message sent to test connectivity
     *  to the server during the connection attempt succeeds.
     *  Before triggering connect success handling the streaming channel must set
     *  up its streaming connection with the server.
     */
    override protected function internalPingComplete(msg:AsyncMessage):void
    {
        if (msg != null)
        {
            ServerConfig.updateServerConfigData(msg.body as ConfigMap, endpoint);

            // Set the server assigned FlexClient Id.
            if (FlexClient.getInstance().id == null && msg.headers[AbstractMessage.FLEX_CLIENT_ID_HEADER] != null)
                FlexClient.getInstance().id = msg.headers[AbstractMessage.FLEX_CLIENT_ID_HEADER];
        }
        if (credentials != null && !(msg is ErrorMessage))
            setAuthenticated(true);

        if (streamingConnectionHandler == null)
        {
            streamingConnectionHandler = new StreamingHTTPConnectionHandler(this, _log);
            streamingConnectionHandler.addEventListener(Event.OPEN, streamOpenHandler);
            streamingConnectionHandler.addEventListener(Event.COMPLETE, streamCompleteHandler);
            streamingConnectionHandler.addEventListener(HTTPStatusEvent.HTTP_STATUS, streamHttpStatusHandler);
            streamingConnectionHandler.addEventListener(IOErrorEvent.IO_ERROR, streamIoErrorHandler);
            streamingConnectionHandler.addEventListener(SecurityErrorEvent.SECURITY_ERROR, streamSecurityErrorHandler);
            streamingConnectionHandler.addEventListener(StatusEvent.STATUS, streamStatusHandler);
        }
        streamingConnectionHandler.openStreamingConnection(appendToURL);
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  If the streaming connection receives an open event the channel is setup
     *  and gets ready for messaging.
     *
     *  @param event The OPEN Event.
     */
    private function streamOpenHandler(event:Event):void
    {
       connectSuccess();
    }

    /**
     *  A complete event indicates that the streaming connection has been closed by the server.
     *  This is a no-op if the channel is disconnected on the client, otherwise notifies the client
     *  channel that it has disconnected.
     *
     *  @param event The COMPLETE Event.
     */
    private function streamCompleteHandler(event:Event):void
    {
        if (connected) // The server has closed the connection but the client currently believes it should be connected.
        {
            // Dispatch a channel fault event.
            var faultEvent:ChannelFaultEvent = ChannelFaultEvent.createEvent(this, false, "Channel.Stream.Failed", "error", "Remote endpoint has closed the streaming connection.");
            faultEvent.rootCause = event;
            dispatchEvent(faultEvent);
            // And disconnect the channel.
            internalDisconnect();
        }
    }

    /**
     *  Handles HTTP status events dispatched by the streaming connection.
     *
     *  @param event The HTTPStatusEvent.
     */
    private function streamHttpStatusHandler(event:HTTPStatusEvent):void
    {
        // No-op because most of the times, HTTP status is zero.
        // See ioErrorHandler.
    }

    /**
     *  Handles IO error events dispatched by the streaming connection.
     *
     *  @param event The IOErrorEvent.
     */
    private function streamIoErrorHandler(event:IOErrorEvent):void
    {
        var faultEvent:ChannelFaultEvent;
        if (connected)
        {
            // Dispatch a channel fault event.
            faultEvent = ChannelFaultEvent.createEvent(this, false, "Channel.Stream.Failed", "error", " url: '" + endpoint + "'");
            faultEvent.rootCause = event;
            dispatchEvent(faultEvent);
            // And disconnect the channel.
            internalDisconnect();
        }
        else
        {
            // Fault the current connect attempt.
            faultEvent = ChannelFaultEvent.createEvent(this, false, "Channel.Stream.Failed", "error", " url: '" + endpoint + "'");
            faultEvent.rootCause = event;
            connectFailed(faultEvent);
        }
    }

    /**
     *  Handles security error events dispatched by the streaming connection.
     *
     *  @param event The SecurityErrorEvent.
     */
    private function streamSecurityErrorHandler(event:SecurityErrorEvent):void
    {
        // Just log as we'll never reach this handler because the prior ping will trigger
        // a security error before we try to establish the streaming connection.
        if (Log.isDebug())
            _log.debug("'{0}' channel encountered a security error: {1}", id, event.text);
    }

    /**
     *  Handle status events dispatched by the streaming connection.
     *
     *  @param event The StatusEvent.
     */
    private function streamStatusHandler(event:StatusEvent):void
    {
        // Only a disconnect status event is currently handled.
        if (event.code == StreamingConnectionHandler.DISCONNECT_CODE)
        {
            streamingConnectionHandler.closeStreamingConnection();
            disconnectSuccess(true /* rejected */);
        }
    }
}

}

//------------------------------------------------------------------------------
//
// Private Classes
//
//------------------------------------------------------------------------------

import flash.net.ObjectEncoding;
import flash.utils.ByteArray;

import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.Channel;
import mx.messaging.channels.StreamingConnectionHandler;
import mx.messaging.channels.amfx.AMFXDecoder;
import mx.messaging.channels.amfx.AMFXContext;
import mx.messaging.messages.IMessage;

/**
 *  A helper class that is used by the streaming channels to open an internal
 *  HTTP connection to the server that is held open to allow the server to
 *  stream data down to the client with no poll overhead.
 */
class StreamingHTTPConnectionHandler extends StreamingConnectionHandler
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates an new StreamingHTTPConnectionHandler instance.
     *
     *  @param channel The Channel that uses this class.
     *  @param log Reference to the logger for the associated Channel.
     */
    public function StreamingHTTPConnectionHandler(channel:Channel, log:ILogger)
    {
        super(channel, log);
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Used by the streamProgressHandler to read an AMFX encoded message.
     */
    override protected function readMessage():IMessage
    {
        var message:IMessage;
        chunkBuffer.position = dataOffset - 1;
        var messageBytes:ByteArray = new ByteArray();
        messageBytes.objectEncoding = ObjectEncoding.AMF3;
        try
        {
            chunkBuffer.readBytes(messageBytes, 0, dataBytesToRead);
            var messageString:String = messageBytes.readUTFBytes(dataBytesToRead);
            var messageObject:Object = AMFXDecoder.decodeValue(XML(messageString), new AMFXContext());
            message = IMessage(messageObject);
        }
        catch(error:Error)
        {
            if (Log.isError())
                _log.error("'{0}' channel encountered an error while reading a message from the streaming connection: {1}", channel.id, error.message);
        }
        return message;
    }
}
