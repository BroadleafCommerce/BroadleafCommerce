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
 * The event that indicates an RPC operation, such as a WebService SOAP request,
 * returned a header in the response. A new header event is dispatched for each
 * service header.
 */
public class HeaderEvent extends AbstractEvent
{
   /**
    * The HEADER event type.
    *
    * <p>The properties of the event object have the following values:</p>
    * <table class="innertable">
    *     <tr><th>Property</th><th>Value</th></tr>
    *     <tr><td><code>bubbles</code></td><td>false</td></tr>
    *     <tr><td><code>token</code></td><td>The token that represents the call
    *     to the method. Used in the asynchronous completion token pattern.</td></tr>
    *     <tr><td><code>cancelable</code></td><td>true, but the preventDefault() method has 
    *       no effect.</td></tr>
    *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
    *       event listener that handles the event. For example, if you use 
    *       <code>myButton.addEventListener()</code> to register an event listener, 
    *       myButton is the value of the <code>currentTarget</code>. </td></tr>
    *     <tr><td><code>header</code></td><td>Header that the RPC call returns in the response. 
    *     </td></tr>   
    *     <tr><td><code>message</code></td><td>The Message associated with this event.</td></tr>
    *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
    *       it is not always the Object listening for the event. 
    *       Use the <code>currentTarget</code> property to always access the 
    *       Object listening for the event.</td></tr>
    *  </table>
    *  @eventType header 
    */
    public static const HEADER:String = "header";

    private var _header:Object;

    /**
     * Creates a new HeaderEvent.
     *
     * @param type The event type; indicates the action that caused the event.
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     * @param cancelable Specifies whether the behavior associated with the event can be prevented. The dfault is <code>true</code>, but the <code>preventDefault()</code> method has no effect.
     * @param header Object that holds the header of the call.
     * @param token AsyncToken that represents the call to the method. Used in the asynchronous completion token pattern.
     * @param message Source Message of the header.
     */
    public function HeaderEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = true,
            header:Object = null, token:AsyncToken = null, message:IMessage = null)
    {
        super(type, bubbles, cancelable, token, message);

        _header = header;
    }

    /**
     * Header that the RPC call returned in the response.
     */
    public function get header():Object
    {
        return _header;
    }

    /**
     * Utility method to create a new HeaderEvent that doesn't bubble and is cancelable.
     *
     * @param header Object that holds the header of the call.
     * @param token AsyncToken that represents the call to the method. Used in the asynchronous completion token pattern.
     * @param message Source Message header.
     *
     * @return Returns a new HeaderEvent that doesn't bubble and is cancelable.
     */
    public static function createEvent(header:Object, token:AsyncToken, message:IMessage):HeaderEvent
    {
        return new HeaderEvent(HeaderEvent.HEADER, false, true, header, token, message);
    }

    /**
      * @private
      */
    override public function clone():Event
    {
        return new HeaderEvent(type, bubbles, cancelable, header, token, message);
    }

   /**
     * Returns a string representation of the HeaderEvent.
     *
     * @return String representation of the HeaderEvent.
     */
    override public function toString():String
    {
        return formatToString("HeaderEvent", "type", "bubbles", "cancelable", "eventPhase");
    }
}

}
