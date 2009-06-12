////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.accessibility
{

import flash.accessibility.Accessibility;
import flash.events.Event;
import mx.controls.MenuBar;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.MenuEvent;

use namespace mx_internal;

/**
 *  The MenuBarAccImpl class is the accessibility class for MenuBar.
 *
 *  @helpid 3007
 *  @tiptext This is the MenuBarAccImpl Accessibility Class.
 *  @review
 */
public class MenuBarAccImpl extends AccImpl
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class initialization
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Static variable triggering the hookAccessibility() method.
	 *  This is used for initializing MenuBarAccImpl class to hook its
	 *  createAccessibilityImplementation() method to MenuBar class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of MenuBar with the MenuBarAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		MenuBar.createAccessibilityImplementation =
			createAccessibilityImplementation;

		return true;
	}

	//--------------------------------------------------------------------------
	//
	//  Constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Role of menuItem.
	 */
	private static const ROLE_SYSTEM_MENUITEM:uint = 0x0C;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSABLE:uint = 0x00100000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_HASPOPUP:uint = 0x40000000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_HOTTRACKED:uint = 0x00000080;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTABLE:uint = 0x00200000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_UNAVAILABLE:uint = 0x00000001;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_FOCUS:uint = 0x8005;
	
	/**
	 *  @private
	 */
	private static const EVENT_SYSTEM_MENUEND:uint = 0x00000005;

	/**
	 *  @private
	 */
	private static const EVENT_SYSTEM_MENUSTART:uint = 0x00000004;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006;
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Method for creating the Accessibility class.
	 *  This method is called from UIComponent.
	 *  @review
	 */
	mx_internal static function createAccessibilityImplementation(
								component:UIComponent):void
	{
		component.accessibilityImplementation =
			new MenuBarAccImpl(component);
	}

	/**
	 *  Method call for enabling accessibility for a component.
	 *  This method is required for the compiler to activate
	 *  the accessibility classes for a component.
	 */
	public static function enableAccessibility():void
	{
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param master The UIComponent instance that this AccImpl instance
	 *  is making accessible.
	 */
	public function MenuBarAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x02;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties: AccImpl
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  eventsToHandle
	//----------------------------------

	/**
	 *  @private
	 *	Array of events that we should listen for from the master component.
	 */
	override protected function get eventsToHandle():Array
	{
		return super.eventsToHandle.concat(
			[ "menuShow", "menuHide", "focusIn", "focusOut" ]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  Gets the role for the component.
	 *
	 *  @param childID uint
	 */
	override public function get_accRole(childID:uint):uint
	{
		if (childID == 0)
			return role;
			
		return ROLE_SYSTEM_MENUITEM;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the MenuItem.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon the listItem being Selected, Selectable,
	 *  Invisible, Offscreen, a value is returned.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		
		if (childID > 0)
		{
			var menuBar:MenuBar = MenuBar(master);

			var index:int = childID - 1;

			if (!menuBar.menuBarItems[index] || !menuBar.menuBarItems[index].enabled)
			{
				accState |= STATE_SYSTEM_UNAVAILABLE;
			}
			else
			{
				accState |= STATE_SYSTEM_SELECTABLE | STATE_SYSTEM_FOCUSABLE;
				
				// if (menuBar.getMenuAt(index))
				accState |= STATE_SYSTEM_HASPOPUP;

				if (index == menuBar.selectedIndex)
					accState |= STATE_SYSTEM_HOTTRACKED | STATE_SYSTEM_FOCUSED;
			}
		}
		return accState;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the Default Action.
	 *
	 *  @param childID uint
	 *
	 *  @return name of default action.
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		if (childID == 0)
			return null;
		
		return childID - 1 == MenuBar(master).selectedIndex ? "Close" : "Open";
	}

	/**
	 *  @private
	 *  IAccessible method for executing the Default Action.
	 *
	 *  @param childID uint
	 */
	override public function accDoDefaultAction(childID:uint):void
	{
		if (childID > 0)
		{
			var index:int = childID - 1;
			//MenuBar(master).selectedIndex = index;
			//MenuBar(master).showMenu(index);
		}
	}

 	/**
	 *  @private
	 *  Method to return an array of childIDs.
	 *
	 *  @return Array
	 */
	override public function getChildIDArray():Array
	{
		var childIDs:Array = [];
		
		if (MenuBar(master).menuBarItems)
		{
			var n:int = MenuBar(master).menuBarItems.length;
			for (var i:int = 0; i < n; i++)
			{
				childIDs[i] = i + 1;
			}
		}
		return childIDs;
	}

 	/**
	 *  @private
	 *  IAccessible method for returning the bounding box of the MenuBarItem.
	 *
	 *  @param childID uint
	 *
	 *  @return Location Object
	 */
	override public function accLocation(childID:uint):*
	{
		//should check that this is returning the needed component
		return MenuBar(master).menuBarItems[childID - 1];
		//return MenuBar(master).getMenuBarItemAt(childID - 1);
	}

	/**
	 *  @private
	 *  IAccessible method for returning the childFocus of the List.
	 *
	 *  @param childID uint
	 *
	 *  @return focused childID.
	 */
	override public function get_accFocus():uint
	{
		var index:int = MenuBar(master).selectedIndex;
		
		return index >= 0 ? index + 1 : 0;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the name of the MenuBar
	 *  which is spoken out by the screen reader.
	 *  The MenuItem should return the label as the name
	 *  and MenuBar should return the name specified in the Accessibility Panel.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 */
	override protected function getName(childID:uint):String
	{
		if (childID == 0)
			return "";
		var menuBar:MenuBar = MenuBar(master);
		var index:int = childID - 1;
		
		if (menuBar.menuBarItems && menuBar.menuBarItems.length > index)
		{
			if (menuBar.menuBarItems[index] && menuBar.menuBarItems[index].data)
				return menuBar.itemToLabel(menuBar.menuBarItems[index].data);
		}
		return "";
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Override the generic event handler.
	 *  All AccImpl must implement this
	 *  to listen for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		switch (event.type)
		{
			case "menuShow":
			{
				var index:int = MenuBar(master).selectedIndex;
				
				// since all the menu events are also received by Menubar.
				if (index >= 0 && !MenuEvent(event).menu.parentMenu)
				{
					var childID:uint = index + 1;

					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_FOCUS);

					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_SELECTION);
				}

				break;
			}

			case "menuHide":
			{
				if (!MenuEvent(event).menu.parentMenu)
					Accessibility.sendEvent(master, 0, EVENT_SYSTEM_MENUEND);
				break;
			}

			case "focusIn":
			{
				Accessibility.sendEvent(master, 0, EVENT_SYSTEM_MENUSTART);
				break;
			}
			
			case "focusOut":
			{
				if (MenuBar(master).selectedIndex == -1)
					Accessibility.sendEvent(master, 0, EVENT_SYSTEM_MENUEND);
				break;
			}
		}
	}
}

}
