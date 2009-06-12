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

import mx.containers.Panel;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The PanelAccImpl class is the accessibility class for Panel.
 *
 *  @helpid 3011
 *  @tiptext This is the Panel Accessibility Class.
 *  @review
 */
public class PanelAccImpl extends AccImpl
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
	 *  This is used for initializing PanelAccImpl class to hook its
	 *  createAccessibilityImplementation() method to Panel class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static Method for swapping the
	 *  createAccessibilityImplementation method of Panel with
	 *  the PanelAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		Panel.createAccessibilityImplementation =
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
	private static const ROLE_SYSTEM_DIALOG:uint = 0x12;
	
	/**
	 *  @private
	 */
	private static const ROLE_SYSTEM_TITLEBAR:uint = 0x01;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;

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
		Panel(component).getTitleBar().accessibilityImplementation =
			new PanelAccImpl(component);
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
	public function PanelAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x09;
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
	 *  @param childID children of the component
	 */
	override public function get_accRole(childID:uint):uint
	{
		var accRole:uint = role;
		
		switch (childID)
		{
			case 1:
			{
				accRole = ROLE_SYSTEM_TITLEBAR;
				break;
			}

			case 2:
			{
				accRole = ROLE_SYSTEM_DIALOG;
				break;
			}

			default:
				accRole = role;
				break;
		}
		
		return accRole;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the Panel.
	 *  States are predefined for all the components in MSAA. Values are assigned to each state.
	 *  Depending upon the Panel being Focusable, Focused and Moveable, a value is returned.
	 *
	 *  @param childID:int
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
				break;
			}
		}
		
		return accState;
	}

	/**
	 *  @private
	 *  Method to return an array of childIDs of Panel component
	 *
	 *  @return Array
	 */
	override public function getChildIDArray():Array
	{
		var childIDs:Array = [];

		for (var i:int = 0; i < 2; ++i)
		{
			childIDs[i] = i + 1;
		}

		return childIDs;
	}
	/**
	 *  @private
	 *  IAccessible method for returning the bounding box of the Panel.
	 *
	 *  @param childID:uint
	 *
	 *  @return Location:Object
	 */
	override public function accLocation(childID:uint):*
	{
		var location:Object = master;
		
		switch (childID)
		{
			case 1:
			{
				location = Panel(master).getTitleBar();
				break;
			}

			case 2:
			{
				location = Panel(master).contentPane;
				break;
			}

			default:
			{
				break;
			}
		}

		return location;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the Panel
	 *  which is spoken out by the screen reader
	 *  The Panel should return the Title as the name.
	 *
	 *  @param childID:uint
	 *
	 *  @return Name:String
	 */
	override protected function getName(childID:uint):String
	{
		var name:String = Panel(master).title;

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
				name = Panel(master).title + " " + Panel(master).className;
				break;
			}
		}

		return name;
	}
}

}
