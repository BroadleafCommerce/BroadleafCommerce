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
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the thumb in a ScrollBar.
 */
public class ScrollThumbSkin extends Border
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
			var o:Object = cache[key] = {}
			
			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// ScrollArrowDown-specific styles.
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
		return 16;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 10;
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
		var backgroundColor:Number = getStyle("backgroundColor");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");				
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub.
		var gripColor:uint = 0x6F7777;
		
		// Derived styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);
												 
		var radius:Number = Math.max(cornerRadius - 1, 0);
		var cr:Object = { tl: 0, tr: radius, bl: 0, br: radius };
		radius = Math.max(radius - 1, 0);
		var cr1:Object = { tl: 0, tr: radius, bl: 0, br: radius };

		var horizontal:Boolean = parent &&
								 parent.parent &&
								 parent.parent.rotation != 0;

		if (isNaN(backgroundColor))
			backgroundColor = 0xFFFFFF;
		
		graphics.clear();
		
		// Opaque backing to force the scroll elements
		// to match other components by default.
		drawRoundRect(
			1, 0, w - 3, h, cr,
			backgroundColor, 1);                            

		switch (name)
		{
			default:
			case "thumbUpSkin":
			{
   				var upFillColors:Array = [ fillColors[0], fillColors[1] ];
   				
				var upFillAlphas:Array = [ fillAlphas[0], fillAlphas[1] ];

				// positioning placeholder
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0); 

				// shadow
				if (horizontal)
				{
					drawRoundRect(
						1, 0, w - 2, h, cornerRadius,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontalGradientMatrix(2, 0, w, h),
						GradientType.LINEAR, null, 
						{ x: 1, y: 1, w: w - 4, h: h - 2, r: cr1 });
				}
				else
				{
					drawRoundRect(
						1, h - radius, w - 3, radius + 4,
						{ tl: 0, tr: 0, bl: 0, br: radius },
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontal ?
						horizontalGradientMatrix(0, h - 4, w - 3, 8) :
						verticalGradientMatrix(0, h - 4, w - 3, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h-radius, w: w - 4, h: radius,
						  r: { tl: 0, tr: 0, bl: 0, br: radius - 1 } }); 
				}
				// border
				drawRoundRect(
					1, 0, w - 3, h, cr,
					[ borderColor, derStyles.borderColorDrk1 ], 1,
					horizontal ?
					horizontalGradientMatrix(0, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 4, h: h - 2, r: cr1 });  

				// fill
				drawRoundRect(
					1, 1, w - 4, h - 2, cr1,
					upFillColors, upFillAlphas,
					horizontal ?
					horizontalGradientMatrix(1, 0, w - 2, h - 2) :
					verticalGradientMatrix(1, 0, w - 2, h - 2)); 

				// highlight
				if (horizontal)
				{
					drawRoundRect(
						1, 0, (w - 4) / 2, h - 2, 0,
						[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
						horizontalGradientMatrix(1, 1, w - 4, (h - 2) / 2)); 
				}
				else
				{
					drawRoundRect(
						1, 1, w - 4, (h - 2) / 2, cr1,
						[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
						horizontal ?
						horizontalGradientMatrix(1, 0, (w - 4) / 2, h - 2) :
						verticalGradientMatrix(1, 1, w - 4, (h - 2) / 2)); 
				}
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

				// positioning placeholder
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0); 

				// shadow
				if (horizontal)
				{
					drawRoundRect(
						1, 0, w - 2, h, cornerRadius,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontalGradientMatrix(2, 0, w, h),
						GradientType.LINEAR, null, 
						{ x: 1, y: 1, w: w - 4, h: h - 2, r: cr1 });
				}
				else
				{
					drawRoundRect(
						1, h - radius, w - 3, radius + 4,
						{ tl: 0, tr: 0, bl: 0, br: radius },
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontal ?
						horizontalGradientMatrix(0, h - 4, w - 3, 8) :
						verticalGradientMatrix(0, h - 4, w - 3, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h-radius, w: w - 4, h: radius,
						  r: { tl: 0, tr: 0, bl: 0, br: radius - 1 } }); 
				}
								
				// border
				drawRoundRect(
					1, 0, w - 3, h, cr,
					[ themeColor, derStyles.themeColDrk1], 1,
					horizontal ?
					horizontalGradientMatrix(1, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
                    { x: 1, y: 1, w: w - 4, h: h - 2, r: cr1 });

				// fill
				drawRoundRect(
					1, 1, w - 4, h - 2, cr1,
					overFillColors, overFillAlphas,
					horizontal ?
					horizontalGradientMatrix(1, 0, w, h) :
					verticalGradientMatrix(1, 0, w, h)); 
				
				break;
			}
			
			case "thumbDownSkin":
			{				
				// shadow
				if (horizontal)
				{
					drawRoundRect(
						1, 0, w - 2, h, cr,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [1, 0],
						horizontalGradientMatrix(2, 0, w, h),
						GradientType.LINEAR, null, 
						{ x: 1, y: 1, w: w - 4, h: h - 2, r: cr1 }); 
				}
				else
				{
					drawRoundRect(
						1, h - radius, w - 3, radius + 4,
						{ tl: 0, tr: 0, bl: 0, br: radius },
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontal ?
						horizontalGradientMatrix(0, h - 4, w - 3, 8) :
						verticalGradientMatrix(0, h - 4, w - 3, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h-radius, w: w - 4, h: radius,
						  r: { tl: 0, tr: 0, bl: 0, br: radius - 1 } }); 
				}

				// border
				drawRoundRect(
					1, 0, w - 3, h, cr,
					[ themeColor, derStyles.themeColDrk2], 1,
					horizontal ?
					horizontalGradientMatrix(1, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
                    { x: 1, y: 1, w: w - 4, h: h - 2, r: cr1});  

				// fill
				drawRoundRect(
					1, 1, w - 4, h - 2, cr1,
					[ derStyles.fillColorPress1, derStyles.fillColorPress2 ], 1,
					horizontal ?
					horizontalGradientMatrix(1, 0, w, h) :
					verticalGradientMatrix(1, 0, w, h)); 
									
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
					1, 0, w - 3, h, cr,
					0x999999, 0.5);
				
				// fill
				drawRoundRect(
					1, 1, w - 4, h - 2, cr1,
					0xFFFFFF, 0.5);
				
				break;
			}
		}
		
		// Draw grip.
		
		var gripW:Number = Math.floor(w / 2 - 4);
		
		drawRoundRect(
			gripW, Math.floor(h / 2 - 4), 5, 1, 0,
			0x000000, 0.4);
		
		drawRoundRect(
			gripW, Math.floor(h / 2 - 2), 5, 1, 0,
			0x000000, 0.4);
		
		drawRoundRect(
			gripW, Math.floor(h / 2), 5, 1, 0,
			0x000000, 0.4);
		
		drawRoundRect(
			gripW, Math.floor(h / 2 + 2), 5, 1, 0,
			0x000000, 0.4);

		drawRoundRect(
			gripW, Math.floor(h / 2 + 4), 5, 1, 0,
			0x000000, 0.4);
	}
}

}
