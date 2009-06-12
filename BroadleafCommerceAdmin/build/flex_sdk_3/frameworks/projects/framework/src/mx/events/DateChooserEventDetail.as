////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

/**
 *  Constants that define the  values of the <code>detail</code> property
 *  of a DateChooserEvent object.
 *
 *  @see mx.events.DateChooserEvent
 */
public final class DateChooserEventDetail
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Indicates that the user scrolled the calendar to the next month.
	 */
	public static const NEXT_MONTH:String = "nextMonth";

	/**
	 *  Indicates that the user scrolled the calendar to the next year.
	 */
	public static const NEXT_YEAR:String = "nextYear";

	/**
	 *  Indicates that the user scrolled the calendar to the previous month.
	 */
	public static const PREVIOUS_MONTH:String = "previousMonth";

	/**
	 *  Indicates that the user scrolled the calendar to the previous year.
	 */
	public static const PREVIOUS_YEAR:String = "previousYear";
}

}
