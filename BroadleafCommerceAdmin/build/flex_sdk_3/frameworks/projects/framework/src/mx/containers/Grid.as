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

package mx.containers
{

import mx.containers.gridClasses.GridColumnInfo;
import mx.containers.gridClasses.GridRowInfo;
import mx.core.EdgeMetrics;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Number of pixels between children in the horizontal direction. 
 *  The default value is 8.
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between children in the vertical direction. 
 *  The default value is 6.
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

[IconFile("Grid.png")]

/**
 *  A Grid container lets you arrange children as rows and columns
 *  of cells, similar to an HTML table. 
 *  The Grid container contains one or more rows, and each row can contain
 *  one or more cells, or items. You use the following tags to define a Grid control:
 *
 *  <ul>
 *     <li>The <code>&lt;mx:Grid&gt;</code> tag defines a Grid container.</li>
 * 
 *     <li>The <code>&lt;mx:GridRow&gt;</code> tag defines a grid row, 
 *     which has one or more cells. The grid row must be a child of the 
 *     <code>&lt;Grid&gt;</code> tag.</li>
 * 
 *     <li>The <code>&lt;mx:GridItem&gt;</code> tag defines a grid cell,
 *     and must be a child of the <code>&lt;GridRow&gt;</code> tag.
 *     The <code>&lt;mx:GridItem&gt;</code> tag can contain
 *     any number of children.</li>
 *  </ul>
 * 
 *  <p>The height of all the cells in a single row is the same,
 *  but each row can have a different height. 
 *  The width of all cells in a single column is the same,
 *  but each column can have a different width. 
 *  You can define a different number of cells
 *  for each row or each column of the Grid container. 
 *  In addition, a cell can span multiple columns
 *  or multiple rows of the container.</p>
 *  
 *  <p>The Grid, GridRow, and GridItem containers have the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Grid height</td>
 *           <td>The sum of the default or explicit heights of all rows plus the gaps between rows.</td>
 *        </tr>
 *        <tr>
 *           <td>Grid width</td>
 *           <td>The sum of the default or explicit width of all columns plus the gaps between columns.</td>
 *        </tr>
 *        <tr>
 *           <td>Height of each row and each cell</td>
 *           <td>The default or explicit height of the tallest item in the row. If a GridItem container does not 
 *               have an explicit size, its default height is the default or explicit height of the child in the cell.</td>
 *        </tr>
 *        <tr>
 *           <td>Width of each column and each cell</td>
 *           <td>The default or explicit width of the widest item in the column. If a GridItem container does not have an explicit 
 *               width, its default width is the default or explicit width of the child in the cell.</td>
 *        </tr>
 *        <tr>
 *           <td>Gap between rows and columns</td>
 *           <td>Determined by the horizontalGap and verticalGap properties of the Grid class. The default value for both 
 *               gaps is 6 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for the top, bottom, left, and right values, for all three container classes.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Grid&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass, except the <code>Box.direction</code>
 *  property, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Grid
 *    <strong>Styles</strong>
 *    horizontalGap="8"
 *    verticalGap="6"
 *    &gt;
 *      ...
 *    <i>These child tags are examples only:</i>
 *       &lt;mx:GridRow id="row1"&gt;
 *        &lt;mx:GridItem
 *          rowSpan="1"
 *          colSpan="1">
 *            &lt;mx:Button label="Button 1"/&gt;
 *        &lt;/mx:GridItem&gt;
 *        ...
 *       &lt;/mx:GridRow&gt;
 *    ...
 *  &lt;/mx:Grid&gt;
 *  </pre>
 *  
 *  @includeExample examples/GridLayoutExample.mxml
 *
 *  @see mx.containers.GridRow
 *  @see mx.containers.GridItem
 */
public class Grid extends Box
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Grid()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Minimum, maximum, and preferred width of each column.
     */
    private var columnWidths:Array /* of GridColumnInfo */;

    /**
     *  @private
     *  Minimum, maximum, and preferred height of each row.
     */
    private var rowHeights:Array /* of GridRowInfo */;

    /**
     *  @private
     */
    private var needToRemeasure:Boolean = true;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods:UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function invalidateSize():void
    {
        // When the Grid's size is invalidated, the rowHeights
        // and columnWidths arrays need to be recalculated.
        // Those arrays are usually recalculated in measure(),
        // but set a flag in case measure() isn't called (which
        // will happen if the Grid's width and height are explicit).
        if (!isNaN(explicitWidth) && !isNaN(explicitHeight))        
            needToRemeasure = true;
        
        super.invalidateSize();
    }   

    /**
     *  Calculates the preferred, minimum, and maximum sizes of the Grid.

     *  <p>You should not call this method directly; it is an advanced 
     *  method for use in subclassing.
     *  The Flex LayoutManger calls the <code>measure()</code> method 
     *  at the appropriate time.
     *  At application startup, the Flex LayoutManager attempts
     *  to measure all components from the children to the parents
     *  before setting them to their final sizes.</p>
     *
     *  <p>To understand how the Grid container calculates its measurements,
     *  assume that every GridItem container has its <code>rowSpan</code>
     *  property and <code>colSpan</code> property set to 1.
     *  The measured width of the first column of the Grid container
     *  is equal to the maximum among of the measured widths
     *  of all GridItem containers in the first column.
     *  Similarly, the measured width of the second column is
     *  the maximum of all measured widths among the GridItem containers
     *  in the second column, and so on.
     *  The <code>measuredWidth</code> of the entire Grid container
     *  is the sum of all columns' measured widths, plus the thickness
     *  of the border, plus the left and right padding, plus room
     *  for the horizontal gap between adjacent grid cells.</p>
     *
     *  <p>The <code>measuredHeight</code>, <code>minWidth</code>,
     *  <code>minHeight</code>, <code>maxWidth</code>, and
     *  <code>maxHeight</code> properties' values are all calculated
     *  in a similar manner, by adding together the values of the
     *  GridItem containers' <code>measuredHeight</code> properties,
     *  <code>minWidth</code> properties, and so on.</p>
     *
     *  <p>If a GridItem container's <code>colSpan</code> property is 3,
     *  that GridItem container's <code>measuredWidth</code> is divided
     *  among 3 columns.
     *  If the <code>measuredWidth</code> is divided equally,
     *  each of the three columns calculates its measured width
     *  as if the GridItem container were only in that column
     *  and the GridItem container's <code>measuredWidth</code>
     *  were one-third of its actual value.</p>
     *
     *  <p>However, the GridItem container's <code>measuredWidth</code>
     *  property is not always divided equally among all the columns it spans.
     *  If some of the columns have a property with a percentage value
     *  of <code>width</code>, the GridItem container's
     *  <code>measuredWidth</code> property is divided accordingly,
     *  attempting to give each column the requested percentage
     *  of the Grid container.</p>
     *
     *  <p>All of the values described previously are the
     *  <i>measured</i> widths and heights of Grid.
     *  The user can override the measured values by explicitly
     *  supplying a value for the following properties:</p>
     *
     *  <ul>
     *    <li><code>minHeight</code></li>
     *    <li><code>minWidth</code></li>
     *    <li><code>maxHeight</code></li>
     *    <li><code>maxWidth</code></li>
     *    <li><code>height</code></li>
     *    <li><code>width</code></li>
     *  </ul>
     *
     *  <p>If you override this method, your implementation must call the 
     *  <code>super.measure()</code> method or set the
     *  <code>measuredHeight</code> and <code>measuredWidth</code> properties.
     *  You may also optionally set the following properties:</p>
     * 
     *  <ul>
     *    <li><code>measuredMinWidth</code></li>
     *    <li><code>measuredMinHeight</code></li>
     *  </ul>
     * 
     *  <p>These properties correspond to the layout properties listed previously 
     *  and, therefore, are not documented separately.</p>
     */
    override protected function measure():void
    {
        // 1. Determine the number of grid columns,
        // taking into account rowSpan and colSpan

        var numGridRows:int = 0;
        var numGridColumns:int = 0;
        var columnOccupiedUntilRow:Array = [];
        var gridRow:GridRow;
        var gridItem:GridItem;
        var i:int;
        var j:int;
        var colIndex:int;

        var rowList:Array = []; // GridRows

        for (var index:int = 0; index < numChildren; index++)
        {
            gridRow = GridRow(getChildAt(index));
            if (gridRow.includeInLayout)
            {
                rowList.push(gridRow);
                numGridRows++;
            }
        }


        for (i = 0; i < numGridRows; i++)
        {
            colIndex = 0;
            gridRow = rowList[i];

            // Cache the number of children as a property on the gridRow
            gridRow.numGridItems = gridRow.numChildren;

            // Tell the grid row what row number it is
            gridRow.rowIndex = i;
            for (j = 0; j < gridRow.numGridItems; j++)
            {           
                // If this column is occupied by a cell in the previous row,
                // then push cells in this row to the right.
                if (i > 0)
                {
                    var occupied:int = columnOccupiedUntilRow[colIndex];
                    while (!isNaN(occupied) && occupied >= i)
                    {
                        colIndex++;
                        occupied = columnOccupiedUntilRow[colIndex];
                    }
                }

                // Set the position of this GridItem to the next
                // available space.
                gridItem = GridItem(gridRow.getChildAt(j));
                gridItem.colIndex = colIndex;

                // If this cell extends to rows beyond this one, remember
                // which columns are occupied.
                if (gridItem.rowSpan > 1)
                {
                    var lastRowOccupied:int = i + gridItem.rowSpan - 1;
                    for (var k:int = 0; k < gridItem.colSpan; k++)
                    {
                        columnOccupiedUntilRow[colIndex + k] = lastRowOccupied;
                    }
                }

                colIndex += gridItem.colSpan;
            }

            if (colIndex > numGridColumns)
                numGridColumns = colIndex;
        }

        // 2. Create the rowHeights and colWidths arrays.
        // Initially set all heights and widths to zero.

        rowHeights = new Array(numGridRows);
        columnWidths = new Array(numGridColumns);

        for (i = 0; i < numGridRows; i++)
        {
            rowHeights[i] = new GridRowInfo();
        }
        for (i = 0; i < numGridColumns; i++)
        {
            columnWidths[i] = new GridColumnInfo();
        }
        
        // 3. Visit all the GridItems again.
        // Expand each row and each column so it's large enough
        // to hold its GridItems.
        // We'll deal with rowSpans and colSpans of 1 first,
        // then rowSpans and colSpans of 2, and so on.

        var BIG_INT:int = int.MAX_VALUE;
        var curSpan:int;
        var nextSpan:int = 1;
        
        var horizontalGap:Number = getStyle("horizontalGap");
        var verticalGap:Number = getStyle("verticalGap");
        
        do
        {
            curSpan = nextSpan;
            nextSpan = BIG_INT;  

            for (i = 0; i < numGridRows; i++)
            {
                gridRow = rowList[i];

                // Store pointers to the columnWidths and rowHeights arrays
                // on each GridRow object.
                gridRow.columnWidths = columnWidths;
                gridRow.rowHeights = rowHeights;

                for (j = 0; j < gridRow.numGridItems; j++)
                {
                    gridItem = GridItem(gridRow.getChildAt(j));
                    var rowSpan:int = gridItem.rowSpan;
                    var colSpan:int = gridItem.colSpan;

                    // During this iteration of the outermost do-while loop,
                    // we're dealing with rows and columns that have a rowSpan
                    // or colSpan equal to curSpan.  If we encounter a row or
                    // column with a larger span, remember its span in nextSpan
                    // for the next iteration through the do-while loop.

                    if (rowSpan == curSpan)
                        distributeItemHeight(gridItem, i, verticalGap, rowHeights);
                    else if (rowSpan > curSpan && rowSpan < nextSpan)
                        nextSpan = rowSpan;

                    if (colSpan == curSpan)
                    {
                        distributeItemWidth(gridItem, gridItem.colIndex,
                                            horizontalGap, columnWidths);
                    }
                    else if (colSpan > curSpan && colSpan < nextSpan)
                    {
                        nextSpan = colSpan;
                    }
                }
            }
        }
        while (nextSpan < BIG_INT);

        // 4. Reconcile min/preferred/max values, so that min <= pref <= max.
        // Also compute sums of all measurements.

        var minWidth:Number = 0;
        var minHeight:Number = 0;
        var preferredWidth:Number = 0;
        var preferredHeight:Number = 0;

        for (i = 0; i < numGridRows; i++)
        {
            var rowInfo:GridRowInfo = rowHeights[i];

            if (rowInfo.min > rowInfo.preferred)
                rowInfo.min = rowInfo.preferred;
            if (rowInfo.max < rowInfo.preferred)
                rowInfo.max = rowInfo.preferred;

            minHeight += rowInfo.min;
            preferredHeight += rowInfo.preferred;
        }

        for (i = 0; i < numGridColumns; i++)
        {
            var columnInfo:GridColumnInfo = columnWidths[i];

            if (columnInfo.min > columnInfo.preferred)
                columnInfo.min = columnInfo.preferred;
            if (columnInfo.max < columnInfo.preferred)
                columnInfo.max = columnInfo.preferred;

            minWidth += columnInfo.min;
            preferredWidth += columnInfo.preferred;
        }

        // 5. Add horizontal space for the gaps between the grid cells
        // and the margins around them.

        // Add space for grid's left and right margins
        var vm:EdgeMetrics = viewMetricsAndPadding;
        var padding:Number = vm.left + vm.right;
        var row:GridRow;
        var rowVm:EdgeMetrics;
        var maxRowPadding:Number = 0;

        // Add space for horizontal gaps between grid items.
        if (numGridColumns > 1)
            padding += getStyle("horizontalGap") * (numGridColumns - 1);

        // Add space for the gridrow's left and right margins.
        for (i = 0; i < numGridRows; i++)
        {
            row = rowList[i];
            rowVm = row.viewMetricsAndPadding;
            var rowPadding:Number = rowVm.left + rowVm.right;
            if (rowPadding > maxRowPadding)
                maxRowPadding = rowPadding;
        }
        padding += maxRowPadding;

        minWidth += padding;
        preferredWidth += padding;

        // 6. Add vertical space for the gaps between grid cells
        // and the margins around them.

        // Add space for grid's left and right margins.
        padding = vm.top + vm.bottom;

        // Add space for vertical gaps between grid items.
        if (numGridRows > 1)
            padding += getStyle("verticalGap") * (numGridRows - 1);

        // Add each of the grid rows' margins.
        for (i = 0; i < numGridRows; i++)
        {
            row = rowList[i];
            rowVm = row.viewMetricsAndPadding;
            padding += rowVm.top + rowVm.bottom;
        }

        minHeight += padding;
        preferredHeight += padding;

        // 7. Now that the Grid is finished measuring itself,
        // update all the measurements of the child GridRows.

        for (i = 0; i < numGridRows; i++)
        {
            row = rowList[i];
            row.updateRowMeasurements();
        }

        // 8. Up until now, we've calculated all our measurements
        // based on the GridItems.
        // If someone has explicitly set the width (or percentWidth
        // or whatever) of a GridRow, we've been ignoring it.
        // Run the standard Box measurement algorithm, which will
        // take into account hard-coded values on the GridRow objects,
        // and combine the measured values with the ones
        // we've calculated based on GridItems.

        super.measure();
        
        measuredMinWidth = Math.max(measuredMinWidth, minWidth);
        measuredMinHeight = Math.max(measuredMinHeight, minHeight);
        measuredWidth = Math.max(measuredWidth, preferredWidth);
        measuredHeight = Math.max(measuredHeight, preferredHeight);
        
        needToRemeasure = false;        
    }

    /**
     *  Sets the size and position of each child of the Grid.
     *
     *  <p>You should not call this method directly; it is an advanced 
     *  method for use in subclassing.
     *  The Flex LayoutManager calls the <code>updateDisplayList</code>
     *  method at the appropriate time.
     *  At application startup, the Flex LayoutManager calls
     *  the <code>updateDisplayList()</code> method on every component,
     *  starting with the root and working downward.</p>
     *
     *  <p>The Grid container follows the same layout rules
     *  as the VBox container.  
     *  The positions and sizes of the GridRow containers
     *  are calculated the same way that a VBox container
     *  determines the positions and sizes of its children.
     *  Similarly, a GridRow container positions its GridItem containers
     *  using a similar layout algorithm of an HBox container.</p>
     *
     *  <p>The only difference is that the GridRow containers
     *  all coordinate with one another, so they all choose
     *  the same positions and sizes for their children
     *  (so that the columns of the Grid container align). </p>
     *
     *  <p>If you override this method, your implementation should call
     *  the <code>super.updateDisplayList()</code> method
     *  and call the <code>move()</code> and the <code>setActualSize()</code>
     *  methods on each of the children.
     *  For the purposes of performing layout, you should get the size
     *  of this container from the <code>unscaledWidth</code>
     *  and <code>unscaledHeight</code> properties, not the
     *  <code>width</code> and <code>height</code> properties.
     *  The <code>width</code> and <code>height</code> properties do not
     *  take into account the values of the <code>scaleX</code>
     *  and <code>scaleY</code> properties for this container.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.   
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {       
        // If the measure function wasn't called (because the widths
        // and heights of the Grid are explicitly set), then we need
        // to generate the rowHeights and columnWidths arrays now.
        if (needToRemeasure)
            measure();
            
        // Follow standard VBox rules for laying out the child rows.
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var n:int = 0;
        var i:int;
        var child:GridRow;

        var rowList:Array = []; // GridRows

        for (var index:int = 0; index < numChildren; index++)
        {
            child = GridRow(getChildAt(index));
            if (child.includeInLayout)
            {
                rowList.push(child);
                n++;
            }
        }

        // Copy the row heights, which were calculated by the VBox layout
        // algorithm, into the rowHeights array.
        for (i = 0; i < n; i++)
        {
            child = rowList[i];
            
            rowHeights[i].y = child.y;
            rowHeights[i].height = child.height;
        }

        // Now that all the rows have been layed out, lay out the GridItems
        // in those rows, based on the info stored in the colWidths and
        // rowHeights arrays.
        for (i = 0; i < n; i++)
        {
            child = rowList[i];
            
            child.doRowLayout(child.width * child.scaleX,
                              child.height * child.scaleY);
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function distributeItemHeight(item:GridItem, rowIndex:Number,
                                          verticalGap:Number,
                                          rowHeights:Array):void
    {
        var maxHeight:Number = item.maxHeight;
        var preferredHeightToDistribute:Number =
            item.getExplicitOrMeasuredHeight();
        var minHeightToDistribute:Number = item.minHeight;
        var rowSpan:int = item.rowSpan;
        var totalFlex:Number = 0;
        var divideEqually:Boolean = false;
        var i:int;
        var rowInfo:GridRowInfo;

        // If the row(s) spanned by this GridItem are already non-empty,
        // subtract the existing sizes of those rows from the item's height.
        for (i = rowIndex; i < rowIndex + rowSpan; i++)
        {
            rowInfo = rowHeights[i];
            preferredHeightToDistribute -= rowInfo.preferred;
            minHeightToDistribute -= rowInfo.min;
            totalFlex += rowInfo.flex;
        }

        // Subtract space for gaps between the rows.
        if (rowSpan > 1)
        {
            var gap:Number = verticalGap * (rowSpan - 1);
            preferredHeightToDistribute -= gap;
            minHeightToDistribute -= gap;
        }

        // If none of the rows spanned by this item are resizable,
        // then divide space among the rows equally.
        if (totalFlex == 0)
        {
            totalFlex = rowSpan;
            divideEqually = true;
        }

        // If we haven't yet distributed the height of the object,
        // divide remaining height among the rows.
        // If some rows are resizable and others are not,
        // allocate space to the resizable ones.
        preferredHeightToDistribute =
            preferredHeightToDistribute > 0 ?
            Math.ceil(preferredHeightToDistribute / totalFlex) :
            0;
        minHeightToDistribute =
            minHeightToDistribute > 0 ?
            Math.ceil(minHeightToDistribute / totalFlex) :
            0;          
        
        for (i = rowIndex; i < rowIndex + rowSpan; i++)
        {
            rowInfo = rowHeights[i];
            var flex:Number = divideEqually ? 1 : rowInfo.flex;
            rowInfo.preferred += preferredHeightToDistribute * flex;
            rowInfo.min += minHeightToDistribute * flex;
        }

        // The GridItem.maxHeight attribute is respected only for rows
        // with a rowSpan of 1.
        if (rowSpan == 1 && maxHeight < rowInfo.max)
            rowInfo.max = maxHeight;
    }

    /**
     *  @private
     */
    private function distributeItemWidth(item:GridItem, colIndex:int,
                                         horizontalGap:Number, columnWidths:Array):void
    {
        var maxWidth:Number = item.maxWidth;
        var preferredWidthToDistribute:Number =
            item.getExplicitOrMeasuredWidth();
        var minWidthToDistribute:Number = item.minWidth;
        var colSpan:int = item.colSpan;
        var percentWidth:Number = item.percentWidth;
        var totalFlex:Number = 0;
        var divideEqually:Boolean = false;
        var i:int;
        var columnInfo:GridColumnInfo;

        // If the column(s) spanned by this GridItem are already non-empty,
        // subtract the existing sizes of those columns from the item's width.
        for (i = colIndex; i < colIndex + colSpan; i++)
        {
            columnInfo = columnWidths[i];
            preferredWidthToDistribute -= columnInfo.preferred;
            minWidthToDistribute -= columnInfo.min;
            totalFlex += columnInfo.flex;
        }

        // Subtract space for gaps between the columns.
        if (colSpan > 1)
        {
            var gap:Number = horizontalGap * (colSpan - 1);
            preferredWidthToDistribute -= gap;
            minWidthToDistribute -= gap;
        }

        // If none of the columns spanned by this item are resizable,
        // then divide space among the columns equally.
        if (totalFlex == 0)
        {
            totalFlex = colSpan;
            divideEqually = true;
        }

        // If we haven't yet distributed the width of the object,
        // divide remaining width among the columns.
        // If some columns are resizable and others are not,
        // allocate space to the resizable ones.
        preferredWidthToDistribute =
            preferredWidthToDistribute > 0 ?
            Math.ceil(preferredWidthToDistribute / totalFlex) :
            0;
        minWidthToDistribute =
            minWidthToDistribute > 0 ?
            Math.ceil(minWidthToDistribute / totalFlex) :
            0;
        
        for (i = colIndex; i < colIndex + colSpan; i++)
        {
            columnInfo = columnWidths[i];
            var flex:Number = divideEqually ? 1 : columnInfo.flex;
            columnInfo.preferred += preferredWidthToDistribute * flex;
            columnInfo.min += minWidthToDistribute * flex;
            if (percentWidth)
            {
                columnInfo.percent = Math.max(columnInfo.percent,
                                              percentWidth / colSpan);
            }
        }

        // The GridItem.maxWidth attribute is respected only for columns
        // with a colSpan of 1.
        if (colSpan == 1 && maxWidth < columnInfo.max)
            columnInfo.max = maxWidth;
    }   
}

}
