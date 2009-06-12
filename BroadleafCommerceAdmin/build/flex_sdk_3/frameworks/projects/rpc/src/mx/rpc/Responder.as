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

package mx.rpc
{

/**
 *  This class provides a default implementation <code>mx.rpc.IResponder</code>
 */
public class Responder implements IResponder
{
	/**
	 *  Constructs an instance of the responder with the specified handlers.
	 *  
	 *  @param	result Function that should be called when the request has
	 *           completed successfully.
	 *  @param	fault Function that should be called when the request has
	 *			completed with errors.
	 */
	public function Responder(result:Function, fault:Function)
	{
		super();
		_resultHandler = result;
		_faultHandler = fault;
	}
	
	/**
	 *  This method is called by a remote service when the return value has been 
	 *  received.
         *
         * @param data While <code>data</code> is typed as Object, it is often (but not always) an mx.rpc.events.ResultEvent.
	 */
	public function result(data:Object):void
	{
		_resultHandler(data);
	}
	
	/**
	 *  This method is called by a service when an error has been received.
         *
         * @param info While <code>info</code> is typed as Object, it is often (but not always) an mx.rpc.events.FaultEvent.
	 */
	public function fault(info:Object):void
	{
		_faultHandler(info);
	}
	
	/**
	 *  @private
	 */
	private var _resultHandler:Function;
	
	/**
	 *  @private
	 */
	private var _faultHandler:Function;
}


}