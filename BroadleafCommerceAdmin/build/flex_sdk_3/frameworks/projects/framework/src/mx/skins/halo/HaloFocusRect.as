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

import flash.display.Graphics;
import mx.skins.ProgrammaticSkin;
import mx.styles.CSSStyleDeclaration;
import mx.styles.IStyleClient;
import mx.utils.GraphicsUtil;

/**
 *  Defines the skin for the focus indicator. This is the rectangle that appears around a control when it has focus.
 */
public class HaloFocusRect extends ProgrammaticSkin implements IStyleClient
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
	public function HaloFocusRect()
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
  	private var _focusColor:Number;

	//--------------------------------------------------------------------------
	//
	//  Properties: IStyleClient
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  className
	//----------------------------------

	/**
	 *  @private
	 */
	public function get className():String
	{
		return "HaloFocusRect";
	}

	//----------------------------------
	//  inheritingStyles
	//----------------------------------

	/**
	 *  @private
	 */
	public function get inheritingStyles():Object
	{
		return styleName.inheritingStyles;
	}

	/**
	 *  @private
	 */
	public function set inheritingStyles(value:Object):void
	{
	}

	//----------------------------------
	//  nonInheritingStyles
	//----------------------------------

	/**
	 *  @private
	 */
	public function get nonInheritingStyles():Object
	{
		return styleName.nonInheritingStyles;
	}
	
	/**
	 *  @private
	 */
	public function set nonInheritingStyles(value:Object):void
	{
	}

	//----------------------------------
	//  styleDeclaration
	//----------------------------------

	/**
	 *  @private
	 */
	public function get styleDeclaration():CSSStyleDeclaration
	{
		return CSSStyleDeclaration(styleName);
	}
	
	/**
	 *  @private
	 */
	public function set styleDeclaration(value:CSSStyleDeclaration):void
	{
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

		var focusBlendMode:String = getStyle("focusBlendMode");
		var focusAlpha:Number = getStyle("focusAlpha");
		var focusColor:Number = getStyle("focusColor");
		var cornerRadius:Number = getStyle("cornerRadius");
		var focusThickness:Number = getStyle("focusThickness");
		var focusRoundedCorners:String = getStyle("focusRoundedCorners");
		var themeColor:Number = getStyle("themeColor");
		
		var rectColor:Number = focusColor;
		if (isNaN(rectColor))
			rectColor = themeColor;
			
		var g:Graphics = graphics;
		g.clear();
		
		blendMode = focusBlendMode;
		
		if (focusRoundedCorners != "tl tr bl br" && cornerRadius > 0)
		{
			// We have rounded corners on just some of the corners.
			
			var tl:Number = 0;
			var bl:Number = 0;
			var tr:Number = 0;
			var br:Number = 0;
			
			var nr:Number = cornerRadius + focusThickness;
			
			if (focusRoundedCorners.indexOf("tl") >= 0)
				tl = nr;

			if (focusRoundedCorners.indexOf("tr") >= 0)
				tr = nr;

			if (focusRoundedCorners.indexOf("bl") >= 0)
				bl = nr;

			if (focusRoundedCorners.indexOf("br") >= 0)
				br = nr;
				
			// outer ring
			g.beginFill(rectColor, focusAlpha);
			GraphicsUtil.drawRoundRectComplex(g, 0, 0, w, h, tl, tr, bl, br);
			tl = tl ? cornerRadius : 0;
			tr = tr ? cornerRadius : 0;
			bl = bl ? cornerRadius : 0;
			br = br ? cornerRadius : 0;
			GraphicsUtil.drawRoundRectComplex(g, focusThickness, focusThickness,
								   w - 2 * focusThickness, h - 2 * focusThickness,
								   tl, tr, bl, br);
			g.endFill();
			
			// inner ring
			nr = cornerRadius + (focusThickness / 2);
			tl = tl ? nr : 0;
			tr = tr ? nr : 0;
			bl = bl ? nr : 0;
			br = br ? nr : 0;
			g.beginFill(rectColor, focusAlpha);
			GraphicsUtil.drawRoundRectComplex(g, focusThickness / 2, focusThickness / 2,
								   w - focusThickness, h - focusThickness,
								   tl, tr, bl, br);
			tl = tl ? cornerRadius : 0;
			tr = tr ? cornerRadius : 0;
			bl = bl ? cornerRadius : 0;
			br = br ? cornerRadius : 0;
			GraphicsUtil.drawRoundRectComplex(g, focusThickness, focusThickness,
								   w - 2 * focusThickness, h - 2 * focusThickness,
								   tl, tr, bl, br);
			g.endFill();
		}
		else
		{
			var ellipseSize:Number;
			
			// outer ring
			g.beginFill(rectColor, focusAlpha);
			ellipseSize = (cornerRadius > 0 ? cornerRadius + focusThickness : 0) * 2;
			g.drawRoundRect(0, 0, w, h, ellipseSize, ellipseSize);
			ellipseSize = cornerRadius * 2;
			g.drawRoundRect(focusThickness, focusThickness,
					w - 2 * focusThickness, h - 2 * focusThickness,
					ellipseSize, ellipseSize);
			g.endFill();

			// inner ring
			g.beginFill(rectColor, focusAlpha);
			ellipseSize = (cornerRadius > 0 ? cornerRadius + focusThickness / 2 : 0) * 2;
			g.drawRoundRect(focusThickness / 2, focusThickness / 2,
					w - focusThickness, h - focusThickness,
					ellipseSize, ellipseSize);
			ellipseSize = cornerRadius * 2;
			g.drawRoundRect(focusThickness, focusThickness,
					w - 2 * focusThickness, h - 2 * focusThickness,
					ellipseSize, ellipseSize);
			g.endFill();
		}
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods: IStyleClient
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    override public function getStyle(styleProp:String):*
	{
		return styleProp == "focusColor" ?
			   _focusColor :
			   super.getStyle(styleProp);
	}

	/**
	 *  @private
	 */
    public function setStyle(styleProp:String, newValue:*):void
	{
		if (styleProp == "focusColor")
			_focusColor = newValue;
	}

	/**
	 *  @private
	 */
	public function clearStyle(styleProp:String):void
	{
		if (styleProp == "focusColor")
			_focusColor = NaN;
	}

	/**
	 *  @private
	 */
	public function getClassStyleDeclarations():Array
	{
		return [];
	}

	/**
	 *  @private
	 */
    public function notifyStyleChangeInChildren(
						styleProp:String, recursive:Boolean):void
	{
	}

	/**
	 *  @private
	 */
    public function regenerateStyleCache(recursive:Boolean):void
	{
	}

	/**
	 *  @private
	 */
    public function registerEffects(effects:Array /* of String */):void
	{
	}
}

}
