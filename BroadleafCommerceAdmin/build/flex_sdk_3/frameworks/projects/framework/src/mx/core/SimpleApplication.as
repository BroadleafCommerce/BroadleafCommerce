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

package mx.core
{

import flash.display.MovieClip;

[Frame(factoryClass="mx.core.FlexApplicationBootstrap")]

[ExcludeClass]

/**
 *  @private
 *  SimpleApplication is nothing other than a base class to use when
 *  you need a trivial application bootstrapped by FlexApplicationBootstrap.
 */
public class SimpleApplication extends MovieClip
{
	include "../core/Version.as";
}

}
