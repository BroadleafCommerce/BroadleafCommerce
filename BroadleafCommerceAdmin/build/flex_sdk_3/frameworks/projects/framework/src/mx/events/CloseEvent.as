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
 *  The CloseEvent class represents event objects specific to popup windows, 
 *  such as the Alert control.
 *
 *  @see mx.controls.Alert
 */
public class CloseEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The <code>CloseEvent.CLOSE</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>close</code> event.
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
     *     <tr><td><code>detail</code></td><td>For controls with multiple buttons, 
     *       <code>detail</code> identifies the button in the popped up control 
     *       that was clicked.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType close
     */
    public static const CLOSE:String = "close";

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
     *  @param detail Value of the detail property; identifies the button in the popped up
     *  control that was clicked.
     */
    public function CloseEvent(type:String, bubbles:Boolean = false,
                               cancelable:Boolean = false, detail:int = -1)
    {
        super(type, bubbles, cancelable);

        this.detail = detail;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  detail
    //----------------------------------

    /**
     *  Identifies the button in the popped up control that was clicked. This 
     *  property is for controls with multiple buttons.
     *  The Alert control sets this property to one of the following constants:
     *  <ul>
     *    <li><code>Alert.YES</code></li>
     *    <li><code>Alert.NO</code></li>
     *    <li><code>Alert.OK</code></li>
     *    <li><code>Alert.CANCEL</code></li>
     *  </ul>
     */
    public var detail:int;

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
        return new CloseEvent(type, bubbles, cancelable, detail);
    }
}

}
