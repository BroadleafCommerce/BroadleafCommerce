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

package mx.rpc.xml
{

import mx.collections.IList;
import mx.utils.object_proxy;

use namespace object_proxy;

[ExcludeClass]

/**
 * A helper class to iterate over an Array, IList, or XMLList.
 * 
 * @private
 */
public class TypeIterator
{
    public function TypeIterator(value:*)
    {
        super();
        _value = value;
    }

    public function hasNext():Boolean
    {
        if (_value != null)
        {
            return (getLength(_value) > counter);
        }

        return false;
    }

    public function next():*
    {
        var result:*;

        try
        {
            result = getItemAt(_value, counter);
        }
        finally
        {
            counter++;
        }

        return result;
    }

    public function get length():uint
    {
        return getLength(_value);
    }

    public function reset():void
    {
        counter = 0;
    }

    public function get value():*
    {
        return _value;
    }

    public static function getItemAt(value:*, index:uint):*
    {
        var result:*;

        if (value != null)
        {
            if (value is Array)
            {
                var arrayValue:Array = value as Array;
                result = arrayValue[index];
            }
            else if (value is IList)
            {
                var listValue:IList = value as IList;
                result = listValue.getItemAt(index);
            }
            else if (value is XMLList)
            {
                var xmlList:XMLList = value as XMLList;
                result = xmlList[index];
            }
        }

        return result;
    }

    public static function getLength(value:*):uint
    {
        var result:uint;

        if (value != null)
        {
            if (value is Array)
            {
                var arrayValue:Array = value as Array;
                result = arrayValue.length;
            }
            else if (value is IList)
            {
                var listValue:IList = value as IList;
                result = listValue.length;
            }
            else if (value is XMLList)
            {
                var xmlList:XMLList = value as XMLList;
                result = xmlList.length();
            }
        }

        return result;
    }

    public static function isIterable(value:*):Boolean
    {
        if (value is ContentProxy && !ContentProxy(value).object_proxy::isSimple)
        {
            var complexValue:* = ContentProxy(value).object_proxy::content;
            if (TypeIterator.isIterable(complexValue))
            {
                value = complexValue;
            }
        }

        if (value is Array || value is IList || value is XMLList)
            return true;

        return false;
    }

    public static function push(parent:*, value:*):uint
    {
        var result:uint;

        if (parent != null)
        {
            if (parent is ContentProxy && !ContentProxy(parent).object_proxy::isSimple)
            {
                var complexParent:* = ContentProxy(parent).object_proxy::content;
                if (TypeIterator.isIterable(complexParent))
                {
                    parent = complexParent;
                }
            }

            if (parent is Array)
            {
                var arrayValue:Array = parent as Array;
                result = arrayValue.push(value);
            }
            else if (parent is IList)
            {
                var listValue:IList = parent as IList;
                listValue.addItem(value);
                result = listValue.length;
            }
            else if (parent is XMLList)
            {
                var xmlList:XMLList = parent as XMLList;
                xmlList += value;
                result = xmlList.length();
            }
        }

        return result;
    }

    private var _value:*;
    private var counter:uint;
}

}