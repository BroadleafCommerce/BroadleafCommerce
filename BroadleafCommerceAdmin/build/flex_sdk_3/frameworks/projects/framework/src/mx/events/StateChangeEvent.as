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
 *  The StateChangeEvent class represents an event that is dispatched when the 
 *  <code>currentState</code> property of a component changes.
 *
 *  @see mx.core.UIComponent
 *  @see mx.states.State
 */
public class StateChangeEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The StateChangeEvent.CURRENT_STATE_CHANGE constant defines the
	 *  value of the <code>type</code> property of the event that is dispatched
	 *  when the view state has changed.
	 *  The value of this constant is "currentStateChange".
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
     *     <tr><td><code>newState</code></td><td>The name of the view state
	 *       that was entered.</td></tr>
     *     <tr><td><code>oldState</code></td><td>The name of the view state
	 *       that was exited.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>StateChangeEvent.CURRENT_STATE_CHANGE</td></tr>
	 *  </table>
	 *
     *  @eventType currentStateChange
	 */
	public static const CURRENT_STATE_CHANGE:String = "currentStateChange";

	/**
	 *  The StateChangeEvent.CURRENT_STATE_CHANGING constant defines the
	 *  value of the <code>type</code> property of the event that is dispatched
	 *  when the view state is about to change.
	 *  The value of this constant is "currentStateChanging".
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
     *     <tr><td><code>newState</code></td><td>The name of the view state
	 *       that is being entered.</td></tr>
     *     <tr><td><code>oldState</code></td><td>The name of the view state
	 *       that is being exited.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>StateChangeEvent.CURRENT_STATE_CHANGING</td></tr>
	 *  </table>
	 *
     *  @eventType currentStateChanging
	 */
	public static const CURRENT_STATE_CHANGING:String = "currentStateChanging";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *  Normally called by a Flex control and not used in application code.
	 *
	 *  @param type The event type; indicates the action that caused the event.
	 *
	 *  @param bubbles Specifies whether the event can bubble
	 *  up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior
	 *  associated with the event can be prevented.
	 *
	 *  @param oldState The name of the view state the component is exiting.
	 *
	 *  @param newState The name of the view state the component is entering.
	 */
	public function StateChangeEvent(type:String, bubbles:Boolean = false,
									 cancelable:Boolean = false,
									 oldState:String = null,
									 newState:String = null)
	{
		super(type, bubbles, cancelable);

		this.oldState = oldState;
		this.newState = newState;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  newState
	//----------------------------------

	/**
	 *  The name of the view state that the component is entering.
	 */
	public var newState:String;

	//----------------------------------
	//  oldState
	//----------------------------------

	/**
	 *  The name of the view state that the component is exiting.
	 */
	public var oldState:String;

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
		return new StateChangeEvent(type, bubbles, cancelable,
									oldState, newState);
	}
}

}
