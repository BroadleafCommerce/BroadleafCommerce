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

package mx.messaging.events
{

import flash.events.Event;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.IMessage;

/**
 *  The MessageAckEvent class is used to propagate acknowledge messages within the messaging system.
 */
public class MessageAckEvent extends MessageEvent
{    
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------    

    /**
     *  The ACKNOWLEDGE event type; dispatched upon receipt of an acknowledgement.
     *  <p>The value of this constant is <code>"acknowledge"</code>.</p>
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>acknowledgeMessage</code></td><td> Utility property to get
     *       the message property from MessageEvent as an AcknowledgeMessage.</td></tr> 
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>correlate</code></td><td> The original Message correlated with
     *       this acknowledgement.</td></tr>
     *     <tr><td><code>message</code></td><td>The Message associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *  @eventType acknowledge    
     *
     */     
    public static const ACKNOWLEDGE:String = "acknowledge";

    //--------------------------------------------------------------------------
    //
    // Static Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Utility method to create a new MessageAckEvent that doesn't bubble and
     *  is not cancelable.
     * 
     *  @param ack The AcknowledgeMessage this event should dispatch.
     *  
     *  @param correlation The Message correlated with this acknowledgement.
     * 
     *  @return New MessageAckEvent.
     */
    public static function createEvent(ack:AcknowledgeMessage = null, correlation:IMessage = null):MessageAckEvent
    {
        return new MessageAckEvent(MessageAckEvent.ACKNOWLEDGE, false, false, ack, correlation);
    }
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Constructs an instance of this event with the specified acknowledge
     *  message and original correlated message.
     *
     *  @param type The type for the MessageAckEvent.
     * 
     *  @param bubbles Specifies whether the event can bubble up the display 
     *  list hierarchy.
     * 
     *  @param cancelable Indicates whether the behavior associated with the 
     *  event can be prevented.
     * 
     *  @param ack The AcknowledgeMessage this event should dispatch.
     *  
     *  @param correlation The message correlated with this acknowledgement.
     */
    public function MessageAckEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false, 
            ack:AcknowledgeMessage = null, correlation:IMessage = null)
    {
        super(type, bubbles, cancelable, ack);

        this.correlation = correlation;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------    

    /**
     *  The original Message correlated with this acknowledgement.
     */
    public var correlation:IMessage;
    
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------        
    
	//----------------------------------
	//  acknowledgeMessage
	//----------------------------------
    
    /**
     *  Utility property to get the message property from the MessageEvent as an AcknowledgeMessage.  
     */
    public function get acknowledgeMessage():AcknowledgeMessage
    {
        return message as AcknowledgeMessage;
    }

	//----------------------------------
	//  correlationId
	//----------------------------------
    
    /**
     *  @private
     */
    public function get correlationId():String
    {
        if (correlation != null)
        {
            return correlation.messageId;
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    // 
    //--------------------------------------------------------------------------        

    /**
     *  Clones the MessageAckEvent.
     *
     *  @return Copy of this MessageAckEvent.
     */
    override public function clone():Event
    {
        return new MessageAckEvent(type, bubbles, cancelable, message as AcknowledgeMessage, correlation);
    }
    
    /**
     *  Returns a string representation of the MessageAckEvent.
     *
     *  @return String representation of the MessageAckEvent.
     */
    override public function toString():String
    {
        return formatToString("MessageAckEvent", "messageId", "correlationId", "type", "bubbles", "cancelable", "eventPhase");
    }
}

}