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

import flash.display.DisplayObject;
import mx.containers.gridClasses.GridColumnInfo;
import mx.containers.gridClasses.GridRowInfo;
import mx.containers.utilityClasses.Flex;
import mx.core.EdgeMetrics;
import mx.core.ScrollPolicy;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Horizontal alignment of children in the container.
 *  Possible values are <code>"left"</code>, <code>"center"</code>,
 *  and <code>"right"</code>.
 *  The default value is <code>"left"</code>.
 */
[Style(name="horizontalAlign", type="String", enumeration="left,center,right", inherit="no")]

/**
 *  Vertical alignment of children in the container.
 *  Possible values are <code>"top"</code>, <code>"middle"</code>,
 *  and <code>"bottom"</code>.
 *  The default value is <code>"top"</code>.
 */
[Style(name="verticalAlign", type="String", enumeration="bottom,middle,top", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="clipContent", kind="property")]
[Exclude(name="direction", kind="property")]
[Exclude(name="focusEnabled", kind="property")]
[Exclude(name="focusManager", kind="property")]
[Exclude(name="focusPane", kind="property")]
[Exclude(name="horizontalLineScrollSize", kind="property")]
[Exclude(name="horizontalPageScrollSize", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="horizontalScrollPosition", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="mouseFocusEnabled", kind="property")]
[Exclude(name="verticalLineScrollSize", kind="property")]
[Exclude(name="verticalPageScrollSize", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPosition", kind="property")]

[Exclude(name="adjustFocusRect", kind="method")]
[Exclude(name="getFocus", kind="method")]
[Exclude(name="isOurFocus", kind="method")]
[Exclude(name="setFocus", kind="method")]

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]
[Exclude(name="move", kind="event")]
[Exclude(name="scroll", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
[Exclude(name="horizontalGap", kind="style")]
[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalGap", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]
[Exclude(name="moveEffect", kind="effect")]

/**
 *  The GridRow container defines a row in a Grid container, and contains
 *  GridCell containers.
 *
 *  <p>GridRow containers have the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
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
 *  <p>The <code>&lt;mx:GridRow&gt;</code> must be a child of the 
 *  <code>&lt;mx:Grid&gt;</code> tag, and has one or more child 
 *  <code>&lt;mx:GridItem&gt;</code> tags that define the grid cells.</p>
 * 
 *  <p>The <code>&lt;mx:GridRow&gt;</code> container inherits the
 *  tag attributes of its superclass, and adds the following tag attributes:</p>
 * 
 *  <pre>
 *  &lt;mx:Grid&gt;
 *    &lt;mx:GridRow
 *    <strong>Styles</strong>
 *      horizontalAlign="left|center|right"
 *      verticalAlign="top|middle|bottom"
 *    &gt;
 *      &lt;mx:GridItem
 *        <i>child components</i>
 *      &lt;/mx:GridItem&gt;
 *      ...
 *    &lt;/mx:GridRow&gt;
 *    ...
 *  &lt;/mx:Grid&gt;
 *  </pre>
 *
 *  @see mx.containers.Grid
 *  @see mx.containers.GridItem
 *
 *  @includeExample examples/GridLayoutExample.mxml
 */
public class GridRow extends HBox
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
    public function GridRow()
    {
        super();

        // Set this to false so Container doesn't clip.
        super.clipContent = false;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Width of columns in the row.
     */
    internal var columnWidths:Array /* of GridColumnInfo */;

    /**
     *  @private
     *  Height of rows.
     */
    internal var rowHeights:Array /* of GridRowInfo */;

    /**
     *  @private
     *  Index of this row.
     */
    internal var rowIndex:int = 0;

    /**
     *  @private
     */
    internal var numGridItems:int;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  clipContent
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get clipContent():Boolean
    {
        return false;
    }

    /**
     *  @private
     *  Don't allow user to set clipContent.
     *  The Grid will clip all GridItems, if necessary.
     *  We don't want GridRows to do clipping, because GridItems with
     *  rowSpan > 1 will extend outside the borders of the GridRow.
     */
    override public function set clipContent(value:Boolean):void
    {
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get horizontalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     *  Don't allow user to set horizontalScrollPolicy.
     */
    override public function set horizontalScrollPolicy(value:String):void
    {
    }

    //----------------------------------
    //  verticalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     */
    override public function get verticalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     *  Don't allow user to set verticalScrollPolicy.
     */
    override public function set verticalScrollPolicy(value:String):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: DisplayObjectContainer
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  If the child index of a row item is changed, we need to recompute
     *  the column size and arrange children in proper order.
     *  Hence invalidate the size and layout of the Grid.
     */
    override public function setChildIndex(child:DisplayObject,
                                           newIndex:int):void
    {
        super.setChildIndex(child, newIndex);

        Grid(parent).invalidateSize();
        Grid(parent).invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  If something causes a GridRow's measurements to change,
     *  this function is called.
     *  Ordinarily, this function registers the object with the
     *  layout manager, so that measure is called later.
     *  For GridRows, we can't do measurement until after the parent Grid
     *  has done its measurement.
     *  Therefore, we register the parent Grid with the layout manager.
     *  The Grid.measure() contains code that calls measure()
     *  on each child GridRow.
     */
    override public function invalidateSize():void
    {
        super.invalidateSize();
        if (parent)
            Grid(parent).invalidateSize();
    }
  
    /**
     *  @private
     */
    override public function invalidateDisplayList():void
    {
        super.invalidateDisplayList();
        if (parent)
            Grid(parent).invalidateDisplayList();
    }

    /**
     *  Sets the size and position of each child of the GridRow container.
     *  For more information about the Grid layout algorithm, 
     *  see the <a href="Grid.html#updateDisplayList()">Grid.updateDisplayList()</a>
     *  method.
     *
     *  <p>You should not call this method directly.
     *  The Flex LayoutManager calls it at the appropriate time.
     *  At application startup, the Flex LayoutManager calls the
     *  <code>updateDisplayList()</code> method on every component,
     *  starting with the root and working downward.</p>
     *
     *  <p>This is an advanced method for use in subclassing.
     *  If you override this method, your implementation should call the
     *  <code>super.updateDisplayList()</code> method and call the
     *  <code>move()</code> and <code>setActualSize()</code> methods
     *  on each of the children.
     *  For the purposes of performing layout, you should get the size
     *  of this container from the <code>unscaledWidth</code> and
     *  <code>unscaledHeight</code> properties, not the <code>width</code>
     *  and <code>height</code> properties.
     *  The <code>width</code> and <code>height</code> properties
     *  do not take into account the value of the <code>scaleX</code>
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
        // Don't do any layout at the usual time.
        // Instead, we'll do our layout
        // when Grid.updateDisplayList() calls GridRow.doRowLayout().
    }

    /**
     *  @private
     *  Use the horizontalGap from my parent grid;
     *  ignore any horizontalGap that is set on the GridRow.
     */
    override public function getStyle(styleProp:String):*
    {
        return styleProp == "horizontalGap" && parent ?
               Grid(parent).getStyle("horizontalGap") :
               super.getStyle(styleProp);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Calculates the preferred, minimum and maximum sizes
     *  of the GridRow container.
     */
    internal function updateRowMeasurements():void
    {
        var numChildren:Number = columnWidths.length;

        var minWidth:Number = 0;
        var preferredWidth:Number = 0;

        for (var i:int = 0; i < numChildren; i++)
        {
            var child:GridColumnInfo = columnWidths[i];
            minWidth += child.min;
            preferredWidth += child.preferred;
        }

        var wPadding:Number =
            mx_internal::layoutObject.widthPadding(numChildren);
        var hPadding:Number = mx_internal::layoutObject.heightPadding(0);
        var rowInfo:GridRowInfo = rowHeights[rowIndex];

        measuredMinWidth = minWidth + wPadding;
        measuredMinHeight = rowInfo.min + hPadding;
        measuredWidth = preferredWidth + wPadding;
        measuredHeight = rowInfo.preferred + hPadding;
    }

    /**
     *  @private
     */
    internal function doRowLayout(unscaledWidth:Number,
                                  unscaledHeight:Number):void
    {       
        // Call layoutChrome() to size and position the border.
        layoutChrome(unscaledWidth, unscaledHeight);

        var n:Number = numChildren;
        if (n == 0)
            return;

        // Block requeue-ing. No layout pass should
        // result in the invalidation of your size and layout.
        var oldSizeFlag:Boolean = invalidateSizeFlag;
        var oldDisplayListFlag:Boolean = invalidateDisplayListFlag;
        invalidateSizeFlag = true;
        invalidateDisplayListFlag = true;

        // Run the standard Flex layout algorithm to position the columns.
        // We normally only run the Flex layout algorithm once, but we'll
        // run it again if the min/max/prefWidth changes (which can happen
        // if those values are set explicitly for this row).
        if (parent.getChildIndex(this) == 0 ||
            isNaN(columnWidths[0].x) ||
            columnWidths.minWidth != minWidth ||
            columnWidths.maxWidth != maxWidth ||
            columnWidths.preferredWidth != getExplicitOrMeasuredWidth() ||
            columnWidths.percentWidth != percentWidth ||
            columnWidths.width != unscaledWidth ||
            columnWidths.paddingLeft != getStyle("paddingLeft") ||
            columnWidths.paddingRight != getStyle("paddingRight") ||
            columnWidths.horizontalAlign != getStyle("horizontalAlign") ||
            columnWidths.borderStyle != getStyle("borderStyle"))
        {
            calculateColumnWidths();
            columnWidths.minWidth = minWidth;
            columnWidths.maxWidth = maxWidth;
            columnWidths.preferredWidth = getExplicitOrMeasuredWidth();
            columnWidths.percentWidth = percentWidth;
            columnWidths.width = unscaledWidth;
            columnWidths.paddingLeft = getStyle("paddingLeft");
            columnWidths.paddingRight = getStyle("paddingRight");
            columnWidths.horizontalAlign = getStyle("horizontalAlign");
            columnWidths.borderStyle = getStyle("borderStyle");
        }

        var vm:EdgeMetrics = viewMetricsAndPadding;

        // Now position the actual child GridItem objects, based on the
        // values that were stored in the rowHeights and columnWidths arrays.
        for (var i:int = 0; i < n; i++)
        {
            var child:GridItem = GridItem(getChildAt(i));
            var colIndex:int = child.colIndex;
            var x:Number = columnWidths[colIndex].x;
            var y:Number = vm.top;
            var percentHeight:Number = child.percentHeight;

            var finalColIndex:int =
                Math.min(colIndex + child.colSpan, columnWidths.length);
            var finalCol:GridColumnInfo = columnWidths[finalColIndex - 1];
            var w:Number = finalCol.x + finalCol.width - x;

            var finalRowIndex:int =
                Math.min(rowIndex + child.rowSpan, rowHeights.length);
            var finalRow:GridRowInfo = rowHeights[finalRowIndex - 1];
            var h:Number = finalRow.y + finalRow.height -
                           rowHeights[rowIndex].y - vm.top - vm.bottom;

            // If the grid cell is larger than the maxWidth/maxHeight
            // of this GridItem, then position the GridItem
            // according to horizontalAlign & vertical
            // Also, if the GridItem has a percentHeight that's less than 100%,
            // then shrink the height of the GridItem so that it consumes only
            // a fraction of the space allocated to the row.
            var diff:Number = w - child.maxWidth;
            if (diff > 0)
            {
                x += diff * mx_internal::layoutObject.getHorizontalAlignValue();
                w -= diff;
            }
            diff = h - child.maxHeight;
            if (percentHeight && percentHeight < 100)
                diff = Math.max(diff, h * (100 - percentHeight));
            if (diff > 0)
            {
                y = diff * mx_internal::layoutObject.getVerticalAlignValue();
                h -= diff;
            }
            
            w = Math.ceil(w);
            h = Math.ceil(h);
            
            child.move(x, y);
            child.setActualSize(w, h);
        }

        invalidateSizeFlag = oldSizeFlag;
        invalidateDisplayListFlag = oldDisplayListFlag;
    }

    /**
     *  @private
     */
    private function calculateColumnWidths():void
    {
        var vm:EdgeMetrics = viewMetricsAndPadding;
        var gap:Number = getStyle("horizontalGap");
        var numChildren:int = columnWidths.length;
        var spaceForChildren:Number =
            unscaledWidth - vm.left -
            vm.right - (numChildren - 1) * gap;
        var spaceToDistribute:Number;
        var columnInfo:GridColumnInfo;
        var left:Number;
        var i:int;

        var totalPercentWidth:Number = 0;
        var flexibleColumnWidths:Array = [];
        spaceToDistribute = spaceForChildren;

        // If the child is flexible, store information about it in the
        // flexibleColumnWidths.  For non-flexible children, just set the
        // columnWidths[i].width property immediately.
        //
        // Also calculate the sum of all percentWidths, and calculate the
        // sum of the width of all non-flexible children.
        for (i = 0; i < numChildren; i++)
        {
            columnInfo = columnWidths[i];
            var percentWidth:Number = columnInfo.percent;

            if (percentWidth)
            {
                totalPercentWidth += percentWidth;
                flexibleColumnWidths.push(columnInfo);
            }
            else
            {
                var width:Number = columnInfo.width = columnInfo.preferred;
                spaceToDistribute -= width;
            }
        }

        // Distribute the extra space among the children.
        if (totalPercentWidth)
        {
            spaceToDistribute = Flex.flexChildrenProportionally(
                spaceForChildren, spaceToDistribute,
                totalPercentWidth, flexibleColumnWidths);

            // Copy the calculated width
            // from columnInfo.space to columnInfo.width.
            var numFlexChildren:int = flexibleColumnWidths.length;
            for (i = 0; i < numFlexChildren; i++)
            {
                columnInfo = flexibleColumnWidths[i];
                columnInfo.width = columnInfo.size;
            }
        }

        // Set each child's x-coordinate.
        left = vm.left + spaceToDistribute * mx_internal::layoutObject.getHorizontalAlignValue();
        for (i = 0; i < numChildren; i++)
        {
            columnInfo = columnWidths[i];
            columnInfo.x = left;
            left += columnInfo.width + gap;
        }
    }
}

}
