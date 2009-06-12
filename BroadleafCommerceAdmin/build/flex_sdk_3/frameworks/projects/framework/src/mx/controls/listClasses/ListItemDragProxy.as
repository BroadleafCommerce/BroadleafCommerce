////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.listClasses
{

import flash.display.DisplayObject;
import mx.core.mx_internal;
import mx.core.UIComponent;

use namespace mx_internal;

/**
 *  The default drag proxy used when dragging from a list-based control
 *  (except for the DataGrid class).
 *  A drag proxy is a component that parents the objects
 *  or copies of the objects being dragged
 *
 *  @see mx.controls.dataGridClasses.DataGridDragProxy
 */
public class ListItemDragProxy extends UIComponent
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
    public function ListItemDragProxy()
    {
        super();
        
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        var items:Array /* of unit */ = ListBase(owner).selectedItems;

        var n:int = items.length;
        for (var i:int = 0; i < n; i++)
        {
            var src:IListItemRenderer = ListBase(owner).itemToItemRenderer(items[i]);
            if (!src)
                continue;

            var o:IListItemRenderer = ListBase(owner).createItemRenderer(items[i]);
    
            o.styleName = ListBase(owner);
            
            if (o is IDropInListItemRenderer)
            {
                var listData:BaseListData =
                    IDropInListItemRenderer(src).listData;
                
                IDropInListItemRenderer(o).listData = items[i] ?
                                                      listData :
                                                      null;
            }

            o.data = items[i];
            
            addChild(DisplayObject(o));

            var contentHolder:ListBaseContentHolder = src.parent as ListBaseContentHolder;
            
            o.setActualSize(src.width, src.height);
            o.x = src.x + contentHolder.leftOffset;
            o.y = src.y + contentHolder.topOffset;

            measuredHeight = Math.max(measuredHeight, o.y + o.height);
            measuredWidth = Math.max(measuredWidth, o.x + o.width);
            o.visible = true;
        }

        invalidateDisplayList();
    }
    
    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();
        
        var w:Number = 0;
        var h:Number = 0;
        var child:IListItemRenderer;
        
        for (var i:int = 0; i < numChildren; i++)
        {
            child = getChildAt(i) as IListItemRenderer;
            
            if (child)
            {
                /*trace("ListItemDragProxy.measure x",child.x,"y",child.y,"h",child.getExplicitOrMeasuredHeight(),
                        "w",child.getExplicitOrMeasuredWidth(),"child",child);
                */
                w = Math.max(w, child.x + child.width);
                h = Math.max(h, child.y + child.height);
            }
        }
        
        measuredWidth = measuredMinWidth = w;
        measuredHeight = measuredMinHeight = h;
    }
}

}
