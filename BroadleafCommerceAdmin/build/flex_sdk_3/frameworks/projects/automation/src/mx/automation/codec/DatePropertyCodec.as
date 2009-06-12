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

/**
 * Translates between internal Flex date and automation-friendly version
 */
public class DatePropertyCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constrcutor
     */ 
    public function DatePropertyCodec()
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
    	obj[propertyDescriptor.name] =  new Date(Date.parse(String(value)));
    }
}

}
