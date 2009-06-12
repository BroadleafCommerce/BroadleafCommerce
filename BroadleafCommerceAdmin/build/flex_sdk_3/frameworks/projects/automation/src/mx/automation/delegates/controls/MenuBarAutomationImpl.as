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

import mx.automation.Automation;
import mx.automation.AutomationIDPart;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationObjectHelper;
import mx.automation.tabularData.MenuBarTabularData;
import mx.automation.delegates.core.UIComponentAutomationImpl;
import mx.controls.Menu;
import mx.controls.MenuBar;
import mx.controls.menuClasses.MenuBarItem;
import mx.core.mx_internal;
import mx.events.MenuEvent;
import mx.automation.events.MenuShowEvent;
import mx.controls.menuClasses.IMenuBarItemRenderer;
import mx.core.EventPriority;
import mx.core.UIComponent;

use namespace mx_internal;

[Mixin]

/**
 *  Defines methods and properties required to perform instrumentation for the 
 *  MenuBar control.
 * 
 *  @see mx.controls.MenuBar 
 */
public class MenuBarAutomationImpl extends UIComponentAutomationImpl 
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
        Automation.registerDelegateClass(MenuBar, MenuBarAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param obj MenuBar object to be automated.     
     */
    public function MenuBarAutomationImpl(obj:MenuBar)
    {
        super(obj);
        
        obj.addEventListener(MenuEvent.MENU_SHOW, menuShowHandler,
                             false, 0, true);
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  menuBar
    //----------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get menuBar():MenuBar
    {
        return uiComponent as MenuBar;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function createAutomationIDPart(
                                    child:IAutomationObject):Object
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help ? help.helpCreateIDPart(uiAutomationObject, child) : null;
    }

    /**
     *  @private
     */
    override public function resolveAutomationIDPart(part:Object):Array
    {
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        return help ? help.helpResolveIDPart(uiAutomationObject, part) : null;
    }

    /**
     *  @private
     */
    override public function get numAutomationChildren():int
    {
        var itemCount:int = menuBar.menuBarItems.length;
        
        // add menus present
        var menuCount:int = 0;
        for (var i:int = 0; i < menuBar.menus.length; ++i)
        {
            if (menuBar.menus[i])
                ++menuCount;
        }
            
        return itemCount + menuCount;
    }

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        if (index < menuBar.menuBarItems.length)
            return menuBar.menuBarItems[index] as IAutomationObject;

        var menuIndex:int = index - menuBar.menuBarItems.length;
        
        // count the menus present and match it with the index
        var menuCount:int = 0;
        var i:int;
        for (i = 0; i < menuBar.menus.length; ++i)
        {   if (menuBar.menus[i])
            {
                if (menuCount == menuIndex)
                    break;
                ++menuCount;
            }
        }
                
        return menuBar.menus[i] as IAutomationObject;
    }

    /**
     *  @private
     */
    override public function get automationTabularData():Object
    {
        return new MenuBarTabularData(uiAutomationObject);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function menuShowHandler(event:MenuEvent):void
    {
        // if menu is having a parent menu showing is recorded
        // by the parent. 
        if (event.menu.parentMenu)
            return;

        if (event.target == uiComponent)
        {
            var itemRenderer:IMenuBarItemRenderer;
            var menus:Array = menuBar.menus;
            for (var i:int = 0; i < menus.length; ++i)
            {
                if (menus[i] == event.menu)
                {
                    itemRenderer = menus[i].sourceMenuBarItem;
                    break;
                }
            }

            if (itemRenderer)
            {   
                var msEvent:MenuShowEvent = new MenuShowEvent(MenuShowEvent.MENU_SHOW, itemRenderer);
                recordAutomatableEvent(msEvent);
            }
        }
    }
    
    /**
     *  @private
     *  Replays the event specified by the parameter if possible.
     *
     *  @param interaction The event to replay.
     * 
     *  @return Whether or not a replay was successful.
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        if (interaction is MenuShowEvent)
        {
            var me:MenuShowEvent = MenuShowEvent(interaction);
            switch (interaction.type)
            {
                case MenuShowEvent.MENU_SHOW:
                {
                    var menuBarItem:UIComponent = me.itemRenderer as UIComponent;
                    menuBarItem.dispatchEvent(new MouseEvent(MouseEvent.MOUSE_OVER));
                    if (menuBar.selectedIndex == -1)
                    {
                        menuBarItem.dispatchEvent(new MouseEvent(MouseEvent.MOUSE_DOWN));
                    }
                    return true;
                }
            }
        }

        return super.replayAutomatableEvent(interaction);
    }
    
}

}
