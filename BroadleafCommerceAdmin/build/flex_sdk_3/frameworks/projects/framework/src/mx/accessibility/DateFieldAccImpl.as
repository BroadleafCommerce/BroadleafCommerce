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
import mx.controls.DateField;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The DateFieldAccImpl class is the accessibility class for DateChooser.
 *
 *  @helpid
 *  @tiptext This is the Button Accessibility Class.
 *  @review
 */
public class DateFieldAccImpl extends AccImpl
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
	 *  This is used for initializing DateFieldAccImpl class to hook its
	 *  createAccessibilityImplementation() method to DateField class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of DateField with the DateFieldAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		DateField.createAccessibilityImplementation =
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
	private static const EVENT_OBJECT_FOCUS:uint =  0x8005;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_SELECTION:uint =  0x8006;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Method for creating the Accessibility class. This
	 *  method is called from UIComponent. 
	 *  @review
	 */
	mx_internal static function createAccessibilityImplementation(
								component:UIComponent):void
	{
		component.accessibilityImplementation =
			new DateFieldAccImpl(component);
	}

	/**
	 *  Method call for enabling accessibility for a component.
	 *  This method is required for the compiler to activate
	 *  the accessibility classes for a component.
	 */
	public static function enableAccessibility():void
	{
		DateChooserAccImpl.enableAccessibility();
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
	public function DateFieldAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x2e;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var instructionsFlag:Boolean = false;

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
		return super.eventsToHandle.concat([ "change", "focusIn", "focusOut", "open", "close" ]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of the DateField.
	 *  States are predefined for all the components in MSAA.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 *  @review
	 */
	override public function get_accState(childID:uint):uint
	{
		return getState(childID);
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the DateField
	 *  should return the selected date with weekday, month and year.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		var name:String = "Drop Down Calendar, ";
		
		var dateField:DateField = DateField(master);
		
		if (dateField.displayedMonth && !isNaN(dateField.displayedYear))
		{
			name += dateField.monthNames[dateField.displayedMonth] + " " +
					dateField.displayedYear;
		}
		
		var instrString:String = ", to open press control down";
		
		var selDate:Date = dateField.selectedDate;
		if (selDate)
		{
			var tDate:String = "" + selDate.getDate() + " " + 
							   dateField.monthNames[selDate.getMonth()] + " " +
							   selDate.getFullYear();
			
			name = "Drop Down Calendar, " + tDate;
		}

		if (instructionsFlag)
			name += instrString;

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
	 *  All AccImpl must implement this to listen
	 *  for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		switch (event.type)
		{
			case "change":
			{
				var childID:uint = 289; // not sure where 289 number has come from
				
				var dateField:DateField = DateField(master);

				// need to check != null for Date
				if (dateField.selectedDate != null)
					childID += dateField.selectedDate.getDate();
				
				Accessibility.sendEvent(master, childID, EVENT_OBJECT_FOCUS);
				Accessibility.sendEvent(master, childID, EVENT_OBJECT_SELECTION);
				break;
			}

			case "close":
			case "focusIn":
			{
				instructionsFlag = true;
				break;
			}

			case "open":
			case "focusOut":
			{
				instructionsFlag = false;
				break;
			}
		}
	}
}

}
