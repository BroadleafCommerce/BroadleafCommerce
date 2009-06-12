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
import flash.display.Graphics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of a thumb in a Slider.
 */
public class SliderThumbSkin extends Border
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
			
			// Cross-Component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// SliderThumb-unique styles.
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -50);
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
	public function SliderThumbSkin()
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
	
        /**
	 * For internal use.
	 */
	protected function drawThumbState(w:Number, h:Number, borderColors:Array, fillColors:Array, fillAlphas:Array, drawBacking:Boolean, drillHole:Boolean):void
	{
		var g:Graphics = graphics;
	
		var down:Boolean = getStyle("invertThumbDirection");
		
		var h0:Number = down ? h : 0;
		var h1:Number = down ? h - 1 : 1;
		var h2:Number = down ? h - 2 : 2;
		var hhm2:Number = down ? 2 : h - 2;
		var hhm1:Number = down ? 1 : h - 1;
		var hh:Number = down ? 0 : h;
		
		// if we are inverting, then swap the direction of the colors
		if (down)
		{
			borderColors = [borderColors[1], borderColors[0]];
			fillColors = [fillColors[1], fillColors[0]];
			fillAlphas = [fillAlphas[1], fillAlphas[0]];	
		}
		
		// backing - for opacity
		if (drawBacking)
		{
			g.beginGradientFill(GradientType.LINEAR,
								[ 0xFFFFFF, 0xFFFFFF ],
								[ 0.6, 0.6 ], 
								[ 0, 0xFF ],
								verticalGradientMatrix(0, 0, w, h));
			g.moveTo(w / 2, h0);
			g.curveTo(w / 2, h0, w / 2 - 2, h2);
			g.lineTo(0, hhm2);
			g.curveTo(0, hhm2, 2, hh);
			g.lineTo(w - 2, hh);
			g.curveTo(w - 2, hh, w, hhm2);
			g.lineTo(w / 2 + 2, h2);
			g.curveTo(w / 2 + 2, h2, w / 2, h0);
			g.endFill();
		}

		// border 
		g.beginGradientFill(GradientType.LINEAR,
							borderColors,
							[ 1.0, 1.0 ], 
							[ 0, 0xFF ],
							verticalGradientMatrix(0, 0, w, h));
		g.moveTo(w / 2, h0);
		g.curveTo(w / 2, h0, w / 2 - 2, h2);
		g.lineTo(0, hhm2);
		g.curveTo(0, hhm2, 2, hh);
		g.lineTo(w - 2, hh);
		g.curveTo(w - 2, hh, w, hhm2);
		g.lineTo(w / 2 + 2, h2);
		g.curveTo(w / 2 + 2, h2, w / 2, h0);
		
		if (drillHole)
		{
			// drillhole
			g.moveTo(w / 2, h1);
			g.curveTo(w / 2, h0, w / 2 - 1, h2);
			g.lineTo(1, hhm1);
			g.curveTo(1, hhm1, 1, hhm1);
			g.lineTo(w - 1, hhm1);
			g.curveTo(w - 1, hhm1, w - 1, hhm2);
			g.lineTo(w / 2 + 1, h2);
			g.curveTo(w / 2 + 1, h2, w / 2, h1);
			g.endFill();
		}
		
		// fill
		g.beginGradientFill(GradientType.LINEAR,
							fillColors,
							fillAlphas, 
							[ 0, 0xFF ],
							verticalGradientMatrix(0, 0, w, h));
		g.moveTo(w / 2, h1);
		g.curveTo(w / 2, h0, w/2 - 1, h2);
		g.lineTo(1, hhm1);
		g.curveTo(1, hhm1, 1, hhm1);
		g.lineTo(w - 1, hhm1);
		g.curveTo(w - 1, hhm1, w - 1, hhm2);
		g.lineTo(w / 2 + 1, h2);
		g.curveTo(w / 2 + 1, h2, w / 2, h1);
		g.endFill();				
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
		var fillAlphas:Array = getStyle("fillAlphas");
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
				drawThumbState(w, h, 
							   [ borderColor, derStyles.borderColorDrk1 ], 
							   [ fillColors[0], fillColors[1] ], 
							   [ fillAlphas[0], fillAlphas[1] ], 
							   true,
							   true);
				break;
			}

			case "thumbOverSkin":
			{
				var overFillColors:Array;
				if (fillColors.length > 2)
					overFillColors = [ fillColors[2], fillColors[3] ];
				else
					overFillColors = [ fillColors[0], fillColors[1] ];

				var overFillAlphas:Array;
				if (fillAlphas.length > 2)
					overFillAlphas = [ fillAlphas[2], fillAlphas[3] ];
  				else
					overFillAlphas = [ fillAlphas[0], fillAlphas[1] ];
					
				drawThumbState(w, h, 
				               [ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 
				               overFillColors, 
				               overFillAlphas, 
				               true,
				               true);
				break;
			}
			
			case "thumbDownSkin":
			{
				drawThumbState(w, h, 
							   [ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 
							   [ derStyles.fillColorPress1, derStyles.fillColorPress2 ],
							   [ 1.0, 1.0 ],
							   true,
							   false);
				break;
			}
			
			case "thumbDisabledSkin":
			{
				drawThumbState(w, h,
							   [ borderColor, derStyles.borderColorDrk1 ],
							   [ fillColors[0], fillColors[1] ],
							   [ Math.max(0, fillAlphas[0] - 0.15), Math.max(0, fillAlphas[1] - 0.15) ],
							   false,
							   false);
				break;
			}
		}
	}
}

}
