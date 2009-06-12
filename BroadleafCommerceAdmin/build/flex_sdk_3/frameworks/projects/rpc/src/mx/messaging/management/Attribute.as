////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.management
{

import mx.utils.ObjectUtil;

[RemoteClass(alias='flex.management.jmx.Attribute')]

/**
 * Client representation of a MBean attribute.
 */
public class Attribute
{
    /**
     *  Creates a new instance of an empty Attribute.
     */
    public function Attribute()
    {
        super();
    }

    /**
     * The attribute name.
     */
    public var name:String;

    /**
     * The attribute value.
     */
    public var value:Object;
    
    /**
     *  Returns a string representation of the attribute.
     * 
     *  @return String representation of the attribute.
     */
    public function toString():String
    {
        return ObjectUtil.toString(this);
    }
}

}