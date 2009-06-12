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
import mx.messaging.messages.ErrorMessage;

/**
 * The MessageFaultEvent class is used to propagate fault messages within the messaging system.
 */
public class MessageFaultEvent extends Event
{
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------    

    /**
     *  The FAULT event type; dispatched for a message fault.
     *  <p>The value of this constant is <code>"fault"</code>.</p>
     * 
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>   
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>faultCode</code></td><td>Provides destination-specific
     *       details of the failure.</td></tr>
     *     <tr><td><code>faultDetail</code></td><td>Provides access to the
     *       destination-specific reason for the failure.</td></tr>
     *     <tr><td><code>faultString</code></td><td>Provides access to the underlying
     *        reason for the failure if the channel did not raise the failure itself.</td></tr>
     *     <tr><td><code>message</code></td><td>The ErrorMessage for this event.</td></tr>    
     *     <tr><td><code>rootCause</code></td><td> Provides access to the underlying reason
     *       for the failure, if one exists.</td></tr>         
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *  @eventType fault
     */     
    public static const FAULT:String = "fault";

    //--------------------------------------------------------------------------
    //
    // Static Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Utility method to create a new MessageFaultEvent that doesn't bubble and
     *  is not cancelable.
     * 
     *  @param message The ErrorMessage associated with the fault.
     * 
     *  @return New MessageFaultEvent.
     */
    public static function createEvent(msg:ErrorMessage):MessageFaultEvent
    {
        return new MessageFaultEvent(MessageFaultEvent.FAULT, false, false, msg);
    }

    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Constructs an instance of a fault message event for the specified message
     *  and fault information.
     * 
     *  @param type The type for the MessageAckEvent.
     * 
     *  @param bubbles Specifies whether the event can bubble up the display 
     *  list hierarchy.
     * 
     *  @param cancelable Indicates whether the behavior associated with the 
     *  event can be prevented.
     * 
     *  @param message The ErrorMessage associated with the fault.
     */
    public function MessageFaultEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false,
            message:ErrorMessage = null)
    {
        super(type, bubbles, cancelable);

        this.message = message;
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------    

    /**
     *  The ErrorMessage for this event.
     */
    public var message:ErrorMessage;
    
    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  faultCode
	//----------------------------------

    /**
     *  Provides access to the destination specific failure code.
     *  For more specific details see <code>faultString</code> and
     *  <code>faultDetails</code> properties.
     *
     *  <p>The format of the fault codes are provided by the remote destination,
     *  but, will typically have the following form: <i>host.operation.error</i>
     *  For example, <code>"Server.Connect.Failed"</code></p>
     *
     *  @see #faultString
     *  @see #faultDetail
     */
    public function get faultCode():String
    {
        return message.faultCode;
    }

	//----------------------------------
	//  faultDetail
	//----------------------------------

    /**
     *  Provides destination specific details of the failure.
     *
     *  <p>Typically fault details are a stack trace of an exception thrown at
     *  the remote destination.</p>
     *
     *  @see #faultString
     *  @see #faultCode
     */
    public function get faultDetail():String
    {
        return message.faultDetail;
    }

	//----------------------------------
	//  faultString
	//----------------------------------

    /**
     *  Provides access to the destination specific reason for the failure.
     *
     *  @see #faultCode
     *  @see #faultDetail
     */
    public function get faultString():String
    {
        return message.faultString;
    }

	//----------------------------------
	//  rootCause
	//----------------------------------

    /**
     *  Provides access to the root cause of the failure, if one exists.
     *
     *  In the case of custom exceptions thrown by a destination, the root cause
     *  represents the top level failure that is merely transported by the
     *  ErrorMessage.
     *
     *  @see MessageFaultEvent#rootCause
     */
    public function get rootCause():Object
    {
        return message.rootCause;
    }

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    // 
    //--------------------------------------------------------------------------        

    /**
     *  Clones the MessageFaultEvent.
     *
     *  @return Copy of this MessageFaultEvent.
     */
    override public function clone():Event
    {
        return new MessageFaultEvent(type, bubbles, cancelable, message);
    }
    
    /**
     *  Returns a string representation of the MessageFaultEvent.
     *
     *  @return String representation of the MessageFaultEvent.
     */
    override public function toString():String
    {
        return formatToString("MessageFaultEvent", "faultCode", "faultDetail", "faultString", "rootCause", "type", "bubbles", "cancelable", "eventPhase");
    }
}

}
