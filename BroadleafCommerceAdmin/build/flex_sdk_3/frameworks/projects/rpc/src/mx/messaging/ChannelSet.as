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

package mx.messaging
{

import flash.errors.IllegalOperationError;
import flash.events.EventDispatcher;
import flash.events.TimerEvent;
import flash.utils.Dictionary;
import flash.utils.Timer;

import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;
import mx.messaging.channels.NetConnectionChannel;
import mx.messaging.config.ServerConfig;
import mx.messaging.errors.NoChannelAvailableError;
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
import mx.rpc.AsyncToken;
import mx.rpc.events.AbstractEvent;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.utils.Base64Encoder;

use namespace mx_internal;

[DefaultProperty("channels")]

/**
 *  Dispatched after a Channel in the ChannelSet has connected to its endpoint.
 *
 *  @eventType mx.messaging.events.ChannelEvent.CONNECT
 */
[Event(name="channelConnect", type="mx.messaging.events.ChannelEvent")]

/**
 *  Dispatched after a Channel in the ChannelSet has disconnected from its 
 *  endpoint.
 *
 *  @eventType mx.messaging.events.ChannelEvent.DISCONNECT
 */
[Event(name="channelDisconnect", type="mx.messaging.events.ChannelEvent")]

/**
 *  Dispatched after a Channel in the ChannelSet has faulted.
 * 
 *  @eventType mx.messaging.events.ChannelFaultEvent.FAULT
 */
[Event(name="channelFault", type="mx.messaging.events.ChannelFaultEvent")]

/**
 * The result event is dispatched when a login or logout call successfully returns.
 * @eventType mx.rpc.events.ResultEvent.RESULT 
 */
[Event(name="result", type="mx.rpc.events.ResultEvent")]

/**
 * The fault event is dispatched when a login or logout call fails.
 * @eventType mx.rpc.events.FaultEvent.FAULT 
 */
[Event(name="fault", type="mx.rpc.events.FaultEvent")]

/**
 *  Dispatched when a property of the ChannelSet changes.
 * 
 *  @eventType mx.events.PropertyChangeEvent.PROPERTY_CHANGE
 */
[Event(name="propertyChange", type="mx.events.PropertyChangeEvent")]

[ResourceBundle("messaging")]

/**
 *  The ChannelSet is a set of Channels that are used to send messages to a
 *  target destination. The ChannelSet improves the quality of service on the 
 *  client by hunting through its Channels to send messages in the face of
 *  network failures or individual Channel problems.
 */
public class ChannelSet extends EventDispatcher
{    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructs a ChannelSet. 
     *  If the <code>channelIds</code> argument is provided, the ChannelSet will 
     *  use automatically configured Channels obtained via <code>ServerConfig.getChannel()</code> 
     *  to reach a destination. 
     *  Attempting to manually assign Channels to a ChannelSet that uses configured
     *  Channels is not allowed.
     *  
     *  <p>If the <code>channelIds</code> argument is not provided or is null, 
     *  Channels must be manually created and added to the ChannelSet in order
     *  to connect and send messages.</p>
     * 
     *  <p>If the ChannelSet is clustered using url-load-balancing (where each server
     *  declares a unique RTMP or HTTP URL and the client fails over from one URL to
     *  the next), the first time that a Channel in the ChannelSet successfully connects
     *  the ChannelSet will automatically make a request for all of the endpoints across 
     *  the cluster for all member Channels and will assign these failover URLs to each 
     *  respective Channel.
     *  This allows Channels in the ChannelSet to failover individually, and when failover
     *  options for a specific Channel are exhausted the ChannelSet will advance to the next 
     *  Channel in the set to attempt to reconnect.</p>
     * 
     *  <p>Regardless of clustering, if a Channel cannot connect or looses
     *  connectivity, the ChannelSet will advance to its next available Channel
     *  and attempt to reconnect.
     *  This allows the ChannelSet to hunt through Channels that use different 
     *  protocols, ports, etc., in search of one that can connect to its endpoint 
     *  successfully.</p>
     * 
     *  @param channelIds The ids of configured Channels obtained from ServerConfig for this ChannelSet to
     *                    use. If null, Channels must be manually added to the ChannelSet.
     * 
     *  @param clusteredWithURLLoadBalancing True if the Channels in the ChannelSet are clustered
     *                   using url load balancing.
     */ 
    public function ChannelSet(channelIds:Array = null, clusteredWithURLLoadBalancing:Boolean = false)
    {
        super();        
        _clustered = clusteredWithURLLoadBalancing;
        _connected = false;
        _connecting = false;
        _currentChannelIndex = -1;
        if (channelIds != null)
        {                        
            _channelIds = channelIds;
            _channels = new Array(_channelIds.length);
            _configured = true;
        }        
        else
        {            
            _channels = [];
            _configured = false;
        }
        _hasRequestedClusterEndpoints = false;
        _hunting = false;
        _messageAgents = [];
        _pendingMessages = new Dictionary();
        _pendingSends = [];
        _shouldBeConnected = false;
        _shouldHunt = true;
    }
            
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------
            
    /**
     *  @private
     *  Helper MessageAgent used for direct authentication.
     */         
    private var _authAgent:AuthenticationAgent;        
              
    /**
     *  @private
     *  Flag indicating whether the ChannelSet is in the process of connecting
     *  over the current Channel.
     */
    private var _connecting:Boolean;
    
    /**
     *  @private
     *  Stored credentials to be set on the member channels.
     */
    private var _credentials:String;    

    /**
     *  @private
     *  The character-set encoding used to create the credentials String.
     */
    private var _credentialsCharset:String;

    /**
     *  @private
     *  Current index into the _channels/_channelIds arrays.
     */
    private var _currentChannelIndex:int;    
    
    /**
     *  @private
     *  This flag restricts our cluster request to only happen upon initial
     *  connect to the cluster.
     */
    private var _hasRequestedClusterEndpoints:Boolean;
    
    /**
     *  @private
     *  Flag indicating whether the ChannelSet is in the process of hunting to a
     *  new Channel; this lets us control the "reconnecting" flag on 
     *  CONNECT ChannelEvents that we dispatch when we hunt to a new 
     *  Channel that isn't internally failing over. The new Channel doesn't know we're
     *  in a reconnect attempt when it makes its initial connect attempt so this lets 
     *  us set "reconnecting" to true on the CONNECT event if it succeeds.
     */
    private var _hunting:Boolean;
    
    /**
     *  @private
     *  A dictionary of pending messages used to filter out duplicate
     *  messages passed to the ChannelSet to send while it is not connected. 
     *  This allows agents to perform message resend behavior (i.e. Consumer resubscribe
     *  attempts) without worrying about duplicate messages queuing up and being sent to 
     *  the server once a connection is established.
     */
    private var _pendingMessages:Dictionary;
    
    /**
     *  @private
     *  An array of PendingSend instances to pass into send() when a connection 
     *  is (re)established.
     */
    private var _pendingSends:Array;

    /**
     *  @private
     *  A timer used to do a delayed reconnect for NetConnection channels.
     */
    private var _reconnectTimer:Timer = null;
    
    /**
     *  @private
     *  Flag indicating whether the ChannelSet should be connected. 
     *  If true, the ChannelSet will attempt to hunt to the next available 
     *  Channel when a disconnect or fault occurs. If false, hunting is not 
     *  performed.
     */
    private var _shouldBeConnected:Boolean;
    
    /**
     *  @private
     *  Flag indicating whether a Channel disconnect/fault should trigger hunting or not;
     *  used when connected Channels are removed from the ChannelSet which should not trigger 
     *  hunting.
     */
    private var _shouldHunt:Boolean;

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
     *  Indicates whether the ChannelSet has an underlying Channel that successfully
     *  authenticated with its endpoint.
     */
    public function get authenticated():Boolean
    {
        return _authenticated;
    }
    
    /**
     *  @private
     */
    mx_internal function setAuthenticated(value:Boolean, creds:String, notifyAgents:Boolean=true):void
    {
        if (_authenticated != value)
        {
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "authenticated", _authenticated, value); 
            _authenticated = value;            
            
            if (notifyAgents)
            {
                var ma:MessageAgent;
                for (var i:int = 0; i < _messageAgents.length; i++)
                {
                    ma = MessageAgent(_messageAgents[i]);
                    ma.mx_internal::setAuthenticated(value, creds);
                } 
            }
            
            dispatchEvent(event);
        }
    }

    //----------------------------------
    //  channels
    //----------------------------------
    
    /**
     *  @private
     */
    private var _channels:Array;
    
    /**
     *  Provides access to the Channels in the ChannelSet.
     *  This property may be used to assign a set of channels at once or channels 
     *  may be added directly to the ChannelSet via addChannel() individually.
     *  If this ChannelSet is <code>configured</code> automatically the individual
     *  channels are created lazily and added to this property as needed.
     * 
     *  @throws flash.errors.IllegalOperationError If the ChannelSet is 
     *             <code>configured</code>, assigning to this property is not allowed.
     */
    public function get channels():Array
    {
        return _channels;
    }
    
    [ArrayElementType("mx.messaging.Channel")]
    /**
     *  @private
     */        
    public function set channels(values:Array):void
    {                   
        if (configured)
        {
            var message:String = resourceManager.getString(
                "messaging", "cannotAddWhenConfigured");
            throw new IllegalOperationError(message);
        }
            
        // Remove existing channels
        var channelsToRemove:Array = _channels.slice();
        var n:int = channelsToRemove.length;
        for (var i:int = 0; i < n; i++)
        {                           
            removeChannel(channelsToRemove[i]);   
        }        
                
        // Add new channels
        if (values != null && values.length > 0)
        {
            var m:int = values.length; 
            for (var j:int = 0; j < m; j++)
            {
                addChannel(values[j]);
            }
        }
    }
    
    //----------------------------------
    //  channelIds
    //----------------------------------

    /**
     *  @private
     */
    private var _channelIds:Array;                 

    /**
     *  The ids of the Channels used by the ChannelSet.
     */
    public function get channelIds():Array
    {
        if (_channelIds != null)
        {
            return _channelIds;
        }
        else
        {
            var ids:Array = [];
            var n:int = _channels.length;
            for (var i:int = 0; i < n; i++)
            {
                if (_channels[i] != null)
                    ids.push(_channels[i].id);
                else
                    ids.push(null);    
            }
            return ids;
        }
    } 

    //----------------------------------
    //  currentChannel
    //----------------------------------

    /**
     *  @private
     */
    private var _currentChannel:Channel;

    /**
     *  Returns the current Channel for the ChannelSet. 
     */
    public function get currentChannel():Channel
    {
        return _currentChannel;
    }

    //----------------------------------
    //  channelFailoverURIs
    //----------------------------------

    /**
     *  @private
     */
    private var _channelFailoverURIs:Object;

    /**
     *  @private
     *  Map of arrays of failoverURIs keyed by channel id for the Channels in this ChannelSet. 
     *  This property is assigned to by the ClusterMessageResponder in order to update the 
     *  member Channels with their failoverURIs.
     */
    mx_internal function get channelFailoverURIs():Object
    {
        return _channelFailoverURIs;
    }
    
    /**
     *  @private
     */
    mx_internal function set channelFailoverURIs(value:Object):void
    {
        _channelFailoverURIs = value;
        // Update any existing Channels in the set with their current failover endpoint URIs.
        var n:int = _channels.length;
        for (var i:int = 0; i < n; i++)
        {
            var channel:Channel = _channels[i];
            if (channel == null)
            {
                break; // The rest of the Channels have not been loaded yet.
            }
            else if (_channelFailoverURIs[channel.id] != null)
            {
                channel.failoverURIs = _channelFailoverURIs[channel.id];
            }
        }
    }
    
    //----------------------------------
    //  configured
    //----------------------------------
    
    /**
     *  @private
     */
    private var _configured:Boolean; 
    
    /**
     *  Indicates whether the ChannelSet is using automatically configured 
     *  Channels or manually assigned Channels.
     */
    mx_internal function get configured():Boolean
    {
        return _configured; 
    }    

    //----------------------------------
    //  connected
    //----------------------------------

    /**
     *  @private
     */
    private var _connected:Boolean;

    [Bindable(event="propertyChange")]
    /**
     *  Indicates whether the ChannelSet is connected.
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
            var event:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "connected", _connected, value)
            _connected = value;
            dispatchEvent(event);
            setAuthenticated(value && currentChannel && currentChannel.authenticated, _credentials, false /* Agents also listen for channel disconnects */);
        }
    }
        
    //----------------------------------
    //  clustered
    //----------------------------------    

    /**
     *  @private
     */
    private var _clustered:Boolean;
    
    /**
     *  Indicates whether the ChannelSet targets a clustered destination. 
     *  If true, upon a successful connection the ChannelSet will query the 
     *  destination for all clustered endpoints for its Channels and will assign
     *  failoverURIs to them.
     *  Channel ids are used to assign failoverURIs to the proper Channel instances
     *  so this requires that all Channels in the ChannelSet have non-null ids and an
     *  Error will be thrown when this property is set to true if this is not the case. 
     *  If the ChannelSet is not using url load balancing on the client this 
     *  property should not be set to true.
     */
    public function get clustered():Boolean
    {
        return _clustered;
    }
    
    /**
     *  @private
     */
    public function set clustered(value:Boolean):void
    {
        if (_clustered != value)
        {
            if (value)
            {
                // Cannot have a clustered ChannelSet that contains Channels with null ids.
                var ids:Array = channelIds;
                var n:int = ids.length;
                for (var i:int = 0; i < n; i++)
                {
                    if (ids[i] == null)
                    {
                        var message:String = resourceManager.getString(
                            "messaging", "cannotSetClusteredWithdNullChannelIds");
                        throw new IllegalOperationError(message);  
                    }                  
                }            
            }
            _clustered = value;
        }
    }
    
    //----------------------------------
    //  initialDestinationId
    //----------------------------------    
    
    /**
     *  @private
     */
    private var _initialDestinationId:String;
    
    /**
     *  Provides access to the initial destination this ChannelSet is used to access.
     *  When the clustered property is true, this value is used to request available failover URIs
     *  for the configured channels for the destination.
     */
    public function get initialDestinationId():String
    {
        return _initialDestinationId;
    }
    
    /**
     *  @private
     */
    public function set initialDestinationId(value:String):void
    {
        _initialDestinationId = value;
    }
    
    //----------------------------------
    //  messageAgents
    //----------------------------------    
    
    /**
     *  @private
     */ 
    private var _messageAgents:Array;
    
    /**
     *  Provides access to the set of MessageAgents that use this ChannelSet.
     */
    public function get messageAgents():Array
    {
        return _messageAgents;
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Returns a String containing the ids of the Channels in the ChannelSet.
     * 
     *  @return String representation of the ChannelSet.
     */
    override public function toString():String
    {
        var s:String = "[ChannelSet ";
        for (var i:uint = 0; i < _channels.length; i++)
        {
            if (_channels[i] != null)
                s += _channels[i].id + " ";
        }
        s += "]";
        return s;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Adds a Channel to the ChannelSet. A Channel with a null id cannot be added 
     *  to the ChannelSet if the ChannelSet targets a clustered destination.
     * 
     *  @param channel The Channel to add.
     * 
     *  @throws flash.errors.IllegalOperationError If the ChannelSet is 
     *             <code>configured</code>, adding a Channel is not supported.
     *             This error is also thrown if the ChannelSet's <code>clustered</code> property
     *             is <code>true</code> but the Channel has a null id.
     */
    public function addChannel(channel:Channel):void
    {
        if (channel == null)
            return;

        var message:String;
            
        if (configured)
        {
            message = resourceManager.getString(
                "messaging", "cannotAddWhenConfigured");
            throw new IllegalOperationError(message);
        }

        if (clustered && channel.id == null)
        {
            message = resourceManager.getString(
                "messaging", "cannotAddNullIdChannelWhenClustered");
            throw new IllegalOperationError(message);
        }
        
        if (_channels.indexOf(channel) != -1)
            return; // Channel already exists in the set.
                    
        _channels.push(channel);
        if (_credentials)
            channel.setCredentials(_credentials, null, _credentialsCharset);        
    }
    
    /**
     *  Removes a Channel from the ChannelSet. If the Channel to remove is 
     *  currently connected and being used by the ChannelSet, it is
     *  disconnected as well as removed.
     * 
     *  @param channel The Channel to remove.
     * 
     *  @throws flash.errors.IllegalOperationError If the ChannelSet is 
     *             <code>configured</code>, removing a Channel is not supported. 
     */
    public function removeChannel(channel:Channel):void
    {
        if (configured)
        {
            var message:String = resourceManager.getString(
                "messaging", "cannotRemoveWhenConfigured");
            throw new IllegalOperationError(message);
        }
        
        var channelIndex:int = _channels.indexOf(channel);
        if (channelIndex > -1)
        {
            _channels.splice(channelIndex, 1);
            // If the Channel being removed is currently in use, we need
            // to null it out for re-hunting, and potentially disconnect it.
            if ((_currentChannel != null) && (_currentChannel == channel))
            {
                if (connected)
                {
                    _shouldHunt = false;
                    disconnectChannel();
                }
                _currentChannel = null;
                _currentChannelIndex = -1;
            }
        }
    }
    
    /**
     *  Connects a MessageAgent to the ChannelSet. Once connected, the agent
     *  can use the ChannelSet to send messages.
     * 
     *  @param agent The MessageAgent to connect.
     */
    public function connect(agent:MessageAgent):void
    {
        if ((agent != null) && (_messageAgents.indexOf(agent) == -1))
        {
            _shouldBeConnected = true;
            _messageAgents.push(agent);
            agent.mx_internal::internalSetChannelSet(this);
            // Wire up agent's channel event listeners to this ChannelSet.
            addEventListener(ChannelEvent.CONNECT, agent.channelConnectHandler);
            addEventListener(ChannelEvent.DISCONNECT, agent.channelDisconnectHandler);
            addEventListener(ChannelFaultEvent.FAULT, agent.channelFaultHandler);
            
            // If the ChannelSet is already connected, notify the agent.
            if (connected)
                agent.channelConnectHandler(ChannelEvent.createEvent(ChannelEvent.CONNECT,
                                                                     _currentChannel, 
                                                                     false,
                                                                     false,
                                                                     connected)); 
        }                                       
    }
    
    /**
     *  Disconnects a specific MessageAgent from the ChannelSet. If this is the
     *  last MessageAgent using the ChannelSet and the current Channel in the set is 
     *  connected, the Channel will physically disconnect from the server.
     * 
     *  @param agent The MessageAgent to disconnect.
     */
    public function disconnect(agent:MessageAgent):void
    {
        if (agent == null) // Disconnect the ChannelSet completely.
        {
            var allMessageAgents:Array = _messageAgents.slice();
            var n:int = allMessageAgents.length;
            for (var i:int = 0; i < n; i++)
            {
                allMessageAgents[i].disconnect();
            }
            if (_authAgent != null)
            {
                _authAgent.state = AuthenticationAgent.SHUTDOWN_STATE;
                _authAgent = null;
            }
        }
        else // Disconnect a specific MessageAgent.
        {
            var agentIndex:int = agent != null ? _messageAgents.indexOf(agent) : -1;
            if (agentIndex != -1)
            {
                _messageAgents.splice(agentIndex, 1);
                // Remove the agent as a listener to this ChannelSet.
                removeEventListener(ChannelEvent.CONNECT, agent.channelConnectHandler);
                removeEventListener(ChannelEvent.DISCONNECT, agent.channelDisconnectHandler);
                removeEventListener(ChannelFaultEvent.FAULT, agent.channelFaultHandler);
                
                if (connected || _connecting) // Notify the agent of the disconnect.
                {
                    agent.channelDisconnectHandler(ChannelEvent.createEvent(ChannelEvent.DISCONNECT, 
                                                                            _currentChannel, false));
                }
                else // Remove any pending sends for this agent.
                {
                    var n2:int = _pendingSends.length;
                    for (var j:int = 0; j < n2; j++)
                    {
                        var ps:PendingSend = PendingSend(_pendingSends[j]);
                        if (ps.agent == agent)
                        {
                            _pendingSends.splice(j, 1);
                            j--;
                            n2--;
                            delete _pendingMessages[ps.message];
                        }
                    }
                }
                // Shut down the underlying Channel connection if this ChannelSet has
                // no more agents using it.
                if (_messageAgents.length == 0)
                {
                    _shouldBeConnected = false;
                    if (connected)
                        disconnectChannel();
                }
                
                // Null out automatically assigned ChannelSet on agent; if manually assigned leave it alone.
                if (agent.mx_internal::channelSetMode == MessageAgent.mx_internal::AUTO_CONFIGURED_CHANNELSET)
                    agent.mx_internal::internalSetChannelSet(null);
            }
        }
    }
    
    /**
     *  Disconnects all associated MessageAgents and disconnects any underlying Channel that
     *  is connected.
     *  Unlike <code>disconnect(MessageAgent)</code> which is invoked by the disconnect implementations
     *  of specific service components, this method provides a single, convenient point to shut down
     *  connectivity between the client and server.
     */
    public function disconnectAll():void
    {
        disconnect(null);
    }
    
    /**
     *  Handles a CONNECT ChannelEvent and redispatches the event.
     * 
     *  @param event The ChannelEvent.
     */
    public function channelConnectHandler(event:ChannelEvent):void
    {        
        _connecting = false;
        _connected = true; // Set internally to allow us to send pending messages before dispatching the connect event.
                                                  
        // Send any pending messages.
        while (_pendingSends.length > 0)
        {
            var ps:PendingSend = PendingSend(_pendingSends.shift());
            delete _pendingMessages[ps.message];
            
            var command:CommandMessage = ps.message as CommandMessage;
            if (command != null)
            {
                // Filter out any commands to trigger connection establishment, and ack them locally.            
                if (command.operation == CommandMessage.TRIGGER_CONNECT_OPERATION)
                {
                    var ack:AcknowledgeMessage = new AcknowledgeMessage();
                    ack.clientId = ps.agent.clientId;
                    ack.correlationId = command.messageId;
                    ps.agent.acknowledge(ack, command);
                    continue; 
                } 
                 
                if (!ps.agent.configRequested && ps.agent.needsConfig && 
                    (command.operation == CommandMessage.CLIENT_PING_OPERATION))
                { 
                    command.headers[CommandMessage.NEEDS_CONFIG_HEADER] = true;
                    ps.agent.configRequested = true;
                }
            }
                
            send(ps.agent, ps.message);
        }
        
        if (_hunting)
        {
            event.reconnecting = true;
            _hunting = false;
        }       
                    
        // Redispatch Channel connect event.                       
        dispatchEvent(event);
        // Dispatch delayed "connected" property change event.      
        var connectedChangeEvent:PropertyChangeEvent = PropertyChangeEvent.createUpdateEvent(this, "connected", false, true)
        dispatchEvent(connectedChangeEvent);
    }
    
    /**
     *  Handles a DISCONNECT ChannelEvent and redispatches the event.
     * 
     *  @param event The ChannelEvent.
     */
    public function channelDisconnectHandler(event:ChannelEvent):void
    {      
        _connecting = false;
        setConnected(false);
        
        // If we should be connected and the Channel isn't failing over
        // internally and wasn't rejected, hunt and try to reconnect.
        if (_shouldBeConnected && !event.reconnecting && !event.rejected)
        {
            if (_shouldHunt && hunt())
            {
                event.reconnecting = true;
                dispatchEvent(event);
                if (_currentChannel is NetConnectionChannel)
                {
                    // Insert slight delay for reconnect to allow NetConnection
                    // based channels to shut down and clean up in preparation 
                    // for our next connect attempt.
                    if (_reconnectTimer == null)
                    {
                        _reconnectTimer = new Timer(1, 1);
                        _reconnectTimer.addEventListener(TimerEvent.TIMER, reconnectChannel);
                        _reconnectTimer.start();
                    }
                }
                else // No need to wait with other channel types.
                {
                    connectChannel();
                }
            }
            else // No more hunting options; give up and fault pending sends.
            {
                dispatchEvent(event); 
                faultPendingSends(event);   
            }
        }
        else
        {
            dispatchEvent(event);
            // If the underlying Channel was rejected, fault pending sends.
            if (event.rejected)
                faultPendingSends(event);
        }
        // Flip this back to true in case it was turned off by an explicit Channel removal 
        // that triggered the current disconnect event.
        _shouldHunt = true;           
    }
    
    /**
     *  Handles a ChannelFaultEvent and redispatches the event.
     * 
     *  @param event The ChannelFaultEvent.
     */
    public function channelFaultHandler(event:ChannelFaultEvent):void
    {     
        if (event.channel.connected)
        {
            dispatchEvent(event);    
        } 
        else // The channel fault has resulted in disconnecting.
        {
            _connecting = false;
            setConnected(false);
            
            // If we should be connected and the Channel isn't failing over
            // internally, hunt and try to reconnect.
            if (_shouldBeConnected && !event.reconnecting && !event.rejected)
            {
                if (hunt())
                {
                    event.reconnecting = true;
                    dispatchEvent(event);
                    if (_currentChannel is NetConnectionChannel)
                    {
                        // Insert slight delay for reconnect to allow 
                        // NetConnection based channels to shut down and clean 
                        // up in preparation for our next connect attempt.
                        if (_reconnectTimer == null)
                        {
                            _reconnectTimer = new Timer(1, 1);
                            _reconnectTimer.addEventListener(TimerEvent.TIMER, reconnectChannel);
                            _reconnectTimer.start();
                        }
                    }
                    else // No need to wait with other channel types.
                    {
                        connectChannel();   
                    }
                }
                else // No more hunting options; give up and fault pending sends.
                {
                    dispatchEvent(event); 
                    faultPendingSends(event);
                }
            }
            else
            {
                dispatchEvent(event);
                // If the underlying Channel was rejected, fault pending sends.
                if (event.rejected)
                    faultPendingSends(event);
            }        
        }
    }    
    
    /**
     *  Authenticates the ChannelSet with the server using the provided credentials.
     *  Unlike other operations on Channels and the ChannelSet, this operation returns an 
     *  AsyncToken that client code may add a responder to in order to handle success or 
     *  failure directly.
     *  If the ChannelSet is not connected to the server when this method is invoked it will 
     *  trigger a connect attempt, and if successful, send the login command to the server.
     *  Only one login or logout operation may be pending at a time and overlapping calls will
     *  generate an IllegalOperationError.
     *  Invoking login when the ChannelSet is already authenticated will generate also generate
     *  an IllegalOperationError.
     * 
     *  @param username The username.
     *  @param password The password.
     *  @param charset The character set encoding to use while encoding the
     *  credentials. The default is null, which implies the legacy charset of
     *  ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     *
     *  @return Returns a token that client code may add a responder to in order to handle
     *  success or failure directly.
     * 
     *  @throws flash.errors.IllegalOperationError in two situations; if the ChannelSet is
     *          already authenticated, or if a login or logout operation is currently in progress.
     */
    public function login(username:String, password:String, charset:String=null):AsyncToken
    {
        if (authenticated)
            throw new IllegalOperationError("ChannelSet is already authenticated.");
            
        if ((_authAgent != null) && (_authAgent.state != AuthenticationAgent.LOGGED_OUT_STATE))
            throw new IllegalOperationError("ChannelSet is in the process of logging in or logging out.");
        
        if (charset != Base64Encoder.CHARSET_UTF_8);
            charset = null; // Use legacy charset, ISO-Latin-1.
        
        var credentials:String = null;
        if (username != null && password != null)
        {
            var rawCredentials:String = username + ":" + password;
            var encoder:Base64Encoder = new Base64Encoder();
            if (charset == Base64Encoder.CHARSET_UTF_8)
                encoder.encodeUTFBytes(rawCredentials);            
            else
                encoder.encode(rawCredentials);
            credentials = encoder.drain();
        }
        
        var msg:CommandMessage = new CommandMessage();
        msg.operation = CommandMessage.LOGIN_OPERATION;
        msg.body = credentials;
        if (charset != null)
            msg.headers[CommandMessage.CREDENTIALS_CHARSET_HEADER] = charset;           

        // A non-null, non-empty destination is required to send using an agent.
        // This value is ignored on the server and the message must be handled by an AuthenticationService.
        msg.destination = "auth"; 
        
        var token:AsyncToken = new AsyncToken(msg);
        if (_authAgent == null)
            _authAgent = new AuthenticationAgent(this);
        _authAgent.registerToken(token);
        _authAgent.state = AuthenticationAgent.LOGGING_IN_STATE;
        send(_authAgent, msg);
        return token;
    }
    
    /**
     *  Logs the ChannelSet out from the server. Unlike other operations on Channels
     *  and the ChannelSet, this operation returns an AsyncToken that client code may
     *  add a responder to in order to handle success or failure directly.
     *  If logout is successful any credentials that have been cached for use in
     *  automatic reconnects are cleared for the ChannelSet and its Channels and their
     *  authenticated state is set to false.
     *  If the ChannelSet is not connected to the server when this method is invoked it
     *  will trigger a connect attempt, and if successful, send a logout command to the server.
     *
     *  <p>The MessageAgent argument is present to support legacy logout behavior and client code that 
     *  invokes this method should not pass a MessageAgent reference. Just invoke <code>logout()</code>
     *  passing no arguments.</p>
     *
     *  <p>This method is also invoked by service components from their <code>logout()</code>
     *  methods, and these components pass a MessageAgent reference to this method when they logout. 
     *  The presence of this argument is the trigger to execute legacy logout behavior that differs 
     *  from the new behavior described above. 
     *  Legacy behavior only sends a logout request to the server if the client is connected
     *  and authenticated. 
     *  If these conditions are not met the legacy behavior for this method is to do nothing other 
     *  than clear any credentials that have been cached for use in automatic reconnects.</p>
     *  
     *  @param agent Legacy argument. The MessageAgent that is initiating the logout.
     *  
     *  @return Returns a token that client code may
     *  add a responder to in order to handle success or failure directly.
     * 
     *  @throws flash.errors.IllegalOperationError if a login or logout operation is currently in progress.
     */ 
    public function logout(agent:MessageAgent=null):AsyncToken
    {        
        _credentials = null;
        if (agent == null)
        {
            if ((_authAgent != null) && (_authAgent.state == AuthenticationAgent.LOGGING_OUT_STATE
                                         || _authAgent.state == AuthenticationAgent.LOGGING_IN_STATE))
                throw new IllegalOperationError("ChannelSet is in the process of logging in or logging out.");
            
            // Clear out current credentials on the client.
            var n:int = _messageAgents.length;
            var i:int = 0;
            for (; i < n; i++)
            {
                _messageAgents[i].internalSetCredentials(null);
            }
            n = _channels.length;
            for (i = 0; i < n; i++)
            {
                if (_channels[i] != null)
                    _channels[i].internalSetCredentials(null);
            }
            
            var msg:CommandMessage = new CommandMessage();
            msg.operation = CommandMessage.LOGOUT_OPERATION;
            
            // A non-null, non-empty destination is required to send using an agent.
            // This value is ignored on the server and the message must be handled by an AuthenticationService.
            msg.destination = "auth";
            
            var token:AsyncToken = new AsyncToken(msg);
            if (_authAgent == null)
                _authAgent = new AuthenticationAgent(this);
            _authAgent.registerToken(token);
            _authAgent.state = AuthenticationAgent.LOGGING_OUT_STATE;
            send(_authAgent, msg);  
            return token;          
        }
        else // Legacy logout logic.
        {            
            var n2:int = _channels.length;
            for (var i2:int = 0; i2 < n2; i2++)
            {
                if (_channels[i2] != null)
                    _channels[i2].logout(agent);
            }
            return null; // Legacy service logout() impls don't expect a token.
        }
    }    

    /**
     *  Sends a message from a MessageAgent over the currently connected Channel.
     * 
     *  @param agent The MessageAgent sending the message.
     *  
     *  @param message The Message to send.
     * 
     *  @throws mx.messaging.errors.NoChannelAvailableError If the ChannelSet has no internal
     *                                  Channels to use.
     */
    public function send(agent:MessageAgent, message:IMessage):void
    {
        if (connected)
        {    
            // Filter out any commands to trigger connection establishment, and ack them locally.            
            if ((message is CommandMessage) && (CommandMessage(message).operation == CommandMessage.TRIGGER_CONNECT_OPERATION))
            {
                var ack:AcknowledgeMessage = new AcknowledgeMessage();
                ack.clientId = agent.clientId;
                ack.correlationId = message.messageId;
                agent.acknowledge(ack, message);
                return; 
            }
                              
            // If this ChannelSet targets a clustered destination, request the
            // endpoint URIs for the cluster.
            if (!_hasRequestedClusterEndpoints && clustered)
            {            
                var msg:CommandMessage = new CommandMessage();
                // Fetch failover URIs for the correct destination.
                if (agent is AuthenticationAgent)
                {
                    msg.destination = initialDestinationId;
                }
                else
                {
                    msg.destination = agent.destination;
                }
                msg.operation = CommandMessage.CLUSTER_REQUEST_OPERATION;
                _currentChannel.sendClusterRequest(new ClusterMessageResponder(msg, this));    
                _hasRequestedClusterEndpoints = true;                           
            }                    

            _currentChannel.send(agent, message);
        }
        else
        {
            // Filter out duplicate messages here while waiting for the underlying Channel to connect.
            if (_pendingMessages[message] == null)
            {
                _pendingMessages[message] = true;
                _pendingSends.push(new PendingSend(agent, message));
            }
            
            if (!_connecting)
            {
                if ((_currentChannel == null) || (_currentChannelIndex == -1))
                    hunt();
                    
                if (_currentChannel is NetConnectionChannel)
                {
                    // Insert a slight delay in case we've hunted to a
                    // NetConnection channel that doesn't allow a reconnect 
                    // within the same frame as a disconnect.
                    if (_reconnectTimer == null)
                    {
                        _reconnectTimer = new Timer(1, 1);
                        _reconnectTimer.addEventListener(TimerEvent.TIMER, reconnectChannel);
                        _reconnectTimer.start();
                    }
                }
                else // No need to wait with other channel types.
                {
                    connectChannel();   
                }
            }
        }
    }
    
    /**
     *  Stores the credentials and passes them through to every connected channel.
     *
     *  @param credentials The credentials for the MessageAgent.
     *  @param agent The MessageAgent that is setting the credentials.
     *  @param charset The character set encoding used while encoding the
     *  credentials. The default is null, which implies the legacy encoding of
     *  ISO-Latin-1.
     *
     *  @throws flash.errors.IllegalOperationError in two situations; if credentials
     *  have already been set and an authentication is in progress with the remote
     *  detination, or if authenticated and the credentials specified don't match
     *  the currently authenticated credentials.
     */
    public function setCredentials(credentials:String, agent:MessageAgent, charset:String=null):void
    {
        _credentials = credentials;
        var n:int = _channels.length;        
        for (var i:int = 0; i < n; i++)
        {
            if (_channels[i] != null)
                _channels[i].setCredentials(_credentials, agent, charset);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  @private
     *  Handles a successful login or logout operation for the ChannelSet.
     */
    mx_internal function authenticationSuccess(agent:AuthenticationAgent, token:AsyncToken, ackMessage:AcknowledgeMessage):void
    {
        // Reset authentication state depending on whether a login or logout was successful.
        var command:CommandMessage = CommandMessage(token.message);
        var handlingLogin:Boolean = (command.operation == CommandMessage.LOGIN_OPERATION); 
        var creds:String = (handlingLogin) ? String(command.body) : null;
        
        if (handlingLogin)
        {
            // First, sync everything with the current credentials.
            _credentials = creds;
            var n:int = _messageAgents.length;
            var i:int = 0;
            for (; i < n; i++)
            {
                _messageAgents[i].internalSetCredentials(creds);
            }
            n = _channels.length;
            for (i = 0; i < n; i++)
            {
                if (_channels[i] != null)
                    _channels[i].internalSetCredentials(creds);
            }
            
            agent.state = AuthenticationAgent.LOGGED_IN_STATE;
            // Flip the currently connected channel to authenticated; this percolates
            // back up through the ChannelSet and agent's authenticated properties.
            currentChannel.setAuthenticated(true);
        }
        else // Logout.
        {       
            // Shutdown the current logged out agent.
            agent.state = AuthenticationAgent.SHUTDOWN_STATE;
            _authAgent = null;
            disconnect(agent);                 
                     
            // Flip current channel to *not* authenticated; this percolates
            // back up through the ChannelSet and agent's authenticated properties.
            currentChannel.setAuthenticated(false);
        }
        
        // Notify.
        var resultEvent:ResultEvent = ResultEvent.createEvent(ackMessage.body, token, ackMessage);
        dispatchRPCEvent(resultEvent);
    }
    
    /**
     *  @private
     *  Handles a failed login or logout operation for the ChannelSet.
     */
    mx_internal function authenticationFailure(agent:AuthenticationAgent, token:AsyncToken, faultMessage:ErrorMessage):void
    {
        var messageFaultEvent:MessageFaultEvent = MessageFaultEvent.createEvent(faultMessage);        
        var faultEvent:FaultEvent = FaultEvent.createEventFromMessageFault(messageFaultEvent, token);        
        // Leave the ChannelSet in its current auth state and dispose of the auth agent that failed.
        agent.state = AuthenticationAgent.SHUTDOWN_STATE;                        
        _authAgent = null;
        disconnect(agent);
        // And notify.
        dispatchRPCEvent(faultEvent);
    }
    
    //--------------------------------------------------------------------------
    //
    // Private Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  @private
     *  Helper method to connect the current internal Channel.
     */
    private function connectChannel():void
    {
        if (!connected && !_connecting)
        {
            _connecting = true;
            _currentChannel.connect(this);
            // Listen for any server pushed messages on the Channel.
            _currentChannel.addEventListener(MessageEvent.MESSAGE, messageHandler);
        }
    }
    
    /**
     *  @private
     *  Helper method to disconnect the current internal Channel.
     */
    private function disconnectChannel():void
    {
        _connecting = false;        
        // Stop listening for server pushed messages on the Channel.
        _currentChannel.removeEventListener(MessageEvent.MESSAGE, messageHandler);
        _currentChannel.disconnect(this);
    }
    
    /**
     *  @private
     *  Helper method to dispatch authentication-related RPC events.
     * 
     *  @param event The event to dispatch.
     */
    private function dispatchRPCEvent(event:AbstractEvent):void
    {
        event.callTokenResponders();
        dispatchEvent(event);
    }
    
    /**
     *  @private
     *  Redispatches message events from the currently connected Channel.
     * 
     *  @param event The MessageEvent from the Channel.
     */
    private function messageHandler(event:MessageEvent):void
    {
        dispatchEvent(event);
    }
    
    /**
     *  @private
     *  Helper method to hunt to the next available internal Channel for the
     *  ChannelSet.
     * 
     *  @return True if hunting to the next available Channel was successful; false if hunting
     *          exhausted available channels and has reset to the beginning of the set.
     * 
     *  @throws mx.messaging.errors.NoChannelAvailableError If the ChannelSet has no internal
     *                                  Channels to use.
     */
    private function hunt():Boolean
    {
        if (_channels.length == 0)
        {
            var message:String = resourceManager.getString(
                "messaging", "noAvailableChannels");
            throw new NoChannelAvailableError(message);
        }
        
        // Advance to next channel, and reset to beginning if all Channels in the set
        // have been attempted.
        if (++_currentChannelIndex >= _channels.length)
        {
            _currentChannelIndex = -1;
            return false;
        }       
        
        // If we've advanced past the first channel, indicate that we're hunting.
        if (_currentChannelIndex > 0)
            _hunting = true;
        
        // Set current channel.              
        if (configured)
        {           
            if (_channels[_currentChannelIndex] != null)
            {
                _currentChannel = _channels[_currentChannelIndex];   
            }
            else
            {
                _currentChannel = ServerConfig.getChannel(_channelIds[
                                        _currentChannelIndex], _clustered);
                _currentChannel.setCredentials(_credentials);
                _channels[_currentChannelIndex] = _currentChannel;
            }
        }
        else
        {            
            _currentChannel = _channels[_currentChannelIndex];
        }
        
        // Ensure that the current channel is assigned failover URIs it if was lazily instantiated.
        if ((_channelFailoverURIs != null) && (_channelFailoverURIs[_currentChannel.id] != null))
            _currentChannel.failoverURIs = _channelFailoverURIs[_currentChannel.id];
        
        return true;
    }
    
    /**
     *  @private
     *  This method is invoked by a timer and it works around a reconnect issue 
     *  with NetConnection based channels within a single frame by reconnecting after a slight delay.
     */
    private function reconnectChannel(event:TimerEvent):void
    {
        _reconnectTimer.stop();
        _reconnectTimer.removeEventListener(TimerEvent.TIMER, reconnectChannel);
        _reconnectTimer = null;
        connectChannel();        
    }
    
    /**
     *  @private
     *  Helper method to fault pending messages. 
     *  The ErrorMessage is tagged with a __retryable__ header to indicate that 
     *  the error was due to connectivity problems on the client as opposed to 
     *  a server error response and the message can be retried (resent).
     * 
     *  @param event A ChannelEvent.DISCONNECT or a ChannelFaultEvent that is the root cause
     *               for faulting these pending sends.
     */
    private function faultPendingSends(event:ChannelEvent):void
    {
        while (_pendingSends.length > 0)
        {
            var ps:PendingSend = _pendingSends.shift() as PendingSend;
            var pendingMsg:IMessage = ps.message;                       
            delete _pendingMessages[pendingMsg];
            // Fault the message to its agent.
            var errorMsg:ErrorMessage = new ErrorMessage();
            errorMsg.correlationId = pendingMsg.messageId;
            errorMsg.headers[ErrorMessage.RETRYABLE_HINT_HEADER] = true;
            errorMsg.faultCode = "Client.Error.MessageSend";
            errorMsg.faultString = resourceManager.getString(
                "messaging", "sendFailed");
            if (event is ChannelFaultEvent)
            {
                var faultEvent:ChannelFaultEvent = event as ChannelFaultEvent;
                errorMsg.faultDetail = faultEvent.faultCode + " " + 
                                   faultEvent.faultString + " " +
                                   faultEvent.faultDetail;
                // This is to make streaming channels report authentication fault
                // codes correctly as they don't report connected until streaming 
                // connection is established and hence end up here.  
                if (faultEvent.faultCode == "Channel.Authentication.Error")
                    errorMsg.faultCode = faultEvent.faultCode;
            }
            // ChannelEvent.DISCONNECT is treated the same as never
            // being able to connect at all.
            else
            {
                errorMsg.faultDetail = resourceManager.getString(
                    "messaging", "cannotConnectToDestination");
            }
            errorMsg.rootCause = event;
            ps.agent.fault(errorMsg, pendingMsg);
        }
    }
}

}

//------------------------------------------------------------------------------
//
// Private Classes
// 
//------------------------------------------------------------------------------

import mx.core.mx_internal;
import mx.messaging.ChannelSet;
import mx.messaging.MessageAgent;
import mx.messaging.MessageResponder;
import mx.logging.Log;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.rpc.AsyncToken;

use namespace mx_internal;

/**
 *  @private
 *  Clustered ChannelSets need to request the clustered channel endpoints for
 *  the channels they contain upon a successful connect. However, Channels
 *  require that all outbound messages be sent by a MessageAgent that their 
 *  internal MessageResponder implementations can callback to upon a response
 *  or fault. The ChannelSet is not a MessageAgent, so in this case, it 
 *  circumvents the regular Channel.send() by passing its own custom responder
 *  to Channel.sendUsingCustomResponder().
 * 
 *  This is the custom responder.
 */
class ClusterMessageResponder extends MessageResponder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
     
    /**
     *  Constructor.
     */
    public function ClusterMessageResponder(message:IMessage, channelSet:ChannelSet)
    {
        super(null, message);
        _channelSet = channelSet;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  @private
     *  Gives the responder access to this ChannelSet, to pass it failover URIs for
     *  its channels.
     */
    private var _channelSet:ChannelSet;
    
    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Handles a cluster message response.
     * 
     *  @param message The response Message.
     */
    override protected function resultHandler(message:IMessage):void
    {        
        if ((message.body != null) && (message.body is Array))
        {
            var channelFailoverURIs:Object = {};
            var mappings:Array = message.body as Array;
            var n:int = mappings.length;
            for (var i:int = 0; i < n; i++)
            {
                var channelToEndpointMap:Object = mappings[i];
                for (var channelId:Object in channelToEndpointMap)
                {
                    if (channelFailoverURIs[channelId] == null)
                        channelFailoverURIs[channelId] = [];
                        
                    channelFailoverURIs[channelId].push(channelToEndpointMap[channelId]);
                }
            }
            _channelSet.channelFailoverURIs = channelFailoverURIs;
        }   
    }
}
 
/**
 *  @private
 *  Stores a pending message to send when the ChannelSet does not have a
 *  connected Channel to use immediately.
 */
class PendingSend
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Constructor.
     * 
     *  @param agent The MessageAgent sending the message.
     *  
     *  @param msg The Message to send. 
     */
    public function PendingSend(agent:MessageAgent, message:IMessage)
    {
        super();
        this.agent = agent;
        this.message = message;
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  The MessageAgent.
     */
    public var agent:MessageAgent;

    /**
     *  @private
     *  The Message to send.
     */
    public var message:IMessage;
    
}

/**
 *  @private
 *  Helper class for handling and redispatching login and logout results or faults.
 */
class AuthenticationAgent extends MessageAgent
{
    //--------------------------------------------------------------------------
    //
    // Public Static Constants
    // 
    //--------------------------------------------------------------------------
    
    // State constants.
    public static const LOGGED_OUT_STATE:int = 0;
    public static const LOGGING_IN_STATE:int = 1;
    public static const LOGGED_IN_STATE:int = 2;
    public static const LOGGING_OUT_STATE:int = 3;
    public static const SHUTDOWN_STATE:int = 4;
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Constructor.
     */
    public function AuthenticationAgent(channelSet:ChannelSet)
    {
        _log = Log.getLogger("ChannelSet.AuthenticationAgent");
        _agentType = "authentication agent";
        // Must set log and agent type before assigning channelSet.
        this.channelSet = channelSet;        
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     * Map of login/logout message Ids to associated tokens.
     */
    private var tokens:Object = {};
    
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------
    
    private var _state:int = LOGGED_OUT_STATE;

    /**
     * Returns the current state for the agent.
     * See the static state constants defined by this class.
     */
    public function get state():int
    {
        return _state;
    }
    
    public function set state(value:int):void
    {
        _state = value;
        if (value == SHUTDOWN_STATE)
            tokens = null;
    }

    //--------------------------------------------------------------------------
    //
    // Public Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     * Registers an outbound login/logout message and its associated token for response/fault handling.
     */
    public function registerToken(token:AsyncToken):void
    {
        tokens[token.message.messageId] = token;
    }
    
    /**
     * Acknowledge message callback.
     */
    override public function acknowledge(ackMsg:AcknowledgeMessage, msg:IMessage):void
    {
        if (state == SHUTDOWN_STATE)
            return;
        
        var error:Boolean = ackMsg.headers[AcknowledgeMessage.ERROR_HINT_HEADER];
        // Super will clean the error hint from the message.
        super.acknowledge(ackMsg, msg);
        // If acknowledge is *not* for a message that caused an error
        // dispatch a result event.
        if (!error)
        {
            var token:AsyncToken = tokens[msg.messageId];
            delete tokens[msg.messageId];
            channelSet.authenticationSuccess(this, token, ackMsg as AcknowledgeMessage);
        }
    }
    
    /**
     * Fault callback.
     */
    override public function fault(errMsg:ErrorMessage, msg:IMessage):void
    {
        if (state == SHUTDOWN_STATE)
            return;
        
        super.fault(errMsg, msg);
        
        var token:AsyncToken = tokens[msg.messageId];
        delete tokens[msg.messageId];
        channelSet.authenticationFailure(this, token, errMsg as ErrorMessage);
    }
}
