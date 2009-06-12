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

package mx.rpc.soap.types
{

import mx.rpc.soap.SOAPDecoder;
import mx.rpc.soap.SOAPEncoder;

[ExcludeClass]

/**
 * Implementations handle encoding and decoding between custom SOAP types and 
 * ActionScript.
 * 
 * @private
 */
public interface ICustomSOAPType
{
    function encode(encoder:SOAPEncoder, parent:XML, name:QName, value:*, restriction:XML = null):void;

    function decode(decoder:SOAPDecoder, parent:*, name:*, value:*, restriction:XML = null):void;
}

}