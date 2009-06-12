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

import mx.controls.Button;
import mx.controls.CheckBox;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The CheckBoxAccImpl class is the accessibility class for CheckBox.
 *
 *  @helpid 3003
 *  @tiptext This is the CheckBoxAccImpl Accessibility Class.
 *  @review
 */
public class CheckBoxAccImpl extends ButtonAccImpl
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
	 *  This is used for initializing CheckBoxAccImpl class to hook its
	 *  createAccessibilityImplementation() method to CheckBox class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of CheckBox with the CheckBoxAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		CheckBox.createAccessibilityImplementation =
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
	private static const STATE_SYSTEM_CHECKED:uint = 0x00000010;

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
			new CheckBoxAccImpl(component);
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
	public function CheckBoxAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x2C;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the state of the CheckBox.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon whether the CheckBox is checked or unchecked,
	 *  a value is returned.
	 *
	 *  @param childID uint
	 *
	 *  @return State Whether the CheckBox is checked or unchecked.
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		
		if (Button(master).selected)
			accState |= STATE_SYSTEM_CHECKED;

		return accState;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the default action of
	 *  the CheckBox, which is Check or UnCheck depending on the state.
	 *
	 *  @param childID uint
	 *
	 *  @return DefaultAction Check or UnCheck.
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		return Button(master).selected ? "UnCheck" : "Check";
	}
}

}
