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

package mx.messaging.messages
{

import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("messaging")]

[RemoteClass(alias="flex.messaging.messages.HTTPMessage")]

/**
 *  HTTP requests are sent to the HTTP endpoint using this message type.
 *  An HTTPRequestMessage encapsulates content and header information normally
 *  found in HTTP requests made by a browser.
 */
public class HTTPRequestMessage extends AbstractMessage
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Constructs an uninitialized HTTP request.
     */
    public function HTTPRequestMessage()
    {
        super();
        _method = GET_METHOD;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     *  Indicates the content type of this message.
     *  This value must be understood by the destination this request is sent to.
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public var contentType:String;

    /**
     *  Contains specific HTTP headers that should be placed on the request made
     *  to the destination.
     */
    public var httpHeaders:Object;
    
    /**
     * Only used when going through the proxy, should the proxy 
     * send back the request and response headers it used.  Defaults to false.
     * Currently only set when using the NetworkMonitor.
     */
    public var recordHeaders:Boolean;    
    
    [Inspectable(defaultValue="undefined", category="General")]
    /**
     *  Contains the final destination for this request.
     *  This is the URL that the content of this message, found in the
     *  <code>body</code> property, will be sent to, using the method specified.
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public var url:String;    

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
	//  method
	//----------------------------------

    /**
     *  @private
     */
    private var _method:String;

    [Inspectable(category="General")]
    /**
     *  Indicates what method should be used for the request.
     *  The only values allowed are:
     *  <ul>
     *    <li><code>HTTPRequestMessage.DELETE_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.GET_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.HEAD_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.POST_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.OPTIONS_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.PUT_METHOD</code></li>
     *    <li><code>HTTPRequestMessage.TRACE_METHOD</code></li>
     *  </ul>
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public function get method():String
    {
        return _method;
    }

    /**
     *  @private
     */
    public function set method(value:String):void
    {
        if (VALID_METHODS.indexOf(value) == -1)
		{
			var message:String = resourceManager.getString(
				"messaging", "invalidRequestMethod");
            throw new ArgumentError(message);
		}

        _method = value;
    }

    //--------------------------------------------------------------------------
    //
    // Static Constants
    // 
    //--------------------------------------------------------------------------
    
    /**
     *  Indicates that the content of this message is XML.
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_XML;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const CONTENT_TYPE_XML:String = "application/xml";
    
    /**
     *  Indicates that the content of this message is a form.
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const CONTENT_TYPE_FORM:String = "application/x-www-form-urlencoded";
    
    /**
     *  Indicates that the content of this message is XML meant for a SOAP
     *  request.
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_SOAP_XML;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const CONTENT_TYPE_SOAP_XML:String = "text/xml; charset=utf-8";

    /**
     *  Indicates that the method used for this request should be "post".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.POST_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const POST_METHOD:String = "POST";

    /**
     *  Indicates that the method used for this request should be "get".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.GET_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const GET_METHOD:String = "GET";

    /**
     *  Indicates that the method used for this request should be "put".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.PUT_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const PUT_METHOD:String = "PUT";

    /**
     *  Indicates that the method used for this request should be "head".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.HEAD_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const HEAD_METHOD:String = "HEAD";

    /**
     *  Indicates that the method used for this request should be "delete".
     *  
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.DELETE_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const DELETE_METHOD:String = "DELETE";

    /**
     *  Indicates that the method used for this request should be "options".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.OPTIONS_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const OPTIONS_METHOD:String = "OPTIONS";

    /**
     *  Indicates that the method used for this request should be "trace".
     *
     *    <pre>
     *      var msg:HTTPRequestMessage = new HTTPRequestMessage();
     *      msg.contentType = HTTPRequestMessage.CONTENT_TYPE_FORM;
     *      msg.method = HTTPRequestMessage.TRACE_METHOD;
     *      msg.url = "http://my.company.com/login";
     *    </pre>
     */
    public static const TRACE_METHOD:String = "TRACE";

    /**
     *  @private
     */
    private static const VALID_METHODS:String = "POST,PUT,GET,HEAD,DELETE,OPTIONS,TRACE";       
}

}
