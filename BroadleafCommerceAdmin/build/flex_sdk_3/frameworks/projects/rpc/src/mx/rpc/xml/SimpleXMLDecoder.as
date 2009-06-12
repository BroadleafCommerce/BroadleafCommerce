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

import flash.xml.XMLNode;
import flash.xml.XMLNodeType;
import mx.collections.ArrayCollection;
import mx.utils.ObjectProxy;

/**
 *  The SimpleXMLDecoder class deserialize XML into a graph of ActionScript objects.
 * Use  this class when no schema information is available.
 */
public class SimpleXMLDecoder
{
    //--------------------------------------------------------------------------
    //
    //  Class Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public static function simpleType(val:Object):Object
    {
        var result:Object = val;

        if (val != null)
        {
            //return the value as a string, a boolean or a number.
            //numbers that start with 0 are left as strings
            //bForceObject removed since we'll take care of converting to a String or Number object later
            if (val is String && String(val) == "")
            {
                result = val.toString();    
            }
            else if (isNaN(Number(val)) || (val.charAt(0) == '0') || ((val.charAt(0) == '-') && (val.charAt(1) == '0')) || val.charAt(val.length -1) == 'E')
            {
                var valStr:String = val.toString();

                //Bug 101205: Also check for boolean
                var valStrLC:String = valStr.toLowerCase();
                if (valStrLC == "true")
                    result = true;
                else if (valStrLC == "false")
                    result = false;
                else
                    result = valStr;
            }
            else
            {
                result = Number(val);
            }
        }

        return result;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Constructor.
     */
    public function SimpleXMLDecoder(makeObjectsBindable:Boolean = false)
    {
        super();

        this.makeObjectsBindable = makeObjectsBindable;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Converts a tree of XMLNodes into a tree of ActionScript Objects.
     *
     *  @param dataNode An XMLNode to be converted into a tree of ActionScript Objects.
     *
     *  @return A tree of ActionScript Objects.
     */
    public function decodeXML(dataNode:XMLNode):Object
    {
        var result:Object;
        var isSimpleType:Boolean = false;

        if (dataNode == null)
            return null;

        // Cycle through the subnodes
        var children:Array = dataNode.childNodes;
        if (children.length == 1 && children[0].nodeType == XMLNodeType.TEXT_NODE)
        {
            // If exactly one text node subtype, we must want a simple
            // value.
            isSimpleType = true;
            result = SimpleXMLDecoder.simpleType(children[0].nodeValue);
        }
        else if (children.length > 0)
        {
            result = {};
            if (makeObjectsBindable)
                result = new ObjectProxy(result);

            for (var i:uint = 0; i < children.length; i++)
            {
                var partNode:XMLNode = children[i];

                // skip text nodes, which are part of mixed content
                if (partNode.nodeType != XMLNodeType.ELEMENT_NODE)
                {
                    continue;
                }

                var partName:String = getLocalName(partNode);
                var partObj:Object = decodeXML(partNode);

                // Enable processing multiple copies of the same element (sequences)
                var existing:Object = result[partName];
                if (existing != null)
                {
                    if (existing is Array)
                    {
                        existing.push(partObj);
                    }
                    else if (existing is ArrayCollection)
                    {
                        existing.source.push(partObj);
                    }
                    else
                    {
                        existing = [existing];
                        existing.push(partObj);

                        if (makeObjectsBindable)
                            existing = new ArrayCollection(existing as Array);

                        result[partName] = existing;
                    }
                }
                else
                {
                    result[partName] = partObj;
                }
            }
        }

        // Cycle through the attributes
        var attributes:Object = dataNode.attributes;
        for (var attribute:String in attributes)
        {
            if (attribute == "xmlns" || attribute.indexOf("xmlns:") != -1)
                continue;

            // result can be null if it contains no children.
            if (result == null)
            {
                result = {};
                if (makeObjectsBindable)
                    result = new ObjectProxy(result);
            }

            // If result is not currently an Object (it is a Number, Boolean,
            // or String), then convert it to be a ComplexString so that we
            // can attach attributes to it.  (See comment in ComplexString.as)
            if (isSimpleType && !(result is ComplexString))
            {
                result = new ComplexString(result.toString());
                isSimpleType = false;
            }

            result[attribute] = SimpleXMLDecoder.simpleType(attributes[attribute]);
        }

        return result;
    }

    /**
     * Returns the local name of an XMLNode.
     *
     * @return The local name of an XMLNode.
     */
    public static function getLocalName(xmlNode:XMLNode):String
    {
        var name:String = xmlNode.nodeName;
        var myPrefixIndex:int = name.indexOf(":");
        if (myPrefixIndex != -1)
        {
            name = name.substring(myPrefixIndex+1);
        }
        return name;
    }

    private var makeObjectsBindable:Boolean;
}

}
