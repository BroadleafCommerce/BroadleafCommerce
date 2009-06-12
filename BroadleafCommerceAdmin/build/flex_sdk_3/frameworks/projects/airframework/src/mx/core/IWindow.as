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

package mx.core
{

import flash.display.NativeWindow;

/**
 *  The IWindow interface defines the API for components that serve as top-level
 *  containers in Flex-based AIR applications (containers that represent operating
 *  system windows).
 * 
 *  @playerversion AIR 1.1
 */
public interface IWindow
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  maximizable
    //----------------------------------

	/**
	 *  Specifies whether the window can be maximized.
	 */
	function get maximizable():Boolean;
	
    //----------------------------------
	//  minimizable
    //----------------------------------

	/**
	 *  Specifies whether the window can be minimized.
	 */
	function get minimizable():Boolean;
	
    //----------------------------------
	//  nativeWindow
    //----------------------------------

	/**
	 *  The underlying NativeWindow that the Window component uses.
	 */
	function get nativeWindow():NativeWindow

    //----------------------------------
	//  resizable
    //----------------------------------

	/**
	 *  Specifies whether the window can be resized.
	 */
	function get resizable():Boolean;
	
    //----------------------------------
	//  status
    //----------------------------------

	/**
	 *  The string that appears in the status bar, if it is visible.
	 */
	function get status():String;
	
	/**
	 *  @private
	 */
	function set status(value:String):void;
	
    //----------------------------------
	//  systemChrome
    //----------------------------------

	/**
	 *  Specifies the type of system chrome (if any) the window has.
	 *  The set of possible values is defined by the constants
	 *  in the NativeWindowSystemChrome class.
	 *
     *  @see flash.display.NativeWindowSystemChrome
	 */
	function get systemChrome():String;
	
    //----------------------------------
	//  title
    //----------------------------------

	/**
	 *  The title text that appears in the window title bar and
     *  the taskbar.
	 */
	function get title():String;
	
	/**
	 *  @private
	 */
	function set title(value:String):void;
	
    //----------------------------------
	//  titleIcon
    //----------------------------------

	/**
	 *  The Class (usually an image) used to draw the title bar icon.
	 */
	function get titleIcon():Class;
	
	/**
	 *  @private
	 */
	function set titleIcon(value:Class):void;
	
    //----------------------------------
	//  transparent
    //----------------------------------

	/**
	 *  Specifies whether the window is transparent.
	 */
	function get transparent():Boolean;
	
    //----------------------------------
	//  type
    //----------------------------------

	/**
	 *  Specifies the type of NativeWindow that this component
	 *  represents. The set of possible values is defined by the constants
	 *  in the NativeWindowType class.
	 *
	 *  @see flash.display.NativeWindowType
	 */
	function get type():String;
	
    //----------------------------------
	//  visible
    //----------------------------------

	/**
	 *  Controls the window's visibility.
	 */
	function get visible():Boolean;
	
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  Closes the window.
	 */
	function close():void;
	
	/**
	 *  Maximizes the window, or does nothing if it's already maximized.
	 */
	function maximize():void
	
	/**
	 *  Minimizes the window.
	 */
	function minimize():void;
	
	/**
	 *  Restores the window (unmaximizes it if it's maximized, or
     *  unminimizes it if it's minimized).
	 */
	function restore():void;
}

}
