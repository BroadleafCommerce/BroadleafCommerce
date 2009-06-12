////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.Graphics;
import mx.skins.Border;

/**
 *  The skin for all the states of a ColorPicker.
 */
public class ColorPickerSkin extends Border
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
    public function ColorPickerSkin()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    private var borderShadowColor:uint = 0x9A9B9D;

    /**
     *  @private
     */
    private var borderHighlightColor:uint = 0xFEFEFE;
    
    /**
     *  @private
     */
	private var backgroundColor:uint = 0xE5E6E7;
    
    /**
     *  @private
     */
	private var borderSize:Number = 1;
    
    /**
     *  @private
     */
	private var bevelSize:Number = 1;
    
    /**
     *  @private
     */
	private var arrowWidth:Number = 7;
    
    /**
     *  @private
     */
	private var arrowHeight:Number = 5;

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

		var arrowColor:uint = getStyle("iconColor");

        var arrowX:Number = (w - arrowWidth - bevelSize);
        var arrowY:Number = (h - arrowHeight - bevelSize);

        graphics.clear();
        
        if (name == "upSkin" || name == "overSkin")
        {
            // invisible hit area
			drawFill(x, y, w + bevelSize, h + bevelSize, 0xCCCCCC, 0);
            
			// outer border
			drawBorder(x, y, w, h, borderHighlightColor, borderShadowColor,
					   bevelSize, 1.0); 

            // background
			drawBorder(x + bevelSize, y + bevelSize,
					   w - (bevelSize * 2), h - (bevelSize * 2),
					   backgroundColor, backgroundColor, borderSize, 1.0);                      
            
			// inner border
			drawBorder(x + bevelSize + borderSize, y + bevelSize + borderSize,
					   w - ((bevelSize + borderSize) * 2),
					   h - ((bevelSize + borderSize) * 2),
					   borderShadowColor, borderHighlightColor,
					   bevelSize, 1.0);     
            
			// arrow background
			drawFill(arrowX, arrowY, arrowWidth, arrowHeight,
					 backgroundColor, 1.0);                                                     
			
            // arrow
			drawArrow(arrowX + 1.5, arrowY + 1.5,
					  arrowWidth - 3, arrowHeight - 3, arrowColor, 1.0);                                 
        }

        else if (name == "downSkin")
        {
			// invisible hit area
            drawFill(x, y, w, h, 0xCCCCCC, 0);
            
			// outer border
			drawBorder(x, y, w, h, borderHighlightColor, 0xCCCCCC,
					   bevelSize, 1.0);
            
			// background
			drawBorder(x + bevelSize, y + bevelSize,
					   w - 2 * bevelSize, h - 2 * bevelSize,
					   backgroundColor, backgroundColor, borderSize, 1.0);
            
			// inner border
			drawBorder(x + bevelSize + borderSize, y + bevelSize + borderSize,
					   w - 2 * (bevelSize + borderSize),
					   h - 2 * (bevelSize + borderSize),
					   borderShadowColor, borderHighlightColor,
					   bevelSize, 1.0);
            
			// arrow background
			drawFill(arrowX, arrowY, arrowWidth, arrowHeight,
					 backgroundColor, 1.0);
            
			// arrow
			drawArrow(arrowX + 1.5, arrowY + 1.5,
					  arrowWidth - 3, arrowHeight - 3, arrowColor, 1.0);
        }

        else if (name == "disabledSkin")
        {
        	arrowColor = getStyle("disabledIconColor");
        	
			// For blur effect when disabled
            drawRoundRect(
				x, y, w, h, 0,
				0xFFFFFF, 0.6);
            
			// invisible hit area
			drawFill(x, y, w, h, 0xFFFFFF, 0.25);
            
			// outer border
			drawBorder(x, y, w, h, borderHighlightColor, 0xCCCCCC,
					   bevelSize, 1.0);
            
			// background
			drawBorder(x + bevelSize, y + bevelSize,
					   w - (bevelSize * 2), h - (bevelSize * 2),
					   backgroundColor, backgroundColor, borderSize, 1.0);
            
			// inner border        
			drawBorder(x + bevelSize + borderSize, y + bevelSize + borderSize,
					   w - 2 * (bevelSize + borderSize),
					   h - 2 * (bevelSize + borderSize),
					   borderShadowColor, borderHighlightColor,
					   bevelSize, 1.0);
            
			// arrow background                
			drawFill(arrowX, arrowY, arrowWidth, arrowHeight,
					 backgroundColor, 1.0);
            
			// blurred arrow        
			drawArrow(arrowX + 1.5, arrowY + 1.5,
					  arrowWidth - 3, arrowHeight - 3, arrowColor, 1.0);
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
    private function drawBorder(x:Number, y:Number, w:Number, h:Number,
							    c1:Number, c2:Number, s:Number, a:Number):void
    {
		// border line on the left side
        drawFill(x, y, s, h, c1, a);

        // border line on the top side
        drawFill(x, y, w, s, c1, a);

        // border line on the right side
        drawFill(x + (w - s), y, s, h, c2, a);

        // border line on the bottom side
        drawFill(x, y + (h - s), w, s, c2, a);
    }

    /**
     *  @private
     */    
    private function drawFill(x:Number, y:Number, w:Number, h:Number,
							  c:Number, a:Number):void
    {
        var g:Graphics = graphics;
        g.moveTo(x, y);
        g.beginFill(c, a);
        g.lineTo(x + w, y);
        g.lineTo(x + w, h + y);
        g.lineTo(x, h + y);
        g.lineTo(x, y);
        g.endFill();
    }

    /**
     *  @private
     */    
    private function drawArrow(x:Number, y:Number, w:Number, h:Number,
							   c:Number, a:Number):void
    {
		var g:Graphics = graphics;    
        g.moveTo(x, y);
        g.beginFill(c, a);
        g.lineTo(x + w, y);
        g.lineTo(x + w / 2, h + y);
        g.lineTo(x, y);
        g.endFill();
    }
}

}
