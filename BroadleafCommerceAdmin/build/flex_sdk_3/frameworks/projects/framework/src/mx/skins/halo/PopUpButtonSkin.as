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

package mx.skins.halo
{

import flash.display.DisplayObject;
import flash.display.GradientType;
import mx.core.IFlexDisplayObject;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;
import mx.core.IProgrammaticSkin;

/**
 *  The skin for all the states of a PopUpButton.
 */
public class PopUpButtonSkin extends UIComponent implements IProgrammaticSkin
{
    include "../../core/Version.as";
    
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

		mouseEnabled = false;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------


	//----------------------------------
	//  measuredWidth
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredWidth():Number
	{
		return DEFAULT_MEASURED_MIN_WIDTH;
	}

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return DEFAULT_MEASURED_MIN_HEIGHT;
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
        var arrowColor:uint = getStyle("iconColor");
        var borderColor:uint = getStyle("borderColor");
        var cornerRadius:Number = getStyle("cornerRadius");
		var fillAlphas:Array = getStyle("fillAlphas");
		var fillColors:Array = getStyle("fillColors");
        StyleManager.getColorNames(fillColors);
		var highlightAlphas:Array = getStyle("highlightAlphas");		
        var themeColor:uint = getStyle("themeColor");

        // Derivative styles.
        var derStyles:Object = calcDerivedStyles(themeColor, fillColors[0],
                                                 fillColors[1]);

        var borderColorDrk1:Number =
			ColorUtil.adjustBrightness2(borderColor, -50);
		
		var themeColorDrk1:Number =
			ColorUtil.adjustBrightness2(themeColor, -25);
		
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

        var arrowButtonWidth:Number = Math.max(getStyle("arrowButtonWidth"),
											   popUpIcon.width + 3 + 1);
        
		var dividerPosX:Number = w - arrowButtonWidth;

		popUpIcon.move(w - (arrowButtonWidth + popUpIcon.width) / 2,
					   (h - popUpIcon.height) / 2);   

        var cr:Number = Math.max(0, cornerRadius);
        var cr1:Number = Math.max(0, cornerRadius - 1);
		
		var upFillColors:Array;
		var upFillAlphas:Array;

		var overFillColors:Array;
		var overFillAlphas:Array;

		graphics.clear();
        
        switch (name)
        {            
            case "upSkin":
            {
   				upFillColors = [ fillColors[0], fillColors[1] ];
   				upFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ borderColor, borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });      

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ borderColor, borderColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h)); 

				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					GradientType.LINEAR, null, 
					{ x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });

				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h - 2) / 2,
					{ tl: cr1, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2),
					GradientType.LINEAR, null,
					{ x: dividerPosX, y: 1, w: 1, h: (h - 2) / 2, r: 0 });

				// side bevel highlight edges
				drawRoundRect(
					dividerPosX - 1, 1, 1, h - 2, 0,
					borderColor, 0);                        
				drawRoundRect(
					dividerPosX + 1, 1, 1, h - 2, 0,
					borderColor, 0);                                    
                                   
                break;
            }
                        
            case "overSkin": // for hover on the main button (left) side
            {
   				upFillColors = [ fillColors[0], fillColors[1] ];
				upFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				if (fillColors.length > 2)
					overFillColors = [ fillColors[2], fillColors[3] ];
				else
					overFillColors = [ fillColors[0], fillColors[1] ];

				if (fillAlphas.length > 2)
					overFillAlphas = [ fillAlphas[2], fillAlphas[3] ];
  				else
					overFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });        

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));  
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					GradientType.LINEAR, null, 
					{ x: dividerPosX, y: 1, w: 1, h: h - 2, 
					  r: getRadius(cr1, true) });                         
                                            
				// left/main button fill
				drawRoundRect(
					1, 1, w - arrowButtonWidth - 2, h - 2,
					getRadius(cr1, true),
					overFillColors, overFillAlphas,
					verticalGradientMatrix(1, 1, dividerPosX - 2, h - 2));                               

				// top highlight 
				drawRoundRect(
					1, 1, w - 2, (h -2) / 2,
					{ tl: cr1, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2),
					GradientType.LINEAR, null,
					{ x: dividerPosX, y: 1, w: 1, h: (h - 2) / 2, r: 0 }); 

				// side bevel highlight edges
				drawRoundRect(
					dividerPosX - 1, 1, 1, h - 2, 0,
					themeColor, 0.35);                                    

                break;
            }

            case "popUpOverSkin": // for hover on the arrow-button (right) side
            {
   				upFillColors = [ fillColors[0], fillColors[1] ];
				upFillAlphas = [ fillAlphas[0], fillAlphas[1] ];
              
				if (fillColors.length > 2)
					overFillColors = [ fillColors[2], fillColors[3] ];
				else
					overFillColors = [ fillColors[0], fillColors[1] ];

				if (fillAlphas.length > 2)
					overFillAlphas = [ fillAlphas[2], fillAlphas[3] ];
  				else
					overFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });        

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));  
                    
				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, getRadius(cr1, true),
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					GradientType.LINEAR, null, 
					{ x: dividerPosX, y: 1, w: arrowButtonWidth - 1, h: h - 2,
					  r: getRadius(cr1, true) });
                                            
				// right button fill
				drawRoundRect(
					dividerPosX + 1, 1, arrowButtonWidth - 2, h - 2,
					getRadius(cr1, false),
					overFillColors, overFillAlphas,
					verticalGradientMatrix(dividerPosX, 0,
										   arrowButtonWidth - 1, h - 2));                       

				// top highlight 
				drawRoundRect(
					1, 1, w - 2, (h -2) / 2,
					{ tl: cr1, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2),
					GradientType.LINEAR, null,
					{ x: dividerPosX, y: 1, w: 1, h: (h - 2) / 2, r: 0 });

				// side bevel highlight edges
				drawRoundRect(
					dividerPosX + 1, 1, 1, h - 2, 0,
					themeColor, 0.35);                                    

                break;
            }
            
            case "downSkin": // for press on the main button (left) side
            {
   				upFillColors = [ fillColors[0], fillColors[1] ];
				upFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				// button border/ddge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h ),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });        

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));  

				// button fill                
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					GradientType.LINEAR, null, 
					{ x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });                         

				// left/main button fill
				drawRoundRect(
					1, 1, w - arrowButtonWidth - 2, h - 2,
					getRadius(cr1, true),
					[ derStyles.fillColorPress1, derStyles.fillColorPress2], 1,
					verticalGradientMatrix(1, 1, dividerPosX - 2, h - 2));      

				// top highlight (checked, works)
				drawRoundRect(
					1, 1, w - 2, (h -2) / 2,
					{ tl: cr1, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2),
					GradientType.LINEAR, null,
					{ x: dividerPosX, y: 1, w: 1, h: (h -2) / 2, r: 0 }); 

				// side bevel highlight edges
				drawRoundRect(
					dividerPosX - 1, 1, 1, h - 2, 0,
					themeColorDrk1, 0.3);                                    
				drawRoundRect(
					dividerPosX + 1, 1, 1, h - 2, 0,
					borderColor, 0);                                    

                break;
            }

            case "popUpDownSkin": // for press on the arrow-button (right) side
            {
   				upFillColors = [ fillColors[0], fillColors[1] ];
				upFillAlphas = [ fillAlphas[0], fillAlphas[1] ];

				// button border/edge
				drawRoundRect(
					0, 0, w, h, cr,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h ),
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });        

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ themeColor, themeColorDrk1 ], 1,
					verticalGradientMatrix(0, 0, w, h));  

				// button fill                
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					upFillColors, upFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					GradientType.LINEAR, null, 
					{ x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });                         

			    // right button fill
				drawRoundRect(
					dividerPosX + 1, 1, arrowButtonWidth - 2, h - 2,
					getRadius(cr1, false),
					[ derStyles.fillColorPress1,
					  derStyles.fillColorPress2], 1,
					verticalGradientMatrix(dividerPosX, 0,
										   arrowButtonWidth - 1, h - 2));           

				// top highlight
				drawRoundRect(
					1, 1, w - 2, (h -2) / 2,
					{ tl: cr1, tr: cr1, bl: 0, br: 0 },
					[ 0xFFFFFF, 0xFFFFFF ], highlightAlphas,
					verticalGradientMatrix(1, 1, w - 2, (h - 2) / 2),
					GradientType.LINEAR, null,
					{ x: dividerPosX, y: 1, w: 1, h: (h - 2) / 2, r: 0 });

				// side bevel highlight edges
				drawRoundRect(
					dividerPosX - 1, 1, 1, h - 2, 0,
					borderColor, 0);                                    
				drawRoundRect(
					dividerPosX + 1, 1, 1, h - 2, 0,
					themeColorDrk1, 0.3);

                break;
            }
            
            case "disabledSkin":
            {
                arrowColor = getStyle("disabledIconColor");
                
   				var disFillColors:Array = [ fillColors[0], fillColors[1] ];
   				
				var disFillAlphas:Array =
					[ Math.max(0, fillAlphas[0] - 0.15),
					  Math.max(0, fillAlphas[1] - 0.15) ];

				// outer edge
				drawRoundRect(
					0, 0, w, h, cornerRadius,
					[ borderColor, borderColorDrk1 ], 0.5,
					verticalGradientMatrix(0, 0, w, h ),      
					GradientType.LINEAR, null, 
					{ x: 1, y: 1, w: w - 2, h: h - 2, r: cornerRadius - 1 });        

				drawRoundRect(
					dividerPosX, 1, 1, h - 2, 0,
					[ borderColor, borderColorDrk1 ], 0.5);  

				// button fill
				drawRoundRect(
					1, 1, w - 2, h - 2, cr1,
					disFillColors, disFillAlphas,
					verticalGradientMatrix(1, 1, w - 2, h - 2),
					null, null, 
					{ x: dividerPosX, y: 1, w: 1, h: h - 2, r: 0 });
                
                break;
            }
        }

        if (popUpIcon is PopUpIcon)
        	PopUpIcon(popUpIcon).mx_internal::arrowColor = arrowColor;
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
			   { br: 0, bl: r, tr: 0, tl: r } :
			   { br: r, bl: 0, tr: r, tl: 0 };
    }
}

}
