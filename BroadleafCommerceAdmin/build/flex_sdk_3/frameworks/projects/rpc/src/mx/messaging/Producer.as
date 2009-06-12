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

import flash.errors.IllegalOperationError;
import flash.events.TimerEvent;
import flash.utils.Timer;
import mx.core.mx_internal;
import mx.logging.Log;
import mx.events.PropertyChangeEvent;
import mx.messaging.errors.InvalidDestinationError;
import mx.messaging.errors.MessagingError;
import mx.messaging.events.ChannelEvent;
import mx.messaging.events.ChannelFaultEvent;
import mx.messaging.events.MessageEvent;
import mx.messaging.events.MessageFaultEvent;
import mx.messaging.messages.AbstractMessage;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.IMessage;

use namespace mx_internal;

/**
 *  A Producer sends messages to a destination.
 *  Producers dispatch a MessageAckEvent or MessageFaultEvent 
 *  for each message they send depending upon whether the outbound message
 *  was sent and processed successfully or not.
 *  @mxml
 *  <p>
 *  The &lt;mx:Producer&gt; tag inherits all the tag attributes of its superclass, and adds the following tag attributes:
 *  </p>
 *  <pre>
 *   &lt;mx:Producer
 *    <b>Properties</b>
 *    defaultHeaders="<i>No default.</i>"
 *  /&gt;
 *  </pre>
 */
public class Producer extends AbstractProducer
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructs a Producer.
     * 
     *  @example
     *  <pre>
     *   function sendMessage():void
     *   {
     *       var producer:Producer = new Producer();
     *       producer.destination = "NASDAQ";
     *       var msg:AsyncMessage = new AsyncMessage();
     *       msg.headers.operation = "UPDATE";
     *       msg.body = {"SYMBOL":50.00};
     *       producer.send(msg);
     *   }
     *   </pre>
     */
	public function Producer()
	{
		super();
	    _log = Log.getLogger("mx.messaging.Producer");
		_agentType = "producer";
	}

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  subtopic
	//----------------------------------	

    /**
     *  @private
     */
    private var _subtopic:String = "";
    
    [Bindable(event="propertyChange")]
    
    /**
     *  Provides access to the subtopic for the remote destination that the MessageAgent uses.
     */
    public function get subtopic():String
    {
        return _subtopic;
    }
    
    /**
     *  @private
     */
    public function set subtopic(value:String):void
    {
        if (_subtopic != value)
        {
            var event:PropertyChangeEvent;
            if (value == null)
                value = "";

            event = PropertyChangeEvent.createUpdateEvent(this, "subtopic", _subtopic, value);
            _subtopic = value;                

            dispatchEvent(event);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */ 
    override protected function internalSend(message:IMessage, waitForClientId:Boolean = true):void
    {
        if (subtopic.length > 0)
            message.headers[AsyncMessage.SUBTOPIC_HEADER] = subtopic;    

        super.internalSend(message, waitForClientId);
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    // 
    //--------------------------------------------------------------------------
	
}

}
