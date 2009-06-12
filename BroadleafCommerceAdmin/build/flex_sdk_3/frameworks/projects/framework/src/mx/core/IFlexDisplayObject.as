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

package mx.core
{

import flash.accessibility.AccessibilityProperties;
import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.IBitmapDrawable;
import flash.display.LoaderInfo;
import flash.display.Stage;
import flash.events.IEventDispatcher;
import flash.geom.Rectangle;
import flash.geom.Point;
import flash.geom.Transform;

/**
 *  The IFlexDisplayObject interface defines the interface for skin elements.
 *  At a minimum, a skin must be a DisplayObject and implement this interface.
 */
public interface IFlexDisplayObject extends IBitmapDrawable, IEventDispatcher
{

include "IDisplayObjectInterface.as"

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------


	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  The measured height of this object.
	 *
	 *  <p>This is typically hard-coded for graphical skins
	 *  because this number is simply the number of pixels in the graphic.
	 *  For code skins, it can also be hard-coded
	 *  if you expect to be drawn at a certain size.
	 *  If your size can change based on properties, you may want
	 *  to also be an ILayoutManagerClient so a <code>measure()</code>
	 *  method will be called at an appropriate time,
	 *  giving you an opportunity to compute a <code>measuredHeight</code>.</p>
	 */
	function get measuredHeight():Number;

	//----------------------------------
	//  measuredWidth
	//----------------------------------

	/**
	 *  The measured width of this object.
	 *
	 *  <p>This is typically hard-coded for graphical skins
	 *  because this number is simply the number of pixels in the graphic.
	 *  For code skins, it can also be hard-coded
	 *  if you expect to be drawn at a certain size.
	 *  If your size can change based on properties, you may want
	 *  to also be an ILayoutManagerClient so a <code>measure()</code>
	 *  method will be called at an appropriate time,
	 *  giving you an opportunity to compute a <code>measuredHeight</code>.</p>
	 */
	function get measuredWidth():Number;


	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Moves this object to the specified x and y coordinates.
	 * 
	 *  @param x The new x-position for this object.
	 * 
	 *  @param y The new y-position for this object.
	 */
	function move(x:Number, y:Number):void;

	/**
	 *  Sets the actual size of this object.
	 *
	 *  <p>This method is mainly for use in implementing the
	 *  <code>updateDisplayList()</code> method, which is where
	 *  you compute this object's actual size based on
	 *  its explicit size, parent-relative (percent) size,
	 *  and measured size.
	 *  You then apply this actual size to the object
	 *  by calling <code>setActualSize()</code>.</p>
	 *
	 *  <p>In other situations, you should be setting properties
	 *  such as <code>width</code>, <code>height</code>,
	 *  <code>percentWidth</code>, or <code>percentHeight</code>
	 *  rather than calling this method.</p>
	 * 
	 *  @param newWidth The new width for this object.
	 * 
	 *  @param newHeight The new height for this object.
	 */
	function setActualSize(newWidth:Number, newHeight:Number):void;
}

}
