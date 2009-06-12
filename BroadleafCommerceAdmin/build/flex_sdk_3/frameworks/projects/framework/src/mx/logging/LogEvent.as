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

package mx.logging
{

import flash.events.Event;

/**
 *  Represents the log information for a single logging event.
 *  The loging system dispatches a single event each time a process requests
 *  information be logged.
 *  This event can be captured by any object for storage or formatting.
 */
public class LogEvent extends Event
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  Event type constant; identifies a logging event.
     */
    public static const LOG:String = "log";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Returns a string value representing the level specified.
	 *
     *  @param The level a string is desired for.
	 *
     *  @return The level specified in English.
     */
    public static function getLevelString(value:uint):String
    {
        switch (value)
        {
            case LogEventLevel.INFO:
			{
                return "INFO";
			}

            case LogEventLevel.DEBUG:
			{
                return "DEBUG";
            }

            case LogEventLevel.ERROR:
			{
                return "ERROR";
            }

            case LogEventLevel.WARN:
			{
                return "WARN";
            }

            case LogEventLevel.FATAL:
			{
                return "FATAL";
            }

            case LogEventLevel.ALL:
			{
                return "ALL";
            }
		}

		return "UNKNOWN";
    }

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
     *  @param msg String containing the log data.
	 *
     *  @param level The level for this log event.
     *  Valid values are:
     *  <ul>
     *    <li><code>LogEventLevel.FATAL</code> designates events that are very
     *    harmful and will eventually lead to application failure</li>
     *
     *    <li><code>LogEventLevel.ERROR</code> designates error events that might
     *    still allow the application to continue running.</li>
     *
     *    <li><code>LogEventLevel.WARN</code> designates events that could be
     *    harmful to the application operation</li>
	 *
     *    <li><code>LogEventLevel.INFO</code> designates informational messages
     *    that highlight the progress of the application at
	 *    coarse-grained level.</li>
	 *
     *    <li><code>LogEventLevel.DEBUG</code> designates informational
     *    level messages that are fine grained and most helpful when
     *    debugging an application.</li>
	 *
     *    <li><code>LogEventLevel.ALL</code> intended to force a target to
     *    process all messages.</li>
     *  </ul>
     */
    public function LogEvent(message:String = "",
							 level:int = 0 /* LogEventLevel.ALL */)
    {
        super(LogEvent.LOG, false, false);

        this.message = message;
        this.level = level;
    }

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  level
	//----------------------------------

    /**
     *  Provides access to the level for this log event.
     *  Valid values are:
     *    <ul>
     *      <li><code>LogEventLogEventLevel.INFO</code> designates informational messages
     *      that highlight the progress of the application at
     *      coarse-grained level.</li>
     *
     *      <li><code>LogEventLevel.DEBUG</code> designates informational
     *      level messages that are fine grained and most helpful when
     *      debugging an application.</li>
     *
     *      <li><code>LogEventLevel.ERROR</code> designates error events that might
     *      still allow the application to continue running.</li>
     *
     *      <li><code>LogEventLevel.WARN</code> designates events that could be
     *      harmful to the application operation.</li>
     *
     *      <li><code>LogEventLevel.FATAL</code> designates events that are very
     *      harmful and will eventually lead to application failure.</li>
     *    </ul>
     */
    public var level:int;

	//----------------------------------
	//  message
	//----------------------------------

    /**
     *  Provides access to the message that was logged.
     */
    public var message:String;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Event
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
     */
    override public function clone():Event
    {
        return new LogEvent(message, /*type,*/ level);
    }
}

}