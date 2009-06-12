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
import flash.utils.getQualifiedClassName;
import flash.utils.describeType;
import mx.containers.BoxDirection;
import mx.core.IButton;
import mx.core.UIComponent;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the ButtonBarButtons in a ButtonBar.
 */
public class ButtonBarButtonSkin extends Border
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
			
			// Button-specific styles.
			o.innerEdgeColor1 = ColorUtil.adjustBrightness2(fillColor0, -10);
			o.innerEdgeColor2 = ColorUtil.adjustBrightness2(fillColor1, -25);
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
	public function ButtonBarButtonSkin()
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
		return UIComponent.DEFAULT_MEASURED_MIN_WIDTH;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return UIComponent.DEFAULT_MEASURED_MIN_HEIGHT;
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
		var borderColor:uint = getStyle("borderColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");		
		var themeColor:uint = getStyle("themeColor");

		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
												 fillColors[1]);

		var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -50);
		
		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);

		var emph:Boolean = false;
		
		if (parent is IButton)
			emph = (parent as IButton).emphasized;

		var tmp:Number;

		var bar:Object = parent && parent.parent && isButtonBar(parent.parent) ? parent.parent : null;
		var horizontal:Boolean = true;
		var pos:int = 0;

		if (bar)
		{
			if (bar.direction == BoxDirection.VERTICAL)
				horizontal = false;

			// first: -1, middle: 0, last: 1
			var index:int = bar.getChildIndex(parent);
			pos = (index == 0 ? -1 : (index == bar.numChildren - 1 ? 1 : 0));
		}

		var radius:Object = getCornerRadius(pos, horizontal, cornerRadius);
		var cr:Object = getCornerRadius(pos, horizontal, cornerRadius);
		var cr1:Object = getCornerRadius(pos, horizontal, cornerRadius - 1);
		var cr2:Object = getCornerRadius(pos, horizontal, cornerRadius - 2);
		var cr3:Object = getCornerRadius(pos, horizontal, cornerRadius - 3);
		
		graphics.clear();

		switch (name)
		{			
			case "selectedUpSkin":
			case "selectedOverSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h ),
					GradientType.LINEAR, null, 
					{ x: 2, y: 2, w: w - 4, h: h - 4, r: cr2 }); 
										
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					[ fillColors[1], fillColors[1] ],
					[ fillAlphas[0], fillAlphas[1] ],
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 

				break;
			}

			case "upSkin":
			{
   				var upFillColors:Array = [ fillColors[0], fillColors[1] ];
   				var upFillAlphas:Array = [ fillAlphas[0], fillAlphas[1] ];
			
				if (emph)
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						[ themeColor, themeColorDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h ),
						GradientType.LINEAR, null, 
						{ x: 2, y: 2, w: w - 4, h: h - 4, r: cr2 }); 

					// button fill
					drawRoundRect(
						2, 2, w - 4, h - 4, cr2,
						upFillColors, upFillAlphas,
						verticalGradientMatrix(1, 1, w - 2, h - 2)); 

					// top highlight
					if (!(radius is Number))
						{ radius.bl = radius.br = 0;}
					drawRoundRect(
						2, 2, w - 4, (h - 4) / 2, radius,
						[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
						verticalGradientMatrix(2, 2, w - 2, (h - 4) / 2));
				}
				else
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						[ borderColor, borderColorDrk1 ], 1,
						verticalGradientMatrix(0, 0, w, h ),
						GradientType.LINEAR, null, 
						{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 }); 

					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						upFillColors, upFillAlphas,
						verticalGradientMatrix(1, 1, w - 2, h - 2));
						
					// top highlight
					if (!(radius is Number))
						{ radius.bl = radius.br = 0;}
					drawRoundRect(
						1, 1, w - 2, (h - 2) / 2, radius,
						[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
						verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2)); 
				}
				break;
			}
						
			case "overSkin":
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

				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, derStyles.themeColDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 }); 
					
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					overFillColors, overFillAlphas,
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				
				// top highlight
				if (!(radius is Number))
					{ radius.bl = radius.br = 0;}
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2, radius,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2));
				break;
			}
								
			case "downSkin":
			case "selectedDownSkin":
			{
				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, derStyles.themeColDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h)); 
					
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					[ derStyles.fillColorPress1, derStyles.fillColorPress2 ], 1,
					verticalGradientMatrix(0, 0, w - 2, h - 2)); 
								  
				// top highlight
				if (!(radius is Number))
					{ radius.bl = radius.br = 0;}
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2, radius,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2)); 
				
				break;
			}
					
			case "disabledSkin":
			case "selectedDisabledSkin":
			{
                var disFillColors:Array = [ fillColors[0], fillColors[1] ];
                
                var disFillAlphas:Array =
                    [ Math.max( 0, fillAlphas[0] - 0.15),
                      Math.max( 0, fillAlphas[1] - 0.15) ];
				
				// outer edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ borderColor, borderColorDrk1 ], 0.5,
					verticalGradientMatrix(0, 0, w, h ),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cr1 } ); 	
								
				// button fill
                drawRoundRect(
                    1, 1, w - 2, h - 2, cr1,
                    disFillColors, disFillAlphas,
                    verticalGradientMatrix(0, 0, w - 2, h - 2));
				
				break;
			}
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function getCornerRadius(pos:int, horizontal:Boolean,
									 radius:Number):Object
	{
		if (pos == 0)
			return 0;
		
		radius = Math.max(0, radius);

		if (horizontal)
		{
			if (pos == -1)
				return { tl: radius, tr: 0, bl: radius, br: 0 };
			else // pos == 1
				return { tl: 0, tr: radius, bl: 0, br: radius };
		}
		else
		{
			if (pos == -1)
				return { tl: radius, tr: radius, bl: 0, br: 0 };
			else // pos == 1
				return { tl: 0, tr: 0, bl: radius, br: radius };
		}
	}

	/**
	 *  We don't use 'is' to prevent dependency issues
	 */
	static private var bbars:Object = {};

	static private function isButtonBar(parent:Object):Boolean
	{
		var s:String = getQualifiedClassName(parent);
		if (bbars[s] == 1)
			return true;

		if (bbars[s] == 0)
			return false;

		if (s == "mx.controls::ButtonBar")
		{
			bbars[s] == 1;
			return true;
		}

		var x:XML = describeType(parent);
		var xmllist:XMLList = x.extendsClass.(@type == "mx.controls::ButtonBar");
		if (xmllist.length() == 0)
		{
			bbars[s] = 0;
			return false;
		}
		
		bbars[s] = 1;
		return true;
	}

}

}
