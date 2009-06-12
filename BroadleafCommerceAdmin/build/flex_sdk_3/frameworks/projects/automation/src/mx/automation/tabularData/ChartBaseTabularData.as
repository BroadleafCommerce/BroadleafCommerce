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

import mx.charts.chartClasses.ChartBase;
import mx.charts.chartClasses.Series;
import mx.automation.AutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.core.mx_internal;
use namespace mx_internal;

/**
 *  @private
 */
public class ChartBaseTabularData
    implements IAutomationTabularData
{

    private var chart:ChartBase;
    private var delegate:IAutomationObject;
   	private var maxItems:int = 0;


    /**
     *  Constructor
     */
    public function ChartBaseTabularData(delegate:IAutomationObject)
    {
		super();

        this.delegate = delegate;
        this.chart = delegate as ChartBase;
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
        return chart.series.length-1;
    }

    /**
     *  @inheritDoc
     */
    public function get numRows():int
    {
        return chart.series.length;
    }


    /**
     *  @inheritDoc
     */
    public function get numColumns():int
    {
    	maxItems = 0;
    	for(var i:int = 0; i < delegate.numAutomationChildren; ++i)
    	{
    		var child:IAutomationObject = delegate.getAutomationChildAt(i);
			if(child is Series)
	    	{
    			var series:Object = child;
    			var items:Array = series.items;
    			if(maxItems < items.length)
    				maxItems = items.length
    		}	
    	    		
    	}
        return maxItems;
    }

    /**
     *  @inheritDoc
     */
    public function get columnNames():Array
    {
    	var names:Array = [];
    	maxItems = numColumns;
    	for(var i:int = 0; i < maxItems; ++i)
			names.push(i);		
        return names;
    }

    /**
     *  @inheritDoc
     */
    public function getValues(start:uint = 0, end:uint = 0):Array
    {
    	var _values:Array = [];
    	var longestRow:int = 0;
		var i:int;
		var j:int;
    	for(i = start; i <= end; ++i)
    	{
    		var child:IAutomationObject = delegate.getAutomationChildAt(i);
    		var childValues:Array = [];
			if(child is Series)
	    	{
    			var series:Object = child;
    			var seriesContainer:IAutomationObject = series as IAutomationObject;
    			var tabularData:IAutomationTabularData = 
    				seriesContainer.automationTabularData as IAutomationTabularData;
    			var items:Array = series.items;
    			for(j = 0; j < items.length; ++j)
				{
					var values:Array = tabularData.getAutomationValueForData(items[j]);
					childValues.push(values.join("|"));
				}	
	    		_values.push(childValues);
    			if(longestRow < items.length)
    				longestRow = items.length;
    		}
    	}
    	
        // normalize the grid so all rows have the same number of columns
        for (i = 0; i < _values.length; i++)
        {
            for (j = _values[i].length; j < longestRow; j++)
            {
                _values[i].push("");
            }
        }
        return _values;
    }
    
    /**
     *  @inheritDoc
     */
    public function getAutomationValueForData(data:Object):Array
    {
		return [];
    }
}
}
