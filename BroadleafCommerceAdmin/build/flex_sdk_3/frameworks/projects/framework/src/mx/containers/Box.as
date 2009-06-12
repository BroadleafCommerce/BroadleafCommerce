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

import flash.events.Event;
import mx.containers.utilityClasses.BoxLayout;
import mx.core.Container;
import mx.core.IUIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/AlignStyles.as";
include "../styles/metadata/GapStyles.as";

/**
 *  Number of pixels between the container's bottom border
 *  and the bottom of its content area.
 *  The default value is 0.
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the container's top border
 *  and the top of its content area.
 *  The default value is 0.
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("Box.png")]

/**
 *  A Box container lays out its children in a single vertical column
 *  or a single horizontal row.
 *  The <code>direction</code> property determines whether to use
 *  vertical (default) or horizontal layout.
 *
 *  <p>The Box class is the base class for the VBox and HBox classes.
 *  You use the <code>&lt;mx:Box&gt;</code>, <code>&lt;mx:VBox&gt;</code>,
 *  and <code>&lt;mx:HBox&gt;</code> tags to define Box containers.</p>
 *
 *  <p>A Box container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td><strong>Vertical Box</strong> The height is large enough to hold all its children at the default 
 *               or explicit height of the children, plus any vertical gap between the children, plus the top and 
 *               bottom padding of the container. The width is the default or explicit width of the widest child, 
 *               plus the left and right padding of the container. 
 *               <br><strong>Horizontal Box</strong> The width is large enough to hold all of its children at the 
 *               default width of the children, plus any horizontal gap between the children, plus the left and 
 *               right padding of the container. The height is the default or explicit height of the tallest child, 
 *               plus the top and bottom padding for the container.</br>
 *           </td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Box&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass, and adds the following tag attributes:</p>
 *
 *  <p>
 *  <pre>
 *  &lt;mx:Box
 *    <strong>Properties</strong>
 *    direction="vertical|horizontal"
 *    <strong>Styles</strong>
 *    horizontalAlign="left|center|right"
 *    horizontalGap="8"
 *    paddingBottom="0"
 *    paddingTop="0"
 *    verticalAlign="top|middle|bottom"
 *    verticalGap="6"
 *    &gt;
 *    ...
 *      <i>child tags</i>
 *    ...
 *  &lt;/mx:Box&gt;
 *  </pre>
 *  </p>
 *
 *  @includeExample examples/SimpleBoxExample.mxml
 *
 *  @see mx.containers.HBox
 *  @see mx.containers.VBox
 */
