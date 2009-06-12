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


package mx.automation.delegates.controls 
{
import flash.display.DisplayObject;

import mx.automation.Automation;
import mx.controls.CheckBox;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  CheckBox control.
 * 
 *  @see mx.controls.CheckBox 
 *
 */
public class CheckBoxAutomationImpl extends ButtonAutomationImpl 
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
            Automation.registerDelegateClass(CheckBox, CheckBoxAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj CheckBox object to be automated.     
     */
    public function CheckBoxAutomationImpl(obj:CheckBox)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get chk():CheckBox
    {
        return uiComponent as CheckBox;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  automationValue
    //----------------------------------
    
    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        var result:String = chk.selected ? "[X]" : "[ ]";
        if (chk.label || chk.toolTip)
            result += " " + (chk.label || chk.toolTip);
        return [ result ];
    }

}
}