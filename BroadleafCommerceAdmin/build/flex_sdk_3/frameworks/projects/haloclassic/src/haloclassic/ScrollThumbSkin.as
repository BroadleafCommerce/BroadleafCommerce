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
 *  The skin for all the states of the thumb in a ScrollBar.
 */
public class ScrollThumbSkin extends Border
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
			var o:Object = cache[key] = {}
			
			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// ScrollArrowDown-specific styles.
			o.borderColorDrk2 = ColorUtil.adjustBrightness2(borderColor, -30);
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
	public function ScrollThumbSkin()
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
		return 15;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
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

		// User-defined styles.
		var bevel:Boolean = getStyle("bevel");
		var radius:Number = Math.max(getStyle("cornerRadius") - 1, 0);
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub.
		var gripColor:uint = 0x6F7777;
		
		// Derived styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);

		var borderColor:uint = ColorUtil.adjustBrightness2(
			getStyle("borderColor"), 40);

		var cornerRadius:Object = [ 0, radius, 0, radius ]; // tl, tr, bl, br
		var cornerRadius2:Array = [];
		cornerRadius2[0] = Math.max(cornerRadius[0] - 1, 0);
		cornerRadius2[1] = Math.max(cornerRadius[1] - 1, 0);
		cornerRadius2[2] = Math.max(cornerRadius[2] - 1, 0);
		cornerRadius2[3] = Math.max(cornerRadius[3] - 1, 0);

		graphics.clear();

		switch (name)
		{
			default:
			case "thumbUpSkin":
			{
				// positioning placeholder
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0); 
				
				if (bevel)
				{
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ borderColor, derStyles.borderColorDrk2 ], 1,
						horizontalGradientMatrix(0, 0, w / 2, h)); 
					
					// bevel highlight edge
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						horizontalGradientMatrix(0, 0, w - 2, h - 2)); 
					
					// fill
					drawRoundRect(
						3, 2, w - 4, h - 3,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ fillColors[0], fillColors[1] ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						borderColor, 1); 
					
					// fill
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ fillColors[0], fillColors[1] ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 3)); 
				}
				break;
			}
			
			case "thumbOverSkin":
			{
				// positioning placeholder
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0); 
				
				if (bevel)
				{
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ borderColor, derStyles.themeColDrk1 ], 1,
						horizontalGradientMatrix(0, 0, w / 2, h)); 
					
					// border left cover
					drawRoundRect(
						1, 0, 1, h, 0,
						borderColor, 1); 
					
					// bevel highlight edge
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						horizontalGradientMatrix(0, 0, w, h)); 
					
					// fill
					drawRoundRect(
						3, 2, w - 4, h - 3,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						horizontalGradientMatrix(1, 0, w, h)); 
				}
				else
				{
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						derStyles.themeColDrk2, 1); 
					
					// border left cover
					drawRoundRect(
						1, 0, 1, h, 0,
						borderColor, 1); 
					
					// fill
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						horizontalGradientMatrix(1, 0, w, h)); 
				}
				break;
			}
			
			case "thumbDownSkin":
			{
				if (bevel)
				{
					// positioning placeholder
					drawRoundRect(
						0, 0, w, h, 0,
						0xFFFFFF, 0); 
					
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ borderColor, derStyles.themeColDrk1], 1,
						horizontalGradientMatrix(0, 0, w / 2, h)); 
					
					// border left cover
					drawRoundRect(
						1, 0, 1, h, 0,
						borderColor, 1); 
					
					// fill
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						1, 0, w - 1, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						derStyles.themeColDrk2, 1); 
					
					// border left cover
					drawRoundRect(
						1, 0, 1, h, 0,
						borderColor, 1); 
					
					// fill
					drawRoundRect(
						2, 1, w - 3, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						horizontalGradientMatrix(1, 0, w, h)); 
				}
				break;
			}
			
			case "thumbDisabledSkin":
			{
				// positioning placeholder
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0); 
				
				// border
				drawRoundRect(
					1, 0, w - 1, h,
					{ tl: cornerRadius[0], tr: cornerRadius[1],
					  bl: cornerRadius[2], br: cornerRadius[3] },
					0x999999, 0.50);
				
				// fill
				drawRoundRect(
					2, 1, w - 3, h - 2,
					{ tl: cornerRadius2[0], tr: cornerRadius2[1],
					  bl: cornerRadius2[2], br: cornerRadius2[3] },
					0xFFFFFF, 0.50);
				
				break;
			}
		}
		
		// Draw grip
		
		drawRoundRect(
			Math.floor(w / 2 - 3), Math.floor(h / 2 - 2), 6, 1, 0,
			0x000000, 0.40);
		
		drawRoundRect(
			Math.floor(w / 2 - 3), Math.floor(h / 2), 6, 1, 0,
			0x000000, 0.40);
		
		drawRoundRect(
			Math.floor(w / 2 - 3), Math.floor(h/2 + 2), 6, 1, 0,
			0x000000, 0.40);
		
		if (bevel)
		{
			drawRoundRect(
				Math.floor(w / 2 - 3), Math.floor(h / 2 - 3), 6, 1, 0,
				0xFFFFFF, 0.40);
			
			drawRoundRect(
				Math.floor(w / 2 - 3), Math.floor(h / 2 - 1), 6, 1, 0,
				0xFFFFFF, 0.40);
			
			drawRoundRect(
				Math.floor(w / 2 - 3), Math.floor(h / 2 + 1), 6, 1, 0,
				0xFFFFFF, 0.40);
		}
	}
}

}
