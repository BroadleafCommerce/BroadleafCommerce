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
import flash.geom.Matrix;
import mx.styles.StyleManager;
import mx.skins.ProgrammaticSkin;

    
/**
 *  The skin for the background of the column headers in a DataGrid control.
 *
 *  @see mx.controls.DataGrid
 */
public class DataGridHeaderBackgroundSkin extends ProgrammaticSkin
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
	public function DataGridHeaderBackgroundSkin()
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
		var g:Graphics = graphics;
		g.clear();
		
		var colors:Array = getStyle("headerColors");
		StyleManager.getColorNames(colors);
		
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h + 1, Math.PI/2, 0, 0);
		
		colors = [ colors[0], colors[0], colors[1] ];
		var ratios:Array = [ 0, 60, 255 ];
		var alphas:Array = [ 1.0, 1.0, 1.0 ];
		
		g.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, matrix);
		g.lineStyle(0, 0x000000, 0);
		g.moveTo(0, 0);
		g.lineTo(w, 0);
		g.lineTo(w, h - 0.5);
		g.lineStyle(0, getStyle("borderColor"), 100);
		g.lineTo(0, h - 0.5);
		g.lineStyle(0, 0x000000, 0);
		g.endFill();
	}
}

}
