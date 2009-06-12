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

package mx.containers.utilityClasses
{

import mx.core.IUIComponent;

[ExcludeClass]

/**
 *  @private
 *  Helper class for the Flex.flexChildrenProportionally() method.
 */
public class FlexChildInfo
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
	public function FlexChildInfo()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  child
	//----------------------------------

	/**
	 *  @private
	 */
	public var child:IUIComponent;

	//----------------------------------
	//  size
	//----------------------------------

	/**
	 *  @private
	 */
	public var size:Number = 0;

	//----------------------------------
	//  preferred
	//----------------------------------

	/**
	 *  @private
	 */
	public var preferred:Number = 0;

	//----------------------------------
	//  flex
	//----------------------------------

	/**
	 *  @private
	 */
	public var flex:Number = 0;
	
	//----------------------------------
	//  percent
	//----------------------------------

	/**
	 *  @private
	 */
	public var percent:Number;

	//----------------------------------
	//  min
	//----------------------------------

	/**
	 *  @private
	 */
	public var min:Number;

	//----------------------------------
	//  max
	//----------------------------------

	/**
	 *  @private
	 */
	public var max:Number;

	//----------------------------------
	//  width
	//----------------------------------

	/**
	 *  @private
	 */
	public var width:Number;

	//----------------------------------
	//  height
	//----------------------------------

	/**
	 *  @private
	 */
	public var height:Number;
}

}
