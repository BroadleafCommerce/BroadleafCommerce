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

package haloclassic
{

import mx.skins.Border;

/**
 *  The skin for the highlighted state of the track of a Slider.
 */
public class SliderHighlightSkin extends Border
{
	include "../mx/core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructor.
	 */
	public function SliderHighlightSkin()
	{
		super();
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
	 *  The preferred width of this object.
	 */
	override public function get measuredWidth():Number
	{
		return 1;
	}

	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  The preferred height of this object.
	 */
	override public function get measuredHeight():Number
	{
		return 2;
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

		//var themeColor = getStyle("themeColor");
		var themeColor:int = 0x80FF4D;
		
		graphics.clear();
				
		// highlight
		drawRoundRect(
			0, 0, w, h, 0,
			themeColor, 1);
	}
}

}
