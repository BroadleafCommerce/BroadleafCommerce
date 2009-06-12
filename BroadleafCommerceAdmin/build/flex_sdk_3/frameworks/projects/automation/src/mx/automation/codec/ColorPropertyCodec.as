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
 * Translates between internal Flex color and automation-friendly version
 */
public class ColorPropertyCodec extends DefaultPropertyCodec
{
    /**
     * Constructor
     */
    public function ColorPropertyCodec()
	{
		super();
	}
   
    /**
     * @private
     */
    override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, propertyDescriptor);

        if (val != null)
        {
            val = Number(val).toString(16);
            while (val.length != 6)
            {
                val = "0" + val;
            }
            val = "#" + val;
        }
        
        return val;
    }

    /**
     * @private
     */
    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
        obj[propertyDescriptor.name] = parseInt(String(value).substring(1), 16).toString();
    }
}

}
