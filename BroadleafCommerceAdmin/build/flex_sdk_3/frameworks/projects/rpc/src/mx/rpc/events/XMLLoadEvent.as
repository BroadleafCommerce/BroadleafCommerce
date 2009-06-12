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

[ExcludeClass]

/**
 * The XMLLoadEvent class is a base class for events that are dispatched when an RPC service
 * successfully loaded an XML document.
 */
public class XMLLoadEvent extends Event
{
    /**
     * Constructor.
     *
     * @param type The event type; indicates the action that triggered the event.
     *
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     *
     * @param cancelable Specifies whether the behavior associated with the event can be prevented.
     *
     * @param xml The XML document loaded.
     *
     * @param location The path used to load the document.
     */
    public function XMLLoadEvent(type:String, bubbles:Boolean = false, 
        cancelable:Boolean = true, xml:XML = null, location:String = null)
    {
        super(type == null ? LOAD : type,
            bubbles,
            cancelable);

        this.xml = xml;
        this.location = location;
    }

    /**
     * The raw XML document.
     */
    public var xml:XML;

    /**
     * The location from which the document was loaded.
     */
    public var location:String;

    /**
     * Returns a copy of this XMLLoadEvent object.
     */
    override public function clone():Event
    {
        return new XMLLoadEvent(type, bubbles, cancelable, xml, location);
    }

    /**
     * Returns a String representation of this XMLLoadEvent object.
     */
    override public function toString():String
    {
        return formatToString("XMLLoadEvent", "location", "type", "bubbles", 
            "cancelable", "eventPhase");
    }

    /**
     * A helper method to create a new XMLLoadEvent.
     * @private
     */
    public static function createEvent(xml:XML = null, location:String = null):XMLLoadEvent
    {
        return new XMLLoadEvent(LOAD, false, true, xml, location);
    }

    /**
     * The LOAD constant defines the value of the <code>type</code> property of the event object 
     * for a <code>xmlLoad</code> event.
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
     * <tr><td><code>location</code></td><td>The location from which the document was loaded.</td></tr>
     * <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *     it is not always the Object listening for the event. 
     *     Use the <code>currentTarget</code> property to always access the 
     *     Object listening for the event.</td></tr>
     * <tr><td><code>xml</code></td><td>The raw XML document.</td></tr>
     * </table>
     *     
     * @eventType result      
     */
    public static const LOAD:String = "xmlLoad";
}

}