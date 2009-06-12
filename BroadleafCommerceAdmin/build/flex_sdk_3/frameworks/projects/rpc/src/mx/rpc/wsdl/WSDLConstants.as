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

package mx.rpc.wsdl
{

import mx.utils.StringUtil;
import mx.utils.URLUtil;

[ExcludeClass]

/**
 * Manages the constants for a particular version of WSDL (and its
 * accompanying version of SOAP).
 * 
 * The default version is WSDL 1.1.
 * 
 * @private
 */
public class WSDLConstants
{
    public function WSDLConstants(wsdlNS:Namespace = null, soapNS:Namespace = null)
    {
        super();

        // Default to WSDl 1.1 and SOAP 1.1
        if (wsdlNS == null)
            wsdlNS = new Namespace(WSDL_PREFIX, WSDL_URI);
        if (soapNS == null)
            soapNS = new Namespace(WSDL_SOAP_PREFIX, WSDL_SOAP_URI);

        _wsdlNS = wsdlNS;
        _soapNS = soapNS;

        definitionsQName = new QName(wsdlURI, "definitions");
        importQName = new QName(wsdlURI, "import");
        typesQName = new QName(wsdlURI, "types");
        messageQName = new QName(wsdlURI, "message");
        portTypeQName = new QName(wsdlURI, "portType");
        bindingQName = new QName(wsdlURI, "binding");
        serviceQName = new QName(wsdlURI, "service");
        documentationQName = new QName(wsdlURI, "documentation");
        portQName = new QName(wsdlURI, "port");
        operationQName = new QName(wsdlURI, "operation");
        inputQName = new QName(wsdlURI, "input");
        outputQName = new QName(wsdlURI, "output");
        partQName = new QName(wsdlURI, "part");
        faultQName = new QName(wsdlURI, "fault");   
        wsdlArrayTypeQName = new QName(wsdlURI, "arrayType"); 

        soapAddressQName = new QName(soapURI, "address");
        soapBindingQName = new QName(soapURI, "binding");
        soapOperationQName = new QName(soapURI, "operation");
        soapBodyQName = new QName(soapURI, "body");
        soapFaultQName = new QName(soapURI, "fault");
        soapHeaderQName = new QName(soapURI, "header");
        soapHeaderFaultQName = new QName(soapURI, "headerfault");
    }


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    public function get soapURI():String
    {
        return soapNamespace.uri;
    }

    public function get wsdlURI():String
    {
        return wsdlNamespace.uri;
    }

    public function get soapNamespace():Namespace
    {
        return _soapNS;
    }

    public function get wsdlNamespace():Namespace
    {
        return _wsdlNS;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    public static function getConstants(xml:XML):WSDLConstants
    {
        var wsdlNS:Namespace;
        var soapNS:Namespace;

        if (xml != null)
        {
            var nsArray:Array = xml.inScopeNamespaces();
            for each (var ns:Namespace in nsArray)
            {
                if (URLUtil.urisEqual(ns.uri, WSDL_URI))
                {
                    wsdlNS = ns;
                }
                else if (URLUtil.urisEqual(ns.uri, WSDL20_URI))
                {
                    wsdlNS = ns;
                }
                if (URLUtil.urisEqual(ns.uri, WSDL_SOAP_URI))
                {
                    soapNS = ns;
                }
                else if (URLUtil.urisEqual(ns.uri, WSDL20_SOAP_URI))
                {
                    soapNS = ns;
                }
                else if (URLUtil.urisEqual(ns.uri, WSDL20_SOAP12_URI))
                {
                    soapNS = ns;
                }
            }
        }

        // Default to WSDL 1.1
        if (wsdlNS == null)
            wsdlNS = new Namespace(WSDL_PREFIX, WSDL_URI);

        if (soapNS == null)
            soapNS = new Namespace(WSDL_SOAP_PREFIX, WSDL_SOAP_URI);

        if (constantsCache == null)
            constantsCache = {};

        var constants:WSDLConstants = constantsCache[wsdlNS.uri];
        if (constants == null)
        {
            constants = new WSDLConstants(wsdlNS, soapNS);
            constantsCache[wsdlNS.uri] = constants;
        }

        return constants;
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    // WSDL QNames
    public var definitionsQName:QName;
    public var typesQName:QName;
    public var messageQName:QName;
    public var portTypeQName:QName;
    public var bindingQName:QName;
    public var serviceQName:QName;
    public var importQName:QName;
    public var documentationQName:QName;
    public var portQName:QName;
    public var operationQName:QName;
    public var inputQName:QName;
    public var outputQName:QName;
    public var partQName:QName;
    public var faultQName:QName;
    public var wsdlArrayTypeQName:QName;

    // WSDL SOAP QNames
    public var soapAddressQName:QName;
    public var soapBindingQName:QName;
    public var soapOperationQName:QName;
    public var soapBodyQName:QName;
    public var soapFaultQName:QName;
    public var soapHeaderQName:QName;
    public var soapHeaderFaultQName:QName;

    /**
     * The namespace representing the version of SOAP used by a WSDL,
     * currently 1.1 is supported.
     * FIXME: Need SOAP 1.2 support.
     */
    private var _soapNS:Namespace;

    /**
     * The namespace representing the version of WSDL, currently only 1.1 is
     * supported.
     * TODO: Need WSDL 2.0 support.
     */
    private var _wsdlNS:Namespace;

    private static var constantsCache:Object;


    //--------------------------------------------------------------------------
    //
    // Constants
    // 
    //--------------------------------------------------------------------------
    
    // SOAP Binding Constants
    public static const SOAP_HTTP_URI:String = "http://schemas.xmlsoap.org/soap/http/";

    public static const MODE_IN:int = 0;
    public static const MODE_OUT:int = 1;
    public static const MODE_FAULT:int = 2;
    public static const MODE_HEADER:int = 3;

    //WSDL 1.1 Namespaces
    public static const WSDL_URI:String = "http://schemas.xmlsoap.org/wsdl/";
    public static const WSDL_SOAP_URI:String = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static const WSDL_SOAP12_URI:String = "http://schemas.xmlsoap.org/wsdl/soap12/";
    public static const WSDL_HTTP_URI:String = "http://schemas.xmlsoap.org/wsdl/http/";

    //WSDL 2.0 Namespaces
    public static const WSDL20_URI:String = "http://www.w3.org/2006/01/wsdl";
    public static const WSDL20_SOAP_URI:String = "http://www.w3.org/2006/01/wsdl/soap";
    public static const WSDL20_SOAP12_URI:String = "http://www.w3.org/2006/01/wsdl/soap";
    public static const WSDL20_HTTP_URI:String = "http://www.w3.org/2006/01/wsdl/http";

    // WSDL Namespace Prefix
    public static const WSDL_PREFIX:String = "wsdl";
    public static const WSDL_SOAP_PREFIX:String = "wsoap";

    public static const DEFAULT_STYLE:String = "document";
    public static const DEFAULT_WSDL_VERSION:String = "1.1";
}
    
}