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

package mx.rpc.soap
{

[ExcludeClass]

/**
 * A context for the result of an SOAP based Remote Procedure Call.
 * @private
 */
public class SOAPResult
{
    public var headers:Array;
    public var isFault:Boolean;
    public var result:*;

    public function SOAPResult()
    {
        super();
    }
}

}
