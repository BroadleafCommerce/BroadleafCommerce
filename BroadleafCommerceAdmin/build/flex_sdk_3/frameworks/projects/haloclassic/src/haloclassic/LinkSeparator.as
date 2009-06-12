////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package haloclassic
{

import flash.display.Graphics;
import mx.containers.Box;
import mx.containers.BoxDirection;
import mx.skins.ProgrammaticSkin;

/**
 *  The skin for the separator between the Links in a LinkBar.
 */
public class LinkSeparator extends ProgrammaticSkin
{
	include "../mx/core/Version.as";

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

		var separatorWidth:Number = getStyle("separatorWidth");
		var separatorColor:uint = getStyle("separatorColor");
		var isVertical:Boolean = false;
		
		var g:Graphics = graphics;
				
		g.clear();
		
		if (separatorWidth > 0)
		{
			if (parent is Box)
				isVertical = Box(parent).direction == BoxDirection.VERTICAL;
			
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
}

}
