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

import flash.utils.getTimer;
import flash.xml.XMLDocument;
import flash.xml.XMLNode;

import mx.collections.IList;
import mx.core.mx_internal;
import mx.logging.ILogger;
import mx.logging.Log;
import mx.rpc.soap.types.ICustomSOAPType;
import mx.rpc.wsdl.WSDLMessagePart;
import mx.rpc.wsdl.WSDLConstants;
import mx.rpc.wsdl.WSDLEncoding;
import mx.rpc.wsdl.WSDLOperation;
import mx.rpc.xml.ContentProxy;
import mx.rpc.xml.DecodingContext;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.SchemaDatatypes;
import mx.rpc.xml.TypeIterator;
import mx.rpc.xml.XMLDecoder;
import mx.utils.ObjectProxy;
import mx.utils.object_proxy;
import mx.utils.StringUtil;
import mx.utils.XMLUtil;

use namespace object_proxy;

[ExcludeClass]

/**
 * Decodes the SOAP response for a particular operation
 * 
 * @private
 */
public class SOAPDecoder extends XMLDecoder implements ISOAPDecoder
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    public function SOAPDecoder()
    {
        super();
        log = Log.getLogger("mx.rpc.soap.SOAPDecoder");
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    /**
     * Controls whether the decoder supports the legacy literal style encoding
     * for generic compound type (such as arrays). Older document-literal SOAP
     * implementations sometimes encoded unbounded element sequences with
     * generic child <code>item</code> elements instead of repeating the
     * value element itself. The default is true.
     */
    public var supportGenericCompoundTypes:Boolean = false;


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    public function get forcePartArrays():Boolean
    {
        return _forcePartArrays;
    }

    public function set forcePartArrays(value:Boolean):void
    {
        _forcePartArrays = value;
    }

    public function get headerFormat():String
    {
        return _headerFormat;
    }

    public function set headerFormat(value:String):void
    {
        _headerFormat = value;
    }

    /**
     * Determines whether the decoder should ignore whitespace when processing
     * the XML of a SOAP encoded response. The default is <code>true</code>
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
    }

    public function get multiplePartsFormat():String
    {
        return _multiplePartsFormat;
    }
    
    public function set multiplePartsFormat(value:String):void
    {
        _multiplePartsFormat = value;
    }

    public function get resultFormat():String
    {
        return _resultFormat;
    }

    public function set resultFormat(value:String):void
    {
        _resultFormat = value;
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

    /**
     * @private
     */
    protected function get inputEncoding():WSDLEncoding
    {
        var encoding:WSDLEncoding;
        if (_wsdlOperation.inputMessage != null)
            encoding = _wsdlOperation.inputMessage.encoding;
        else
            encoding = new WSDLEncoding();
        return encoding;
    }

    /**
     * @private
     */
    protected function get outputEncoding():WSDLEncoding
    {
        var encoding:WSDLEncoding;
        if (_wsdlOperation.outputMessage != null)
            encoding = _wsdlOperation.outputMessage.encoding;
        else
            encoding = new WSDLEncoding();
        return encoding;
    }

    //--------------------------------------------------------------------------
    //
    // Methods - SOAP Decoding
    // 
    //--------------------------------------------------------------------------

    /**
     * Decodes a SOAP response into a result and headers. 
     */
    public function decodeResponse(response:*):SOAPResult
    {
        var soapResult:SOAPResult;
        var responseString:String;

        if (response is XML)
            responseString = XML(response).toXMLString(); 
        else
            responseString = String(response);

        var startTime:int = getTimer();

        log.info("Decoding SOAP response");

        // Reset the decoder state to clear contexts for schema types, etc.
        reset();

        if (responseString != null)
        {
            log.debug("Encoded SOAP response {0}", responseString);

            // Keep track of the previous ignoreWhitespace setting (as it is
            // unfortunately a static API on the intrinsic XML type) so we
            // can set it back to its original state once we're finished.
            var oldIgnoreWhitespace:Boolean = XML.ignoreWhitespace;
            try
            {
                // Work around Flash Player bug 192355 by removing whitespace
                // between processing instructions and the root tag before
                // constructing an XML instance of the SOAP response.
                responseString = responseString.replace(PI_WHITESPACE_PATTERN, "?><");
                responseString = StringUtil.trim(responseString);
                XML.ignoreWhitespace = ignoreWhitespace;
                var responseXML:XML = new XML(responseString);

                soapResult = decodeEnvelope(responseXML);
            }
            finally
            {
                XML.ignoreWhitespace = oldIgnoreWhitespace;
            }
        }

        log.info("Decoded SOAP response into result [{0} millis]", getTimer() - startTime);
        return soapResult
    }

    protected function decodeEnvelope(responseXML:XML):SOAPResult
    {
        log.debug("Decoding SOAP response envelope");
        var soapResult:SOAPResult = new SOAPResult();

        var envNS:Namespace;

        if (responseXML != null)
        {    
            envNS = responseXML.namespace();
        }

        if (envNS == null)
        {
            throw new Error("SOAP Response cannot be decoded. Raw response: " + responseXML);
        }
        else if (envNS.uri != SOAPConstants.SOAP_ENVELOPE_URI)
        {
            throw new Error("SOAP Response Version Mismatch");
        }
        else
        {
            // Set the namespaces and the Schema uri for the decoder.
            var schemaConst:SchemaConstants;
            var nsArray:Array = responseXML.inScopeNamespaces();
            for each (var ns:Namespace in nsArray)
            {
                schemaManager.namespaces[ns.prefix] = ns;
            }

            // SOAP Headers
            var headerXML:XML = responseXML[soapConstants.headerQName][0];
            if (headerXML != null)
            {
                soapResult.headers = decodeHeaders(headerXML);
            }
            // SOAP Body
            var bodyXML:XML = responseXML[soapConstants.bodyQName][0];
            
            if (bodyXML == null || bodyXML.hasComplexContent() == false || bodyXML.children().length() <= 0)
            {
                soapResult.result = undefined;
            }
            else 
            {
                // Check for SOAP faults for all resultFormats
                var faultXMLList:XMLList = bodyXML[soapConstants.faultQName];
                if (faultXMLList.length() > 0)
                {
                    soapResult.isFault = true;
                    soapResult.result = decodeFaults(faultXMLList);
                }
                else
                {
                    if (resultFormat == "object")
                    {
                        decodeBody(bodyXML, soapResult);
                    }
                    else if (resultFormat == "e4x")
                    {
                         // Return the children as an XMLList.
                        soapResult.result = bodyXML.children();
                    }
                    else if (resultFormat == "xml")
                    {
                         // Return the children as an Array of XMLNode
                         // or String for text children
                        var bodyArray:Array = [];
                        var bodyXMLList:XMLList = bodyXML.children();
                        for each (var bodyChild:XML in bodyXMLList)
                        {
                            var nodeKind:String = bodyChild.nodeKind();
                            if (nodeKind == "element")
                            {
                                var xmlDoc:XMLDocument = new XMLDocument(bodyChild.toString());
                                var xmlNode:XMLNode = xmlDoc.firstChild;
                                bodyArray.push(xmlNode);
                            }
                            else if (nodeKind == "text")
                            {
                                bodyArray.push(bodyChild.toString());
                            }
                        }
                        soapResult.result = bodyArray;
                    }
                }
            }
        }

        return soapResult;
    }

    /**
     * Decodes the response SOAP Body. The contents may either be the encoded
     * output parameters, or a collection of SOAP faults.
     */
    protected function decodeBody(bodyXML:XML, soapResult:SOAPResult):void
    {
        log.debug("Decoding SOAP response body");
        var result:*;
        document = bodyXML;

        // Pre-process encoded body.
        preProcessXML(bodyXML);

        // Check for operations without an output message
        if (wsdlOperation.outputMessage == null)
        {
            soapResult.result = undefined;
            return;
        }

        // Decode WSDL output message parts  
        var parts:Array = wsdlOperation.outputMessage.parts;

        // Check for operations with a void return type
        if (parts == null || parts.length == 0)
        {
            soapResult.result = undefined;
            return;
        }

        var outputMessageXML:XML = bodyXML;
        if (wsdlOperation.style == SOAPConstants.RPC_STYLE)
        {
            // Unwrap the output message from the operation; both RPC encoded and literal wrap.
            outputMessageXML = outputMessageXML.elements()[0];
        }
        else if (outputEncoding.useStyle == SOAPConstants.USE_LITERAL && wsdlOperation.outputMessage.isWrapped == true)
        {
            // Unwrap the output message only if this is wrapped literal.
            outputMessageXML = outputMessageXML.elements()[0];
        }

        // The "literal" use style may define a part using a type or
        // element, the "encoded" use style always defines a part with a
        // type.
        for each (var part:WSDLMessagePart in parts)
        {
            var encodedPartValues:XMLList;
            var encodedPartValue:XML;
            var decodedPart:*;
            var partQName:QName;
            var partType:QName;
            var partDefinition:XML;

            // If we have an element, use that to find the part
            if (part.element != null)
            {
                if (outputMessageXML.hasComplexContent())
                    encodedPartValues = outputMessageXML.elements(part.element);
                else
                    encodedPartValues = outputMessageXML.text();
                partQName = part.element;
                partType = null;
            }
            // Otherwise, find the part by name and decode using the 
            // specified type definition
            else
            {
                partType = part.type;
                partDefinition = part.definition;

                if (outputMessageXML.hasComplexContent())
                {
                    if (outputEncoding.useStyle == SOAPConstants.USE_ENCODED)
                    {
                        // First, look for the part with an unqualified name
                        partQName = new QName("", part.name.localName);
                        encodedPartValues = outputMessageXML.elements(partQName);

                        if (encodedPartValues.length() == 0)
                        {
                            // HACK: Sometimes the soap:body namespace attribute
                            // is used to qualify parts under the operation
                            // wrapper, so we then look with this in mind...
                            var encodedNamespace:String = outputEncoding.namespaceURI;
                            partQName = new QName(encodedNamespace, part.name.localName);
                            encodedPartValues = outputMessageXML.elements(partQName);

                            // HACK: Sometimes the inputEncoding soap:body
                            // namespace attribute is incorrectly used
                            // for the output message parts too...
                            if (encodedPartValues.length() == 0)
                            {
                                encodedNamespace = inputEncoding.namespaceURI;
                                partQName = new QName(encodedNamespace, part.name.localName);
                                encodedPartValues = outputMessageXML.elements(partQName);
                            }
                        }
                    }
                    else
                    {
                        encodedPartValues = outputMessageXML.elements(part.name);
                    }
                }
                else
                {
                    encodedPartValues = outputMessageXML.text();
                }
            }

            for each (encodedPartValue in encodedPartValues)
            {
                decodedPart = decode(encodedPartValue, partQName, partType, partDefinition);

                // Handle multiple output parts separately...
                if (parts.length > 1)
                {
                    // Map multiple parts to named properties on the result object
                    if (multiplePartsFormat == "object")
                    {
                        // Create the result object, if not created already
                        // (this is the first part we have seen so far)
                        if (result == null)
                        {
                            // The QName of the element that contains the part
                            // values is used to look up a registered AS type
                            // for the result object. For RPC operations the
                            // QName will be the full operation name. For Doc/Lit
                            // wrapped the QName will be the same as the
                            // outputMessage.wrappedQName. For Doc/Lit bare
                            // the QName will be soap:Body.
                            result = createContent(outputMessageXML.name());
                            result.isSimple = false;
                        }
                        
                        if (result[part.name.localName] == null)
                        {
                            // We need to create an array for this part's values
                            // if there are > 1 encodedPartValues, or we are
                            // forcingPartArrays for parts defined with maxOccurs>1
                            // regardless of number of values.
                            var partMaxOccurs:uint = getMaxOccurs(partDefinition);
                            if ((partMaxOccurs > 1 && forcePartArrays)
                                    || encodedPartValues.length() > 1)
                            {
                                result[part.name.localName] = createIterableValue(part.type);
                            }
                        }

                        // If we have created an iterable container, this part
                        // value needs to be pushed on it
                        if (TypeIterator.isIterable(result[part.name.localName]))
                            TypeIterator.push(result[part.name.localName], decodedPart);
                        // Otherwise just assign the single value to the named property.
                        else
                            result[part.name.localName] = decodedPart;
                    }
                    else if (multiplePartsFormat == "array")
                    {
                        // If multiplePartsFormat == "array", we return each part
                        // as an element in an Array (or some registered collection)
                        if (result == null)
                        {
                            // If this is the first part/value, we create the
                            // array (or typed collection based on the container
                            // element's QName)
                            result = createIterableValue(outputMessageXML.name());
                        }
                        TypeIterator.push(result, decodedPart);
                    }
                }
                else
                {
                    // Single output part. Create result object if not created.
                    if (result == null)
                    {
                        // The result object with only one part becomes the part
                        // itself. The type of the result object is the type of
                        // the part (if the part specifies a type).
                        var sinlgePartResultType:QName = partType;
                        
                        // If the part specifies an element, the type of the result
                        // object is looked up based on the element QName.
                        if (sinlgePartResultType == null)
                            sinlgePartResultType = part.element;

                        // If neither a type, nor element is specified (rare case
                        // where part becomes anyType), the part.name is used to
                        // look up a strong type for the result object.
                        if (sinlgePartResultType == null)
                            sinlgePartResultType = part.name;

                        var singlePartMaxOccurs:uint = getMaxOccurs(partDefinition);
                        if ((singlePartMaxOccurs > 1 && forcePartArrays)
                                 || encodedPartValues.length() > 1)
                        {
                            // If more than one value was returned for a single
                            // output part, or the part is defined with maxOccurs > 1,
                            // we treat it as an array of values. The appropriate
                            // IList class is created based on the singlePartResultType
                            result = createIterableValue(sinlgePartResultType);
                        }
                        else
                        {
                            // decodedPart will already be an instance of the
                            // required strong type (or ObjectProxy or Object).
                            // If the single part has a single value, the result
                            // object itself becomes that value, so we only need
                            // a content proxy here.
                            result = createContent();
                        }
                    }
                    
                    if (TypeIterator.isIterable(result))
                    {
                        // Push multiple values to the iterable result
                        TypeIterator.push(result, decodedPart);
                    }
                    else
                    {
                        result = decodedPart;
                    }
                }
            }
        }

        // If necessary, unwrap result from its proxy wrapper
        if (result is ContentProxy)
            result = ContentProxy(result).object_proxy::content;

        soapResult.result = result;
    }

    /**
     * Decodes a SOAP 1.1. Fault.
     * 
     * FIXME: We need to add SOAP 1.2 Fault support which is very different
     * from SOAP 1.1.
     */
    protected function decodeFaults(faultsXMLList:XMLList):Array
    {
        log.debug("SOAP: Decoding SOAP response fault");
        var faults:Array = [];

        for each (var faultXML:XML in faultsXMLList)
        {
            var code:QName;
            var string:String;
            var detail:String;
            var element:XML = faultXML;
            var actor:String;

            var faultProperties:XMLList = faultXML.children();
            for each (var child:XML in faultProperties)
            {
                if (child.localName() == "faultcode")
                {   
                    code = schemaManager.getQNameForPrefixedName(child.toString(), child);
                }
                else if (child.localName() == "faultstring")
                {
                    string = child.toString();
                }
                else if (child.localName() == "faultactor")
                {
                    actor = child.toString();
                }
                else if (child.localName() == "detail")
                {       
                    if (child.hasComplexContent())
                    {
                        detail = child.children().toXMLString();    
                    }
                    else
                    {
                        detail = child.toString();
                    }
                }
            }

            var fault:SOAPFault = new SOAPFault(code, string, detail, element, actor);
            faults.push(fault);
        }
        return faults;
    }

    protected function decodeHeaders(headerXML:XML):Array
    {
        log.debug("Decoding SOAP response headers");

        var headers:Array = [];
        var headerXMLList:XMLList = headerXML.elements();
        for each (var headerChild:XML in headerXMLList)
        {
            if (headerFormat == "object")
            {
                var xsiType:QName = getXSIType(headerChild);
                var definition:XML = null;
                var headerContent:Object = null;

                // Check for xsi:type on the header
                if (xsiType != null)
                    definition = schemaManager.getNamedDefinition(xsiType,
                            constants.complexTypeQName, constants.simpleTypeQName);

                // We found a definition for the type.
                if (definition != null)
                {
                    // Release scope, since we were only checking if definition exists.
                    schemaManager.releaseScope();
                    headerContent = decode(headerChild, null, xsiType);
                }
                else
                {
                    // We don't have a type definition. Attempt to find an element
                    // definition for the QName of the header. If there is none,
                    // decode() will fall back to anyType.
                    headerContent = decode(headerChild, headerChild.name());
                }

                // Create the SOAPHeader wrapper.
                var headerObject:SOAPHeader = new SOAPHeader(headerChild.name(), headerContent);

                // decode mustUnderstand attribute
                var muValue:String = XMLUtil.getAttributeByQName(headerChild,
                            soapConstants.mustUnderstandQName).toString();
                if (muValue == "1")
                    headerObject.mustUnderstand = true;

                // decode actor attribute
                var actValue:String = XMLUtil.getAttributeByQName(headerChild,
                            soapConstants.actorQName).toString();
                if (actValue != "")
                    headerObject.role = actValue;

                headers.push(headerObject);
            }
            else if (headerFormat == "e4x")
            {
                headers.push(headerChild);
            }
            else if (headerFormat == "xml")
            {
                headers.push(new XMLDocument(headerChild.toString()));
            }
        }

        return headers;
    }


    //--------------------------------------------------------------------------
    //
    // Methods - XML Decoding
    // 
    //--------------------------------------------------------------------------

    /**
    * @private
    */
    override public function decodeComplexType(definition:XML, parent:*, name:QName, value:*, restriction:XML=null, context:DecodingContext=null):void
    {
        if (value is XML)
        {
            var valXML:XML = value as XML;

            if (valXML.elements(SOAPConstants.diffgramQName).length() > 0
                && valXML.elements(schemaConstants.schemaQName).length() > 0)
            {
                // If we have XML with the elements of a .NET DataSet, we
                // short-circuit to decodeType, which will call the special
                // decode function for the DataSetType
                decodeType(SOAPConstants.diffgramQName, parent, valXML.name(), value);
                return;
            }
        }

        // If the value provided is not XML, or doesn't have the elements
        // of a .NET DataSet, we just call super.
        super.decodeComplexType(definition, parent, name, value, restriction, context);
    }

    /**
     * @private
     */
    override public function decodeType(type:QName, parent:*, name:QName, value:*, restriction:XML = null):void
    {
        // SOAP encoding specifies a type directly on the value and we
        // can usually use it unless it is a type that is not built-in
        // nor has a schema type definition. The value's specific type is
        // retained as originalType in case we need to fall back to using it.
        var originalType:QName = type;
        var xsiType:QName = getXSIType(value);
        if (xsiType != null)
            type = xsiType;

        // HACK: If encoded, translate simple SOAP types to XSD types.
        if (outputEncoding.useStyle == SOAPConstants.USE_ENCODED)
        {
            // FIXME: This should be managed by the schemaManager's unmarshaller
            if (SOAPConstants.isSOAPEncodedType(type))
            {
                var datatypes:SchemaDatatypes = schemaManager.schemaDatatypes;
                if (type == soapConstants.soapBase64QName)
                {
                    type = datatypes.base64BinaryQName;
                }
                else
                {
                    var localName:String = type.localName;
                    if (localName != "Array" && localName != "arrayType")
                    {
                        type = schemaConstants.getQName(localName);
                    }
                }
            }
        }

        // Look for a custom SOAP type to handle the decoding
        var customType:ICustomSOAPType = SOAPConstants.getCustomSOAPType(type);
        if (customType != null)
        {
            customType.decode(this, parent, name, value, restriction);
            setXSIType(parent, type);
        }
        else
        {
            // We didn't do custom SOAP type decoding, so we need to delegate
            // but we need to pass a valid type to the base processing routine.
            var constants:SchemaConstants = schemaManager.schemaConstants;
            if (isBuiltInType(type))
            {
                super.decodeType(type, parent, name, value, restriction);
            }
            else
            {
                var definition:XML = schemaManager.getNamedDefinition(type,
                        constants.complexTypeQName,
                        constants.simpleTypeQName,
                        constants.elementTypeQName);

                if (definition != null)
                {
                    // We're done with this definition; just needed to see if
                    // we had it so we release the scope.
                    schemaManager.releaseScope();
                    super.decodeType(type, parent, name, value, restriction);
                }
                else
                {
                    // We don't have a type def for the value's specific
                    // xsi type, so fall back to the default type passed in.
                    super.decodeType(originalType, parent, name, value, restriction);
                }
            }
        }
    }

    /**
     * This override intercepts dencoding a complexType with complexContent based
     * on a SOAP encoded Array. This awkward approach to Array type definitions
     * was popular in WSDL 1.1 rpc-encoded operations and is a special case that
     * needs to be handled, but note it violates the WS-I Basic Profile 1.0.
     * 
     * @private
     */
    override public function decodeComplexRestriction(restriction:XML, parent:*, name:QName, value:*):void
    {
        // Handle <restriction base="soap-enc:Array"> as a special case
        var schemaConstants:SchemaConstants = schemaManager.schemaConstants;
        var baseName:String = restriction.@base;
        var baseQName:QName = schemaManager.getQNameForPrefixedName(baseName, restriction);
        if (baseQName == soapConstants.soapencArrayQName)
        {
            var customType:ICustomSOAPType = SOAPConstants.getCustomSOAPType(baseQName);
            if (customType != null)
            {
                customType.decode(this, parent, name, value, restriction);
                return;
            }
        }

        super.decodeComplexRestriction(restriction, parent, name, value);
    }
    
    
    override public function reset():void
    {
        super.reset();
        _referencesResolved = false;
        _elementsWithId = null;
    }
    

    /**
     * Overrides XMLDecoder.parseValue to allow us to detect a legacy case
     * of literal style encoding where by generically encoded compound types
     * (such as arrays) had entries encoded with multiple child
     * <code>item</code> elements (instead of matching the correct schema
     * definition of just repeating the value node).
     * 
     * @private
     */
    override protected function parseValue(name:*, value:XMLList):*
    {
        if (supportGenericCompoundTypes 
            && outputEncoding.useStyle == SOAPConstants.USE_LITERAL
            && value.length() > 0)
        {
            // Look for child <item> elements as direct descendents of the value
            var itemQName:QName = new QName(value[0].name().uri, "item");
            var items:XMLList = value.elements(itemQName);
            if (items.length() > 0)
                value = items;
        }

        return super.parseValue(name, value);
    }
    
    
    /**
     * Overrides XMLDecoder.preProcessXML to allow us to handle multi-ref SOAP
     * encoding.
     * @private
     */
    override protected function preProcessXML(root:XML):void
    {
        // Only RPC/encoded uses multi-ref encoding.
        if (outputEncoding.useStyle == SOAPConstants.USE_ENCODED)
            resolveReferences(root);
    }

    /**
     * Resolves multi-refs in rpc/encoded. Substitutes each reference by its
     * referent node.
     */
    private function resolveReferences(root:XML, cleanupElementsWithIdCache:Boolean=true):void
    {
        if (_referencesResolved) return;
        
        var index:uint = 0;
        if (_elementsWithId == null)
            _elementsWithId = document..*.(attribute("id").length() > 0);
    
        // Note that we must consider all child nodes here, not just elements
        // as we need the accurate index in terms of child XML nodes to replace
        // a node with the referent.
        for each (var child:XML in root.children())
        {
            if (child.nodeKind() == "element")
            {
                var element:XML = child;
                var href:String = getAttributeFromNode("href", element);
                if (href != null)
                {
                    var hashPosition:int = href.indexOf("#");
                    if (hashPosition >= 0)
                        href = href.substring(hashPosition + 1);
    
                    // Find the first element with a matching id attribute 
                    var matches:XMLList = _elementsWithId.(@id == href);
                    var referent:XML;
                    
                    if (matches.length() > 0)
                        referent = matches[0];
                    else
                        throw new Error("The element referenced by id '" + href + "' was not found.");
                        
                    referent.setName(element.name());
                    
                    if (referent.hasComplexContent())
                        resolveReferences(referent, false);
                        
                    root.replace(index, referent);
                }
                else if (element.hasComplexContent())
                {
                    resolveReferences(element, false);
                }
            }

            index++;
        }
        
        if (cleanupElementsWithIdCache)
        {
            _elementsWithId = null;
            // At this point all references have been resolved.
            _referencesResolved = true;
        }
    }


    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    
    private var log:ILogger;
    private var _elementsWithId:XMLList;
    private var _forcePartArrays:Boolean;
    private var _headerFormat:String;
    private var _ignoreWhitespace:Boolean = true;
    private var _multiplePartsFormat:String;
    private var _referencesResolved:Boolean; // Used to prevent repeat resolution passes for a single document.
    private var _resultFormat:String;
    private var _wsdlOperation:mx.rpc.wsdl.WSDLOperation;


    /**
     * A RegEx pattern to help replace the whitespace between processing
     * instructions and root tags.
     */
    public static var PI_WHITESPACE_PATTERN:RegExp = new RegExp("[\\?][>]\\s*[<]", "g");
}  

}
