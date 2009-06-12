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

package mx.messaging
{

import flash.events.TimerEvent;
import flash.net.Responder;
import flash.utils.Timer;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.ErrorMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("messaging")]

/**
 *  The MessageResponder class handles a successful result or fault from a message
 *  destination. For each message that a Channel sends, the Channel creates a
 *  MessageResponder to handle the result. Upon a response, the Channel will
 *  invoke either the <code>result()</code> or <code>status()</code> callback
 *  on the MessageResponder. MessageResponder subclasses should override these
 *  methods to perform any necessary processing. For every response, whether a 
 *  successful result or an error, the MessageResponder should invoke 
 *  <code>acknowledge()</code> on its agent. If the response was a fault, the
 *  MessageResponder should also invoke <code>fault()</code> on its agent.
 */
public class MessageResponder extends Responder
{
	//--------------------------------------------------------------------------
	//
	// Constructor
	// 
	//--------------------------------------------------------------------------
    
    /**
     *  Constructs a MessageResponder to handle the response for the specified
     *  Message for the specified MessageAgent.
     *
     *  @param  agent The MessageAgent sending the Message.
     * 
     *  @param  message The Message being sent.
     * 
     *  @param channel The Channel used to send. 
     */
    public function MessageResponder(agent:MessageAgent, message:IMessage,
                                                        channel:Channel = null)
    {
        super(result, status);

        _agent = agent;
        _channel = channel;
        _message = message;
        _requestTimedOut = false;       
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Flag indicating whether the request corresponding to this responder
	 *  has timed out. This is used by responders that cannot close
	 *  their underlying connection (NetConnection for instance) so they must 
	 *  instead ignore any response that is returned after the request timeout 
	 *  is reached.
	 */
	private var _requestTimedOut:Boolean;
	
	/**
	 *  @private
	 *  Timer used to trigger a request timeout.
	 */
	private var _requestTimer:Timer;   
    
    /**
     * @private
     */
    private var resourceManager:IResourceManager =
									ResourceManager.getInstance();
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  agent
	//----------------------------------
    
    /**
     *  @private
     */
    private var _agent:MessageAgent;
    
    /**
     *  Provides access to the MessageAgent that sent the message.
     */
    public function get agent():MessageAgent
    {
        return _agent;
    }
    
	//----------------------------------
	//  channel
	//----------------------------------
    
    /**
     *  @private
     */
    private var _channel:Channel;
    
    /**
     *  Provides access to the Channel used to send the message.
     */
    public function get channel():Channel
    {
        return _channel;   
    }

	//----------------------------------
	//  message
	//----------------------------------

    /**
     *  @private
     */
    private var _message:IMessage;

    /**
     *  Provides access to the sent Message.
     */
    public function get message():IMessage
    {
        return _message;
    }

    /**
     * @private
     */
    public function set message(value:IMessage):void
    {
        _message = value;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------    

    /**
     *  @private 
     *  Starts a timer to monitor a request timeout. If the timer hits the
     *  specified requestTimeout before a response is returned, 
     *  <code>requestTimedOut()</code> is invoked and any subsequent 
     *  response is ignored by this responder.
     * 
     *  @param requestTimeout The amount of time in seconds to allow a request
     *                        to run before timing it out.
     */
    final public function startRequestTimeout(requestTimeout:int):void
    {
        _requestTimer = new Timer(requestTimeout * 1000, 1);
        _requestTimer.addEventListener(TimerEvent.TIMER, timeoutRequest);
        _requestTimer.start();
    }
    
    /**
     *  Called by the channel that created this MessageResponder when a
     *  response returns from the destination.
     *  This method performs core result processing and then invokes the
     *  <code>resultHandler()</code> method that subclasses may override to
     *  perform any necessary custom processing.
     *
     *  @param message The result Message returned by the destination.
     */
    final public function result(message:IMessage):void 
    {
        // Ignore any response after the request has timed out.
        if (!_requestTimedOut)
        {
            // Shut down the timeout timer if it's alive.
            if (_requestTimer != null)
            {
                releaseTimer();
            }
            resultHandler(message);
        }
    }
    
    /**
     *  Called by the channel that created this MessageResponder when a fault
     *  response returns from the destination.
     *  This method performs core result processing and then invokes the
     *  <code>statusHandler()</code> method that subclasses may override to
     *  perform any necessary custom processing.
     * 
     *  @param message The fault Message returned by the destination.
     */
    final public function status(message:IMessage):void 
    {
        // Ignore any response after the request has timed out.
        if (!_requestTimedOut)
        {
            // Shut down the timeout timer if it's alive.
            if (_requestTimer != null)
            {
                releaseTimer();
            }
            statusHandler(message);
        }
    }
    
	//--------------------------------------------------------------------------
	//
	// Protected Members
	// 
	//--------------------------------------------------------------------------
    
    /**
     *  Constructs an ErrorMessage that can be passed to the associated 
     *  MessageAgent's callbacks upon a request timeout.
     *
     *  @return Returns an ErrorMessage that can be passed to the associated
     *  MessageAgent's callbacks upon a request timeout.
     */
    protected function createRequestTimeoutErrorMessage():ErrorMessage
    {
        var errorMsg:ErrorMessage = new ErrorMessage();
        errorMsg.correlationId = message.messageId;
        errorMsg.faultCode = "Client.Error.RequestTimeout";
        errorMsg.faultString = resourceManager.getString(
			"messaging", "requestTimedOut");
        errorMsg.faultDetail = resourceManager.getString(
			"messaging", "requestTimedOut.details");
        return errorMsg;
    }

    /**
     *  Subclasses must override this method to perform custom processing of
     *  the result and invoke the proper callbacks on the associated 
     *  MessageAgent.
     * 
     *  @param message The result Message returned by the destination.
     */
    protected function resultHandler(message:IMessage):void {}

    /**
     *  Subclasses must override this method to handle a request timeout and 
     *  invoke the proper callbacks on the associated MessageAgent.
     */ 
    protected function requestTimedOut():void {}
    
    /**
     *  Subclasses must override this method to perform custom processing of
     *  the status and invoke the proper callbacks on the associated 
     *  MessageAgent.
     * 
     *  @param message The fault Message returned by the destination.
     */
    protected function statusHandler(message:IMessage):void {}

	//--------------------------------------------------------------------------
	//
	// Private Members
	// 
	//--------------------------------------------------------------------------

    /**
     *  @private
     *  Helper callback that flags the request as timed out before delegating
     *  to custom timeout processing.
     */
    private function timeoutRequest(event:TimerEvent):void
    {
        _requestTimedOut = true;
        releaseTimer();
        requestTimedOut();
    }
    
    /**
     *  @private
     *  Utility method to shutdown the request timeout Timer.
     */
    private function releaseTimer():void
    {
        _requestTimer.stop();
        _requestTimer.removeEventListener(TimerEvent.TIMER, timeoutRequest);
        _requestTimer = null;
    }
}

}
