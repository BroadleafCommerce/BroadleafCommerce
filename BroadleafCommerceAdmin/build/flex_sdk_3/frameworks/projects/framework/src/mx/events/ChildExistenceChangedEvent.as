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

import flash.display.DisplayObject;
import flash.events.Event;

/**
 *  Represents events that are dispatched when a the child of a control
 *  is created or destroyed. 
 */
public class ChildExistenceChangedEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>ChildExistenceChangedEvent.CHILD_ADD</code> constant 
	 *  defines the value of the <code>type</code> property of the event 
	 *  object for a <code>childAdd</code> event.
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
	 *     <tr><td><code>relatedObject</code></td><td>Contains a reference
     *         to the child object that was created.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType childAdd
	 */
	public static const CHILD_ADD:String = "childAdd";

	/**
	 *  The <code>ChildExistenceChangedEvent.CHILD_REMOVE</code> constant 
	 *  defines the value of the <code>type</code> property of the event 
	 *  object for a <code>childRemove</code> event.
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
	 *     <tr><td><code>relatedObject</code></td><td>Contains a reference
     *        to the child object that is about to be removed.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType childRemove
	 */
	public static const CHILD_REMOVE:String = "childRemove";
	
	/**
	 *  The <code>ChildExistenceChangedEvent.OVERLAY_CREATED</code> constant 
	 *  defines the value of the <code>type</code> property of the event object 
	 *  for a <code>overlayCreated</code> event.
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
	 *     <tr><td><code>relatedObject</code></td><td>Contains a reference
     *        to the child object whose overlay was created.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType overlayCreated
	 */
	public static const OVERLAY_CREATED:String = "overlayCreated";

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
	 *  @param bubbles Specifies whether the event can bubble up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
	 *
	 *  @param relatedObject Reference to the child object that was created or destroyed.
	 */
	public function ChildExistenceChangedEvent(
								type:String, bubbles:Boolean = false,
								cancelable:Boolean = false,
								relatedObject:DisplayObject = null)
	{
		super(type, bubbles, cancelable);

		this.relatedObject = relatedObject;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  relatedObject
	//----------------------------------

	/**
	 *  Reference to the child object that was created or destroyed.
	 */
	public var relatedObject:DisplayObject;

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
		return new ChildExistenceChangedEvent(type, bubbles, cancelable,
											  relatedObject);
	}
}

}
