////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.modules
{

import flash.events.EventDispatcher;

[Frame(factoryClass="mx.core.FlexModuleFactory")]

/**
 *  The base class for ActionScript-based dynamically-loadable modules.
 *  If you write an ActionScript-only module, you should extend this class.
 *  If you write an MXML-based module by using the <code>&lt;mx:Module&gt;</code> 
 *  tag in an MXML file, you instead extend the Module class.
 *  
 *  @see mx.modules.Module
 */
public class ModuleBase extends EventDispatcher
{
    include "../core/Version.as";
}

}
