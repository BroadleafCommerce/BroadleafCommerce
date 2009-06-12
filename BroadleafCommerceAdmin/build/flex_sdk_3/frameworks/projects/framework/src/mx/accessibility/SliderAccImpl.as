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
import mx.controls.sliderClasses.Slider;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.SliderEvent;
import flash.events.FocusEvent;

use namespace mx_internal;
	
/**
 *  The SliderAccImpl class is the accessibility class for HSlider and VSlider.
 *
 *  @helpid
 *  @tiptext This is the TabNavigator Accessibility Class.
 *  @review
 */
public class SliderAccImpl extends AccImpl
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
		Slider.createAccessibilityImplementation =
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
	 *  Default state for all the components.
	 */
	private static const STATE_SYSTEM_NORMAL:uint = 0x00000000;

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
	private static const STATE_SYSTEM_SELECTABLE:uint = 0x00200000;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTED:uint = 0x00000002;

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
	private static const EVENT_OBJECT_VALUECHANGE:uint = 0x800E;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006;
	
	/**
	 *  @private
	 */
	private static const ROLE_SLIDER:uint = 0x33;

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
			new SliderAccImpl(component);
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
	public function SliderAccImpl(master:UIComponent)
	{
		super(master);
		master.addEventListener(FocusEvent.FOCUS_IN, focusInHandler);
		role = 0x33;
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
	 *  @param childID uint.
	 */
	override public function get_accRole(childID:uint):uint
	{
		return role;
	}
	
		/**
	 *  @private
	 *  IAccessible method for returning the value of the slider
	 *  (which would be the value of the item selected).
	 *  The slider should return the value of the current thumb as the value.
	 *
	 *  @param childID uint
	 *
	 *  @return Value String
	 *  @review
	 */
	override public function get_accValue(childID:uint):String
	{
		var val:Number = Slider(master).values[Math.max(childID - 1, 0)];
		val = (val -  Slider(master).minimum)/(Slider(master).maximum - Slider(master).minimum) * 100;
		
		return String(val) + " percent";
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
		
		var n:Number = Slider(master).thumbCount;
		for (var i:int = 0; i < n; i++)
			childIDs[i] = i + 1;
		return childIDs;
	}
	/**
	 *  @private
	 *  method for returning the name of the slider
	 *  should return the value
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		
		if (master.id)
			 return master.id;
		return "";
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the Button.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		accState |= STATE_SYSTEM_SELECTABLE;
		if (childID == 0)
			accState |=  STATE_SYSTEM_SELECTED;
		else 
			accState |=  STATE_SYSTEM_SELECTED | STATE_SYSTEM_FOCUSED;
		return accState;
	}
	
	/**
	 *  Utility method determines state of the accessible component.
	 */
	override protected function getState(childID:uint):uint
	{
		var accState:uint = STATE_SYSTEM_NORMAL;
		
		if (!UIComponent(master).enabled)
			accState |= STATE_SYSTEM_UNAVAILABLE;
		else
		{
			accState |= STATE_SYSTEM_FOCUSABLE
			
			for (var i:uint = 0; i < Slider(master).thumbCount; i++)
			{
				if (Slider(master).getThumbAt(i) == Slider(master).focusManager.getFocus())
				{
					//trace("has focus", UIComponent(master),  UIComponent(master).getFocus());
					accState |= STATE_SYSTEM_FOCUSED;
					break;
				}
			}
		}
		return accState;
	}
	
	
	
	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Override the generic event handler.
	 *  All AccImpl must implement this to listen
	 *  for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		if (event.type == "change")
		{
			var childID:uint = SliderEvent(event).thumbIndex + 1;
			Accessibility.sendEvent(master, 0, EVENT_OBJECT_SELECTION);
			Accessibility.sendEvent(master, 0,
								EVENT_OBJECT_VALUECHANGE, true)
		} 
	}
	
	/**
	 *  @private
	 *  This is (kind of) a hack to get around the fact that Slider is not 
	 *  an IFocusManagerComponent. It forces frocus from accessibility when one of 
	 *  its thumbs get focus. 
	 */
	private function focusInHandler(event:Event):void
	{
		Accessibility.sendEvent(master, 0, EVENT_OBJECT_FOCUS);
	}
}
}