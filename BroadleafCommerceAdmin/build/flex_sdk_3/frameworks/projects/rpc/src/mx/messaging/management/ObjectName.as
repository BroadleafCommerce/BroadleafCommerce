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

[RemoteClass(alias='flex.management.jmx.ObjectName')]

/**
 * Client representation of the name for server-side management controls.
*/
public class ObjectName 
{
    /**
     *  Creates a new instance of an empty ObjectName.
     */
	public function ObjectName()
	{
		super();
	}

	/**
	 * The canonical form of the name; a string representation with 
	 * the properties sorted in lexical order.
	 */
	public var canonicalName:String;
	
	/**
	 * A string representation of the list of key properties, with the key properties sorted in lexical order.
	 */
	public var canonicalKeyPropertyListString:String;
	
	/**
	 * Indicates if the object name is a pattern.
	 */
	public var pattern:Boolean;
	
	/**
	 * Indicates if the object name is a pattern on the domain part.
	 */
	public var domainPattern:Boolean;
	
	/**
	 * Indicates if the object name is a pattern on the key properties.
	 */
	public var propertyPattern:Boolean;
	
	/**
	 * The domain part.
	 */
	public var domain:String;
	
	/**
	 * The key properties as an Object, keyed by property name.
	 */
	public var keyPropertyList:Object;
	
	/**
	 * A string representation of the list of key properties.
	 */
	public var keyPropertyListString:String;
	
	/**
	 * Returns the value associated with the specified property key.
	 *
	 * @param The property key.
	 */
	public function getKeyProperty(property:String):Object
	{
		if (keyPropertyList != null && keyPropertyList.hasOwnProperty(property))
		{
			return keyPropertyList[property];
		}
		return null;
	}
	
	/**
     *  This method will return a string representation of the object name.
     * 
     *  @return String representation of the object name.
     */
	public function toString():String
    {
    	if (canonicalName)
    	{
    		return canonicalName;
    	}
    	else
    	{
	        return ObjectUtil.toString(this);
	    }
    }

}

}