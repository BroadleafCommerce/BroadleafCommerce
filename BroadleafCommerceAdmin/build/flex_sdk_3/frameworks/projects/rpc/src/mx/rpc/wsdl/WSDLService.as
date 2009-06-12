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
 * A service groups a set of related ports together for a given WSDL.
 * 
 * @private
 */
public class WSDLService
{
    public function WSDLService(name:String)
    {
        super();
        _name = name;
        _ports = {};
    }


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    public function get defaultPort():WSDLPort
    {
        return _defaultPort;
    }

    /**
     * The unique name of this service.
     */
    public function get name():String
    {
        return _name;
    }

    /**
     * Provides access to this service's map of ports.
     */
    public function get ports():Object
    {
        return _ports;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Registers a port with this service.
     */
    public function addPort(port:WSDLPort):void
    {
        _ports[port.name] = port;
        if (_defaultPort == null)
            _defaultPort = port;
    }    

    /**
     * Retrieves a port by name.
     */
    public function getPort(name:String):WSDLPort
    {
        return _ports[name];
    }

    
    private var _defaultPort:WSDLPort;
    private var _name:String;
    private var _ports:Object;
}

}
