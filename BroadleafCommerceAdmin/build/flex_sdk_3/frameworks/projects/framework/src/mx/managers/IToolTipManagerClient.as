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

import mx.core.IFlexDisplayObject;

/**
 *  Components that implement IToolTipManagerClient can have tooltips and must 
 *  have a toolTip getter/setter.
 *  The ToolTipManager class manages showing and hiding the 
 *  tooltip on behalf of any component which is an IToolTipManagerClient.
 * 
 *  @see mx.controls.ToolTip
 *  @see mx.managers.ToolTipManager
 *  @see mx.core.IToolTip
 */
public interface IToolTipManagerClient extends IFlexDisplayObject
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  toolTip
	//----------------------------------

	/**
	 *  The text of this component's tooltip.
	 */
	function get toolTip():String;
	
	/**
	 *  @private
	 */
	function set toolTip(value:String):void;

}

}
