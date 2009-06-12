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

package mx.automation.codec
{ 

import mx.automation.AutomationError;
import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.core.mx_internal;
import mx.controls.AdvancedDataGrid;
import mx.automation.delegates.advancedDataGrid.AdvancedDataGridAutomationImpl;

use namespace mx_internal;

[ResourceBundle("automation_agent")]

/**
 * Translates between internal Flex List item and automation-friendly version
 */
public class AdvancedDataGridSelectedCellCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function AdvancedDataGridSelectedCellCodec()
	{
		super();
	}
   
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    override public function encode(automationManager:IAutomationManager,
                                    obj:Object,  
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, propertyDescriptor);
         
		if (val != null)
		{ 
		    //val = relativeParent.automationTabularData.getAutomationValueForFiedData(val).join(" | ");
		    var adg:AdvancedDataGrid = relativeParent as AdvancedDataGrid;
		    var objdel:AdvancedDataGridAutomationImpl = (adg.automationDelegate) as AdvancedDataGridAutomationImpl;
        	var ret:Array = [];
        	if((val.columnIndex == -1)&& (val.rowIndex != -1))
        	{
        	 return objdel.getRowData(val.rowIndex,true);
        	}
        	if((val.rowIndex != -1) &&(val.columnIndex != -1))
        	{
            	ret.push(objdel.getCellData(val.rowIndex,val.columnIndex,true)); 
            }
            val= ret;
    	
		}
        
        return val;
    }

    /**
     *  @private
     */ 
    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
		var message:String = resourceManager.getString(
			"automation_agent", "notSupported");
        throw new AutomationError(message, AutomationError.ILLEGAL_OPERATION);
    }
}

}
