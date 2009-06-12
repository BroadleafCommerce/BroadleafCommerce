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

package mx.automation.delegates.controls 
{
import flash.display.DisplayObject;
import flash.display.InteractiveObject;

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import mx.automation.Automation;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationDragEvent;
import mx.automation.events.ListItemSelectEvent; 
import mx.automation.tabularData.DataGridTabularData;
import mx.controls.DataGrid;
import mx.controls.dataGridClasses.DataGridHeader;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBaseContentHolder;

import mx.core.mx_internal;
import mx.core.IFlexDisplayObject
import mx.events.DataGridEvent;
import mx.events.DragEvent;
import mx.events.IndexChangedEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  DataGrid control.
 * 
 *  @see mx.controls.DataGrid 
 *
 */
public class DataGridAutomationImpl extends ListBaseAutomationImpl 
{
    include "../../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Registers the delegate class for a component class with automation manager.
     *  
     *  @param root The SystemManger of the application.
     */
    public static function init(root:DisplayObject):void
    {
        Automation.registerDelegateClass(DataGrid, DataGridAutomationImpl);
    }   

    /**
     *  Constructor.
     * @param obj DataGrid object to be automated.     
     */
    public function DataGridAutomationImpl(obj:DataGrid)
    {
        super(obj);
        
        
        obj.addEventListener(IndexChangedEvent.HEADER_SHIFT, headerShiftHandler, false, 0, true);
        obj.addEventListener(DataGridEvent.HEADER_RELEASE, headerReleaseHandler, false, 0, true);
        obj.addEventListener(DataGridEvent.COLUMN_STRETCH, columnStretchHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get grid():DataGrid
    {
        return uiComponent as DataGrid;
    }

    /**
     * @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
         var listItems:Array = getCompleteRenderersArray();
    	//var listItems:Array = grid.rendererArray; .. changed as above to take care of the
    	// locked row and locked column changed handling of DG

        var numCols:int = listItems[0].length;
        var row:uint = uint(numCols == 0 ? 0 : index / numCols);
        var col:uint = uint(numCols == 0 ? index : index % numCols);
        var item:IListItemRenderer = listItems[row][col];
        
        if (grid.itemEditorInstance &&
            grid.editedItemPosition &&
            item == grid.editedItemRenderer)
        {
            return grid.itemEditorInstance as IAutomationObject;
        }

        return  item as IAutomationObject;
    }

    /**
     * @private
     */
    override public function getItemAutomationIndex(delegate:IAutomationObject):String
    {
        var item:IListItemRenderer = delegate as IListItemRenderer;
        if (item == grid.itemEditorInstance && grid.editedItemPosition)
            item = grid.editedItemRenderer;
        var row:int = grid.itemRendererToIndex(item);
        return (row < 0
                ? getItemAutomationName(delegate)
                : grid.gridColumnMap[item.name].dataField + ":" + row);
    }

    /**
     *  @private
     */
    override public function getItemAutomationValue(item:IAutomationObject):String
    {
        return getItemAutomationNameOrValueHelper(item, false);
    }

    /**
     *  @private
     */
    override public function getItemAutomationName(item:IAutomationObject):String
    {
        return getItemAutomationNameOrValueHelper(item, true);
    }

    /**
     *  @private
     */
    private function getItemAutomationNameOrValueHelper(delegate:IAutomationObject,
                                                        useName:Boolean):String
    {
        var result:Array = [];
        var item:IListItemRenderer = delegate as IListItemRenderer;

        if (item == grid.itemEditorInstance)
            item = grid.editedItemRenderer;

        var row:int = grid.itemRendererToIndex(item);
        var isHeader:Boolean = false;
    
        if (row == int.MIN_VALUE)
        {
           // return null;  -- this is commented after the header related 
           // changes in DG.
           
           // now for the headers also , it cmes as min_value
           // so we cannot make out header or invalid renderer
            isHeader = grid.headerVisible;
        }
          
            
        row = row < grid.lockedRowCount ?
                   row :
                   row - grid.verticalScrollPosition;            

         if (row >= 0)
        {
            if (grid.headerVisible)
                ++row;
        }
        else if (isHeader)
            row = 0;
        
      
         var listItems:Array = getCompleteRenderersArray();
    	//var listItems:Array = grid.rendererArray; .. changed as above to take care of the
    	// locked row and locked column changed handling of DG
        
        // this varaible is added, since we are proceeding
        // even if the itemRendererToIndex is returning  int.MIN_VALUE
        // we are assuming that the user clicked the header in this case
        // But we need to find whether this is valid
        // this is found by checking whether we get the clicked item
        // in one of the column header renderer
        var validItemRendererFound:Boolean = false;
        
        for (var col:int = 0; col < listItems[row].length; col++)
        {
            var i:IListItemRenderer = listItems[row][col];
            if(i == grid.editedItemRenderer)
                i = grid.itemEditorInstance;
            var itemDelegate:IAutomationObject = i as IAutomationObject;
            var s:String = (useName
                            ? itemDelegate.automationName
                            : itemDelegate.automationValue.join(" | "));
            if ( i == item )
            {
                // we got a valid item renderer
                s= "*" + s + "*";
                validItemRendererFound= true;
            }               
            result.push(s);
        }
        
        if(isHeader && (validItemRendererFound==false))
        {
            // we got the itemRendererToIndex(item) as int.MIN_VALUE
            // so we considered it as a header row
            // but no element on the header row match with the
            // current item renderer. Hence returning null
            return null;
        }
        return (isHeader
                ? "[" + result.join("] | [") + "]"
                : result.join(" | "));
    }

    /**
     *  @private
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        var mouseEvent:MouseEvent;
        switch (interaction.type)
        {
            case "headerShift":
            {
                var icEvent:IndexChangedEvent = IndexChangedEvent(interaction);
                grid.shiftColumns(icEvent.oldIndex, icEvent.newIndex);
                return true;
            }

            case DataGridEvent.HEADER_RELEASE:
            {
                 var listItems:Array = getCompleteRenderersArray();
		    	//var listItems:Array = grid.rendererArray; .. changed as above to take care of the
		    	// locked row and locked column changed handling of DG
		    	
                var c:IListItemRenderer = listItems[0][DataGridEvent(interaction).columnIndex];
                return help.replayClick(c);
            }

            case DataGridEvent.COLUMN_STRETCH:
            {
                var s:IFlexDisplayObject = DataGridHeader((grid .dataGridHeader)). getSeparators()[DataGridEvent(interaction).columnIndex];
           
                s.dispatchEvent(new MouseEvent(MouseEvent.MOUSE_DOWN));
                // localX needs to be passed in the constructor
                // to get stageX value computed.
                mouseEvent = new MouseEvent(MouseEvent.MOUSE_UP, 
                            true, // bubble 
                            false, // cancellable 
                            DataGridEvent(interaction).localX, 
                            20, // dummy value 
                            uiComponent as InteractiveObject );
                return help.replayMouseEvent(uiComponent, mouseEvent);
              
               
            }

            case DataGridEvent.ITEM_EDIT_BEGIN:
            {
                var de:DataGridEvent = new DataGridEvent(DataGridEvent.ITEM_EDIT_BEGINNING);
                var input:DataGridEvent = interaction as DataGridEvent;
                de.itemRenderer = input.itemRenderer;
                de.rowIndex = input.rowIndex;
                de.columnIndex = input.columnIndex;
                uiComponent.dispatchEvent(de);
            }

            case ListItemSelectEvent.DESELECT:
            case ListItemSelectEvent.MULTI_SELECT:
            case ListItemSelectEvent.SELECT:
            default:
            {
                return super.replayAutomatableEvent(interaction);
            }
        }
    }

    /**
     *  A matrix of the automationValues of each item in the grid. The return value
     *  is an array of rows, each of which is an array of item renderers (row-major).
     */
    override public function get automationTabularData():Object
    {
        return  new DataGridTabularData(grid);
    }
    
    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        if (grid.itemEditorInstance || event.target != event.currentTarget)
            return;

        super.keyDownHandler(event);
    }
    
    /**
     *  @private
     */
    private function columnStretchHandler(event:DataGridEvent):void 
    {
       recordAutomatableEvent(event);
    }

    /**
     *  @private
     */
    private function headerReleaseHandler(event:DataGridEvent):void 
    {
       recordAutomatableEvent(event);
    }
    
    /**
     *  @private
     */
    private function headerShiftHandler(event:IndexChangedEvent):void 
    {
        if (event.triggerEvent)
            recordAutomatableEvent(event);
    }
    
    /**
     *  @private
     */
    private function itemEditHandler(event:DataGridEvent):void
    {
        recordAutomatableEvent(event, true);    
    }
    
    /**
     *  @private
     */
    override protected function dragDropHandler(event:DragEvent):void
    {
        if(dragScrollEvent)
        {
            recordAutomatableEvent(dragScrollEvent);
            dragScrollEvent=null;
        }

        var am:IAutomationManager = Automation.automationManager;
        var index:int = grid.calculateDropIndex(event);
        var drag:AutomationDragEvent = new AutomationDragEvent(event.type);
        drag.action = event.action;

        if (grid.dataProvider && index != grid.dataProvider.length)
        {
            //increment the index if headers are being shown
            if(grid.headerVisible)
                ++index;
            
            if (index >= grid.lockedRowCount)
                index -= grid.verticalScrollPosition;
    
   			var completeListitems:Array = getCompleteRenderersArray();
			
	        //var rc:Number = grid.rendererArray.length;
	        var rc:Number = completeListitems.length;
	        
            if (index >= rc)
                index = rc - 1;
            
            if (index < 0)
                index = 0;

            //if(grid.rendererArray && grid.rendererArray[0] && grid.rendererArray[0].length)
                //index = index * grid.rendererArray[0].length;
	 
			if(completeListitems && completeListitems[0] && completeListitems[0].length)
	    		index = index * completeListitems[0].length;
    
            drag.draggedItem = getAutomationChildAt(index);
        }

        preventDragDropRecording = false;
        am.recordAutomatableEvent(uiAutomationObject, drag);
        preventDragDropRecording = true;
    }
    
  
    /**
     *  @private
     */
    override protected function mouseDownHandler(event:MouseEvent):void
    {
        //var listItems:Array = grid.rendererArray;
    	var listItems:Array = getCompleteRenderersArray();

        var r:IListItemRenderer = grid.getItemRendererForMouseEvent(event);
        var headerClick:Boolean = false;
        // if headers are visible and clickable for sorting
        if (grid.enabled && (grid.sortableColumns || grid.draggableColumns)
                && grid.headerVisible && listItems.length)
        {

            // find out if we clicked on a header
            var n:int = listItems[0].length;
            for (var i:int = 0; i < listItems[0].length; i++)
            {
                // if we did click on a header
                if (r == listItems[0][i])
                {
                    headerClick = true;     
                }
            }
        }
    
        if(!headerClick)
            super.mouseDownHandler(event);
    }

    /**
     *  @private
     */
    override protected function getItemRendererForEvent(lise:ListItemSelectEvent):IListItemRenderer
    {
        var rowIndex:int = lise.itemIndex;
        //This portion is commented out as now the rowHeaders are separated
        /*
        if(grid.headerVisible)
            ++rowIndex;
        */  
        
            
        rowIndex = rowIndex < grid.lockedRowCount ? rowIndex : rowIndex - grid.verticalScrollPosition;
    
        return grid.indicesToItemRenderer(rowIndex, 0);
    }
    
    /**
     *  @private
     */
    override protected function fillItemRendererIndex(item:IListItemRenderer, event:ListItemSelectEvent):void
    {
     	//var listItems:Array = grid.rendererArray;
    	var listItems:Array = getCompleteRenderersArray();
	
        var startRow:int = 0;
        //This portion is commented out as now the rowHeaders are separated
        /*
        if(grid.headerVisible)
            ++startRow;
            */
            
        
        for(var y:int = startRow; y < listItems.length; ++y)
        {
            for(var x:int = 0; x < listItems[y].length; ++x)
            {   
                if (listItems[y][x] == item)
                {
                    event.itemIndex = (y < grid.lockedRowCount ? y :
                                        y + grid.verticalScrollPosition) - 1;
                }
             }
        }
    }   
    
     
    /**
     *  @private
     */
   	public function getCompleteRenderersArray():Array
	{
		// we have different areas in the dg.
		// it has the following areas
		// locked header				(A),	unlocked header				(B),  
		// lockedrowandcolumn contents	(C),	locked row contents			(D), 
		// locekd column contents 		(E),	unlokced columnrow contents	(F)
		
		// note  renderArray (which is combination of
		// hedaer items and list items = B+F).
		
			
		
		// ..........get the Arrays............
		var lockedHeaderList:Array = new Array(); // Array A
		if(grid.dataGridLockedColumnHeader)
			lockedHeaderList = DataGridHeader(grid.dataGridLockedColumnHeader).rendererArray;
	
		var lockedRowAndColumnList:Array = new Array(); // Array C
		if(grid.dataGridLockedColumnAndRows)
			lockedRowAndColumnList= ListBaseContentHolder(grid.dataGridLockedColumnAndRows).listItems;
			
				var lockedRowList:Array = new Array(); // Array D
		if(grid.dataGridLockedRows)
			lockedRowList=ListBaseContentHolder(grid.dataGridLockedRows).listItems;// ArrayD
			
		var lockedColumnList:Array = new Array(); // Arrya E
		if(grid.dataGridLockedColumns)
			 lockedColumnList = ListBaseContentHolder(grid.dataGridLockedColumns).listItems;
		
		var unlockedRowColumnList:Array = grid.rendererArray; // Array B+F
		
		
		
		//***************** now let us combine the arrays.********
		
		var completeArray:Array = new Array();
		
		
		// *********get the header elements**********
		var headerArray:Array = new Array();
		if(lockedHeaderList.length)
		{
			headerArray = lockedHeaderList;
		}
		
		
		if(unlockedRowColumnList.length)
		{
			if(headerArray.length)
			{
				headerArray=headerArray.concat(unlockedRowColumnList[0]);
			}
			else	
				headerArray = unlockedRowColumnList[0];
		}
		
		
		completeArray.push(headerArray); 
		
		
		
		
		
		//.... let us add elements from C+D
		// number of rows corresponds to locked rowcount
		var locked_row_count:int = grid.lockedRowCount;
		for (var index:int=0; index < locked_row_count ; index++)
		{
			// C list we need to traverse for the locked column count
			// and D we need to traverse for the unlocked columncount
			var rowArray:Array = new Array();
			if(index < lockedRowAndColumnList.length)
				rowArray = lockedRowAndColumnList[index];
			
			if(index < lockedRowList.length) 
			{
				if(rowArray.length)
				{
					rowArray = rowArray.concat(lockedRowList[index]);
				}
				else
					rowArray = lockedRowList[index];
			}
			
			completeArray.push(rowArray);
			
		}
		
		
		//.... let us add elements from E+F
		// number of rows corresponds to unlocked rowcount
		var unlocked_row_count:int = grid.rowCount- grid.lockedRowCount;
		for ( index=1; index < unlocked_row_count+1 ; index++)
		{
			// C list we need to traverse for the locked column count
			// and D we need to traverse for the unlocked columncount
			rowArray = new Array();
			if(index < lockedColumnList.length)
				rowArray = lockedColumnList[index-1];
		
			if(index < unlockedRowColumnList.length) 
			{
				if(rowArray.length)
				{
					//unlockedRowColumnList contians the unlocked header arraya also
					// hence +1 for the index
					
					rowArray = rowArray.concat(unlockedRowColumnList[index]);
						
				}
				else
					rowArray = unlockedRowColumnList[index];
			}
			
			completeArray.push(rowArray);
			
		}
		
		
	
		return completeArray;
		
	}
	
	/**
	 *  @private
     */
    override public function get numAutomationChildren():int
    {
    	var listItems:Array = getCompleteRenderersArray();
        if (listItems.length == 0)
            return 0;

        var result:int = listItems.length * listItems[0].length;
        var row:uint = listItems.length - 1;
        var col:uint = listItems[0].length - 1;
        while (!listItems[row][col] && result > 0)
        {
            result--;
            if (col != 0)
                col--;
            else if (row != 0)
            {
                row--;
                col = listItems[0].length - 1;
            }
        }
        return result;
    }	
	

    
}
}
