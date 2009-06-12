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
import mx.controls.listClasses.IListItemRenderer;

/**
 *   The DataGridEvent class represents event objects that are specific to
 *   the DataGrid control, such as the event that is dispatched when an 
 *   editable grid item gets the focus.
 *
 *  @see mx.controls.DataGrid
 */
public class DataGridEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The DataGridEvent.ITEM_EDIT_BEGIN constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemEditBegin</code> event, which indicates that an 
     *  item is ready to be edited. 
     *
     *  <p>The default listener for this event performs the following actions:</p>
     * 
     *  <ul>
     *    <li>Creates an item editor object by using a call to the
     *    <code>createItemEditor()</code> method.</li>
     *    <li>Copies the <code>data</code> property
     *    from the item to the editor. By default, the item editor object is an instance 
     *    of the TextInput control. You use the <code>itemEditor</code> property of the 
     *    list control to specify a custom item editor class.</li>
     *
     *    <li>Set the <code>itemEditorInstance</code> property of the list control 
     *    to reference the item editor instance.</li>
     *  </ul>
     *
     *  <p>You can write an event listener for this event to modify the data passed to 
     *  the item editor. For example, you might modify the data, its format, or other information 
     *  used by the item editor.</p>
     *
     *  <p>You can also create an event listener to specify the item editor used to 
     *  edit the item. For example, you might have two different item editors. 
     *  Within the event listener you can examine the data to be edited or 
     *  other information, and open the appropriate item editor by doing the following:</p>
     * 
     *  <ol>
     *     <li>Call <code>preventDefault()</code> to stop Flex from calling 
     *         the <code>createItemEditor()</code> method as part 
     *         of the default event listener.</li>
     *     <li>Set the <code>itemEditor</code> property to the appropriate editor.</li>
     *     <li>Call the <code>createItemEditor()</code> method.</li>
     *  </ol>
     *
     *  <p>The properties of the event object have the following values:</p>
     *
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>null</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *       that is being edited.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.ITEM_EDIT_BEGIN</td></tr>
     *  </table>
     *
     *  @eventType itemEditBegin
     */
    public static const ITEM_EDIT_BEGIN:String = "itemEditBegin";

    /**
     *  The DataGridEvent.ITEM_EDIT_END constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemEditEnd</code> event, which indicates that an edit 
     *  session is ending.  
     *
     *  <p>The list components have a default handler for this event that copies the data 
     *  from the item editor to the data provider of the list control. 
     *  The default event listener performs the following actions:</p>
     * 
     *  <ul>
     *    <li>Uses the <code>editorDataField</code> property of the DataGridColumn 
     *    associated with this event to 
     *    determine the property of the item editor containing the new data and updates
     *    the data provider item with that new data.
     *    Since the default item editor is the TextInput control, the default value of the 
     *    <code>editorDataField</code> property 
     *    is <code>"text"</code>, to specify that the <code>text</code> property of the 
     *    TextInput contains the new item data.</li>
     *
     *    <li>Calls the <code>destroyItemEditor()</code> method to close the item editor.</li>
     *  </ul>
     *
     *  <p>You typically write an event listener for this event to perform the following actions:</p>
     *  <ul>
     *    <li>In your event listener, you can modify the data returned by the editor 
     *    to the list component. For example, you can reformat the data before returning 
     *    it to the list control. By default, an item editor can only return a single value. 
     *    You must write an event listener for the <code>itemEditEnd</code> event 
     *    if you want to return multiple values.</li> 
     *
     *    <li>In your event listener, you can examine the data entered into the item editor. 
     *    If the data is incorrect, you can call the <code>preventDefault()</code> method 
     *    to stop Flex from passing the new data back to the list control and from closing 
     *    the editor. </li>
     *  </ul>
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>The name of the field or property in the
     *       data associated with the item's column.</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *       that is being edited.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>A constant defining the reason for the event. 
     *       The value must be a member of the <code>DataGridEventReason</code> class.</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.ITEM_EDIT_END</td></tr>
     *  </table>
     *
     *  @eventType itemEditEnd
     */
    public static const ITEM_EDIT_END:String = "itemEditEnd"

    /**
     *  The DataGridEvent.ITEM_FOCUS_IN constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemFocusIn</code> event, which indicates that an 
     *  item has received the focus. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>null</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item editor instance for the item
     *       that is being edited.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.ITEM_FOCUS_IN</td></tr>
     *  </table>
     *
     *  @eventType itemFocusIn
    */
    public static const ITEM_FOCUS_IN:String = "itemFocusIn";

    /**
     *  The DataGridEvent.ITEM_FOCUS_OUT constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemFocusOut</code> event, which indicates that an 
     *  item has lost the focus. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>null</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item editor instance for the item
     *       that is being edited.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.ITEM_FOCUS_OUT</td></tr>
     *  </table>
     *
     *  @eventType itemFocusOut
     */
    public static const ITEM_FOCUS_OUT:String = "itemFocusOut";

    /**
     *  The DataGridEvent.ITEM__EDIT_BEGINNING constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemEditBeginning</code> event, which indicates that the user has 
     *  prepared to edit an item, for example, by releasing the mouse button 
     *  over the item. 
     *
     *  <p>The default listener for this event sets the <code>DataGrid.editedItemPosition</code> 
     *  property to the item that has focus, which starts the item editing session.</p>
     *
     *  <p>You typically write your own event listener for this event to 
     *  disallow editing of a specific item or items. 
     *  Calling the <code>preventDefault()</code> method from within your own 
     *  event listener for this event prevents the default listener from executing.</p>
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>The name of the field or property in the
     *       data associated with the item's column.</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *       that will be edited. This property is null if this event is
     *       generated by keyboard, as the item to be edited may be off-screen.</td></tr>
     *     <tr><td><code>localX</code></td><td>NaN</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.ITEM_EDIT_BEGINNING</td></tr>
     *  </table>
     *
     *  @eventType itemEditBeginning
     */
    public static const ITEM_EDIT_BEGINNING:String = "itemEditBeginning";

    /**
     *  The DataGridEvent.COLUMN_STRETCH constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>columnStretch</code> event, which indicates that a
     *  user expanded a column horizontally.
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>dataField</code></td><td>The name of the field or property in the
     *       data associated with the column.</td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>null</td></tr>
     *     <tr><td><code>localX</code></td><td>the x position of the mouse</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *       item in the data provider.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>DataGridEvent.COLUMN_STRETCH</td></tr>
     *  </table>
     *
     *  @eventType columnStretch
    */
    public static const COLUMN_STRETCH:String = "columnStretch";

    /**
     *  The DataGridEvent.HEADER_RELEASE constant defines the value of the 
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
     *       item's column in the DataGrid object's <code>columns</code> array.</td></tr>
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
     *     <tr><td><code>type</code></td><td>DataGridEvent.HEADER_RELEASE</td></tr>
     *  </table>
     *
     *  @eventType headerRelease
     */
    public static const HEADER_RELEASE:String = "headerRelease";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  Normally called by the DataGrid object; not used in application code.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
     *
     *  @param columnIndex The zero-based index of the column where the event occurred.
     *
     *  @param dataField  The name of the field or property in the data associated with the column.
     *
     *  @param rowIndex The zero-based index of the item in the in the data provider.
     *
     *  @param reason The reason for an <code>itemEditEnd</code> event.
     *
     *  @param itemRenderer The item renderer that is being edited or the header renderer that
     *  was clicked..
     *
     *  @param localX Column x-position for replaying <code>columnStretch</code> events.
     */
    public function DataGridEvent(type:String, bubbles:Boolean = false,
                                  cancelable:Boolean = false,
                                  columnIndex:int = -1,
                                  dataField:String = null,
                                  rowIndex:int = -1, reason:String = null,
                                  itemRenderer:IListItemRenderer = null,
                                  localX:Number = NaN)
    {
        super(type, bubbles, cancelable);

        this.columnIndex = columnIndex;
        this.dataField = dataField;
        this.rowIndex = rowIndex;
        this.reason = reason;
        this.itemRenderer = itemRenderer;
        this.localX = localX;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  columnIndex
    //----------------------------------

    /**
     *  The zero-based index in the DataGrid object's <code>columns</code> array
     *  of the column associated with the event.
     */
    public var columnIndex:int;

    //----------------------------------
    //  dataField
    //----------------------------------

    /**
     *  The name of the field or property in the data associated with the column.
     */
    public var dataField:String;

    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  The item renderer for the item that is being edited or the header
     *  render that is being clicked or stretched.
     *  You can access the data provider item using this property. 
     */
    public var itemRenderer:IListItemRenderer;

    //----------------------------------
    //  localX
    //----------------------------------

    /**
     *  The column's x-position; used for replaying column stretch events.
     */
    public var localX:Number;

    //----------------------------------
    //  reason
    //----------------------------------

    /**
     *  The reason the <code>itemEditEnd</code> event was dispatched. 
     *  Valid only for events with type <code>ITEM_EDIT_END</code>.
     *  The possible values are defined in the DataGridEventReason class.
     * 
     *  @see DataGridEventReason
     */
    public var reason:String;

    //----------------------------------
    //  rowIndex
    //----------------------------------

    /**
     *  The zero-based index of the item in the data provider.
     */
    public var rowIndex:int;

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
        return new DataGridEvent(type, bubbles, cancelable,
                                 columnIndex, dataField, rowIndex,
                                 reason, itemRenderer, localX);
    }
}

}
