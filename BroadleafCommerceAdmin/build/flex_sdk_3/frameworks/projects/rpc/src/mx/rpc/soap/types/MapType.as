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

import flash.utils.ByteArray;

import mx.rpc.soap.SOAPDecoder;
import mx.rpc.soap.SOAPEncoder;
import mx.rpc.xml.ContentProxy;
import mx.rpc.xml.SchemaDatatypes;
import mx.rpc.xml.SchemaConstants;
import mx.rpc.xml.SchemaMarshaller;
import mx.utils.object_proxy;

use namespace object_proxy;

[ExcludeClass]

/**
 * Marshalls between the SOAP representation of a java.util.Map and
 * ActionScript.
 * @private
 */
public class MapType implements ICustomSOAPType
{
    public function MapType()
    {
        super();
    }

    public function encode(encoder:SOAPEncoder, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        var datatypes:SchemaDatatypes = encoder.schemaManager.schemaDatatypes;

        for (var item:String in value)
        {
            var itemNode:XML = encoder.createElement(itemQName);
            var keyNode:XML = encoder.createElement(keyQName);
            var valueNode:XML = encoder.createElement(valueQName);

            // TODO: a better solution is needed for both key and value processing here...
            if (item != null)
                encoder.encodeType(datatypes.stringQName, keyNode, keyQName, item);
            else
                encoder.setValue(keyNode, null);

            encoder.setValue(itemNode, keyNode);

            //FIXME: We need a public API to handle any, even complex values
            var keyValue:Object = value[item];
            if (keyValue != null)
            {
                // FIXME: completely.
                var typeLocalName:String = "string";
                if (keyValue is Number)
                {
                    if (keyValue is uint)
                    {
                        typeLocalName = "unsignedInt";
                    }
                    else if (keyValue is int)
                    {
                        typeLocalName = "int";
                    }
                    else
                    {
                        typeLocalName = "double";
                    }
                }
                else if (keyValue is Boolean)
                {
                    typeLocalName = "boolean";
                }
                else if (keyValue is String)
                {
                    typeLocalName = "string";
                }
                else if (keyValue is ByteArray)
                {
                    if (SchemaMarshaller.byteArrayAsBase64Binary)
                        typeLocalName = "base64Binary";
                    else
                        typeLocalName = "hexBinary";
                }
                else if (keyValue is Date)
                {
                    typeLocalName = "dateTime";
                }

                var constants:SchemaConstants = encoder.schemaManager.schemaConstants;
                var type:QName = (typeLocalName != null) ? new QName(constants.xsdURI, typeLocalName) : null;

                encoder.encodeType(type, valueNode, valueQName, keyValue);
            }
            else
            {
                encoder.setValue(valueNode, null);
            }

            encoder.setValue(itemNode, valueNode);
            encoder.setValue(parent, itemNode);
        }
    }

    public function decode(decoder:SOAPDecoder, parent:*, name:*, value:*, restriction:XML = null):void
    {
        if (parent is ContentProxy)
            parent.object_proxy::isSimple = false;

        var constants:SchemaConstants = decoder.schemaManager.schemaConstants;
        var mapValue:XML = value as XML;

        for each (var item:XML in mapValue.elements())
        {
            var key:Object;
            var itemValue:Object;
            for each (var itemChild:XML in item.elements())
            {
                var typeName:String = itemChild.attribute(constants.typeAttrQName);
                var type:QName;
                if (typeName != null && typeName != "")
                    type = decoder.schemaManager.getQNameForPrefixedName(typeName, itemChild);
                else
                    type = constants.anyTypeQName;

                if (itemChild.localName() == "key")
                {
                    key = decoder.createContent();
                    decoder.decodeType(type, key, itemChild.name(), itemChild);
                }
                else if (itemChild.localName() == "value")
                {
                    itemValue = decoder.createContent();
                    decoder.decodeType(type, itemValue, itemChild.name(), itemChild);
                }
                else
                {
                    throw new Error("Apache Map datatype must only contain key/value pairs.");
                }
            }
            decoder.setValue(parent, key, itemValue);
        }
    }

    public static var itemQName:QName = new QName("", "item");
    public static var keyQName:QName = new QName("", "key");
    public static var valueQName:QName = new QName("", "value");
}

}