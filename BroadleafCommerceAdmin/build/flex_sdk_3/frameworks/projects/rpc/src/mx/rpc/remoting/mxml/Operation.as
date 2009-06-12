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

package mx.rpc.remoting.mxml
{

import mx.core.mx_internal;
import mx.managers.CursorManager;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AbstractService;
import mx.rpc.AsyncToken;
import mx.rpc.AsyncDispatcher;
import mx.rpc.Fault;
import mx.rpc.events.FaultEvent;
import mx.rpc.mxml.Concurrency;
import mx.rpc.mxml.IMXMLSupport;
import mx.rpc.remoting.RemoteObject;
import mx.rpc.remoting.Operation;
import mx.rpc.remoting.mxml.RemoteObject;
import mx.validators.Validator;

use namespace mx_internal;

[ResourceBundle("rpc")]

/**
 * The Operation used for RemoteObject when created in an MXML document.
 */
public class Operation extends mx.rpc.remoting.Operation implements IMXMLSupport
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    public function Operation(remoteObject:mx.rpc.remoting.RemoteObject = null, name:String = null)
    {
        super(remoteObject, name);

        this.remoteObject = mx.rpc.remoting.mxml.RemoteObject(remoteObject);
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

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

    [Inspectable(enumeration="multiple,single,last", defaultValue="multiple", category="General")]
    /**
     * The concurrency for this Operation.  If it has not been explicitly set the setting from the RemoteObject
     * will be used.
     */
    public function get concurrency():String
    {
        if (_concurrencySet)
        {
            return _concurrency;
        }
        //else
        return remoteObject.concurrency;
    }

    /**
     *  @private
     */
    public function set concurrency(c:String):void
    {
        _concurrency = c;
        _concurrencySet = true;
    }

    override mx_internal function setService(ro:AbstractService):void
    {
        super.setService(ro);
        remoteObject = mx.rpc.remoting.mxml.RemoteObject(ro);
    }

    /**
     * Whether this operation should show the busy cursor while it is executing.
     * If it has not been explicitly set the setting from the RemoteObject
     * will be used.
     */
    public function get showBusyCursor():Boolean
    {
        if (_showBusyCursorSet)
        {
            return _showBusyCursor;
        }
        //else
        return remoteObject.showBusyCursor;
    }

    public function set showBusyCursor(sbc:Boolean):void
    {
        _showBusyCursor = sbc;
        _showBusyCursorSet = true;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    override public function cancel(id:String = null):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.removeBusyCursor();
        }
        return super.cancel(id);
    }

    /**
     * @inheritDoc
     */
    override public function send(... args:Array):AsyncToken
    {
        if (Concurrency.SINGLE == concurrency && activeCalls.hasActiveCalls())
        {
            var token:AsyncToken = new AsyncToken(null);
			var message:String = resourceManager.getString(
				"rpc", "pendingCallExists");
            var fault:Fault = new Fault("ConcurrencyError", message);
            var faultEvent:FaultEvent = FaultEvent.createEvent(fault, token);
            new AsyncDispatcher(dispatchRpcEvent, [faultEvent], 10);
            return token;
        }
        
        // We delay endpoint initialization until now because MXML codegen may set 
        // the destination attribute after the endpoint and will clear out the 
        // channelSet.
        if (asyncRequest.channelSet == null && remoteObject.endpoint != null)
        {
            remoteObject.mx_internal::initEndpoint();
        }
        return super.send.apply(null, args);
    }


    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     * @private
     * Return the id for the NetworkMonitor
     */
    override mx_internal function getNetmonId():String
    {
        return remoteObject.id;
    }
    

    override mx_internal function invoke(message:IMessage, token:AsyncToken = null):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.setBusyCursor();
        }

        return super.invoke(message, token);
    }

    /*
     * Kill the busy cursor, find the matching call object and pass it back
     */
    override mx_internal function preHandle(event:MessageEvent):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.removeBusyCursor();
        }

        var wasLastCall:Boolean = activeCalls.wasLastCall(AsyncMessage(event.message).correlationId);
        var token:AsyncToken = super.preHandle(event);

        if (Concurrency.LAST == concurrency && !wasLastCall)
        {
            return null;
        }
        //else
        return token;
    }

    //--------------------------------------------------------------------------
    //
    // Private Variables
    // 
    //--------------------------------------------------------------------------

    private var _concurrency:String;
    
	private var _concurrencySet:Boolean;
    
	private var remoteObject:mx.rpc.remoting.mxml.RemoteObject;
    
	private var _showBusyCursor:Boolean;
    
	private var _showBusyCursorSet:Boolean;
}

}
