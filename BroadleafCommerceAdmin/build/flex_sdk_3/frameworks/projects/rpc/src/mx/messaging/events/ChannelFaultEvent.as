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
import mx.messaging.Channel;
import mx.messaging.messages.ErrorMessage;

/**
 *  The ChannelFaultEvent class is used to propagate channel fault events within the messaging system.
 */
public class ChannelFaultEvent extends ChannelEvent
{
    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------    

    /**
     *  The FAULT event type; indicates that the Channel faulted.
     *  <p>The value of this constant is <code>"channelFault"</code>.</p>
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>channel</code></td><td>The Channel that generated this event.</td></tr>   
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
     *     <tr><td><code>reconnecting</code></td><td> Indicates whether the channel
     *       that generated this event is reconnecting.</td></tr> 
     *     <tr><td><code>rootCause</code></td><td> Provides access to the underlying reason
     *       for the failure if the channel did not raise the failure itself.</td></tr>         
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *  @eventType channelFault
     */        
    public static const FAULT:String = "channelFault";    

    //--------------------------------------------------------------------------
    //
    // Static Methods
    // 
    //--------------------------------------------------------------------------    

    /**
     *  Utility method to create a new ChannelFaultEvent that doesn't bubble and
     *  is not cancelable.
     *  
     *  @param channel The Channel generating the event.
     * 
     *  @param reconnecting Indicates whether the Channel is in the process of
     *  reconnecting or not.
     * 
     *  @param code The fault code.
     * 
     *  @param level The fault level.
     * 
     *  @param description The fault description.
     * 
     *  @param rejected Indicates whether the Channel's connection has been rejected,
     *  which suppresses automatic reconnection.
     * 
     *  @param connected Indicates whether the Channel that generated this event 
     *  is already connected.
     * 
     *  @return New ChannelFaultEvent.
     */
    public static function createEvent(channel:Channel, reconnecting:Boolean = false, 
            code:String = null, level:String = null, description:String = null,
            rejected:Boolean = false, connected:Boolean = false):ChannelFaultEvent
    {
        return new ChannelFaultEvent(ChannelFaultEvent.FAULT, false, false, 
                channel, reconnecting, code, level, description, rejected, connected);
    }

    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Constructs an instance of this event with the specified type.
     *  Note that the <code>rejected</code> and <code>connected</code> arguments that correspond to properties
     *  defined by the super-class <code>ChannelEvent</code> were not originally included in this method signature and have been 
     *  added at the end of the argument list to preserve backward compatibility even though this signature differs from 
     *  <code>ChannelEvent</code>'s constructor.
     * 
     *  @param type The type of the event.
     *
     *  @param bubbles Indicates whether the event can bubble up the display list hierarchy.
     *
     *  @param cancelable Indicates whether the behavior associated with the event can be prevented.
     *
     *  @param channel The Channel generating the event.
     * 
     *  @param reconnecting Indicates whether the Channel is in the process of
     *                      reconnecting or not.
     * 
     *  @param code The fault code.
     * 
     *  @param level The fault level.
     * 
     *  @param description The fault description.
     * 
     *  @param rejected Indicates whether the Channel's connection has been rejected,
     *  which suppresses automatic reconnection.
     * 
     *  @param connected Indicates whether the Channel that generated this event 
     *  is already connected.
     */
    public function ChannelFaultEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false,
            channel:Channel = null, reconnecting:Boolean = false, code:String = null, level:String = null, description:String = null,
            rejected:Boolean = false, connected:Boolean = false)
    {
        super(type, bubbles, cancelable, channel, reconnecting, rejected, connected);

        faultCode = code;
        faultString = level;
        faultDetail = description;
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------    

    /**
     *  Provides access to the destination-specific failure code. For more 
     *  specific details see the <code>faultString</code> and 
     *  <code>faultDetails</code> properties.
     *
     *  <p>The format of the fault codes are provided by the remote destination, 
     *  but will typically have the following form: <i>host.operation.error</i> 
     *  e.g. <code>"Server.Connect.Failed"</code></p>
     *
     *  @see #faultString
     *  @see #faultDetail
     */
    public var faultCode:String;

    /**
     *  Provides destination-specific details of the failure.
     *
     *  <p>Typically fault details are a stack trace of an exception thrown at 
     *  the remote destination.</p>
     *
     *  @see #faultString
     *  @see #faultCode
     */
    public var faultDetail:String;

    /**
     *  Provides access to the destination-specific reason for the failure.
     *
     *  @see #faultCode
     *  @see #faultDetail
     */
    public var faultString:String;

    /**
     * Provides access to the underlying reason for the failure if the channel did
     * not raise the failure itself.
     */
    public var rootCause:Object;

    //--------------------------------------------------------------------------
    //
    // Overridden Methods
    // 
    //--------------------------------------------------------------------------    
    
    /**
     *  Clones the ChannelFaultEvent.
     *
     *  @return Copy of this ChannelFaultEvent.
     */
    override public function clone():Event
    {
        var faultEvent:ChannelFaultEvent = new ChannelFaultEvent(type, bubbles, cancelable, channel, reconnecting, faultCode, faultString, faultDetail, rejected, connected);
        faultEvent.rootCause = rootCause;
        return faultEvent;
    }

    /**
     *  Returns a string representation of the ChannelFaultEvent.
     *
     *  @return String representation of the ChannelFaultEvent.
     */
    override public function toString():String
    {
        return formatToString("ChannelFaultEvent", "faultCode", "faultString", "faultDetail",
                "channelId", "type", "bubbles", "cancelable", "eventPhase");
    }
    
    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------    

    /**
     *  Creates an ErrorMessage based on the ChannelFaultEvent by copying over
     *  the faultCode, faultString, faultDetail and rootCause to the new ErrorMessage.
     * 
     *  @return The ErrorMessage.
     */
    public function createErrorMessage():ErrorMessage
    {
        var result:ErrorMessage = new ErrorMessage();
        result.faultCode = faultCode;
        result.faultString = faultString;
        result.faultDetail = faultDetail;
        result.rootCause = rootCause;
        return result;
    }

}

}
