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
 *  The skin for the track in a ScrollBar.
 */
public class ScrollTrackSkin extends Border
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
		var bevel:Boolean = getStyle("bevel");
		var fillColors:Array = getStyle("trackColors");
		StyleManager.getColorNames(fillColors);
		
		var borderColor:uint =
			ColorUtil.adjustBrightness2(getStyle("borderColor"), 40);
		
		var borderColorDrk2:uint =
			ColorUtil.adjustBrightness2(borderColor, -30);
		
		graphics.clear();
		
		if (bevel)
		{
			// border
			drawRoundRect(
				0, 0, w, h, 0,
				[ borderColorDrk2, borderColor], 1,
				horizontalGradientMatrix(0, 0, w, h));
		}
		else
		{
			// border
			drawRoundRect(
				0, 0, w, h, 0,
				borderColor, 1);
		}
		
		// fill
		drawRoundRect(
			1, 1, w - 2, h - 2, 0,
			fillColors, 1, 
			horizontalGradientMatrix(1, 1, w / 3 * 2, h - 2)); 
	}
}

}
