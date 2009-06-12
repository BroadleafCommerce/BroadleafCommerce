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

package mx.controls.videoClasses 
{

[ExcludeClass]

/**
 *  @private
 *  Holds client-side functions for remote procedure calls (RPCs) from
 *  the FCS during reconnection.
 *  One of these objects is created and passed to the
 *  <code>NetConnection.client</code> property.
 */
public class NCManagerReconnectClient
{
	include "../../core/Version.as";

    public var owner:NCManager;

    public function NCManagerReconnectClient(owner:NCManager = null)
    {
		super();

        this.owner = owner;
    }

    // This is defined just to work around bug 121673
    public function onBWCheck(... rest):uint
    {
        return ++owner.payload;
    }

    public function onBWDone(... rest):void
    {
        owner.onReconnected();
    }
}

}