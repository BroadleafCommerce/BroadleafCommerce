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
import mx.messaging.messages.IMessage;

[ExcludeClass]

/**
 *  Represents events that are dispatched as a result of invoking operations
 *  on a MessagePersister.
 *
 *  @see mx.messaging.MessagePersister
 * @private
 */
public class MessagePersisterEvent extends Event
{
    /**
     *  Normally called internally and not used in application code. This event is
     *  generated to report a successful result for a load() invocation or succes or fault
     *  for invoking operations on a <code>MessagePersister</code>.
     *
     *  @see mx.messaging.MessagePersister
     *
     *  @param The event type of the event.
     * 
     *  @param bubbles Specifies whether the event can bubble up the display 
     *  list hierarchy.
     * 
     *  @param cancelable Indicates whether the behavior associated with the 
     *  event can be prevented.
     * 
     *  @param The Id for the message agent that invoked the operation.
     *
     *  @param The operation that the message agent invoked.
     */
    public function MessagePersisterEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false, 
            id:String = null, operation:String = null)
    {
        super(type, bubbles, cancelable);

        _id = id;
        _op = operation;
    }
    
    /**
     *  The Id for the message agent that invoked the operation.
     */
    public function get id():String
    {
        return _id;
    }
    
    /**
     *  The operation that the message agent invoked.
     */
    public function get operation():String
    {
        return _op;
    }

    /**
     *  The messages associated with an event having status <code>RESULT</code>.
     */
    public function set messages(value:Array):void
    {
        _messages = value;
    }

    public function get messages():Array
    {
        return _messages;
    }
    
    /**
     *  The source message that was passed to <code>save</code>
     *  that was successfully saved or faulted.
     */
    public function set message(value:IMessage):void
    {
        _message = message;
    }
    
    public function get message():IMessage
    {
        return _message;
    }   

    /**
     * @private
     */
    public function get messageId():String
    {
        if (_message != null)
        {
            return _message.messageId;
        }
        return null;
    }

    /**
     * @private
     */
    public function get messageCount():int
    {
        if (_messages != null)
        {
            return _messages.count;
        }
        return 0;
    }
    
    public static function createEvent(type:String, id:String, operation:String):MessagePersisterEvent
    {
        return new MessagePersisterEvent(type, false, false, id, operation);
    }

    /**
     *  @private
     */
    override public function clone():Event
    {
        var cloneEvent:MessagePersisterEvent = new MessagePersisterEvent(type, bubbles, cancelable, _id, _op);
        if (_messages)
        {
            cloneEvent.messages = _messages;
        }
        return cloneEvent;
    }

    override public function toString():String
    {
        return formatToString("MessagePersisterEvent", "id", "operation", "messageId", "messageCount", "type", "bubbles", "cancelable", "eventPhase");
    }

    // Status constants.
    /**
     *  The <code>SUCCESS</code> status indicates that an operation invoked on a
     *  message persister completed without errors.
     */
    public static const SUCCESS:String = "success";

    /**
     *  The <code>RESULT</code> status indicates that a <code>load</code> operation 
     *  invoked on a message persister is returning an array of stored messages.
     */
    public static const RESULT:String = "result";
    
    /**
     *  The <code>FAULT</code> status indicates that an operation invoked on a 
     *  message persister failed.
     */
    public static const FAULT:String = "fault";

    // private members
    private var _id:String;
    private var _op:String;
    private var _messages:Array;
    private var _message:IMessage;
}

}
