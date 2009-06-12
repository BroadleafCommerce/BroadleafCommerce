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

package mx.skins.halo
{

import flash.display.Graphics;
import mx.skins.ProgrammaticSkin;
import mx.utils.ColorUtil;

/**
 *  The skin for the column drop indicator in a DataGrid.
 */
public class DataGridColumnDropIndicator extends ProgrammaticSkin
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
	public function DataGridColumnDropIndicator()
	{
		super();
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

		g.lineStyle(1, getStyle("rollOverColor"));
		g.moveTo(0, 0);
		g.lineTo(0, h);

		g.lineStyle(1, ColorUtil.adjustBrightness(getStyle("themeColor"), -75));
		g.moveTo(1, 0);
		g.lineTo(1, h);

		g.lineStyle(1, getStyle("rollOverColor"));
		g.moveTo(2, 0);
		g.lineTo(2, h);
	}
}

}
