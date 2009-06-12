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
import mx.collections.CursorBookmark;
import mx.collections.IViewCursor;
import mx.controls.ComboBase;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The ComboBaseAccImpl class is the accessibility class for ComboBase.
 *  Since ComboBox inherits from ComboBase,
 *  this class is inherited by ComboBoxAccImpl.
 *
 *  @helpid 3004
 *  @tiptext This is the ComboBase Accessibility Class.
 *  @review
 */
public class ComboBaseAccImpl extends AccImpl
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
	 *  This is used for initializing ComboBaseAccImpl class to hook its
	 *  createAccessibilityImplementation() method to ComboBase class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of ComboBase with the ComboBaseAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		ComboBase.createAccessibilityImplementation =
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
	 */
	private static const ROLE_SYSTEM_LISTITEM:uint = 0x22;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;
	
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
	 */
	private static const EVENT_OBJECT_VALUECHANGE:uint = 0x800E;
	
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
			new ComboBaseAccImpl(component);
	}

	/**
	 *  Method call for enabling accessibility for a component.
	 *  this method is required for the compiler to activate
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
	public function ComboBaseAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x2E;
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
		return super.eventsToHandle.concat([ "change", "valueCommit" ]);
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
	 *  @param childID uint.
	 */
	override public function get_accRole(childID:uint):uint
	{
		if (childID == 0)
			return role;
			
		return ROLE_SYSTEM_LISTITEM;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the value of the ComboBase
	 *  (which would be the text of the item selected).
	 *  The ComboBase should return the content of the TextField as the value.
	 *
	 *  @param childID uint
	 *
	 *  @return Value String
	 *  @review
	 */
	override public function get_accValue(childID:uint):String
	{
		if (childID == 0)
			return ComboBase(master).text;

		return null;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the ListItem
	 *  (basically to remove 'not selected').
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
			accState |= STATE_SYSTEM_SELECTABLE;
		
			if (ComboBase(master).selectedIndex == childID - 1)
				accState |= STATE_SYSTEM_SELECTED | STATE_SYSTEM_FOCUSED;
		}

		return accState;
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
		
		if (ComboBase(master).dataProvider)
		{
			var n:int = ComboBase(master).dataProvider.length;
			for (var i:int = 0; i < n; i++)
			{
				childIDs[i] = i + 1;
			}
		}
		
		return childIDs;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the ComboBase
	 *  For children items, it would add m of n string to the name.
	 *  ComboBase should return the name specified in the AccessibilityProperties.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		if (childID == 0)
			return "";

		var comboBase:ComboBase = ComboBase(master);
		var iterator:IViewCursor = comboBase.collectionIterator;
		iterator.seek(CursorBookmark.FIRST, childID - 1);
		var item:Object = iterator.current;
		
		var mofn:String = " " + childID + " of " + comboBase.dataProvider.length;
		
		if (typeof(item) != "object")
			return item + mofn;
		
		return !item.label ? item.data + mofn : item.label + mofn;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Override the generic event handler.
	 *  All AccImpl must implement this to listen for events
	 *  from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		switch (event.type)
		{
			case "change":
			{
				var index:int = ComboBase(master).selectedIndex;
				
				if (index >= 0)
				{
					var childID:uint = index + 1;
					
					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_SELECTION);
					
					Accessibility.sendEvent(master, 0, 
											EVENT_OBJECT_VALUECHANGE);
				}
				break;
			}

			case "valueCommit":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_VALUECHANGE);
				break;
			}
		}
	}
}

}
