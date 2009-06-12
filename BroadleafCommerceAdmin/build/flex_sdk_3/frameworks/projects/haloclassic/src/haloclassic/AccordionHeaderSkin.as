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
 *  The skin for all the states of an AccordionHeader in an Accordion.
 */
public class AccordionHeaderSkin extends Border
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
			
			// Accordion-specific styles.
			o.borderColorDrk1 = ColorUtil.adjustBrightness2(borderColor, -10);
			o.falseFillColorBright1 =
				ColorUtil.adjustBrightness(falseFillColor0, 15);
			o.falseFillColorBright2 =
				ColorUtil.adjustBrightness(falseFillColor1, 15);
			o.falseBevelHighlight =
				ColorUtil.adjustBrightness2(falseFillColor0, 60);
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
	public function AccordionHeaderSkin()
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
	 */
	override public function get borderMetrics():EdgeMetrics
	{
		return _borderMetrics;
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
		
		// Placehold styles stub.
		var falseFillColors:Array /* of Color */ = []; // added style prop
		falseFillColors[0] = fillColors[0];	// 0xE6EEEE; // default halo fill color for false bar
		falseFillColors[1] = fillColors[1];	// 0xFFFFFF;
		
		fillColors = getStyle("selectedFillColors");
		if (!fillColors)
		{
			fillColors = [];	// So we don't clobber the original...
			fillColors[0] = ColorUtil.adjustBrightness2(themeColor, 65); // default style for halo
			fillColors[1] = 0xFFFFFF;
		}
		
		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, borderColor,
												 falseFillColors[0],
												 falseFillColors[1],
												 fillColors[0], fillColors[1]);
		
		graphics.clear();

		switch (name)
		{
			case "upSkin":
			case "disabledSkin":
			case "selectedDisabledSkin":
			{
				// edge 
				drawRoundRect(
					0, 0, w, h, 0,
					[ borderColor, derStyles.borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// fill 
				drawRoundRect(
					1, 1,w - 2, h - 2, 0,
					[ falseFillColors[0], falseFillColors[1] ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				if (bevel)
				{
					// top edge bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.falseBevelHighlight, 1);
					
					// bottom edge bevel shadow
					drawRoundRect(
						1, h - 2, w - 2, 1, 0,
						0x000000, 0.15);
				}
				break;
			}
						
			case "overSkin":
			{
				// edge
				drawRoundRect(
					0, 0, w, h, 0,
					[ derStyles.themeColDrk2, borderColor ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					[ derStyles.falseFillColorBright1,
					  derStyles.falseFillColorBright2 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				if (bevel)
				{
					// top edge bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.falseBevelHighlight, 1);
					
					// bottom edge bevel shadow
					drawRoundRect(
						1, h - 2, w - 2, 1, 0,
						0x000000, 0.15);
				}
				break;
			}
						
			case "downSkin":
			{
				// edge 
				drawRoundRect(
					0, 0, w, h, 0,
					[ derStyles.themeColDrk2, borderColor ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// fill
				drawRoundRect(
					1, 1, w - 2, h - 2, 0,
					[ derStyles.fillColorPress2, derStyles.fillColorPress1 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				break;
			}
						
			case "selectedUpSkin":
			case "selectedOverSkin":
			case "selectedDownSkin":
			{
				// edge 
				drawRoundRect(
					0, 0, w, h, 0, 
					[ borderColor, derStyles.borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				// fill
				drawRoundRect(
					1, 1,w - 2, h - 2, 0,
					[ fillColors[0], fillColors[1] ], 1,
					verticalGradientMatrix(0, 0, w, h));
				
				if (bevel)
				{
					// top edge bevel highlight
					drawRoundRect(
						1, 1, w - 2, 1, 0,
						derStyles.bevelHighlight1, 1);
					
					// bottom edge highlight
					drawRoundRect(
						1, h - 2, w - 2, 1, 0,
						0x000000, 0.15);
				}
				break;
			}
		}
	}
}

}
