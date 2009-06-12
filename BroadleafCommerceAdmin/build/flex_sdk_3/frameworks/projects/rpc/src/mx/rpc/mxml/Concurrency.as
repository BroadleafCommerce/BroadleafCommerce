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

package mx.rpc.mxml
{

/**
 * Concurrency is set via MXML based access to RPC services to indicate how to handle multiple
 * calls to the same service. The default concurrency value is <code>multiple</code>.
 */
public final class Concurrency
{
    /**
     * Making a request cancels any existing request.
     */
    public static const LAST:String = "last";

    /**
     * Existing requests are not cancelled, and the developer is responsible for ensuring
     * the consistency of returned data by carefully managing the event stream.
     */
    public static const MULTIPLE:String = "multiple";


    /**
     * Only a single request at a time is allowed on the operation; multiple requests generate a fault.
     */
    public static const SINGLE:String = "single";
}

}