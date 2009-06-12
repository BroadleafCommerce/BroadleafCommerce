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

package mx.rpc.wsdl
{

import mx.rpc.soap.SOAPConstants;

[ExcludeClass]

/**
 * SOAP specific WSDL bindings that describe how to encode messages
 * for a given operation.
 * 
 * @private
 */
public class WSDLEncoding
{
    public function WSDLEncoding()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * Currently only SOAP 1.1 encoding is supported. The default
     * encoding style is
     * <code>"http://schemas.xmlsoap.org/soap/encoding/"</code>.
     * 
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;The value of the <code>encodingStyle</code> attribute is a list of
     * URIs, each separated by a single space. The URI's represent encodings
     * used within the message, in order from most restrictive to least
     * restrictive (exactly like the encodingStyle attribute defined in the
     * SOAP specification).
     * </p>
     */
    public function get encodingStyle():String
    {
        if (_encodingStyle == null)
            _encodingStyle = soapConstants.encodingURI;

        return _encodingStyle;
    }

    public function set encodingStyle(value:String):void
    {
        _encodingStyle = value ? value : soapConstants.encodingURI;
    }

    /**
     * The SOAP header and header fault extensions for WSDL bindings define
     * a <code>message</code> attribute to target a WSDL message that contains
     * the specified part.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;Together, the message attribute (of type QName) and the part
     * attribute (of type nmtoken) reference the message part that defines the
     * header type. The referenced message need not be the same as the message
     * that defines the SOAP Body.&quot;
     * </p>
     */
    public function get message():QName
    {
        return _message;
    }

    public function set message(value:QName):void
    {
        _message = value;
    }


    /**
     * The SOAP body extension for WSDL bindings using the rpc style may define
     * a <code>namespace</code> attribute that is used to create a qualified
     * wrapper element within the body.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;The wrapper element is named identically to the operation name and
     * its namespace is the value of the <code>namespace</code> attribute.&quot;
     * </p>
     */
    public function get namespaceURI():String
    {
        return _namespaceURI;
    }

    public function set namespaceURI(value:String):void
    {
        _namespaceURI = value;
    }

    /**
     * The SOAP body extension for WSDL bindings may define a <code>parts</code>
     * attribute to select which message parts appear in the SOAP Body. The SOAP
     * header and header fault extensions for WSDL bindings may define a
     * <code>part</code> attribute to select a single message part to appear
     * as the header content.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;The optional parts attribute of type nmtokens indicates which parts
     * appear somewhere within the SOAP Body portion of the message (other
     * parts of a message may appear in other portions of the message such as
     * when SOAP is used in conjunction with the multipart/related MIME
     * binding). If the parts attribute is omitted, then all parts defined by
     * the message are assumed to be included in the SOAP Body portion.&quot;
     * </p>
     */
    public function get parts():Array
    {
        return _parts;
    }

    /**
     * The SOAPConstants associated with this set of encoding rules
     * for the WSDL binding of a messsage for a particular operation.
     */
    public function get soapConstants():SOAPConstants
    {
        if (_soapConstants == null)
            _soapConstants = SOAPConstants.getConstants(null);

        return _soapConstants;
    }

    /**
     * <p>
     * Expected use style values are <code>literal</code> or
     * <code>encoded</code>. The default is <code>encoded</code>.
     * </p>
     * From the WSDL 1.1 specification:
     * 
     * <p>
     * &quot;The required use attribute indicates whether the message parts are
     * encoded using some encoding rules, or whether the parts define the
     * concrete schema of the message.&quot;
     * </p>
     */
    public function get useStyle():String
    {
        return _useStyle;
    }

    public function set useStyle(value:String):void
    {
        _useStyle = value;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Determines whether a part should be included when encoding this message.
     */
    public function hasPart(name:String):Boolean
    {
        if (_parts != null && _parts.length > 0)
        {
            for each (var part:String in _parts)
            {
                if (part == name)
                    return true;
            }
            return false;
        }

        // No parts defined means that we include all by default.
        return true;
    }

    /**
     * Establishes a subset of parts that should be included in the message.
     * If a subset is not defined, the default is to include all parts in a
     * message.
     */
    public function setParts(value:String):void
    {
        if (value != null && value.length > 0)
        {
            _parts = [];
            var array:Array = value.split(" ");
            for each (var part:String in array)
            {
                if (part.length > 0)
                {
                    _parts.push(part);
                }
            }
        }
        else
        {
            _parts = null;
        }
    }

    private var _encodingStyle:String = SOAPConstants.SOAP_ENCODING_URI;
    private var _message:QName;
    private var _namespaceURI:String;
    private var _parts:Array;
    private var _soapConstants:SOAPConstants; 
    private var _useStyle:String = SOAPConstants.DEFAULT_USE;
}

}