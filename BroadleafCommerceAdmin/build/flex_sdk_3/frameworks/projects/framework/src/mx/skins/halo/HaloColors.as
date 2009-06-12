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

package mx.skins.halo
{

import mx.utils.ColorUtil;

/**
 *  Defines the colors used by components that support the Halo theme.
 */
public class HaloColors 
{
	include "../../core/Version.as";

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
	 *  Returns a unique hash key based on the colors that are
	 *  passed in. This key is used to store the calculated colors
	 *  so they only need to be calculated once.
	 *
	 *  @param colors An arbitrary number of RGB colors expressed
	 *  as <code>uint</code> values (for example, 0xFF0000).
	 */
	public static function getCacheKey(... colors):String
	{
		return colors.join(",");
	}

	/**
	 *  Calculates colors that are used by components that 
	 *  support the Halo theme, such as the colors of beveled
	 *  edges.  This method uses the <code>themeColor</code> and
	 *  <code>fillColors</code> properties to calculate its
	 *  colors.
	 * 
	 *  @param colors The object on which the calculated color
	 *  values are stored.  
	 *  
	 *  @param themeColor The value of the <code>themeColor</code>
	 *  style property.
	 * 
	 * @param fillColor0 The start color of a fill.

	 * @param fillColor1 The end color of a fill.
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
			
			// Cross-component styles
			o.themeColLgt = ColorUtil.adjustBrightness(themeColor, 100);
			o.themeColDrk1 = ColorUtil.adjustBrightness(themeColor, -75);
			o.themeColDrk2 = ColorUtil.adjustBrightness(themeColor, -25);
			o.fillColorBright1 = ColorUtil.adjustBrightness2(fillColor0, 15);
			o.fillColorBright2 = ColorUtil.adjustBrightness2(fillColor1, 15);
			o.fillColorPress1 = ColorUtil.adjustBrightness2(themeColor, 85);
			o.fillColorPress2 = ColorUtil.adjustBrightness2(themeColor, 60);
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
