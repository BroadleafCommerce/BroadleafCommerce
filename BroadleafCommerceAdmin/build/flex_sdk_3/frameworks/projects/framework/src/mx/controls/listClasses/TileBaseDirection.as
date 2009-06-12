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

/**
 *  Values for the <code>direction</code> property of the TileList component.
 *
 *  @see mx.controls.listClasses.TileBase#direction
 */
public final class TileBaseDirection
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Arrange children horizontally.
	 *  For controls, such as TileList, that arrange children in
	 *  two dimensions, arrange the children by filling up a row 
	 *  before going on to the next row.
	 */
	public static const HORIZONTAL:String = "horizontal";
	
	/**
	 *  Arrange chidren vertically.
	 *  For controls, such as TileList, that arrange children in
	 *  two dimensions, arrange the children by filling up a column
	 *  before going on to the next column.
	 */
	public static const VERTICAL:String = "vertical";
}

}
