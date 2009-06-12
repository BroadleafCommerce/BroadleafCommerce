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
import flash.accessibility.AccessibilityImplementation;
import flash.accessibility.AccessibilityProperties;
import flash.events.Event;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The AccImpl class is the base class for accessibility in components.
 *  AccImpl supports system roles, object based events, and states.
 *
 *  @helpid 3001
 *  @tiptext The base class for accessibility in components.
 */ 
public class AccImpl extends AccessibilityImplementation
{
    include "../core/Version.as";

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
	private static const STATE_SYSTEM_UNAVAILABLE:uint = 0x00000001;
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_NAMECHANGE:uint = 0x800C;
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  All subclasses must implement this function.
	 */	
	mx_internal static function createAccessibilityImplementation(
								component:UIComponent):void
	{
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
	public function AccImpl(master:UIComponent)
	{
		super();

		this.master = master;
		
		stub = false;
		
		// Hook in UIComponentAccImpl setup here!
		master.accessibilityProperties = new AccessibilityProperties();
		
		// Hookup events to listen for
		var events:Array = eventsToHandle;
		if (events)
		{
			var n:int = events.length;
			for (var i:int = 0; i < n; i++)
			{
				master.addEventListener(events[i], eventHandler);
			}
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  A reference to the UIComponent instance that this AccImpl instance
	 *  is making accessible.
	 */
	protected var master:UIComponent;
	
	/**
	 *  Accessibility Role of the component being made accessible.
	 *  @review
	 */
	protected var role:uint;
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  All subclasses must override this function by returning an array
	 *  of strings of the events to listen for.
	 */
	protected function get eventsToHandle():Array
	{
		return [ "errorStringChanged", "toolTipChanged" ];
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Returns the system role for the component.
	 *
	 *  @param childID uint.
	 *
	 *  @return Role associated with the component.
	 *
	 *  @tiptext Returns the system role for the component
	 *  @helpid 3000
	 */
	override public function get_accRole(childID:uint):uint
	{
		return role;
	}
	
	/**
	 *  @private
	 *  Returns the name of the component.
	 *
	 *  @param childID uint.
	 *
	 *  @return Name of the component.
	 *
	 *  @tiptext Returns the name of the component
	 *  @helpid 3000
	 */
	override public function get_accName(childID:uint):String
	{
		var accName:String = UIComponentAccImpl.getFormName(master);

		if (childID == 0 && master.accessibilityProperties 
			&& master.accessibilityProperties.name 
				&& master.accessibilityProperties.name != "")
			accName += master.accessibilityProperties.name + " ";

		accName += getName(childID) + getStatusName();

		return (accName != null && accName != "") ? accName : null;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Returns the name of the accessible component.
	 *  All subclasses must implement this
	 *  instead of implementing get_accName.
	 */
	protected function getName(childID:uint):String
	{
		return null;
	}
	
	/**
	 *  Utility method determines state of the accessible component.
	 */
	protected function getState(childID:uint):uint
	{
		var accState:uint = STATE_SYSTEM_NORMAL;
		
		if (!UIComponent(master).enabled)
			accState |= STATE_SYSTEM_UNAVAILABLE;
		else
		{
			accState |= STATE_SYSTEM_FOCUSABLE
		
			if (UIComponent(master) == UIComponent(master).getFocus())
				accState |= STATE_SYSTEM_FOCUSED;
		}

		return accState;
	}

	/**
	 *  @private
	 */
	private function getStatusName():String
	{
		var statusName:String = "";
		
		if (master.toolTip)
			statusName += " " + master.toolTip;
		
		if (master is UIComponent && UIComponent(master).errorString)
			statusName += " " + UIComponent(master).errorString;
		
		return statusName;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  Generic event handler.
	 *  All AccImpl subclasses must implement this
	 *  to listen for events from its master component. 
	 */
	protected function eventHandler(event:Event):void
	{
		switch (event.type)
		{
			case "errorStringChanged":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_NAMECHANGE);
				Accessibility.updateProperties();
				break;
			}
			
			case "toolTipChanged":
			{
				Accessibility.sendEvent(master, 0, EVENT_OBJECT_NAMECHANGE);
				Accessibility.updateProperties();
				break;
			}
		}
	}
}

}
