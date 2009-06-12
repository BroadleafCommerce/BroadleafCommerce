////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import mx.skins.Border;

/**
 *  The skin for the border of a SwatchPanel. 
 */
public class SwatchPanelSkin extends Border
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
    public function SwatchPanelSkin()
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

        if (name == "swatchPanelBorder")
        {
            var backgroundColor:uint = getStyle("backgroundColor");
				// used for darker color in the gradient

            var borderColor:uint = getStyle("borderColor");
				// used for outer border top

            var highlightColor:uint = getStyle("highlightColor");
				// used for white edge
				// and also for lighter color in the gradient

            var shadowColor:uint = getStyle("shadowColor");
				// used for outer border bottom

            var x:Number = 0;
            var y:Number = 0;
            
			graphics.clear();

			// outer border top
            drawRoundRect(
				x, y, w, h, 0,
				borderColor, 1);

 			// outer border bottom
            drawRoundRect(
				x + 1, y + 1, w - 1, h - 1, 0,
				shadowColor, 1);

			// white edge
            drawRoundRect(
				x + 1, y + 1, w - 2, h - 2, 0,
				highlightColor, 1);

			// gradient fill
            drawRoundRect(
				x + 2, y + 2, w - 4, h - 4, 0,
				[ backgroundColor, highlightColor ], 1,
				verticalGradientMatrix(x + 2, y + 2, w - 4, h - 4));
        }
    }
}

}
