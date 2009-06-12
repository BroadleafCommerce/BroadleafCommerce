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
 * A port defines an individual endpoint by specifying a single address for
 * a binding.
 * 
 * @private
 */
public class WSDLPort
{
    public function WSDLPort(name:String, service:WSDLService)
    {
        super();
        _name = name;
        _service = service;
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * Represents the binding which defines the message format and protocol
     * used to interoperate with operations for this port.
     */
    public function get binding():WSDLBinding
    {
        return _binding;
    }

    public function set binding(value:WSDLBinding):void
    {
        _binding = value;
    }

    /**
     * The endpointURI is the SOAP bound address defined for this port.
     */
    public function get endpointURI():String
    {
        return _endpointURI;
    }

    public function set endpointURI(value:String):void
    {
        _endpointURI = value;
    }

    /**
     * The unique name of this port.
     */
    public function get name():String
    {
        return _name;
    }
    
    /**
     * The WSDL service to which this port belongs.
     */
    public function get service():WSDLService
    {
        return _service;
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
        return "WSDLPort name=" + _name
            + ", endpointURI=" + _endpointURI
            + ", binding=" + _binding;
    }

    private var _binding:WSDLBinding
    private var _endpointURI:String;
    private var _name:String;
    private var _service:WSDLService;
}

}
