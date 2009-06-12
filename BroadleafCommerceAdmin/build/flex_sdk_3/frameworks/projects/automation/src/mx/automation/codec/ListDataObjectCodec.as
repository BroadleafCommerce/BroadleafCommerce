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

import mx.automation.AutomationError;
import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.core.mx_internal;

use namespace mx_internal;

[ResourceBundle("automation_agent")]

/**
 * Translates between internal Flex List item and automation-friendly version
 */
public class ListDataObjectCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function ListDataObjectCodec()
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
		    val = relativeParent.automationTabularData.getAutomationValueForData(val).join(" | ");
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
