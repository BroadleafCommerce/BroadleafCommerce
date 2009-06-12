////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.channels.amfx
{

[ExcludeClass]

/**
 * A simple context to hold the result of an AMFX request.
 * @private
 */
public class AMFXResult
{
    public var version:uint;
    public var headers:Array;
    public var result:Object;

    public function AMFXResult()
    {
        super();
    }
}

}
