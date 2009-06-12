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

import flash.display.Graphics;
import mx.collections.IList;
import mx.core.UIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The skin used for all color swatches in a ColorPicker.
 */
public class SwatchSkin extends UIComponent
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
    public function SwatchSkin()
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
    mx_internal var color:uint = 0x000000;
     
    /**
     *  @private
     */
    mx_internal var colorField:String = "color";

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

		mx_internal::updateSkin(mx_internal::color);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */    
    mx_internal function updateGrid(dp:IList):void 
    {
        if (name == "swatchGrid")
        {
            graphics.clear();
            drawGrid(dp, mx_internal::colorField);
        }
    }
    
    /**
     *  @private
     */    
    mx_internal function updateSkin(c:Number):void
    {
        var g:Graphics = graphics;
        
		switch (name)
        {
            case "colorPickerSwatch":
            {
                var w:Number = UIComponent(parent).width /
							   Math.abs(UIComponent(parent).scaleX);
                var h:Number = UIComponent(parent).height /
							   Math.abs(UIComponent(parent).scaleY);
                
				g.clear();
                drawSwatch(0, 0, w, h, c);
                break;
            }

            case "swatchPreview":
            {
                var previewWidth:Number = getStyle("previewWidth");
                var previewHeight:Number = getStyle("previewHeight");
        
                g.clear();
                drawSwatch(0, 0, previewWidth, previewHeight, c);
                drawBorder(0, 0, previewWidth, previewHeight,
						   0x999999, 0xFFFFFF, 1, 1.0);
                break;
            }

            case "swatchHighlight":
            {
                var swatchWidth:Number = getStyle("swatchWidth");
                var swatchHeight:Number = getStyle("swatchHeight");
                var swatchHighlightColor:uint =
					getStyle("swatchHighlightColor");
                var swatchHighlightSize:Number =
					getStyle("swatchHighlightSize");

                g.clear();
                drawBorder(0, 0, swatchWidth, swatchHeight,
						   swatchHighlightColor, swatchHighlightColor,
						   swatchHighlightSize, 1.0);
                break;
            }
        }
    }

    /**
     *  @private
     */    
    private function drawGrid(dp:IList, cf:String):void
    {
		var columnCount:int = getStyle("columnCount");
        var horizontalGap:Number = getStyle("horizontalGap");
        var previewWidth:Number = getStyle("previewWidth");
        var swatchGridBackgroundColor:uint =
			getStyle("swatchGridBackgroundColor");
        var swatchGridBorderSize:Number =
			getStyle("swatchGridBorderSize");
        var swatchHeight:Number = getStyle("swatchHeight");
        var swatchWidth:Number = getStyle("swatchWidth");
        var textFieldWidth:Number = getStyle("textFieldWidth");
        var verticalGap:Number = getStyle("verticalGap");

        var cellOffset:int = 1;
        var itemOffset:int = 3;

        // Adjust for dataProviders that are less than the columnCount.
        var length:int = dp.length;
        if (columnCount > length)
            columnCount = length;

        // Define local values.
        var rows:Number = Math.ceil(length / columnCount);
        if (isNaN(rows))
        	rows = 0;
        var totalWidth:Number = columnCount * (swatchWidth - cellOffset) +
								cellOffset +
								(columnCount - 1) * horizontalGap +
								2 * swatchGridBorderSize;
        var totalHeight:Number = rows * (swatchHeight - cellOffset) +
								 cellOffset +
								 (rows - 1) * verticalGap +
								 2 * swatchGridBorderSize;

        // Adjust width if it falls shorter than the width of the preview area.
        var previewArea:Number = previewWidth + textFieldWidth + itemOffset;
        if (totalWidth < previewArea)
            totalWidth = previewArea;

        // Draw the background for the swatches
        drawFill(0, 0, totalWidth, totalHeight, swatchGridBackgroundColor, 100);
		setActualSize(totalWidth, totalHeight);

        // Draw the swatches
        var cNum:int = 0;
		var rNum:int = 0;
		for (var n:int = 0; n < length; n++)
        {
            var swatchX:Number = swatchGridBorderSize + cNum *
								(swatchWidth + horizontalGap - cellOffset);
            
			var swatchY:Number = swatchGridBorderSize + rNum *
								 (swatchHeight + verticalGap - cellOffset);
            
			var c:Number = typeof(dp.getItemAt(n)) != "object" ?
						   Number(dp.getItemAt(n)) :
						   Number((dp.getItemAt(n))[mx_internal::colorField]);

            // Draw rectangle...
            drawSwatch(swatchX, swatchY, swatchWidth, swatchHeight, c);
            
			if (cNum < columnCount - 1)
            {
                cNum++
            }
            else
            {
                cNum = 0;
                rNum++
            }
        }
    }

    /**
     *  @private
     */    
    private function drawSwatch(x:Number, y:Number, w:Number, h:Number,
							    c:Number):void
    {
        // Load styles...
        var swatchBorderColor:uint =
			getStyle("swatchBorderColor");
        var swatchBorderSize:Number =
			getStyle("swatchBorderSize");

        if (swatchBorderSize == 0)
        {
            // Don't show a border...
            drawFill(x, y, w, h, c, 1.0);
        }
        else if (swatchBorderSize < 0 || isNaN(swatchBorderSize))
        {
            // Default to a border size of 1 if invalid.
            drawFill(x, y, w, h, swatchBorderColor, 1.0);
            drawFill(x + 1, y + 1, w - 2, h - 2, c, 1.0);
        }
        else
        {
            // Otherwise use specified border size.
            drawFill(x, y, w, h, swatchBorderColor, 1.0);
            drawFill(x + swatchBorderSize, y + swatchBorderSize,
					 w - 2 * swatchBorderSize, h - 2 * swatchBorderSize,
					 c, 1.0);
        }
    }

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
}

}
