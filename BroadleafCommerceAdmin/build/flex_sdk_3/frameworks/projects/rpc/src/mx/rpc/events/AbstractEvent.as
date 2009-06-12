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

package mx.rpc.events
{

import mx.core.mx_internal;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.IMessage;
import mx.rpc.AsyncToken;

use namespace mx_internal;

/**
 * The base class for events that RPC services dispatch.
 */
public class AbstractEvent extends MessageEvent
{
    private var _token:AsyncToken;

    /**
     * @private
     */
    public function AbstractEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = true, 
        token:AsyncToken = null, message:IMessage = null)
    {
        super(type, bubbles, cancelable, message);

        _token = token;
    }

    /**
     * The token that represents the call to the method. Used in the asynchronous completion token pattern.
     */
    public function get token():AsyncToken
    {
        return _token;
    }
    
    /**
     * Does nothing by default.
     */
    mx_internal function callTokenResponders():void
    {
    }
}

}
