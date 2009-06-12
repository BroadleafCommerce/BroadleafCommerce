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

package mx.messaging.config
{

import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  This class acts as a context for the messaging framework so that it
 *  has access the URL and arguments of the SWF without needing
 *  access to the root MovieClip's LoaderInfo or Flex's Application
 *  class.
 */
public class LoaderConfig
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
	 *  <p>One instance of LoaderConfig is created by the SystemManager. 
	 *  You should not need to construct your own.</p>
     */
    public function LoaderConfig()
    {
        super();
    }

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  parameters
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the parameters property.
	 */
	mx_internal static var _parameters:Object;

    /**
     *  If the LoaderConfig has been initialized, this
     *  should represent the top-level MovieClip's parameters.
     */
    public static function get parameters():Object
    {
        return _parameters;
    }

	//----------------------------------
	//  url
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the url property.
	 */
    mx_internal static var _url:String = null;

    /**
     *  If the LoaderConfig has been initialized, this
     *  should represent the top-level MovieClip's URL.
     */
    public static function get url():String
    {
        return _url;
    }
}

}
