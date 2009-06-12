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
import mx.controls.listClasses.IListItemRenderer;

/**
 *  The ListItemSelectEvent class represents event objects that are dispatched 
 *  when an item in a list-based control such as a Menu,
 *  DataGrid, or Tree control is selected or deselected.
 */
public class ListItemSelectEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The <code>ListItemSelectEvent.DESELECT</code> constant defines the value of the
     *  <code>type</code> property of the event object for an event that is
     *  dispatched when a previously selected item is deselected.
     *
     *  <p>The properties of the event object have the following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td><td>Boolean value indicating whether
     *              the Alt key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td><td>Boolean value indicating whether
     *              the Ctrl key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer object 
     *              for the item.</td></tr>
     *     <tr><td><code>shiftKey</code></td><td>Boolean value indicating whether
     *              the Shift key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>The event, such as a 
     *             mouse or keyboard event, that triggered the action.</td></tr>
     *     <tr><td><code>type</code></td><td>ListItemSelectEvent.DESELECT</td></tr>
     *  </table>
     *
     *  @eventType deselect
     */
    public static const DESELECT:String = "deselect";
    
    /**
     *  The <code>ListItemSelectEvent.MULTI_SELECT</code> constant defines the value of the
     *  <code>type</code> property of the event object for an event that is
     *  dispatched when an  item is selected as part of an
     *  action that selects multiple items.
     *
     *  <p>The properties of the event object have the following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td><td>Boolean value indicating whether
     *              the Alt key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td><td>Boolean value indicating whether
     *              the Ctrl key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer object 
     *              for the item.</td></tr>
     *     <tr><td><code>shiftKey</code></td><td>Boolean value indicating whether
     *              the Shift key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>The event, such as a 
     *             mouse or keyboard event, that triggered the action.</td></tr>
     *     <tr><td><code>type</code></td><td>ListItemSelectEvent.MULTI_SELECT</td></tr>
     *  </table>
     *
     *  @eventType multiselect
     */
    public static const MULTI_SELECT:String = "multiSelect";
    
    /**
     *  The <code>ListItemSelectEvent.SELECT</code> constant defines the value of the
     *  <code>type</code> property of the event object for an event that is
     *  dispatched when a single item is selected.
     *
     *  <p>The properties of the event object have the following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td><td>Boolean value indicating whether
     *              the Alt key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td><td>Boolean value indicating whether
     *              the Ctrl key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer object 
     *              for the item.</td></tr>
     *     <tr><td><code>shiftKey</code></td><td>Boolean value indicating whether
     *              the Shift key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>The event, such as a 
     *             mouse or keyboard event, that triggered the action.</td></tr>
     *     <tr><td><code>type</code></td><td>ListItemSelectEvent.SELECT</td></tr>
     *  </table>
     *
     *  @eventType select
     */
    public static const SELECT:String = "select";
    
    /**
     *  The <code>ListItemSelectEvent.SELECT</code> constant defines the value of the
     *  <code>type</code> property of the event object for an event that is
     *  dispatched when a single item is selected.
     *
     *  <p>The properties of the event object have the following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>altKey</code></td><td>Boolean value indicating whether
     *              the Alt key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>ctrlKey</code></td><td>Boolean value indicating whether
     *              the Ctrl key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemIndex</code></td><td>The data provider index of 
     *              the item to be selected.</td></tr>
     *     <tr><td><code>shiftKey</code></td><td>Boolean value indicating whether
     *              the Shift key was pressed at the time of the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>The event, such as a 
     *             mouse or keyboard event, that triggered the action.</td></tr>
     *     <tr><td><code>type</code></td><td>ListItemSelectEvent.SELECT</td></tr>
     *  </table>
     *
     *  @eventType select
     */
    public static const SELECT_INDEX:String = "selectIndex";
    
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
     *  @param bubbles Specifies whether the event can bubble
     *  up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     * 
     *  @param itemRenderer The item renderer object for the item.
     * 
     *  @param triggerEvent The event, such as a mouse or keyboard event, that
     *              triggered the selection action.
     *
     *  @param altKey Whether the Alt key was pressed at the time of the event.
     *
     *  @param ctrlKey Whether the Ctrl key was pressed at the time of the event.
     *
     *  @param shiftKey Whether the Shift key was pressed at the time of the event.
     * 
     */
    public function ListItemSelectEvent(type:String, bubbles:Boolean = false,
                                        cancelable:Boolean = false,
                                        itemRenderer:IListItemRenderer = null,
                                        triggerEvent:Event = null,
                                        ctrlKey:Boolean = false,
                                        altKey:Boolean = false,
                                        shiftKey:Boolean = false)
    {
        super(type, bubbles, cancelable);

        this.itemRenderer = itemRenderer;
        this.triggerEvent = triggerEvent;
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
    //  altKey
    //----------------------------------

    /**
     *  Indicates whether the Alt key was pressed at the time of the event, <code>true</code>, 
     *  or not, <code>false</code>.
     * 
     *  @default false
     */
    public var altKey:Boolean;
    
    //----------------------------------
    //  ctrlKey
    //----------------------------------

    /**
     *  Indicates whether the Ctrl key was pressed at the time of the event, <code>true</code>,
     *  or not, <code>false</code>.
     * 
     *  @default false
     */
    public var ctrlKey:Boolean;
    
    //----------------------------------
    //  triggerEvent
    //----------------------------------

    /**
     *  Event that triggered the item selection event, 
     *  such as a keyboard or mouse event.
     * 
     *  @default null
     */
    public var triggerEvent:Event;
    
    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  Item renderer object for the item being selected or deselected.
     *  You can access the cell data using this property.
     * 
     *  @default null
     */
    public var itemRenderer:IListItemRenderer;
    
    //----------------------------------
    //  shiftKey
    //----------------------------------

    /**
     *  Indicates whether the Shift key was pressed at the time of the event, <code>true</code>,
     *  or not, <code>false</code>.
     * 
     *  @default false
     */
    public var shiftKey:Boolean;

    
    /**
     *  The automationValue string of the item to be selected.
     *  This is used when the item to be selected is not visible in the control. 
     */
    public var itemAutomationValue:String;
    

    /**
     *  The data provider index of the item to be selected.
     */
    public var itemIndex:uint;

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
        return new ListItemSelectEvent(type, bubbles, cancelable,
                                       itemRenderer,  triggerEvent,
                                       ctrlKey, altKey, shiftKey);
    }
}

}
