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

[ExcludeClass]

/**
 * From the WSDL 1.1 specification:
 * 
 * <blockquote>
 * Messages consist of one or more logical parts. Each part is associated with
 * a type from some type system using a message-typing attribute. The set of
 * message-typing attributes is extensible.
 * </blockquote>
 * 
 * @private
 */
public class WSDLMessage
{
    public function WSDLMessage(name:String = null)
    {
        super();

        this.name = name;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     * The SOAP encoding extensions for this message.
     */
    public var encoding:WSDLEncoding;

    /**
     * Whether this message is using .NET wrapped style for document literal
     * requests.
     */
    public var isWrapped:Boolean;

    /**
     * The unique name of this message.
     */
    public var name:String;

    /**
     * An Array of message parts which describe the parameters of this
     * message and the order in which they were specified. By default each of
     * these parameters appear in a SOAP Envelope's Body section.
     */
    public var parts:Array;

    /**
     * The QName of the element wrapper if the message is to be encoded using
     * .NET document-literal wrapped style.
     */
    public var wrappedQName:QName;


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Add a part to this message. The parts Array tracks the order in which
     * parts were added; an internal map allows a part to be located by name.
     * @see #getPart(String)
     */
    public function addPart(part:WSDLMessagePart):void
    {
        if (_partsMap == null)
            _partsMap = {};

        if (parts == null)
            parts = [];

        _partsMap[part.name.localName] = part;
        parts.push(part);
    }

    /**
     * Locates a message part by name.
     */
    public function getPart(name:String):WSDLMessagePart
    {
        var part:WSDLMessagePart;

        if (_partsMap != null)
            part = _partsMap[name];

        return part;
    }

    /**
     * @private
     */
    public function addHeader(header:WSDLMessage):void
    {
        if (_headersMap == null)
            _headersMap = {};

        _headersMap[header.name] = header;
    }

    /**
     * @private
     */
    public function getHeader(name:String):WSDLMessage
    {
        var header:WSDLMessage;

        if (_headersMap != null)
            header = _headersMap[name];

        return header;
    }

    /**
     * @private
     */
    public function addHeaderFault(headerFault:WSDLMessage):void
    {
        if (_headerFaultsMap == null)
            _headerFaultsMap = {};

        _headerFaultsMap[headerFault.name] = headerFault;
    }

    /**
     * @private
     */
    public function getHeaderFault(name:String):WSDLMessage
    {
        var headerFault:WSDLMessage;

        if (_headerFaultsMap != null)
            headerFault = _headerFaultsMap[name];

        return headerFault;
    }

    private var _partsMap:Object;
    private var _headersMap:Object;
    private var _headerFaultsMap:Object;
}

}