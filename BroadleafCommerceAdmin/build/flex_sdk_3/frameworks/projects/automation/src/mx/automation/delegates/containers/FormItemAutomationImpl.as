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

package mx.automation.delegates.containers 
{
import flash.display.DisplayObject;

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.ContainerAutomationImpl;
import mx.containers.FormItem;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  FormItem class. 
 * 
 *  @see mx.containers.FormItem
 *  
 */
public class FormItemAutomationImpl extends ContainerAutomationImpl 
{
    include "../../../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Registers the delegate class for a component class with automation manager.
     *  
     *  @param root The SystemManger of the application.
     */
    public static function init(root:DisplayObject):void
    {
        Automation.registerDelegateClass(FormItem, FormItemAutomationImpl);
    }   

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get formItem():FormItem
    {
        return uiComponent as FormItem;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @param obj The FormItem object to be automated.
     */
    public function FormItemAutomationImpl(obj:FormItem)
    {
        super(obj);

    }


    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        var label:IAutomationObject = formItem.itemLabel as IAutomationObject;
        var result:Array = [ label ? label.automationName : null ];
        for (var i:int = 0; i < numAutomationChildren; i++)
        {
            var child:IAutomationObject = getAutomationChildAt(i);
            if (child == label)
                continue;
            var x:Array = child.automationValue;
            if (x && x.length != 0)
                result.push(x);
        }
        return result;
    }
    
    //----------------------------------
    //  numAutomationChildren
    //----------------------------------

    /**
     *  @private
     */
    override public function get numAutomationChildren():int
    {
        return formItem.numChildren + (formItem.itemLabel != null ? 1 : 0);
    }
        
    /**
     *  @private
     */
    override public function createAutomationIDPart(child:IAutomationObject):Object
    { 
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.helpCreateIDPart(uiAutomationObject, child, getItemAutomationName);
    }

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var labelObj:IAutomationObject = formItem.itemLabel as IAutomationObject;
        return (index == formItem.numChildren && labelObj != null 
                ? (labelObj)
                : super.getAutomationChildAt(index));
    }

    /**
     * @private
     */
    private function getItemAutomationName(child:IAutomationObject):String
    {
         var labelObj:IAutomationObject = formItem.itemLabel as IAutomationObject;
        var label:String = labelObj ? labelObj.automationName : "";
        var result:String = null;
        if (child.automationName && child.automationName.length != 0)
            result = (((label)&&(label.length != 0))
                      ? label + ":" + child.automationName 
                      : child.automationName);
        else
        {
            for (var i:uint = 0; !result && i < numAutomationChildren; i++)
            {
                if (getAutomationChildAt(i) == child)
                {
                    result = (i == 0 && 
                              numAutomationChildren == (labelObj ? 2 : 1)
                              ? label
                              : label + ":" + i);
                }
            }
        }
        return result;
    }


        
}
}