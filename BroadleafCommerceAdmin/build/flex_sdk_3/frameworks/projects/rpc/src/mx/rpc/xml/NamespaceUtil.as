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

[ExcludeClass]

/**
 * @private
 */
public class NamespaceUtil
{
    public function NamespaceUtil()
    {
        super();
    }

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

    public static function getElementsByLocalName(xmlNode:XMLNode, lname:String):Array
    {
        var elements:Array;

        if (xmlNode is XMLDocument)
        {
            elements = getElementsByLocalName(xmlNode.firstChild, lname);
        }
        else
        {
            elements = [];
            if (getLocalName(xmlNode) == lname)
            {
                elements.push(xmlNode);
            }

            var numChildren:uint = xmlNode.childNodes.length;
            for (var i:uint = 0; i < numChildren; i++)
            {
                var subElement:XMLNode = xmlNode.childNodes[i];
                // NOTE: No longer digging deeper than one level (big perf boost)
                if (getLocalName(subElement) == lname)
                {
                    elements.push(subElement);
                }
            }
        }

        return elements;
    }
}

}