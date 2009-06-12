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
 *  The AutomationReplayEvent class represents event objects that are dispatched 
 *  by the AutomationManager, and used by the functional testing classes
 *  and any other classes that must replay user interactions.
 */
public class AutomationReplayEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The <code>AutomationReplayEvent.REPLAY</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>replay</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>automationObject</code></td><td>Delegate of the UIComponent 
     *        that dispatched the interaction earlier.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>replayableEvent</code></td><td>Event that needs to be replayed.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType replay
     */
    public static const REPLAY:String = "replay";
    
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
     *  @param automationObject Delegate of the UIComponent that dispatched the interaction earlier.
     *
     *  @param replayableEvent Event that needs to be replayed.
     */
    public function AutomationReplayEvent(type:String = "replay", 
                                          bubbles:Boolean = false,
                                          cancelable:Boolean = false,
                                          automationObject:IAutomationObject = null, 
                                          replayableEvent:Event = null)
    {
        super(type, bubbles, cancelable);

        this.automationObject = automationObject;
        this.replayableEvent = replayableEvent;
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
     *  Delegate of the UIComponent object on which this event will be replayed
     *  since the target on an event that was not really dispatched
     *  is not available.
     */
    public var automationObject:IAutomationObject;
    
    //----------------------------------
    //  replayableEvent
    //----------------------------------
        
    /**
     *  Event to the replayed.
     */
    public var replayableEvent:Event;

    //----------------------------------
    //  succeeded
    //----------------------------------
        
    /**
     *  Contains <code>true</code> if the replay was successful, and <code>false</code> if not.
     */
    public var succeeded:Boolean;
    
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
        return new AutomationReplayEvent(type, bubbles, cancelable,
                                         automationObject,
                                         replayableEvent);
    }
}

}
