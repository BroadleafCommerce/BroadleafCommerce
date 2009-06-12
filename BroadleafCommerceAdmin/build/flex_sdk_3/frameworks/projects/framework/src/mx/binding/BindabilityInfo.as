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

package mx.binding
{

import mx.events.PropertyChangeEvent;

[ExcludeClass]

/**
 *  @private
 *  Bindability information for children (properties or methods)
 *  of a given class, based on the describeType() structure for that class.
 */
public class BindabilityInfo
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Name of [Bindable] metadata.
	 */
	public static const BINDABLE:String = "Bindable";
	
	/**
	 *  Name of [Managed] metadata.
	 */
	public static const MANAGED:String = "Managed";
	
	/**
	 *  Name of [ChangeEvent] metadata.
	 */
	public static const CHANGE_EVENT:String = "ChangeEvent";
	
	/**
	 *  Name of [NonCommittingChangeEvent] metadata.
	 */
	public static const NON_COMMITTING_CHANGE_EVENT:String =
		"NonCommittingChangeEvent";

	/**
	 *  Name of describeType() <accessor> element.
	 */
	public static const ACCESSOR:String = "accessor";
	
	/**
	 *  Name of describeType() <method> element.
	 */
	public static const METHOD:String = "method";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function BindabilityInfo(typeDescription:XML)
	{
		super();

		this.typeDescription = typeDescription;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var typeDescription:XML;
	
	/**
	 *  @private
	 *  event name -> true
	 */
	private var classChangeEvents:Object;
	
	/**
	 *  @private
	 *  child name -> { event name -> true }
	 */
	private var childChangeEvents:Object = {};	

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  Object containing { eventName: true } for each change event
	 *  (class- or child-level) that applies to the specified child.
	 */
	public function getChangeEvents(childName:String):Object
	{
		var changeEvents:Object = childChangeEvents[childName];

		if (!changeEvents)
		{
			// Seed with class-level events.
			changeEvents = copyProps(getClassChangeEvents(), {});

			// Get child-specific events.
			var childDesc:XMLList =
				typeDescription.accessor.(@name == childName) +
				typeDescription.method.(@name == childName);
			
			var numChildren:int = childDesc.length();

			if (numChildren == 0)
			{
				// we've been asked for events on an unknown property
				if (!typeDescription.@dynamic)
				{
					trace("warning: no describeType entry for '" +
						  childName + "' on non-dynamic type '" +
						  typeDescription.@name + "'");
				}
			}
			else
			{
				if (numChildren > 1)
				{
					trace("warning: multiple describeType entries for '" +
						  childName + "' on type '" + typeDescription.@name +
						  "':\n" + childDesc);
				}

				addBindabilityEvents(childDesc.metadata, changeEvents);
			}

			childChangeEvents[childName] = changeEvents;
		}

		return changeEvents;
	}

	/**
	 *  @private
	 *  Build or return cached class change events object.
	 */
	private function getClassChangeEvents():Object
	{
		if (!classChangeEvents)
		{
			classChangeEvents = {};

			addBindabilityEvents(typeDescription.metadata, classChangeEvents);

			// Class-level [Managed] means all properties
			// dispatch propertyChange.
			if (typeDescription.metadata.(@name == MANAGED).length() > 0)
			{
				classChangeEvents[PropertyChangeEvent.PROPERTY_CHANGE] = true;
			}
		}

		return classChangeEvents;
	}

	/**
	 *  @private
	 */
	private function addBindabilityEvents(metadata:XMLList,
										  eventListObj:Object):void
	{
		addChangeEvents(metadata.(@name == BINDABLE), eventListObj, true);
		addChangeEvents(metadata.(@name == CHANGE_EVENT), eventListObj, true);
		addChangeEvents(metadata.(@name == NON_COMMITTING_CHANGE_EVENT),
						eventListObj, false);
	}

	/**
	 *  @private
	 *  Transfer change events from a list of change-event-carrying metadata
	 *  to an event list object.
	 *  Note: metadata's first arg value is assumed to be change event name.
	 */
	private function addChangeEvents(metadata:XMLList, eventListObj:Object, isCommit:Boolean):void
	{
		for each (var md:XML in metadata)
		{
			var arg:XMLList = md.arg;
			if (arg.length() > 0)
			{
				var eventName:String = arg[0].@value;
				eventListObj[eventName] = isCommit;
			}
			else
			{
				trace("warning: unconverted Bindable metadata in class '" +
					  typeDescription.@name + "'");
			}
		}
	}

	/**
	 *  @private
	 *  Copy properties from one object to another.
	 */
	private function copyProps(from:Object, to:Object):Object
	{
		for (var propName:String in from)
		{
			to[propName] = from[propName];
		}

		return to;
	}
}

}
