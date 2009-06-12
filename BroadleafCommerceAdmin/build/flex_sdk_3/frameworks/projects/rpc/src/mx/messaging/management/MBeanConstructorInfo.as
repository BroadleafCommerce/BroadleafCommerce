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

[RemoteClass(alias='flex.management.jmx.MBeanConstructorInfo')]    
    
/**
 * Client representation of metadata for a MBean constructor.
 */
public class MBeanConstructorInfo extends MBeanFeatureInfo 
{
    /**
     *  Creates a new instance of an empty MBeanConstructorInfo.
     */
    public function MBeanConstructorInfo()
	{
		super();
	}
	
	/**
	 * The parameter data types that make up the constructor signature.
	 */
	public var signature:Array;
    
}

}