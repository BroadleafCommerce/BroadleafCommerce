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

package mx.rpc
{

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import flash.utils.describeType;
import flash.utils.flash_proxy;
import flash.utils.Proxy;

import mx.core.mx_internal;
import mx.messaging.ChannelSet;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.events.AbstractEvent;
import mx.utils.ObjectUtil;

use namespace flash_proxy;
use namespace mx_internal;

/**
 * The invoke event is dispatched when a service Operation is invoked so long as
 * an Error is not thrown before the Channel attempts to send the message.
 * @eventType mx.rpc.events.InvokeEvent.INVOKE 
  */
[Event(name="invoke", type="mx.rpc.events.InvokeEvent")]

/**
 * The result event is dispatched when a service call successfully returns and
 * isn't handled by the Operation itself.
 * @eventType mx.rpc.events.ResultEvent.RESULT 
 */
[Event(name="result", type="mx.rpc.events.ResultEvent")]

/**
 * The fault event is dispatched when a service call fails and isn't handled by
 * the Operation itself.
 * @eventType mx.rpc.events.FaultEvent.FAULT 
 */
[Event(name="fault", type="mx.rpc.events.FaultEvent")]

[ResourceBundle("rpc")]

[Bindable(event="operationsChange")]

/**
 * The AbstractService class is the base class for the WebService and
 * RemoteObject classes. This class does the work of creating Operations
 * which do the actual execution of remote procedure calls.
 */
public dynamic class AbstractService extends Proxy implements IEventDispatcher
{   
    //-------------------------------------------------------------------------
    //
    // Constructor
    //
    //-------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @param destination The destination of the service.
     */
    public function AbstractService(destination:String = null)
    {
        super();
        eventDispatcher = new EventDispatcher(this);
        asyncRequest = new AsyncRequest();

        if (destination)
            this.destination = destination;

        _operations = {};
    }

    //-------------------------------------------------------------------------
    //
    // Variables
    //
    //-------------------------------------------------------------------------

    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //-------------------------------------------------------------------------
    //
    //              Properties
    //
    //-------------------------------------------------------------------------

    //----------------------------------
    //  channelSet
    //----------------------------------

    /**
     *  Provides access to the ChannelSet used by the service. The
     *  ChannelSet can be manually constructed and assigned, or it will be 
     *  dynamically created to use the configured Channels for the
     *  <code>destination</code> for this service.
     */
    public function get channelSet():ChannelSet
    {
        return asyncRequest.channelSet;
    }

    /**
     *  @private
     */
    public function set channelSet(value:ChannelSet):void
    {
        if (channelSet != value)
        {
            asyncRequest.channelSet = value;
        }
    }

    //----------------------------------
    //  destination
    //----------------------------------

    [Inspectable(category="General")]

    /**
     * The destination of the service. This value should match a destination
     * entry in the services-config.xml file.
     */
    public function get destination():String
    {
        return asyncRequest.destination;
    }

    public function set destination(name:String):void
    {
        asyncRequest.destination = name;
    }

    //----------------------------------
    //  operations
    //----------------------------------

    /**
     * @private
     */
    mx_internal var _operations:Object;

    /**
     * @private
     * This is required by data binding.
     */
    public function get operations():Object
    {
        return _operations;
    }

    /**
     * The Operations array is usually only set by the MXML compiler if you
     * create a service using an MXML tag.
     */
    public function set operations(ops:Object):void
    {
        var op:AbstractOperation;
        for (var i:String in ops)
        {
            op = AbstractOperation(ops[i]);
            op.setService(this); // service is a write only property.
            if (!op.name)
                op.name = i;
            op.asyncRequest = asyncRequest;
        }
        _operations = ops;
    }

    //----------------------------------
    //  requestTimeout
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  Provides access to the request timeout in seconds for sent messages. 
     *  A value less than or equal to zero prevents request timeout.
     */ 
    public function get requestTimeout():int
    {
        return asyncRequest.requestTimeout;
    }

    /**
     *  @private
     */
    public function set requestTimeout(value:int):void
    {
        if (requestTimeout != value)
        {
            asyncRequest.requestTimeout = value;
        }
    }
    
    //-------------------------------------------------------------------------
    //
    //              Methods
    //
    //-------------------------------------------------------------------------

    //---------------------------------
    //   EventDispatcher methods
    //---------------------------------

    /**
     * @private
     */
    public function addEventListener(type:String, listener:Function,
        useCapture:Boolean = false, priority:int = 0, useWeakReference:Boolean = false):void
    {
        eventDispatcher.addEventListener(type, listener, useCapture, priority, useWeakReference);
    }

    /**
     * @private
     */
    public function dispatchEvent(event:Event):Boolean
    {
        return eventDispatcher.dispatchEvent(event);
    }

    /**
     * @private
     */
    public function removeEventListener(type:String, listener:Function, useCapture:Boolean = false):void
    {
        eventDispatcher.removeEventListener(type, listener, useCapture);
    }

    /**
     * @private
     */
    public function hasEventListener(type:String):Boolean
    {
        return eventDispatcher.hasEventListener(type);
    }
    
    /**
     * @private
     */
    public function willTrigger(type:String):Boolean
    {
        return eventDispatcher.willTrigger(type);
    }

    //---------------------------------
    //   Proxy methods
    //---------------------------------
    /**
     * @private
     */
    override flash_proxy function getProperty(name:*):*
    {
        return getOperation(getLocalName(name));
    }

    /**
     * @private
     */
    override flash_proxy function setProperty(name:*, value:*):void
    {
        var message:String = resourceManager.getString(
            "rpc", "operationsNotAllowedInService", [ getLocalName(name) ]);
        throw new Error(message);
    }

    /**
     * @private
     */
    override flash_proxy function callProperty(name:*, ... args:Array):*
    {
        return getOperation(getLocalName(name)).send.apply(null, args);
    }

    //used to store the nextName values
    private var nextNameArray:Array;
    
    /**
     * @private
     */
    override flash_proxy function nextNameIndex(index:int):int
    {
        if (index == 0)
        {
            nextNameArray = [];
            for (var op:String in _operations)
            {
                nextNameArray.push(op);    
            }    
        }
        return index < nextNameArray.length ? index + 1 : 0;
    }

    /**
     * @private
     */
    override flash_proxy function nextName(index:int):String
    {
        return nextNameArray[index-1];
    }

    /**
     * @private
     */
    override flash_proxy function nextValue(index:int):*
    {
        return _operations[nextNameArray[index-1]];
    }

    mx_internal function getLocalName(name:Object):String
    {
        if (name is QName)
        {
            return QName(name).localName;
        }
        else
        {
            return String(name);
        }
    }

    //---------------------------------
    //   Public methods
    //---------------------------------

    /**
     * Returns an Operation of the given name. If the Operation wasn't
     * created beforehand, subclasses are responsible for creating it during
     * this call. Operations are usually accessible by simply naming them after
     * the service variable (<code>myService.someOperation</code>), but if your
     * Operation name happens to match a defined method on the service (like
     * <code>setCredentials</code>), you can use this method to get the
     * Operation instead.
     * @param name Name of the Operation.
     * @return Operation that executes for this name.
     */
    public function getOperation(name:String):AbstractOperation
    {
        var o:Object = _operations[name];
        var op:AbstractOperation = (o is AbstractOperation) ? AbstractOperation(o) : null;
        return op;
    }

    /**
     *  Disconnects the service's network connection and removes any pending
     *  request responders.
     *  This method does not wait for outstanding network operations to complete.
     */
    public function disconnect():void
    {
        asyncRequest.disconnect();
    }

    /**
     * Sets the credentials for the destination accessed by the service when using Data Services on the server side.
     * The credentials are applied to all services connected over the same
     * ChannelSet. Note that services that use a proxy or a third-party adapter
     * to a remote endpoint will need to setRemoteCredentials instead.
     * 
     * @param username The username for the destination.
     * @param password The password for the destination.
     * @param charset The character set encoding to use while encoding the
     * credentials. The default is null, which implies the legacy charset of
     * ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     */
    public function setCredentials(username:String, password:String, charset:String=null):void
    {
        asyncRequest.setCredentials(username, password, charset);
    }

    /**
     * Logs the user out of the destination. 
     * Logging out of a destination applies to everything connected using the
     * same ChannelSet as specified in the server configuration. For example,
     * if you're connected over the my-rtmp channel and you log out using one
     * of your RPC components, anything that was connected over the same
     * ChannelSet is logged out.
     */
    public function logout():void
    {
        asyncRequest.logout();
    }

    /**
     * The username and password to be used to authenticate a user when
     * accessing a remote, third-party endpoint such as a web service through a
     * proxy or a remote object through a custom adapter when using Data Services on the server side.
     *
     * @param remoteUsername the username to pass to the remote endpoint
     * @param remotePassword the password to pass to the remote endpoint
     * @param charset The character set encoding to use while encoding the
     * remote credentials. The default is null, which implies the legacy charset
     * of ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     */
    public function setRemoteCredentials(remoteUsername:String, remotePassword:String, charset:String=null):void
    {
        asyncRequest.setRemoteCredentials(remoteUsername, remotePassword, charset);
    }
    
    //--------------------------------------------------------------
    //   Public methods from Object prototype not inherited by Proxy
    //--------------------------------------------------------------
    
    /**
     * Returns this service.
     * 
     * @private
     */  
    public function valueOf():Object
    {
        return this;
    }
    
    
    //--------------------------------------------------------------
    //   mx_internal for package methods
    //--------------------------------------------------------------
    
    /**
     * @private
     */
    mx_internal function hasTokenResponders(event:Event):Boolean
    {
        if (event is AbstractEvent)
        {
            var rpcEvent:AbstractEvent = event as AbstractEvent;
            if (rpcEvent.token != null && rpcEvent.token.hasResponder())
            {
                return true;
            }
        }

        return false;
    }   


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    mx_internal var _availableChannelIds:Array;
    mx_internal var asyncRequest:AsyncRequest;
    private var eventDispatcher:EventDispatcher;
}

}
