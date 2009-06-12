////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
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
import mx.controls.ColorPicker;
import mx.controls.colorPickerClasses.SwatchPanel;
import mx.controls.ComboBase;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.ColorPickerEvent;
import mx.events.DropdownEvent;
import mx.skins.halo.SwatchSkin;

use namespace mx_internal;
	
public class ColorPickerAccImpl extends ComboBaseAccImpl
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
	 *  This is used for initializing DateChooserAccImpl class to hook its
	 *  createAccessibilityImplementation() method to DateChooser class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of Slider withthe SliderAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		ColorPicker.createAccessibilityImplementation =
			createAccessibilityImplementation;

		return true;
	}
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_VALUECHANGE:uint = 0x800E;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006;
	
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
			new ColorPickerAccImpl(component);
	}

	/**
	 *  Method call for enabling accessibility for a component.
	 *  This method is required for the compiler to activate
	 *  the accessibility classes for a component.
	 */
	public static function enableAccessibility():void
	{
		//SwatchPanelAccImpl.enableAccessibility();
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
	
	public function ColorPickerAccImpl(master:UIComponent)
	{
		super(master);
		master.accessibilityProperties.description = "Color Picker";
		Accessibility.updateProperties();
		ColorPicker(master).addEventListener(DropdownEvent.OPEN, openHandler);
		ColorPicker(master).addEventListener(DropdownEvent.CLOSE, closeHandler);
		//role = 0x2E;
	}
	
	private function openHandler(event:Event):void
	{
		ColorPicker(master).dropdown.addEventListener("change",  dropdown_changeHandler);
	}
	private function closeHandler(event:Event):void
	{
		ColorPicker(master).dropdown.removeEventListener("change",  dropdown_changeHandler);
	}
	
	private function dropdown_changeHandler(event:Event):void
	{
		master.dispatchEvent(new Event("childChange"));
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the ComboBase
	 *  For children items (i. e. ColorSwatch colors), it returns the digits if the hex
	 *  color. We add a space between each digit to force the screen reader to read it
	 *  as a series of text, not a number (e.g. #009900 is "zero, zero, nine, nine, zero, zero",
	 *  not "nine thousand, nine hundred".
	 *  
	 *  ComboBase should return the name specified in the AccessibilityProperties.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		if (childID == 0 || childID > 0xFFF)
			return "";

		var colorPicker:ColorPicker = ColorPicker(master);
		var iterator:IViewCursor = colorPicker.collectionIterator;
		iterator.seek(CursorBookmark.FIRST, childID - 1);
		var item:Object = iterator.current;
		
		
		if (typeof(item) != "object")
		{
			var str:String = item.toString(16);
			var x:String =  formatColorString(str);
	//		trace(x);
			return x;
		}
			
		return !item.label ? item.data : item.label;
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
		
			accState |= STATE_SYSTEM_SELECTED | STATE_SYSTEM_FOCUSED;
		}

		return accState;
	}

	/**
	 *  @private
	 *  Method to return the current val;ue of the component
	 *
	 *  @return string
	 */
	override public function get_accValue(childID:uint):String
	{
		if (ColorPicker(master).showingDropdown)
		{
			return ColorPicker(master).dropdown ? 
				ColorPicker(master).dropdown.textInput.text :
				null;
		}
		else
			return ColorPicker(master).selectedColor.toString(16);
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
		if (ColorPicker(master).dropdown)
		{
			var n:uint= ColorPicker(master).dropdown.length;
			for (var i:int = 0; i < n; i++)
			{
				childIDs[i] = i + 1;
			}
		}
		return childIDs;
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
		return super.eventsToHandle.concat([ "childChange"]);
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
			case "childChange":
			{
				var index:int = ComboBase(master).selectedIndex;
				Accessibility.sendEvent(master, ColorPicker(master).dropdown.focusedIndex + 1, EVENT_OBJECT_SELECTION);
				Accessibility.sendEvent(master, 0,
								EVENT_OBJECT_VALUECHANGE, true)
			}

			case "valueCommit":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_VALUECHANGE);
				break;
			}
		}
	}
	
	/**
	 *  @private
	 *  formats string color to add a space between each digit (hexit?).
	 *  Makes screen readers read color properly.
	 */
	private function formatColorString(color:String):String
	{
		var str2:String = "";
		var n:int = color.length;
		for (var i:uint = 0; i < n; i++)
			str2 += color.charAt(i) + " ";
		return str2;
	}
}

}
