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

package mx.controls
{

/**
 *  The ProgressBarMode class defines the values for the <code>mode</code> property
 *  of the ProgressBar class.
 *
 *  @see mx.controls.ProgressBar
 */
public final class ProgressBarMode
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  The control specified by the <code>source</code> property must
	 *  dispatch <code>progress</code> and <code>completed</code> events. 
	 *  The ProgressBar uses these events to update its status.
	 */
	public static const EVENT:String = "event";

	/**
	 *  You manually update the ProgressBar status. In this mode, you
	 *  specify the <code>maximum</code> and <code>minimum</code>
	 *  properties and use the <code>setProgress()</code> method
	 *  to specify the status. This mode is often used when the
	 *  <code>indeterminate</code> property is <code>true</code>.
	 */
	public static const MANUAL:String = "manual";

	/**
	 *  The <code>source</code> property must specify an object that
	 *  exposes the <code>getBytesLoaded()</code> and
	 *  <code>getBytesTotal()</code> methods.  The ProgressBar control
	 *  calls these methods to update its status.
	 */
	public static const POLLED:String = "polled";
}

}