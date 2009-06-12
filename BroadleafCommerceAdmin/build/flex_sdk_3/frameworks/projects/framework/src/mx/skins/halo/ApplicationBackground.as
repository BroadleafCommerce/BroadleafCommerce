////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.Graphics;
import mx.skins.ProgrammaticSkin;
import mx.utils.ColorUtil;

/**
 *  The skin for application background gradient.
 */
public class ApplicationBackground extends ProgrammaticSkin
{
    include "../../core/Version.as";    
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor	 
     */
    public function ApplicationBackground()
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
        return 8;
    }
    
    //----------------------------------
	//  measuredHeight
    //----------------------------------
    
    /**
     *  @private
     */        
    override public function get measuredHeight():Number
    {
        return 8;
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

        var g:Graphics = graphics;
        var fillColors:Array = getStyle("backgroundGradientColors");
        var fillAlphas:Array = getStyle("backgroundGradientAlphas");

		if (!fillColors)
		{
			var bgColor:uint = getStyle("backgroundColor");
			
			if (isNaN(bgColor))
				bgColor = 0xFFFFFF;
			
			fillColors = [];
			fillColors[0] = ColorUtil.adjustBrightness(bgColor, 15);
			fillColors[1] = ColorUtil.adjustBrightness(bgColor, -25);
		}
		
		if (!fillAlphas)
			fillAlphas = [1, 1];
		
        g.clear();
        drawRoundRect(0, 0, w, h, 0, fillColors, fillAlphas, 
        			  verticalGradientMatrix(0, 0, w, h));
    }
}

}
