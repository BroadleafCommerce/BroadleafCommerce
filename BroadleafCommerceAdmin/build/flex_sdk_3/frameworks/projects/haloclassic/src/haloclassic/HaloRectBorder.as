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

import flash.display.GradientType;
import flash.display.Graphics;
import flash.filters.DropShadowFilter;
import mx.core.EdgeMetrics;
import mx.core.Container;
import mx.core.IChildList;
import mx.skins.RectangularBorder;
import mx.styles.IStyleClient;

/**
 *  Documentation is not currently available.
 *  @review
 */
public class HaloRectBorder extends RectangularBorder
{
	include "../mx/core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  A look up table for the offsets.
	 */
	private static var BORDER_WIDTHS:Object =
	{
		none: 0,
		solid: 1,
		inset: 2,
		outset: 2, 
		alert: 3,
		dropdown: 2,
		menuBorder: 2,
		comboNonEdit: 2
	};

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function HaloRectBorder() 
	{
		super(); 

		// 'default' is a keyword; setting it this way avoids a compiler error
		BORDER_WIDTHS["default"] = 3;
	}

	//--------------------------------------------------------------------------
	//
	//  Fields
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  A pointer to the object used to cast a drop shadow.
	 *  See the drawDropShadow() method for details.
	 */	 
	private var dropShadowObject:DropShadow; 
	 
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
	 *  Internal object that contains the thickness of each edge
	 *  of the border
	 */
	private var _borderMetrics:EdgeMetrics;

