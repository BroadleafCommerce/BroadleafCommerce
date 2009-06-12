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
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationObjectHelper;
import mx.controls.RadioButton;
import mx.core.EventPriority;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  RadioButton control.
 * 
 *  @see mx.controls.RadioButton 
 *
 */
public class RadioButtonAutomationImpl extends ButtonAutomationImpl 
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
        Automation.registerDelegateClass(RadioButton, RadioButtonAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj RadioButton object to be automated.     
     */
    public function RadioButtonAutomationImpl(obj:RadioButton)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get radioButton():RadioButton
    {
        return uiComponent as RadioButton;
    }

    /**
     *  @private
     *  Replays click interactions on the button.
     *  If the interaction was from the mouse,
     *  dispatches MOUSE_DOWN, MOUSE_UP, and CLICK.
     *  If interaction was from the keyboard,
     *  dispatches KEY_DOWN, KEY_UP.
     *  Button's KEY_UP handler then dispatches CLICK.
     *
     *  @param event ReplayableClickEvent to replay.
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;

        if (event is KeyboardEvent)
            return help.replayKeyboardEvent(uiComponent, KeyboardEvent(event));
        else
            return super.replayAutomatableEvent(event);
    }

    /**
     *  @private
     *  Support the use of keyboard within the group.
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        switch (event.keyCode)
        {
            case Keyboard.DOWN:
            case Keyboard.UP:
            case Keyboard.LEFT:
            case Keyboard.RIGHT:
            //for form defaults:
            case Keyboard.ENTER:
                recordAutomatableEvent(event);
                break;
        }
    }
        
}
}