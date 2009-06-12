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

package haloclassic
{

import flash.display.Graphics;
import mx.collections.IList;
import mx.core.UIComponent;
import mx.core.mx_internal;

/**
 *  Documentation is not currently available.
 *  @review
 */
public class SwatchSkin extends UIComponent
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
    mx_internal var colorList:IList; 

    /**
     *  @private
     */
    mx_internal var colorField:String = "color";

    /**
     *  @private
     */   
    mx_internal var color:uint = 0x000000;
     
	/**
	 *  @private
	 */        
	mx_internal var owner:Object;

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------    

    /**
     *  @private
     *	If this object is resized, then set a flag so that it's redrawn
     */
    override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
    {
		super.updateDisplayList(unscaledWidth, unscaledHeight);

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
                var w:Number = UIComponent(parent).width / Math.abs(UIComponent(parent).scaleX);
                var h:Number = UIComponent(parent).height / Math.abs(UIComponent(parent).scaleY);
                
				g.clear();
                drawSwatch(0, 0, w, h, c);
                break;
            }

            case "swatchPreview":
            {
                // Load styles...
                var previewWidth:Number =
					getStyle("previewWidth");
                var previewHeight:Number =
					getStyle("previewHeight");
        
                g.clear();
                drawSwatch(0, 0, previewWidth, previewHeight, c);
                drawBorder(0, 0, previewWidth, previewHeight,
						   0x999999, 0xFFFFFF, 1, 1.0);
                break;
            }
            case "swatchHighlight":
            {
                // Load styles...
                var swatchWidth:Number =
					getStyle("swatchWidth");
                var swatchHeight:Number =
					getStyle("swatchHeight");
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
        // Load styles...
        var columnCount:int = getStyle("columnCount");
        var verticalGap:Number = getStyle("verticalGap");
        var horizontalGap:Number = getStyle("horizontalGap");
        var swatchWidth:Number = getStyle("swatchWidth");
        var swatchHeight:Number = getStyle("swatchHeight");
        var swatchGridBorderSize:Number =
			getStyle("swatchGridBorderSize");
        var swatchGridBackgroundColor:uint =
			getStyle("swatchGridBackgroundColor");
        var previewWidth:Number = getStyle("previewWidth");
        var textFieldWidth:Number = getStyle("textFieldWidth");

        var cellOffset:int = 1;
        var itemOffset:int = 3;

        // Adjust for dataProviders that are less than the columnCount
        var length:int = dp.length;
        if (columnCount > length)
            columnCount = length;

        // Define local values...
        var rows:Number = Math.ceil(length / columnCount);
        var totalWidth:Number = columnCount * (swatchWidth - cellOffset) +
								cellOffset +
								(columnCount - 1) * horizontalGap +
								2 * swatchGridBorderSize;
        var totalHeight:Number = rows * (swatchHeight - cellOffset) +
								 cellOffset +
								 (rows - 1) * verticalGap +
								 2 * swatchGridBorderSize;

        // Adjust width if it falls shorter than the width of the preview area
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
            
			var c:Number = (typeof(dp.getItemAt(n)) != "object") ?
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
