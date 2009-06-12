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

package mx.rpc.remoting.mxml
{

import flash.events.Event;
import flash.events.ErrorEvent;

import mx.core.mx_internal;
import mx.core.IMXMLObject;
import mx.messaging.Channel;
import mx.messaging.ChannelSet;
import mx.messaging.channels.AMFChannel;
import mx.messaging.channels.SecureAMFChannel;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AbstractOperation;
import mx.rpc.events.FaultEvent;
import mx.rpc.mxml.Concurrency;
import mx.rpc.mxml.IMXMLSupport;
import mx.rpc.remoting.mxml.Operation;
import mx.rpc.remoting.RemoteObject;

use namespace mx_internal;

[ResourceBundle("rpc")]

/**
 * The &lt;mx:RemoteObject&gt; tag gives you access to the methods of
 * Java objects using Action Message Format (AMF) encoding.

 * @mxml
 * <p>
 * The &lt;mx:RemoteObject&gt; tag accepts the following tag attributes:
 * </p>
 * <pre>
 * &lt;mx:RemoteObject
 *  <b>Properties</b>
 *  concurrency="multiple|single|last"
 *  destination="<i>No default.</i>"
 *  id="<i>No default.</i>"
 *  endpoint="<i>No default.</i>"
 *  showBusyCursor="false|true"
 *  source="<i>No default.</i>" (currently, Adobe ColdFusion only)
 *  makeObjectsBindable="false|true"
 *  
 *  <b>Events</b>
 *  fault="<i>No default.</i>"
 *  result="<i>No default.</i>"  
 * /&gt;
 * </pre>
 * </p>
 *
 * <p>
 * &lt;mx:RemoteObject&gt; can have multiple &lt;mx:method&gt; tags, which have the following tag attributes:
 * </p>
 * <pre>
 * &lt;mx:method
 *  <b>Properties</b>
 *  concurrency="multiple|single|last"
 *  name="<i>No default, required.</i>"
 *  makeObjectsBindable="false|true"
 *         
 * <b>Events</b>
 *  fault="<i>No default.</i>"
 *  result="<i>No default.</i>"
 * /&gt;
 * </pre>
 * <p>
 * It then can have a single &lt;mx:arguments&gt; child tag which is an array of objects that is passed
 * in order.
 *
 * @includeExample examples/RemoteObjectExample.mxml -noswf
 *
 */
public dynamic class RemoteObject extends mx.rpc.remoting.RemoteObject implements IMXMLSupport, IMXMLObject
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Create a new RemoteObject.
     * @param destination the destination of the RemoteObject, should match a destination name 
     * in the services-config.xml file.
     */
    public function RemoteObject(destination:String = null)
    {
        super(destination);
        concurrency = Concurrency.MULTIPLE;
        showBusyCursor = false;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
                                    ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    [Inspectable(enumeration="multiple,single,last", defaultValue="multiple", category="General")]
   /**
    * Value that indicates how to handle multiple calls to the same service. The default
    * value is multiple. The following values are permitted:
    * <ul>
    * <li>multiple Existing requests are not cancelled, and the developer is
    * responsible for ensuring the consistency of returned data by carefully
    * managing the event stream. This is the default.</li>
    * <li>single Only a single request at a time is allowed on the operation;
    * multiple requests generate a fault.</li>
    * <li>last Making a request cancels any existing request.</li>
    * </ul>
    */
    public function get concurrency():String
    {
        return _concurrency;
    }

    /**
     *  @private
     */
    public function set concurrency(c:String):void
    {
        _concurrency = c;
    }

    [Inspectable(category="General")]
    /**
     * This property allows the developer to quickly specify an endpoint for a RemoteObject
     * destination without referring to a services configuration file at compile time or programmatically creating 
     * a ChannelSet. It also overrides an existing ChannelSet if one has been set for the RemoteObject service.
     *
     * <p>If the endpoint url starts with "https" a SecureAMFChannel will be used, otherwise an AMFChannel will 
     * be used. Two special tokens, {server.name} and {server.port}, can be used in the endpoint url to specify
     * that the channel should use the server name and port that was used to load the SWF. </p>
     *
     * <p><b>Note:</b> This property is required when creating AIR applications.</p>
     */
    public function get endpoint():String
    {
        return _endpoint;
    }
    
    public function set endpoint(url:String):void
    {
        // If endpoint has changed, null out channelSet to force it
        // to be re-initialized on the next Operation send
        if (_endpoint != url || url == null)
        {
            _endpoint = url;
            channelSet = null;
        }
    }

    [Inspectable(defaultValue="false", category="General")]
    /**
    * If <code>true</code>, a busy cursor is displayed while a service is executing. The default
    * value is <code>false</code>.
    */
    public function get showBusyCursor():Boolean
    {
        return _showBusyCursor;
    }

    public function set showBusyCursor(sbc:Boolean):void
    {
        _showBusyCursor = sbc;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * If this event is an error or fault, and the event type does not
     * have a listener, we notify the parent document.  If the     
     * parent document does not have a listener, then we throw
     * a runtime exception.  However, this is an asynchronous runtime
     * exception which is only exposed through the debug player.
     * A listener should be defined.
     *
     * @private
     */
    override public function dispatchEvent(event:Event):Boolean
    {
        if (hasEventListener(event.type))
        {
            return super.dispatchEvent(event);
        }
        else if ((event is FaultEvent && !hasTokenResponders(event)) || event is ErrorEvent)
        {
            var reason:String = (event is FaultEvent) ?
                FaultEvent(event).fault.faultString :
                ErrorEvent(event).text;

            if (document && document.willTrigger(ErrorEvent.ERROR))
            {
                var evt:ErrorEvent = new ErrorEvent(ErrorEvent.ERROR, true, true);
                evt.text = reason;
                return document.dispatchEvent(evt);
            }
            else if (event is FaultEvent)
            {
                throw FaultEvent(event).fault;
            }
            else
            {
                var message:String = resourceManager.getString(
                    "rpc", "noListenerForEvent", [ reason ]);
                throw new Error(message);
            }
        }

        return false;
    }

    /**
     * Returns an Operation of the given name. If the Operation wasn't
     * created beforehand, a new <code>mx.rpc.remoting.mxml.Operation</code> is
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
        var o:Object = _operations[name];
        var op:AbstractOperation = (o is AbstractOperation) ? AbstractOperation(o) : null;
        if (op == null)
        {
            op = new Operation(this, name);
            _operations[name] = op;
            op.asyncRequest = asyncRequest;
        }
        return op;
    }

    /**
     * Called automatically by the MXML compiler if the RemoteObject is set up using a tag.  If you create
     * the RemoteObject through ActionScript you may want to call this method yourself as it is useful for
     * validating any arguments.
     *
     * @param document the MXML document on which this RemoteObject lives
     * @param id the id of this RemoteObject within the document
     */
    public function initialized(document:Object, id:String):void
    {
        this.document = document;
        this.id = id;
    }


    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *@private
     */
    mx_internal function initEndpoint():void
    {
        if (endpoint != null)
        {
            var chan:Channel;
            if (endpoint.indexOf("https") == 0)
            {
                chan = new SecureAMFChannel(null, endpoint);
            }
            else
            {
                chan = new AMFChannel(null, endpoint);
            }
            channelSet = new ChannelSet();
            channelSet.addChannel(chan);
        }
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    mx_internal var document:Object;
    
    mx_internal var id:String;
    
    private var _concurrency:String;
    
    private var _showBusyCursor:Boolean;
    
    private var _endpoint:String;
}

}
