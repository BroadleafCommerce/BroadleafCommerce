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
import mx.automation.IAutomationObject;
import mx.automation.IAutomationManager;
import mx.automation.IAutomationObjectHelper;
import mx.automation.events.ListItemSelectEvent;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.menuClasses.IMenuItemRenderer;
import mx.controls.Menu;
import mx.core.Application;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.events.MenuEvent;

use namespace mx_internal;

[Mixin]
/**
 * 
 *  Defines methods and properties required to perform instrumentation for the 
 *  Menu control.
 * 
 *  @see mx.controls.Menu 
 *
 */
public class MenuAutomationImpl extends ListAutomationImpl {

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
        Automation.registerDelegateClass(Menu, MenuAutomationImpl);
    }   

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     * @param obj Menu object to be automated.     
     */
    public function MenuAutomationImpl(obj:Menu)
    {
        super(obj);
        
        var rootMenu:Menu = obj.getRootMenu();
        // attach event listeners only on the root menu
        if(rootMenu == obj)
        {
            rootMenu.addEventListener(MenuEvent.ITEM_CLICK, menuItemClickHandler, false, 0, true);
            rootMenu.addEventListener(MenuEvent.MENU_SHOW, menuShowHandler, false, 0, true);
            rootMenu.addEventListener(MenuEvent.MENU_HIDE, menuHideHandler, false, EventPriority+1, true);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  storage for the owner component
     */
    protected function get  menu():Menu
    {
        return uiComponent as Menu;
    }

    //----------------------------------
    //  dontRecordShow
    //----------------------------------
    
    /**
     *  Flag indicating whehter to record the show event or not.
     *  We should use triggerEvent property on MenuEvent.
     */
    public var showHideFromKeys:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function get automationName():String
    {
        var menuItem:IAutomationObject = associatedMenuItem;
        if (menuItem != null && menuItem.automationName != null)
            return menuItem.automationName;
        else
            return super.automationName;
    }

    /**
     *  @private
     */
   override public function get automationValue():Array
    {
        var menuItem:IAutomationObject = associatedMenuItem;
        if (menuItem != null && menuItem.automationName != null)
            return [menuItem.automationName];
        else
            return super.automationValue;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function getMenusRenderer(menu:Menu):IListItemRenderer
    {
        var itemRenderer:IListItemRenderer;
        var delegate:MenuAutomationImpl = menu.automationDelegate as MenuAutomationImpl;
        if (delegate)
        {
            var itemDelegate:IAutomationObject = delegate.associatedMenuItem;
            if (itemDelegate)
                itemRenderer = itemDelegate as IListItemRenderer;           
        }
        return itemRenderer;
    }

    /**
     *  @private
     */
    private function setEventsRenderer(menuEvent:MenuEvent):Boolean
    {
        if (!menuEvent.itemRenderer)
        {
            menuEvent.itemRenderer = getMenusRenderer(menuEvent.menu);
        }
        
        if(menuEvent.itemRenderer)
            return true;
        return false;
    }


    /**
     *  @private
     */
    private function recordAutomatableMenuEvent(type:String,
                                                source:Menu,
                                                renderer:Object = null,
                                                label:String = null, 
                                                item:Object = null
                                                ):void
    {
        var am:IAutomationManager = Automation.automationManager;
        if (am) //&& am.recording)
        {
            var menuEvent:MenuEvent = new MenuEvent(type);
            menuEvent.menu = source;
            menuEvent.label = label;
            menuEvent.item = item;
            menuEvent.itemRenderer = IListItemRenderer(renderer);
            
            recordAutomatableEvent(menuEvent);
        }
    }

    /**
     *  @private
     */
    private function get associatedMenuItem():IAutomationObject
    {
        var parentMenu:Menu = menu.parentMenu;
        if (parentMenu)
        {
            var listItems:Array = parentMenu.rendererArray;
            // do this search column-major, since unless it's a grid we should
            // find the child in the first column
            for (var col:int = 0; col < listItems[0].length; col++)
            {
                for (var vOffset:int = 0; vOffset < listItems.length; vOffset++)
                {
                    if ((listItems[vOffset][col] as IMenuItemRenderer).menu == menu)
                    {
                        var obj:Object = listItems[vOffset][col];
                        return obj as IAutomationObject;
                    }    
                }
            }
            return null;
        }
        else if (menu.sourceMenuBarItem)
            return menu.sourceMenuBarItem as IAutomationObject;
        else
            return null;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function getAutomationChildAt(index:int):IAutomationObject
    {
        var result:IAutomationObject = super.getAutomationChildAt(index);
        var menu:Menu = IMenuItemRenderer(result).menu;
        var delegate:IAutomationObject = menu as IAutomationObject;
        return (delegate || result);
    }

    /**
     *  @private
     */
    override public function recordAutomatableEvent(event:Event,
                                                    cacheable:Boolean = false):void
    {
        // don't dispatch select events.  we dispatch our own change events.
        if (! (event is ListItemSelectEvent))
            super.recordAutomatableEvent(event, cacheable);
    }

    /**
     * @private
     * Replays ITEM_CLICK, [sub]MENU_SHOW, and MENU_HIDE events. CHANGE is
     * replayed as a mouse up on the item in question. MENU_SHOW is replayed
     * as a mouse over on the item of this menu that spawns the submenu in
     * question. MENU_HIDE is replayed by calling hide().
     */
    override public function replayAutomatableEvent(interaction:Event):Boolean
    {
        var completeTime:Number;
        var help:IAutomationObjectHelper = Automation.automationObjectHelper;
        if (interaction is MenuEvent)
        {
            var me:MenuEvent = MenuEvent(interaction);
            var target:IListItemRenderer = me.itemRenderer;
            var mouseEvent:MouseEvent;
            switch (interaction.type)
            {
            case MenuEvent.ITEM_CLICK:
            {
                // replay as mouse up
                //                mouseEvent = new MouseEvent(MouseEvent.MOUSE_UP);
                // dispatch the event

                completeTime = getTimer() +
                    menu.getStyle("selectionDuration") as Number;
                help.addSynchronization(function():Boolean
                {
                    return getTimer() >= completeTime;
                });
                return help.replayClick(target);
            }

            case MenuEvent.MENU_HIDE:
            {
                menu.hide();
                return true;
            }

            case MenuEvent.MENU_SHOW:
            {

                completeTime = getTimer() +
                    menu.getStyle("openDuration") as Number;
                help.addSynchronization(function():Boolean
                {
                    return getTimer() >= completeTime;
                });

                // don't have to worry about non-submenus since they either come
                // from a MenuBar, which has its own replay, or from
                // programmatic calls, which we don't need to replay

                // replay as mouse over on the item that spawns this menu
                mouseEvent = new MouseEvent(MouseEvent.MOUSE_OVER);
                var item:IListItemRenderer =
                    (target is Menu
                     ? getMenusRenderer(target as Menu)
                     : target) as IListItemRenderer;
                return item.dispatchEvent(mouseEvent);
            }

            default:
                return false;
            }
        }
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
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        var delegate:MenuAutomationImpl
            = menu.getRootMenu().automationDelegate as MenuAutomationImpl;
        if (event.keyCode == Keyboard.LEFT ||
                event.keyCode == Keyboard.RIGHT)
        {
            recordAutomatableEvent(event);
            delegate.showHideFromKeys = true;
        }
        else if (event.keyCode == Keyboard.ESCAPE)
        {
            delegate.showHideFromKeys = true;
            recordAutomatableEvent(event);
        }
        else if (event.keyCode == Keyboard.UP ||
                event.keyCode == Keyboard.DOWN)
        {
            recordAutomatableEvent(event);
        }   
        else if (event.keyCode == Keyboard.TAB)
        {
            recordAutomatableMenuEvent(MenuEvent.MENU_HIDE, 
                        menu.getRootMenu(), getMenusRenderer(menu));
        }
    }

    /**
     *  @private
     */
    private function menuItemClickHandler(menuEvent:MenuEvent):void
    {
        if (menuEvent.index != -1)
        {
            var delegate:IAutomationObject = menuEvent.menu as IAutomationObject;
            var am:IAutomationManager = Automation.automationManager;
            am.recordAutomatableEvent(delegate,menuEvent);  
        }
    }
    
    private function menuHideHandler(menuEvent:MenuEvent):void
    {
        var delegate:MenuAutomationImpl
            = (menu.getRootMenu().automationDelegate) as MenuAutomationImpl;
    
        if (delegate.showHideFromKeys)
        {
            delegate.showHideFromKeys = false;
            return;
        }

        if (!setEventsRenderer(menuEvent))
            return;
            
        if (menuEvent.menu)
        {
            var am:IAutomationManager = Automation.automationManager;
            am.recordAutomatableEvent(menuEvent.menu, menuEvent, true);
        }
    }

    /**
     *  @private
     */
    private function menuShowHandler(menuEvent:MenuEvent):void
    {
        var delegate:MenuAutomationImpl
            = (menu.getRootMenu().automationDelegate) as MenuAutomationImpl;
    
        if (delegate.showHideFromKeys)
        {
            delegate.showHideFromKeys = false;
            return;
        }
        
        if (!setEventsRenderer(menuEvent))
            return;

        // if menu doesn't have a parent menu the 
        // MenuBar will record the show event
        if (menuEvent.menu.parentMenu)
        {
            var replayer:IAutomationObject = (menuEvent.menu.parentMenu as IAutomationObject);
            var am:IAutomationManager = Automation.automationManager;
            am.recordAutomatableEvent(replayer, menuEvent);
        }
        
    }
}
}
