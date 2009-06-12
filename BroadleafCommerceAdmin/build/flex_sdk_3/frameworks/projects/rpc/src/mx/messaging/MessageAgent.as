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

import flash.events.EventDispatcher;

import mx.core.IMXMLObject;
import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.FlexClient;
import mx.messaging.config.ConfigMap;
import mx.messaging.config.ServerConfig;
import mx.messaging.errors.InvalidDestinationError;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageAckEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.MessagePerformanceUtils;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.Base64Encoder;
import mx.utils.UIDUtil;

use namespace mx_internal;

/**
 *  Dispatched when an acknowledge message is received for a sent message.
 *
 *  @eventType mx.messaging.events.MessageAckEvent.ACKNOWLEDGE
 */
[Event(name="acknowledge", type="mx.messaging.events.MessageAckEvent")]

/**
 *  Dispatched when a message fault occurs.
 * 
 *  @eventType mx.messaging.events.MessageFaultEvent.FAULT
 */
[Event(name="fault", type="mx.messaging.events.MessageFaultEvent")]

/**
 *  Dispatched when the underlying Channel the MessageAgent is using connects.
 *
 *  @eventType mx.messaging.events.ChannelEvent.CONNECT
 */
[Event(name="channelConnect", type="mx.messaging.events.ChannelEvent")]

/**
 *  Dispatched when the underlying Channel the MessageAgent is using disconnects.
 *
 *  @eventType mx.messaging.events.ChannelEvent.DISCONNECT
 */
[Event(name="channelDisconnect", type="mx.messaging.events.ChannelEvent")]

/**
 *  Dispatched when the underlying Channel the MessageAgent is using faults.
 * 
 *  @eventType mx.messaging.events.ChannelFaultEvent.FAULT
 */
[Event(name="channelFault", type="mx.messaging.events.ChannelFaultEvent")]

/**
 *  Dispatched when the <code>connected</code> property of the MessageAgent changes.
 *  Also dispatched when the <code>subscribed</code> of a Consumer changes.
 *  @see mx.messaging.Consumer
 *  @eventType mx.events.PropertyChangeEvent.PROPERTY_CHANGE
 */
[Event(name="propertyChange", type="mx.events.PropertyChangeEvent")]

[ResourceBundle("messaging")]

/**
 *  The MessageAgent class provides the basic low-level functionality common to
 *  message handling for a destination.
 *
 *  <p><b>Note:</b> For advanced use only.
 *  Use this class for creating custom message agents like the existing Producer
 *  and Consumer classes.</p>
 *
 *  @mxml
 *  <p>
 *  All message agent classes, including the Producer and Consumer classes, extend
 *  MessageAgent and inherit the following tag attributes:
 *  </p>
 *  <pre>
 *   &lt;mx:<i>tagname</i><br>
 *    <b>Properties</b>
 *    channelSet="<i>No default.</i>"  
 *    clientId="<i>No default.</i>"
 *    connected="<i>false</i>"
 *    destination="<i>No default.</i>"
 *    requestTimeout="<i>-1</i>"
 *    subtopic="<i>No default.</i>"
 * 
 *
 *   <b>Events</b>
 *    acknowledge="<i>No default.</i>"
 *    channelConnect="<i>No default.</i>"
 *    channelDisconnect="<i>No default.</i>"
 *    channelFault="<i>No default.</i>"
 *    fault="<i>No default.</i>"
 *    propertyChange="<i>No default.</i>"  
 *  /&gt;
 *  </pre> 
 */
public class MessageAgent extends EventDispatcher implements IMXMLObject
{
    //--------------------------------------------------------------------------
    //
    // Internal Static Constants
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Indicates that the MessageAgent is used an automatically configured ChannelSet
     *  obtained from ServerConfig.
     */
    mx_internal static const AUTO_CONFIGURED_CHANNELSET:int = 0;
    
    /**
     *  @private
     *  Indicates that the MessageAgent is using a manually assigned ChannelSet.
     */
    mx_internal static const MANUALLY_ASSIGNED_CHANNELSET:int = 1;
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function MessageAgent()
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
     *  The type of MessageAgent.
     *  This variable is used for logging and MUST be assigned by subclasses.
     */
    protected var _agentType:String;

    /**
     *  @private
     *  The Base64 encoded credentials that will be passed through to
     *  the ChannelSet.
     */
    protected var _credentials:String;

