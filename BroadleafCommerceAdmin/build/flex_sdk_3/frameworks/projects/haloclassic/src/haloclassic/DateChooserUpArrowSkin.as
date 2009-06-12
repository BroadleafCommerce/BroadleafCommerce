////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package haloclassic
{

import flash.display.Graphics;
import mx.skins.Border;

/**
 *  Documentation is not currently available.
 *  @review
 */
public class DateChooserUpArrowSkin extends Border
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
	public function DateChooserUpArrowSkin()
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
	private var hitLength:Number = 2;
	 
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
		return 6 + 2 * hitLength;
	}
	
	//----------------------------------
	//  measuredHeight
	//----------------------------------

	/**
	 *  @private
	 */
	override public function get measuredHeight():Number
	{
		return 4 + 2 * hitLength;
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

		var g:Graphics = graphics;
	
		g.clear();
	
		switch (name)
		{
			case "nextYearUpSkin":
			case "nextYearDownSkin":
			case "nextYearOverSkin":		
			{
				// Invisible hit area
				g.beginFill(0x000000, 0.0); 
				g.moveTo(0, 0);
				g.lineTo(0, h);
				g.lineTo(w, h);
				g.lineTo(w, 0);
				g.lineTo(0, 0);				
				g.endFill();
					
				// Visible button area				
				g.beginFill(0x000000);								
				g.moveTo(w / 2, h / 2 - 2);
				g.lineTo(w / 2 - 3, h / 2 + 2);
				g.lineTo(w / 2 + 3, h / 2 + 2);
				g.lineTo(w / 2, h / 2 - 2);
				g.endFill();				
				break;
			}

			case "nextYearDisabledSkin":
			{
				g.beginFill(0x999999);
				g.moveTo(w / 2, h / 2 - 2);
				g.lineTo(w / 2 - 3, h / 2 + 2);
				g.lineTo(w / 2 + 3, h / 2 + 2);
				g.lineTo(w / 2, h / 2 - 2);
				g.endFill();
				break;
			}
		}
	}
}

}
