////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.formatters
{

import flash.events.Event;
import mx.core.mx_internal;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("formatters")]
[ResourceBundle("SharedResources")]

/**
 *  The DateBase class contains the localized string information
 *  used by the mx.formatters.DateFormatter class and the parsing function
 *  that renders the pattern.
 *  This is a helper class for the DateFormatter class that is not usually
 *  used independently.
 *
 *  @see mx.formatters.DateFormatter
 */
public class DateBase
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var initialized:Boolean = false;

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
	//  Class properties
	//
	//--------------------------------------------------------------------------
		
	//----------------------------------
	//  dayNamesLong
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the dayNamesLong property.
	 */
	private static var _dayNamesLong:Array; /* of String */
	
    /**
	 *  @private
	 */
	private static var dayNamesLongOverride:Array; /* of String */

	/**
	 *  Long format of day names.
	 * 
	 *  @default [Sunday", "Monday", "Tuesday", "Wednesday",
	 *  "Thursday", "Friday", "Saturday"]
	 */
	public static function get dayNamesLong():Array /* of String */
	{
		initialize();

		return _dayNamesLong;
	}

	/**
	 *  @private
	 */
	public static function set dayNamesLong(value:Array /* of String*/):void
	{
		dayNamesLongOverride = value;

		_dayNamesLong = value != null ?
						value :
						resourceManager.getStringArray(
						    "SharedResources", "dayNames");
	}
		
	//----------------------------------
	//  dayNamesShort
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the dayNamesShort property.
	 */
	private static var _dayNamesShort:Array; /* of String */
	
    /**
	 *  @private
	 */
	private static var dayNamesShortOverride:Array; /* of String */

	/**
	 *  Short format of day names.
	 * 
	 *  @default ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
	 */
	public static function get dayNamesShort():Array /* of String */
	{
		initialize();

		return _dayNamesShort;
	}

	/**
	 *  @private
	 */
	public static function set dayNamesShort(value:Array /* of String*/):void
	{
		dayNamesShortOverride = value;

		_dayNamesShort = value != null ?
						 value :
						 resourceManager.getStringArray(
						     "formatters", "dayNamesShort");
	}
	
	//----------------------------------
	//  defaultStringKey
	//----------------------------------

	/**
	 *  @private
	 */		
	mx_internal static function get defaultStringKey():Array /* of String */
	{
		initialize();

		return monthNamesLong.concat(timeOfDay);
	}
	
	//----------------------------------
	//  monthNamesLong
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the monthNamesLong property.
	 */
	private static var _monthNamesLong:Array; /* of String */
	
    /**
	 *  @private
	 */
	private static var monthNamesLongOverride:Array; /* of String */

	/**
	 *  Long format of month names.
	 *
	 *  @default ["January", "February", "March", "April", "May", "June", 
	 *  "July", "August", "September", "October", "November", "December"].
	 */
	public static function get monthNamesLong():Array /* of String */
	{
		initialize();

		return _monthNamesLong;
	}

	/**
	 *  @private
	 */
	public static function set monthNamesLong(value:Array /* of String*/):void
	{
		monthNamesLongOverride = value;

		_monthNamesLong = value != null ?
						  value :
						  resourceManager.getStringArray(
						      "SharedResources", "monthNames");

		if (value == null)
		{
			// Currently there is no way to get a null 
			// string from resourceBundles using getString
			// Hence monthSymbol is a space in English, but
			// we actually want it to be a null string.
			var monthSymbol:String = resourceManager.getString(
				"SharedResources", "monthSymbol");
			if (monthSymbol != " ")
			{
				// _monthNamesLong will be null if there are no resources.
				var n:int = _monthNamesLong ? _monthNamesLong.length : 0;
				for (var i:int = 0; i < n; i++)
				{
					_monthNamesLong[i] += monthSymbol;
				}
			}
		}
	}
				
	//----------------------------------
	//  monthNamesShort
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the monthNamesShort property.
	 */
	private static var _monthNamesShort:Array; /* of String */
	
    /**
	 *  @private
	 */
	private static var monthNamesShortOverride:Array; /* of String */

	/**
	 *  Short format of month names.
	 *
	 *  @default ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
	 *  "Jul", "Aug", "Sep", "Oct","Nov", "Dec"]
	 */
	public static function get monthNamesShort():Array /* of String */
	{
		initialize();

		return _monthNamesShort;
	}

	/**
	 *  @private
	 */
	public static function set monthNamesShort(value:Array /* of String*/):void
	{
		monthNamesShortOverride = value;

		_monthNamesShort = value != null ?
						   value :
						   resourceManager.getStringArray(
						       "formatters", "monthNamesShort");
		if (value == null)
		{
			// Currently there is no way to get a null 
			// string from resourceBundles using getString
			// Hence monthSymbol is a space in English, but
			// we actually want it to be a null string.
			var monthSymbol:String = resourceManager.getString(
				"SharedResources", "monthSymbol");
			if (monthSymbol != " ")
			{
				// _monthNamesShort will be null if there are no resources.
				var n:int = _monthNamesShort ? _monthNamesShort.length : 0;
				for (var i:int = 0; i < n; i++)
				{
					_monthNamesShort[i] += monthSymbol;
				}
			}
		}
	}
	    	
	//----------------------------------
	//  timeOfDay
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the timeOfDay property.
	 */
	private static var _timeOfDay:Array; /* of String */
	
    /**
	 *  @private
	 */
	private static var timeOfDayOverride:Array; /* of String */

	/**
	 *  Time of day names.
	 * 
	 *  @default ["AM", "PM"]
	 */
	public static function get timeOfDay():Array /* of String */ 
	{
		initialize();

		return _timeOfDay;
	}

    /**
	 *  @private
     */
	public static function set timeOfDay(value:Array /* of String */):void
	{
		timeOfDayOverride = value;

		var am:String = resourceManager.getString("formatters", "am");
		var pm:String = resourceManager.getString("formatters", "pm");

		_timeOfDay = value != null ? value : [ am, pm ];
	}
		
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private    
     */
	private static function initialize():void
	{
		if (!initialized)
		{
			// Register as a weak listener for "change" events
			// from ResourceManager.
			resourceManager.addEventListener(
				Event.CHANGE, static_resourceManager_changeHandler,
				false, 0, true);

			static_resourcesChanged();

			initialized = true;
		}
	}

    /**
	 *  @private    
     */
	private static function static_resourcesChanged():void
	{
		dayNamesLong = dayNamesLongOverride;
		dayNamesShort = dayNamesShortOverride;
		monthNamesLong = monthNamesLongOverride;
		monthNamesShort = monthNamesShortOverride;
		timeOfDay = timeOfDayOverride;
	}

	/**
	 *  @private
	 *  Parses token objects and renders the elements of the formatted String.
	 *  For details about token objects, see StringFormatter.
	 *
	 *  @param date Date object.
	 *
	 *  @param tokenInfo Array object that contains token object descriptions.
	 *
	 *  @return Formatted string.
	 */
	mx_internal static function extractTokenDate(date:Date,
											tokenInfo:Object):String
	{
		initialize();

		var result:String = "";
		
		var key:int = int(tokenInfo.end) - int(tokenInfo.begin);
		
		var day:int;
		var hours:int;
		
		switch (tokenInfo.token)
		{
			case "Y":
			{
				// year
				var year:String = date.getFullYear().toString();
				if (key < 3)
					return year.substr(2);
				else if (key > 4)
					return setValue(Number(year), key);
				else
					return year;
			}

			case "M":
			{
				// month in year
				var month:int = int(date.getMonth());
				if (key < 3)
				{
					month++; // zero based
					result += setValue(month, key);
					return result;
				}
				else if (key == 3)
				{
					return monthNamesShort[month];
				}
				else
				{
					return monthNamesLong[month];
				}
			}

			case "D":
			{
				// day in month
				day = int(date.getDate());
				result += setValue(day, key);
				return result;
			}

			case "E":
			{
				// day in the week
				day = int(date.getDay());
				if (key < 3)
				{
					result += setValue(day, key);
					return result;
				}
				else if (key == 3)
				{
					return dayNamesShort[day];
				}
				else
				{
					return dayNamesLong[day];
				}
			}

			case "A":
			{
				// am/pm marker
				hours = int(date.getHours());
				if (hours < 12)
					return timeOfDay[0];
				else
					return timeOfDay[1];
			}

			case "H":
			{
				// hour in day (1-24)
				hours = int(date.getHours());
				if (hours == 0)
					hours = 24;
				result += setValue(hours, key);
				return result;
			}

			case "J":
			{
				// hour in day (0-23)
				hours = int(date.getHours());
				result += setValue(hours, key);
				return result;
			}

			case "K":
			{
				// hour in am/pm (0-11)
				hours = int(date.getHours());
				if (hours >= 12)
					hours = hours - 12;
				result += setValue(hours, key);
				return result;
			}

			case "L":
			{
				// hour in am/pm (1-12)
				hours = int(date.getHours());
				if (hours == 0)
					hours = 12;
				else if (hours > 12)
					hours = hours - 12;
				result += setValue(hours, key);
				return result;
			}

			case "N":
			{
				// minutes in hour
				var mins:int = int(date.getMinutes());
				result += setValue(mins, key);
				return result;
			}

			case "S":
			{
				// seconds in minute
				var sec:int = int(date.getSeconds());
				result += setValue(sec, key);
				return result;
			}
		}

		return result;
	}

	/**
	 *  @private
	 *  Makes a given length of digits longer by padding with zeroes.
	 *
	 *  @param value Value to pad.
	 *
	 *  @param key Length of the string to pad.
	 *
	 *  @return Formatted string.
	 */
	private static function setValue(value:Object, key:int):String
	{
		var result:String = "";

		var vLen:int = value.toString().length;
		if (vLen < key)
		{
			var n:int = key - vLen;
			for (var i:int = 0; i < n; i++)
			{
				result += "0"
			}
		}

		result += value.toString();

		return result;
	}

	//--------------------------------------------------------------------------
	//
	//  Class event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static function static_resourceManager_changeHandler(event:Event):void
	{
		static_resourcesChanged();
	}
}

}
