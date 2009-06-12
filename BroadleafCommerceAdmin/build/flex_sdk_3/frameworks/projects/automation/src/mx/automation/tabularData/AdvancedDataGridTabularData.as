////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.automation.tabularData
{

import mx.automation.Automation;
import mx.automation.tabularData.AdvancedListBaseTabularData;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.controls.AdvancedDataGrid;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 * @private
 */
public class AdvancedDataGridTabularData extends AdvancedListBaseTabularData
{
    /**
     *  Constructor
     */
    public function AdvancedDataGridTabularData(dg:AdvancedDataGrid)
    {
 
 		super(dg);
        this.dg = dg;
    }

    /**
     *  @private
     */
    private var dg:AdvancedDataGrid;

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
		var listItems:Array = dg.rendererArray;

        if (!dg.headerVisible)
            return super.firstVisibleRow;
        else
            return (listItems[0][0] 
                    ? dg.itemRendererToIndex(listItems[0][0])
                    : 0);
    }

    /**
     *  @private
     */
    override public function getAutomationValueForData(data:Object):Array
    {
        var ret:Array = [];
        var colCount:int = dg.columnCount;

      //   var listItems:Array = dg.rendererArray;
        for (var colNo:int = 0; colNo < colCount; ++colNo)
        {
            //since visibleData data is only keyed per row
            //and doesn't include renderers for each column
            //we can't optimize by using it
            //var item:IListItemRenderer = visibleData[itemToUID(data)];
            var item:IListItemRenderer;

            //if (item == null)
            //{
                var c:AdvancedDataGridColumn = dg.columns[colNo];
             //   item = dg.listItems[colNo];
                item = dg.getMeasuringRenderer(c, false,c.dataField);
                dg.setupRendererFromData(c, item, data);
            //}

            ret.push(IAutomationObject(item).automationValue.join(" | "));
        }

        return ret;
    }
    
    
      /**
     *  @inheritDoc
     */
    public override function get numRows():int
    {
    	if (dg.dataProvider)
			return dg.dataProvider.length;
		
        return super.numRows;
    }
 
    
   
}
}
