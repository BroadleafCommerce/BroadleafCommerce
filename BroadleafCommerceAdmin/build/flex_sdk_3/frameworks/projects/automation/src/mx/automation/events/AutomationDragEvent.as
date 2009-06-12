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
import flash.events.MouseEvent;
import mx.automation.IAutomationObject;
import mx.core.IUIComponent;

/**
 *  The AutomationDragEvent class represents event objects that are 
 *  dispatched as part of a drag-and-drop operation.
 *
 *  @see mx.managers.DragManager
 *  @see mx.core.UIComponent
 */
public class AutomationDragEvent extends MouseEvent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Defines the value of the 
     *  <code>type</code> property of the event object for a <code>dragComplete</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td>
     *         <td>Indicates whether the Alt key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>action</code></td><td>The action that caused the event: 
     *       <code>DragManager.COPY</code>, <code>DragManager.LINK</code>, 
     *       <code>DragManager.MOVE</code>, or <code>DragManager.NONE</code>.</td></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>true</code></td></tr>
     *     <tr><td><code>ctrlKey</code></td>
     *         <td>Indicates whether the Control key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>draggedItem</code></td><td>The item being dragged.</td></tr>
     *     <tr><td><code>dropParent</code></td><td>The object that
     *       parents the item that was dropped.</td></tr>
     *     <tr><td><code>shiftKey</code></td>
     *         <td>Indicates whether the Shift key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object that listens for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType dragComplete
     */
    public static const DRAG_COMPLETE:String = "dragComplete";

    /**
     *  Defines the value of the 
     *  <code>type</code> property of the event object for a <code>dragDrop</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td>
     *         <td>Indicates whether the Alt key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>action</code></td><td>The action that caused the event: 
     *       <code>DragManager.COPY</code>, <code>DragManager.LINK</code>, 
     *       <code>DragManager.MOVE</code>, or <code>DragManager.NONE</code>.</td></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>true</code></td></tr>
     *     <tr><td><code>ctrlKey</code></td>
     *         <td>Indicates whether the Control key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>draggedItem</code></td><td>The item being dragged.</td></tr>
     *     <tr><td><code>dropParent</code></td><td>The object that
     *       parents the item that was dropped.</td></tr>
     *     <tr><td><code>shiftKey</code></td>
     *         <td>Indicates whether the Shift key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object that listens for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object that is listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType dragDrop
     */
    public static const DRAG_DROP:String = "dragDrop";

    /**
     *  Defines the value of the 
     *  <code>type</code> property of the event object for a <code>dragStart</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td>
     *         <td>Indicates whether the Alt key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>action</code></td><td>The action that caused the event: 
     *       <code>DragManager.COPY</code>, <code>DragManager.LINK</code>, 
     *       <code>DragManager.MOVE</code>, or <code>DragManager.NONE</code>.</td></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>true</code></td></tr>
     *     <tr><td><code>ctrlKey</code></td>
     *         <td>Indicates whether the Control key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       <code>myButton</code> is the value of <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>draggedItem</code></td><td>The item being dragged.</td></tr>
     *     <tr><td><code>dropParent</code></td><td>The object that
     *       parents the item that was dropped.</td></tr>
     *     <tr><td><code>shiftKey</code></td>
     *         <td>Indicates whether the Shift key is down
     *            (<code>true</code>) or not (<code>false</code>).</td></tr>
     *     <tr><td><code>target</code></td><td>The object that dispatched the event; 
     *       it is not always the object that listens for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       object that is listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType dragStart
     */
    public static const DRAG_START:String = "dragStart";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  Normally called by the Flex control and not used in application code.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
     *
     *  @param action The specified drop action, such as <code>DragManager.MOVE</code>.
     *
     *  @param ctrlKey Indicates whether the Control key was pressed.
     *
     *  @param altKey Indicates whether the Alt key was pressed.
     *
     *  @param shiftKey Indicates whether the Shift key was pressed.
     */
    public function AutomationDragEvent(type:String, bubbles:Boolean = false,
                              cancelable:Boolean = true,
                              action:String = null,
                              ctrlKey:Boolean = false,
                              altKey:Boolean = false,
                              shiftKey:Boolean = false)
    {
        super(type, bubbles, cancelable);

        this.action = action;
        this.ctrlKey = ctrlKey;
        this.altKey = altKey;
        this.shiftKey = shiftKey;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  action
    //----------------------------------

    /**
     *  The requested action.
     *  One of <code>DragManager.COPY</code>, <code>DragManager.LINK</code>,
     *  <code>DragManager.MOVE</code>, or <code>DragManager.NONE</code>.
     *
     *  @see mx.managers.DragManager
     */
    public var action:String;
    
    //----------------------------------
    //  draggedItem
    //----------------------------------

    /**
     *  Contains the child IAutomationObject object that is being dragged.
     */
    public var draggedItem:IAutomationObject;

    
    /**
     *  The IAutomationObject object that parents the dropped item.
     */
    public var dropParent:IAutomationObject;

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
        var cloneEvent:AutomationDragEvent = new AutomationDragEvent(type, bubbles, cancelable, 
                                                 action, ctrlKey,
                                                 altKey, shiftKey);

        // Set relevant MouseEvent properties.
        cloneEvent.relatedObject = this.relatedObject;
        cloneEvent.localX = this.localX;
        cloneEvent.localY = this.localY;

        return cloneEvent;
    }
}

}