    /**
     *  @private
     *  The character set encoding used to create the credentials String.
     */
    protected var _credentialsCharset:String;
    
    /**
     *  @private
     *  Indicates whether the agent is explicitly disconnected.
     *  This allows agents to supress processing of acks/faults that return
     *  after the client has issued an explicit disconnect().
     */
    protected var _disconnectBarrier:Boolean;
    
    /**
     *  @private
     *  This helps in the runtime configuration setup by delaying the connect
     *  event until the configuration has been setup. See acknowledge().
     */
    private var _pendingConnectEvent:ChannelEvent;
    
    /**
     *  @private
     *  The Base64 encoded credentials that are passed through to a 
     *  3rd party.
     */
    private var _remoteCredentials:String = "";

    /**
     *  @private
     *  The character set encoding used to create the remoteCredentials String.
     */
    private var _remoteCredentialsCharset:String;

    /**
     *  @private
     *  Indicates that the remoteCredentials value has changed and should
     *  be sent to the server.
     */
    private var _sendRemoteCredentials:Boolean;
    
    /**
     *  @private
     *  The logger MUST be assigned by subclasses, for example
     *  Consumer and Producer.
     */
    protected var _log:ILogger;

    /**
     *  @private
     *  A queue to store pending outbound messages while waiting for a server response
     *  that contains a server-generated clientId.
     *  Serializing messages from a MessageAgent to the server is essential until we
     *  receive a response containing a server-generated clientId; otherwise the server
     *  will treat each message as if it was sent by a different, "new" MessageAgent instance.
     */
    private var _clientIdWaitQueue:Array;
    
    /**
     *  @private
     * Flag being set to true denotes that we should skip remaining fault
     * processing logic because the fault has already been handled.  
     * Currently used during an automatic resend of a faulted message if the fault
     * was due to a server session timeout and is authentication/authorization related.
     */ 
    protected var _ignoreFault:Boolean = false;

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
    //  authenticated
    //----------------------------------

    /**
     *  @private
     */
    private var _authenticated:Boolean;
    
    [Bindable(event="propertyChange")]
    /**
     *  Indicates if this MessageAgent is using an authenticated connection to 
     *  its destination.
     */
    public function get authenticated():Boolean
    {
        return _authenticated;
    }
    
