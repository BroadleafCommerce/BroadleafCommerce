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
 * A portType lists a set of named operations and defines abstract interface or
 * "messages" used to interoperate with each operation.
 * 
 * @private
 */ 
public class WSDLPortType
{
    public function WSDLPortType(name:String)
    {
        super();
        _name = name;
        _operations = {};
    }


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * The unique name for this portType.
     */
    public function get name():String
    {
        return _name;
    }

    public function operations():Object
    {
        return _operations;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    public function addOperation(operation:WSDLOperation):void
    {
        _operations[operation.name] = operation;
    }

    public function getOperation(name:String):WSDLOperation
    {
        return _operations[name];
    }

    private var _name:String;
    private var _operations:Object;
}

}
