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
import mx.controls.Menu;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.MenuEvent;

use namespace mx_internal;

/**
 *  The MenuAccImpl class is the accessibility class for Menu.
 *
 *  @helpid 3007
 *  @tiptext This is the MenuAccImpl Accessibility Class.
 *  @review
 */
public class MenuAccImpl extends ListBaseAccImpl
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
	 *  This is used for initializing MenuAccImpl class to hook its
	 *  createAccessibilityImplementation() method to Menu class
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of Menu with MenuAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		Menu.createAccessibilityImplementation =
			createAccessibilityImplementation;

		return true;
	}

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Role of menuItem
	 */
	private static const ROLE_SYSTEM_MENUITEM:uint = 0x0C;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_CHECKED:uint = 0x00000010;

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
	private static const STATE_SYSTEM_UNAVAILABLE:uint = 0x00000001;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_FOCUS:uint = 0x8005;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006;
	
	/**
	 *  @private
	 */
	private static const EVENT_SYSTEM_MENUPOPUPSTART:uint = 0x00000006;

	/**
	 *  @private
	 */
	private static const EVENT_SYSTEM_MENUPOPUPEND:uint = 0x00000007;

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
			new MenuAccImpl(component);
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
	public function MenuAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x0B;
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
			[ "itemRollOver", "menuShow", "menuHide" ]);
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Gets the role for the component.
	 *
	 *  @param childID children of the component
	 */
	override public function get_accRole(childID:uint):uint
	{
		if (childID == 0)
			return role;

		return ROLE_SYSTEM_MENUITEM;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the value of the MenuItem/Menu
	 *  which is spoken out by the screen reader
	 *  The Menu should return the name of the currently selected item
	 *  with m of n string as value when focus moves to Menu.
	 *
	 *  @param childID uint
	 *
	 *  @return Value String
	 *  @review
	 */
	override public function get_accValue(childID:uint):String
	{
		if (childID > 0)
			return null;

		var accValue:String = "";

		var menu:Menu = Menu(master);
		var selectedIndex:int = menu.selectedIndex;
		if (selectedIndex > -1)
		{
			var item:Object = menu.dataProvider[selectedIndex];

			if (menu.dataDescriptor.isBranch(item))
				accValue = menu.itemToLabel(item);
			else
				accValue = menu.itemToLabel(item);
		}

		return accValue;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the Menu.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon the menuItem being Selected, Selectable,
	 *  Invisible, Offscreen, a value is returned.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 *  @review
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);

		if (childID > 0 && childID < 100000)
		{
			var item:Object = Menu(master).dataProvider[childID - 1];

			if (!Menu(master).dataDescriptor.isEnabled(item))
			{
				accState |= STATE_SYSTEM_UNAVAILABLE;
				return accState;
			}

			//if (Menu(master).dataDescriptor.isFocused(item))
			accState |= STATE_SYSTEM_HOTTRACKED | STATE_SYSTEM_FOCUSED;
			
			if (Menu(master).dataDescriptor.isToggled(item))
				accState |= STATE_SYSTEM_CHECKED;

			if (Menu(master).dataDescriptor.isBranch(item))
				accState |= STATE_SYSTEM_HASPOPUP;
		}
		return accState;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the Default Action.
	 *
	 *  @param childID uint
	 *
	 *  @return focused childID.
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		if (childID == 0)
			return null;

		var item:Object = Menu(master).dataProvider[childID - 1];

		return Menu(master).dataDescriptor.isBranch(item) ? "Open" : "Execute";
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the MenuItem
	 *  which is spoken out by the screen reader
	 *  The MenuItem should return the label as the name
	 *  and Menu should return the name specified in the Accessibility Panel.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		if (childID == 0 || childID > 100000)
			return "";

		var name:String;

		var menu:Menu = Menu(master);
		var item:Object = menu.dataProvider[childID - 1];

		if (menu.dataDescriptor.isBranch(item))
			name = menu.itemToLabel(item);
		else
			name = menu.itemToLabel(item);

		return name;
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
		var index:int = 0;
		var childID:uint;

		switch (event.type)
		{
			case MenuEvent.ITEM_ROLL_OVER:
			{
				index = MenuEvent(event).index;
				if (index >= 0)
				{
					childID = index + 1;

					Accessibility.sendEvent(MenuEvent(event).menu, childID,
											EVENT_OBJECT_FOCUS);

					Accessibility.sendEvent(MenuEvent(event).menu, childID,
											EVENT_OBJECT_SELECTION);
				}
				break;
			}

			case MenuEvent.ITEM_CLICK:
			{
				index = MenuEvent(event).menu.selectedIndex;
				if (index >= 0)
				{
					childID = index + 1;

					Accessibility.sendEvent(MenuEvent(event).menu, childID,
											EVENT_OBJECT_FOCUS);

					Accessibility.sendEvent(MenuEvent(event).menu, childID,
											EVENT_OBJECT_SELECTION);
				}
				break;
			}

			case MenuEvent.MENU_SHOW:
			{
				Accessibility.sendEvent(MenuEvent(event).menu, 0,
										EVENT_SYSTEM_MENUPOPUPSTART);
				break;
			}

			case MenuEvent.MENU_HIDE:
			{
				Accessibility.sendEvent(MenuEvent(event).menu, 0,
										EVENT_SYSTEM_MENUPOPUPEND);
				break;
			}
		}
	}
}

}
