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
import mx.controls.scrollClasses.ScrollBar;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the up button in a ScrollBar.
 */
public class ScrollArrowUpSkin extends Border
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
			
			// Cross-Component Styles
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// ScrollArrowUp-Unique Styles
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -30);
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
	public function ScrollArrowUpSkin()
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
        return ScrollBar.THICKNESS;
    }
    
    //----------------------------------
	//  measuredHeight
    //----------------------------------
    
    /**
     *  @private
     */        
    override public function get measuredHeight():Number
    {
        return ScrollBar.THICKNESS;
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
		var radius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placeholder styles stub.
		var arrowColor:uint = 0x111111;
		
		// Derived styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);

		var cornerRadius:Object = [ 0, 0, radius, radius ]; // tl, tr, bl, br
		var cornerRadius2:Array = [];
		cornerRadius2[0] = Math.max(cornerRadius[0] - 1, 0);
		cornerRadius2[1] = Math.max(cornerRadius[1] - 1, 0);
		cornerRadius2[2] = Math.max(cornerRadius[2] - 1, 0);
		cornerRadius2[3] = Math.max(cornerRadius[3] - 1, 0);

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
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ borderColor,
						  derStyles.borderColorDrk1 ], 1,
						horizontalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight edge
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 2)); 
					
					// fill
					drawRoundRect(
						2, 2, w - 3, h - 3,
					    { tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
					    [ fillColors[0], fillColors[1] ], 1,
					    horizontalGradientMatrix(1, 0, w - 3, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						borderColor, 1); 
					
					// fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ fillColors[0], fillColors[1] ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 2)); 
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
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 1,
						horizontalGradientMatrix(0, 0, w, h)); 
					
					// bevel highlight edge
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.bevelHighlight1,
						  derStyles.bevelHighlight2 ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 2)); 
					
					// fill
					drawRoundRect(
						2, 2, w - 3, h - 3,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						horizontalGradientMatrix(1, 0, w - 3, h - 3)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						derStyles.themeColDrk2, 1); 
					
					// fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorBright1,
						  derStyles.fillColorBright2 ], 1,
						horizontalGradientMatrix(1, 0, w - 2, h - 2)); 
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
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						[ derStyles.themeColDrk2, derStyles.themeColDrk1 ], 1,
						horizontalGradientMatrix(0, 0, w, h)); 
					
					// fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						horizontalGradientMatrix(1, 0, w, h)); 
				}
				else
				{
					// border
					drawRoundRect(
						0, 0, w, h,
						{ tl: cornerRadius[0], tr: cornerRadius[1],
						  bl: cornerRadius[2], br: cornerRadius[3] },
						derStyles.themeColDrk2, 1); 
					
					// fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2[0], tr: cornerRadius2[1],
						  bl: cornerRadius2[2], br: cornerRadius2[3] },
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						horizontalGradientMatrix(1, 0, w, h)); 
				}
				break;
			}
			
			default:
			{
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0);
				
				return;
				
				break;
			}
		}

		// Draw up arrow
		g.beginFill(arrowColor);
		g.moveTo(w / 2, 6);
		g.lineTo(w - 5, h - 6);
		g.lineTo(5, h - 6);
		g.lineTo(w / 2, 6);
		g.endFill();
	}
}

}
