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

package mx.logging
{

import mx.logging.errors.InvalidCategoryError;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("logging")]

/**
 *  Provides pseudo-hierarchical logging capabilities with multiple format and
 *  output options.
 *  The log system consists of two major components, the logger and a target.
 *  You can use the logger to send information to a target.
 *  The target is responsible for formatting and general output of the log data.
 *  <p>
 *  Loggers are singleton instances created for a particular category of
 *  information.
 *  Typically, the category is the package name of the component
 *  that desires to log information.
 *  The category provides users the ability to specify what log information they
 *  are interested in.
 *  Multiple categories can be selected and combined with regular expressions.
 *  This allows for both broad and narrow logging information to be acquired.
 *  For example, you might be interested in all logging information under
 *  the "mx.messaging" and "mx.rpc" packages and want the output from these
 *  packages to be formatted as XML.
 *  To get the all of the logging information under the "mx.messaging" category
 *  including sub-packages and components a wildcard expression is required, such as
 *  "mx.messaging.~~".
 *  See the code example below for more details.
 *  </p>
 *  <p>Targets provide the output mechanism of the data being logged.
 *  This mechanism typically includes formatting, transmission, or storage, but
 *  can be anything possible under the VM.
 *  There are two targets provided: <code>MiniDebugTarget</code> and 
 *  <code>TraceTarget</code>.
 *  Each of these writers take the current log information and "sends" it
 *  somewhere for display and/or storage.
 *  Targets also provide the specification of what log data to output.
 *  </p>
 *
 *  @example
 *  <pre>
 *  ... 
 *  import mx.logging.targets.*;
 *  import mx.logging.*;
 *
 *  private function initLogging():void {
 *      // Create a target.
 *      var logTarget:TraceTarget = new TraceTarget();
 *
 *      // Log only messages for the classes in the mx.rpc.* and 
 *      // mx.messaging packages.
 *      logTarget.filters=["mx.rpc.*","mx.messaging.*"];
 *
 *      // Log all log levels.
 *      logTarget.level = LogEventLevel.ALL;
 *
 *      // Add date, time, category, and log level to the output.
 *      logTarget.includeDate = true;
 *      logTarget.includeTime = true;
 *      logTarget.includeCategory = true;
 *      logTarget.includeLevel = true;
 *
 *      // Begin logging.
 *      Log.addTarget(logTarget);
 *  } 
 *  ...
 *  </pre>
 */
