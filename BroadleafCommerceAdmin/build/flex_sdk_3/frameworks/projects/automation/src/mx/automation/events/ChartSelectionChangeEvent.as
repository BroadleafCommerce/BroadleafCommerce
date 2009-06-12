////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
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
public class ChartSelectionChangeEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    
    /**
     *  The AdvancedDataGridEvent.HEADER_RELEASE constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>headerRelease</code> event, which indicates that the
     *  user pressed and released the mouse on a column header.
     * 
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the AdvancedDataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>The name of the field or property in the
     *       data associated with the column.</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The header renderer that is
     *       being released.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>null</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>AdvancedDataGridEvent.HEADER_RELEASE</td></tr>
     *  </table>
     *
     *  @eventType headerRelease
     */
    public static const CHANGE:String = "change";
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
    public function ChartSelectionChangeEvent(type:String, 
                                        selectionInfo:Array=null ,bubbles:Boolean = false,
                                        cancelable:Boolean = false,
                                        triggerEvent:Event = null,
                                        ctrlKey:Boolean = false,
                                        altKey:Boolean = false,
                                        shiftKey:Boolean = false)
    {
        super(type, bubbles, cancelable);

        this.selectionInfo = selectionInfo; 
        //this.chartItemIndex = chartItemIndex;
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
    //  seriesIndex
    //----------------------------------
  
    //----------------------------------
    //  shiftKey
    //----------------------------------


     public var selectionInfo:Array;
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
        return new ChartSelectionChangeEvent(type, selectionInfo, bubbles, cancelable,
                                       triggerEvent,
                                       ctrlKey, altKey, shiftKey);
    }
}

}
