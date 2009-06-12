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
import mx.automation.Automation;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.styles.IStyleClient;

[ResourceBundle("automation_agent")]

/**
 * Base class for codecs, which translate between internal Flex properties 
 * and automation-friendly ones.
 */
public class DefaultPropertyCodec implements IAutomationPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	public function DefaultPropertyCodec()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	protected var resourceManager:IResourceManager =
									ResourceManager.getInstance();

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */ 
    public function encode(automationManager:IAutomationManager,
                           obj:Object, 
                           pd:IQTPPropertyDescriptor,
                           relativeParent:IAutomationObject):Object
    {
        var val:Object = getMemberFromObject(automationManager, obj, pd);

		//QTP can't handle NaN although COM can
		//If other testing tools want NaN, then we should extract this to a different codec
		//specific to QTP
		//if (val is Number && isNaN(Number(val)))
		//	return null;

        return getValue(automationManager, obj, val, pd);
    }

    /**
     *  @private
     */ 
    public function decode(automationManager:IAutomationManager,
                           obj:Object, 
                           value:Object,
                           pd:IQTPPropertyDescriptor,
                           relativeParent:IAutomationObject):void
    {
        obj[pd.name] = getValue(automationManager, obj, value, pd, true);
    }

    /**
     *  @private
     */ 
    public function getMemberFromObject(automationManager:IAutomationManager,
                                        obj:Object, 
                                        pd:IQTPPropertyDescriptor):Object
    {
    	var part:Object;
    	var component:Object;

    	if (obj is IAutomationObject)
	    {
	        part = automationManager.createIDPart(obj as IAutomationObject);
	        component = obj;
	    }   
	    else
	    	component = obj;
	    	
        var result:Object = null;

        if (part != null && pd.name in part)
            result = part[pd.name];
        else if (pd.name in obj)
            result = obj[pd.name];
        else if (component != null)
        {
        	if (pd.name in component)
	            result = component[pd.name];
    	    else if (component is IStyleClient)
        	    result = IStyleClient(component).getStyle(pd.name);
        }
       
        return result;
    }

    /**
     *  @private
     */ 
    private function getValue(automationManager:IAutomationManager,
                                    obj:Object, 
                                    val:Object,
                                    pd:IQTPPropertyDescriptor,
                                    useASType:Boolean = false):Object
    {
		if (val == null)
			return null;

		var type:String = useASType && pd.asType ? pd.asType : pd.QTPtype;

        switch (type)
        {
			case "Boolean":
            case "boolean":
                if (val is Boolean)
                    return val;
                val = val ? val.toString().toLowerCase() : "false";
                return val == "true";
			case "String":
            case "string":
                if (val is String)
                    return val;
                return val.toString();
			case "int":
			case "uint":
            case "integer":
                if (val is int || val is uint)
                    return val;
                if (val is Date)
                    return val.time;
                if (val is Number)
				{
					var message:String = resourceManager.getString(
						"automation_agent", "precisionLoss", [pd.name]);
                    throw new Error(message);
				}
                return parseInt(val.toString());
			case "Number":
            case "decimal":
                if (val is Number)
                    return val;
                if (val is Date)
                    return val.time;
                return parseFloat(val.toString());
			case "Date":
            case "date":
                if (val is Date)
                    return val;
                var num:Number = Date.parse(val.toString());
                return new Date(num);
            default:
                return val;
        }
    }
}

}
