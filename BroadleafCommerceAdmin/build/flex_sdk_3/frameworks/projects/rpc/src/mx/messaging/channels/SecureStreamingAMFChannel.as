////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.messaging.channels
{

/**
 *  The SecureStreamingAMFChannel class is identical to the StreamingAMFChannel 
 *  class except that it uses a secure protocol, HTTPS, to send messages to an 
 *  AMF endpoint.
 */
public class SecureStreamingAMFChannel extends StreamingAMFChannel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Creates an new SecureStreamingAMFChannel instance.
     *
	 *  @param id The id of this Channel.
	 *  
	 *  @param uri The uri for this Channel.
     */
    public function SecureStreamingAMFChannel(id:String = null, uri:String = null)
    {
        super(id, uri);
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     *  Returns the protocol for this channel (https).
     */
    override public function get protocol():String
    {
        return "https";
    }
}

}
