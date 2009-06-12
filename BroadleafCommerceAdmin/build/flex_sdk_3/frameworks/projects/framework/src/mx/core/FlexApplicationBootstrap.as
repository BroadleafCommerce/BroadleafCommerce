////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

import flash.display.DisplayObject;
import flash.events.Event;
import mx.events.FlexEvent;

[ExcludeClass]

/**
 *  @private
 */
public class FlexApplicationBootstrap extends FlexModuleFactory
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
	public function FlexApplicationBootstrap()
    {
        // Register for "ready" first, because we may already be ready.
		addEventListener("ready", readyHandler);
		
		super();
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
    public function readyHandler(event:Event):void
    {
        removeEventListener("ready", readyHandler);
        
		var o:Object = create();
        
		if (o is DisplayObject)
		{
            addChild(DisplayObject(o));
		    o.dispatchEvent(new FlexEvent(FlexEvent.APPLICATION_COMPLETE));
		}

    }
}

}
