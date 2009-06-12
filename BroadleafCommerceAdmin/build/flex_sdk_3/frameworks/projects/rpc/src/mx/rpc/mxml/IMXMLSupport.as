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
 *  Implementing this interface means that an RPC service
 *  can be used in an MXML document via tags
 *  since it supports the interfaces specified during Flex 1.
 */
public interface IMXMLSupport
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  concurrency
    //----------------------------------

    [Inspectable(enumeration="multiple,single,last", defaultValue="multiple", category="General")]
    
	/**
     *  The concurrency setting of the RPC operation or HTTPService.
	 *  One of "multiple" "last" or "single."
     */
    function get concurrency():String;
    
	/**
     *  @private
     */
    function set concurrency(value:String):void;

    //----------------------------------
	//  showBusyCursor
    //----------------------------------

    /**
     *  Indicates whether the RPC operation or HTTPService
	 *  should show the busy cursor while it is executing.
     */
    
	function get showBusyCursor():Boolean;

    /**
     *  @private
     */
    function set showBusyCursor(value:Boolean):void;
}

}
