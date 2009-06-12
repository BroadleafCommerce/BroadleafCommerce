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

package mx.printing
{

import flash.events.KeyboardEvent;
import mx.controls.DataGrid;
import mx.core.EdgeMetrics;
import mx.core.ScrollPolicy;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

//--------------------------------------
//  public properties
//--------------------------------------

[Exclude(name="allowDragSelection", kind="property")]
[Exclude(name="allowMultipleSelection", kind="property")]
[Exclude(name="dataTipField", kind="property")]
[Exclude(name="dataTipFunction", kind="property")]
[Exclude(name="doubleClickEnabled", kind="property")]
[Exclude(name="dragEnabled", kind="property")]
[Exclude(name="draggableColumns", kind="property")]
[Exclude(name="dragMoveEnabled", kind="property")]
[Exclude(name="dropEnabled", kind="property")]
[Exclude(name="dropTarget", kind="property")]
[Exclude(name="editable", kind="property")]
[Exclude(name="editedItemPosition", kind="property")]
[Exclude(name="editedItemRenderer", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="scrollTipFunction", kind="property")]
[Exclude(name="selectable", kind="property")]
[Exclude(name="selectedIndex", kind="property")]
[Exclude(name="selectedIndices", kind="property")]
[Exclude(name="selectedItem", kind="property")]
[Exclude(name="selectedItems", kind="property")]
[Exclude(name="showScrollTips", kind="property")]
[Exclude(name="toolTip", kind="property")]
[Exclude(name="useHandCursor", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]

//--------------------------------------
//  protected properties
//--------------------------------------

[Exclude(name="anchorBookmark", kind="property")]
[Exclude(name="anchorIndex", kind="property")]
[Exclude(name="caretBookmark", kind="property")]
[Exclude(name="caretIndex", kind="property")]
[Exclude(name="caretIndicator", kind="property")]
[Exclude(name="caretItemRenderer", kind="property")]
[Exclude(name="caretUID", kind="property")]
[Exclude(name="dragImage", kind="property")]
[Exclude(name="dragImageOffsets", kind="property")]
[Exclude(name="highlightIndicator", kind="property")]
[Exclude(name="highlightUID", kind="property")]
[Exclude(name="keySelectionPending", kind="property")]
[Exclude(name="lastDropIndex", kind="property")]
[Exclude(name="selectionLayer", kind="property")]
[Exclude(name="selectionTweens", kind="property")]
[Exclude(name="showCaret", kind="property")]

//--------------------------------------
//  public methods
//--------------------------------------

[Exclude(name="calculateDropIndex", kind="method")]
[Exclude(name="createItemEditor", kind="method")]
[Exclude(name="destroyItemEditor", kind="method")]
[Exclude(name="effectFinished", kind="method")]
[Exclude(name="effectStarted", kind="method")]
[Exclude(name="endEffectStarted", kind="method")]
[Exclude(name="hideDropFeedback", kind="method")]
[Exclude(name="isItemHighlighted", kind="method")]
[Exclude(name="isItemSelected", kind="method")]
[Exclude(name="showDropFeedback", kind="method")]
[Exclude(name="startDrag", kind="method")]

//--------------------------------------
//  protected methods
//--------------------------------------

[Exclude(name="dragCompleteHandler", kind="method")]
[Exclude(name="dragDropHandler", kind="method")]
[Exclude(name="dragEnterHandler", kind="method")]
[Exclude(name="dragExitHandler", kind="method")]
[Exclude(name="dragOverHandler", kind="method")]
[Exclude(name="dragScroll", kind="method")]
[Exclude(name="drawCaretIndicator", kind="method")]
[Exclude(name="drawHighlightIndicator", kind="method")]
[Exclude(name="drawSelectionIndicator", kind="method")]
[Exclude(name="mouseClickHandler", kind="method")]
[Exclude(name="mouseDoubleClickHandler", kind="method")]
[Exclude(name="mouseDownHandler", kind="method")]
[Exclude(name="mouseEventToItemRenderer", kind="method")]
[Exclude(name="mouseMoveHandler", kind="method")]
[Exclude(name="mouseOutHandler", kind="method")]
[Exclude(name="mouseOverHandler", kind="method")]
[Exclude(name="mouseUpHandler", kind="method")]
[Exclude(name="mouseWheelHandler", kind="method")]
[Exclude(name="moveSelectionHorizontally", kind="method")]
[Exclude(name="moveSelectionVertically", kind="method")]
[Exclude(name="placeSortArrow", kind="method")]
[Exclude(name="removeIndicators", kind="method")]
[Exclude(name="selectItem", kind="method")]
[Exclude(name="setScrollBarProperties", kind="method")]

//--------------------------------------
//  events
//--------------------------------------

[Exclude(name="click", kind="event")]
[Exclude(name="doubleClick", kind="event")]
[Exclude(name="dragComplete", kind="event")]
[Exclude(name="dragDrop", kind="event")]
[Exclude(name="dragEnter", kind="event")]
[Exclude(name="dragExit", kind="event")]
[Exclude(name="dragOver", kind="event")]
[Exclude(name="effectEnd", kind="event")]
[Exclude(name="effectStart", kind="event")]
[Exclude(name="headerRelease", kind="event")]
[Exclude(name="itemClick", kind="event")]
[Exclude(name="itemDoubleClick", kind="event")]
[Exclude(name="itemEditBegin", kind="event")]
[Exclude(name="itemEditBeginning", kind="event")]
[Exclude(name="itemEditEnd", kind="event")]
[Exclude(name="itemFocusIn", kind="event")]
[Exclude(name="itemFocusOut", kind="event")]
[Exclude(name="itemRollOut", kind="event")]
[Exclude(name="itemRollOver", kind="event")]
[Exclude(name="keyDown", kind="event")]
[Exclude(name="keyUp", kind="event")]
[Exclude(name="mouseDown", kind="event")]
[Exclude(name="mouseDownOutside", kind="event")]
[Exclude(name="mouseFocusChange", kind="event")]
[Exclude(name="mouseMove", kind="event")]
[Exclude(name="mouseOut", kind="event")]
[Exclude(name="mouseOver", kind="event")]
[Exclude(name="mouseUp", kind="event")]
[Exclude(name="mouseWheel", kind="event")]
[Exclude(name="mouseWheelOutside", kind="event")]
[Exclude(name="rollOut", kind="event")]
[Exclude(name="rollOver", kind="event")]
[Exclude(name="toolTipCreate", kind="event")]
[Exclude(name="toolTipEnd", kind="event")]
[Exclude(name="toolTipHide", kind="event")]
[Exclude(name="toolTipShow", kind="event")]
[Exclude(name="toolTipShown", kind="event")]
[Exclude(name="toolTipStart", kind="event")]

//--------------------------------------
//  styles
//--------------------------------------

[Exclude(name="columnDropIndicatorSkin", kind="style")]
[Exclude(name="columnResizeSkin", kind="style")]
[Exclude(name="dropIndicatorSkin", kind="style")]
[Exclude(name="headerDragProxyStyleName", kind="style")]
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="rollOverColor", kind="style")]
[Exclude(name="selectionColor", kind="style")]
[Exclude(name="selectionDisabledColor", kind="style")]
[Exclude(name="selectionDuration", kind="style")]
[Exclude(name="selectionEasingFunction", kind="style")]
[Exclude(name="strechCursor", kind="style")]
[Exclude(name="textRollOverColor", kind="style")]
[Exclude(name="textSelectedColor", kind="style")]
[Exclude(name="useRollOver", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

//--------------------------------------
//  effects
//--------------------------------------

[Exclude(name="addedEffect", kind="effect")]
[Exclude(name="creationCompleteEffect", kind="effect")]
[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]
[Exclude(name="hideEffect", kind="effect")]
[Exclude(name="mouseDownEffect", kind="effect")]
[Exclude(name="mouseUpEffect", kind="effect")]

[Exclude(name="moveEffect", kind="effect")]
[Exclude(name="removedEffect", kind="effect")]
[Exclude(name="resizeEffect", kind="effect")]
[Exclude(name="rollOutEffect", kind="effect")]
[Exclude(name="rollOverEffect", kind="effect")]
[Exclude(name="showEffect", kind="effect")]

/**
 *  The PrintDataGrid control is a DataGrid subclass that is styled
 *  to show a table with line borders and is optimized for printing. 
 *  It can automatically size to properly fit its container, and removes 
 *  any partially displayed rows.
 *
 *  @mxml
 * 
 *  <p>The <code>&lt;mx:PrintDataGrid&gt;</code> tag inherits the tag attributes
 *  of its superclass; however, you do not use the properties, styles, events,
 *  and effects (or methods) associated with user interaction.
 *  The <code>&lt;mx:PrintDataGrid&gt;</code> tag adds the following tag attribute:
 *  </p>
 *  <pre>
 *  &lt;mx:PrintDataGrid
 *    <b>Properties</b>
 *    sizeToPage="true|false"
 *  &gt; 
 *  ...
 *  &lt;/mx:PrintDataGrid&gt;
 *  </pre>
 * 
 *  @see FlexPrintJob
 * 
 *  @includeExample examples/FormPrintHeader.mxml -noswf
 *  @includeExample examples/FormPrintFooter.mxml -noswf
 *  @includeExample examples/FormPrintView.mxml -noswf
 *  @includeExample examples/PrintDataGridExample.mxml
 */
public class PrintDataGrid extends DataGrid
{
	include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  <p>Constructs a DataGrid without scrollbars or user interactivity:
	 *  column sorting, resizing, drag scrolling, selection, or keyboard
	 *  interaction.
	 *  The default height is 100% of the container height, or the height 
	 *  required to display all the dataProvider rows, whichever is smaller.</p>
	 */
	public function PrintDataGrid()
	{
		super();

		horizontalScrollPolicy = ScrollPolicy.OFF;
		verticalScrollPolicy = ScrollPolicy.OFF;
		sortableColumns = false;
		selectable = false;
		// to disable dragScrolling
		dragEnabled = true;
		resizableColumns = false;
		super.percentHeight = 100;
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  height
    //----------------------------------

    [Bindable("heightChanged")]
    [Inspectable(category="General")]
	[PercentProxy("percentHeight")]

    /**
	 *  @private
	 *  Getter needs to be overridden if setter is overridden.
	 */
    override public function get height():Number
	{
		return super.height;
	}

	/**
	 *  @private
	 *  Height setter needs to be overridden to update _originalHeight.
	 */
	override public function set height(value:Number):void
	{
		_originalHeight = value;
		if (!isNaN(percentHeight))
		{
			super.percentHeight = NaN;
			measure();
			value = measuredHeight;
		}
		
		super.height = value;
		
		invalidateDisplayList();

		if (sizeToPage && !isNaN(explicitHeight))
			explicitHeight = NaN;
	}

    //----------------------------------
	//  percentHeight
    //----------------------------------

    [Bindable("resize")]
    [Inspectable(category="Size", defaultValue="NaN")]
    /**
	 *  @private
	 *  Getter needs to be overridden if setter is overridden.
	 */
    override public function get percentHeight():Number
    {
        return super.percentHeight;
    }

    /**
     *  @private
     *  percentHeight setter needs to be overridden to update _originalHeight.
     */
    override public function set percentHeight(value:Number):void
    {
		_originalHeight = NaN;
		super.percentHeight = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  currentPageHeight
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the currentPageHeight property.
	 */
	private var _currentPageHeight:Number;

	/**
	 *  The height of PrintDataGrid that would be, if <code>sizeToPage</code> 
	 *  property is <code>true</code> and PrintDataGrid displays only completely
	 *  viewable rows and no partial rows. If <code>sizeToPage</code> property 
	 *  is <code>true</code>, the value of this property equals 
	 *  the <code>height</code> property.
	 */
	public function get currentPageHeight():Number
	{
		return _currentPageHeight;
	}

    //----------------------------------
	//  originalHeight
    //----------------------------------

	/**
	 *  Storage for the originalHeight property.
	 *  @private
	 */
	private var _originalHeight:Number;

	/**
	 *  The height of PrintDataGrid as set by the user.
	 *  If the <code>sizeToPage</code> property is <code>false</code>,
	 *  the value of this property equals the <code>height</code> property.
	 */
	public function get originalHeight():Number
	{
		return _originalHeight;
	}

    //----------------------------------
	//  sizeToPage
    //----------------------------------

	/**
	 *  If <code>true</code>, the PrintDataGrid readjusts its height to display
	 *  only completely viewable rows.
	 */
	public var sizeToPage:Boolean = true;

    //----------------------------------
	//  validNextPage
    //----------------------------------

	/**
	 *  Indicates the data provider contains additional data rows that follow 
	 *  the rows that the PrintDataGrid control currently displays.
	 *
	 *  @return A Boolean value of <code>true</code> if a set of rows is 
	 *  available else <code>false</false>.
	 */
	public function get validNextPage():Boolean
	{
		var vPos:int = verticalScrollPosition + rowCount;

		return dataProvider && vPos < dataProvider.length ? true : false;
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Sets the default number of display rows to dataProvider.length.
	 */
	override protected function measure():void
	{
		var oldRowCount:uint = rowCount;

		var count:uint = (dataProvider) ? dataProvider.length : 0;

		if (count >= verticalScrollPosition)
			count -= verticalScrollPosition;
		else
			count = 0;

		if (headerVisible)
			count++;

		setRowCount(count);

		// need to calculate rowCount before super()
		super.measure();
		measureHeight();

		if (isNaN(_originalHeight))
			_originalHeight = measuredHeight;
		_currentPageHeight = measuredHeight;

		if (!sizeToPage)
		{
			setRowCount(oldRowCount);
			super.measure();
		}
	}

	/**
	 *  @private
	 *  setActualSize() is overridden to update _originalHeight.
	 */
	override public function setActualSize(w:Number, h:Number):void
	{
		if (!isNaN(percentHeight))
		{
			_originalHeight = h;
			super.percentHeight = NaN;
			measure();
			h = measuredHeight;
		}

		super.setActualSize(w, h);
		
		invalidateDisplayList();

		if (sizeToPage && !isNaN(explicitHeight))
			explicitHeight = NaN;
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: ListBase
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Overridden configureScrollBars to disable autoScrollUp.
	 */
	override protected function configureScrollBars():void
	{
	}

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  Puts the next set of data rows in view;
	 *  that is, it sets the PrintDataGrid <code>verticalScrollPosition</code>
	 *  property to equal <code>verticalScrollPosition</code> + (number of scrollable rows).
	 */
	public function nextPage():void
	{
		verticalScrollPosition += rowCount - lockedRowCount;

		invalidateSize();
		invalidateDisplayList();
	}

	/**
	 *  @private
	 *  ListBase.measure() does'nt calculate measuredHeight in required way
	 *  so have to add the code here.
	 */
	private function measureHeight():void
	{
		if (dataProvider && dataProvider.length > 0 
			&& (verticalScrollPosition >= dataProvider.length))
		{
			setRowCount(0);
			measuredHeight = 0;
			measuredMinHeight = 0;
			return;
		}

		var o:EdgeMetrics = viewMetrics;
        var rc:int = (explicitRowCount < 1) ? rowCount : explicitRowCount;

        var maxHeight:Number = isNaN(_originalHeight) ? -1 
        						: _originalHeight - o.top - o.bottom;

		measuredHeight = measureHeightOfItemsUptoMaxHeight(
			-1, rc, maxHeight) + o.top + o.bottom;

		measuredMinHeight = measureHeightOfItemsUptoMaxHeight(
			-1, Math.min(rc, 2), maxHeight) + o.top + o.bottom;
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Overridden keyDown to disable keyboard functionality.
	 */
	override protected function keyDownHandler(event:KeyboardEvent):void
	{
	}
}

}
