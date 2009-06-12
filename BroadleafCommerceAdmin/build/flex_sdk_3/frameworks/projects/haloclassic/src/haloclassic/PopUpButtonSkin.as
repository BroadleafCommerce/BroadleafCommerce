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

package haloclassic
{

import flash.display.DisplayObject;
import flash.display.GradientType;
import mx.core.EdgeMetrics;
import mx.core.IFlexDisplayObject;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 *  The skin for all the states of a PopUpButton.
 */
public class PopUpButtonSkin extends UIComponent
{
	include "../mx/core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    private static var cache:Object = {}; 
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
	 *  Several colors used for drawing are calculated from the base colors
	 *  of the component (themeColor, borderColor and fillColors).
	 *  Since these calculations can be a bit expensive,
	 *  we calculate once per color set and cache the results.
     */
    private static function calcDerivedStyles(themeColor:uint,
                                              fillColor0:uint,
                                              fillColor1:uint):Object
    {
        var key:String = HaloColors.getCacheKey(themeColor,
												fillColor0, fillColor1); 
                
        if (!cache[key])
        {
            var o:Object = cache[key] = {};
            
            // Cross-component styles.
            HaloColors.addHaloColors(o, themeColor, fillColor0, fillColor1);
            
            // PopUpButton-specific styles.
            o.innerEdgeColor1 = ColorUtil.adjustBrightness2(fillColor0, -10);
            o.innerEdgeColor2 = ColorUtil.adjustBrightness2(fillColor1, -25);
        }
        
        return cache[key];
    }
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function PopUpButtonSkin()
    {
        super();
    }
    
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
     *  Storage for the borderMetrics property.
     */
    private var _borderMetrics:EdgeMetrics;

    /**
     *  Return the thickness of the border edges.
	 *
     *  @return Object left, top, right, bottom thickness in pixels
     */
    public function get borderMetrics():EdgeMetrics
    {
        if (_borderMetrics)
            return _borderMetrics;
        
        var borderThickness:Number = getStyle("borderThickness");
        
		_borderMetrics = new EdgeMetrics(borderThickness, borderThickness,
                                         borderThickness, borderThickness);
                                          
        return _borderMetrics;
    }

	//----------------------------------
	//  measuredWidth
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredWidth():Number
	{
		return 50;
	}

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 22;
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

        // User-defined styles.
        var arrowButtonWidth:Number = getStyle("arrowButtonWidth");
        var bevel:Boolean = getStyle("bevel");
        var borderColor:uint = getStyle("borderColor");
        var borderThickness:Number =
            Math.max(0, getStyle("borderThickness") - 2);
        var cornerRadius:Number = getStyle("cornerRadius");
        var fillColors:Array = getStyle("fillColors");
        StyleManager.getColorNames(fillColors);
        var themeColor:uint = getStyle("themeColor");

        // Derivative styles.
        var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
                                                 fillColors[1]);

        var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -25);

		var dividerPosX:Number = w - arrowButtonWidth;
        
		var arrowColor:uint = 0x111111;

		var cr:Number = Math.max(0, cornerRadius);
		var cr1:Number = Math.max(0, cornerRadius - 1);
		var cr2:Number = Math.max(0, cornerRadius - 2);
		var cr3:Number = Math.max(0, cornerRadius - 3);

        var tmp:Number;
        
        graphics.clear();
        
        switch (name)
        {            
            case "upSkin":
            {
                if (borderThickness > 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
						[ borderColor, borderColorDrk1 ], 1,
                              verticalGradientMatrix(0, 0, w - 2, h - 2)); 
                    
                    // inner glow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
						[ 0xFFFFFF, 0xE6E6E6 ], 1,
						verticalGradientMatrix(0, 0, w - 2, h - 2)); 
                    
                    tmp = borderThickness + 1;
                    
                    // inner border/edge
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
						[ derStyles.innerEdgeColor1,
						  derStyles.innerEdgeColor2], 1,
						verticalGradientMatrix(0, 1, w - 4, h - 5)); 
                    
                    tmp = borderThickness + 2;
                    if (bevel)
                    {
                        // top bevel highlight edge
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
							derStyles.bevelHighlight1, 1, 
                            null, null, null, 
                            { x: dividerPosX, y: tmp,
							  w: 1, h: h - tmp * 2, r: 0 }); 
                        
                        // button fill
                        drawRoundRect(
							tmp, tmp + 1, w - tmp * 2, h - (tmp * 2 + 1), cr3,
							[ fillColors[0], fillColors[1] ], 1,
							verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp + 1,
							  w: 1, h: h - (tmp * 2 + 1), r: 0 }); 

                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, tmp, 1, h - tmp * 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, tmp, 1, h - tmp * 2, 0,
                            derStyles.bevelHighlight1, 1);                                    
                    }
                    else // (flat button, no bevel)
                    {
                        // button fill
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp,
                              w: 1, h: h - tmp * 2, r: 0 }); 
                    }
                }
                else if (borderThickness == 0) 
                {
                    if (bevel)
                    {
                        // button border/edge
                        drawRoundRect(
							0, 0, w, h, cr,
                            [ borderColor, borderColorDrk1 ], 1,
                            verticalGradientMatrix(0, 0, w - 2, h - 2));
                        
                        // top bevel highlight edge
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            derStyles.bevelHighlight1, 1,
                            null, null, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });

                        // button fill
                        drawRoundRect(
							1, 2, w - 2, h - 3, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 2, h - 2),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 2, w: 1, h: h - 3, r: 0 });

                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);                                    
                    }
                    else // (flat button, no bevel)
                    {                            
                        // button border/edge
                        drawRoundRect(
							0, 0, w, h, cr,
                            borderColor, 1);                                         
                        
                        // button fill
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 2, h - 2),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                    }
                }
                break;
            }
            
            case "overSkin":
            {
                if (borderThickness > 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1);
                                     
                    // inner blow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1, 
                        [ 0xFFFFFF, 0xE6E6E6 ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2));             
                    
                    // left inner glow
                    drawRoundRect(
						1, 1, dividerPosX, h - 2,
						getRadius(cr1, false), 
                        derStyles.themeColLgt, 1); 
                    
                    tmp = borderThickness + 1;
                    
                    // inner border/edge
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
                        [ derStyles.innerEdgeColor1,
						  derStyles.innerEdgeColor2], 1,
                        verticalGradientMatrix(0, 1, w - 4, h - 5));                     
                    
                    // left inner border/edge                    
                    drawRoundRect(
						tmp, tmp, dividerPosX + 1 - tmp, h - tmp * 2,
						getRadius(cr2, false),
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(0, 1, dividerPosX - 1, h - 5));
                    
                    drawRoundRect(
						tmp, tmp, dividerPosX + 1 - tmp, h - tmp * 2,
                        getRadius(cr2, false),
                        derStyles.themeColDrk1, 0.40);        
                
                    tmp = borderThickness + 2;
                    
                    if (bevel)
                    {
                        // top bevel highlight edge
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                            derStyles.bevelHighlight1, 1,
                            null, null, null, 
                            { x: dividerPosX, y: tmp,
							  w: 1, h: h - tmp * 2, r: 0}); 
                        
                        // button fill
                        drawRoundRect(
							tmp, tmp + 1, w - tmp * 2, h - (tmp * 2 + 1), cr3,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp + 1,
                              w: 1, h: h - (tmp * 2 + 1), r: 0 }); 
                        
                        // left button fill    
                        drawRoundRect(
							tmp, tmp + 1,
							w - arrowButtonWidth - tmp + 1, h - (tmp * 2 + 1),
                            getRadius(cr3, false),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(0, 0, dividerPosX - 2, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp + 1,
                              w: 1, h: h - (tmp * 2 + 1), r: 0 }); 
                            
                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, tmp, 1, h - tmp * 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, tmp, 1, h - tmp * 2, 0,
                            derStyles.bevelHighlight1, 1);                                            
            
                    } 
                    else // (flat button, no bevel)
                    {
                        // button fill
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp,
							  w: 1, h: h - tmp * 2, r: 0 });
        
                        // left button fill
                        drawRoundRect(
							tmp, tmp, dividerPosX - tmp, h - tmp * 2,
                            getRadius(cr3, false),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(0, 0, dividerPosX - 2, h - 4),
                            GradientType.LINEAR);                          
                    }
                    
                }
                else if (borderThickness == 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                       derStyles.themeColDrk1, 1); 
                    
                    if (bevel)
                    {
                        // top bevel highlight edge
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            derStyles.bevelHighlight1, 1,
                            null, null, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 }); 
                        
                        // button fill
                        drawRoundRect(
							1, 2, w - 2, h - 3, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 2, w: 1, h: h - 3, r: 0 });
                        
                        // left button fill
                        drawRoundRect(
							1, 2, w - arrowButtonWidth + 1, h - 3,
                            getRadius(cr1, false),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(0, 0, dividerPosX - 2, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 2, w: 1, h: h - 3, r: 0 });  

                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);                            
                    }
                    else // (flat button, no bevel)
                    {
                        // button fill
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 2, h - 2),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 }); 
                        
                        // left button fill
                        drawRoundRect(
							1, 1, dividerPosX - 1, h - 2,
                            getRadius(cr1, false),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(0, 0,
								dividerPosX - 2, h - 4));
                    }
                }
                break;
            }

            case "popUpOverSkin":
            {
                if (borderThickness > 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 

                    // inner glow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1, 
                        [ 0xFFFFFF, 0xE6E6E6 ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2));             
                    
                    // right inner glow
                    drawRoundRect(
						dividerPosX, 1, arrowButtonWidth - 1, h - 2,
                        getRadius(cr1, true),
                        derStyles.themeColLgt, 1);
                    
                    tmp = borderThickness + 1;
                    
                    // inner border/edge
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
                        [ derStyles.innerEdgeColor1,
                          derStyles.innerEdgeColor2], 1,
                        verticalGradientMatrix(0, 1, w - 4, h - 5));                     

                    // right inner border/edge                        
                    drawRoundRect(
						dividerPosX, tmp, arrowButtonWidth - tmp, h - tmp * 2,
						getRadius(cr2, true),
						[ fillColors[0], fillColors[1] ], 1,
						verticalGradientMatrix(
							dividerPosX, 1, arrowButtonWidth, h - 5));
                    
                    drawRoundRect(
						dividerPosX, tmp, arrowButtonWidth - tmp, h - tmp * 2,
						getRadius(cr2, true),
						derStyles.themeColDrk1, 0.40);            
                    
                    tmp = borderThickness + 2;
                    
                    if (bevel)
                    {
                        // top bevel highlight edge
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                            derStyles.bevelHighlight1, 1,
                            null, null, null, 
                            { x: dividerPosX, y: tmp,
                              w: 1, h: h - tmp * 2, r: 0 }); 

                        // button fill
                        drawRoundRect(
							tmp, tmp + 1, w - tmp * 2, h - (tmp * 2 + 1), cr3,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp + 1,
                              w: 1, h: h - (tmp * 2 + 1), r: 0 }); 
                        
                        // right button fill
                        drawRoundRect(
							dividerPosX, tmp + 1,
							arrowButtonWidth - tmp, h - (tmp * 2 + 1),
                            getRadius(cr3, true),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(
								dividerPosX, 0, arrowButtonWidth - 2, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp + 1,
                              w: 1, h: h - (tmp * 2 + 1), r: 0 });                     

                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, tmp, 1, h - tmp * 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, tmp, 1, h - tmp * 2, 0,
                           derStyles.bevelHighlight1, 1);                            
                        
                    } 
                    else // (flat button, no bevel)
                    {
                        // button fill
                        drawRoundRect(
							tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: tmp,
							  w: 1, h: h - tmp * 2, r: 0 });
                        
                        // right button fill
                        drawRoundRect(
							dividerPosX + 1, tmp,
							arrowButtonWidth - tmp - 1, h - tmp * 2,
                            getRadius(cr3, true),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(
								dividerPosX, 0, arrowButtonWidth - 2, h - 2));
                    }
                }
                else if (borderThickness == 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 
                    
                    if (bevel)
                    {
                        // top bevel highlight edge
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            derStyles.bevelHighlight1, 1,
                            null, null, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                        
                        // button fill
                        drawRoundRect(
							1, 2, w - 2, h - 3, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 4, h - 4),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 2, w: 1, h: h - 3, r: 0 });
                                        
                        // right button fill
                        drawRoundRect(
							dividerPosX + 1, 2, arrowButtonWidth - 2, h - 3,
                            getRadius(cr1, true),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(
								dividerPosX + 1, 0,
								arrowButtonWidth - 1, h - 2));

                        // side bevel highlight edges
                        drawRoundRect(
							dividerPosX - 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);                        
                        drawRoundRect(
							dividerPosX + 1, 1, 1, h - 2, 0,
                            derStyles.bevelHighlight1, 1);            
                        
                    }
                    else // (flat button, no bevel)
                    {
                        // button fill
                        drawRoundRect(
							1, 1, w - 2, h - 2, cr1,
                            [ fillColors[0], fillColors[1] ], 1,
                            verticalGradientMatrix(0, 0, w - 2, h - 2),
                            GradientType.LINEAR, null, 
                            { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                        
                        // right button fill
                        drawRoundRect(
							dividerPosX + 1, 1, arrowButtonWidth - 2, h - 2,
                            getRadius(cr1, true),
                            [ derStyles.fillColorBright1,
                              derStyles.fillColorBright2 ], 1,
                            verticalGradientMatrix(
								dividerPosX, 0, arrowButtonWidth - 2, h - 2));
                    }
                }
                break;
            }
            
            case "downSkin":
            {
                if (borderThickness > 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 

                    // inner glow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
                        [ 0xFFFFFF, 0xE6E6E6 ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2));             
                    
                    // left inner glow
                    drawRoundRect(
						1, 1, dividerPosX, h - 2,
                        getRadius(cr1, false),
                        themeColor, 1); 
                    
                    tmp = borderThickness + 1;
                    
                    // inner border/edge
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
                        [ derStyles.innerEdgeColor1,
                          derStyles.innerEdgeColor2], 1,
                       verticalGradientMatrix(0, 1, w - 4, h - 5));                     
                    
                    // left inner border/edge
                    drawRoundRect(
						tmp, tmp, dividerPosX + 1 - tmp, h - tmp * 2,
						getRadius(cr2, false),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						verticalGradientMatrix(0, 1, dividerPosX - 2, h - 5));
                
                    drawRoundRect(
						tmp, tmp, dividerPosX + 1 - tmp, h - tmp * 2,
						getRadius(cr2, false),
						derStyles.themeColDrk1, 0.40);    
                    
                    tmp = borderThickness + 2;

                    // button fill                
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                        [ fillColors[0], fillColors[1] ], 1,
                        verticalGradientMatrix(0, 0, w - 4, h - 4),
                        GradientType.LINEAR, null, 
                        { x: dividerPosX, y: tmp,
                          w: 1, h: h - (tmp * 2), r: 0 });                         

                    // left/main button fill
                    drawRoundRect(
						tmp, tmp, w - arrowButtonWidth - 1 - tmp, h - tmp * 2,
						getRadius(cr3, false),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1], 1,
						verticalGradientMatrix(0, 0, dividerPosX - 2, h - 4));
                } 
                else if (borderThickness == 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 
                    
                    // button fill
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
                        [ fillColors[0], fillColors[1] ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2),
                        GradientType.LINEAR, null, 
                        { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                    

                    // left/main button fill
                    drawRoundRect(
						1, 1, dividerPosX - 1, h - 2,
						getRadius(cr1, false),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1], 1,
						verticalGradientMatrix(0, 0, dividerPosX - 1, h - 2));                   

                }
                break;
            }

            case "popUpDownSkin":            
            {
                if (borderThickness > 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 
                    
                    // inner glow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1, 
                        [ 0xFFFFFF, 0xE6E6E6 ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2));     
                    
                    // right inner glow
                    drawRoundRect(
						dividerPosX, 1, arrowButtonWidth - 1, h - 2,
                        getRadius(cr1, true),
                        themeColor, 1); 
                    
                    tmp = borderThickness + 1;
                    
                    // inner border/edge
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr2,
                        [ derStyles.innerEdgeColor1,
                          derStyles.innerEdgeColor2], 1,
                        verticalGradientMatrix(0, 1, w - 4, h - 5));         
                    
                    // right inner border/edge
                    drawRoundRect(
						dividerPosX + 1, tmp,
						arrowButtonWidth - tmp - 1, h - tmp * 2,
						getRadius(cr2, true),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1 ], 1,
						verticalGradientMatrix(
							dividerPosX, 1, arrowButtonWidth - 1, h - 5));
                
                    drawRoundRect(
						dividerPosX, tmp, arrowButtonWidth - tmp, h - tmp * 2,
						getRadius(cr2, true),
						derStyles.themeColDrk1, 0.40);                        

                    tmp = borderThickness + 2;

                    // button fill                
                    drawRoundRect(
						tmp, tmp, w - tmp * 2, h - tmp * 2, cr3,
                        [ fillColors[0], fillColors[1] ], 1,
                        verticalGradientMatrix(0, 0, w - 4, h - 4),
                        GradientType.LINEAR, null, 
                        { x: dividerPosX, y: tmp,
						  w: 1, h: h - (tmp * 2), r: 0 });

                    // right button fill
                    drawRoundRect(
						dividerPosX + 1, tmp,
						arrowButtonWidth - tmp - 1, h - tmp * 2,
						getRadius(cr3, true),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1], 1,
						verticalGradientMatrix(
							dividerPosX, 0, arrowButtonWidth - 1, h - 4));                        
                } 
                else if (borderThickness == 0) 
                {
                    // button border/edge
                    drawRoundRect(
						0, 0, w, h, cr,
                        derStyles.themeColDrk1, 1); 
                    
                    // button fill
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
                        [ fillColors[0], fillColors[1] ], 1,
                        verticalGradientMatrix(0, 0, w - 2, h - 2),
                        GradientType.LINEAR, null, 
                        { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                    
                    // right button fill
                    drawRoundRect(
						dividerPosX + 1, 1, arrowButtonWidth - 2, h - 2,
						getRadius(cr1, true),
						[ derStyles.fillColorPress2,
						  derStyles.fillColorPress1], 1,
						verticalGradientMatrix(
							dividerPosX, 0, arrowButtonWidth - 1, h - 2));                                             
                }
                break;
            }
            
            case "disabledSkin":
            {
                arrowColor = 0x919999;
                
				if (borderThickness > 0) 
                {
                    // outer edge
                    drawRoundRect(
						0, 0, w, h, cornerRadius,
                        0x999999, 0.50);

                    // inner glow
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
                        0xE6E6E6, 0.50);

                    // button edge
                    drawRoundRect(
						2, 2, w - 4, h - 4, cr2,
                        0xAAAAAA, 0.50); 

                    // button fill
                    drawRoundRect(
						3, 3, w - 6, h - 6, cr3,
                        0xE6E6E6, 0.50,
                        null, null, null, 
                        { x: dividerPosX, y: 3, w: 1, h: h - 6, r: 0 });
                }
                else if (borderThickness == 0)
                {
                    // outer edge
                    drawRoundRect(
						0, 0, w, h, cornerRadius,
                        0x999999, 0.50);
                    
                    // button fill
                    drawRoundRect(
						1, 1, w - 2, h - 2, cr1,
                        0xE6E6E6, 0.50,
                        null, null, null, 
                        { x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                }
                break;
            }
        }
        
        var popUpIcon:IFlexDisplayObject =
			IFlexDisplayObject(getChildByName("popUpIcon"));
        
        if (!popUpIcon)
        {
            var popUpIconClass:Class = Class(getStyle("popUpIcon"));
            popUpIcon = new popUpIconClass();
            DisplayObject(popUpIcon).name = "popUpIcon";
            addChild(DisplayObject(popUpIcon));
            DisplayObject(popUpIcon).visible = true;            
        }
        
		if (popUpIcon is PopUpIcon)
        	PopUpIcon(popUpIcon).mx_internal::arrowColor = arrowColor;   
        
		popUpIcon.move(w - arrowButtonWidth / 2 - borderThickness, h / 2);   
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function getRadius(r:Number, left:Boolean):Object
    {
		return left ?
			   { br: r, bl: 0, tr: r, tl: 0 } :
			   { br: 0, bl: r, tr: 0, tl: r };
    }
}

}
