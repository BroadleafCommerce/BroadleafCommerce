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
 * translates between internal Flex asset and automation-friendly version
 */
public class AssetPropertyCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function AssetPropertyCodec()
	{
		super();
	}
   
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, pd);

		if (val != null)
		{
	        val = val.toString();
	            
	        val = stripNoiseFromString("css_", String(val));
	        val = stripNoiseFromString("embed_mxml_", String(val));
	        val = stripNoiseFromString("embed_as_", String(val));
		}
        
        return val;
    }

    protected function stripNoiseFromString(beginPart:String, asset:String):String
    {
	    var pos:int = asset.indexOf(beginPart);

	    if (pos != -1)
	    {
	        asset = asset.substr(pos + beginPart.length);
	        
	        var lastUnderscorePos:int = asset.lastIndexOf("_");
	        
	        if (lastUnderscorePos != -1)
	        {
	            asset = asset.substr(0, lastUnderscorePos);
	        }
	    }
	    
	    return asset;
    }

    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
        var message:String = resourceManager.getString(
			"automation_agent", "notSettable");
		throw new Error(message);
    }
}

}
