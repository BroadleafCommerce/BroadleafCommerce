////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.utils
{

import flash.display.Graphics;

/**
 *  The Graphics class is an all-static class with utility methods
 *  related to the Graphics class.
 *  You do not create instances of GraphicsUtil;
 *  instead you simply call methods such as the
 *  <code>GraphicsUtil.drawRoundRectComplex()</code> method.
 */
public class GraphicsUtil
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------
		
	/**
	 * Draws a rounded rectangle using the size of a radius to draw the rounded corners. 
	 * You must set the line style, fill, or both 
	 * on the Graphics object before 
	 * you call the <code>drawRoundRectComplex()</code> method 
	 * by calling the <code>linestyle()</code>, 
	 * <code>lineGradientStyle()</code>, <code>beginFill()</code>, 
	 * <code>beginGradientFill()</code>, or 
	 * <code>beginBitmapFill()</code> method.
	 * 
     * @param graphics The Graphics object that draws the rounded rectangle.
     *
     * @param x The horizontal position relative to the 
     * registration point of the parent display object, in pixels.
     * 
     * @param y The vertical position relative to the 
     * registration point of the parent display object, in pixels.
     * 
     * @param width The width of the round rectangle, in pixels.
     * 
     * @param height The height of the round rectangle, in pixels.
     * 
     * @param topLeftRadius The radius of the upper-left corner, in pixels.
     * 
     * @param toRightRadius The radius of the upper-right corner, in pixels.
     * 
     * @param bottomLeftRadius The radius of the bottom-left corner, in pixels.
     * 
     * @param bottomRightRadius The radius of the bottom-right corner, in pixels.
     *
     */
	public static function drawRoundRectComplex(graphics:Graphics, x:Number, y:Number, 
							  width:Number, height:Number, 
                              topLeftRadius:Number, topRightRadius:Number, 
                              bottomLeftRadius:Number, bottomRightRadius:Number):void
	{
		var xw:Number = x + width;
		var yh:Number = y + height;

		// Make sure none of the radius values are greater than w/h.
		// These are all inlined to avoid function calling overhead
		var minSize:Number = width < height ? width * 2 : height * 2;
		topLeftRadius = topLeftRadius < minSize ? topLeftRadius : minSize;
		topRightRadius = topRightRadius < minSize ? topRightRadius : minSize;
		bottomLeftRadius = bottomLeftRadius < minSize ? bottomLeftRadius : minSize;
		bottomRightRadius = bottomRightRadius < minSize ? bottomRightRadius : minSize;
		
		// Math.sin and Math,tan values for optimal performance.
		// Math.rad = Math.PI / 180 = 0.0174532925199433
		// r * Math.sin(45 * Math.rad) =  (r * 0.707106781186547);
		// r * Math.tan(22.5 * Math.rad) = (r * 0.414213562373095);
		//
		// We can save further cycles by precalculating
		// 1.0 - 0.707106781186547 = 0.292893218813453 and
		// 1.0 - 0.414213562373095 = 0.585786437626905

		// bottom-right corner
		var a:Number = bottomRightRadius * 0.292893218813453;		// radius - anchor pt;
		var s:Number = bottomRightRadius * 0.585786437626905; 	// radius - control pt;
		graphics.moveTo(xw, yh - bottomRightRadius);
		graphics.curveTo(xw, yh - s, xw - a, yh - a);
		graphics.curveTo(xw - s, yh, xw - bottomRightRadius, yh);

		// bottom-left corner
		a = bottomLeftRadius * 0.292893218813453;
		s = bottomLeftRadius * 0.585786437626905;
		graphics.lineTo(x + bottomLeftRadius, yh);
		graphics.curveTo(x + s, yh, x + a, yh - a);
		graphics.curveTo(x, yh - s, x, yh - bottomLeftRadius);

		// top-left corner
		a = topLeftRadius * 0.292893218813453;
		s = topLeftRadius * 0.585786437626905;
		graphics.lineTo(x, y + topLeftRadius);
		graphics.curveTo(x, y + s, x + a, y + a);
		graphics.curveTo(x + s, y, x + topLeftRadius, y);

		// top-right corner
		a = topRightRadius * 0.292893218813453;
		s = topRightRadius * 0.585786437626905;
		graphics.lineTo(xw - topRightRadius, y);
		graphics.curveTo(xw - s, y, xw - a, y + a);
		graphics.curveTo(xw, y + s, xw, y + topRightRadius);
		graphics.lineTo(xw, yh - bottomRightRadius);
	}
}

}
