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

import flash.geom.Rectangle;
import flash.events.Event;
import flash.events.NativeWindowBoundsEvent;

/**
 *  The FlexNativeWindowBoundsEvent is dispatched when the size or location changes for
 *  the NativeWindow that underlies a Window or WindowedApplication component.
 *
 *  @see mx.core.Window
 *  @see mx.core.WindowedApplication
 * 
 *  @playerversion AIR 1.1
 */
public class FlexNativeWindowBoundsEvent extends NativeWindowBoundsEvent
{
	
	/**
     *  The FlexNativeWindowBoundsEvent.WINDOW_RESIZE constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>windowResize</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
	 *     <tr><td><code>afterBounds</code></td><td>The bounds of the window after the bounds changed.</td></tr>
	 *     <tr><td><code>beforeBounds</code></td><td>The bounds of the window before the bounds changed.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FlexNativeWindowBoundsEvent.WINDOW_RESIZE</td></tr>
     *  </table>
	 *
	 *  @eventType windowResize
	 */
	public static const WINDOW_RESIZE:String = "windowResize";
	
	/**
     *  The FlexNativeWindowBoundsEvent.WINDOW_MOVE constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>windowMove</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
	 *     <tr><td><code>afterBounds</code></td><td>The bounds of the window after the bounds changed.</td></tr>
	 *     <tr><td><code>beforeBounds</code></td><td>The bounds of the window before the bounds changed.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FlexNativeWindowBoundsEvent.WINDOW_MOVE</td></tr>
     *  </table>
	 *
	 *  @eventType windowMove
	 */
	public static const WINDOW_MOVE:String = "windowMove";
	
	//--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble up
     *  the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     *
     *  @param beforeBounds The bounds of the window before the resize.
     *
     *  @param afterBounds The bounds of the window before the resize.
     */
	public function FlexNativeWindowBoundsEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false,
					beforeBounds:Rectangle = null, afterBounds:Rectangle = null)
	{
		super(type, bubbles, cancelable, beforeBounds, afterBounds);
	}
		
	
	
	//--------------------------------------------------------------------------
    //
    //  Overridden methods: Event
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function clone():Event
    {
        return new FlexNativeWindowBoundsEvent(type, bubbles, cancelable, beforeBounds, afterBounds);
    }
}
}