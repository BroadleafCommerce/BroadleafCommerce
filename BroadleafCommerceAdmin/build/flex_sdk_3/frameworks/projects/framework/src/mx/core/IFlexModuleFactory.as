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

package mx.core
{

/**
 *  The IFlexModuleFactory interface represents the contract expected
 *  for bootstrapping Flex applications and dynamically loaded
 *  modules.
 *
 *  <p>Calling the <code>info()</code> method is legal immediately after
 *  the <code>complete</code> event is dispatched.</p>
 *
 *  <p>A well-behaved module dispatches a <code>ready</code> event when
 *  it is safe to call the <code>create()</code> method.</p>
 */
public interface IFlexModuleFactory
{
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  A factory method that requests
     *  an instance of a definition known to the module.
     *
     *  <p>You can provide an optional set of parameters to let
     *  building factories change what they create based
     *  on the input.
     *  Passing <code>null</code> indicates that the default
     *  definition is created, if possible.</p>
     *
     *  @param parameters An optional list of arguments. You can pass any number
     *  of arguments, which are then stored in an Array called <code>parameters</code>.
     *
     *  @return An instance of the module, or <code>null</code>.
     */
    function create(... parameters):Object;

    /**
     *  Returns a block of key/value pairs
     *  that hold static data known to the module.
     *  This method always succeeds, but can return an empty object.
     *
     *  @return An object containing key/value pairs. Typically, this object
     *  contains information about the module or modules created by this 
     *  factory; for example:
     * 
     *  <pre>
     *  return {"description": "This module returns 42."};
     *  </pre>
     *  
     *  Other common values in the returned object include the following:
     *  <ul>
     *   <li><code>fonts</code>: A list of embedded font faces.</li>
     *   <li><code>rsls</code>: A list of run-time shared libraries.</li>
     *   <li><code>mixins</code>: A list of classes initialized at startup.</li>
     *  </ul>
     */
    function info():Object;
}

}