public class Log
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Sentinal value for the target log level to indicate no logging.
     */
    private static var NONE:int = int.MAX_VALUE;

    /**
     *  @private
     *  The most verbose supported log level among registered targets.
     */
    private static var _targetLevel:int = NONE;
        // Initialize target level to a value out of range.

    /**
     *  @private
     *  An associative Array of existing loggers keyed by category
     */
    private static var _loggers:Array;

    /**
     *  @private
     *  Array of targets that should be searched any time
     *  a new logger is created.
     */
    private static var _targets:Array = [];

	/**
	 *  @private
	 *  Storage for the resourceManager getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
	 */
	private static var _resourceManager:IResourceManager;
	
	/**
	 *  @private
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
	 */
	private static function get resourceManager():IResourceManager
	{
		if (!_resourceManager)
			_resourceManager = ResourceManager.getInstance();

		return _resourceManager;
	}

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Indicates whether a fatal level log event will be processed by a
     *  log target.
     *
     *  @return true if a fatal level log event will be logged; otherwise false.
     */
    public static function isFatal():Boolean
    {
        return (_targetLevel <= LogEventLevel.FATAL) ? true : false;
    }
    
    /**
     *  Indicates whether an error level log event will be processed by a
     *  log target.
     *
     *  @return true if an error level log event will be logged; otherwise false.
     */
    public static function isError():Boolean
    {
        return (_targetLevel <= LogEventLevel.ERROR) ? true : false;
    }
    
    /**
     *  Indicates whether a warn level log event will be processed by a
     *  log target.
     *
     *  @return true if a warn level log event will be logged; otherwise false.
     */
    public static function isWarn():Boolean
    {
        return (_targetLevel <= LogEventLevel.WARN) ? true : false;
    }

    /**
     *  Indicates whether an info level log event will be processed by a
     *  log target.
     *
     *  @return true if an info level log event will be logged; otherwise false.
     */ 
    public static function isInfo():Boolean
    {
        return (_targetLevel <= LogEventLevel.INFO) ? true : false;
    }
    
    /**
     *  Indicates whether a debug level log event will be processed by a
     *  log target.
     *
     *  @return true if a debug level log event will be logged; otherwise false.
     */
    public static function isDebug():Boolean
    {
        return (_targetLevel <= LogEventLevel.DEBUG) ? true : false;
    }

    /**
     *  Allows the specified target to begin receiving notification of log
     *  events.
     *
     *  @param The specific target that should capture log events.
     */
    public static function addTarget(target:ILoggingTarget):void
    {
        if (target)
        {
            var filters:Array = target.filters;
            var logger:ILogger;
            // need to find what filters this target matches and set the specified
            // target as a listener for that logger.
            for (var i:String in _loggers)
            {
                if (categoryMatchInFilterList(i, filters))
                    target.addLogger(ILogger(_loggers[i]));
            }
            // if we found a match all is good, otherwise we need to
            // put the target in a waiting queue in the event that a logger
            // is created that this target cares about.
            _targets.push(target);
            
            if (_targetLevel == NONE)
                _targetLevel = target.level
            else if (target.level < _targetLevel)
                _targetLevel = target.level;
        }
        else
        {
            var message:String = resourceManager.getString(
                "logging", "invalidTarget");
            throw new ArgumentError(message);
        }
    }

    /**
     *  Stops the specified target from receiving notification of log
     *  events.
     *
     *  @param The specific target that should capture log events.
     */
    public static function removeTarget(target:ILoggingTarget):void
    {
        if (target)
        {
            var filters:Array = target.filters;
            var logger:ILogger;
            // Disconnect this target from any matching loggers.
            for (var i:String in _loggers)
            {
                if (categoryMatchInFilterList(i, filters))
                {
                    target.removeLogger(ILogger(_loggers[i]));
                }                
            }
            // Remove the target.
            for (var j:int = 0; j<_targets.length; j++)
            {
                if (target == _targets[j])
                {
                    _targets.splice(j, 1);
                    j--;
                }
            }
            resetTargetLevel();
        }
        else
        {
            var message:String = resourceManager.getString(
                "logging", "invalidTarget");
            throw new ArgumentError(message);
        }
    }

    /**
     *  Returns the logger associated with the specified category.
     *  If the category given doesn't exist a new instance of a logger will be
     *  returned and associated with that category.
     *  Categories must be at least one character in length and may not contain
     *  any blanks or any of the following characters:
     *  []~$^&amp;\/(){}&lt;&gt;+=`!#%?,:;'"&#64;
     *  This method will throw an <code>InvalidCategoryError</code> if the
     *  category specified is malformed.
     *
     *  @param category The category of the logger that should be returned.
     *
     *  @return An instance of a logger object for the specified name.
     *  If the name doesn't exist, a new instance with the specified
     *  name is returned.
     */
    public static function getLogger(category:String):ILogger
    {
        checkCategory(category);
        if (!_loggers)
            _loggers = [];

        // get the logger for the specified category or create one if it
        // doesn't exist
        var result:ILogger = _loggers[category];
        if (result == null)
        {
            result = new LogLogger(category);
            _loggers[category] = result;
        }

        // check to see if there are any targets waiting for this logger.
        var target:ILoggingTarget;
        for (var i:int = 0; i < _targets.length; i++)
        {
            target = ILoggingTarget(_targets[i]);
            if (categoryMatchInFilterList(category, target.filters))
                target.addLogger(result);
        }

        return result;
    }

    /**
     *  This method removes all of the current loggers from the cache.
     *  Subsquent calls to the <code>getLogger()</code> method return new instances
     *  of loggers rather than any previous instances with the same category.
     *  This method is intended for use in debugging only.
     */
    public static function flush():void
    {
        _loggers = [];
        _targets = [];
        _targetLevel = NONE;
    }

    /**
     *  This method checks the specified string value for illegal characters.
     *
     *  @param value The String to check for illegal characters.
     *            The following characters are not valid:
     *                []~$^&amp;\/(){}&lt;&gt;+=`!#%?,:;'"&#64;
     *  @return   <code>true</code> if there are any illegal characters found,
     *            <code>false</code> otherwise
     */
    public static function hasIllegalCharacters(value:String):Boolean
    {
        return value.search(/[\[\]\~\$\^\&\\(\)\{\}\+\?\/=`!@#%,:;'"<>\s]/) != -1;
    }

    // private members
    /**
     *  This method checks that the specified category matches any of the filter
     *  expressions provided in the <code>filters</code> Array.
     *
     *  @param category The category to match against
     *  @param filters A list of Strings to check category against.
     *  @return <code>true</code> if the specified category matches any of the
     *            filter expressions found in the filters list, <code>false</code>
     *            otherwise.
     *  @private
     */
    private static function categoryMatchInFilterList(category:String, filters:Array):Boolean
    {
        var result:Boolean = false;
        var filter:String;
        var index:int = -1;
        for (var i:uint = 0; i < filters.length; i++)
        {
            filter = filters[i];
            // first check to see if we need to do a partial match
            // do we have an asterisk?
            index = filter.indexOf("*");

            if (index == 0)
                return true;

            index = index < 0 ? index = category.length : index -1;

            if (category.substring(0, index) == filter.substring(0, index))
                return true;
        }
        return false;
    }

    /**
     *  This method will ensure that a valid category string has been specified.
     *  If the category is not valid an <code>InvalidCategoryError</code> will
     *  be thrown.
     *  Categories can not contain any blanks or any of the following characters:
     *    []`*~,!#$%^&amp;()]{}+=\|'";?&gt;&lt;./&#64; or be less than 1 character in length.
     *  @private
     */
    private static function checkCategory(category:String):void
    {
        var message:String;
        
        if (category == null || category.length == 0)
        {
            message = resourceManager.getString(
                "logging", "invalidLen");
            throw new InvalidCategoryError(message);
        }

        if (hasIllegalCharacters(category) || (category.indexOf("*") != -1))
        {
            message = resourceManager.getString(
                "logging", "invalidChars");
            throw new InvalidCategoryError(message);
        }
    }
    
    /**
     *  @private
     *  This method resets the Log's target level to the most verbose log level
     *  for the currently registered targets.
     */
    private static function resetTargetLevel():void
    {
        var minLevel:int = NONE;
        for (var i:int = 0; i < _targets.length; i++)
        {
            if (minLevel == NONE || _targets[i].level < minLevel)
                minLevel = _targets[i].level;
        }
        _targetLevel = minLevel;
    }
}

}
