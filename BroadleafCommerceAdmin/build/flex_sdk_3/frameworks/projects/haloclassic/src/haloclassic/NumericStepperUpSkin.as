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
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the up button in a NumericStepper.
 */
public class NumericStepperUpSkin extends Border
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
			
			// ComboBoxArrow-specific styles.
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -100);
			o.borderColorDrk2 = ColorUtil.adjustBrightness2(borderColor, -25);
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
	public function NumericStepperUpSkin()
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
		return 18;
	}

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  The preferred height of this object.
	 */
	override public function get measuredHeight():Number
	{
		return 11;
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

		// User-defined styles
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub.
		var arrowColor:uint = 0x111111;
		
		// Derivative styles
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);

		var cornerRadius1:Number = cornerRadius - 1;

		// Draw the background and border.
		var g:Graphics = graphics;
		
		g.clear();
		
		switch (name)
		{
			case "upArrowUpSkin":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						[ borderColor, derStyles.borderColorDrk2 ], 1,
						verticalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
					
					// button fill
					drawRoundRect(
						2, 2, w - 3, h - 3,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 3, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						borderColor, 1); 
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				break;
			}
			
			case "upArrowOverSkin":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						verticalGradientMatrix(0,0, w - 2, h - 2)); 
					
					// button fill
					drawRoundRect(
						2, 2, w - 3, h - 3,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 3, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						derStyles.themeColDrk2, 1); 
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				break;
			}
			
			case "upArrowDownSkin":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						[ derStyles.themeColDrk2,
						  derStyles.themeColDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, 
						{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
						derStyles.themeColDrk2, 1); 
				}
				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2,
					{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
					[ derStyles.fillColorPress2,
					  derStyles.fillColorPress1 ], 1,
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				
				break;
			}
			
			case "upArrowDisabledSkin":
			{
				// border
				drawRoundRect(
					0, 0, w, h,
					{ tl: 0, tr: cornerRadius, bl: 0, br: 0 },
					0x999999, 0.50); 
				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2,
					{ tl: 0, tr: cornerRadius1, bl: 0, br: 0 },
					0xFFFFFF, 0.50); 
				
				arrowColor = 0x919999;
				break;
			}
		}
		
		// Draw the arrow.
		g.beginFill(arrowColor);
		g.moveTo(w / 2, h / 2 - 2);
		g.lineTo(w / 2 - 3, h / 2 + 2);
		g.lineTo(w / 2 + 3, h / 2 + 2);
		g.lineTo(w / 2, h / 2 - 2);
		g.endFill();
	}
}

}
