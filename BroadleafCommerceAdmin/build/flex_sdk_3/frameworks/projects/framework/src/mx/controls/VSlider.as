////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import mx.controls.sliderClasses.Slider;
import mx.controls.sliderClasses.SliderDirection;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  The location of the data tip relative to the thumb.
 *  Possible values are <code>"left"</code>, <code>"right"</code>,
 *  <code>"top"</code>, and <code>"bottom"</code>.
 *  
 *  @default "left"
 */
[Style(name="dataTipPlacement", type="String", enumeration="left, top, right, bottom", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="value", destination="labels")]

[DefaultTriggerEvent("change")]

[IconFile("VSlider.png")]

/**	
 *  The VSlider control lets users select a value by moving
 *  a slider thumb between the end points of the slider track.
 *  The current value of the slider is determined by the relative
 *  location of the thumb between the end points of the slider,
 *  corresponding to the slider's minimum and maximum values.
 *
 *  <p>The slider may allow a continuous range of values between its
 *  minimum and maximum values, or it may be restricted to values
 *  at concrete intervals between the minimum and maximum value.
 *  It may show tick marks at specified intervals along the track. These
 *  tick marks are independent of the allowed values of the slider. It
 *  may also use a data tip to display its current value.</p>
 *  	
 *  <p>The VSlider has a vertical orientation.
 *  The slider track stretches from bottom to top, and the labels
 *  and tick marks are placed to the left or right of the track.</p>
 *
 *  <p>The VSlider control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>250 pixels high, wide enough to hold the slider and any associated labels</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>None</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>None</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:VSlider&gt;</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attribute:</p>
 * 
 *  <pre>
 *  &lt;mx:VSlider
 *    <strong>Styles</strong>
 *    dataTipPlacement="top"
 *  /&gt;
 *  </pre>
 *  </p>
 *  
 *  @includeExample examples/SimpleImageVSlider.mxml
 *  	
 *  @see mx.controls.HSlider
 *  @see mx.controls.sliderClasses.Slider
 *  @see mx.controls.sliderClasses.SliderThumb
 *  @see mx.controls.sliderClasses.SliderDataTip
 *  @see mx.controls.sliderClasses.SliderLabel
 */
public class VSlider extends Slider
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
	public function VSlider()
	{
		super();

		direction = SliderDirection.VERTICAL;
	}
}

}
