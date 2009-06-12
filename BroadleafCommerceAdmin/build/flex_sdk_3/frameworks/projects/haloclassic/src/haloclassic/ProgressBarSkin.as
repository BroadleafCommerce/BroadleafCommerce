////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package haloclassic
{

import flash.display.Graphics;
import mx.skins.Border;
import mx.utils.ColorUtil;

/**
 *  The skin for a ProgressBar.
 */
public class ProgressBarSkin extends Border
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
	public function ProgressBarSkin()
	{
		super();		
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  measuredWidth
    //----------------------------------
    
    /**
     *  @private
     */    
    override public function get measuredWidth():Number
    {
        return 200;
    }
    
    //----------------------------------
	//  measuredHeight
    //----------------------------------
    
    /**
     *  @private
     */        
    override public function get measuredHeight():Number
    {
        return 6;
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

		// User-defined styles
		var barColor:Object = getStyle("barColor");
		if (!barColor)
			barColor = getStyle("themeColor"); 
		
		// default fill color for halo uses theme color
		var fillColors:Array = [ barColor, barColor ]; 
		
		var g:Graphics = graphics;
		
		g.clear();
				
		// Glow
		drawRoundRect(
			0, 0, w, h, 0,
			fillColors, 0.50,
			verticalGradientMatrix(0, 0, w - 2, h - 2));
		
		// Fill
		drawRoundRect(
			1, 1, w - 2, h - 2, 0,
			fillColors, 1,
			verticalGradientMatrix(0, 0, w - 2, h - 2));
	}
}

}
