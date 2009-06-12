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

import flash.xml.XMLDocument;
import flash.xml.XMLNode;

import mx.core.mx_internal;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.rpc.soap.types.ICustomSOAPType;
import mx.rpc.wsdl.WSDLConstants;
import mx.rpc.wsdl.WSDLEncoding;
import mx.rpc.wsdl.WSDLOperation;
import mx.rpc.wsdl.WSDLMessage;
import mx.rpc.wsdl.WSDLMessagePart;
import mx.rpc.xml.Schema;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.SchemaDatatypes;
import mx.rpc.xml.SchemaMarshaller;
import mx.rpc.xml.XMLEncoder;

[ResourceBundle("rpc")]

[ExcludeClass]

/**
 * A SOAPEncoder is used to create SOAP 1.1 formatted requests for a web service
 * operation. A WSDLOperation provides the definition of how SOAP request should
 * be formatted and thus must be set before a call is made to encode().
 * 
 * TODO: Create a SOAP 1.2 specific subclass of this encoder.
 * 
 * @private
 */
public class SOAPEncoder extends XMLEncoder implements ISOAPEncoder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    public function SOAPEncoder()
    {
        super();
        log = Log.getLogger("mx.rpc.soap.SOAPEncoder");
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

    /**
     * Determines whether the encoder should ignore whitespace when
     * constructing an XML representation of a SOAP request.
     * The default is <code>true</code> and thus whitespace is not preserved.
     * If an XML Schema type definition specifies a <code>whiteSpace</code>
     * restriction set to <code>preserve</code> then ignoreWhitespace must
     * first be set to false. Conversely, if a type <code>whiteSpace</code>
     * restriction is set to <code>replace</code> or <code>collapse</code> then
     * that setting will be honored even if ignoreWhitespace is set to false.
     */
    public function get ignoreWhitespace():Boolean
    {
        return _ignoreWhitespace;
    }

    public function set ignoreWhitespace(value:Boolean):void
    {
        _ignoreWhitespace = value;
    }

    /**
     * @private
     */
    protected function get inputEncoding():WSDLEncoding
    {
        return _wsdlOperation.inputMessage.encoding;
    }

    public function get schemaConstants():SchemaConstants
    {
        return schemaManager.schemaConstants;
    }

    public function get soapConstants():SOAPConstants
    {
        return wsdlOperation.soapConstants;
    }

    public function get wsdlOperation():WSDLOperation
    {
        return _wsdlOperation;
    }

    public function set wsdlOperation(value:WSDLOperation):void
    {
        _wsdlOperation = value;
        schemaManager = _wsdlOperation.schemaManager;
    }


    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SOAP encodes request to an operation from the given input
     * parameters and headers.
     */
    public function encodeRequest(args:* = null, headers:Array = null):XML
    {
        // FIXME: Some level of validation should occur here
        // to check we're ready to start encoding.

        reset();

        var envelopeXML:XML;

        // Keep track of the previous ignoreWhitespace setting (as it is
        // unfortunately a static API on the intrinsic XML type) so we
        // can set it back to its original state once we're finished.
        var oldIgnoreWhitespace:Boolean = XML.ignoreWhitespace;
        var oldPrettyPrinting:Boolean = XML.prettyPrinting;
        try
        {
            XML.ignoreWhitespace = ignoreWhitespace;
            XML.prettyPrinting = false;
            envelopeXML = encodeEnvelope(args, headers);
        }
        finally
        {
            XML.ignoreWhitespace = oldIgnoreWhitespace;
            XML.prettyPrinting = oldPrettyPrinting;
        }

        return envelopeXML;
    }

    /**
     * A SOAP Envelope element is the root element of a SOAP message. It
     * must specify the SOAP namespace.
     */
    protected function encodeEnvelope(args:*, headers:Array):XML
    {
        log.debug("Encoding SOAP request envelope");

        var envelopeXML:XML = <{soapConstants.envelopeQName.localName}/>;
        envelopeXML.setNamespace(soapConstants.envelopeNamespace);
        envelopeXML.addNamespace(schemaConstants.xsdNamespace);
        envelopeXML.addNamespace(schemaConstants.xsiNamespace);
        
        // Make sure the envelope namespace is registered with the schemaManager
        // so that its prefix can be looked up when encoding header attributes.
        schemaManager.namespaces[soapConstants.envelopeNamespace.prefix] =
                soapConstants.envelopeNamespace;
        
        encodeHeaders(headers, envelopeXML);
        encodeBody(args, envelopeXML);
        return envelopeXML;
    }

    /**
     * Appends SOAP Header to the SOAP Envelope
     */
    protected function encodeHeaders(headers:Array, envelopeXML:XML):void
    {
        if (headers != null)
        {
            var count:uint = headers.length;
            if (count > 0)
            {
                var headersXML:XML = <{soapConstants.headerQName.localName}/> 
                headersXML.setNamespace(soapConstants.envelopeNamespace);
                envelopeXML.appendChild(headersXML);

                for (var i:uint = 0; i < count; i++)
                {
                    encodeHeaderElement(headers[i], headersXML);
                }
            }
        }
    }

    /**
     * Appends a header element to top SOAP Header tag
     */ 
    protected function encodeHeaderElement(header:Object, headersXML:XML):void
    {
        var preEncodedNode:* = preEncodedCheck(header);
        if (preEncodedNode != null)
        {
            headersXML.appendChild(header);
        }
        else
        {
            var headerElement:XMLList = new XMLList();

            // header value might contain a mapping 
            if (header.content != null && header.content.hasOwnProperty(header.qname.localName))
            {
                header.content = header.content[header.qname.localName];
            }

            // check the content of the header for pre-encoded values
            preEncodedNode = preEncodedCheck(header.content);
            if (preEncodedNode != null)
            {
                // We can't set namespaces or attributes on XMLList, so we
                // just append it and return...
                if (preEncodedNode is XMLList)
                {
                    headerElement = preEncodedNode as XMLList;
                }
                else
                {
                    headerElement = new XMLList(preEncodedNode);
                }
            }
            else
            {
                // If not pre-encoded, we encode the header value. If there is a
                // definition in the schema for this header QName, we will use it,
                // otherwise encode with anyType. This is handled by XMLEncoder.
                headerElement = encode(header.content, header.qname);
            }

            
            // Typically headerElement will be an XMLList of length 1, but we
            // loop through it anyway, in case we were given a pre-encoded XMLList,
            // or an array value for the header.content
            for each (var headerElementNode:XML in headerElement)
            {

                // add namespace to the header, if not already set by encoding.
                if (header.qname.uri != null && header.qname.uri.length > 0
                    && headerElementNode.namespace().uri != header.qname.uri)
                {
                    var prefix:String = schemaManager.getOrCreatePrefix(header.qname.uri);
                    var ns:Namespace = new Namespace(prefix, header.qname.uri);
                    headerElementNode.setNamespace(ns);
                }
    
                // add header attributes
                var attrStr:String;
                if (header.mustUnderstand)
                {
                    attrStr = schemaManager.getOrCreatePrefix(soapConstants.mustUnderstandQName.uri);
                    headerElementNode.@[attrStr + ":" + soapConstants.mustUnderstandQName.localName] = "1"; // Use "1" form for WS-I compatibility
                }
    
                if (header.role != null)
                {
                    attrStr = schemaManager.getOrCreatePrefix(soapConstants.actorQName.uri);
                    headerElementNode.@[attrStr + ":" + soapConstants.actorQName.localName] = header.role;
                }
            }

            // finally, add header list to SOAP Header node
            headersXML.appendChild(headerElement);
        }
    }

    /**
     * Encodes the SOAP Body. Currently assumes only one operation sub-element.
     */
    protected function encodeBody(inputParams:*, envelopeXML:XML):void
    {
        log.debug("Encoding SOAP request body");

        // Create SOAP Body element
        var bodyXML:XML = <{soapConstants.bodyQName.localName}/>; 
        bodyXML.setNamespace(soapConstants.envelopeNamespace);

        // FIXME: Should we continue to support pre-encoded XML fragments here?
        // How would we tell the difference between an XML type input and
        // a pre-encoded message?

        // Special case handling for input params that are pre-encoded SOAP
        // body contents.
        var preEncoded:Object = preEncodedCheck(inputParams);
        if (preEncoded != null)
        {
            bodyXML.appendChild(preEncoded);
        }
        else
        {
            // Encode the operation and append it to the body.
            if (wsdlOperation.style == SOAPConstants.DOC_STYLE)
            {
                if (inputEncoding.useStyle == SOAPConstants.USE_LITERAL)
                {
                    encodeOperationAsDocumentLiteral(inputParams, bodyXML);
                }
                else
                {
                    //Note: document-encoded not support by WSDL 1.1
                    throw new Error("WSDL 1.1 supports operations with binding style 'document' only if use style is 'literal'.");
                }
            }
            else if (wsdlOperation.style == SOAPConstants.RPC_STYLE)
            {
               if (inputEncoding.useStyle == SOAPConstants.USE_LITERAL)
               {    
                    encodeOperationAsRPCLiteral(inputParams, bodyXML);
               }
               else if (inputEncoding.useStyle == SOAPConstants.USE_ENCODED)
               {
                   encodeOperationAsRPCEncoded(inputParams, bodyXML);
               }
               else
               {
                   throw new Error("WSDL 1.1 does not support operations with binding style 'rpc' and use style " + inputEncoding.useStyle + ".");
               }
            }
            else
            {
                throw new Error("Unrecognized binding style '" + wsdlOperation.style + "'. Only 'document' and 'rpc' styles are supported.");
            }
        }

        // Add the fully encoded body to the envelope.
        envelopeXML.appendChild(bodyXML);
    }
    
    /**
     * Encodes a WSDL operation using document literal format.
     * There's no need to generate an operation element so advance directly
     * to encoding the message.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;If <code>use</code> is <b>literal</b>, then each part references
     * a concrete schema definition using either the <code>element</code> or
     * <code>type</code> attribute. In the first case, the element referenced
     * by the part will appear directly under the Body element (for document
     * style bindings)... In the second, the type referenced by the part
     * becomes the schema type of the enclosing element (Body for document
     * style...).&quot;
     * </p>
     */
    protected function encodeOperationAsDocumentLiteral(inputParams:Object, bodyXML:XML):void
    {
        var parts:Array = wsdlOperation.inputMessage.parts;
        if (wsdlOperation.inputMessage.isWrapped)
        {
            // Wrapped-style uses a single part element as the outer wrapper.
            var wrappedQName:QName = wsdlOperation.inputMessage.wrappedQName;
            var operationXML:XML = <{wrappedQName.localName}/>;
            if (wrappedQName.uri != null && wrappedQName.uri != "")
            {
                var prefix:String = schemaManager.getOrCreatePrefix(wrappedQName.uri);
                var inputNamespace:Namespace = new Namespace(prefix, wrappedQName.uri);
                operationXML.setNamespace(inputNamespace);
            }

            encodeMessage(inputParams, operationXML);
            bodyXML.appendChild(operationXML);
        }
        else
        {
            // Non-wrapped document-literal style directly encodes the parts
            // as children of the SOAP Body.
            encodeMessage(inputParams, bodyXML);
        }
    }
    
    /**
     * Encodes a WSDL operation using RPC literal format.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;If the operation style is <code>rpc</code> each part is a parameter
     * or a return value and appears inside a wrapper element within the body
     * (following Section 7.1 of the SOAP specification). The wrapper element
     * is named identically to the operation name and its namespace is the
     * value of the namespace attribute. Each message part (parameter) appears
     * under the wrapper, represented by an accessor named identically to the
     * corresponding parameter of the call. Parts are arranged in the same
     * order as the parameters of the call.&quot;
     * </p>
     * <p>
     * &quot;If <code>use</code> is <b>literal</b>, then each part references
     * a concrete schema definition using either the <code>element</code> or
     * <code>type</code> attribute. In the first case, the element referenced
     * by the part will appear ... under an accessor element named after the
     * message part (in rpc style). In the second, the type referenced by the
     * part becomes the schema type of the enclosing element ( ... part accessor
     * element for rpc style).&quot;
     * </p>
     */
    protected function encodeOperationAsRPCLiteral(inputParams:Object, bodyXML:XML):void
    {
        var operationXML:XML = <{wsdlOperation.name}/>;
        var prefix:String = schemaManager.getOrCreatePrefix(inputEncoding.namespaceURI);
        var ns:Namespace = new Namespace(prefix, inputEncoding.namespaceURI);
        operationXML.setNamespace(ns);

        encodeMessage(inputParams, operationXML);
        
        bodyXML.appendChild(operationXML);
    }
    
    /**
     * Encodes a WSDL message part using RPC encoded format.
     * <p>
     * From the WSDL 1.1 specification:
     * </p>
     * <p>
     * &quot;If the operation style is <code>rpc</code> each part is a parameter
     * or a return value and appears inside a wrapper element within the body
     * (following Section 7.1 of the SOAP specification). The wrapper element
     * is named identically to the operation name and its namespace is the
     * value of the namespace attribute. Each message part (parameter) appears
     * under the wrapper, represented by an accessor named identically to the
     * corresponding parameter of the call. Parts are arranged in the same
     * order as the parameters of the call.&quot;
     * </p>
     * <p>
     * &quot;If <code>use</code> is <b>encoded</b>, then each message part
     * references an abstract type using the <code>type</code> attribute. These
     * abstract types are used to produce a concrete message by applying an
     * encoding specified by the <code>encodingStyle</code> attribute. The part
     * names, types and value of the namespace attribute are all inputs to the
     * encoding, although the namespace attribute only applies to content not
     * explicitly defined by the abstract types. If the referenced encoding
     * style allows variations in it's format (such as the SOAP encoding does),
     * then all variations MUST be supported ("reader makes right").&quot;
     * </p>
     */
    protected function encodeOperationAsRPCEncoded(inputParams:*, bodyXML:XML):void
    {
        isSOAPEncoding = true;        
        var operationXML:XML = <{wsdlOperation.name}/>;
        var inputNamespaceURI:String = inputEncoding.namespaceURI;
        var inputPrefix:String = schemaManager.getOrCreatePrefix(inputNamespaceURI);
        var inputNamespace:Namespace = new Namespace(inputPrefix, inputNamespaceURI);
        operationXML.setNamespace(inputNamespace);

        encodeMessage(inputParams, operationXML); 

        bodyXML.appendChild(operationXML);
        bodyXML.@[SOAPConstants.SOAP_ENV_PREFIX + ":encodingStyle"] = soapConstants.encodingURI;        
    }    

    /**
     * Encodes an input message for a WSDL operation. The provided input
     * parameters are validated against the required message parts.
     */
    protected function encodeMessage(inputParams:*, operationXML:XML):void
    {
        var parts:Array;
        if (wsdlOperation.inputMessage != null)
            parts = wsdlOperation.inputMessage.parts;

        if (parts == null)
            return;

        // Keep track of part names for validation
        var partNames:Object = {};

        // Keep track of any optional parts that were not provided
        var optionalOmitted:int = 0;

		var message:String;

        // Match the input parameters to WSDL message parts
        for (var i:uint = 0; i < parts.length; i++)
        {
            var part:WSDLMessagePart = parts[i];
            var value:* = undefined;

            // Ordered Parameters
            if (inputParams is Array)
            {
                value = inputParams[i];
                if (value === undefined)
                {
                    if (part.optional)
					{
                        optionalOmitted++;
					}
                    else
					{
						message = resourceManager.getString(
							"rpc", "missingInputParameter", [ i ]);
                        throw new Error(message);
					}
                }
            }
            // Named Parameters
            else
            {
                var name:String = part.name.localName;
                if (inputParams != null)
                    value = inputParams[name];
                partNames[name] = value;

                if (value === undefined || (inputParams != null && !inputParams.hasOwnProperty(name)))
                {
                    if (part.optional)
					{
                        optionalOmitted++;
					}
                    else
					{
						message = resourceManager.getString(
							"rpc", "missingInputParameterWithName", [ name ])
                        throw new Error(message);
					}
                }
            }

            if (value !== undefined)
            {
                // Finally, encode the value for the WSDL message part
                var partXMLList:XMLList = encodePartValue(part, value);
                operationXML.appendChild(partXMLList);
            }
        }

        // Check for any unexpected parameters
        if (inputParams != null)
        {
            if (inputParams is Array)
            {
                if (inputParams.length < (parts.length - optionalOmitted))
                {
					message = resourceManager.getString(
						"rpc", "tooFewInputParameters",
						[ parts.length, inputParams.length ]);
                    throw new Error(message);
                }
            }
            else
            {
                for (var inName:String in inputParams)
                {
                    if (!partNames.hasOwnProperty(inName))
                    {
						message = resourceManager.getString(
							"rpc", "unexpectedInputParameter", [ inName ]);
                        throw new Error(message);
                    }
                }
            }
        }
    }

    /**
     * A WSDL message part may either refer to an XML Schema type (that is, a
     * &lt;complexType&gt; or &lt;simpleType&gt;) directly by QName or to an element
     * definition by QName depending on the SOAP use and encodingStyle
     * attributes.
     */
    protected function encodePartValue(part:WSDLMessagePart, value:*):XMLList
    {
        var partXMLList:XMLList;

        if (part.element != null)
        {
            partXMLList = encode(value, part.element);
        }
        else
        {
            partXMLList = encode(value, part.name, part.type, part.definition);
        }

        return partXMLList;
    }

    /**
     * Looks to see whether a pre-encoded SOAP request has been passed to the
     * encoder.
     */
    protected function preEncodedCheck(value:*):Object
    {
        var preEncodedNode:Object = null;
        if (value != null)
        {
            if (value is XMLList)
            {
                preEncodedNode = value as XMLList;
            }
            else if (value is XML)
            {
                preEncodedNode = value as XML;
            }
            else if (value is XMLDocument)
            {
                var xmlDocument:XMLDocument = value as XMLDocument;
                preEncodedNode = new XML(xmlDocument.firstChild.toString());
            }
            else if (value is XMLNode)
            {
                var xmlNode:XMLNode = value as XMLNode;
                preEncodedNode = new XML(xmlNode.toString());
            }
        }
        return preEncodedNode;
    }


    //--------------------------------------------------------------------------
    //
    // Custom XML Encoding Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * SOAP specific override to handle special wrapped-style document-literal 
     * parameters which can specify minOccurs and maxOccurs attributes on
     * local element definitions that are, for all intents and purposes,
     * really top-level element definitions representing operation parts.
     * XML Schema does not normally allow minOccurs or maxOccurs on top-level
     * element definitions and the SchemaProcessor is not looking out for them
     * so we have to special case this situation here.
     */
    override public function encode(value:*, name:QName = null, type:QName = null, definition:XML = null):XMLList
    {
        if (definition != null)
        {
            var result:XMLList = new XMLList();

            // <element minOccurs="..." maxOccurs="..."> occur on a local element
            // that may act like a top-level element for wrapped-style document
            // literal.
            var maxOccurs:uint = getMaxOccurs(definition);
            var minOccurs:uint = getMinOccurs(definition);

            // If the maximum occurence should 0 this element must not be present.
            if (maxOccurs == 0)
                return result;

            // If minOccurs == 0 the element is optional so we can omit it if
            // a value was not provided.
            if (value == null && minOccurs == 0)
                return result;

            if (maxOccurs > 1)
            {
                // Create a temporary XML parent to hold the XMLList of values
                // during encoding ...
                var content:XMLList = new XMLList();
                // To make sure we will use the correct namespace if there are more
                // than one top level schemas, we push the relevant schema as a new scope.
                var foundScope:Boolean = schemaManager.pushNamespaceInScope(name.uri);
                
                encodeGroupElement(definition, content, name, value);
                
                if (foundScope)
                    schemaManager.releaseScope();

                result += content;
                return result;
            }
        }

        return super.encode(value, name, type, definition);
    }

    /**
     * SOAP specific override to intercept SOAP encoded types such as base64.
     * Also, SOAP encoding requires an XSI type attribute to be specified on
     * encoded types.
     * 
     * @private
     */
    override public function encodeType(type:QName, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var datatypes:SchemaDatatypes = schemaManager.schemaDatatypes;

        // Allow instance level overrides for the type (which can also be used
        // to specify the XSI type too).
        var xsiType:QName = getXSIType(value);
        if (xsiType != null)
            type = xsiType;

        // We can encode many SOAP types as their XML Schema equivalents so
        // long as we set the XSI type correctly for SOAP encoded operations
        if (isSOAPEncoding)
        {
            // Keep track of the real type for setting the SOAP encoding
            // xsi:type as we may need to use a different type internally for
            // XML Schema encoding...
            xsiType = type;

            if (SOAPConstants.isSOAPEncodedType(type))
            {
                // We treat SOAP encoded base64 as the XML Schema base64Binary
                if (type == soapConstants.soapBase64QName)
                {
                    type = datatypes.base64BinaryQName;
                }
                else
                {
                    // HACK: Translate simple SOAP encoded types to XSD types
                    // for the purposes of schema type marshalling
                    var localName:String = type.localName;
                    if (localName != "Array" && localName != "arrayType")
                    {
                        type = schemaConstants.getQName(localName);
                    }
                }
            }
        }

        // Look for custom handlers to override processing of SOAP types.
        var customType:ICustomSOAPType = SOAPConstants.getCustomSOAPType(type);
        if (customType != null)
        {
            customType.encode(this, parent, name, value, restriction);
        }
        else
        {
            super.encodeType(type, parent, name, value, restriction);
        }

        // Finally, we may need to set an xsi:type attribute on the parent...
        if (xsiType != null)
        {
//            var parentNode:XML = parent as XML;

            // ... but we don't override if an existing xsi:type was determined
            // up stream (ie.. for anyType and anySimpleType).
            var xsiTypeAttr:String = parent.@[schemaConstants.getXSIToken(schemaConstants.typeAttrQName)]
            if (xsiTypeAttr == null || xsiTypeAttr == "")
            {
                // FIXME: we shouldn't need super anymore since deriveXSIType is separate
                super.setXSIType(parent, xsiType);
            }
        }
    }

    /**
     * This override intercepts encoding a complexType with complexContent based
     * on a SOAP encoded Array. This awkward approach to Array type definitions
     * was popular in WSDL 1.1 rpc-encoded operations and is a special case that
     * needs to be handled, but note it violates the WS-I Basic Profile 1.0.
     * 
     * @private
     */
    override public function encodeComplexRestriction(restriction:XML, parent:XML, name:QName, value:*):void
    {
        // Handle for <restriction base="soap-enc:Array"> as a special case
        var schemaConstants:SchemaConstants = schemaManager.schemaConstants;
        var baseName:String = restriction.@base;
        var baseQName:QName = schemaManager.getQNameForPrefixedName(baseName, restriction);
        if (baseQName == soapConstants.soapencArrayQName)
        {
            var customType:ICustomSOAPType = SOAPConstants.getCustomSOAPType(baseQName);
            if (customType != null)
            {
                customType.encode(this, parent, name, value, restriction);
                return;
            }
        }

        super.encodeComplexRestriction(restriction, parent, name, value);
    }

    /**
     * This override tries to determine the XSI type for the encoded value if
     * SOAP use style is set to <code>encoded</code>.
     * 
     * @private
     */
    protected override function deriveXSIType(parent:XML, type:QName, value:*):void
    {

        // Add XSI type if the SOAP message has use style as "encoded"
        if (isSOAPEncoding)
        {
            var datatypes:SchemaDatatypes = schemaManager.schemaDatatypes;
            var soapType:QName;

            // HACK: For anyType and anySimpleType, try to guess the schema
            // type for simple values (note we can't guess for complex values).
            if (type == datatypes.anyTypeQName || type == datatypes.anySimpleTypeQName)
            {
                if (isSimpleValue(value) || type == datatypes.anySimpleTypeQName)
                {
                    var localName:String = SchemaMarshaller.guessSimpleType(value);
                    soapType = new QName(schemaConstants.xsdURI, localName);
                }
            }
            else
            {
                soapType = type;
            }
            
            if (soapType != null)
            {
                // FIXME: should be OK to use setXSIType
//                setXSIType(parent, soapType);
                parent.@[schemaConstants.getXSIToken(schemaConstants.typeAttrQName)] = schemaConstants.getXSDToken(soapType);
            }
        }
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    private var _ignoreWhitespace:Boolean = true;
    private var isSOAPEncoding:Boolean = false;
    private var log:ILogger;
    private var _wsdlOperation:WSDLOperation;
}

}
