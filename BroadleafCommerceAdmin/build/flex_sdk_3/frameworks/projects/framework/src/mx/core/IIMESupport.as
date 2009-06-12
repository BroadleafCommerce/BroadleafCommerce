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

package mx.core
{

/**
 *  The IIMESupport interface defines the interface for any component that supports IME 
 *  (input method editor).
 *  IME is used for entering characters in Chinese, Japanese, and Korean.
 * 
 *  @see flash.system.IME
 */
public interface IIMESupport
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  imeMode
	//----------------------------------

	/**
	 *  The IME mode of the component.
	 */
	function get imeMode():String;

	/**
	 *  @private
	 */
	function set imeMode(value:String):void;
}

}
