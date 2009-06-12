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

package mx.graphics
{

import flash.display.BitmapData;
import flash.display.Graphics;
import flash.display.Shape;
import flash.filters.DropShadowFilter;
import flash.geom.Matrix;
import flash.geom.Point;
import flash.geom.Rectangle;
import mx.core.FlexShape;
import mx.utils.GraphicsUtil;

/**
 *  Drop shadows are typically created using the DropShadowFilter class.
 *  However, the DropShadowFilter, like all bitmap filters,
 *  can be computationally expensive.
 *  If the DropShadowFilter is applied to a DisplayObject,
 *  then the drop shadow is recalculated
 *  whenever the appearance of the object changes.
 *  If the DisplayObject is animated (using a Resize effect, for example),
 *  then the presence of drop shadows hurts the animation refresh rate.
 *
 *  <p>This class optimizes drop shadows for a common case.
 *  If you are applying a drop shadow to a rectangularly-shaped object
 *  whose edges fall on pixel boundaries, then this class should
 *  be used instead of using the DropShadowFilter directly.</p>
 *
 *  <p>This class accepts the first four parameters that are passed
 *  to DropShadowFilter: <code>alpha</code>, <code>angle</code>,
 *  <code>color</code>, and <code>distance</code>.
 *  In addition, this class accepts the corner radii for each of the four
 *  corners of the rectangularly-shaped object that is casting a shadow.</p>
 *
 *  <p>Once those 8 values have been set,
 *  this class pre-computes the drop shadow in an offscreen Bitmap.
 *  When the <code>drawShadow()</code> method is called, pieces of the
 *  precomputed drop shadow are  copied onto the passed-in Graphics object.</p>
 *  
 *  @see flash.filters.DropShadowFilter
 *  @see flash.display.DisplayObject
 */
public class RectangularDropShadow
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function RectangularDropShadow()
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
	 *  The drop shadow is rendered into this BitmapData object,
	 *  which is later copied to the passed-in Graphics
	 */	 
	private var shadow:BitmapData;
	
	/**
	 *  @private
	 */
	private var leftShadow:BitmapData;
	
	/**
	 *  @private
	 */
	private var rightShadow:BitmapData;

	/**
	 *  @private
	 */
	private var topShadow:BitmapData;

	/**
	 *  @private
	 */
	private var bottomShadow:BitmapData;

	/**
	 *  @private
	 *  Remembers whether any of the public properties have changed
	 *  since the most recent call to drawDropShadow().
	 */
	private var changed:Boolean = true;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  alpha
    //----------------------------------

    /**
     *  @private
     *  Storage for the alpha property.
     */
    private var _alpha:Number = 0.4;

	[Inspectable]

    /**
     *  @copy flash.filters.DropShadowFilter#alpha
     */
    public function get alpha():Number
    {
        return _alpha;
    }

    /**
     *  @private
     */
    public function set alpha(value:Number):void
    {
    	if (_alpha != value)
    	{
			_alpha = value;
			changed = true;
		}
    }

    //----------------------------------
    //  angle
    //----------------------------------

    /**
     *  @private
     *  Storage for the angle property.
     */
    private var _angle:Number = 45.0;

	[Inspectable]

    /**
     *  @copy flash.filters.DropShadowFilter#angle
     */
    public function get angle():Number
    {
        return _angle;
    }

    /**
     *  @private
     */
    public function set angle(value:Number):void
    {
    	if (_angle != value)
    	{
			_angle = value;
			changed = true;
		}
    }

    //----------------------------------
    //  color
    //----------------------------------

    /**
     *  @private
     *  Storage for the color property.
     */
    private var _color:int = 0;

	[Inspectable]

    /**
     *  @copy flash.filters.DropShadowFilter#color
     */
    public function get color():int
    {
        return _color;
    }

    /**
     *  @private
     */
    public function set color(value:int):void
    {
    	if (_color != value)
    	{
			_color = value;
			changed = true;
		}
    }

    //----------------------------------
    //  distance
    //----------------------------------

    /**
     *  @private
     *  Storage for the distance property.
     */
    private var _distance:Number = 4.0;

	[Inspectable]

    /**
     *  @copy flash.filters.DropShadowFilter#distance
     */
    public function get distance():Number
    {
        return _distance;
    }

    /**
     *  @private
     */
    public function set distance(value:Number):void
    {
    	if (_distance != value)
    	{
			_distance = value;
			changed = true;
		}
    }

    //----------------------------------
    //  tlRadius
    //----------------------------------

    /**
     *  @private
     *  Storage for the tlRadius property.
     */
    private var _tlRadius:Number = 0;

	[Inspectable]

    /**
     *  The corner radius of the top left corner
	 *  of the rounded rectangle that is casting the shadow.
	 *  May be zero for non-rounded rectangles.
     */
    public function get tlRadius():Number
    {
        return _tlRadius;
    }

    /**
     *  @private
     */
    public function set tlRadius(value:Number):void
    {
    	if (_tlRadius != value)
    	{
			_tlRadius = value;
			changed = true;
		}
    }

    //----------------------------------
    //  trRadius
    //----------------------------------

    /**
     *  @private
     *  Storage for the trRadius property.
     */
    private var _trRadius:Number = 0;

	[Inspectable]

    /**
     *  The corner radius of the top right corner
	 *  of the rounded rectangle that is casting the shadow.
	 *  May be zero for non-rounded rectangles.
     */
    public function get trRadius():Number
    {
        return _trRadius;
    }

    /**
     *  @private
     */
    public function set trRadius(value:Number):void
    {
    	if (_trRadius != value)
    	{
			_trRadius = value;
			changed = true;
		}
    }

    //----------------------------------
    //  blRadius
    //----------------------------------

    /**
     *  @private
     *  Storage for the blRadius property.
     */
    private var _blRadius:Number = 0;

	[Inspectable]

    /**
     *  The corner radius of the bottom left corner
	 *  of the rounded rectangle that is casting the shadow.
	 *  May be zero for non-rounded
     *  rectangles.
     */
    public function get blRadius():Number
    {
        return _blRadius;
    }

    /**
     *  @private
     */
    public function set blRadius(value:Number):void
    {
    	if (_blRadius != value)
    	{
			_blRadius = value;
			changed = true;
		}
    }

    //----------------------------------
    //  brRadius
    //----------------------------------

    /**
     *  @private
     *  Storage for the brRadius property.
     */
    private var _brRadius:Number = 0;

	[Inspectable]

    /**
     *  The corner radius of the bottom right corner
	 *  of the rounded rectangle that is casting the shadow.
	 *  May be zero for non-rounded rectangles.
     */
    public function get brRadius():Number
    {
        return _brRadius;
    }

    /**
     *  @private
     */
    public function set brRadius(value:Number):void
    {
    	if (_brRadius != value)
    	{
			_brRadius = value;
			changed = true;
		}
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
	
    /**
     *  Renders the shadow on the screen. 
     *  
     *  @param g The Graphics object on which to draw the shadow.
     *  
     *  @param x The horizontal offset of the drop shadow,
	 *  based on the Graphics object's position.
     *  
     *  @param y The vertical offset of the drop shadow,
	 *  based on the Graphics object's position.
     *  
     *  @param width The width of the shadow, in pixels.
     *  
     *  @param height The height of the shadow, in pixels.
    */
	public function drawShadow(g:Graphics, 
							   x:Number, y:Number, 
							   width:Number, height:Number):void
	{	
		// If any parameters of the shadow have changed,
		// then regenerate the offscreen bitmaps.
		if (changed)
		{
			createShadowBitmaps();
			changed = false;
		}
		
		width = Math.ceil(width);
		height = Math.ceil(height);
		
		// Determine the thickness of the shadow along each of the edges
		var leftThickness:int = leftShadow ? leftShadow.width : 0;
		var rightThickness:int = rightShadow ? rightShadow.width : 0;
		var topThickness:int = topShadow ? topShadow.height : 0;
		var bottomThickness:int = bottomShadow ? bottomShadow.height : 0;
		
		var widthThickness:int = leftThickness + rightThickness;
		var heightThickness:int = topThickness + bottomThickness;
		var maxCornerHeight:Number = (height + heightThickness) / 2;
		var maxCornerWidth:Number = (width + widthThickness) / 2;

		var matrix:Matrix = new Matrix();

		// Copy the corners of the shadow bitmap onto the graphics object
		if (leftShadow || topShadow)
		{
			var tlWidth:Number = Math.min(tlRadius + widthThickness,
										  maxCornerWidth);
			var tlHeight:Number = Math.min(tlRadius + heightThickness,
										   maxCornerHeight);

			matrix.tx = x - leftThickness;
			matrix.ty = y - topThickness;

			g.beginBitmapFill(shadow, matrix);
			g.drawRect(x - leftThickness, y - topThickness, tlWidth, tlHeight);
			g.endFill();
		}
		
		if (rightShadow || topShadow)
		{
			var trWidth:Number = Math.min(trRadius + widthThickness,
										  maxCornerWidth);
			var trHeight:Number = Math.min(trRadius + heightThickness,
										   maxCornerHeight);

			matrix.tx = x + width + rightThickness - shadow.width;
			matrix.ty = y - topThickness;			

			g.beginBitmapFill(shadow, matrix);
			g.drawRect(x + width + rightThickness - trWidth,
					   y - topThickness,
					   trWidth, trHeight);
			g.endFill();
		}
		
		if (leftShadow || bottomShadow)
		{
			var blWidth:Number = Math.min(blRadius + widthThickness,
										  maxCornerWidth);
			var blHeight:Number = Math.min(blRadius + heightThickness,
										   maxCornerHeight);

			matrix.tx = x - leftThickness;
			matrix.ty = y + height + bottomThickness - shadow.height;

			g.beginBitmapFill(shadow, matrix);
			g.drawRect(x - leftThickness, 
					   y + height + bottomThickness - blHeight,
					   blWidth, blHeight);
			g.endFill();
		}
			
		if (rightShadow || bottomShadow)
		{
			var brWidth:Number = Math.min(brRadius + widthThickness,
										  maxCornerWidth);
			var brHeight:Number = Math.min(brRadius + heightThickness,
										   maxCornerHeight);

			matrix.tx = x + width + rightThickness - shadow.width; 
			matrix.ty = y + height + bottomThickness - shadow.height; 

			g.beginBitmapFill(shadow, matrix);
			g.drawRect(x + width + rightThickness - brWidth, 
					   y + height + bottomThickness - brHeight,
					   brWidth, brHeight);
			g.endFill();
		}

		// Copy the sides of the shadow bitmap onto the graphics object
		if (leftShadow)
		{		
			matrix.tx = x - leftThickness;
			matrix.ty = 0;

			g.beginBitmapFill(leftShadow, matrix);
			g.drawRect(x - leftThickness, 
					   y - topThickness + tlHeight,
					   leftThickness,
					   height + topThickness +
					   bottomThickness - tlHeight - blHeight);
			g.endFill();
		}

		if (rightShadow)
		{		
			matrix.tx = x + width;
			matrix.ty = 0;

			g.beginBitmapFill(rightShadow, matrix);
			g.drawRect(x + width,
					   y - topThickness + trHeight,
					   rightThickness,
					   height + topThickness +
					   bottomThickness - trHeight - brHeight);
			g.endFill();
		}
		
		if (topShadow)
		{		
			matrix.tx = 0;
			matrix.ty = y - topThickness;

			g.beginBitmapFill(topShadow, matrix);
			g.drawRect(x - leftThickness + tlWidth,
					   y - topThickness, 
					   width + leftThickness +
					   rightThickness - tlWidth - trWidth,
					   topThickness);
			g.endFill();
		}

		if (bottomShadow)
		{
			matrix.tx = 0;
			matrix.ty = y + height;

			g.beginBitmapFill(bottomShadow, matrix);
			g.drawRect(x - leftThickness + blWidth,
					   y + height,
					   width + leftThickness +
					   rightThickness - blWidth - brWidth, 
					   bottomThickness);
			g.endFill();		
		}
	}	

	/**
	 *  @private
	 *  Render the drop shadow for the rounded rectangle
	 *  in a small BitmapData object.
	 *  The shadow will be copied onto the graphics object
	 *  passed into drawDropShadow().
	 */
	private function createShadowBitmaps():void
	{		
		// Create a Shape containing a round rectangle that the
		// specified corner radii and very short sides.
		var roundRectWidth:Number = Math.max(tlRadius, blRadius) + 
								    2 * distance + 
									Math.max(trRadius, brRadius);
		var roundRectHeight:Number = Math.max(tlRadius, trRadius) +
									 2 * distance +
									 Math.max(blRadius, brRadius);

		if (roundRectWidth < 0 || roundRectHeight < 0)
			return;
			
		var roundRect:Shape = new FlexShape();
		var g:Graphics = roundRect.graphics;
		g.beginFill(0xFFFFFF);
		GraphicsUtil.drawRoundRectComplex(
			g, 0, 0, roundRectWidth, roundRectHeight,
			tlRadius, trRadius, blRadius, brRadius);
		g.endFill();
		
		// Copy the round rectangle into a BitmapData object
		var roundRectBitmap:BitmapData = new BitmapData(
			roundRectWidth,
			roundRectHeight,
			true,
			0x00000000);
		roundRectBitmap.draw(roundRect, new Matrix());

		// Get the size of the drop shadow that will be cast by this
		// rounded rectangle.
		var filter:DropShadowFilter = 
			new DropShadowFilter(distance, angle, color, alpha);
		filter.knockout = true;	
		var inputRect:Rectangle = new Rectangle(0, 0, 
			roundRectWidth, roundRectHeight);
		var outputRect:Rectangle = 
			roundRectBitmap.generateFilterRect(inputRect, filter);
	
		// Determine the thickness of each edge of the drop shadow
		var leftThickness:Number = inputRect.left - outputRect.left;
		var rightThickness:Number = outputRect.right - inputRect.right;
		var topThickness:Number = inputRect.top - outputRect.top;
		var bottomThickness:Number = outputRect.bottom - inputRect.bottom;

		// Create a BitmapData object large enough to contain the
		// rounded rectangle and its drop shadow.  Render the drop
		// shadow into this BitmapData
		shadow = new BitmapData(outputRect.width, outputRect.height);
		shadow.applyFilter(roundRectBitmap, inputRect, 
			new Point(leftThickness, topThickness),
			filter);
			
		// For each of the four sides of the round rectangle, create a copy
		// of the drop shadow in a separate BitmapData object
		var origin:Point = new Point(0, 0);
		var rect:Rectangle = new Rectangle();
		
		if (leftThickness > 0)
		{
			rect.x = 0;
			rect.y = tlRadius + topThickness + bottomThickness;
			rect.width = leftThickness;
			rect.height = 1;
			
			leftShadow = new BitmapData(leftThickness, 1);
			leftShadow.copyPixels(shadow, rect, origin);
		}
		else
		{
			leftShadow = null;
		}
		
		if (rightThickness > 0)
		{
			rect.x = shadow.width - rightThickness;
			rect.y = trRadius + topThickness + bottomThickness;
			rect.width = rightThickness;
			rect.height = 1;
			
			rightShadow = new BitmapData(rightThickness, 1);
			rightShadow.copyPixels(shadow, rect, origin);
		}
		else
		{
			rightShadow = null;
		}
		
		if (topThickness > 0)
		{
			rect.x = tlRadius + leftThickness + rightThickness;
			rect.y = 0;
			rect.width = 1;
			rect.height = topThickness;
			
			topShadow = new BitmapData(1, topThickness);
			topShadow.copyPixels(shadow, rect, origin);
		}
		else
		{
			topShadow = null;
		}
		
		if (bottomThickness > 0)
		{
			rect.x = blRadius + leftThickness + rightThickness;
			rect.y = shadow.height - bottomThickness;
			rect.width = 1;
			rect.height = bottomThickness;
			
			bottomShadow = new BitmapData(1, bottomThickness);
			bottomShadow.copyPixels(shadow, rect, origin);
		}
		else
		{
			bottomShadow = null;
		}		
	}	
}

}
