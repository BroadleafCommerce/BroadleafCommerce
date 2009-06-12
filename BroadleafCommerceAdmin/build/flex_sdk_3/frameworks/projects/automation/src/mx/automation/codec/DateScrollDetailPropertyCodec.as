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
 * Translates between internal Flex description of a scroll event 
 * and automation-friendly version
 */
public class DateScrollDetailPropertyCodec extends DefaultPropertyCodec
{
    /**
     *  Constructor
     */ 
    public function DateScrollDetailPropertyCodec()
	{
		super();
	}
   
    /**
     *  @private
     */ 
    override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:int = 0;
        
        if (!("detail" in obj))
        	return val;

	    switch (obj["detail"])
	    {
		    case "nextMonth": return 1;
		    case "nextYear": return 2;
		    case "previousMonth": return 3;
		    case "previousYear": return 4;
	    }
	    
        return val;
    }

    /**
     *  @private
     */ 
    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
    	var details:Array = 
    	[
	    "nextMonth", "nextYear", "previousMonth", "previousYear"
	    ];
        	
        if ("detail" in obj && value < details.length)
	        obj["detail"] = details[Number(value)-1];
    }
}

}
