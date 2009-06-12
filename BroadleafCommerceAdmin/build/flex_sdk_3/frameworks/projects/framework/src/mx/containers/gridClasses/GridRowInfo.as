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

package mx.containers.gridClasses
{

import mx.core.UIComponent;

[ExcludeClass]

/**
 *  @private
 *  Internal helper class used to exchange information between
 *  Grid and GridRow.
 */
public class GridRowInfo
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
	public function GridRowInfo()
	{
		super();

		min = 0;
		preferred = 0;
		max = UIComponent.DEFAULT_MAX_HEIGHT;
		flex = 0;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  flex
	//----------------------------------

	/**
	 *  Input: Measurement for the GridRow.
	 */
	public var flex:Number;
	
	//----------------------------------
	//  height
	//----------------------------------

	/**
	 *  Output: The actual height of each row,
	 *  as determined by updateDisplayList().
	 */
	public var height:Number;

	//----------------------------------
	//  max
	//----------------------------------

	/**
	 *  Input: Measurement for the GridRow.
	 */
	public var max:Number;
	
	//----------------------------------
	//  min
	//----------------------------------

	/**
	 *  Input: Measurement for the GridRow.
	 */
	public var min:Number;
	
	//----------------------------------
	//  preferred
	//----------------------------------

	/**
	 *  Input: Measurement for the GridRow.
	 */
	public var preferred:Number;
	
	//----------------------------------
	//  y
	//----------------------------------

	/**
	 *  Output: The actual position of each row,
	 *  as determined by updateDisplayList().
	 */
	public var y:Number;
}

}
