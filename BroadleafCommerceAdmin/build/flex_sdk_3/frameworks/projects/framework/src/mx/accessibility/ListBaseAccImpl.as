////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
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
import mx.collections.CursorBookmark;
import mx.collections.IViewCursor;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The ListBaseAccImpl class is the accessibility class
 *  for ListBase.
 *  Since List inherits from ListBase,
 *  this class is used in ListAccImpl as well.
 *
 *  @helpid 3009
 *  @tiptext This is the ListBase Accessibility Class.
 *  @review
 */
public class ListBaseAccImpl extends AccImpl
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
	 *  This is used for initializing ListBaseAccImpl class to hook its
	 *  createAccessibilityImplementation() method to ListBase class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of ListBase with the ListBaseAccImpl class.
	 */ 
	private static function hookAccessibility():Boolean
	{
		ListBase.createAccessibilityImplementation =
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
	 *  Role of listItem.
	 */
	private static const ROLE_SYSTEM_LISTITEM:uint = 0x22; 
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_INVISIBLE:uint = 0x00008000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_OFFSCREEN:uint = 0x00010000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTABLE:uint = 0x00200000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTED:uint = 0x00000002;
	
	/**
	 *  @private
	 *  Event emitted if 1 item is selected.
	 */
	private static const EVENT_OBJECT_FOCUS:uint = 0x8005; 
	
	/**
	 *  @private
	 *  Event emitted if 1 item is selected.
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
			new ListBaseAccImpl(component);
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
	public function ListBaseAccImpl(master:UIComponent)
	{
		super(master);
		
		role = 0x21;
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
		return super.eventsToHandle.concat([ "change" ]);
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
			
		return ROLE_SYSTEM_LISTITEM;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the value of the ListItem/ListBox
	 *  which is spoken out by the screen reader
	 *  The listBox should return the name of the currently selected item
	 *  with m of n string as value when focus moves to list box.
	 *
	 *  @param childID uint
	 *
	 *  @return Value String
	 *  @review
	 */
	override public function get_accValue(childID:uint):String
	{
		var accValue:String;
		
		var listBase:ListBase = ListBase(master);
		
		var index:int = listBase.selectedIndex;
		if (childID == 0)
		{
			if (index > -1)
			{
				var item:Object = getItemAt(index);

				if (item is String)
				{
					accValue = item + " " + (index + 1) + " of " + listBase.dataProvider.length;
				}
				else
				{
					accValue = listBase.itemToLabel(item) + " " + (index + 1) +
							   " of " + listBase.dataProvider.length;
				}
			}
		}
		/*
		else
		{
			if (index > -1)
			{
				accValue = (listBase.selectedIndex + 1) + "";
			}
		}
		*/

		return accValue;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the ListItem.
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
			var listBase:ListBase = ListBase(master);

			var index:uint = childID - 1;

			// For returning states (OffScreen and Invisible)
			// when the list Item is not in the displayed rows.
			if (index < listBase.verticalScrollPosition ||
				index >= listBase.verticalScrollPosition + listBase.rowCount)
			{
				accState |= (STATE_SYSTEM_OFFSCREEN |
							 STATE_SYSTEM_INVISIBLE);
			}
			else
			{
				accState |= STATE_SYSTEM_SELECTABLE;

				var item:Object = getItemAt(index);

				var renderer:IListItemRenderer =
					listBase.itemToItemRenderer(item);

				if (renderer != null && listBase.isItemSelected(renderer.data))
					accState |= STATE_SYSTEM_SELECTED | STATE_SYSTEM_FOCUSED;
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
	 *  @return DefaultAction String
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		if (childID == 0)
			return null;

		return "Double Click";
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
			ListBase(master).selectedIndex = childID - 1;
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

		if (ListBase(master).dataProvider)
		{
			var n:uint = ListBase(master).dataProvider.length;
			for (var i:int = 0; i < n; i++)
			{
				childIDs[i] = i + 1;
			}
		}
		return childIDs;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the bounding box of the ListItem.
	 *
	 *  @param childID uint
	 *
	 *  @return Location Object
	 */
	override public function accLocation(childID:uint):*
	{
		var listBase:ListBase = ListBase(master);

		var index:uint = childID - 1;
		
		if (index < listBase.verticalScrollPosition ||
			index >= listBase.verticalScrollPosition + listBase.rowCount)
		{
			return null;
		}
		var item:Object = getItemAt(index);

		return listBase.itemToItemRenderer(item);
	}

	/**
	 *  @private
	 *  IAccessible method for returning the child Selections in the List.
	 *
	 *  @param childID uint
	 *
	 *  @return focused childID.
	 */
	override public function get_accSelection():Array
	{
		var accSelection:Array = [];

		var selectedIndices:Array = ListBase(master).selectedIndices;
		
		var n:int = selectedIndices.length;
		for (var i:int = 0; i < n; i++)
		{
			accSelection[i] = selectedIndices[i] + 1;
		}
		
		return accSelection;
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
		var index:uint = ListBase(master).selectedIndex;
		
		return index >= 0 ? index + 1 : 0;
	}

	/**
	 *  @private
	 *  IAccessible method for selecting an item.
	 *
	 *  @param childID uint
	 */
	override public function accSelect(selFlag:uint, childID:uint):void
	{
		var listBase:ListBase = ListBase(master);

		var index:uint = childID - 1;
		
		if (index >= 0 && index < listBase.dataProvider.length)
			listBase.selectedIndex = index;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

 	/**
	 *  @private
	 *  method for returning the name of the ListItem/ListBox
	 *  which is spoken out by the screen reader.
	 *  The ListItem should return the label as the name
	 *  with m of n string and ListBox should return the name
	 *  specified in the Accessibility Panel.
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
		
		var listBase:ListBase = ListBase(master);
		var item:Object = getItemAt(childID - 1);
		
		// Assuming childID is always ItemID + 1
		// because getChildIDArray is not always invoked.
		
		// Sometimes item may be an object.
		if (item is String)
		{
			return item + " " + childID + " of " + listBase.dataProvider.length;
		}
		else
		{
			return listBase.itemToLabel(item) + " " + childID +
				   " of " + listBase.dataProvider.length;
		}
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
			case "change":
			{
				var index:uint = ListBase(master).selectedIndex;
				
				if (index >= 0)
				{
					var childID:uint = index + 1;

					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_FOCUS);

					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_SELECTION);
				}
			}
			break;
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function getItemAt(index:int):Object
	{
		var iterator:IViewCursor = ListBase(master).collectionIterator;
		iterator.seek(CursorBookmark.FIRST, index);
		return iterator.current;
	}

}

}
