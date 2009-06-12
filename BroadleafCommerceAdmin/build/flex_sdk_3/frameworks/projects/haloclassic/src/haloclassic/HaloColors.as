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

import mx.utils.ColorUtil;

/**
 *  Documentation is not currently available.
 *  @review
 */
public class HaloColors 
{
	include "../mx/core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var cache:Object = {};
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Documentation is not currently available.
	 *  @review
	 */
	public static function getCacheKey(... colors):String
	{
		return colors.join(",");
	}

	/**
	 *  This function calculates several halo colors
	 *  based off 'themeColor' and 'fillColors'.
	 */
	public static function addHaloColors(colors:Object,
										 themeColor:uint,
										 fillColor0:uint,
										 fillColor1:uint):void
	{
		var key:String = getCacheKey(themeColor, fillColor0, fillColor1); 
		var o:Object = cache[key];
		
		if (!o)
		{
			o = cache[key] = {};
			
			// Cross-Component Styles
			o.themeColLgt = ColorUtil.adjustBrightness(themeColor, 100);
			o.themeColDrk1 = ColorUtil.adjustBrightness(themeColor, -75);
			o.themeColDrk2 = ColorUtil.adjustBrightness(themeColor, -25);
			o.fillColorBright1 = ColorUtil.adjustBrightness(fillColor0, 15);
			o.fillColorBright2 = ColorUtil.adjustBrightness(fillColor1, 15);
			o.fillColorPress1 = ColorUtil.adjustBrightness2(o.themeColLgt, 50);
			o.fillColorPress2 = ColorUtil.adjustBrightness2(themeColor, 50);
			o.bevelHighlight1 = ColorUtil.adjustBrightness2(fillColor0, 40);
			o.bevelHighlight2 = ColorUtil.adjustBrightness2(fillColor1, 40);
		}
		
		colors.themeColLgt = o.themeColLgt;
		colors.themeColDrk1 = o.themeColDrk1;
		colors.themeColDrk2 = o.themeColDrk2;
		colors.fillColorBright1 = o.fillColorBright1;
		colors.fillColorBright2 = o.fillColorBright2;
		colors.fillColorPress1 = o.fillColorPress1;
		colors.fillColorPress2 = o.fillColorPress2;
		colors.bevelHighlight1 = o.bevelHighlight1;
		colors.bevelHighlight2 = o.bevelHighlight2;
	}
}

}
