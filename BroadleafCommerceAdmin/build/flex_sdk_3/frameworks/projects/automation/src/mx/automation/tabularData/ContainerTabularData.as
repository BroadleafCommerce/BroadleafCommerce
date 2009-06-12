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

import flash.display.DisplayObject;

import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.core.mx_internal;
import mx.core.Repeater;
use namespace mx_internal;

/**
 * @private
 */
public class ContainerTabularData
    implements IAutomationTabularData
{
    /**
     *  Constructor
     */
    public function ContainerTabularData(container:IAutomationObject)
    {
		super();

        this.containerDelegate = container;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var containerDelegate:IAutomationObject;

    /**
     *  @private
     */
    private var _values:Array;

    /**
     *  @private
     */
    private var oldStart:uint;

    /**
     *  @private
     */
    private var oldEnd:int;


    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function get firstVisibleRow():int
    {
        return 0;
    }
    
    /**
     *  @private
     */
    public function get lastVisibleRow():int
    {
        return Math.max(numRows - 1, 0);
    }

    /**
     *  @private
     */
    public function get numRows():int
    {
    	var visibleChildren:int = 0; 
        for (var i:int = 0; i < containerDelegate.numAutomationChildren; ++i)
        {
            var ao:IAutomationObject = containerDelegate.getAutomationChildAt(i);
            var disp:DisplayObject = ao as DisplayObject;
            if (disp.visible && !(disp is Repeater))
                ++visibleChildren;
        }
		return visibleChildren;
    }

    /**
     *  @private
     */
    public function get numColumns():int
    {
        var a:Array = _values || getValues(0, numRows);
        return a && a.length > 0 ? a[0].length : 0;
    }

    /**
     *  @private
     */
    public function get columnNames():Array
    {
        var result:Array = new Array(numColumns);
        for (var i:int = 0; i < result.length; i++)
        {
            result[i] = "";
        }
        return result;
    }

    /**
     *  @private
     */
    public function getValues(start:uint = 0, end:uint = 0):Array
    {
        if (_values && oldStart == start && oldEnd == end)
            return _values;
            
        var longestRow:int = 1;
        _values = [ ];
        var k:int = 0; 
        for (var i:int = 0; 
             	i < containerDelegate.numAutomationChildren && k <= end; ++i)
        {
            var ao:IAutomationObject = containerDelegate.getAutomationChildAt(i);
            var disp:DisplayObject = ao  as DisplayObject;
            if (disp.visible && !(disp is Repeater))
            {
	            if(k >=start && k <= end)
	            {
		            var av:Array = flattenArray(ao.automationValue);
    		        _values.push(av);
        	     	longestRow = Math.max(longestRow, av.length);
        	    }
             	++k;
            }
        }

        // normalize the grid so all rows have the same number of columns
        for (i = 0; i < _values.length; i++)
        {
            for (var j:int = _values[i].length; j < longestRow; j++)
            {
                _values[i].push("");
            }
        }
        oldStart = start;
        oldEnd = end;
        return _values;
    }

    /**
     *  @private
     */
    private static function flattenArray(a:Array):Array
    {
    	if(!a)
    		return [];
        for (var i:int = 0; i < a.length; ++i)
        {
            if (a[i] is Array)
            {
                var tmp:Array = [];
                
                if (i > 0)
                    tmp = a.slice(0, i);
                    
                tmp = tmp.concat(a[i]);
                    
                if (i < a.length - 1)
                    tmp = tmp.concat(a.slice(i + 1));
            
                a = tmp;
                i = -1;
            }
        }
        return a;
    }

    /**
     *  @private
     */
    public function getAutomationValueForData(data:Object):Array
    {
    	return [];
    }
}
}
