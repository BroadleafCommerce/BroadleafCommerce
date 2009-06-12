////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.utils.Dictionary;
import mx.collections.CursorBookmark;
import mx.collections.IList;
import mx.collections.ItemResponder;
import mx.collections.errors.ItemPendingError;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.controls.listClasses.ListBaseContentHolder;
import mx.controls.listClasses.ListBaseSeekPending;
import mx.controls.listClasses.ListData;
import mx.controls.listClasses.ListItemRenderer;
import mx.controls.listClasses.ListRowInfo;
import mx.controls.scrollClasses.ScrollBar;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.EventPriority;
import mx.core.FlexShape;
import mx.core.FlexSprite;
import mx.core.FlexVersion;
import mx.core.IChildList;
import mx.core.IFactory;
import mx.core.IIMESupport;
import mx.core.IInvalidating;
import mx.core.IPropertyChangeNotifier;
import mx.core.IRawChildrenContainer;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.ListEvent;
import mx.events.ListEventReason;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.styles.StyleManager;
import mx.collections.ItemWrapper;
import mx.collections.ModifiedCollectionView;
import mx.core.IUIComponent;

use namespace mx_internal;

[IconFile("List.png")]

[DataBindingInfo("acceptedTypes", "{ dataProvider: { label: &quot;String&quot; } }")]

[DefaultProperty("dataProvider")]

[DefaultBindingProperty(source="selectedItem", destination="dataProvider")]

[DefaultTriggerEvent("change")]

[AccessibilityClass(implementation="mx.accessibility.ListAccImpl")]

/**
 *  Dispatched when the user releases the mouse button while over an item,
 *  tabs to the List or within the List, or in any other way
 *  attempts to edit an item.
 *
 *  @eventType mx.events.ListEvent.ITEM_EDIT_BEGINNING
 */
[Event(name="itemEditBeginning", type="mx.events.ListEvent")]

/**
 *  Dispatched when the <code>editedItemPosition</code> property is set
 *  and the item can be edited.
 *
 *  @eventType mx.events.ListEvent.ITEM_EDIT_BEGIN
 */
[Event(name="itemEditBegin", type="mx.events.ListEvent")]

/**
 *  Dispatched when an item editing session is ending for any reason.
 *
 *  @eventType mx.events.ListEvent.ITEM_EDIT_END
 */
[Event(name="itemEditEnd", type="mx.events.ListEvent")]

/**
 *  Dispatched when an item renderer gets focus, which can occur if the user
 *  clicks on an item in the List control or navigates to the item using a 
 *  keyboard.
 *  Only dispatched if the list item is editable.
 *
 *  @eventType mx.events.ListEvent.ITEM_FOCUS_IN
 */
[Event(name="itemFocusIn", type="mx.events.ListEvent")]

/**
 *  Dispatched when an item renderer loses the focus, which can occur if the 
 *  user clicks another item in the List control or outside the list, 
 *  or uses the keyboard to navigate to another item in the List control
 *  or outside the List control.
 *  Only dispatched if the list item is editable.
 *
 *  @eventType mx.events.ListEvent.ITEM_FOCUS_OUT
 */
