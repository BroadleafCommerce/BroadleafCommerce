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
import flash.display.GradientType;
import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Matrix;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.ui.Keyboard;
import flash.utils.describeType;
import flash.utils.Dictionary;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.ItemResponder;
import mx.collections.Sort;
import mx.collections.SortField;
import mx.collections.errors.ItemPendingError;
import mx.controls.dataGridClasses.DataGridBase;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.dataGridClasses.DataGridDragProxy;
import mx.controls.dataGridClasses.DataGridHeader;
import mx.controls.dataGridClasses.DataGridItemRenderer;
import mx.controls.dataGridClasses.DataGridListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBaseContentHolder;
import mx.controls.listClasses.ListBaseSeekPending;
import mx.controls.listClasses.ListRowInfo;
import mx.controls.scrollClasses.ScrollBar;
import mx.core.ContextualClassFactory;
import mx.core.EdgeMetrics;
import mx.core.EventPriority;
import mx.core.FlexShape;
import mx.core.FlexSprite;
import mx.core.FlexVersion;
import mx.core.IChildList;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.IIMESupport;
import mx.core.IInvalidating;
import mx.core.IPropertyChangeNotifier;
import mx.core.IRawChildrenContainer;
import mx.core.IRectangularBorder;
import mx.core.IUIComponent;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.ListEvent;
import mx.events.DataGridEvent;
import mx.events.DataGridEventReason;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.managers.CursorManager;
import mx.managers.CursorManagerPriority;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.skins.halo.ListDropIndicator;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleManager;
import mx.utils.ObjectUtil;
import mx.managers.ISystemManager;
import mx.core.IFlexModuleFactory;
import mx.utils.StringUtil;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the user releases the mouse button while over an item 
 *  renderer, tabs to the DataGrid control or within the DataGrid control, 
 *  or in any other way attempts to edit an item.
 *
 *  @eventType mx.events.DataGridEvent.ITEM_EDIT_BEGINNING
 */
