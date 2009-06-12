////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.automation.tabularData
{

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.controls.DataGrid;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.automation.delegates.controls.DataGridAutomationImpl;
import mx.controls.listClasses.ListBaseContentHolder;
import flash.display.DisplayObject;

import mx.core.mx_internal;

use namespace mx_internal;

/**
 * @private
 */
public class DataGridTabularData extends ListBaseTabularData
{
    /**
     *  Constructor
     */
    public function DataGridTabularData(dg:DataGrid)
    {
        super(dg);
        this.dg = dg;
    }

    /**
     *  @private
     */
    private var dg:DataGrid;

    /**
     *  @private
     */
    override public function get numColumns():int
    {
        return dg.columnCount;
    }


    /**
     *  @private
     */
    override public function get columnNames():Array
    {
        //override to provide the column names
        var result:Array = [];
        var colCount:int = dg.columnCount;
        var columns:Array = dg.columns;
        for (var i:int = 0; i < dg.columnCount; ++i)
        {
            result.push(columns[i].dataField);
        }
        return result;
    }

    /**
     *  @private
     */
    
    
    override public function get firstVisibleRow():int
    {
    	
    	if (!dg.headerVisible)
            return super.firstVisibleRow;
            	
    	var listItems:Array;
    	if(dg.lockedRowCount == 0)
    	{
			 listItems= dg.rendererArray;
              return (listItems[1][0] 
                    ? dg.itemRendererToIndex(listItems[1][0])
                    : 0);
   		 }
			 
		else
		{
			listItems = ((dg.automationDelegate) as DataGridAutomationImpl).getCompleteRenderersArray();

        	var firstVisibleRow:int = -1;
        	var rowCount:int = listItems.length;
        	
        	// the locked rows can be in the visible area or invisible area
        	// so if had locked rows we cannot blindly take the fist row element
        	var index:int = 0;
        	while((firstVisibleRow < 0)&&(index < rowCount))
        	{
              firstVisibleRow = (listItems[index][0] 
                    ? dg.itemRendererToIndex(listItems[index][0])
                    : 0);
                    
               index ++; 
        	 }
        	 
        	 if(firstVisibleRow < 0)
        		 firstVisibleRow = 0;
        	 return firstVisibleRow;
        }
        
        
    }


    /**
    *  @private
    */
    override public function getAutomationValueForData(data:Object):Array
    {
        var ret:Array = [];
        var colCount:int = dg.columnCount;
		var rowCount:int = dg.rowCount;
        for (var colNo:int = 0; colNo < colCount; ++colNo)
        {
            //since visibleData data is only keyed per row
            //and doesn't include renderers for each column
            //we can't optimize by using it
            //var item:IListItemRenderer = visibleData[itemToUID(data)];
            var item:IListItemRenderer;

            var c:DataGridColumn = dg.columns[colNo];
            item = c.getMeasuringRenderer(false,data);
            if(item.owner == null) 
        		  (dg.getListContentHolder() as ListBaseContentHolder).addChild(DisplayObject(item));
            dg.setupRendererFromData(c, item, data);

            ret.push(IAutomationObject(item).automationValue.join(" | "));
        }

        return ret;
    }

}
}
