////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import flash.events.IEventDispatcher;
import mx.managers.IFocusManager;
import mx.managers.ISystemManager;

/**
 *  The IFocusManagerContainer interface defines the interface that 
 *  containers implement to host a FocusManager.
 *  The PopUpManager automatically installs a FocusManager
 *  in any IFocusManagerContainer it pops up.
 */
public interface IFocusManagerContainer extends IEventDispatcher 
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  focusManager
	//----------------------------------

	/**
	 *  The FocusManager for this component. 
	 *  The FocusManager must be in a <code>focusManager</code> property.
	 */
	function get focusManager():IFocusManager;
	
	/**
	 *  @private
	 */
	function set focusManager(value:IFocusManager):void;
	
	//----------------------------------
	//  systemManager
	//----------------------------------

	/**
	 *  @copy mx.core.UIComponent#systemManager
	 */
	function get systemManager():ISystemManager;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

 	/**
	 *  Determines whether the specified display object is a child 
	 *  of the container instance or the instance itself. 
	 *  The search includes the entire display list including this container instance. 
	 *  Grandchildren, great-grandchildren, and so on each return <code>true</code>.
	 *
	 *  @param child The child object to test.
	 *
	 *  @return <code>true</code> if the child object is a child of the container 
	 *  or the container itself; otherwise <code>false</code>.
	 */
	function contains(child:DisplayObject):Boolean;
}

}
