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

import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;

/**
 * Translates between internal Flex triggerEvent property and automation-friendly version
 */
public class TriggerEventPropertyCodec extends DefaultPropertyCodec
{
    public function TriggerEventPropertyCodec()
	{
		super();
	}
	
	override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, propertyDescriptor);

       /* return (val is MouseEvent ? "mouse" :
                val is KeyboardEvent ? "keyboard" : null); */
       return (val is MouseEvent ? 1 :
                val is KeyboardEvent ? 2 : null); 
    }

    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    propertyDescriptor:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
        obj[propertyDescriptor.name] = 
            (value == 1 ? new MouseEvent(MouseEvent.CLICK) :
             value == 2 ? new KeyboardEvent(KeyboardEvent.KEY_UP) : null);
    }
}

}
