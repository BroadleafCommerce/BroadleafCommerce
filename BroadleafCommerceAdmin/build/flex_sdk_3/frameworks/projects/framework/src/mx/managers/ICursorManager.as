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

[ExcludeClass]

import mx.core.IUIComponent;

/**
 *  @private
 */
public interface ICursorManager
{
	function get currentCursorID():int;
	function set currentCursorID(value:int):void;
    function get currentCursorXOffset():Number
	function set currentCursorXOffset(value:Number):void;
    function get currentCursorYOffset():Number
	function set currentCursorYOffset(value:Number):void;

	function showCursor():void;
	function hideCursor():void;
	function setCursor(cursorClass:Class, priority:int = 2,
			xOffset:Number = 0, yOffset:Number = 0):int;
	function removeCursor(cursorID:int):void;
	function removeAllCursors():void;
	function setBusyCursor():void;
	function removeBusyCursor():void; 

	function registerToUseBusyCursor(source:Object):void;
	function unRegisterToUseBusyCursor(source:Object):void;
}

}

