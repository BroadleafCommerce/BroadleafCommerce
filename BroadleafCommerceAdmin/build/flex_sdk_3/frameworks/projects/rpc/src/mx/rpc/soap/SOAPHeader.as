////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.soap
{

/**
 * You use a SOAPHeader to specify the headers that need 
 * to be added to a SOAP envelope of a WebService Operation request.
 */
public class SOAPHeader
{
    /**
     * Constructs a new SOAPHeader. The qualified name and content for the
     * SOAP header are required.
     *
     * @param qname The qualified name of the SOAP header.
     *
     * @param content The content to send for the header value.
     */
    public function SOAPHeader(qname:QName, content:Object)
    {
        super();
        this.qname = qname;
        this.content = content;
    }

    /**
     * The content to send for the header value. 
     * If you provide an XML or flash.xml.XMLNode instance for the header, it is
     * used directly as pre-encoded content and appended as a child to the soap:header element.
     * Otherwise, you can provide the value as a String or Number, etc. and the underlying SOAP encoder
     * attempts to encode the value correctly based on the QName provided in the SOAPHeader
     * (with the last resort being xsd:anyType if a type definition is not present).     
     */
    public var content:Object;

    /**
     * The qualified name of the SOAP header.
     */
    public var qname:QName;

    /**
     * Internal use only. The internal datatype as determined from
     * the WSDL schema that should be used to encode the content.
     * @private
     */
    public var xmlType:QName;

    /**
     * Specifies whether the header must be understood by the endpoint. If
     * the header is handled but must be understood the endpoint should
     * return a SOAP fault.
     */
    public var mustUnderstand:Boolean;
    
    /**
     * Specifies the URI for the role that this header is intended in a 
     * potential chain of endpoints processing a SOAP request. If defined, 
     * this value is used to specify the <code>actor</code> for the SOAP 
     * header.
     */
    public var role:String;

    /**
     * @private
     */
    public function toString():String
    {
        return qname + ", " + content + ", " + role; 
    }
}

}
