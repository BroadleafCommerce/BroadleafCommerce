////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.DisplayObject;
import flash.display.GradientType;
import flash.filters.BlurFilter;
import flash.utils.getQualifiedClassName;
import flash.utils.describeType;
import mx.core.ApplicationGlobals;
import mx.skins.Border;
import mx.styles.IStyleClient;
import mx.utils.ColorUtil;

/**
 *  Defines the up, down, and over states for MenuBarItem objects.
 */
public class ActivatorSkin extends Border
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
	public function ActivatorSkin()
	{
		super();
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

		if (!getStyle("translucent"))
			drawHaloRect(w, h);
		else
			drawTranslucentHaloRect(w, h);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function drawHaloRect(w:Number, h:Number):void
	{
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
		var highlightAlphas:Array = getStyle("highlightAlphas");				
		var themeColor:uint = getStyle("themeColor");

		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);

		// Derivative styles.
		var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
												 fillColors[1]);
												 
		graphics.clear();

		switch (name)
		{
			case "itemUpSkin": // up/disabled
			{
				// invisible hit area
				drawRoundRect(
					x, y, w, h, 0,
					0xFFFFFF, 0);
				break;
			}

			case "itemDownSkin":
			{
				// face
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2, 0,
					[ derStyles.fillColorPress1, derStyles.fillColorPress2], 1,
					verticalGradientMatrix(0, 0, w, h )); 
									
				// highlight
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2 / 2, 0,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(0, 0, w - 2, h - 2));

				break;
			}

			case "itemOverSkin":
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

				// face
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2, 0,
					overFillColors, overFillAlphas,
					verticalGradientMatrix(0, 0, w, h )); 

				// highlight
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2 / 2, 0,
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(0, 0, w - 2, h - 2));
				
				break;
			}
		}

		filters = [ new BlurFilter(2, 0) ];
	}

	/**
	 *  @private
	 *  The drawTranslucentHaloRect function is called when the "translucent"
	 *  style is set to true, which happens when a MenuBar is inside an ACB.
	 */
	private function drawTranslucentHaloRect(w:Number,h:Number):void
	{
		// Find the parent app bar's colors.
		var p:IStyleClient = parent as IStyleClient;
		while (p && !(isApplicationControlBar(p)))
		{
			p = DisplayObject(p).parent as IStyleClient;
		}
		if (!p)
			return;

		var fillColor:Object = p.getStyle("fillColor");
		var backgroundColor:Object =
			p.getStyle("backgroundColor");
		var backgroundAlpha:Number =
			p.getStyle("backgroundAlpha");

		var radius:Number = p.getStyle("cornerRadius");
		var docked:Boolean = p.getStyle("docked");

		if (backgroundColor == "")
			backgroundColor = null;

		if (docked && !backgroundColor)
		{
			// The docked bar always has a background, so take the
			// main application's background color.
			var pabc:Object =
				ApplicationGlobals.application.getStyle("backgroundColor");
			backgroundColor = pabc ? pabc : 0x919999;
		}

		graphics.clear();

		switch (name)
		{
			case "itemUpSkin": // up/disabled
			{
				drawRoundRect(
					1, 1, w - 2, h - 1, 0,
					0, 0);
				filters = [];
				break;
			}

			case "itemOverSkin":
			{
				if (backgroundColor)
				{
					drawRoundRect(
						1, 1, w - 2, h - 1, 0,
						backgroundColor, backgroundAlpha);
				}
				
				drawRoundRect(
					1, 1, w - 2, h - 1, 0,
					[ fillColor, fillColor, fillColor, fillColor, fillColor ],
					[ 1, 0.75, 0.6, 0.7, 0.9 ],
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR,
					[ 0, 0x5F, 0x7F, 0xBF, 0xFF ]);
				
				filters = [ new BlurFilter(0, 4) ];
				break;
			}

			case "itemDownSkin":
			{
				if (backgroundColor)
				{
					drawRoundRect(
						1, 1, w - 2, h - 1, 0,
						backgroundColor, backgroundAlpha);
				}
			
				drawRoundRect(
					1, 1, w - 2, h - 1, 0,
					[ fillColor, fillColor, fillColor, fillColor, fillColor ],
					[ 0.85, 0.6, 0.45, 0.55, 0.75 ],
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR,
					[ 0, 0x5F, 0x7F, 0xBF, 0xFF ]);
				
				filters = [ new BlurFilter(0, 4) ];
				break;
			}
		}
	}

	/**
	 *  We don't use 'is' to prevent dependency issues
	 */
	static private var acbs:Object = {};

	static private function isApplicationControlBar(parent:Object):Boolean
	{
		var s:String = getQualifiedClassName(parent);
		if (acbs[s] == 1)
			return true;

		if (acbs[s] == 0)
			return false;

		if (s == "mx.containers::ApplicationControlBar")
		{
			acbs[s] == 1;
			return true;
		}

		var x:XML = describeType(parent);
		var xmllist:XMLList = x.extendsClass.(@type == "mx.containers::ApplicationControlBar");
		if (xmllist.length() == 0)
		{
			acbs[s] = 0;
			return false;
		}
		
		acbs[s] = 1;
		return true;
	}
}

}
