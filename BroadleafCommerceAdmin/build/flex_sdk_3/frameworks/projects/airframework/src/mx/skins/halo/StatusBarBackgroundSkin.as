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

/**
 *  The skin for the StatusBar of a WindowedApplication or Window.
 * 
 *  @playerversion AIR 1.1
 */
public class StatusBarBackgroundSkin extends ProgrammaticSkin
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
	public function StatusBarBackgroundSkin()
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
		
		graphics.clear();
		drawRoundRect(
			0, 0, unscaledWidth, unscaledHeight, null,
			getStyle("statusBarBackgroundColor"), 1.0);
		graphics.moveTo(0, 0);
		graphics.lineStyle(1, 0x000000, 0.35);
		graphics.lineTo(unscaledWidth, 0);
	}
}

}
