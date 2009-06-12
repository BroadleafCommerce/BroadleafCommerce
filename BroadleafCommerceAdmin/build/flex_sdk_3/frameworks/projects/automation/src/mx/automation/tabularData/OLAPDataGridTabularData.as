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

import mx.collections.ArrayCollection;
import mx.controls.OLAPDataGrid;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.core.mx_internal;
import mx.olap.IOLAPAxisPosition;
import mx.olap.IOLAPResult;
import mx.olap.IOLAPResultAxis;
import mx.olap.OLAPQuery;

use namespace mx_internal;

/**
 * @private
 */
public class OLAPDataGridTabularData extends AdvancedDataGridTabularData
{
    /**
     *  Constructor
     */
    public function OLAPDataGridTabularData(dg:OLAPDataGrid)
    {
 
 		super(dg);
        this.OLAPdg = dg;
    }

    /**
     *  @private
     */
    private var OLAPdg:OLAPDataGrid;

    /**
     *  @private
     */
    override public function get numColumns():int
    {
    	/*    
    	var resultVar:IOLAPResult = OLAPdg.dataProvider as IOLAPResult;
    	// no of the columns of the tabular data is the actual column count +
    	// the rowHeader.count
    	var columnCount:int = resultVar.getAxis(OLAPQuery.COLUMN_AXIS).positions.length;
    	var rowHeaderCount : int =  resultVar.getAxis(OLAPQuery.ROW_AXIS).positions[0].members.length;
        return  (columnCount + rowHeaderCount);
        */
        return OLAPdg.columns.length;
    }


    /**
     *  @private
     */
    override public function get columnNames():Array
    {
        //override to provide the column names
        // this includes the rowHeader names + leaf level column Names
        var result:Array = [];
        
        
        //**************get the columnHeader names*************
        var columns:Array = OLAPdg.columns;
        var colCount:int = columns.length;
        for (var i:int = 0; i < colCount; ++i)
        {
            result.push((AdvancedDataGridColumn (columns[i])).headerText);
        }
        //*******************************************************
        
        return result;
    }

    /**
     *  @private
     */
    override public function get firstVisibleRow():int
    {
		var listItems:Array = OLAPdg.rendererArray;

        if (!OLAPdg.headerVisible)
            return super.firstVisibleRow;
        else
            return (listItems[0][0] 
                    ? OLAPdg.itemRendererToIndex(listItems[0][0])
                    : 0);
    }
    
    
    /**
    *   @private
    * */

    override public function getValues(start:uint = 0, end:uint = 0):Array
    {
   		var result:Array = [];
    	  
        // index include the header also 
    	var rowInex:int = start;
      	var endRowIndex:int = end;
      		
   	
   			
    	// this method is used to give the data of the ODG table as the tabular data
     	// the ODG structure will be as follows
     
    	// RowHeader1:RowHeader2:Column1;Colum2...... Columnn
     	// i.e we want the data of the rowAxis also to be added to the columns
     
   	   
	     
	      // *********** get the total column count ************
	      // we need to get the total number of columns
	      // i.e the sum of the  rowHeader counts + lno : of column count
	      var resultVar:IOLAPResult = OLAPdg.dataProvider as IOLAPResult;
	      var rowAxis:IOLAPResultAxis = resultVar.getAxis(OLAPQuery.ROW_AXIS);
	      var rowAxisHeader:IOLAPAxisPosition = rowAxis.positions[0];
	      var columns:Array = OLAPdg.columns;
	      
	      // ********** get the counts **********
	      // get the rowHeader Count
	      var rowHeaderCount:int = rowAxisHeader.members.length;
	      // get the row Count
	      var rowCount:int = rowAxis.positions.length;
	        // get column count      
	      var columnCount:int = columns.length - rowHeaderCount;
	      
	      
	      	// check for the validity of the start and the end index
   			if(rowInex <  0) 
   			{
   				trace (" the start Index is less than zero. set it to zero");
   				rowInex = 0;
   			}
   			if(endRowIndex > rowCount)
   			{
   				trace (" the end Index is greater than rowCount. set it to maxValue");
   				endRowIndex = rowCount;
   			} 
   		
   		
       
       	 // the user given rowIndex is with the header
       	 // but the position on the rowAxis and hte getcell is from the first data row
       	 // remember that the row indices are inclusive of the column Header
       	 for (var currRowIndex:int = rowInex; currRowIndex < endRowIndex ; currRowIndex++)
         {
         	var rowData:Array = []; // array to hold a row info
         	var rowMembersList:IOLAPAxisPosition = rowAxis.positions[currRowIndex];
           	for (var rowMemberIndex:int =0; rowMemberIndex < rowHeaderCount ; rowMemberIndex++)
          	{
          		var rowDatString:String = rowMembersList.members[rowMemberIndex].name;
        		rowData.push(rowDatString);
          	}
          	
          	// traverse the column data using getCell
          	for (var columnIndex:int =0; columnIndex < columnCount ; columnIndex++)
          	{
          		var columnDataString:String = String(resultVar.getCell(currRowIndex,columnIndex).value);
        		rowData.push(columnDataString);
          	}
          	
          	var rowString:String = rowData.join("|")
          	result.push(rowData);
         }
       	 
     return result;
     
    }
 

    
      /**
     *  @inheritDoc
     */
    public override function get numRows():int
    {
      var resultVar:IOLAPResult = OLAPdg.dataProvider as IOLAPResult;
		// rows for the tabular data includes the column header also
	   // hence add one to the length
        return resultVar.getAxis(OLAPQuery.ROW_AXIS).positions.length + 1;
    }
 
    override public function getAutomationValueForData(data:Object):Array
    {
    	// we get the data as the IOLAPAxisPosition
    	// hence we need a diff handling compared to the the
    	// DG and ADG
    	// This method gets the Automationvalue corresponding to the
    	// current IOLAPPosition
    	
        var ret:Array = [];
        var rowMembersList:IOLAPAxisPosition = data as IOLAPAxisPosition;
        // this should be present in the ROW_AXIS
        // get the row no:
       	 var resultVar:IOLAPResult = OLAPdg.dataProvider as IOLAPResult;
	     var rowAxis:IOLAPResultAxis = resultVar.getAxis(OLAPQuery.ROW_AXIS);
	     var rowElements:ArrayCollection =  rowAxis.positions as ArrayCollection;
	     if(rowElements != null)
	     {
		     var rowPos:Number = rowElements.getItemIndex(data);
		     if(rowPos != -1)
		     {
		         var rowHeaderCount:int = rowMembersList.members.length;
		         
		        for (var rowMemberIndex:int =0; rowMemberIndex < rowHeaderCount ; rowMemberIndex++)
		      	{
		      		var rowDatString:String = rowMembersList.members[rowMemberIndex].name;
		    		ret.push(rowDatString);
		      	}
		      	
		      	var columns:Array = OLAPdg.columns;
		      	var columnCount:int = columns.length - rowHeaderCount;
	      	 	// traverse the column data using getCell
	          	for (var columnIndex:int =0; columnIndex < columnCount ; columnIndex++)
	          	{
	          		var columnDataString:String = String(resultVar.getCell(rowPos,columnIndex).value);
	        		ret.push(columnDataString);
	          	}
	          	
	          	ret.join("|")
       		}
	     }
      

        return ret;
    }
    
   
}
}
