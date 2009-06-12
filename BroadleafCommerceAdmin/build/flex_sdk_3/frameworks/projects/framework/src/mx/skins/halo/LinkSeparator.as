////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.Graphics;
import flash.utils.getQualifiedClassName;
import flash.utils.describeType;

import mx.containers.BoxDirection;
import mx.skins.ProgrammaticSkin;

/**
 *  The skin for the separator between the Links in a LinkBar.
 */
public class LinkSeparator extends ProgrammaticSkin
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function LinkSeparator()
	{
		super();
	}
		
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override protected function updateDisplayList(w:Number, h:Number):void
	{
		super.updateDisplayList(w, h);

		var separatorColor:uint = getStyle("separatorColor");
		var separatorWidth:Number = getStyle("separatorWidth");
		
		var isVertical:Boolean = false;
		
		var g:Graphics = graphics;
				
		g.clear();
		
		if (separatorWidth > 0)
		{
			if (isBox(parent))
				isVertical = Object(parent).direction == BoxDirection.VERTICAL;
			
			g.lineStyle(separatorWidth, separatorColor);
			if (isVertical)
			{
				g.moveTo(4, h / 2);
				g.lineTo(w - 4, h / 2);
			}
			else
			{
				g.moveTo(w / 2, 6);
				g.lineTo(w / 2, h - 5);
			}
		}
	}

	/**
	 *  We don't use 'is' to prevent dependency issues
	 */
	static private var boxes:Object = {};

	static private function isBox(parent:Object):Boolean
	{
		var s:String = getQualifiedClassName(parent);
		if (boxes[s] == 1)
			return true;

		if (boxes[s] == 0)
			return false;

		if (s == "mx.containers::Box")
		{
			boxes[s] == 1;
			return true;
		}

		var x:XML = describeType(parent);
		var xmllist:XMLList = x.extendsClass.(@type == "mx.containers::Box");
		if (xmllist.length() == 0)
		{
			boxes[s] = 0;
			return false;
		}
		
		boxes[s] = 1;
		return true;
	}
}

}
