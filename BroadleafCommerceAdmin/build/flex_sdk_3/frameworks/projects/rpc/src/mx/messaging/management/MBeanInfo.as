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

[RemoteClass(alias='flex.management.jmx.MBeanInfo')]

/**
 * Client representation of metadata for a MBean.
 */
public class MBeanInfo 
{
    /**
     *  Creates a new instance of an empty MBeanInfo.
     */
	public function MBeanInfo()
	{
		super();
	}
	
	/**
	 * The class name for the MBean.
	 */
	public var className:String;
	
	/**
	 * The description for the MBean.
	 */
	public var description:String;
	
	/**
	 * The attributes exposed by the MBean.
	 */
	public var attributes:Array;
	
	/**
	 * The constructors exposed by the MBean.
	 */
	public var constructors:Array;
	
	/**
	 * The operations provided by the MBean.
	 */
	public var operations:Array;
	
	/**
     *  Returns a string representation of the MBean info.
     * 
     *  @return String representation of the MBean info.
     */
	public function toString():String
    {
        return ObjectUtil.toString(this);
    }

}

}