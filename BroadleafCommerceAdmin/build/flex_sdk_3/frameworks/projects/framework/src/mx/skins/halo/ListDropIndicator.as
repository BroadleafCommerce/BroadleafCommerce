////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
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

/**
 *  The skin for the drop indicator of a list-based control.
 */
public class ListDropIndicator extends ProgrammaticSkin
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
	public function ListDropIndicator()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  direction
	//----------------------------------

	/**
	 *  Should the skin draw a horizontal line or vertical line.
	 *  Default is horizontal.
	 */
	public var direction:String = "horizontal";
	
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
		g.lineStyle(2, 0x2B333C);
				
		// Line
		if (direction == "horizontal")
		{
		    g.moveTo(0, 0);
		    g.lineTo(w, 0);
        }
        else
        {
            g.moveTo(0, 0);
            g.lineTo(0, h);
        }		
	}
}

}
