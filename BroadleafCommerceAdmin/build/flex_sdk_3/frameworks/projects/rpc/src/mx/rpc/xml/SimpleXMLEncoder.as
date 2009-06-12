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

package mx.rpc.xml
{

import flash.xml.XMLDocument;
import flash.xml.XMLNode;
import mx.utils.ObjectUtil;

/**
 * The SimpleXMLEncoder class takes ActionScript Objects and encodes them to XML
 * using default serialization.
 */
public class SimpleXMLEncoder
{
	//--------------------------------------------------------------------------
	//
	//  Class Methods
	//
	//--------------------------------------------------------------------------
    /**
     * @private
     */
    static internal function encodeDate(rawDate:Date, dateType:String):String
    {
        var s:String = new String();
        var n:Number;

        if (dateType == "dateTime" || dateType == "date")
        {
            s = s.concat(rawDate.getUTCFullYear(), "-");

            n = rawDate.getUTCMonth()+1;
            if (n < 10) s = s.concat("0");
            s = s.concat(n, "-");

            n = rawDate.getUTCDate();
            if (n < 10) s = s.concat("0");
            s = s.concat(n);
        }

        if (dateType == "dateTime")
        {
            s = s.concat("T");
        }

        if (dateType == "dateTime" || dateType == "time")
        {
            n = rawDate.getUTCHours();
            if (n < 10) s = s.concat("0");
            s = s.concat(n, ":");

            n = rawDate.getUTCMinutes();
            if (n < 10) s = s.concat("0");
            s = s.concat(n, ":");

            n = rawDate.getUTCSeconds();
            if (n < 10) s = s.concat("0");
            s = s.concat(n, ".");

            n = rawDate.getUTCMilliseconds();
            if (n < 10) s = s.concat("00");
            else if (n < 100) s = s.concat("0");
            s = s.concat(n);
        }

        s = s.concat("Z");

        return s;
    }

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    public function SimpleXMLEncoder(myXML:XMLDocument)
    {
        super();

        this.myXMLDoc = myXML ? myXML : new XMLDocument();
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    private var myXMLDoc:XMLDocument;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     * Encodes an ActionScript object to XML using default serialization.
     * 
     * @param obj The ActionScript object to encode.
     * 
     * @param qname The qualified name of the child node.
     * 
     * @param parentNode An XMLNode under which to put the encoded
     * value.
     */
    public function encodeValue(obj:Object, qname:QName, parentNode:XMLNode):XMLNode
    {
        var myElement:XMLNode;

        if (obj == null)
            return null;

		// Skip properties that are functions
        var typeType:uint = getDataTypeFromObject(obj);
        if (typeType == SimpleXMLEncoder.FUNCTION_TYPE)
            return null;

        if (typeType == SimpleXMLEncoder.XML_TYPE)
        {
            myElement = obj.cloneNode(true);
            parentNode.appendChild(myElement);
            return myElement;
        }

        myElement = myXMLDoc.createElement("foo");
        myElement.nodeName = qname.localName;
        parentNode.appendChild(myElement);

        if (typeType == SimpleXMLEncoder.OBJECT_TYPE)
        {
            var classInfo:Object = ObjectUtil.getClassInfo(obj, null, CLASS_INFO_OPTIONS);
			var properties:Array = classInfo.properties;
			var pCount:uint = properties.length;
			for (var p:uint = 0; p < pCount; p++)
			{
				var fieldName:String = properties[p];
				var propQName:QName = new QName("", fieldName);
				encodeValue(obj[fieldName], propQName, myElement);
			}
        }
        else if (typeType == SimpleXMLEncoder.ARRAY_TYPE)
        {
            var numMembers:uint = obj.length;
            var itemQName:QName = new QName("", "item");

            for (var i:uint = 0; i < numMembers; i++)
            {
                encodeValue(obj[i], itemQName, myElement);
            }
        }
        else
        {
            // Simple types fall through to here
            var valueString:String;

            if (typeType == SimpleXMLEncoder.DATE_TYPE)
            {
                valueString = encodeDate(obj as Date, "dateTime");
            }
            else if (typeType == SimpleXMLEncoder.NUMBER_TYPE)
            {
                if (obj == Number.POSITIVE_INFINITY)
                    valueString = "INF";
                else if (obj == Number.NEGATIVE_INFINITY)
                    valueString = "-INF";
                else
                {
                    var rep:String = obj.toString();
                    // see if its hex
                    var start:String = rep.substr(0, 2);
                    if (start == "0X" || start == "0x")
                    {
                        valueString = parseInt(rep).toString();
                    }
                    else
                    {
                        valueString = rep;
                    }
                }
            }
            else
            {
                valueString = obj.toString();
            }

            var valueNode:XMLNode = myXMLDoc.createTextNode(valueString);
            myElement.appendChild(valueNode);
        }

        return myElement;
    }

    /**
     *  @private
     */
    private function getDataTypeFromObject(obj:Object):uint
    {
        if (obj is Number)
        	return SimpleXMLEncoder.NUMBER_TYPE;
        else if (obj is Boolean)
        	return SimpleXMLEncoder.BOOLEAN_TYPE;
        else if (obj is String)
        	return SimpleXMLEncoder.STRING_TYPE;
        else if (obj is XMLDocument)
			return SimpleXMLEncoder.XML_TYPE;
		else if (obj is Date)
			return SimpleXMLEncoder.DATE_TYPE;
		else if (obj is Array)
			return SimpleXMLEncoder.ARRAY_TYPE;
        else if (obj is Function)
            return SimpleXMLEncoder.FUNCTION_TYPE;
		else if (obj is Object)
            return SimpleXMLEncoder.OBJECT_TYPE;
        else
            // Otherwise force it to string
        	return SimpleXMLEncoder.STRING_TYPE;
    }


    private static const NUMBER_TYPE:uint   = 0;
    private static const STRING_TYPE:uint   = 1;
    private static const OBJECT_TYPE:uint   = 2;
    private static const DATE_TYPE:uint     = 3;
    private static const BOOLEAN_TYPE:uint  = 4;
    private static const XML_TYPE:uint      = 5;
    private static const ARRAY_TYPE:uint    = 6;  // An array with a wrapper element
    private static const MAP_TYPE:uint      = 7;
    private static const ANY_TYPE:uint      = 8;
    // We don't appear to use this type anywhere, commenting out
    //private static const COLL_TYPE:uint     = 10; // A collection (no wrapper element, just maxOccurs)
    private static const ROWSET_TYPE:uint   = 11;
    private static const QBEAN_TYPE:uint    = 12; // CF QueryBean
    private static const DOC_TYPE:uint      = 13;
    private static const SCHEMA_TYPE:uint   = 14;
    private static const FUNCTION_TYPE:uint = 15; // We currently do not serialize properties of type function
    private static const ELEMENT_TYPE:uint  = 16;
    private static const BASE64_BINARY_TYPE:uint = 17;
    private static const HEX_BINARY_TYPE:uint = 18;

    /**
     * @private
     */
    private static const CLASS_INFO_OPTIONS:Object = {includeReadOnly:false, includeTransient:false};
}

}
