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

package mx.rpc.soap.mxml
{

import flash.events.Event;
import flash.events.ErrorEvent;
import flash.events.ErrorEvent;
import flash.events.IEventDispatcher;

import mx.core.IMXMLObject;
import mx.core.mx_internal;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AbstractOperation;
import mx.rpc.events.FaultEvent;
import mx.rpc.mxml.Concurrency;
import mx.rpc.mxml.IMXMLSupport;
import mx.rpc.soap.mxml.Operation;
import mx.rpc.soap.WebService;

use namespace mx_internal;

[ResourceBundle("rpc")]

/**
 * The &lt;mx:WebService&gt; tag gives you access to the operations of SOAP-compliant
 * web services.
 * @mxml 
 * <p>
 * The &lt;mx:WebService&gt; tag accepts the following tag attributes:
 * </p>
 * <pre>
 * &lt;mx:WebService
 *   <b>Properties</b>
 *   concurrency="multiple|single|last"
 *   destination="<i>No default.</i>"
 *   id="<i>No default.</i>"
 *   serviceName="<i>No default.</i>"
 *   showBusyCursor="false|true"
 *   makeObjectsBindable="false|true"
 *   useProxy="false|true"
 *   wsdl="<i>No default.</i>"
 *
 *   <b>Events</b>
 *   fault="<i>No default.</i>"
 *   result="<i>No default.</i>"
 * /&gt;
 * </pre>
 * </p>
 * <p>
 * An &lt;mx:WebService&gt; tag can have multiple &lt;mx:operation&gt; tags, which have the following tag attributes:
 * </p>
 * <pre>
 * &lt;mx:operation
 *   <b>Properties</b>
 *   concurrency="multiple|single|last"
 *   name=<i>No default, required.</i>
 *   resultFormat="object|xml|e4x"
 *   makeObjectsBindable="false|true"
 *
 *   
 *   <b>Events</b>
 *   fault=<i>No default.</i>
 *   result=<i>No default.</i>
 * /&gt;
 * </pre>
 *
 * An &lt;mx:Operation&gt; tag contains an &lt;mx:request&gt; tag. 
 * To specify an XML structure in an &lt;mx:request&gt; tag, you must set the value of the tag's
 * <code>format</code> attribute to <code>"xml"</code>. Otherwise, the body is converted into Objects.
 *
 *  @includeExample examples/WebServiceExample.mxml -noswf
 *
 */
public dynamic class WebService extends mx.rpc.soap.WebService implements IMXMLSupport, IMXMLObject
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new WebService component.
     *
     * @param destination The destination of the WebService, which should
     * match a destination name in the services-config.xml file. If
     * unspecified, the WebService component uses the DefaultHTTP destination.
     */
    public function WebService(destination:String = null)
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

    /**
     * Deprecated, use the appropriate destination instead, or if using a url, use <code>DefaultHTTP</code> or <code>DefaultHTTPS</code>.
     * The deprecated behavior will simply update the destination if the default is being used.
     */
    [Deprecated("Property protocol is deprecated. Please use an appropriate channel from your services configuration file (DefaultHTTP or DefaultHTTPS for instance); when useProxy is false set your URL to use the right protocol")]
    public function set protocol(protocol:String):void
    {
        if (destination == DEFAULT_DESTINATION_HTTP || destination == DEFAULT_DESTINATION_HTTP)
        {
            if (protocol == "http")
            {
                destination = DEFAULT_DESTINATION_HTTP;
            }
            else if (protocol == "https")
            {
                destination = DEFAULT_DESTINATION_HTTPS;
            }
            else
            {
				var message:String = resourceManager.getString(
					"rpc", "unknownProtocol", [ protocol ]);
                throw new Error(message);
            }
        }
    }

    [Deprecated(replacement="destination")]
    public function get serviceName():String
    {
        return destination;
    }

    public function set serviceName(sn:String):void
    {
        destination = sn;
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
     * This handler is called after the document fires the creationComplete event so that
     * if errors occur while loading the WSDL all components will be ready to handle the fault.
     * @private
     */
    public function creationComplete(event:Event):void
    {
        if (canLoadWSDL())
        {
            loadWSDL();
        }
    }

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
     * created beforehand, a new <code>mx.rpc.soap.mxml.Operation</code> is
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
            initializeOperation(op as Operation);
        }
        return op;
    }

    /**
     * Called automatically by the MXML compiler if the WebService is setup using a tag.  If you create
     * the WebService through ActionScript you may want to call this method yourself as it is useful for
     * validating any arguments.
     *
     * @param document the MXML document on which this WebService lives
     * @param id the id of this WebService within the document
     */
    public function initialized(document:Object, id:String):void
    {
        this.document = document;
        this.id = id;
        if (document is IEventDispatcher)
        {
            IEventDispatcher(document).addEventListener("creationComplete", creationComplete);
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
}

}
