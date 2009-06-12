////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.graphics
{

import flash.display.Graphics;

/**
 *  Defines the interface that classes that define a line must implement.
 */
public interface IStroke
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  weight
	//----------------------------------

	/**
	 *  The line weight, in pixels.
	 *  For many chart lines, the default value is 1 pixel.
	 */
	function get weight():Number;
	
	/**
	 *  @private
	 */
	function set weight(value:Number):void;
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Applies the properties to the specified Graphics object.
	 *   
	 *  @param g The Graphics object to apply the properties to.
	 */
	function apply(g:Graphics):void;
}

}
