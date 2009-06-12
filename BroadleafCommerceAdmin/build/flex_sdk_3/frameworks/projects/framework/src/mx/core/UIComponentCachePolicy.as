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
 *  The ContainerCreationPolicy class defines the constant values
 *  for the <code>cachePolicy</code> property of the UIComponent class.
 *
 *  @see mx.core.UIComponent#cachePolicy
 */
public final class UIComponentCachePolicy
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  Specifies that the Flex framework should use heuristics
	 *  to decide whether to cache the object as a bitmap.
     */
    public static const AUTO:String = "auto";
    
	/**
     *  Specifies that the Flex framework should never attempt
	 *  to cache the object as a bitmap.
     */
    public static const OFF:String = "off";
    
	/**
     *  Specifies that the Flex framework should always cache
	 *  the object as a bitmap.
     */
    public static const ON:String = "on";
}

}
