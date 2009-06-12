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
import mx.rpc.xml.SchemaManager;

[ExcludeClass]

/**
 * A <code>WSDLOperation</code> describes both the interface for messages being
 * sent to and from an operation and the SOAP encoding style for operation
 * binding.
 * 
 * @private
 */
public class WSDLOperation
{    
    /**
     * Constructor.
     */
    public function WSDLOperation(name:String)
    {
        super();
        _name = name;
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     * Describes the parts and encoding for the input message of this
     * operation.
     */
    public var inputMessage:WSDLMessage;

    /**
     * Used to map prefixes to namespace URIs.
     */
    public var namespaces:Object;

    /**
     * Describes the parts and encoding for the output message of this
     * operation.
     */
    public var outputMessage:WSDLMessage;

    [Inspectable(enumeration="document,rpc", category="General")]
    /**
     * Represents the style attribute for an operation's SOAP binding which
     * indicates whether an operation is RPC-oriented (messages containing
     * parameters and return values) or document-oriented (message containing
     * document(s)).
     * <p>
     * If a style is not specified for an operation the default will be
     * determined from the parent WSDLBinding's style attribute.
     * </p>
     */
    public var style:String;


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * The name of this WSDL operation.
     */
    public function get name():String
    {
        return _name;
    }

    /**
     * A SchemaManager handles the XML Schema types section of a WSDL and
     * is used to locate a type definition by QName.
     */
    public function get schemaManager():SchemaManager
    {
        return _schemaManager;
    }
    
    public function set schemaManager(manager:SchemaManager):void
    {
        _schemaManager = manager;
    }

    [Inspectable(defaultValue="", category="General")]
    /**
     * Specifies the value for the SOAPAction HTTPHeader used for the HTTP
     * binding of SOAP.
     * <p>
     * Although technically the soapAction must always be specified, a default
     * value of the empty String will be returned in the event that it is
     * not set.
     * </p>
     */    
    public function get soapAction():String
    {
        if (_soapAction == null)
            _soapAction = "";

        return _soapAction;
    }

    public function set soapAction(value:String):void
    {
        _soapAction = value;
    }

    /**
     * The constants for the version of SOAP used to encode messages
     * to and from this operation.
     */
    public function get soapConstants():SOAPConstants
    {
        if (_soapConstants == null)
            _soapConstants = SOAPConstants.getConstants(null);

        return _soapConstants;
    }

    public function set soapConstants(value:SOAPConstants):void
    {
        _soapConstants = value;
    }

    /**
     * The constants for the version of WSDL used to define this operation.
     */
    public function get wsdlConstants():WSDLConstants
    {
        if (_wsdlConstants == null)
            _wsdlConstants = WSDLConstants.getConstants(null);

        return _wsdlConstants;
    }

    public function set wsdlConstants(value:WSDLConstants):void
    {
        _wsdlConstants = value;
    }
    
  
    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Registers the encoding and type information for a potential fault for
     * this operation.
     * @private
     */
    public function addFault(fault:WSDLMessage):void
    {
        if (_faultsMap == null)
            _faultsMap = {};

        _faultsMap[fault.name] = fault;
    }

    /**
     * Locates a fault by name.
     * @private
     */
    public function getFault(name:String):WSDLMessage
    {
        var fault:WSDLMessage;

        if (_faultsMap != null)
            fault = _faultsMap[name];

        return fault;
    }

    private var _faultsMap:Object;
    private var _name:String;
    private var _soapAction:String;
    private var _soapConstants:SOAPConstants;
    private var _schemaManager:SchemaManager;
    private var _wsdlConstants:WSDLConstants;
}

}