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
import flash.events.KeyboardEvent;
import flash.events.TextEvent;
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.automation.events.TextSelectionEvent;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.NumericStepper;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.events.NumericStepperEvent;
import mx.controls.TextInput;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  NumericStepper control.
 * 
 *  @see mx.controls.NumericStepper 
 *
 */
public class NumericStepperAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(NumericStepper, NumericStepperAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj NumericStepper object to be automated.     
     */
    public function NumericStepperAutomationImpl(obj:NumericStepper)
    {
        super(obj);
        
        obj.addEventListener(NumericStepperEvent.CHANGE, nsChangeHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get ns():NumericStepper
    {
        return uiComponent as NumericStepper;   
    }

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [ ns.value.toString() ];
    }

    /**
     *  @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (event is NumericStepperEvent)
        {
            var nsEvent:NumericStepperEvent = NumericStepperEvent(event);
            return help.replayClick(nsEvent.value > ns.value?
                                    ns.nextButton :
                                    ns.prevButton);
        }
        else if (event is KeyboardEvent)
        {
            return help.replayKeyboardEvent(ns.inputField,
                                            KeyboardEvent(event));
        }
        else if (event is TextEvent || event is TextSelectionEvent)
        {
            return (ns.inputField as IAutomationObject).replayAutomatableEvent(event);
        }
        else
        {
            return super.replayAutomatableEvent(event);
        }
    }
    
    /**
     *  Method which gets called after the component has been initialized. 
     *  This can be used to access any sub-components and act on the component.
     */
    override protected function componentInitialized():void
    {
        super.componentInitialized();
        
        ns.inputField.addEventListener(AutomationRecordEvent.RECORD,
                                   inputField_recordHandler);
        ns.inputField.addEventListener(KeyboardEvent.KEY_DOWN, 
                    inputField_keyDownHandler, false, EventPriority.DEFAULT+1);
    }
    
    private function inputField_keyDownHandler(event:KeyboardEvent):void
    {
        if (event.keyCode == Keyboard.HOME ||
            event.keyCode == Keyboard.END ||
            event.keyCode == Keyboard.UP ||
            event.keyCode == Keyboard.DOWN)
        {
            recordAutomatableEvent(event);
        }
    }
    
    /**
     *  @private
     */
    protected function nsChangeHandler(event:NumericStepperEvent):void
    {
        if(event.triggerEvent)
            recordAutomatableEvent(event);
    }
    
    /**
     *  @private
     */
    private function inputField_recordHandler(event:AutomationRecordEvent):void
    {
        // enter key is recorded by the base class.
        // prevent its recording
        var re:Object = event.replayableEvent;
        if(re is KeyboardEvent && re.keyCode == Keyboard.ENTER)
            return;
        recordAutomatableEvent(event.replayableEvent);
    }
        
}
}