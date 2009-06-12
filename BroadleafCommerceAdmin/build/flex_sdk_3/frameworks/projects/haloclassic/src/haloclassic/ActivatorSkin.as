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

import flash.display.DisplayObject;
import flash.display.GradientType;
import mx.containers.ApplicationControlBar;
import mx.core.Application;
import mx.skins.Border;
import mx.styles.IStyleClient;

/**
 *  Documentation is not currently available.
 *  @review
 */
public class ActivatorSkin extends Border
{
	include "../mx/core/Version.as";

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
			drawHaloRect(width, height);
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
		var themeColor:uint = getStyle("themeColor");

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
				// outer border
				drawRoundRect(
					x, y, w, h, 0,
					0x919999, 1);
				
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2, 0,
					[ 0x333333, 0xFCFCFC ], 1,
					rotatedGradientMatrix(0, 0, w, h, -90),
					GradientType.RADIAL);
				
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2, 0,
					themeColor, 0.5);
				
				// highlight
				drawRoundRect(
					x + 3, y + 3, w - 6, h - 6, 0,
					0xFFFFFF, 1);
				
				// face
				drawRoundRect(
					x + 3, y + 4, w - 6, h - 7, 0,
					themeColor, 0.2);

				break;
			}

			case "itemOverSkin":
			{
				// OuterBorder
				drawRoundRect(
					x, y, w, h, 0,
					0x919999, 1);
				
				// OuterBorder
				drawRoundRect(
					x, y, w, h, 0,
					themeColor, 0.5);
				
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 2, 0,
					[ 0xFFFFFF, 0xDDDDDD ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.RADIAL);
				
				// highlight
				drawRoundRect(
					x + 3, y + 3, w - 6, h - 6, 0,
					0xFFFFFF, 1);
				
				// face
				drawRoundRect(
					x + 3, y + 4, w - 6, h - 7, 0,
					0xF8F8F8, 1);

				break;
			}
		}
	}

	/**
	 *  @private
	 *  The drawTranslucentHaloRect function is called when the "translucent"
	 *  style is set to true, which happens when a MenuBar is inside an ACB.
	 */
	private function drawTranslucentHaloRect(w:Number,h:Number):void
	{
		// Find the parent app bar's colors
		var p:IStyleClient = parent as IStyleClient;
		while (p && !(p is ApplicationControlBar))
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
			// the docked bar always has a background, so take the
			// main application's background color
			var pabc:Object =
				Application.application.getStyle("backgroundColor");
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
					[ 1, 0.75, 0.60, 0.70, 0.90 ],
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR,
					[ 0, 0x5F, 0x7F, 0xBF, 0xFF ]);

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
					[ 0.85, 0.60, 0.45, 0.55, 0.75 ],
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR,
					[ 0, 0x5F, 0x7F, 0xBF, 0xFF ]);

				break;
			}
		}
	}
}

}
