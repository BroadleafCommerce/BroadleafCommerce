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
 *  Represents event objects that are dispatched when a Flex component moves.
 *
 *  @see mx.core.UIComponent
 */
public class MoveEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>MoveEvent.MOVE</code> constant defines the value of the
	 *  <code>type</code> property of the event object for a <code>move</code> event.
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
     *     <tr><td><code>oldX</code></td><td>The previous x coordinate of the object, in pixels.</td></tr>
     *     <tr><td><code>oldY</code></td><td>The previous y coordinate of the object, in pixels.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType move
	 */
	public static const MOVE:String = "move";

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
	 *  @param oldX The previous x coordinate of the object, in pixels.
	 *
	 *  @param oldY The previous y coordinate of the object, in pixels.
	 */
	public function MoveEvent(type:String, bubbles:Boolean = false,
							  cancelable:Boolean = false,
							  oldX:Number = NaN, oldY:Number = NaN)
	{
		super(type, bubbles, cancelable);

		this.oldX = oldX;
		this.oldY = oldY;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  oldX
	//----------------------------------

	/**
	 *  The previous <code>x</code> coordinate of the object, in pixels.
	 */
	public var oldX:Number;

	//----------------------------------
	//  oldY
	//----------------------------------

	/**
	 *  The previous <code>y</code> coordinate of the object, in pixels.
	 */
	public var oldY:Number;

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
		return new MoveEvent(type, bubbles, cancelable, oldX, oldY);
	}
}

}
