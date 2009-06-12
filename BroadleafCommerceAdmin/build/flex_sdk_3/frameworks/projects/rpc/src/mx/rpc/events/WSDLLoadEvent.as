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

package mx.rpc.events
{

import flash.events.Event;
import mx.rpc.wsdl.WSDL;

[ExcludeClass]

/**
 * The WSDLLoadEvent class represents the event object for the event dispatched 
 * when a WSDL XML document has loaded successfully.
 */
public class WSDLLoadEvent extends XMLLoadEvent
{
    /**
     * Constructor.
     *
     * @param type The event type; indicates the action that caused the event.
     *
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     *
     * @param cancelable Specifies whether the behavior associated with the event can be prevented.
     *
     * @param wsdl The full WSDL document.
     *
     * @param location The path used to load the document.
     */
    public function WSDLLoadEvent(type:String, bubbles:Boolean = false, 
        cancelable:Boolean = true, wsdl:WSDL = null, location:String = null)
    {
        super(type == null ? LOAD : type,
            bubbles,
            cancelable,
            wsdl == null ? null : wsdl.xml,
            location);

        this.wsdl = wsdl;
    }

    /**
     * The full WSDL document.
     */
    public var wsdl:WSDL

    /**
     * Returns a copy of this WSDLLoadEvent object.
     */
    override public function clone():Event
    {
        return new WSDLLoadEvent(type, bubbles, cancelable, wsdl, location);
    }

    /**
     * Returns a String representation of this WSDLLoadEvent object.
     */
    override public function toString():String
    {
        return formatToString("WSDLLoadEvent", "location", "type", "bubbles",
            "cancelable", "eventPhase");
    }

    /**
     * A helper method to create a new WSDLLoadEvent.
     * @private
     */
    public static function createEvent(wsdl:WSDL, location:String = null):WSDLLoadEvent
    {
        return new WSDLLoadEvent(LOAD, false, true, wsdl, location);
    }

    /**
     * The LOAD constant defines the value of the <code>type</code> property of the event object 
     * for a <code>wsdlLoad</code> event.
     *
     * <p>The properties of the event object have the following values:</p>
     * <table class="innertable">
     * <tr><th>Property</th><th>Value</th></tr>
     * <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     * <tr><td><code>cancelable</code></td><td><code>true</code></td></tr>
     * <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *     event listener that handles the event. For example, if you use 
     *     <code>myButton.addEventListener()</code> to register an event listener, 
     *     myButton is the value of the <code>currentTarget</code>. </td></tr>
     * <tr><td><code>location</code></td><td>The path used to load the document.</td></tr>
     * <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *     it is not always the Object listening for the event. 
     *     Use the <code>currentTarget</code> property to always access the 
     *     Object listening for the event.</td></tr>
     * <tr><td><code>wsdl</code></td><td>The full WSDL document.</td></tr>
     * </table>
     *     
     * @eventType result      
     */
    public static const LOAD:String = "wsdlLoad";
}

}