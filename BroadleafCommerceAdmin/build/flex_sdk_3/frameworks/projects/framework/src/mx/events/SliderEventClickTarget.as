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
 *  The SliderEventClickTarget class defines the constants for the values of 
 *  the <code>clickTarget</code> property of the SliderEvent class.
 *
 *  @see mx.events.SliderEvent
 */
public final class SliderEventClickTarget
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Specifies that the Slider's track was clicked.
	 */
	public static const TRACK:String = "track";
	
	/**
	 *  Specifies that the Slider's thumb was clicked.
	 */
	public static const THUMB:String = "thumb";
}

}
