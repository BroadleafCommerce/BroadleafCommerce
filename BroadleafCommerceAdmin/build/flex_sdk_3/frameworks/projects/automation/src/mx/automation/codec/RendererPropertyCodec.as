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

import mx.automation.AutomationClass;
import mx.automation.AutomationIDPart;
import mx.automation.Automation;
import mx.automation.IAutomationClass;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.qtp.IQTPPropertyDescriptor;

[ResourceBundle("automation_agent")]

/**
 *  Translates between internal Flex component and automation-friendly version
 */
public class RendererPropertyCodec extends AutomationObjectPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function RendererPropertyCodec()
	{
		super();
	}
   
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
     * @private
     */
    override public function encode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, pd);

		var delegate:IAutomationObject = val as IAutomationObject;
        if (delegate)
        {
        	var part:AutomationIDPart = automationManager.createIDPart(delegate);
           	val = part.automationName;
        	var automationClass:IAutomationClass;
        	if (relativeParent)
  	            automationClass = automationManager.automationEnvironment.getAutomationClassByInstance(relativeParent);
  	        if (automationClass)
		    {
		    	var propertyNameMap:Object = automationClass.propertyNameMap;
	        	if (propertyNameMap["enableIndexBasedSelection"])
		        	val = part.automationIndex;
		    }
        }
        
        if (!val && !(val is int))
        	val = "";

        return val;
    }

    /**
     * @private
     */
    override public function decode(automationManager:IAutomationManager,
                                    obj:Object, 
                                    value:Object,
                                    pd:IQTPPropertyDescriptor,
                                    relativeParent:IAutomationObject):void
    {
		if (value == null || value.length == 0)
		{
	        obj[pd.name] = null;
		}
		else
		{
	        var aoc:IAutomationObject = 
	        		(relativeParent != null ? relativeParent : obj as IAutomationObject);

	        var part:AutomationIDPart = new AutomationIDPart();
	        // If we have any descriptive programming element
            // in the value string use that property.
            // If it is a normal string assume it to be automationName
	        var text:String = String(value);
	        var separatorPos:int = text.indexOf(":=");
	        var items:Array = [];
	        if(separatorPos != -1)
	        	items = text.split(":=");

        	if(items.length == 2)
	        	part[items[0]] = items[1]; 
	        else
				part.automationName = text;
	            
			var ao:Array = automationManager.resolveIDPart(aoc, part);
			var delegate:IAutomationObject = (ao[0] as IAutomationObject);
			if (delegate)
		    	obj[pd.name] = delegate;
		    else
		    	obj[pd.name] = ao[0];
		    	
	    	if (ao.length > 1)
	    	{
				var message:String = resourceManager.getString(
					"automation_agent", "matchesMsg",[ ao.length,
                    part.toString().replace(/\n/, ' ')]) + ":\n";

        	    for (var i:int = 0; i < ao.length; i++)
				{
            	    message += AutomationClass.getClassName(ao[i]) + 
							   "(" + ao[i].automationName + ")\n";
				}

	    		trace(message);
	    	}

            // we couldnot find the itemRenderer in the visible area
            // set itemString property if it is available so that the
            // delegate can handle this.
	    	if (!ao.length)
	    	{
	    		if (obj.hasOwnProperty("itemAutomationValue"))
	    			obj["itemAutomationValue"] = text;
	    	}
		}
    }
}

}
