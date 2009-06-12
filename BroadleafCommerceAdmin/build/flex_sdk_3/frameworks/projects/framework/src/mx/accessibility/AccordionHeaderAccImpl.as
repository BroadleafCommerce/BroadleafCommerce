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
import flash.display.DisplayObject;
import flash.events.Event;
import mx.containers.Accordion;
import mx.containers.accordionClasses.AccordionHeader;
import mx.controls.Button;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The AccordionHeaderAccImpl is the class
 *  for enabling Accordion Accessibility.
 *
 *  @helpid
 *  @tiptext This is the AccordionHeader Accessibility Class.
 *  @review
 */
public class AccordionHeaderAccImpl extends AccImpl
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
	 *  This is used for initializing AccordionHeaderAccImpl class to hook its
	 *  createAccessibilityImplementation() method to AccordionHeader class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();
	
	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of AccordionHeader with the AccordionHeaderAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		AccordionHeader.createAccessibilityImplementation =
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
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006;

	/**
	 *  @private
	 */
	private static const MAX_NUM:uint = 100000;	

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
			new AccordionHeaderAccImpl(component);
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
	public function AccordionHeaderAccImpl(master:UIComponent)
	{
		super(master);
		
		role = 0x25;

		master.parent.addEventListener("change", eventHandler);
		master.addEventListener("removed", removedHandler);
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
		return super.eventsToHandle.concat([ "focusDraw" ]);
	}
	 
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of Tab selected.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 *  @review
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		
		var accordion:Accordion = Accordion(master.parent);
		
		if (accordion.enabled)
		{
			accState |= STATE_SYSTEM_SELECTABLE;
		
			if (Button(master).selected)
				accState |= STATE_SYSTEM_SELECTED;
				
			var index:int = accordion.focusedIndex;
			if (index >= 0 && master == accordion.getHeaderAt(index))
				accState |= STATE_SYSTEM_FOCUSED;
		}					 
		else
			accState |= STATE_SYSTEM_UNAVAILABLE;

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
		return "Switch";
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the Tab
	 *  which is spoken out by the screen reader.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 */
	override protected function getName(childID:uint):String
	{
		var tab:Button = Button(master);
		
		var parentAccordion:Accordion = Accordion(master.parent);

		if (childID > 0) 
			tab = parentAccordion.getHeaderAt(parentAccordion.focusedIndex);

		var name:String = tab.label + " Tab";

		//if (tab.selected) // until 125587 is fixed.
		if (tab == parentAccordion.getHeaderAt(parentAccordion.selectedIndex))
			name += ", Active";

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
	 *  All AccImpl must implement this to listen for events
	 *  from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		var index:int;
		switch (event.type)
		{
			case "focusDraw":
			{
				// sending + 10000 to ensure sending out different number
				// than sent last time in change.

				//event.currentTarget here is the AccordionHeader (a/k/a 'master')
				index = Accordion(event.currentTarget.parent).focusedIndex;

				if (index >= 0 && master == Accordion(event.currentTarget.parent).getHeaderAt(index))
				{
					Accessibility.sendEvent(DisplayObject(event.currentTarget), index + MAX_NUM + 1,
											EVENT_OBJECT_FOCUS, true);
					break;
				}
			}

			case "change":
			{
				//event.currentTarget here is the Accordion (a/k/a 'master.parent')
				index = Accordion(event.currentTarget).selectedIndex;
				if (index >= 0 && master == Accordion(event.currentTarget).getHeaderAt(index))
				{
					Accessibility.sendEvent(master, index + 1,
											EVENT_OBJECT_SELECTION, true);
					break;
				}
			}
		}
	}
	
	/**
	 *  @private
	 *  Remove the change handler on the accordion that was causing this to stick around in memory 
	 *  and cause RTE. Also remove self.
	 */
	protected function removedHandler(event:Event):void
	{
		master.parent.removeEventListener("change", eventHandler);
		master.removeEventListener("removed", removedHandler);
	}

}

}
