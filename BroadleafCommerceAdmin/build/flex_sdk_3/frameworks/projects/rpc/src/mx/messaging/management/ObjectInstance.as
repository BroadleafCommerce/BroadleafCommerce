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

[RemoteClass(alias='flex.management.jmx.ObjectInstance')]

/**
 * Client representation of an object name instance for server-side management controls.
 */
public class ObjectInstance
{
    /**
     *  Creates a new instance of an empty ObjectInstance.
     */
    public function ObjectInstance()
    {
        super();
    }

    /**
     * The object name.
     */
    public var objectName:ObjectName;

    /**
     * The class name.
     */
    public var className:String;

    /**
     *  Returns a string representation of the object name instance.
     * 
     *  @return String representation of the object name instance.
     */
    public function toString():String
    {
        return ObjectUtil.toString(this);
    }
}

}