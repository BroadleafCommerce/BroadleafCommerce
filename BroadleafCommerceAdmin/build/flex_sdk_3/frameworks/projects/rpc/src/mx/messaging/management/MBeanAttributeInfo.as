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

[RemoteClass(alias='flex.management.jmx.MBeanAttributeInfo')]

/**
 * Client representation of metadata for a MBean attribute.
 */
public class MBeanAttributeInfo extends MBeanFeatureInfo 
{
    /**
     *  Creates a new instance of an empty MBeanAttributeInfo.
     */
	public function MBeanAttributeInfo()
	{
		super();
	}
		
	/**
	 * The data type of the attribute.
	 */
	public var type:String;
	
	/**
	 * Indicates if the attribute is readable.
	 */
	public var readable:Boolean;
	
	/**
	 * Indicates if the attribute is writable.
	 */
	public var writable:Boolean;
	
	/**
	 * Indicates if the server-side getter for the attribute has an 'is' prefix.
	 */
	public var isIs:Boolean;
	
}

}