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

package mx.controls.listClasses
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.utils.Dictionary;
import flash.utils.setInterval;
import mx.collections.CursorBookmark;
import mx.collections.ItemResponder;
import mx.collections.errors.ItemPendingError;
import mx.controls.scrollClasses.ScrollBar;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.FlexShape;
import mx.core.FlexSprite;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.events.ListEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.events.ScrollEventDirection;
import mx.skins.halo.ListDropIndicator;
import mx.styles.StyleManager;
import mx.collections.ItemWrapper;
import mx.collections.ModifiedCollectionView;

use namespace mx_internal;

/**
 *  The TileBase class is the base class for controls
 *  that display data items in a sequence of rows and columns.
 *  TileBase-derived classes ignore the <code>variableRowHeight</code>
 *  and <code>wordWrap</code> properties inherited from their parent class.
 *  All items in a TileList are the same width and height.
 *
 *  <p>This class is not used directly in applications.</p>
 */
public class TileBase extends ListBase
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function TileBase()
    {
        super();

        itemRenderer = new ClassFactory(TileListItemRenderer);

        // Set default sizes.
        setRowHeight(50);
        setColumnWidth(50);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    // These three keep track of the key selection that caused
    // the page fault.
    
    private var bShiftKey:Boolean = false;
    
    private var bCtrlKey:Boolean = false;
    
    private var lastKey:uint = 0;
    
    private var bSelectItem:Boolean = false;

    private var lastColumnCount:int = 0;
    private var lastRowCount:int = 0;

    /**
     *  Cache of measuring objects by factory.
     */
    protected var measuringObjects:Dictionary;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  direction
    //----------------------------------

    /**
     *  @private
     *  Storage for direction property.
     */
    private var _direction:String = TileBaseDirection.HORIZONTAL;

    [Bindable("directionChanged")]
    [Inspectable(category="General", enumeration="vertical,horizontal", defaultValue="horizontal")]

    /**
     *  The direction in which this control lays out its children.
     *  Possible values are <code>TileBaseDirection.HORIZONTAL</code>
     *  and <code>TileBaseDirection.VERTICAL</code>.
     *  The default value is <code>TileBaseDirection.HORIZONTAL</code>.
     *
     *  <p>If the value is <code>TileBaseDirection.HORIZONTAL</code>, the tiles are
     *  laid out along the first row until the number of visible columns or maxColumns
     *  is reached and then a new row is started.  If more rows are created
     *  than can be displayed at once, the control will display a vertical scrollbar.
     *  The opposite is true if the value is <code>TileBaseDirection.VERTICAL</code>.</p>
     */
    public function get direction():String
    {
        return _direction;
    }

    /**
     *  @private
     */
    public function set direction(value:String):void
    {
        _direction = value;

        itemsSizeChanged = true;
        offscreenExtraRowsOrColumnsChanged = true;

        if (listContent)
        {           
            if (direction == TileBaseDirection.HORIZONTAL)
            {
                listContent.leftOffset = listContent.rightOffset = 0;
                offscreenExtraColumnsLeft = offscreenExtraColumnsRight = 0;
            }
            else
            {
                listContent.topOffset = listContent.bottomOffset = 0;
                offscreenExtraRowsTop = offscreenExtraRowsBottom = 0;
            }
        }
        invalidateProperties();

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("directionChanged"));
    }

    //----------------------------------
    //  maxColumns
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxColumns property.
     */
    private var _maxColumns:int = 0;

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The maximum number of columns that the control can have.
     *  If 0, then there are no limits to the number of
     *  columns.  This value is ignored
     *  if the direction is <code>TileBaseDirection.VERTICAL</code>
     *  because the control will have as many columns as it needs to 
     *  to display all the data.
     *
     *  <p>The default value is 0 (no limit).</p>
     */
    public function get maxColumns():int
    {
        return _maxColumns;
    }

    /**
     *  @private
     */
    public function set maxColumns(value:int):void
    {
        if (_maxColumns != value)
        {
            _maxColumns = value;

            invalidateSize();
            invalidateDisplayList();
        }
    }

    //----------------------------------
    //  maxRows
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxRows property.
     */
    private var _maxRows:int = 0;

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  The maximum number of rows that the control can have.
     *  If 0, then there is no limit to the number of
     *  rows.  This value is ignored
     *  if the direction is <code>TileBaseDirection.HORIZONTAL</code>
     *  because the control will have as many rows as it needs to 
     *  to display all the data.
     *
     *  <p>The default value is 0 (no limit).</p>
     */
    public function get maxRows():int
    {
        return _maxRows;
    }

    /**
     *  @private
     */
    public function set maxRows(value:int):void
    {
        if (_maxRows != value)
        {
            _maxRows = value;

            invalidateSize();
            invalidateDisplayList();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        listContent.mask = maskShape;
    }

    /**
     *  @private
     */
    override protected function makeRowsAndColumns(left:Number, top:Number,
                                                right:Number, bottom:Number,
                                                firstCol:int, firstRow:int,
                                                byCount:Boolean = false, rowsNeeded:uint = 0):Point
    {
         //trace(this, "makeRowsAndColumns " + left + " " + top + " " + right + " " + bottom + " " + firstCol + " " + firstRow);

        var numRows:int;
        var numCols:int;
        var colNum:int;
        var rowNum:int;
        var xx:Number;
        var yy:Number;
        var wrappedData:Object;
        var data:Object;
        var uid:String
        var oldItem:IListItemRenderer 
        var item:IListItemRenderer;
        var more:Boolean;
        var valid:Boolean;
        var i:int;
        var rh:Number;
        var lastRowMade:int;
        var lastColumnMade:int;
        
        var bSelected:Boolean = false;
        var bHighlight:Boolean = false;
        var bCaret:Boolean = false;

//      trace("TileBase.makeRowsAndColumns, horizontalScrollPosition = " + horizontalScrollPosition +
//           ", iterator index = " + iterator.bookmark.getViewIndex() + ", iterator current = " + 
//           iterator.current);
             
        if (columnWidth == 0 || rowHeight == 0)
            return null;
            
        invalidateSizeFlag = true;
        allowItemSizeChangeNotification = false;

        if (direction == TileBaseDirection.VERTICAL)
        {
            numRows = maxRows > 0 ? maxRows : Math.max(Math.floor(listContent.heightExcludingOffsets / rowHeight), 1);
            numCols = Math.max(Math.ceil((listContent.widthExcludingOffsets)/ columnWidth), 1);
            setRowCount(numRows);
            setColumnCount(numCols);
            colNum = firstCol;
            xx = left;

            lastColumnMade = colNum - 1;
            more = (iterator != null && !iterator.afterLast && iteratorValid);

            while ((byCount && rowsNeeded--) || (!byCount && (colNum < numCols + firstCol)))
            {
                rowNum = firstRow;
                yy = top;
                while (rowNum < numRows)
                {
                    valid = more;
                    wrappedData = more ? iterator.current : null;
                    data = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;
                    more = moveNextSafely(more);

                    if (!listItems[rowNum])
                        listItems[rowNum] = [];

                    if (valid && yy < bottom)
                    {
                        uid = itemToUID(wrappedData);
                        rowInfo[rowNum] = new ListRowInfo(yy, rowHeight, uid);
                        item = getPreparedItemRenderer(rowNum, colNum, wrappedData, data, uid);
                        placeAndDrawItemRenderer(item,xx,yy,uid);
                        lastColumnMade = Math.max(colNum,lastColumnMade);
                    }
                    else
                    {
                        oldItem = listItems[rowNum][colNum];
                        if (oldItem)
                        {
                            addToFreeItemRenderers(oldItem);
//                          delete rowMap[oldItem.name];
                            listItems[rowNum][colNum] = null;
                        }
                        rowInfo[rowNum] = new ListRowInfo(yy, rowHeight, uid);
                    }
                    yy += rowHeight;
                    rowNum++;
                }
                colNum ++;
                if (firstRow)
                {
                    // we're doing a row along the bottom so we have to skip the beginning of the next column
                    for (i = 0; i < firstRow; i++)
                        more = moveNextSafely(more);
                }
                xx += columnWidth;
            }
        }
        else // horizontal
        {
            numCols = maxColumns > 0 ? maxColumns : Math.max(Math.floor((listContent.widthExcludingOffsets)/ columnWidth), 1);
            numRows = Math.max(Math.ceil(listContent.heightExcludingOffsets / rowHeight), 1);
            setColumnCount(numCols);
            setRowCount(numRows);
            rowNum = firstRow;
            yy = top;
            more = (iterator != null && !iterator.afterLast && iteratorValid);

            lastRowMade = rowNum-1;

            while ((byCount && rowsNeeded--) || (!byCount && rowNum < numRows + firstRow))
            {
                colNum = firstCol;
                xx = left;
                rowInfo[rowNum] = null;

                while (colNum < numCols)
                {
                    valid = more;
                    wrappedData = more ? iterator.current : null;
                    data = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;
                    more = moveNextSafely(more);

                    if (!listItems[rowNum])
                        listItems[rowNum] = [];

                    if (valid && xx < right)
                    {
                        uid = itemToUID(wrappedData);

                        if (!rowInfo[rowNum])
                            rowInfo[rowNum] = new ListRowInfo(yy, rowHeight, uid);
                        item = getPreparedItemRenderer(rowNum, colNum, wrappedData, data, uid);
                        placeAndDrawItemRenderer(item,xx,yy,uid);
                        lastRowMade = rowNum;
                    }
                    else
                    {
                        if (!rowInfo[rowNum])
                            rowInfo[rowNum] = new ListRowInfo(yy, rowHeight, uid);
                        oldItem = listItems[rowNum][colNum];
                        if (oldItem)
                        {
                            addToFreeItemRenderers(oldItem);
                            listItems[rowNum][colNum] = null;
                        }
                    }

                    xx += columnWidth;
                    colNum++;
                }
                rowNum ++;
                if (firstCol)
                {
                    // we're doing a column along the side so we have to skip the beginning of the next column
                    for (i = 0; i < firstCol; i++)
                        more = moveNextSafely(more);
                }
                yy += rowHeight;
            }
        }

        if (!byCount)
        {
            var a:Array;
            // prune excess rows and columns
            while (listItems.length > numRows + offscreenExtraRowsTop)
            {
                a = listItems.pop();
                rowInfo.pop();
                for (i = 0; i < a.length; i++)
                {
                    oldItem = a[i];
                    if (oldItem)
                    {
                        addToFreeItemRenderers(oldItem);
                    }
                }
            }
            if (listItems.length && listItems[0].length > numCols + offscreenExtraColumnsLeft)
            {
                for (i = 0; i < numRows + offscreenExtraRowsTop; i++)
                {
                    a = listItems[i];
                    while (a.length > numCols + offscreenExtraColumnsLeft)
                    {
                        oldItem = a.pop();
                        if (oldItem)
                        {
                            addToFreeItemRenderers(oldItem);
                        }
                    }
                }
            }
        }

        allowItemSizeChangeNotification = true;
        invalidateSizeFlag = false;

        return new Point(lastColumnMade - firstCol + 1,lastRowMade - firstRow + 1);
    }

    private function moveNextSafely(more:Boolean):Boolean
    {
        if (iterator && more)
        {
            try 
            {
                more = iterator.moveNext();
            }
            catch(e1:ItemPendingError)
            {
                lastSeekPending = new ListBaseSeekPending(CursorBookmark.CURRENT, 0);
                e1.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler, 
                                            lastSeekPending));
                more = false;
                iteratorValid = false;
            }
        }
        
        return more;
    }

    /**
     *  @private
     */
    private function getPreparedItemRenderer(rowNum:int,colNum:int, wrappedData:Object, 
                                             data:Object, uid:String):IListItemRenderer
    {
        var oldItem:IListItemRenderer = listItems[rowNum][colNum];
        var item:IListItemRenderer;
        var rowData:ListData;

        if (oldItem)
        {
            // If we're running a data effect, do a more expensive check when
            // determining if we can reuse this item renderer
            if (runningDataEffect ? (dataItemWrappersByRenderer[oldItem] != wrappedData) : (oldItem.data != data))
                addToFreeItemRenderers(oldItem);
            else
                item = oldItem;
        }

        if (!item)
        {
            // if we're allowed to re-use existing renderers
            if (allowRendererStealingDuringLayout)
            {
                // try to steal item renderer if it already exists,
                // but don't steal item renderers that have already
                // been used in the layout. (This will happen if there
                // are duplicate UIDs in the collection, which shouldn't
                // really happen, but nevertheless may happen).
                item = visibleData[uid];
                
                // if we can't steal an item based on it's UID,
                // steal based on the UID of the underlying data
                if (!item && (wrappedData != data))
                    item = visibleData[itemToUID(data)];
            }

            if (item) // if we've stolen a renderer from somewhere else...
            {
                // update data structures so we're not pointing to it twice
                var ld:ListData = ListData(rowMap[item.name]);
                if (ld)
                {
                    if (((direction == TileBaseDirection.HORIZONTAL) && 
                         ((ld.rowIndex > rowNum) || ((ld.rowIndex == rowNum) && (ld.columnIndex > colNum)))) ||
                        ((direction == TileBaseDirection.VERTICAL) && 
                         ((ld.columnIndex > colNum) || ((ld.columnIndex == colNum) && (ld.rowIndex > rowNum)))))
                        listItems[ld.rowIndex][ld.columnIndex] = null;
                    else
                        item = null;
                }
            }

            if (!item)
            {
                item = getReservedOrFreeItemRenderer(wrappedData);
                if (item && !isRendererUnconstrained(item))
                {
                    item.x = 0;
                    item.y = 0;
                }
            }
            // if all else fails...
            if (!item)
                item = createItemRenderer(data);
            item.owner = this;
            item.styleName = listContent;
            item.visible = true;
        }

        rowData = ListData(makeListData(data, uid, rowNum, colNum));
        rowMap[item.name] = rowData;
        if (item is IDropInListItemRenderer)
            IDropInListItemRenderer(item).listData = data ? rowData : null;
        item.data = data;
        if (wrappedData != data)
            dataItemWrappersByRenderer[item] = wrappedData;

        if (!item.parent)
            listContent.addChild(DisplayObject(item));
        item.visible = true;
        if (uid)
            visibleData[uid] = item;
        listItems[rowNum][colNum] = item;
        UIComponentGlobals.layoutManager.validateClient(item, true);

        return item;
    }   
    
    /**
     *  @private
     */
    private function placeAndDrawItemRenderer(item:IListItemRenderer, xx:Number, yy:Number, uid:String):void
    {
        var bSelected:Boolean = false;
        var bHighlight:Boolean = false;
        var bCaret:Boolean = false;
        var rh:Number;

        rh = item.getExplicitOrMeasuredHeight();
        if (item.width != columnWidth || rh != (rowHeight - cachedPaddingTop - cachedPaddingBottom))
            item.setActualSize(columnWidth, rowHeight - cachedPaddingTop - cachedPaddingBottom);
            // this is not really doing anything yet
        if (!isRendererUnconstrained(item))
            item.move(xx, yy + cachedPaddingTop);
        bSelected = selectedData[uid] != null;
        if (runningDataEffect)
        {
            bSelected = bSelected || (selectedData[itemToUID(item.data)] != null);
            bSelected = bSelected && (!getRendererSemanticValue(item,ModifiedCollectionView.REPLACEMENT))
                && (!getRendererSemanticValue(item,ModifiedCollectionView.ADDED));
        }
        bHighlight = highlightUID == uid;
        bCaret = caretUID == uid;
        if (uid)
            drawItem(item, bSelected, bHighlight, bCaret);
    }
    
    /**
     *  @private
     */
    override protected function configureScrollBars():void
    {
        var rowCount:int = listItems.length;
        if (rowCount == 0)
            return;
        
        var colCount:int = listItems[0].length;
        if (colCount == 0)
            return;

        if (rowCount > 1 && (rowCount - offscreenExtraRowsTop - offscreenExtraRowsBottom) * rowHeight > listContent.heightExcludingOffsets)
            rowCount--;
                    
        rowCount -= (offscreenExtraRowsTop + offscreenExtraRowsBottom);

        if (colCount > 1 && (colCount - offscreenExtraColumnsLeft - offscreenExtraColumnsRight) * columnWidth > listContent.widthExcludingOffsets)
            colCount--;
            
        colCount -= (offscreenExtraColumnsLeft + offscreenExtraColumnsRight);
        
        var oldHorizontalScrollBar:Object = horizontalScrollBar;
        var oldVerticalScrollBar:Object = verticalScrollBar;

        var numRows:int;
        var numCols:int;
        var index:int;
        
        if (direction == TileBaseDirection.VERTICAL)
        {
            // handle extra blank items at end of list.  This is the equivalent
            // of adjustVerticalScrollPositionDownward in List.as
            if (iteratorValid && horizontalScrollPosition > 0)
            {
                var fillerCols:int = 0;
                // adjust colCount for null items
                // note that we should never have offscreenExtraColumnsRight > 0 if there are columns filled with null items
                while ((colCount > 0) && listItems[0][colCount + offscreenExtraColumnsLeft - 1] == null)
                {
                    colCount--
                    fillerCols++;
                }
                // If we have pinned the scroll position while running a data effect, we can
                // sometimes end up without null filler columns, but still needing to adjust the
                // scroll position
                var expectedFullColumns:int = Math.floor((listContent.widthExcludingOffsets)/ columnWidth);
                var extraEmptyColumns:int = Math.max(0,expectedFullColumns - (colCount + fillerCols));
                
                if (fillerCols || extraEmptyColumns)
                {
                    for (var i:int = 0; i < listItems.length; i++)
                        while (listItems[i].length > colCount + offscreenExtraColumnsLeft)
                            (listItems[i] as Array).pop();
                            
                    if (!runningDataEffect)
                    {
                        // need to keep >= 0...only a concern if if offscreenColumns > 0
                        horizontalScrollPosition = Math.max(0, horizontalScrollPosition - (fillerCols + extraEmptyColumns));
    
                        // back up enough to make the appropriate number of offscreen columns
                        index = scrollPositionToIndex(Math.max(0, horizontalScrollPosition - offscreenExtraColumnsLeft), 
                            verticalScrollPosition);
                        seekPositionSafely(index);
                        updateList();
                    }
                    return;
                }
            }
            if (!iteratorValid)
                rowCount = Math.floor(listContent.heightExcludingOffsets / rowHeight);
            numRows = maxRows > 0 ? maxRows : rowCount;
            // we take out partialColumn if there's no collection because it'll get factored back in
            // and we want the math to workout that there's no scrollbars
            numCols = collection ? Math.ceil(collection.length / numRows) : colCount;
        }
        else 
        {
            // handle extra blank items at end of list.  This is the equivalent
            // of adjustVerticalScrollPositionDownward in List.as
            if (iteratorValid && verticalScrollPosition > 0)
            {
                var fillerRows:int = 0;
                while ((rowCount > 0) && ((listItems[rowCount + offscreenExtraRowsTop - 1] == null)  
                                        || (listItems[rowCount + offscreenExtraRowsTop - 1][0] == null)))
                {
                    rowCount--;
                    fillerRows++;
                }
                if (fillerRows)
                {
                    // literally prune out the extra rows
                    // not currently doing this for columns, but it's necessary here.
                    while (listItems.length > rowCount + offscreenExtraRowsTop)
                    {
                        listItems.pop();
                        rowInfo.pop();
                    }
            
                    if (!runningDataEffect)
                    {   
                        verticalScrollPosition = Math.max(0, verticalScrollPosition - fillerRows);

                        index = scrollPositionToIndex(horizontalScrollPosition, Math.max(0,verticalScrollPosition-offscreenExtraRowsTop));
                        seekPositionSafely(index);
                        updateList();
                    }
                    return;
                }
            }
            if (!iteratorValid)
                colCount = Math.floor(listContent.widthExcludingOffsets / columnWidth);

            numCols = maxColumns > 0 ? maxColumns : colCount;
            // we take out partialRow if there's no collection because it'll get factored back in
            // and we want the math to workout that there's no scrollbars
            numRows = collection ? Math.ceil(collection.length / numCols) : rowCount;
        }
        
        // Depending on the direction of the Tile control the colCount and
        // rowCount can be greater than numRows and numCols, resulting in 
        // negative values for H and V scroll positions. 
        // We ignore them when they are negative.
        maxHorizontalScrollPosition = Math.max(0, numCols - colCount);
        maxVerticalScrollPosition = Math.max(0, numRows - rowCount);

        // offscreenColumns/Rows shouldn't change within this function (if index is changed,
        // this code isn't reached)
        // trace("setScrollBarProperties " + numRows + " " + rowCount);     
        setScrollBarProperties(numCols, colCount, numRows, rowCount);
    }

    /**
     *  @private
     *  Move any rows that don't need rerendering
     *  Move and rerender any rows left over.
     */
    override protected function scrollVertically(pos:int, deltaPos:int,
                                              scrollUp:Boolean):void
    {
        var numRows:int;
        var numCols:int;
        var curY:Number;
        var uid:String;
        var index:int;

        // remove the clip mask that was applied to items in the last row of the list
        removeClipMask();

        var moveBlockDistance:Number;
        var oldOffscreenExtraRowsBottom:int = offscreenExtraRowsBottom;
        var oldOffscreenExtraRowsTop:int = offscreenExtraRowsTop;
        var desiredOffscreenExtraRowsTop:int = offscreenExtraRows/2;
        var desiredOffscreenExtraRowsBottom:int = offscreenExtraRows/2;


        // by default, we are going to delete as many rows as we're scrolling
        // but if we're not currently buffering as many offscreen rows as we
        // want to, we might reduce that number
        var modDeltaPos:int;
        // rows whose renderers we have to discard, i.e. which are scrolling
        // offscreen and not into the offscreen buffer
        var rowsToClear:int; 
        var rowDelta:int; // rows that have to be spliced out of the arrays, or added 
        if (scrollUp)
        {
            offscreenExtraRowsTop = Math.min(desiredOffscreenExtraRowsTop, offscreenExtraRowsTop + deltaPos);
            modDeltaPos = deltaPos - (offscreenExtraRowsTop - oldOffscreenExtraRowsTop);
            rowsToClear = modDeltaPos;
        }
        else 
        {
            // partialRowOffset accounts for an edge case where we are scrolled to the bottom
            // but the last row will still be on screen (a partial row) if we scroll.
            var partialRowOffset:int = ((offscreenExtraRowsBottom == 0) && listItems.length && listItems[listItems.length-1][0] &&
                (listItems[listItems.length-1][0].y + rowHeight < listContent.heightExcludingOffsets - listContent.topOffset)) ? 1 : 0;
            offscreenExtraRowsTop = Math.min(desiredOffscreenExtraRowsTop,pos);
            offscreenExtraRowsBottom = Math.min(offscreenExtraRowsBottom + deltaPos - partialRowOffset,desiredOffscreenExtraRowsBottom);

            modDeltaPos = deltaPos - (oldOffscreenExtraRowsTop - offscreenExtraRowsTop);
            rowDelta = (offscreenExtraRowsTop - oldOffscreenExtraRowsTop) + partialRowOffset +
                        (offscreenExtraRowsBottom - oldOffscreenExtraRowsBottom);
            rowsToClear = deltaPos - (offscreenExtraRowsBottom - oldOffscreenExtraRowsBottom) - partialRowOffset;
        }

        var rowCount:int = listItems.length;
        
        // clear out the old rows that are being scrolled offscreen and not preserved
        // in the offscreen buffer dictated by offscreenExtraRows

        for (var i:int = 0; i < rowsToClear; i++)
        {
            numCols = scrollUp ? listItems[i].length : listItems[rowCount - i - 1].length;
            for (var j:int = 0; j < columnCount && j < numCols; j++)
            {
                var r:IListItemRenderer = scrollUp ? listItems[i][j] : listItems[rowCount - i - 1][j];
                if (r)
                {
                    delete visibleData[rowMap[r.name].uid];
                    removeIndicators(rowMap[r.name].uid);
                    addToFreeItemRenderers(r);
                    delete rowMap[r.name];
                    if (scrollUp)
                        listItems[i][j] = null;
                    else
                        listItems[rowCount - i - 1][j] = null;
                }
            }
        }

        var actualRowCount:int = listItems.length;

        if (scrollUp)
        {
            // move the rows that don't change
            // note that we start from zero because we've taken some rows
            // off and put them on the free list already
            moveBlockDistance =  modDeltaPos * rowHeight;
            curY = 0;
            for (i = modDeltaPos; i < actualRowCount; i++)
            {
                numCols = listItems[i].length;
                for (j = 0; j < columnCount && j < numCols; j++)
                {
                    r = listItems[i][j];
                    listItems[i - modDeltaPos][j] = r;
                    if (r)
                    {
                        r.y -= moveBlockDistance;
                        rowMap[r.name].rowIndex -= modDeltaPos;
                        moveIndicatorsVertically(rowMap[r.name].uid, -moveBlockDistance);
                    }
                }
                
                // when the row has less number of columns
                // we need to clean up the row.
                if (numCols < columnCount)
                    for (j = numCols; j < columnCount; ++j)
                        listItems[i-modDeltaPos][j] = null;

                rowInfo[i - modDeltaPos] = rowInfo[i];
                rowInfo[i - modDeltaPos].y -= moveBlockDistance;
                curY = rowInfo[i - modDeltaPos].y + rowHeight;
            }
            listItems.splice(actualRowCount - modDeltaPos -1,modDeltaPos);
            rowInfo.splice(actualRowCount - modDeltaPos -1,modDeltaPos);
            // perhaps call setRowCount here()? or better to wait until done

            index = indicesToIndex(verticalScrollPosition - offscreenExtraRowsTop + actualRowCount - modDeltaPos, horizontalScrollPosition);
            seekPositionSafely(index);
            // note that we may be making extra rows at the bottom here, if we are adding to the number of rows
            // offscreen above.
            var rowsNeeded:int = deltaPos + (desiredOffscreenExtraRowsBottom - oldOffscreenExtraRowsBottom);
            var actual:Point = 
                makeRowsAndColumns(0, curY, listContent.width, curY+deltaPos*rowHeight, 0, actualRowCount - modDeltaPos, true, rowsNeeded);
            var extraEmptyRows:int = rowsNeeded-actual.y;
            while (extraEmptyRows--)
            {
                listItems.pop();
                rowInfo.pop();
            }

            index = indicesToIndex(verticalScrollPosition - offscreenExtraRowsTop, horizontalScrollPosition);
            seekPositionSafely(index);
            // if we didn't make as many rows as we wanted to, we must not have entirely 
            // filled the buffer below the visible area
            offscreenExtraRowsBottom = Math.max(0,desiredOffscreenExtraRowsBottom - (actual.y < deltaPos ? rowsNeeded - actual.y : 0));
            
        }
        else
        {
            if (rowDelta < 0)
            {
                listItems.splice(listItems.length + rowDelta, - rowDelta);
                rowInfo.splice(rowInfo.length + rowDelta, -rowDelta);
            }
            else if (rowDelta > 0)
            {
                // we are going to increase listItems and rowInfo
                for (i = 0; i < rowDelta; i++)
                    listItems[actualRowCount + i] = [];
            }
                

            moveBlockDistance =  modDeltaPos * rowHeight;

            curY = rowInfo[modDeltaPos].y;
            
            for (i = listItems.length - 1 - modDeltaPos; i >= 0; i--)
            {
                numCols = listItems[i].length;
                for (j = 0; j < columnCount && j < numCols; j++)
                {
                    r = listItems[i][j];
                    if (r)
                    {
                        r.y += moveBlockDistance;
                        // using modDeltaPos twice here, but it's not necessarily the same value!!??
                        // or is it? (not if we're splicing...
                        rowMap[r.name].rowIndex += modDeltaPos;
                        uid = rowMap[r.name].uid;
                        listItems[i + modDeltaPos][j] = r;
                        moveIndicatorsVertically(uid, moveBlockDistance);
                    }
                    else
                        listItems[i + modDeltaPos][j] = null;
                }
                rowInfo[i + modDeltaPos] = rowInfo[i];
                rowInfo[i + modDeltaPos].y += moveBlockDistance;
            }

            for (i = 0; i < modDeltaPos; i++)
                for (j = 0; j < columnCount; j++)
                    listItems[i][j] = null;

            index = indicesToIndex(verticalScrollPosition - offscreenExtraRowsTop, horizontalScrollPosition);
            seekPositionSafely(index);
            // can't allow makeRowsAndColumns to steal renderers during incremental scrolling down
            // because any empty slots in listItems won't be refilled.
            allowRendererStealingDuringLayout = false;
            actual = makeRowsAndColumns(0, 0, listContent.width, curY, 0, 0, true, modDeltaPos);
            allowRendererStealingDuringLayout = true;
            seekPositionSafely(index);
        }

        var oldListContentHeight:Number = listContent.heightExcludingOffsets;
        listContent.topOffset = - rowHeight * offscreenExtraRowsTop;
        listContent.bottomOffset = offscreenExtraRowsBottom ?
             rowInfo[rowInfo.length-1].y + rowHeight + listContent.topOffset - oldListContentHeight
            : 0;
        adjustListContent();

        // if needed, add a clip mask to the items in the last row of the list
        addClipMask(false);
    }

    /**
     *  @inheritDoc
     */
    override protected function scrollHorizontally(pos:int, deltaPos:int,
                                                scrollUp:Boolean):void
    {
        if (deltaPos == 0)
            return;
    
        var numRows:int;
        var numCols:int;
        var curX:Number;
        var uid:String;
        var index:int;

        // remove the clip mask that was applied to items in the last row of the list
        removeClipMask();

        var moveBlockDistance:Number;

        var oldOffscreenExtraColumnsRight:int = offscreenExtraColumnsRight;
        var oldOffscreenExtraColumnsLeft:int = offscreenExtraColumnsLeft;
        var desiredOffscreenExtraColumnsLeft:int = offscreenExtraColumns/2;
        var desiredOffscreenExtraColumnsRight:int = offscreenExtraColumns/2;

        // by default, we are going to delete as many rows as we're scrolling
        // but if we're not currently buffering as many offscreen rows as we
        // want to, we might reduce that number
        var modDeltaPos:int;
        // rows whose renderers we have to discard, i.e. which are scrolling
        // offscreen and not into the offscreen buffer
        var columnsToClear:int; 
        var columnDelta:int; // rows that have to be spliced out of the arrays, or added 
        if (scrollUp)
        {
            offscreenExtraColumnsLeft = Math.min(desiredOffscreenExtraColumnsLeft, offscreenExtraColumnsLeft + deltaPos);
            modDeltaPos = deltaPos - (offscreenExtraColumnsLeft - oldOffscreenExtraColumnsLeft);
            columnsToClear = modDeltaPos;
        }
        else 
        {
            var partialColumnOffset:int = ((offscreenExtraColumnsRight == 0) && listItems[0] && (listItems[0].length > 0) 
                && listItems[0][listItems[0].length - 1] 
                && (listItems[0][listItems[0].length - 1].x + columnWidth < listContent.widthExcludingOffsets - listContent.leftOffset))? 1 : 0;
            offscreenExtraColumnsLeft = Math.min(desiredOffscreenExtraColumnsLeft,pos);
            offscreenExtraColumnsRight = Math.min(offscreenExtraColumnsRight + deltaPos - partialColumnOffset,desiredOffscreenExtraColumnsRight);
            modDeltaPos = deltaPos - (oldOffscreenExtraColumnsLeft - offscreenExtraColumnsLeft);
            columnDelta = (offscreenExtraColumnsLeft - oldOffscreenExtraColumnsLeft) + 
                        partialColumnOffset + 
                        (offscreenExtraColumnsRight - oldOffscreenExtraColumnsRight);
            columnsToClear = deltaPos - (offscreenExtraColumnsRight - oldOffscreenExtraColumnsRight) - partialColumnOffset;
        }

        // a temporary measure, because columnCount isn't always being maintained accurately :-(
        var columnCount:int = listItems[0].length;
        
        // toss the old rows
        for (var i:int = 0; i < columnsToClear; i++)
        {
            for (var j:int = 0; j < rowCount; j++)
            {
                var r:IListItemRenderer = scrollUp ? listItems[j][i] : listItems[j][columnCount - i - 1];
                if (r)
                {
                    delete visibleData[rowMap[r.name].uid];
                    removeIndicators(rowMap[r.name].uid);
                    addToFreeItemRenderers(r);
                    delete rowMap[r.name];
                    if (scrollUp)
                        listItems[j][i] = null
                    else
                        listItems[j][columnCount - i - 1] = null;
                }
            }
        }

        if (scrollUp)
        {
            moveBlockDistance = modDeltaPos * columnWidth;
            curX = 0;

            for (i = modDeltaPos; i < columnCount; i++)
            {
                for (j = 0; j < rowCount; j++)
                {
                    var temp:IListItemRenderer = listItems[j][i];
                    if (temp)
                    {
                        r = temp;
                        r.x -= moveBlockDistance;
                        uid = rowMap[r.name].uid;
                        listItems[j][i - modDeltaPos] = r;
                        rowMap[r.name].columnIndex -= modDeltaPos;
                        moveIndicatorsHorizontally(uid, -moveBlockDistance);
                    }
                    else
                        listItems[j][i - modDeltaPos] = null;
                }
                curX += columnWidth;
            }
            for (i = 0; i < modDeltaPos; i++)
                for (j = 0; j < rowCount; j++)
                    listItems[j][columnCount - i - 1] = null;

            index = indicesToIndex(verticalScrollPosition, horizontalScrollPosition + columnCount - offscreenExtraColumnsLeft - modDeltaPos);
            seekPositionSafely(index);

            var columnsNeeded:int = deltaPos + (desiredOffscreenExtraColumnsRight - oldOffscreenExtraColumnsRight);
            var currentColumns:int = listItems.length ? listItems[0].length - modDeltaPos: 0;
            allowRendererStealingDuringLayout = false;
            var actual:Point = 
                makeRowsAndColumns(curX, 0, listContent.width, listContent.height, columnCount - modDeltaPos, 0, true,columnsNeeded);
            allowRendererStealingDuringLayout = true;
            
            var extraEmptyColumns:int = listItems[0].length - (currentColumns + actual.x);
            if (extraEmptyColumns)
            {
                for (i = 0; i < listItems.length; i++)
                    for (j = 0; j < extraEmptyColumns; j++)
                        listItems[i].pop();
            }
                
            index = indicesToIndex(verticalScrollPosition, horizontalScrollPosition - offscreenExtraColumnsLeft);
            seekPositionSafely(index);
            // if we didn't make as many columns as we wanted to, we must not have entirely 
            // filled the buffer to the right of the visible area
            offscreenExtraColumnsRight = Math.max(0,desiredOffscreenExtraColumnsRight - (actual.x < deltaPos ? columnsNeeded - actual.x : 0));
        }
        else
        {
            if (columnDelta < 0)
            {
                // we are reducing the left offscreen columns and not shifting the array as
                // much, we have to delete the columns we were planning on shifting into
                var adjustedColumnCount:int = listItems[0].length + columnDelta;
                for (j = 0; j < rowCount; j++)
                    while(listItems[j].length > adjustedColumnCount)
                        listItems[j].pop();
            }

            moveBlockDistance = modDeltaPos * columnWidth;
            if (modDeltaPos)
                curX = moveBlockDistance;
            var newColumnCount:int = columnCount + columnDelta;
            for (i = newColumnCount - modDeltaPos - 1; i >= 0; i--)
            {
                for (j = 0; j < rowCount; j++)
                {
                    r = listItems[j][i];
                    if (r)
                    {
                        r.x += moveBlockDistance;
                        uid = rowMap[r.name].uid;
                        listItems[j][i + modDeltaPos] = r;
                        rowMap[r.name].columnIndex += modDeltaPos;
                        moveIndicatorsHorizontally(uid,moveBlockDistance);
                    }
                    else
                        listItems[j][i + modDeltaPos] = null;
                }
            }
            for (i = 0; i < modDeltaPos; i++)
                for (j = 0; j < rowCount; j++)
                    listItems[j][i] = null;

            index = indicesToIndex(verticalScrollPosition, horizontalScrollPosition - offscreenExtraColumnsLeft);
            seekPositionSafely(index);
            allowRendererStealingDuringLayout = false;
            makeRowsAndColumns(0, 0, curX, listContent.height, 0, 0, true, modDeltaPos);
            allowRendererStealingDuringLayout = true;
            seekPositionSafely(index);
        }

        var oldListContentWidth:Number = listContent.widthExcludingOffsets;
        listContent.leftOffset = - columnWidth * offscreenExtraColumnsLeft;
        listContent.rightOffset = offscreenExtraColumnsRight ?
            (listItems[0][listItems[0].length-1].x + listItems[0][listItems[0].length-1].width 
                + listContent.leftOffset - oldListContentWidth)
            : 0;

        adjustListContent();

        // if needed, add a clip mask to the items in the last row of the list
        addClipMask(false);
    }

    
    /**
     *  @private
     */
    override protected function moveSelectionVertically(
                                    code:uint, shiftKey:Boolean, 
                                    ctrlKey:Boolean):void
    {
        var newVerticalScrollPosition:Number;
        var newHorizontalScrollPosition:Number;
        var listItem:IListItemRenderer;
        var uid:String;
        var len:int;
        var selected:Boolean;
        var bSelChanged:Boolean = false;
        var rowIndex:int;
        var colIndex:int;
        var rowCount:int = listItems.length - offscreenExtraRowsTop - offscreenExtraRowsBottom;
        var numRows:int = ((maxRows > 0) && (direction != TileBaseDirection.HORIZONTAL)) ? maxRows : rowCount;
        var partialRow:int = displayingPartialRow() ? 1 : 0;
        var partialColumn:int = displayingPartialColumn() ? 1 : 0;
        
        if (!collection)
            return;

        showCaret = true;

        switch (code)
        {
            case Keyboard.UP:
            {
                if (caretIndex > 0)
                {
                    if (direction == TileBaseDirection.VERTICAL)
                        --caretIndex;
                    else
                    {
                        rowIndex = indexToRow(caretIndex);
                        colIndex = indexToColumn(caretIndex);
                        if (rowIndex == 0)
                        {
                            colIndex--;
                            rowIndex = lastRowInColumn(colIndex);
                        }
                        else
                            rowIndex--;
                        caretIndex = Math.min(indicesToIndex(rowIndex, colIndex), collection.length - 1);

                    }
                    rowIndex = indexToRow(caretIndex);
                    colIndex = indexToColumn(caretIndex);

                    // scroll up if we need to
                    if (rowIndex < verticalScrollPosition)
                        newVerticalScrollPosition = verticalScrollPosition - 1;

                    // wrap down if we need to
                    if (rowIndex > verticalScrollPosition + rowCount - partialRow)
                        newVerticalScrollPosition = maxVerticalScrollPosition;

                    if (colIndex < horizontalScrollPosition)
                        newHorizontalScrollPosition = horizontalScrollPosition - 1;
                }
                break;
            }

            case Keyboard.DOWN:
            {
                if (caretIndex < collection.length - 1)
                {
                    if (direction == TileBaseDirection.VERTICAL
                            || caretIndex == -1)
                    {
                        ++caretIndex;
                    }
                    else
                    {
                        rowIndex = indexToRow(caretIndex);
                        colIndex = indexToColumn(caretIndex);
                        if (rowIndex == lastRowInColumn(colIndex))
                        {
                            rowIndex = 0;
                            colIndex++;
                        }
                        else
                        {
                            rowIndex++;
                        }
                        caretIndex = Math.min(indicesToIndex(rowIndex, colIndex), collection.length - 1);
                    }

                    rowIndex = indexToRow(caretIndex);
                    colIndex = indexToColumn(caretIndex);

                    if (rowIndex >= verticalScrollPosition + rowCount - partialRow &&
                        verticalScrollPosition < maxVerticalScrollPosition)
                    {
                        newVerticalScrollPosition = verticalScrollPosition + 1;
                    }

                    if (rowIndex < verticalScrollPosition)
                        newVerticalScrollPosition = rowIndex;

                    if (colIndex > horizontalScrollPosition + columnCount - 1)
                        newHorizontalScrollPosition = horizontalScrollPosition + 1;
                }
                break;
            }

            case Keyboard.PAGE_UP:
            {
                if (caretIndex < 0)
                    caretIndex = scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition);
                    
                rowIndex = indexToRow(caretIndex);
                colIndex = indexToColumn(caretIndex);
                if (verticalScrollPosition > 0)
                {
                    if (rowIndex == verticalScrollPosition)
                        newVerticalScrollPosition = rowIndex = Math.max(verticalScrollPosition - (rowCount - partialRow), 0);
                    else
                        rowIndex = verticalScrollPosition;

                    caretIndex = indicesToIndex(rowIndex, colIndex);
                    // this break is here so we fall throught to .HOME otherwise
                    break;
                }
            }

            case Keyboard.HOME:
            {
                if (collection.length)
                {
                    caretIndex = 0;
                    newVerticalScrollPosition = 0;
                    newHorizontalScrollPosition = 0;
                }
                break;
            }

            case Keyboard.PAGE_DOWN:
            {
                if (caretIndex < 0)
                    caretIndex = scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition);

                rowIndex = indexToRow(caretIndex);
                colIndex = indexToColumn(caretIndex);
                
                if (rowIndex < maxVerticalScrollPosition)
                {
                    if (rowIndex == verticalScrollPosition + (rowCount - partialRow))
                    {
                        newVerticalScrollPosition = Math.min(verticalScrollPosition + rowCount - partialRow, maxVerticalScrollPosition);
                        rowIndex = verticalScrollPosition + rowCount;
                    }
                    else
                    {
                        rowIndex = Math.min(verticalScrollPosition + rowCount - partialRow, indexToRow(collection.length - 1));
                        if (rowIndex == verticalScrollPosition + rowCount - partialRow)
                            newVerticalScrollPosition = Math.min(verticalScrollPosition + rowCount - partialRow, maxVerticalScrollPosition);
                    }

                    caretIndex = Math.min(indicesToIndex(rowIndex, colIndex), collection.length - 1);
                    // this break is here so we fall through to .END otherwise
                    break;
                }
            }

            case Keyboard.END:
            {
                if (caretIndex < collection.length)
                {
                    caretIndex = collection.length - 1;
                    newVerticalScrollPosition = maxVerticalScrollPosition;
                    newHorizontalScrollPosition = maxHorizontalScrollPosition;
                }
                break;
            }
        }

        var scrollEvent:ScrollEvent;

        if (!isNaN(newVerticalScrollPosition))
        {
            if (newVerticalScrollPosition != verticalScrollPosition)
            {
                scrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
                scrollEvent.detail = ScrollEventDetail.THUMB_POSITION;
                scrollEvent.direction = ScrollEventDirection.VERTICAL;
                scrollEvent.delta = newVerticalScrollPosition - verticalScrollPosition;
                scrollEvent.position = newVerticalScrollPosition;
                verticalScrollPosition = newVerticalScrollPosition;
                dispatchEvent(scrollEvent);
            }
        }

        if (iteratorValid)
        {
            if (!isNaN(newHorizontalScrollPosition))
            {
                if (newHorizontalScrollPosition != horizontalScrollPosition)
                {
                    scrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
                    scrollEvent.detail = ScrollEventDetail.THUMB_POSITION;
                    scrollEvent.direction = ScrollEventDirection.HORIZONTAL;
                    scrollEvent.delta = newHorizontalScrollPosition - horizontalScrollPosition;
                    scrollEvent.position = newHorizontalScrollPosition;
                    horizontalScrollPosition = newHorizontalScrollPosition;
                    dispatchEvent(scrollEvent);
                }
            }
        }

        if (!iteratorValid)
        {
            keySelectionPending = true;
            return;
        }

        bShiftKey = shiftKey;
        bCtrlKey = ctrlKey;
        lastKey = code;

        finishKeySelection();
    }

    /**
     *  @private
     */
    override protected function moveSelectionHorizontally(
                                code:uint, shiftKey:Boolean,
                                ctrlKey:Boolean):void
    {
        var newVerticalScrollPosition:Number;
        var newHorizontalScrollPosition:Number;
        var listItem:IListItemRenderer;
        var uid:String;
        var len:int;
        var selected:Boolean;
        var rowIndex:int;
        var colIndex:int;
        var columnCount:int = listItems[0].length - offscreenExtraColumnsLeft - offscreenExtraColumnsRight;
        var numCols:int = ((maxColumns > 0) && (direction != TileBaseDirection.VERTICAL)) ? maxColumns : columnCount;
        var partialRow:int = displayingPartialRow() ? 1 : 0;
        var partialColumn:int = displayingPartialColumn() ? 1 : 0;

        if (!collection)
            return;

        showCaret = true;

        switch (code)
        {
            case Keyboard.LEFT:
            {
                if (caretIndex > 0)
                {
                    if (direction == TileBaseDirection.HORIZONTAL)
                    {
                        --caretIndex;
                    }
                    else
                    {
                        rowIndex = indexToRow(caretIndex);
                        colIndex = indexToColumn(caretIndex);
                        if (colIndex == 0)
                        {
                            rowIndex--;
                            colIndex = lastColumnInRow(rowIndex);
                        }
                        else
                        {
                            colIndex--;
                        }
                        caretIndex = Math.min(indicesToIndex(rowIndex, colIndex), collection.length - 1);
                    }

                    rowIndex = indexToRow(caretIndex);
                    colIndex = indexToColumn(caretIndex);
                    if (direction == TileBaseDirection.HORIZONTAL)
                    {
                        // scroll up if we need to
                        if (rowIndex < verticalScrollPosition)
                        {
                            newVerticalScrollPosition = verticalScrollPosition - 1;
                        }
                        // wrap down if we need to
                        else if (rowIndex > verticalScrollPosition + rowCount - partialRow)
                        {
                            newVerticalScrollPosition = maxVerticalScrollPosition;
                        }
                    }
                    else
                    {
                        // scroll left if we need to
                        if (colIndex < horizontalScrollPosition)
                        {
                            newHorizontalScrollPosition = horizontalScrollPosition - 1;
                        }
                        // wrap right if we need to
                        else if (colIndex > horizontalScrollPosition + columnCount - 1 - partialColumn)
                        {
                            newHorizontalScrollPosition = maxHorizontalScrollPosition;
                        }
                    }
                }
                break;
            }

            case Keyboard.RIGHT:
            {
                if (caretIndex < collection.length - 1)
                {
                    if (direction == TileBaseDirection.HORIZONTAL
                            || caretIndex == -1)
                    {
                        ++caretIndex;
                    }
                    else
                    {
                        rowIndex = indexToRow(caretIndex);
                        colIndex = indexToColumn(caretIndex);
                        if (colIndex == lastColumnInRow(rowIndex))
                        {
                            colIndex = 0;
                            rowIndex++;
                        }
                        else
                        {
                            colIndex++;
                        }
                        caretIndex = Math.min(indicesToIndex(rowIndex, colIndex), collection.length - 1);
                    }

                    rowIndex = indexToRow(caretIndex);
                    colIndex = indexToColumn(caretIndex);
                    if (direction == TileBaseDirection.HORIZONTAL)
                    {
                        if (rowIndex >= verticalScrollPosition + rowCount - partialRow &&
                            verticalScrollPosition < maxVerticalScrollPosition)
                        {
                            newVerticalScrollPosition = verticalScrollPosition + 1;
                        }
                        if (rowIndex < verticalScrollPosition)
                        {
                            newVerticalScrollPosition = rowIndex;
                        }
                    }
                    else
                    {
                        if (colIndex >= horizontalScrollPosition + columnCount - partialColumn &&
                            horizontalScrollPosition < maxHorizontalScrollPosition)
                        {
                            newHorizontalScrollPosition = horizontalScrollPosition + 1;
                        }
                        if (colIndex < horizontalScrollPosition)
                        {
                            newHorizontalScrollPosition = colIndex;
                        }
                    }
                }
                break;
            }

            case Keyboard.PAGE_UP:
            {
                if (caretIndex < 0)
                    caretIndex = scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition);
                rowIndex = indexToRow(caretIndex);
                colIndex = indexToColumn(caretIndex);
                if (colIndex > 0)
                {
                    newHorizontalScrollPosition = colIndex = Math.max(horizontalScrollPosition - (columnCount - partialColumn), 0);

                    caretIndex = indicesToIndex(rowIndex, colIndex);
                }
                break;
            }
        
            case Keyboard.PAGE_DOWN:
            {
                if (caretIndex < 0)
                    caretIndex = scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition);
                rowIndex = indexToRow(caretIndex);
                colIndex = indexToColumn(caretIndex);
                if (colIndex < maxHorizontalScrollPosition)
                {
                    colIndex = Math.min(horizontalScrollPosition + columnCount - partialColumn, indexToColumn(collection.length - 1));

                    if (colIndex > horizontalScrollPosition)
                        newHorizontalScrollPosition = Math.min(colIndex,maxHorizontalScrollPosition);

                    caretIndex = indicesToIndex(rowIndex, colIndex);
                }
                break;
            }

            case Keyboard.HOME:
            {
                if (collection.length)
                {
                    caretIndex = 0;
                    newHorizontalScrollPosition = 0;
                    newVerticalScrollPosition = 0;
                }
                break;
            }

            case Keyboard.END:
            {
                if (caretIndex < collection.length)
                {
                    caretIndex = collection.length - 1;
                    newHorizontalScrollPosition = maxHorizontalScrollPosition;
                    newVerticalScrollPosition = maxVerticalScrollPosition;
                }
                break;
            }
        }

        var scrollEvent:ScrollEvent;

        if (!isNaN(newVerticalScrollPosition))
        {
            if (newVerticalScrollPosition != verticalScrollPosition)
            {
                scrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
                scrollEvent.detail = ScrollEventDetail.THUMB_POSITION;
                scrollEvent.direction = ScrollEventDirection.VERTICAL;
                scrollEvent.delta = newVerticalScrollPosition - verticalScrollPosition;
                scrollEvent.position = newVerticalScrollPosition;
                verticalScrollPosition = newVerticalScrollPosition;
                dispatchEvent(scrollEvent);
            }
        }

        if (iteratorValid)
        {
            if (!isNaN(newHorizontalScrollPosition))
            {
                if (newHorizontalScrollPosition != horizontalScrollPosition)
                {
                    scrollEvent = new ScrollEvent(ScrollEvent.SCROLL);
                    scrollEvent.detail = ScrollEventDetail.THUMB_POSITION;
                    scrollEvent.direction = ScrollEventDirection.HORIZONTAL;
                    scrollEvent.delta = newHorizontalScrollPosition - horizontalScrollPosition;
                    scrollEvent.position = newHorizontalScrollPosition;
                    horizontalScrollPosition = newHorizontalScrollPosition;
                    dispatchEvent(scrollEvent);
                }
            }
        }

        if (!iteratorValid)
        {
            keySelectionPending = true;
            return;
        }

        bShiftKey = shiftKey;
        bCtrlKey = ctrlKey;
        lastKey = code;

        finishKeySelection();
    }
    
    private function displayingPartialRow():Boolean
    {
        var row:Array = listItems[listItems.length - 1 - offscreenExtraRowsBottom];
        if (row && row.length > 0)
        {
            var r:IListItemRenderer = row[0];
            // ignore partial row
            if (!r || (r.y + r.height > (listContent.heightExcludingOffsets - listContent.topOffset)))
                return true;
        }
        return false;
    }
    
    private function displayingPartialColumn():Boolean
    {
        if (listItems[0] && (listItems[0].length > 0))
        {
            var r:IListItemRenderer = listItems[0][listItems[0].length - 1 - offscreenExtraColumnsRight];
            if (r && r.x + r.width > listContent.widthExcludingOffsets - listContent.leftOffset)
                return true;
        }
        return false;
    }
    
    /**
     *  @private
     */
    override protected function finishKeySelection():void
    {
        var uid:String;
        var bSelChanged:Boolean = false;
        var rowIndex:int;
        var colIndex:int;
        var listItem:IListItemRenderer;

        if (caretIndex < 0)
            return;


        rowIndex = indexToRow(caretIndex);
        colIndex = indexToColumn(caretIndex);

        listItem = listItems[rowIndex - verticalScrollPosition + offscreenExtraRowsTop][colIndex - horizontalScrollPosition + offscreenExtraColumnsLeft];
        if (!bCtrlKey)
        {
            selectItem(listItem, bShiftKey, bCtrlKey);
            bSelChanged = true;
        }
        if (bCtrlKey)
        {
            //tkr - is this correct to assign this to our UID?
            uid = itemToUID(listItem.data);
            drawItem(visibleData[uid], selectedData[uid] != null, false, true);
        }

        if (bSelChanged)
        {
            var evt:ListEvent = new ListEvent(ListEvent.CHANGE);
            evt.itemRenderer = listItem;
            evt.rowIndex = rowIndex;
            evt.columnIndex = colIndex;
            dispatchEvent(evt);
        }
    }

    /**
     *  @private
     */
    override public function itemRendererToIndex(item:IListItemRenderer):int
    {
        var uid:String;
        
        // perhaps wrap this in function in ListBase
        if (runningDataEffect)
            uid = itemToUID(dataItemWrappersByRenderer[item]);
        else
            uid = itemToUID(item.data);
        
        var n:int = listItems.length;
        for (var i:int = 0; i < listItems.length; i++)
        {
            var m:int = listItems[i].length;
            for (var j:int = 0; j < m; j++)
            {
                if (listItems[i][j] && rowMap[listItems[i][j].name].uid == uid)
                {
                    if (direction == TileBaseDirection.VERTICAL)
                        return (j + horizontalScrollPosition - offscreenExtraColumnsLeft) * Math.max(maxRows, rowCount) + i;
                    else
                        return (i + verticalScrollPosition - offscreenExtraRowsTop) * Math.max(maxColumns, columnCount) + j;
                }
            }
        }

        return -1;
    }

    /**
     *  @private
     */
    override public function indexToItemRenderer(index:int):IListItemRenderer
    {
        // XXarielb: theoretically these lock variables mean something
        // right now they're always 0, so we're not quite sure what to
        // do with them.  come back an revisit
        var rowIndex:int = indexToRow(index);
        
        if (rowIndex < verticalScrollPosition ||
            rowIndex >= verticalScrollPosition + rowCount)
        {
            return null;
        }

        var colIndex:int = indexToColumn(index);
        
        if (colIndex < horizontalScrollPosition ||
            colIndex >= horizontalScrollPosition + columnCount)
        {
            return null;
        }
        
        return listItems[rowIndex - verticalScrollPosition]
                        [colIndex - horizontalScrollPosition];
    }

    /**
     *  @private
     */
    override public function calculateDropIndex(event:DragEvent = null):int
    {
        if (event)
        {
            var item:IListItemRenderer;
            var pt:Point = new Point(event.localX, event.localY);
            pt = DisplayObject(event.target).localToGlobal(pt);
            pt = listContent.globalToLocal(pt);

            var rc:int = listItems.length;
            for (var i:int = 0; i < rc; i++)
            {
                if (rowInfo[i].y <= pt.y && pt.y < rowInfo[i].y + rowInfo[i].height)
                {
                    var cc:int = listItems[i].length;
                    for (var j:int = 0; j < cc; j++)
                    {
                        if (listItems[i][j] && listItems[i][j].x <= pt.x
                            && pt.x < listItems[i][j].x + listItems[i][j].width)
                        {
                            item = listItems[i][j];
                            if (!DisplayObject(item).visible)
                                item = null;
                            break;
                        }
                    }
                    break;
                }
            }

            if (item)
                lastDropIndex = itemRendererToIndex(item);
            else
                lastDropIndex = collection ? collection.length : 0;
        }

        return lastDropIndex;
    }

    /**
     *  @private
     */
    override public function showDropFeedback(event:DragEvent):void
    {
        if (!dropIndicator)
        {
            var dropIndicatorClass:Class = getStyle("dropIndicatorSkin");
            if (!dropIndicatorClass)
                dropIndicatorClass = ListDropIndicator;
            dropIndicator = IFlexDisplayObject(new dropIndicatorClass());

            var vm:EdgeMetrics = viewMetrics;

            drawFocus(true);

            dropIndicator.x = 2;
            if (direction == TileBaseDirection.HORIZONTAL)
            {
                dropIndicator.setActualSize(rowHeight - 4, 4);
                DisplayObject(dropIndicator).rotation = 90;
            }
            else
                dropIndicator.setActualSize(columnWidth - 4, 4);
            dropIndicator.visible = true;
            listContent.addChild(DisplayObject(dropIndicator));

            if (collection)
                dragScrollingInterval = setInterval(dragScroll, 15);
        }

        var dropIndex:int = calculateDropIndex(event);
        var rowNum:int = indexToRow(dropIndex);
        var colNum:int = indexToColumn(dropIndex);

        rowNum -= verticalScrollPosition - offscreenExtraRowsTop;

        colNum -= horizontalScrollPosition - offscreenExtraColumnsLeft;

        var rc:Number = listItems.length;
        if (rowNum >= rc)
            rowNum = rc - 1;

        var cc:Number = rc ? listItems[0].length : 0;
        if (colNum > cc)
            colNum = cc;

        dropIndicator.x = cc && listItems[rowNum].length && listItems[rowNum][colNum] ? listItems[rowNum][colNum].x : colNum * columnWidth;
        dropIndicator.y = rc && listItems[rowNum].length && listItems[rowNum][0] ? listItems[rowNum][0].y : rowNum * rowHeight;
    }

    /**
     *  @private
     */
    override public function measureWidthOfItems(index:int = -1,
                                            count:int = 0):Number
    {
        var item:IListItemRenderer;
        var w:Number;
        var rowData:ListData;
        var needSize:Boolean = false;

        if (collection && collection.length)
        {
            var wrappedData:Object = iterator.current;
            var data:Object = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;

            if (!measuringObjects)
                measuringObjects = new Dictionary(true);

            var factory:IFactory = getItemRendererFactory(data);
            item = measuringObjects[factory];
            if (!item)
            {
                item = getMeasuringRenderer(data);
                needSize = true;
            }
            
            rowData = ListData(makeListData(data, uid, 0, 0));
            
            if (item is IDropInListItemRenderer)
                IDropInListItemRenderer(item).listData = data ? rowData : null;
            
            item.data = data;
            
            UIComponentGlobals.layoutManager.validateClient(item, true);
            
            w = item.getExplicitOrMeasuredWidth();
            if (needSize)
            {
                item.setActualSize(w, item.getExplicitOrMeasuredHeight());
                needSize = false;
            }
        }
        
        if (isNaN(w) || w == 0)
            w = 50;
        
        return w * count;
    }

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
     *  @param data The renderer's data structure.
     *  
     *  @return The item renderer.
     */
    override public function createItemRenderer(data:Object):IListItemRenderer
    {
        var factory:IFactory;

        // get the factory for the data
        factory = getItemRendererFactory(data);
        if (!factory)
        {
            if (!data)
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
     */
    mx_internal function setupRendererFromData(item:IListItemRenderer, data:Object):void
    {
        var rowData:ListData = ListData(makeListData(data, itemToUID(data), 0, 0));
        
        if (item is IDropInListItemRenderer)
            IDropInListItemRenderer(item).listData = data ? rowData : null;
        
        item.data = data;
        
        UIComponentGlobals.layoutManager.validateClient(item, true);
    }

    /**
     *  @private
     */
    override public function measureHeightOfItems(index:int = -1,
                                             count:int = 0):Number
    {
        var h:Number;
        var needSize:Boolean = false;

        if (collection && collection.length)
        {
            var wrappedData:Object = iterator.current;
            var data:Object = (wrappedData is ItemWrapper) ? wrappedData.data : wrappedData;
            
            var factory:IFactory = getItemRendererFactory(data);
            var item:IListItemRenderer = measuringObjects[factory];

            if (item == null)
            {
                item = getMeasuringRenderer(data);
                needSize = true;
            }
            
            setupRendererFromData(item, data);
            
            h = item.getExplicitOrMeasuredHeight();
            if (needSize)
            {
                item.setActualSize(item.getExplicitOrMeasuredWidth(), h);
                needSize = false;
            }
        }
        
        if (isNaN(h) || h == 0)
            h = 50;

        var paddingTop:Number = getStyle("paddingTop");
        var paddingBottom:Number = getStyle("paddingBottom");

        h += paddingTop + paddingBottom;
        
        return h * count;
    }

    /**
     *  @private
     */
    override protected function scrollPositionToIndex(horizontalScrollPosition:int, verticalScrollPosition:int):int
    {
        if (iterator)
        {
            var startIndex:int;

            if (direction == TileBaseDirection.HORIZONTAL)
                startIndex = verticalScrollPosition * columnCount + horizontalScrollPosition;
            else
                startIndex = horizontalScrollPosition * rowCount + verticalScrollPosition;

            return startIndex;
        }
        return -1;
    }

    /**
     *  @private
     */
    override protected function get dragImageOffsets():Point
    {
        var pt:Point = new Point(8192, 8192);
        var found:Boolean = false;
        var rC:int = listItems.length;
        
        for (var s:String in visibleData)
        {
            if (selectedData[s])
            {
                pt.x = Math.min(pt.x, visibleData[s].x);
                pt.y = Math.min(pt.y, visibleData[s].y);
                found = true;
            }
        }
        
        if (found)
            return pt;
        
        return new Point(0, 0);
    }

    /**
     *  @private
     *
     *  see ListBase.as
     */
    override mx_internal function addClipMask(layoutChanged:Boolean):void
    {
    }
    
    /**
     *  @private
     *
     *  Undo the effects of the addClipMask function (above)
     */
    override mx_internal function removeClipMask():void
    {
    }

    /**
     *  @private
     */
    override mx_internal function adjustOffscreenRowsAndColumns():void
    {
        if (direction == TileBaseDirection.VERTICAL)
        {
            offscreenExtraRows = 0;
            offscreenExtraColumns = offscreenExtraRowsOrColumns;
        }
        else
        {
            offscreenExtraColumns = 0;
            offscreenExtraRows = offscreenExtraRowsOrColumns;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Creates a new ListData instance and populates the fields based on
     *  the input data provider item. 
     *  
     *  @param data The data provider item used to populate the ListData.
     *  @param uid The UID for the item.
     *  @param rowNum The index of the item in the data provider.
     *  @param columnNum The columnIndex associated with this item. 
     *  
     *  @return A newly constructed ListData object.
     */
    protected function makeListData(data:Object, uid:String, 
        rowNum:int, columnNum:int):BaseListData
    {
        return new ListData(itemToLabel(data), itemToIcon(data), labelField, uid, 
            this, rowNum, columnNum);
    }

    /**
     *  @private
     *  Assumes horizontal.
     */
    private function lastRowInColumn(index:int):int
    {
        var numCols:int = maxColumns > 0 ? maxColumns : columnCount;
        var numRows:int = Math.floor((collection.length - 1) / numCols);
        if (index * numRows > collection.length)
            numRows--;
        return numRows;
    }

    /**
     *  @private
     *  Assumes vertical.
     */
    private function lastColumnInRow(index:int):int
    {
        var numRows:int = maxRows > 0 ? maxRows : rowCount;
        var numCols:int = Math.floor((collection.length - 1) / numRows);
        if (indicesToIndex(index, numCols) >= collection.length)
            numCols--;
        return numCols;
    }

    /**
     *  @private
     */
    override protected function indexToRow(index:int):int
    {
        if (direction == TileBaseDirection.VERTICAL)
        {
            var numRows:int = maxRows > 0 ? maxRows : rowCount;
            return index % numRows;
        }

        var numCols:int = maxColumns > 0 ? maxColumns : columnCount;
        return Math.floor(index / numCols);
    }

    /**
     *  @private
     */
    override protected function indexToColumn(index:int):int
    {
        if (direction == TileBaseDirection.VERTICAL)
        {
            var numRows:int = maxRows > 0 ? maxRows : rowCount;
            return Math.floor(index / numRows);
        }

        var numCols:int = maxColumns > 0 ? maxColumns : columnCount;
        return index % numCols;
    }

    /**
     *  @private
     */
    override public function indicesToIndex(rowIndex:int, columnIndex:int):int
    {
        if (direction == TileBaseDirection.VERTICAL)
        {
            var numRows:int = maxRows > 0 ? maxRows : rowCount;
            return columnIndex * numRows + rowIndex;
        }

        var numCols:int = maxColumns > 0 ? maxColumns : columnCount;
        return rowIndex * numCols + columnIndex;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function collectionChangeHandler(event:Event):void
    {
        if (event is CollectionEvent)
        {
            var ce:CollectionEvent = CollectionEvent(event);

            if (ce.location == 0 || ce.kind == CollectionEventKind.REFRESH)
            {
                itemsNeedMeasurement = true;
                invalidateProperties();
            }

            // may not want to do this if we are running data effect
            if (ce.kind == CollectionEventKind.REMOVE)
            {
                // oldIndex does not take into account offscreen rows & columns...scroll
                // position has to be adjusted if items are removed before the first
                // *visible* item (not taking into account offscreen rows/columns).
                var oldIndex:int = indicesToIndex(verticalScrollPosition,horizontalScrollPosition);
                
                // ListBase handles adjusting the iterator for listType = "vertical". 
                // It should probably handle this case as well, in a more general manner.
                // There's more complexity for TileList, in that removing n items
                // before the first visible item where n is not a multiple of the row/column
                // size might actually result in a visual shift in scroll position (in order
                // to keep the first onscreen item onscreen).
                // Meanwhile...we expect full layout after this, but we need to make sure
                // that the scroll positions and the iterator are at the right place.
                if (ce.location < oldIndex)
                {
                    oldIndex -= ce.items.length;
                    super.collectionChangeHandler(event);
                    var newOffscreenRowsTop:int = 0;
                    var newOffscreenColumnsLeft:int = 0;
                    if (direction == TileBaseDirection.HORIZONTAL)
                    {
                        super.verticalScrollPosition = indexToRow(oldIndex);
                        newOffscreenRowsTop = Math.min(offscreenExtraRows / 2, verticalScrollPosition);
                    }
                    else
                    {
                        super.horizontalScrollPosition = indexToColumn(oldIndex);
                        newOffscreenColumnsLeft = Math.min(offscreenExtraColumns / 2, horizontalScrollPosition);
                    }
                    var index:int = scrollPositionToIndex(horizontalScrollPosition - newOffscreenColumnsLeft,
                                                          verticalScrollPosition - newOffscreenRowsTop);
                    seekPositionSafely(index);
                    return;
                }
            }

        }

        super.collectionChangeHandler(event);
    }

    /**
     *  @private
     */
    mx_internal override function reconstructDataFromListItems():Array
    {
        // super behavior is fine for horizontal layout
        if (direction == TileBaseDirection.HORIZONTAL || !listItems)
            return (super.reconstructDataFromListItems());

        // might make sense to ignore offscreen columns here
        var items:Array = [];

        if (listItems.length > 0)
        {
            for (var j:int = 0; j < listItems[0].length; j++)
            {
                for (var i:int = 0; i < listItems.length; i++)
                {
                    if (listItems[i] && listItems[i].length > j)
                    {
                        var renderer:IListItemRenderer = listItems[i][j] as IListItemRenderer;
                        var data:Object;
                        if (renderer)
                        {
                            data = renderer.data;
                            items.push(data);
                        }
                    }
                }
            }
        }
        return items;
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
                setRowHeight(measureHeightOfItems(0, 1));
            if (isNaN(explicitColumnWidth))
                setColumnWidth(measureWidthOfItems(0, 1));
        }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // setup the tile sizes before calling the base class
        if (explicitColumnCount > 0 && isNaN(explicitColumnWidth))
        {
            // enforce that we can see the right number of columns
            // even if we squeeze the columns
            setColumnWidth(Math.floor((width - viewMetrics.left - viewMetrics.right) / explicitColumnCount));

        }
        if (explicitRowCount > 0 && isNaN(explicitRowHeight))
        {
            // enforce that we can see the right number of columns
            // even if we squeeze the columns
            setRowHeight(Math.floor((height - viewMetrics.top - viewMetrics.bottom) / explicitRowCount));

        }

        super.updateDisplayList(unscaledWidth, unscaledHeight);

        drawTileBackgrounds();
    }

    /**
     *  Draws the backgrounds, if any, behind all of the tiles.
     *  This implementation makes a Sprite names "tileBGs" if
     *  it doesn't exist, adds it to the back
     *  of the z-order in the <code>listContent</code>, and
     *  calls <code>drawTileBackground()</code> for each visible
     *  tile.
     */
    protected function drawTileBackgrounds():void
    {
        var tileBGs:Sprite = Sprite(listContent.getChildByName("tileBGs"));
        if (!tileBGs)
        {
            tileBGs = new FlexSprite();
            tileBGs.mouseEnabled = false;
            tileBGs.name = "tileBGs";
            listContent.addChildAt(tileBGs, 0)
        }

        var colors:Array;

        colors = getStyle("alternatingItemColors");

        if (!colors || colors.length == 0)
        {
            while (tileBGs.numChildren > n)
            {
                tileBGs.removeChildAt(tileBGs.numChildren - 1);
            }
            return;
        }

        StyleManager.getColorNames(colors);

        var curItem:int = 0;
        for (var i:int = 0; i < rowCount; i++)
        {
            for (var j:int = 0; j < columnCount; j++)
            {
                // Height is usually as tall is the items in the row,
                // but not if it would extend below the bottom of listContent.
                var height:Number = (i < rowCount - 1) ? rowHeight :
                                Math.min(rowHeight,
                                         listContent.height - ((rowCount - 1) * rowHeight));

                var width:Number = (j < columnCount - 1) ? columnWidth : 
                                Math.min(columnWidth,
                                         listContent.width - ((columnCount - 1) * columnWidth));
                var item:IListItemRenderer = (listItems[i] ? listItems[i][j] : null);
                var actualIndex:int = (verticalScrollPosition + i) * columnCount + (horizontalScrollPosition + j);
                var bg:DisplayObject = drawTileBackground(tileBGs, i, j, width, height, colors[actualIndex % colors.length], item);
                bg.y = i * rowHeight;
                bg.x = j * columnWidth;

            }
        }

        var n:int = rowCount * columnCount;
        while (tileBGs.numChildren > n)
        {
            tileBGs.removeChildAt(tileBGs.numChildren - 1);
        }
    }

    /**
     *  Draws the background for an individual tile. 
     *  Takes a Sprite object, applies the background dimensions
     *  and color, and returns the sprite with the values applied.
     *  
     *  @param s The Sprite that contains the individual tile backgrounds.
     *  @param rowIndex The index of the row that contains the tile.
     *  @param columnIndex The index of the column that contains the tile.
     *  @param width The width of the background.
     *  @param height The height of the background.
     *  @param color The fill color for the background.
     *  @param item The item renderer for the tile.
     * 
     *  @return The background Sprite.
     * 
     */
    protected function drawTileBackground(s:Sprite, rowIndex:int, columnIndex:int, width:Number, height:Number,  
                                                                    color:uint, item:IListItemRenderer):DisplayObject
    {
        // trace("drawTileBackground " + rowIndex + " " + col);

        var tileBGIndex:int = rowIndex * columnCount + columnIndex;

        var bg:Shape;
        if (tileBGIndex < s.numChildren)
        {
            bg = Shape(s.getChildAt(tileBGIndex));
        }
        else
        {
            bg = new FlexShape();
            bg.name = "tileBackground";
            s.addChild(bg);
        }

        var g:Graphics = bg.graphics;
        g.clear();
        g.beginFill(color, getStyle("backgroundAlpha"));
        g.drawRect(0, 0, width, height);
        g.endFill();

        return bg;
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        var selectedListItem:IListItemRenderer;

        if (!iteratorValid) return;

        if (!collection) return;

        switch (event.keyCode)
        {
            case Keyboard.UP:
            case Keyboard.DOWN:
            {
                moveSelectionVertically(event.keyCode,
                                        event.shiftKey, 
                                        event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.LEFT:
            case Keyboard.RIGHT:
            {
                moveSelectionHorizontally(event.keyCode, 
                                          event.shiftKey, 
                                          event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.END:
            case Keyboard.HOME:
            case Keyboard.PAGE_UP:
            case Keyboard.PAGE_DOWN:
            {
                if (direction == TileBaseDirection.VERTICAL)
                {
                    moveSelectionHorizontally(event.keyCode, 
                                              event.shiftKey, 
                                              event.ctrlKey);
                }
                else
                {
                    moveSelectionVertically(event.keyCode, 
                                            event.shiftKey, 
                                            event.ctrlKey);
                }
                event.stopPropagation();
                break;
            }

            case Keyboard.SPACE:
            {
                if (caretIndex < 0)
                    break;
                var rowIndex:int = indexToRow(caretIndex);
                var colIndex:int = indexToColumn(caretIndex);
                selectedListItem = listItems
                    [rowIndex - verticalScrollPosition]
                    [colIndex - horizontalScrollPosition];
                selectItem(selectedListItem, event.shiftKey, event.ctrlKey);
                break;
            }

            default:
            {
                if (findKey(event.keyCode))
                    event.stopPropagation();
            }
        }
    }

    /**
     *  @private
     */
    override protected function scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            if (!liveScrolling &&
                ScrollEvent(event).detail == ScrollEventDetail.THUMB_TRACK)
            {
                return;
            }

            var scrollBar:ScrollBar = ScrollBar(event.target);
            var pos:Number = scrollBar.scrollPosition;
            var delta:int;
            var startIndex:int;
            var o:EdgeMetrics;
            var bookmark:CursorBookmark;

            if (scrollBar == verticalScrollBar)
            {
                delta = pos - verticalScrollPosition;
                
                super.scrollHandler(event);
                
                if (Math.abs(delta) >= listItems.length || !iteratorValid)
                {
                    startIndex = indicesToIndex(pos, horizontalScrollPosition);
                    try
                    {
                        iterator.seek(CursorBookmark.FIRST, startIndex);
                        if (!iteratorValid)
                        {
                            iteratorValid = true;
                            lastSeekPending = null;
                        }
                    }
                    catch(e:ItemPendingError)
                    {
                        lastSeekPending = new ListBaseSeekPending(CursorBookmark.FIRST, startIndex);
                        e.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler, 
                                                lastSeekPending));
                        // trace("IPE in UpdateDisplayList");
                        iteratorValid = false;
                        // don't do anything, we'll repaint when the data arrives
                    }
                    bookmark = iterator.bookmark;
                     //if we scrolled more than the number of scrollable rows
                    clearIndicators();
                    clearVisibleData();
                    makeRowsAndColumns(0, 0, listContent.width, listContent.height, 0, 0);
                    iterator.seek(bookmark, 0);
                    drawRowBackgrounds();
                }
                else if (delta != 0)
                {
                    scrollVertically(pos, Math.abs(delta), delta > 0);
                }
            }
            else
            {
                delta = pos - horizontalScrollPosition;
                
                super.scrollHandler(event);
                
                if (Math.abs(delta) >= listItems[0].length || !iteratorValid)
                {
                    startIndex = indicesToIndex(verticalScrollPosition, pos);
                    try
                    {
                        iterator.seek(CursorBookmark.FIRST, startIndex);
                        if (!iteratorValid)
                        {
                            iteratorValid = true;
                            lastSeekPending = null;
                        }
                    }
                    catch(e:ItemPendingError)
                    {
                        lastSeekPending = new ListBaseSeekPending(CursorBookmark.FIRST, startIndex);
                        e.addResponder(new ItemResponder(seekPendingResultHandler, seekPendingFailureHandler, 
                                                lastSeekPending));
                        // trace("IPE in UpdateDisplayList");
                        iteratorValid = false;
                        // don't do anything, we'll repaint when the data arrives
                    }
                    bookmark = iterator.bookmark;
                     //if we scrolled more than the number of scrollable rows
                    clearIndicators();
                    clearVisibleData();
                    makeRowsAndColumns(0, 0, listContent.width, listContent.height, 0, 0);
                    iterator.seek(bookmark, 0);
                    drawRowBackgrounds();
                }
                else if (delta != 0)
                {
                    scrollHorizontally(pos, Math.abs(delta), delta > 0);
                }
            }
        }
    }

    /**
     *  @private 
     */
    override public function scrollToIndex(index:int):Boolean
    {
        var newVPos:int;
        var newHPos:int;

        var firstIndex:int = scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition);
        var numItemsVisible:int = ((listItems.length - offscreenExtraRowsTop - offscreenExtraRowsBottom) * 
                                    (listItems[0].length - offscreenExtraColumnsLeft - offscreenExtraColumnsRight));
        if (index >= firstIndex + numItemsVisible || index < firstIndex)
        {
            newVPos = Math.min(indexToRow(index), maxVerticalScrollPosition);
            newHPos = Math.min(indexToColumn(index), maxHorizontalScrollPosition);
        
            try
            {
                iterator.seek(CursorBookmark.FIRST, scrollPositionToIndex(horizontalScrollPosition, verticalScrollPosition));
                super.horizontalScrollPosition = newHPos;
                super.verticalScrollPosition = newVPos;
            }
            catch(e:ItemPendingError)
            {
            }
            return true;
        }
        return false;
    }

    /**
     *  Called from the <code>updateDisplayList()</code> method to adjust the size and position of
     *  listContent.
     *  
     *  @param unscaledWidth The width of the listContent before any external scaling is applied.
     *  
     *  @param unscaledHeight The height of the listContent before any external scaling is applied.
     */
    override protected function adjustListContent(unscaledWidth:Number = -1,
                                       unscaledHeight:Number = -1):void
    {
        super.adjustListContent(unscaledWidth, unscaledHeight);

        if (!collection)
            return;

        var partial:Boolean;
        var index:int;
        var numRows:int;
        var numCols:int;
        var numItems:int = collection.length;

        if (direction == TileBaseDirection.VERTICAL)
        {
            numRows = maxRows > 0 ? maxRows : Math.max(Math.floor(listContent.heightExcludingOffsets / rowHeight), 1);
            numCols = Math.max(Math.ceil(listContent.widthExcludingOffsets / columnWidth), 1);
            if (numRows != lastRowCount)
            {
                partial = (listContent.widthExcludingOffsets / columnWidth) != Math.ceil(listContent.widthExcludingOffsets / columnWidth);
                var maxHSP:int = Math.max(Math.ceil(numItems / numRows) - numCols, 0);
                if (partial)
                    maxHSP++;
                if (horizontalScrollPosition > maxHSP)
                    $horizontalScrollPosition = maxHSP;
                setRowCount(numRows);
                setColumnCount(numCols);
                index = scrollPositionToIndex(Math.max(0, horizontalScrollPosition - offscreenExtraColumnsLeft), 
                                            verticalScrollPosition);
                        
                seekPositionSafely(index);
            }
            lastRowCount = numRows;
        }
        else // horizontal
        {
            numCols = maxColumns > 0 ? maxColumns : Math.max(Math.floor((listContent.widthExcludingOffsets)/ columnWidth), 1);
            numRows = Math.max(Math.ceil(listContent.heightExcludingOffsets / rowHeight), 1);
            if (numCols != lastColumnCount)
            {
                partial = (listContent.heightExcludingOffsets / rowHeight) != Math.ceil(listContent.heightExcludingOffsets / rowHeight);

                var maxVSP:int = Math.max(Math.ceil(numItems / numCols) - numRows, 0);
                if (partial)
                    maxVSP++;
                if (verticalScrollPosition > maxVSP)
                    $verticalScrollPosition = maxVSP;
                setRowCount(numRows);
                setColumnCount(numCols);
                index = scrollPositionToIndex(horizontalScrollPosition, Math.max(0,verticalScrollPosition-offscreenExtraRowsTop));
                seekPositionSafely(index);
            }
            lastColumnCount = numCols;
        }

    }

}

}
