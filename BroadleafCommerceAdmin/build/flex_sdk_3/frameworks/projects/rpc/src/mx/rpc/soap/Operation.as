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
import flash.xml.XMLNode;

import mx.core.mx_internal;
import mx.logging.Log;
import mx.logging.ILogger;
import mx.messaging.ChannelSet;
import mx.messaging.events.MessageEvent;
import mx.messaging.messages.IMessage;
import mx.messaging.messages.SOAPMessage;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.AbstractOperation;
import mx.rpc.AbstractService;
import mx.rpc.AsyncToken;
import mx.rpc.Fault;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.HeaderEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.soap.AbstractWebService;
import mx.rpc.wsdl.WSDLOperation;
import mx.rpc.xml.SchemaConstants;
import mx.utils.ObjectProxy;
import mx.utils.XMLUtil;

use namespace mx_internal;

/**
 * Dispatched when an Operation invocation returns with SOAP headers in the
 * response. A HeaderEvent is dispatched for each SOAP header.
 * @eventType mx.rpc.events.HeaderEvent.HEADER
 */
[Event(name="header", type="mx.rpc.events.HeaderEvent")]

[ResourceBundle("rpc")]

/**
 * An Operation used specifically by WebServices. An Operation is an individual
 * method on a service. An Operation can be called either by invoking the
 * function of the same name on the service or by accessing the Operation as a
 * property on the service and calling the <code>send()</code> method.
 */
