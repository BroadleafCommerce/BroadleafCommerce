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

package mx.events
{

/**
 *  The PropertyChangeEventKind class defines the constant values 
 *  for the <code>kind</code> property of the PropertyChangeEvent class.
 * 
 *  @see mx.events.PropertyChangeEvent
 */
public final class PropertyChangeEventKind
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
	 *  Indicates that the value of the property changed.
	 */
	public static const UPDATE:String = "update";

    /**
	 *  Indicates that the property was deleted from the object.
	 */
	public static const DELETE:String = "delete";
}

}
