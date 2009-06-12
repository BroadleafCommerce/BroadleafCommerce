////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

import flash.events.Event;

/**
 *  Represents event objects that are dispatched when the size of a Flex 
 *  component changes.
 *
 *  @see mx.core.UIComponent
 */
public class ResizeEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>ResizeEvent.RESIZE</code> constant defines the value of the
	 *  <code>type</code> property of the event object for a <code>resize</code> event.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>oldHeight</code></td><td>The previous height of the object, in pixels.</td></tr>
     *     <tr><td><code>oldWidth</code></td><td>The previous width of the object, in pixels.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType resize
	 */
	public static const RESIZE:String = "resize";

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
	 *  @param bubbles Specifies whether the event can bubble
	 *  up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior
	 *  associated with the event can be prevented.
	 *
	 *  @param oldWidth The previous width of the object, in pixels.
	 *
	 *  @param oldHeight The previous height of the object, in pixels.
	 */
	public function ResizeEvent(type:String, bubbles:Boolean = false,
							    cancelable:Boolean = false,
							    oldWidth:Number = NaN, oldHeight:Number = NaN)
	{
		super(type, bubbles, cancelable);

		this.oldWidth = oldWidth;
		this.oldHeight = oldHeight;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  oldHeight
	//----------------------------------

	/**
	 *  The previous <code>height</code> of the object, in pixels.
	 */
	public var oldHeight:Number;

	//----------------------------------
	//  oldWidth
	//----------------------------------

	/**
	 *  The previous <code>width</code> of the object, in pixels.
	 */
	public var oldWidth:Number;

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
		return new ResizeEvent(type, bubbles, cancelable, oldWidth, oldHeight);
	}
}

}
