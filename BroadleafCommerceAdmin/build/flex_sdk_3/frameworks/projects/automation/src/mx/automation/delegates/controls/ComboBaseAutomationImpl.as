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
import flash.events.TextEvent;
import flash.ui.Keyboard;

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.ComboBase;
import mx.controls.TextInput;
import mx.core.mx_internal;
import mx.core.UIComponent;
import flash.display.DisplayObject;
import mx.automation.IAutomationObject;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ComboBase class.
 * 
 *  @see mx.controls.ComboBase 
 *
 */
public class ComboBaseAutomationImpl extends UIComponentAutomationImpl {

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
            Automation.registerDelegateClass(ComboBase, ComboBaseAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj ComboBase object to be automated.     
     */
    public function ComboBaseAutomationImpl(obj:ComboBase)
    {
        super(obj);

        obj.addEventListener("editableChanged", editableChangedHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get comboBase():ComboBase
    {
        return uiComponent as ComboBase;
    }
    
    /**
     *  @private
     *  returns the text of the current selection as a one-item array
     */
    override public function get automationValue():Array
    {
        return [comboBase.text];
    }

    /**
     * @private
     * Replays a text event by delegating responsibility to the text input.,
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (event is TextEvent)
        {
            comboBase.getTextInput().text = "";
            var replayer:IAutomationObject = 
                        comboBase.getTextInput() as IAutomationObject;
            return (replayer && comboBase.editable
                    ? replayer.replayAutomatableEvent(event)
                    : false);
        }
        return super.replayAutomatableEvent(event);
    }

    /**
     * @private
    */
    override protected function componentInitialized():void
    {
        super.componentInitialized();
        setupEditHandler();
    }

    /**
     * @private
    */
    protected function editableChangedHandler(event:Event):void
    {
        setupEditHandler();
    }
    
    /**
     * @private
    */
    protected function setupEditHandler():void
    {
        var text:DisplayObject = comboBase.getTextInput();
        if (!text)
            return;
        if (comboBase.editable)
        {
            text.addEventListener(AutomationRecordEvent.RECORD,
                                       textInput_recordHandler, false, 0, true);
        }
        else
        {
            text.removeEventListener(AutomationRecordEvent.RECORD,
                                       textInput_recordHandler);
        }
    }
    
    /**
     *  @private
     *  textInput is a automationComposite. Hence its own recording is skipped by AT.
     *  We need to handle this specifically.
     */
    private function textInput_recordHandler(event:AutomationRecordEvent):void
    {
        var keyEvent:KeyboardEvent = event.replayableEvent as KeyboardEvent;
        if (!keyEvent || 
            (keyEvent.keyCode != Keyboard.ENTER && keyEvent.keyCode != Keyboard.ESCAPE))
            recordAutomatableEvent(event.replayableEvent);
    }
    
}
}
