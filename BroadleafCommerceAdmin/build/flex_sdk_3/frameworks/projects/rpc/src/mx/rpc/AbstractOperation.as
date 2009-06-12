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

import mx.core.mx_internal;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AsyncToken;
import mx.rpc.events.AbstractEvent;

use namespace mx_internal;

/**
 * Dispatched when an Operation invocation successfully returns.
 * @eventType mx.rpc.events.ResultEvent.RESULT 
 */
[Event(name="result", type="mx.rpc.events.ResultEvent")]

/**
 * Dispatched when an Operation call fails.
 * @eventType mx.rpc.events.FaultEvent.FAULT 
 */
[Event(name="fault", type="mx.rpc.events.FaultEvent")]

[ResourceBundle("rpc")]

/**
 * The AbstractOperation class represents an individual method on a
 * service. An Operation can be called either by invoking the function of the
 * same name on the service or by accessing the Operation as a property on the
 * service and calling the <code>send()</code> method.
 * 
 * @see mx.rpc.AbstractService
 * @see mx.rpc.remoting.RemoteObject
 * @see mx.rpc.soap.WebService
 */
public class AbstractOperation extends AbstractInvoker
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new Operation. This is usually done directly by the MXML
     * compiler or automatically by the service when an unknown Operation has
     * been accessed. It is not recommended that a developer use this
     * constructor directly.
     *  
     *  @param service The service on which the Operation is being invoked.
     *  
     *  @param name The name of the new Operation.
     */
    public function AbstractOperation(service:AbstractService = null, name:String = null)
    {
        super();

        _service = service
        _name = name;
        this.arguments = {};
    }


    //-------------------------------------------------------------------------
    //
    // Variables
    //
    //-------------------------------------------------------------------------

    /**
     * The arguments to pass to the Operation when it is invoked. If you call
     * the <code>send()</code> method with no parameters, an array based on
     * this object is sent. If you call the <code>send()</code> method with
     * parameters (or call the function directly on the service) those
     * parameters are used instead of whatever is stored in this property.
     * For RemoteObject Operations the associated argumentNames array determines
     * the order of the arguments passed.
     */
    public var arguments:Object;

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

    /**
     * The name of this Operation. This is how the Operation is accessed off the
     * service. It can only be set once.
     */
    public function get name():String
    {
        return _name;
    }

    public function set name(n:String):void
    {
        if (!_name)
        {
            _name = n;
        }
        else
        {
            var message:String = resourceManager.getString(
                "rpc", "cannotResetOperationName");
            throw new Error(message);
        }
    }

    /**
     * Provides convenient access to the service on which the Operation
     * is being invoked. Note that the service cannot be changed after
     * the Operation is constructed.
     */
    public function get service():AbstractService
    {
        return _service;
    }

    /**
     * @private
     */
    mx_internal function setService(s:AbstractService):void
    {
        if (!_service)
        {
            _service = s;
        }
        else
        {
            var message:String = resourceManager.getString(
                "rpc", "cannotResetService")
            throw new Error(message);
        }
    }


    //-------------------------------------------------------------------------
    //
    //  Methods
    //
    //-------------------------------------------------------------------------

    /**
     * Executes the method. Any arguments passed in are passed along as part of
     * the method call. If there are no arguments passed, the arguments object
     * is used as the source of parameters.
     *
     * @param args Optional arguments passed in as part of the method call. If there
     * are no arguments passed, the arguments object is used as the source of 
     * parameters.
     *
     * @return AsyncToken Call using the asynchronous completion token pattern.
     * The same object is available in the <code>result</code> and
     * <code>fault</code> events from the <code>token</code> property.
     *
     */
    /* abstract */ public function send(... args:Array):AsyncToken
    {
        return null;
    }

    //---------------------------------
    // Helper methods
    //---------------------------------

   /*
    * This is unless we come up with a way for faceless components to support
    * event bubbling; dispatch the event if there's someone listening on us,
    * otherwise have the RemoteObject dispatch it in case there's a default
    * handler.
    */
    override mx_internal function dispatchRpcEvent(event:AbstractEvent):void
    {
        event.callTokenResponders();
        if (!event.isDefaultPrevented())
        {
            if (hasEventListener(event.type))
            {
                dispatchEvent(event);
            }
            else
            {
                _service.dispatchEvent(event);
            }
        }
    }


    //--------------------------------------------------------------------------
    //
    // Private Variables
    // 
    //--------------------------------------------------------------------------

    mx_internal var _service:AbstractService;
    private var _name:String;
}

}
