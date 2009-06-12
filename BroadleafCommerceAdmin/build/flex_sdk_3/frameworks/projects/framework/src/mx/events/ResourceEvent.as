////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

import flash.events.Event;
import flash.events.ProgressEvent;

/**
 *  The ResourceEvent class represents an Event object that is dispatched
 *  when the ResourceManager loads the resource bundles in a resource module
 *  by calling the <code>loadResourceModule()</code> method.
 *
 *  @see mx.resources.ResourceManager
 */
public class ResourceEvent extends ProgressEvent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Dispatched when the resource module SWF file has finished loading.     
     *  The ResourceEvent.COMPLETE constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>complete</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td><td>Empty</td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object that listens for the event.</td></tr>
     *  </table>
     *
     *  @eventType complete
     */
    public static const COMPLETE:String = "complete";
    
    /**
     *  Dispatched when there is an error loading the resource module SWF file.
     *  The ResourceEvent.ERROR constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>error</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>Empty</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>Empty</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use the 
     *       <code>myButton.addEventListener()</code> method to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td>An error message.<td></td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object that is listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object that listens for the event.</td></tr>
     *  </table>
     *
     *  @eventType error
     */
    public static const ERROR:String = "error";

    /**
     *  Dispatched when the resource module SWF file is loading.
     *  The ResourceEvent.PROGRESS constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>progress</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>The number of bytes loaded.</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>The total number of bytes to load.</td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use the 
     *       <code>myButton.addEventListener()</code> method to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>errorText</code></td>Empty<td></td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object that listens for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object that is listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType progress
     */
    public static const PROGRESS:String = "progress"; 
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * 
     *  @param type The value of the <code>type</code> property of the event object. Possible values are:
     *  <ul>
     *     <li><code>"progress"</code> (<code>ResourceEvent.PROGRESS</code>)</li>
     *     <li><code>"complete"</code> (<code>ResourceEvent.COMPLETE</code>)</li>
     *     <li><code>"error"</code> (<code>ResourceEvent.ERROR</code>)</li>
     *  </ul>
     *
     *  @param bubbles Determines whether the Event object
     *  participates in the bubbling stage of the event flow.
     *
     *  @param cancelable Determines whether the Event object can be cancelled.
     *
     *  @param bytesLoaded The number of bytes loaded
     *  at the time the listener processes the event.
     *
     *  @param bytesTotal The total number of bytes
     *  that will ultimately be loaded if the loading process succeeds.
     *
     *  @param errorText The error message of the error
     *  when <code>type</code> is <code>ResourceEvent.ERROR</code>.
     */    
    public function ResourceEvent(type:String, bubbles:Boolean = false,
                                  cancelable:Boolean = false,
                                  bytesLoaded:uint = 0, bytesTotal:uint = 0,
                                  errorText:String = null)
    {
        super(type, bubbles, cancelable, bytesLoaded, bytesTotal);
        
        this.errorText = errorText;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  errorText
    //----------------------------------

    /**
     *  The error message if the <code>type</code> is <code>ERROR</code>;
     *  otherwise, it is <code>null</code>.
     */
    public var errorText:String;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties: Event
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function clone():Event
    {
        return new ResourceEvent(type, bubbles, cancelable,
                                 bytesLoaded, bytesTotal, errorText);
    }
}

}
