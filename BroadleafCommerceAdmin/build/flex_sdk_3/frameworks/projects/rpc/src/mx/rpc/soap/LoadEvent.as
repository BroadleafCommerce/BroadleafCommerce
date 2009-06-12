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

package mx.rpc.soap
{

import flash.events.Event;
import flash.xml.XMLDocument;
import mx.rpc.events.WSDLLoadEvent;
import mx.rpc.wsdl.WSDL;

// Note that this event is retained in the mx.rpc.soap package
// for backwards compatibility.

/**
 * This event is dispatched when a WSDL XML document has loaded successfully.   
 */
public class LoadEvent extends WSDLLoadEvent
{
    /**
     * Creates a new WSDLLoadEvent.
     * @param type The event type; indicates the action that triggered the event.
     * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     * @param cancelable Specifies whether the behavior associated with the event can be prevented.
     * @param wsdl Object that contains the WSDL document.
     * @param location URL of the WSDL document.     
     */
    public function LoadEvent(type:String, bubbles:Boolean = false, 
        cancelable:Boolean = true, wsdl:WSDL = null, location:String = null)
    {
        super(type == null ? LOAD : type,
            bubbles,
            cancelable,
            wsdl,
            location);
    }

    [Deprecated(replacement="xml")]
    /**
     * This getter is retained to provide legacy access to the loaded document
     * as an instance of flash.xml.XMLDocument.
     */
    public function get document():XMLDocument
    {
        if (_document == null && xml != null)
        {
            try
            {
                _document = new XMLDocument(xml.toXMLString());
            }
            catch(e:Error)
            {
            }
        }
        return _document;
    }

    /**
     * Returns a copy of this LoadEvent.
     *
     * @return Returns a copy of this LoadEvent.
     */
    override public function clone():Event
    {
        return new LoadEvent(type, bubbles, cancelable, wsdl, location);
    }

    /**
     * Returns a String representation of this LoadEvent.
     *
     * @return Returns a String representation of this LoadEvent.
     */
    override public function toString():String
    {
        return formatToString("LoadEvent", "location", "type", "bubbles",
            "cancelable", "eventPhase");
    }

    /**
     * A helper method to create a new LoadEvent.
     * @private
     */
    public static function createEvent(wsdl:WSDL, location:String = null):LoadEvent
    {
        return new LoadEvent(LOAD, false, true, wsdl, location);
    }


  /**
    * The <code>LOAD</code> constant defines the value of the <code>type</code> property
    * of the event object for a <code>load</code> event.
    *
    * <p>The properties of the event object have the following values:</p>
    * <table class="innertable">
    *     <tr><th>Property</th><th>Value</th></tr>
    *     <tr><td><code>bubbles</code></td><td>false</td></tr>
    *     <tr><td><code>cancelable</code></td><td>true</td></tr>
    *     <tr><td><code>wsdl</code></td><td>WSDL object.</td></tr>
    *     <tr><td><code>location</code></td><td>URI of the WSDL document</td></tr>
    *  </table>
    *
    *  @eventType load 
    */    
    public static const LOAD:String = "load";

    private var _document:XMLDocument;
}

}
