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
import mx.skins.ProgrammaticSkin;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for a TitleWindow.
 */
public class TitleBackground extends ProgrammaticSkin
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
	public function TitleBackground()
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

		var cornerRadius:Number = getStyle("cornerRadius");
		var headerColors:Array = getStyle("headerColors");
        StyleManager.getColorNames(headerColors);
		
		var g:Graphics = graphics;
		
		g.clear();
		
		if (h < 3)
			return;
	
		if (cornerRadius > 0)
			cornerRadius--;
		
		// bottom
		g.lineStyle(0, headerColors[0], 100);
		g.moveTo(0, h);
		g.lineTo(w, h);
		g.lineStyle(0, 0, 0);

		drawRoundRect(
			0, 0, w, h,
			{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
			headerColors, 1,
			verticalGradientMatrix(0, 0, w, h));
		
		if (headerColors.length > 1 && headerColors[0] != headerColors[1])
		{
			drawRoundRect(
				0, 0, w, h,
				{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
				[ 0xFFFFFF, 0xFFFFFF ], [ 0.80, 0.20 ],
				verticalGradientMatrix(0, 0, w, h));
			
			drawRoundRect(
				1, 1, w - 2, h - 2,
				{ tl: cornerRadius - 1, tr: cornerRadius - 1, bl: 0, br: 0 },
				headerColors, 1,
				verticalGradientMatrix(0, 0, w, h));
		}
	}	
}

}
