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

package haloclassic
{

import mx.core.EdgeMetrics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for the background of a MenuBar.
 */
public class MenuBarBackgroundSkin extends Border
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
	 *  @private
	 *  Several colors used for drawing are calculated from the base colors
	 *  of the component (themeColor, borderColor and fillColors).
	 *  Since these calculations can be a bit expensive,
	 *  we calculate once per color set and cache the results.
	 */
	private static function calcDerivedStyles(themeColor:uint,
											  fillColor0:uint,
											  fillColor1:uint):Object
	{
		var key:String = HaloColors.getCacheKey(themeColor,
												fillColor0, fillColor1); 
				
		if (!cache[key])
		{
			var o:Object = cache[key] = {};
			
			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
		}
		
		return cache[key];
	}
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function MenuBarBackgroundSkin()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  borderMetrics
	//----------------------------------

	/**
	 *  @private
	 *  Internal object that contains the thickness of each edge
	 *  of the border
	 */
	private var _borderMetrics:EdgeMetrics = new EdgeMetrics(1, 1, 1, 1);

	/**
	 *  @private
	 */
	override public function get borderMetrics():EdgeMetrics
	{
		return _borderMetrics;
	}

	//----------------------------------
	//  measuredWidth
	//----------------------------------
	
	/**
	 *  @private
	 */
	override public function get measuredWidth():Number
	{
		return 50;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 22;
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

		// User-defined Styles
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");

		// Derivative Styles
		var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
												 fillColors[1]);

		var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -25);

		var cr:Number = Math.max(0, cornerRadius);
		var cr1:Number = Math.max(0, cornerRadius - 1);

		graphics.clear();
												
		if (bevel)
		{
			// button border/edge
			drawRoundRect(
				0, 0, w, h, cr,
				[ borderColor, borderColor ], 1,
				verticalGradientMatrix(0, 0, w - 2, h - 2)); 

			// top bevel highlight edge
			drawRoundRect(
				1, 1, w - 2, h - 2, cr1,
				derStyles.bevelHighlight1, 1); 

			// button fill
			drawRoundRect(
				1, 2, w - 2, h - 3, cr1,
				[ fillColors[0], fillColors[1] ], 1,
				verticalGradientMatrix(0, 0, w - 2, h - 2)); 
		}
		else // (flat button, no bevel)
		{

			// button border/edge
			drawRoundRect(
				0, 0, w, h, cr,
				borderColor, 1); 

			// button fill
			drawRoundRect(
				1, 1, w - 2, h - 2, cr1,
				[ fillColors[0], fillColors[1] ], 1,
				verticalGradientMatrix(0, 0, w - 2, h - 2)); 
		}
	}
}

}