public class Operation extends AbstractOperation
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * Creates a new Operation. This is usually done directly by the MXML
     * compiler or automatically by the WebService when an unknown operation
     * has been accessed. It is not recommended that a developer use this
     * constructor directly.
     *
     * @param webService The web service upon which this Operation is invoked.
     *
     * @param name The name of this Operation.
     */
    public function Operation(webService:AbstractService = null, name:String = null)
    {
        super(webService, name);

        _resultFormat = "object";
        _headerFormat = "e4x";
        _headers = [];
        log = Log.getLogger("mx.rpc.soap.Operation");

        if (webService)
        {
            this.webService = AbstractWebService(webService);
            log.info("Creating SOAP Operation for {0}", name);
        }

        // No explicit timeout value by default.  The user can set this, and
        // thus engage a timer for this # of milliseconds on each call. If the
        // timer fires before the call returns, a fault will be generated.
        timeout = -1;
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

    //----------------------------------
    //  decoder
    //----------------------------------

    /**
     * The ISOAPDecoder implementation used by this Operation to decode a SOAP
     * encoded response into ActionScript.
     * 
     * @private
     */
    public function get decoder():ISOAPDecoder
    {
        if (_decoder == null)
        {
            _decoder = new SOAPDecoder();
            _decoder.makeObjectsBindable = makeObjectsBindable;
            _decoder.ignoreWhitespace = ignoreWhitespace;
        }

        if (_decoder.wsdlOperation == null)
            _decoder.wsdlOperation = wsdlOperation;

        return _decoder;
    }

    /**
     * @private
     */
    public function set decoder(value:ISOAPDecoder):void
    {
        _decoder = value;
    }


    //----------------------------------
    //  encoder
    //----------------------------------

    /**
     * The ISOAPEncoder implementation used by this Operation to encode
     * ActionScript input arguments as a SOAP encoded request.
     * 
     * @private
     */
    public function get encoder():ISOAPEncoder
    {
        if (_encoder == null)
        {
            _encoder = new SOAPEncoder();
            _encoder.ignoreWhitespace = ignoreWhitespace;
        }

        if (_encoder.wsdlOperation == null)
            _encoder.wsdlOperation = wsdlOperation;
            
        // Tell the encoder to use the xmlSpecialCharsFilter function specified
        // on the Operation (or Service). If null, encoder will default to its
        // own implementation.
        _encoder.xmlSpecialCharsFilter = xmlSpecialCharsFilter;

        return _encoder;
    }

    /**
     * @private
     */
    public function set encoder(value:ISOAPEncoder):void
    {
        _encoder = value;
    }


    //----------------------------------
    //  endpointURI
    //----------------------------------

    /**
     * The location of the WebService for this Operation. Normally, the WSDL
     * specifies the location of the services, but you can set this property to
     * override that location for the individual Operation.
     */
    public function get endpointURI():String
    {
        return _endpointURI ? _endpointURI : webService.endpointURI;
    }

    public function set endpointURI(uri:String):void
    {
        _endpointURI = uri;
    }

    //----------------------------------
    //  forcePartArrays
    //----------------------------------

    [Inspectable(defaultValue="false", category="General")]
    /**
     * Determines whether or not a single or empty return value for an output
     * message part that is defined as an array should be returned as an array
     * containing one (or zero, respectively) elements. This is applicable for
     * document/literal "wrapped" web services, where one or more of the elements
     * that represent individual message parts in the "wrapper" sequence could
     * have the maxOccurs attribute set with a value greater than 1. This is a
     * hint that the corresponding part should be treated as an array even if
     * the response contains zero or one values for that part. Setting
     * forcePartArrays to true will always create an array for parts defined in
     * this manner, regardless of the number of values returned. Leaving
     * forcePartArrays as false will only create arrays if two or more elements
     * are returned.
     */
    public function get forcePartArrays():Boolean
    {
        return _forcePartArrays;
    }

    public function set forcePartArrays(value:Boolean):void
    {
        _forcePartArrays = value;
    }

    //----------------------------------
    //  headerFormat
    //----------------------------------

    [Inspectable(enumeration="object,e4x,xml,E4X,XML", defaultValue="e4x", category="General")]
    /**
     * Determines how the SOAP encoded headers are decoded. A value of
     * <code>object</code> specifies that each header XML node will be decoded
     * into a SOAPHeader object, and its <code>content</code> property will be
     * an object structure as specified in the WSDL document. A value of
     * <code>xml</code> specifies that the XML will be left as XMLNodes. A
     * value of <code>e4x</code> specifies that the XML will be accessible
     * using ECMAScript for XML (E4X) expressions.
     */
    public function get headerFormat():String
    {
        if (_headerFormat == null)
            _headerFormat = "e4x";

        return _headerFormat;
    }

    public function set headerFormat(hf:String):void
    {
        if (hf != null)
            hf = hf.toLowerCase();

        _headerFormat = hf;
    }

    //----------------------------------
    //  headers
    //----------------------------------

    /**
     * Accessor to an Array of SOAPHeaders that are to be sent on
     * each invocation of the operation.
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
        if (_httpHeaders != null)
            return _httpHeaders;

        return AbstractWebService(service).httpHeaders; 
    }

    public function set httpHeaders(value:Object):void
    {
        _httpHeaders = value;
    }


    //----------------------------------
    //  ignoreWhitespace
    //----------------------------------

    [Inspectable(defaultValue="true", category="General")]
    /**
     * Determines whether whitespace is ignored when processing XML for a SOAP
     * encoded request or response. The default is <code>true</code>
     * and thus whitespace is not preserved. If an XML Schema type definition
     * specifies a <code>whiteSpace</code> restriction set to
     * <code>preserve</code> then ignoreWhitespace must first be set to false.
     * Conversely, if a type <code>whiteSpace</code> restriction is set to
     * <code>replace</code> or <code>collapse</code> then that setting will
     * be honored even if ignoreWhitespace is set to false.
     */
    public function get ignoreWhitespace():Boolean
    {
        return _ignoreWhitespace;
    }

    public function set ignoreWhitespace(value:Boolean):void
    {
        _ignoreWhitespace = value;

        if (_decoder != null)
            _decoder.ignoreWhitespace = _ignoreWhitespace;

        if (_encoder != null)
            _encoder.ignoreWhitespace = _ignoreWhitespace;
    }


    //----------------------------------
    //  makeObjectsBindable
    //----------------------------------

    [Inspectable(defaultValue="true", category="General")]
    /**
     * When this value is true, anonymous objects returned are forced to
     * bindable objects.
     */
    override public function get makeObjectsBindable():Boolean
    {
        if (_makeObjectsBindableSet)
        {
            return _makeObjectsBindable;
        }
        return AbstractWebService(service).makeObjectsBindable;    
    }

    override public function set makeObjectsBindable(value:Boolean):void
    {
        _makeObjectsBindable = value;
        _makeObjectsBindableSet = true;

        if (_decoder != null)
            _decoder.makeObjectsBindable = value;
    }


    //----------------------------------
    //  multiplePartsFormat
    //----------------------------------

    [Inspectable(enumeration="object,array", defaultValue="object", category="General")]
    /**
     * Determines the type of the default result object for calls to web services
     * that define multiple parts in the output message. A value of "object"
     * specifies that the lastResult object will be an Object with named properties
     * corresponding to the individual output parts. A value of "array" would
     * make the lastResult an array, where part values are pushed in the order
     * they occur in the body of the SOAP message. The default value for document-
     * literal operations is "object". The default for rpc operations is "array".
     * The multiplePartsFormat property is applicable only when
     * resultFormat is "object" and ignored otherwise.
     */
    public function get multiplePartsFormat():String
    {
        if (_multiplePartsFormat == null)
        {
            // To keep Flex 2.0.1 HF 2+ behavior, we need to determine the
            // default value based on the style of the operation.
            if (_wsdlOperation != null && _wsdlOperation.style == "rpc")
                _multiplePartsFormat = "array";
            else
                _multiplePartsFormat = "object";
        }

        return _multiplePartsFormat;
    }

    public function set multiplePartsFormat(value:String):void
    {
        if (value != null)
            value = value.toLowerCase();

        _multiplePartsFormat = value;
    }

    //----------------------------------
    //  request
    //----------------------------------

    /**
     * The request of the Operation is an object structure or an XML structure.
     * If you specify XML, the XML is sent as is. If you pass an object, it is
     * encoded into a SOAP XML structure.
     */
    public function get request():Object
    {
        return this.arguments;
    }

    public function set request(r:Object):void
    {
        this.arguments = r;
    }


    //----------------------------------
    //  resultFormat
    //----------------------------------

    [Inspectable(enumeration="object,e4x,xml,E4X,XML", defaultValue="object", category="General")]
    /**
     * Determines how the Operation result is decoded. A value of
     * <code>object</code> specifies that the XML will be decoded into an
     * object structure as specified in the WSDL document. A value of
     * <code>xml</code> specifies that the XML will be left as XMLNodes. A
     * value of <code>e4x</code> specifies that the XML will be accessible
     * using ECMAScript for XML (E4X) expressions.
     */
    public function get resultFormat():String
    {
        if (_resultFormat == null)
            _resultFormat = "object";

        return _resultFormat;
    }

    public function set resultFormat(rf:String):void
    {
        if (rf != null)
            rf = rf.toLowerCase();

        _resultFormat = rf;
    }


    //----------------------------------
    //  resultHeaders
    //----------------------------------

    [Bindable("resultForBinding")]
    /**
     * The headers that were returned as part of the last execution of this
     * operation. They match up with the <code>lastResult</code> property and
     * are the same as the collection of headers that are dispatched
     * individually as HeaderEvents.
     */
    public function get resultHeaders():Array
    {
        return _responseHeaders;
    }


    //----------------------------------
    //  xmlSpecialCharsFilter
    //----------------------------------

    private var _xmlSpecialCharsFilter:Function;

    public function get xmlSpecialCharsFilter():Function
    {
        if (_xmlSpecialCharsFilter != null)
            return _xmlSpecialCharsFilter;

        return AbstractWebService(service).xmlSpecialCharsFilter;
    }
    
    public function set xmlSpecialCharsFilter(func:Function):void
    {
        _xmlSpecialCharsFilter = func;
    }


    //--------------------------------------------------------------------------
    //
    // Internal Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    mx_internal var handleAxisSession:Boolean;

    /**
     * @private
     */
    mx_internal function get wsdlOperation():WSDLOperation
    {
        return _wsdlOperation;
    }

    /**
     * @private
     */
    mx_internal function set wsdlOperation(value:WSDLOperation):void
    {
        _wsdlOperation = value;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Adds a header that is applied only to this Operation. The header can be
     * provided in a pre-encoded form as an XML instance, or as a SOAPHeader
     * instance which leaves the encoding up to the internal SOAP encoder.
     * @param header The SOAP header to add to this Operation.
     */
    public function addHeader(header:Object):void
    {
        _headers.push(header);
    }

    /**
     * Adds a header that is applied only to this Operation.
     * @param qnameLocal the localname for the header QName
     * @param qnameNamespace the namespace for header QName
     * @param headerName Name of the header.
     * @param headerValue Value of the header.
     */
    public function addSimpleHeader(qnameLocal:String, qnameNamespace:String,
            headerName:String, headerValue:String):void
    {
        var obj:Object = {};
        obj[headerName] = headerValue;
        addHeader(new SOAPHeader(new QName(qnameNamespace, qnameLocal), obj));
    }

    /**
     * @inheritDoc
     */
    override public function cancel(id:String = null):AsyncToken
    {
        
        if (hasPendingInvocations())
        {
            if (null == id)
            {
                // remove the last pending call
                return pendingInvocations.pop().token;
            }
            // check if id is one of the pending calls
            for (var i:int = pendingInvocations.length-1; i >= 0; i--)
            {
                if (pendingInvocations[i].token.message != null &&
                    pendingInvocations[i].token.message.messageId == id)
                {
                    var pc:OperationPendingCall = pendingInvocations.splice(i,1)[0];
                    return pc.token;
                }
            }
        }
        //if the call is not pending, use super
        return super.cancel(id);
    }

    /**
     * Clears the headers for this individual Operation.
     */
    public function clearHeaders():void
    {
        _headers.length = 0;
    }

    /**
     * Returns a header if a match is found based on QName localName and URI.
     * @param qname QName of the SOAPHeader.
     * @param headerName Name of a header in the SOAPHeader content (Optional)
     * @return Returns the SOAPHeader.
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
     * @param qname QName of the SOAPHeader.
     * @param headerName Name of a header in the SOAPHeader content (Optional)
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
     * @private
     */
    override public function send(...args:Array):AsyncToken
    {
        var argsToPass:Object = null;
        if (args && args.length > 0)
        {
            if ((args.length == 1) && (args[0] is XMLNode || args[0] is XML))
            {
                // special case: handle xml node as single argument and drop
                // into literal mode.
                argsToPass = args[0];
            }
            else
            {
                argsToPass = args;
            }
        }
        //Syntactically pre-registered
        else if (this.arguments)
        {
            argsToPass = this.arguments;
        }

        var combinedHeaders:Array = [];

        if (_headers)
        {
            combinedHeaders = combinedHeaders.concat(_headers);
        }

        if (webService.headers)
        {
            combinedHeaders = combinedHeaders.concat(webService.headers);
        }

        //create an empty message and a token to hold it
        var message:SOAPMessage = new SOAPMessage();
        var token:AsyncToken = new AsyncToken(message);
        
        var pc:OperationPendingCall = new OperationPendingCall(argsToPass, combinedHeaders, token);

        if (webService.ready)
        {
            invokePendingCall(pc);
        }
        else // if (!webService.wsdlFault)
        {
            log.debug("Queueing SOAP operation {0}", name);
            if (!pendingInvocations)
            {
                pendingInvocations = [];
            }
            pendingInvocations.push(pc);
        }
        // FIXME: Handle WSDL error case
        /*
        else
        {
            var errMsg:String = "Cannot invoke method " + name + " as WSDL did not load successfully";
            dispatchRpcEvent(createFaultEvent("Client.InvalidWSDL", errMsg));
        }
        */

        return pc.token;
    }


    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    mx_internal function hasPendingInvocations():Boolean
    {
        return pendingInvocations != null && pendingInvocations.length > 0;
    }

    /**
     * @private
     */
    mx_internal function invokeAllPending():void
    {
        if (hasPendingInvocations())
        {
            for (var i:int = 0; i < pendingInvocations.length; ++i)
            {
                invokePendingCall(pendingInvocations[i]);
            }
            pendingInvocations = null;
        }
    }

    /**
     * We now SOAP encode the pending call and send the request.
     * 
     * @private
     */
    mx_internal function invokePendingCall(pc:OperationPendingCall):void
    {
        log.debug("Invoking SOAP operation {0}", name);

        startTime = new Date();

        var message:SOAPMessage = SOAPMessage(pc.token.message);
        var soap:XML;

        if (wsdlOperation == null)
        {
            log.debug("No operation found {0}", name);
            dispatchRpcEvent(createFaultEvent("Client.NoSuchMethod", "Couldn't find method '" + name + "' in service."));
            return;
        }

        try
        {
            soap = encoder.encodeRequest(pc.args, pc.headers);
        }
        catch(fault:Fault)
        {
            dispatchRpcEvent(FaultEvent.createEvent(fault));
            return;
        }
        catch(error:Error)
        {
            var errorMsg:String = error.message ? error.message : "";
            var fault2:Fault = new Fault("EncodingError", errorMsg);
            var faultEvent:FaultEvent = FaultEvent.createEvent(fault2);
            dispatchRpcEvent(faultEvent);
            return;
        }

        message.httpHeaders = httpHeaders;
        if (message.getSOAPAction() == null)
            message.setSOAPAction(wsdlOperation.soapAction);
        message.body = soap.toXMLString();
        message.url = endpointURI;
        invoke(message, pc.token);
    }

    /**
     * We decode the SOAP encoded response and update the result and response
     * headers (if any).
     * 
     * @private
     */
    override mx_internal function processResult(message:IMessage, token:AsyncToken):Boolean
    {
        var body:Object = message.body;
        var dispatchResultEvent:Boolean = true;

        try
        {
            var stringResult:String = String(body);
            decoder.resultFormat = resultFormat;
            decoder.headerFormat = headerFormat;
            decoder.multiplePartsFormat = multiplePartsFormat;
            decoder.forcePartArrays = forcePartArrays;

            var soapResult:SOAPResult = decoder.decodeResponse(stringResult);

            // Reset result
            _result = null;

            // Expose headers for the bindable property "resultHeaders" as
            // well as RPC fault and result events...
            _responseHeaders = soapResult.headers;

            // Process headers for both result and fault
            dispatchResultEvent = processHeaders(_responseHeaders, token, message);

            // Handle faults
            if (soapResult.isFault)
            {
                var faults:Array = soapResult.result as Array;
                for each (var soapFault:Fault in faults)
                {
                    dispatchRpcEvent(FaultEvent.createEvent(soapFault, token, message));
                }
                dispatchResultEvent = false;
            }

            if (dispatchResultEvent)
                _result = soapResult.result;
        }
        catch(fault:Fault)
        {
            dispatchRpcEvent(FaultEvent.createEvent(fault, token, message));
            return false;
        }
        catch(error:Error)
        {
            var errorMsg:String = error.message != null ? error.message : "";
            var fault2:Fault = new Fault("DecodingError", errorMsg);
            var faultEvent:FaultEvent = FaultEvent.createEvent(fault2, token, message);
            dispatchRpcEvent(faultEvent);
            return false;
        }

        return dispatchResultEvent;
    }

    /**
     * Checks SOAP response headers and enforces any mustUnderstand attributes
     * by checking that a listener exists for the "header" event. If we're
     * honoring Axis sessions then we also look out for for the sessionID
     * header and add it to the Operation's corresponding service for
     * subsequent invocations. Finally, the header is dispatched as a
     * HeaderEvent. If no problems are encountered the method simply returns
     * true.
     * 
     * @private
     */
    protected function processHeaders(responseHeaders:Array, token:AsyncToken, message:IMessage):Boolean
    {
        if (responseHeaders != null)
        {
            for (var i:uint = 0; i < responseHeaders.length; i++)
            {
                var header:Object = responseHeaders[i];
                var mustUnderstand:Boolean;
                var headerQName:QName;
                var headerContent:Object;

                if (header is XML)
                {
                    var headerXML:XML = header as XML;

                    mustUnderstand = headerXML.@mustUnderstand == "1" ? true : false;
                    headerQName = headerXML.name();

                    if (headerXML.hasComplexContent())
                        headerContent = headerXML.elements();
                    else
                        headerContent = headerXML.text();

                }
                else if (header is SOAPHeader)
                {
                    mustUnderstand = header.mustUnderstand;
                    headerQName = header.qname;
                    headerContent = header.content;
                }

                // If header was either XML or SOAPHeader, headerQName would
                // be set by now.
                if (headerQName != null)
                {
                    if (mustUnderstand == true)
                    {
                        if (!hasEventListener(HeaderEvent.HEADER) && !service.hasEventListener(HeaderEvent.HEADER))
                        {
    						var msg:String = resourceManager.getString(
    							"rpc", "noListenerForHeader",
    							[ headerQName ]);
                            var fault:Fault = new Fault("Client.MustUnderstand", msg);
                            var faultEvent:FaultEvent = FaultEvent.createEvent(fault, token, message);
                            dispatchRpcEvent(faultEvent);
                            return false;
                        }
                    }

                    if (handleAxisSession)
                    {
                        // pass the session id on to any request made on this proxy
                        if (headerQName != null && headerQName.localName == "sessionID" &&
                            headerQName.uri == "http://xml.apache.org/axis/session")
                        {
                            var newHeader:SOAPHeader = new SOAPHeader(headerQName, headerContent);
                            webService.addHeader(newHeader);
                        }
                    }

                    var headerEvent:HeaderEvent = HeaderEvent.createEvent(header, token, message);
                    dispatchRpcEvent(headerEvent);
                }
            }
        }

        return true; 
    }

    /**
     * @private
     */
    override mx_internal function setService(value:AbstractService):void
    {
        super.setService(value);
        webService = AbstractWebService(value);
    }

    /**
     * @private
     */
    protected function createFaultEvent(faultCode:String = null, faultString:String = null, faultDetail:String = null):FaultEvent
    {
        var fault:Fault = new Fault(faultCode, faultString, faultDetail);
        var faultEvent:FaultEvent = FaultEvent.createEvent(fault);
        return faultEvent;
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    // Backing variables for public getter/setters
    private var _endpointURI:String;
    private var _forcePartArrays:Boolean = false; // Flex 2.0.1 HF2+ behavior is false
    private var _headerFormat:String;
    private var _headers:Array;
    private var _resultFormat:String;
    private var _makeObjectsBindableSet:Boolean;
    private var _multiplePartsFormat:String;

    // Internal properties
    //any vars here with underscores minimally have a getter or setter
    private var _decoder:ISOAPDecoder;
    private var _encoder:ISOAPEncoder;
    private var _ignoreWhitespace:Boolean = true;
    private var log:ILogger;
    private var pendingInvocations:Array;
    private var startTime:Date;
    private var timeout:int;
    private var webService:AbstractWebService;

    /**
     * @private
     */
    protected var _wsdlOperation:mx.rpc.wsdl.WSDLOperation;
}
}

import mx.rpc.AsyncToken;

/**
 * @private
 */
class OperationPendingCall
{
    public var args:*;
    public var headers:Array;
    public var token:AsyncToken;

    public function OperationPendingCall(args:*, headers:Array, token:AsyncToken)
    {
        super();
        this.args = args;
        this.headers = headers;
        this.token = token;
    }
}
