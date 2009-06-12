////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
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
import mx.rpc.AbstractOperation;
import mx.rpc.AbstractService;
import mx.rpc.AsyncRequest;
import mx.rpc.Fault;
import mx.rpc.events.FaultEvent;
import mx.rpc.http.HTTPService;
import mx.utils.URLUtil;
import mx.utils.XMLUtil;

use namespace mx_internal;

/**
 * AbstractWebService is an abstract base class for implementations 
 * that provide RPC access to SOAP-based web services. This class does not
 * load WSDL descriptions at runtime.
 */
public class AbstractWebService extends AbstractService
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new WebService.  The destination, if specified, should match an
     * entry in services-config.xml.  If unspecified, the WebService uses the
     * DefaultHTTP destination. The <code>rootURL</code> is required if you
     * intend to use a relative URL find the WSDL document for this WebService.
     */
    public function AbstractWebService(destination:String = null, rootURL:String = null)
    {
        super(destination);

        _useProxy = false;
        _headers = [];
        _log = Log.getLogger("mx.rpc.soap.AbstractWebService");

        if (destination == null)
        {
            // If the SWF was loaded via HTTPS, we'll use the DefaultHTTPS
            // destination by default
            if (URLUtil.isHttpsURL(LoaderConfig.url))
                super.destination = DEFAULT_DESTINATION_HTTPS;
            else
                super.destination = DEFAULT_DESTINATION_HTTP;

            destinationSet = false;
        }
        else
        {
            destinationSet = true;
        }

        makeObjectsBindable = true;

        _ready = true;
    }

    //-------------------------------------------------------------------------
    //
    // Properties
    //
    //-------------------------------------------------------------------------

    //----------------------------------
    //  description
    //----------------------------------

    /**
     * The description of the service for the currently active port.
     */
    public function get description():String
    {
        return _description;
    }

    public function set description(value:String):void
    {
        _description = value;
    }


    //----------------------------------
    //  destination
    //----------------------------------

    /**
     * @inheritDoc
     */
    override public function get destination():String
    {
        return super.destination;
    }

    override public function set destination(value:String):void
    {
        super.destination = value;
        useProxy = true;
        destinationSet = true;
    }


    //----------------------------------
    //  endpointURI
    //----------------------------------

    /**
     * The location of the WebService. Normally, the WSDL document specifies
     * the location of the services, but you can set this property to override
     * that location.
     */
    public function get endpointURI():String
    {
        return (endpointOverride) ? endpointOverride : _endpointURI;
    }

    public function set endpointURI(value:String):void
    {
        endpointOverride = value;
    }


    //----------------------------------
    //  headers
    //----------------------------------

    /**
     * Returns the array of SOAPHeaders registered for the WebService.
     */
    public function get headers():Array
    {
        return _headers;
    }


    //----------------------------------
    //  httpHeaders
    //----------------------------------

    private var _httpHeaders:Object;

    [Inspectable(defaultValue="undefined", category="General")]
    /**
     * Custom HTTP headers to be sent to the SOAP endpoint. If multiple
     * headers need to be sent with the same name the value should be specified
     * as an Array.
     */
    public function get httpHeaders():Object
    {
        return _httpHeaders;
    }

    public function set httpHeaders(value:Object):void
    {
        _httpHeaders = value;
    }


    //----------------------------------
    //  makeObjectsBindable
    //----------------------------------

    [Inspectable(defaultValue="true", category="General")]
    /**
     * When this value is true, anonymous objects returned are forced to
     * bindable objects.
     */
    public function get makeObjectsBindable():Boolean
    {
        return _makeObjectsBindable;
    }

    public function set makeObjectsBindable(value:Boolean):void
    {
        _makeObjectsBindable = value;
    }


    //----------------------------------
    //  port
    //----------------------------------

    /**
     * Specifies the port within the WSDL document that this WebService should
     * use.
     */
    public function get port():String
    {
        return _port;
    }

    public function set port(value:String):void
    {
        _port = value;
    }


    //----------------------------------
    //  ready
    //----------------------------------

    /**
     * Specifies whether the WebService is ready to make requests.
     */
    public function get ready():Boolean
    {
        return _ready;
    }


    //----------------------------------
    //  rootURL
    //----------------------------------

    /**
     * The URL that the WebService should use when computing relative URLs. This
     * property is only used when going through the proxy. When the
     * <code>useProxy</code> property is set <code>to false</code> the relative
     * URL is computed automatically based on the location of the SWF running
     * this application. If not set explicitly <code>rootURL</code> is
     * automatically set to the URL of mx.messaging.config.LoaderConfig.url.
     */
    public function get rootURL():String
    {
        if (_rootURL == null)
        {
            _rootURL = LoaderConfig.url;
        }
        return _rootURL;
    }

    public function set rootURL(value:String):void
    {
        _rootURL = value;
    }


    //----------------------------------
    //  service
    //----------------------------------

    /**
     * Specifies the service within the WSDL document that this WebService
     * should use.
     */
    public function get service():String
    {
        return _service;
    }

    public function set service(value:String):void
    {
        _service = value;
    }


    //----------------------------------
    //  useProxy
    //----------------------------------

    [Inspectable(defaultValue="false", category="General")]
    /**
     * Specifies whether to use the Flex proxy service. The default value is
     * <code>false</code>. If you do not specify <code>true</code> to proxy
     * requests though the Flex server, you must ensure that Flash Player can
     * reach the target URL. You also cannot use destinations defined in the
     * services-config.xml file if the <code>useProxy</code> property is set
     * to <code>false</code>.
     *
     * @default false
     */
    public function get useProxy():Boolean
    {
        return _useProxy;
    }

    public function set useProxy(value:Boolean):void
    {
        if (value != _useProxy)
        {
            _useProxy = value;

            var dcs:ChannelSet = getDirectChannelSet();
            if (!useProxy)
            {
                if (dcs != asyncRequest.channelSet)
                    asyncRequest.channelSet = dcs;
            }
            else
            {
                if (asyncRequest.channelSet == dcs)
                    asyncRequest.channelSet = null;
            }
        }
    }


    //----------------------------------
    //  xmlSpecialCharsFilter
    //----------------------------------

    private var _xmlSpecialCharsFilter:Function;

    [Inspectable(defaultValue="undefined", category="General")]
    /**
     * Custom function to be used to escape XML special characters before
     * encoding any simple content. Valid for all operations on the web
     * service unless specifically overwritten on the operation level.
     * If none is provided, the defaults to whatever is set by the particular
     * implementation of IXMLEncoder
     */
    public function get xmlSpecialCharsFilter():Function
    {
        return _xmlSpecialCharsFilter;
    }

    public function set xmlSpecialCharsFilter(func:Function):void
    {
        _xmlSpecialCharsFilter = func;
    }


    //-------------------------------------------------------------------------
    //
    // Methods
    //
    //-------------------------------------------------------------------------

    /**
     * Adds a header that will be applied to all operations of this web service.
     * The header can be provided in a pre-encoded form as an XML instance, or
     * as a SOAPHeader instance which leaves the encoding up to the internal
     * SOAP encoder.
     *  
     * @param header The SOAP header to add to all operations.
     */
    public function addHeader(header:Object):void
    {
        _headers.push(header);
    }

    /**
     * Add a header that will be applied to all operations of this WebService.
     *  
     * @param qnameLocal The localname for the header QName.
     * @param qnameNamespace The namespace for the header QName.
     * @param headerName The name of the header.
     * @param headerValue The value of the header.
     */
    public function addSimpleHeader(qnameLocal:String, qnameNamespace:String, headerName:String, headerValue:String):void
    {
        var obj:Object = {};
        obj[headerName] = headerValue;
        addHeader(new SOAPHeader(new QName(qnameNamespace, qnameLocal), obj));
    }

    /**
     * Clears the headers that applied to all operations.
     */
    public function clearHeaders():void
    {
        _headers.length = 0;
    }

    /**
     * Returns a header if a match is found based on QName, localName, and URI.
     *  
     * @param qname QName of the SOAPHeader.
     * @param headerName (Optional) Name of a header in the SOAPHeader content.
     *
     * @return Returns a header if a match is found based on QName, localName, and URI.
     */
    public function getHeader(qname:QName, headerName:String = null):SOAPHeader
    {
        var length:uint = _headers.length;
        for (var i:uint = 0; i < length; i++)
        {
            var header:SOAPHeader = SOAPHeader(_headers[i]);
            if (XMLUtil.qnamesEqual(header.qname, qname))
            {
                if (headerName)
                {
                    if (header.content && header.content[headerName])
                    {
                        return header;
                    }
                }
                else
                {
                    return header;
                }
            }
        }

        return null;
    }

    /**
     * Removes the header with the given QName from all operations.
     *  
     * @param qname QName of the SOAPHeader.
     * @param headerName (Optional) Name of a header in the SOAPHeader content.
     */
    public function removeHeader(qname:QName, headerName:String = null):void
    {
        var length:uint = _headers.length;
        for (var i:uint = 0; i < length; i++)
        {
            var header:SOAPHeader = SOAPHeader(_headers[i]);
            if (XMLUtil.qnamesEqual(header.qname, qname))
            {
                if (headerName)
                {
                    if (header.content && header.content[headerName])
                    {
                        _headers.splice(i, 1);
                        return; // Got it
                    }
                }
                else
                {
                    _headers.splice(i, 1);
                    return; // Got it
                }
            }
        }
    }

    /**
     * The username and password to authenticate a user when accessing
     * the webservice.  These will be passed as part of the HTTP Authorization
     * header from the proxy to the endpoint. If useProxy is false this property
     * will be ignored.
     *
     * @param remoteUsername The username to pass to the remote endpoint.
     * @param remotePassword The password to pass to the remote endpoint.
     * @param charset The character set encoding to use while encoding the
     * remote credentials. The default is null, which implies the legacy charset
     * of ISO-Latin-1. The only other supported charset is &quot;UTF-8&quot;.
     */
    override public function setRemoteCredentials(remoteUsername:String, remotePassword:String, charset:String=null):void
    {
        super.setRemoteCredentials(remoteUsername, remotePassword, charset);
    }

    //---------------------------------
    // Helper methods
    //---------------------------------

    /**
     * Returns a JSESSIOND found in the URL.  This should be attached
     * to any communication back with the server where session state needs to
     * be preserved
     * @private
     */
    mx_internal static function findJSessionID():String
    {
        var jsessionid:String = null;
        var args:Object = LoaderConfig.parameters;
        if (args)
        {
            var js:Object = args.jsessionid;
            if (!js)
            {
                js = args.JSESSIONID;

                if (!js)
                {
                    var u:String = LoaderConfig.url;
                    if (u)
                    {
                        var ind:Number = u.lastIndexOf(";")
                        if (ind != -1)
                        {
                            var arr:Array = u.substring(ind + 1).split("=");

                            if (arr[0] == "jsessionid" || arr[0] == "JSESSIONID")
                            {
                                jsessionid = arr[1];
                            }
                        }
                    }
                }
                else
                {
                    jsessionid = String(js);
                }
            }
            else
            {
                jsessionid = String(js);
            }
        }
        return jsessionid;
    }

    /**
     * @private
     */
    mx_internal function getDirectChannelSet():ChannelSet
    {
        if (_directChannelSet == null)
        {
            var dcs:ChannelSet = new ChannelSet();
            dcs.addChannel(new DirectHTTPChannel("direct_http_channel"));
            _directChannelSet = dcs;            
        }
        return _directChannelSet;  
    }

    // Deals with the queued calls - if a fault is passed, we'll simply dispatch
    // a FaultEvent for each call.  Otherwise we invoke each one.
    /**
     * @private
     */
    protected function unEnqueueCalls(fault:Fault = null):void
    {
        var op:Operation;
        for (var opName:String in _operations)
        {
            op = _operations[opName];
            if (op.hasPendingInvocations())
            {
                if (fault != null)
                {
                    _log.info("Faulting previously queued operation calls {0}", op.name);
                    while (op.hasPendingInvocations())
                    {
                        op.cancel();
                        op.dispatchEvent(FaultEvent.createEvent(fault));
                    }
                }
                else
                {
                    _log.info("Invoking previously queued calls {0}", op.name);
                    op.invokeAllPending();
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    //
    // Variables
    //
    //-------------------------------------------------------------------------

    //--------------------------------------------
    // Backing variables for public getter/setters
    //--------------------------------------------

    /**
     * @private
     */
    protected var _endpointURI:String;
 
    private var _description:String;
 
    private var endpointOverride:String;
 
    private var _headers:Array;
 
    private var _makeObjectsBindable:Boolean;
 
    /**
     * @private
     */
    protected var _port:String;

    private var _rootURL:String;

    /**
     * @private
     */
    protected var _service:String;

    private var _useProxy:Boolean;

    //---------------------------------
    // Internal properties
    //---------------------------------
 
    //private var _activePort:Object;
 
    /**
     * @private
     */
    protected var destinationSet:Boolean;
 
    /**
     * @private
     */
    protected var _ready:Boolean;
 
    private var _log:ILogger;

    /**
     * @private
     */
    private static var _directChannelSet:ChannelSet = null;

    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------


    /**
     * The default destination to use for HTTP connections when invoking a webservice through a proxy. 
     * If you don't provide a destination and you set the <code>useProxy</code> property to <code>true</code>, 
     * the default destinations will be used to route the requests to the webservice endpoint. 
     * 
     * <p>Note that if the default destinations are used, you must specify the WSDL and endpointURI on the client. 
     * If you use a non-default proxy destination, you can have the WSDL and endpointURI specified in the 
     * destination configuration.</p>
     */
    public static const DEFAULT_DESTINATION_HTTP:String = "DefaultHTTP";

    /**
     * The default destination to use for HTTPS connections when invoking a webservice through a proxy. 
     * If you don't provide a destination and you set the <code>useProxy</code> property to <code>true</code>, 
     * the default destinations will be used to route the requests to the webservice endpoint. 
     * 
     * <p>Note that if the default destinations are used, you must specify the WSDL and endpointURI on the client. 
     * If you use a non-default proxy destination, you can have the WSDL and endpointURI specified in the 
     * destination configuration.</p>
     */
    public static const DEFAULT_DESTINATION_HTTPS:String = "DefaultHTTPS";
}

}