public class Box extends Container
{
    include "../core/Version.as"

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Box()
    {
        super();

        layoutObject.target = this;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var layoutObject:BoxLayout = new BoxLayout();

    //--------------------------------------------------------------------------
    //
    //  Public properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  direction
    //----------------------------------

    [Bindable("directionChanged")]
    [Inspectable(category="General", enumeration="vertical,horizontal", defaultValue="vertical")]

    /**
     *  The direction in which this Box container lays out its children.
     *  Possible MXML values are
     *  <code>"horizontal"</code> and <code>"vertical"</code>.
     *  Possible values in ActionScript are <code>BoxDirection.HORIZONTAL</code>
     *  and <code>BoxDirection.VERTICAL</code>.
     *
     *  @default BoxDirection.VERTICAL
     */
    public function get direction():String
    {
        return layoutObject.direction;
    }

    /**
     *  @private
     */
    public function set direction(value:String):void
    {
        layoutObject.direction = value;

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("directionChanged"));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Calculates the default sizes and minimum and maximum values of the Box
     *  container.
     *
     *  <p>If the Box container's <code>direction</code> property is set to 
     *  <code>BoxDirection.HORIZONTAL</code>, its <code>measuredWidth</code>
     *  property is equal to the sum of default widths of all of its children,
     *  plus the thickness of the borders, plus the left and right padding,
     *  plus the horizontal gap between each child.
     *  The value of the <code>measuredHeight</code> property is the maximum of
     *  all the children's default heights, plus room for the borders and
     *  padding.
     *  If the Box container's <code>direction</code> property is set to 
     *  <code>BoxDirection.VERTICAL</code>, these two values are reversed.</p>
     *
     *  <p>The Box container's <code>minWidth</code> and <code>minHeight</code>
     *  properties are calculated similarly, by combining the minimum widths
     *  and minimum heights of the children.
     *  If the child's <code>width</code> property is a percentage value,  the
     *  Box container's minimum width is equal to the value of the child's
     *  <code>minWidth</code> property.
     *  If the child's <code>width</code> is unset or  set to a fixed value,
     *  the child refuses to grow or shrink, so the Box container's minimum
     *  width is equal to the value of the child's <code>explicitWidth</code>
     *  property.
     *  The child's minimum height is calculated similarly.</p>
     *
     *  <p>The Box container's <code>maxWidth</code> and
     *  <code>maxHeight</code> properties are not calculated.
     *  The Box container is assumed to have an infinite maximum width and
     *  height.</p>
     *
     *  <p>All of the values described previously are the <i>measured</i>
     *  widths and heights of the Box container.
     *  The user can override the measured values by explicitly supplying
     *  a value for the following properties:</p>
     *
     *  <ul>
     *    <li><code>width</code></li>
     *    <li><code>height</code></li>
     *    <li><code>minWidth</code></li>
     *    <li><code>minHeight</code></li>
     *    <li><code>maxWidth</code></li>
     *    <li><code>maxHeight</code></li>
     *  </ul>
     *
     *  <p>You should not call the <code>measure()</code> method directly.
     *  The Flex LayoutManager calls it at the appropriate time.
     *  At application startup, the Flex LayoutManager attempts to measure
     *  all components from the children to the parents before setting them
     *  to their final sizes.</p>
     *
     *  <p>This is an advanced method for use in subclassing.
     *  If you override this method, your implementation must call
     *  the <code>super.measure()</code> method, or set the
     *  <code>measuredHeight</code> and
     *  <code>measuredWidth</code> properties.
     *  You may also optionally set the following properties:</p>
     *
     *  <ul>
     *    <li><code>measuredMinWidth</code></li>
     *    <li><code>measuredMinHeight</code></li>
     *  </ul>
     *
     *  <p>These properties correspond to the layout properties listed
     *  previously and, therefore, are not separately documented.</p>
     */
    override protected function measure():void
    {
        super.measure();

        layoutObject.measure();
    }

    /**
     *  Sets the size and position of each child of the Box container.
     *
     *  <p>To understand the layout algorithm for the Box container, assume 
     *  that the Box container's direction is horizontal.</p>
     *
     *  <p>All of the Box container's children are positioned side-by-side in a
     *  single horizontal row, with <code>horizontalGap</code> pixels between
     *  each pair of adjacent children.
     *  Initially, the widths of children without an explicit
     *  width value are set equal to the values of their
     *  <code>measuredWidth</code> properties.</p>
     *
     *  <p>If the sum of the values of the <code>measuredWidth</code>
     *  properties of the children is greater than or less than the width of
     *  the Box container, and if some of the children have a percentage value
     *  for the <code>width</code> property, the sizes of those children are
     *  grown or shrunk until all children exactly fit in the Box.
     *  However, no child is shrunk to less than the value of its
     *  <code>minWidth</code> property or increased greater than the value of
     *  its <code>maxWidth</code> property.
     *  Among the growing (or shrinking) children, extra space is added
     *  (or removed) in proportion to the child's <code>percentWidth</code>.
     *  For example, a child with a <code>percentWidth</code> of 40 percent
     *  will grow twice as much as a child with a <code>percentWidth</code> of
     *  20 percent until all of the available space is filled or the prescribed
     *  sizes are reached.</p>
     *
     *  <p>After all flexible children have grown or shrunk, Flex checks to see
     *  if the sum of the children's widths match the width of the Box
     *  container.
     *  If not, the group of children are all shifted according to the value
     *  of the Box container's <code>horizontalAlign</code> property, so the
     *  children are aligned with the left edge of the Box, aligned with the
     *  right edge of the Box, or centered in the middle of the Box.</p>
     *
     *  <p>To determine the height of each child, Flex examines the value
     *  of the child's <code>height</code> property.
     *  If the <code>height</code> is unset, the child's height
     *  is set to its <code>measuredHeight</code>.
     *  If the <code>height</code> is set to a pixel value, that value is used.
     *  If the <code>height</code> is set to a percentage value,
     *  the child's height is grown or shrunk to match the specified
     *  percentage of the height of the Box, as long as the child's height
     *  is not shrunk to less than the value of its <code>minHeight</code>
     *  property or grown to be larger than the value of its
     *  <code>maxHeight</code> property.</p>
     *
     *  <p>The vertical position of each child is determined by
     *  the Box container's <code>verticalAlign</code> property.
     *  Each child is shifted so that it is aligned with the top edge
     *  of the box, aligned with the bottom edge of the box,
     *  or centered in the Box.</p>
     *
     *  <p>If the Box container's <code>direction</code> is
     *  <code>vertical</code>, the same rules apply, except that the widths and
     *  heights are swapped.
     *  The children are arranged in a single vertical column.</p>
     *
     *  <p>You should not call this method directly.
     *  The Flex LayoutManager calls it at the appropriate time.
     *  At application startup, the Flex LayoutManager calls the
     *  <code>updateDisplayList()</code> method on every component,
     *  starting with the Application object and working downward.</p>
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
     *  do not take into account the values of the <code>scaleX</code>
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
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        layoutObject.updateDisplayList(unscaledWidth, unscaledHeight);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Method used to convert number of pixels to a
     *  percentage relative to the contents of this container.
     *
     *  <p>The percentage value is relevant only while the
     *  container does not change size or layout.
     *  After a resize and/or new layout has occurred,
     *  the value returned from this method may be stale.</p>
     *
     *  <p>An example of how this method could be used would
     *  be to restore a component's size to a specific number
     *  of pixels after hiding it.</p>
     *
     *  @param pxl The number of pixels for which a percentage
     *  value is desired.
     *
     *  @return The percentage value that would be equivalent
     *  to <code>pxl</code> under the current layout conditions
     *  of this container.
     *  A negative value indicates that the container must grow
     *  in order to accommodate the requested size.
     */
    public function pixelsToPercent(pxl:Number):Number
    {
        var vertical:Boolean = isVertical();

        // Compute our total percent and total # pixels for that percent.
        var totalPerc:Number = 0;
        var totalSize:Number = 0;

        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IUIComponent = IUIComponent(getChildAt(i));

            var size:Number = vertical ? child.height : child.width;
            var perc:Number = vertical ? child.percentHeight : child.percentWidth;

            if (!isNaN(perc))
            {
                totalPerc += perc;
                totalSize += size;
            }
        }

        // Now if we found one let's compute the percent amount
        // that we'd require for a given number of pixels.
        var p:Number = 100;
        if (totalSize != pxl)
        {
            // Now we want the ratio of pixels per percent to
            // remain constant as we assume the a component
            // will consume them. So,
            //
            //  (totalSize - pxl) / totalPerc = totalSize / (totalPerc + p)
            //
            // where we solve for p.

            p = ((totalSize * totalPerc) / (totalSize - pxl)) - totalPerc;
        }

        return p;
    }

    /**
     *  @private
     */
    mx_internal function isVertical():Boolean
    {
        return direction != BoxDirection.HORIZONTAL;
    }
}

}
