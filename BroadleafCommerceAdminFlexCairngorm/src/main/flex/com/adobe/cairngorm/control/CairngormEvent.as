/*

Copyright (c) 2006. Adobe Systems Incorporated.
All rights reserved.

Includes "Cairngorm 3" extensions developed by Marcin Hagmajer, mhagmajer@gmail.com.
Find out more at: http://students.mimuw.edu.pl/~mhagmajer/cairngorm3/.
The note below applies to the modifications as well.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
  * Neither the name of Adobe Systems Incorporated nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

@ignore
*/

package com.adobe.cairngorm.control
{
import flash.events.Event;
import flash.events.IEventDispatcher;

/**
 * The CairngormEvent class is used to differentiate Cairngorm events 
 * from events raised by the underlying Flex framework (or
 * similar). It is mandatory for Cairngorm event dispatching. 
 * 
 * <p>For more information on how event dispatching works in Cairngorm, 
 * please check with CairngormEventDispatcher.</p>
 * <p>
 * Events are typically broadcast as the result of a user gesture occuring
 * in the application, such as a button click, a menu selection, a double
 * click, a drag and drop operation, etc.  
 * </p>
 *
 * @see com.adobe.cairngorm.control.CairngormEventDispatcher
 */
public class CairngormEvent extends Event
{	
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------	
	
	/**
	 * Constructor, takes the event name (type) and data object (defaults to null)
	 * and also defaults the standard Flex event properties bubbles and cancelable
	 * to true and false respectively.
	 */
	public function CairngormEvent(type:String, bubbles:Boolean = false,
		cancelable:Boolean = false)
	{
    	super(type, bubbles, cancelable);
	}

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  data
    //----------------------------------

    /**
     * The data property can be used to hold information to be passed with the event
     * in cases where the developer does not want to extend the CairngormEvent class.
     * However, it is recommended that specific classes are created for each type
     * of event to be dispatched.
     */
    public var data : *;
    
    //----------------------------------
    //  handler
    //----------------------------------

	/**
	 * @private
	 */    
    private var _completeHandlerFunction:Function;
    
    /**
    * Handler function which is called on the commands result or fault
    * example: handler(responder:ICairngormResponder);
    */
    public function get completeHandlerFunction():Function {
    	return _completeHandlerFunction;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------   

    /**
     * Dispatches this event via the Cairngorm event dispatcher.
     * 
     * @param completeHandlerFunction Function which is to be called after the command execution
     * finishes. The function should take one parameter (responder:ICairngormResponder).
     */
    public function dispatch(completeHandlerFunction:Function = null):Boolean
    {
    	_completeHandlerFunction = completeHandlerFunction;
        return CairngormEventDispatcher.getInstance().dispatchEvent(this);
    }    
}
}