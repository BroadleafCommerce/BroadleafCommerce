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

[RemoteClass(alias='flex.management.jmx.MBeanParameterInfo')]

/**
 * Client representation of metadata for a MBean operation parameter.
 */
public class MBeanParameterInfo extends MBeanFeatureInfo 
{
    /**
     *  Creates a new instance of an empty MBeanParameterInfo.
     */
	public function MBeanParameterInfo()
	{
		super();
	}
	
	/**
	 * The data type of the operation parameter.
	 */
	public var type:String;

}

}