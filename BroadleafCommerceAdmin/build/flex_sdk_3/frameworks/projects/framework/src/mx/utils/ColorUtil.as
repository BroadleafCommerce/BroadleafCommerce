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

package mx.utils
{

/**
 *  The ColorUtil class is an all-static class
 *  with methods for working with RGB colors within Flex.
 *  You do not create instances of ColorUtil;
 *  instead you simply call static methods such as 
 *  the <code>ColorUtil.adjustBrightness()</code> method.
 */
public class ColorUtil
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Performs a linear brightness adjustment of an RGB color.
	 *
	 *  <p>The same amount is added to the red, green, and blue channels
	 *  of an RGB color.
	 *  Each color channel is limited to the range 0 through 255.</p>
	 *
	 *  @param rgb Original RGB color.
	 *
	 *  @param brite Amount to be added to each color channel.
	 *  The range for this parameter is -255 to 255;
	 *  -255 produces black while 255 produces white.
	 *  If this parameter is 0, the RGB color returned
	 *  is the same as the original color.
	 *
	 *  @return New RGB color.
	 */
	public static function adjustBrightness(rgb:uint, brite:Number):uint
	{
		var r:Number = Math.max(Math.min(((rgb >> 16) & 0xFF) + brite, 255), 0);
		var g:Number = Math.max(Math.min(((rgb >> 8) & 0xFF) + brite, 255), 0);
		var b:Number = Math.max(Math.min((rgb & 0xFF) + brite, 255), 0);
		
		return (r << 16) | (g << 8) | b;
	} 

	/**
	 *  Performs a scaled brightness adjustment of an RGB color.
	 *
	 *  @param rgb Original RGB color.
	 *
	 *  @param brite The percentage to brighten or darken the original color.
	 *  If positive, the original color is brightened toward white
	 *  by this percentage. If negative, it is darkened toward black
	 *  by this percentage.
	 *  The range for this parameter is -100 to 100;
	 *  -100 produces black while 100 produces white.
	 *  If this parameter is 0, the RGB color returned
	 *  is the same as the original color.
	 *
	 *  @return New RGB color.
	 */
	public static function adjustBrightness2(rgb:uint, brite:Number):uint
	{
		var r:Number;
		var g:Number;
		var b:Number;
		
		if (brite == 0)
			return rgb;
		
		if (brite < 0)
		{
			brite = (100 + brite) / 100;
			r = ((rgb >> 16) & 0xFF) * brite;
			g = ((rgb >> 8) & 0xFF) * brite;
			b = (rgb & 0xFF) * brite;
		}
		else // bright > 0
		{
			brite /= 100;
			r = ((rgb >> 16) & 0xFF);
			g = ((rgb >> 8) & 0xFF);
			b = (rgb & 0xFF);
			
			r += ((0xFF - r) * brite);
			g += ((0xFF - g) * brite);
			b += ((0xFF - b) * brite);
			
			r = Math.min(r, 255);
			g = Math.min(g, 255);
			b = Math.min(b, 255);
		}
	
		return (r << 16) | (g << 8) | b;
	}

	/**
	 *  Performs an RGB multiplication of two RGB colors.
	 *  
	 *  <p>This always results in a darker number than either
	 *  original color unless one of them is white,
	 *  in which case the other color is returned.</p>
	 *
	 *  @param rgb1 First RGB color.
	 *
	 *  @param rgb2 Second RGB color.
	 *
	 *  @return RGB multiplication of the two colors.
	 */
	public static function rgbMultiply(rgb1:uint, rgb2:uint):uint
	{
		var r1:Number = (rgb1 >> 16) & 0xFF;
		var g1:Number = (rgb1 >> 8) & 0xFF;
		var b1:Number = rgb1 & 0xFF;
		
		var r2:Number = (rgb2 >> 16) & 0xFF;
		var g2:Number = (rgb2 >> 8) & 0xFF;
		var b2:Number = rgb2 & 0xFF;
		
		return ((r1 * r2 / 255) << 16) |
			   ((g1 * g2 / 255) << 8) |
			    (b1 * b2 / 255);
	} 
}

}
