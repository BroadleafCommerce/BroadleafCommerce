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

package haloclassic
{

import flash.display.Graphics;
import mx.core.SpriteAsset;

// These are placeholder skins that should be used for testing purposes only

[ExcludeClass]

/**
 *  @private
 */
public class BasicX extends SpriteAsset
{
	include "../mx/core/Version.as";

	public function BasicX(col:Number)
	{
		color = col;
		drawX(10, 10);
		super();
		measuredWidth = measuredHeight = 10;
	}
	
	private var color:uint;
	private var _measuredWidth:Number;
	private var _measuredHeight:Number;
	
	override public function get measuredWidth():Number
	{
		return _measuredWidth;
	}
	
	public function set measuredWidth(value:Number):void
	{
		_measuredWidth = value;
	}
	
	override public function get measuredHeight():Number
	{
		return _measuredHeight;
	}
	
	public function set measuredHeight(value:Number):void
	{
		_measuredHeight = value;
	}
	
	override public function setActualSize(newWidth:Number, newHeight:Number):void
	{
		drawX(newWidth, newHeight);
	}
	
	private function drawX(w:Number, h:Number, noEvent:Boolean = false):void
	{
		var g:Graphics = graphics;
		
		g.clear();
		g.beginFill(0xFFFFFF);
		g.lineStyle(1, color);
		g.drawRect(0, 0, w, h);
		g.endFill();
		g.moveTo(0, 0);
		g.lineTo(w, h);
		g.moveTo(0, h + 1);
		g.lineTo(w + 1, 0);
	}
}

[ExcludeClass]

/**
 *  @private
 */
public class RedX extends BasicX
{
	include "../mx/core/Version.as";

	public function RedX()
	{
		super(0xFF0000);
	}
}

[ExcludeClass]

/**
 *  @private
 */
public class GreenX extends BasicX
{
	include "../mx/core/Version.as";

	public function GreenX()
	{
		super(0x00FF00);
	}
}

[ExcludeClass]

/**
 *  @private
 */
public class BlueX extends BasicX
{
	include "../mx/core/Version.as";

	public function BlueX()
	{
		super(0x0000FF);
	}
}

[ExcludeClass]

/**
 *  @private
 */
public class BlackX extends BasicX
{
	include "../mx/core/Version.as";

	public function BlackX()
	{
		super(0x000000);
	}
}

}
