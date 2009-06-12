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
import flash.events.MouseEvent;
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.controls.DateField;
import mx.core.EventPriority;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.DateChooserEvent;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;


use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  DateField control.
 * 
 *  @see mx.controls.DateField 
 *
 */
public class DateFieldAutomationImpl extends ComboBaseAutomationImpl 
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
        Automation.registerDelegateClass(DateField, DateFieldAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj DateField object to be automated.     
     */
    public function DateFieldAutomationImpl(obj:DateField)
    {
        super(obj);
        
        obj.addEventListener(DropdownEvent.OPEN, openCloseHandler, false, 0, true);
        obj.addEventListener(DropdownEvent.CLOSE, openCloseHandler, false, 0, true);
    }
    
    /**
     *  @private
     *  storage for the owner component
     */
    protected function get dateField():DateField
    {
        return uiComponent as DateField;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Replays DateChooserEvents and CalendarLayoutChangeEvents by
     *  delegating to the drop-down list, which is a DateChooser control. Replays
     *  <code>DropdownEvent.OPEN</code> events when the user clicks the
     *  down-arrow button or presses the Spacebar, depending on the <code>interaction.inputType</code>.
     *  Replays <code>DropdownEvent.CLOSE</code> events when the user clicks the
     *  date display field or presses the Spacebar, depending on the <code>event.inputType</code>.
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;

        //the dropdown datechooser doesn't get the focus so keyboard events
        //go through us (and ultimately get sent to our keyDownHandler which
        //sends them to the datechooser)
        if (event is KeyboardEvent)
        {
            //send the events to the focus, not to "this" because
            //the text input may have the focus and only the item with
            //focus has the keyDown handler
            dateField.setFocus();
            return help.replayKeyboardEvent(dateField.getFocus(), KeyboardEvent(event));
        }

        if (event is DateChooserEvent ||
            event is CalendarLayoutChangeEvent)
        {
            var replayer:IAutomationObject
                        = dateField.dropdown as IAutomationObject;
            return replayer.replayAutomatableEvent(event);
        }
        else if (event is DropdownEvent)
        {
            var validOpen:Boolean =
                !dateField.showingDropdown && event.type == DropdownEvent.OPEN;

            var validClose:Boolean =
                dateField.showingDropdown && event.type == DropdownEvent.CLOSE;

            // if this is not a valid open or close, its bogus
            if (!validOpen && !validClose)
                return false;

            if (DropdownEvent(event).triggerEvent is KeyboardEvent)
            {
                //send the events to the focus, not to "this" because
                //the text input may have the focus and only the item with
                //focus has the keyDown handler
                dateField.setFocus();
                if (validOpen)
                    return help.replayKeyDownKeyUp(dateField.getFocus(), Keyboard.DOWN, true);
                else
                    return help.replayKeyDownKeyUp(dateField.getFocus(), Keyboard.ENTER);
            }
            else if (DropdownEvent(event).triggerEvent is MouseEvent)
            {
                if (validOpen)
                    return help.replayClick(dateField.ComboDownArrowButton);
                else if (validClose)
                    return help.replayClickOffStage();
            }
            else
                throw new Error();
            return false;
        }
        return super.replayAutomatableEvent(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function openCloseHandler(ev:DropdownEvent):void 
    {
        // record only if we have associated trigger event
        // trigger event is not specified in cases where open/close event
        // should not be recorded. Ex: pressing ESCAPE key to close the dropdown.
        if(ev.triggerEvent)
        {
            var textInput:DisplayObject = dateField.getTextInput();
            if(ev.type == DropdownEvent.OPEN)
            {
                recordAutomatableEvent(ev);
                dateField.dropdown.addEventListener(AutomationRecordEvent.RECORD,
                                   dropdown_recordHandler, false, 0, true);
            }
            else
            {
                // if keyboard is being used to close the dropdown
                // we need to do speical handling of the enter key.
                if(ev.triggerEvent is KeyboardEvent)
                {
                    var keyEvent:KeyboardEvent = ev.triggerEvent as KeyboardEvent;
                    if (keyEvent.ctrlKey)
                        recordAutomatableEvent(ev);
                    else
                    {
                        switch(keyEvent.keyCode)
                        {
                            case Keyboard.ENTER :
                                // for editable dateField UIComponent records the Enter.
                                if(dateField.editable)
                                    recordAutomatableEvent(ev.triggerEvent);
                                break;
                            default :
                                recordAutomatableEvent(ev.triggerEvent);
                                break;
                        }
                    }
                }   
                else
                    recordAutomatableEvent(ev);
                dateField.dropdown.removeEventListener(AutomationRecordEvent.RECORD,
                                   dropdown_recordHandler);
            }
        }
    }

    /**
     *  @private
     *  Records the events of dateChooser as DateFields events.
     */
    private function dropdown_recordHandler(event:AutomationRecordEvent):void
    {
        // record the date change and calender navigation events
        if (event.replayableEvent is CalendarLayoutChangeEvent ||
            event.replayableEvent is DateChooserEvent)
            recordAutomatableEvent(event.replayableEvent);
        
        // record keyboard naviagation
        if (event.replayableEvent is KeyboardEvent)
        {
            var keyEvent:KeyboardEvent = event.replayableEvent as KeyboardEvent;
            if(keyEvent.keyCode != Keyboard.ENTER)
                recordAutomatableEvent(keyEvent);
        }
    }

}

}
