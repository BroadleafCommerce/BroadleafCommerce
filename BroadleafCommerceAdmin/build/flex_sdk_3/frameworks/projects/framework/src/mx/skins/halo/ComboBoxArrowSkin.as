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
 *  The skin for all the states of the button in a ComboBox.
 */
public class ComboBoxArrowSkin extends Border
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
	private static function calcDerivedStyles(themeColor:uint, borderColor:uint,
									  fillColor0:uint, fillColor1:uint):Object
	{
		var key:String = HaloColors.getCacheKey(themeColor, borderColor,
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
	public function ComboBoxArrowSkin()
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
        return 22;
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

		// User-defined styles.
		var arrowColor:uint = getStyle("iconColor");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var dropdownBorderColor:Number = getStyle("dropdownBorderColor");
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");		
		var themeColor:uint = getStyle("themeColor");
				
		// The dropdownBorderColor is currently only used
		// when displaying an error state.
		if (!isNaN(dropdownBorderColor))
			borderColor = dropdownBorderColor;
		
		// Derivative Styles
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);
		
		var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -50);
		
		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);
		
		var cornerRadius1:Number = Math.max(cornerRadius - 1, 0);
		var cr:Object = { tl: 0, tr: cornerRadius, bl: 0, br: cornerRadius };
		var cr1:Object = { tl: 0, tr: cornerRadius1, bl: 0, br: cornerRadius1 };
		
		var arrowOnly:Boolean = true;

		// If our name doesn't include "editable", we are drawing the non-edit
		// skin which spans the entire control
		if (name.indexOf("editable") < 0)
		{
			arrowOnly = false;
			cr.tl = cr.bl = cornerRadius;
			cr1.tl = cr1.bl = cornerRadius1;
		}
		
		var g:Graphics = graphics;
		
		g.clear();
		
		// Draw the border and fill.
		switch (name)
		{
			case "upSkin":
			case "editableUpSkin":
			{
   				var upFillColors:Array = [ fillColors[0], fillColors[1] ];
   				var upFillAlphas:Array = [ fillAlphas[0], fillAlphas[1] ];
			
				// border
				drawRoundRect(
					0, 0, w, h, cr,
					[ borderColor, borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 });

				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2));
					
				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2, 
					{ tl: cornerRadius1, tr: cornerRadius1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2)); 

				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 22, 4, 1, h - 8, 0,
						borderColor, 1); 
					drawRoundRect(
						w - 21, 4, 1, h - 8, 0,
						0xFFFFFF, 0.2); 
				}
				
				break;
			}
			
			case "overSkin":
			case "editableOverSkin":
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

				// border
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 }); 
					
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					overFillColors, overFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2));
					
				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2, 
					{ tl: cornerRadius1, tr: cornerRadius1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(0, 0, w - 2, (h - 2) / 2));


				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 22, 4, 1, h - 8, 0,
						derStyles.themeColDrk2,1);
					drawRoundRect(
						w - 21, 4, 1, h - 8, 0,
						0xFFFFFF, 0.2); 
				}
				
				break;
			}
			
			case "downSkin":
			case "editableDownSkin":
			{
				// border
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					[ derStyles.fillColorPress1, derStyles.fillColorPress2 ], 1,
					verticalGradientMatrix(1, 1, w - 2, h - 2));
				
				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2, 
					{ tl: cornerRadius1, tr: cornerRadius1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2)); 

				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 22, 4, 1, h - 8, 0,
						themeColorDrk1, 1); 
					drawRoundRect(
						w - 21, 4, 1, h - 8, 0,
						0xFFFFFF, 0.2); 
				}

				break;
			}
			
			case "disabledSkin":
			case "editableDisabledSkin":
			{
   				var disFillColors:Array = [ fillColors[0], fillColors[1] ];
   				
				var disFillAlphas:Array = [ Math.max(0, fillAlphas[0] - 0.15),
											Math.max(0, fillAlphas[1] - 0.15) ];

				// border
				drawRoundRect(
					0, 0, w, h, cr,
					[ borderColor, borderColorDrk1 ], 0.5,
					verticalGradientMatrix(0, 0, w, h ),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 });

				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					disFillColors, disFillAlphas,
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				
				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 22, 4, 1, h - 8, 0,
						0x999999, 0.5); 
				}
				
				arrowColor = getStyle("disabledIconColor");
				
				break;
			}
		}
		
		// Draw the triangle.
		g.beginFill(arrowColor);
		g.moveTo(w - 11.5, h / 2 + 3);
		g.lineTo(w - 15, h / 2 - 2);
		g.lineTo(w - 8, h / 2 - 2);
		g.lineTo(w - 11.5, h / 2 + 3);
		g.endFill();
	}
}

}
