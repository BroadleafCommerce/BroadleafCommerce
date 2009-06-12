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

import flash.display.Graphics;
import mx.skins.Border;

/**
 *  The skin for the indeterminate state of a ProgressBar.
 */
public class ProgressIndeterminateSkin extends Border
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
	public function ProgressIndeterminateSkin()
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
     *  @private
     */    
    override public function get measuredWidth():Number
    {
        return 195;
    }
    
    //----------------------------------
	//  measuredHeight
    //----------------------------------
    
    /**
     *  @private
     */        
    override public function get measuredHeight():Number
    {
        return 6;
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
		
		// Hatches
		for (var i:int = 0; i < w; i += 28)
		{
			g.beginFill(0xFFFFFF, 0.65);
			g.moveTo(i, 1);
			g.lineTo(Math.min(i + 14, w), 1);
			g.lineTo(Math.min(i + 10, w), h - 1);
			g.lineTo(Math.max(i - 4, 0), h - 1);
			g.lineTo(i, 1);
			g.endFill();
		}
	}
}

}
