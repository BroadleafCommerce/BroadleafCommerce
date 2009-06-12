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
import mx.core.mx_internal;

/**
 *  The skin for all the states of the icon in a PopUpMenuButton.
 */
public class PopUpMenuIcon extends PopUpIcon
{
    include "../../core/Version.as";    
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor	 
     */ 
    public function PopUpMenuIcon()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overriden Methods
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
        g.lineStyle(1, mx_internal::arrowColor);
        g.moveTo(0, 0);
        g.lineTo(w / 2, height);
        g.lineTo(w, 0);
    }
}

}
