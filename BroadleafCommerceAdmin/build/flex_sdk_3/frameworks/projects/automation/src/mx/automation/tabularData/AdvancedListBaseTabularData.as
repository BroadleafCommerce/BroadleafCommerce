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
import mx.collections.CursorBookmark;
import mx.collections.errors.ItemPendingError;
import mx.controls.listClasses.AdvancedListBase;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.List;
import mx.core.mx_internal;
use namespace mx_internal;

/**
 *  @private
 */
public class AdvancedListBaseTabularData
    implements IAutomationTabularData
{

    private var list:AdvancedListBase;

    /**
     *  Constructor
     */
    public function AdvancedListBaseTabularData(l:AdvancedListBase)
    {
		super();

        list = l;
    }

    /**
     *  @inheritDoc
     */
    public function get firstVisibleRow():int
    {
		var listItems:Array = list.rendererArray;

		if (listItems && listItems.length && listItems[0].length)
            return list.itemRendererToIndex(listItems[0][0])
    	return 0;
    }
    
    /**
     *  @inheritDoc
     */
    public function get lastVisibleRow():int
    {
		var listItems:Array = list.rendererArray;

		if (listItems && listItems.length && listItems[0].length)
        {
	        var row:int = listItems.length - 1;
	        var col:int = listItems[0].length - 1;
	        while (!listItems[row][col] && row >= 0 && col >= 0)
	        {
	            if (col != 0)
	                col --;
	            else if (row != 0)
	            {
	                row--;
	                col = listItems[0].length - 1;
	            }
	        }
        
	        return (row >= 0 && col >= 0 
    	            ? list.itemRendererToIndex(listItems[row][col]) 
        	        : numRows);
        }
        
        return 0;
    }

    /**
     *  @inheritDoc
     */
    public function get numRows():int
    {
        return (list.collectionIterator ? list.collectionIterator.view.length : 0);
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
        return [ "" ];
    }

    /**
     *  @inheritDoc
     */
    public function getValues(start:uint = 0, end:uint = 0):Array
    {
        //empty list of column names for list, dg overrides
        var values:Array = [ ];
		if (list.collectionIterator)		
		{
            var bookmark:CursorBookmark = list.collectionIterator.bookmark;
            var i:int = start;
    		var more:Boolean = true;
            list.collectionIterator.seek(CursorBookmark.FIRST, start);
    		
		    while (more && !list.collectionIterator.afterLast && 
		           (i <= end))
		    {
                var data:Object = list.collectionIterator.current;
                var curRowValues:Array = getAutomationValueForData(data);
                
                values.push(curRowValues);

				try 
				{
					more = list.collectionIterator.moveNext();
				}
				catch (e:ItemPendingError)
				{
					more = false;
				}

			    ++i;
		    }

		    // reset to previous iterator position
		    list.collectionIterator.seek(bookmark, 0);
        }

        return values;
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
