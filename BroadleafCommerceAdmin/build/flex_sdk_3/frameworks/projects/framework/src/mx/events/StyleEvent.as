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

package mx.events
{

import flash.events.Event;
import flash.events.ProgressEvent;

/**
 *  The StyleEvent class represents an event object used by the StyleManager
 *  class when a style SWF is being downloaded.
 *
 *  @see mx.styles.StyleManager
 */
public class StyleEvent extends ProgressEvent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Dispatched when the style SWF has finished downloading.     
     *  The <code>StyleEvent.COMPLETE</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>styleComplete</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td><td>Empty</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType styleComplete
     */
    public static const COMPLETE:String = "complete";
    
    /**
     *  Dispatched when there is an error downloading the style SWF.
     *  The <code>StyleEvent.ERROR</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>styleError</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>Empty</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>Empty</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td>An error message.<td></td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType styleError
     */
    public static const ERROR:String = "error";

    /**
     *  Dispatched when the style SWF is downloading.
     *  The <code>StyleEvent.PROGRESS</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>styleProgress</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>The number of bytes loaded.</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>The total number of bytes to load.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td>Empty<td></td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType styleProgress
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
     *  @param type The type of the event. Possible values are:
     *  <ul>
     *     <li>"progress" (<code>StyleEvent.PROGRESS</code>);</li>
     *     <li>"complete" (<code>StyleEvent.COMPLETE</code>);</li>
     *     <li>"error" (<code>StyleEvent.ERROR</code>);</li>
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
	 *  when type is StyleEvent.ERROR.
     *
     *  @tiptext Constructor for <code>StyleEvent</code> objects.
     */    
    public function StyleEvent(type:String, bubbles:Boolean = false,
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
        return new StyleEvent(type, bubbles, cancelable,
                              bytesLoaded, bytesTotal, errorText);
    }
}

}
