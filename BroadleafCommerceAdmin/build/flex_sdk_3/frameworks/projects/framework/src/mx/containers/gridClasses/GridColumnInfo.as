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

import mx.containers.utilityClasses.FlexChildInfo;
import mx.core.UIComponent;

[ExcludeClass]

/**
 *  @private
 *  Internal helper class used to exchange information between
 *  Grid and GridRow.
 */
public class GridColumnInfo extends FlexChildInfo
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
	public function GridColumnInfo()
	{
		super();

		min = 0;
		preferred = 0;
		max = UIComponent.DEFAULT_MAX_WIDTH;
		flex = 0;
		percent = 0;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  x
	//----------------------------------

	/**
	 *  Output: the actual position of each column,
	 *  as determined by updateDisplayList().
	 */
	public var x:Number;
}

}