[Event(name="itemFocusOut", type="mx.events.ListEvent")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  The data effect to play when a change occur to the control's data provider.
 *
 *  <p>By default, the List control does not use a data effect. 
 *  For the List control, use an instancs of the the DefaultListEffect class 
 *  to configure the data effect. </p>
 *
 * @default undefined
 */
[Effect(name="itemsChangeEffect", event="itemsChange")]

/**
 *  The List control displays a vertical list of items.
 *  Its functionality is very similar to that of the SELECT
 *  form element in HTML.
 *  If there are more items than can be displayed at once, it
 *  can display a vertical scroll bar so the user can access
 *  all items in the list.
 *  An optional horizontal scroll bar lets the user view items
 *  when the full width of the list items is unlikely to fit.
 *  The user can select one or more items from the list, depending
 *  on the value of the <code>allowMultipleSelection</code> property.
 *
 *  <p>The List control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Wide enough to fit the widest label in the first seven 
 *               visible items (or all visible items in the list, if 
 *               there are less than seven); seven rows high, where 
 *               each row is 20 pixels high.</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>5000 by 5000.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:List&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:List
 *    <b>Properties</b>
 *    editable="false|true"
 *    editedItemPosition="<i>No default</i>"
 *    editorDataField="text"
 *    editorHeightOffset="0"
 *    editorUsesEnterKey="false|true"
 *    editorWidthOffset="0"
 *    editorXOffset="0"
 *    editorYOffset="0"
 *    imeMode="null"    
 *    itemEditor="TextInput"
 *    itemEditorInstance="<i>Current item editor</i>"
 *    rendererIsEditor="false|true"
 *    
 *    <b>Styles</b>
 *    backgroundDisabledColor="0xDDDDDD"
 *    
 *    <b>Events</b>
 *    itemEditBegin="<i>No default</i>"
 *    itemEditEnd="<i>No default</i>"
 *    itemEditBeginning="<i>No default</i>"
 *    itemFocusIn="<i>No default</i>"
 *    itemFocusOut="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/SimpleList.mxml
 *
 *  @see mx.events.ListEvent
 */
public class List extends ListBase implements IIMESupport
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by ListAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function List()
    {
        super();

        listType = "vertical";

        bColumnScrolling = false;

        itemRenderer = new ClassFactory(ListItemRenderer);

        _horizontalScrollPolicy = ScrollPolicy.OFF;
        _verticalScrollPolicy = ScrollPolicy.AUTO;

        defaultColumnCount = 1;
        defaultRowCount = 7;

        // Register default handlers for item editing and sorting events.
        addEventListener(ListEvent.ITEM_EDIT_BEGINNING,
                         itemEditorItemEditBeginningHandler,
                         false, EventPriority.DEFAULT_HANDLER);
        addEventListener(ListEvent.ITEM_EDIT_BEGIN,
                         itemEditorItemEditBeginHandler,
                         false, EventPriority.DEFAULT_HANDLER);
        addEventListener(ListEvent.ITEM_EDIT_END,
                         itemEditorItemEditEndHandler,
                         false, EventPriority.DEFAULT_HANDLER);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  A reference to the currently active instance of the item editor, 
     *  if it exists.
     *
     *  <p>To access the item editor instance and the new item value when an 
     *  item is being edited, you use the <code>itemEditorInstance</code> 
     *  property. The <code>itemEditorInstance</code> property
     *  is not valid until after the event listener for
     *  the <code>itemEditBegin</code> event executes. Therefore, you typically
     *  only access the <code>itemEditorInstance</code> property from within 
     *  the event listener for the <code>itemEditEnd</code> event.</p>
     *
     *  <p>The <code>itemEditor</code> property defines the
     *  class of the item editor
     *  and, therefore, the data type of the item editor instance.</p>
     *
     *  <p>You do not set this property in MXML.</p>
     */
    public var itemEditorInstance:IListItemRenderer;

    /**
     *  A reference to the item renderer
     *  in the DataGrid control whose item is currently being edited.
     *
     *  <p>From within an event listener for the <code>itemEditBegin</code>
     *  and <code>itemEditEnd</code> events,
     *  you can access the current value of the item being edited
     *  using the <code>editedItemRenderer.data</code> property.</p>
     */
    public function get editedItemRenderer():IListItemRenderer
    {
        if (!itemEditorInstance) return null;

        return listItems[actualRowIndex][actualColIndex];
    }

    /**
     *  @private
     *  true if we want to block editing on mouseUp
     */
    private var dontEdit:Boolean = false;

    /**
     *  @private
     *  true if we want to block editing on mouseUp
     */
    private var losingFocus:Boolean = false;

    /**
     *  @private
     *  true if we're in the endEdit call.  Used to handle
     *  some timing issues with collection updates
     */
    private var inEndEdit:Boolean = false;

    // last known position of item editor
    private var actualRowIndex:int;
    private var actualColIndex:int = 0;

    /**
     *  cache of measuring objects by factory
     */
    protected var measuringObjects:Dictionary;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a List is calculated the same as for ListBase.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            if (listItems.length && listItems[0].length)
                return borderMetrics.top + cachedPaddingTop + listItems[0][0].baselinePosition;

            return NaN;
        }
        
        return super.baselinePosition;
    }

    //----------------------------------
    //  maxHorizontalScrollPosition
    //----------------------------------

    /**
     *  @private
     *  The maximum value of <code>horizontalScrollPosition</code> in pixels.
     *  The default value is NaN.
     *  If this value is NaN, the first time the List is layed out
     *  it sets <code>horizontalScrollPosition</code> to twice the width.
     *  You can calculate the exact value of
     *  <code>maxHorizontalScrollPosition</code> by calling
     *  the <code>measureWidthOfItems()</code> method on the widest string,
     *  and then subtracting the width of the List and the width of its borders.
     *
     *  <p>For example if the fifth item is the widest,
     *  you set <code>maxHorizontalScrollPosition</code> like this:</p>
     *  <pre>list.maxHorizontalScrollPosition = list.measureWidthOfItems(5, 1) - (list.width -
     *  list.viewMetrics.left - list.viewMetrics.right)</pre>
     */
    override public function set maxHorizontalScrollPosition(value:Number):void
    {
        super.maxHorizontalScrollPosition = value;
        scrollAreaChanged = true;

        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  editable
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  A flag that indicates whether or not the user can edit
     *  items in the data provider.
     *  If <code>true</code>, the item renderers in the control are editable.
     *  The user can click on an item renderer to open an editor.
     *
     *  @default false
     */
    public var editable:Boolean = false;

    //----------------------------------
    //  itemEditor
    //----------------------------------

    [Inspectable(category="Data")]

    /**
     *  The class factory for the item editor to use for the control, if the 
     *  <code>editable</code> property is set to <code>true</code>. 
     *
     *  @default new ClassFactory(mx.controls.TextInput)
     */
    public var itemEditor:IFactory = new ClassFactory(TextInput);

    //----------------------------------
    //  editorDataField
    //----------------------------------

    [Inspectable(category="Data")]

    /**
     *  The name of the property of the item editor that contains the new
     *  data for the list item.
     *  For example, the default <code>itemEditor</code> is
     *  TextInput, so the default value of the <code>editorDataField</code> property is
     *  <code>"text"</code>, which specifies the <code>text</code> property of the
     *  the TextInput control.
     */
    public var editorDataField:String = "text";

    //----------------------------------
    //  editorHeightOffset
    //----------------------------------

    [Inspectable(defaultValue="0")]
    
    /**
     *  The height of the item editor, in pixels, relative to the size of the 
     *  item renderer. This property can be used to make the editor overlap
     *  the item renderer by a few pixels to compensate for a border around the
     *  editor.  
     *  <p>Changing these values while the editor is displayed
     *  will have no effect on the current editor, but will affect the next
     *  item renderer that opens an editor.</p>
     *
     *  @default 0
     */
    public var editorHeightOffset:Number = 0;

    //----------------------------------
    //  editorWidthOffset
    //----------------------------------

    [Inspectable(defaultValue="0")]
    
    /**
     *  The width of the item editor, in pixels, relative to the size of the 
     *  item renderer. This property can be used to make the editor overlap
     *  the item renderer by a few pixels to compensate for a border around the
     *  editor.
     *  <p>Changing these values while the editor is displayed
     *  will have no effect on the current editor, but will affect the next
     *  item renderer that opens an editor.</p>
     *
     *  @default 0
     */
    public var editorWidthOffset:Number = 0;

    //----------------------------------
    //  editorXOffset
    //----------------------------------

    [Inspectable(defaultValue="0")]
    
    /**
     *  The x location of the upper-left corner of the item editor,
     *  in pixels, relative to the upper-left corner of the item.
     *  This property can be used to make the editor overlap
     *  the item renderer by a few pixels to compensate for a border around the
     *  editor.
     *  <p>Changing these values while the editor is displayed
     *  will have no effect on the current editor, but will affect the next
     *  item renderer that opens an editor.</p>
     * 
     *  @default 0
     */
    public var editorXOffset:Number = 0;

    //----------------------------------
    //  editorYOffset
    //----------------------------------

    [Inspectable(defaultValue="0")]
    
    /**
     *  The y location of the upper-left corner of the item editor,
     *  in pixels, relative to the upper-left corner of the item.
     *  This property can be used to make the editor overlap
     *  the item renderer by a few pixels to compensate for a border around the
     *  editor.
     *  <p>Changing these values while the editor is displayed
     *  will have no effect on the current editor, but will affect the next
     *  item renderer that opens an editor.</p>
     *
     *  @default 0
     */
    public var editorYOffset:Number = 0;

    //----------------------------------
    //  editorUsesEnterKey
    //----------------------------------

    [Inspectable(defaultValue="false")]

    /**
     *  A flag that indicates whether the item editor uses Enter key.
     *  If this property is set to <code>true</code>, the item editor uses the Enter key and the
     *  List will not look for the Enter key and move the editor in
     *  response.
     *  <p>Changing this value while the editor is displayed
     *  will have no effect on the current editor, but will affect the next
     *  item renderer that opens an editor.</p>
     *
     *  @default false
     */
    public var editorUsesEnterKey:Boolean = false;

    //----------------------------------
    //  enabled
    //----------------------------------

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;

        if (itemEditorInstance)
            endEdit(ListEventReason.OTHER);

        invalidateDisplayList();
    }

    //----------------------------------
    //  editedItemPosition
    //----------------------------------

    /**
     *  @private
     */
    private var bEditedItemPositionChanged:Boolean = false;

    /**
     *  @private
     *  undefined means we've processed it
     *  null means don't put up an editor
     *  {} is the coordinates for the editor
     */
    private var _proposedEditedItemPosition:*;

    /**
     *  @private
     *  the last editedItemPosition.  We restore editing
     *  to this point if we get focus from the TAB key
     */
    private var lastEditedItemPosition:*;

    /**
     *  @private
     */
    private var _editedItemPosition:Object;

    [Bindable("itemFocusIn")]

    /**
     *  The column and row index of the item renderer for the
     *  data provider item being edited, if any.
     *
     *  <p>This Object has two fields, <code>columnIndex</code> and 
     *  <code>rowIndex</code>,
     *  the zero-based column and item indexes of the item.
     *  For a List control, the <code>columnIndex</code> property is always 0;
     *  for example: <code>{columnIndex:0, rowIndex:3}</code>.</p>
     *
     *  <p>Setting this property scrolls the item into view and
     *  dispatches the <code>itemEditBegin</code> event to
     *  open an item editor on the specified item,
     *  </p>
     */
    public function get editedItemPosition():Object
    {
        if (_editedItemPosition)
            return {rowIndex: _editedItemPosition.rowIndex,
                columnIndex: 0};
        else
            return _editedItemPosition;
    }

    /**
     *  @private
     */
    public function set editedItemPosition(value:Object):void
    {
        var newValue:Object = {rowIndex: value.rowIndex,
            columnIndex: 0};

        setEditedItemPosition(newValue);
    }

    //----------------------------------
    //  lockedRowCount
    //----------------------------------

    /**
     *  @private
     *  Storage for the lockedRowCount property.
     */
    mx_internal var _lockedRowCount:int = 0;

    [Inspectable(defaultValue="0")]

    /**
     *  The index of the first row in the control that scrolls.
     *  Rows above this one remain fixed in view.
     * 
     *  @default 0
     *  @private
     */
    public function get lockedRowCount():int
    {
        return _lockedRowCount;
    }

    /**
     *  @private
     */
    public function set lockedRowCount(value:int):void
    {
        _lockedRowCount = value;

        invalidateDisplayList();
    }

    //----------------------------------
    //  rendererIsEditor
    //----------------------------------

    [Inspectable(category="Data", defaultValue="false")]
    
    /**
     *  Specifies whether the item renderer is also an item 
     *  editor. If this property is <code>true</code>, Flex
     *  ignores the <code>itemEditor</code> property.
     *
     *  @default false
     */
    public var rendererIsEditor:Boolean = false;

    //----------------------------------
    //  imeMode
    //----------------------------------

    /**
     *  @private
     *  Storage for the imeMode property.
     */
    private var _imeMode:String;

    /**
     *  Specifies the IME (input method editor) mode.
     *  The IME enables users to enter text in Chinese, Japanese, and Korean.
     *  Flex sets the specified IME mode when the control gets focus,
     *  and sets it back to the previous value when the control loses focus.
     *
     * <p>The flash.system.IMEConversionMode class defines constants for the
     *  valid values for this property.
     *  You can also specify <code>null</code> to specify no IME.</p>
     *  
     *  @see flash.system.IME
     *  @see flash.system.IMEConversionMode
     *  
     *  @default null
     */
    public function get imeMode():String
    {
        return _imeMode;
    }

    /**
     *  @private
     */
    public function set imeMode(value:String):void
    {
        _imeMode = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    [Inspectable(category="Data", defaultValue="undefined")]

    /**
     *  @private
     */
    override public function set dataProvider(value:Object):void
    {
        if (itemEditorInstance)
            endEdit(ListEventReason.OTHER);
        
        super.dataProvider = value;
    }

    /**
     *  @private
     *  Called by the initialize() method of UIComponent
     *  to hook in the accessibility code.
     */
    override protected function initializeAccessibility():void
    {
        if (createAccessibilityImplementation != null)
            createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (itemsNeedMeasurement)
        {
            itemsNeedMeasurement = false;
            if (isNaN(explicitRowHeight))
            {
                if (iterator)
                {
                    var paddingTop:Number = getStyle("paddingTop");
                    var paddingBottom:Number = getStyle("paddingBottom");

                    // trace("calculate height " + index + " " + count);
                    var item:IListItemRenderer = getMeasuringRenderer(iterator.current);

                    var ww:Number = 200;
                    if (listContent.width)
                        ww = listContent.width;
                    item.explicitWidth = ww;

                    setupRendererFromData(item, iterator.current);

                    var rh:int = item.getExplicitOrMeasuredHeight() + paddingTop + paddingBottom;

                    // unless specified otherwise, rowheight defaults to 20
                    setRowHeight(Math.max(rh, 20));
                }
                else
                    setRowHeight(20);
            }
            if (isNaN(explicitColumnWidth))
                setColumnWidth(measureWidthOfItems(0, (explicitRowCount < 1) ? defaultRowCount : explicitRowCount));
        }
    }

    /**
     *  @private
     *  The measuredWidth is widest of the items in the first set of rows it will display.
     *  If the rowCount property has been set it will measure that many rows, otherwise
     *  it will measure 7 rows and use the widest.
     *  The measuredHeight is based on the height of one line of text or 20 pixels, whichever
     *  is greater.  Thus the measuredHeight will depend on fonts related styles like fontSize.
     *  Then that height is multiplied by 7 or rowCount if it has been specified.
     *  
     */
    override protected function measure():void
    {
        super.measure();

        var o:EdgeMetrics = viewMetrics;
        measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH; // room for scrollbar and room for some of the renderer

        // if we've been layed out once (initialized)
        // then if no explicit rowCount or rowHeight, just use the current
        // height.  Otherwise, if the data changes and has variable rowheight
        // the height can shift and cause re-layout.
        if (initialized && variableRowHeight && explicitRowCount < 1 && isNaN(explicitRowHeight))
            measuredHeight = height;
    }

    /**
     *  @private
     */
    override protected function configureScrollBars():void
    {
        var rowCount:int = listItems.length;
        if (rowCount == 0) return;

        // ignore nonvisible rows off the top
        var yy:Number;
        var i:int;
        var n:int = listItems.length;
        // if there is more than one row and it is a partial row we dont count it
        while (rowCount > 1 && rowInfo[n - 1].y + rowInfo[n-1].height > listContent.height - listContent.bottomOffset)
        {
            rowCount--;
            n--;
        }

        // offset, when added to rowCount, is the index of the dataProvider
        // item for that row.  IOW, row 10 in listItems is showing dataProvider
        // item 10 + verticalScrollPosition - lockedRowCount - 1;
        var offset:int = verticalScrollPosition - lockedRowCount - 1;
        // don't count filler rows at the bottom either.
        var fillerRows:int = 0;
        // don't count filler rows at the bottom either.
        while (rowCount && listItems[rowCount - 1].length == 0)
        {
            if (collection && rowCount + offset >= collection.length)
            {
                rowCount--;
                ++fillerRows;
            }
            else
                break;
        }

        // we have to scroll up.  We can't have filler rows unless the scrollPosition is 0
        // We don't do the adjustment if a data effect is running, because that prevents
        // a smooth effect. Effectively, we pin the scroll position while the effect is
        // running.
        if (verticalScrollPosition > 0 && fillerRows > 0 && !runningDataEffect)
        {
            if (adjustVerticalScrollPositionDownward(Math.max(rowCount, 1)))
                return;
        }

        if (listContent.topOffset)
        {
            yy = Math.abs(listContent.topOffset);
            i = 0;
            while (rowInfo[i].y + rowInfo[i].height <= yy)
            {
                rowCount--;
                i++;
                if (i == rowCount)
                    break;
            }
        }

        var colCount:int = listItems[0].length;
        var oldHorizontalScrollBar:Object = horizontalScrollBar;
        var oldVerticalScrollBar:Object = verticalScrollBar;
        var roundedWidth:int = Math.round(unscaledWidth);
        var length:int = collection ? collection.length - lockedRowCount: 0;
        var numRows:int = rowCount - lockedRowCount;

        setScrollBarProperties((isNaN(_maxHorizontalScrollPosition)) ?
                            Math.round(listContent.width) :
                            Math.round(_maxHorizontalScrollPosition + roundedWidth),
                            roundedWidth, length, numRows);
        maxVerticalScrollPosition = Math.max(length - numRows, 0);

    }

    /**
     *  @private
     *  Makes verticalScrollPosition smaller until it is 0 or there
     *  are no empty rows.  This is needed if we're scrolled to the
     *  bottom and something is deleted or the rows resize so more
     *  rows can be shown.
     */
    private function adjustVerticalScrollPositionDownward(rowCount:int):Boolean
    {
        var bookmark:CursorBookmark = iterator.bookmark;

        // add up how much space we're currently taking with valid items
        var h:Number = 0;
        
        var ch:Number = 0;
        var n:int;
        var j:int;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");

        h = rowInfo[rowCount - 1].y + rowInfo[rowCount - 1].height;
        h = listContent.heightExcludingOffsets - listContent.topOffset - h;
        
        // back up one
        var numRows:int = 0;
        try
        {
            if (iterator.afterLast)
                iterator.seek(CursorBookmark.LAST, 0)
            else
                var more:Boolean = iterator.movePrevious();
        }
        catch(e:ItemPendingError)
        {
            more = false;
        }
        if (!more)
        {
            // reset to 0;
            super.verticalScrollPosition = 0;
            try
            {
                iterator.seek(CursorBookmark.FIRST, 0);
                if (!iteratorValid)
                {
                    iteratorValid = true;
                    lastSeekPending = null;
                }
            }
            catch(e:ItemPendingError)
            {
                lastSeekPending = new ListBaseSeekPending(CursorBookmark.FIRST, 0);
                e.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler,
                                                lastSeekPending));
                iteratorValid = false;
                invalidateList();
                return true;
            }
            updateList();
            return true;
        }

        var item:IListItemRenderer = getMeasuringRenderer(iterator.current);
        item.explicitWidth = listContent.width - paddingLeft - paddingRight;

        // now work backwards to see how many more rows we need to create
        while (h > 0 && more)
        {
            var data:Object;
            
            if (more)
            {
                data = iterator.current;
                setupRendererFromData(item, data);
                ch = variableRowHeight ? 
                    item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop : 
                    rowHeight;
            }
            
            h -= ch;
        
            try 
            {
                more = iterator.movePrevious();
                numRows++;
            }
            catch(e:ItemPendingError)
            {
                // if we run out of data, assume all remaining rows are the size of the previous row
                more = false;
            }
        }

        // if we overrun, go back one.
        if (h < 0)
            numRows--;

        iterator.seek(bookmark, 0);
        
        verticalScrollPosition = Math.max(0, verticalScrollPosition - numRows);

        // make sure we get through configureScrollBars w/o coming in here.
        if (numRows > 0 && !variableRowHeight)
            configureScrollBars();

        return numRows > 0;
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        setRowCount(listItems.length);

        // if this code doesn't execute because editing is prevented while
        // an effect is running, the editor should appear after the effect
        // finishes
        if (bEditedItemPositionChanged && !editingTemporarilyPrevented(_proposedEditedItemPosition))
        {
            bEditedItemPositionChanged = false;
            commitEditedItemPosition(_proposedEditedItemPosition);
            _proposedEditedItemPosition = undefined;
        }

        drawRowBackgrounds();
    }

    /**
     *  @private
     */
    override protected function adjustListContent(unscaledWidth:Number = -1,
                                       unscaledHeight:Number = -1):void
    {
        // Can't currently call this without valid parameters...

        var lcx:Number = viewMetrics.left + Math.max(listContent.leftOffset, 0);
        var lcy:Number = viewMetrics.top + listContent.topOffset;
        listContent.move(lcx, lcy);
        
        var ww:Number = Math.max(0, listContent.rightOffset) - lcx - viewMetrics.right;
        var hh:Number = Math.max(0, listContent.bottomOffset) - lcy - viewMetrics.bottom;
        var scrollableWidth:Number = unscaledWidth + ww;
        
        // if they want the scrollbar, we don't know how much to scroll, without rendering
        // everything (on and/or off-screen).  Since this could be expensive, we use a heuristic: 
        // if maxHorizontalScrollPosition is specified, we let them scroll that much.  If it's not 
        // specified, we double the amount of viewable space and let them scroll that much.  This is
        // just a heuristic and may not work perfectly for really long content or short content.
        if (horizontalScrollPolicy == ScrollPolicy.ON ||
            (horizontalScrollPolicy == ScrollPolicy.AUTO && !isNaN(_maxHorizontalScrollPosition)))
        {
            if (isNaN(_maxHorizontalScrollPosition))
                scrollableWidth *= 2;
            else
                scrollableWidth += _maxHorizontalScrollPosition;
        }
        listContent.setActualSize(scrollableWidth, unscaledHeight + hh);
    }

    /**
     *  @private
     */
    override protected function drawRowBackgrounds():void
    {
        var rowBGs:Sprite = Sprite(listContent.getChildByName("rowBGs"));
        if (!rowBGs)
        {
            rowBGs = new FlexSprite();
            rowBGs.mouseEnabled = false;
            rowBGs.name = "rowBGs";
            listContent.addChildAt(rowBGs, 0)
        }

        var colors:Array;

        colors = getStyle("alternatingItemColors");

        if (!colors || colors.length == 0)
        {
            while (rowBGs.numChildren > n)
            {
                rowBGs.removeChildAt(rowBGs.numChildren - 1);
            }
            return;
        }

        StyleManager.getColorNames(colors);

        var curRow:int = 0;
        var actualRow:int = verticalScrollPosition;
        var i:int = 0;
        var n:int = listItems.length;

        while (curRow < n)
        {
            drawRowBackground(rowBGs, i++, rowInfo[curRow].y, rowInfo[curRow].height, colors[actualRow % colors.length], actualRow);
            curRow++;
            actualRow++;
        }

        while (rowBGs.numChildren > n)
        {
            rowBGs.removeChildAt(rowBGs.numChildren - 1);
        }
    }

    /**
     *  Draws a row background 
     *  at the position and height specified. This creates a Shape as a
     *  child of the input Sprite and fills it with the appropriate color.
     *  This method also uses the <code>backgroundAlpha</code> style property 
     *  setting to determine the transparency of the background color.
     * 
     *  @param s A Sprite that will contain a display object
     *  that contains the graphics for that row.
     *
     *  @param rowIndex The row's index in the set of displayed rows. The
     *  header does not count; the top most visible row has a row index of 0.
     *  This is used to keep track of the objects used for drawing
     *  backgrounds so that a particular row can reuse the same display object
     *  even though the index of the item that the row is rendering has changed.
     *
     *  @param y The suggested y position for the background.
     * 
     *  @param height The suggested height for the indicator.
     * 
     *  @param color The suggested color for the indicator.
     * 
     *  @param dataIndex The index of the item for that row in the
     *  data provider. For example, this can be used to color the 10th item differently.
     */
    protected function drawRowBackground(s:Sprite, rowIndex:int,
                                            y:Number, height:Number, color:uint, dataIndex:int):void
    {
        // trace("drawRowBackground " + rowIndex + " " + color);

        var bg:Shape;
        if (rowIndex < s.numChildren)
        {
            bg = Shape(s.getChildAt(rowIndex));
        }
        else
        {
            bg = new FlexShape();
            bg.name = "rowBackground";
            s.addChild(bg);
        }

        // Height is usually as tall is the items in the row,
        // but not if it would extend below the bottom of listContent.
        var height:Number = Math.min(
            rowInfo[rowIndex].height,
            listContent.height - rowInfo[rowIndex].y);

        bg.y = rowInfo[rowIndex].y;

        var g:Graphics = bg.graphics;
        g.clear();
        g.beginFill(color, getStyle("backgroundAlpha"));
        g.drawRect(0, 0, listContent.width, height);
        g.endFill();
    }

    /**
     *  @private
     */
    override protected function makeRowsAndColumns(left:Number, top:Number,
                                                right:Number, bottom:Number,
                                                firstCol:int, firstRow:int,
                                                byCount:Boolean = false, rowsNeeded:uint = 0):Point
    {
        // trace(this, "makeRowsAndColumns " + left + " " + top + " " + right + " " + bottom + " " + firstCol + " " + firstRow);
        listContent.allowItemSizeChangeNotification = false;

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");

        var xx:Number = left + paddingLeft - horizontalScrollPosition;
        var ww:Number = right - paddingLeft - paddingRight;
        var yy:Number;
        var hh:Number;

        var bSelected:Boolean = false;
        var bHighlight:Boolean = false;
        var bCaret:Boolean = false;

        var i:int;
        var j:int;

        var colNum:int = 0;
        var rowNum:int = lockedRowCount;
        var rowsMade:int = 0;

        var item:IListItemRenderer;
        var oldItem:IListItemRenderer;
        var rowData:BaseListData;
        var data:Object;
        var wrappedData:Object;
        var uid:String;
        var more:Boolean = true;
        var valid:Boolean = true;
        var rh:Number;
        
            yy = top;
            rowNum = firstRow;
            more = (iterator != null && !iterator.afterLast && iteratorValid);

            while ((!byCount && yy < bottom) || (byCount && rowsNeeded > 0))
            {
                if (byCount)
                    rowsNeeded--;

                valid = more;
                wrappedData = more ? iterator.current : null;
                data = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;

                uid = null;

                if (!listItems[rowNum])
                    listItems[rowNum] = [];

                // TODO more code around renderer recycling should be shared with TileBase /
                // moved to ListBase if possible
                if (valid)
                {
                    item = listItems[rowNum][colNum];
                    uid = itemToUID(wrappedData);

                    // If we have an item at the current location, we want to 
                    // re use it if the data is "the same", but the test
                    // is a little complex.
                    // If we're running a data effect, and the renderer is already
                    // presenting wrapped data, make sure the wrappers are the same.
                    // If running a data effect, but not presenting wrapped data
                    // (meaning the data effect was just initiated), or if
                    // not running data effect, just check that the data is the same.
                    if (!item ||
                        ((runningDataEffect && dataItemWrappersByRenderer[item]) 
                            ? (dataItemWrappersByRenderer[item] != wrappedData)
                            : (item.data != data)))
                    {
                        // if we're allowed to re-use existing renderers
                        if (allowRendererStealingDuringLayout)
                        {
                            // first try to steal an item renderer
                            item = visibleData[uid];
                            // if we can't steal a renderer with the same wrapper, try to
                            // steal one with the same actual data (if the data is wrapped).
                            // This is to re-use renderers when we start running a data effect.
                            if (!item && (wrappedData != data))
                                item = visibleData[itemToUID(data)];
                        }
                        
                        if (item) // if we've stolen a renderer from somewhere else...
                        {
                            // update data structures so we're not pointing to it twice
                            var ld:BaseListData = BaseListData(rowMap[item.name]);

                            // don't steal a previous one...only a later one
                            if (ld && (ld.rowIndex > rowNum))
                                listItems[ld.rowIndex] = [];
                            else
                                item = null;
                        }
                        
                        if (!item)
                            item = getReservedOrFreeItemRenderer(wrappedData);
                        
                        if (!item)
                        {
                            item = createItemRenderer(data);
                            item.owner = this;
                            item.styleName = listContent;
                            // trace("created item " + item);
                            listContent.addChild(DisplayObject(item));
                        }

                        oldItem = listItems[rowNum][colNum];
                        if (oldItem)
                            addToFreeItemRenderers(oldItem);
                        listItems[rowNum][colNum] = item;
                    }
                    rowData = makeListData(data, uid, rowNum);
                    rowMap[item.name] = rowData;

                    if (item is IDropInListItemRenderer)
                    {
                        if (data != null)
                            IDropInListItemRenderer(item).listData = rowData;
                        else
                            IDropInListItemRenderer(item).listData = null;
                    }

                    item.data = data;
                    item.enabled = enabled;
                    item.visible = true;
                    if (uid != null)
                        visibleData[uid] = item;

                    if (wrappedData != data)
                        dataItemWrappersByRenderer[item] = wrappedData;

                    item.explicitWidth = ww;

                    if ((item is IInvalidating)
                        && (wordWrapChanged || variableRowHeight))
                        IInvalidating(item).invalidateSize();

                    UIComponentGlobals.layoutManager.validateClient(item, true);

                    hh = Math.ceil(variableRowHeight ?
                         item.getExplicitOrMeasuredHeight() +
                         cachedPaddingTop + cachedPaddingBottom :
                         rowHeight);
                    rh = item.getExplicitOrMeasuredHeight();
                    item.setActualSize(ww, variableRowHeight ? rh : rowHeight - cachedPaddingTop - cachedPaddingBottom);
                    item.move(xx, yy + cachedPaddingTop);
                }
                else
                {
                    // trace("not valid");
                    // if we've run out of data, we dont make renderers
                    // and we inherit the previous row's height or rowHeight
                    // if it is the first row.
                    hh = rowNum > 0 ? rowInfo[rowNum - 1].height : rowHeight;

                    if (hh == 0)
                        hh = rowHeight;

                    oldItem = listItems[rowNum][colNum];
                    if (oldItem)
                    {
                        addToFreeItemRenderers(oldItem);
                        listItems[rowNum].splice(colNum, 1);
                    }
                }
                bSelected = selectedData[uid] != null;
                if (wrappedData != data)
                {
                    bSelected = bSelected || selectedData[itemToUID(data)];
                    bSelected = bSelected && (!getRendererSemanticValue(item,ModifiedCollectionView.REPLACEMENT))
                        && (!getRendererSemanticValue(item,ModifiedCollectionView.ADDED));
                }
                    
                bHighlight = highlightUID == uid;
                bCaret = caretUID == uid;
                rowInfo[rowNum] = new ListRowInfo(yy, hh, uid, data);
                if (valid)
                    drawItem(item, bSelected, bHighlight, bCaret);
                yy += hh;
                rowNum++;
                rowsMade++;
                if (iterator && more)
                {
                    try
                    {
                        more = iterator.moveNext();
                    }
                    catch(e:ItemPendingError)
                    {
                        lastSeekPending = new ListBaseSeekPending(CursorBookmark.CURRENT, 0)
                        e.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler,
                                                        lastSeekPending));
                        more = false;
                        iteratorValid = false;
                    }
                }
            }
            // byCount means we're making rows and wont get all the way to the bottom
            // so we skip this cleanup pass
            if (!byCount)
            {
                // delete extra rows
                while (rowNum < listItems.length)
                {
                    var rr:Array = listItems.pop();
                    rowInfo.pop();
                    while (rr.length)
                    {
                        item = rr.pop();
                        addToFreeItemRenderers(item);
                    }
                }
            }

        if (itemEditorInstance)
        {
            listContent.setChildIndex(DisplayObject(itemEditorInstance),
                                      listContent.numChildren - 1);
            item = listItems[actualRowIndex][actualColIndex];
            var rowInfo:ListRowInfo = rowInfo[actualRowIndex];
            if (item && !rendererIsEditor)
            {
                var dx:Number = editorXOffset;
                var dy:Number = editorYOffset;
                var dw:Number = editorWidthOffset;
                var dh:Number = editorHeightOffset;
                layoutEditor(item.x + dx, rowInfo.y + dy,
                            Math.min(item.width + dw, listContent.width - listContent.x - itemEditorInstance.x),
                            Math.min(rowInfo.height + dh, listContent.height - listContent.y - itemEditorInstance.y));

            }
        }

        listContent.allowItemSizeChangeNotification = variableRowHeight;

        return new Point(colNum, rowsMade);
    }

    /**
     *  Positions the item editor instance at the suggested position
     *  with the suggested dimensions. The Tree control overrides this
     *  method and adjusts the position to compensate for indentation
     *  of the renderer.
     *
     *  @param x The suggested x position for the indicator.
     *  @param y The suggested y position for the indicator.
     *  @param w The suggested width for the indicator.
     *  @param h The suggested height for the indicator.
     */
    protected function layoutEditor(x:int, y:int, w:int, h:int):void
    {
        itemEditorInstance.move(x, y);
        itemEditorInstance.setActualSize(w, h);
    }

    /**
     *  @private
     */
    override protected function scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            if (itemEditorInstance)
                endEdit(ListEventReason.OTHER);

            if (!liveScrolling &&
                ScrollEvent(event).detail == ScrollEventDetail.THUMB_TRACK)
            {
                return;
            }

            var scrollBar:ScrollBar = ScrollBar(event.target);
            var pos:Number = scrollBar.scrollPosition;
            var delta:int;
            var o:EdgeMetrics;

            removeClipMask();

            if (scrollBar == verticalScrollBar)
            {
                delta = pos - verticalScrollPosition;
                
                super.scrollHandler(event);
                
                if (Math.abs(delta) >= listItems.length - lockedRowCount || !iteratorValid)
                {
                    try
                    {
                        if (!iteratorValid)
                            iterator.seek(CursorBookmark.FIRST, pos);
                        else
                            iterator.seek(CursorBookmark.CURRENT, delta);
                        if (!iteratorValid)
                        {
                            iteratorValid = true;
                            lastSeekPending = null;
                        }
                    }
                    catch(e:ItemPendingError)
                    {
                        lastSeekPending = new ListBaseSeekPending(CursorBookmark.FIRST, pos);
                        e.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler,
                                                        lastSeekPending));
                        // trace("IPE in UpdateDisplayList");
                        iteratorValid = false;
                        // don't do anything, we'll repaint when the data arrives
                    }
                    var bookmark:CursorBookmark = iterator.bookmark;
                     //if we scrolled more than the number of scrollable rows
                    clearIndicators();
                    clearVisibleData();
                    makeRowsAndColumns(0, 0, listContent.width, listContent.height, 0, 0);
                    iterator.seek(bookmark, 0);
                }
                else if (delta != 0)
                    scrollVertically(pos, Math.abs(delta), Boolean(delta > 0));
                // if variable rowheight, we have to recalibrate the scrollbars thumb size
                // on each scroll, otherwise you can't scroll down to a bunch of fat rows
                // at the bottom of a list.
                if (variableRowHeight)
                    configureScrollBars();

                drawRowBackgrounds();
            }
            else
            {
                delta = pos - _horizontalScrollPosition;
                
                super.scrollHandler(event);
                
                scrollHorizontally(pos, Math.abs(delta), Boolean(delta > 0));
            }

            // if needed, add a clip mask to the items in the last row of the list
            addClipMask(false);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  List scrolls horizontally by pixels.
     */
    override protected function scrollHorizontally(pos:int, deltaPos:int, scrollUp:Boolean):void
    {
        var n:int = listItems.length;
        var paddingLeft:Number = getStyle("paddingLeft");
        for (var i:int = 0; i < n; i++)
        {
            if (listItems[i].length)
                listItems[i][0].x = -pos + paddingLeft;
        }
    }

    /**
     *  Creates a new ListData instance and populates the fields based on
     *  the input data provider item.
     *  
     *  @param data The data provider item used to populate the ListData.
     *  @param uid The UID for the item.
     *  @param rowNum The index of the item in the data provider.
     *  
     *  @return A newly constructed ListData object.
     */
    protected function makeListData(data:Object, uid:String,
                                 rowNum:int):BaseListData
    {
        return new ListData(itemToLabel(data), itemToIcon(data), labelField, uid, this, rowNum);
    }
    
    /**
     *  @private
     */
    mx_internal function setupRendererFromData(item:IListItemRenderer, wrappedData:Object):void
    {
        var data:Object = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;
        
        if (item is IDropInListItemRenderer)
        {
            if (data != null)
                IDropInListItemRenderer(item).listData = makeListData(data, itemToUID(wrappedData), 0);
            else
                IDropInListItemRenderer(item).listData = null;
        }

        item.data = data;

        if (item is IInvalidating)
            IInvalidating(item).invalidateSize();

        UIComponentGlobals.layoutManager.validateClient(item, true);
    }    

    /**
     *  @private
     */
    override public function measureWidthOfItems(index:int = -1, count:int = 0):Number
    {
        if (count == 0)
            count = (collection) ? collection.length : 0;

        // if empty collection, don't measure anything
        if (collection && collection.length == 0)
            count = 0;

        var item:IListItemRenderer

        var w:Number = 0;

        var bookmark:CursorBookmark = (iterator) ? iterator.bookmark : null;
        if (index != -1 && iterator)
        {
            try
            {
                iterator.seek(CursorBookmark.FIRST, index);
            }
            catch (e:ItemPendingError)
            {
                // even the first item isn't paged in
                return 0;
            }

        }
        var rw:Number;
        var more:Boolean = iterator != null;
        for (var i:int = 0; i < count; i++)
        {
            var data:Object;
            if (more)
            {
                data = iterator.current;
                var factory:IFactory = getItemRendererFactory(data);
                item = measuringObjects[factory];
                if (!item)
                {
                    item = getMeasuringRenderer(data);
                }

                item.explicitWidth = NaN;   // gets set in measureHeightOfItems
                setupRendererFromData(item, data);

                rw = item.measuredWidth;
                w = Math.max(w, rw);
            }

            if (more)
            {
                try
                {
                    more = iterator.moveNext();
                }
                catch(e:ItemPendingError)
                {
                    // if we run out of data, assume all remaining rows are the size of the previous row
                    more = false;
                }
            }
        }

        if (iterator)
            iterator.seek(bookmark, 0);

        if (w == 0)
        {
            if (explicitWidth)
                return explicitWidth;
            else
                return DEFAULT_MEASURED_WIDTH;
        }

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingRight:Number = getStyle("paddingRight");
        w += paddingLeft + paddingRight

        return w;
    }

    /**
     *  @private
     */
    override public function measureHeightOfItems(index:int = -1, count:int = 0):Number
    {
        if (count == 0)
            count = (collection) ? collection.length : 0;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var ww:Number = 200;
        if (listContent.width)
            ww = listContent.width;

        var h:Number = 0;

        var bookmark:CursorBookmark = (iterator) ? iterator.bookmark : null;
        if (index != -1 && iterator)
            iterator.seek(CursorBookmark.FIRST, index);

        var rh:Number = rowHeight;
        var more:Boolean = iterator != null;
        for (var i:int = 0; i < count; i++)
        {
            var data:Object;
            if (more)
            {
                rh = rowHeight;
                data = iterator.current;

                // trace("calculate height " + index + " " + count);
                var item:IListItemRenderer = getMeasuringRenderer(data);
                item.explicitWidth = ww;

                setupRendererFromData(item, data);

                if (variableRowHeight)
                    rh = item.getExplicitOrMeasuredHeight() + paddingTop + paddingBottom;
            }
            h += rh;

            if (more)
            {
                try
                {
                    more = iterator.moveNext();
                }
                catch(e:ItemPendingError)
                {
                    // if we run out of data, assume all remaining rows are the size of the previous row
                    more = false;
                }
            }
        }

        if (iterator)
            iterator.seek(bookmark, 0);

        return h;
    }

    /**
     *  @private
     */
    override protected function mouseEventToItemRenderer(event:MouseEvent):IListItemRenderer
    {
        var r:IListItemRenderer = super.mouseEventToItemRenderer(event);
        return r == itemEditorInstance ? null : r;
    }

    [Inspectable(category="Data")]

    /**
     *  @private
     */
    mx_internal function getMeasuringRenderer(data:Object):IListItemRenderer
    {
        var item:IListItemRenderer;
        if (!measuringObjects)
            measuringObjects = new Dictionary(true);

        var factory:IFactory = getItemRendererFactory(data);
        item = measuringObjects[factory];

        if (!item)
        {
            item = createItemRenderer(data);
            item.owner = this;
            item.name = "hiddenItem";
            item.visible = false;
            item.styleName = listContent;
            listContent.addChild(DisplayObject(item));
            measuringObjects[factory] = item;
        }
        
        return item;
    }

    /**
     *  @private
     */
    mx_internal function purgeMeasuringRenderers():void
    {
        var item:IListItemRenderer;

        for each (item in measuringObjects)
            if (item.parent)
                item.parent.removeChild(DisplayObject(item));

        if (!measuringObjects)
            measuringObjects = new Dictionary(true);
    }

    /**
     *  @private
     */
    override public function set itemRenderer(value:IFactory):void
    {
        super.itemRenderer = value;
        purgeMeasuringRenderers();
    }

    /**
     *  Get the appropriate renderer, using the default renderer if none is specified.
     * 
     *  @param data The object from which the item renderer is created.
     *  
     *  @return The renderer.
     */
    override public function createItemRenderer(data:Object):IListItemRenderer
    {
        var factory:IFactory;

        // get the factory for the data
        factory = getItemRendererFactory(data);
        if (!factory)
        {
            if (data == null)
                factory = nullItemRenderer;
            if (!factory)
                factory = itemRenderer;
        }

        var renderer:IListItemRenderer;

        // if it is the default column factory, see if
        // the freeItemRenderers table has a free one
        if (factory == itemRenderer)
        {
            if (freeItemRenderers && freeItemRenderers.length)
            {
                renderer = freeItemRenderers.pop();
                delete freeItemRenderersByFactory[factory][renderer];
            }
        }
        else if (freeItemRenderersByFactory)
        {
            // other re-usable renderers are in the FactoryMap
            var d:Dictionary = freeItemRenderersByFactory[factory];
            if (d)
            {
                for (var p:* in d)
                {
                    renderer = IListItemRenderer(p);
                    delete d[p];
                    break;
                }
            }
        }

        if (!renderer)
        {
            renderer = factory.newInstance();
            renderer.styleName = this;
            factoryMap[renderer] = factory;
        }

        renderer.owner = this;
        return renderer;
    }

    /**
     *  @private
     * 
     *  Determines whether editing is prevented for a specific location 
     */
    private function editingTemporarilyPrevented(coord:Object):Boolean
    {
        // This code prevents possible race conditions when trying to
        // edit an item that is either being removed or replaced when
        // an effect is running. (E.g., after editing the last item of
        // a list, we can't have the editor pick up the old value of
        // the data, which might appear to persist while the effect is
        // running)
        if (runningDataEffect && coord)
        {
            var rowIndex:int = coord.rowIndex - verticalScrollPosition + offscreenExtraRowsTop;
            if ((rowIndex < 0) || rowIndex >= listItems.length)
                return false;
            var item:IListItemRenderer = listItems[rowIndex][0];
            if (item && (getRendererSemanticValue(item,"replaced") || 
                         getRendererSemanticValue(item,"removed")))
                return true;
        }
        
        return false;
    }
    
    /**
     *  @private
     */
    private function setEditedItemPosition(coord:Object):void
    {
        bEditedItemPositionChanged = true;
        _proposedEditedItemPosition = coord;
        invalidateDisplayList();
    }

    /**
     *  @private
     *  focus an item in the grid - harder than it looks
     */
    private function commitEditedItemPosition(coord:Object):void
    {
        if (!enabled || !editable)
            return;

        // just give focus back to the itemEditorInstance
        if (itemEditorInstance && coord &&
            itemEditorInstance is IFocusManagerComponent &&
            _editedItemPosition.rowIndex == coord.rowIndex)
        {
            IFocusManagerComponent(itemEditorInstance).setFocus();
            return;
        }

        // dispose of any existing editor, saving away its data first
        if (itemEditorInstance)
        {
            var reason:String;
            if (!coord)
                reason = ListEventReason.OTHER;
            else
                reason = ListEventReason.NEW_ROW;
            // trace("calling endEdit from commitEditedItemPosition", _editedItemPosition.rowIndex);
            if (!endEdit(reason) && reason != ListEventReason.OTHER)
                return;
        }

        // store the value
        _editedItemPosition = coord;

        // allow setting of undefined to dispose item editor
        if (!coord || dontEdit)
            return;

        var rowIndex:int = coord.rowIndex;
        var colIndex:int = coord.columnIndex;

        // trace("setEditedItemPosition ", coord.rowIndex, selectedIndex);

        if (selectedIndex != coord.rowIndex)
            commitSelectedIndex(coord.rowIndex);

        var actualLockedRows:int = lockedRowCount;

        // determine last *visible* row
        var lastRowIndex:int = verticalScrollPosition + listItems.length - offscreenExtraRowsTop - offscreenExtraRowsBottom - 1;
        var partialRow:int = (rowInfo[listItems.length - offscreenExtraRowsBottom - 1].y + 
            rowInfo[listItems.length - offscreenExtraRowsBottom - 1].height > listContent.height) ? 1 : 0;

        // actual row/column is the offset into listItems
        if (rowIndex > actualLockedRows)
        {
            // not a locked editable row make sure it is on screen
            if (rowIndex < verticalScrollPosition + actualLockedRows)
                verticalScrollPosition = rowIndex - actualLockedRows;
            else
            {
                // variable row heights means that we can't know how far to scroll sometimes so we loop
                // until we get it right
                while (rowIndex > lastRowIndex ||
                    // we're the last row, and we're partially visible, but we're not
                    // the top scrollable row already
                    (rowIndex == lastRowIndex && rowIndex > verticalScrollPosition + actualLockedRows &&
                        partialRow))
                {
                    if (verticalScrollPosition == maxVerticalScrollPosition)
                        break;
                    verticalScrollPosition = Math.min(verticalScrollPosition + (rowIndex > lastRowIndex ? rowIndex - lastRowIndex : partialRow), 
                                                      maxVerticalScrollPosition);
                    lastRowIndex = verticalScrollPosition + listItems.length - offscreenExtraRowsTop - offscreenExtraRowsBottom - 1;
                    partialRow = (rowInfo[listItems.length - offscreenExtraRowsBottom - 1].y + 
                        rowInfo[listItems.length - offscreenExtraRowsBottom - 1].height > listContent.height) ? 1 : 0;
                }
            }

            actualRowIndex = rowIndex - verticalScrollPosition;

        }
        else
        {
            if (rowIndex == actualLockedRows)
                verticalScrollPosition = 0;

            actualRowIndex = rowIndex;
        }

        var bm:EdgeMetrics = borderMetrics;

        actualColIndex = colIndex;

        // get the actual references for the column, row, and item
        var item:IListItemRenderer = listItems[actualRowIndex][actualColIndex];
        if (!item)
        {
            // assume that editing was cancelled
            commitEditedItemPosition(null);
            return;
        }
        if (!isItemEditable(item.data))
        {
            // assume that editing was cancelled
            commitEditedItemPosition(null);
            return;
        }

        var event:ListEvent =
            new ListEvent(ListEvent.ITEM_EDIT_BEGIN, false, true);
            // ITEM_EDIT events are cancelable
        event.rowIndex = _editedItemPosition.rowIndex;
        event.itemRenderer = item;
        dispatchEvent(event);

        lastEditedItemPosition = _editedItemPosition;

        // user may be trying to change the edited item
        if (bEditedItemPositionChanged)
        {
            bEditedItemPositionChanged = false;
            commitEditedItemPosition(_proposedEditedItemPosition);
            _proposedEditedItemPosition = undefined;
        }

        if (!itemEditorInstance)
        {
            // assume that editing was cancelled
            commitEditedItemPosition(null);
        }
    }

    /**
     *  Creates the item editor for the item renderer at the
     *  <code>editedItemPosition</code> using the editor
     *  specified by the <code>itemEditor</code> property.
     *
     *  <p>This method sets the editor instance as the 
     *  <code>itemEditorInstance</code> property.</p>
     *
     *  <p>You can call this method only from within the event listener
     *  for the <code>itemEditBegin</code> event. To create an editor 
     *  at other times, set the <code>editedItemPosition</code> property 
     *  to generate the <code>itemEditBegin</code> event.</p>
     *
     *  @param colIndex The column index. Flex sets the value of this property to 0 for a List control.
     *
     *  @param rowIndex The index in the data provider of the item to be 
     *  edited.
     */
    public function createItemEditor(colIndex:int, rowIndex:int):void
    {
        colIndex = 0;

        if (rowIndex > lockedRowCount)
            rowIndex -= verticalScrollPosition;

        var item:IListItemRenderer = listItems[rowIndex][colIndex];
        var rowData:ListRowInfo = rowInfo[rowIndex];
        // rendererIsEditor is part of the IListItemRenderer interface. It allows the item itself to do the editing
        if (!rendererIsEditor)
        {
            var dx:Number = 0;
            var dy:Number = -2;
            var dw:Number = 0;
            var dh:Number = 4;
            // if this isn't implemented, use an input control as editor
            if (!itemEditorInstance)
            {
                dx = editorXOffset;
                dy = editorYOffset;
                dw = editorWidthOffset;
                dh = editorHeightOffset;
                itemEditorInstance = itemEditor.newInstance();
                itemEditorInstance.owner = this;
                itemEditorInstance.styleName = this;
                listContent.addChild(DisplayObject(itemEditorInstance));
            }
            listContent.setChildIndex(DisplayObject(itemEditorInstance), listContent.numChildren - 1);
            // give it the right size, look and placement
            itemEditorInstance.visible = true;
            layoutEditor(item.x + dx, rowData.y + dy,
                             Math.min(item.width + dw, listContent.width - listContent.x - itemEditorInstance.x),
                             Math.min(rowData.height + dh, listContent.height - listContent.y - itemEditorInstance.y));
            DisplayObject(itemEditorInstance).addEventListener("focusOut", itemEditorFocusOutHandler);

        }
        else
        {
            // if the item is an itemEditorInstance, we'll use it
            itemEditorInstance = item;
        }

        // listen for keyStrokes on the itemEditorInstance (which lets the grid supervise for ESC/ENTER)
        DisplayObject(itemEditorInstance).addEventListener(KeyboardEvent.KEY_DOWN, editorKeyDownHandler);
        // we disappear on any mouse down outside the editor
        // use weak reference
        stage.addEventListener(MouseEvent.MOUSE_DOWN, editorMouseDownHandler, true, 0, true);

    }

    /**
     *  @private
     *  Determines the next item to navigate to by using the Tab key.
     *  If the item to be focused falls out of range (the end or beginning
     *  of the grid), moves the focus outside the grid.
     */
    private function findNextItemRenderer(shiftKey:Boolean):Boolean
    {
        if (!lastEditedItemPosition)
            return false;

        // some other thing like a collection change has changed the
        // position, so bail and wait for commit to reset the editor.
        if (_proposedEditedItemPosition !== undefined)
            return true;

        _editedItemPosition = lastEditedItemPosition;

        var rowIndex:int = _editedItemPosition.rowIndex;
        var columnIndex:int = _editedItemPosition.columnIndex;
        // modify direction with SHIFT (up or down)
        var newIndex:int = _editedItemPosition.rowIndex +
                           (shiftKey ? -1 : 1);
        // only move if we're within range
        if (newIndex < collection.length && newIndex >= 0)
            rowIndex = newIndex;
        else
        {
            // if we've fallen off the rows, we need to leave the grid. get rid of the editor
            setEditedItemPosition(null);
            // set focus back to the grid so default handler will move it to the next component
            losingFocus = true;
            setFocus();
            return false;
        }

        // send event to create the new one
        var listEvent:ListEvent =
            new ListEvent(ListEvent.ITEM_EDIT_BEGINNING, false, true);
            // ITEM_EDIT events are cancelable
        listEvent.rowIndex = rowIndex;
        listEvent.columnIndex = columnIndex;
        dispatchEvent(listEvent);

        return true;
    }

    /**
     *  Closes an item editor that is currently open on an item. 
     *  You typically call this method only from within the event listener 
     *  for the <code>itemEditEnd</code> event, after
     *  you call the <code>preventDefault()</code> method to prevent
     *  the default event listener from executing.
     */
    public function destroyItemEditor():void
    {
        // trace("destroyItemEditor");
        if (itemEditorInstance)
        {
            DisplayObject(itemEditorInstance).removeEventListener(KeyboardEvent.KEY_DOWN, editorKeyDownHandler);
            if (stage)
                stage.removeEventListener(MouseEvent.MOUSE_DOWN, editorMouseDownHandler, true);

            var event:ListEvent =
                new ListEvent(ListEvent.ITEM_FOCUS_OUT);
            event.rowIndex = _editedItemPosition.rowIndex;
            event.itemRenderer = editedItemRenderer;
            dispatchEvent(event);

            if (!rendererIsEditor)
            {
                if (itemEditorInstance && itemEditorInstance is UIComponent)
                    UIComponent(itemEditorInstance).drawFocus(false);

                // must call removeChild() so FocusManager.lastFocus becomes null
                listContent.removeChild(DisplayObject(itemEditorInstance));
            }
            itemEditorInstance = null;
            _editedItemPosition = null;
        }
    }

    /**
     *  Stops the editing of an item in the data provider.
     *  When the user finished editing an item, the control calls this method.  
     *  It dispatches the <code>itemEditEnd</code> event to start the process
     *  of copying the edited data from
     *  the <code>itemEditorInstance</code> to the data provider and hiding the 
     *  <code>itemEditorInstance</code>.
     *  
     *  @param reason A constant defining the reason for the event 
     *  (such as "CANCELLED", "NEW_ROW", or "OTHER"). 
     *  The value must be a member of the ListEventReason class.
     *  
     *  @return Returns <code>true</code> if <code>preventDefault()</code> is not called.
     *  Otherwise, <code>false</code>.
     *  
     *  @see mx.events.ListEventReason
     */
    protected function endEdit(reason:String):Boolean
    {
        // this happens if the renderer is removed asynchronously ususally with FDS
        if (!editedItemRenderer)
            return true;

        inEndEdit = true;

        var listEvent:ListEvent =
            new ListEvent(ListEvent.ITEM_EDIT_END, false, true);
            // ITEM_EDIT events are cancelable
        listEvent.rowIndex = editedItemPosition.rowIndex;
        listEvent.itemRenderer = editedItemRenderer;
        listEvent.reason = reason;
        dispatchEvent(listEvent);
        // set a flag to not open another edit session if the item editor is still up
        // this means somebody wants the old edit session to stay.
        dontEdit = itemEditorInstance != null;
        // trace("dontEdit", dontEdit);
        
        if (!dontEdit && reason == ListEventReason.CANCELLED)
        {
            losingFocus = true;
            setFocus();
        }
        
        inEndEdit = false;

        return !(listEvent.isDefaultPrevented())
    }

    /**
     *  Determines if the item renderer for a data provider item 
     *  is editable.
     *
     *  @param data The data provider item
     *  @return <code>true</code> if the item is editable
     */
    public function isItemEditable(data:Object):Boolean
    {
        if (!editable)
            return false;

        if (data == null)
            return false;

        return true;
    }

    /**
     *  @private
     */
    override protected function mouseDownHandler(event:MouseEvent):void
    {
        // trace(">>mouseDownHandler");
        var r:IListItemRenderer;
        var s:Sprite;

        r = mouseEventToItemRenderer(event);

        var isItemEditor:Boolean = itemRendererContains(itemEditorInstance, DisplayObject(event.target));

        // If it isn't an item, or an external item editor do default behavior
        if (!isItemEditor)
        {
            if (r && r.data)
            {
                var pos:Point = itemRendererToIndices(r);

                var bEndedEdit:Boolean = true;

                if (itemEditorInstance)
                {
                    bEndedEdit = endEdit(ListEventReason.NEW_ROW);
                }

                // if we didn't end edit session, don't do default behavior (call super)
                if (!bEndedEdit)
                    return;
            }
            else
            {
                // trace("end edit?");
                if (itemEditorInstance)
                    endEdit(ListEventReason.OTHER);
            }

            super.mouseDownHandler(event);
        }
        // trace("<<mouseDownHandler");
    }

    /**
     *  @private
     */
    override protected function mouseUpHandler(event:MouseEvent):void
    {
        var listEvent:ListEvent;
        var r:IListItemRenderer;
        var s:Sprite;
        var n:int;
        var i:int;

        r = mouseEventToItemRenderer(event);

        super.mouseUpHandler(event);

        if (r && r.data && r != itemEditorInstance)
        {
            var pos:Point = itemRendererToIndices(r);

            if (editable && !dontEdit)
            {
                listEvent = new ListEvent(ListEvent.ITEM_EDIT_BEGINNING, false, true);
                // ITEM_EDIT events are cancelable
                listEvent.rowIndex = pos.y;
                listEvent.columnIndex = 0;
                listEvent.itemRenderer = r;
                dispatchEvent(listEvent);
                //dispatchReplayableInteraction(listEvent);
            }
        }
    }

    /**
     *  @private
     *  when the grid gets focus, focus an item
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        // trace(">>DGFocusIn ", selectedIndex);

        if (event.target != this)
        {
            // trace("subcomponent got focus ignoring");
            // trace("<<DGFocusIn ");
            return;
        }

        if (losingFocus)
        {
            losingFocus = false;
            // trace("losing focus via tab");
            // trace("<<DGFocusIn ");
            return;
        }

        super.focusInHandler(event);

        if (editable && !isPressed) // don't do this if we're mouse focused
        {
            _editedItemPosition = lastEditedItemPosition;

            var foundOne:Boolean = editedItemPosition != null;

            // start somewhere
            if (!_editedItemPosition)
            {
                _editedItemPosition = { rowIndex: 0, columnIndex: 0 };
                foundOne = (listItems.length && listItems[0].length > 0);
            }

            if (foundOne)
            {
                // trace("setting focus", _editedItemPosition.columnIndex, _editedItemPosition.rowIndex);
                setEditedItemPosition(_editedItemPosition);
            }

            // if (foundOne)
            //  callLater(setEditedItemPosition, [ _editedItemPosition ]);
        }

        if (editable)
        {
            addEventListener(FocusEvent.KEY_FOCUS_CHANGE, keyFocusChangeHandler);
            addEventListener(MouseEvent.MOUSE_DOWN, mouseFocusChangeHandler);
        }
        // trace("<<DGFocusIn ");
    }

    /**
     *  @private
     *  when the grid loses focus, close the editor
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        // trace(">>DGFocusOut " + itemEditorInstance + " " + event.relatedObject, event.target);
        if (event.target == this)
        {
            super.focusOutHandler(event);
        }

        // we're done if item editor is losing focus back to grid.  Usually happens
        // when someone clicks out of the editor onto a new item.
        if (event.relatedObject == this && itemRendererContains(itemEditorInstance, DisplayObject(event.target)))
            return;

        // just leave if the cell renderer is losing focus to nothing while its editor exists. 
        // this happens when we make the cell renderer invisible as we put up the editor
        // if the renderer can have focus.
        if (event.relatedObject == null && itemRendererContains(editedItemRenderer, DisplayObject(event.target)))
            return;

        // just leave if item editor is losing focus to nothing.  Usually happens
        // when someone clicks out of the textfield
        if (event.relatedObject == null && itemRendererContains(itemEditorInstance, DisplayObject(event.target)))
            return;

        // however, if we're losing focus to anything other than the editor or the grid
        // hide the editor;
        if (itemEditorInstance && (!event.relatedObject || !itemRendererContains(itemEditorInstance, event.relatedObject)))
        {
            // trace("call edit item from focus out");
            endEdit(ListEventReason.OTHER);
            removeEventListener(FocusEvent.KEY_FOCUS_CHANGE, keyFocusChangeHandler);
            removeEventListener(MouseEvent.MOUSE_DOWN, mouseFocusChangeHandler);
        }
        // trace("<<DGFocusOut " + itemEditorInstance + " " + event.relatedObject);
    }

    /**
     *  @private
     */
    private function deactivateHandler(event:Event):void
    {
        // trace("List deactivating");
        // if stage losing activation, set focus to DG so when we get it back
        // we popup an editor again
        if (itemEditorInstance)
        {
            endEdit(ListEventReason.OTHER);
            losingFocus = true;
            setFocus();
        }

    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (itemEditorInstance)
            return;

        super.keyDownHandler(event);
    }

    /**
     *  @private
     */
    private function editorMouseDownHandler(event:MouseEvent):void
    {
        if (!itemRendererContains(itemEditorInstance, DisplayObject(event.target)))
            endEdit(ListEventReason.OTHER);
    }

    /**
     *  @private
     */
    private function editorKeyDownHandler(event:KeyboardEvent):void
    {
        // ESC just kills the editor, no new data
        if (event.keyCode == Keyboard.ESCAPE)
        {
            endEdit(ListEventReason.CANCELLED);
        }
        else if (event.ctrlKey && event.charCode == 46)
        {   // Check for Ctrl-.
            endEdit(ListEventReason.CANCELLED);
        }
        else if (event.charCode == Keyboard.ENTER && event.keyCode != 229)
        {
            // multiline editors can take the enter key.
            if (editorUsesEnterKey)
                return;

            // Enter edits the item, moves down a row
            // The 229 keyCode is for IME compatability. When entering an IME expression,
            // the enter key is down, but the keyCode is 229 instead of the enter key code.
            // Thanks to Yukari for this little trick...
            if (endEdit(ListEventReason.NEW_ROW))
                if (!dontEdit)
                    findNextEnterItemRenderer(event);
        }
    }

    /**
     *  @private
     *  find the next item down from the currently edited item, and focus it.
     */
    private function findNextEnterItemRenderer(event:KeyboardEvent):void
    {
        // some other thing like a collection change has changed the
        // position, so bail and wait for commit to reset the editor.
        if (_proposedEditedItemPosition !== undefined)
            return;

        _editedItemPosition = lastEditedItemPosition;

        var rowIndex:int = _editedItemPosition.rowIndex;
        var columnIndex:int = _editedItemPosition.columnIndex;
        // modify direction with SHIFT (up or down)
        var newIndex:int = _editedItemPosition.rowIndex +
                           (event.shiftKey ? -1 : 1);
        // only move if we're within range
        if (newIndex < collection.length && newIndex >= 0)
            rowIndex = newIndex;

        // send event to create the new one
        var listEvent:ListEvent =
            new ListEvent(ListEvent.ITEM_EDIT_BEGINNING, false, true);
            // ITEM_EDIT events are cancelable
        listEvent.rowIndex = rowIndex;
        listEvent.columnIndex = 0;
        dispatchEvent(listEvent);
    }

    /**
     *  @private
     *  This gets called when the focus is changed by using the mouse.
     */
    private function mouseFocusChangeHandler(event:MouseEvent):void
    {
        // trace("mouseFocus handled by " + this);

        if (itemEditorInstance &&
            !event.isDefaultPrevented() &&
            itemRendererContains(itemEditorInstance, DisplayObject(event.target)))
        {
            event.preventDefault();
        }
    }

    /**
     *  @private
     *  This gets called when the focus is changed by pressing the Tab key.
     */
    private function keyFocusChangeHandler(event:FocusEvent):void
    {
        // trace("tabHandled by " + this);

        if (event.keyCode == Keyboard.TAB &&
            ! event.isDefaultPrevented() &&
            findNextItemRenderer(event.shiftKey))
        {
            event.preventDefault();
        }
    }

    /**
     *  @private
     *  Hides the itemEditorInstance if it loses focus.
     */
    private function itemEditorFocusOutHandler(event:FocusEvent):void
    {
        // trace("itemEditorFocusOut " + event.relatedObject);
        if (event.relatedObject && contains(event.relatedObject))
            return;

        // ignore textfields losing focus on mousedowns
        if (!event.relatedObject)
            return;

        // trace("endEdit from itemEditorFocusOut");
        if (itemEditorInstance)
            endEdit(ListEventReason.OTHER);
    }

    /**
     *  @private
     */
    private function itemEditorItemEditBeginningHandler(event:ListEvent):void
    {
        // trace("itemEditorItemEditBeginningHandler");
        if (!event.isDefaultPrevented())
            setEditedItemPosition({columnIndex: event.columnIndex, rowIndex: event.rowIndex});
        else if (!itemEditorInstance)
        {
            _editedItemPosition = null;
            // return focus to the grid w/o selecting an item
            editable = false;
            setFocus();
            editable = true;
        }
    }

    /**
     *  @private
     *  create the editor for the item.
     */
    private function itemEditorItemEditBeginHandler(event:ListEvent):void
    {
        // trace("listening for deactivate");
        // weak reference to stage
        stage.addEventListener(Event.DEACTIVATE, deactivateHandler, false, 0, true);

        if (!event.isDefaultPrevented() && listItems[actualRowIndex][actualColIndex].data != null)
        {
            createItemEditor(event.columnIndex, event.rowIndex);

             // trace("beginEditHandler", event.rowIndex, editedItemRenderer.data);

            if (editedItemRenderer is IDropInListItemRenderer && itemEditorInstance is IDropInListItemRenderer)
                IDropInListItemRenderer(itemEditorInstance).listData = IDropInListItemRenderer(editedItemRenderer).listData;
            // if rendererIsEditor, don't apply the data as the data may have already changed in some way.
            // This can happen if clicking on a checkbox rendererIsEditor as the checkbox will try to change
            // its value as we try to stuff in an old value here.
            if (!rendererIsEditor)
                itemEditorInstance.data = editedItemRenderer.data;
    
            if (itemEditorInstance is IInvalidating)
                IInvalidating(itemEditorInstance).validateNow();

            if (itemEditorInstance is IIMESupport)
                IIMESupport(itemEditorInstance).imeMode = imeMode;

            var fm:IFocusManager = focusManager;
            // trace("setting focus to item editor");
            if (itemEditorInstance is IFocusManagerComponent)
                fm.setFocus(IFocusManagerComponent(itemEditorInstance));
            fm.defaultButtonEnabled = false;

            var event:ListEvent =
                new ListEvent(ListEvent.ITEM_FOCUS_IN);
            event.rowIndex = _editedItemPosition.rowIndex;
            event.itemRenderer = itemEditorInstance;
            dispatchEvent(event);
        }
    }

    /**
     *  @private
     *  save off the data and get rid of the editor
     */
    private function itemEditorItemEditEndHandler(event:ListEvent):void
    {
        if (!event.isDefaultPrevented())
        {
            var bChanged:Boolean = false;
            var bFieldChanged:Boolean = false;

            var newData:Object = itemEditorInstance[editorDataField];
            var data:Object = event.itemRenderer.data;

            // trace("itemEditEndHandler", event.rowIndex, data);

            if (data is String)
            {
                if (!(newData is String))
                    newData = newData.toString();
            }
            else if (data is uint)
            {
                if (!(newData is uint))
                    newData = uint(newData);
            }
            else if (data is int)
            {
                if (!(newData is int))
                    newData = int(newData);
            }
            else if (data is Number)
            {
                if (!(newData is int))
                    newData = Number(newData);
            }
            else    // assume some sort of object
            {
                bFieldChanged = true;
                try
                {
                    data[labelField] = newData;
                    if (!(data is IPropertyChangeNotifier))
                    {
                        // update the underlying collection if a data effect is running
                        if (actualCollection)
                            actualCollection.itemUpdated(data, labelField);
                        else
                            collection.itemUpdated(data, labelField);
                    }
                }
                catch(e:Error)
                {
                    trace("attempt to write to", labelField, "failed.  You may need a custom ITEM_EDIT_END handler");
                }
            }
            if (!bFieldChanged)
            {
                if (data !== newData)
                {
                    bChanged = true;
                    data = newData;
                }
                if (bChanged)
                {
                    // if running a data effect try to update the underlying collection
                    var editCollection:IList = actualCollection ? actualCollection as IList : collection as IList;
                    // if editCollection is null here, underlying collection doesn't implement IList
                    if (editCollection)
                        IList(editCollection).setItemAt(data, event.rowIndex);
                    else
                        trace("attempt to update collection failed.  You may need a custom ITEM_EDIT_END handler");
                }
            }
            if (event.itemRenderer is IDropInListItemRenderer)
            {
                var listData:BaseListData = BaseListData(IDropInListItemRenderer(event.itemRenderer).listData);
                listData.label = itemToLabel(data);
                IDropInListItemRenderer(event.itemRenderer).listData = listData;
            }
            // re-key the entry for this item renderer in visibleData
            delete visibleData[itemToUID(event.itemRenderer.data)];
            event.itemRenderer.data = data;
            visibleData[itemToUID(data)] = event.itemRenderer;
        }
        else
        {
            if (event.reason != ListEventReason.OTHER)
            {
                if (itemEditorInstance && _editedItemPosition)
                {
                    // edit session is continued so restore focus and selection
                    if (selectedIndex != _editedItemPosition.rowIndex)
                        selectedIndex = _editedItemPosition.rowIndex;
                    var fm:IFocusManager = focusManager;
                    // trace("setting focus to item editor", selectedIndex);
                    if (itemEditorInstance is IFocusManagerComponent)
                        fm.setFocus(IFocusManagerComponent(itemEditorInstance));
                }
            }
        }

        if (event.reason == ListEventReason.OTHER || !event.isDefaultPrevented())
        {
            destroyItemEditor();
        }
    }

    /**
     *  @private
     */
    override protected function drawHighlightIndicator(indicator:Sprite, x:Number, y:Number, width:Number, height:Number, color:uint, itemRenderer:IListItemRenderer):void
    {
        super.drawHighlightIndicator(indicator, 0, y, unscaledWidth - viewMetrics.left - viewMetrics.right, height, color, itemRenderer);
    }

    /**
     *  @private
     */
    override protected function drawCaretIndicator(indicator:Sprite, x:Number, y:Number, width:Number, height:Number, color:uint, itemRenderer:IListItemRenderer):void
    {
        super.drawCaretIndicator(indicator, 0, y, unscaledWidth - viewMetrics.left - viewMetrics.right, height, color, itemRenderer);
    }

    /**
     *  @private
     */
    override protected function drawSelectionIndicator(indicator:Sprite, x:Number, y:Number, width:Number, height:Number, color:uint, itemRenderer:IListItemRenderer):void
    {
        super.drawSelectionIndicator(indicator, 0, y, unscaledWidth - viewMetrics.left - viewMetrics.right, height, color, itemRenderer);
    }

    /**
     *  @private
     */
    override protected function mouseWheelHandler(event:MouseEvent):void
    {
        if (itemEditorInstance)
            endEdit(ListEventReason.OTHER);

        super.mouseWheelHandler(event);
    }

    /**
     *  @private
     *  Catches any events from the model. Optimized for editing one item.
     *  @param eventObj
     */
    override protected function collectionChangeHandler(event:Event):void
    {
        if (event is CollectionEvent)
        {
            var ceEvent:CollectionEvent = CollectionEvent(event)
            // if we get a remove while editing it may invalidate the edit position
            if (ceEvent.kind == CollectionEventKind.REMOVE)
            {
                if (editedItemPosition)
                {
                    //trace("editedItemPosition", editedItemPosition.rowIndex);
                    if (collection.length == 0)
                    {
                        if (itemEditorInstance)
                            endEdit(ListEventReason.CANCELLED);
                        setEditedItemPosition(null); // nothing left to edit
                    }
                    else if (ceEvent.location <= editedItemPosition.rowIndex)
                    {
                        if (inEndEdit)
                            _editedItemPosition = { columnIndex : editedItemPosition.columnIndex, 
                                                rowIndex : Math.max(0, editedItemPosition.rowIndex - ceEvent.items.length)};
                        else
                            setEditedItemPosition({ columnIndex : editedItemPosition.columnIndex, 
                                                rowIndex : Math.max(0, editedItemPosition.rowIndex - ceEvent.items.length)});
                    }
                }
            }
        }

        super.collectionChangeHandler(event);

    }

    /**
     *  @private
     */    
    mx_internal function callSetupRendererFromData(item:IListItemRenderer, data:Object):void
    {
        setupRendererFromData(item, data);
    }
    
    /**
     *  @private
     */    
    mx_internal function callMakeListData(data:Object, uid:String,
                                 rowNum:int):BaseListData
    {
        return makeListData(data, uid, rowNum);
    }

}

}
