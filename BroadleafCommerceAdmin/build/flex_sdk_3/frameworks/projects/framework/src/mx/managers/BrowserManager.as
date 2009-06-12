////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import mx.core.Singleton;

/**
 *  The BrowserManager is a Singleton manager that acts as
 *  a proxy between the browser and the application.
 *  It provides access to the URL in the browser address
 *  bar similar to accessing the <code>document.location</code> property in JavaScript.
 *  Events are dispatched when the <code>url</code> property is changed. 
 *  Listeners can then respond, alter the URL, and/or block others
 *  from getting the event. 
 * 
 *  <p>To use the BrowserManager, you call the <code>getInstance()</code> method to get the current
 *  instance of the manager, and call methods and listen to
 *  events on that manager. See the IBrowserManager class for the
 *  methods, properties, and events to use.</p>
 *
 *  @see mx.managers.IBrowserManager
 *  @see mx.managers.HistoryManager
 */
public class BrowserManager
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Linker dependency on implementation class.
     */
    private static var implClassDependency:BrowserManagerImpl;

    /**
     *  @private
     */
    private static var instance:IBrowserManager;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the sole instance of this Singleton class;
     *  creates it if it does not already exist.
     *
     *  @return Returns the sole instance of this Singleton class;
     *  creates it if it does not already exist.
     */
    public static function getInstance():IBrowserManager
    {
        if (!instance)
        {
            instance = IBrowserManager(
                Singleton.getInstance("mx.managers::IBrowserManager"));
        }

        return instance;
    }
}

}
