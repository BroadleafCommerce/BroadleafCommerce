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

import flash.display.Graphics;
import mx.skins.ProgrammaticSkin;
import mx.utils.GraphicsUtil;

/**
 *  The skin for the focus indicator.
 */
public class HaloFocusRect extends ProgrammaticSkin
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function HaloFocusRect()
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

		var rectCol:Number = getStyle("focusColor");
		var a:Number = getStyle("focusAlpha");
		var r:Number = getStyle("cornerRadius");
		var thickness:Number = getStyle("focusThickness");
		var roundedCorners:String = getStyle("focusRoundedCorners");
		
		if (!rectCol)
			rectCol = getStyle("themeColor");
			
		var g:Graphics = graphics;
		
		g.clear();
		
		if (roundedCorners != "tl tr bl br" && r > 0)
		{
			// We have rounded corners on just some of the corners.
			
			var tl:Number = 0;
			var bl:Number = 0;
			var tr:Number = 0;
			var br:Number = 0;
			
			var nr:Number = r + thickness;
			
			if (roundedCorners.indexOf("tl") >= 0)
				tl = nr;

			if (roundedCorners.indexOf("tr") >= 0)
				tr = nr;

			if (roundedCorners.indexOf("bl") >= 0)
				bl = nr;

			if (roundedCorners.indexOf("br") >= 0)
				br = nr;
				
			// Outer ring
			g.beginFill(rectCol, a);
			GraphicsUtil.drawRoundRectComplex(g, 0, 0, w, h, tl, tr, bl, br);
			tl = tl ? r : 0;
			tr = tr ? r : 0;
			bl = bl ? r : 0;
			br = br ? r : 0;
			GraphicsUtil.drawRoundRectComplex(g, thickness, thickness,
								   w - 2 * thickness, h - 2 * thickness,
								   tl, tr, bl, br);
			g.endFill();
			
			// Inner ring
			nr = r + (thickness / 2);
			tl = tl ? nr : 0;
			tr = tr ? nr : 0;
			bl = bl ? nr : 0;
			br = br ? nr : 0;
			g.beginFill(rectCol, a);
			GraphicsUtil.drawRoundRectComplex(g, thickness / 2, thickness / 2,
								   w - thickness, h - thickness,
								   tl, tr, bl, br);
			tl = tl ? r : 0;
			tr = tr ? r : 0;
			bl = bl ? r : 0;
			br = br ? r : 0;
			GraphicsUtil.drawRoundRectComplex(g, thickness, thickness,
								   w - 2 * thickness, h - 2 * thickness,
								   tl, tr, bl, br);
			g.endFill();
		}
		else
		{
			// Outer ring
			g.beginFill(rectCol, a);
			g.drawRoundRect(0, 0, w, h, r > 0 ? r + thickness : r);
			g.drawRoundRect(thickness, thickness,
							w - 2 * thickness, h - 2 * thickness, r);
			g.endFill();

			// Inner ring
			g.beginFill(rectCol, a);
			g.drawRoundRect(thickness / 2, thickness / 2,
							w - thickness, h - thickness,
							r > 0 ? r + thickness / 2 : r);
			g.drawRoundRect(thickness, thickness,
							w - 2 * thickness, h - 2 * thickness, r);
			g.endFill();
		}
	}
}

}
