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

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]
[Exclude(name="focusEnabled", kind="property")]
[Exclude(name="focusManager", kind="property")]
[Exclude(name="focusPane", kind="property")]
[Exclude(name="mouseFocusEnabled", kind="property")]

[Exclude(name="adjustFocusRect", kind="method")]
[Exclude(name="getFocus", kind="method")]
[Exclude(name="isOurFocus", kind="method")]
[Exclude(name="setFocus", kind="method")]

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]
[Exclude(name="move", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
[Exclude(name="horizontalGap", kind="style")]
[Exclude(name="verticalGap", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]
[Exclude(name="moveEffect", kind="effect")]

/**
 *  The GridItem container defines a grid cell in GridRow container.
 *  (The GridRow container, in turn, defines a row in a Grid container.)
 *  The GridItem container can contain any number of children,
 *  which are laid out as in an HBox container.
 *  If you do not want HBox layout, create a container, such as a VBox
 *  container, as a child of the GridItem control and put other 
 *  components in this child container.
 *
 *  <p>GridItem containers have the following default sizing characteristics:</p>
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
 *           <td>The default or explicit width of the widest item in the column. If a GridItem container doed not have an explicit 
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
 *  <p>The <code>&lt;mx:GridItem&gt;</code> tag must be a child of the 
 *  <code>&lt;GridRow&gt;</code> tag, which defines a grid row.
 *  The <code>&lt;mx:GridItem&gt;</code> container inherits the
 *  tag attributes of its superclass, and adds the following tag attributes.</p>
 *
 *  <pre>
 *  &lt;mx:Grid&gt;
 *    &lt;mx:GridRow&gt;
 *      &lt;mx:GridItem
 *        rowSpan="1"
 *        colSpan="1">
 *          <i>child components</i>
 *      &lt;/mx:GridItem&gt;
 *      ...
 *    &lt;/mx:GridRow&gt;
 *    ...
 *  &lt;/mx:Grid&gt;
 *  </pre>
 *
 *  @see mx.containers.Grid
 *  @see mx.containers.GridRow
 *
 *  @includeExample examples/GridLayoutExample.mxml
 */
public class GridItem extends HBox
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
    public function GridItem()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    internal var colIndex:int = 0;

    //--------------------------------------------------------------------------
    //
    //  Public Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  colSpan
    //----------------------------------

    /**
     *  @private
     *  Storage for the colSpan property.
     */
    private var _colSpan:int = 1;

    [Inspectable(category="General", defaultValue="1")]

    /**
     *  Number of columns of the Grid container spanned by the cell.
     *
     *  @default 1
     */
    public function get colSpan():int
    {
        return _colSpan;
    }

    /**
     *  @private
     */
    public function set colSpan(value:int):void
    {
        _colSpan = value;

        invalidateSize();
    }

    //----------------------------------
    //  rowSpan
    //----------------------------------

    /**
     *  @private
     *  Storage for the rowSpan property.
     */
    private var _rowSpan:int = 1;

    [Inspectable(category="General", defaultValue="1")]

    /**
     *  Number of rows of the Grid container spanned by the cell.
     *  You cannot extend a cell past the number of rows in the Grid.
     *
     *  @default 1
     */
    public function get rowSpan():int
    {
        return _rowSpan;
    }

    /**
     *  @private
     */
    public function set rowSpan(value:int):void
    {
        _rowSpan = value;

        invalidateSize();
    }
}

}
