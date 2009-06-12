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
 *  The SecureStreamingHTTPChannel class is identical to the StreamingHTTPChannel
 *  class except that it uses a secure protocol, HTTPS, to send messages to an 
 *  HTTP endpoint.
 */
public class SecureStreamingHTTPChannel extends StreamingHTTPChannel
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Creates an new SecureStreamingHTTPChannel instance.
     *
	 *  @param id The id of this Channel.
	 *  
	 *  @param uri The uri for this Channel.
     */
    public function SecureStreamingHTTPChannel(id:String = null, uri:String = null)
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
