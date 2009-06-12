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

import mx.controls.ToolTip;

/**
 *  The SliderDataTip class defines the tooltip used in the mx.controls.Slider control. 
 *  The class adds no additional functionality to mx.controls.ToolTip.
 *  It is used only to apply a type selector style.
 *  	
 *  @see mx.controls.HSlider
 *  @see mx.controls.VSlider
 *  @see mx.controls.sliderClasses.Slider
 *  @see mx.controls.sliderClasses.SliderLabel
 *  @see mx.controls.sliderClasses.SliderThumb
 */
public class SliderDataTip extends ToolTip
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
	public function SliderDataTip()
	{
		super();
	}
}

}
