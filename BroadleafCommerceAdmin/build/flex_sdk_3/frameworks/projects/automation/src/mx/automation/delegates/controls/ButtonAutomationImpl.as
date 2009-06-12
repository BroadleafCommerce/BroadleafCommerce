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
import flash.events.MouseEvent;
import flash.events.KeyboardEvent;
import flash.ui.Keyboard;
import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.Button;
import mx.core.EventPriority;
import mx.core.mx_internal;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  Button control.
 * 
 *  @see mx.controls.Button 
 *
 */
public class ButtonAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(Button, ButtonAutomationImpl);
    }   

    /**
     *  Constructor.
     * @param obj Button object to be automated.     
     */
    public function ButtonAutomationImpl(obj:Button)
    {
        super(obj);

        obj.addEventListener(KeyboardEvent.KEY_UP, btnKeyUpHandler, false, EventPriority.DEFAULT+1, true);          
        obj.addEventListener(MouseEvent.CLICK, clickHandler, false, EventPriority.DEFAULT+1, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get btn():Button
    {
        return uiComponent as Button;
    }

    /**
     *  @private
     */
    private var ignoreReplayableClick:Boolean;

    //----------------------------------
    //  automationName
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationName():String
    {
        return btn.label || btn.toolTip || super.automationName;
    }

    //----------------------------------
    //  automationValue
    //----------------------------------

    /**
     *  @private
     */
    override public function get automationValue():Array
    {
        return [ btn.label || btn.toolTip ];
    }

    /**
     *  @private
     */
    protected function clickHandler(event:MouseEvent):void 
    {
        if (!ignoreReplayableClick)
            recordAutomatableEvent(event);
        ignoreReplayableClick = false;
    }
    
    /**
     *  @private
     */
    private function btnKeyUpHandler(event:KeyboardEvent):void 
    {
        if (!btn.enabled)
            return;

        if (event.keyCode == Keyboard.SPACE)
        {
            // we need to ignore recording a click being dispatched here
            ignoreReplayableClick = true;
            recordAutomatableEvent(event);
        }
    }


    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

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
        
        if (event is MouseEvent && event.type == MouseEvent.CLICK)
            return help.replayClick(uiComponent, MouseEvent(event));
        else if (event is KeyboardEvent)
            return help.replayKeyboardEvent(uiComponent, KeyboardEvent(event));
        else
            return super.replayAutomatableEvent(event);
    }
    
}

}