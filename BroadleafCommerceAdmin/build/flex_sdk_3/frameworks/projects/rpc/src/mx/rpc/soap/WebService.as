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

package mx.rpc.soap
{

import flash.events.Event;

import mx.core.mx_internal;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.messaging.ChannelSet;
import mx.messaging.channels.DirectHTTPChannel;
import mx.messaging.config.LoaderConfig;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AbstractOperation;
import mx.rpc.AbstractService;
import mx.rpc.AsyncRequest;
import mx.rpc.Fault;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.WSDLLoadEvent;
import mx.rpc.http.HTTPService;
import mx.rpc.wsdl.WSDL;
import mx.rpc.wsdl.WSDLLoader;
import mx.rpc.wsdl.WSDLOperation;
import mx.rpc.wsdl.WSDLPort;
import mx.utils.URLUtil;
import mx.utils.XMLUtil;

use namespace mx_internal;

/**
 * The <code>LoadEvent.LOAD</code> is dispatched when the WSDL
 * document has loaded successfully.
 *
 * @eventType mx.rpc.soap.LoadEvent.LOAD
 */
[Event(name="load", type="mx.rpc.soap.LoadEvent")]

[ResourceBundle("rpc")]

/**
 * The WebService class provides access to SOAP-based web services on remote
 * servers.
 */
public dynamic class WebService extends AbstractWebService
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new WebService.  The destination, if specified, should match
     * an entry in services-config.xml.  If unspecified, the WebService uses
     * the DefaultHTTP destination. The <code>rootURL</code> is required if you
     * intend to use a relative URL to find the WSDL document for this WebService.
     *
     * @param destination The destination of the WebService, should match a destination 
     * name in the services-config.xml file.
     *
     * @param rootURL The root URL of the WebService.
     */
    public function WebService(destination:String = null, rootURL:String = null)
    {
        super(destination, rootURL);

        _ready = false;
        _log = Log.getLogger("mx.rpc.soap.WebService");
    }

    //-------------------------------------------------------------------------
    //
    // Variables
    //
    //-------------------------------------------------------------------------

    /**
     *  @private
     */
    private var resourceManager:IResourceManager =
									ResourceManager.getInstance();
    //-------------------------------------------------------------------------
    //
    // Properties
    //
    //-------------------------------------------------------------------------

    mx_internal function get wsdlFault():Boolean
    {
        return _wsdlFault;
    }

    [Inspectable(defaultValue="true", category="General")]

    /**
     * The location of the WSDL document for this WebService. If you use a
     * relative URL, make sure that the <code>rootURL</code> has been specified
     * or that you created the WebService in MXML.
     */
    public function get wsdl():String
    {
        return _wsdlURL;
    }

    public function set wsdl(w:String):void
    {
        _wsdlURL = w;
    }

    //-------------------------------------------------------------------------
    //
    // Methods
    //
    //-------------------------------------------------------------------------

    /**
     * Returns a Boolean value that indicates whether the WebService is ready to
     * load a WSDL (does it have a valid destination or wsdl specified).
     *
     * @return Returns <code>true</code> if the WebService is ready to load a WSDL;
     * otherwise, returns <code>false</code>.
     */
    public function canLoadWSDL():Boolean
    {
        if (wsdl)
            return true;
        if (destination != DEFAULT_DESTINATION_HTTP
            && destination != DEFAULT_DESTINATION_HTTPS)
            return true;
        return false;
    }

    /**
     * Returns an Operation of the given name. If the Operation wasn't
     * created beforehand, a new <code>mx.rpc.soap.Operation</code> is created
     * during this call. Operations are usually accessible by simply naming
     * them after the service variable (<code>myService.someOperation</code>),
     * but if your Operation name happens to match a defined method on the
     * service (like <code>setCredentials</code>), you can use this method to
     * get the Operation instead.
     * @param name Name of the Operation.
     * @return Operation that executes for this name.
     */
    override public function getOperation(name:String):mx.rpc.AbstractOperation
    {
        var op:mx.rpc.AbstractOperation = super.getOperation(name);
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
     * Instructs the WebService to download the WSDL document.  The WebService
     * calls this method automatically WebService when specified in the
     * WebService MXML tag, but it must be called manually if you create the
     * WebService object in ActionScript after you have specified the
     * <code>destination</code> or <code>wsdl</code> property value.
     *
     * @param uri If the wsdl hasn't been specified previously, it may be
     * specified here.
     */
    public function loadWSDL(uri:String = null):void
    {
        if (uri != null)
        {
            wsdl = uri;
        }

        // If wsdl is not set check useProxy is true and a destination has
        // been set...
        if (!wsdl)
        {
			var message:String;
            if (!useProxy)
            {
				message = resourceManager.getString(
					"rpc", "mustSpecifyWSDLLocation");
                var fault:Fault = new Fault("Client.WSDL", message);
                dispatchEvent(FaultEvent.createEvent(fault));
                return;
            }
            else if ((destination == null) || !destinationSet 
                || (destination == DEFAULT_DESTINATION_HTTP) 
                || (destination == DEFAULT_DESTINATION_HTTPS))
            {
				message = resourceManager.getString(
					"rpc", "destinationOrWSDLNotSpecified");
                var fault1:Fault = new Fault("Client.WSDL", message);
                dispatchEvent(FaultEvent.createEvent(fault1));
                return;
            }
        }

        // If a destination wasn't set and the app was loaded via HTTPS
        // then use DefaultHTTPS instead of DefaultHTTP for our destination
        if (!destinationSet && URLUtil.isHttpsURL(wsdl))
        {
            // We avoid changing super.destination as we don't want to change
            // the current useProxy flag...
            asyncRequest.destination = DEFAULT_DESTINATION_HTTPS;
            destinationSet = true;
        }

        _wsdlLoader = new WSDLLoader(deriveHTTPService());
        _wsdlLoader.addEventListener(WSDLLoadEvent.LOAD, wsdlHandler);
        _wsdlLoader.addEventListener(FaultEvent.FAULT, wsdlFaultHandler);

        // Get the WSDL
        _wsdlFault = false;
        _wsdlLoader.load(wsdl);
    }

    /**
     * Represents an instance of WebService as a String, describing
     * important properties such as the destination id and the set of
     * channels assigned.
     *
     * @return Returns a String representation of the WebService.
     */
    public function toString():String
    {
        var s:String = "[WebService ";
        s += " destination=\"" + destination + "\"";
        if (wsdl)
            s += " wsdl=\"" + wsdl + "\"";
        s += " channelSet=\"" + channelSet + "\"]";
        return s;
    }

    //---------------------------------
    // Helper methods
    //---------------------------------

    mx_internal function wsdlFaultHandler(event:FaultEvent):void
    {
        // remember that the wsdl failed
        _wsdlFault = true;

        // we just chain and fire
        dispatchEvent(event);

        // also inform enqueued operations of the wsdl failure
        unEnqueueCalls(event.fault);
    }

    mx_internal function wsdlHandler(event:WSDLLoadEvent):void
    {
        _log.debug("WSDL loaded");
        _wsdl = event.wsdl;

        // Record the SOAP address location as the "endpointURI"
        try
        {
            var wsdlPort:WSDLPort = _wsdl.getPort(service, port);
            _endpointURI = wsdlPort.endpointURI;

            // Resolve any relative endpoint URLs against the WSDL URL
            if (!URLUtil.isHttpURL(_endpointURI) && event.location != null)
                _endpointURI = URLUtil.getFullURL(event.location, _endpointURI);

            _service = wsdlPort.service.name;
            _port = wsdlPort.name;

            // Operations may have been created before the WSDL was loaded so
            // we initialize them here before unenqueuing any calls.
            for each (var op:Operation in _operations)
            {
                initializeOperation(op);
            }

            _ready = true;        

            // Now that we're ready, dispatch load event
            // Note that we dispatch a legacy mx.rpc.soap.LoadEvent here instead to
            // maintain backwards compatibility...
            var loadEvent:LoadEvent = LoadEvent.createEvent(event.wsdl, event.location);
            dispatchEvent(loadEvent);

            unEnqueueCalls();
        }
        catch(fault:Fault)
        {
            var faultEvent:FaultEvent = FaultEvent.createEvent(fault);
            dispatchEvent(faultEvent);
            super.unEnqueueCalls(fault); // Jump straight to fault handling; ops cannot be initialized.
            return;
        }
        catch(error:Error)
        {
            var errorMessage:String = error.message ? error.message : ""; 
			var message:String =  resourceManager.getString(
				"rpc", "unexpectedException", [ errorMessage ])          
            var fault:Fault = new Fault("WSDLError", message);
            fault.rootCause = error;
            var faultEvent2:FaultEvent = FaultEvent.createEvent(fault);
            dispatchEvent(faultEvent2);
            super.unEnqueueCalls(fault); // Jump straight to fault handling; ops cannot be initialized.
            return;
        }
    }

    /**
     * @private
     */
    mx_internal function deriveHTTPService():HTTPService
    {
        var httpService:HTTPService = new HTTPService();
        httpService.asyncRequest = asyncRequest;
        if (destination)
            httpService.destination = destination;
        httpService.useProxy = useProxy;
        httpService.resultFormat = HTTPService.RESULT_FORMAT_XML;
        httpService.rootURL = rootURL;
        httpService.headers = httpHeaders;
        return httpService;
    }

   /**
    * Initializes a new Operation.
    *
    * @param operation The Operation to initialize.
    */
    protected function initializeOperation(operation:Operation):void
    {
        if (_wsdl != null)
        {
            var wsdlOp:mx.rpc.wsdl.WSDLOperation = _wsdl.getOperation(operation.name, service, port);
            if (operation.endpointURI == null) // Only set if the operation has not override the endpoint URI.
                operation.endpointURI = endpointURI;
            operation.wsdlOperation = wsdlOp;
        }
    }

    private function dispatchFault(faultCode:String, faultString:String, faultDetail:String = null):void
    {
        var fault:Fault = new Fault(faultCode, faultString, faultDetail);
        var event:FaultEvent = FaultEvent.createEvent(fault);
        dispatchEvent(event);
    }

    //--------------------------------------------
    // Backing variables for public getter/setters
    //--------------------------------------------
    
	private var _wsdlURL:String;

    //---------------------------------
    // Internal properties
    //---------------------------------
    
	private var _log:ILogger;
    
	private var _wsdlFault:Boolean;
    
	private var _wsdl:mx.rpc.wsdl.WSDL;
    
	private var _wsdlLoader:WSDLLoader;

    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------

    public static const DEFAULT_DESTINATION_HTTP:String = "DefaultHTTP";
    
	public static const DEFAULT_DESTINATION_HTTPS:String = "DefaultHTTPS";
}

}
