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
import flash.events.KeyboardEvent;
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.alertClasses.AlertForm;
import mx.controls.Button;
import mx.core.EventPriority;
import mx.core.mx_internal;
import flash.events.Event;
import mx.automation.IAutomationObject;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the AlertForm class. 
 * 
 *  @see mx.controls.alertClasses.AlertForm
 *  
 */
public class AlertFormAutomationImpl extends UIComponentAutomationImpl 
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
            Automation.registerDelegateClass(AlertForm, AlertFormAutomationImpl);
    }   
    
    /**
     *  Constructor.
     * @param obj AlertForm object to be automated.     
     */
    public function AlertFormAutomationImpl(obj:AlertForm)
    {
        super(obj);
        
        alertForm = obj;
    }
    
    private var alertForm:AlertForm;
    
    /**
     *  Method which gets called after the component has been initialized. 
     *  This can be used to access any sub-components and act on the component.
     */
    override protected function componentInitialized():void
    {   
        super.componentInitialized();
        for each(var b:Button in alertForm.buttons)
        {
            // we want to record escape key before alertForm closes the alert
            b.addEventListener(KeyboardEvent.KEY_DOWN, alertKeyDownHandler,
                                false, EventPriority.DEFAULT+1, true);
        }
    }
 

    /**
     *  @private
     */
    private function alertKeyDownHandler(event:KeyboardEvent):void
    {
        if (event.keyCode == Keyboard.ESCAPE)
        {
            // we want to record the escape key as invoked from the button.
            var am:IAutomationManager = Automation.automationManager;
            var delegate:IAutomationObject = event.target as IAutomationObject;
            if(am && delegate)
                am.recordAutomatableEvent(delegate, event);
        }
    }
}
}