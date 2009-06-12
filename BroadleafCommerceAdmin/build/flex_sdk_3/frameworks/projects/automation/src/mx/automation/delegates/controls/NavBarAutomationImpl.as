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
import mx.automation.Automation;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.AutomationRecordEvent;
import mx.automation.delegates.core.ContainerAutomationImpl;
import mx.controls.NavBar;
import mx.core.mx_internal;
import mx.events.ItemClickEvent;
import mx.core.EventPriority;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  NavBar control.
 * 
 *  @see mx.controls.NavBar 
 *
 */
public class NavBarAutomationImpl extends ContainerAutomationImpl 
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
        Automation.registerDelegateClass(NavBar, NavBarAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj NavBar object to be automated.     
     */
    public function NavBarAutomationImpl(obj:NavBar)
    {
        super(obj);

        recordClick = false;
        
        obj.addEventListener(AutomationRecordEvent.RECORD, automationRecordHandler, false, EventPriority.DEFAULT+1, true);
        
        obj.addEventListener(ItemClickEvent.ITEM_CLICK, itemClickHandler, false, 0, true);

    }

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get  nBar():NavBar
    {
        return uiComponent as NavBar;
    }
    
    /**
     *  @private
     *  Replays <code>click</code> events by dispatching a MouseEvent
     *  to the item that was clicked.
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (interaction is ItemClickEvent)
        {
            var itemClickInteraction:ItemClickEvent =
                ItemClickEvent(interaction);
            if (itemClickInteraction.relatedObject != null)
            {
                
                return help.replayClick(itemClickInteraction.relatedObject);
            }
            else
                return false;
        }
        else if (interaction is KeyboardEvent)
            return help.replayKeyboardEvent(uiComponent, KeyboardEvent(interaction));
        else
            return super.replayAutomatableEvent(interaction);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function automationRecordHandler(event:AutomationRecordEvent):void
    {
        if (event.replayableEvent.type == MouseEvent.CLICK)
            event.stopImmediatePropagation();
    }

    /**
     *  @private
     */
    protected function itemClickHandler(event:ItemClickEvent):void
    {
        recordAutomatableEvent(event);
    }

    /**
     * @private
     */
    public function getItemsCount():int
    {
        if (nBar.dataProvider)
            return nBar.dataProvider.length;
        
        return 0;
    }
        
}

}