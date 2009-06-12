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

package mx.binding
{

import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.utils.getQualifiedClassName;
import mx.core.EventPriority;
import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;
import mx.utils.DescribeTypeCache;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class PropertyWatcher extends Watcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Create a PropertyWatcher
     *
     *  @param prop The name of the property to watch.
     *  @param event The event type that indicates the property has changed.
     *  @param listeners The binding objects that are listening to this Watcher.
     *  @param propertyGetter A helper function used to access non-public variables.
     */
    public function PropertyWatcher(propertyName:String,
                                    events:Object,
                                    listeners:Array,
                                    propertyGetter:Function = null)
    {
		super(listeners);

        _propertyName = propertyName;
        this.events = events;
        this.propertyGetter = propertyGetter;
        useRTTI = !events;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
     *  The parent object of this property.
     */
    private var parentObj:Object;

    /**
     *  The events that indicate the property has changed
     */
    protected var events:Object;

    /**
     *  Storage for the propertyGetter property.
     */
    protected var propertyGetter:Function;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  propertyName
	//----------------------------------

	/**
     *  Storage for the propertyName property.
     */
    private var _propertyName:String;
    
    /**
     *  The name of the property this Watcher is watching.
     */
    public function get propertyName():String
    {
        return _propertyName;
    }

	//----------------------------------
	//  useRTTI
	//----------------------------------

    /**
     *	If compiler can't determine bindability from static type,
	 *  use RTTI on runtime values.
     */
    private var useRTTI:Boolean;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Watcher
	//
	//--------------------------------------------------------------------------

    /**
     *  If the parent has changed we need to update ourselves
     */
    override public function updateParent(parent:Object):void
    {
        if (parentObj && parentObj is IEventDispatcher)
        {
            for (var eventType:String in events)
            {
                parentObj.removeEventListener(eventType, eventHandler);
            }
        }

        if (parent is Watcher)
            parentObj = parent.value;
        else
            parentObj = parent;

        if (parentObj)
        {
			if (useRTTI)
			{
				// Use RTTI to ensure that parentObj is an IEventDispatcher,
				// and that bindability metadata exists
				// for parentObj[_propertyName].

				events = {};

				if (parentObj is IEventDispatcher)
				{
					var info:BindabilityInfo =
						DescribeTypeCache.describeType(parentObj).
						bindabilityInfo;

					events = info.getChangeEvents(_propertyName);

					if (objectIsEmpty(events))
					{
						trace("warning: unable to bind to property '" +
							  _propertyName + "' on class '" +
							  getQualifiedClassName(parentObj) + "'");
					}
					else
					{
						addParentEventListeners();
					}
				}
				else
				{
					trace("warning: unable to bind to property '" +
						  _propertyName + "' on class '" +
						  getQualifiedClassName(parentObj) +
						  "' (class is not an IEventDispatcher)");
				}
			}
			else
			{
				// useRTTI == false implies that the compiler
				// has provided us with a list of change events.
				// NOTE: this normally also implies that parentObj
				// is guaranteed to implement IEventDispatcher.
				// The guard below is necessitated by Proxy cases,
				// which provide blanket bindability information
				// on properties which are not strongly typed,
				// and so could accept values that do not implement
				// IEventDispatcher.
				// In these cases, correct binding behavior depends on
				// the Proxy implementation providing after-the-fact
				// bindability by wrapping assigned values and attaching
				// event listeners at that point.
				// Here we can only fail silently.

				if (parentObj is IEventDispatcher)
					addParentEventListeners();
			}
        }
        
		// Now get our property.
        wrapUpdate(updateProperty);
    }

    /**
	 *  @private
	 */
    override protected function shallowClone():Watcher
    {
        var clone:PropertyWatcher = new PropertyWatcher(_propertyName,
                                                        events,
                                                        listeners,
                                                        propertyGetter);

        return clone;
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function addParentEventListeners():void
	{
		for (var eventType:String in events)
		{
			if (eventType != "__NoChangeEvent__")
			{
				parentObj.addEventListener(
					eventType, eventHandler, false, EventPriority.BINDING, true);
			}
		}
	}

	/**
	 *  @private
	 */
	private function traceInfo():String
	{
		return "Watcher(" + getQualifiedClassName(parentObj) + "." +
				_propertyName + "): events = [" +
				eventNamesToString() + (useRTTI ? "] (RTTI)" : "]");
	}

	/**
	 *  @private
	 */
	private function eventNamesToString():String
	{
		var s:String = " ";

		for (var ev:String in events)
		{
			s += ev + " ";
		}
		
		return s;
	}

	/**
	 *  @private
	 */
	private function objectIsEmpty(o:Object):Boolean
	{
		for (var p:String in o)
		{
			return false;
		}
		return true;
	}

    /**
     *  Gets the actual property then updates
	 *  the Watcher's children appropriately.
     */
    private function updateProperty():void
    {
        if (parentObj)
        {
            if (_propertyName == "this")
            {
                value = parentObj;
            }
            else
            {
                if (propertyGetter != null)
                {
                    value = propertyGetter.apply(parentObj, [ _propertyName ]);
                }
                else
                {
                    value = parentObj[_propertyName];
                }
            }
        }
        else
        {
            value = null;
        }

        updateChildren();
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
     *  The generic event handler.
	 *  The only event we'll hear indicates that the property has changed.
     */
    public function eventHandler(event:Event):void
    {
        if (event is PropertyChangeEvent)
        {
            var propName:Object = PropertyChangeEvent(event).property

            if (propName != _propertyName)
                return;
        }

		wrapUpdate(updateProperty);

        notifyListeners(events[event.type]);
    }
}

}