	/**
	 *  @private
	 *  Return the thickness of the border edges.
	 *
	 *  @return Object	top, bottom, left, right thickness in pixels
	 */
	override public function get borderMetrics():EdgeMetrics
	{		
		if (_borderMetrics)
			return _borderMetrics;
			
		var borderThickness:Number;

		// Add support for "custom" style type here when we support it.
		var borderStyle:String = getStyle("borderStyle");

 		if (borderStyle == "default" ||
			borderStyle == "alert")
 		{
 			borderThickness = 0;
 			
 			if (borderStyle == "default" || borderStyle == "alert")
 				borderThickness = getStyle("borderThickness");
 			
			_borderMetrics = new EdgeMetrics(3 + borderThickness,
											 1 + borderThickness, 
											 3 + borderThickness,
											 3 + borderThickness);
 		}		
		
		else if (borderStyle == "controlBar" ||
				 borderStyle == "applicationControlBar")
		{
			_borderMetrics = new EdgeMetrics(1, 1, 1, 1);
		}
		
		else if (borderStyle == "solid")
		{
			borderThickness = getStyle("borderThickness");
			if (isNaN(borderThickness))
				borderThickness = 0;

			_borderMetrics = new EdgeMetrics(borderThickness,
											  borderThickness,
											  borderThickness,
											  borderThickness);
				
			var borderSides:String = getStyle("borderSides");			
			if (borderSides != "left top right bottom")
			{
				// Adjust metrics based on which sides we have.			
				if (borderSides.indexOf("left") == -1)
					_borderMetrics.left = 0;
				
				if (borderSides.indexOf("top") == -1)
					_borderMetrics.top = 0;
				
				if (borderSides.indexOf("right") == -1)
					_borderMetrics.right = 0;
				
				if (borderSides.indexOf("bottom") == -1)
					_borderMetrics.bottom = 0;
			}
		}
		
		else
		{
			borderThickness = BORDER_WIDTHS[borderStyle];
			if (isNaN(borderThickness))
				borderThickness = 0;
		
			_borderMetrics = new EdgeMetrics(borderThickness,
											 borderThickness,
											 borderThickness,
											 borderThickness);
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
	 *  Draw the border, either 3D or 2D or nothing at all.
	 */
	override protected function updateDisplayList(w:Number, h:Number):void
	{	
		if (isNaN(w) || isNaN(h))
			return;
			
		super.updateDisplayList(w, h);

		var borderStyle:String = getStyle("borderStyle");
		
		// Store background color in an object,
		// so that null is distinct from black.
		var backgroundColor:Object = getStyle("backgroundColor");

		// Other styles that we may fetch.
		var backgroundAlpha:Number;
		var borderCapColor:uint;
		var borderColor:uint;
		var borderSides:String;
		var borderThickness:Number;
		var buttonColor:uint;
		var docked:Boolean;
		var dropdownBorderColor:uint;
		var fillColors:Array;
		var footerColors:Array;
		var highlightColor:uint;
		var shadowCapColor:uint;
		var shadowColor:uint;
		var themeColor:uint;
		var translucent:Boolean;
		
		var radius:Number = 0;
		var radiusObj:Object = null;
		var bRoundedCorners:Boolean = false;
		var hole:Object;
				
		var g:Graphics = graphics;
		g.clear();

		if (borderStyle)
		{
			switch (borderStyle)
			{
				case "none":
				{
					break;
				}

				case "inset":
				{
					borderColor = getStyle("borderColor");		
					shadowColor = getStyle("shadowColor");			
					highlightColor = getStyle("highlightColor");
					buttonColor = getStyle("buttonColor");
					borderCapColor = getStyle("borderCapColor");
					shadowCapColor = getStyle("shadowCapColor");

					draw3dBorder(borderCapColor, buttonColor, borderColor,
								 highlightColor, shadowColor, shadowCapColor);
					break;
				}

				case "outset":
				{
					borderColor = getStyle("borderColor");				
					shadowColor = getStyle("shadowColor");
					highlightColor = getStyle("highlightColor");
					buttonColor = getStyle("buttonColor");
					borderCapColor = getStyle("borderCapColor");
					shadowCapColor = getStyle("shadowCapColor");

					draw3dBorder(borderCapColor, borderColor, buttonColor,
								 shadowColor, highlightColor, shadowCapColor);
					break;
				}

				case "alert":
				case "default":
				{
					borderThickness = getStyle("borderThickness");

					radius = getStyle("cornerRadius");
					bRoundedCorners = (getStyle("roundedBottomCorners").
										toString().toLowerCase() == "true");
					var br:Number = bRoundedCorners ? radius : 0;
										
					if (borderThickness > 0)
					{						
						hole = { x: 3 + borderThickness,
								 y: 1 + borderThickness,
								 w: w - (6 + borderThickness * 2),
								 h: h - (4 + borderThickness * 2) };
						
						var holeTopRadius:Number =
							Math.max(0, radius - borderThickness);

						var holeBottomRadius:Number =
							Math.max(0, br - borderThickness);
						
						borderColor = getStyle("borderColor");		

						hole.r = { tl: holeTopRadius, tr: holeTopRadius,
								   bl: holeBottomRadius, br: holeBottomRadius};

						// Fill interior.
						drawRoundRect(
							3, 1, w - 6, h - 4,
							{ tl: radius, tr: radius, br: br, bl: br },
							borderColor, 1, null, null, null, hole); 

						// Use the hole radius for background drawing below.
						radius = holeTopRadius;
					}
					
					drawDropShadow(radius, radius, br, br);
					
					// If we don't have rounded corners we need to initialize
					// to complex radius object so the background fill code
					// below works correctly.
					if (!bRoundedCorners)
						radiusObj = {};
						
					break;
				}

				case "dropDown":
				{
					// The dropdownBorderColor is currently only used
					// when displaying an error state.
					dropdownBorderColor = getStyle("dropdownBorderColor");

					// frame
					drawRoundRect(
						0, 0, w, h,
						{ tl: 4, tr: 0, br: 0, bl: 4 },
						0x4D555E, 1);

					// gradient
					drawRoundRect(
						0, 0, w, h,
						{ tl: 4, tr: 0, br: 0, bl: 4},
						[ 0xFFFFFF, 0xFFFFFF ], [ 0.70, 0 ],
						verticalGradientMatrix(0, 0, w, h)); 
					
					// button top higlight edge
					drawRoundRect(
						1, 1, w - 1, h - 2,
						{ tl: 3, tr: 0, br: 0, bl: 3 },
						0xFFFFFF, 1); 
					
					// button face
					drawRoundRect(
						1, 2, w - 1, h - 3,
						{ tl: 3, tr: 0, br: 0, bl: 3 },
						[ 0xEEEEEE, 0xFFFFFF ], 1,
						verticalGradientMatrix(0, 0, w - 1, h - 3)); 

					if (!isNaN(dropdownBorderColor))
					{
						// combo background in error state
						drawRoundRect(
							0, 0, w + 1, h,
							{ tl: 4, tr: 0, br: 0, bl: 4 },
							dropdownBorderColor, 0.50);
						
						// button top higlight edge
						drawRoundRect(
							1, 1, w - 1, h - 2,
							{ tl: 3, tr: 0, br: 0, bl: 3 },
							0xFFFFFF, 1); 
						
						// button face
						drawRoundRect(
							1, 2, w - 1, h - 3,
							{ tl: 3, tr: 0, br: 0, bl: 3 },
							[ 0xEEEEEE, 0xFFFFFF ], 1,
							verticalGradientMatrix(0, 0, w - 1, h - 3)); 
					}
					
					drawDropShadow(4, 4, 4, 4);
					
					// Make sure the border isn't filled in down below.
					backgroundColor = null;

					break;
				}

				case "menuBorder":
				{
					themeColor = getStyle("themeColor");
					translucent = getStyle("translucent");
					
					// dropShadow
					drawRoundRect(
						4, 4, w - 2, h - 3, 0,
						[ 0x5E5E5E, 0x5E5E5E ], 0.10,
						horizontalGradientMatrix(0, 0, w, h),
						GradientType.RADIAL); 
					
					// dropShadow
					drawRoundRect(4, 4, w - 1, h - 2, 0,
								  0x5E5E5E, 0.10); 
					
					if (translucent)
					{
						drawRoundRect(
							0, 0, w, h, 0,
							[ 0xFFFFFF, 0xFFFFFF ], 1,
							rotatedGradientMatrix(0, 0, w, h, 250),
							GradientType.LINEAR); 
					}
					else
					{
						// base color
						drawRoundRect(
							0, 0, w, h, 0,
							[ 0xAAAAAA, 0xDADADA ], 1,
							rotatedGradientMatrix(0, 0, w, h, 250),
							GradientType.LINEAR); 
						
						// themeColor
						drawRoundRect(
							0, 0, w, h, 0,
							themeColor, 0.50); 
						
						drawRoundRect(
							2, 2, w - 4, h - 4, 0,
							0xFFFFFF, 1);
					}
					
					break;
				}
				
				case "comboNonEdit":
				{
					break;
				}
				
				case "controlBar":
				{
					if (w == 0 || h == 0)
					{
						// If the width or height is 0, don't draw anything.
						backgroundColor = null;
						break;
					}

					footerColors = getStyle("footerColors");
						
					// Top
					g.lineStyle(0, footerColors.length > 0 ?
								footerColors[1] : footerColors[0], 100);
					g.moveTo(0, 0);
					g.lineTo(w, 0);
					g.lineStyle(0, 0, 0);

					// If backgroundColor isn't set, we are drawing a
					// "floating" border, which is empty for ControlBar.
					if (backgroundColor === null || backgroundColor == "")
						break;

					// cornerRadius is defined on our parent container.
					radius = IStyleClient(parent.parent).getStyle("cornerRadius");
					if (isNaN(radius))
						radius = 0;
					
					// If our parent has square bottom corners,
					// use square corners.
					if (IStyleClient(parent.parent).
						getStyle("roundedBottomCorners").toString().toLowerCase()
						!= "true")
					{
						radius = 0;
					}
					if (radius > 0)
						radius--;

					drawRoundRect(
						0, 1, w, h - 1,
						{ bl:radius, br: radius, tl: 0, tr: 0 },
						footerColors, 1,
						verticalGradientMatrix(0, 0, w, h));
					
					if (footerColors.length > 1 && footerColors[0] != footerColors[1])
					{
						drawRoundRect(
							0, 1, w, h - 1,
							{ bl: radius, br: radius, tl: 0, tr: 0 },
							[ 0xFFFFFF, 0xFFFFFF ], [ 0.20, 0.80 ],
							verticalGradientMatrix(0, 0, w, h));
						
						drawRoundRect(
							1, 2, w - 2, h - 3,
							{ bl: radius - 1, br: radius - 1, tl: 0, tr: 0 },
							footerColors, 1,
							verticalGradientMatrix(0, 0, w, h));
					}
					
					// Don't draw the background color below.
					// We've already handled it here.
					backgroundColor = null;
					break;
				}
				
				case "applicationControlBar":
				{
					fillColors = getStyle("fillColors");
					backgroundAlpha = getStyle("backgroundAlpha");
					docked = getStyle("docked");

					// background color of the bar
					var backgroundColorNum:uint = uint(backgroundColor);

					radius = getStyle("cornerRadius");
					if (!radius)
						radius = 0;

					if (backgroundColor)
					{
						drawRoundRect(
							0, 1, w, h - 1, radius,
							backgroundColorNum, backgroundAlpha,
							verticalGradientMatrix(0, 0, w, h));
					}

					drawRoundRect(
						0, 1, w, h - 1, radius,
						[ fillColors[0], fillColors[0],
						  fillColors[0], fillColors[0] ],
						[ 0.85, 0.90, 0.80, 0.70 ],
						verticalGradientMatrix(0, 0, w, h),
						GradientType.LINEAR,
						[ 0, 0x5F, 0x7F, 0xFF ],
						{ x: 1, y: 2, w: w - 2, h: h - 3, r: radius });

					drawRoundRect(
						1, 2, w - 2, h - 3, radius,
						[ fillColors[1], fillColors[1],
						  fillColors[1], fillColors[1], fillColors[1] ],
						[ 0.70, 0.45, 0.30, 0.40, 0.60 ],
						verticalGradientMatrix(0, 0, w, h),
						GradientType.LINEAR,
						[ 0, 0x5F, 0x7F, 0xBF, 0xFF ]);

					drawDropShadow(radius, radius, radius, radius);
					
					// Don't draw the background color below.
					// We've already handled it here.
					backgroundColor = null;

					break;
				}

				default: // ((borderStyle == "solid") || (borderStyle == null))
				{
					borderColor = getStyle("borderColor");		
					borderThickness = getStyle("borderThickness");
					borderSides = getStyle("borderSides");
					var bHasAllSides:Boolean = true;
					radius = getStyle("cornerRadius");
					
					// Make sure bottom corners are rounded too.
					bRoundedCorners = true;
					
					var holeRadius:Number =
						Math.max(radius - borderThickness, 0);
					
					hole = { x: borderThickness,
							 y: borderThickness,
							 w: w - borderThickness * 2,
							 h: h - borderThickness * 2,
							 r: holeRadius };

					if (borderSides != "left top right bottom")
					{
						// Convert the radius values from a scalar to an object
						// because we need to adjust individual radius values
						// if we are missing any sides.
						hole.r = { tl: holeRadius,
								   tr: holeRadius,
								   bl: holeRadius,
								   br: holeRadius };
						
						radiusObj = { tl: radius,
									  tr: radius,
									  bl: radius,
									  br: radius };
						
						borderSides = borderSides.toLowerCase();
						
						if (borderSides.indexOf("left") == -1)
						{
							hole.x = 0;
							hole.w += borderThickness;
							hole.r.tl = 0;
							hole.r.bl = 0;
							radiusObj.tl = 0;
							radiusObj.bl = 0;
							bHasAllSides = false;
						}
						
						if (borderSides.indexOf("top") == -1)
						{
							hole.y = 0;
							hole.h += borderThickness;
							hole.r.tl = 0;
							hole.r.tr = 0;
							radiusObj.tl = 0;
							radiusObj.tr = 0;
							bHasAllSides = false;
						}
						
						if (borderSides.indexOf("right") == -1)
						{
							hole.w += borderThickness;
							hole.r.tr = 0;
							hole.r.br = 0;
							radiusObj.tr = 0;
							radiusObj.br = 0;
							bHasAllSides = false;
						}
						
						if (borderSides.indexOf("bottom") == -1)
						{
							hole.h += borderThickness;
							hole.r.bl = 0;
							hole.r.br = 0;
							radiusObj.bl = 0;
							radiusObj.br = 0;
							bHasAllSides = false;
						}
					}

					if (radius == 0 && bHasAllSides)
					{
						g.beginFill(borderColor);
						g.drawRect(0, 0, w, h);
						g.drawRect(borderThickness, borderThickness,
								   w - 2 * borderThickness,
								   h - 2 * borderThickness);
						g.endFill();
						
						drawDropShadow(0, 0, 0, 0);
					}
					else if (radiusObj)
					{
						drawRoundRect(
							0, 0, w, h, radiusObj,
							borderColor, 1,
							null, null, null, hole);
						
						drawDropShadow(radiusObj.tl, radiusObj.tr,
									   radiusObj.br, radiusObj.bl);
						
						// Reset radius here so background drawing
						// below is correct.
						radiusObj.tl = Math.max(radius - borderThickness, 0);
						radiusObj.tr = Math.max(radius - borderThickness, 0);
						radiusObj.bl = Math.max(radius - borderThickness, 0);
						radiusObj.br = Math.max(radius - borderThickness, 0);
					}
					else
					{
						drawRoundRect(
							0, 0, w, h, radius,
							borderColor, 1,
							null, null, null, hole);
						
						drawDropShadow(radius, radius, radius, radius);
						
						// Reset radius here so background drawing 
						// below is correct.
						radius = Math.max(getStyle("cornerRadius") -
								 borderThickness, 0);
					}									
				}
			} // switch
		}
				
		// The behavior used to be that we always create a background
		// regardless of whether we have a background color or not.
		// Now we only create a background if we have a color or if
		// the mouseShield or mouseShieldChildren styles are true.
		// Look at Container.addEventListener() and Container.isBorderNeeded()
		// for the mouseShield logic. JCS 6/24/05
		if ((backgroundColor !== null &&
		     backgroundColor !== "") ||
			getStyle("mouseShield") ||
			getStyle("mouseShieldChildren"))
		{
			var nd:Number = Number(backgroundColor);
			var alpha:Number = 1.0;
			var bm:EdgeMetrics = borderMetrics;
			
			if (isNaN(nd) || backgroundColor === "" || backgroundColor === null)
			{
				alpha = 0;
				nd = 0xFFFFFF;
			}
			else
			{
				alpha = getStyle("backgroundAlpha");
			}

			// If we have a non-zero radius, use drawRoundRect()
			// to fill in the background.
			if (radius != 0)
			{			
				var bottom:Number = bm.bottom;
				
				if (radiusObj)
				{
					var bottomRadius:Number = bRoundedCorners ? radius + 1 : 0;
					
					radiusObj = { tl: radius + 1,
							      tr: radius + 1,
							      bl: bottomRadius,
							      br: bottomRadius };

					drawRoundRect(
						bm.left, bm.top,
						width - (bm.left + bm.right),
						height - (bm.top + bottom), 
						radiusObj, nd, alpha);
				}
				else
				{
					drawRoundRect(
						bm.left, bm.top,
						width - (bm.left + bm.right),
						height - (bm.top + bottom), 
						radius, nd, alpha);
				}
			}
			else
			{
				g.beginFill(nd, alpha);
				g.drawRect(bm.left, bm.top,
						   w - bm.right - bm.left,
						   h - bm.bottom - bm.top);
				g.endFill();
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
			styleProp == "styleName" ||
			styleProp == "borderThickness" ||
			styleProp == "borderSides")
		{
			_borderMetrics = null;
		}
		
		invalidateDisplayList();
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Draw a 3D border.
	 */
	private function draw3dBorder(c1:Number, c2:Number, c3:Number,
								  c4:Number, c5:Number, c6:Number):void
	{
		var w:Number = width;
		var h:Number = height;
		
		/*
		// temp color override to verify layout of lines
		var c1:Number = 0x919999;
		var c2:Number = 0x6F7777;
		var c3:Number = 0xD5DDDD;
		var c4:Number = 0xC4CCCC;
		var c5:Number = 0xEEEEEE;
		var c6:Number = 0xD5DDDD;
		*/
		
		var g:Graphics = graphics;
		
		// outside sides
		g.beginFill(c1);
		g.drawRect(0, 0, w, h);
		g.drawRect(1, 0, w - 2, h);
		g.endFill();
		
		// outside top
		g.beginFill(c2);
		g.drawRect(1, 0, w - 2, 1);
		g.endFill();
		
		// outside bottom
		g.beginFill(c3);
		g.drawRect(1, h - 1, w - 2, 1);
		g.endFill();
		
		// inside top
		g.beginFill(c4);
		g.drawRect(1, 1, w - 2, 1);
		g.endFill();
		
		// inside bottom
		g.beginFill(c5);
		g.drawRect(1, h - 2, w - 2, 1);
		g.endFill();
		
		// inside sides
		g.beginFill(c6);
		g.drawRect(1, 2, w - 2, h - 4);
		g.drawRect(2, 2, w - 4, h - 4);
		g.endFill();
		
		drawDropShadow(0, 0, 0, 0);
	}

	/**
	 *  @private
	 *  Apply a drop shadow using a bitmap filter.
	 *
	 *  Bitmap filters are slow, and their slowness is
	 *  proportional to the number of pixels being filtered.
	 *  If I'm a large, opaque HaloRectBorder, then it's wasteful
	 *  to create a big shadow behind me - the user can only see
	 *  the piece of the shadow that's visible around the edges.
	 *
	 *  If I'm an opaque HaloRectBorder, then I'll create a separate
	 *  object to cast my shadow. This separate object will look like
	 *  the same as me, except that it is hollow in the middle
	 *  (so no shadow is cast).
	 *
	 *  This other object will be a peer of me.
	 *  It can't be a child because I'm a Shape,
	 *  and Shapes don't have children.	 	 
	 */
	private function drawDropShadow(tlRadius:Number, trRadius:Number, 
									brRadius:Number, blRadius:Number):void
	{
		var childCollection:IChildList = parent is Container ?
											Container(parent).rawChildren :
											IChildList(parent);	
	
		// Do I need a drop shadow in the first place?  If not, destroy
		// any drop shadow objects that I've created in the past, and 
		// clear any drop shadow filters that I've applied to myself
		if (getStyle("dropShadowEnabled") == false || 
			width == 0 || 
			height == 0)
		{
			if (dropShadowObject)
			{
				childCollection.removeChild(dropShadowObject);
				dropShadowObject = null;
			}

			filters = [];
			
			return;
		}

		// Calculate the angle and distance for the shadow
		var distance:Number = getStyle("shadowDistance");
		var direction:String = getStyle("shadowDirection");
		var color:uint = getStyle("dropShadowColor");
		var angle:Number;		
		if (getStyle("borderStyle") == "applicationControlBar")
		{
			var docked:Boolean = getStyle("docked");
			angle = docked ? 90 : getDropShadowAngle(distance, direction);
			distance = Math.abs(distance);
		}
		else
		{
			angle = getDropShadowAngle(distance, direction);
			distance = Math.abs(distance) + 2;
		}
		
		// Am I translucent? If so, then I need to create the whole drop 
		// shadow - not just the pieces around the edges. Therefore, I can't
		// use the dropShadowObject optimization. Instead, I will apply the
		// drop shadow filter to myself
		if (getStyle("backgroundAlpha") != 1.0
				|| (getStyle("backgroundColor") === null))
		{
			// If I'd previously created a dropShadowObject, get rid of it
			if (dropShadowObject)
			{
				childCollection.removeChild(dropShadowObject);
				dropShadowObject = null;
			}			
			
			setFiltersProperty(distance, angle, color);
			return;
		}

		// At this point, we know that I am an opaque HaloRectBorder.
		// If I haven't already done so, I should create a separate
		// dropShadowObject to cast my shadow.  
		if (!dropShadowObject)
		{
			dropShadowObject = new DropShadow();
			childCollection.addChildAt(dropShadowObject, 0);					

			// If I'd previously applied a filter to myself, remove it
			filters = [];
		}

		dropShadowObject.setShadowProperties(distance, angle, color);

		dropShadowObject.updateDisplayList(width, height, 
			tlRadius, trRadius, brRadius, blRadius, _borderMetrics);
	}

	/**
	 *  @private
	 *  Convert the value of the shadowDirection property into a shadow
	 *  angle.
	 */
	private function getDropShadowAngle(distance:Number,
										direction:String):Number
	{
		var angle:Number;

		if (direction == "left")
		{
			if (distance >= 0)
				angle = 135;
			else
				angle = 225;
		}
		else if (direction == "right")
		{
			if (distance >= 0)
				angle = 45;
			else
				angle = 315;
		}
		else // Default: direction == "center"
		{
			if (distance >= 0)
				angle = 90;
			else
				angle = 270;
		}

		return angle;
	}

	/**
	 *  @private
	 *  Apply a DropShadow filter to the target object.
	 *  If I'm translucent,then the target object will be myself.
	 *  Otherwise, it'll be the dropShadowObject Sprite.  
	 */
	private function setFiltersProperty(distance:Number,
										angle:Number,
										color:uint):void
	{
		var f:Array = filters;

		// If we already have a drop shadow and it's parameters are correct,
		// then keep it.
		if (f && f[0] is DropShadowFilter)
		{
			var dropShadowFilter:DropShadowFilter = DropShadowFilter(f[0]);			
			if (dropShadowFilter.distance == distance &&
				dropShadowFilter.angle == angle &&
				dropShadowFilter.color == color)
			{
				return;
			}
		}

		filters = [ new DropShadowFilter(distance, angle, color, 0.4) ];
	}
}

}

////////////////////////////////////////////////////////////////////////////////

import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.filters.DropShadowFilter;
import flash.geom.Rectangle;
import mx.core.EdgeMetrics;

/**
 *  @private
 */
class DropShadow extends Sprite
{
	/**
	 *  Constructor
	 */
	public function DropShadow()
	{
		super();
	}

	private var shadowDistance:Number;
	private var shadowAngle:Number;
	private var shadowColor:uint;

	private var tlCorner:Shape;
	private var trCorner:Shape;
	private var brCorner:Shape;
	private var blCorner:Shape;

	private var leftSide:Shape;
	private var rightSide:Shape;
	private var topSide:Shape;
	private var bottomSide:Shape;

	/**
	 *  @private
	 */
	public function setShadowProperties(distance:Number, angle:Number,
										color:uint):void
	{
		if (distance != shadowDistance ||
			angle != shadowAngle ||
			color != shadowColor)
		{
			shadowDistance = distance;
			shadowAngle = angle;
			shadowColor = color;

			// Update all children that already exist
			var n:int = numChildren;
			for (var i:int = 0; i < n; i++)
			{
				getChildAt(i).filters =
					[ new DropShadowFilter(distance, angle, color, 0.4) ];
			}
		}
	}

	/**
	 *  @private
	 */
	public function updateDisplayList(width:Number, height:Number,
									  tlRadius:Number, trRadius:Number,
									  brRadius:Number, blRadius:Number,
									  borderMetrics:EdgeMetrics):void
	{
		var left:Number;
		var right:Number;
		var bottom:Number;
		var top:Number;

		// Create a Shape for the top left corner
		tlCorner = setCorner(tlCorner, borderMetrics.left,
							 borderMetrics.top,	tlRadius,
							 tlRadius, tlRadius);

		// Create a Shape for the top right corner
		trCorner = setCorner(trCorner,
							 width - borderMetrics.right - trRadius,
							 borderMetrics.top,
							 0, trRadius, trRadius);

		// Create a Shape for the bottom right corner
		brCorner = setCorner(brCorner,
							 width - borderMetrics.right - brRadius,
							 height - borderMetrics.bottom - brRadius,
							 0, 0, brRadius);

		// Create a Shape for the bottom right corner
		blCorner = setCorner(blCorner,
							 borderMetrics.left,
							 height - borderMetrics.bottom - blRadius,
							 blRadius, 0, blRadius);

		// Create a Shape for the left side
		if (shadowAngle <= 90 || shadowAngle >= 270)
		{
			top = borderMetrics.top + (tlCorner ? tlRadius : 0);
			bottom = height - borderMetrics.bottom - (blCorner ? blRadius : 0);
			leftSide = setSide(leftSide, borderMetrics.left, top,
							   shadowDistance, bottom - top);
		}
		else if (leftSide)
		{
			removeChild(leftSide);
			leftSide = null;
		}

		// Create a Shape for the right side
		if (shadowAngle >= 90 && shadowAngle <= 270)
		{
			top = borderMetrics.top + (trCorner ? trRadius : 0);
			bottom = height - borderMetrics.bottom - (brCorner ? brRadius : 0);
			rightSide = setSide(rightSide,
								width - borderMetrics.right - shadowDistance,
								top, shadowDistance, bottom - top);
		}
		else if (rightSide)
		{
			removeChild(rightSide);
			rightSide = null;
		}

		// Create a Shape for the top side
		if (shadowAngle >= 180 && shadowAngle <= 360)
		{
			left = borderMetrics.left;
			if (tlCorner)
				left += tlRadius;
			else if (leftSide)
				left += shadowDistance;

			right = width - borderMetrics.right;
			if (trCorner)
				right -= trRadius;
			else if (rightSide)
				right -= shadowDistance;

			topSide = setSide(topSide, left, borderMetrics.top,
							  right - left, shadowDistance);
		}
		else if (topSide)
		{
			removeChild(topSide);
			topSide = null;
		}

		// Create a Shape for the bottom side
		if (shadowAngle >= 0 && shadowAngle <= 180)
		{
			left = borderMetrics.left;
			if (blCorner)
				left += blRadius;
			else if (leftSide)
				left += shadowDistance;

			right = width - borderMetrics.right;
			if (brCorner)
				right -= brRadius;
			else if (rightSide)
				right -= shadowDistance;

			bottomSide = setSide(bottomSide, left,
								 height - borderMetrics.bottom - shadowDistance,
								 right - left, shadowDistance);
		}
		else if (bottomSide)
		{
			removeChild(bottomSide);
			bottomSide = null;
		}
	}

	/**
	 *  @private
	 */
	private function setCorner(cornerShape:Shape,
							   cornerX:Number, cornerY:Number,
							   circleX:Number, circleY:Number,
							   circleRadius:Number):Shape
	{
		if (circleRadius <= shadowDistance)
		{
			if (cornerShape)
				removeChild(cornerShape);

			return null;
		}

		if (!cornerShape)
		{
			cornerShape = Shape(addChild(new Shape()));
			cornerShape.name = "cornerShape";
			var dropShadowFilder:DropShadowFilter = new DropShadowFilter(
				shadowDistance, shadowAngle, shadowColor, 0.4);
			cornerShape.filters = [ dropShadowFilder ];
		}

		cornerShape.scrollRect =
			new Rectangle(0, 0, circleRadius, circleRadius);
		cornerShape.x = cornerX;
		cornerShape.y = cornerY;

		var g:Graphics = cornerShape.graphics;
		g.clear();
		g.beginFill(0xFFFFFF);
		g.drawCircle(circleX, circleY, circleRadius);
		g.endFill();

		return cornerShape;
	}

	/**
	 *  @private
	 */
	private function setSide(sideShape:Shape, x:Number, y:Number,
							 width:Number, height:Number):Shape
	{
		if (!sideShape)
		{
			sideShape = Shape(addChild(new Shape()));
			sideShape.name = "sideShape";
			var dropShadowFilter:DropShadowFilter = new DropShadowFilter(
				shadowDistance, shadowAngle, shadowColor, 0.4);
			sideShape.filters = [ dropShadowFilter ];
		}

		sideShape.x = x;
		sideShape.y = y;

		var g:Graphics = sideShape.graphics;
		g.clear();
		g.beginFill(0xFFFFFF);
		g.drawRect(0, 0, width, height);
		g.endFill();

		return sideShape;
	}
}

