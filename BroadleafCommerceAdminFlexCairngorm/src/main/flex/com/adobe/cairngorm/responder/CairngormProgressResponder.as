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

package com.adobe.cairngorm.responder
{
import flash.events.Event;
import flash.events.ProgressEvent;

/**
 *  Dispatched to reflect the process execution progress.
 *
 *  @eventType flash.events.ProgressEvent.PROGRESS
 */
[Event(name="progress", type="flash.events.ProgressEvent")]

/**
* <strong>Cairngorm 3</strong> Base class of responders that provide progress information.
*/
public class CairngormProgressResponder extends CairngormResponder implements ICairngormProgressResponder
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  bytesLoaded
    //----------------------------------    
    
    private var _bytesLoaded:Number;
    
    [Bindable("progress")]
    /**
    * The number of items or bytes loaded when the listener processes the event.
    */
    public function get bytesLoaded():Number {
    	return _bytesLoaded;
    }

    //----------------------------------
    //  bytesTotal
    //----------------------------------        
    
    private var _bytesTotal:Number;
    
    [Bindable("progress")]
	/**
	 * The total number of items or bytes that will be loaded if the loading process succeeds.
	 * 
	 * If the progress event is dispatched/attached to a Socket object, the bytesTotal will always
	 * be 0 unless a value is specified in the bytesTotal parameter of the constructor. The actual
	 * number of bytes sent back or forth is not set and is up to the application developer.
	 */
    public function get bytesTotal():Number {
    	return _bytesTotal;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Protected methods: EventDispatcher
    //
    //--------------------------------------------------------------------------
    
    /**
    * Dispatches progress event.
    */
    protected function dispatchProgress(bytesLoaded:Number, bytesTotal:Number):Boolean {
    	var event:ProgressEvent = new ProgressEvent(ProgressEvent.PROGRESS);
    	event.bytesLoaded = bytesLoaded;
    	event.bytesTotal = bytesTotal;
    	return dispatchEvent(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Overriden methods: EventDispatcher
    //
    //--------------------------------------------------------------------------
    
    /**
    * @inheritDoc
    */
    override public function dispatchEvent(event:Event):Boolean {
    	if (event is ProgressEvent && event.type == ProgressEvent.PROGRESS) {
    		_bytesLoaded = ProgressEvent(event).bytesLoaded;
    		_bytesTotal = ProgressEvent(event).bytesTotal;
    	}
    	
		return super.dispatchEvent(event);
	}
}
}