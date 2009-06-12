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
 *  This error is thrown when a destination can't be accessed
 *  or is not valid.
 *  This error is thrown by the following methods/properties
 *  within the framework:
 *  <ul>
 *    <li><code>ServerConfig.getChannelSet()</code> if an invalid destination is specified.</li>
 *    <li><code>ServerConfig.getProperties()</code> if an invalid destination is specified.</li>
 *    <li><code>Channel.send()</code> if no destination is specified for the message to send.</li>
 *    <li><code>MessageAgent.destination</code> setter if the destination value is null or zero length.</li>
 *    <li><code>Producer.send()</code> if no destination is specified for the Producer or message to send.</li>
 *    <li><code>Consumer.subscribe()</code> if no destination is specified for the Consumer.</li>
 *  </ul>
 */
public class InvalidDestinationError extends ChannelError
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructs a new instance of an InvalidDestinationError with the specified message.
     *
     *  @param msg String that contains the message that describes this InvalidDestinationError.
     */
    public function InvalidDestinationError(msg:String)
    {
        super(msg);
    }
}

}
