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
 *  The skin for all the states of the icon in a RadioButton.
 */
public class RadioButtonIcon extends Border
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
			
			// RadioButton-unique styles
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
	public function RadioButtonIcon()
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
		var bevel:Boolean = getStyle("bevel");
		var borderColor:uint = getStyle("borderColor");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub
		var radioColor:uint = 0x2B333C; // added style prop
		
		// Derived styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);
		
		var g:Graphics = graphics;
		
		g.clear();
		
		switch (name)
		{
			case "upIcon":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						[ borderColor, derStyles.borderColorDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2 - 1,
						derStyles.bevelHighlight1, 1); 
					
					// radio fill
					drawRoundRect(
						1, 2, w - 2, h - 3, w / 2 - 1,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						borderColor,1); 
					
					// radio fill
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				break;
			}

			case "overIcon":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h / 2)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2 - 1,
						derStyles.bevelHighlight1, 1); 
					
					// radio fill
					drawRoundRect(
						1, 2, w - 2, h - 3, w / 2 - 1,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						derStyles.themeColDrk2, 1); 
					
					// radio fill
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2,
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				break;
			}

			case "downIcon":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						[ derStyles.themeColDrk2,
						  derStyles.themeColDrk1], 1,
						verticalGradientMatrix(0, 0, w, h / 2)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2 - 1,
						derStyles.bevelHighlight1, 1); 
					
					// radio fill
					drawRoundRect(
						1, 2, w - 2, h - 3, w / 2 - 1,
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						derStyles.themeColDrk2, 1); 
					
					// radio fill
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2,
						[ derStyles.fillColorPress1,
						  derStyles.fillColorPress2 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2));
				}
				break;
			}
			
			case "disabledIcon":
			{
				drawRoundRect(
					0, 0, w, h, w / 2,
					0x999999, 0.50);
				
				drawRoundRect(
					1, 1, w - 2, h - 2, w / 2 - 1,
					0xFFFFFF, 0.50);
				
				break;
			}
							
			case "selectedUpIcon":
			case "selectedOverIcon":
			case "selectedDownIcon":
			{
				if (bevel)
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						[ borderColor,
						  derStyles.borderColorDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2 - 1,
						derStyles.bevelHighlight1, 1); 
					
					// radio fill
					drawRoundRect(
						1, 2, w - 2, h - 3, w / 2 - 1,
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h, w / 2,
						borderColor, 1); 
					
					// radio fill
					drawRoundRect(
						1, 1, w - 2, h - 2, w / 2,
						[ fillColors[0], fillColors[1] ], 1); 
				}
				
				// radio symbol
				drawRoundRect(
					3, 3, w - 6, h - 6, 3,
					0xFFFFFF, 0.25);
				drawRoundRect(
					4, 4, w - 8, h - 8, 2,
					radioColor, 1);
				
				break;
			}

			case "selectedDisabledIcon":
			{
				drawRoundRect(
					0, 0, w, h, w/2,
					0x999999, 0.50);
				
				drawRoundRect(
					1, 1, w - 2, h - 2, w / 2 - 1,
					0xFFFFFF, 0.50);
				
				drawRoundRect(
					4, 4, w - 8, h - 8, 2,
					0x999999, 1);
				
				break;
			}
		}
	}
	
}

}
