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
 *  The skin for all the states of the icon in a CheckBox.
 */
public class CheckBoxIcon extends Border
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
			
			// CheckBox-specific styles.
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
	public function CheckBoxIcon()
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

		// User-defined styles
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub	
		var checkColor:uint = 0x2B333C;// added style prop
		
		// Derived styles
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor, 
												 fillColors[0], fillColors[1]);
		
		var bDrawCheck:Boolean = false;
		
		var g:Graphics = graphics;
		
		g.clear();
		
		switch (name)
		{
			//--------------------------
			// checkbox false
			//--------------------------
			
			case "upIcon":
			{
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					borderColor, 1); 
				
				if (bevel)
				{	
					// bottom right bevel edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.borderColorDrk1, 1);
					 
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.bevelHighlight1, 1); 

					// box fill
					drawRoundRect(
						1, 2, w - 2, h - 3, 0,
						[ fillColors[0], fillColors[1] ], 1, 
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				else
				{
					// box fill
					drawRoundRect(
						1, 1, w - 2, h - 2, 0,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				break;
			}
				
			case "overIcon":
			{
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					derStyles.themeColDrk2, 1); 
				
				if (bevel)
				{
					// bottom right bevel edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.themeColDrk1, 1); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.themeColLgt, 1); 
					
					// box fill
					drawRoundRect(
						1, 2, w - 2, h - 3, 0,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 4, h -4)); 
				}
				else
				{
					// box fill
					drawRoundRect(
						1, 1, w - 2, h - 2, 0,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				break;
			}

			case "downIcon":
			{				
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					derStyles.themeColDrk2, 1); 
				
				if (bevel) 
				{
					// bottom right bevel edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.themeColDrk1, 1); 
				}
				
				// box fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					[ derStyles.fillColorPress2,
					  derStyles.fillColorPress1 ], 1,
					verticalGradientMatrix(0, 0, w - 4, h - 4));
					 
				break;
			}

			case "disabledIcon":
			{
				drawRoundRect(
					0, 0, w, h, 0,
					0x999999, 0.50);

				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					0xFFFFFF, 0.50);

				break;
			}
			
			//--------------------------
			// checkbox true
			//--------------------------
			
			case "selectedUpIcon":
			{
				bDrawCheck = true;
				
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					borderColor, 1); 
				
				if (bevel)
				{
					// bottom right bevel edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.borderColorDrk1, 1); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.bevelHighlight1, 1); 
					
					// box fill
					drawRoundRect(
						1, 2, w - 2, h - 3, 0,
						[ fillColors[0], fillColors[1]], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				else
				{
					// box fill
					drawRoundRect(
						1, 1, w - 2, h - 2, 0,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				break;
			}

			case "selectedOverIcon":
			{
				bDrawCheck = true;
				
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					derStyles.themeColDrk2, 1); 
				
				if (bevel)
				{
					// bottom bevel right edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.themeColDrk1, 1); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.themeColLgt, 1); 
					
					// box fill
					drawRoundRect(
						1, 2, w - 2, h - 3, 0,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				else
				{
					// box fill
					drawRoundRect(
						1, 1, w - 2, h - 2, 0,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4)); 
				}
				break;
			}

			case "selectedDownIcon":
			{
				bDrawCheck = true;
				
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					derStyles.themeColDrk2, 1); 
				
				if (bevel)
				{
					// bottom right bevel edge
					drawRoundRect(
						1, 1, w - 1, h - 1, 0,
						derStyles.themeColDrk1, 1);
				}
				
				// box fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					[ derStyles.fillColorPress2,
					  derStyles.fillColorPress1 ], 1,
					verticalGradientMatrix(0, 0, w - 4, h - 4));

				break;
			}

			case "selectedDisabledIcon":
			{
				bDrawCheck = true;
				checkColor = 0x999999;

				drawRoundRect(
					0, 0, w, h, 0,
					0x999999, 0.50);
			
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					0xFFFFFF, 0.50);
				
				break;
			}
		}
		
		// Draw checkmark symbol
		if (bDrawCheck)
		{
			g.beginFill(checkColor);
			g.moveTo(3, 4);
			g.lineTo(5, 9);
			g.lineTo(7, 9);
			g.lineTo(12, 1);
			g.lineTo(12, 0);
			g.lineTo(10, 0);
			g.lineTo(6, 7);
			g.lineTo(5, 4);
			g.lineTo(3, 4);
			g.endFill();
		}
	}
}

}
