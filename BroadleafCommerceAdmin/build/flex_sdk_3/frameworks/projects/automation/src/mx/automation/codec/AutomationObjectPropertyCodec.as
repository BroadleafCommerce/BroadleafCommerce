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
import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;

[ResourceBundle("automation_agent")]

/**
 * Translates between internal Flex component and automation-friendly version
 */
public class AutomationObjectPropertyCodec extends DefaultPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function AutomationObjectPropertyCodec()
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
            //only use automationName
            val = automationManager.createIDPart(delegate).automationName;
			
            //the following is if we decide to support "automationObject"'s that are not direct
            //decendents of the interaction replayer
            /*
            var id:ReproducibleID = automationManager.createID(val, 
                                            IAutomationObject(relativeParent));
            
            if (id.length == 0)
                return "";
            
            var nameChain:String = id.removeFirst().automationName;
            
            while (id.length)
            {
                //should escape seperator
                var an:String = id.removeFirst().automationName;
                nameChain += "^" + an;
            }
            
            val =  nameChain;
            */
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
		}
        
        //the following is if we decide to support "automationObject"'s that are not direct
        //decendents of the interaction replayer
        /*        
            var automationNameArray:Array = automationNames.split("^");
            var rid:ReproducibleID = new ReproducibleID();
            
            while (automationNameArray.length)
            {
                rid.addFirst({automationName: automationNameArray.pop()});
            }

            return automationManager.resolveIDToSingleObject(rid, IAutomationObject(target));
        */
    }
}

}
