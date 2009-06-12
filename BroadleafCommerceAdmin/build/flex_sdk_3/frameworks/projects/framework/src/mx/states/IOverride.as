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

package mx.states
{

import mx.core.UIComponent;

/**
 *  The IOverride interface is used for view state overrides.
 *  All entries in the State class <code>overrides</code>
 *  property array must implement this interface.
 *
 *  @see mx.states.State
 */
public interface IOverride
{
	/**
	 *  Initializes the override.
	 *  Flex calls this method before the first call to the
	 *  <code>apply()</code> method, so you put one-time initialization
	 *  code for the override in this method.
 	 *
	 *  <p>Flex calls this method automatically when the state is entered.
	 *  It should not be called directly.</p>
    */
    function initialize():void

	/**
	 *  Applies the override. Flex retains the original value, so that it can 
	 *  restore the value later in the <code>remove()</code> method.
	 *
	 *  <p>This method is called automatically when the state is entered.
	 *  It should not be called directly.</p>
	 *
	 *  @param parent The parent of the state object containing this override.
	 *  The override should use this as its target if an explicit target was
	 *  not specified.
	 */
	function apply(parent:UIComponent):void;

	/**
	 *  Removes the override. The value remembered in the <code>apply()</code>
	 *  method is restored.
	 *
	 *  <p>This method is called automatically when the state is entered.
	 *  It should not be called directly.</p>
	 *
	 *  @param parent The parent of the state object containing this override.
	 *  The override should use this as its target if an explicit target was
	 *  not specified.
	 */
	function remove(parent:UIComponent):void;
}
}
