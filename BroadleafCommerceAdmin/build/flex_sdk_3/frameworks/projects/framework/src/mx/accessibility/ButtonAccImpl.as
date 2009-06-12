////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
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
import flash.events.KeyboardEvent;
import flash.ui.Keyboard;
import mx.controls.Button;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
  *  The ButtonAccImpl class is the accessibility class for Button.
  *  This AccImpl class is used in CheckBox and RadioButton,
  *  as these components extend the Button class.
  *
  *  @helpid 3002
  *  @tiptext This is the Button Accessibility Class.
  *  @review
  */
public class ButtonAccImpl extends AccImpl
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
	 *  This is used for initializing ButtonAccImpl class to hook its
	 *  createAccessibilityImplementation() method to Button class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of Button with the ButtonAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		Button.createAccessibilityImplementation =
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
	private static const STATE_SYSTEM_PRESSED:uint = 0x00000008;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_NAMECHANGE:uint = 0x800C;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_STATECHANGE:uint = 0x800A;
	
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
			new ButtonAccImpl(component);
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
	public function ButtonAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x2B;
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
		return super.eventsToHandle.concat([ "click", "labelChanged" ]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of the Button.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon the button being pressed or released,
	 *  a value is returned.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);

		if (Button(master).selected)
			accState |= STATE_SYSTEM_PRESSED;

		return accState;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the default action
	 *  of the Button, which is Press.
	 *
	 *  @param childID uint
	 *
	 *  @return DefaultAction String
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		return "Press";
	}

	/**
	 *  @private
	 *  IAccessible method for performing the default action
	 *  associated with Button, which is Press.
	 *
	 *  @param childID uint
	 */
	override public function accDoDefaultAction(childID:uint):void
	{
		if (master.enabled)
		{
			var event:KeyboardEvent = new KeyboardEvent(KeyboardEvent.KEY_DOWN);
			event.keyCode = Keyboard.SPACE;
			master.dispatchEvent(event);

			event = new KeyboardEvent(KeyboardEvent.KEY_UP);
			event.keyCode = Keyboard.SPACE;
			master.dispatchEvent(event);
		}
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the Button
	 *  which is spoken out by the screen reader
	 *  The Button should return the label inside as the name of the Button.
	 *  The name returned here would take precedence over the name
	 *  specified in the accessibility panel.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 */
	override protected function getName(childID:uint):String
	{
		var label:String = Button(master).label;
		
		return label != null && label != "" ? label : "";
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
			case "click":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_STATECHANGE);
				Accessibility.updateProperties();
				break;
			}
			
			case "labelChanged":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_NAMECHANGE);
				Accessibility.updateProperties();
				break;
			}
		}
	}
}

}
