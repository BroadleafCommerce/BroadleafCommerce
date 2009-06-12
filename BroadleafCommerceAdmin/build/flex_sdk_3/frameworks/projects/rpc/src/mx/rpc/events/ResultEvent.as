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

package mx.rpc.events
{

import flash.events.Event;

import mx.core.mx_internal;
import mx.messaging.messages.IMessage;
import mx.rpc.AsyncToken;

use namespace mx_internal;

/**
 * The event that indicates an RPC operation has successfully returned a result.
 */
public class ResultEvent extends AbstractEvent
{
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
    * The RESULT event type.
    *
    * <p>The properties of the event object have the following values:</p>
    * <table class="innertable">
    *     <tr><th>Property</th><th>Value</th></tr>
    *     <tr><td><code>bubbles</code></td><td>false</td></tr>
    *     <tr><td><code>cancelable</code></td><td>true, preventDefault() 
    *       from the associated token's responder.result method will prevent
    *       the service or operation from dispatching this event</td></tr>
    *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
    *       event listener that handles the event. For example, if you use 
    *       <code>myButton.addEventListener()</code> to register an event listener, 
    *       myButton is the value of the <code>currentTarget</code>. </td></tr>
    *     <tr><td><code>message</code></td><td> The Message associated with this event.</td></tr>
    *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
    *       it is not always the Object listening for the event. 
    *       Use the <code>currentTarget</code> property to always access the 
    *       Object listening for the event.</td></tr>
    *     <tr><td><code>result</code></td><td>Result that the RPC call returns.</td></tr>
    *     <tr><td><code>token</code></td><td>The token that represents the indiviudal call
    *     to the method. Used in the asynchronous completion token pattern.</td></tr>
    *  </table>
    *     
    *  @eventType result      
     */
    public static const RESULT:String = "result";


    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Creates a new ResultEvent.
     * @param type The event type; indicates the action that triggered the event.
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     * @param cancelable Specifies whether the behavior associated with the event can be prevented.
     * @param result Object that holds the actual result of the call.
     * @param token Token that represents the call to the method. Used in the asynchronous completion token pattern.
     * @param message Source Message of the result.
     */
    public function ResultEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = true,
                                result:Object = null, token:AsyncToken = null, message:IMessage = null)
    {
        super(type, bubbles, cancelable, token, message);

        _result = result;
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     * In certain circumstances, headers may also be returned with a fault to
     * provide further context to the failure.
     */
    public function get headers():Object
    {
        return _headers;
    }

    /**
     * @private
     */
    public function set headers(value:Object):void
    {
        _headers = value;
    }

    /**
     * Result that the RPC call returns.
     */
    public function get result():Object
    {
        return _result;
    }


    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    public static function createEvent(result:Object = null, token:AsyncToken = null, message:IMessage = null):ResultEvent
    {
        return new ResultEvent(ResultEvent.RESULT, false, true, result, token, message);
    }

    /**
     * Because this event can be re-dispatched we have to implement clone to
     * return the appropriate type, otherwise we will get just the standard
     * event type.
     * @private
     */
    override public function clone():Event
    {
        return new ResultEvent(type, bubbles, cancelable, result, token, message);
    }
   /**
     * Returns a string representation of the ResultEvent.
     *
     * @return String representation of the ResultEvent.
     */
    override public function toString():String
    {
        return formatToString("ResultEvent", "messageId", "type", "bubbles", "cancelable", "eventPhase");
    }

    /*
     * Have the token apply the result.
     */
    override mx_internal function callTokenResponders():void
    {
        if (token != null)
            token.applyResult(this);
    }


    //--------------------------------------------------------------------------
    //
    //  Private Variables
    //
    //--------------------------------------------------------------------------

    private var _result:Object;
    private var _headers:Object;
}

}
