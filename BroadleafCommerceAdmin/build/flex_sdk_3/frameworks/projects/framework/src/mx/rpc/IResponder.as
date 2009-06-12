////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc
{

/**
 *  This interface provides the contract for any service
 *  that needs to respond to remote or asynchronous calls.
 */
public interface IResponder
{
	/**
	 *  This method is called by a service when the return value
	 *  has been received. 
	 *  While <code>data</code> is typed as Object, it is often
	 *  (but not always) an mx.rpc.events.ResultEvent.
	 */
	function result(data:Object):void;
	
	/**
	 *  This method is called by a service when an error has been received.
	 *  While <code>info</code> is typed as Object it is often
	 *  (but not always) an mx.rpc.events.FaultEvent.
	 */
	function fault(info:Object):void;
}

}
