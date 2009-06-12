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
import mx.rpc.AbstractOperation;
import mx.rpc.AbstractService;

use namespace mx_internal;

/**
 * The RemoteObject class gives you access to classes on a remote application server.
 */
public dynamic class RemoteObject extends AbstractService
{
    //-------------------------------------------------------------------------
    //
    //              Constructor
    //
    //-------------------------------------------------------------------------

    /**
     * Creates a new RemoteObject.
     * @param destination [optional] Destination of the RemoteObject; should match a destination name in the services-config.xml file.
     */
    public function RemoteObject(destination:String = null)
    {
        super(destination);

        makeObjectsBindable = true;
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    private var _source:String;
    
    /**
     *  @private
     */
    private var _makeObjectsBindable:Boolean;

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

	//----------------------------------
	//  makeObjectsBindable
	//----------------------------------

    [Inspectable(category="General", defaultValue="true")]
    
    /**
     * When this value is true, anonymous objects returned are forced to bindable objects.
     */
    public function get makeObjectsBindable():Boolean
    {
        return _makeObjectsBindable;
    }

    public function set makeObjectsBindable(b:Boolean):void
    {
        _makeObjectsBindable = b;
    }

	//----------------------------------
	//  source
	//----------------------------------

	[Inspectable(category="General")]
    /**
     * Lets you specify a source value on the client; not supported for destinations that use the JavaAdapter. This allows you to provide more than one source
     * that can be accessed from a single destination on the server. 
     *     
     */
    public function get source():String
    {
        return _source;
    }

    public function set source(s:String):void
    {
        _source = s;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Returns an Operation of the given name. If the Operation wasn't
     * created beforehand, a new <code>mx.rpc.remoting.Operation</code> is
     * created during this call. Operations are usually accessible by simply
     * naming them after the service variable
     * (<code>myService.someOperation</code>), but if your Operation name
     * happens to match a defined method on the service
     * (like <code>setCredentials</code>), you can use this method to get the
     * Operation instead.
     * @param name Name of the Operation.
     * @return Operation that executes for this name.
     */
    override public function getOperation(name:String):AbstractOperation
    {
        var op:AbstractOperation = super.getOperation(name);
        if (op == null)
        {
            op = new Operation(this, name);
            _operations[name] = op;
            op.asyncRequest = asyncRequest;
        }
        return op;
    }

    /**
     * If a remote object is managed by an external service, such a ColdFusion Component (CFC),
     * a username and password can be set for the authentication mechanism of that remote service.
     *
     * @param remoteUsername the username to pass to the remote endpoint
     * @param remotePassword the password to pass to the remote endpoint
     * @param charset The character set encoding to use while encoding the
     * remote credentials. The default is null, which implies the legacy charset
     * of ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     */
    override public function setRemoteCredentials(remoteUsername:String, remotePassword:String, charset:String=null):void
    {
        super.setRemoteCredentials(remoteUsername, remotePassword, charset);
    }
    
    /**
     * Represents an instance of RemoteObject as a String, describing
     * important properties such as the destination id and the set of
     * channels assigned.
     *
     * @return Returns a String representing an instance of a RemoteObject.
     */
    public function toString():String
    {
        var s:String = "[RemoteObject ";
        s += " destination=\"" + destination + "\"";
        if (source)
            s += " source=\"" + source + "\"";
        s += " channelSet=\"" + channelSet + "\"]";
        return s;
    }
}

}
