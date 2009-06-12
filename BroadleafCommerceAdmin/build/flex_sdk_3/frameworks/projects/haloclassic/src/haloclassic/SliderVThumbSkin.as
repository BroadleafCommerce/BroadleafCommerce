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
import flash.display.GradientType;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of a thumb in a vertical Slider.
 */
public class SliderVThumbSkin extends Border
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
											  borderColor:uint,
											  fillColor0:uint,
											  fillColor1:uint):Object
	{
		var key:String = HaloColors.getCacheKey(themeColor, borderColor,
												fillColor0, fillColor1);
		
		if (!cache[key])
		{
			var o:Object = cache[key] = {};
			
			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// SliderThumb-specific styles.
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -60);
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
	public function SliderVThumbSkin()
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
		return 12;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 12;
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
		var borderColor:uint = getStyle("borderColor");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);

		var g:Graphics = graphics;
	
		g.clear();
		
		switch (name)
		{
			case "thumbUpSkin":
			{
				// border 
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ borderColor, derStyles.borderColorDrk1 ],
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 0);
				g.curveTo(w / 2, 0, w / 2 - 2, 2);
				g.lineTo(0, h - 2);
				g.curveTo(0, h - 2, 2, h);
				g.lineTo(w - 2, h);
				g.curveTo(w - 2, h, w, h - 2);
				g.lineTo(w / 2 + 2, 2);
				g.curveTo(w / 2 + 2, 2, w / 2, 0);
				g.endFill();
				
				// fill
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ fillColors[0], fillColors[1] ], 
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 1);
				g.curveTo(w / 2, 0, w / 2 - 1, 2);
				g.lineTo(1, h - 1);
				g.curveTo(1, h - 1, 1, h - 1);
				g.lineTo(w - 1, h - 1);
				g.curveTo(w - 1, h - 1, w - 1, h - 2);
				g.lineTo(w / 2 + 1, 2);
				g.curveTo(w / 2 + 1, 2, w / 2, 1);
				g.endFill();
				
				break;
			}

			case "thumbOverSkin":
			{
				// border 
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 0);
				g.curveTo(w / 2, 0, w / 2 - 2, 2);
				g.lineTo(0, h - 2);
				g.curveTo(0, h - 2, 2, h);
				g.lineTo(w - 2, h);
				g.curveTo(w - 2, h, w, h - 2);
				g.lineTo(w / 2 + 2, 2);
				g.curveTo(w / 2 + 2, 2, w / 2, 0);
				g.endFill();
				
				// fill
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ derStyles.fillColorBright1, derStyles.fillColorBright2 ], 
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 1);
				g.curveTo(w / 2, 0, w / 2 - 1, 2);
				g.lineTo(1, h - 1);
				g.curveTo(1, h - 1, 1, h - 1);
				g.lineTo(w - 1, h - 1);
				g.curveTo(w - 1, h - 1, w - 1, h - 2);
				g.lineTo(w / 2 + 1, 2);
				g.curveTo(w / 2 + 1, 2, w / 2, 1);
				g.endFill();
				
				break;
			}
			
			case "thumbDownSkin":
			{
				// border 
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 0);
				g.curveTo(w / 2, 0, w / 2 - 2, 2);
				g.lineTo(0, h - 2);
				g.curveTo(0, h - 2, 2, h);
				g.lineTo(w - 2, h);
				g.curveTo(w - 2, h, w, h - 2);
				g.lineTo(w / 2 + 2, 2);
				g.curveTo(w / 2 + 2, 2, w / 2, 0);
				g.endFill();
				
				// fill
				g.beginGradientFill(
					GradientType.LINEAR, 
					[ derStyles.fillColorPress1, derStyles.fillColorPress2 ], 
					[ 1.0, 1.0 ], [ 0, 0xFF ],
					verticalGradientMatrix(0, 0, w, h));
				g.moveTo(w / 2, 1);
				g.curveTo(w / 2, 0, w / 2 - 1, 2);
				g.lineTo(1, h - 1);
				g.curveTo(1, h - 1, 1, h - 1);
				g.lineTo(w - 1, h - 1);
				g.curveTo(w - 1, h - 1, w - 1, h - 2);
				g.lineTo(w / 2 + 1, 2);
				g.curveTo(w / 2 + 1, 2, w / 2, 1);
				g.endFill();
				
				break;
			}
			
			case "thumbDisabledSkin":
			{
				// border 
				g.beginFill(0x999999, 0.5);
				g.moveTo(w / 2, 0);
				g.curveTo(w / 2, 0, w / 2 - 2, 2);
				g.lineTo(0, h - 2);
				g.curveTo(0, h - 2, 2, h);
				g.lineTo(w - 2, h);
				g.curveTo(w - 2, h, w, h - 2);
				g.lineTo(w / 2 + 2, 2);
				g.curveTo(w / 2 + 2, 2, w / 2, 0);
				g.endFill();
				
				// fill
				g.beginFill(0xFFFFFF, 0.5);
				g.moveTo(w / 2, 1);
				g.curveTo(w / 2, 0, w / 2 - 1, 2);
				g.lineTo(1, h - 1);
				g.curveTo(1, h - 1, 1, h - 1);
				g.lineTo(w - 1, h - 1);
				g.curveTo(w - 1, h - 1, w - 1, h - 2);
				g.lineTo(w / 2 + 1, 2);
				g.curveTo(w / 2 + 1, 2, w / 2, 1);
				g.endFill();
				
				break;
			}
		}
	}
}

}
