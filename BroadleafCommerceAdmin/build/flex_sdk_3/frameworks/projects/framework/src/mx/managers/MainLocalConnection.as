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
import mx.managers.HistoryManager;

//-------------------------------------------------------------------------------
//
//  Private class: MainLocalConnection
//
//  This class should live inside HistoryManager.as, but can't at the moment due
//  to a player bug.
//
//-------------------------------------------------------------------------------

[ExcludeClass]

/**
 *  @private
 */
internal class MainLocalConnection extends LocalConnection
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
	public function MainLocalConnection()
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
	public function loadState(stateVars:Object):void
	{
		HistoryManager.mx_internal::load(stateVars);
	}

	/**
	 *  @private
	 */
	public function register():void
	{
		HistoryManager.mx_internal::registerHandshake();
	}

	/**
	 *  @private
	 */
	public function registered():void
	{
		HistoryManager.mx_internal::registered();
	}
}

}
