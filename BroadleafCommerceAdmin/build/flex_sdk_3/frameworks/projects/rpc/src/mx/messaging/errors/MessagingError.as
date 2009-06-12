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
 *  This is the base class for any messaging related error.
 *  It allows for less granular catch code.
 */
public class MessagingError extends Error
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  Constructs a new instance of a MessagingError with the
	 *  specified message.
	 *
	 *  @param msg String that contains the message that describes the error.
     */
    public function MessagingError(msg:String)
    {
        super(msg);
    }
    
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
    
    /**
     *  Returns the string "[MessagingError]" by default, and includes the message property if defined.
     * 
     *  @return String representation of the MessagingError.
     */
    public function toString():String
    {
        var value:String = "[MessagingError";
        if (message != null)
            value += " message='" + message + "']";
        else
            value += "]";
        return value;
    }
}

}
