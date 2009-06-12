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
 *  Represents events that are specific to the ColorPicker control,
 *  such as when the user rolls the mouse over or out of a swatch in
 *  the swatch panel.
 */
public class ColorPickerEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
	 *  The <code>ColorPickerEvent.CHANGE</code> constant defines the value of the
	 *  <code>type</code> property of the event that is dispatched when the user 
	 *  selects a color from the ColorPicker control.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>color</code></td><td>The RGB color that was selected.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>index</code></td>
	 *         <td>The zero-based index in the Color's data provider that corresponds 
	 *             to the color that was selected.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType change
	 */
    public static const CHANGE:String = "change";

    /**
	 *  The <code>ColorPickerEvent.ENTER</code> constant defines the value of the
	 *  <code>type</code> property of the event that is dispatched when the user 
	 *  presses the Enter key after typing in the color selector box.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>color</code></td><td>The RGB color that was entered.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>index</code></td>
	 *         <td>Always -1.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType enter
     */
    public static const ENTER:String = "enter";

    /**
 	 *  The <code>ColorPickerEvent.ITEM_ROLL_OUT</code> constant defines the value of the
	 *  <code>type</code> property of the event that is dispatched when the user 
	 *  rolls the mouse out of a swatch in the swatch panel.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>color</code></td><td>The RGB color of the color 
	 *                   that was rolled over.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>index</code></td>
	 *         <td>The zero-based index in the Color's data provider that corresponds 
	 *             to the color that was rolled over.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType itemRollOut
     */
    public static const ITEM_ROLL_OUT:String = "itemRollOut";

    /**
 	 *  The <code>ColorPickerEvent.ITEM_ROLL_OVER</code> constant defines the value of the
	 *  <code>type</code> property of the event that is dispatched when the user 
	 *  rolls the mouse over of a swatch in the swatch panel.
	 *
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>color</code></td><td>The RGB color of the color 
	 *                   that the user rolled out of.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>index</code></td>
	 *         <td>The zero-based index in the Color's data provider that corresponds 
	 *             to the color that the user rolled out of.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
     *  @eventType itemRollOver
     */
    public static const ITEM_ROLL_OVER:String = "itemRollOver";

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
	 *  @param index The zero-based index in the Color's data provider
	 *  that corresponds to the color that was rolled over, rolled out of,
	 *  or selected.
	 *
	 *  @param color The RGB color that was rolled over, rolled out of,
	 *  selected, or entered.
     */
    public function ColorPickerEvent(
						type:String, bubbles:Boolean = false,
                        cancelable:Boolean = false, index:int = -1,
						color:uint = 0xFFFFFFFF /* StyleManager.NOT_A_COLOR */)
    {
        super(type, bubbles, cancelable);

		this.index = index;
		this.color = color;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
	//  color
	//----------------------------------

    /**
	 *  The RGB color that was rolled over, rolled out of, selected, or
	 *  entered.
     */
    public var color:uint;

	//----------------------------------
	//  index
	//----------------------------------

    /**
	 *  The zero-based index in the Color's data provider that corresponds
	 *  to the color that was rolled over, rolled out of, or selected.
	 *  If the event type is <code>ColorPickerEvent.ENTER</code>,
	 *  will have default value -1; it is not set in this case because
	 *  the user can enter an RGB string that doesn't match any color
	 *  in the data provider.
     */
    public var index:int;

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
        return new ColorPickerEvent(type, bubbles, cancelable, index, color);
    }
}

}
