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
import mx.core.IRepeaterClient;
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class RepeatableBinding extends Binding
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Create a Binding object
	 *
     *  @param document The document that is the target of all of this work.
	 *
     *  @param srcFunc The function that returns us the value
	 *  to use in this Binding.
	 *
     *  @param destFunc The function that will take a value
	 *  and assign it to the destination.
	 *
     *  @param destString The destination represented as a String.
	 *  We can then tell the ValidationManager to validate this field.
     */
    public function RepeatableBinding(document:Object, srcFunc:Function,
									  destFunc:Function, destString:String)
    {
        super(document, srcFunc, destFunc, destString);
    }

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Execute the binding.
     *  Call the source function and get the value we'll use.
     *  Then call the destination function passing the value as an argument.
     */
    override public function execute(o:Object = null):void
    {
        if (isExecuting)
            return;

        isExecuting = true;
    
        // o is an array index, a single instance of a UIComponent,
		// a Repeater, or is null.
        // If it is a number it is because a Watcher fired
		// and we are being passed the cloneIndex
        // If it is defined as an Object, it is because the Binding Manager
		// just called executeBindings() on that particular instance,
		// and passed it in.
        // If it is null (now unlikely for RepeatableBinding) a watcher
		// has just fired and we will execute this RepeatableBinding
		// on all repeated instances of the object specified by
		// the _destString of this RepeatableBinding.
        // For example, if the _destString is "b.label", we update
        // all instances with id "b", which we locate via their indexed
        // id references on the document, such as b[2][4].
        var id:String;
        if (!o)
        {
            id = destString.substring(0, destString.indexOf("."));
            o = document[id];
        }
        else if (typeof(o) == "number")
        {
            id = destString.substring(0, destString.indexOf("."));
            var components:Array = document[id] as Array;
            if (components)
                o = components[o];
            else
                o = null;
        }

        if (o)
            recursivelyProcessIDArray(o);
   
        isExecuting = false;
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
	private function recursivelyProcessIDArray(o:Object):void
    {
        // o is either a scalar id reference (to a UIComponent or a Repeater)
        // or an array, perhaps multi-dimensional, of id references

        if (o is Array)
        {
            var array:Array = o as Array;
			var n:int = array.length;
            for (var i:int = 0; i < n; i++)
            {
                recursivelyProcessIDArray(array[i]);
            }
        }
        else if (o is IRepeaterClient)
        {
            var client:IRepeaterClient = IRepeaterClient(o);

            wrapFunctionCall(this, function():void
            {
                var value:Object = wrapFunctionCall(this, srcFunc, null, client.instanceIndices, client.repeaterIndices);

                if (BindingManager.debugDestinationStrings[destString])
                {
                    trace("RepeatableBinding: destString = " + destString + ", srcFunc result = " + value);
                }

                destFunc(value, client.instanceIndices);
            },
            o);
        }
    }

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

    /**
     *  The only reason a Binding listens to an event
	 *  is because it wants a signal to execute
     */
    public function eventHandler(event:Event):void
    {
        if (isHandlingEvent)
            return;
        isHandlingEvent = true;

        execute();

        isHandlingEvent = false;
    }
}

}
