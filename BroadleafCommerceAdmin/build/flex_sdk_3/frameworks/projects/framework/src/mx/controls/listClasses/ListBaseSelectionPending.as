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

package mx.controls.listClasses
{

import mx.collections.CursorBookmark;

[ExcludeClass]

/**
 *  @private
 *  The object that we use to store seek data
 *  that was interrupted by an ItemPendingError.
 *  Used when trying to match a selectedIndex to a selectedItem
 */
public class ListBaseSelectionPending
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function ListBaseSelectionPending(incrementing:Boolean, index:int,
											 stopData:Object,
											 transition:Boolean,
											 placeHolder:CursorBookmark,
											 bookmark:CursorBookmark,
											 offset:int)
	{
		super();

		this.incrementing = incrementing;
		this.index = index;
		this.stopData = stopData;
		this.transition = transition;
		this.placeHolder = placeHolder;
		this.bookmark = bookmark;
		this.offset = offset;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  bookmark
	//----------------------------------

	/**
	 *  The bookmark we have to seek to
	 */
	public var bookmark:CursorBookmark;

	//----------------------------------
	//  incrementing
	//----------------------------------

	/**
	 *  True if we moveNext(), false if we movePrevious()
	 */
	public var incrementing:Boolean;

	//----------------------------------
	//  index
	//----------------------------------

	/**
	 *  The index into the iterator when we hit the page fault
	 */
	public var index:int;

	//----------------------------------
	//  offset
	//----------------------------------

	/**
	 *  The offset from the bookmark we have to seek to
	 */
	public var offset:int;

	//----------------------------------
	//  placeHolder
	//----------------------------------

	/**
	 *  The bookmark we have to restore after we're done
	 */
	public var placeHolder:CursorBookmark;

	//----------------------------------
	//  stopData
	//----------------------------------

	/**
	 *  The data of the current item, which is the thing we are looking for.
	 */
	public var stopData:Object;

	//----------------------------------
	//  transition
	//----------------------------------

	/**
	 *  Whether to tween in the visuals
	 */
	public var transition:Boolean;
}

}
