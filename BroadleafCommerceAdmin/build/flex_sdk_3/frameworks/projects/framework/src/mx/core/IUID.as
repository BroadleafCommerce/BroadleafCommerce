////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The IUID interface defines the interface for objects that must have 
 *  Unique Identifiers (UIDs) to uniquely identify the object.
 *  UIDs do not need to be universally unique for most uses in Flex.
 *  One exception is for messages send by data services.
 */
public interface IUID
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  uid
	//----------------------------------
	
	/**
	 *  The unique identifier for this object.
     */
    function get uid():String;
    
    /**
     *  @private
     */
    function set uid(value:String):void;
}

}
