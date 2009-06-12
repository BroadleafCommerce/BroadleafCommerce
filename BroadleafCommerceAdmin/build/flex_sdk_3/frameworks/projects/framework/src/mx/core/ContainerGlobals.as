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

package mx.core
{

import flash.display.InteractiveObject;
import mx.managers.IFocusManager;

public class ContainerGlobals
{
    /**
     *  @private
     *  Internal variable that keeps track of the container
     *  that currently has focus.
     */
    public static var focusedContainer:InteractiveObject;

	/**
     *  @private
     *  Support for defaultButton.
     */
    public static function checkFocus(oldObj:InteractiveObject,
                                           newObj:InteractiveObject):void
    {
        var objParent:InteractiveObject = newObj;
        var currObj:InteractiveObject = newObj;
        var lastUIComp:IUIComponent = null;

        if (newObj != null && oldObj == newObj)
            return;
        
        // Find the Container parent with a defaultButton defined.
        while (currObj)
        {
            if (currObj.parent)
            {
                objParent = currObj.parent;
            }
            else
            {
                objParent = null;
            }

            if (currObj is IUIComponent)
                lastUIComp = IUIComponent(currObj);

            currObj = objParent;

            if (currObj &&
                currObj is IContainer && IContainer(currObj).defaultButton)
            {
                break;
            }
        }

        if (ContainerGlobals.focusedContainer != currObj || 
        	(ContainerGlobals.focusedContainer == null && currObj == null))
        {
            if (!currObj)
                currObj = InteractiveObject(lastUIComp);

            if (currObj && currObj is IContainer)
            {
                var fm:IFocusManager = IContainer(currObj).focusManager;
				if (!fm)
					return;
                var defButton:IButton = IContainer(currObj).defaultButton as IButton;
                if (defButton)
                {
                    ContainerGlobals.focusedContainer = InteractiveObject(currObj);
                    fm.defaultButton = defButton as IButton;
                }
                else
                {
                    ContainerGlobals.focusedContainer = InteractiveObject(currObj);
                    fm.defaultButton = null;
                }
            }
        }
    }


}

}

