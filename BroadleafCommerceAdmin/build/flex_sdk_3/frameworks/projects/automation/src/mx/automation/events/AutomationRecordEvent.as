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
import mx.automation.IAutomationObject;

/**
 *  The AutomationRecordEvent class represents event objects that are dispatched 
 *  by the AutomationManager. Used by the functional testing classes
 *  and any other classes that must record user interactions.
 */
public class AutomationRecordEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------
    
    /**
     *  The <code>AutomationRecordEvent.RECORD</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>record</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>args</code></td><td>Array of arguments to the method.</td></tr>
     *     <tr><td><code>automationObject</code></td><td>Delegate of the UIComponent 
     *        that is dispatching the interaction.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>true</td></tr>
     *     <tr><td><code>cacheable</code></td><td><code>true</code> if the event 
     *       should be saved in the event cache, and <code>false</code> if not.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>methodName</code></td><td>A displayable Name of the operation </td></tr>
     *     <tr><td><code>replayableEvent</code></td><td>Underlying event that 
     *         represents the interaction.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType record
     */
    public static const RECORD:String = "record";
    
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
     *  @param automationObject Delegate of the UIComponent that is dispatching the interaction.
     *
     *  @param replayableEvent Underlying event that represents the interaction.
     * 
     *  @param args Array of arguments to the method.
     * 
     *  @param methodName Displayable name of the operation.
     * 
     *  @param cacheable <code>true</code> if the event should be saved in the event cache, 
     *  and <code>false</code> if not.
     */
    public function AutomationRecordEvent(type:String = "record", 
                                          bubbles:Boolean = true,
                                          cancelable:Boolean = true,
                                          automationObject:IAutomationObject = null, 
                                          replayableEvent:Event = null,
                                          args:Array = null,
                                          name:String = null,
                                          cacheable:Boolean = false)
    {
        super(type, bubbles, cancelable);

        this.automationObject = automationObject;
        this.replayableEvent = replayableEvent;
        this.args = args;
        this.name = name;
        this.cacheable = cacheable;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  automationObject
    //----------------------------------
    
    /**
     *  The delegate of the UIComponent object that is recording this event.
     */
    public var automationObject:IAutomationObject;
    
    //----------------------------------
    //  replayableEvent
    //----------------------------------
        
    /**
     *  The underlying interaction.
     */
    public var replayableEvent:Event;
    
    /**
     *  A serialized representation of the event as an Array
     *  of it's property values.
     */
    public var args:Array;


    /**
     *  The automation event name.
     */
    public var name:String;

    /**
     *  Contains <code>true</code> if this is a cacheable event, and <code>false</code> if not.
     */
    public var cacheable:Boolean;

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
        return new AutomationRecordEvent(type, bubbles, cancelable,
                                         automationObject,
                                         replayableEvent,
                                         args,
                                         name,
                                         cacheable);
    }
}

}
