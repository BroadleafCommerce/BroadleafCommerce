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
import mx.core.EventPriority;
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class FunctionReturnWatcher extends Watcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Constructor.
	 */
	public function FunctionReturnWatcher(functionName:String,
										  document:Object,
										  parameterFunction:Function,
										  events:Object,
                                          listeners:Array,
                                          functionGetter:Function = null)
    {
		super(listeners);

        this.functionName = functionName;
        this.document = document;
        this.parameterFunction = parameterFunction;
        this.events = events;
        this.functionGetter = functionGetter;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
     *  The name of the property, used to actually get the property
	 *  and for comparison in propertyChanged events.
     */
    private var functionName:String;
    
	/**
 	 *  @private
     *  The document is what we need to use toe execute the parameter function.
     */
    private var document:Object;
    
	/**
 	 *  @private
     *  The function that will give us the parameters for calling the function.
     */
    private var parameterFunction:Function;
    
    /**
 	 *  @private
     *  The events that indicate the property has changed.
     */
    private var events:Object;
    
	/**
	 *  @private
     *  The parent object of this function.
     */
    private var parentObj:Object;
    
	/**
	 *  @private
     *  The watcher holding onto the parent object.
     */
    public var parentWatcher:Watcher;

    /**
     *  Storage for the functionGetter property.
     */
    private var functionGetter:Function;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
 	 *  @private
     */
    override public function updateParent(parent:Object):void
    {
        if (!(parent is Watcher))
            setupParentObj(parent);
        
		else if (parent == parentWatcher)
            setupParentObj(parentWatcher.value);
        
		updateFunctionReturn();
    }

    /**
 	 *  @private
     */
    override protected function shallowClone():Watcher
    {
        var clone:FunctionReturnWatcher = new FunctionReturnWatcher(functionName,
                                                                    document,
                                                                    parameterFunction,
                                                                    events,
                                                                    listeners,
                                                                    functionGetter);

        return clone;
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
 	 *  @private
     *  Get the new return value of the function.
     */
    public function updateFunctionReturn():void
    {
        wrapUpdate(function():void
		{
            if (functionGetter != null)
            {
                value = functionGetter(functionName).apply(parentObj,
                                                           parameterFunction.apply(document));
            }
            else
            {
                value = parentObj[functionName].apply(parentObj,
                                                      parameterFunction.apply(document));
            }
			
			updateChildren();
		});
    }

    /**
 	 *  @private
     */
    private function setupParentObj(newParent:Object):void
    {
		var eventDispatcher:IEventDispatcher;
        var p:String;

        if (parentObj != null &&
            parentObj is IEventDispatcher &&
            events != null)
        {
            eventDispatcher = parentObj as IEventDispatcher;
            
			for (p in events)
            {
                eventDispatcher.removeEventListener(p, eventHandler);
            }
        }
        
		parentObj = newParent;
        
        if (parentObj != null &&
            parentObj is IEventDispatcher &&
            events != null)
        {
            eventDispatcher = parentObj as IEventDispatcher;

            for (p in events)
            {
                if (p != "__NoChangeEvent__")
				{
                    eventDispatcher.addEventListener(
						p, eventHandler, false, EventPriority.BINDING, true);
				}
            }
        }
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
 	 *  @private
     */
    public function eventHandler(event:Event):void
    {
        updateFunctionReturn();

        notifyListeners(events[event.type]);
    }
}

}
