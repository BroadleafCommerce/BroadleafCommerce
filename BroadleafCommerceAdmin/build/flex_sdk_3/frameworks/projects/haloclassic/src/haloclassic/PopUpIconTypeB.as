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
import mx.core.mx_internal;

[ExcludeClass]

/**
 *  Documentation is not currently available.
 *  @review
 */
public class PopUpIconTypeB extends PopUpIcon
{
	include "../mx/core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor	 
     */ 
    public function PopUpIconTypeB()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overriden methods
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
        g.moveTo(-w / 2, -h / 2);
        g.lineTo(0, height / 2);
        g.lineTo(w / 2, -h / 2);
    }
}

}
