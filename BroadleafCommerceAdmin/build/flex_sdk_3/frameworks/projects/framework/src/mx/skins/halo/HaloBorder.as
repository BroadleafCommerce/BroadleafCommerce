////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.halo
{

import flash.display.GradientType;
import flash.display.Graphics;
import mx.core.IContainer;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.graphics.RectangularDropShadow;
import mx.skins.RectangularBorder;
import mx.styles.IStyleClient;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;
import mx.graphics.GradientEntry;

use namespace mx_internal;

/**
 *  Defines the appearance of the default border for the Halo theme.
 */
public class HaloBorder extends RectangularBorder
{
	include "../../core/Version.as";

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
		menuBorder: 1,
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
	public function HaloBorder() 
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
	 *  A reference to the object used to cast a drop shadow.
	 *  See the drawDropShadow() method for details.
	 */	 
	private var dropShadow:RectangularDropShadow;
	
	mx_internal var backgroundColor:Object;	
	mx_internal var backgroundAlphaName:String; 
	mx_internal var backgroundHole:Object;
	mx_internal var bRoundedCorners:Boolean;
	mx_internal var radius:Number;
	mx_internal var radiusObj:Object;
	
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
	protected var _borderMetrics:EdgeMetrics;

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
 			if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
	        	_borderMetrics = new EdgeMetrics(0, 0, 0, 0);
	        else
	        	return EdgeMetrics.EMPTY;
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
				// Adjust metrics based on which sides we have				
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
	 *  If borderStyle may have changed, clear the cached border metrics.
	 */
	override public function styleChanged(styleProp:String):void
	{
		if (styleProp == null ||
			styleProp == "styleName" ||
			styleProp == "borderStyle" ||
			styleProp == "borderThickness" ||
			styleProp == "borderSides")
		{
			_borderMetrics = null;
		}
		
		invalidateDisplayList();
	}

	/**
	 *  @private
	 *  Draw the border, either 3D or 2D or nothing at all.
	 */
	override protected function updateDisplayList(w:Number, h:Number):void
	{	
		if (isNaN(w) || isNaN(h))
			return;
			
		super.updateDisplayList(w, h);

		// Store background color in an object,
		// so that null is distinct from black.
		backgroundColor = getBackgroundColor();
		bRoundedCorners = false;	
		backgroundAlphaName = "backgroundAlpha";
		backgroundHole = null;
		radius = 0;
		radiusObj = null;

		drawBorder(w,h);		
		drawBackground(w,h);	
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	mx_internal function drawBorder(w:Number, h:Number):void
	{
		var borderStyle:String = getStyle("borderStyle");
				
		// Other styles that we may fetch.
		var highlightAlphas:Array = getStyle("highlightAlphas");	
		
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
		
		var hole:Object;
		var drawTopHighlight:Boolean = false;
		
		var borderColorDrk1:Number
		var borderColorDrk2:Number
		var borderColorLt1:Number

		var borderInnerColor:Object;

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

				case "inset": // used for text input & numeric stepper
				{
					borderColor = getStyle("borderColor");		
					borderColorDrk1 =
						ColorUtil.adjustBrightness2(borderColor, -40);
					borderColorDrk2 =
						ColorUtil.adjustBrightness2(borderColor, +25);
					borderColorLt1 = 
						ColorUtil.adjustBrightness2(borderColor, +40);
						
					borderInnerColor = backgroundColor;
					if (borderInnerColor === null ||
						borderInnerColor === "")
					{
						borderInnerColor = borderColor;
					}
					
					draw3dBorder(borderColorDrk2, borderColorDrk1, borderColorLt1,
								 Number(borderInnerColor), 
								 Number(borderInnerColor), 
								 Number(borderInnerColor));
					break;
				}

				case "outset":
				{
					borderColor = getStyle("borderColor");		
					borderColorDrk1 =
						ColorUtil.adjustBrightness2(borderColor, -40);
					borderColorDrk2 =
						ColorUtil.adjustBrightness2(borderColor, -25);
					borderColorLt1 = 
						ColorUtil.adjustBrightness2(borderColor, +40);
					
					borderInnerColor = backgroundColor;
					if (borderInnerColor === null ||
						borderInnerColor === "")
					{
						borderInnerColor = borderColor;
					}
					
					draw3dBorder(borderColorDrk2, borderColorLt1, borderColorDrk1,
								 Number(borderInnerColor), 
								 Number(borderInnerColor), 
								 Number(borderInnerColor));
					break;
				}

				case "alert":
				case "default":
				{
					if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
					{
						// For Panel/Alert, "borderAlpha" is the alpha for the
						// title/control/gutter area and "backgroundAlpha"
						// is the alpha for the content area.
						// We flip-flop the variables here so the "borderAlpha"
						// is applied by the background drawing code at the bottom.
						var contentAlpha:Number = getStyle("backgroundAlpha");
						backgroundAlpha = getStyle("borderAlpha");
						backgroundAlphaName = "borderAlpha";
						
						radius = getStyle("cornerRadius");
						bRoundedCorners =
							getStyle("roundedBottomCorners").toString().toLowerCase() == "true";
						var br:Number = bRoundedCorners ? radius : 0;
		
						drawDropShadow(0, 0, w, h, radius, radius, br, br);
						
						// If we don't have rounded corners we need to initialize
						// the complex radius object so the background fill code
						// below works correctly.
						if (!bRoundedCorners)
							radiusObj = {};
						
						var parentContainer:IContainer = parent as IContainer;
		
						if (parentContainer)
						{
							var vm:EdgeMetrics = parentContainer.viewMetrics;
		
							// The backgroundHole is the content area
							backgroundHole = {x:vm.left, y:vm.top, 
											  w: Math.max(0, w - vm.left - vm.right), 
											  h: Math.max(0, h - vm.top - vm.bottom),
											  r:0};
		
							if (backgroundHole.w > 0 && backgroundHole.h > 0)
							{
								// Draw a shadow around the content
								// if the content and panel alpha are different.
								// This could be a style property if needed
								if (contentAlpha != backgroundAlpha)
								{
									drawDropShadow(backgroundHole.x, backgroundHole.y,
											backgroundHole.w, backgroundHole.h,
											0, 0, 0, 0);
								}
		
								// Fill in the content area
								g.beginFill(Number(backgroundColor), contentAlpha);
								g.drawRect(backgroundHole.x, backgroundHole.y, 
										backgroundHole.w, backgroundHole.h);
								g.endFill();
							}
						}
		
						// When the content and panel alpha are different, the border
						// of the panel is drawn using borderColor. We've already
						// drawn the content background so we set backgroundColor to
						// borderColor here so the drawing code below is done with the
						// border color.					
						backgroundColor = getStyle("borderColor");
					}
										
					break;
				}

				case "dropdown": // never used
				{
					// The dropdownBorderColor is currently only used
					// when displaying an error state.
					dropdownBorderColor = getStyle("dropdownBorderColor");

					drawDropShadow(0, 0, w, h, 4, 0, 0, 4);

					// frame
					drawRoundRect(
						0, 0, w, h,
						{ tl: 4, tr: 0, br: 0, bl: 4 },
						0x4D555E, 1);

					// gradient
					drawRoundRect(
						0, 0, w, h,
						{ tl: 4, tr: 0, br: 0, bl: 4},
						[ 0xFFFFFF, 0xFFFFFF ], [ 0.7, 0 ],
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
							dropdownBorderColor, 0.5);
						
						// button top higlight edge
						drawRoundRect(
							1, 1, w - 1, h - 2,
							{ tl: 3, tr: 0, br: 0, bl: 3 },
							0xFFFFFF, 1); 
						
						//button face
						drawRoundRect(
							1, 2, w - 1, h - 3,
							{ tl: 3, tr: 0, br: 0, bl: 3 },
							[ 0xEEEEEE, 0xFFFFFF ], 1,
							verticalGradientMatrix(0, 0, w - 1, h - 3)); 
					}
					
					// Make sure the border isn't filled in down below.
					backgroundColor = null;
					
					break;
				}

				case "menuBorder":
				{
					borderColor = getStyle("borderColor");
					drawRoundRect(
						0, 0, w, h, 0,
						borderColor, 1);
					drawDropShadow(1, 1, w - 2, h - 2, 0, 0, 0, 0);
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
					var showChrome:Boolean = footerColors != null;
					var borderAlpha:Number = getStyle("borderAlpha");
						
					if (showChrome)
					{
						g.lineStyle(0, footerColors.length > 0 ?
									footerColors[1] : footerColors[0], borderAlpha);
						g.moveTo(0, 0);
						g.lineTo(w, 0);
						g.lineStyle(0, 0, 0);

						// cornerRadius is defined on our parent container. Reach up
						// and grab it. Yes, this is cheating...
						if (parent && parent.parent && parent.parent is IStyleClient)
						{
							radius =
								IStyleClient(parent.parent).getStyle("cornerRadius"); 
							borderAlpha = 
								IStyleClient(parent.parent).getStyle("borderAlpha"); 
						}
						
						if (isNaN(radius))
							radius = 0;

						// If our parent has square bottom corners,
						// use square corners.
						if (IStyleClient(parent.parent).
							getStyle("roundedBottomCorners").toString().toLowerCase() != "true")
						{
							radius = 0;
						}
						
						drawRoundRect(
							0, 1, w, h - 1,
							{ tl: 0, tr: 0, bl:radius, br: radius },
							footerColors, borderAlpha,
							verticalGradientMatrix(0, 0, w, h));

						if (footerColors.length > 1 &&
							footerColors[0] != footerColors[1])
						{
							drawRoundRect(
								0, 1, w, h - 1,
								{ tl: 0, tr: 0, bl: radius, br: radius },
								[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
								verticalGradientMatrix(0, 0, w, h));

							drawRoundRect(
								1, 2, w - 2, h - 3,
								{ tl: 0, tr: 0, bl: radius - 1, br: radius - 1 },
								footerColors, borderAlpha,
								verticalGradientMatrix(0, 0, w, h));
						}
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
					highlightAlphas = getStyle("highlightAlphas");
					var fillAlphas:Array = getStyle("fillAlphas");
					docked = getStyle("docked");

					// background color of the bar
					var backgroundColorNum:uint = uint(backgroundColor);

					radius = getStyle("cornerRadius");
					if (!radius)
						radius = 0;

					drawDropShadow(0, 1, w, h - 1,
								   radius, radius, radius, radius);

					if (backgroundColor !== null && StyleManager.isValidStyleValue(backgroundColor))
					{
						drawRoundRect(
							0, 1, w, h - 1, radius,
							backgroundColorNum, backgroundAlpha,
							verticalGradientMatrix(0, 0, w, h));
					}

					// surface
					drawRoundRect(
						0, 1, w, h - 1, radius,
						fillColors, fillAlphas,
						verticalGradientMatrix(0, 0, w, h));
					
					// highlight
					drawRoundRect(
						0, 1, w, (h / 2) - 1,
						{ tl: radius, tr: radius, bl: 0, br: 0 },
						[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
						verticalGradientMatrix(0, 0, w, h / 2 - 1));

					// edge
					drawRoundRect(
						0, 1, w, h - 1,
						{ tl: radius, tr: radius, bl: 0, br: 0 },
						0xFFFFFF, 0.3, null,
						GradientType.LINEAR, null, 
						{ x: 0, y: 2, w: w, h: h - 2,
						  r: { tl: radius, tr: radius, bl: 0, br: 0 } });

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
					
					bRoundedCorners =
						getStyle("roundedBottomCorners").toString().toLowerCase() == "true";
					
					var holeRadius:Number =
						Math.max(radius - borderThickness, 0);
						
					hole = { x: borderThickness,
							 y: borderThickness,
							 w: w - borderThickness * 2,
							 h: h - borderThickness * 2,
							 r: holeRadius };
					
					if (!bRoundedCorners)
					{
						radiusObj = {tl:radius, tr:radius, bl:0, br:0};
						hole.r = {tl:holeRadius, tr:holeRadius, bl:0, br:0};
					}
					
					if (borderSides != "left top right bottom")
					{
						// Convert the radius values from a scalar to an object
						// because we need to adjust individual radius values
						// if we are missing any sides.
						hole.r = { tl: holeRadius,
								   tr: holeRadius,
								   bl: bRoundedCorners ? holeRadius : 0,
								   br: bRoundedCorners ? holeRadius : 0 };
						
						radiusObj = { tl: radius,
									  tr: radius,
									  bl: bRoundedCorners ? radius : 0,
									  br: bRoundedCorners ? radius : 0};
						
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
						drawDropShadow(0, 0, w, h, 0, 0, 0, 0);
					
						g.beginFill(borderColor);
						g.drawRect(0, 0, w, h);
						g.drawRect(borderThickness, borderThickness,
								   w - 2 * borderThickness,
								   h - 2 * borderThickness);
						g.endFill();
					}
					else if (radiusObj)
					{
						drawDropShadow(0, 0, w, h, 
									   radiusObj.tl, radiusObj.tr, 
									   radiusObj.br, radiusObj.bl);
					
						drawRoundRect(
							0, 0, w, h, radiusObj,
							borderColor, 1,
							null, null, null, hole);
						
						// Reset radius here so background drawing
						// below is correct.
						radiusObj.tl = Math.max(radius - borderThickness, 0);
						radiusObj.tr = Math.max(radius - borderThickness, 0);
						radiusObj.bl = bRoundedCorners ? Math.max(radius - borderThickness, 0) : 0;
						radiusObj.br = bRoundedCorners ? Math.max(radius - borderThickness, 0) : 0;
					}
					else
					{
						drawDropShadow(0, 0, w, h,
									   radius, radius, radius, radius);
					
						drawRoundRect(
							0, 0, w, h, radius,
							borderColor, 1,
							null, null, null, hole);
						
						// Reset radius here so background drawing
						// below is correct.
						radius = Math.max(getStyle("cornerRadius") -
								 borderThickness, 0);
					}									
				}
			} // switch
		}
	}


	/**
	 *  @private
	 *  Draw a 3D border.
	 */
	mx_internal function draw3dBorder(c1:Number, c2:Number, c3:Number,
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

		drawDropShadow(0, 0, width, height, 0, 0, 0, 0);
		
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
	}

	/**
	 *  @private
	 */
	mx_internal function drawBackground(w:Number, h:Number):void
	{
		// The behavior used to be that we always create a background
		// regardless of whether we have a background color or not.
		// Now we only create a background if we have a color
		// or if the mouseShield or mouseShieldChildren styles are true.
		// Look at Container.addEventListener and Container.isBorderNeeded
		// for the mouseShield logic. JCS 6/24/05
		if ((backgroundColor !== null &&
		     backgroundColor !== "") ||
			getStyle("mouseShield") ||
			getStyle("mouseShieldChildren"))
		{
			var nd:Number = Number(backgroundColor);
			var alpha:Number = 1.0;
			var bm:EdgeMetrics = getBackgroundColorMetrics();
			var g:Graphics = graphics;
			
			if (isNaN(nd) ||
				backgroundColor === "" ||
				backgroundColor === null)
			{
				alpha = 0;
				nd = 0xFFFFFF;
			}
			else
			{
				alpha = getStyle(backgroundAlphaName);
			}

			// If we have a non-zero radius, use drawRoundRect()
			// to fill in the background.
			if (radius != 0 || backgroundHole)
			{			
				var bottom:Number = bm.bottom;

				if (radiusObj)
				{
					var bottomRadius:Number =
						bRoundedCorners ? radius : 0;

					radiusObj = { tl: radius,
							      tr: radius,
							      bl: bottomRadius,
							      br: bottomRadius };

					drawRoundRect(
						bm.left, bm.top,
						width - (bm.left + bm.right),
						height - (bm.top + bottom), 
						radiusObj,
						nd, alpha, null,
						GradientType.LINEAR, null,
						backgroundHole);
				}
				else
				{
					drawRoundRect(
						bm.left, bm.top,
						width - (bm.left + bm.right),
						height - (bm.top + bottom), 
						radius,
						nd, alpha, null,
						GradientType.LINEAR, null,
						backgroundHole);
				}
			}
			else
			{
				g.beginFill(nd, alpha);
				g.drawRect(bm.left, bm.top,
						   w - bm.right - bm.left, h - bm.bottom - bm.top);
				g.endFill();
			}
		}
		
		var borderStyle:String = getStyle("borderStyle");
		
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 && (borderStyle == "alert" || borderStyle == "default") && getStyle("headerColors") == null)
 		{
 			var highlightAlphas:Array = getStyle("highlightAlphas");
 			var highlightAlpha:Number = highlightAlphas ? highlightAlphas[0] : 0.3;
 			// edge
 			drawRoundRect(
 				0, 0, w, h,
 				{ tl: radius, tr: radius, bl: 0, br: 0 },
 				0xFFFFFF, highlightAlpha, null,
 				GradientType.LINEAR, null, 
 				{ x: 0, y: 1, w: w, h: h - 1,
 				  r: { tl: radius, tr: radius, bl: 0, br: 0 } });
 		}	
	}

	/**
	 *  @private
	 *  Apply a drop shadow using a bitmap filter.
	 *
	 *  Bitmap filters are slow, and their slowness is proportional
	 *  to the number of pixels being filtered.
	 *  For a large HaloBorder, it's wasteful to create a big shadow.
	 *  Instead, we'll create the shadow offscreen
	 *  and stretch it to fit the HaloBorder.
	 */
	mx_internal function drawDropShadow(x:Number, y:Number, 
									width:Number, height:Number,
									tlRadius:Number, trRadius:Number, 
									brRadius:Number, blRadius:Number):void
	{
		// Do I need a drop shadow in the first place?  If not, return
		// immediately.
		if (getStyle("dropShadowEnabled") == false || 
		    getStyle("dropShadowEnabled") == "false" ||
			width == 0 || 
			height == 0)
		{
			return;
		}

		// Calculate the angle and distance for the shadow
		var distance:Number = getStyle("shadowDistance");
		var direction:String = getStyle("shadowDirection");
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
		
		// Create a RectangularDropShadow object, set its properties,
		// and draw the shadow
		if (!dropShadow)
			dropShadow = new RectangularDropShadow();

		dropShadow.distance = distance;
		dropShadow.angle = angle;
		dropShadow.color = getStyle("dropShadowColor");
		dropShadow.alpha = 0.4;

		dropShadow.tlRadius = tlRadius;
		dropShadow.trRadius = trRadius;
		dropShadow.blRadius = blRadius;
		dropShadow.brRadius = brRadius;

		dropShadow.drawShadow(graphics, x, y, width, height);
	}

	/**
	 *  @private
	 *  Convert the value of the shadowDirection property
	 *  into a shadow angle.
	 */
	mx_internal function getDropShadowAngle(distance:Number,
										direction:String):Number
	{
		if (direction == "left")
			return distance >= 0 ? 135 : 225;

		else if (direction == "right")
			return distance >= 0 ? 45 : 315;
		
		else // direction == "center"
			return distance >= 0 ? 90 : 270;
	}

	/**
	 *  @private
	 */
	mx_internal function getBackgroundColor():Object
	{
		var p:IUIComponent = parent as IUIComponent;
		if (p && !p.enabled)
		{
			var color:Object = getStyle("backgroundDisabledColor");
			if (color !== null && StyleManager.isValidStyleValue(color))
				return color;
		}

		return getStyle("backgroundColor");
	}
	
	/**
	 *  @private
	 */
	mx_internal function getBackgroundColorMetrics():EdgeMetrics
	{
		return borderMetrics;
	}
}

}
