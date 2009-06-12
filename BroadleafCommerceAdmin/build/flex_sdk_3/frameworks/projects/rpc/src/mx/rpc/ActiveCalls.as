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

import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 * @private
 */
public class ActiveCalls
{
    private var calls:Object;
    private var callOrder:Array;

    public function ActiveCalls()
    {
        super();
        calls = {};
        callOrder = [];
    }

    public function addCall(id:String, token:AsyncToken):void
    {
        calls[id] = token;
        callOrder.push(id);
    }
    
    public function getAllMessages():Array
    {
        var msgs:Array = [];
        for (var id:String in calls)
        {
            msgs.push(calls[id]);
        }
        return msgs;
    }

    public function cancelLast():AsyncToken
    {
        if (callOrder.length > 0)
        {
            return removeCall(callOrder[callOrder.length - 1] as String);
        }
        return null;
    }

    public function hasActiveCalls():Boolean
    {
        return callOrder.length > 0;
    }

    public function removeCall(id:String):AsyncToken
    {
        var token:AsyncToken = calls[id];
        if (token != null)
        {
            delete calls[id];
			callOrder.splice(callOrder.lastIndexOf(id),1);
        }
        return token;
    }

    public function wasLastCall(id:String):Boolean
    {
    	if (callOrder.length > 0)
    	{
    		return callOrder[callOrder.length - 1] == id;
    	}
    	return false;
    }
}

}
