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
import mx.collections.IViewCursor;
import mx.core.FlexShape;
import mx.core.FlexSprite;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../../styles/metadata/PaddingStyles.as"

/**
 *  Background color of the component.
 */
[Style(name="backgroundColor", type="uint", format="Color", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ExcludeClass]

/**
 *  The ListBaseContentHolder class defines a container in a list-based control
 *  of all of the control's item renderers and item editors.
 *  Flex uses it to mask areas of the renderers that extend outside
 *  of the control, and to block certain styles, such as <code>backgroundColor</code>, 
 *  from propagating to the renderers so that highlights and 
 *  alternating row colors can show through the control.
 *
 *  @see mx.controls.listClasses.ListBase
 */
public class ListBaseContentHolder extends UIComponent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param parentList The list-based control.
     */
    public function ListBaseContentHolder(parentList:ListBase)
    {
        super();

        this.parentList = parentList;

        setStyle("backgroundColor", "");
        setStyle("borderStyle", "none");

        // This invisible layer, which is a child of listContent
        // catches mouse events for all items
        // and is where we put selection highlighting by default.
        if (!selectionLayer)
        {
            selectionLayer = new FlexSprite();
            selectionLayer.name = "selectionLayer";
            selectionLayer.mouseEnabled = false;
            addChild(selectionLayer);

            // trace("selectionLayer parent set to " + selectionLayer.parent);

            var g:Graphics = selectionLayer.graphics;
            g.beginFill(0, 0); // 0 alpha means transparent
            g.drawRect(0, 0, 10, 10);
            g.endFill();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The layer in the content defined by the <code>:istbase.listContent</code> property 
     *  where all selection and highlight indicators are drawn.
     */
    public var selectionLayer:Sprite;

    /**
     *  A hash table of data provider item renderers currently in view.
     *  The table is indexed by the data provider item's UID and is used
     *  to quickly get the renderer used to display a particular item.
     */
    public var visibleData:Object = {};

    /**
     *  An Array of Arrays that contains
     *  the item renderer instances that render each data provider item.
     *  This is a two-dimensional, row-major array, which means 
     *  an Array of rows that are Arrays of columns.
     */
    public var listItems:Array = [];

    /**
     *  An Array of ListRowInfo objects that cache row heights and 
     *  other tracking information for the rows defined in 
     *  the <code>listItems</code> property.
     */
    public var rowInfo:Array = [];

    /**
     *  The IViewCursor instance used to fetch items from the
     *  data provider and pass the items to the renderers.
     *  At the end of any sequence of code, it must always
     *  be positioned at the top-most visible item being displayed.
     */
    public var iterator:IViewCursor;

    /**
     *  @private
     */
    private var parentList:ListBase;

    /**
     *  @private
     */
    private var maskShape:Shape;

    /**
     *  @private
     */
    mx_internal var allowItemSizeChangeNotification:Boolean = true;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  @private
     */
    override public function set focusPane(value:Sprite):void
    {
        if (value)
        {
            // Something inside us is getting focus so apply a clip mask
            // if we don't already have one.
            if (!maskShape)
            {
                maskShape = new FlexShape();
                maskShape.name = "mask";

                var g:Graphics = maskShape.graphics;
                g.beginFill(0xFFFFFF);
                g.drawRect(-2, -2, parentList.width + 2, parentList.height + 2);
                g.endFill();

                addChild(maskShape);
            }

            maskShape.visible = false;

            value.mask = maskShape;
        }
        else
        {
            if (parentList.focusPane.mask == maskShape)
                parentList.focusPane.mask = null;
        }

        parentList.focusPane = value;
        value.x = x;
        value.y = y;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function invalidateSize():void
    {
        if (allowItemSizeChangeNotification)
            parentList.invalidateList();
    }

    /**
     *  Sets the position and size of the scroll bars and content
     *  and adjusts the mask.
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

        // have to resize selection layer without scaling so refill it
        var g:Graphics = selectionLayer.graphics;
        g.clear();
        if (unscaledWidth > 0 && unscaledHeight > 0)
        {
            g.beginFill(0x808080, 0);
            g.drawRect(0, 0, unscaledWidth, unscaledHeight);
            g.endFill();
        }

        if (maskShape)
        {
            maskShape.width = unscaledWidth;
            maskShape.height = unscaledHeight;
        }
    }

    /**
     *  @private
     */
    mx_internal function getParentList():ListBase
    {
        return parentList;
    }

    /**
     *  Offset, in pixels, for the upper-left corner in the list control of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public var leftOffset:Number = 0;

    /**
     *  Offset, in pixels, for the upper-left corner in the list control of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public var topOffset:Number = 0;

    /**
     *  Offset, in pixels, for the lower-right corner in the list control of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public var rightOffset:Number = 0;

    /**
     *  Offset, in pixels, for the lower-right corner in the list control of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public var bottomOffset:Number = 0;
    
    /**
     *  Height, in pixels excluding the top and
     *  bottom offsets, of the central part of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public function get heightExcludingOffsets():Number
    {
        return height + topOffset - bottomOffset;
    }
    
    /**
     *  Width, in pixels excluding the top and
     *  bottom offsets, of the central part of the content defined 
     *  by the <code>ListBase.listContent</code> property.
     *
     *  @see mx.controls.listClasses.ListBase#listContent
     */
    public function get widthExcludingOffsets():Number
    {
        return width + leftOffset - rightOffset;
    }
}

}
