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
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class RepeaterComponentWatcher extends PropertyWatcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
     *
     *  Create a RepeaterComponentWatcher
     *
     *  @param prop The name of the property to watch.
     *  @param event The event type that indicates the property has changed.
     *  @param listeners The binding objects that are listening to this Watcher.
     *  @param propertyGetter A helper function used to access non-public variables.
	 */
    public function RepeaterComponentWatcher(propertyName:String,
                                             events:Object,
                                             listeners:Array,
                                             propertyGetter:Function = null)
    {
		super(propertyName, events, listeners, propertyGetter);
    }

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    private var clones:Array;

	/**
	 *  @private
	 */
    private var original:Boolean = true;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Watcher
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    override public function updateChildren():void
    {
        if (original)
        {
            updateClones();
        }
        else
        {
            super.updateChildren();
        }
    }

	/**
	 *  @private
	 */
    override protected function shallowClone():Watcher
    {
        return new RepeaterComponentWatcher(propertyName, events, listeners, propertyGetter);
    }

	/**
	 *  @private
	 */
    private function updateClones():void
    {
        var components:Array = value as Array;

        if (components)
        {
            if (clones)
                clones = clones.splice(0, components.length);
            else
                clones = [];

            for (var i:int = 0; i < components.length; i++)
            {
                var clone:RepeaterComponentWatcher = RepeaterComponentWatcher(clones[i]);
                
                if (!clone)
                {
                    clone = RepeaterComponentWatcher(deepClone(i));
                    clone.original = false;
                    clones[i] = clone;
                }

                clone.value = components[i];
                clone.updateChildren();
            }
        }
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
     *  Invokes super's notifyListeners() on each of the clones.
     */
    override public function notifyListeners(commitEvent:Boolean):void
    {
        if (original)
        {
            if (clones)
            {
                for (var i:int = 0; i < clones.length; i++)
                {
                    RepeaterComponentWatcher(clones[i]).notifyListeners(commitEvent);
                }
            }
        }

        super.notifyListeners(commitEvent);
    }
}

}
