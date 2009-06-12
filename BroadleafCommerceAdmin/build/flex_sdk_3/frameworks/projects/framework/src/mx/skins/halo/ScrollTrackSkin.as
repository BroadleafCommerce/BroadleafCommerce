////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.GradientType;
import mx.core.FlexVersion;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for the track in a ScrollBar.
 */
public class ScrollTrackSkin extends Border
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
	public function ScrollTrackSkin()
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
        return 16;
    }
    
    //----------------------------------
	//  measuredHeight
    //----------------------------------
    
    /**
     *  @private
     */        
    override public function get measuredHeight():Number
    {
        return 1;
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

		// User-defined styles.
		var fillColors:Array = getStyle("trackColors");
		StyleManager.getColorNames(fillColors);
		
		var borderColor:uint =
			ColorUtil.adjustBrightness2(getStyle("borderColor"), -20);
		
		var borderColorDrk1:uint =
			ColorUtil.adjustBrightness2(borderColor, -30);
		
		graphics.clear();
		
		var fillAlpha:Number = 1;
		
		if (name == "trackDisabledSkin" && FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
			fillAlpha = .2;
		
		// border
		drawRoundRect(
			0, 0, w, h, 0,
			[ borderColor, borderColorDrk1 ], fillAlpha,
			verticalGradientMatrix(0, 0, w, h),
			GradientType.LINEAR, null,
			{ x: 1, y: 1, w: w - 2, h: h - 2, r: 0 }); 

		// fill
		drawRoundRect(
			1, 1, w - 2, h - 2, 0,
			fillColors, fillAlpha, 
			horizontalGradientMatrix(1, 1, w / 3 * 2, h - 2)); 
	}
}

}
