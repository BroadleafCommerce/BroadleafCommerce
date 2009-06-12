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
 * Translates between internal Flex ScrollEvent direction and automation-friendly version
 */
public class ScrollDirectionPropertyCodec extends DefaultPropertyCodec
{
    /**
     *  Constructor
     */
    public function ScrollDirectionPropertyCodec()
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
		
		if (!("direction" in obj))
			return val;

	    switch (obj["direction"])
	    {
		    case "horizontal" : return 1;
		    case "vertical" : return 2;
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
    	var directions:Array = 
    	[ "horizontal", "vertical" ];
        	
        if ("direction" in obj && value > 0 && value <= directions.length)
	        obj["direction"] = directions[uint(value)-1];
    }
}

}
