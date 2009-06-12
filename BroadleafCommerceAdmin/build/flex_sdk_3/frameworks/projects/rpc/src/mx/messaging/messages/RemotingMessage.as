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

package mx.messaging.messages
{

[RemoteClass(alias="flex.messaging.messages.RemotingMessage")]

/**
 *  RemotingMessages are used to send RPC requests to a remote endpoint.
 *  These messages use the <code>operation</code> property to specify which
 *  method to call on the remote object.
 *  The <code>destination</code> property indicates what object/service should be
 *  used.
 */
public class RemotingMessage extends AbstractMessage
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Constructs an uninitialized RemotingMessage.
     */
    public function RemotingMessage()
    {
        super();
        operation = "";
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------    

    /**
     *  Provides access to the name of the remote method/operation that
     *  should be called.
     */
    public var operation:String;

    /**
     *  This property is provided for backwards compatibility. The best
     *  practice, however, is to not expose the underlying source of a
     *  RemoteObject destination on the client and only one source to
     *  a destination. Some types of Remoting Services may even ignore
     *  this property for security reasons.
     */
    public var source:String;
}

}
