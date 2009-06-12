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
 *   The ListEvent class represents events associated with items 
 *   in list-based controls such as List, Tree, Menu, and DataGrid.
 *
 *  @see mx.controls.List
 *  @see mx.controls.listClasses.ListBase
 */
public class ListEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The ListEvent.CHANGE constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for a
     *  <code>change</code> event, which indicates that selection
     *  changed as a result of user interaction.
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer that was  
     *        clicked.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.CHANGE</td></tr>
     *  </table>
     *
     *  @eventType change
     */
    public static const CHANGE:String = "change";

    /**
     *  The ListEvent.ITEM_EDIT_BEGIN constant defines the value of the 
     *  <code>type</code> property of the event object for a 
     *  <code>itemEditBegin</code> event, which indicates that an 
     *  item is ready to be edited. 
     *
     *  <p>The default listener for this event performs the following actions:</p>
     * 
     *  <ul>
     *    <li>Creates an item editor object via a call to the
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
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *       that is being edited.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_EDIT_BEGIN</td></tr>
     *  </table>
     *
     *  @eventType itemEditBegin
     */
    public static const ITEM_EDIT_BEGIN:String = "itemEditBegin";

    /**
     *  The ListEvent.ITEM_EDIT_END constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for a 
     *  <code>itemEditEnd</code> event, which indicates that an edit 
     *  session is ending. 
     *
     *  <p>The list components have a default handler for this event that copies the data 
     *  from the item editor to the data provider of the list control. 
     *  The default event listener performs the following actions:</p>
     * 
     *  <ul>
     *    <li>Uses the <code>editorDataField</code> property of the list control to determine 
     *    the property of the item editor containing the new data and updates
     *    the data provider item with that new data.  Since the default item editor 
     *    is the TextInput control, the default value of the <code>editorDataField</code> property 
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
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *        that was edited.</td></tr>
     *     <tr><td><code>reason</code></td><td>A constant defining the reason for the event. 
     *       The value must be a member of the <code>ListEventReson</code> class.</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_EDIT_END</td></tr>
     *  </table>
     *
     *  @eventType itemEditEnd
     */
    public static const ITEM_EDIT_END:String = "itemEditEnd"

    /**
     *  The ListEvent.ITEM_FOCUS_IN constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemFocusIn</code> event, which indicates that an item 
     *  has received the focus.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item editor instance for
     *        the item that is being edited.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_FOCUS_IN</td></tr>
     *  </table>
     *
     *  @eventType itemFocusIn
     */
    public static const ITEM_FOCUS_IN:String = "itemFocusIn";

    /**
     *  The ListEvent.ITEM_FOCUS_OUT constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemFocusOut</code> event, which indicates that an item 
     *  has lost the focus.
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item editor instance for the
     *        item that was being edited.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_FOCUS_OUT</td></tr>
     *  </table>
     *
     *  @eventType itemFocusOut
     */
    public static const ITEM_FOCUS_OUT:String = "itemFocusOut";

    /**
     *  The ListEvent.ITEM_EDIT_BEGINNING constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for a 
     *  <code>itemEditBeginning</code> event, which indicates that the user has 
     *  prepared to edit an item, for example, by releasing the mouse button 
     *  over the item. 
     *
     *  <p>The default listener for this event sets the <code>List.editedItemPosition</code> 
     *  property to the item that has focus, which starts the item editing session.</p>
     *
     *  <p>You typically write your own event listener for this event to 
     *  disallow editing of a specific item or items. 
     *  Calling the <code>preventDefault()</code> method from within your own 
     *  event listener for this event prevents the default listener from executing.</p>
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer for the item
     *       that will be edited. This property is null if this event is
     *       generated by keyboard, as the item to be edited may be off-screen.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_EDIT_BEGIN</td></tr>
     *  </table>
     *
     *  @eventType itemEditBeginning
     */
    public static const ITEM_EDIT_BEGINNING:String = "itemEditBeginning";

    /**
     *  The ListEvent.ITEM_CLICK constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemClick</code> event, which indicates that the 
     *  user clicked the mouse over a visual item in the control.
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer that was  
     *        clicked.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_CLICK</td></tr>
     *  </table>
     *
     *  @eventType itemClick
     */
    public static const ITEM_CLICK:String = "itemClick";

    /**
     *  The ListEvent.ITEM_DOUBLE_CLICK constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemDoubleClick</code> event, which indicates that the 
     *  user double clicked the mouse over a visual item in the control.
     * 
     *  <p>To receive itemDoubleClick events, you must set the component's
     *  <code>doubleClickEnabled</code> property to <code>true</code>.</p>
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer that was
     *        double clicked.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_DOUBLE_CLICK</td></tr>
     *  </table>
     *
     *  @eventType itemDoubleClick
     */
    public static const ITEM_DOUBLE_CLICK:String = "itemDoubleClick";

    /**
     *  The ListEvent.ITEM_ROLL_OUT constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemRollOut</code> event, which indicates that the user rolled 
     *  the mouse pointer out of a visual item in the control.
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer that was
     *        rolled out.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_ROLL_OUT</td></tr>
     *  </table>
     *
     *  @eventType itemRollOut
     */
    public static const ITEM_ROLL_OUT:String = "itemRollOut";

    /**
     *  The ListEvent.ITEM_ROLL_OVER constant defines the value of the 
     *  <code>type</code> property of the ListEvent object for an
     *  <code>itemRollOver</code> event, which indicates that the user rolled 
     *  the mouse pointer over a visual item in the control.
     *
     *  <p>The properties of the event object have the following values:</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>columnIndex</code></td><td> The zero-based index of the 
     *        column associated with the event.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer that was
     *        rolled over.</td></tr>
     *     <tr><td><code>reason</code></td><td>null</td></tr>
     *     <tr><td><code>rowIndex</code></td><td>The zero-based index of the 
     *        item associated with the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>Type</code></td><td>ListEvent.ITEM_ROLL_OVER</td></tr>
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
     *  @param columnIndex The zero-based index of the column that contains
     *  the renderer.
     *
     *  @param rowIndex The zero-based index of the row that contains
     *  the renderer, or for editing events, the index of the item in
     *  the data provider that is being edited
     *
     *  @param reason The reason for an <code>itemEditEnd</code> event.
     *
     *  @param itemRenderer The item renderer for the data provider item.
     */
    public function ListEvent(type:String, bubbles:Boolean = false,
                              cancelable:Boolean = false,
                              columnIndex:int = -1, rowIndex:int = -1,
                              reason:String = null,
                              itemRenderer:IListItemRenderer = null)
    {
        super(type, bubbles, cancelable);

        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.reason = reason;
        this.itemRenderer = itemRenderer;
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
     *  The zero-based index of the column that contains
     *  the item renderer where the event occurred.
     */
    public var columnIndex:int;

    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  The item renderer where the event occurred.
     *  You can access the data provider item using this property. 
     */
    public var itemRenderer:IListItemRenderer;

    //----------------------------------
    //  reason
    //----------------------------------

    /**
     *  The reason the <code>itemEditEnd</code> event was dispatched. 
     *  Valid only for events with type <code>ITEM_EDIT_END</code>.
     *  The possible values are defined in the ListEventReason class.
     * 
     *  @see ListEventReason
     */
    public var reason:String;

    //----------------------------------
    //  rowIndex
    //----------------------------------

    /**
     *  In the zero-based index of the row that contains
     *  the item renderer where the event occured, or for editing events,
     *  the index of the item in the data provider that is being edited.
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
        return new ListEvent(type, bubbles, cancelable,
                             columnIndex, rowIndex, reason, itemRenderer);
    }
}

}
