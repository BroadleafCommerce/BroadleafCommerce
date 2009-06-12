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
 *  This error is thrown when no Channel is available to send messages.
 *  This error is thrown by the following methods within the framework:
 *  <ul>
 *    <li><code>ChannelSet.send()</code> if the ChannelSet has no channels.</li>
 *  </ul>
 */
public class NoChannelAvailableError extends MessagingError
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructs a new instance of the NoChannelAvailableError with the specified message.
     *
     *  @param msg String that contains the message that describes this NoChannelAvailableError.
     */
    public function NoChannelAvailableError(msg:String)
    {
        super(msg);
    }
}

}
