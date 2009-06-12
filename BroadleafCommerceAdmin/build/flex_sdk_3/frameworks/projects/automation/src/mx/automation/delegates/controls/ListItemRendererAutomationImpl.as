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
import flash.events.Event;
import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.listClasses.ListItemRenderer;
import mx.core.mx_internal;
import mx.core.IUITextField;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ListItemRenderer class.
 * 
 *  @see mx.controls.listClasses.ListItemRenderer 
 *
 */
public class ListItemRendererAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(ListItemRenderer, ListItemRendererAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj ListItemRenderer object to be automated.     
     */
    public function ListItemRendererAutomationImpl(obj:ListItemRenderer)
    {
        super(obj);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get listItem():ListItemRenderer
    {
        return uiComponent as ListItemRenderer;
    }


    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  automationName
    //----------------------------------
   
    /**
     *  @private
     */
    override public function get automationName():String
    {
        return listItem.getLabel().text || super.automationName;
    }

    //----------------------------------
    //  automationValue
    //----------------------------------
   
    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [automationName];
    }

}
}