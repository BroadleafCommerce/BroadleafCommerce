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
 * Parts are a flexible mechanism for describing the content of a message.
 * 
 * @private
 */
public class WSDLMessagePart
{
    public function WSDLMessagePart(name:QName, element:QName = null, type:QName = null)
    {
        super();

        _name = name;
        this.type = type;
        this.element = element;
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    public var type:QName;

    public var element:QName;

    public var definition:XML;

    public var optional:Boolean;

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * The unique name of this message part.
     */
    public function get name():QName
    {
        return _name;
    }

    private var _name:QName;
}

}