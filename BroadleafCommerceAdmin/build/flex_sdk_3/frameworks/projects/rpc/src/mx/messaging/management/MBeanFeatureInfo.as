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

[RemoteClass(alias='flex.management.jmx.MBeanFeatureInfo')]

/**
 * Client representation of metadata for a MBean feature.
 */
public class MBeanFeatureInfo 
{
    /**
     *  Creates a new instance of an empty MBeanFeatureInfo.
     */
	public function MBeanFeatureInfo()
	{
		super();
	}
	
	/**
	 * The name of the MBean feature.
	 */
	public var name:String;
	
	/**
	 * The description of the MBean feature.
	 */
	public var description:String;

	/**
     *  Returns a string representation of the feature info.
     * 
     *  @return String representation of the feature info.
     */
	public function toString():String
    {
        return ObjectUtil.toString(this);
    }

}

}