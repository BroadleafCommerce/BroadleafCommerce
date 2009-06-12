////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
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
import mx.core.EventPriority;
import mx.events.PropertyChangeEvent;

[ExcludeClass]

/**
 *  @private
 */
public class StaticPropertyWatcher extends Watcher
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Create a StaticPropertyWatcher
     *
     *  @param prop The name of the static property to watch.
     *  @param event The event type that indicates the static property has changed.
     *  @param listeners The binding objects that are listening to this Watcher.
     *  @param propertyGetter A helper function used to access non-public variables.
     */
    public function StaticPropertyWatcher(propertyName:String,
                                          events:Object,
                                          listeners:Array,
                                          propertyGetter:Function = null)
    {
        super(listeners);

        _propertyName = propertyName;
        this.events = events;
        this.propertyGetter = propertyGetter;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The parent class of this static property.
     */
    private var parentObj:Class;

    /**
     *  The events that indicate the static property has changed
     */
    protected var events:Object;

    /**
     *  Storage for the propertyGetter property.
     */
    private var propertyGetter:Function;

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
        // The assumption is that parent is of type, Class, and that
        // the class has a static variable or property,
        // staticEventDispatcher, of type IEventDispatcher.
        parentObj = Class(parent);

        if (parentObj["staticEventDispatcher"] != null)
        {
            for (var eventType:String in events)
            {
                if (eventType != "__NoChangeEvent__")
                {
                    var eventDispatcher:IEventDispatcher = parentObj["staticEventDispatcher"];

                    eventDispatcher.addEventListener(eventType, eventHandler, false,
                                                     EventPriority.BINDING, true);
                }
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
        var clone:StaticPropertyWatcher = new StaticPropertyWatcher(_propertyName,
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
    private function traceInfo():String
    {
        return ("StaticPropertyWatcher(" + parentObj + "." + _propertyName + 
                "): events = [" + eventNamesToString() + "]");
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
     *  Gets the actual property then updates
     *  the Watcher's children appropriately.
     */
    private function updateProperty():void
    {
        if (parentObj)
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
