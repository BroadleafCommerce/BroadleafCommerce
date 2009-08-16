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
import com.adobe.cairngorm.business.Delegate;
import com.adobe.cairngorm.control.CairngormEvent;

import flash.events.Event;
import flash.events.EventDispatcher;

import mx.rpc.IResponder;

/**
 *  Dispatched at the end of command execution.
 *
 *  @eventType flash.events.Event.COMPLETE
 */
[Event(name="complete", type="flash.events.Event")]

[Bindable("complete")]
/**
 * <strong>Cairngorm 3</strong> Base responder class.
 */
public class CairngormResponder extends EventDispatcher implements ICairngormResponder, IResponder
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------		
	
	//----------------------------------
    //  event
    //----------------------------------

    /**
    * Event that made the controller execute this command.
    */
    public function get event():CairngormEvent {
    	return _event;
    }
    
    /**
    * Called only by the FrontController.
    * 
    * @private
    */    
    public function set event(value:CairngormEvent):void {
    	_event = value;
    }

    private var _event:CairngormEvent;

	//----------------------------------
    //  asynchronous
    //----------------------------------

    /**
    * Indicates whether the execution of command finished in execute() call.
    */
    public function get asynchronous():Boolean {
    	return _asynchronous;
    }
    
    /**
    * Called only by the FrontController.
    * 
    * @private
    */    
    public function set asynchronous(value:Boolean):void {
    	_asynchronous = value;
    }

    private var _asynchronous:Boolean = false;

    //----------------------------------
    //  active
    //----------------------------------

	/**
	 * Indicates wheter the command is still being executed.
	 */
    public function get active():Boolean {
        return _active;
    }
    
    private var _active:Boolean = true;
    
    //----------------------------------
    //  succeded
    //----------------------------------
    
    /**
    * Indicates whether the execution finished with normal result.
    */
    public function get succeded():Boolean {
        return _succeded;
    }
    
    private var _succeded:Boolean = false;

    //----------------------------------
    //  resultData
    //----------------------------------
        
    /**
    * Result data passed in super.result() call.
    */
    public function get resultData():Object {
    	return _resultData;
    }

    private var _resultData:Object;    
    
    //----------------------------------
    //  failed
    //----------------------------------
    
    /**
    * Indicates whether the execution finished with error.
    */
    public function get failed():Boolean {
        return _failed;
    }
    
    private var _failed:Boolean = false;    
    
    //----------------------------------
    //  faultInfo
    //----------------------------------
    
    /**
    * Fault info passed in super.fault() call.
    */
    public function get faultInfo():Object {
    	return _faultInfo;
    } 

	private var _faultInfo:Object;

    //----------------------------------
    //  delegate
    //----------------------------------

	/**
	 * Reference to delegate used during the execution.
	 * 
	 * It prevents the garbage collector from removing the unreferenced delegate.
	 */
    protected var delegate:Delegate;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------		

	/**
	 * Finishes the command with normal result (data).
	 * 
	 * @param data result data 
	 */
    public function result(data:Object):void {
    	if (!active) {
    		return;
    	}
    	
    	_resultData = data;
    	_succeded = true;    	
		_active = false;
				
	    dispatchEvent(new Event(Event.COMPLETE));
    }

	/**
	 * Finishes the command with fault (info).
	 * 
	 * @param data fault info (usually an Error object)
	 */
    public function fault(info:Object):void {
    	if (!active) {
    		return;
    	}
    	
    	_faultInfo = info;
    	_failed = true;
		_active = false;
				
	    dispatchEvent(new Event(Event.COMPLETE));
    }
}
}