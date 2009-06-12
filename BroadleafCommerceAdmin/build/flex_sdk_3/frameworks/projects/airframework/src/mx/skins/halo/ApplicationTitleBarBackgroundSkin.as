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

package mx.skins.halo
{

import mx.skins.ProgrammaticSkin;
import mx.styles.StyleManager;

/**
 *  The skin for the TitleBar of a WindowedApplication or Window.
 * 
 *  @playerversion AIR 1.1
 */
public class ApplicationTitleBarBackgroundSkin extends ProgrammaticSkin
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
	public function ApplicationTitleBarBackgroundSkin()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Programmatic Skin
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
									  			  unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);
		
		var cornerRadius:Number = getStyle("cornerRadius");
		var titleBarColors:Array = getStyle("titleBarColors");
		StyleManager.getColorNames(titleBarColors);
		graphics.clear();
		drawRoundRect(
			0, 0, unscaledWidth, unscaledHeight, {tl: cornerRadius, 
			tr: cornerRadius, bl: 0, br: 0},
			titleBarColors, [ 1.0, 1.0 ],
			verticalGradientMatrix(0, 0, unscaledWidth, unscaledHeight));
		graphics.lineStyle(1, 0xFFFFFF, 0.2);
		graphics.moveTo(0, unscaledHeight - 1);
		graphics.lineTo(0, cornerRadius);
		graphics.curveTo(0, 0, cornerRadius, 0);
		graphics.lineTo(unscaledWidth-1 - cornerRadius, 0);
		graphics.curveTo(unscaledWidth-1, 0, unscaledWidth - 1, cornerRadius);
		graphics.lineTo(unscaledWidth-1, unscaledHeight - 1);
		graphics.moveTo(0, unscaledHeight - 1);
		graphics.lineStyle(1, 0x000000, 0.35);
		graphics.lineTo(unscaledWidth, unscaledHeight - 1);
		
	}
}

}
