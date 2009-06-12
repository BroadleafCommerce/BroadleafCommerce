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

package mx.rpc.xml
{

[ExcludeClass]

/**
 * This internal utility class is used by XMLDecoder. The class is basically
 * a dynamic version any simple type such as Number, String, Boolean so 
 * that other properties can be attached to it as annotations.
 *
 * @private
 */
internal dynamic class SimpleContent
{
    // FIXME: Should this be in a custom namespace?
    public var value:*;

    public function SimpleContent(val:*)
    {
        super();
        value = val;
    }

    public function toString():String
    {
        var object:Object = value as Object;
        return object == null ? null : object.toString();
    }

    public function valueOf():Object
    {
        return value as Object;
    }
}

}