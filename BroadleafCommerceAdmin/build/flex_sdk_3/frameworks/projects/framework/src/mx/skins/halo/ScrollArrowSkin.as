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

import flash.display.GradientType;
import flash.display.Graphics;
import mx.controls.scrollClasses.ScrollBar;
import mx.core.FlexVersion;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the up or down button in a ScrollBar.
 */
public class ScrollArrowSkin extends Border
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
			
			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// ScrollArrow-specific styles
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -25);
			o.borderColorDrk2 = ColorUtil.adjustBrightness2(borderColor, -50);
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
	public function ScrollArrowSkin()
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
		var backgroundColor:Number = getStyle("backgroundColor");
		var borderColor:uint = getStyle("borderColor");
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");				
		var themeColor:uint = getStyle("themeColor");
		
		var upArrow:Boolean = (name.charAt(0) == 'u');
		
		// Placeholder styles stub.
		var arrowColor:uint = getStyle("iconColor");
		
		// Derived styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 fillColors[0], fillColors[1]);

		var horizontal:Boolean = parent &&
								 parent.parent &&
								 parent.parent.rotation != 0;
		
		var borderColors:Array;
		if (upArrow && !horizontal)
			borderColors = [ borderColor, derStyles.borderColorDrk1 ];
		else
			borderColors = [ derStyles.borderColorDrk1,
							 derStyles.borderColorDrk2 ];

		//------------------------------
		//  background
		//------------------------------
		
		var g:Graphics = graphics;
		g.clear();
		
		if (isNaN(backgroundColor))
			backgroundColor = 0xFFFFFF;
		
		// Opaque backing to force the scroll elements
		// to match other components by default.
		if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0 || name.indexOf("Disabled") == -1)
		{
			drawRoundRect(
				0, 0, w, h, 0,
				backgroundColor, 1);
		}

		switch (name)
		{
			case "upArrowUpSkin":
			{			
				// shadow
				if (!horizontal)
				{
					drawRoundRect(
						1, h - 4, w - 2, 8, 0,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						verticalGradientMatrix(1, h - 4, w - 2, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h-4, w: w - 2, h: 4, r: 0 });
				}

				// intentionally fall through to the next case statement
			}

			case "downArrowUpSkin":
			{
   				var upFillColors:Array = [ fillColors[0], fillColors[1] ];
   				var upFillAlphas:Array = [ fillAlphas[0], fillAlphas[1] ];

				// border
				drawRoundRect(
					0, 0, w, h, 0,
					borderColors, 1,
					horizontal ?
					horizontalGradientMatrix(0, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: 0 });  

				// fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					upFillColors, upFillAlphas,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2 / 2));
				
				// top highlight
				drawRoundRect(
					1, 1, w - 2, h - 2 / 2, 0,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2 / 2));
				
				break;
			}
			
			case "upArrowOverSkin":
			{                           
				// shadow
				if (!horizontal)
				{
					drawRoundRect(
						1, h - 4, w - 2, 8, 0,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						verticalGradientMatrix(1, h - 4, w - 2, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h-4, w: w - 2, h: 4, r: 0}); 
				}

				// intentionally fall through to the next case statement
			}
			
			case "downArrowOverSkin":
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

				// white backing to force the scroll elements
				// to match other components by default
				drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 1);  

				// border
				drawRoundRect(
					0, 0, w, h, 0,
					[ themeColor, derStyles.themeColDrk1 ], 1,
					horizontal ?
					horizontalGradientMatrix(0, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: 0}); 

				// fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					overFillColors, overFillAlphas,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				
				// top highlight
				drawRoundRect(
					1, 1, w - 2, h -2 / 2, 0,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2 / 2)); 
				break;
			}
			
			case "upArrowDownSkin":
			{	
				// shadow
				if (!horizontal)
				{
					drawRoundRect(
						1, h - 4, w - 2, 8, 0,
						[ derStyles.borderColorDrk1,
						  derStyles.borderColorDrk1 ], [ 1, 0 ],
						horizontal ?
						horizontalGradientMatrix(1, h - 4, w - 2, 8) :
						verticalGradientMatrix(1, h - 4, w - 2, 8),
						GradientType.LINEAR, null, 
						{ x: 1, y: h - 4, w: w - 2, h: 4, r: 0 }); 
				}
					
				// intentionally fall through to the next case statement
			}

			case "downArrowDownSkin":
			{
				// border
				drawRoundRect(
					0, 0, w, h, 0,
					[ themeColor, derStyles.themeColDrk1 ], 1,
					horizontal ?
					horizontalGradientMatrix(0, 0, w, h) :
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: 0 }); 

				// fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					[ derStyles.fillColorPress1,
					  derStyles.fillColorPress2 ], 1,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				
				// top highlight
				drawRoundRect(
					1, 1, w - 2, h -2 / 2, 0,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					horizontal ?
					horizontalGradientMatrix(0, 0, w - 2, h - 2) :
					verticalGradientMatrix(0, 0, w - 2, h - 2 / 2)); 
				break;
			}
			
			case "upArrowDisabledSkin":
			{	
				if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
				{		
					// shadow
					if (!horizontal)
					{
						drawRoundRect(
							1, h - 4, w - 2, 8, 0,
							[ derStyles.borderColorDrk1,
							  derStyles.borderColorDrk1 ], [ .5, 0 ],
							verticalGradientMatrix(1, h - 4, w - 2, 8),
							GradientType.LINEAR, null, 
							{ x: 1, y: h-4, w: w - 2, h: 4, r: 0 });
					}
				}

				// intentionally fall through to the next case statement
			} 
			
			case "downArrowDisabledSkin":
			{
				if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
				{
	   				var disFillColors:Array = [ fillColors[0], fillColors[1] ];
	   				var disFillAlphas:Array = [ fillAlphas[0] - 0.15, fillAlphas[1] - 0.15 ];
	
					// border
					drawRoundRect(
						0, 0, w, h, 0,
						borderColors, 0.5,
						horizontal ?
						horizontalGradientMatrix(0, 0, w, h) :
						verticalGradientMatrix(0, 0, w, h),
						GradientType.LINEAR, null, 
						{ x: 1, y: 1, w: w - 2, h: h - 2, r: 0 });  
	
					// fill
					drawRoundRect(
						1, 1, w - 2, h - 2, 0,
						disFillColors, disFillAlphas,
						horizontal ?
						horizontalGradientMatrix(0, 0, w - 2, h - 2) :
						verticalGradientMatrix(0, 0, w - 2, h - 2 / 2));
	
					arrowColor = getStyle("disabledIconColor");
				}
				else
				{
					drawRoundRect(
					0, 0, w, h, 0,
					0xFFFFFF, 0);
				
					return;
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

		// Draw up or down arrow
		g.beginFill(arrowColor);
		if (upArrow)
		{
			g.moveTo(w / 2, 6);
			g.lineTo(w - 5, h - 6);
			g.lineTo(5, h - 6);
			g.lineTo(w / 2, 6);
		}
		else
		{
			g.moveTo(w / 2, h - 6);
			g.lineTo(w - 5, 6);
			g.lineTo(5, 6);
			g.lineTo(w / 2, h - 6);
		}
		g.endFill();
	}
}

}
