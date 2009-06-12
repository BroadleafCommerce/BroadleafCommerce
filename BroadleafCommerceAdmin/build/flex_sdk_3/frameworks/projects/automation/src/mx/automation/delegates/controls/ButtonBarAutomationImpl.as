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
import mx.controls.ButtonBar;
import mx.core.mx_internal;
import mx.core.EventPriority;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  ButtonBar control.
 * 
 *  @see mx.controls.ButtonBar 
 *
 */
public class ButtonBarAutomationImpl extends NavBarAutomationImpl 
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
            Automation.registerDelegateClass(ButtonBar, ButtonBarAutomationImpl);
    }   

    /**
     *  Constructor.
     * @param obj ButtonBar object to be automated.     
     */
    public function ButtonBarAutomationImpl(obj:ButtonBar)
    {
        super(obj);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get buttonBar():ButtonBar
    {
        return uiComponent as ButtonBar;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function recordAutomatableEvent(
                                event:Event, cacheable:Boolean = false):void
    {
        if (buttonBar.simulatedClickTriggerEvent == null ||
            buttonBar.simulatedClickTriggerEvent is MouseEvent)
        {
            super.recordAutomatableEvent(event, cacheable);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void 
    {
        switch (event.keyCode)
        {
            case Keyboard.DOWN:
            case Keyboard.RIGHT:
            case Keyboard.UP:
            case Keyboard.LEFT:
                recordAutomatableEvent(event);
                break;  
        }
    }

}
}
