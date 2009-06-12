////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
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
import flash.events.MouseEvent;
import mx.containers.Panel;
import mx.containers.TitleWindow;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The TitleWindowAccImpl class is the accessibility class for TitleWindow.
 *
 *  @helpid 3011
 *  @tiptext This is the TitleWindow Accessibility Class.
 *  @review
 */
public class TitleWindowAccImpl extends PanelAccImpl
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
	 *  This is used for initializing TitleWindowAccImpl class to hook its
	 *  createAccessibilityImplementation() method to TitleWindow class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static Method for swapping the
	 *  createAccessibilityImplementation method of TitleWindow with
	 *  the TitleWindowAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		TitleWindow.createAccessibilityImplementation =
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
	private static const STATE_SYSTEM_MOVEABLE:uint = 0x00040000;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_CREATE:uint = 0x8000;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_DESTROY:uint = 0x8001;

	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_LOCATIONCHANGE:uint = 0x800B;
	
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
		var titleBar:UIComponent = TitleWindow(component).getTitleBar();

		var titleBarAccImpl:TitleWindowAccImpl =
			new TitleWindowAccImpl(component);

		titleBar.accessibilityImplementation = titleBarAccImpl;

		Accessibility.sendEvent(titleBar, 0, EVENT_OBJECT_CREATE);

		Accessibility.updateProperties();
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
	public function TitleWindowAccImpl(master:UIComponent)
	{
		super(master);
		
		// Typecasting as Panel and not TitleWindow since AlertAccImpl
		// also extends from this but Alert is not a subclass of TitleWindow
		// but is of Panel.
		
		Panel(master).getTitleBar().addEventListener(MouseEvent.MOUSE_UP,
													 eventHandler);
		
		Panel(master).closeButton.addEventListener(MouseEvent.MOUSE_UP,
														eventHandler);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of the TitleWindow.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon the TitleWindow being Focusable, Focused and Moveable,
	 *  a value is returned.
	 *
	 *  @param childID:uint
	 *
	 *  @return State:uint
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		
		switch (childID)
		{
			case 1:
			{
				break;
			}

			case 2:
			{
				accState |= STATE_SYSTEM_FOCUSED;
				break;
			}

			default:
			{
				accState |= STATE_SYSTEM_MOVEABLE;
				break;
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
	 *  All AccImpl must implement this
	 *  to listen for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		switch (event.type)
		{
			case MouseEvent.MOUSE_UP:
			{
				if (event.target == Panel(master).getTitleBar())
				{
					Accessibility.sendEvent(Panel(master).getTitleBar(), 0,
											EVENT_OBJECT_LOCATIONCHANGE, true);
				}

				if (event.target == Panel(master).closeButton)
				{
					Accessibility.sendEvent(Panel(master).getTitleBar(), 0,
											EVENT_OBJECT_DESTROY, true);
				}

				Accessibility.updateProperties();
				break;
			}
		}
	}
}

}
