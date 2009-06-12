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

package mx.messaging.errors
{

import mx.messaging.messages.ErrorMessage;

/**
 *  This error indicates a problem serializing a message within a channel.
 *  It provides a fault property which corresponds to an ErrorMessage generated
 *  when this error is thrown.
 */
public class MessageSerializationError extends MessagingError
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructs a new instance of the MessageSerializationError
	 *  with the specified message.
	 *
	 *  @param msg String that contains the message that describes the error.
	 *  @param fault Provides specific information about the fault that occured
	 *  and for which message.
     */
    public function MessageSerializationError(msg:String, fault:ErrorMessage)
    {
        super(msg);
        this.fault = fault;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  Provides specific information about the fault that occurred and for
     *  which message.
     */
    public var fault:ErrorMessage;
}

}
