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

import mx.controls.LinkButton;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The LinkButtonAccImpl class is the accessibility class for Link.
 *
 *  @helpid 3002
 *  @tiptext This is the LinkButton Accessibility Class.
 *  @review
 */
public class LinkButtonAccImpl extends ButtonAccImpl
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
	 *  This is used for initializing LinkButtonAccImpl class to hook its
	 *  createAccessibilityImplementation() method to LinkButton class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();

	/**
	 *  @private
	 *  Static Method for swapping the
	 *  createAccessibilityImplementation method of LinkButton with
	 *  the LinkButtonAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		LinkButton.createAccessibilityImplementation =
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
		component.accessibilityImplementation =
			new LinkButtonAccImpl(component);
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
	public function LinkButtonAccImpl(master:UIComponent)
	{
		super(master);
		
		role = 0x1E;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  IAccessible method for returning the default action
	 *  of the LinkButton, which is Jump.
	 *
	 *  @param childID uint
	 *
	 *  @return DefaultAction String
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		return "Jump";
	}
}

}
