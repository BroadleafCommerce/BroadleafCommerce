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

package mx.rpc.soap
{

import mx.rpc.wsdl.WSDLOperation;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.IXMLEncoder;

[ExcludeClass]

/**
 * An ISOAPEncoder is used to create SOAP 1.1 formatted requests for a web
 * service operation. A WSDLOperation provides the definition of how a SOAP
 * request should be formatted and therefore must be set before a call is made to
 * encode().
 * 
 */
public interface ISOAPEncoder extends IXMLEncoder
{
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * Determines whether the encoder should ignore whitespace when
     * constructing an XML representation of a SOAP request.
     * The default should be <code>true</code> and thus whitespace not preserved.
     * If an XML Schema type definition specifies a <code>whiteSpace</code>
     * restriction set to <code>preserve</code> then ignoreWhitespace must
     * first be set to false. Conversely, if a type <code>whiteSpace</code>
     * restriction is set to <code>replace</code> or <code>collapse</code> then
     * that setting will be honored even if ignoreWhitespace is set to <code>false</code>.
     */
    function get ignoreWhitespace():Boolean;
    function set ignoreWhitespace(value:Boolean):void;

    /**
     * A WSDLOperation defines the SOAP binding styles and specifies how to
     * encode a SOAP request.
     */
    function get wsdlOperation():WSDLOperation;
    function set wsdlOperation(value:WSDLOperation):void;

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a SOAP-encoded request to an operation from the given input
     * parameters and headers.
     */
    function encodeRequest(args:* = null, headers:Array = null):XML;
}

}