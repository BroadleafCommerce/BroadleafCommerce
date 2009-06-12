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

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.geom.Matrix;
import flash.geom.Rectangle;
import flash.utils.getDefinitionByName;

/** 
 *  Defines a set of values used to fill an area on screen
 *  with a bitmap or other DisplayObject.
 *  
 *  @see mx.graphics.IFill
 *  @see flash.display.Bitmap
 *  @see flash.display.BitmapData
 *  @see flash.display.DisplayObject
 */
public class BitmapFill implements IFill
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

 	/**
	 *  Constructor.
 	 */
	public function BitmapFill()
 	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var bitmapData:BitmapData;

	/**
	 *  @private
	 */
	private var matrix:Matrix;	
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  centerX
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  The horizontal origin for the bitmap fill.
	 *  The bitmap fill is offset so that this point appears at the origin.
	 *  Scaling and rotation of the bitmap are performed around this point.
	 *
	 *  @default 0 
	 */
	public var originX:Number = 0;
	
	//----------------------------------
	//  centerY
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  The vertical origin for the bitmap fill.
	 *  The bitmap fill is offset so that this point appears at the origin.
	 *  Scaling and rotation of the bitmap are performed around this point.
	 *
	 *  @default 0 
	 */
	public var originY:Number = 0;

	//----------------------------------
	//  offsetX
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  How far the bitmap is horizontally offset from the origin.
	 *  This adjustment is performed after rotation and scaling.
	 *
	 *  @default 0
	 */
	public var offsetX:Number = 0;

	//----------------------------------
	//  offsetY
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  How far the bitmap is vertically offset from the origin.
	 *  This adjustment is performed after rotation and scaling.
	 *
	 *  @default 0
	 */
	public var offsetY:Number = 0;

	//----------------------------------
	//  repeat
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  Whether the bitmap is repeated to fill the area.
	 *  Set to <code>true</code> to cause the fill to tile outward
	 *  to the edges of the filled region.
	 *  Set to <code>false</code> to end the fill at the edge of the region.
	 *
	 *  @default true
	 */
	public var repeat:Boolean = true;

	//----------------------------------
	//  rotation
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  The number of degrees to rotate the bitmap.
	 *  Valid values range from 0.0 to 360.0.
	 *  
	 *  @default 0
	 */
	public var rotation:Number = 0.0;

	//----------------------------------
	//  scaleX
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  The percent to horizontally scale the bitmap when filling,
	 *  from 0.0 to 1.0.
	 *  If 1.0, the bitmap is filled at its natural size.
	 *
	 *  @default 1.0
	 */
	public var scaleX:Number = 1.0;

	//----------------------------------
	//  scaleY
	//----------------------------------
	
	[Inspectable(category="General")]	
	
	/**
	 *  The percent to vertically scale the bitmap when filling,
	 *  from 0.0 to 1.0.
	 *  If 1.0, the bitmap is filled at its natural size.
	 *
	 *  @default 1.0 
	 */
	public var scaleY:Number = 1.0;

	//----------------------------------
	//  source
	//----------------------------------

    [Inspectable(category="General")]

	/**
	 *  The source used for the bitmap fill.
	 *  The fill can render from various graphical sources,
	 *  including the following: 
	 *  <ul>
	 *   <li>A Bitmap or BitmapData instance.</li>
	 *   <li>A class representing a subclass of DisplayObject.
	 *   The BitmapFill instantiates the class
	 *   and creates a bitmap rendering of it.</li>
	 *   <li>An instance of a DisplayObject.
	 *   The BitmapFill copies it into a Bitmap for filling.</li>
	 *   <li>The name of a subclass of DisplayObject.
	 *   The BitmapFill loads the class, instantiates it, 
	 *   and creates a bitmap rendering of it.</li>
	 *  </ul>
	 *
	 *  @default null
	 */
	public function get source():Object 
	{
		return bitmapData;
	}
	
	/**
	 *  @private
	 */
	public function set source(value:Object):void
	{
		var tmpSprite:DisplayObject;
		
		if (value is BitmapData)
		{
			bitmapData = BitmapData(value);
			return;
		}

		if (value is Class)
		{
			var cls:Class = Class(value);
			tmpSprite = new cls();
		}
		else if (value is Bitmap)
		{
			bitmapData = value.bitmapData;
		}
		else if (value is DisplayObject)
		{
			tmpSprite = value as DisplayObject;
		}
		else if (value is String)
		{
			var tmpClass:Class = Class(getDefinitionByName(String(value)));
			tmpSprite = new tmpClass();
		}
		else
		{
			return;
		}
			
		if (!bitmapData && tmpSprite)
		{
			bitmapData = new BitmapData(tmpSprite.width, tmpSprite.height);
			bitmapData.draw(tmpSprite, new Matrix());
		}
	}

	//----------------------------------
	//  smooth
	//----------------------------------

	[Inspectable(category="General")]	
	
	/**
	 *  A flag indicating whether to smooth the bitmap data
	 *  when filling with it.
	 *
	 *  @default false
	 */
	public var smooth:Boolean = false;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public function begin(target:Graphics, rc:Rectangle):void
	{
		buildMatrix();
		
		if (!bitmapData)
			return;
		
		target.beginBitmapFill(bitmapData, matrix, repeat, smooth);
	}
	
	/**
	 *  @private
	 */
	public function end(target:Graphics):void
	{
		target.endFill();
	}

	/**
	 *  @private
	 */
	private function buildMatrix():void
	{
		matrix = new Matrix();

		matrix.translate(-originX, -originY);
		matrix.scale(scaleX, scaleY);
		matrix.rotate(rotation);
		matrix.translate(offsetX, offsetY);
	}
}

}
