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

import flash.display.Graphics;
import mx.core.FlexVersion;
import mx.skins.Border;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of the next-year and previous-year
 *  buttons in a DateChooser.
 */
public class DateChooserYearArrowSkin extends Border
{
	include "../../core/Version.as";
				
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function DateChooserYearArrowSkin()
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
		return 6;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ? 4 : 6;
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

		var themeColor:uint = getStyle("themeColor");
		
		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);

		var arrowColor:uint = getStyle("iconColor");

		var g:Graphics = graphics;
	
		g.clear();
	
		switch (name)
		{
			case "prevYearUpSkin":
			case "nextYearUpSkin":
			{
				break;
			}

			case "prevYearOverSkin":
			case "nextYearOverSkin":
			{
				arrowColor = themeColor;
				break;
			}

			case "prevYearDownSkin":
			case "nextYearDownSkin":		
			{
				arrowColor = themeColorDrk1;
				break;
			}

			case "prevYearDisabledSkin":
			case "nextYearDisabledSkin":
			{
				arrowColor = getStyle("disabledIconColor");
				break;
			}
		}
		
		// Viewable Button area				
		g.beginFill(arrowColor);
		if (name.charAt(0) == "p")
		{
			g.moveTo(w / 2, h / 2 + 2);
			g.lineTo(w / 2 - 3, h / 2 - 2);
			g.lineTo(w / 2 + 3, h / 2 - 2);
			g.lineTo(w / 2, h / 2 + 2);
		}
		else
		{								
			g.moveTo(w / 2, h / 2 - 2);
			g.lineTo(w / 2 - 3, h / 2 + 2);
			g.lineTo(w / 2 + 3, h / 2 + 2);
			g.lineTo(w / 2, h / 2 - 2);
		}
		g.endFill();				
	}
}

}
