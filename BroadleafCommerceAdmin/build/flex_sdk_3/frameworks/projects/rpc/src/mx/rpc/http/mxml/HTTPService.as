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

package mx.rpc.http.mxml
{

import flash.events.ErrorEvent;
import flash.events.ErrorEvent;

import mx.core.mx_internal;
import mx.core.IMXMLObject;
import mx.managers.CursorManager;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.AsyncMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AsyncToken;
import mx.rpc.AsyncDispatcher;
import mx.rpc.Fault;
import mx.rpc.http.HTTPService;
import mx.rpc.events.AbstractEvent;
import mx.rpc.events.FaultEvent;
import mx.rpc.mxml.Concurrency;
import mx.rpc.mxml.IMXMLSupport;
import mx.validators.Validator;

use namespace mx_internal;

[ResourceBundle("rpc")]

/**
 * You use the <code>&lt;mx:HTTPService&gt;</code> tag to represent an
 * HTTPService object in an MXML file. When you call the HTTPService object's
 * <code>send()</code> method, it makes an HTTP request to the
 * specified URL, and an HTTP response is returned. Optionally, you can pass
 * parameters to the specified URL. When you do not go through the server-based
 * proxy service, you can use only HTTP GET or POST methods. However, when you set
 * the useProxy  property to true and you use the server-based proxy service, you
 * can also use the HTTP HEAD, OPTIONS, TRACE, and DELETE methods.
 *
 * <p><b>Note:</b> Due to a software limitation, HTTPService does not generate
 * user-friendly error messages when using GET.
 * </p>
 *
 * @mxml
 * <p>
 * The &lt;mx:HTTPService&gt; tag accepts the following tag attributes:
 * </p>
 * <pre>
 * &lt;mx:HTTPService
 * <b>Properties</b>
 * concurrency="multiple|single|last"
 * contentType="application/x-www-form-urlencoded|application/xml"
 * destination="<i>DefaultHTTP</i>"
 * id="<i>No default.</i>"
 * method="GET|POST|HEAD|OPTIONS|PUT|TRACE|DELETE"
 * resultFormat="object|array|xml|e4x|flashvars|text"
 * showBusyCursor="false|true"
 * makeObjectsBindable="false|true"
 * url="<i>No default.</i>"
 * useProxy="false|true"
 * xmlEncode="<i>No default.</i>"
 * xmlDecode="<i>No default.</i>"
 *
 * <b>Events</b>
 * fault="<i>No default.</i>"
 * result="<i>No default.</i>"
 * /&gt;
 * </pre>
 *
 * The <code>&lt;mx:HTTPService&gt;</code> tag can have a single &lt;mx:request&gt; tag under which the parameters can be specified.
 * </p>
 *
 * @includeExample examples/HTTPServiceExample.mxml -noswf
 *
 * @see mx.rpc.http.HTTPService
 * @see mx.validators.Validator
 * @see mx.managers.CursorManager
 */
public class HTTPService extends mx.rpc.http.HTTPService implements IMXMLSupport, IMXMLObject
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new HTTPService. This constructor is usually called by the generated code of an MXML document.
     * You usually use the mx.rpc.http.HTTPService class to create an HTTPService in ActionScript.
     *
     * @param rootURL The URL the HTTPService should use when computing relative URLS.
     *
     * @param destination An HTTPService destination name in the service-config.xml file.
     */
    public function HTTPService(rootURL:String = null, destination:String = null)
    {
        super(rootURL, destination);

        showBusyCursor = false;
        concurrency = Concurrency.MULTIPLE;
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
     * value is <code>multiple</code>. The following values are permitted:
     * <ul>
     * <li><code>multiple</code> Existing requests are not cancelled, and the developer is
     * responsible for ensuring the consistency of returned data by carefully
     * managing the event stream. This is the default value.</li>
     * <li><code>single</code> Only a single request at a time is allowed on the operation;
     * multiple requests generate a fault.</li>
     * <li><code>last</code> Making a request cancels any existing request.</li>
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
    // Public Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    override public function cancel(id:String = null):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.removeBusyCursor();
        }
        return super.cancel(id);
    }

    /**
     * Called after the implementing object has been created and all
     * component properties specified on the MXML tag have been
     * initialized. 
     *
     * If you create this class in ActionScript and want it to function with validation, you must
     * call this method and pass in the MXML document and the
     * HTTPService's <code>id</code>.
     *
     * @param document The MXML document that created this object.
     *
     * @param id The identifier used by <code>document</code> to refer
     * to this object. If the object is a deep property on document,
     * <code>id</code> is null. 
     */
    public function initialized(document:Object, id:String):void
    {
        this.id = id;
        this.document = document;
    }

    /**
     * Executes an HTTPService request. The parameters are optional, but if specified should
     * be an Object containing name-value pairs or an XML object depending on the contentType.
     * @return an AsyncToken.  It will be the same object available in the <code>result</code>
     * or <code>fault</code> event's <code>token</code> property.
     */
    override public function send(parameters:Object = null):AsyncToken
    {
        //concurrency check
        if (Concurrency.SINGLE == concurrency && activeCalls.hasActiveCalls())
        {
            var token:AsyncToken = new AsyncToken(null);
			var message:String = resourceManager.getString(
				"rpc", "pendingCallExists");
            var fault:Fault = new Fault("ConcurrencyError", message);
            var faultEvent:FaultEvent = FaultEvent.createEvent(fault, token);
            new AsyncDispatcher(dispatchRpcEvent, [faultEvent], 10);
            return token;
        }

        return super.send(parameters);
    }


    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

    override mx_internal function invoke(message:IMessage, token:AsyncToken = null):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.setBusyCursor();
        }

        return super.invoke(message, token);
    }

    /*
     * Kill the busy cursor, find the matching call object and pass it back
     */
    override mx_internal function preHandle(event:MessageEvent):AsyncToken
    {
        if (showBusyCursor)
        {
            CursorManager.removeBusyCursor();
        }

        var wasLastCall:Boolean = activeCalls.wasLastCall(AsyncMessage(event.message).correlationId);
        var token:AsyncToken = super.preHandle(event);

        if (Concurrency.LAST == concurrency && !wasLastCall)
        {
            return null;
        }
        //else
        return token;
    }

    /**
     * If this event is a fault, and the event type does not
     * have a listener, we notify the parent document.  If the
     * parent document does not have a listener, then we throw
     * a runtime exception.  However, this is an asynchronous runtime
     * exception which is only exposed through the debug player.
     * A listener should be defined.
     *
     * @private
     */
    override mx_internal function dispatchRpcEvent(event:AbstractEvent):void
    {
        event.callTokenResponders();
        if (!event.isDefaultPrevented())
        {
            if (hasEventListener(event.type))
            {
                dispatchEvent(event);
            }
            else if (event is FaultEvent && (event.token == null || !event.token.hasResponder()))
            {
                if (document && document.willTrigger(ErrorEvent.ERROR))
                {
                    var evt:ErrorEvent = new ErrorEvent(ErrorEvent.ERROR, true, true);
                    evt.text = FaultEvent(event).fault.faultString;
                    document.dispatchEvent(evt);
                }
                else
                {
                    
                    // last-ditch effort to notify the user that something went wrong
                    throw FaultEvent(event).fault;
                }
            }
        }
        
    }


    //--------------------------------------------------------------------------
    //
    // Private Variables
    // 
    //--------------------------------------------------------------------------

    private var _concurrency:String;
    
	private var document:Object; //keep the document for validation
    
	private var id:String; //need to know our own id for validation
    
	private var _showBusyCursor:Boolean;
}

}
