////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
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
 *  Represents events that are specific to the NumericStepper control.
 *
 *  @see mx.controls.NumericStepper
 */
public class NumericStepperEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The <code>NumericStepperEvent.CHANGE</code> constant defines the value of the
	 *  <code>type</code> property of the event object for a <code>change</code> event.
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
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>value</code></td><td>The value of the NumericStepper control 
     *       when the event was dispatched.</td></tr>
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
	 *  @param type The event type; indicates the action that caused the event.
	 *
	 *  @param bubbles Specifies whether the event can bubble up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
	 *
	 *  @param value The value of the NumericStepper control when the event was dispatched.
         *
         *  @param triggerEvent If the value changed in response to a user action, contains a value
         *  indicating the type of input action, either <code>InteractionInputType.MOUSE</code>
         *  or <code>InteractionInputType.KEYBOARD</code>.
	 */
	public function NumericStepperEvent(type:String, bubbles:Boolean = false,
                                        cancelable:Boolean = false,
                                        value:Number = NaN,
                                        triggerEvent:Event = null)
	{
		super(type, bubbles, cancelable);

        this.value = value;
        this.triggerEvent = triggerEvent;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  value
	//----------------------------------

	/**
	 *	The value of the NumericStepper control when the event was dispatched.
	 */	
	public var value:Number;

	//----------------------------------
	//  triggerEvent
	//----------------------------------

	/**
	 *  If the value is changed in response to a user action, 
	 *  this property contains a value indicating the type of input action. 
	 *  The value is either <code>InteractionInputType.MOUSE</code> 
	 *  or <code>InteractionInputType.KEYBOARD</code>.
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
		return new NumericStepperEvent(type, bubbles, cancelable, value);
	}
}

}
