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
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.IMessage;
import mx.rpc.AsyncToken;
import mx.rpc.Fault;

use namespace mx_internal;

/**
 * This event is dispatched when an RPC call has a fault.
 */
public class FaultEvent extends AbstractEvent
{
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------    
    
   /**
    * The FAULT event type.
    *
    * <p>The properties of the event object have the following values:</p>
    * <table class="innertable">
    *     <tr><th>Property</th><th>Value</th></tr>
    *     <tr><td><code>bubbles</code></td><td>false</td></tr>
    *     <tr><td><code>cancelable</code></td><td>true, calling preventDefault() 
    *       from the associated token's responder.fault method will prevent
    *       the service or operation from dispatching this event</td></tr>
    *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
    *       event listener that handles the event. For example, if you use 
    *       <code>myButton.addEventListener()</code> to register an event listener, 
    *       myButton is the value of the <code>currentTarget</code>. </td></tr>
    *     <tr><td><code>fault</code></td><td>The Fault object that contains the
    *     details of what caused this event.</td></tr>   
    *     <tr><td><code>message</code></td><td>The Message associated with this event.</td></tr>
    *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
    *       it is not always the Object listening for the event. 
    *       Use the <code>currentTarget</code> property to always access the 
    *       Object listening for the event.</td></tr>
    *     <tr><td><code>token</code></td><td>The token that represents the call
    *     to the method. Used in the asynchronous completion token pattern.</td></tr>   
    *  </table>
    *  @eventType fault 
    */
    public static const FAULT:String = "fault";


    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------    

    /**
     * Creates a new FaultEvent. The fault is a required parameter while the call and message are optional.
     *
     * @param type The event type; indicates the action that triggered the event.
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     * @param cancelable Specifies whether the behavior associated with the event can be prevented.
     * @param fault Object that holds details of the fault, including a faultCode and faultString.
     * @param token Token representing the call to the method. Used in the asynchronous completion token pattern.
     * @param message Source Message of the fault.
     */
    public function FaultEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = true,
                                fault:Fault = null, token:AsyncToken = null, message:IMessage = null)
    {
        super(type, bubbles, cancelable, token, message);

        _fault = fault;
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     * The Fault object that contains the details of what caused this event.
     */
    public function get fault():Fault
    {
        return _fault;
    }

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


    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /** 
     * Because this event can be redispatched we have to implement clone to
     * return the appropriate type, otherwise we will get just the standard
     * event type.
     * @private
     */
    override public function clone():Event
    {
        return new FaultEvent(type, bubbles, cancelable, fault, token, message);
    }

     /**
      * Returns a string representation of the FaultEvent.
      *
      * @return String representation of the FaultEvent.
      */  
    override public function toString():String
    {
        return formatToString("FaultEvent", "fault", "messageId", "type", "bubbles", "cancelable", "eventPhase");
    }

    /*
     * Have the token apply the fault.
     */
    override mx_internal function callTokenResponders():void
    {
        if (token != null)
            token.applyFault(this);
    }

    /**
     *  Given a MessageFaultEvent, this method constructs and
     *  returns a FaultEvent.
     * 
     *  @param value MessageFaultEvent reference to extract the appropriate
     *  fault information from.
     *  @param token AsyncToken [optional] associated with this fault.
     *  @return Returns a FaultEvent.
     */ 
    public static function createEventFromMessageFault(value:MessageFaultEvent, token:AsyncToken = null):FaultEvent
    {
        var fault:Fault = new Fault(value.faultCode, value.faultString, value.faultDetail);
        fault.rootCause = value.rootCause;
        return new FaultEvent(FaultEvent.FAULT, false, true, fault, token, value.message);
    }

    /**
     *  Given a Fault, this method constructs and
     *  returns a FaultEvent.
     * 
     *  @param fault Fault that contains the details of the FaultEvent.
     *  @param token AsyncToken [optional] associated with this fault.
     *  @param msg Message [optional] associated with this fault.
     *  @return Returns a FaultEvent.
     */ 
    public static function createEvent(fault:Fault, token:AsyncToken = null, msg:IMessage = null):FaultEvent
    {
        return new FaultEvent(FaultEvent.FAULT, false, true, fault, token, msg);
    }


    //--------------------------------------------------------------------------
    //
    //  Private Variables
    //
    //--------------------------------------------------------------------------

    private var _fault:Fault;
    private var _headers:Object;
}

}
