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

package mx.skins
{

import flash.display.Graphics;
import flash.display.Shape;
import flash.geom.Matrix;
import mx.core.IInvalidating;
import mx.core.IFlexDisplayObject;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.managers.ILayoutManagerClient;
import mx.styles.ISimpleStyleClient;
import mx.styles.IStyleClient;
import mx.utils.GraphicsUtil;
import mx.utils.NameUtil;
import mx.core.FlexShape;
import mx.core.IProgrammaticSkin;

use namespace mx_internal;

/**
 *  This class is the base class for skin elements
 *  which draw themselves programmatically.
 */
public class ProgrammaticSkin extends FlexShape
							  implements IFlexDisplayObject, IInvalidating,
							  ILayoutManagerClient, ISimpleStyleClient, IProgrammaticSkin
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Set by horizontalGradientMatrix() or verticalGradientMatrix().
	 */
	private static var tempMatrix:Matrix = new Matrix();
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function ProgrammaticSkin()
	{
		super();
				
		// If nobody explicitly sets a size for this object,
		// then set its width and height to be its measured size.
		_width = measuredWidth;
		_height = measuredHeight;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var invalidateDisplayListFlag:Boolean = false;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  height
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the height property.
	 */
	private var _height:Number;

	/**
	 *  @private
	 */
	override public function get height():Number
	{
		return _height;
	}

	/**
	 *  @private
	 */
	override public function set height(value:Number):void
	{
		_height = value;

		invalidateDisplayList();
	}

	//----------------------------------
	//  width
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the width property.
	 */
	private var _width:Number;
	
	/**
	 *  @private
	 */
	override public function get width():Number
	{
		return _width;
	}

	/**
	 *  @private
	 */
	override public function set width(value:Number):void
	{
		_width = value;

		invalidateDisplayList();
	}

	//--------------------------------------------------------------------------
	//
	//  Properties: IFlexDisplayObject
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  The measured height of this object.
	 *  This should be overridden by subclasses to return the preferred height for
	 *  the skin.
	 *
	 *  @return The measured height of the object, in pixels.
	 */
	public function get measuredHeight():Number
	{
		return 0;
	}

	//----------------------------------
	//  measuredWidth
	//----------------------------------

	/**
	 *  The measured width of this object.
	 *  This should be overridden by subclasses to return the preferred width for
	 *  the skin.
	 *
	 *  @return The measured width of the object, in pixels.
	 */
	public function get measuredWidth():Number
	{
		return 0;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties: ILayoutManagerClient 
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  initialized
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the initialized property.
	 */
	private var _initialized:Boolean = false;

    /**
	 *  @copy mx.core.UIComponent#initialized
     */
    public function get initialized():Boolean
	{
		return _initialized;
	}

    /**
     *  @private
     */
    public function set initialized(value:Boolean):void
	{
		_initialized = value;
	}

    //----------------------------------
    //  nestLevel
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the nestLevel property.
	 */
	private var _nestLevel:int = 0;
    
	/**
     *  @copy mx.core.UIComponent#nestLevel
     */
	public function get nestLevel():int
	{
		return _nestLevel;
	}
	
	/**
     *  @private
     */
	public function set nestLevel(value:int):void
	{
		_nestLevel = value;
		
		// After nestLevel is initialized, add this object to the
		// LayoutManager's queue, so that it is drawn at least once
		invalidateDisplayList();
	}
	
	//----------------------------------
	//  processedDescriptors
	//----------------------------------

    /**
     *  @private
	 *  Storage for the processedDescriptors property.
     */
	private var _processedDescriptors:Boolean = false;

    /**
     *  @copy mx.core.UIComponent#processedDescriptors
     */
    public function get processedDescriptors():Boolean
	{
		return _processedDescriptors;
	}

    /**
     *  @private
     */
    public function set processedDescriptors(value:Boolean):void
	{
		_processedDescriptors = value;
	}

	//----------------------------------
	//  updateCompletePendingFlag
	//----------------------------------

    /**
     *  @private
	 *  Storage for the updateCompletePendingFlag property.
     */
	private var _updateCompletePendingFlag:Boolean = true;

    /**
	 *  A flag that determines if an object has been through all three phases
	 *  of layout validation (provided that any were required).
     */
    public function get updateCompletePendingFlag():Boolean
	{
		return _updateCompletePendingFlag;
	}

    /**
     *  @private
     */
    public function set updateCompletePendingFlag(value:Boolean):void
	{
		_updateCompletePendingFlag = value;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties: ISimpleStyleClient
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  styleName
    //----------------------------------

    /**
     *  @private
     *  Storage for the styleName property.
	 *  For skins, it is always a UIComponent.
     */
    private var _styleName:IStyleClient;

    /**
     *  A parent component used to obtain style values. This is typically set to the
     *  component that created this skin.
     */
    public function get styleName():Object
    {
        return _styleName;
    }

    /**
     *  @private
     */
    public function set styleName(value:Object):void
    {
        if (_styleName != value)
		{
			_styleName = value as IStyleClient;
			invalidateDisplayList();
		}
    }

	//--------------------------------------------------------------------------
	//
	//  Methods: IFlexDisplayObject
	//
	//--------------------------------------------------------------------------

	/**
	 *  Moves this object to the specified x and y coordinates.
	 *
     	 *  @param x The horizontal position, in pixels.
	 *
     	 *  @param y The vertical position, in pixels.
	 */
	public function move(x:Number, y:Number):void
	{
		this.x = x;
		this.y = y;
	}

	/**
	 *  Sets the height and width of this object.
	 *
     	 *  @param newWidth The width, in pixels, of this object.
	 *
     	 *  @param newHeight The height, in pixels, of this object.
	 */
	public function setActualSize(newWidth:Number, newHeight:Number):void
	{
		var changed:Boolean = false;
		
		if (_width != newWidth)
		{
			_width = newWidth;
			changed = true;
		}

		if (_height != newHeight)
		{
			_height = newHeight;
			changed = true;
		}
		
		if (changed)
			invalidateDisplayList();
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: ILayoutManagerClient 
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  This function is an empty stub so that ProgrammaticSkin
	 *  can implement the ILayoutManagerClient  interface.
	 *  Skins do not call <code>LayoutManager.invalidateProperties()</code>, 
	 *  which would normally trigger a call to this method.
	 */
	public function validateProperties():void
	{
	}
	
	/**
	 *  This function is an empty stub so that ProgrammaticSkin
	 *  can implement the ILayoutManagerClient  interface.
	 *  Skins do not call <code>LayoutManager.invalidateSize()</code>, 
	 *  which would normally trigger a call to this method.
         *
     	 *  @param recursive Determines whether children of this skin are validated. 
	 */
	public function validateSize(recursive:Boolean = false):void
	{
	}
	
	/**
	 *  This function is called by the LayoutManager
	 *  when it's time for this control to draw itself.
	 *  The actual drawing happens in the <code>updateDisplayList</code>
	 *  function, which is called by this function.
	 */
	public function validateDisplayList():void
	{
		invalidateDisplayListFlag = false;
		
		updateDisplayList(width, height);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods: ISimpleStyleClient
	//
	//--------------------------------------------------------------------------

	/**
	 *  Whenever any style changes, redraw this skin.
	 *  Subclasses can override this method
	 *  and perform a more specific test before calling invalidateDisplayList().
	 *
	 *  @param styleProp The name of the style property that changed, or null
	 *  if all styles have changed.
	 */
	public function styleChanged(styleProp:String):void
	{
		invalidateDisplayList();
	}
			
	//--------------------------------------------------------------------------
	//
	//  Methods: Other
	//
	//--------------------------------------------------------------------------

	/**
	 *  @copy mx.core.UIComponent#invalidateDisplayList()
	 */
	public function invalidateDisplayList():void
	{
		// Don't try to add the object to the display list queue until we've
		// been assigned a nestLevel, or we'll get added at the wrong place in
		// the LayoutManager's priority queue.
		if (!invalidateDisplayListFlag && nestLevel > 0)
		{
			invalidateDisplayListFlag = true;
			UIComponentGlobals.layoutManager.invalidateDisplayList(this);
		}
	}

	/**
	 *  Programmatically draws the graphics for this skin.
	 *
	 *  <p>Subclasses should override this method and include calls
	 *  to methods such as <code>graphics.moveTo()</code> and
	 *  <code>graphics.lineTo()</code>.</p>
	 *
	 *  <p>This occurs before any scaling from sources
	 *  such as user code or zoom effects. 
	 *  The component is unaware of the scaling that takes place later.</p> 
	 *
         *  @param unscaledWidth
	 *  The width, in pixels, of this object before any scaling.
	 *
         *  @param unscaledHeight
	 *  The height, in pixels, of this object before any scaling.
	 */
	protected function updateDisplayList(unscaledWidth:Number,
									     unscaledHeight:Number):void
	{
	}

	/**
	 *  @inheritDoc
	 */
	public function invalidateSize():void
	{
	}

	/**
	 *  @inheritDoc
	 */
	public function invalidateProperties():void
	{
	}
	
	/**
	 *  Validate and update the properties and layout of this object
	 *  and redraw it, if necessary.
	 */
	public function validateNow():void
	{
		// Since we don't have commit/measure/layout phases,
		// all we need to do here is the draw phase
		if (invalidateDisplayListFlag)
			validateDisplayList();
	}

    /**
     *  Returns the value of the specified style property.
     *
     *  @param styleProp Name of the style property.
     *
     *  @return The style value. This can be any type of object that style properties can be, such as 
     *  int, Number, String, etc.
     */
    public function getStyle(styleProp:String):*
    {
        return _styleName ? _styleName.getStyle(styleProp) : null;
    }
		
	/**
	 *  Utility function to create a horizontal gradient matrix.
	 *
	 *  @param x The left edge of the gradient.
	 *
	 *  @param y The top edge of the gradient.
	 *
	 *  @param width The width of the gradient.
	 *
	 *  @param height The height of the gradient.
	 *
	 *  @return The horizontal gradient matrix. This is a temporary
	 *  object that should only be used for a single subsequent call
	 *  to the <code>drawRoundRect()</code> method.
	 */
	protected function horizontalGradientMatrix(x:Number, y:Number,
												width:Number,
												height:Number):Matrix
	{
		return rotatedGradientMatrix(x, y, width, height, 0);
	}
	
	/**
	 *  Utility function to create a vertical gradient matrix.
	 *
	 *  @param x The left edge of the gradient.
	 *
	 *  @param y The top edge of the gradient.
	 *
	 *  @param width The width of the gradient.
	 *
	 *  @param height The height of the gradient.
	 *
	 *  @return The horizontal gradient matrix. This is a temporary
	 *  object that should only be used for a single subsequent call
	 *  to the <code>drawRoundRect()</code> method.
	 */
	protected function verticalGradientMatrix(x:Number, y:Number,
											  width:Number,
											  height:Number):Matrix
	{
		return rotatedGradientMatrix(x, y, width, height, 90);
	}

	/**
	 *  Utility function to create a rotated gradient matrix.
	 *
	 *  @param x The left edge of the gradient.
	 *
	 *  @param y The top edge of the gradient.
	 *
	 *  @param width The width of the gradient.
	 *
	 *  @param height The height of the gradient.
	 *
	 *  @param rotation The amount to rotate, in degrees.
	 *
	 *  @return The horizontal gradient matrix. This is a temporary
	 *  object that should only be used for a single subsequent call
	 *  to the <code>drawRoundRect()</code> method.
	 */
	protected function rotatedGradientMatrix(x:Number, y:Number,
											 width:Number,
											 height:Number,
											 rotation:Number):Matrix
	{
		tempMatrix.createGradientBox(width, height,
									 rotation * Math.PI / 180, x, y);
		return tempMatrix;
	}

	/**
	 *  Programatically draws a rectangle into this skin's Graphics object.
	 *
	 *  <p>The rectangle can have rounded corners.
	 *  Its edges are stroked with the current line style
	 *  of the Graphics object.
	 *  It can have a solid color fill, a gradient fill, or no fill.
	 *  A solid fill can have an alpha transparency.
	 *  A gradient fill can be linear or radial. You can specify
	 *  up to 15 colors and alpha values at specified points along
	 *  the gradient, and you can specify a rotation angle
	 *  or transformation matrix for the gradient.
	 *  Finally, the rectangle can have a rounded rectangular hole
	 *  carved out of it.</p>
	 *
	 *  <p>This versatile rectangle-drawing routine is used by many skins.
	 *  It calls the <code>drawRect()</code> or
	 *  <code>drawRoundRect()</code>
	 *  methods (in the flash.display.Graphics class) to draw into this
	 *  skin's Graphics object.</p>
	 *
	 *	@param x Horizontal position of upper-left corner
	 *  of rectangle within this skin.
	 *
	 *	@param y Vertical position of upper-left corner
	 *  of rectangle within this skin.
	 *
	 *	@param width Width of rectangle, in pixels.
	 *
	 *	@param height Height of rectangle, in pixels.
	 *
	 *	@param cornerRadius Corner radius/radii of rectangle.
	 *  Can be <code>null</code>, a Number, or an Object.
	 *  If it is <code>null</code>, it specifies that the corners should be square
	 *  rather than rounded.
	 *  If it is a Number, it specifies the same radius, in pixels,
	 *  for all four corners.
	 *  If it is an Object, it should have properties named
	 *  <code>tl</code>, <code>tr</code>, <code>bl</code>, and
	 *  <code>br</code>, whose values are Numbers specifying
	 *  the radius, in pixels, for the top left, top right,
	 *  bottom left, and bottom right corners.
	 *  For example, you can pass a plain Object such as
	 *  <code>{ tl: 5, tr: 5, bl: 0, br: 0 }</code>.
	 *  The default value is null (square corners).
	 *
	 *	@param color The RGB color(s) for the fill.
	 *  Can be <code>null</code>, a uint, or an Array.
	 *  If it is <code>null</code>, the rectangle not filled.
	 *  If it is a uint, it specifies an RGB fill color.
	 *  For example, pass <code>0xFF0000</code> to fill with red.
	 *  If it is an Array, it should contain uints
	 *  specifying the gradient colors.
	 *  For example, pass <code>[ 0xFF0000, 0xFFFF00, 0x0000FF ]</code>
	 *  to fill with a red-to-yellow-to-blue gradient.
	 *  You can specify up to 15 colors in the gradient.
	 *  The default value is null (no fill).
	 *
	 *	@param alpha Alpha value(s) for the fill.
	 *  Can be null, a Number, or an Array.
	 *  This argument is ignored if <code>color</code> is null.
	 *  If <code>color</code> is a uint specifying an RGB fill color,
	 *  then <code>alpha</code> should be a Number specifying
	 *  the transparency of the fill, where 0.0 is completely transparent
	 *  and 1.0 is completely opaque.
	 *  You can also pass null instead of 1.0 in this case
	 *  to specify complete opaqueness.
	 *  If <code>color</code> is an Array specifying gradient colors,
	 *  then <code>alpha</code> should be an Array of Numbers, of the
	 *  same length, that specifies the corresponding alpha values
	 *  for the gradient.
	 *  In this case, the default value is <code>null</code> (completely opaque).
	 *
     *  @param gradientMatrix Matrix object used for the gradient fill. 
     *  The utility methods <code>horizontalGradientMatrix()</code>, 
     *  <code>verticalGradientMatrix()</code>, and
     *  <code>rotatedGradientMatrix()</code> can be used to create the value for 
     *  this parameter.
	 *
	 *	@param gradientType Type of gradient fill. The possible values are
	 *  <code>GradientType.LINEAR</code> or <code>GradientType.RADIAL</code>.
	 *  (The GradientType class is in the package flash.display.)
	 *
	 *	@param gradientRatios (optional default [0,255])
	 *  Specifies the distribution of colors. The number of entries must match
	 *  the number of colors defined in the <code>color</code> parameter.
	 *  Each value defines the percentage of the width where the color is 
	 *  sampled at 100%. The value 0 represents the left-hand position in 
	 *  the gradient box, and 255 represents the right-hand position in the 
	 *  gradient box. 
	 *
	 *	@param hole (optional) A rounded rectangular hole
	 *  that should be carved out of the middle
	 *  of the otherwise solid rounded rectangle
	 *  { x: #, y: #, w: #, h: #, r: # or { br: #, bl: #, tl: #, tr: # } }
	 *
	 *  @see flash.display.Graphics#beginGradientFill()
	 */
	protected function drawRoundRect(
							x:Number, y:Number, width:Number, height:Number,
							cornerRadius:Object = null,
							color:Object = null,
							alpha:Object = null,
							gradientMatrix:Matrix = null,
							gradientType:String = "linear",
							gradientRatios:Array /* of Number */ = null,
							hole:Object = null):void
	{
		var g:Graphics = graphics;

		// Quick exit if weight or height is zero.
		// This happens when scaling a component to a very small value,
		// which then gets rounded to 0.
		if (width == 0 || height == 0)
			return;

		// If color is an object then allow for complex fills.
		if (color !== null)
		{
			if (color is uint)
			{
				g.beginFill(uint(color), Number(alpha));
			}
			else if (color is Array)
			{
				var alphas:Array = alpha is Array ?
								   alpha as Array :
								   [ alpha, alpha ];

				if (!gradientRatios)
					gradientRatios = [ 0, 0xFF ];

				g.beginGradientFill(gradientType,
									color as Array, alphas,
									gradientRatios, gradientMatrix);
			}
		}

		var ellipseSize:Number;

		// Stroke the rectangle.
		if (!cornerRadius)
		{
			g.drawRect(x, y, width, height);
		}
		else if (cornerRadius is Number)
		{
			ellipseSize = Number(cornerRadius) * 2;
			g.drawRoundRect(x, y, width, height, 
							ellipseSize, ellipseSize);
		}
		else
		{
			GraphicsUtil.drawRoundRectComplex(g,
								   x, y, width, height,
								   cornerRadius.tl, cornerRadius.tr,
								   cornerRadius.bl, cornerRadius.br);
		}

		// Carve a rectangular hole out of the middle of the rounded rect.
		if (hole)
		{
			var holeR:Object = hole.r;
			if (holeR is Number)
			{
				ellipseSize = Number(holeR) * 2;
				g.drawRoundRect(hole.x, hole.y, hole.w, hole.h, 
								ellipseSize, ellipseSize);
			}
			else
			{
				GraphicsUtil.drawRoundRectComplex(g,
									   hole.x, hole.y, hole.w, hole.h,
									   holeR.tl, holeR.tr, holeR.bl, holeR.br);
			}	
		}

		if (color !== null)
			g.endFill();
	}		
}

}
