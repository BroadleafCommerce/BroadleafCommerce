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
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;
import flash.utils.getTimer;
import mx.automation.Automation;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.delegates.core.ContainerAutomationImpl;
import mx.containers.Accordion;
import mx.core.mx_internal;
import mx.core.EventPriority;
import mx.events.ChildExistenceChangedEvent;
import mx.events.IndexChangedEvent;
import mx.controls.Button;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines the methods and properties required to perform instrumentation for the 
 *  Accordion class. 
 * 
 *  @see mx.containers.Accordion
 *  
 */
public class AccordionAutomationImpl extends ContainerAutomationImpl 
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
        Automation.registerDelegateClass(Accordion, AccordionAutomationImpl);
    }   
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj Accordion object to be automated.
     */
    public function AccordionAutomationImpl(obj:Accordion)
    {
        super(obj);
        
        obj.addEventListener(KeyboardEvent.KEY_DOWN, accordionKeyDownHandler, false, EventPriority.DEFAULT+1, true);
        
        obj.addEventListener(IndexChangedEvent.CHANGE, indexChangeHandler, false, 0, true);
    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get accr():Accordion
    {
        return uiComponent as Accordion;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     * Replays an <code>IndexChangedEvent</code> event by dispatching
     * a <code>MouseEvent</code> to the header that was clicked.
     *  
     *  @param event The event to replay.
     *  
     *  @return <code>true</code> if the replay was successful. Otherwise, returns <code>false</code>.
     */
    override public function replayAutomatableEvent(event:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        var completeTime:Number = getTimer() + accr.getStyle("openDuration");
        if (event is IndexChangedEvent)
        {
            help.addSynchronization(function():Boolean
            {
                return getTimer() >= completeTime;
            });
            var ice:IndexChangedEvent = IndexChangedEvent(event);
            var child:DisplayObject = ice.relatedObject;
            var header:Button = accr.getHeaderAt(accr.getChildIndex(child));
            var ao:IAutomationObject = header as IAutomationObject;
            return ao.replayAutomatableEvent(new MouseEvent(MouseEvent.CLICK));
        }
        else if (event is KeyboardEvent)
        {
            var keyEvent:KeyboardEvent = KeyboardEvent(event);
            
            if (keyEvent.keyCode == Keyboard.PAGE_UP ||
                keyEvent.keyCode == Keyboard.PAGE_DOWN ||
                keyEvent.keyCode == Keyboard.HOME ||
                keyEvent.keyCode == Keyboard.END ||
                keyEvent.keyCode == Keyboard.SPACE ||
                keyEvent.keyCode == Keyboard.ENTER)
            {               
                help.addSynchronization(function():Boolean
                {
                    return getTimer() >= completeTime;
                });
            }
            return help.replayKeyboardEvent(uiComponent, keyEvent);
        }    
        else
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
    private function accordionKeyDownHandler(event:KeyboardEvent):void 
    {
        // Only listen for events that have come from the accordion itself.
        if (event.target != accr)
            return;
   
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
    
    /**
     *  @private
     */
    private function indexChangeHandler(event:IndexChangedEvent):void
    {
        if (event.triggerEvent is MouseEvent)
            recordAutomatableEvent(event, false);
        else
            recordAutomatableEvent(event.triggerEvent, false);
    }

    /**
     *  @private
     *  Prevent duplicate ENTER key recordings. 
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        ;
    }
        
}
}