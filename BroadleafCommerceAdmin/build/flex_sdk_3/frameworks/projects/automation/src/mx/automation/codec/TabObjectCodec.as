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

import mx.automation.AutomationIDPart;
import mx.automation.Automation;
import mx.automation.qtp.IQTPPropertyDescriptor;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.codec.AutomationObjectPropertyCodec;
import mx.containers.TabNavigator;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 * Translates between internal Flex TabNavigator object and automation-friendly version
 */
public class TabObjectCodec extends AutomationObjectPropertyCodec
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function TabObjectCodec()
	{
		super();
	}
   
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

  
    /**
	 *  This is only used for TabNavigators.  
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
			var aoc:IAutomationObject;
	        if (relativeParent != null)
	        {
	        	var tabBar:Object = Object(relativeParent).getTabBar();
		        aoc = tabBar as IAutomationObject;
	        }
	        else
	        {
	        	aoc = obj as IAutomationObject;
	 		}
		
			for (var i:uint = 0; i < aoc.numAutomationChildren; i++)
			{
				var delegate:IAutomationObject = aoc.getAutomationChildAt(i);
				if (delegate.automationName == value)
				{
					obj[pd.name] = delegate;
					break;
				}
			}
		}
    }
}

}
