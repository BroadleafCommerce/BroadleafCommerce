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

import mx.collections.errors.ItemPendingError;
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class Watcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
    public function Watcher(listeners:Array = null)
    {
		super();

        this.listeners = listeners;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     *  The binding objects that are listening to this Watcher.
     *  The standard event mechanism isn't used because it's too heavyweight.
     */
    protected var listeners:Array;

    /**
     *  @private
     *  Children of this watcher are watching sub values.
     */
    protected var children:Array;

    /**
     *  @private
     *  The value itself.
     */
    public var value:Object;

    /**
     *  @private
     *  Keep track of cloning when used in Repeaters.
     */
    protected var cloneIndex:int;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     *  This is an abstract method that subclasses implement.
     */
    public function updateParent(parent:Object):void
	{
	}

    /**
     *  @private
     *  Add a child to this watcher, meaning that the child
	 *  is watching a sub value of ours.
     */
    public function addChild(child:Watcher):void
    {
        if (!children)
            children = [ child ];
        else
            children.push(child);

        child.updateParent(this);
    }

    /**
     *  @private
     *  Remove all children beginning at a starting index.
     *  If the index is not specified, it is assumed to be 0.
     *  This capability is used by Repeater, which must remove
     *  cloned RepeaterItemWatchers (and their descendant watchers).
     */
    public function removeChildren(startingIndex:int):void
    {
        children.splice(startingIndex);
    }

    /**
     *  We have probably changed, so go through
	 *  and make sure our children are updated.
     */
    public function updateChildren():void
    {
        if (children)
        {
            var n:int = children.length;
			for (var i:int = 0; i < n; ++i)
            {
                children[i].updateParent(this);
            }
        }
    }

    /**
     *  @private
     */
    private function valueChanged(oldval:Object):Boolean
    {
        if (oldval == null && value == null)
            return false;

        var valType:String = typeof(value);
        
		// The first check is meant to catch the delayed instantiation case
		// where a control comes into existence but its value is still
		// the equivalent of not having been filled in.
        // Otherwise we simply return whether the value has changed.
        
		if (valType == "string")
        {
            if (oldval == null && value == "")
                return false;
            else
                return oldval != value;
        }
        
		if (valType == "number")
        {
            if (oldval == null && value == 0)
                return false;
            else
                return oldval != value;
        }
        
		if (valType == "boolean")
        {
            if (oldval == null && value == false)
                return false;
            else
                return oldval != value;
        }

        return true;
    }

    /**
	 *  @private
     */
    protected function wrapUpdate(wrappedFunction:Function):void
    {
        try
        {
            wrappedFunction.apply(this);
        }
        catch(itemPendingError:ItemPendingError)
        {
            // The parent's value is not yet available.  This is being ignored for now -
            // updateParent() will be called when the parent has a value.
            value = null;
        }
        catch(rangeError:RangeError)
        {
            // The parent's value is not yet available.  This is being ignored for now -
            // updateParent() will be called when the parent has a value.
            value = null;
        }
        catch(error:Error)
        {
            // Certain errors are normal when executing an update, so we swallow them:
            //   Error #1006: Call attempted on an object that is not a function.
            //   Error #1009: null has no properties.
            //   Error #1010: undefined has no properties.
            //   Error #1055: - has no properties.
            //   Error #1069: Property - not found on - and there is no default value
            // We allow any other errors to be thrown.
            if ((error.errorID != 1006) &&
                (error.errorID != 1009) &&
                (error.errorID != 1010) &&
                (error.errorID != 1055) &&
                (error.errorID != 1069))
            {
                throw error;
            }
        }
    }

    /**
	 *  @private
     *  Clone this Watcher and all its descendants.
     *  Each clone triggers the same Bindings as the original;
     *  in other words, the Bindings do not get cloned.
     *
     *  This cloning capability is used by Repeater in order
     *  to watch the subproperties of multiple dataProvider items.
     *  For example, suppose a repeated LinkButton's label is
     *    {r.currentItem.firstName} {r.currentItem.lastName}
     *  where r is a Repeater whose dataProvider is
     *    [ { firstName: "Matt",   lastName: "Chotin" },
     *      { firstName: "Gordon", lastName: "Smith"  } ]
     *  The MXML compiler emits a watcher tree (one item of _watchers[])
     *  that looks like this:
     *    PropertyWatcher for "r"
     *      PropertyWatcher for "dataProvider"
     *        RepeaterItemWatcher
     *          PropertyWatcher for "firstName"
     *          PropertyWatcher for "lastName"
     *  At runtime the RepeaterItemWatcher serves as a template
     *  which gets cloned for each dataProvider item:
     *    PropertyWatcher for "r"
     *      PropertyWatcher for "dataProvider"
     *        RepeaterItemWatcher               (index: null)
     *          PropertyWatcher for "firstName" (value: null)
     *          PropertyWatcher for "lastName"  (value: null)
     *        RepeaterItemWatcher               (index: 0)
     *          PropertyWatcher for "firstName" (value: "Matt")
     *          PropertyWatcher for "lastName"  (value: "Chotin")
     *        RepeaterItemWatcher               (index: 1)
     *          PropertyWatcher for "firstName" (value: "Gordon")
     *          PropertyWatcher for "lastName"  (value: "Smith")
     */
    protected function deepClone(index:int):Watcher
    {
        // Clone this watcher object itself.
        var w:Watcher = shallowClone();
        w.cloneIndex = index;

        // Clone its listener queue.
        if (listeners)
        {
            w.listeners = listeners.concat();
        }

        // Recursively clone its children.
        if (children)
        {
            var n:int = children.length;
            for (var i:int = 0; i < n; i++)
            {
                var clonedChild:Watcher = children[i].deepClone(index);
                w.addChild(clonedChild);
            }
        }
        
        // Return the cloned tree of watchers.
        return w;
    }

    /**
	 *  @private
     *  Clone this watcher object itself, without cloning its children.
     *  The clone is not connec
     *  Subclasses must override this method to copy their properties.
     */
    protected function shallowClone():Watcher
    {
        return new Watcher();
    }

    /**
	 *  @private
     */
    public function notifyListeners(commitEvent:Boolean):void
    {
        if (listeners)
        {
            var n:int = listeners.length;

            for (var i:int = 0; i < n; i++)
            {
                listeners[i].watcherFired(commitEvent, cloneIndex);
            }
        }
    }
}

}
