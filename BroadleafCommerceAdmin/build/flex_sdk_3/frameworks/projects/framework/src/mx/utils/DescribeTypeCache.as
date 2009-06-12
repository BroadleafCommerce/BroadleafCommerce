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

package mx.utils
{

import flash.utils.describeType;
import flash.utils.getDefinitionByName;
import flash.utils.getQualifiedClassName;
import mx.binding.BindabilityInfo;

[ExcludeClass]

/**
 *  DescribeTypeCache is a convenience class that is used to 
 *  cache the return values of <code>flash.utils.describeType()</code>
 *  so that calls made subsequent times return faster.
 *
 *  This class also lets you set handler functions for specific value types.
 *  These will get called when the user tries to access these values on
 *  the <code>DescribeTypeCacheRecord</code> class.
 * 
 *  @see mx.utils.DescribeTypeCacheRecord
 */
public class DescribeTypeCache
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class initialization
	//
	//--------------------------------------------------------------------------

	registerCacheHandler("bindabilityInfo", bindabilityInfoHandler);

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var typeCache:Object = {};
	
	/**
	 *  @private
	 */
	private static var cacheHandlers:Object = {};

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Calls <code>flash.utils.describeType()</code> for the first time and caches
         *  the return value so that subsequent calls return faster. 
         *
         *  @param o Can be either a string describing a fully qualified class name or any 
         *  ActionScript value, including all available ActionScript types, object instances,
         *  primitive types (such as <code>uint</code>), and class objects.
         *
         *  @return Returns the cached record.
         *
         *  @see flash.utils#describeType() 
	 */
	public static function describeType(o:*):DescribeTypeCacheRecord
	{
		var className:String;

		if (o is String)
			className = o;
		else
			className = getQualifiedClassName(o);

		if (className in typeCache)
		{
			return typeCache[className];
		}
		else
		{
			if (o is String)
				o = getDefinitionByName(o);

			var typeDescription:XML = flash.utils.describeType(o);
			var record:DescribeTypeCacheRecord = new DescribeTypeCacheRecord();
			record.typeDescription = typeDescription;
			record.typeName = className;
			typeCache[className] = record;

			return record;
		}
	}

	/**
	 *  registerCacheHandler lets you add function handler for specific strings.
         *  These functions get called when the user refers to these values on a
         *  instance of <code>DescribeTypeCacheRecord</code>.
	 *
	 *  @param valueName String that specifies the value for which the handler must be set.
         *  @param handler Function that should be called when user references valueName.
	 */
	public static function registerCacheHandler(valueName:String, handler:Function):void
	{
		cacheHandlers[valueName] = handler;
	}

	/**
	 *  @private
	 */
	internal static function extractValue(valueName:String, record:DescribeTypeCacheRecord):*
	{
		if (valueName in cacheHandlers)
			return cacheHandlers[valueName](record);

		return undefined;
	}
	
	/**
	 *  @private
	 */
	private static function bindabilityInfoHandler(record:DescribeTypeCacheRecord):*
	{
		return new BindabilityInfo(record.typeDescription);
	}
}

}
