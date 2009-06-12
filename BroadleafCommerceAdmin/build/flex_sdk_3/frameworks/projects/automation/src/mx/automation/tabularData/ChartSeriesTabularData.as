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

import mx.automation.AutomationManager;
import mx.automation.IAutomationTabularData;
import mx.charts.chartClasses.Series;
import mx.charts.series.items.AreaSeriesItem;
import mx.charts.series.items.BarSeriesItem;
import mx.charts.series.items.BubbleSeriesItem;
import mx.charts.series.items.ColumnSeriesItem;
import mx.charts.series.items.HLOCSeriesItem;
import mx.charts.series.items.LineSeriesItem;
import mx.charts.series.items.PieSeriesItem;
import mx.charts.series.items.PlotSeriesItem;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  @private
 */
public class ChartSeriesTabularData
    implements IAutomationTabularData
{

    private var series:Object;

    /**
     *  @private
     */
    public function ChartSeriesTabularData(series:Object)
    {
		super();

        this.series = series ;
    }

    /**
     *  @inheritDoc
     */
    public function get firstVisibleRow():int
    {
    	return 0;
    }
    
    /**
     *  @inheritDoc
     */
    public function get lastVisibleRow():int
    {
        return series.items.length-1;
    }

    /**
     *  @inheritDoc
     */
    public function get numRows():int
    {
        return series.items.length;
    }


    /**
     *  @inheritDoc
     */
    public function get numColumns():int
    {
        return 1;
    }

    /**
     *  @inheritDoc
     */
    public function get columnNames():Array
    {
        return ["values"];
    }

    /**
     *  @inheritDoc
     */
    public function getValues(start:uint = 0, end:uint = 0):Array
    {
    	var _values:Array = [];
        if (end == 0)
    		end = series.items.length;
		var i:int;
		var items:Array = series.items;
    	for(i = start; i <= end; ++i)
    	{
			var values:Array = getAutomationValueForData(items[i]);
			_values.push([ values.join("|") ]);
    	}
    	
        return _values;
    }
    
    /**
     *  @inheritDoc
     */
    public function getAutomationValueForData(data:Object):Array
    {
		if(data is AreaSeriesItem)
		{
			return [data.xNumber, data.yNumber];
		}
		if(data is BarSeriesItem)
		{
			return [data.xNumber, data.yNumber];
		}
		if(data is BubbleSeriesItem)
		{
			return [data.xNumber, data.yNumber, data.zNumber];
		}
		if(data is ColumnSeriesItem)
		{
			return [data.xNumber,data.yNumber];
		}
		if(data is HLOCSeriesItem)
		{
			return [data.openNumber, data.closeNumber, data.highNumber, data.lowNumber];
		}
		if(data is LineSeriesItem)
		{
			return [data.xNumber,data.yNumber];
		}
		if(data is PieSeriesItem)
		{
			return [data.number];
		}
		if(data is PlotSeriesItem)
		{
			return [data.xNumber, data.yNumber];
		}

		return [];
    }
}
}
