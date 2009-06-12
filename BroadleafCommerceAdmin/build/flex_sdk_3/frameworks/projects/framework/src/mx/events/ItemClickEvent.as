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

import flash.display.InteractiveObject;
import flash.events.Event;

/**
 *  Represents events that are dispatched when a navigation item on a navigator bar,
 *  such as a ButtonBar, LinkBar, or TabBar control, has been clicked.
 *
 *  @see mx.controls.NavBar
 *  @see mx.controls.Button
 *  @see mx.controls.List
 */
public class ItemClickEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>ItemClickEvent.ITEM_CLICK</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for an <code>itemClick</code> event.
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
     *     <tr><td><code>index</code></td><td>The index of the navigation item that was clicked.</td></tr>
     *     <tr><td><code>item</code></td><td>The item in the data provider of the navigation
     * 	   item that was clicked.</td></tr>
     *     <tr><td><code>label</code></td><td>The label of the navigation item that was clicked.</td></tr>
     *     <tr><td><code>relatedObject</code></td><td>The child object that generated the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType itemClick 
	 */
	public static const ITEM_CLICK:String = "itemClick";

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
	 *  @param label The label of the associated navigation item. 
	 *
	 *  @param index The index of the associated navigation item.
	 *
	 *  @param relatedObject The child object that generated the event.
	 *
	 *  @param item The item in the data provider for the associated navigation item.
	 */
	public function ItemClickEvent(type:String, bubbles:Boolean = false,
								   cancelable:Boolean = false,
								   label:String = null, index:int = -1,
								   relatedObject:InteractiveObject = null,
								   item:Object = null)
	{
		super(type, bubbles, cancelable);

		this.label = label;
		this.index = index;
		this.relatedObject = relatedObject;
		this.item = item;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  index
	//----------------------------------

	/**
	 *  The index of the associated navigation item.
	 */
	public var index:int;

	//----------------------------------
	//  item
	//----------------------------------

	/**
	 *  The item in the data provider of the associated navigation item.
	 */
	public var item:Object;

	//----------------------------------
	//  label
	//----------------------------------

	/**
	 *  The label of the associated navigation item.
	 */
	public var label:String;

	//----------------------------------
	//  relatedObject
	//----------------------------------

	/**
	 *  The child object that generated the event; for example,
	 *  the button that a user clicked in a ButtonBar control.
	 */
	public var relatedObject:InteractiveObject;

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
		return new ItemClickEvent(type, bubbles, cancelable,
								  label, index, relatedObject, item);
	}
}

}
