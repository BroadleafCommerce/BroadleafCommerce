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

import flash.display.Graphics;
import mx.core.EdgeMetrics;
import mx.skins.RectangularBorder;

/**
 *  The skin for a ToolTip.
 */
public class ToolTipBorder extends RectangularBorder
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
	public function ToolTipBorder() 
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
	private var _borderMetrics:EdgeMetrics;

	/**
	 *  @private
	 */
	override public function get borderMetrics():EdgeMetrics
	{		
		if (_borderMetrics)
			return _borderMetrics;
			
		var borderStyle:String = getStyle("borderStyle");
		switch (borderStyle)
		{
			case "errorTipRight":
			{
 				_borderMetrics = new EdgeMetrics(15, 1, 3, 3);
				break;
			}
			
			case "errorTipAbove":
			{
 				_borderMetrics = new EdgeMetrics(3, 1, 3, 15);
 				break;
			}
		
			case "errorTipBelow":
			{
 				_borderMetrics = new EdgeMetrics(3, 13, 3, 3);
 				break;
			}
			
 			default: // "toolTip"
			{
				_borderMetrics = new EdgeMetrics(3, 1, 3, 3);
 				break;
			}
 		}
		
		return _borderMetrics;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Draw the background and border.
	 */
	override protected function updateDisplayList(w:Number, h:Number):void
	{	
		super.updateDisplayList(w, h);

		var backgroundColor:uint = getStyle("backgroundColor");
		var borderColor:uint = getStyle("borderColor");
		var borderStyle:String = getStyle("borderStyle");
		var shadowColor:uint = getStyle("shadowColor");
		var shadowAlpha:Number = 0.1;

		var g:Graphics = graphics;

		g.clear();

		switch (borderStyle)
		{
			case "toolTip":
			{
				// outer shadow 
				drawRoundRect(
					0, 5, w, h - 5, 5,
					shadowColor, 0.10); 

				// inner shadow
				drawRoundRect(
					1, 4, w - 2, h - 5, 4,
					shadowColor, 0.10);

				// border
				drawRoundRect(
					2, 0, w - 4, h - 2, 3,
					borderColor, 1); 

				// face
				drawRoundRect(
					3, 1, w - 6, h - 4, 2,
					backgroundColor, 1); 

				break;
			}

			case "errorTipRight":
			{
				// outer shadow 
				drawRoundRect(
					x + 11, y + 5, w - 11, h - 5, 5,
					shadowColor, shadowAlpha);

				// inner shadow 
				drawRoundRect(
					x + 13, y + 4, w - 13, h - 5, 4,
					shadowColor, shadowAlpha);

				// border 
				drawRoundRect(
					x + 11, y, w - 11, h - 2, 3,
					borderColor, 1); 

				// face
				drawRoundRect(
					x + 13, y + 1, w - 13, h - 4, 2,
					borderColor, 1);

				// left shadow 
				g.beginFill(shadowColor, shadowAlpha);
				g.moveTo(x + 11, y + 7);
				g.lineTo(x, y + 14);
				g.lineTo(x + 11, y + 20);
				g.moveTo(x + 11, y + 8);
				g.endFill();

				// left pointer 
				g.beginFill(borderColor);
				g.moveTo(x + 11, y + 7);
				g.lineTo(x, y + 13);
				g.lineTo(x + 11, y + 19);
				g.moveTo(x + 11, y + 7);
				g.endFill();

				break;
			}

			case "errorTipAbove":
			{
				// outer Shadow 
				drawRoundRect(
					x, y + 5, w, h - 16, 5,
					shadowColor, shadowAlpha);

				// inner Shadow 
				drawRoundRect(
					x + 1, y + 4, w - 2, h - 16, 4,
					shadowColor, shadowAlpha);

				// border 
				drawRoundRect(
					x, y, w, h - 13, 3,
					borderColor, 1); 

				// face
				drawRoundRect(
					x + 1, y + 1, w - 2, h - 15, 2,
					borderColor, 1);

				g.beginFill(shadowColor, shadowAlpha);

				// bottom shadow
				g.moveTo(x + 9, y + h - 11);
				g.lineTo(x + 15, y + h);
				g.lineTo(x + 21, y + h - 11);
				g.moveTo(x + 10, y + h - 11);
				g.endFill();

				// bottom pointer 
				g.beginFill(borderColor);
				g.moveTo(x + 9, y + h - 13);
				g.lineTo(x + 15, y + h - 2);
				g.lineTo(x + 21, y + h - 13);
				g.moveTo(x + 9, y + h - 13);
				g.endFill();

				break;
			}

			case "errorTipBelow":
			{
				// outer shadow 
				drawRoundRect(
					x, y + 16, w, h - 16, 5,
					shadowColor, shadowAlpha);

				// inner shadow 
				drawRoundRect(
					x + 1, y + 15, w - 2, h - 16, 4,
					shadowColor, shadowAlpha);

				// border 
				drawRoundRect(
					x, y + 11, w, h - 13, 3,
					borderColor, 1); 

				// face
				drawRoundRect(
					x + 1, y + 13, w - 2, h - 15, 2,
					borderColor, 1);

				// top pointer 
				g.beginFill(borderColor);
				g.moveTo(x + 9, y + 11);
				g.lineTo(x + 15, y);
				g.lineTo(x + 21, y + 11);
				g.moveTo(x + 10, y + 11);
				g.endFill();
				
				break;
			}
		}
	}

	/**
	 *  @private
	 *  If borderStyle may have changed, clear the cached border metrics.
	 */
	override public function styleChanged(styleProp:String):void
	{
		if (styleProp == "borderStyle" ||
			styleProp == "styleName")
		{
			_borderMetrics = null;
		}
		
		invalidateDisplayList();
	}
}

}
