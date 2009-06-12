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
import flash.utils.getTimer;

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.controls.PopUpButton;
import mx.events.DropdownEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  PopUpButton control.
 * 
 *  @see mx.controls.PopUpButton 
 *
 */
public class PopUpButtonAutomationImpl extends ButtonAutomationImpl 
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
        Automation.registerDelegateClass(PopUpButton, PopUpButtonAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj PopUpButton object to be automated.     
     */
    public function PopUpButtonAutomationImpl(obj:PopUpButton)
    {
        super(obj);
        obj.addEventListener(DropdownEvent.OPEN, popUpOpenHandler, false, 0, true);
        obj.addEventListener(DropdownEvent.CLOSE, popUpCloseHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get popUpButton():PopUpButton
    {
        return uiComponent as PopUpButton;
    }
    
    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     *  We need to override Button's behavior since we don't want
     *  to use a changing label as our automation name
     *  though we're fine with using it for automationValue.
     */
    override public function get automationName():String
    {
        return (uiComponent as UIComponent).id;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (event is DropdownEvent)
        {
            if (DropdownEvent(event).triggerEvent is KeyboardEvent)
            {
                var kbEvent:KeyboardEvent =
                    new KeyboardEvent(KeyboardEvent.KEY_DOWN);
                kbEvent.keyCode = event.type == DropdownEvent.OPEN ?
                                  Keyboard.DOWN :
                                  Keyboard.UP;
                kbEvent.ctrlKey = true;
                help.replayKeyboardEvent(uiComponent, kbEvent);
            }
            else if (DropdownEvent(event).triggerEvent is MouseEvent)
            {
                if ((event.type == DropdownEvent.OPEN && !popUpButton.isShowingPopUp) ||
                    (event.type == DropdownEvent.CLOSE && popUpButton.isShowingPopUp))
                {
                    var mEvent:MouseEvent = new MouseEvent(MouseEvent.CLICK);
                    mEvent.localX = popUpButton.getUnscaledWidth() - popUpButton.getArrowButtonsWidth();
                    mEvent.localY = popUpButton.getUnscaledHeight() / 2;
                    super.replayAutomatableEvent(mEvent);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                throw new Error();
            }
            
            var completeTime:Number = getTimer() +
                popUpButton.getStyle(DropdownEvent(event).type == DropdownEvent.OPEN ?
                         "openDuration": "closeDuration") as Number;
            
            help.addSynchronization(function():Boolean
            {
                return getTimer() >= completeTime;
            });
            
            return true;
        }
        else 
        {
            return super.replayAutomatableEvent(event);
        }
    }
    
    /**
     *  @private
     */
    override public function createAutomationIDPart(
                            child:IAutomationObject):Object
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.helpCreateIDPart(uiAutomationObject, child);
    }

    /**
     *  @private
     */
    override public function resolveAutomationIDPart(part:Object):Array
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help.helpResolveIDPart(uiAutomationObject, part);
    }

    //----------------------------------
    //  numAutomationChildren
    //----------------------------------

    /**
     *  @private
     */
    override public function get numAutomationChildren():int
    {
        var delegate:IAutomationObject 
            = popUpButton.popUp as IAutomationObject;
        return delegate ? 1 : 0;
    }

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        return popUpButton.popUp as IAutomationObject;
    }

    //----------------------------------
    //  automationTabularData
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationTabularData():Object
    {
        var delegate:IAutomationObject =
                (popUpButton.popUp as IAutomationObject);
        
        return delegate.automationTabularData;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function popUpOpenHandler(event:DropdownEvent):void 
    {
        if (event.triggerEvent)
        {
            recordAutomatableEvent(event);
        }
    }
    
    /**
     *  @private
     */
    private function popUpCloseHandler(event:DropdownEvent):void 
    {
        if (event.triggerEvent)
        {
            recordAutomatableEvent(event);
        }
    }
    
    /**
     *  @private
     */
    override protected function clickHandler(event:MouseEvent):void     
    {
        if(!popUpButton.overArrowButton(event))
            super.clickHandler(event);
    }
}
}