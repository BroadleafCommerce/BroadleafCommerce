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

package mx.skins.halo
{

import flash.display.Graphics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the next-month and previous-month
 *  buttons in a DateChooser.
 */
public class DateChooserMonthArrowSkin extends Border
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
	public function DateChooserMonthArrowSkin()
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
		return 19;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 20;
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

		// User-defined styles (from ButtonSkin.as)
		var arrowColor:uint = getStyle("iconColor");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");
		var themeColor:uint = getStyle("themeColor");

		// Derivative styles
		var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
												 fillColors[1]);

		var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -50);
		
		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);

		var cr1:Number = Math.max(0, cornerRadius - 1);
		var cr2:Number = Math.max(0, cornerRadius - 2);
		
		var g:Graphics = graphics;
	
		g.clear();
	
		switch (name)
		{
			case "prevMonthUpSkin":
			case "nextMonthUpSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr1,
					[ borderColor, borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h )); 

				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr2,
					[ fillColors[0], fillColors[1] ], 1,
					verticalGradientMatrix(1, 1, w - 2, h - 2)); 

				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2,
					{ tl: cr2, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2));
	
				break;
			}
			
			case "prevMonthOverSkin":
			case "nextMonthOverSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr1,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h)); 
												
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr2,
					[ derStyles.fillColorBright1,
					  derStyles.fillColorBright2 ], 1,
					verticalGradientMatrix(1, 1, w - 2, h - 2)); 
										  
				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2,
					{ tl: cr2, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2)); 

				break;
			}

			case "prevMonthDownSkin":
			case "nextMonthDownSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr1,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h )); 
												
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr2,
					[ derStyles.fillColorPress1, derStyles.fillColorPress2], 1,
					verticalGradientMatrix(1, 1, w - 2, h - 2)); 
										  
				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2,
					{ tl: cr2, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2));
				
				break;
			}
			
			case "prevMonthDisabledSkin":
			case "nextMonthDisabledSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr1,
					[ borderColor, borderColorDrk1 ], 0.6,
					verticalGradientMatrix(0, 0, w, h )); 

				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr2,
					[ fillColors[0], fillColors[1] ], 0.8,
					verticalGradientMatrix(1, 1, w - 2, h - 2));
						
				arrowColor = getStyle("disabledIconColor");
				
				break;
			}
		}

		// Draw the arrow.			
		g.beginFill(arrowColor);
		if (name.charAt(0) == "p")
		{
			g.moveTo(w - 8, h - 14);
			g.lineTo(w - 13, h / 2 + 0.5);
			g.lineTo(w - 8, h - 5);
			g.moveTo(w - 8, h - 14);
		}
		else
		{
			g.moveTo(w - 11, h - 14);
			g.lineTo(w - 6, h / 2 + 0.5);
			g.lineTo(w - 11, h - 5);
			g.moveTo(w - 11, h - 14);
		}
		g.endFill();
	}
}

}
