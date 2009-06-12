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

package mx.automation.codec
{

import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.charts.HitData;
import mx.charts.chartClasses.Series;

import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.charts.ChartItem;
import mx.charts.chartClasses.IChartElement;

use namespace mx_internal;

/**
 * translates between internal Flex HitData and automation-friendly version
 */
public class HitDataCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function HitDataCodec()
	{
		super();
	}
   
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, propertyDescriptor);
		var encodedID:int;

        if (val != null)
        {
        	if (val is HitData)
        	{
        		encodedID = (val as HitData).chartItem.index;
        	}
        }
        
        return encodedID;
    }

    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
    	if (relativeParent is Series)
    	{
    		var series:Object = relativeParent;
    		var items:Array = series.items;
    		for(var i:int = 0; i < items.length; ++i)
    		{
    			if (items[i] is ChartItem)
    			{
    				var chartItem:ChartItem = items[i] as ChartItem;
    				if ( chartItem.index == value)
    				{
   						obj[propertyDescriptor.name] = 
   									new HitData(0, 0, 0, 0, chartItem);
    					break;
    				}
    			}
    		}
    	}
}
}

}
