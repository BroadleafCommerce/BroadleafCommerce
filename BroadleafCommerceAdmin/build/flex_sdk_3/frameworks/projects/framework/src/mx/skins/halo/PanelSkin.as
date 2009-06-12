////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
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
import flash.utils.getQualifiedClassName;
import flash.utils.describeType;
import mx.core.IContainer;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IUIComponent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The PanelSkin class defines the skin for the Panel, TitleWindow, and Alert components.
 */ 
public class PanelSkin extends HaloBorder
{ 
    include "../../core/Version.as";
    
    /**
     *  Constructor
     */
    public function PanelSkin()
    {
        super();
    }
    
    /**
     *  @private
     */
    private var oldHeaderHeight:Number;
    
    /**
     *  @private
     */
    private var oldControlBarHeight:Number;
    
    /**
	 *  @private
	 *  Internal object that contains the thickness of each edge
	 *  of the border
	 */
	protected var _panelBorderMetrics:EdgeMetrics;
    
    /**
     *  @private
     *  Return the thickness of the border edges.
     *
     *  @return Object  top, bottom, left, right thickness in pixels
     */
    override public function get borderMetrics():EdgeMetrics
    {   
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return super.borderMetrics;
        
        var hasPanelParent:Boolean = isPanel(parent);
        var controlBar:IUIComponent = hasPanelParent ? Object(parent).mx_internal::_controlBar : null;        
        var hHeight:Number = hasPanelParent ? Object(parent).mx_internal::getHeaderHeightProxy() : NaN;

        var newControlBarHeight:Number;
        
        if (controlBar && controlBar.includeInLayout)
            newControlBarHeight = controlBar.getExplicitOrMeasuredHeight();

        if (newControlBarHeight != oldControlBarHeight &&
          	!(isNaN(oldControlBarHeight) && isNaN(newControlBarHeight)))
            _panelBorderMetrics = null;
        
        if ((hHeight != oldHeaderHeight) && 
        	!(isNaN(hHeight) && isNaN(oldHeaderHeight)))
            _panelBorderMetrics = null;
                
        if (_panelBorderMetrics)
            return _panelBorderMetrics;

        var o:EdgeMetrics = super.borderMetrics;
        var vm:EdgeMetrics = new EdgeMetrics(0, 0, 0, 0);

        var bt:Number = getStyle("borderThickness");
        var btl:Number = getStyle("borderThicknessLeft");
        var btt:Number = getStyle("borderThicknessTop");
        var btr:Number = getStyle("borderThicknessRight");
        var btb:Number = getStyle("borderThicknessBottom");

        // Add extra space to edges (was margins).
        vm.left = o.left + (isNaN(btl) ? bt : btl);
        vm.top = o.top + (isNaN(btt) ? bt : btt);
        vm.right = o.bottom + (isNaN(btr) ? bt : btr);
        
        // Bottom is a special case. If borderThicknessBottom is NaN,
        // use btl if we don't have a control bar or btt if we do.
        vm.bottom = o.bottom + (isNaN(btb) ? 
            (controlBar && !isNaN(btt) ? btt : isNaN(btl) ? bt : btl) : 
            btb);  
                    
        // Since the header covers the solid portion of the border,  
        // we need to use the larger of borderThickness or headerHeight
        
        oldHeaderHeight = hHeight;
        if (!isNaN(hHeight))
            vm.top += hHeight;
        
        oldControlBarHeight = newControlBarHeight 
        if (!isNaN(newControlBarHeight))
            vm.bottom += newControlBarHeight;
        
        _panelBorderMetrics = vm;
        
        return _panelBorderMetrics;
    }

	/**
	 *  @private
	 *  If borderStyle may have changed, clear the cached border metrics.
	 */
	override public function styleChanged(styleProp:String):void
	{
		super.styleChanged(styleProp);
		
		if (styleProp == null ||
			styleProp == "styleName" ||
			styleProp == "borderStyle" ||
			styleProp == "borderThickness" ||
			styleProp == "borderThicknessTop" ||
			styleProp == "borderThicknessBottom" ||
			styleProp == "borderThicknessLeft" ||
			styleProp == "borderThicknessRight" ||
			styleProp == "borderSides" )
		{
			_panelBorderMetrics = null;
		}
		
		invalidateDisplayList();
	}

    /**
     *  @private
     */
    override mx_internal function drawBorder(w:Number, h:Number):void
    {
        super.drawBorder(w,h);
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return;
        
        var borderStyle:String = getStyle("borderStyle");
        
        if (borderStyle == "default")
        {       
            // For Panel/Alert, "borderAlpha" is the alpha for the
            // title/control/gutter area and "backgroundAlpha"
            // is the alpha for the content area.
            // We flip-flop the variables here so the "borderAlpha"
            // is applied by the background drawing code at the bottom.
            var contentAlpha:Number = getStyle("backgroundAlpha");
            var backgroundAlpha:Number = getStyle("borderAlpha");
            backgroundAlphaName = "borderAlpha";
            
            radiusObj = null;
            radius = getStyle("cornerRadius");
            bRoundedCorners =
                getStyle("roundedBottomCorners").toString().toLowerCase() == "true";
            var br:Number = bRoundedCorners ? radius : 0;
                        
            var g:Graphics = graphics;
    
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
    }
    
    /**
     *  @private
     */
    override mx_internal function drawBackground(w:Number, h:Number):void
    {
        super.drawBackground(w,h);
        
        if (getStyle("headerColors") == null && getStyle("borderStyle") == "default")
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
     */
    override mx_internal function getBackgroundColorMetrics():EdgeMetrics
    {
    	if (getStyle("borderStyle") == "default")
        	return EdgeMetrics.EMPTY;
        else
        {
        	return super.borderMetrics;
        }
    }

   	/**
	 *  We don't use 'is' to prevent dependency issues
	 */
	static private var panels:Object = {};

	static private function isPanel(parent:Object):Boolean
	{
		var s:String = getQualifiedClassName(parent);
		if (panels[s] == 1)
			return true;

		if (panels[s] == 0)
			return false;

		if (s == "mx.containers::Panel")
		{
			panels[s] == 1;
			return true;
		}

		var x:XML = describeType(parent);
		var xmllist:XMLList = x.extendsClass.(@type == "mx.containers::Panel");
		if (xmllist.length() == 0)
		{
			panels[s] = 0;
			return false;
		}
		
		panels[s] = 1;
		return true;
	}

}
}