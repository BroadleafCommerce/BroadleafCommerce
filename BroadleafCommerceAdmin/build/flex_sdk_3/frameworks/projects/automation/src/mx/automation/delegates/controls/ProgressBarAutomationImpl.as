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
import mx.automation.IAutomationObject;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.ProgressBar;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ProgressBar control.
 * 
 *  @see mx.controls.ProgressBar 
 *
 */
public class ProgressBarAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(ProgressBar, ProgressBarAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj ProgressBar object to be automated.     
     */
    public function ProgressBarAutomationImpl(obj:ProgressBar)
    {
        super(obj);
        recordClick = true;
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get  progressBar():ProgressBar
    {
        return uiComponent as ProgressBar;
    }

    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationName():String
    {
        return progressBar.label || progressBar.toolTip || super.automationName;
    }

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [ progressBar.label || progressBar.toolTip ];
    }
        
}

}