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

package mx.managers
{

import flash.net.LocalConnection;
import mx.core.mx_internal;

//-------------------------------------------------------------------------------
//
//  Private class: InitLocalConnection
//
//  This class should live inside HistoryManager.as, but can't at the moment due
//  to a player bug.
//
//-------------------------------------------------------------------------------

[ExcludeClass]

/**
 *  @private
 */
internal class InitLocalConnection extends LocalConnection
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function InitLocalConnection()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public function loadInitialState():void
	{
		HistoryManager.mx_internal::loadInitialState();
	}
}

}