[Event(name="itemEditBeginning", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when the <code>editedItemPosition</code> property has been set
 *  and the item can be edited.
 *
 *  @eventType mx.events.DataGridEvent.ITEM_EDIT_BEGIN
 */
[Event(name="itemEditBegin", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when an item editing session ends for any reason.
 *
 *  @eventType mx.events.DataGridEvent.ITEM_EDIT_END
 */
[Event(name="itemEditEnd", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when an item renderer gets focus, which can occur if the user
 *  clicks on an item in the DataGrid control or navigates to the item using
 *  a keyboard.  Only dispatched if the item is editable.
 *
 *  @eventType mx.events.DataGridEvent.ITEM_FOCUS_IN
 */
[Event(name="itemFocusIn", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when an item renderer loses focus, which can occur if the user
 *  clicks another item in the DataGrid control or clicks outside the control,
 *  or uses the keyboard to navigate to another item in the DataGrid control
 *  or outside the control.
 *  Only dispatched if the item is editable.
 *
 *  @eventType mx.events.DataGridEvent.ITEM_FOCUS_OUT
 */
[Event(name="itemFocusOut", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when a user changes the width of a column, indicating that the 
 *  amount of data displayed in that column may have changed.
 *  If <code>horizontalScrollPolicy</code> is <code>"off"</code>, other
 *  columns shrink or expand to compensate for the columns' resizing,
 *  and they also dispatch this event.
 *
 *  @eventType mx.events.DataGridEvent.COLUMN_STRETCH
 */
[Event(name="columnStretch", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when the user releases the mouse button on a column header
 *  to request the control to sort
 *  the grid contents based on the contents of the column.
 *  Only dispatched if the column is sortable and the data provider supports 
 *  sorting. The DataGrid control has a default handler for this event that implements
 *  a single-column sort.  Multiple-column sort can be implemented by calling the 
 *  <code>preventDefault()</code> method to prevent the single column sort and setting 
 *  the <code>sort</code> property of the data provider.
 * <p>
 * <b>Note</b>: The sort arrows are defined by the default event handler for
 * the headerRelease event. If you call the <code>preventDefault()</code> method
 * in your event handler, the arrows are not drawn.
 * </p>
 *
 *  @eventType mx.events.DataGridEvent.HEADER_RELEASE
 */
[Event(name="headerRelease", type="mx.events.DataGridEvent")]

/**
 *  Dispatched when the user releases the mouse button on a column header after 
 *  having dragged the column to a new location resulting in shifting the column
 *  to a new index.
 *
 *  @eventType mx.events.IndexChangedEvent.HEADER_SHIFT
 */
[Event(name="headerShift", type="mx.events.IndexChangedEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/IconColorStyles.as"

/**
 *  A flag that indicates whether to show vertical grid lines between
 *  the columns.
 *  If <code>true</code>, shows vertical grid lines.
 *  If <code>false</code>, hides vertical grid lines.
 *  @default true
 */
[Style(name="verticalGridLines", type="Boolean", inherit="no")]

/**
 *  A flag that indicates whether to show horizontal grid lines between
 *  the rows.
 *  If <code>true</code>, shows horizontal grid lines.
 *  If <code>false</code>, hides horizontal grid lines.
 *  @default false
 */
[Style(name="horizontalGridLines", type="Boolean", inherit="no")]

/**
 *  The color of the vertical grid lines.
 *  @default 0x666666
 */
[Style(name="verticalGridLineColor", type="uint", format="Color", inherit="yes")]

/**
 *  The color of the horizontal grid lines.
  */
[Style(name="horizontalGridLineColor", type="uint", format="Color", inherit="yes")]

/**
 *  An array of two colors used to draw the header background gradient.
 *  The first color is the top color.
 *  The second color is the bottom color.
 *  @default [0xFFFFFF, 0xE6E6E6]
 */
[Style(name="headerColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  The color of the row background when the user rolls over the row.
 *  @default 0xE3FFD6
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  The color of the background for the row when the user selects 
 *  an item renderer in the row.
 *  @default 0xCDFFC1
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  The name of a CSS style declaration for controlling other aspects of
 *  the appearance of the column headers.
 *  @default "dataGridStyles"
 */
[Style(name="headerStyleName", type="String", inherit="no")]

/**
 *  The class to use as the skin for a column that is being resized.
 *  @default mx.skins.halo.DataGridColumnResizeSkin
 */
[Style(name="columnResizeSkin", type="Class", inherit="no")]


/**
 *  The class to use as the skin that defines the appearance of the  
 *  background of the column headers in a DataGrid control.
 *  @default mx.skins.halo.DataGridHeaderSeparator
 */
[Style(name="headerBackgroundSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin that defines the appearance of the 
 *  separator between column headers in a DataGrid control.
 *  @default mx.skins.halo.DataGridHeaderSeparator
 */
[Style(name="headerSeparatorSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin that defines the appearance of the 
 *  separator between rows in a DataGrid control. 
 *  By default, the DataGrid control uses the 
 *  <code>drawHorizontalLine()</code> and <code>drawVerticalLine()</code> methods
 *  to draw the separators.
 *
 *  @default undefined
 */
[Style(name="horizontalSeparatorSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin that defines the appearance of the 
 *  separator between the locked and unlocked rows in a DataGrid control.
 *  By default, the DataGrid control uses the 
 *  <code>drawHorizontalLine()</code> and <code>drawVerticalLine()</code> methods
 *  to draw the separators.
 *
 *  @default undefined
 */
[Style(name="horizontalLockedSeparatorSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin that defines the appearance of the 
 *  separators between columns in a DataGrid control.
 *  By default, the DataGrid control uses the 
 *  <code>drawHorizontalLine()</code> and <code>drawVerticalLine()</code> methods
 *  to draw the separators.
 *
 *  @default undefined
 */
[Style(name="verticalSeparatorSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin that defines the appearance of the 
 *  separator between the locked and unlocked columns in a DataGrid control.
 *  By default, the DataGrid control uses the 
 *  <code>drawHorizontalLine()</code> and <code>drawVerticalLine()</code> methods
 *  to draw the separators.
 *
 *  @default undefined
 */
[Style(name="verticalLockedSeparatorSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin for the arrow that indicates the column sort 
 *  direction.
 *  @default mx.skins.halo.DataGridSortArrow
 */
[Style(name="sortArrowSkin", type="Class", inherit="no")]

/**
 *  The class to use as the skin for the cursor that indicates that a column
 *  can be resized.
 *  The default value is the "cursorStretch" symbol from the Assets.swf file.
 */
[Style(name="stretchCursor", type="Class", inherit="no")]

/**
 *  The class to use as the skin that indicates that 
 *  a column can be dropped in the current location.
 *
 *  @default mx.skins.halo.DataGridColumnDropIndicator
 */
[Style(name="columnDropIndicatorSkin", type="Class", inherit="no")]

/**
 *  The name of a CSS style declaration for controlling aspects of the
 *  appearance of column when the user is dragging it to another location.
 *
 *  @default "headerDragProxyStyle"
 */
[Style(name="headerDragProxyStyleName", type="String", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="columnCount", kind="property")]
[Exclude(name="iconField", kind="property")]
[Exclude(name="iconFunction", kind="property")]
[Exclude(name="labelField", kind="property")]
[Exclude(name="offscreenExtraRowsOrColumns", kind="property")]
[Exclude(name="offscreenExtraRows", kind="property")]
[Exclude(name="offscreenExtraRowsTop", kind="property")]
[Exclude(name="offscreenExtraRowsBottom", kind="property")]
[Exclude(name="offscreenExtraColumns", kind="property")]
[Exclude(name="offscreenExtraColumnsLeft", kind="property")]
[Exclude(name="offscreenExtraColumnsRight", kind="property")]
[Exclude(name="offscreenExtraRowsOrColumnsChanged", kind="property")]
[Exclude(name="showDataTips", kind="property")]
[Exclude(name="cornerRadius", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.DataGridAccImpl")]

[DataBindingInfo("acceptedTypes", "{ dataProvider: &quot;String&quot; }")]

[DefaultBindingProperty(source="selectedItem", destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("DataGrid.png")]

[RequiresDataBinding(true)]

/**
 *  The <code>DataGrid</code> control is like a List except that it can 
 *  show more than one column of data making it suited for showing 
 *  objects with multiple properties.
 *  <p>
 *  The DataGrid control provides the following features:
 *  <ul>
 *  <li>Columns of different widths or identical fixed widths</li>
 *  <li>Columns that the user can resize at runtime </li>
 *  <li>Columns that the user can reorder at runtime </li>
 *  <li>Optional customizable column headers</li>
 *  <li>Ability to use a custom item renderer for any column to display 
 *      data 
 *  other than text</li>
 *  <li>Support for sorting the data by clicking on a column</li>
 *  </ul>
 *  </p>
 *  The DataGrid control is intended for viewing data, and not as a
 *  layout tool like an HTML table.
 *  The mx.containers package provides those layout tools.
 *  
 *  <p>The DataGrid control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>If the columns are empty, the default width is 300 
 *               pixels. If the columns contain information but define 
 *               no explicit widths, the default width is 100 pixels 
 *               per column. The DataGrid width is sized to fit the 
 *               width of all columns, if possible. 
 *               The default number of displayed rows, including the 
 *               header is 7, and each row, by default, is 20 pixels 
 *               high.
 *           </td>
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
 *  <p>
 *  The <code>&lt;mx:DataGrid&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass, except for <code>labelField</code>, 
 *  <code>iconField</code>, and <code>iconFunction</code>, and adds the 
 *  following tag attributes:
 *  </p>
 *  <pre>
 *  &lt;mx:DataGrid
 *    <b>Properties</b>
 *    columns="<i>From dataProvider</i>"
 *    draggableColumns="true|false"
 *    editable="false|true"
 *    editedItemPosition="<code>null</code>"
 *    horizontalScrollPosition="null"
 *    imeMode="null"
 *    itemEditorInstance="null"
 *    minColumnWidth="<code>NaN</code>"
 *    resizableColumns="true|false"
 *    sortableColumns="true|false"
 *    
 *    <b>Styles</b>
 *    backgroundDisabledColor="0xEFEEEF"
 *    columnDropIndicatorSkin="DataGridColumnDropIndicator"
 *    columnResizeSkin="DataGridColumnResizeSkin"
 *    disabledIconColor="0x999999"
 *    headerColors="[#FFFFFF, #E6E6E6]"
 *    headerDragProxyStyleName="headerDragProxyStyle"
 *    headerSeparatorSkin="DataGridHeaderSeparator"
 *    headerStyleName="dataGridStyles"
 *    horizontalGridLineColor="<i>No default</i>"
 *    horizontalGridLines="false|true"
 *    horizontalLockedSeparatorSkin="undefined"
 *    horizontalSeparatorSkin="undefined"
 *    iconColor="0x111111"
 *    rollOverColor="#E3FFD6"
 *    selectionColor="#CDFFC1"
 *    sortArrowSkin="DataGridSortArrow"
 *    stretchCursor="<i>"cursorStretch" symbol from the Assets.swf file</i>"
 *    verticalGridLineColor="#666666"
 *    verticalGridLines="false|true"
 *    verticalLockedSeparatorSkin="undefined"
 *    verticalSeparatorSkin="undefined"
 *     
 *    <b>Events</b>
 *    columnStretch="<i>No default</i>"
 *    headerRelease="<i>No default</i>"
 *    headerShift="<i>No default</i>"
 *    itemEditBegin="<i>No default</i>"
 *    itemEditBeginning="<i>No default</i>" 
 *    itemEditEnd="<i>No default</i>"
 *    itemFocusIn="<i>No default</i>"
 *    itemFocusOut="<i>No default</i>"
 *  /&gt;
 *   
 *  <b>The following DataGrid code sample specifies the column order:</b>
 *  &lt;mx:DataGrid&gt;
 *    &lt;mx:dataProvider&gt;
 *        &lt;mx:Object Artist="Pavement" Price="11.99"
 *          Album="Slanted and Enchanted"/&gt;
 *        &lt;mx:Object Artist="Pavement"
 *          Album="Brighten the Corners" Price="11.99"/&gt;
 *    &lt;/mx:dataProvider&gt;
 *    &lt;mx:columns&gt;
 *        &lt;mx:DataGridColumn dataField="Album"/&gt;
 *        &lt;mx:DataGridColumn dataField="Price"/&gt;
 *    &lt;/mx:columns&gt;
 *  &lt;/mx:DataGrid&gt;
 *  </pre>
 *  </p>
 *
 *  @see mx.controls.dataGridClasses.DataGridItemRenderer
 *  @see mx.controls.dataGridClasses.DataGridColumn
 *  @see mx.controls.dataGridClasses.DataGridDragProxy
 *  @see mx.events.DataGridEvent
 *
 *  @includeExample examples/SimpleDataGrid.mxml
 */
public class DataGrid extends DataGridBase implements IIMESupport
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by DataGridAccImpl.
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
    public function DataGrid()
    {
        super();

        _columns = [];

        // pick a default row height
        setRowHeight(20);

        // Register default handlers for item editing and sorting events.

        addEventListener(DataGridEvent.ITEM_EDIT_BEGINNING,
                        itemEditorItemEditBeginningHandler,
                        false, EventPriority.DEFAULT_HANDLER);

        addEventListener(DataGridEvent.ITEM_EDIT_BEGIN,
                         itemEditorItemEditBeginHandler,
                         false, EventPriority.DEFAULT_HANDLER);

        addEventListener(DataGridEvent.ITEM_EDIT_END,
                         itemEditorItemEditEndHandler,
                         false, EventPriority.DEFAULT_HANDLER);

        addEventListener(DataGridEvent.HEADER_RELEASE,
                         headerReleaseHandler,
                         false, EventPriority.DEFAULT_HANDLER);
                         
        addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);                         
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    [Inspectable(environment="none")]

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
     *  <p>The <code>DataGridColumn.itemEditor</code> property defines the
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

        return actualContentHolder.listItems[actualRowIndex][actualColIndex];
    }

    /**
     *  @private
     *  true if we want to skip updating the headers during adjustListContent
     */
    private var skipHeaderUpdate:Boolean = false;

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

    /**
     *  @private
     *  true if we've disabled updates in the collection
     */
    private var collectionUpdatesDisabled:Boolean = false;

    /**
     *  @private
     *  The index of the column being sorted.
     */
    mx_internal var sortIndex:int = -1;

    /**
     *  @private
     *  The column being sorted.
     */
    private var sortColumn:DataGridColumn;

    /**
     *  @private
     *  The direction of the sort
     */
    mx_internal var sortDirection:String;

    /**
     *  @private
     *  The index of the last column being sorted on.
     */
    mx_internal var lastSortIndex:int = -1;

    /**
     *  @private
     */
    private var lastItemDown:IListItemRenderer;

    /**
     *  @private
     */
    private var displayWidth:Number;

    /**
     *  @private
     */
    private var lockedColumnWidth:Number = 0;

    /**
     *  @private
     *  The column that is being moved.
     */
    mx_internal var movingColumn:DataGridColumn;

    /**
     *  @private
     *  The column that is being resized.
     */
    mx_internal var resizingColumn:DataGridColumn;

    /**
     *  @private
     *  Columns with visible="true"
     */
    private var displayableColumns:Array;

    /**
     *  @private
     *  Whether we have auto-generated the set of columns
     *  Defaults to true so we'll run the auto-generation at init time if needed
     */
    private var generatedColumns:Boolean = true;

    // last known position of item editor instance
    private var actualRowIndex:int;
    private var actualColIndex:int;
    private var actualContentHolder:ListBaseContentHolder;

    /**
     *  @private
     *  Flag to indicate whether sorting is manual or programmatic.  If it's
     *  not manual, we try to draw the sort arrow on the right column header.
     */
    private var manualSort:Boolean;


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
     *  The baselinePosition of a DataGrid is calculated
     *  for its first column header.
     *  If the headers aren't shown, it is calculated as for List.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            var top:Number = 0;
    
            if (border && border is IRectangularBorder)
                top = IRectangularBorder(border).borderMetrics.top;
    
            return top + measureText(" ").ascent;
        }
                    
        if (!validateBaselinePosition())
            return NaN;

        if (!showHeaders)
            return super.baselinePosition;
        
        var header0:IUIComponent = DataGridHeader(header).rendererArray[0];
        if (!header0)
            return super.baselinePosition;
            
        return header.y + header0.y + header0.baselinePosition;
    }

    /**
     *  @private
     *  Number of columns that can be displayed.
     *  Some may be offscreen depending on horizontalScrollPolicy
     *  and the width of the DataGrid.
     */
    override public function get columnCount():int
    {
        if (_columns)
            return _columns.length;
        else
            return 0;
    }

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
        
        if (header)
            header.enabled = value;

        if (itemEditorInstance)
            endEdit(DataGridEventReason.OTHER);

        invalidateDisplayList();
    }

    //----------------------------------
    //  headerHeight
    //----------------------------------

    /**
     *  @private
     */
    override public function set headerHeight(value:Number):void
    {
        super.headerHeight = value;
        _originalHeaderHeight = isNaN(value) ? 22 : value;
        _originalExplicitHeaderHeight = !isNaN(value);        
    }

    //----------------------------------
    //  horizontalScrollPosition
    //----------------------------------

    /**
     *  The offset into the content from the left edge. 
     *  This can be a pixel offset in some subclasses or some other metric 
     *  like the number of columns in a DataGrid control. 
     *
     *  The DataGrid scrolls by columns so the value of the 
     *  <code>horizontalScrollPosition</code> property is always
     *  in the range of 0 to the index of the columns
     *  that will make the last column visible.  This is different from the
     *  List control that scrolls by pixels.  The DataGrid control always aligns the left edge
     *  of a column with the left edge of the DataGrid control.
     */
    override public function set horizontalScrollPosition(value:Number):void
    {
        // if not init or no data;
        if (!initialized || listItems.length == 0)
        {
            super.horizontalScrollPosition = value;
            return;
        }

        var oldValue:int = super.horizontalScrollPosition;
        super.horizontalScrollPosition = value;

        // columns have variable width so we need to recalc scroll parms
        scrollAreaChanged = true;

        columnsInvalid = true;
        calculateColumnSizes();

        // we are going to get a full repaint so don't repaint now
        if (itemsSizeChanged)
            return;

        if (oldValue != value)
        {
            removeClipMask();

            var bookmark:CursorBookmark;

            if (iterator)
                bookmark = iterator.bookmark;

            clearIndicators();
            clearVisibleData();
            //if we scrolled more than the number of scrollable columns
            makeRowsAndColumns(0, 0, listContent.width, listContent.height, 0, 0);
            if (lockedRowCount)
            {           
                var cursorPos:CursorBookmark;
                cursorPos = lockedRowContent.iterator.bookmark;
                makeRows(lockedRowContent, 0, 0, unscaledWidth, unscaledHeight, 0, 0, true, lockedRowCount);
                if (iteratorValid)
                    lockedRowContent.iterator.seek(cursorPos, 0);
            }

            if (headerVisible && header)
            {
                header.visibleColumns = visibleColumns;
                header.headerItemsChanged = true;
                header.invalidateSize();
                header.validateNow();
            }

            if (iterator && bookmark)
                iterator.seek(bookmark, 0);

            invalidateDisplayList();

            addClipMask(false);
        }
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    /**
     *  @private
     *  Accomodates ScrollPolicy.AUTO.
     *  Makes sure column widths stay in synch.
     *
     *  @param policy on, off, or auto
     */
    override public function set horizontalScrollPolicy(value:String):void
    {
        super.horizontalScrollPolicy = value;
        columnsInvalid = true;
        itemsSizeChanged = true;
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    override public function set verticalScrollPosition(value:Number):void
    {
        skipHeaderUpdate = true;

        var oldValue:Number = super.verticalScrollPosition;
        super.verticalScrollPosition = value;
        if (oldValue != value)
        {
            if (lockedColumnContent)
                drawRowGraphics(lockedColumnContent)
        }
        skipHeaderUpdate = false;
    }

    /**
     *  @private
     *  
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!header)
        {
            header = new headerClass();
            header.styleName = this;
            addChild(header);
        }
    }

    //----------------------------------
    //  imeMode
    //----------------------------------

    /**
     *  @private
     */
    private var _imeMode:String = null;

    [Inspectable(environment="none")]

    /**
     *  Specifies the IME (input method editor) mode.
     *  The IME enables users to enter text in Chinese, Japanese, and Korean.
     *  Flex sets the specified IME mode when the control gets the focus,
     *  and sets it back to the previous value when the control loses the focus.
     *
     * <p>The flash.system.IMEConversionMode class defines constants for the
     *  valid values for this property.
     *  You can also specify <code>null</code> to specify no IME.</p>
     *
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

    //----------------------------------
    //  itemRenderer
    //----------------------------------
    
    /**
     *  @private
     * 
     *  Defer creations of the class factory
     *  to give a chance for the moduleFactory to be set.
     */
    override public function get itemRenderer():IFactory
    {
        if (super.itemRenderer == null)
        {       
            var fontName:String =
                StringUtil.trimArrayElements(getStyle("fontFamily"), ",");
            var fontWeight:String = getStyle("fontWeight");
            var fontStyle:String = getStyle("fontStyle");
            var bold:Boolean = (fontWeight == "bold");
            var italic:Boolean = (fontStyle == "italic");
            
            var flexModuleFactory:IFlexModuleFactory =
                getFontContext(fontName, bold, italic);

            super.itemRenderer = new ContextualClassFactory(
                DataGridItemRenderer, flexModuleFactory);
        }
        
        return super.itemRenderer;
    }
    
    //----------------------------------
    //  minColumnWidth
    //----------------------------------

    /**
     *  @private
     */
    private var _minColumnWidth:Number;

    /**
     *  @private
     */
    private var minColumnWidthInvalid:Boolean = false;

    [Inspectable(defaultValue="NaN")]

    /**
     *  The minimum width of the columns, in pixels.  If not NaN,
     *  the DataGrid control applies this value as the minimum width for
     *  all columns.  Otherwise, individual columns can have
     *  their own minimum widths.
     *  
     *  @default NaN
     */
    public function get minColumnWidth():Number
    {
        return _minColumnWidth;
    }

    /**
     *  @private
     */
    public function set minColumnWidth(value:Number):void
    {
        _minColumnWidth = value;
        minColumnWidthInvalid = true;
        itemsSizeChanged = true;
        columnsInvalid = true;
        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  columns
    //----------------------------------

    /**
     *  @private
     */
    private var _columns:Array; // the array of our DataGridColumns

    [Bindable("columnsChanged")]
    [Inspectable(arrayType="mx.controls.dataGridClasses.DataGridColumn")]

    /**
     *  An array of DataGridColumn objects, one for each column that
     *  can be displayed.  If not explicitly set, the DataGrid control 
     *  attempts to examine the first data provider item to determine the
     *  set of properties and display those properties in alphabetic
     *  order.
     *
     *  <p>If you want to change the set of columns, you must get this array,
     *  make modifications to the columns and order of columns in the array,
     *  and then assign the new array to the columns property.  This is because
     *  the DataGrid control returned a new copy of the array of columns and therefore
     *  did not notice the changes.</p>
     */
    override public function get columns():Array
    {
        return _columns.slice(0);
    }

    /**
     *  @private
     */
    override public function set columns(value:Array):void
    {
        var n:int;
        var i:int;

        n = _columns.length;
        for (i = 0; i < n; i++)
        {
            columnRendererChanged(_columns[i]);
        }
        
        freeItemRenderersTable = new Dictionary(false);
        columnMap = {};

        _columns = value.slice(0);
        columnsInvalid = true;
        generatedColumns = false;

        n = value.length;
        for (i = 0; i < n; i++)
        {
            var column:DataGridColumn = _columns[i];
            column.owner = this;
            column.colNum = i;
        }

        updateSortIndexAndDirection();

        itemsSizeChanged = true;
        invalidateDisplayList();
        dispatchEvent(new Event("columnsChanged"));
    }

    //----------------------------------
    //  draggableColumns
    //----------------------------------

    /**
     *  @private
     *  Storage for the draggableColumns property.
     */
    private var _draggableColumns:Boolean = true;

    [Inspectable(defaultValue="true")]

    /**
     *  A flag that indicates whether the user is allowed to reorder columns.
     *  If <code>true</code>, the user can reorder the columns
     *  of the DataGrid control by dragging the header cells.
     *
     *  @default true
     */
    public function get draggableColumns():Boolean
    {
        return _draggableColumns;
    }
    
    /**
     *  @private
     */
    public function set draggableColumns(value:Boolean):void
    {
        _draggableColumns = value;
    }

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
     *  <p>You can turn off editing for individual columns of the
     *  DataGrid control using the <code>DataGridColumn.editable</code> property,
     *  or by handling the <code>itemEditBeginning</code> and
     *  <code>itemEditBegin</code> events</p>
     *
     *  @default false
     */
    public var editable:Boolean = false;

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

    /**
     *  @private
     */
    private var itemEditorPositionChanged:Boolean = false;


    [Bindable("itemFocusIn")]

    /**
     *  The column and row index of the item renderer for the
     *  data provider item being edited, if any.
     *
     *  <p>This Object has two fields, <code>columnIndex</code> and 
     *  <code>rowIndex</code>,
     *  the zero-based column and row indexes of the item.
     *  For example: {columnIndex:2, rowIndex:3}</p>
     *
     *  <p>Setting this property scrolls the item into view and
     *  dispatches the <code>itemEditBegin</code> event to
     *  open an item editor on the specified item renderer.</p>
     *
     *  @default null
     */
    public function get editedItemPosition():Object
    {
        if (_editedItemPosition)
            return {rowIndex: _editedItemPosition.rowIndex,
                columnIndex: _editedItemPosition.columnIndex};
        else
            return _editedItemPosition;
    }

    /**
     *  @private
     */
    public function set editedItemPosition(value:Object):void
    {
        if (!value)
        {
            setEditedItemPosition(null);
            return;
        }
 
        var newValue:Object = {rowIndex: value.rowIndex,
            columnIndex: value.columnIndex};

        setEditedItemPosition(newValue);
    }


    //----------------------------------
    //  resizableColumns
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  A flag that indicates whether the user can change the size of the
     *  columns.
     *  If <code>true</code>, the user can stretch or shrink the columns of 
     *  the DataGrid control by dragging the grid lines between the header cells.
     *  If <code>true</code>, individual columns must also have their 
     *  <code>resizable</code> properties set to <code>false</code> to 
     *  prevent the user from resizing a particular column.  
     *
     *  @default true
     */
    public var resizableColumns:Boolean = true;

    //----------------------------------
    //  sortableColumns
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  A flag that indicates whether the user can sort the data provider items
     *  by clicking on a column header cell.
     *  If <code>true</code>, the user can sort the data provider items by
     *  clicking on a column header cell. 
     *  The <code>DataGridColumn.dataField</code> property of the column
     *  or the <code>DataGridColumn.sortCompareFunction</code> property 
     *  of the column is used as the sort field.  
     *  If a column is clicked more than once
     *  the sort alternates between ascending and descending order.
     *  If <code>true</code>, individual columns can be made to not respond
     *  to a click on a header by setting the column's <code>sortable</code>
     *  property to <code>false</code>.
     *
     *  <p>When a user releases the mouse button over a header cell, the DataGrid
     *  control dispatches a <code>headerRelease</code> event if both
     *  this property and the column's sortable property are <code>true</code>.  
     *  If no handler calls the <code>preventDefault()</code> method on the event, the 
     *  DataGrid sorts using that column's <code>DataGridColumn.dataField</code> or  
     *  <code>DataGridColumn.sortCompareFunction</code> properties.</p>
     * 
     *  @default true
     *
     *  @see mx.controls.dataGridClasses.DataGridColumn#dataField
     *  @see mx.controls.dataGridClasses.DataGridColumn#sortCompareFunction
     */
    public var sortableColumns:Boolean = true;

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function invalidateDisplayList():void
    {
        super.invalidateDisplayList();
        if (header)
        {
            header.headerItemsChanged = true;
            header.invalidateSize();
            header.invalidateDisplayList();
        }
        if (lockedColumnHeader)
        {
            lockedColumnHeader.headerItemsChanged = true;
            lockedColumnHeader.invalidateSize();
            lockedColumnHeader.invalidateDisplayList();
        }
    }

    [Inspectable(category="Data", defaultValue="undefined")]

    /**
     *  @private
     */
    override public function set dataProvider(value:Object):void
    {
        if (itemEditorInstance)
            endEdit(DataGridEventReason.OTHER);

        lastEditedItemPosition = null;

        super.dataProvider = value;
    }

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (DataGrid.createAccessibilityImplementation != null)
            DataGrid.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     *  Measures the DataGrid based on its contents,
     *  summing the total of the visible column widths.
     */
    override protected function measure():void
    {
        super.measure();

        if (explicitRowCount != -1)
        {
            measuredHeight += headerHeight;
            measuredMinHeight += headerHeight;
        }

        var o:EdgeMetrics = viewMetrics;

        var n:int = columns.length;
        if (n == 0)
        {
            measuredWidth = DEFAULT_MEASURED_WIDTH;
            measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH;
            return;
        }

        var columnWidths:Number = 0;
        var columnMinWidths:Number = 0;
        for (var i:int = 0; i < n; i++)
        {
            if (columns[i].visible)
            {
                columnWidths += columns[i].preferredWidth;
                if (isNaN(_minColumnWidth))
                    columnMinWidths += columns[i].minWidth;
            }
        }

        if (!isNaN(_minColumnWidth))
            columnMinWidths = n * _minColumnWidth;

        measuredWidth = columnWidths + o.left + o.right;
        measuredMinWidth = columnMinWidths + o.left + o.right;

        // factor out scrollbars if policy == AUTO.  See Container.viewMetrics
        if (verticalScrollPolicy == ScrollPolicy.AUTO &&
            verticalScrollBar && verticalScrollBar.visible)
        {
            measuredWidth -= verticalScrollBar.minWidth;
            measuredMinWidth -= verticalScrollBar.minWidth;
        }
        if (horizontalScrollPolicy == ScrollPolicy.AUTO &&
            horizontalScrollBar && horizontalScrollBar.visible)
        {
            measuredHeight -= horizontalScrollBar.minHeight;
            measuredMinHeight -= horizontalScrollBar.minHeight;
        }

    }

    /**
     *  @private
     *  Sizes and positions the column headers, columns, and items based on the
     *  size of the DataGrid.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // Note: We can't immediately call super.updateDisplayList()
        // because the visibleColumns array must be populated first.

        // trace(">>updateDisplayList");
        if (displayWidth != unscaledWidth - viewMetrics.right - viewMetrics.left)
        {
            displayWidth = unscaledWidth - viewMetrics.right - viewMetrics.left;
            columnsInvalid = true;
        }

        calculateColumnSizes();

        if (itemEditorPositionChanged)
        {
            itemEditorPositionChanged = false;
            // don't do this if mouse is down on an item
            // on mouse up, we'll let the edit session logic
            // request a new position
            if (!lastItemDown)
                scrollToEditedItem(editedItemPosition.rowIndex, editedItemPosition.colIndex);
        }

        super.updateDisplayList(unscaledWidth, unscaledHeight);

        if (collection && collection.length)
        {
            setRowCount(listItems.length);

            if (listItems.length)
                setColumnCount(listItems[0].length);
            else
                setColumnCount(0);
        }

        // If we have a vScroll only, we want the scrollbar to be below
        // the header.
        if (verticalScrollBar != null && verticalScrollBar.visible &&
           (horizontalScrollBar == null || !horizontalScrollBar.visible) && 
           headerVisible)
        {
            var hh:Number = header.height;
            var bm:EdgeMetrics = borderMetrics;
            
            if (roomForScrollBar(verticalScrollBar, 
                        unscaledWidth-bm.left-bm.right, 
                        unscaledHeight-hh-bm.top-bm.bottom))
            {
                verticalScrollBar.move(verticalScrollBar.x, viewMetrics.top + hh);
                verticalScrollBar.setActualSize(
                    verticalScrollBar.width,
                    unscaledHeight - viewMetrics.top - viewMetrics.bottom - hh);
                verticalScrollBar.visible = true;
                headerMask.width += verticalScrollBar.getExplicitOrMeasuredWidth();
                
                if (!DataGridHeader(header).needRightSeparator)
                {
                    header.invalidateDisplayList();
                    DataGridHeader(header).needRightSeparator = true;
                }
            }
            else
            {
                if (DataGridHeader(header).needRightSeparator)
                {
                    header.invalidateDisplayList();
                    DataGridHeader(header).needRightSeparator = false;
                }
            }
        }
        else
        {
            if (DataGridHeader(header).needRightSeparator)
            {
                header.invalidateDisplayList();
                DataGridHeader(header).needRightSeparator = false;
            }
        }

        if (bEditedItemPositionChanged)
        {
            bEditedItemPositionChanged = false;
            // don't do this if mouse is down on an item
            // on mouse up, we'll let the edit session logic
            // request a new position
            if (!lastItemDown)
                commitEditedItemPosition(_proposedEditedItemPosition);
            _proposedEditedItemPosition = undefined;
            itemsSizeChanged = false;
        }

        drawRowBackgrounds();
        drawLinesAndColumnBackgrounds();

        if (lockedRowCount && lockedRowContent)
        {
            drawRowGraphics(lockedRowContent);
            drawLinesAndColumnGraphics(lockedRowContent, visibleColumns, { bottom: 1});
            if (lockedColumnCount)
            {
                drawRowGraphics(lockedColumnAndRowContent);
                drawLinesAndColumnGraphics(lockedColumnAndRowContent, visibleLockedColumns, { right: 1, bottom: 1});
            }
        }
        if (lockedColumnCount)
        {
            drawRowGraphics(lockedColumnContent)
            drawLinesAndColumnGraphics(lockedColumnContent, visibleLockedColumns, { right: 1})
        }

        // trace("<<updateDisplayList");
    }

    /**
     *  @private
     */
    override protected function makeRowsAndColumns(left:Number, top:Number,
                                                right:Number, bottom:Number,
                                                firstCol:int, firstRow:int,
                                                byCount:Boolean = false, rowsNeeded:uint = 0):Point
    {
        allowItemSizeChangeNotification = false;

        var pt:Point = super.makeRowsAndColumns(left, top, right, bottom,
                                                firstCol, firstRow, byCount, rowsNeeded);
        if (itemEditorInstance)
        {
            actualContentHolder.setChildIndex(DisplayObject(itemEditorInstance),
                                      actualContentHolder.numChildren - 1);
            var col:DataGridColumn = editedItemPosition.columnIndex < lockedColumnCount ?
                                        visibleLockedColumns[actualColIndex] : 
                                        visibleColumns[actualColIndex];

            var item:IListItemRenderer = actualContentHolder.listItems[actualRowIndex][actualColIndex];
            var rowData:ListRowInfo = actualContentHolder.rowInfo[actualRowIndex];
            if (item && !col.rendererIsEditor)
            {
                var dx:Number = col.editorXOffset;
                var dy:Number = col.editorYOffset;
                var dw:Number = col.editorWidthOffset;
                var dh:Number = col.editorHeightOffset;
                itemEditorInstance.move(item.x + dx, rowData.y + dy);
                itemEditorInstance.setActualSize(Math.min(col.width + dw, actualContentHolder.width - itemEditorInstance.x),
                                         Math.min(rowData.height + dh, actualContentHolder.height - itemEditorInstance.y));
                item.visible = false;

            }

            var lines:Sprite = Sprite(actualContentHolder.getChildByName("lines"));
            if (lines)
                actualContentHolder.setChildIndex(lines, actualContentHolder.numChildren - 1);
        }

        allowItemSizeChangeNotification = variableRowHeight;
        return pt;
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
                if (iterator && columns.length > 0)
                {
                    //set DataGridBase.visibleColumns to the set of 
                    //all columns
                    visibleColumns = columns;
                    columnsInvalid = true;

                    var paddingTop:Number = getStyle("paddingTop");
                    var paddingBottom:Number = getStyle("paddingBottom");

                    var data:Object = iterator.current;
                    var item:IListItemRenderer;
                    var c:DataGridColumn;
                    var ch:Number = 0;
                    var n:int = columns.length;
                    for (var j:int = 0; j < n; j++)
                    {
                        c = columns[j];

                        if (!c.visible)
                            continue;

                        item = c.getMeasuringRenderer(false, data);
                        if (DisplayObject(item).parent == null)
                            listContent.addChild(DisplayObject(item));
                        setupRendererFromData(c, item, data);
                        ch = Math.max(ch, item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop);
                    }

                    // unless specified otherwise, rowheight defaults to 20
                    setRowHeight(Math.max(ch, 20));
                }
                else
                    setRowHeight(20);
            }
        }
    }

    /**
     *  @private
     *  Instead of measuring the items, we measure the visible columns instead.
     */
    override public function measureWidthOfItems(index:int = -1, count:int = 0):Number
    {
        var w:Number = 0;

        var n:int = columns ? columns.length : 0;
        for (var i:int = 0; i < n; i++)
        {
            if (columns[i].visible)
                w += columns[i].width;
        }

        return w;
    }

    /**
     *  @private
     */

    mx_internal function setupRendererFromData(c:DataGridColumn, item:IListItemRenderer, data:Object):void
    {
        var rowData:DataGridListData = DataGridListData(makeListData(data, itemToUID(data), 0, c.colNum, c));
        if (item is IDropInListItemRenderer)
            IDropInListItemRenderer(item).listData = data ? rowData : null;
        item.data = data;
        item.explicitWidth = getWidthOfItem(item, c);
        UIComponentGlobals.layoutManager.validateClient(item, true);
    }

    /**
     *  @private
     */
    override public function measureHeightOfItems(index:int = -1, count:int = 0):Number
    {
        return measureHeightOfItemsUptoMaxHeight(index, count);
    }

    /**
     *  @private
     */
    mx_internal function measureHeightOfItemsUptoMaxHeight(index:int = -1, count:int = 0, maxHeight:Number = -1):Number
    {
        if (!columns.length)
            return rowHeight * count;

        var h:Number = 0;

        var item:IListItemRenderer;
        var c:DataGridColumn;
        var ch:Number = 0;
        var n:int;
        var j:int;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        var lockedCount:int = lockedRowCount;

        if (headerVisible && count > 0 && index == -1)
        {
            h = calculateHeaderHeight();

            if (maxHeight != -1 && h > maxHeight)
            {
                setRowCount(0);
                return 0;
            }

            // trace(this + " header preferredHeight = " + h);
            count --;
            index = 0;
        }

        var bookmark:CursorBookmark = (iterator) ? iterator.bookmark : null;

        var bMore:Boolean = iterator != null;
        if (index != -1 && iterator)
        {
            try
            {
                iterator.seek(CursorBookmark.FIRST, index);
            }
            catch(e:ItemPendingError)
            {
                bMore = false;
            }
        }

        if (lockedCount > 0)
        {
            try
            {
                collectionIterator.seek(CursorBookmark.FIRST,0);
            }
            catch(e:ItemPendingError)
            {
                bMore = false;
            }
        }

        for (var i:int = 0; i < count; i++)
        {
            var data:Object;
            if (bMore)
            {
                data = (lockedCount > 0) ? collectionIterator.current : iterator.current;
                ch = 0;
                n = columns.length;
                for (j = 0; j < n; j++)
                {
                    c = columns[j];

                    if (!c.visible)
                        continue;

                    item = c.getMeasuringRenderer(false, data);
                    if (DisplayObject(item).parent == null)
                        listContent.addChild(DisplayObject(item));
                    setupRendererFromData(c, item, data);
                    ch = Math.max(ch, variableRowHeight ? item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop : rowHeight);
                }
            }

            if (maxHeight != -1 && (h + ch > maxHeight || !bMore))
            {
                try
                {
                    if (iterator)
                        iterator.seek(bookmark, 0);
                }
                catch(e:ItemPendingError)
                {
                    // we don't recover here since we'd only get here if the first seek failed.
                }
                count = (headerVisible) ? i + 1 : i;
                setRowCount(count);
                return h;
            }

            h += ch;
            if (iterator)
            {
                try
                {
                    bMore = iterator.moveNext();
                    if (lockedCount > 0)
                    {
                        collectionIterator.moveNext();
                        lockedCount--;
                    }
                }
                catch(e:ItemPendingError)
                {
                    // if we run out of data, assume all remaining rows are the size of the previous row
                    bMore = false;
                }
            }
        }

        if (iterator)
        {
            try
            {
                iterator.seek(bookmark, 0);
            }
            catch(e:ItemPendingError)
            {
                // we don't recover here since we'd only get here if the first seek failed.
            }
        }

        // trace("calcheight = " + h);
        return h;
    }

    /**
     *  @private
     */
    mx_internal function calculateHeaderHeight():Number
    {
        if (!columns.length)
            return rowHeight;

        // block bad behavior from PDG
        if (!listContent)
            return rowHeight;

        var item:IListItemRenderer;
        var c:DataGridColumn;
        var rowData:DataGridListData;
        var ch:Number = 0;
        var n:int;
        var j:int;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        if (showHeaders)
        {
            ch = 0;
            n = columns.length;

            if (_headerWordWrapPresent)
            {
                _headerHeight = _originalHeaderHeight;
                _explicitHeaderHeight = _originalExplicitHeaderHeight;
            }

            for (j = 0; j < n; j++)
            {
                c = columns[j];

                if (!c.visible)
                    continue;

                item = c.getMeasuringRenderer(true, c);
                if (DisplayObject(item).parent == null)
                    listContent.addChild(DisplayObject(item));
                rowData = DataGridListData(makeListData(c, uid, 0, c.colNum, c));
                rowMap[item.name] = rowData;
                if (item is IDropInListItemRenderer)
                    IDropInListItemRenderer(item).listData = rowData;
                item.data = c;
                item.explicitWidth = c.width;
                UIComponentGlobals.layoutManager.validateClient(item, true);
                ch = Math.max(ch, _explicitHeaderHeight ? headerHeight : item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop);

                if (columnHeaderWordWrap(c))
                    _headerWordWrapPresent = true;
            }

            if (_headerWordWrapPresent)
            {
                // take backups
                _originalHeaderHeight = _headerHeight;
                _originalExplicitHeaderHeight = _explicitHeaderHeight;

                _headerHeight = ch;
                _explicitHeaderHeight = true;
            }
        }
        return ch;
    }

    private var _headerWordWrapPresent:Boolean = false;
    private var _originalExplicitHeaderHeight:Boolean = false;
    private var _originalHeaderHeight:Number = 0;

    /**
     *  @private
     */
    override protected function calculateRowHeight(data:Object, hh:Number, skipVisible:Boolean = false):Number
    {
        var item:IListItemRenderer;
        var c:DataGridColumn;

        var n:int = columns.length;
        var j:int;
        var k:int = 0;
        var l:int = visibleLockedColumns.length;

        if (skipVisible && visibleColumns.length == _columns.length)
            return hh;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        for (j = 0; j < n; j++)
        {
            // skip any columns that are visible
            if (skipVisible && k < l && visibleLockedColumns[k].colNum == columns[j].colNum)
            {
                k++;
                continue;
            }
            if (skipVisible && k - l < visibleColumns.length && visibleColumns[k - l].colNum == columns[j].colNum)
            {
                k++;
                continue;
            }
            c = columns[j];

            if (!c.visible)
                continue;

            item = c.getMeasuringRenderer(false, data);
            if (DisplayObject(item).parent == null)
                listContent.addChild(DisplayObject(item));
            setupRendererFromData(c, item, data);
            hh = Math.max(hh, item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop);
        }
        return hh;
    }

    /**
     *  @private
     */
    override protected function scrollHandler(event:Event):void
    {
        if (event.target == verticalScrollBar ||
            event.target == horizontalScrollBar)
        {
            // TextField.scroll bubbles so you might see it here
            if (event is ScrollEvent)
            {
                if (!liveScrolling &&
                    ScrollEvent(event).detail == ScrollEventDetail.THUMB_TRACK)
                {
                    return;
                }

                if (itemEditorInstance)
                    endEdit(DataGridEventReason.OTHER);

                var scrollBar:ScrollBar = ScrollBar(event.target);
                var pos:Number = scrollBar.scrollPosition;

                if (scrollBar == verticalScrollBar)
                    verticalScrollPosition = pos;
                else if (scrollBar == horizontalScrollBar)
                    horizontalScrollPosition = pos;

                super.scrollHandler(event);
            }
        }
    }

    private function displayingPartialRow():Boolean
    {
        var index:int = listItems.length - 1 - offscreenExtraRowsBottom;
        if (rowInfo[index].y + rowInfo[index].height > listContent.heightExcludingOffsets - listContent.topOffset)
            return true;
        return false;
    }

    /**
     *  @private
     */
    override protected function configureScrollBars():void
    {
        if (columnsInvalid)
            return;

        if (!displayableColumns)
            return;

        // for purposes of computing rows, we need to accomodate
        // the case where all the visible columns are locked columns
        var countableContentListItems:Array = this.listItems;
        if (visibleColumns && !visibleColumns.length && visibleLockedColumns && visibleLockedColumns.length)
            countableContentListItems = lockedColumnContent.listItems;
            
        var oldHorizontalScrollBar:Object = horizontalScrollBar;
        var oldVerticalScrollBar:Object = verticalScrollBar;

        var rowCount:int = countableContentListItems.length;
        if (rowCount == 0)
        {
            // Get rid of any existing scrollbars.
            if (oldHorizontalScrollBar || oldVerticalScrollBar)
                // protect against situation where the scrollbars
                // cause re-layout and the listContent is sized
                // to zero because of number of lockedRowCount
                if (listContent.height) 
                    setScrollBarProperties(0, 0, 0, 0);

            return;
        }

        // partial last rows don't count
        if (rowCount > 1 && displayingPartialRow()) 
            rowCount--;

        // offset, when added to rowCount, is the index of the dataProvider
        // item for that row.  IOW, row 10 in listItems is showing dataProvider
        // item 10 + verticalScrollPosition - lockedRowCount;
        var offset:int = verticalScrollPosition;
        // don't count filler rows at the bottom either.
        var fillerRows:int = 0;
        while (rowCount && countableContentListItems[rowCount - 1].length == 0)
        {
            // as long as we're past the end of the collection, add up
            // fillerRows
            if (collection && rowCount + offset >= collection.length - lockedRowCount)
            {
                rowCount--;
                ++fillerRows;
            }
            else
                break;
        }

        // we have to scroll up.  We can't have filler rows unless the scrollPosition is 0
        if (verticalScrollPosition > 0 && fillerRows > 0)
        {
            if (adjustVerticalScrollPositionDownward(Math.max(rowCount, 1)))
                return;
        }

        rowCount -= (offscreenExtraRowsTop + offscreenExtraRowsBottom);

        var collectionHasRows:Boolean = collection && collection.length > 0;

        var colCount:int = (collectionHasRows && rowCount > 0) ? listItems[0].length : visibleColumns.length;

        // if the last column is visible and partially offscreen (but it isn't the only
        // column) then adjust the column count so we can scroll to see it
        if (collectionHasRows && rowCount > 0 && colCount > 1 && 
            visibleColumns[colCount - 1] == displayableColumns[displayableColumns.length - 1]
            && listItems[0][colCount - 1].x + 
            visibleColumns[colCount - 1].width > (displayWidth - listContent.x + viewMetrics.left))
            colCount--;
        else if (colCount > 1 && !collectionHasRows && 
            visibleColumns[colCount - 1] == displayableColumns[displayableColumns.length - 1])
        {
            // the slower computation requires adding up the previous columns
            var colX:int = 0;
            for (var i:int = 0; i < visibleColumns.length; i++)
            {
                colX += visibleColumns[i].width;
            }
            if (colX > (displayWidth - listContent.x + viewMetrics.left))
                colCount--;
        }

        // trace("configureSB", verticalScrollPosition);

        setScrollBarProperties(displayableColumns.length - lockedColumnCount, Math.max(colCount, 1),
                            collection ? collection.length - lockedRowCount : 0,
                            Math.max(rowCount, 1));

        if ((!verticalScrollBar || !verticalScrollBar.visible) && collection &&
            collection.length - lockedRowCount > rowCount)
            maxVerticalScrollPosition = collection.length - lockedRowCount - rowCount;
        if ((!horizontalScrollBar || !horizontalScrollBar.visible) && 
            displayableColumns.length - lockedColumnCount  > colCount - lockedColumnCount)
            maxHorizontalScrollPosition = displayableColumns.length - lockedColumnCount - colCount;
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

        var item:IListItemRenderer;
        var c:DataGridColumn;
        var ch:Number = 0;
        var n:int;
        var j:int;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        h = rowInfo[rowCount - 1].y + rowInfo[rowCount - 1].height;
        h = listContent.heightExcludingOffsets - listContent.topOffset - h;

        // back up one
        var numRows:int = 0;
        try
        {
            if (iterator.afterLast)
                iterator.seek(CursorBookmark.LAST, 0)
            else
                var bMore:Boolean = iterator.movePrevious();
        }
        catch(e:ItemPendingError)
        {
            bMore = false;
        }
        if (!bMore)
        {
            // reset to 0;
            super.verticalScrollPosition = 0;
            try
            {
                iterator.seek(CursorBookmark.FIRST, 0);
                // sometimes, if the iterator is invalid we'll get lucky and succeed
                // here, then we have to make the iterator valid again
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
    
        // now work backwards to see how many more rows we need to create
        while (h > 0 && bMore)
        {
            var data:Object;
            if (bMore)
            {
                data = iterator.current;
                ch = 0;
                n = columns.length;
                for (j = 0; j < n; j++)
                {
                    c = columns[j];

                    if (!c.visible)
                        continue;

                    if (variableRowHeight)
                    {
                        item = c.getMeasuringRenderer(false, data);
                        if (DisplayObject(item).parent == null)
                            listContent.addChild(DisplayObject(item));
                        setupRendererFromData(c, item, data);
                    }
                    ch = Math.max(ch, variableRowHeight ? item.getExplicitOrMeasuredHeight() + paddingBottom + paddingTop : rowHeight);
                }
            }
            h -= ch;
            try
            {
                bMore = iterator.movePrevious();
                numRows++;
            }
            catch(e:ItemPendingError)
            {
                // if we run out of data, assume all remaining rows are the size of the previous row
                bMore = false;
            }
        }
        // if we overrun, go back one.
        if (h < 0)
        {
            numRows--;
        }

        iterator.seek(bookmark, 0);
        verticalScrollPosition = Math.max(0, verticalScrollPosition - numRows);

        // make sure we get through configureScrollBars w/o coming in here.
        if (numRows > 0 && !variableRowHeight)
            configureScrollBars();

        return (numRows > 0);
    }

    /**
     *  @private
     */
    override public function calculateDropIndex(event:DragEvent = null):int
    {
        if (event)
        {
            var item:IListItemRenderer;
            var lastItem:IListItemRenderer;
            var pt:Point = new Point(event.localX, event.localY);
            pt = DisplayObject(event.target).localToGlobal(pt);
            pt = listContent.globalToLocal(pt);

            var n:int = listItems.length;
            for (var i:int = 0; i < n; i++)
            {
                if (listItems[i][0])
                    lastItem = listItems[i][0];
                    
                if (rowInfo[i].y <= pt.y && pt.y < rowInfo[i].y + rowInfo[i].height)
                {
                    item = listItems[i][0];
                    break;
                }
            }
            if (!item && lockedRowContent)
            {
                pt = listContent.localToGlobal(pt);
                pt = lockedRowContent.globalToLocal(pt);
                n = lockedRowContent.listItems.length;
                for (i = 0; i < n; i++)
                {
                    if (lockedRowContent.rowInfo[i].y <= pt.y && pt.y < lockedRowContent.rowInfo[i].y + lockedRowContent.rowInfo[i].height)
                    {
                        item = lockedRowContent.listItems[i][0];
                        break;
                    }
                }
            }


            if (item)
                lastDropIndex = itemRendererToIndex(item);
            else
            {
                if (lastItem)
                    lastDropIndex = itemRendererToIndex(lastItem) + 1;
                else
                    lastDropIndex = collection ? collection.length : 0;
            }
        }

        return lastDropIndex;
    }
    
    /**
     *  @private
     */
    override protected function drawRowBackgrounds():void
    {
        drawRowGraphics(listContent);
    }

    /**
     *  @private
     */
    protected function drawRowGraphics(contentHolder:ListBaseContentHolder):void
    {
        var rowBGs:Sprite = Sprite(contentHolder.getChildByName("rowBGs"));
        if (!rowBGs)
        {
            rowBGs = new FlexSprite();
            rowBGs.mouseEnabled = false;
            rowBGs.name = "rowBGs";
            contentHolder.addChildAt(rowBGs, 0);
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

        var i:int = 0;
        var actualRow:int = verticalScrollPosition;
        var n:int = contentHolder.listItems.length;

        while (curRow < n)
        {
            drawRowBackground(rowBGs, i++, contentHolder.rowInfo[curRow].y, contentHolder.rowInfo[curRow].height, 
                colors[actualRow % colors.length], actualRow);
            curRow++;
            actualRow++;
        }

        while (rowBGs.numChildren > i)
        {
            rowBGs.removeChildAt(rowBGs.numChildren - 1);
        }
    }

    /**
     *  @private
     */
    override protected function mouseEventToItemRenderer(event:MouseEvent):IListItemRenderer
    {
        var r:IListItemRenderer;

        r = super.mouseEventToItemRenderer(event);

        return r == itemEditorInstance ? null : r;
    }

    /**
     *  @private
     */
    override protected function get dragImage():IUIComponent
    {
        var image:DataGridDragProxy = new DataGridDragProxy();
        image.owner = this;
        image.moduleFactory = moduleFactory;
        return image;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------


    /**
     *  @private
     *  Move a column to a new position in the columns array, shifting all
     *  other columns left or right and updating the sortIndex and
     *  lastSortIndex variables accordingly.
     */
    mx_internal function shiftColumns(oldIndex:int, newIndex:int,
                                      trigger:Event = null):void
    {
        if (newIndex >= 0 && oldIndex != newIndex)
        {
            var incr:int = oldIndex < newIndex ? 1 : -1;
            for (var i:int = oldIndex; i != newIndex; i += incr)
            {
                var j:int = i + incr;
                var c:DataGridColumn = _columns[i];
                _columns[i] = _columns[j];
                _columns[j] = c;
                _columns[i].colNum = i;
                _columns[j].colNum = j;
            }

            if (sortIndex == oldIndex)
                sortIndex += newIndex - oldIndex;
            else if ((oldIndex < sortIndex && sortIndex <= newIndex)
                    || (newIndex <= sortIndex && sortIndex < oldIndex))
                sortIndex -= incr;

            if (lastSortIndex == oldIndex)
                lastSortIndex += newIndex - oldIndex;
            else if ((oldIndex < lastSortIndex
                        && lastSortIndex <= newIndex)
                    || (newIndex <= lastSortIndex
                        && lastSortIndex < oldIndex))
                lastSortIndex -= incr;

            columnsInvalid = true;
            itemsSizeChanged = true;
            invalidateDisplayList();
            if (lockedColumnHeader)
                lockedColumnHeader.invalidateDisplayList();

            var icEvent:IndexChangedEvent =
                new IndexChangedEvent(IndexChangedEvent.HEADER_SHIFT);
            icEvent.oldIndex = oldIndex;
            icEvent.newIndex = newIndex;
            icEvent.triggerEvent = trigger;
            dispatchEvent(icEvent);
        }
    }

    /**
     *  @private
     *  Searches the iterator to determine columns.
     */
    private function generateCols():void
    {
        if (collection.length > 0)
        {
            var col:DataGridColumn;
            var newCols:Array = [];
            var cols:Array;
            if (dataProvider)
            {
                try
                {
                    iterator.seek(CursorBookmark.FIRST);
                    if (!iteratorValid)
                    {
                        iteratorValid = true;
                        lastSeekPending = null;
                    }
                }
                catch(e:ItemPendingError)
                {
                    lastSeekPending = new ListBaseSeekPending(CursorBookmark.FIRST, 0);
                    e.addResponder(new ItemResponder(generateColumnsPendingResultHandler, seekPendingFailureHandler,
                                                    lastSeekPending));
                    iteratorValid = false;
                    return;
                }
                var info:Object =
                    ObjectUtil.getClassInfo(iterator.current,
                                            ["uid", "mx_internal_uid"]);

                if (info)
                    cols = info.properties;
            }

            if (!cols)
            {
                // introspect the first item and use its fields
                var itmObj:Object = iterator.current;
                for (var p:String in itmObj)
                {
                    if (p != "uid")
                    {
                        col = new DataGridColumn();
                        col.dataField = p;
                        newCols.push(col);
                    }
                }
            }
            else
            {
                // this is an old recordset - use its columns
                var n:int = cols.length;
                var colName:Object;
                for (var i:int = 0; i < n; i++)
                {
                    colName = cols[i];
                    if (colName is QName)
                        colName = QName(colName).localName;
                    col = new DataGridColumn();
                    col.dataField = String(colName);
                    newCols.push(col);
                }
            }
            columns = newCols;
            generatedColumns = true;
        }
    }

    /**
     *  @private
     */
    private function generateColumnsPendingResultHandler(data:Object, info:ListBaseSeekPending):void
    {
        // generate cols if we haven't successfully generated them
        if (columns.length == 0)
            generateCols();
        seekPendingResultHandler(data, info);
    }

    /**
     *  @private
     */
    private function calculateColumnSizes():void
    {
        var delta:Number;
        var n:int;
        var i:int;
        var totalWidth:Number = 0;
        var col:DataGridColumn;
        var cw:Number;

        if (columns.length == 0)
        {
            visibleColumns = [];
            visibleLockedColumns = [];
            lockedColumnWidth = 0;
            columnsInvalid = false;
            return;
        }

        // no columns are visible so figure out which ones
        // to make visible
        if (columnsInvalid)
        {
            columnsInvalid = false;
            visibleColumns = [];
            visibleLockedColumns = [];
            lockedColumnWidth = 0;

            if (minColumnWidthInvalid)
            {
                n = columns.length;
                for (i = 0; i < n; i++)
                {
                    columns[i].minWidth = minColumnWidth;
                }
                minColumnWidthInvalid = false;
            }

            displayableColumns = null;
            n = _columns.length;
            for (i = 0; i < n; i++)
            {
                if (displayableColumns && _columns[i].visible)
                {
                    displayableColumns.push(_columns[i]);
                }
                else if (!displayableColumns && !_columns[i].visible)
                {
                    displayableColumns = new Array(i);
                    for (var k:int = 0; k < i; k++)
                        displayableColumns[k] = _columns[k];
                }
            }

            // If there are no hidden columns, displayableColumns points to
            // _columns (we don't need a duplicate copy of _columns).
            if (!displayableColumns)
                displayableColumns = _columns;

            // if no hscroll, then pack columns in available space
            if (horizontalScrollPolicy == ScrollPolicy.OFF)
            {
                n = displayableColumns.length;
                for (i = 0; i < n; i++)
                {
                    col = displayableColumns[i];

                    if (i < lockedColumnCount)
                    {
                        visibleLockedColumns.push(col);
                    }
                    else
                        visibleColumns.push(col);
                }
            }
            else
            {
                n = displayableColumns.length;
                for (i = 0; i < n; i++)
                {
                    if (i >= lockedColumnCount &&
                        i < lockedColumnCount + horizontalScrollPosition)
                    {
                        continue;
                    }

                    col = displayableColumns[i];
                    if (col.preferredWidth < col.minWidth)
                        col.preferredWidth = col.minWidth;

                    if (totalWidth < displayWidth)
                    {
                        if (i < lockedColumnCount)
                        {
                            lockedColumnWidth += isNaN(col.explicitWidth) ? col.preferredWidth : col.explicitWidth;
                            visibleLockedColumns.push(col);
                        }
                        else
                            visibleColumns.push(col);
                        totalWidth += isNaN(col.explicitWidth) ? col.preferredWidth : col.explicitWidth;
                        if (col.width != col.preferredWidth)
                            col.setWidth(col.preferredWidth);
                    }
                    else
                    {
                        if (visibleColumns.length == 0)
                            visibleColumns.push(displayableColumns[0]);
                        break;
                    }
                }
            }
        }

        var lastColumn:DataGridColumn;
        var newSize:Number;

        // if no hscroll, then pack columns in available space
        if (horizontalScrollPolicy == ScrollPolicy.OFF)
        {
            var numResizable:int = 0;
            var fixedWidth:Number = 0;

            // trace("resizing columns");

            // count how many resizable columns and how wide they are
            n = visibleColumns.length;
            for (i = 0; i < n; i++)
            {
                // trace("column " + i + " width = " + visibleColumns[i].width);
                if (visibleColumns[i].resizable && !visibleColumns[i].newlyVisible)
                {
                    // trace("    resizable");
                    if (!isNaN(visibleColumns[i].explicitWidth))
                    {
                        // trace("    explicit width " + visibleColumns[i].width);
                        fixedWidth += visibleColumns[i].width;
                    }
                    else
                    {
                        // trace("    implicitly resizable");
                        numResizable++;
                        fixedWidth += visibleColumns[i].minWidth;
                        // trace("    minWidth " + visibleColumns[i].minWidth);
                    }
                }
                else
                {
                    // trace("    not resizable");
                    fixedWidth += visibleColumns[i].width;
                }

                totalWidth += visibleColumns[i].width;
            }
            n = visibleLockedColumns.length;
            for (i = 0; i < n; i++)
            {
                // trace("column " + i + " width = " + visibleLockedColumns[i].width);
                if (visibleLockedColumns[i].resizable && !visibleLockedColumns[i].newlyVisible)
                {
                    // trace("    resizable");
                    if (!isNaN(visibleLockedColumns[i].explicitWidth))
                    {
                        // trace("    explicit width " + visibleLockedColumns[i].width);
                        fixedWidth += visibleLockedColumns[i].width;
                    }
                    else
                    {
                        // trace("    implicitly resizable");
                        numResizable++;
                        fixedWidth += visibleLockedColumns[i].minWidth;
                        // trace("    minWidth " + visibleLockedColumns[i].minWidth);
                    }
                }
                else
                {
                    // trace("    not resizable");
                    fixedWidth += visibleLockedColumns[i].width;
                }

                totalWidth += visibleLockedColumns[i].width;
            }
            // trace("totalWidth = " + totalWidth);
            // trace("displayWidth = " + displayWidth);

            var ratio:Number;
            var newTotal:Number = displayWidth;
            var minWidth:Number;
            if (displayWidth > fixedWidth && numResizable)
            {
                // we have flexible columns and room to honor minwidths and non-resizable
                // trace("have enough room");

                // divide and distribute the excess among the resizable
                n = visibleLockedColumns.length;
                for (i = 0; i < n; i++)
                {
                    if (visibleLockedColumns[i].resizable && !visibleLockedColumns[i].newlyVisible && isNaN(visibleLockedColumns[i].explicitWidth))
                    {
                        lastColumn = visibleLockedColumns[i];
                        if (totalWidth > displayWidth)
                            ratio = (lastColumn.width - lastColumn.minWidth)/ (totalWidth - fixedWidth);
                        else
                            ratio = lastColumn.width / totalWidth;
                        newSize = lastColumn.width - (totalWidth - displayWidth) * ratio;
                        minWidth = visibleLockedColumns[i].minWidth;
                        visibleLockedColumns[i].setWidth(newSize > minWidth ? newSize : minWidth);
                        // trace("column " + i + " set to " + visibleLockedColumns[i].width);
                    }
                    newTotal -= visibleLockedColumns[i].width;
                    visibleLockedColumns[i].newlyVisible = false;
                }
                n = visibleColumns.length;
                for (i = 0; i < n; i++)
                {
                    if (visibleColumns[i].resizable && !visibleColumns[i].newlyVisible && isNaN(visibleColumns[i].explicitWidth))
                    {
                        lastColumn = visibleColumns[i];
                        if (totalWidth > displayWidth)
                            ratio = (lastColumn.width - lastColumn.minWidth)/ (totalWidth - fixedWidth);
                        else
                            ratio = lastColumn.width / totalWidth;
                        newSize = lastColumn.width - (totalWidth - displayWidth) * ratio;
                        minWidth = visibleColumns[i].minWidth;
                        visibleColumns[i].setWidth(newSize > minWidth ? newSize : minWidth);
                        // trace("column " + i + " set to " + visibleColumns[i].width);
                    }
                    newTotal -= visibleColumns[i].width;
                    visibleColumns[i].newlyVisible = false;
                }
                if (newTotal && lastColumn)
                {
                    // trace("excess = " + newTotal);
                    lastColumn.setWidth(lastColumn.width + newTotal);
                }
            }
            else // can't honor minwidth and non-resizables so just scale everybody
            {
                // trace("too small or too big");
                n = visibleLockedColumns.length;
                for (i = 0; i < n; i++)
                {
                    lastColumn = visibleLockedColumns[i];
                    ratio = lastColumn.width / totalWidth;
                    //totalWidth -= visibleLockedColumns[i].width;
                    newSize = displayWidth * ratio;
                    lastColumn.setWidth(newSize);
                    lastColumn.explicitWidth = NaN;
                    // trace("column " + i + " set to " + visibleLockedColumns[i].width);
                    newTotal -= newSize;
                }
                n = visibleColumns.length;
                for (i = 0; i < n; i++)
                {
                    lastColumn = visibleColumns[i];
                    ratio = lastColumn.width / totalWidth;
                    //totalWidth -= visibleColumns[i].width;
                    newSize = displayWidth * ratio;
                    lastColumn.setWidth(newSize);
                    lastColumn.explicitWidth = NaN;
                    // trace("column " + i + " set to " + visibleColumns[i].width);
                    newTotal -= newSize;
                }
                if (newTotal && lastColumn)
                {
                    // trace("excess = " + newTotal);
                    lastColumn.setWidth(lastColumn.width + newTotal);
                }
            }
        }
        else // we have or can have an horizontalScrollBar
        {
            totalWidth = 0;
            // drop any that completely overflow
            n = visibleColumns.length;
            for (i = 0; i < n; i++)
            {
                if (totalWidth > displayWidth - lockedColumnWidth)
                {
                    visibleColumns.splice(i);
                    break;
                }
                totalWidth += isNaN(visibleColumns[i].explicitWidth) ? visibleColumns[i].preferredWidth : visibleColumns[i].explicitWidth;
            }

            if (visibleColumns.length == 0)
                return;

            i = visibleColumns[visibleColumns.length - 1].colNum + 1;
            // add more if we have room
            if (totalWidth < displayWidth - lockedColumnWidth && i < displayableColumns.length)
            {
                n = displayableColumns.length;
                for (; i < n && totalWidth < displayWidth - lockedColumnWidth; i++)
                {
                    col = displayableColumns[i];

                    visibleColumns.push(col);
                    totalWidth += isNaN(col.explicitWidth) ? col.preferredWidth : col.explicitWidth;
                }
            }
            else if (totalWidth < displayWidth - lockedColumnWidth && horizontalScrollPosition > 0)
            {
                while (totalWidth < displayWidth - lockedColumnWidth && horizontalScrollPosition > 0)
                {
                    col = displayableColumns[lockedColumnCount + horizontalScrollPosition - 1];
                    cw = isNaN(col.explicitWidth) ? col.preferredWidth : col.explicitWidth;
                    if (cw < displayWidth - lockedColumnWidth - totalWidth)
                    {
                        visibleColumns.splice(0, 0, col);
                        super.horizontalScrollPosition--;
                        totalWidth += cw;
                    }
                    else
                        break;
                }
            }

            lastColumn = visibleColumns[visibleColumns.length - 1];
            cw = isNaN(lastColumn.explicitWidth) ? lastColumn.preferredWidth : lastColumn.explicitWidth;
            newSize = cw + displayWidth - lockedColumnWidth - totalWidth;
            if (lastColumn == displayableColumns[displayableColumns.length - 1]
                && lastColumn.resizable 
                && newSize >= lastColumn.minWidth
                && newSize > cw)
            {
                lastColumn.setWidth(newSize);
                maxHorizontalScrollPosition =
                    displayableColumns.length - visibleColumns.length;
            }
            else
            {
                maxHorizontalScrollPosition =
                    displayableColumns.length - visibleColumns.length + 1;
            }
        }
        lockedColumnWidth = 0;
        if (visibleLockedColumns.length)
        {
            n = visibleLockedColumns.length;
            for (i = 0; i < n; i++)
            {
                col = visibleLockedColumns[i];
                lockedColumnWidth += col.width;
            }
        }
    }

    /**
     *  @private
     *  If there is no horizontal scroll bar, changes the display width of other columns when
     *  one column's width is changed.
     *  @param col column whose width is changed
     *  @param w width of column
     */
    override mx_internal function resizeColumn(col:int, w:Number):void
    {
        // there's a window of time before we calccolumnsizes
        // that someone can set width in AS
        if ((!visibleColumns || visibleColumns.length == 0) && (!visibleLockedColumns || visibleLockedColumns.length == 0))
        {
            _columns[col].setWidth(w);
            _columns[col].preferredWidth = w;
            return;
        }

        if (w < _columns[col].minWidth)
            w = _columns[col].minWidth;

        // hScrollBar is present
        if (_horizontalScrollPolicy == ScrollPolicy.ON ||
            _horizontalScrollPolicy == ScrollPolicy.AUTO)
        {
            // adjust the column's width
            _columns[col].setWidth(w);
            _columns[col].explicitWidth = w;
            _columns[col].preferredWidth = w;
            columnsInvalid = true;
        }
        else
        {
            // find the columns in the set of visible columns;
            var n:int = _columns.length;
            var i:int;
            for (i = 0; i < n; i++)
            {
                if (col == _columns[i].colNum)
                    break;
            }
            if (i >= _columns.length - 1)   // no resize of right most column
                return;
            col = i;

            // we want all cols's new widths to the right of this to be in proportion
            // to what they were before the stretch.

            // get the original space to the right not taken up by the column
            var totalSpace:Number = 0;
            var lastColumn:DataGridColumn;
            var newWidth:Number;
            //non-resizable columns don't count though
            for (i = col + 1; i < n; i++)
            {
                if (_columns[i].visible)
                    if (_columns[i].resizable)
                        totalSpace += _columns[i].width;
            }

            var newTotalSpace:Number = _columns[col].width - w + totalSpace;
            if (totalSpace)
            {
                _columns[col].setWidth(w);
                _columns[col].explicitWidth = w;
            }

            var totX:Number = 0;
            // resize the columns to the right proportionally to what they were
            for (i = col + 1; i < n; i++)
            {
                if (_columns[i].visible)
                    if (_columns[i].resizable)
                    {
                        newWidth = Math.floor(_columns[i].width
                                                    * newTotalSpace / totalSpace);
                        if (newWidth < _columns[i].minWidth)
                            newWidth = _columns[i].minWidth;
                        _columns[i].setWidth(newWidth);
                        totX += _columns[i].width;
                        lastColumn = _columns[i];
                    }
            }

            if (totX > newTotalSpace)
            {
                // if excess then should be taken out only from changing column
                // cause others would have already gone to their minimum
                newWidth = _columns[col].width - totX + newTotalSpace;
                if (newWidth < _columns[col].minWidth)
                    newWidth = _columns[col].minWidth;
                _columns[col].setWidth(newWidth);
            }
            else if (lastColumn)
            {
                // if less then should be added in last column
                // dont need to check for minWidth as we are adding
                lastColumn.setWidth(lastColumn.width - totX + newTotalSpace);
            }
        }
        itemsSizeChanged = true

        invalidateDisplayList();
    }

    /**
     *  Draws a row background 
     *  at the position and height specified using the
     *  color specified.  This implementation creates a Shape as a
     *  child of the input Sprite and fills it with the appropriate color.
     *  This method also uses the <code>backgroundAlpha</code> style property 
     *  setting to determine the transparency of the background color.
     * 
     *  @param s A Sprite that will contain a display object
     *  that contains the graphics for that row.
     *
     *  @param rowIndex The row's index in the set of displayed rows.  The
     *  header does not count, the top most visible row has a row index of 0.
     *  This is used to keep track of the objects used for drawing
     *  backgrounds so a particular row can re-use the same display object
     *  even though the index of the item that row is rendering has changed.
     *
     *  @param y The suggested y position for the background
     * 
     *  @param height The suggested height for the indicator
     * 
     *  @param color The suggested color for the indicator
     * 
     *  @param dataIndex The index of the item for that row in the
     *  data provider.  This can be used to color the 10th item differently
     *  for example.
     */
    protected function drawRowBackground(s:Sprite, rowIndex:int,
                                            y:Number, height:Number, color:uint, dataIndex:int):void
    {
        var contentHolder:ListBaseContentHolder = ListBaseContentHolder(s.parent);

        var background:Shape;
        if (rowIndex < s.numChildren)
        {
            background = Shape(s.getChildAt(rowIndex));
        }
        else
        {
            background = new FlexShape();
            background.name = "background";
            s.addChild(background);
        }

        background.y = y;

        // Height is usually as tall is the items in the row, but not if
        // it would extend below the bottom of listContent
        var height:Number = Math.min(height,
                                     contentHolder.height -
                                     y);

        var g:Graphics = background.graphics;
        g.clear();
        g.beginFill(color, getStyle("backgroundAlpha"));
        g.drawRect(0, 0, contentHolder.width, height);
        g.endFill();
    }

    /**
     *  Draws a column background for a column with the suggested color.
     *  This implementation creates a Shape as a
     *  child of the input Sprite and fills it with the appropriate color.
     *
     *  @param s A Sprite that will contain a display object
     *  that contains the graphics for that column.
     *
     *  @param columnIndex The column's index in the set of displayed columns.  
     *  The left most visible column has a column index of 0.
     *  This is used to keep track of the objects used for drawing
     *  backgrounds so a particular column can re-use the same display object
     *  even though the index of the DataGridColumn for that column has changed.
     *
     *  @param color The suggested color for the indicator
     * 
     *  @param column The column of the DataGrid control that you are drawing the background for.
     */
    protected function drawColumnBackground(s:Sprite, columnIndex:int,
                                            color:uint, column:DataGridColumn):void
    {
        var background:Shape;
        background = Shape(s.getChildByName(columnIndex.toString()));
        if (!background)
        {
            background = new FlexShape();
            s.addChild(background);
            background.name = columnIndex.toString();
        }

        var g:Graphics = background.graphics;
        g.clear();
        g.beginFill(color);

        var lastRow:Object = rowInfo[listItems.length - 1];
        var columnHeader:DataGridHeader = (s.parent == lockedColumnContent) ? 
                                        DataGridHeader(lockedColumnHeader) : 
                                        DataGridHeader(header);
            
        var xx:Number = columnHeader.rendererArray[columnIndex].x
        var yy:Number = rowInfo[0].y

        // Height is usually as tall is the items in the row, but not if
        // it would extend below the bottom of listContent
        var height:Number = Math.min(lastRow.y + lastRow.height,
                                     listContent.height - yy);

        g.drawRect(xx, yy, columnHeader.visibleColumns[columnIndex].width,
                   listContent.height - yy);
        g.endFill();
    }

    /**
     *  Creates and sizes the horizontalSeparator skins. If none have been specified, then draws the lines using
     *  drawHorizontalLine(). 
     */
    private function drawHorizontalSeparator(s:Sprite, rowIndex:int, color:uint, y:Number, useLockedSeparator:Boolean = false):void
    {
        var hSepSkinName:String = "hSeparator" + rowIndex;
        var hLockedSepSkinName:String = "hLockedSeparator" + rowIndex;
        var createThisSkinName:String = useLockedSeparator ? hLockedSepSkinName : hSepSkinName;
        var createThisStyleName:String = useLockedSeparator ? "horizontalLockedSeparatorSkin" : "horizontalSeparatorSkin";
        
        var sepSkin:IFlexDisplayObject;
        var lockedSepSkin:IFlexDisplayObject;
        var deleteThisSkin:IFlexDisplayObject;
        var createThisSkin:IFlexDisplayObject;
                                
        // Look for separator by name
        sepSkin = IFlexDisplayObject(s.getChildByName(hSepSkinName));
        lockedSepSkin = IFlexDisplayObject(s.getChildByName(hLockedSepSkinName));
        
        createThisSkin = useLockedSeparator ? lockedSepSkin : sepSkin;
        deleteThisSkin = useLockedSeparator ? sepSkin : lockedSepSkin;
        
        if (deleteThisSkin)
        {
            s.removeChild(DisplayObject(deleteThisSkin));
            //delete deleteThisSkin;
        }
        
        if (!createThisSkin)
        {
            var sepSkinClass:Class = Class(getStyle(createThisStyleName));
        
            if (sepSkinClass)
            {
                createThisSkin = IFlexDisplayObject(new sepSkinClass());
                createThisSkin.name = createThisSkinName;
                
                var styleableSkin:ISimpleStyleClient = createThisSkin as ISimpleStyleClient;
                if (styleableSkin)
                    styleableSkin.styleName = this;
                    
                s.addChild(DisplayObject(createThisSkin));
            }
        }
        
        if (createThisSkin)
        {
            var mHeight:Number = !isNaN(createThisSkin.measuredHeight) ? createThisSkin.measuredHeight : 1;
            createThisSkin.setActualSize(displayWidth - lockedColumnWidth, mHeight); 
            createThisSkin.move(0, y);      
        }
        else // If we still don't have a sepSkin, then we have no skin style defined. Use the default function instead
        {
            drawHorizontalLine(s, rowIndex, color, y);
        }
        
    }

    /**
     *  Draws a line between rows.  This implementation draws a line
     *  directly into the given Sprite.  The Sprite has been cleared
     *  before lines are drawn into it.
     *
     *  @param s A Sprite that will contain a display object
     *  that contains the graphics for that row.
     *
     *  @param rowIndex The row's index in the set of displayed rows.  The
     *  header does not count, the top most visible row has a row index of 0.
     *  This is used to keep track of the objects used for drawing
     *  backgrounds so a particular row can re-use the same display object
     *  even though the index of the item that row is rendering has changed.
     *
     *  @param color The suggested color for the indicator
     * 
     *  @param y The suggested y position for the background
     */
    protected function drawHorizontalLine(s:Sprite, rowIndex:int, color:uint, y:Number):void
    {
        var contentHolder:ListBaseContentHolder = s.parent.parent as ListBaseContentHolder;
        var g:Graphics = s.graphics;

        g.lineStyle(1, color);
        g.moveTo(0, y);
        g.lineTo(contentHolder.width, y);
    }
    
    /**
     *  Creates and sizes the verticalSeparator skins. If none have been specified, then draws the lines using
     *  drawVerticalLine(). 
     */
    private function drawVerticalSeparator(s:Sprite, colIndex:int, color:uint, x:Number, useLockedSeparator:Boolean = false):void
    {
        var vSepSkinName:String = "vSeparator" + colIndex;
        var vLockedSepSkinName:String = "vLockedSeparator" + colIndex;
        var createThisSkinName:String = useLockedSeparator ? vLockedSepSkinName : vSepSkinName;
        var createThisStyleName:String = useLockedSeparator ? "verticalLockedSeparatorSkin" : "verticalSeparatorSkin";
        
        var sepSkin:IFlexDisplayObject;
        var lockedSepSkin:IFlexDisplayObject;
        var deleteThisSkin:IFlexDisplayObject;
        var createThisSkin:IFlexDisplayObject;
                                
        // Look for separator by name
        sepSkin = IFlexDisplayObject(s.getChildByName(vSepSkinName));
        lockedSepSkin = IFlexDisplayObject(s.getChildByName(vLockedSepSkinName));
        
        createThisSkin = useLockedSeparator ? lockedSepSkin : sepSkin;
        deleteThisSkin = useLockedSeparator ? sepSkin : lockedSepSkin;
        
        if (deleteThisSkin)
        {
            s.removeChild(DisplayObject(deleteThisSkin));
            //delete deleteThisSkin;
        }
        
        if (!createThisSkin)
        {
            var sepSkinClass:Class = Class(getStyle(createThisStyleName));
        
            if (sepSkinClass)
            {
                createThisSkin = IFlexDisplayObject(new sepSkinClass());
                createThisSkin.name = createThisSkinName;
                
                var styleableSkin:ISimpleStyleClient = createThisSkin as ISimpleStyleClient;
                if (styleableSkin)
                    styleableSkin.styleName = this;
                    
                s.addChild(DisplayObject(createThisSkin));
            }
        }
        
        if (createThisSkin)
        {
            var mWidth:Number = !isNaN(createThisSkin.measuredWidth) ? createThisSkin.measuredWidth : 1;
            createThisSkin.setActualSize(mWidth, s.parent.parent.height); 
            createThisSkin.move(x - Math.round(mWidth / 2), 0);      
        }
        else // If we still don't have a sepSkin, then we have no skin style defined. Use the default function instead
        {
            drawVerticalLine(s, colIndex, color, x);
        }
        
    }

    /**
     *  Draw lines between columns.  This implementation draws a line
     *  directly into the given Sprite.  The Sprite has been cleared
     *  before lines are drawn into it.
     *
     *  @param s A Sprite that will contain a display object
     *  that contains the graphics for that row.
     *
     *  @param columnIndex The column's index in the set of displayed columns.  
     *  The left most visible column has a column index of 0.
     *
     *  @param color The suggested color for the indicator
     * 
     *  @param x The suggested x position for the background
     */
    protected function drawVerticalLine(s:Sprite, colIndex:int, color:uint, x:Number):void
    {
        var contentHolder:ListBaseContentHolder = s.parent.parent as ListBaseContentHolder;
        //draw our vertical lines
        var g:Graphics = s.graphics;
        g.lineStyle(1, color, 100);
        g.moveTo(x, headerVisible ? 0 : 1);
        g.lineTo(x, contentHolder.height);
    }

    /**
     *  Draw lines between columns, and column backgrounds.
     *  This implementation calls the <code>drawHorizontalLine()</code>, 
     *  <code>drawVerticalLine()</code>,
     *  and <code>drawColumnBackground()</code> methods as needed.  
     *  It creates a
     *  Sprite that contains all of these graphics and adds it as a
     *  child of the listContent at the front of the z-order.
     */
    protected function drawLinesAndColumnBackgrounds():void
    {
        drawLinesAndColumnGraphics(listContent, visibleColumns, {});
    }

    /**
     *  Draw lines between columns, and column backgrounds.
     *  This implementation calls the <code>drawHorizontalLine()</code>, 
     *  <code>drawVerticalLine()</code>,
     *  and <code>drawColumnBackground()</code> methods as needed.  
     *  It creates a
     *  Sprite that contains all of these graphics and adds it as a
     *  child of the listContent at the front of the z-order.
     * 
     *  @param contentHolder A container of all of the DataGrid's item renderers and item editors.
     *  @param visibleColumns An array of the visible columns in the DataGrid.
     *  @param separators An object that defines the top, bottom, left, and right lines that separate the columns and rows.
     */
    protected function drawLinesAndColumnGraphics(contentHolder:ListBaseContentHolder, visibleColumns:Array, separators:Object):void
    {
        var lines:Sprite = Sprite(contentHolder.getChildByName("lines"));
        if (!lines)
        {
            lines = new UIComponent();
            lines.name = "lines";
            lines.cacheAsBitmap = true;
            lines.mouseEnabled = false;
            contentHolder.addChild(lines);
        }
        contentHolder.setChildIndex(lines, contentHolder.numChildren - 1);
        var rowInfo:Array = contentHolder.rowInfo;

        lines.graphics.clear();

        var linesBody:Sprite = Sprite(lines.getChildByName("body"));
        
        if (!linesBody)
        {
            linesBody = new UIComponent();
            linesBody.name = "body";
            linesBody.mouseEnabled = false;
            lines.addChild(linesBody);
        }
        
        linesBody.graphics.clear();
        while (linesBody.numChildren)
        {
            linesBody.removeChildAt(0);
        }


        var tmpHeight:Number = unscaledHeight - 1; // FIXME: can remove?
        var lineCol:uint;

        var i:int;

        var len:uint = visibleColumns ? visibleColumns.length : 0;
        var rowlen:uint = contentHolder.listItems.length

        // draw horizontalGridlines if needed.
        lineCol = getStyle("horizontalGridLineColor");
        if (getStyle("horizontalGridLines"))
        {
            for (i = 0; i < rowlen; i++)
            {
                var yy:Number = rowInfo[i].y + rowInfo[i].height;
                if (yy < contentHolder.height)
                    drawHorizontalSeparator(linesBody, i, lineCol, yy);
            }
        }
        if (separators.top)
            drawHorizontalSeparator(linesBody, i++, 0, rowInfo[0].y, true);
        if (separators.bottom && rowlen > 0)
            drawHorizontalSeparator(linesBody, i++, 0, rowInfo[rowlen - 1].y + rowInfo[rowlen - 1].height, true);

        var vLines:Boolean = getStyle("verticalGridLines");
        lineCol = getStyle("verticalGridLineColor");

        if (len)
        {
            var colBGs:Sprite = Sprite(contentHolder.getChildByName("colBGs"));
            // traverse the columns, set the sizes, draw the column backgrounds
            var lastChild:int = -1;
            var xx:Number = 0;
            for (i = 0; i < len; i++)
            {
                // only draw the vertical separator for the ones in the middle (not beginning and not end)
                if (vLines && i < (len - 1))
                    drawVerticalSeparator(linesBody, i, lineCol, xx + visibleColumns[i].width);

                var col:DataGridColumn = visibleColumns[i];
                var bgCol:Object;
                if (enabled)
                    bgCol = col.getStyle("backgroundColor");
                else
                    bgCol = col.getStyle("backgroundDisabledColor");

                if (bgCol !== null && !isNaN(Number(bgCol)))
                {
                    if (!colBGs)
                    {
                        colBGs = new FlexSprite();
                        colBGs.mouseEnabled = false;
                        colBGs.name = "colBGs";
                        contentHolder.addChildAt(colBGs, contentHolder.getChildIndex(contentHolder.getChildByName("rowBGs")) + 1);
                    }
                    drawColumnBackground(colBGs, i, Number(bgCol), col);
                    lastChild = i;
                }
                else if (colBGs)
                {
                    var background:Shape = Shape(colBGs.getChildByName(i.toString()));
                    if (background)
                    {
                        var g:Graphics = background.graphics;
                        g.clear();
                        colBGs.removeChild(background);
                    }
                }
                xx += visibleColumns[i].width;
            }
            if (colBGs && colBGs.numChildren)
            {
                while (colBGs.numChildren)
                {
                    var bg:DisplayObject = colBGs.getChildAt(colBGs.numChildren - 1);
                    if (parseInt(bg.name) > lastChild)
                        colBGs.removeChild(bg);
                    else
                        break;
                }
            }

        }

        if (separators.right && visibleColumns && visibleColumns.length)
        {
            if (contentHolder.listItems.length && contentHolder.listItems[0].length)
                drawVerticalSeparator(linesBody, i++, 0, contentHolder.listItems[0][len - 1].x + visibleColumns[len - 1].width, true);
            else
            {
                xx = 0;
                for (i = 0; i < len; i++)
                {
                    xx += visibleColumns[i].width;
                }
                drawVerticalSeparator(linesBody, i++, 0, xx, true);
            }
        }
        if (separators.left)
            drawVerticalSeparator(linesBody, i++, 0, 0, true);

    }

    mx_internal function _drawHeaderBackground(headerBG:UIComponent):void
    {
        drawHeaderBackground(headerBG);
    }

    /**
     *  Draws the background of the headers into the given 
     *  UIComponent.  The graphics drawn may be scaled horizontally
     *  if the component's width changes or this method will be
     *  called again to redraw at a different width and/or height
     *
     *  @param headerBG A UIComponent that will contain the header
     *  background graphics.
     */
    protected function drawHeaderBackground(headerBG:UIComponent):void
    {
        DataGridHeader(headerBG.parent)._drawHeaderBackground(headerBG);
    }

    mx_internal function _clearSeparators():void
    {
        clearSeparators();
    }

    /**
     *  Removes column header separators that the user normally uses
     *  to resize columns.
     */
    protected function clearSeparators():void
    {
        DataGridHeader(header)._clearSeparators();
        if (lockedColumnHeader)
            DataGridHeader(lockedColumnHeader)._clearSeparators();
    }

    mx_internal function _drawSeparators():void
    {
        drawSeparators();
    }

    /**
     *  Creates and displays the column header separators that the user 
     *  normally uses to resize columns.  This implementation uses
     *  the same Sprite as the lines and column backgrounds and adds
     *  instances of the <code>headerSeparatorSkin</code> and attaches mouse
     *  listeners to them in order to know when the user wants
     *  to resize a column.
     */
    protected function drawSeparators():void
    {
        DataGridHeader(header)._drawSeparators();
        if (lockedColumnHeader)
            DataGridHeader(lockedColumnHeader)._drawSeparators();
    }

    /**
     *  @private
     *  Update sortIndex and sortDirection based on sort info availabled in
     *  underlying data provider.
     */
    private function updateSortIndexAndDirection():void
    {
        // Don't show sort indicator if sortableColumns is false or if the
        // column sorted on has sortable="false"

        if (!sortableColumns)
        {
            lastSortIndex = sortIndex;
            sortIndex = -1;

            if (lastSortIndex != sortIndex)
                invalidateDisplayList();

            return;
        }

        if (!dataProvider)
            return;

        var view:ICollectionView = ICollectionView(dataProvider);
        var sort:Sort = view.sort;
        if (!sort)
        {
            sortIndex = lastSortIndex = -1;
            return;
        }

        var fields:Array = sort.fields;
        if (!fields)
            return;

        if (fields.length != 1)
        {
            lastSortIndex = sortIndex;
            sortIndex = -1;

            if (lastSortIndex != sortIndex)
                invalidateDisplayList();

            return;
        }

        // fields.length == 1, so the collection is sorted on a single field.
        var sortField:SortField = fields[0];
        var n:int = _columns.length;
        sortIndex = -1;
        for (var i:int = 0; i < n; i++)
        {
            if (_columns[i].dataField == sortField.name)
            {
                sortIndex = _columns[i].sortable ? i : -1;
                sortDirection = sortField.descending ? "DESC" : "ASC";
                return;
            }
        }
    }

    mx_internal function _placeSortArrow():void
    {
        placeSortArrow();
    }

    /**
     *  Draws the sort arrow graphic on the column that is the current sort key.
     *  This implementation creates or reuses an instance of the skin specified
     *  by <code>sortArrowSkin</code> style property and places 
     *  it in the appropriate column header.  It
     *  also shrinks the size of the column header if the text in the header
     *  would be obscured by the sort arrow.
     */
    protected function placeSortArrow():void
    {
        DataGridHeader(header)._placeSortArrow();
        if (lockedColumnHeader)
            DataGridHeader(lockedColumnHeader)._placeSortArrow();
    }

    /**
     *  @private
     */
    private function sortByColumn(index:int):void
    {
        var c:DataGridColumn = columns[index];
        var desc:Boolean = c.sortDescending;

        // do the sort if we're allowed to
        if (c.sortable)
        {
            var s:Sort = collection.sort;
            var f:SortField;
            if (s)
            {
                s.compareFunction = null;
                // analyze the current sort to see what we've been given
                var sf:Array = s.fields;
                if (sf)
                {
                    for (var i:int = 0; i < sf.length; i++)
                    {

                        if (sf[i].name == c.dataField)
                        {
                            // we're part of the current sort
                            f = sf[i]
                            // flip the logic so desc is new desired order
                            desc = !f.descending;
                            break;
                        }
                    }
                }
            }
            else
                s = new Sort;

            if (!f)
                f = new SortField(c.dataField);


            c.sortDescending = desc;
            var dir:String = (desc) ? "DESC" : "ASC";
            sortDirection = dir;

            // set the grid's sortIndex
            lastSortIndex = sortIndex;
            sortIndex = index;
            sortColumn = c;

            // if you have a labelFunction you must supply a sortCompareFunction
            f.name = c.dataField;
            if (c.sortCompareFunction != null)
            {
                f.compareFunction = c.sortCompareFunction;
            }
            else
            {
                f.compareFunction = null;
            }
            f.descending = desc;
            s.fields = [f];
        }
        collection.sort = s;
        collection.refresh();

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
     *  focus an item renderer in the grid - harder than it looks
     */
    private function commitEditedItemPosition(coord:Object):void
    {
        if (!enabled || !editable)
            return;

        if (!collection || collection.length == 0)
            return;

        // just give focus back to the itemEditorInstance
        if (itemEditorInstance && coord &&
            itemEditorInstance is IFocusManagerComponent &&
            _editedItemPosition.rowIndex == coord.rowIndex &&
            _editedItemPosition.columnIndex == coord.columnIndex)
        {
            IFocusManagerComponent(itemEditorInstance).setFocus();
            return;
        }

        // dispose of any existing editor, saving away its data first
        if (itemEditorInstance)
        {
            var reason:String;
            if (!coord)
            {
                reason = DataGridEventReason.OTHER;
            }
            else
            {
                reason = (!editedItemPosition || coord.rowIndex == editedItemPosition.rowIndex) ?
                         DataGridEventReason.NEW_COLUMN :
                         DataGridEventReason.NEW_ROW;
            }
            if (!endEdit(reason) && reason != DataGridEventReason.OTHER)
                return;
        }

        // store the value
        _editedItemPosition = coord;

        // allow setting of undefined to dispose item editor instance
        if (!coord)
            return;

        if (dontEdit)
        {
            return;
        }

        var rowIndex:int = coord.rowIndex;
        var colIndex:int = coord.columnIndex;
        if (displayableColumns.length != _columns.length)
        {
            for (var i:int = 0; i < displayableColumns.length; i++)
            {
                if (displayableColumns[i].colNum >= colIndex)
                {
                    colIndex = i;
                    break;
                }
            }
            if (i == displayableColumns.length)
                colIndex = 0;
        }

        // trace("commitEditedItemPosition ", coord.rowIndex, selectedIndex);

        var needChangeEvent:Boolean = false;
        if (selectedIndex != coord.rowIndex)
        {
            commitSelectedIndex(coord.rowIndex);
            needChangeEvent = true;
        }

        scrollToEditedItem(rowIndex, colIndex);

        // get the actual references for the column, row, and item
        var item:IListItemRenderer = actualContentHolder.listItems[actualRowIndex][actualColIndex];
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

        if (needChangeEvent)
        {
            var evt:ListEvent = new ListEvent(ListEvent.CHANGE);
            evt.columnIndex = coord.columnIndex;
            evt.rowIndex = coord.rowIndex;;
            evt.itemRenderer = item;
            dispatchEvent(evt);
        }

        var event:DataGridEvent =
            new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGIN, false, true);
            // ITEM_EDIT events are cancelable
        event.columnIndex = displayableColumns[colIndex].colNum;
        event.rowIndex = _editedItemPosition.rowIndex;
        event.itemRenderer = item;
        dispatchEvent(event);

        lastEditedItemPosition = _editedItemPosition;

        // user may be trying to change the focused item renderer
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

    // computes actualRowIndex, actualColIndex and actualContentHolder by
    // taking inputs for rowIndex, colIndex and scrolling to the right
    // place
    private function scrollToEditedItem(rowIndex:int, colIndex:int):void
    {
        actualContentHolder = listContent;
        var listItems:Array  = actualContentHolder.listItems;

        var lastRowIndex:int = verticalScrollPosition + listItems.length - 1 + lockedRowCount;
        var partialRow:int = (rowInfo[listItems.length - 1].y + rowInfo[listItems.length - 1].height > listContent.height) ? 1 : 0;

        // actual row/column is the offset into one of the containers
        if (rowIndex > lockedRowCount)
        {
            // not a locked editable row make sure it is on screen
            if (rowIndex < verticalScrollPosition + lockedRowCount)
                verticalScrollPosition = rowIndex - lockedRowCount;
            else
            {
                // variable row heights means that we can't know how far to scroll sometimes so we loop
                // until we get it right
                while (rowIndex > lastRowIndex ||
                    // we're the last row, and we're partially visible, but we're not
                    // the top scrollable row already
                    (rowIndex == lastRowIndex && rowIndex > verticalScrollPosition + lockedRowCount &&
                        partialRow))
                {
                    if (verticalScrollPosition == maxVerticalScrollPosition)
                        break;
                    verticalScrollPosition = Math.min(verticalScrollPosition + (rowIndex > lastRowIndex ? rowIndex - lastRowIndex : partialRow), maxVerticalScrollPosition);
                    lastRowIndex = verticalScrollPosition + listItems.length - 1 + lockedRowCount;
                    partialRow = (rowInfo[listItems.length - 1].y + rowInfo[listItems.length - 1].height > listContent.height) ? 1 : 0;
                }
            }

            actualRowIndex = rowIndex - verticalScrollPosition - lockedRowCount;

        }
        else
        {
            if (rowIndex == lockedRowCount)
            {
                verticalScrollPosition = 0;
                actualRowIndex = rowIndex - lockedRowCount;
            }
            else
            {
                if (lockedRowCount)
                    actualContentHolder = lockedRowContent;

                actualRowIndex = rowIndex;
            }
        }

        // reset since actualContentHolder could have changed
        listItems = actualContentHolder.listItems;

        var len:uint = (listItems && listItems[0]) ? listItems[0].length : visibleColumns.length;
        var lastColIndex:int = horizontalScrollPosition + len - 1 + lockedColumnCount;
        var partialCol:int = (listItems[0][len - 1].x + listItems[0][len - 1].width > listContent.width) ? 1 : 0;

        if (colIndex > lockedColumnCount)
        {
            if (colIndex < horizontalScrollPosition + lockedColumnCount)
                horizontalScrollPosition = colIndex - lockedColumnCount;
            else
            {
                while (colIndex > lastColIndex ||
                       (colIndex == lastColIndex && colIndex > horizontalScrollPosition + lockedColumnCount &&
                       partialCol))
                {
                    if (horizontalScrollPosition == maxHorizontalScrollPosition)
                        break;
                    horizontalScrollPosition = Math.min(horizontalScrollPosition + (colIndex > lastColIndex ? colIndex - lastColIndex : partialCol), maxHorizontalScrollPosition);
                    len = (listItems && listItems[0]) ? listItems[0].length : visibleColumns.length;
                    lastColIndex = horizontalScrollPosition + len - 1 + lockedColumnCount;
                    partialCol = (listItems[0][len - 1].x + listItems[0][len - 1].width > listContent.width) ? 1 : 0;
                }
            }
            actualColIndex = colIndex - horizontalScrollPosition - lockedColumnCount;
        }
        else
        {
            if (colIndex == lockedColumnCount)
            {
                horizontalScrollPosition = 0;
                actualColIndex = colIndex - lockedColumnCount;
            }
            else
            {
                if (lockedColumnCount)
                {
                    if (actualContentHolder == lockedRowContent)
                        actualContentHolder = lockedColumnAndRowContent;
                    else
                        actualContentHolder = lockedColumnContent;
                }

                actualColIndex = colIndex;
            }
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
     *  <p>You may only call this method from within the event listener
     *  for the <code>itemEditBegin</code> event. 
     *  To create an editor at other times, set the
     *  <code>editedItemPosition</code> property to generate 
     *  the <code>itemEditBegin</code> event.</p>
     *
     *  @param colIndex The column index in the data provider of the item to be edited.
     *
     *  @param rowIndex The row index in the data provider of the item to be edited.
     */
    public function createItemEditor(colIndex:int, rowIndex:int):void
    {
        if (displayableColumns.length != _columns.length)
        {
            for (var i:int = 0; i < displayableColumns.length; i++)
            {
                if (displayableColumns[i].colNum >= colIndex)
                {
                    colIndex = i;
                    break;
                }
            }
            if (i == displayableColumns.length)
                colIndex = 0;
        }

        var col:DataGridColumn = displayableColumns[colIndex];
        if (rowIndex >= lockedRowCount)
            rowIndex -= verticalScrollPosition + lockedRowCount;

        if (colIndex >= lockedColumnCount)
            colIndex -= horizontalScrollPosition + lockedColumnCount;

        var item:IListItemRenderer = actualContentHolder.listItems[rowIndex][colIndex];
        var rowData:ListRowInfo = actualContentHolder.rowInfo[rowIndex];

        if (!col.rendererIsEditor)
        {
            var dx:Number = 0;
            var dy:Number = -2;
            var dw:Number = 0;
            var dh:Number = 4;
            // if this isn't implemented, use an input control as editor
            if (!itemEditorInstance)
            {
                var itemEditor:IFactory = col.itemEditor;
                dx = col.editorXOffset;
                dy = col.editorYOffset;
                dw = col.editorWidthOffset;
                dh = col.editorHeightOffset;
                itemEditorInstance = itemEditor.newInstance();
                itemEditorInstance.owner = this;
                itemEditorInstance.styleName = col;
                actualContentHolder.addChild(DisplayObject(itemEditorInstance));
            }
            actualContentHolder.setChildIndex(DisplayObject(itemEditorInstance), actualContentHolder.numChildren - 1);
            // give it the right size, look and placement
            itemEditorInstance.visible = true;
            itemEditorInstance.move(item.x + dx, rowData.y + dy);
            itemEditorInstance.setActualSize(
                            Math.min(col.width + dw, 
                                     actualContentHolder.width - 1 - itemEditorInstance.x),
                            Math.min(rowData.height + dh, 
                                     actualContentHolder.height - itemEditorInstance.y));
            DisplayObject(itemEditorInstance).addEventListener(FocusEvent.FOCUS_OUT, itemEditorFocusOutHandler);
            item.visible = false;

        }
        else
        {
            // if the item renderer is also the editor, we'll use it
            itemEditorInstance = item;
        }

        // listen for keyStrokes on the itemEditorInstance (which lets the grid supervise for ESC/ENTER)
        DisplayObject(itemEditorInstance).addEventListener(KeyboardEvent.KEY_DOWN, editorKeyDownHandler);
        // we disappear on any mouse down outside the editor
        stage.addEventListener(MouseEvent.MOUSE_DOWN, editorMouseDownHandler, true, 0, true);
        // we disappear if stage is resized
        stage.addEventListener(Event.RESIZE, editorStageResizeHandler, true, 0, true);
    }

    /**
     *  @private
     *  Determines the next item renderer to navigate to using the Tab key.
     *  If the item renderer to be focused falls out of range (the end or beginning
     *  of the grid) then move focus outside the grid.
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

        var index:int = _editedItemPosition.rowIndex;
        var colIndex:int = _editedItemPosition.columnIndex;

        var found:Boolean = false;
        var incr:int = shiftKey ? -1 : 1;
        var maxIndex:int = collection.length - 1;

        // cycle till we find something worth focusing, or the end of the grid
        while (!found)
        {
            // go to next column
            colIndex += incr;
            if (colIndex >= _columns.length || colIndex < 0)
            {
                // if we fall off the end of the columns, wrap around
                colIndex = (colIndex < 0) ? _columns.length - 1 : 0;
                // and increment/decrement the row index
                index += incr;
                if (index > maxIndex || index < 0)
                {
                    if (endEdit(DataGridEventReason.NEW_ROW))
                    {
                        // if we've fallen off the rows, we need to leave the grid. get rid of the editor
                        setEditedItemPosition(null);
                        // set focus back to the grid so default handler will move it to the next component
                        losingFocus = true;
                        setFocus();
                        return false;
                    }
                    return true;
                }
            }
            // if we find a visible and editable column, move to it
            if (_columns[colIndex].editable && _columns[colIndex].visible)
            {
                found = true;
                // kill the old edit session
                var reason:String;
                reason = index == _editedItemPosition.rowIndex ?
                         DataGridEventReason.NEW_COLUMN :
                         DataGridEventReason.NEW_ROW;
                if (!itemEditorInstance || endEdit(reason))
                {
                    // send event to create the new one
                    var dataGridEvent:DataGridEvent =
                        new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
                        // ITEM_EDIT events are cancelable
                    dataGridEvent.columnIndex = colIndex;
                    dataGridEvent.dataField = _columns[colIndex].dataField;
                    dataGridEvent.rowIndex = index;
                    dispatchEvent(dataGridEvent);
                }
            }
        }
        return found;
    }

    /**
     *  This method closes an item editor currently open on an item renderer. 
     *  You typically only call this method from within the event listener 
     *  for the <code>itemEditEnd</code> event, after
     *  you have already called the <code>preventDefault()</code> method to 
     *  prevent the default event listener from executing.
     */
    public function destroyItemEditor():void
    {
        // trace("destroyItemEditor");
        if (itemEditorInstance)
        {
            DisplayObject(itemEditorInstance).removeEventListener(KeyboardEvent.KEY_DOWN, editorKeyDownHandler);
            if (stage)
            {
                stage.removeEventListener(MouseEvent.MOUSE_DOWN, editorMouseDownHandler, true);
                stage.removeEventListener(Event.RESIZE, editorStageResizeHandler, true);
            }

            var event:DataGridEvent =
                new DataGridEvent(DataGridEvent.ITEM_FOCUS_OUT);
            event.columnIndex = _editedItemPosition.columnIndex;
            event.rowIndex = _editedItemPosition.rowIndex;
            event.itemRenderer = itemEditorInstance;
            dispatchEvent(event);

            if (! _columns[_editedItemPosition.columnIndex].rendererIsEditor)
            {
                // FocusManager.removeHandler() does not find
                // itemEditors in focusableObjects[] array
                // and hence does not remove the focusRectangle
                if (itemEditorInstance && itemEditorInstance is UIComponent)
                    UIComponent(itemEditorInstance).drawFocus(false);

                // must call removeChild() so FocusManager.lastFocus becomes null
                actualContentHolder.removeChild(DisplayObject(itemEditorInstance));
                editedItemRenderer.visible = true;
            }
            itemEditorInstance = null;
            _editedItemPosition = null;
        }
    }

    /**
     *  @private
     *  When the user finished editing an item, this method is called.
     *  It dispatches the DataGridEvent.ITEM_EDIT_END event to start the process
     *  of copying the edited data from
     *  the itemEditorInstance to the data provider and hiding the itemEditorInstance.
     *  returns true if nobody called preventDefault.
     */
    private function endEdit(reason:String):Boolean
    {
        // this happens if the renderer is removed asynchronously ususally with FDS
        if (!editedItemRenderer)
            return true;

        inEndEdit = true;

        var dataGridEvent:DataGridEvent =
            new DataGridEvent(DataGridEvent.ITEM_EDIT_END, false, true);
            // ITEM_EDIT events are cancelable
        dataGridEvent.columnIndex = editedItemPosition.columnIndex;
        dataGridEvent.dataField = _columns[editedItemPosition.columnIndex].dataField;
        dataGridEvent.rowIndex = editedItemPosition.rowIndex;
        dataGridEvent.itemRenderer = editedItemRenderer;
        dataGridEvent.reason = reason;
        dispatchEvent(dataGridEvent);
        // set a flag to not open another edit session if the item editor is still up
        // this means somebody wants the old edit session to stay.
        dontEdit = itemEditorInstance != null;
        // trace("dontEdit", dontEdit);

        if (!dontEdit && reason == DataGridEventReason.CANCELLED)
        {
            losingFocus = true;
            setFocus();
        }

        inEndEdit = false;

        return !(dataGridEvent.isDefaultPrevented())
    }

    /**
     *  Determines whether to allow editing of a dataprovider item on a per-row basis. 
     *  The default implementation of this method only checks the <code>editable</code> property 
     *  of the DataGrid and returns <code>false</code> if <code>editable</code> is <code>false</code>
     *  or if the dataprovider item is <code>null</code>.
     * 
     *  <p>This method can be overridden to allow fine-grained control of which dataprovider items 
     *  are editable. For example, if you want to disallow editing of grouping rows or summary rows
     *  you would override this method with custom logic to this behavior.</p>
     *
     *  @param data The data provider item. The default implementation of this method returns 
     *  <code>false</code> if the data object is <code>null</code>.
     * 
     *  @return The default behavior is to return <code>true</code> if the DataGrid's <code>editable</code> property is 
     *  <code>true</code> and the data object is not <code>null</code>.
     */
    public function isItemEditable(data:Object):Boolean
    {
        if (!editable)
            return false;

        if (!data)
            return false;

        return true;
    }

    /**
     *  @private
     */
    override mx_internal function columnRendererChanged(c:DataGridColumn):void
    {
        var item:DisplayObject;
        
        var factories:Dictionary = c.measuringObjects;
        if (factories)
        {
            for (var p:* in factories)
            {
                var factory:IFactory = IFactory(p);
                item = c.measuringObjects[factory];
                if (item)
                {
                    item.parent.removeChild(item);
                    c.measuringObjects[factory] = null;
                }

                if (c.freeItemRenderersByFactory && c.freeItemRenderersByFactory[factory])
                {
                    var d:Dictionary = c.freeItemRenderersByFactory[factory];
                    for (var q:* in d)
                    {
                        item = DisplayObject(q);
                        item.parent.removeChild(item);
                    }
                    c.freeItemRenderersByFactory[factory] = new Dictionary(true);
                }
            }
            var freeRenderers:Array = freeItemRenderersTable[c] as Array;
            if (freeRenderers)
            {
                while (freeRenderers.length)
                {
                    item = freeRenderers.pop();
                }
            }
        }

        rendererChanged = true;
        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Catches any events from the model. Optimized for editing one item.
     *  Creates columns when there are none. Inherited from list.
     *  @param eventObj
     */
    override protected function collectionChangeHandler(event:Event):void
    {
        if (event is CollectionEvent)
        {
            var curEditedItemPosition:Object;
            var ceEvent:CollectionEvent = CollectionEvent(event)
            if (ceEvent.kind == CollectionEventKind.RESET)
            {
                if (generatedColumns)
                    generateCols();
                else
                    columnsInvalid = true;
                updateSortIndexAndDirection();
                if (lockedRowContent)
                    lockedRowContent.iterator = collection.createCursor();
                if (lockedColumnAndRowContent)
                    lockedColumnAndRowContent.iterator = collection.createCursor();
            }
            else if (ceEvent.kind == CollectionEventKind.REFRESH && !manualSort)
                updateSortIndexAndDirection();
            else
            {
                // if we get a add while editing adjust the editPosition
                if (ceEvent.kind == CollectionEventKind.ADD)
                {
                    if (editedItemPosition)
                    {
                        if (ceEvent.location <= editedItemPosition.rowIndex)
                        {
                            curEditedItemPosition = editedItemPosition;

                            if (inEndEdit)
                                _editedItemPosition = { columnIndex : editedItemPosition.columnIndex, 
                                                    rowIndex : Math.max(0, editedItemPosition.rowIndex + ceEvent.items.length)};
                            else if (itemEditorInstance)
                            {
                                _editedItemPosition = { columnIndex : editedItemPosition.columnIndex, 
                                                        rowIndex : Math.max(0, editedItemPosition.rowIndex + ceEvent.items.length)};
                                itemEditorPositionChanged = true;
                                lastEditedItemPosition = _editedItemPosition;
                            }
                            else
                                setEditedItemPosition({ columnIndex : curEditedItemPosition.columnIndex, 
                                                    rowIndex : Math.max(0, curEditedItemPosition.rowIndex + ceEvent.items.length)});
                        }
                    }
                }
                // if we get a remove while editing adjust the editPosition
                else if (ceEvent.kind == CollectionEventKind.REMOVE)
                {
                    if (editedItemPosition)
                    {
                        if (collection.length == 0)
                        {
                            if (itemEditorInstance)
                                endEdit(DataGridEventReason.CANCELLED);
                            setEditedItemPosition(null); // nothing left to edit
                        }
                        else if (ceEvent.location <= editedItemPosition.rowIndex)
                        {
                            curEditedItemPosition = editedItemPosition;

                            // if the editor is up on the item going away, cancel the session
                            if (ceEvent.location == editedItemPosition.rowIndex && itemEditorInstance)
                                endEdit(DataGridEventReason.CANCELLED);

                            if (inEndEdit)
                                _editedItemPosition = { columnIndex : editedItemPosition.columnIndex, 
                                                    rowIndex : Math.max(0, editedItemPosition.rowIndex - ceEvent.items.length)};
                            else if (itemEditorInstance)
                            {
                                _editedItemPosition = { columnIndex : editedItemPosition.columnIndex, 
                                                        rowIndex : Math.max(0, editedItemPosition.rowIndex - ceEvent.items.length)};
                                itemEditorPositionChanged = true;
                                lastEditedItemPosition = _editedItemPosition;
                            }
                            else
                                setEditedItemPosition({ columnIndex : curEditedItemPosition.columnIndex, 
                                                    rowIndex : Math.max(0, curEditedItemPosition.rowIndex - ceEvent.items.length)});
                        }
                    }
                }
                else if (ceEvent.kind == CollectionEventKind.REPLACE)
                {
                    if (editedItemPosition)
                    {
                        // if the editor is up on the item going away, cancel the session
                        if (ceEvent.location == editedItemPosition.rowIndex && itemEditorInstance)
                            endEdit(DataGridEventReason.CANCELLED);
                    }
                }
            }
        }

        super.collectionChangeHandler(event);

        if (event is CollectionEvent)
        {
            // trace("ListBase collectionEvent");
            var ce:CollectionEvent = CollectionEvent(event);
            if (ce.kind == CollectionEventKind.ADD)
            {
                // added first item, generate columns for it if needed
                if (collection.length == 1)
                    if (generatedColumns)
                        generateCols();
            }
            else if (ce.kind == CollectionEventKind.REFRESH)
            {
                // refresh locked row count iterator
                if (lockedRowCount && lockedRowContent)
                    lockedRowContent.iterator.seek(CursorBookmark.FIRST, 0);
            }
        }


//      if (event.eventName != "sort" && bRowsChanged)
//          invInitHeaders = true;
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

        lastItemDown = null;

        var isItemEditor:Boolean = itemRendererContains(itemEditorInstance, DisplayObject(event.target));

        // If it isn't an item renderer, or an item editor do default behavior
        if (!isItemEditor)
        {
            var pos:Point;
            if (r)
            {
                lastItemDown = r;

                pos = itemRendererToIndices(r);

                var bEndedEdit:Boolean = true;

                if (itemEditorInstance)
                {
                    if (displayableColumns[pos.x].editable == false)
                        bEndedEdit = endEdit(DataGridEventReason.OTHER);
                    else
                        bEndedEdit = endEdit(editedItemPosition.rowIndex == pos.y ?
                                         DataGridEventReason.NEW_COLUMN :
                                         DataGridEventReason.NEW_ROW);
                }

                // if we didn't end edit session, don't do default behavior (call super)
                if (!bEndedEdit)
                    return;
            }
            else
            {
                // trace("end edit?");
                if (itemEditorInstance)
                    endEdit(DataGridEventReason.OTHER);
            }

            super.mouseDownHandler(event);

            if (r)
            {
                if (displayableColumns[pos.x].rendererIsEditor)
                    resetDragScrolling();
            }
        }
        else
            resetDragScrolling();
        // trace("<<mouseDownHandler");
    }

    /**
     *  @private
     */
    override protected function mouseUpHandler(event:MouseEvent):void
    {
        var dataGridEvent:DataGridEvent;
        var r:IListItemRenderer;
        var s:Sprite;
        var n:int;
        var i:int;
        var pos:Point;

        r = mouseEventToItemRenderer(event);

        super.mouseUpHandler(event);

        if (r && r != itemEditorInstance && lastItemDown == r)
        {
            pos = itemRendererToIndices(r);

            if (pos && pos.y >= 0 && editable && !dontEdit)
            {
                if (displayableColumns[pos.x].editable)
                {
                    dataGridEvent = new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
                    // ITEM_EDIT events are cancelable
                    dataGridEvent.columnIndex = displayableColumns[pos.x].colNum;
                    dataGridEvent.dataField = displayableColumns[pos.x].dataField;
                    dataGridEvent.rowIndex = pos.y;
                    dataGridEvent.itemRenderer = r;
                    dispatchEvent(dataGridEvent);
                }
                else
                    // if the item is not editable, set lastPosition to it any
                    // so future tabbing starts from there
                    lastEditedItemPosition = { columnIndex: pos.x, rowIndex: pos.y };
            }
        }
        else if (lastItemDown && lastItemDown != itemEditorInstance)
        {
            pos = itemRendererToIndices(lastItemDown);

            if (pos && pos.y >= 0 && editable && !dontEdit)
            {
                if (displayableColumns[pos.x].editable)
                {
                    dataGridEvent = new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
                    // ITEM_EDIT events are cancelable
                    dataGridEvent.columnIndex = displayableColumns[pos.x].colNum;
                    dataGridEvent.dataField = displayableColumns[pos.x].dataField;
                    dataGridEvent.rowIndex = pos.y;
                    dataGridEvent.itemRenderer = lastItemDown;
                    dispatchEvent(dataGridEvent);
                }
                else
                    // if the item is not editable, set lastPosition to it any
                    // so future tabbing starts from there
                    lastEditedItemPosition = { columnIndex: pos.x, rowIndex: pos.y };
            }
        }

        lastItemDown = null;
    }

    /**
     *  @private
     *  when the grid gets focus, focus an item renderer
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        // trace(">>DGFocusIn ", selectedIndex);
        var dataGridEvent:DataGridEvent;

        if (losingFocus)
        {
            losingFocus = false;
            // trace("losing focus via tab");
            // trace("<<DGFocusIn ");
            return;
        }

        if (editable)
        {
            addEventListener(FocusEvent.KEY_FOCUS_CHANGE, keyFocusChangeHandler);
            addEventListener(MouseEvent.MOUSE_DOWN, mouseFocusChangeHandler);
        }

        if (event.target != this)
        {
            if (itemEditorInstance && itemRendererContains(itemEditorInstance, DisplayObject(event.target)))
            {
                // trace("item editor got focus ignoring");
                // trace("<<DGFocusIn ");
                return;
            }
            // find renderer for target
            var target:DisplayObject = DisplayObject(event.target);
            while (target && target != this)
            {
                if (target is IListItemRenderer && target.parent.parent == this && target.parent is ListBaseContentHolder)
                {
                    if (target.visible)
                        break;
                }

                if (target is IUIComponent)
                    target = IUIComponent(target).owner;
                else 
                    target = target.parent;
            }
            if (target)
            {
                var pos:Point = itemRendererToIndices(IListItemRenderer(target));
                if (pos && pos.y >= 0 && editable && displayableColumns[pos.x].editable && !dontEdit)
                {
                    dataGridEvent = new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
                    // ITEM_EDIT events are cancelable
                    dataGridEvent.columnIndex = displayableColumns[pos.x].colNum;
                    dataGridEvent.dataField = displayableColumns[pos.x].dataField;
                    dataGridEvent.rowIndex = pos.y;
                    dataGridEvent.itemRenderer = IListItemRenderer(target);
                    dispatchEvent(dataGridEvent);
                }
            }
            // trace("subcomponent got focus ignoring");
            // trace("<<DGFocusIn ");
            return;
        }

        super.focusInHandler(event);

        // isPressed is not correct if we switch in from a different window
        // via mouseDown.  The activation calls focusIn (if we had focus
        // when we lost activation) before mouseDown
        if (editable && !isPressed) // don't do this if we're mouse focused
        {
            _editedItemPosition = lastEditedItemPosition;

            var foundOne:Boolean = false;

            // start somewhere
            if (!_editedItemPosition)
                _editedItemPosition = { rowIndex: 0, columnIndex: 0 };
    

            for (;
                 _editedItemPosition.columnIndex != _columns.length;
                 _editedItemPosition.columnIndex++)
            {
                // If the editedItemPosition is valid, focus it,
                // otherwise find one.
                if (_columns[_editedItemPosition.columnIndex].editable &&
                    _columns[_editedItemPosition.columnIndex].visible)
                {
                    foundOne = true;
                    break;
                }
            }

            if (foundOne)
            {
                // trace("setting focus", _editedItemPosition.columnIndex, _editedItemPosition.rowIndex);
                dataGridEvent = new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
                // ITEM_EDIT events are cancelable
                dataGridEvent.columnIndex = _editedItemPosition.columnIndex;
                dataGridEvent.dataField = _columns[_editedItemPosition.columnIndex].dataField;
                dataGridEvent.rowIndex = _editedItemPosition.rowIndex;
                dispatchEvent(dataGridEvent);
            }

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
            super.focusOutHandler(event);

        // just leave if item editor is losing focus back to grid.  Usually happens
        // when someone clicks out of the editor onto a new item renderer.
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
            // find renderer for target
            var target:DisplayObject = DisplayObject(event.relatedObject);
            while (target && target != this)
            {
                if (target is IListItemRenderer && target.parent.parent == this && target.parent is ListBaseContentHolder)
                {
                    if (target.visible)
                    {
                        // losing focus to another renderer
                        // let other logic end the session
                        return;
                    }
                }

                if (target is IUIComponent)
                    target = IUIComponent(target).owner;
                else 
                    target = target.parent;
            }

            // trace("call endEdit from focus out");
            endEdit(DataGridEventReason.OTHER);
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
        // if stage losing activation, set focus to DG so when we get it back
        // we popup an editor again
        if (itemEditorInstance)
        {
            endEdit(DataGridEventReason.OTHER);
            losingFocus = true;
            setFocus();
        }
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (itemEditorInstance || event.target != event.currentTarget)
            return;

        if (event.keyCode != Keyboard.SPACE)
            super.keyDownHandler(event);
        else if (caretIndex != -1)
        {
            moveSelectionVertically(event.keyCode, event.shiftKey, event.ctrlKey);
        }
    }

    /**
     *  @private
     *  used by ListBase.findString.  Shouldn't be used elsewhere
     *  because column's itemToLabel is preferred
     */
    override public function itemToLabel(data:Object):String
    {
        return displayableColumns[sortIndex == -1 ? 0 : sortIndex].itemToLabel(data);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------


    /**
     *  @private
     */
    private function editorMouseDownHandler(event:MouseEvent):void
    {
        if (!owns(DisplayObject(event.target)))
            endEdit(DataGridEventReason.OTHER);
    }

    /**
     *  @private
     */
    private function editorKeyDownHandler(event:KeyboardEvent):void
    {
        // ESC just kills the editor, no new data
        if (event.keyCode == Keyboard.ESCAPE)
        {
            endEdit(DataGridEventReason.CANCELLED);
        }
        else if (event.ctrlKey && event.charCode == 46)
        {   // Check for Ctrl-.
            endEdit(DataGridEventReason.CANCELLED);
        }
        else if (event.charCode == Keyboard.ENTER && event.keyCode != 229)
        {
            // multiline editors can take the enter key.
            if (columns[_editedItemPosition.columnIndex].editorUsesEnterKey)
                return;

            // Enter edits the item, moves down a row
            // The 229 keyCode is for IME compatability. When entering an IME expression,
            // the enter key is down, but the keyCode is 229 instead of the enter key code.
            // Thanks to Yukari for this little trick...
            if (endEdit(DataGridEventReason.NEW_ROW) && !dontEdit)
                findNextEnterItemRenderer(event);
        }
    }

    /**
     *  @private
     */
    private function editorStageResizeHandler(event:Event):void
    {
        if (event.target is DisplayObjectContainer &&
            DisplayObjectContainer(event.target).contains(this))
            endEdit(DataGridEventReason.OTHER);
    }

    /**
     *  @private
     *  find the next item renderer down from the currently edited item renderer, and focus it.
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
        var dataGridEvent:DataGridEvent =
            new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING, false, true);
            // ITEM_EDIT events are cancelable
        dataGridEvent.columnIndex = columnIndex;
        dataGridEvent.dataField = _columns[columnIndex].dataField;
        dataGridEvent.rowIndex = rowIndex;
        dispatchEvent(dataGridEvent);
    }

    /**
     *  @private
     *  This gets called when the tab key is hit.
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
     *  This gets called when the tab key is hit.
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
     *  Hides the itemEditorInstance.
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
            endEdit(DataGridEventReason.OTHER);
    }

    /**
     *  @private
     */
    private function itemEditorItemEditBeginningHandler(event:DataGridEvent):void
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
     *  focus an item renderer in the grid - harder than it looks
     */
    private function itemEditorItemEditBeginHandler(event:DataGridEvent):void
    {
        // weak reference for deactivation
        if (stage)
            stage.addEventListener(Event.DEACTIVATE, deactivateHandler, false, 0, true);

        // if not prevented and if data is not null (might be from dataservices)
        if (!event.isDefaultPrevented() && actualContentHolder.listItems[actualRowIndex][actualColIndex].data != null)
        {
            createItemEditor(event.columnIndex, event.rowIndex);

            if (editedItemRenderer is IDropInListItemRenderer && itemEditorInstance is IDropInListItemRenderer)
                IDropInListItemRenderer(itemEditorInstance).listData = IDropInListItemRenderer(editedItemRenderer).listData;
            // if rendererIsEditor, don't apply the data as the data may have already changed in some way.
            // This can happen if clicking on a checkbox rendererIsEditor as the checkbox will try to change
            // its value as we try to stuff in an old value here.
            if (!columns[event.columnIndex].rendererIsEditor)
                itemEditorInstance.data = editedItemRenderer.data;

            if (itemEditorInstance is IInvalidating)
                IInvalidating(itemEditorInstance).validateNow();

            if (itemEditorInstance is IIMESupport)
                IIMESupport(itemEditorInstance).imeMode =
                    (columns[event.columnIndex].imeMode == null) ? _imeMode : columns[event.columnIndex].imeMode;

            var fm:IFocusManager = focusManager;
            // trace("setting focus to item editor");
            if (itemEditorInstance is IFocusManagerComponent)
                fm.setFocus(IFocusManagerComponent(itemEditorInstance));
            fm.defaultButtonEnabled = false;

            var event:DataGridEvent =
                new DataGridEvent(DataGridEvent.ITEM_FOCUS_IN);
            event.columnIndex = _editedItemPosition.columnIndex;
            event.rowIndex = _editedItemPosition.rowIndex;
                event.itemRenderer = itemEditorInstance;
            dispatchEvent(event);
        }
    }

    /**
     *  @private
     */
    private function itemEditorItemEditEndHandler(event:DataGridEvent):void
    {
        if (!event.isDefaultPrevented())
        {
            var bChanged:Boolean = false;

            if (event.reason == DataGridEventReason.NEW_COLUMN)
            {
                if (!collectionUpdatesDisabled)
                {
                    collection.disableAutoUpdate();
                    collectionUpdatesDisabled = true;
                }
            }
            else
            {
                if (collectionUpdatesDisabled)
                {
                    collection.enableAutoUpdate();
                    collectionUpdatesDisabled = false;
                }
            }

            if (itemEditorInstance && event.reason != DataGridEventReason.CANCELLED)
            {
                var newData:Object = itemEditorInstance[_columns[event.columnIndex].editorDataField];
                var property:String = _columns[event.columnIndex].dataField;
                var data:Object = event.itemRenderer.data;
                var typeInfo:String = "";
                for each(var variable:XML in describeType(data).variable)
                {
                    if (property == variable.@name.toString())
                    {
                        typeInfo = variable.@type.toString();
                        break;
                    }
                }

                if (typeInfo == "String")
                {
                    if (!(newData is String))
                        newData = newData.toString();
                }
                else if (typeInfo == "uint")
                {
                    if (!(newData is uint))
                        newData = uint(newData);
                }
                else if (typeInfo == "int")
                {
                    if (!(newData is int))
                        newData = int(newData);
                }
                else if (typeInfo == "Number")
                {
                    if (!(newData is int))
                        newData = Number(newData);
                }
                if (property != null && data[property] !== newData)
                {
                    bChanged = true;
                    data[property] = newData;
                }
                if (bChanged && !(data is IPropertyChangeNotifier || data is XML))
                {
                    collection.itemUpdated(data, property);
                }
                if (event.itemRenderer is IDropInListItemRenderer)
                {
                    var listData:DataGridListData = DataGridListData(IDropInListItemRenderer(event.itemRenderer).listData);
                    listData.label = _columns[event.columnIndex].itemToLabel(data);
                    IDropInListItemRenderer(event.itemRenderer).listData = listData;
                }
                event.itemRenderer.data = data;
            }
        }
        else
        {
            if (event.reason != DataGridEventReason.OTHER)
            {
                if (itemEditorInstance && _editedItemPosition)
                {
                    // edit session is continued so restore focus and selection
                    if (selectedIndex != _editedItemPosition.rowIndex)
                        selectedIndex = _editedItemPosition.rowIndex;
                    var fm:IFocusManager = focusManager;
                    // trace("setting focus to itemEditorInstance", selectedIndex);
                    if (itemEditorInstance is IFocusManagerComponent)
                        fm.setFocus(IFocusManagerComponent(itemEditorInstance));
                }
            }
        }

        if (event.reason == DataGridEventReason.OTHER || !event.isDefaultPrevented())
        {
            destroyItemEditor();
        }
    }

    /**
     *  @private
     */
    private function headerReleaseHandler(event:DataGridEvent):void
    {
        if (!event.isDefaultPrevented())
        {
            manualSort = true;
            sortByColumn(event.columnIndex);
            manualSort = false;
        }
    }

    /**
     *  @private
     */
    override protected function mouseWheelHandler(event:MouseEvent):void
    {
        if (itemEditorInstance)
            endEdit(DataGridEventReason.OTHER);

        super.mouseWheelHandler(event);
    }

    /**
     *  @private
     *  if some drags from the same row as an editor we can be left
     *  with updates disabled
     */
    override protected function dragStartHandler(event:DragEvent):void
    {
        if (collectionUpdatesDisabled)
        {
            collection.enableAutoUpdate();
            collectionUpdatesDisabled = false;
        }
        super.dragStartHandler(event);
    }

    mx_internal function get vScrollBar():ScrollBar
    {
        return verticalScrollBar;
    }

    /** 
     *  diagnostics
     */
    override mx_internal function get rendererArray():Array 
    {
        var arr:Array = listItems.slice();
        var arr2:Array = DataGridHeader(header).rendererArray;
        arr.unshift(arr2);
        return arr;
    }

    /** 
     *  diagnostics
     */
    mx_internal function get sortArrow():IFlexDisplayObject 
    {
        return DataGridHeader(header).sortArrow;
    }

    /**
     *  Called from the <code>updateDisplayList()</code> method to adjust the size and position of
     *  listContent.
     *  
     *  @param unscaledWidth The width of the listContent. This value ignores changes to the width from external components or settings.
     *  @param unscaledHeight The height of the listContent. This value ignores changes to the height from external components or settings.
     */
    override protected function adjustListContent(unscaledWidth:Number = -1,
                                       unscaledHeight:Number = -1):void
    {
        var ww:Number;
        var hh:Number = 0;
        var lcx:Number;
        var lcy:Number;
        var hcx:Number;

        if (headerVisible)
        {
            if (lockedColumnCount > 0)
            {
                lockedColumnHeader.visible = true;
                hcx = viewMetrics.left + Math.min(DataGridHeader(lockedColumnHeader).leftOffset, 0);
                lockedColumnHeader.move(hcx, viewMetrics.top);
                hh = lockedColumnHeader.getExplicitOrMeasuredHeight();
                lockedColumnHeader.setActualSize(lockedColumnWidth + 1, hh);
                DataGridHeader(lockedColumnHeader).needRightSeparator = true;
                DataGridHeader(lockedColumnHeader).needRightSeparatorEvents = true;
            }
            header.visible = true;
            hcx = viewMetrics.left + lockedColumnWidth + Math.min(DataGridHeader(header).leftOffset, 0);
            header.move(hcx, viewMetrics.top);
            // If we have a vScroll only, we want the scrollbar to be below
            // the header.
            if (verticalScrollBar != null && verticalScrollBar.visible &&
               (horizontalScrollBar == null || !horizontalScrollBar.visible) && headerVisible &&
               roomForScrollBar(verticalScrollBar, unscaledWidth, unscaledHeight-header.height))
                ww = Math.max(0, DataGridHeader(header).rightOffset) - hcx - borderMetrics.right;
            else
                ww = Math.max(0, DataGridHeader(header).rightOffset) - hcx - viewMetrics.right;
            hh = header.getExplicitOrMeasuredHeight();
            header.setActualSize(unscaledWidth + ww, hh);
            if (!skipHeaderUpdate)
            {
                header.headerItemsChanged = true;
                header.invalidateDisplayList(); // make sure it redraws, even if size didn't change
                // internal renderers could have changed
            }
        }
        else
        {
            header.visible = false;
            if (lockedColumnCount > 0)
                lockedColumnHeader.visible = false;
        }

        if (lockedRowCount > 0 && lockedRowContent && lockedRowContent.iterator)
        {
            try
            {
                lockedRowContent.iterator.seek(CursorBookmark.FIRST);
                var pt:Point = makeRows(lockedRowContent, 0, 0, unscaledWidth, unscaledHeight, 0, 0, true, lockedRowCount, true);

                if (lockedColumnCount > 0)
                {
                    lcx = viewMetrics.left + Math.min(lockedColumnAndRowContent.leftOffset, 0);
                    lcy = viewMetrics.top + Math.min(lockedColumnAndRowContent.topOffset, 0) + Math.ceil(hh);
                    lockedColumnAndRowContent.move(lcx, lcy);
                    lockedColumnAndRowContent.setActualSize(lockedColumnWidth, lockedColumnAndRowContent.getExplicitOrMeasuredHeight());
                }
                lcx = viewMetrics.left + lockedColumnWidth + Math.min(lockedRowContent.leftOffset, 0);
                lcy = viewMetrics.top + Math.min(lockedRowContent.topOffset, 0) + Math.ceil(hh);
                lockedRowContent.move(lcx, lcy);
                ww = Math.max(0, lockedRowContent.rightOffset) - lcx - viewMetrics.right;
                lockedRowContent.setActualSize(unscaledWidth + ww, lockedRowContent.getExplicitOrMeasuredHeight());
                hh += lockedRowContent.getExplicitOrMeasuredHeight();
            }
            catch (e:ItemPendingError)
            {
                e.addResponder(new ItemResponder(lockedRowSeekPendingResultHandler, seekPendingFailureHandler,
                                                    null));

            }
        }

        if (lockedColumnCount > 0)
        {
            lcx = viewMetrics.left + Math.min(lockedColumnContent.leftOffset, 0);
            lcy = viewMetrics.top + Math.min(lockedColumnContent.topOffset, 0) + Math.ceil(hh);
            lockedColumnContent.move(lcx, lcy);
            ww = lockedColumnWidth + lockedColumnContent.rightOffset - lockedColumnContent.leftOffset;
            lockedColumnContent.setActualSize(ww, 
                    unscaledHeight + Math.max(0, lockedColumnContent.bottomOffset) - lcy - viewMetrics.bottom);
        }
        lcx = viewMetrics.left + lockedColumnWidth + Math.min(listContent.leftOffset, 0);
        lcy = viewMetrics.top + Math.min(listContent.topOffset, 0) + Math.ceil(hh);
        listContent.move(lcx, lcy);
        ww = Math.max(0, listContent.rightOffset) - lcx - viewMetrics.right;
        hh = Math.max(0, listContent.bottomOffset) - lcy - viewMetrics.bottom;
        listContent.setActualSize(Math.max(0, unscaledWidth + ww), Math.max(0, unscaledHeight + hh));

    }

    private function lockedRowSeekPendingResultHandler(data:Object,
                                                info:ListBaseSeekPending):void
    {
        try 
        {
            lockedRowContent.iterator.seek(CursorBookmark.FIRST);
        }
        catch(e:ItemPendingError)
        {
            e.addResponder(new ItemResponder(lockedRowSeekPendingResultHandler, seekPendingFailureHandler,
                                                null));
        }
        itemsSizeChanged = true;
        invalidateDisplayList();
    }


    /**
     *  @inheritDoc
     */
    override protected function scrollPositionToIndex(horizontalScrollPosition:int,
                                             verticalScrollPosition:int):int
    {
        return iterator ? verticalScrollPosition + lockedRowCount : -1;
    }

    /**
     *  @inheritDoc
     */
    override protected function scrollVertically(pos:int, deltaPos:int,
                                        scrollUp:Boolean):void
    {
        super.scrollVertically(pos, deltaPos, scrollUp);
        if (getStyle("horizontalGridLines"))
        {
            drawLinesAndColumnGraphics(listContent, visibleColumns, {});
            if (lockedColumnCount)
            {
                drawLinesAndColumnGraphics(lockedColumnContent, visibleLockedColumns, { right: 1})
            }
        }
    }

    private var _focusPane:Sprite;

    /**
     *  @private
     */
    override public function set focusPane(value:Sprite):void
    {
        super.focusPane = value;
        if (!value && _focusPane)
            _focusPane.mask = null;
        _focusPane = value;
    }

    mx_internal var lockedColumnDropIndicator:IFlexDisplayObject;

    /**
     *  @private
     */
    override public function showDropFeedback(event:DragEvent):void
    {
        super.showDropFeedback(event);

        if (lockedColumnCount > 0)
        {
            if (!lockedColumnDropIndicator)
            {
                var dropIndicatorClass:Class = getStyle("dropIndicatorSkin");
                if (!dropIndicatorClass)
                    dropIndicatorClass = ListDropIndicator;
                lockedColumnDropIndicator = IFlexDisplayObject(new dropIndicatorClass());

                lockedColumnDropIndicator.x = 2;
                lockedColumnDropIndicator.setActualSize(lockedColumnContent.width - 2, 4);
                lockedColumnDropIndicator.visible = true;
            }
            if (dropIndicator.parent == listContent)
                lockedColumnContent.addChild(DisplayObject(lockedColumnDropIndicator));
            else
                lockedColumnAndRowContent.addChild(DisplayObject(lockedColumnDropIndicator));


            lockedColumnDropIndicator.y = dropIndicator.y;
        }
    }

    /**
     *  @private
     */
    override public function hideDropFeedback(event:DragEvent):void
    {
        super.hideDropFeedback(event);

        if (lockedColumnDropIndicator)
        {
            DisplayObject(lockedColumnDropIndicator).parent.removeChild(DisplayObject(lockedColumnDropIndicator));
            lockedColumnDropIndicator = null;
        }
    }

}

}
