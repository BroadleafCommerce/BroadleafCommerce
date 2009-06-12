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

package haloclassic
{

import mx.containers.BoxDirection;
import mx.controls.Button;
import mx.controls.ButtonBar;
import mx.core.EdgeMetrics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the ButtonBarButtons in a ButtonBar.
 */
public class ButtonBarButtonSkin extends Border
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
	//  borderMetrics
	//----------------------------------

	/**
	 *  @private
	 *  Internal object that contains the thickness of each edge
	 *  of the border
	 */
	private var _borderMetrics:EdgeMetrics;

	/**
	 *  @private
	 */
	override public function get borderMetrics():EdgeMetrics
	{
		if (_borderMetrics)
			return _borderMetrics;
		
		var borderThickness:Number = getStyle("borderThickness");
		
		_borderMetrics = new EdgeMetrics(borderThickness, borderThickness,
										 borderThickness, borderThickness);
										  
		return _borderMetrics;
	}

	//----------------------------------
	//  measuredWidth
	//----------------------------------
	
	/**
	 *  @private
	 */
	override public function get measuredWidth():Number
	{
		return 50;
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
		var borderThickness:Number =
			Math.max(0, getStyle("borderThickness") - 2);
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");

		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
												 fillColors[1]);

		var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -25);
		
		var emph:Boolean = false;
		
		if (parent is Button)
			emph = (parent as Button).emphasized;

		var tmp:Number;

		var bar:ButtonBar = parent ? ButtonBar(parent.parent) : null;
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
				if (borderThickness > 0) 
				{
					// border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 

					// inner glow
					if (horizontal)
					{
						drawRoundRect(
							pos == -1 ? 1 : 0, 1,
							w - (pos == 0 ? 0 : 1), h - 2, cr1,
							derStyles.themeColLgt, 1);
					}
					else
					{
						drawRoundRect(
							1, pos == -1 ? 1 : 0,
							w - 2, h - (pos == 0 ? 0 : 1), cr1,
							derStyles.themeColLgt, 1);
					}
					
					// inner border/edge
					tmp = borderThickness + 1;
					if (horizontal)
					{
						drawRoundRect(
							pos == -1 ? tmp : 0, tmp,
							w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr2,
							[ derStyles.innerEdgeColor1,
							  derStyles.innerEdgeColor2 ], 1,
							verticalGradientMatrix(0, 1, w, h - 5));
					}
					else
					{
						drawRoundRect(
							tmp, pos == -1 ? tmp : 0,
							w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr2,
							[ derStyles.innerEdgeColor1,
							  derStyles.innerEdgeColor2 ], 1,
							verticalGradientMatrix(0, 1, w, h - 5));
					}
					
					tmp = borderThickness + 2;
					
					if (bevel)
					{
						// top bevel highlight edge
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 1, tmp,
								w - (pos == 0 ? 2 : tmp + 1), h - tmp * 2, cr3,
								derStyles.bevelHighlight1, 1);
						}
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp : 1,
								w - tmp * 2, h - (pos == 0 ? 2 : tmp + 1), cr3,
								derStyles.bevelHighlight1, 1); 
						}
						
						// button fill
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 1, tmp + 1,
								w - (pos == 0 ? 2 : tmp + 1),
								h - (tmp * 2 + 1), cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
						else
						{
							drawRoundRect(
								tmp + 1, pos == -1 ? tmp : 1,
								w - (tmp * 2 + 1),
								h - (pos == 0 ? 2 : tmp + 1), cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
					}
					else // (flat button, no bevel)
					{
						// button fill
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 0, tmp,
								w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w, h)); 
						}
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp : 0,
								w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
					}
				}
				else if (borderThickness == 0)
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 
					
					// inner glow
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						derStyles.themeColLgt, 1); 
					
					if (bevel)
					{
						// top bevel highlight edge
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							derStyles.bevelHighlight1, 1); 

						// button fill
						drawRoundRect(
							1, 2, w - 2, h - 3, cr1,
							[ fillColors[0], fillColors[1] ], 1,
							verticalGradientMatrix(0, 0, w - 2, h - 2)); 
					}
					else // (flat button, no bevel)
					{
						// button fill
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							[ fillColors[0], fillColors[1] ], 1,
							verticalGradientMatrix(0, 0, w - 2, h - 2)); 
					}
				}
				break;
			}

			case "upSkin":
			{
				if (emph)
				{
					if (borderThickness > 0) 
					{
						// button border/edge
						drawRoundRect(
							0, 0, w, h, cr,
							derStyles.themeColDrk1, 1); 
						
						// inner glow
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							derStyles.themeColLgt, 1); 
						
						tmp = borderThickness + 1;
						
						// inner border/edge
						drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
							[ derStyles.innerEdgeColor1,
							  derStyles.innerEdgeColor2 ], 1,
							verticalGradientMatrix(0, 1, w - 4, h - 5));
						
						tmp = borderThickness + 2;
						
						if (bevel)
						{
							// top bevel highlight edge
							drawRoundRect(
								tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
								derStyles.bevelHighlight1, 1); 
							
							// button fill
							drawRoundRect(
								tmp, tmp + 1,
								w - tmp * 2, h - (tmp * 2 + 1), cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 4, h - 4));
						}
						else // (flat button, no bevel)
						{
							// button fill
							drawRoundRect(
								tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 4, h - 4));
						}
					}
					else if (borderThickness == 0) 
					{
						// button border/edge
						drawRoundRect(
							0, 0, w, h, cr,
							derStyles.themeColDrk1, 1); 
						
						// inner glow
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							derStyles.themeColLgt, 1); 
						
						if (bevel)
						{
							// top bevel highlight edge
							drawRoundRect(
								1, 1, w - 2, h - 2, cr1,
								derStyles.bevelHighlight1, 1); 
							
							// button fill
							drawRoundRect(
								1, 2, w - 2, h - 3, cr1,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2)); 
						}
						else // (flat button, no bevel)
						{
							// button fill
							drawRoundRect(
								1, 1, w - 2, h - 2, cr1,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2)); 
						}
					}
				}
				else
				{
					if (borderThickness > 0) 
					{
						// button border/edge
						drawRoundRect(
							0, 0, w, h, cr,
							[ borderColor, borderColorDrk1 ], 1,
							verticalGradientMatrix(0, 0, w - 2, h - 2)); 
						
						// inner glow
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? 1 : 0, 1,
								w - (pos == 0 ? 0 : 1), h - 2, cr1,
								[ 0xFFFFFF,0xE6E6E6 ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2));
						}
						else
						{
							drawRoundRect(
								1, pos == -1 ? 1 : 0,
								w - 2, h - (pos == 0 ? 0 : 1), cr1,
								[ 0xFFFFFF,0xE6E6E6 ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2));
						}
						
						tmp = borderThickness + 1;
						
						// inner border/edge
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 0, tmp,
								w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr2,
								[ derStyles.innerEdgeColor1,
								  derStyles.innerEdgeColor2], 1,
								verticalGradientMatrix(0, 1, w - 4, h - 5));
						}
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp : 0,
								w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr2,
								[ derStyles.innerEdgeColor1,
								  derStyles.innerEdgeColor2], 1,
								verticalGradientMatrix(0, 1, w - 4, h - 5));
						}
						
						tmp = borderThickness + 2;
									
						if (bevel)
						{
							// top bevel highlight edge
							if (horizontal)
							{
								drawRoundRect(
									pos == -1 ? tmp : 1, tmp,
									w - (pos == 0 ? 2 : tmp + 1),
									h - tmp * 2, cr3,
									derStyles.bevelHighlight1, 1);
							}
							else
							{
								drawRoundRect(
									tmp, pos == -1 ? tmp : 1,
									w - tmp * 2,
									h - (pos == 0 ? 2 : tmp + 1), cr3,
									derStyles.bevelHighlight1, 1);
							}
							
							// button fill
							if (horizontal)
							{
								drawRoundRect(
									pos == -1 ? tmp: 1, tmp + 1,
									w - (pos == 0 ? 2 : tmp + 1),
									h - (tmp * 2 + 1), cr3,
									[ fillColors[0], fillColors[1] ], 1,
									verticalGradientMatrix(0, 0, w, h));
							}
							else
							{
								drawRoundRect(
									tmp + 1, pos == -1 ? tmp: 1,
									w - (tmp * 2 + 1),
									h - (pos == 0 ? 2 : tmp + 1), cr3,
									[ fillColors[0], fillColors[1] ], 1,
									verticalGradientMatrix(0, 0, w, h));
							}
						}
						else // (flat button, no bevel)
						{
							// button fill
							if (horizontal)
							{
								drawRoundRect(
									pos == -1 ? tmp : 0, tmp,
									w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr3,
									[ fillColors[0], fillColors[1] ], 1,
									verticalGradientMatrix(0, 0, w, h));
							}
							else
							{
								drawRoundRect(
									tmp, pos == -1 ? tmp : 0,
									w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr3,
									[ fillColors[0], fillColors[1] ], 1,
									verticalGradientMatrix(0, 0, w, h));
							}
						}
					}
					else if (borderThickness == 0)
					{
						if (bevel)
						{
							// button border/edge
							drawRoundRect(
								0, 0, w, h, cr,
								[ borderColor, borderColorDrk1 ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2));
							
							// top bevel highlight edge
							drawRoundRect(
								1, 1, w - 2, h - 2, cr1,
								derStyles.bevelHighlight1, 1); 
							
							// button fill
							drawRoundRect(
								1, 2, w - 2, h - 3, cr1,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2)); 
						}
						else // (flat button, no bevel)
						{
							// button border/edge
							drawRoundRect(
								0, 0, w, h, cr,
								borderColor, 1); 
							
							// button fill
							drawRoundRect(
								1, 1, w - 2, h - 2, cr1,
								[ fillColors[0], fillColors[1] ], 1,
								verticalGradientMatrix(0, 0, w - 2, h - 2));
						}
					}
				}
				break;
			}
						
			case "overSkin":
			{
				if (borderThickness > 0) 
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 
					
					// inner glow
					if (horizontal)
					{
						drawRoundRect(
							pos == -1 ? 1 : 0, 1,
							w - (pos == 0 ? 0 : 1), h - 2, cr1,
							derStyles.themeColLgt, 1);
					}
					else
					{
						drawRoundRect(
							1, pos == -1 ? 1 : 0,
							w - 2, h - (pos == 0 ? 0 : 1), cr1,
							derStyles.themeColLgt, 1);
					}
					
					tmp = borderThickness + 1;
					
					// inner border/edge
					if (horizontal)
					{
						drawRoundRect(
							pos == -1 ? tmp : 0, tmp,
							w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr2,
							[ fillColors[0], fillColors[1] ], 1,
							verticalGradientMatrix(0, 1, w - 4, h - 5));
					}
					else
					{
						drawRoundRect(
							tmp, pos == -1 ? tmp : 0,
							w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr2,
							[ fillColors[0], fillColors[1] ], 1,
							verticalGradientMatrix(0, 1, w - 4, h - 5));
					}
				
					if (horizontal)
					{
						drawRoundRect(
							pos == -1 ? tmp : 0, tmp,
							w - (pos == 0 ? 0 : tmp), h - tmp * 2, cr2,
							derStyles.themeColDrk1, 0.40);
					}
					else
					{
						drawRoundRect(
							tmp, pos == -1 ? tmp : 0,
							w - tmp * 2, h - (pos == 0 ? 0 : tmp), cr2,
							derStyles.themeColDrk1, 0.40);
					}
					
					tmp = borderThickness + 2;
					
					if (bevel)
					{
						// top bevel highlight edge
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 1, tmp,
								w - (pos == 0 ? 2 : tmp + 1), h - tmp * 2, cr3,
								derStyles.bevelHighlight1, 1);
						}
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp : 1,
								w - tmp * 2, h - (pos == 0 ? 2 : tmp + 1), cr3,
								derStyles.bevelHighlight1, 1);
						}
						
						// button face
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 1, tmp + 1,
								w - (pos == 0 ? 2 : tmp + 1),
								h - (tmp * 2 + 1), cr3,
								[ derStyles.fillColorBright1,
								  derStyles.fillColorBright2 ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp + 2: 1,
								w - tmp * 2, h - (pos == 0 ? 2 : tmp + 3), cr3,
								[ derStyles.fillColorBright1,
								  derStyles.fillColorBright2 ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
					} 
					else // (flat button, no bevel)
					{
						// button face
						if (horizontal)
						{
							drawRoundRect(
								pos == -1 ? tmp : 1, tmp,
								w - (pos == 0 ? 2 : tmp + 1), h - tmp * 2, cr3,
								[ derStyles.fillColorBright1,
								  derStyles.fillColorBright2 ], 1,
								verticalGradientMatrix(0, 0, w, h));
						} 
						else
						{
							drawRoundRect(
								tmp, pos == -1 ? tmp : 1,
								w - tmp * 2, h - (pos == 0 ? 2 : tmp + 1), cr3,
								[ derStyles.fillColorBright1,
								  derStyles.fillColorBright2 ], 1,
								verticalGradientMatrix(0, 0, w, h));
						}
					}
				}
				else if (borderThickness == 0)
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 
					
					if (bevel)
					{
						// top bevel highlight edge
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							derStyles.bevelHighlight1, 1); 
						
						// button fill
						drawRoundRect(
							1, 2, w - 2, h - 3, cr1,
							[ derStyles.fillColorBright1,
							  derStyles.fillColorBright2 ], 1,
							verticalGradientMatrix(0, 0, w - 2, h - 2)); 
					}
					else // (flat button, no bevel)
					{
						// button fill
						drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
							[ derStyles.fillColorBright1,
							  derStyles.fillColorBright2 ], 1,
							verticalGradientMatrix(0, 0, w - 2, h - 2));
					}
				}
				break;
			}
		
			case "downSkin":
			case "selectedDownSkin":
			{
				if (borderThickness > 0) 
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 

					// inner glow
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						themeColor, 1); 
					
					tmp = borderThickness + 1;
					
					// inner border/edge
					drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						verticalGradientMatrix(0, 1, w - 4, h - 5)); 
					
					drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
						derStyles.themeColDrk1, 0.60);
					
					tmp = borderThickness + 2;
					
					// button fill
					drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1], 1,
						verticalGradientMatrix(0, 0, w - 4, h - 4));
				} 
				else if (borderThickness == 0) 
				{
					// button border/edge
					drawRoundRect(
						0, 0, w, h, cr,
						derStyles.themeColDrk1, 1); 
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
				}
				break;
			}
					
			case "disabledSkin":
			case "selectedDisabledSkin":
			{
				if (borderThickness > 0) 
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h, cornerRadius,
						0x999999, 0.50);

					// inner glow
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						0xE6E6E6, 0.50);

					// button edge
					drawRoundRect(
						2, 2, w - 4, h - 4, cr2,
						0xAAAAAA, 0.50); 

					// button fill
					drawRoundRect(
						3, 3, w - 6, h - 6, cr3,
						0xE6E6E6, 0.50);
				}
				else if (borderThickness == 0)
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h, cornerRadius,
						0x999999, 0.50);
					
					// button fill
					drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						0xE6E6E6, 0.50);
				}
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
}

}