    /**
     *  @private
     */
    mx_internal function setAuthenticated(value:Boolean, creds:String):void
    {
        if (_authenticated != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "authenticated", _authenticated, value); 
            _authenticated = value;
            dispatchEvent(event);
            
            if (value)
                assertCredentials(creds);
        }
    }
        
    //----------------------------------
    //  channelSet
    //----------------------------------

    /**
     *  @private
     */
    private var _channelSet:ChannelSet;
    
    [Bindable(event="propertyChange")]    
    /**
     *  Provides access to the ChannelSet used by the MessageAgent. The
     *  ChannelSet can be manually constructed and assigned, or it will be 
     *  dynamically initialized to use the configured Channels for the
     *  destination for this MessageAgent.
     */
    public function get channelSet():ChannelSet
    {
        return _channelSet;
    }
    
    /**
     *  @private
     */
    public function set channelSet(value:ChannelSet):void
    {
        internalSetChannelSet(value);
        _channelSetMode = MANUALLY_ASSIGNED_CHANNELSET;
    }
    
    /**
     *  @private
     *  This method is called by ChannelSet.connect(agent) to set up the bidirectional
     *  relationship between the MessageAgent and the ChannelSet.
     *  It also handles the case of customer code calling channelSet.connect(agent)
     *  directly rather than assigning the ChannelSet to the MessageAgent's channelSet 
     *  property.
     */
    mx_internal function internalSetChannelSet(value:ChannelSet):void
    {
        if (_channelSet != value)
        {
            if (_channelSet != null)
                _channelSet.disconnect(this); 
              
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "channelSet", _channelSet, value); 
            _channelSet = value;
            
            if (_channelSet != null)
            {
                if (_credentials)
                    _channelSet.setCredentials(_credentials, this, _credentialsCharset);

                _channelSet.connect(this);                        
            }
            
            dispatchEvent(event);
        }
    }

    //----------------------------------
    //  clientId
    //----------------------------------
    
    /**
     *  @private
     */
    private var _clientId:String;

    [Bindable(event="propertyChange")]
    /**
     *  Provides access to the client id for the MessageAgent.
     *  MessageAgents are assigned their client id by the remote destination
     *  and this value is used to route messages from the remote destination to
     *  the proper MessageAgent.
     */
    public function get clientId():String
    {
        return _clientId;
    }
    
    /** 
     *  @private
     *  This method is used to assign a server-generated client id to the MessageAgent
     *  in the common scenario.
     *  It may also be used by the framework to sync up cooperating MessageAgents under
     *  a single client id value so that they appear as a single MessageAgent to the server.
     *  Assigning a client id value will flush any messages that have been queued while we
     *  were waiting for a server-generated client id value to be returned.
     *  Queued messages are sent to the server in order.
     */
    mx_internal function setClientId(value:String):void
    {
        if (_clientId != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "clientId", _clientId, value);
            _clientId = value;
            flushClientIdWaitQueue();
            dispatchEvent(event);
        }       
    } 
            
    //----------------------------------
    //  connected
    //----------------------------------    
    
    /**
     *  @private
     */
    private var _connected:Boolean = false;
    
    [Bindable(event="propertyChange")]    
    /**
     *  Indicates whether this MessageAgent is currently connected to its
     *  destination via its ChannelSet. The <code>propertyChange</code> event is dispatched when
     *  this property changes.
     */
    public function get connected():Boolean
    {
        return _connected;
    }
    
    /**
     *  @private
     */
    protected function setConnected(value:Boolean):void
    {
        if (_connected != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "connected", _connected, value);
            _connected = value;
            dispatchEvent(event);
            setAuthenticated(value && channelSet && channelSet.authenticated, _credentials);
        }
    }

    //----------------------------------
    //  destination
    //----------------------------------    

    /**
     *  @private
     */
    private var _destination:String = "";

    [Bindable(event="propertyChange")]    
    /**
     *  Provides access to the destination for the MessageAgent. 
     *  Changing the destination will disconnect the MessageAgent if it is
     *  currently connected.
     *
     *  @throws mx.messaging.errors.InvalidDestinationError If the destination is null or 
     *                                  zero-length.
     */ 
    public function get destination():String
    {
        return _destination;
    }

    /**
     *  @private
     */
    public function set destination(value:String):void
    {
        if ((value == null) || (value.length == 0))
        {
            var message:String = resourceManager.getString(
                "messaging", "emptyDestinationName", [ value ]);
            throw new InvalidDestinationError(message);
        }

        if (_destination != value)
        {
            // If we're using an automatically configured ChannelSet,
            // disconnect from it and null out our ref so we look up the
            // proper configured ChannelSet for the new destination on our next send().
            if ((_channelSetMode == AUTO_CONFIGURED_CHANNELSET) && (channelSet != null))
            {
                channelSet.disconnect(this);
                channelSet = null;
            }
                
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "destination", _destination, value);                
            _destination = value;           
            dispatchEvent(event);                   
            
            if (Log.isInfo())           
                _log.info("'{0}' {2} set destination to '{1}'.", id, _destination,  _agentType);
        }
    }

    //----------------------------------
    //  id
    //----------------------------------    

    /**
     *  @private
     */
    private var _id:String = UIDUtil.createUID();

    [Bindable(event="propertyChange")]
    /**
     *  @private
     *  The id of this agent.
     */
    public function get id():String
    {
        return _id;
    }
    
    /**
     *  @private
     */
    public function set id(value:String):void
    {
        if (_id != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "id", _id, value);  
            _id = value;
            dispatchEvent(event);
        }       
    }
    
    //----------------------------------
    //  requestTimeout
    //----------------------------------
    
    /**
     *  @private
     */
    private var _requestTimeout:int = -1;   
    
    [Bindable(event="propertyChange")]    
    /**
     *  Provides access to the request timeout in seconds for sent messages.
     *  If an acknowledgement, response or fault is not received from the 
     *  remote destination before the timeout is reached the message is faulted on the client.
     *  A value less than or equal to zero prevents request timeout.
     */ 
    public function get requestTimeout():int
    {
        return _requestTimeout;
    }    
    
    /**
     *  @private
     */
    public function set requestTimeout(value:int):void
    {
        if (_requestTimeout != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "requestTimeout", _requestTimeout, value);              
            _requestTimeout = value;
            dispatchEvent(event);
        }       
    }
    
    //--------------------------------------------------------------------------
    //
    // Internal Properties
    // 
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  channelSetMode
    //----------------------------------
    
    /**
     *  @private
     */
    private var _channelSetMode:int = AUTO_CONFIGURED_CHANNELSET;
       
    mx_internal function get channelSetMode():int
    {
        return _channelSetMode;
    }
        
    //----------------------------------
    //  configRequested
    //----------------------------------    
    
    /**
     *  @private
     *  Indicates whether the agent has requested configuration from the server.
     */
    mx_internal var configRequested:Boolean = false;    
    
    //----------------------------------
    //  needsConfig
    //----------------------------------

    /**
     * @private
     */  
    private var _needsConfig:Boolean;
    
    /**
     *  Indicates if this MessageAgent needs to request configuration from the 
     *  server. 
     */
    mx_internal function get needsConfig():Boolean
    {
        return _needsConfig;
    }
    
    /**
     *  @private
     */    
    mx_internal function set needsConfig(value:Boolean):void
    {
        if (_needsConfig != value)
        {
            _needsConfig = value;
            if (_needsConfig)
            {
                var cs:ChannelSet = channelSet;
                try
                {
                    disconnect();
                }
                finally
                {
                    internalSetChannelSet(cs);
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Invoked by a MessageResponder upon receiving a result for a sent
     *  message. Subclasses may override this method if they need to perform
     *  custom acknowledgement processing, but must invoke
     *  <code>super.acknowledge()</code> as well. This method dispatches a 
     *  MessageAckEvent.
     * 
     *  @param ackMsg The AcknowledgMessage returned.
     * 
     *  @param msg The original sent message.
     */
    public function acknowledge(ackMsg:AcknowledgeMessage, msg:IMessage):void
    {
        if (Log.isInfo())
            _log.info("'{0}' {2} acknowledge of '{1}'.", id, msg.messageId, _agentType);
            
        if (Log.isDebug() && channelSet != null && channelSet.currentChannel != null && 
                channelSet.currentChannel.mpiEnabled)
        {
            try
            {
                var mpiutil:MessagePerformanceUtils = new MessagePerformanceUtils(ackMsg);
                _log.debug(mpiutil.prettyPrint());
            }
            catch (e:Error)
            {
                _log.debug("Could not get message performance information for: " + msg.toString());   
            }
        }            

        // Remove error hint. ErrorMessages returned by the server are
        // acknowledged before being faulted.
        if (ackMsg.headers[AcknowledgeMessage.ERROR_HINT_HEADER])
            delete ackMsg.headers[AcknowledgeMessage.ERROR_HINT_HEADER];                        
        
        if (configRequested)
        {
            configRequested = false; 
            ServerConfig.updateServerConfigData(ackMsg.body as ConfigMap);
            needsConfig = false;
            if (_pendingConnectEvent)
                channelConnectHandler(_pendingConnectEvent);
                
            _pendingConnectEvent = null;
        }
         
        if (clientId == null)
        {
            if (ackMsg.clientId != null)
                setClientId(ackMsg.clientId); // Triggers a call to flush the clientId wait queue.
            else
                flushClientIdWaitQueue();
        }
                    
        dispatchEvent(MessageAckEvent.createEvent(ackMsg, msg));                
    }
        
    /**
     *  Disconnects the MessageAgent's network connection.
     *  This method does not wait for outstanding network operations to complete.
     */
    public function disconnect():void
    {
        if (!_disconnectBarrier)
        {
            _disconnectBarrier = true;
            if (_channelSetMode == AUTO_CONFIGURED_CHANNELSET)
                internalSetChannelSet(null);
            else if (_channelSet != null)
                _channelSet.disconnect(this);                
        }
    }   
    
    /**
     *  Invoked by a MessageResponder upon receiving a fault for a sent message.
     *  Subclasses may override this method if they need to perform custom fault
     *  processing, but must invoke <code>super.fault()</code> as well. This
     *  method dispatchs a MessageFaultEvent.
     *
     *  @param errMsg The ErrorMessage.
     * 
     *  @param msg The original sent message that caused this fault.
     */
    public function fault(errMsg:ErrorMessage, msg:IMessage):void
    {   
        if (Log.isError())
            _log.error("'{0}' {2} fault for '{1}'.", id, msg.messageId, _agentType);
            
        _ignoreFault = false;
        configRequested = false;
                
        // Remove retryable hint.
        if (errMsg.headers[ErrorMessage.RETRYABLE_HINT_HEADER])
            delete errMsg.headers[ErrorMessage.RETRYABLE_HINT_HEADER];

        if (clientId == null)
        {
            if (errMsg.clientId != null)
                setClientId(errMsg.clientId); // Triggers a call to flush the clientId wait queue.
            else
                flushClientIdWaitQueue();
        }
                
        dispatchEvent(MessageFaultEvent.createEvent(errMsg));      
        
        // If we get an authentication fault on the server and our authenticated
        // flag is true then the authentication fault must have been caused by a
        // session expiration on the server.  Set our authentication state to false.
        // If loginAfterDisconnect flag is on, resend credentials by doing a 
        // disconnect/connect and try sending the message again 
        if (errMsg.faultCode == "Client.Authentication" && authenticated && 
            channelSet != null && channelSet.currentChannel != null)
        {
            channelSet.currentChannel.setAuthenticated(false);
            
            if (channelSet.currentChannel.loginAfterDisconnect)
            {
                reAuthorize(msg);
                _ignoreFault = true;
            }
        }                                   
    }
    
    /**
     * This function should be overriden by sublasses to implement re-authorization due to 
     * server session time-out behavior specific to them.  In general it should
     * follow disconnect, connect, re-send message pattern
     * 
     *  @param msg The message that caused the fault and should be resent once we have
     *  disconnected/connected causing re-authentication.
     */    
    protected function reAuthorize(msg:IMessage):void
    {
        disconnect();
        internalSend(msg);      
    }
    
    /**
     *  Handles a CONNECT ChannelEvent. Subclasses that need to perform custom
     *  processing should override this method, and invoke 
     *  <code>super.channelConnectHandler()</code>.
     * 
     *  @param event The ChannelEvent.
     */
    public function channelConnectHandler(event:ChannelEvent):void 
    {
        _disconnectBarrier = false;         
        // If we are waiting on config to come in we can't be connected until
        // we get it. See acknowledge().
        if (needsConfig)
        {
            if (Log.isInfo())
                _log.info("'{0}' {1} waiting for configuration information.", id, _agentType);
                
            _pendingConnectEvent = event;
        }
        else
        {
            if (Log.isInfo())
                _log.info("'{0}' {1} connected.", id, _agentType);  
            setConnected(true);
            dispatchEvent(event);
        }
    }
    
    /**
     *  Handles a DISCONNECT ChannelEvent. Subclasses that need to perform
     *  custom processing should override this method, and invoke
     *  <code>super.channelDisconnectHandler()</code>.
     * 
     *  @param event The ChannelEvent.
     */
    public function channelDisconnectHandler(event:ChannelEvent):void
    {
        if (Log.isWarn())
            _log.warn("'{0}' {1} channel disconnected.", id, _agentType);           
        setConnected(false);
        // If we have remoteCredentials we need to send them on reconnect.
        if (_remoteCredentials != null)
        {
            _sendRemoteCredentials = true;
        }
        dispatchEvent(event);
    }
    
    /**
     *  Handles a ChannelFaultEvent. Subclasses that need to perform custom
     *  processing should override this method, and invoke
     *  <code>super.channelFaultHandler()</code>.
     * 
     *  @param The ChannelFaultEvent
     */
    public function channelFaultHandler(event:ChannelFaultEvent):void
    {
        if (Log.isWarn())
            _log.warn("'{0}' {1} channel faulted with {2} {3}", id, _agentType, event.faultCode, event.faultDetail);
        
        if (!event.channel.connected)
        {
            setConnected(false);
            // If we have remoteCredentials we need to send them on reconnect.
            if (_remoteCredentials != null)
            {
                _sendRemoteCredentials = true;
            }
        }
        dispatchEvent(event);    
    }
    
    /**
     *  Called after the implementing object has been created
     *  and all properties specified on the tag have been assigned.
     *
     *  @param document MXML document that created this object.
     *
     *  @param id id used by the document to refer to this object.
     *  If the object is a deep property on the document, id is null.
     */
    public function initialized(document:Object, id:String):void
    {
        this.id = id;
    }
    
    /**
     *  Logs the MessageAgent out from its remote destination. 
     *  Logging out of a destination applies to everything connected using the same ChannelSet
     *  as specified in the server configuration. For example, if several DataService components
     *  are connected over an RTMP channel and <code>logout()</code> is invoked on one of them, 
     *  all other client components that are connected using the same ChannelSet are also logged out.
     */
    public function logout():void
    {
        _credentials = null;
        if (channelSet)
            channelSet.logout(this);
    }   
    
    /**
     *  Sets the credentials that the MessageAgent uses to authenticate to 
     *  destinations.
     *  The credentials are applied to all services connected over the same ChannelSet. 
     * 
     *  @param username The username.
     *  @param password The password.
     *  @param charset The character set encoding to use while encoding the
     *  credentials. The default is null, which implies the legacy charset of
     *  ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     *
     *  @throws flash.errors.IllegalOperationError in two situations; if credentials
     *  have already been set and an authentication is in progress with the remote
     *  detination, or if authenticated and the credentials specified don't match
     *  the currently authenticated credentials.
     */
    public function setCredentials(username:String, password:String, charset:String=null):void
    {
        if (username == null && password == null)
        {
            _credentials = null;
            _credentialsCharset = null;
        }
        else
        {
            var cred:String = username + ":" + password;
            var encoder:Base64Encoder = new Base64Encoder();
            if (charset == Base64Encoder.CHARSET_UTF_8)
                encoder.encodeUTFBytes(cred);
            else
                encoder.encode(cred);
            _credentials = encoder.drain();
            _credentialsCharset = charset;
        }

        if (channelSet != null)
            channelSet.setCredentials(_credentials, this, _credentialsCharset);
    }

    /**
     *  Sets the remote credentials that will be passed through to the remote destination
     *  for authenticating to secondary systems.
     * 
     *  @param username The username.
     *  @param password The password.
     *  @param charset The character set encoding to use while encoding the
     *  remote credentials. The default is null, which implies the legacy
     *  charset of ISO-Latin-1. The only other currently supported option is
     *  &quot;UTF-8&quot;.
     */
    public function setRemoteCredentials(username:String, password:String, charset:String=null):void
    {
        if (username == null && password == null)
        {
            _remoteCredentials = "";
            _remoteCredentialsCharset = null;
        }
        else
        {
            var cred:String = username + ":" + password;
            var encoder:Base64Encoder = new Base64Encoder();
            if (charset == Base64Encoder.CHARSET_UTF_8)
                encoder.encodeUTFBytes(cred);
            else
                encoder.encode(cred);
            _remoteCredentials = encoder.drain();
            _remoteCredentialsCharset = charset;
        }
        _sendRemoteCredentials = true;      
    }
    
    /**
    * Returns true if there are any pending requests for the passed in message.
    * This method should be overriden by subclasses
    * 
    * @param msg The message for which the existence of pending requests is checked.
    *
    * @return Returns <code>true</code> if there are any pending requests for the 
    * passed in message.
    */
    public function hasPendingRequestForMessage(msg:IMessage):Boolean
    {
        return false;
    }    

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Internal hook for ChannelSet to assign credentials when it has authenticated
     *  successfully via a direct <code>login(...)</code> call to the server or logged
     *  out directly.
     */
    mx_internal function internalSetCredentials(credentials:String):void
    {
        _credentials = credentials;
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    final protected function assertCredentials(value:String):void
    {
        if (_credentials != null && (_credentials != value))
        {
            var errMsg:ErrorMessage = new ErrorMessage();
            errMsg.faultCode = "Client.Authentication.Error";
            errMsg.faultString = "Credentials specified do not match those used on underlying connection.";
            errMsg.faultDetail = "Channel was authenticated with a different set of credentials than those used for this agent.";
            dispatchEvent(MessageFaultEvent.createEvent(errMsg));
        }   
    }
    
    /**
     *  @private
     *  Utility method to flush any pending queued messages to send once we have
     *  received a clientId from the remote destination.
     */
    final protected function flushClientIdWaitQueue():void
    {
        if (_clientIdWaitQueue != null)
        {
            // If we have a valid clientId, flush all pending messages.
            if (clientId != null)
            {
                while (_clientIdWaitQueue.length > 0)
                {
                    internalSend(_clientIdWaitQueue.shift() as IMessage);
                }                               
            }
            // If we still don't have a clientId, remove the first queued message and send it.
            // Leave the queue intact to buffer subsequent sends until we get a response/fault
            // back for this one.
            if (_clientIdWaitQueue.length > 0)
            {
                internalSend(_clientIdWaitQueue.shift() as IMessage);
            }
            else
            {
                // Regardless of whether the clientId is defined or not, if the wait queue
                // is empty set it to null to allow the next message to be processed by the
                // send code path rather than being routed to the queue.
                _clientIdWaitQueue = null;
            }
        }
    }

    /**
     *  Sends a Message from the MessageAgent to its destination using the
     *  agent's ChannelSet. MessageAgent subclasses must use this method to
     *  send their messages.
     * 
     *  @param message The message to send.
     * 
     *  @param waitForClientId If true the message may be queued until a clientId has been
     *                         assigned to the agent. In general this is the desired behavior.
     *                         For special behavior (automatic reconnect and resubscribe) the
     *                         agent may pass false to override the default queuing behavior.
     * 
     *  @throws mx.messaging.errors.InvalidDestinationError If no destination is set.
     */
    protected function internalSend(message:IMessage, waitForClientId:Boolean = true):void
    {
        // If we don't have a client or server assigned clientId, we
        // need to send a single message and then store any subsequent messages
        // in a buffer to be sent once we've gotten back a server-generated
        // clientId. Otherwise, N outbound messages sent before receiving an ack for
        // the first will result in the generation of N different clientIds in the
        // response/fault messages from the server.
        if ((message.clientId == null) && waitForClientId && (clientId == null))
        {
            if (_clientIdWaitQueue == null)
            {
                _clientIdWaitQueue = [];
                // Current message will be sent but subsequent messages sent before
                // its ack/fault will be queued.
            }
            else
            {
                _clientIdWaitQueue.push(message);
                return; // We've queued the message and will send it once we get a clientId or the outstanding send fails.
            }
        }

        if (message.clientId == null)
            message.clientId = clientId;

        if (requestTimeout > 0)
            message.headers[AbstractMessage.REQUEST_TIMEOUT_HEADER] = requestTimeout;

        if (_sendRemoteCredentials)
        {
            if (! ((message is CommandMessage) && 
                    (CommandMessage(message).operation == CommandMessage.TRIGGER_CONNECT_OPERATION)))
            {
                message.headers[AbstractMessage.REMOTE_CREDENTIALS_HEADER] = _remoteCredentials;
                message.headers[AbstractMessage.REMOTE_CREDENTIALS_CHARSET_HEADER] = _remoteCredentialsCharset;
                _sendRemoteCredentials = false;
            }
        }

        if (channelSet != null)
        {
            if (!connected && (_channelSetMode == MANUALLY_ASSIGNED_CHANNELSET))
                _channelSet.connect(this);
            
            if (channelSet.connected && needsConfig && !configRequested)
            {
                message.headers[CommandMessage.NEEDS_CONFIG_HEADER] = true;
                configRequested = true;
            }
            
            channelSet.send(this, message);
        }
        else if (destination.length > 0)
        {
            initChannelSet(message);
            if (channelSet != null)
                channelSet.send(this, message);
        }        
        else
        {
            var msg:String = resourceManager.getString(
                "messaging", "destinationNotSet");
            throw new InvalidDestinationError(msg);
        }
    }

    /**
     *  Used to automatically initialize the <code>channelSet</code> property for the
     *  MessageAgent before it connects for the first time. 
     *  Subtypes may override to perform custom initialization.
     * 
     *  @param message The message that needs to be sent.
     */
    protected function initChannelSet(message:IMessage):void
    {
        if (_channelSet == null)
        {
            _channelSetMode = AUTO_CONFIGURED_CHANNELSET; 
            internalSetChannelSet(ServerConfig.getChannelSet(destination));
        }
        
        if (_channelSet.connected && needsConfig && !configRequested)            
        {
            message.headers[CommandMessage.NEEDS_CONFIG_HEADER] = true;
            configRequested = true;
        }
            
        _channelSet.connect(this);
        
        if (_credentials != null)
            channelSet.setCredentials(_credentials, this, _credentialsCharset);
    }        
}

}
