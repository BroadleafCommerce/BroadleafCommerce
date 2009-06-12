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

package mx.controls.dataGridClasses
{

import flash.display.DisplayObject;
import flash.geom.Point;
import mx.controls.DataGrid;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The DataGridDragProxy class defines the default drag proxy 
 *  used when dragging data from a DataGrid control.
 */
public class DataGridDragProxy extends UIComponent
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
    public function DataGridDragProxy()
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
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();
        
        var items:Array /* of unit */ = DataGridBase(owner).selectedItems;

        var n:int = items.length;
        for (var i:int = 0; i < n; i++)
        {
            var src:IListItemRenderer = DataGridBase(owner).itemToItemRenderer(items[i]);
            if (!src)
                continue;

            var o:UIComponent;
            
            var data:Object = items[i];
            
            o = new UIComponent();
            addChild(DisplayObject(o));
            
            var ww:Number = 0;
            var m:int;
            var j:int;
            var col:DataGridColumn;
            var c:IListItemRenderer;
            var rowData:DataGridListData;

            if (DataGridBase(owner).visibleLockedColumns)
            {
                m = DataGridBase(owner).visibleLockedColumns.length;
                for (j = 0; j < m; j++)
                {
                    col = DataGridBase(owner).visibleLockedColumns[j];
                    
                    c = DataGridBase(owner).createColumnItemRenderer(col, false, data);
                    
                    rowData = new DataGridListData(
                        col.itemToLabel(data), col.dataField,
                        col.colNum, "", DataGridBase(owner));
                    
                    if (c is IDropInListItemRenderer)
                    {
                        IDropInListItemRenderer(c).listData =
                            data ? rowData : null;
                    }
                    
                    c.data = data;
                    c.styleName = DataGridBase(owner);
                    c.visible = true;
                    
                    o.addChild(DisplayObject(c));
                    
                    c.setActualSize(col.width, src.height);
                    c.move(ww, 0);
                    
                    ww += col.width;
                }
            }
            m = DataGridBase(owner).visibleColumns.length;
            for (j = 0; j < m; j++)
            {
                col = DataGridBase(owner).visibleColumns[j];
                
                c = DataGridBase(owner).createColumnItemRenderer(col, false, data);
                
                rowData = new DataGridListData(
                    col.itemToLabel(data), col.dataField,
                    col.colNum, "", DataGridBase(owner));
                
                if (c is IDropInListItemRenderer)
                {
                    IDropInListItemRenderer(c).listData =
                        data ? rowData : null;
                }
                
                c.data = data;
                c.styleName = DataGridBase(owner);
                c.visible = true;
                
                o.addChild(DisplayObject(c));
                
                c.setActualSize(col.width, src.height);
                c.move(ww, 0);
                
                ww += col.width;
            }


            o.setActualSize(ww, src.height);
            var pt:Point = new Point(0, 0);
            pt = DisplayObject(src).localToGlobal(pt);
            pt = DataGridBase(owner).globalToLocal(pt);
            o.y = pt.y;

            measuredHeight = o.y + o.height;
            measuredWidth = ww;
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
        var child:UIComponent;
        
        for (var i:int = 0; i < numChildren; i++)
        {
            child = getChildAt(i) as UIComponent;
            
            if (child)
            {
                w = Math.max(w, child.x + child.width);
                h = Math.max(h, child.y + child.height);
            }
        }
        
        measuredWidth = measuredMinWidth = w;
        measuredHeight = measuredMinHeight = h; 
    }
}

}
