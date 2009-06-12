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
import flash.accessibility.AccessibilityProperties;
import flash.events.Event;
import mx.controls.Alert;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.managers.SystemManager;

use namespace mx_internal;

/**
 *  The AlertAccImpl class is the accessibility class for Alert.
 *
 *  @helpid 3030
 *  @tiptext This is the Alert Accessibility Class.
 *  @review
 */
public class AlertAccImpl extends TitleWindowAccImpl
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
	 *  This is used for initializing AlertAccImpl class to hook its
	 *  createAccessibilityImplementation() method to Alert class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of Alert with the AlertAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		Alert.createAccessibilityImplementation =
			createAccessibilityImplementation;
		
		return true;
	}

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
		var titleBar:UIComponent = Alert(component).getTitleBar();

		var titleBarAccImpl:AlertAccImpl = new AlertAccImpl(component);
		titleBar.accessibilityImplementation = titleBarAccImpl;
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
	public function AlertAccImpl(master:UIComponent)
	{
		super(master);
		
		role = 0x08;
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
		return super.eventsToHandle.concat([ "close", "creationComplete" ]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the child
	 *  which is spoken out by the screen reader.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		var name:String = Alert(master).title;

		switch (childID)
		{
			case 1:
			{
				name = "";
				break;
			}

			case 2:
			{
				name = "";
				break
			}

			default:
			{
				name = Alert(master).title + ", " +
					   Alert(master).text;
				break;
			}
		}

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
	 *  All AccImpl must implement this
	 *  to listen for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		var titleBar:UIComponent;
		
		switch (event.type)
		{
			case "close":
			{
				titleBar = Alert(master).getTitleBar();

				SystemManager(master.parent).document.accessibilityProperties.silent = false;

				Accessibility.updateProperties();

				Accessibility.sendEvent(titleBar,0,0x0011); // DialogEnd
				Accessibility.sendEvent(titleBar,0,0x8004); // ObjectReorder
				Accessibility.sendEvent(titleBar,0,0x8001); // ObjectDestroyed
				Accessibility.sendEvent(titleBar,0,0x800B); // LocationChanged
				Accessibility.sendEvent(titleBar,0,0x800F); // ParentChanged
				Accessibility.sendEvent(titleBar,0,0x0003); // ObjectHide
				Accessibility.sendEvent(titleBar,0,0x0003); // ForegroundChanged
				if (SystemManager(master.parent).stage.focus)
					Accessibility.sendEvent(SystemManager(master.parent).stage.focus,0,0x8005); // Focus

				break;
			}
			break;

			case "creationComplete":
			{
				if (!SystemManager(master.parent).document.accessibilityProperties)
				{
					SystemManager(master.parent).document.accessibilityProperties =
						new AccessibilityProperties();
				}
				SystemManager(master.parent).document.accessibilityProperties.
					silent = true;

				master.$visible = true;

				titleBar = Alert(master).getTitleBar();

				titleBar.tabIndex = 0;
				Alert(master).alertForm.textField.tabIndex = 0;

   				UIComponent(titleBar).$visible = true;

				Accessibility.updateProperties();

				Accessibility.sendEvent(titleBar, 0, 0x0010); // DialogStart
				Accessibility.sendEvent(titleBar, 0, 0x8004); // ObjectReorder
				Accessibility.sendEvent(titleBar, 0, 0x8000); // ObjectCreated
				Accessibility.sendEvent(titleBar, 0, 0x800B); // LocationChanged
				Accessibility.sendEvent(titleBar, 0, 0x800F); // ParentChanged
				Accessibility.sendEvent(titleBar, 0, 0x0002); // ObjectShow
				Accessibility.sendEvent(titleBar, 0, 0x0003); // ForegroundChanged
				Accessibility.sendEvent(titleBar, 0, 0x8005); // Focus
				Accessibility.sendEvent(Alert(master).alertForm.defaultButton, 0, 0x8005); // Focus

				break;
			}
			break;
		}
	}

}

}
