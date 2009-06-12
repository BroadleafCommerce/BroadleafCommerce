////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.soap
{

import flash.utils.getDefinitionByName;
import flash.utils.getQualifiedClassName;

import mx.rpc.soap.types.ICustomSOAPType;
import mx.rpc.soap.types.MapType;
import mx.rpc.soap.types.QueryBeanType;
import mx.rpc.soap.types.RowSetType;
import mx.rpc.soap.types.SOAPArrayType;
import mx.rpc.soap.types.ApacheDocumentType;
import mx.rpc.soap.types.DataSetType;
import mx.utils.StringUtil;
import mx.utils.URLUtil;

[ExcludeClass]

/**
 * A helper class listing all of the constants required to encode and decode
 * SOAP messages.
 * 
 * @private
 */
public class SOAPConstants
{
    public function SOAPConstants(envelopeNS:Namespace = null, encodingNS:Namespace = null)
    {
        super();

        // Default to SOAP 1.1
        if (envelopeNS == null)
            envelopeNS = new Namespace(SOAP_ENV_PREFIX, SOAP_ENVELOPE_URI);

        if (encodingNS == null)
            encodingNS = new Namespace(SOAP_ENC_PREFIX, SOAP_ENCODING_URI);

        _envelopeNS = envelopeNS;
        _encodingNS = encodingNS;

        envelopeQName = new QName(envelopeURI, "Envelope");
        headerQName = new QName(envelopeURI, "Header");
        bodyQName = new QName(envelopeURI, "Body");
        faultQName = new QName(envelopeURI, "Fault");
        actorQName = new QName(envelopeURI, "actor");
        mustUnderstandQName = new QName(envelopeURI, "mustUnderstand");

        soapencArrayQName = new QName(encodingURI, "Array");
        soapencArrayTypeQName = new QName(encodingURI, "arrayType");
        soapencRefQName = new QName(encodingURI, "multiRef");
        soapoffsetQName = new QName(encodingURI, "offset");
        soapBase64QName = new QName(encodingURI, "base64");

        // Initialize custom SOAP type handlers
        if (!customTypesInitialized)
            initCustomSOAPTypes();
        
        // Register Types with version-specific QNames
        registerCustomSOAPType(soapencArrayQName, SOAPArrayType);
        registerCustomSOAPType(soapencArrayTypeQName, SOAPArrayType);
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------
    
    public function get contentType():String
    {
        return _contentType;
    }

    public function get encodingURI():String
    {
        return encodingNamespace.uri;
    }

    public function get encodingNamespace():Namespace
    {
        return _encodingNS;
    }
    
    public function get envelopeURI():String
    {
        return envelopeNamespace.uri;
    }

    public function get envelopeNamespace():Namespace
    {
        return _envelopeNS;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    public function getSOAPEncodingToken(type:QName):String
    {
        return encodingNamespace.prefix + ":" + type.localName;
    }

    public static function getConstants(xml:XML = null):SOAPConstants
    {
        var envelopeNS:Namespace;
        var encodingNS:Namespace;

        if (xml != null)
        {
            var nsArray:Array = xml.namespaceDeclarations();
            for each (var ns:Namespace in nsArray)
            {
                if (URLUtil.urisEqual(ns.uri, SOAP_ENVELOPE_URI)
                    || URLUtil.urisEqual(ns.uri, SOAP12_ENVELOPE_URI))
                {
                    envelopeNS = ns;
                }
                else if (URLUtil.urisEqual(ns.uri, SOAP_ENCODING_URI)
                    || URLUtil.urisEqual(ns.uri, SOAP12_ENCODING_URI))
                {
                    encodingNS = ns;
                }
            }
        }

        // Default to SOAP 1.1
        if (envelopeNS == null)
            envelopeNS = new Namespace(SOAP_ENV_PREFIX, SOAP_ENVELOPE_URI);

        if (encodingNS == null)
            encodingNS = new Namespace(SOAP_ENC_PREFIX, SOAP_ENCODING_URI);

        if (constantsCache == null)
            constantsCache = {};

        var constants:SOAPConstants = constantsCache[envelopeNS.uri];
        if (constants == null)
        {
            constants = new SOAPConstants(envelopeNS, encodingNS);
            constantsCache[envelopeNS.uri] = constants;
        }

        return constants;
    }

    public static function isSOAPEncodedType(type:QName):Boolean
    {
        var uri:String = (type != null) ? type.uri : null;
        if (uri != null)
        {
            if (URLUtil.urisEqual(uri, SOAPConstants.SOAP_ENCODING_URI) ||
                URLUtil.urisEqual(uri, SOAPConstants.SOAP12_ENCODING_URI))
            {
                return true;    
            }
        }
        return false;
    }

    /**
     * Looks for an ICustomSOAPType implementation for the given type. 
     * 
     * @return A new instance of the ICustomSOAPType, if registered, otherwise
     * null.
     */
    public static function getCustomSOAPType(type:QName):ICustomSOAPType
    {
        var soapType:ICustomSOAPType;
        if (type != null)
        {
            var key:String = getKey(type);
            var definitionName:String = typeMap[key] as String;

            if (definitionName != null)
            {
                try
                {
                    var c:Class = getDefinitionByName(definitionName) as Class;
                    soapType = new c() as ICustomSOAPType;
                }
                catch(e:Error)
                {
                }
            }
        }
        return soapType;
    }

    /**
     * Maps a type QName to a definition of an ISOAPType implementation.
     * The definition can be a String representation of the fully qualified
     * class name, an Object instance or the Class instance itself.
     */
    public static function registerCustomSOAPType(type:QName, definition:*):void
    {
        var key:String = getKey(type);

        var definitionName:String;

        if (definition is String)
            definitionName = definition as String;
        else
            definitionName = getQualifiedClassName(definition);

        typeMap[key] = definitionName;
    }

    /**
     * Removes the ICustomSOAPType from the registry for the given type.
     */
    public static function unregisterCustomSOAPType(type:QName):void
    {
        if (type != null)
        {
            var key:String = getKey(type);
            delete typeMap[key];
        }
    }

    private static function getKey(type:QName):String
    {
        var key:String;
        if (type.uri == null || type.uri == "")
            key = type.localName;
        else
            key = type.toString();
        return key;
    }

    private static function initCustomSOAPTypes():void
    {
        registerCustomSOAPType(queryBeanQName, QueryBeanType);
        registerCustomSOAPType(mapQName, MapType);
        registerCustomSOAPType(rowSetQName, RowSetType);
        registerCustomSOAPType(documentQName, ApacheDocumentType);
        registerCustomSOAPType(diffgramQName, DataSetType);
        customTypesInitialized = true;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------
    
    // SOAP Envelope QNames
    public var envelopeQName:QName;
    public var headerQName:QName;
    public var bodyQName:QName;
    public var faultQName:QName;
    public var actorQName:QName;
    public var mustUnderstandQName:QName;

    // SOAP Encoding QNames
    public var soapencArrayQName:QName;
    public var soapencArrayTypeQName:QName;
    public var soapencRefQName:QName;
    public var soapoffsetQName:QName;
    public var soapBase64QName:QName;

    private var _contentType:String;
    private var _envelopeNS:Namespace;
    private var _encodingNS:Namespace;

    private static var constantsCache:Object;
    private static var customTypesInitialized:Boolean;
    private static var typeMap:Object = {};

    //--------------------------------------------------------------------------
    //
    // Constants
    // 
    //--------------------------------------------------------------------------

    // ColdFusion SOAP Type QNames
    public static const queryBeanQName:QName = new QName(COLD_FUSION_URI, "QueryBean");

    // Apache SOAP Type QNames
    public static const rowSetQName:QName = new QName(APACHE_SOAP_URI, "RowSet");
    public static const mapQName:QName = new QName(APACHE_SOAP_URI, "Map");
    public static const documentQName:QName = new QName(APACHE_SOAP_URI, "Document");
    
    // MS .NET DataSet Constants
    public static const msdataURI:String = "urn:schemas-microsoft-com:xml-msdata";
    public static const diffgramQName:QName = new QName("urn:schemas-microsoft-com:xml-diffgram-v1", "diffgram");

    // SOAP Envelope Constants
    public static const SOAP_ENVELOPE_URI:String = "http://schemas.xmlsoap.org/soap/envelope/";
    public static const SOAP12_ENVELOPE_URI:String = "http://www.w3.org/2002/12/soap-envelope";
    public static const XML_DECLARATION:String = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

    // SOAP Encoding Constants
    public static const SOAP_ENCODING_URI:String = "http://schemas.xmlsoap.org/soap/encoding/";
    public static const SOAP12_ENCODING_URI:String = "http://www.w3.org/2002/12/soap-encoding";
    public static const SOAP_CONTENT_TYPE:String = "text/xml; charset=utf-8";
    public static const SOAP12_CONTENT_TYPE:String = "application/soap+xml; charset=utf-8";    

    public static const RPC_STYLE:String = "rpc";
    public static const DOC_STYLE:String = "document";
    public static const WRAPPED_STYLE:String = "wrapped";
    public static const USE_ENCODED:String = "encoded";
    public static const USE_LITERAL:String = "literal";
    public static const DEFAULT_OPERATION_STYLE:String = "document";
    public static const DEFAULT_USE:String = "literal";

    // Default namespace prefixes
    public static const SOAP_ENV_PREFIX:String = "SOAP-ENV";
    public static const SOAP_ENC_PREFIX:String = "SOAP-ENC";

    // Custom SOAP Type Constants
    public static const COLD_FUSION_URI:String   = "http://rpc.xml.coldfusion";
    public static const APACHE_SOAP_URI:String   = "http://xml.apache.org/xml-soap";
}

}
