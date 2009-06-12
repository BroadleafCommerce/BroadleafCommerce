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
import mx.controls.DateChooser;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The DateChooserAccImpl class is the accessibility class for DateChooser.
 *
 *  @helpid
 *  @tiptext This is the Button Accessibility Class.
 *  @review
 */
public class DateChooserAccImpl extends AccImpl
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
	 *  method of DateChooser withthe DateChooserAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		DateChooser.createAccessibilityImplementation =
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
	private static const EVENT_OBJECT_FOCUS:uint = 0x8005;

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
			new DateChooserAccImpl(component);
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
	public function DateChooserAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x09;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var calFlag:Boolean = true;
	
	/**
	 *  @private
	 */
	private var monthFlag:Boolean = true;

	/**
	 *  @private
	 */
	private var lastSelectedDate:Date;

	/**
	 *  @private
	 */
	private var selDateFallsInCurrMonth:Boolean;
	
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
		return super.eventsToHandle.concat([ "focusIn", "change", "scroll"]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityProperties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of the DateChooser.
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

	/**
	 *  @private
	 *  IAccessible method for executing the Default Action.
	 *
	 *  @param childID uint
	 */
	override public function accDoDefaultAction(childID:uint):void
	{
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the DateChooser
	 *  should return the selected date with weekday, month and year.
	 *  appends 'today' if selected date is also today date.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		var name:String;
		
		var dateChooser:DateChooser = DateChooser(master);
		
		var selDate:Date = dateChooser.selectedDate;
		
		selDateFallsInCurrMonth =
			selDate != null &&
			selDate.getMonth() == dateChooser.displayedMonth &&
			selDate.getFullYear() == dateChooser.displayedYear;

		if (selDate != null && selDateFallsInCurrMonth)
		{
			if (monthFlag)
			{
				name = "" + selDate.getDate() + " " +
					   dateChooser.dayNames[selDate.getDay()] + ", " +
					   dateChooser.monthNames[dateChooser.displayedMonth] + " " +
					   dateChooser.displayedYear;
			}
			else
			{
				name = "" + selDate.getDate() + " " +
					   dateChooser.dayNames[selDate.getDay()];
			}

			var todayDate:Date = new Date();
			
			if (todayDate.getDate() == selDate.getDate() &&
				todayDate.getMonth() == selDate.getMonth() &&
				todayDate.getFullYear() == selDate.getFullYear())
			{
				name += ", today";
			}
		}
		else
		{
			name = dateChooser.monthNames[dateChooser.displayedMonth] + " " +
				   dateChooser.displayedYear;
		}

		if (calFlag)
			name = " Calendar View, " + name;
			
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
		var dateChooser:DateChooser = DateChooser(master);

		var randomDate:int = dateChooser.displayedMonth + dateChooser.displayedYear;

		switch (event.type)
		{
			case "focusIn":
			{
				calFlag = true;
				monthFlag = true;
				break;
			}

			case "change":
			{
				calFlag = false;
				
				var selDate:Date = dateChooser.selectedDate;
				if (selDate)
				{
					if (lastSelectedDate)
						monthFlag =  lastSelectedDate.getDate() == selDate.getDate();

					Accessibility.sendEvent(master,
											randomDate + selDate.getDate() + 100,
											EVENT_OBJECT_FOCUS);
					
					Accessibility.sendEvent(master,
											randomDate + selDate.getDate() + 100,
											EVENT_OBJECT_SELECTION);
				}
				lastSelectedDate = selDate;

				break;
			}

			case "scroll":
			{
				calFlag = false;
				monthFlag = true;

				Accessibility.sendEvent(master, randomDate,
										EVENT_OBJECT_FOCUS);

				Accessibility.sendEvent(master, randomDate,
										EVENT_OBJECT_SELECTION);

				break;
			}
		}
	}
}

}
