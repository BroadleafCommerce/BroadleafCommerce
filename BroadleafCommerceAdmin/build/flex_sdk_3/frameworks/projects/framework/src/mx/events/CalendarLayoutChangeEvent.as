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
 *  The CalendarLayoutChangeEvent class represents the event object passed to 
 *  the event listener for the <code>change</code> event for 
 *  the DateChooser and DateField controls.
 *
 *  @see mx.controls.DateChooser
 *  @see mx.controls.DateField
 */
public class CalendarLayoutChangeEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>CalendarLayoutChangeEvent.CHANGE</code> constant 
	 *  defines the value of the <code>type</code> property of the event 
	 *  object for a <code>change</code> event.
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
     *     <tr><td><code>newDate</code></td><td>The date selected in the control.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>The event that triggered this change event;
	 *       usually a <code>change</code> event.</td></tr>
	 *  </table>
	 *
     *  @eventType change
	 */
	public static const CHANGE:String = "change";
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param type The event type; indicates the action that triggered the event.
	 *
	 *  @param bubbles Specifies whether the event can bubble
	 *  up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior
	 *  associated with the event can be prevented.
	 *
	 *  @param newDate The date selected in the control.
	 *
	 *  @param triggerEvent The event that triggered this change event;
	 *       usually a <code>change</code> event.
	 */
	public function CalendarLayoutChangeEvent(type:String,
										      bubbles:Boolean = false,
								  	 	      cancelable:Boolean = false,
                                              newDate:Date = null,
                                              triggerEvent:Event = null)
	{
		super(type, bubbles, cancelable);
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  newDate
	//----------------------------------

	/**
	 *  The selected date of the control.
	 */
	public var newDate:Date;
			
	//----------------------------------
	//  triggerEvent
	//----------------------------------

	/**
	 *  The event that triggered the change of the date;
	 *  usually a <code>change</code> event.
	 */
	public var triggerEvent:Event;
		
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
        return new CalendarLayoutChangeEvent(type, bubbles, cancelable,
											 newDate, triggerEvent);
	}
}

}
