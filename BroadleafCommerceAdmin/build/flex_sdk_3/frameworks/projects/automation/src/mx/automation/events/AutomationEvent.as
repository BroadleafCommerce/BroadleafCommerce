////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.automation.events
{

import flash.events.Event;

/**
 *  The AutomationEvent class represents event objects that are dispatched 
 *  by the AutomationManager. Used by the functional testing classes
 *  and any other classes that must record user interactions.
 */
public class AutomationEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------
    
    /**
     *  The <code>AutomationEvent.BEGIN_RECORD</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>beginRecord</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>true</td></tr>
     *     <tr><td><code>cacheable</code></td><td><code>true</code> if the event 
     *       should be saved in the event cache, and <code>false</code> if not.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType record
     */
    public static const BEGIN_RECORD:String = "beginRecord";

    /**
     *  The <code>AutomationEvent.BEGIN_RECORD</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>endRecord</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>true</td></tr>
     *     <tr><td><code>cacheable</code></td><td><code>true</code> if the event 
     *       should be saved in the event cache, and <code>false</code> if not.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType record
     */
    public static const END_RECORD:String = "endRecord";
    
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
     *  @param bubbles Whether the event can bubble up the display list hierarchy.
     *
     *  @param cancelable Whether the behavior associated with the event can be prevented.
     * 
     */
    public function AutomationEvent(type:String = "beginRecord", 
                                          bubbles:Boolean = true,
                                          cancelable:Boolean = true)
    {
        super(type, bubbles, cancelable);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

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
        return new AutomationEvent(type, bubbles, cancelable);
    }
}

}
