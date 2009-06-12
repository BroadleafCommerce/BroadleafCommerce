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
 *  The ValidationResultEvent class represents the event object 
 *  passed to the listener for the <code>valid</code> validator event
 *  or the <code>invalid</code> validator event. 
 *
 *  @see mx.validators.Validator
 *  @see mx.validators.ValidationResult
 *  @see mx.validators.RegExpValidationResult
 */
public class ValidationResultEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  The <code>ValidationResultEvent.INVALID</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for an <code>invalid</code> event.
	 *  The value of this constant is "invalid".
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
     *     <tr><td><code>field</code></td><td>The name of the field that failed validation.</td></tr>
     *     <tr><td><code>message</code></td><td>A single string that contains 
     *       every error message from all of the ValidationResult objects in the results Array.</td></tr>
     *     <tr><td><code>results</code></td><td>An array of ValidationResult objects, 
     *       one per validated field.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType invalid 
	 */
	public static const INVALID:String = "invalid";

	/**
	 *  The <code>ValidationResultEvent.VALID</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>valid</code>event.
	 *  The value of this constant is "valid".
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
     *     <tr><td><code>field</code></td><td>An empty String.</td></tr>
     *     <tr><td><code>message</code></td><td>An empty String.</td></tr>
     *     <tr><td><code>results</code></td><td>An empty Array.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType valid 
	 */
	public static const VALID:String = "valid";

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
	 *  @param bubbles Specifies whether the event can bubble up the 
	 *  display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
	 *
	 *  @param field The name of the field that failed validation and triggered the event.
	 *
	 *  @param results An array of ValidationResult objects, one per validated field. 
	 */
    public function ValidationResultEvent(type:String, bubbles:Boolean = false,
										  cancelable:Boolean = false,
										  field:String = null,
										  results:Array = null)
    {
        super(type, bubbles, cancelable);

        this.field = field;
        this.results = results;
    }

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
    //  field
    //----------------------------------

	/**
	 *  The name of the field that failed validation and triggered the event.
	 */
    public var field:String;
	
	//----------------------------------
    //  message
    //----------------------------------

	/**
	 *  A single string that contains every error message from all
	 *  of the ValidationResult objects in the results Array.
	 */
    public function get message():String
    {
        var msg:String = "";;
        
		var n:int = results.length;
		for (var i:int = 0; i < n; ++i)
        {
			if (results[i].isError)
			{
	            msg += msg == "" ? "" : "\n";
				msg += results[i].errorMessage;
			}
        }
        
		return msg;
    }

	//----------------------------------
    //  results
    //----------------------------------

	/**
	 *  An array of ValidationResult objects, one per validated field. 
	 *
	 *  @see mx.validators.ValidationResult
	 */
    public var results:Array;
	
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
		return new ValidationResultEvent(type, bubbles, cancelable,
										 field, results);
	}
}

}
