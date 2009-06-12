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

import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  Records used by list classes to keep track of what is selected.
 *  Each selected item is represented by an instance of this class. 
 *
 *  @see mx.controls.listClasses.ListBase#selectedData
 */
public class ListBaseSelectionData
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param data The data Object that is selected
	 *
	 *  @param index The index in the data provider of the selected item. (may be approximate) 
	 *
	 *  @param approximate If true, then the index property is an approximate value and not the exact value.
	 */
	public function ListBaseSelectionData(data:Object, index:int,
										  approximate:Boolean)
	{
		super();

		this.data = data;
		this.index = index;
		this.approximate = approximate;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
    
	/**
     *  @private
     *  The next ListBaseSelectionData in a linked list
	 *  of ListBaseSelectionData.
     *  ListBaseSelectionData instances are linked together and keep track
	 *  of the order in which the user selects items.
	 *  This order is reflected in selectedIndices and selectedItems.
     */
    mx_internal var nextSelectionData:ListBaseSelectionData;

    /**
     *  @private
     *  The previous ListBaseSelectionData in a linked list
	 *  of ListBaseSelectionData.
     *  ListBaseSelectionData instances are linked together and keep track
	 *  of the order in which the user selects items.
	 *  This order is reflected in selectedIndices and selectedItems.
     */
    mx_internal var prevSelectionData:ListBaseSelectionData;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  approximate
	//----------------------------------

	/**
	 *  If true, then the index property is an approximate value and not the exact value.
	 */
	public var approximate:Boolean;

	//----------------------------------
	//  data
	//----------------------------------

	/**
	 *  The data Object that is selected (selectedItem)
	 */
	public var data:Object;

	//----------------------------------
	//  index
	//----------------------------------

	/**
	 *  The index in the data provider of the selected item. (may be approximate)
	 */
	public var index:int;
}

}