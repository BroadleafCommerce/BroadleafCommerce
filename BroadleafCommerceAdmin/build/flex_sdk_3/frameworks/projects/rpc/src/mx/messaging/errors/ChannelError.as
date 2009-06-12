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

/**
 *  This is the base class for any channel related errors.
 *  It allows for less granular catch code. 
 */
public class ChannelError extends MessagingError
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructs a new instance of a ChannelError with the
	 *  specified message.
	 *
	 *  @param msg String that contains the message that describes the error.
     */
    public function ChannelError(msg:String)
    {
        super(msg);
    }
}

}
