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
import mx.automation.IAutomationObject;
import mx.automation.delegates.core.ContainerAutomationImpl;
import mx.containers.Panel;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  Panel class. 
 * 
 *  @see mx.containers.Panel
 *  
 */
public class PanelAutomationImpl extends ContainerAutomationImpl 
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
        Automation.registerDelegateClass(Panel, PanelAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj Panel object to be automated.     
     */
    public function PanelAutomationImpl(obj:Panel)
    {
        super(obj);
        recordClick = true;
    }


    /**
     *  @private
     *  storage for the owner component
     */
    protected function get panel():Panel
    {
        return uiComponent as Panel;
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
        return panel.title || super.automationName;
    }

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [ panel.title ];
    }
    
    //----------------------------------
    //  numAutomationChildren
    //----------------------------------

    /**
     *  @private
     */
    override public function get numAutomationChildren():int
    {
        var result:int = super.numAutomationChildren;
        
        var controlBar:Object = panel.getControlBar();

        if (controlBar && (controlBar is IAutomationObject))
            ++result;

        if (panel._showCloseButton)
            ++result;

        return result;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var result:int = super.numAutomationChildren;
        var controlBar:Object = panel.getControlBar();
        if (index < result)
        {
            return super.getAutomationChildAt(index);
        }
        
        if (controlBar)
        {
            if(index == result)
                return (controlBar as IAutomationObject);
            ++result;
        }
        
        if (panel._showCloseButton && index == result)
            return (panel.closeButton as IAutomationObject);
        
        return null;
    }

}

}
