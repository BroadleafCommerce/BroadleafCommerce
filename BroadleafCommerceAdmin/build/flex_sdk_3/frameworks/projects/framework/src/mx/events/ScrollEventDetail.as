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
 *  Constants for the values of the <code>detail</code> property
 *  of a ScrollEvent.
 *
 *  @see mx.events.ScrollEvent
 */
public final class ScrollEventDetail
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Indicates that the scroll bar is at the bottom of its scrolling range.
	 */
	public static const AT_BOTTOM:String = "atBottom";

	/**
	 *  Indicates that the scroll bar is at the left of its scrolling range.
	 */
	public static const AT_LEFT:String = "atLeft";

	/**
	 *  Indicates that the scroll bar is at the right of its scrolling range.
	 */
	public static const AT_RIGHT:String = "atRight";

	/**
	 *  Indicates that the scroll bar is at the top of its scrolling range.
	 */
	public static const AT_TOP:String = "atTop";

	/**
	 *  Indicates that the scroll bar has moved down by one line.
	 */
	public static const LINE_DOWN:String = "lineDown";

	/**
	 *  Indicates that the scroll bar has moved left by one line.
	 */
	public static const LINE_LEFT:String = "lineLeft";

	/**
	 *  Indicates that the scroll bar has moved right by one line.
	 */
	public static const LINE_RIGHT:String = "lineRight";

	/**
	 *  Indicates that the scroll bar has moved up by one line.
	 */
	public static const LINE_UP:String = "lineUp";

	/**
	 *  Indicates that the scroll bar has moved down by one page.
	 */
	public static const PAGE_DOWN:String = "pageDown";

	/**
	 *  Indicates that the scroll bar has moved left by one page.
	 */
	public static const PAGE_LEFT:String = "pageLeft";

	/**
	 *  Indicates that the scroll bar has moved right by one page.
	 */
	public static const PAGE_RIGHT:String = "pageRight";

	/**
	 *  Indicates that the scroll bar has moved up by one page.
	 */
	public static const PAGE_UP:String = "pageUp";

	/**
	 *  Indicates that the scroll bar thumb has stopped moving.
	 */
	public static const THUMB_POSITION:String = "thumbPosition";

	/**
	 *  Indicates that the scroll bar thumb is moving.
	 */
	public static const THUMB_TRACK:String = "thumbTrack";
}

}
