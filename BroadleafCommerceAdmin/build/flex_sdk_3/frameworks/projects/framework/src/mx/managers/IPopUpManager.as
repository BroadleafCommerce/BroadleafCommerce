////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import mx.core.IFlexDisplayObject;

[ExcludeClass]

/**
 *  @private
 */
public interface IPopUpManager
{
	function createPopUp(parent:DisplayObject,
			className:Class,
			modal:Boolean = false,
			childList:String = null):IFlexDisplayObject;
	function addPopUp(window:IFlexDisplayObject,
			parent:DisplayObject,
			modal:Boolean = false,
			childList:String = null):void;
	function centerPopUp(popUp:IFlexDisplayObject):void;
	function removePopUp(popUp:IFlexDisplayObject):void;
	function bringToFront(popUp:IFlexDisplayObject):void;
}

}

