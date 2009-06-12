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

import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  Provides a logger target that uses the global <code>trace()</code> method to output log messages.
 *  
 *  <p>To view <code>trace()</code> method output, you must be running the 
 *  debugger version of Flash Player or AIR Debug Launcher.</p>
 *  
 *  <p>The debugger version of Flash Player and AIR Debug Launcher send output from the <code>trace()</code> method 
 *  to the flashlog.txt file. The default location of this file is the same directory as 
 *  the mm.cfg file. You can customize the location of this file by using the <code>TraceOutputFileName</code> 
 *  property in the mm.cfg file. You must also set <code>TraceOutputFileEnable</code> to 1 in your mm.cfg file.</p>
 */
public class TraceTarget extends LineFormattedTarget
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
     *  the log data to the global <code>trace()</code> method.
     *  All output will be directed to flashlog.txt by default.</p>
     */
    public function TraceTarget()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method outputs the specified message directly to 
     *  <code>trace()</code>.
     *  All output will be directed to flashlog.txt by default.
     *
     *  @param message String containing preprocessed log message which may
     *  include time, date, category, etc. based on property settings,
     *  such as <code>includeDate</code>, <code>includeCategory</code>, etc.
     */
    override mx_internal function internalLog(message:String):void
    {
        trace(message);
    }
}

}
