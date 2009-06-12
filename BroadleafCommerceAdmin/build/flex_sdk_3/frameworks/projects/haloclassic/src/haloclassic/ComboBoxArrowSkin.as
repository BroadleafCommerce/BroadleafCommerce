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

package haloclassic
{

import flash.display.Graphics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the button in a ComboBox.
 */
public class ComboBoxArrowSkin extends Border
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
			
			// Cross-component styles
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// ComboBoxArrow-unique styles.
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
        return 18;
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
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub.
		var arrowColor:uint = 0x111111;
		
		// The dropdownBorderColor is currently only used
		// when displaying an error state.
		var errorBorderCol:Number = getStyle("dropdownBorderColor");
		if (!isNaN(errorBorderCol))
			borderColor = errorBorderCol;
		
		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);
		
		var cornerRadius1:Number = Math.max(cornerRadius - 1, 0);
		var cr:Object = { tl: 0, tr: cornerRadius, bl: 0, br: cornerRadius};
		var cr1:Object = { tl: 0, tr: cornerRadius1, bl: 0, br: cornerRadius1};
		
		var arrowOnly:Boolean = true;

		// If our width > 18, we're drawing the whole combo box, not just the arrow
		if (width > 18)
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
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, cr,
						[ borderColor, derStyles.borderColorDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h));
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2));
					
					// button fill
					drawRoundRect(
						1, 2, w - 2, h - 3, cr1,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 3, h - 2));
					
					if (!arrowOnly) 
					{
						// line
						drawRoundRect(
							w - 17, 4, 1, h - 8, 0,
							0xFFFFFF, 0.40);
						
						// line
						drawRoundRect(
							w - 18, 4, 1, h - 8, 0,
							0x000000, 0.35);
					}
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, cr,
						borderColor, 1);
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2));
					
					if (!arrowOnly)
					{
						// line
						drawRoundRect(
							w - 18, 4, 1, h - 8, 0,
							borderColor, 1); 
					}
				}
				break;
			}
			
			case "overSkin":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, cr,
						[ derStyles.themeColDrk2,
						  derStyles.themeColDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h));
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2));
					
					// button fill
					drawRoundRect(
						1, 2, w - 2, h - 3, cr1,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 3, h - 3));
					
					if (!arrowOnly)
					{
						// line
						drawRoundRect(
							w - 17, 4, 1, h - 8, 0,
							0xFFFFFF, 0.40);
						
						// line
						drawRoundRect(
							w - 18, 4, 1, h - 8, 0,
							0x000000, 0.35);
					}
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk2,1);
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2));
					
					if (!arrowOnly)
					{
						// line
						drawRoundRect(
							w - 18, 4, 1, h - 8, 0,
							derStyles.themeColDrk2, 1);
					}
				}
				break;
			}
			
			case "downSkin":
			{
				// border
				drawRoundRect(
					0, 0, w, h, cr,
					[ derStyles.themeColDrk2,
					  derStyles.themeColDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					[ derStyles.fillColorPress2,
					  derStyles.fillColorPress1 ], 1,
					verticalGradientMatrix(0, 0, w - 2, h - 2));
				
				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 18, 4, 1, h - 8, 0,
						derStyles.themeColDrk1, 1); 
				}

				break;
			}
			
			case "disabledSkin":
			{
				// border
				drawRoundRect(
					0, 0, w, h, cr,
					0x999999, 0.50); 
				
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					0xFFFFFF, 0.50); 
				
				if (!arrowOnly)
				{
					// line
					drawRoundRect(
						w - 18, 4, 1, h - 8, 0,
						0x999999, 0.50); 
				}
				
				arrowColor = 0x919999;
				
				break;
			}
		}
		
		// Draw the triangle.
		g.beginFill(arrowColor);
		g.moveTo(w - 9, h / 2 + 2);
		g.lineTo(w - 12, h / 2 - 2);
		g.lineTo(w - 6, h / 2 - 2);
		g.lineTo(w - 9, h / 2 + 2);
		g.endFill();
	}
}

}
