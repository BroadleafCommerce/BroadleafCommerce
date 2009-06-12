////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

/**
 *  The IFlexContextMenu interface defines the interface for a 
 *  Flex context menus.  
 *
 *  @see mx.core.UIComponent#flexContextMenu
 */
public interface IFlexContextMenu
{
	import flash.display.InteractiveObject;

	/**
	 *  Sets the context menu of an InteractiveObject.  This will do 
	 *  all the necessary steps to add ourselves as the context 
	 *  menu for this InteractiveObject, such as adding listeners, etc..
	 * 
	 *  @param component InteractiveObject to set context menu on
	 */ 
	function setContextMenu(component:InteractiveObject):void;
	
	/**
	 *  Unsets the context menu of a InteractiveObject.  This will do 
	 *  all the necessary steps to remove ourselves as the context 
	 *  menu for this InteractiveObject, such as removing listeners, etc..
	 * 
	 *  @param component InteractiveObject to unset context menu on
	 */ 
	function unsetContextMenu(component:InteractiveObject):void;

}

}
