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

package mx.containers
{

/**
 *  The TileDirection class defines the constant values for the
 *  <code>direction</code> property of the Tile container.
 *
 *  @see mx.containers.Tile
 */
public final class TileDirection
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
	 *  Specifies that the children of the Tile container are laid out
	 *  horizontally; that is, starting with the first row.
     */
    public static const HORIZONTAL:String = "horizontal";
    
    /**
	 *  Specifies that the children of the Tile container are laid out
	 *  vertically; that is, starting with the first column.
     */
    public static const VERTICAL:String = "vertical";
}

}
