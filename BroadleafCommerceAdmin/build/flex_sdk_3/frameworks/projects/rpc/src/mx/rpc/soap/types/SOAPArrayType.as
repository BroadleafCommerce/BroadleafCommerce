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

package mx.rpc.soap.types
{

import mx.collections.IList;
import mx.rpc.soap.SOAPConstants;
import mx.rpc.soap.SOAPEncoder;
import mx.rpc.soap.SOAPDecoder;
import mx.rpc.wsdl.WSDLConstants;
import mx.rpc.xml.ContentProxy;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.SchemaDatatypes;
import mx.rpc.xml.SchemaManager;
import mx.rpc.xml.SchemaMarshaller;
import mx.rpc.xml.SchemaProcessor;
import mx.rpc.xml.TypeIterator;
import mx.rpc.xml.XMLEncoder;
import mx.rpc.xml.XMLDecoder;
import mx.utils.object_proxy;
import mx.utils.StringUtil;
import mx.utils.ObjectUtil;

use namespace object_proxy;

[ExcludeClass]

/**
 * Marshalls SOAP 1.1 encoded Arrays between XML and ActionScript.
 * @private
 */
public class SOAPArrayType implements ICustomSOAPType
{
    public function SOAPArrayType()
    {
        super();
        itemName = new QName("", "item");                            
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Encode an ActionScript Array as a SOAP encoded Array in XML.
     * 
     * TODO: Support soap array offset and item position attributes?
     */
    public function encode(encoder:SOAPEncoder, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        processor = encoder;
        soapConstants = encoder.soapConstants;
        schemaManager = encoder.schemaManager;
        schemaConstants = schemaManager.schemaConstants;

        // HACK: Handle the MXML artifact where by <mx:request> based syntax
        // for SOAP parameters can lead to Arrays of properties being wrapped
        // in an Object with a single property with the Array as its value.
        value = unwrapMXMLArray(value);

        // FIXME: Should we look in the restriction definition to see
        // if a sequence was provided to define the element name? The
        // SOAPArrayEncoder will just assume "item" for now...

        if (restriction != null)
        {
            // Look for arrayType under <restriction base="soap-enc:Array">
            var wsdlArrayType:String = determineWSDLArrayType(restriction, encoder.wsdlOperation.wsdlConstants);

            // Parse the arrayType looking for XML Schema type, dimensions and size info
            parseWSDLArrayType(wsdlArrayType);
        }

        schemaType = schemaManager.getQNameForPrefixedName(schemaTypeName, restriction);

        // Ensure SOAP encoding namespace has been declared
        // e.g. <myArray xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/">
        parent.addNamespace(soapConstants.encodingNamespace);

        // Add SOAP encoding arrayType attribute for parent node
        // e.g. <myArray soapenc:arrayType="xsd:int[][3]">
        encodeDimensionInformation(parent, dimensionString);
        encodeArray(parent, dimensions, value);
    }

    /**
     * Decodes a SOAP encoded array assuming the XML Schema type definiton
     * uses a complexType restriction base to declare the array type, e.g.
     * 
     * <pre>
     * &lt;xsd:complexType name="Example"&gt;
     *   &lt;xsd:complexContent mixed="false"&gt;
     *     &lt;xsd:restriction base="soapenc:Array"&gt;
     *       &lt;xsd:attribute wsdl:arrayType="tns:Example[]" ref="soapenc:arrayType" 
     *           xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" /&gt;
     *      &lt;/xsd:restriction&gt;
     *   &lt;/xsd:complexContent&gt;
     * &lt;/xsd:complexType&gt;
     * </pre>
     */
    public function decode(decoder:SOAPDecoder, parent:*, name:*, value:*, restriction:XML = null):void
    {
        processor = decoder;
        soapConstants = decoder.soapConstants;
        schemaManager = decoder.schemaManager;
        schemaConstants = schemaManager.schemaConstants;

        // Handle short-hand syntax for SOAP encoded arrayType
        var valueXML:XML = value as XML;
        if (valueXML != null)
        {
            var arrayTypeString:String = valueXML.@[soapConstants.soapencArrayTypeQName];
            if (arrayTypeString != null && arrayTypeString != "")
            {
                parseWSDLArrayType(arrayTypeString);
                schemaType = schemaManager.getQNameForPrefixedName(schemaTypeName, valueXML);
            }
        }

        // Otherwise look for arrayType under <restriction base="soap-enc:Array">
        if (schemaType == null && restriction != null)
        {
            var wsdlArrayType:String = determineWSDLArrayType(restriction, decoder.wsdlOperation.wsdlConstants);
            parseWSDLArrayType(wsdlArrayType);
            schemaType = schemaManager.getQNameForPrefixedName(schemaTypeName, restriction);
        }

        if (schemaType != null)
        {
            // Change the parent to an Array. Parent is originally created with
            // createContent in the XMLDecoder, before decodeType is called. We
            // need to make sure that the created parent can be used as an array.

            if (parent is ContentProxy)
            {
                var proxy:ContentProxy = parent as ContentProxy;
                proxy.object_proxy::isSimple = false;
                // Only replace parent with iterable value if not already an IList
                // or an Array.
                if (!(proxy.object_proxy::content is IList) && !(proxy.object_proxy::content is Array))
                    proxy.object_proxy::content = decoder.createIterableValue();
            }

            decodeArray(parent, dimensions, value, decoder.makeObjectsBindable);
        }
    }

    /**
     * Recursively called to encode a set of dimensions at a particular level
     * (potentially many of a jagged/nested array) of an Array.
     */
    private function encodeArray(parent:XML, dimensions:Array, value:*):void
    {
        // Add XSI attribute for SOAP encoded Array
        // e.g. <myArray xsi:type="soapenc:Array" />
        var typeAttr:String = schemaConstants.getXSIToken(schemaConstants.typeAttrQName)
        parent.@[typeAttr] = soapConstants.getSOAPEncodingToken(soapConstants.soapencArrayQName);

        if (!TypeIterator.isIterable(value))
            value = [value];

        var iter:TypeIterator = new TypeIterator(value);
        var d:uint = 0;
        
        if (dimensions.length > 0)
        {
            // For a non-jagged Array we have a single entry that describes the
            // length of each dimension (which can be unbounded), otherwise
            // we have multiple entries, but the first is used to describe
            // the number of jagged arrays at this level...
            var entry:* = dimensions[0];
            if (!(entry is Array))
                entry = [entry];

            for each (var dimensionSize:int in entry)
            {
                var itemValue:*;
                var nestedDimension:Array;
                var nestedDimensionString:String;
                var nestedArray:XML;
                d++;
                
                if (dimensionSize < 0)
                {
                    // Unbounded dimension
                    if (dimensions.length == 1)
                    {
                        while (iter.hasNext())
                        {
                            itemValue = iter.next();
                            nestedArray = <{itemName.localName}/>;
                            encodeArrayItem(nestedArray, itemValue);
                            parent.appendChild(nestedArray);
                        }
                    }
                    else if (dimensions.length > 1)
                    {
                        nestedDimensionString = "[]";

                        // FIXME: We should detect before iterating whether 
                        // sufficient definitions exist for the number of
                        // jagged arrays provided in the value...
                        while (iter.hasNext())
                        {
                            itemValue = iter.next();
                            nestedDimension = dimensions[d];
                            nestedArray = <{itemName.localName}/>;
                            encodeDimensionInformation(nestedArray, nestedDimensionString);
                            encodeArray(nestedArray, nestedDimension, itemValue);
                            parent.appendChild(nestedArray);
                        }
                    }
                }
                else
                {
                    // Bounded dimension
                    for (var i:uint = 0; i < dimensionSize; i++)
                    {
                        itemValue = TypeIterator.getItemAt(iter.value, i);
                        if (dimensions.length == 1)
                        {
                            nestedArray = <{itemName.localName}/>;
                            encodeArrayItem(nestedArray, itemValue);
                            parent.appendChild(nestedArray);
                        }
                        else if (dimensions.length > 1)
                        {
                            nestedDimensionString = "[" + dimensionSize + "]";

                            // FIXME: We should detect invalid no. of nested
                            // dimensions compared to value...
                            nestedDimension = dimensions[d];
                            nestedArray = <{itemName.localName}/>;
                            encodeDimensionInformation(nestedArray, nestedDimensionString);
                            encodeArray(nestedArray, nestedDimension, itemValue);
                            parent.appendChild(nestedArray);
                        }
                    }
                }
            }
        }
    }

    private function decodeArray(parent:*, dimensions:Array, value:*, makeObjectsBindable:Boolean):void
    {
        // If we have a single node, grab the XMLList of child elements as 
        // the actual Array members...
        if (value is XML)
        {
            var dataXML:XML = value as XML;
            value = dataXML.elements();
        }

        if (!TypeIterator.isIterable(value) && value != "")
            value = [value];

        var iter:TypeIterator = new TypeIterator(value);
        var d:uint = 0;

        if (dimensions.length > 0)
        {
            // For a non-jagged Array we have a single entry that describes the
            // length of each dimension (which can be unbounded), otherwise
            // we have multiple entries, but the first is used to describe
            // the number of jagged arrays at this level...
            var entry:* = dimensions[0];
            if (!(entry is Array))
                entry = [entry];

            for each (var dimensionSize:int in entry)
            {
                var itemValue:*;
                var nestedDimension:Array;
                var nestedArray:*;
                d++;

                if (dimensionSize < 0)
                {
                    // Unbounded dimension
                    if (dimensions.length == 1)
                    {
                        while (iter.hasNext())
                        {
                            itemValue = iter.next();
                            decodeArrayItem(parent, itemValue);
                        }
                    }
                    else if (dimensions.length > 1)
                    {
                        // FIXME: We should detect before iterating whether 
                        // sufficient definitions exist for the number of
                        // jagged arrays provided in the value...
                        while (iter.hasNext())
                        {
                            itemValue = iter.next();
                            nestedDimension = dimensions[d];
                            nestedArray = SOAPDecoder(processor).createIterableValue(schemaType);
                            decodeArray(nestedArray, nestedDimension, itemValue, makeObjectsBindable);
                            TypeIterator.push(parent, nestedArray);
                        }
                    }
                }
                else
                {
                    // Bounded dimension
                    for (var i:uint = 0; i < dimensionSize; i++)
                    {
                        itemValue = TypeIterator.getItemAt(iter.value, i);
                        if (dimensions.length == 1)
                        {
                            decodeArrayItem(parent, itemValue);
                        }
                        else if (dimensions.length > 1)
                        {
                            // FIXME: We should detect invalid no. of nested
                            // dimensions compared to value...
                            nestedDimension = dimensions[d];
                            nestedArray = SOAPDecoder(processor).createIterableValue(schemaType);
                            decodeArray(nestedArray, nestedDimension, itemValue, makeObjectsBindable);
                            TypeIterator.push(parent, nestedArray);
                        }
                    }
                }
            }
        }
    }
    
    private function encodeArrayItem(item:XML, value:*):void
    {
        var encoder:SOAPEncoder = processor as SOAPEncoder;
        encoder.encodeType(schemaType, item, itemName, value);
    }
    
    private function  decodeArrayItem(parent:*, value:*):void
    {
        var decoder:SOAPDecoder = processor as SOAPDecoder;
        var item:* = decoder.createContent(schemaType);
        decoder.decodeType(schemaType, item, itemName, value);
        decoder.setValue(parent, itemName, item, schemaType);
    }

    private function encodeDimensionInformation(parent:XML, dimensionString:String):void
    {
        var uri:String = schemaType.uri;
        var prefix:String = schemaManager.getOrCreatePrefix(uri);
        var ns:Namespace = new Namespace(prefix, uri);
        var arrayTypeString:String = prefix + ":" + schemaType.localName;
        arrayTypeString = arrayTypeString + dimensionString;
        parent.addNamespace(ns);
        var arrayTypeAttr:String = soapConstants.getSOAPEncodingToken(soapConstants.soapencArrayTypeQName);
        parent.@[arrayTypeAttr] = arrayTypeString;
    }

    private function getSingleElementFromNode(node:XML, ...types:Array):XML
    {
        var elements:XMLList = node.elements();
        for each (var element:XML in elements)
        {
            if (types != null && types.length > 0)
            {
                for each (var type:QName in types)
                {
                    if (element.name() == type)
                    {
                        return element;
                    }
                }
            }
            else
            {
                return element;
            }
        }
        return null;
    }

    private function determineWSDLArrayType(restriction:XML, wsdlConstants:WSDLConstants):String
    {
        var arrayTypeString:String = "";

        // We expect a <attribute ref="soap-enc:ArrayType"> in the restriction
        var attribute:XML = getSingleElementFromNode(restriction, schemaConstants.attributeQName);
        if (attribute != null)
        {
            // The attribute must declare that it is using the SOAP encoded Array
            // type using a ref="soapenc:Array" attribute...
            var soapencArrayTypeRef:String = attribute.@ref;
            var soapencArrayType:QName = schemaManager.getQNameForPrefixedName(soapencArrayTypeRef, attribute, true);
            if (soapencArrayType == soapConstants.soapencArrayTypeQName)
            {
                // Next, we look for wsdl:arrayType attribute
                arrayTypeString = attribute.attribute(wsdlConstants.wsdlArrayTypeQName).toString();
            }
        }

        return arrayTypeString;
    }
        

    /**
     * Parses the WSDL arrayType to determine the type of the members in a SOAP
     * encoded array, the rank and dimensions of the Array, and potentially
     * the size of the Array (if not unbounded).
     * 
     * TODO: Support SOAP 1.2 syntax for Arrays.
     * 
     * Examples:
     * 1. An unbounded Array of strings:
     * <xsd:attribute ref="soap-enc:arrayType" wsdl:arrayType="xsd:string[]" />
     * 
     * 2. An Array with 5 members of type "Array of integers":
     * <xsd:attribute ref="soap-enc:arrayType" wsdl:arrayType="xsd:int[][5]" />
     * 
     * 3. An Array with 3 members of type "two-dimensional arrays of integers":
     * <xsd:attribute ref="soap-enc:arrayType" wsdl:arrayType="xsd:int[,][3]" />
     * 
     * @param wsdlArrayType The value of the wsdl:arrayType attribute that
     * specifies the signature of the SOAP encoded array including the type and
     * the dimensions and size information.
     */
    private function parseWSDLArrayType(wsdlArrayType:String):void
    {
        // Isolate the prefixed schemaType from the rank and length information
        // QName *(rank) size
        var typeName:String;
        var startBracket:int = wsdlArrayType.indexOf("[");
        var endBracket:int = -1;

        if (startBracket > 0)
        {
            dimensionString = wsdlArrayType.substring(startBracket);
            schemaTypeName = StringUtil.trim(wsdlArrayType.substring(0, startBracket));
            endBracket = wsdlArrayType.indexOf("]", startBracket);
        }

        if (startBracket < 0 || endBracket < 0)
        {
            throw new Error("Invalid SOAP-encoded Array type '" + wsdlArrayType + "'.");
        }

        var rankOrSizeString:String = StringUtil.trim(wsdlArrayType.substring(startBracket));
        var dimsArray:Array = rankOrSizeString.split("[");
        var currentDimension:Array = dimensions;

        // We process the rank from right to left as the size of the type is the
        // right most set of brackets. Jagged arrays are treated as a nested
        // set of dimensions.
        for (var i:int = dimsArray.length - 1; i >= 0; i--)
        {
            var dimsString:String = dimsArray[i] as String;

            // We should have at the very least a close bracket
            if (dimsString.length > 0)
            {
                // A rank may define none [], one [x], or multiple dimensions [x,y]
                if (currentDimension.length > 0)
                {
                    var newDimension:Array = [];
                    currentDimension.push(newDimension);
                    currentDimension = newDimension;
                }
                parseDimensions(wsdlArrayType, dimsString, currentDimension);
            }
        }
    }

    private function parseDimensions(wsdlArrayType:String, dimensionsString:String, currentDimension:Array):void
    {
        // Remove leading or trailing braces from [x,y,...] to reveal x,y,...
        if (dimensionsString.charAt(0) == "[")
            dimensionsString = dimensionsString.substring(1);

        if (dimensionsString.charAt(dimensionsString.length - 1) == "]")
            dimensionsString = dimensionsString.substring(0, dimensionsString.length - 1);

        var dim:Number;
        var dimensions:Array = dimensionsString.split(",");
        if (dimensions.length > 0)
        {
            // Process each dimension from x,y,... as "x" or "y" etc...
            for each (var dimString:String in dimensions)
            {
                if (dimString.length > 0)
                {
                    dim = parseInt(dimString);
                    if (!isNaN(dim) && dim < int.MAX_VALUE)
                    {
                        currentDimension.push(int(dim));
                    }
                    else
                    {
                        throw new Error("Invalid dimension '" + dimString + "' for SOAP encoded Array type '" + wsdlArrayType +"'.");
                    }
                }
                else
                {
                    // Unbounded dimension, record as -1
                    currentDimension.push(-1);
                }
            }
        }
    }

    /**
     * Attempts to unwrap MXML Array properties that are wrapped in an Object
     * with a single child element that is an Array itself.
     * 
     * e.g. the following MXML:
     * 
     * <mx:request>
     *     <inputArray>
     *         <item>A</item>
     *         <item>B</item>
     *     </inputArray>
     * </mx:request>
     * 
     * would return an Object for inputArray as {item:[A,B]} instead of
     * simply the Array [A,B].
     */
    private function unwrapMXMLArray(value:*):*
    {
        var result:* = value;

        if (!(value is Array))
        {
            try
            {
                var classInfo:Object = ObjectUtil.getClassInfo(value as Object);
                var properties:Array = classInfo["properties"];
                if (properties.length == 1)
                {
                    var property:String = properties[0];
                    if (property != null && value.hasOwnProperty(property))
                    {
                        var childValue:* = value[property];
                        if (childValue is Array)
                        {
                            result = childValue;
                        }
                    }
                }
            }
            catch(e:Error)
            {
            }
        }

        return result;
    }

    private function get dimensions():Array
    {
        if (_dimensions == null)
            _dimensions = [];

        return _dimensions;
    }

    private var _dimensions:Array;
    private var dimensionString:String;
    private var itemName:QName;
    private var processor:SchemaProcessor;
    private var schemaConstants:SchemaConstants;
    private var schemaManager:SchemaManager;
    private var schemaTypeName:String;
    private var schemaType:QName;
    private var soapConstants:SOAPConstants;
}

}
