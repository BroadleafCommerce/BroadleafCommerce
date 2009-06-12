////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc
{

import mx.core.mx_internal;
import mx.messaging.Producer;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;

use namespace mx_internal;

/**
 *  The AsyncRequest class provides an abstraction of messaging for RPC call invocation.
 *  An AsyncRequest allows multiple requests to be made on a remote destination
 *  and will call back to the responder specified within the request when
 *  the remote request is completed.
 */
public class AsyncRequest extends mx.messaging.Producer
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

	/**
	 *  Constructs a new asynchronous request.
	 */
	public function AsyncRequest()
	{
		super();
	}
	
    //--------------------------------------------------------------------------
    //
    // Public Methods
    // 
    //--------------------------------------------------------------------------

	/**
	 *  Delegates to the results to responder
	 *  @param    ack Message acknowlegdement of message previously sent
	 *  @param    msg Message that was recieved the acknowledgement
	 *  @private
	 */
	override public function acknowledge(ack:AcknowledgeMessage, msg:IMessage):void
	{
        var error:Boolean = ack.headers[AcknowledgeMessage.ERROR_HINT_HEADER];
        // super will clean the error hint from the message
        super.acknowledge(ack, msg);
        // if acknowledge is *not* for a message that caused an error
        // dispatch a result event
        if (!error)
        {
			var act:String = ack.correlationId;
			var resp:IResponder = IResponder(_pendingRequests[act]);
			if (resp)
			{
				delete _pendingRequests[act];
				resp.result(MessageEvent.createEvent(MessageEvent.RESULT, ack));
			}
		}
	}
	
	/**
	 *  Delegates to the fault to responder
	 *  @param    error message.
	 *            The error codes and informaton are contained in the
	 *            <code>headers</code> property
	 *  @param    msg Message original message that caused the fault.
	 *  @private
	 */
	override public function fault(errMsg:ErrorMessage, msg:IMessage):void
	{
	    super.fault(errMsg, msg);		
	    
	    if (_ignoreFault)
	    	return;     	       	    	       
	    
        // This used to use the errMsg.correlationId here but
        // if the server fails to deserialize the message (like if
        // the body references a non-existent server class)
        // it cannot supply a correlationId to the error message.  
        var act:String = msg.messageId;
		var resp:IResponder = IResponder(_pendingRequests[act]); 
		if (resp)
		{
			delete _pendingRequests[act];
			resp.fault(MessageFaultEvent.createEvent(errMsg));
		}
	}		
	
   /**
    * Returns <code>true</code> if there are any pending requests for the passed in message.
    * 
    * @param msg The message for which the existence of pending requests is checked.
    *
    * @return Returns <code>true</code> if there are any pending requests for the 
    * passed in message; otherwise, returns <code>false</code>.
    */
	override public function hasPendingRequestForMessage(msg:IMessage):Boolean
	{
		var act:String = msg.messageId;
		return _pendingRequests[act];
	}
	

	/**
	 *  Dispatches the asynchronous request and stores the responder to call
	 *  later.
         *
         * @param msg The message to be sent asynchronously.
         *
         * @param responder The responder to be called later.
	 */
	public function invoke(msg:IMessage, responder:IResponder):void
	{
		_pendingRequests[msg.messageId] = responder;
		send(msg);
	}
	
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  manages a list of all pending requests.  each request must implement
	 *  IResponder
	 */
	private var _pendingRequests:Object = {};
}
}
