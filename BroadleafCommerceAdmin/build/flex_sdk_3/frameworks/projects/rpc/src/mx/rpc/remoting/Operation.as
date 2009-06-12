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

package mx.rpc.remoting
{

import mx.core.mx_internal;
import mx.messaging.messages.RemotingMessage;
import mx.rpc.AbstractOperation;
import mx.rpc.AbstractService;
import mx.rpc.AsyncToken;
import mx.utils.ObjectUtil;

use namespace mx_internal;

/**
 * An Operation used specifically by RemoteObjects. An Operation is an individual method on a service.
 * An Operation can be called either by invoking the
 * function of the same name on the service or by accessing the Operation as a property on the service and
 * calling the <code>send()</code> method.
 */
public class Operation extends AbstractOperation
{
    //---------------------------------
    // Constructor
    //---------------------------------

    /**
     * Creates a new Operation. This is usually done directly automatically by the RemoteObject
     * when an unknown operation has been accessed. It is not recommended that a developer use this constructor
     * directly.
     */
    public function Operation(remoteObject:AbstractService = null, name:String = null)
    {
        super(remoteObject, name);

        argumentNames = [];
    }


    //---------------------------------
    // Properties
    //---------------------------------

    [Inspectable(defaultValue="true", category="General")]

    /**
     * When this value is true, anonymous objects returned are forced to bindable objects.
     */
    override public function get makeObjectsBindable():Boolean
    {
        if (_makeObjectsBindableSet)
        {
            return _makeObjectsBindable;
        }

        return RemoteObject(service).makeObjectsBindable;    
    }

    override public function set makeObjectsBindable(b:Boolean):void
    {
        _makeObjectsBindable = b;
        _makeObjectsBindableSet = true;
    }

    /**
     * An ordered list of the names of the arguments to pass to a method invocation.  Since the arguments object is
     * a hashmap with no guaranteed ordering, this array helps put everything together correctly.
     * It will be set automatically by the MXML compiler, if necessary, when the Operation is used in tag form.
     */
    public var argumentNames:Array;

    private var _makeObjectsBindableSet:Boolean;

    //---------------------------------
    // Methods
    //---------------------------------

    /**
     * @inheritDoc
     */
    override public function send(... args:Array):AsyncToken
    {
        if (!args || (args.length == 0 && this.arguments))
        {
            if (this.arguments is Array)
            {
                args = this.arguments as Array;
            }
            else
            {
                args = [];
                for (var i:int = 0; i < argumentNames.length; ++i)
                {
                    args[i] = this.arguments[argumentNames[i]];
                }
            }
        }

        var message:RemotingMessage = new RemotingMessage();
        message.operation = name;
        message.body = args;
        message.source = RemoteObject(service).source;

        return invoke(message);
    }

}

}
