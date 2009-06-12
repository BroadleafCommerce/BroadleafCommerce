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

import mx.rpc.soap.SOAPDecoder;
import mx.rpc.soap.SOAPEncoder;
import mx.rpc.xml.ContentProxy;
import mx.rpc.xml.TypeIterator;
import mx.rpc.xml.XMLDecoder;
import mx.utils.object_proxy;
import mx.utils.ObjectProxy;

use namespace object_proxy;

[ExcludeClass]

/**
 * Marshalls between the SOAP representation of a ColdFusion Query (aka a
 * "Query Bean") and ActionScript.
 * @private
 */
public class QueryBeanType implements ICustomSOAPType
{
    public function QueryBeanType()
    {
        super();
    }

    public function encode(encoder:SOAPEncoder, parent:XML, name:QName, value:*, restriction:XML = null):void
    {
        throw new Error("Unsupported operation - Query Beans cannot be sent to ColdFusion.");
    }

    public function decode(decoder:SOAPDecoder, parent:*, name:*, value:*, restriction:XML = null):void
    {
        var beanXML:XML = value as XML;
        
        var returnVal:*;
        if (decoder.makeObjectsBindable)
            returnVal = new XMLDecoder.listClass();
        else
            returnVal = [];

        // Query Column Names or "columnList" section
        var colXML:XML = beanXML.columnList[0];
        if (colXML == null)
            return;

        var columnsXMLList:XMLList = colXML.elements();
        var columnNames:Array = [];
        for each (var columnName:XML in columnsXMLList)
        {
            columnNames.push(columnName.toString());
        }

        // Query Rows or "data" section
        var dataXML:XML = beanXML.data[0];
        if (dataXML == null)
            return;

        var soapArrayType:SOAPArrayType = new SOAPArrayType();
        var tempParent:Array = [];
        soapArrayType.decode(decoder, tempParent, name, dataXML, restriction);

        // Turn the Array of Arrays into an Array of Objects keyed by a
        // column name to value...
        var iterator:TypeIterator = new TypeIterator(tempParent);
        while (iterator.hasNext())
        {
            var item:* = iterator.next();
            var resultItem:*;
            if (decoder.makeObjectsBindable)
                resultItem = new ObjectProxy();
            else
                resultItem = {};

            for (var i:uint = 0; i < columnNames.length; i++)
            {
                resultItem[columnNames[i]] = item[i];
            }

            TypeIterator.push(returnVal, resultItem);
        }

        decoder.setValue(parent, name, returnVal);
    }
}

}