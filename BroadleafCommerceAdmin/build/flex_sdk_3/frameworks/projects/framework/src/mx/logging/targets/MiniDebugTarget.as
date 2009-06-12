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

package mx.logging.targets
{

import flash.net.LocalConnection;
import flash.events.StatusEvent;
import flash.events.SecurityErrorEvent;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  Provides a logger target that outputs to a <code>LocalConnection</code>,
 *  connected to the MiniDebug application.
 */
public class MiniDebugTarget extends LineFormattedTarget
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
	 *  <p>Constructs an instance of a logger target that will send
	 *  the log data to the MiniDebug application.</p>
	 *
     *  @param connection Specifies where to send the logging information.
     *  This value is the name of the connection specified in the
     *  <code>LocalConnection.connect()</code> method call in the remote SWF,
     *  that can receive calls to a <code>log()</code> method with the
     *  following signature: 
     *  <pre>
     *    log(... args:Array)
     *  </pre> 
     *  Each value specified in the <code>args</code> Array is a String.
     *
     *  @param method Specifies what method to call on the remote connection.
     */
    public function MiniDebugTarget(connection:String = "_mdbtrace",
									method:String = "trace")
    {
        super();

        _lc = new LocalConnection();
        _lc.addEventListener(StatusEvent.STATUS, onStatus);
        _lc.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onSecurityError);
        _connection = connection;
        _method = method;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _lc:LocalConnection;
    
    /**
     *  @private
     *  The name of the method that we should call on the remote connection.
     */
    private var _method:String;

    /**
     *  @private
     *  The name of the connection that we should send to.
     */
    private var _connection:String;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
     *  This method outputs the specified message directly to the method
     *  specified (passed to the constructor) for the local connection.
     *
	 *  @param message String containing preprocessed log message which may
	 *  include time, date, category, etc. based on property settings,
	 *  such as <code>includeDate</code>, <code>includeCategory</code>, etc.
	 */
	override mx_internal function internalLog(message:String):void
	{
        _lc.send(_connection, _method, message);
    }

    private function onStatus(e:StatusEvent):void
    {
        if (e.level == "error")
            trace("Warning: MiniDebugTarget send failed: " + e.code);
    }

    private function onSecurityError(e:SecurityErrorEvent):void
    {
        trace("Error: security error on MiniDebugTarget's local connction");
    }
}

}
