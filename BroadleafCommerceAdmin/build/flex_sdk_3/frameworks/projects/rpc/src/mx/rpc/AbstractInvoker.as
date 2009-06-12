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

import flash.events.EventDispatcher;
import flash.utils.getQualifiedClassName;
import flash.events.Event;

import mx.core.mx_internal;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.errors.MessagingError;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.IMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.events.AbstractEvent;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.InvokeEvent;
import mx.rpc.events.ResultEvent;
import mx.utils.ObjectProxy;
import mx.utils.StringUtil;

use namespace mx_internal;

[ResourceBundle("rpc")]

/**
 * An invoker is an object that actually executes a remote procedure call (RPC).
 * For example, RemoteObject, HTTPService, and WebService objects are invokers.
 */
public class AbstractInvoker extends EventDispatcher
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function AbstractInvoker()
    {
        super();
        _log = Log.getLogger("mx.rpc.AbstractInvoker");
        activeCalls = new ActiveCalls();
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
    // Properties
    //
    //-------------------------------------------------------------------------

    [Bindable("resultForBinding")]

    /**
     *  The result of the last invocation.
     */
    public function get lastResult():Object
    {
        return _result;
    }

    [Inspectable(defaultValue="true", category="General")]

    /**
     * When this value is true, anonymous objects returned are forced to bindable objects.
     */
    public function get makeObjectsBindable():Boolean
    {
        return _makeObjectsBindable;
    }

    public function set makeObjectsBindable(b:Boolean):void
    {
        _makeObjectsBindable = b;
    }
    
    /**
    *  Event dispatched for binding when the <code>result</code> property
    *  changes.
    */
    mx_internal static const BINDING_RESULT:String = "resultForBinding";

    //-------------------------------------------------------------------------
    //
    //             Public Methods
    //
    //-------------------------------------------------------------------------

    /**
     *  Cancels the last service invocation or an invokation with the specified ID.
     *  Even though the network operation may still continue, no result or fault event
     *  is dispatched.
     * 
     *  @param id The messageId of the invocation to cancel. Optional. If omitted, the
     *         last service invocation is canceled.
     *  
     *  @return The AsyncToken associated with the call that is cancelled or null if no call was cancelled.
     */
    public function cancel(id:String = null):AsyncToken
    {
        if (id != null)
            return activeCalls.removeCall(id);
        else 
            return activeCalls.cancelLast();
    }

    /**
     *  Sets the <code>result</code> property of the invoker to <code>null</code>.
     *  This is useful when the result is a large object that is no longer being
     *  used.
     *
     *  @param  fireBindingEvent Set to <code>true</code> if you want anything
     *          bound to the result to update. Otherwise, set to
     *          <code>false</code>.
     *          The default value is <code>true</code>
     */
    public function clearResult(fireBindingEvent:Boolean = true):void
    {
        _result = null;
        if (fireBindingEvent)
            dispatchEvent(new flash.events.Event(BINDING_RESULT));
    }


   //-------------------------------------------------------------------------
   //
   // Internal Methods
   //
   //-------------------------------------------------------------------------

   /**
    *  This method is overridden in subclasses to redirect the event to another
    *  class.
    *
    *  @private
    */
    mx_internal function dispatchRpcEvent(event:AbstractEvent):void
    {
        event.callTokenResponders();
        if (!event.isDefaultPrevented())
        {
            dispatchEvent(event);
        }
    }

    /**
     *  Take the MessageAckEvent and take the result, store it, and broadcast out
     *  appropriately.
     *
     *  @private
     */
    mx_internal function resultHandler(event:MessageEvent):void
    {
        var token:AsyncToken = preHandle(event);

        //if the handler didn't give us something just bail
        if (token == null)
            return;

        if (processResult(event.message, token))
        {
            dispatchEvent(new flash.events.Event(BINDING_RESULT));
            var resultEvent:ResultEvent = ResultEvent.createEvent(_result, token, event.message);
            resultEvent.headers = _responseHeaders;
            dispatchRpcEvent(resultEvent);
        }
        //no else, we assume process would have dispatched the faults if necessary
    }

    /**
     *  Take the fault and convert it into a rpc.events.FaultEvent.
     *
     *  @private
     */
    mx_internal function faultHandler(event:MessageFaultEvent):void
    {
        var msgEvent:MessageEvent = MessageEvent.createEvent(MessageEvent.MESSAGE, event.message);
        var token:AsyncToken = preHandle(msgEvent);

        // continue only on a matching or empty correlationId
        // empty correlationIds could be the result of de/serialization errors
        if ((token == null) &&
            (AsyncMessage(event.message).correlationId != null) &&
            (AsyncMessage(event.message).correlationId != "") &&
            (event.faultCode != "Client.Authentication"))
        {
            return;
        }

        var fault:Fault = new Fault(event.faultCode, event.faultString, event.faultDetail);
        fault.rootCause = event.rootCause;
        var faultEvent:FaultEvent = FaultEvent.createEvent(fault, token, event.message);
        faultEvent.headers = _responseHeaders;
        dispatchRpcEvent(faultEvent);
    }
        
    /**
     * Return the id for the NetworkMonitor.
     * @private
     */
    mx_internal function getNetmonId():String
    {
        return null;
    }

    /**
     * @private
     */
    mx_internal function invoke(message:IMessage, token:AsyncToken = null) : AsyncToken
    {
        if (token == null)
            token = new AsyncToken(message);
        else
            token.setMessage(message);

        activeCalls.addCall(message.messageId, token);

        var fault:Fault;
        try
        {
            //asyncRequest.invoke(message, new AsyncResponder(resultHandler, faultHandler, token));
            asyncRequest.invoke(message, new Responder(resultHandler, faultHandler));
            dispatchRpcEvent(InvokeEvent.createEvent(token, message));
        }
        catch(e:MessagingError)
        {
            _log.warn(e.toString());
            var errorText:String = resourceManager.getString(
                "rpc", "cannotConnectToDestination",
                [ asyncRequest.destination ]);
            fault = new Fault("InvokeFailed", e.toString(), errorText);
            new AsyncDispatcher(dispatchRpcEvent, [FaultEvent.createEvent(fault, token, message)], 10);
        }
        catch(e2:Error)
        {
            _log.warn(e2.toString());
            fault = new Fault("InvokeFailed", e2.message);
            new AsyncDispatcher(dispatchRpcEvent, [FaultEvent.createEvent(fault, token, message)], 10);
        }

        return token;
    }

    /**
     * Find the matching call object and pass it back.
     *
     * @private
     */
    mx_internal function preHandle(event:MessageEvent):AsyncToken
    {
        return activeCalls.removeCall(AsyncMessage(event.message).correlationId);
    }

    /**
     * @private
     */
    mx_internal function processResult(message:IMessage, token:AsyncToken):Boolean
    {
        var body:Object = message.body;
        
        if (makeObjectsBindable && (body != null) && (getQualifiedClassName(body) == "Object"))
        {
            _result = new ObjectProxy(body);            
        }
        else
        {
            _result = body;
        }

        return true;
    }

    /**
     * @private
     */
    mx_internal function get asyncRequest():AsyncRequest
    {
        if (_asyncRequest == null)
        {
            _asyncRequest = new AsyncRequest();
        }
        return _asyncRequest;
    }
    
    /**
     * @private
     */
    mx_internal function set asyncRequest(req:AsyncRequest):void
    {
        _asyncRequest = req;
    }

    /**
     * @private
     */
    mx_internal var activeCalls:ActiveCalls;

    /**
     * @private
     */
    mx_internal var _responseHeaders:Array;

    /**
     * @private
     */
    mx_internal var _result:Object;

    /**
     * @private
     */
    mx_internal var _makeObjectsBindable:Boolean;
    
    /**
     * @private
     */
    private var _asyncRequest:AsyncRequest;

    /**
     * @private
     */
    private var _log:ILogger;
}

}
