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
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.menuClasses.MenuBarItem;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  MenuBarItem class.
 * 
 *  @see mx.controls.menuClasses.MenuBarItem 
 *
 */
public class MenuBarItemAutomationImpl extends UIComponentAutomationImpl 
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
            Automation.registerDelegateClass(MenuBarItem, MenuBarItemAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj MenuBarItem object to be automated.     
     */
    public function MenuBarItemAutomationImpl(obj:MenuBarItem)
    {
        super(obj);
    }

    //----------------------------------
    //  menuBarItem
    //----------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get menuBarItem():MenuBarItem
    {
        return uiComponent as MenuBarItem;
    }

    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationName():String
    {
        return menuBarItem.getLabel().text  || super.automationName;
    }

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [menuBarItem.getLabel().text];
    }

}
}