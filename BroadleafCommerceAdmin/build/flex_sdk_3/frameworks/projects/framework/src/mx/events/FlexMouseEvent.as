////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.MouseEvent;

/**
 *  The FlexMouseEvent class represents the event object passed to
 *  the event listener for Flex-specific mouse activity.
 */
public class FlexMouseEvent extends MouseEvent
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>FlexMouseEvent.MOUSE_DOWN_OUTSIDE</code> constant defines the value of the
	 *  <code>type</code> property of the event object for a <code>mouseDownOutside</code>
	 *  event.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td>
	 *         <td>Indicates whether the Alt key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>buttonDown</code></td>
	 *         <td>Indicates whether the main mouse button is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td>
	 *         <td>Indicates whether the Control key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. 
 	 *       For PopUpManager events, the object is the pop-up window.</td></tr>
     *     <tr><td><code>delta</code></td>
	 *         <td>Indicates how many lines should be scrolled for each notch the user 
	 *             scrolls the mouse wheel. 
	 *             For PopUpManager events this value is 0.</td></tr>
     *     <tr><td><code>localX</code></td>
	 *         <td>The horizontal position at which the event occurred. 
	 *             For PopUpManager events, the value is relative to the pop-up control.</td></tr>
     *     <tr><td><code>localY</code></td>
	 *         <td>The vertical position at which the event occurred. 
	 *             For PopUpManager events, the value is relative to the pop-up control.</td></tr>
     *     <tr><td><code>relatedObject</code></td>
	 *         <td>A reference to a display list object that is related to the event.
	 *             For PopUpManager events, the object is the container over which
	 *             the mouse pointer is located.</td></tr>
     *     <tr><td><code>shiftKey</code></td>
	 *         <td>Indicates whether the Shift key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.
	 *       For PopUpManager events, the object is the pop-up window.</td></tr>
	 *  </table>
	 *
     *  @eventType mouseDownOutside
	 */
	public static const MOUSE_DOWN_OUTSIDE:String = "mouseDownOutside";

	/**
	 *  The <code>FlexMouseEvent.MOUSE_WHEEL_OUTSIDE</code> constant defines the value of the
	 *  <code>type</code> property of the event object for a <code>mouseWheelOutside</code>
	 *  event.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td>
	 *         <td>Indicates whether the Alt key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>buttonDown</code></td>
	 *         <td>Indicates whether the main mouse button is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td>
	 *         <td>Indicates whether the Control key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>currentTarget</code></td>
	 *         <td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. 
 	 *       For PopUpManager events, the object is the pop-up window.</td></tr>
     *     <tr><td><code>delta</code></td>
	 *         <td>Indicates how many lines should be scrolled for each notch the user 
	 *             scrolls the mouse wheel. 
	 *             For PopUpManager events this value is 0.</td></tr>
     *     <tr><td><code>localX</code></td>
	 *         <td>The horizontal position at which the event occurred. 
	 *             For PopUpManager events, the value is relative to the pop-up control.</td></tr>
     *     <tr><td><code>localY</code></td>
	 *         <td>The vertical position at which the event occurred. 
	 *             For PopUpManager events, the value is relative to the pop-up control.</td></tr>
     *     <tr><td><code>relatedObject</code></td>
	 *         <td>A reference to a display list object that is related to the event.
	 *             For PopUpManager events, the object is the container over which
	 *             the mouse pointer is located.</td></tr>
     *     <tr><td><code>shiftKey</code></td>
	 *         <td>Indicates whether the Shift key is down
	 * 	          (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.
	 *       For PopUpManager events, the object is the pop-up window.</td></tr>
	 *  </table>
	 *
     *  @eventType mouseWheelOutside
	 */
	public static const MOUSE_WHEEL_OUTSIDE:String = "mouseWheelOutside";

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
     *  @param localX The horizontal position at which the event occurred.
	 * 
     *  @param localY The vertical position at which the event occurred.
	 * 
     *  @param relatedObject The display list object that is related to the event.
	 * 
	 *  @param ctrlKey Whether the Control key is down.
	 * 
	 *  @param altKey Whether the Alt key is down.
	 * 
	 *  @param shiftKey Whether the Shift key is down.
	 * 
	 *  @param buttonDown Whether the Control key is down.
	 * 
	 *  @param delta How many lines should be scrolled for each notch the 
	 *  user scrolls the mouse wheel.
	 */
	public function FlexMouseEvent(type:String, bubbles:Boolean = false,
								   cancelable:Boolean = false,
								   localX:Number = 0,  localY:Number = 0, 
								   relatedObject:InteractiveObject = null, 
								   ctrlKey:Boolean = false, 
								   altKey:Boolean = false, 
								   shiftKey:Boolean = false, 
								   buttonDown:Boolean = false, 
								   delta:int = 0)
	{
		super(type, bubbles, cancelable, localX, localY, relatedObject,
			  ctrlKey, altKey, shiftKey, buttonDown, delta);
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
		return new FlexMouseEvent(type, bubbles, cancelable, localX, localY,
								  relatedObject, ctrlKey, altKey, shiftKey,
								  buttonDown, delta);
	}
}

}
