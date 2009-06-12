////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package haloclassic
{

import mx.core.EdgeMetrics;
import mx.skins.Border;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of a Tab in a TabNavigator or TabBar.
 */
public class TabSkin extends Border
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
											  falseFillColor0:uint,
											  falseFillColor1:uint,
											  fillColor0:uint,
											  fillColor1:uint):Object
	{
		var key:String = HaloColors.getCacheKey(themeColor, borderColor,
												falseFillColor0,
												falseFillColor1,
												fillColor0, fillColor1);
		
		if (!cache[key])
		{
			var o:Object = cache[key] = {};

			// Cross-component styles.
			HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
			
			// Tab-specific styles.
			o.borderColorDrk1 =
				ColorUtil.adjustBrightness2(borderColor, -30);
			o.falseBevelHighlight =
				ColorUtil.adjustBrightness2(falseFillColor0, 60);
			o.falseFillColorBright1 =
				ColorUtil.adjustBrightness(falseFillColor0, 15);
			o.falseFillColorBright2 =
				ColorUtil.adjustBrightness(falseFillColor1, 15);
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
	public function TabSkin()
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
	 *  Storage for the borderMetrics property.
	 */
	private var _borderMetrics:EdgeMetrics = new EdgeMetrics(1, 1, 1, 1);

	/**
	 *  @private
	 *  Internal object that contains the thickness of each edge
	 *  of the border
	 */
	override public function get borderMetrics():EdgeMetrics
	{
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
		var cornerRadius:Number = getStyle("cornerRadius");
		var fillColors:Array /* of Number */ = getStyle("fillColors");
		StyleManager.getColorNames(fillColors);
		var themeColor:uint = getStyle("themeColor");
		
		// Placehold styles stub
		// themeColor = 0x80FF4D; // new halo green color
		var falseFillColors:Array = []; /* of Number*/ // added style prop
		falseFillColors[0] = fillColors[0];	// 0xE6EEEE; // default halo fill color for false tab
		falseFillColors[1] = fillColors[1];	// 0xFFFFFF;
		
		fillColors = getStyle("selectedFillColors");
		if (!fillColors)
		{
			fillColors = []; // So we don't clobber the original...
			fillColors[0] = ColorUtil.adjustBrightness2(themeColor, 65); // default style for halo
			fillColors[1] = 0xFFFFFF; // default tab navigator background
		}

		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 falseFillColors[0],
												 falseFillColors[1],
												 fillColors[0], fillColors[1]);
		
		var cornerRadius2:Number = Math.max(cornerRadius - 2, 0);

		graphics.clear();
		
		switch (name)
		{
			case "upSkin":
			{
				if (bevel)
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						[ derStyles.borderColorDrk1, borderColor ], 1,
						verticalGradientMatrix(0, 0, w, h));
					
					// highlight edge
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						derStyles.falseBevelHighlight, 1);
					
					// tab fill
					drawRoundRect(
						1, 2, w - 2, h - 3,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ falseFillColors[0], falseFillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h - 3));
				}
				else
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						borderColor, 1);
					
					// tab fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ falseFillColors[0], falseFillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h - 6));
				}
				
				// tab bottom line
				drawRoundRect(
					0, h - 1, w, 1, 0,
					borderColor, 1);
				
				// tab shadow	
				drawRoundRect(
					0, h - 2, w, 1, 0,
					0x000000, 0.10);
				
				// tab shadow
				drawRoundRect(
					0, h - 3, w, 1, 0,
					0x000000, 0.05);
	
				break;
			}

			case "overSkin":
			{
				if (bevel)
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						[ derStyles.themeColDrk1, derStyles.themeColDrk2 ], 1,
						verticalGradientMatrix(0, 0, w, h - 6));
					
					// highlight edge
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						derStyles.falseBevelHighlight, 1);
					
					// tab fill
					drawRoundRect(
						1, 2, w - 2, h - 3,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ derStyles.falseFillColorBright1,
						  derStyles.falseFillColorBright2 ], 1,
						verticalGradientMatrix(2, 2, w - 2, h - 2));
				}
				else
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						derStyles.themeColDrk2, 1);
					
					// tab fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ derStyles.falseFillColorBright1,
						  derStyles.falseFillColorBright2 ], 1,
						verticalGradientMatrix(2, 2, w - 2, h - 2));
				}
				
				// tab bottom line
				drawRoundRect(
					0, h - 1, w, 1, 0,
					borderColor, 1);
				
				// tab shadow	
				drawRoundRect(
					0, h - 2, w, 1, 0,
					0x000000, 0.10);
				
				// tab shadow
				drawRoundRect(
					0, h - 3, w, 1, 0,
					0x000000, 0.05);
				
				break;
			}

			case "disabledSkin":
			{
				if (bevel) 
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						[ derStyles.borderColorDrk1, borderColor ], 1,
						verticalGradientMatrix(0, 0, w, h - 6));
					
					// highlight edge
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						fillColors[1], 1);
					
					// tab fill
					drawRoundRect(
						1, 2, w - 2, h - 3,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h - 2));
				}
				else
				{
					// outer edge
					drawRoundRect(
						0, 0, w, h - 1,
						{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
						borderColor, 1);
					
					// tab fill
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 }, 
					    [ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h - 2));
				}
				
				// tab bottom line
				drawRoundRect(
					0, h - 1, w, 1, 0,
					borderColor, 1);
				
				// tab shadow	
				drawRoundRect(
					0, h - 2, w, 1, 0,
					0x000000, 0.10);
				
				// tab shadow
				drawRoundRect(
					0, h - 3, w, 1, 0,
					0x000000, 0.05);
				
				break;
			}
			
			case "downSkin":
			case "selectedUpSkin":
			case "selectedDownSkin":
			case "selectedOverSkin":
			case "selectedDisabledSkin":
			{
				// outer edge back
				drawRoundRect(
					0, 0, w, h - 1,
					{ tl: cornerRadius, tr: cornerRadius, bl: 0, br: 0 },
					[ derStyles.borderColorDrk1, borderColor ], 1,
					verticalGradientMatrix(0, 0, w, h - 2));
				
				if (bevel)
				{
					// highlight edge color
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						derStyles.bevelHighlight1, 1);
					
					// tab fill color
					drawRoundRect(
						1, 2, w - 2, h - 3,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h / 2));
				}
				else
				{
					// tab fill color
					drawRoundRect(
						1, 1, w - 2, h - 2,
						{ tl: cornerRadius2, tr: cornerRadius2, bl: 0, br: 0 },
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 2, w - 2, h - 2));
				}
				
				// tab bottom line
				drawRoundRect(
					1, h - 1, w - 2, 1, 0,
					fillColors[1], 1);
				
				break;
			}
		}
	}
}

}
