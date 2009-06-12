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

/**
 * A binding defines the message format and protocol for messages sent
 * to and from operations as defined by a particular portType.
 * <p>
 * Currently only SOAP binding is supported for WSDL.
 * </p>
 */
public class WSDLBinding
{
   /**
    * Creates a new WSDLBinding. Currently, only SOAP binding is
    * supported for WSDL.
    *
    * @param name The unique name of this binding.
    */
    public function WSDLBinding(name:String)
    {
        super();
        _name = name;
    }


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * The unique name of this binding.
     */ 
    public function get name():String
    {
        return _name;
    }

    /**
     * The portType for this binding which provides the interface definitions
     * for the operations of this binding.
     */
    public function get portType():WSDLPortType
    {
        return _portType;
    }

    public function set portType(value:WSDLPortType):void
    {
        _portType = value;
    }

    [Inspectable(enumeration="document,rpc", defaultValue="document", category="General")]
    /**
     * Represents a SOAP binding style attribute which is the default for any
     * operation defined under this binding. The style indicates whether an
     * operation is RPC-oriented (messages containing parameters and return
     * values) or document-oriented (message containing document(s)).
     * <p>
     * The default is <code>document</code>.
     * </p>
     */
    public function get style():String
    {
        if (_style == null)
            _style = SOAPConstants.DEFAULT_OPERATION_STYLE;

        return _style;
    }

    public function set style(value:String):void
    {
        _style = value;
    }

    [Inspectable(defaultValue="http://schemas.xmlsoap.org/soap/http/", category="General")]
    /**
     * Represents a SOAP binding transport attribute which indicates the
     * URI of the transport used to send SOAP encoded messages. 
     * The default URI is http://schemas.xmlsoap.org/soap/http/ which signifies
     * SOAP over HTTP (and is currently the only transport supported).
     */
    public function get transport():String
    {
        if (_transport == null)
            _transport = WSDLConstants.SOAP_HTTP_URI;

        return _transport;
    }

    public function set transport(value:String):void
    {
        _transport = value;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    public function toString():String
    {
        return "WSDLBinding name=" + _name
            + ", style=" + _style
            + ", transport=" + _transport;
    }

    private var _name:String;
    private var _portType:WSDLPortType;
    private var _style:String;
    private var _transport:String;
}

}
