////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers.utilityClasses
{

import mx.core.Container;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ExcludeClass]

/**
 *  @private
 */
public class Layout
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
	public function Layout()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Used for accessing localized error messages.
	 */
	protected var resourceManager:IResourceManager =
									ResourceManager.getInstance();

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  target
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the target property.
	 */
	private var _target:Container;

	/**
	 *  The container associated with this layout.
	 */
	public function get target():Container
	{
		return _target;
	}
	
	/**
	 *  @private
	 */
	public function set target(value:Container):void
	{
		_target = value;
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public function measure():void
	{
	}

	/**
	 *  @private
	 */
	public function updateDisplayList(unscaledWidth:Number,
									  unscaledHeight:Number):void
	{
	}
}

}
