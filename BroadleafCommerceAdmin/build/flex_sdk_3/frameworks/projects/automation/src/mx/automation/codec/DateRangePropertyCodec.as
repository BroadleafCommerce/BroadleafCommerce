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

[ResourceBundle("automation_agent")]

/**
 * Translates between internal Flex date range and automation-friendly version
 */
public class DateRangePropertyCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor
     */ 
    public function DateRangePropertyCodec()
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
		var encodedDate:String = "";

        if (val != null)
        {
        	if (val is Date)
        	{
	            encodedDate = (val as Date).toLocaleDateString();
        	}
        	else if (val is Object)
        	{
	        	if ("rangeStart" in val)
            encodedDate = (val.rangeStart as Date).toLocaleDateString();
            encodedDate += " => "
            	if ("rangeEnd" in val)
            encodedDate += (val.rangeEnd as Date).toLocaleDateString();
        }
        }
        
        return encodedDate;
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
			"automation_agent", "notSettable");
        throw new Error(message);
    }
}

}
