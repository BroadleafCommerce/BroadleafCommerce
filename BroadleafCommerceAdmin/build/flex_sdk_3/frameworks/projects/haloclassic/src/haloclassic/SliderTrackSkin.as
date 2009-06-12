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

import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for the track in a Slider.
 */
public class SliderTrackSkin extends Border 
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
	public function SliderTrackSkin()
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
	 *  The preferred width of this object.
	 */
	override public function get measuredWidth():Number
	{
		return 200;
	}

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  The preferred height of this object.
	 */
	override public function get measuredHeight():Number
	{
		return 4;
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
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var trackColors:Array = getStyle("trackColors");
		StyleManager.getColorNames(trackColors);

		var borderColorDrk:Number =
			ColorUtil.adjustBrightness2(borderColor, -50);
		
		graphics.clear();
		
		if (bevel)
		{
			drawRoundRect(
				0, 0, w, h, 0,
				borderColorDrk, 1);

			drawRoundRect(
				1, 1, w - 1, h - 1, 0,
				borderColor, 1);
		}
		else
		{
			drawRoundRect(
				0, 0, w, h, 0,
				borderColor, 1);
		}
		
		drawRoundRect(
			1, 1, w - 2, h - 2, 0,
			trackColors, 1,
			verticalGradientMatrix(0, 0, w - 2, h - 2));
	}
}

}
