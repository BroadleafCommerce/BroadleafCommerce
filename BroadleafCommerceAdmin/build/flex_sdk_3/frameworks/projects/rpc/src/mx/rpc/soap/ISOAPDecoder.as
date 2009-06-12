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
import mx.rpc.xml.IXMLDecoder;
import mx.rpc.xml.SchemaConstants;

[ExcludeClass]

/**
 * Decodes the SOAP response for a particular operation.
 * 
 */
public interface ISOAPDecoder extends IXMLDecoder
{
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * Determines whether or not a single or empty return value for an output
     * message part that is defined as an array should be returned as an array
     * containing one (or zero, respectively) elements. This is applicable for
     * document/literal "wrapped" web services, where one or more of the elements
     * that represent individual message parts in the "wrapper" sequence could
     * have the maxOccurs attribute set with a value greater than 1. This is a
     * hint that the corresponding part should be treated as an array even if
     * the response contains zero or one values for that part. Setting
     * forcePartArrays to true will always create an array for parts defined in
     * this manner, regardless of the number of values returned. Leaving
     * forcePartArrays as false will only create arrays if two or more elements
     * are returned.
     */
    function get forcePartArrays():Boolean;
    function set forcePartArrays(value:Boolean):void;

    /**
     * Determines whether the decoder should ignore whitespace when processing
     * the XML of a SOAP-encoded response. The default should be
     * <code>true</code> and thus whitespace not preserved. If an XML Schema
     * type definition specifies a <code>whiteSpace</code> restriction set to
     * <code>preserve</code> then ignoreWhitespace must first be set to <code>false</code>.
     * Conversely, if a type <code>whiteSpace</code> restriction is set to
     * <code>replace</code> or <code>collapse</code> then that setting is
     * be honored even if ignoreWhitespace is set to <code>false</code>.
     */
    function get ignoreWhitespace():Boolean;
    function set ignoreWhitespace(value:Boolean):void;

    /**
     * Determines how the SOAP-encoded XML result is decoded. A value of
     * <code>object</code> specifies that the XML is decoded into an
     * object structure as specified in the WSDL document. A value of
     * <code>xml</code> specifies that the XML is left as XMLNodes. A
     * value of <code>e4x</code> specifies that the XML will be accessible
     * using ECMAScript for XML (E4X) expressions.
     */
    function get resultFormat():String;
    function set resultFormat(value:String):void;

    /**
     * Determines how the SOAP-encoded headers are decoded. A value of
     * <code>object</code> specifies that each header XML node is decoded
     * into a SOAPHeader object, and its <code>content</code> property is
     * an object structure as specified in the WSDL document. A value of
     * <code>xml</code> specifies that the XML is left as XMLNodes. A
     * value of <code>e4x</code> specifies that the XML should be accessible
     * using ECMAScript for XML (E4X) expressions.
     */
    function get headerFormat():String;
    function set headerFormat(value:String):void;
    
    /**
     * Determines the type of the default result object for calls to web services
     * that define multiple parts in the output message. A value of "object"
     * specifies that the lastResult object will be an Object with named properties
     * corresponding to the individual output parts. A value of "array" would
     * make the lastResult an array, where part values are pushed in the order
     * they occur in the body of the SOAP message. The default value for document-
     * literal web services is "object". The default for rpc services is "array".
     * The multiplePartsFormat property is applicable only when
     * resultFormat="object" and ignored otherwise.
     */
    function get multiplePartsFormat():String;
    function set multiplePartsFormat(value:String):void;

    /**
     * A WSDLOperation defines the SOAP binding styles and specifies how to
     * decode a SOAP result.
     */
    function get wsdlOperation():WSDLOperation;
    function set wsdlOperation(value:WSDLOperation):void;

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Decodes a SOAP response into a result and headers.
     */
    function decodeResponse(response:*):SOAPResult;
}

}