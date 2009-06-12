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

package mx.controls.sliderClasses
{

import flash.text.TextLineMetrics;
import mx.controls.Label;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The SliderLabel class defines the label used in the mx.controls.Slider component. 
 *  The class adds no additional functionality to mx.controls.Label.
 *  It is used to apply a type selector style.
 *  	
 *  @see mx.controls.HSlider
 *  @see mx.controls.VSlider
 *  @see mx.controls.sliderClasses.Slider
 *  @see mx.controls.sliderClasses.SliderDataTip
 *  @see mx.controls.sliderClasses.SliderThumb
 */
public class SliderLabel extends Label
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
	public function SliderLabel()
	{
		super();
	}
	
	/**
	 *  @private 
	 */
	override mx_internal function getMinimumText(t:String):String
	{
		 // If the text is null or empty
		// make the measured size big enough to hold
		// a capital character using the current font.
        if (!t || t.length < 1)
            t = "W";
			
		return t;	
	}
}

}
