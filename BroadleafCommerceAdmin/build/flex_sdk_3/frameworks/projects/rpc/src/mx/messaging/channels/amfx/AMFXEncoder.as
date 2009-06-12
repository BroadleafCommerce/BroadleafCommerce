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

package mx.messaging.channels.amfx
{

import flash.utils.ByteArray;
import flash.utils.describeType;
import flash.utils.IExternalizable;
import flash.xml.XMLDocument;

import mx.logging.Log;
import mx.utils.HexEncoder;
import mx.utils.ObjectProxy;
import mx.utils.ObjectUtil;

[ExcludeClass]

/**
 * Serializes an arbitrary ActionScript object graph to an XML
 * representation that is based on Action Message Format (AMF)
 * version 3.
 * @private
 */
public class AMFXEncoder
{
    public function AMFXEncoder()
    {
        super();
        settings = {};
        settings.prettyPrinting = false;
    }

    public function encode(obj:Object, headers:Array = null):XML
    {
        XML.setSettings(settings);
        var xml:XML = new XML("<amfx />");
        xml.setNamespace(NAMESPACE);
        xml.@["ver"] = CURRENT_VERSION;

        var context:AMFXContext = new AMFXContext();
        context.log = Log.getLogger("mx.messaging.channels.amfx.AMFXEncoder");
        encodePacket(xml, obj, headers, context);

        return xml;
    }

    private static function encodePacket(xml:XML, obj:Object, headers:Array = null, context:AMFXContext = null):void
    {
        if (headers)
        {
            encodeHeaders(xml, headers, context);
        }

        encodeBody(xml, obj, context);
    }

    private static function encodeHeaders(xml:XML, headers:Array, context:AMFXContext):void
    {
        for (var i:uint = 0; i < headers.length; i++)
        {
            var header:Object = headers[i];
            var element:XML = <header />;
            element.@["name"] = header.name;
            element.@["mustUnderstand"] = (header.mustUnderstand == true);
            encodeValue(element, header.content, context);
            xml.appendChild(element);
        }
    }

    private static function encodeBody(xml:XML, obj:*, context:AMFXContext):void
    {
        var element:XML = <body />;
        //element.@["targetURI"] = ""; //TODO: Support this attribute
        encodeValue(element, obj, context);
        xml.appendChild(element);
    }

    public static function encodeValue(xml:XML, obj:*, context:AMFXContext):void
    {
        if (obj != null)
        {
            if (obj is String)
            {
                encodeString(xml, String(obj), context);
            }
            else if (obj is Number)
            {
                encodeNumber(xml, Number(obj));
            }
            else if (obj is Boolean)
            {
                encodeBoolean(xml, Boolean(obj));
            }
            else if (obj is ByteArray)
            {
                encodeByteArray(xml, ByteArray(obj));
            }
            else if (obj is Array)
            {
                encodeArray(xml, obj as Array, context);
            }
            else if (obj is XML || obj is XMLDocument)
            {
                encodeXML(xml, obj);
            }
            else if (obj is Date)
            {
                encodeDate(xml, obj as Date, context);
            }
            else if (obj is Class)
            {
                //TODO: Throw errors for unsupported types?
                if (context.log)
                    context.log.warn("Cannot serialize type Class");
            }
            else
            {
                encodeObject(xml, obj, context);
            }
        }
        else if (obj === undefined)
        {
            xml.appendChild(X_UNDEFINED.copy());
        }
        else
        {
            xml.appendChild(X_NULL.copy());
        }
    }


    private static function encodeArray(xml:XML, array:Array, context:AMFXContext):void
    {
        var ref:int = context.findObject(array);
        var element:XML;
        if (ref >= 0)
        {
            element = <ref />
            element.@["id"] = String(ref);
        }
        else
        {
            //Remember array object
            context.addObject(array);

            element = <array />;

            var named:Object = {};
            var ordinal:Array = [];
            var isECMAArray:Boolean = false;

            // Separate named and ordinal array members
            for (var member:String in array)
            {
                if (isNaN(Number(member)))
                {
                    named[member] = array[member];
                    isECMAArray = true;
                }
                else
                {
                    var num:int = parseInt(member);
                    ordinal[num] = array[num];
                }
            }

            // Encode named items as early as possible
            for (var n:String in named)
            {
                encodeArrayItem(element, n, named[n], context);
            }

            var ordinalLength:uint = 0;
            var dense:Boolean = true;
            for (var i:uint = 0; i < ordinal.length; i++)
            {
                var o:* = ordinal[i];

                // If we have an undefined slot remaining ordinal
                // keys will be converted to named keys to preserve dense set
                if (o !== undefined)
                {
                    if (dense)
                    {
                        encodeValue(element, o, context);
                        ordinalLength++;
                    }
                    else
                    {
                        isECMAArray = true;
                        encodeArrayItem(element, String(i), o, context);
                    }
                }
                else
                {
                    dense = false;
                }
            }

            element.@["length"] = String(ordinalLength);

            if (isECMAArray)
            {
                element.@["ecma"] = "true";
            }
        }

        xml.appendChild(element);
    }

    private static function encodeArrayItem(xml:XML, name:String, value:*, context:AMFXContext):void
    {
        var item:XML = <item />;
        item.@["name"] = name;
        encodeValue(item, value, context);
        xml.appendChild(item);
    }

    private static function encodeBoolean(xml:XML, bool:Boolean):void
    {
        if (bool)
        {
            xml.appendChild(X_TRUE.copy());
        }
        else
        {
            xml.appendChild(X_FALSE.copy());
        }
    }


    private static function encodeByteArray(xml:XML, obj:ByteArray):void
    {
        var element:XML = <bytearray/>;
        var encoder:HexEncoder = new HexEncoder();
        encoder.encode(obj);
        var encoded:String = encoder.flush();
        element.appendChild(encoded);
        xml.appendChild(element);
    }

    private static function encodeDate(xml:XML, date:Date, context:AMFXContext):void
    {
        var ref:int = context.findObject(date);
        var element:XML;
        if (ref >= 0)
        {
            element = <ref />
            element.@["id"] = String(ref);
        }
        else
        {
            //Remember date object
            context.addObject(date);

            element = <date />;
            element.appendChild(new XML(date.getTime().toString()));
        }
        xml.appendChild(element);
    }

    private static function encodeNumber(xml:XML, num:Number):void
    {
        var element:XML = null;
        if (num is int || num is uint)
        {
            element = <int />;
        }
        else
        {
            element = <double />;
        }
        element.appendChild(new XML(num.toString()));
        xml.appendChild(element);

    }

    private static function encodeObject(xml:XML, obj:*, context:AMFXContext):void
    {
        var ref:int = context.findObject(obj);
        var element:XML;
        if (ref >= 0)
        {
            element = <ref />
            element.@["id"] = String(ref);
        }
        else
        {
            //Remember object
            context.addObject(obj);

            element = <object />;

            var classInfo:Object = ObjectUtil.getClassInfo(obj, null, CLASS_INFO_OPTIONS);
            var className:String = classInfo.name;
            var classAlias:String = classInfo.alias;
            var properties:Array = classInfo.properties;
            var count:uint = properties.length;

            // We need to special case ObjectProxy as for serialization we actually need the
            // remote alias of ObjectProxy, not the wrapped object.
            if (obj is ObjectProxy)
            {
                var cinfo:XML = describeType(obj);
                className = cinfo.@name.toString();
                classAlias = cinfo.@alias.toString();
            }

            var remoteClassName:String = ((classAlias != null) ? classAlias : className);

            if (remoteClassName && remoteClassName != "Object" && remoteClassName != "Array")
            {
                element.@["type"] = remoteClassName.replace(REGEX_CLASSNAME, ".");
            }

            if (obj is IExternalizable)
            {
                classInfo.externalizable = true;
                encodeTraits(element, classInfo, context);

                var ext:IExternalizable = IExternalizable(obj);
                var ba:ByteArray = new ByteArray();
                ext.writeExternal(ba);
                encodeByteArray(element, ba);
            }
            else
            {
                classInfo.externalizable = false;
                encodeTraits(element, classInfo, context);

                for (var i:uint = 0; i < count; i++)
                {
                    var prop:String = properties[i];
                    encodeValue(element, obj[prop], context);
                }
            }
        }

        xml.appendChild(element);
    }

    private static function encodeString(xml:XML, str:String, context:AMFXContext, isTrait:Boolean = false):void
    {
        var ref:int = context.findString(str);
        var element:XML = <string />;
        if (ref >= 0)
        {
            element.@["id"] = String(ref);
        }
        else
        {
            //Remember string
            context.addString(str);

            if (str.length > 0)
            {
                // Traits won't contain chars that need escaping
                if (!isTrait)
                    str = escapeXMLString(str);

                var x:XML = new XML(str);
                element.appendChild(x);
            }
        }
        xml.appendChild(element);
    }

    private static function encodeTraits(xml:XML, classInfo:Object, context:AMFXContext):void
    {
        var element:XML = <traits />;

        var ref:int = context.findTraitInfo(classInfo);
        if (ref >= 0)
        {
            element.@["id"] = String(ref);
        }
        else
        {
            //Remember trait info
            context.addTraitInfo(classInfo)

            if (classInfo.externalizable)
            {
                element.@["externalizable"] = "true";
            }
            else
            {
                var properties:Array = classInfo.properties;
                if (properties != null)
                {
                    var count:uint = properties.length;
                    for (var i:uint = 0; i < count; i++)
                    {
                        var prop:String = properties[i];
                        encodeString(element, prop, context, true);
                    }
                }
            }
        }

        xml.appendChild(element);
    }

    private static function encodeXML(xml:XML, xmlObject:Object):void
    {
        var element:XML = <xml />;
        var str:String;
        if (xmlObject is XML)
            str = XML(xmlObject).toXMLString();
        else
            str = xmlObject.toString();

        if (str.length > 0)
        {
            str = escapeXMLString(str);
            var x:XML = new XML(str);
            element.appendChild(x);
        }
        xml.appendChild(element);
    }

    private static function escapeXMLString(str:String):String
    {
        if (str.length > 0)
        {
            if ((str.indexOf("<") != -1) || (str.indexOf("&") != -1))
            {
                if (str.indexOf("]]>") != -1)
                {
                    str = str.replace(REGEX_CLOSE_CDATA, "]]&gt;");
                }

                str = "<![CDATA[" + str + "]]>";
            }
        }

        return str;
    }

    private var settings:Object;

    public static const CURRENT_VERSION:uint = 3;
    public static const NAMESPACE_URI:String = "http://www.macromedia.com/2005/amfx";
    public static const NAMESPACE:Namespace = new Namespace("", NAMESPACE_URI);

    private static const REGEX_CLASSNAME:RegExp = new RegExp("\\:\\:", "g");
    private static const REGEX_CLOSE_CDATA:RegExp = new RegExp("]]>", "g");

    private static const CLASS_INFO_OPTIONS:Object = {includeReadOnly:false, includeTransient:false};

    private static const X_FALSE:XML = <false />;
    private static const X_NULL:XML = <null />;
    private static const X_TRUE:XML = <true />;
    private static const X_UNDEFINED:XML = <undefined />;
}

}
