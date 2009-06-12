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

package mx.utils
{

import flash.utils.ByteArray;
import flash.utils.Dictionary;
import flash.utils.getQualifiedClassName;
import flash.xml.XMLNode;

import mx.collections.IList;

/**
 *  The ObjectUtil class is an all-static class with methods for
 *  working with Objects within Flex.
 *  You do not create instances of ObjectUtil;
 *  instead you simply call static methods such as the 
 *  <code>ObjectUtil.isSimple()</code> method.
 */
public class ObjectUtil
{
    include "../core/Version.as";
    
    /**
    *  Array of properties to exclude from debugging output.
    */
    private static var defaultToStringExcludes:Array = ["password", "credentials"];

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Compares the Objects and returns an integer value 
     *  indicating if the first item is less than greater than or equal to
     *  the second item.
     *  This method will recursively compare properties on nested objects and
     *  will return as soon as a non-zero result is found.
     *  By default this method will recurse to the deepest level of any property.
     *  To change the depth for comparison specify a non-negative value for
     *  the <code>depth</code> parameter.
     *
     *  @param a Object.
     *
     *  @param b Object.
     *
     *  @param depth Indicates how many levels should be 
     *  recursed when performing the comparison.
     *  Set this value to 0 for a shallow comparison of only the primitive 
     *  representation of each property.
     *  For example:<pre>
     *  var a:Object = {name:"Bob", info:[1,2,3]};
     *  var b:Object = {name:"Alice", info:[5,6,7]};
     *  var c:int = ObjectUtil.compare(a, b, 0);</pre>
     *
     *  <p>In the above example the complex properties of <code>a</code> and 
     *  <code>b</code> will be flattened by a call to <code>toString()</code>
     *  when doing the comparison.
     *  In this case the <code>info</code> property will be turned into a string
     *  when performing the comparison.</p>
     *
     *  @return Return 0 if a and b are null, NaN, or equal. 
     *  Return 1 if a is null or greater than b. 
     *  Return -1 if b is null or greater than a. 
     */
    public static function compare(a:Object, b:Object, depth:int = -1):int
    {
        return internalCompare(a, b, 0, depth, new Dictionary(true));
    }
    
    /**
     *  Copies the specified Object and returns a reference to the copy.
     *  The copy is made using a native serialization technique. 
     *  This means that custom serialization will be respected during the copy.
     *
     *  <p>This method is designed for copying data objects, 
     *  such as elements of a collection. It is not intended for copying 
     *  a UIComponent object, such as a TextInput control. If you want to create copies 
     *  of specific UIComponent objects, you can create a subclass of the component and implement 
     *  a <code>clone()</code> method, or other method to perform the copy.</p>
     * 
     *  @param value Object that should be copied.
     * 
     *  @return Copy of the specified Object.
     */ 
    public static function copy(value:Object):Object
    {
        var buffer:ByteArray = new ByteArray();
        buffer.writeObject(value);
        buffer.position = 0;
        var result:Object = buffer.readObject();
        return result;
    }
    
    /**
     *  Returns <code>true</code> if the object reference specified
     *  is a simple data type. The simple data types include the following:
     *  <ul>
     *    <li><code>String</code></li>
     *    <li><code>Number</code></li>
     *    <li><code>uint</code></li>
     *    <li><code>int</code></li>
     *    <li><code>Boolean</code></li>
     *    <li><code>Date</code></li>
     *    <li><code>Array</code></li>
     *  </ul>
     *
     *  @param value Object inspected.
     *
     *  @return <code>true</code> if the object specified
     *  is one of the types above; <code>false</code> otherwise.
     */
    public static function isSimple(value:Object):Boolean
    {
        var type:String = typeof(value);
        switch (type)
        {
            case "number":
            case "string":
            case "boolean":
            {
                return true;
            }

            case "object":
            {
                return (value is Date) || (value is Array);
            }
        }

        return false;
    }

    /**
     *  Compares two numeric values.
     * 
     *  @param a First number.
     * 
     *  @param b Second number.
     *
     *  @return 0 is both numbers are NaN. 
     *  1 if only <code>a</code> is a NaN.
     *  -1 if only <code>b</code> is a NaN.
     *  -1 if <code>a</code> is less than <code>b</code>.
     *  1 if <code>a</code> is greater than <code>b</code>.
     */
    public static function numericCompare(a:Number, b:Number):int
    {
        if (isNaN(a) && isNaN(b))
            return 0;

        if (isNaN(a))
            return 1;

        if (isNaN(b))
           return -1;

        if (a < b)
            return -1;

        if (a > b)
            return 1;

        return 0;
    }

    /**
     *  Compares two String values.
     * 
     *  @param a First String value.
     * 
     *  @param b Second String value.
     *
     *  @param caseInsensitive Specifies to perform a case insensitive compare, 
     *  <code>true</code>, or not, <code>false</code>.
     *
     *  @return 0 is both Strings are null. 
     *  1 if only <code>a</code> is null.
     *  -1 if only <code>b</code> is null.
     *  -1 if <code>a</code> precedes <code>b</code>.
     *  1 if <code>b</code> precedes <code>a</code>.
     */
    public static function stringCompare(a:String, b:String,
                                         caseInsensitive:Boolean = false):int
    {
        if (a == null && b == null)
            return 0;

        if (a == null)
          return 1;

        if (b == null)
           return -1;

        // Convert to lowercase if we are case insensitive.
        if (caseInsensitive)
        {
            a = a.toLocaleLowerCase();
            b = b.toLocaleLowerCase();
        }

        var result:int = a.localeCompare(b);
        
        if (result < -1)
            result = -1;
        else if (result > 1)
            result = 1;

        return result;
    }

    /**
     *  Compares the two Date objects and returns an integer value 
     *  indicating if the first Date object is before, equal to, 
     *  or after the second item.
     *
     *  @param a Date object.
     *
     *  @param b Date object.
     *
     *  @return 0 if <code>a</code> and <code>b</code>
     *  are <code>null</code> or equal; 
     *  1 if <code>a</code> is <code>null</code>
     *  or before <code>b</code>; 
     *  -1 if <code>b</code> is <code>null</code>
     *  or before <code>a</code>. 
     */
    public static function dateCompare(a:Date, b:Date):int
    {
        if (a == null && b == null)
            return 0;

        if (a == null)
          return 1;

        if (b == null)
           return -1;

        var na:Number = a.getTime();
        var nb:Number = b.getTime();
        
        if (na < nb)
            return -1;

        if (na > nb)
            return 1;

        return 0;
    }
        
    /**
     *  Pretty-prints the specified Object into a String.
     *  All properties will be in alpha ordering.
     *  Each object will be assigned an id during printing;
     *  this value will be displayed next to the object type token
     *  preceded by a '#', for example:
     *
     *  <pre>
     *  (mx.messaging.messages::AsyncMessage)#2.</pre>
     *
     *  <p>This id is used to indicate when a circular reference occurs.
     *  Properties of an object that are of the <code>Class</code> type will
     *  appear only as the assigned type.
     *  For example a custom definition like the following:</p>
     *
     *  <pre>
     *    public class MyCustomClass {
     *      public var clazz:Class;
     *    }</pre>
     * 
     *  <p>With the <code>clazz</code> property assigned to <code>Date</code>
     *  will display as shown below:</p>
     * 
     *  <pre>
     *   (somepackage::MyCustomClass)#0
     *      clazz = (Date)</pre>
     *
     *  @param obj Object to be pretty printed.
     * 
     *  @param namespaceURIs Array of namespace URIs for properties 
     *  that should be included in the output.
     *  By default only properties in the public namespace will be included in
     *  the output.
     *  To get all properties regardless of namespace pass an array with a 
     *  single element of "*".
     * 
     *  @param exclude Array of the property names that should be 
     *  excluded from the output.
     *  Use this to remove data from the formatted string.
     * 
     *  @return String containing the formatted version
     *  of the specified object.
     *
     *  @example
     *  <pre>
     *  // example 1
     *  var obj:AsyncMessage = new AsyncMessage();
     *  obj.body = [];
     *  obj.body.push(new AsyncMessage());
     *  obj.headers["1"] = { name: "myName", num: 15.3};
     *  obj.headers["2"] = { name: "myName", num: 15.3};
     *  obj.headers["10"] = { name: "myName", num: 15.3};
     *  obj.headers["11"] = { name: "myName", num: 15.3};
     *  trace(ObjectUtil.toString(obj));
     *
     *  // will output to flashlog.txt
     *  (mx.messaging.messages::AsyncMessage)#0
     *    body = (Array)#1
     *      [0] (mx.messaging.messages::AsyncMessage)#2
     *        body = (Object)#3
     *        clientId = (Null)
     *        correlationId = ""
     *        destination = ""
     *        headers = (Object)#4
     *        messageId = "378CE96A-68DB-BC1B-BCF7FFFFFFFFB525"
     *        sequenceId = (Null)
     *        sequencePosition = 0
     *        sequenceSize = 0
     *        timeToLive = 0
     *        timestamp = 0
     *    clientId = (Null)
     *    correlationId = ""
     *    destination = ""
     *    headers = (Object)#5
     *      1 = (Object)#6
     *        name = "myName"
     *        num = 15.3
     *      10 = (Object)#7
     *        name = "myName"
     *        num = 15.3
     *      11 = (Object)#8
     *        name = "myName"
     *        num = 15.3
     *      2 = (Object)#9
     *        name = "myName"
     *        num = 15.3
     *    messageId = "1D3E6E96-AC2D-BD11-6A39FFFFFFFF517E"
     *    sequenceId = (Null)
     *    sequencePosition = 0
     *    sequenceSize = 0
     *    timeToLive = 0
     *    timestamp = 0
     *
     *  // example 2 with circular references
     *  obj = {};
     *  obj.prop1 = new Date();
     *  obj.prop2 = [];
     *  obj.prop2.push(15.2);
     *  obj.prop2.push("testing");
     *  obj.prop2.push(true);
     *  obj.prop3 = {};
     *  obj.prop3.circular = obj;
     *  obj.prop3.deeper = new ErrorMessage();
     *  obj.prop3.deeper.rootCause = obj.prop3.deeper;
     *  obj.prop3.deeper2 = {};
     *  obj.prop3.deeper2.deeperStill = {};
     *  obj.prop3.deeper2.deeperStill.yetDeeper = obj;
     *  trace(ObjectUtil.toString(obj));
     *
     *  // will output to flashlog.txt
     *  (Object)#0
     *    prop1 = Tue Apr 26 13:59:17 GMT-0700 2005
     *    prop2 = (Array)#1
     *      [0] 15.2
     *      [1] "testing"
     *      [2] true
     *    prop3 = (Object)#2
     *      circular = (Object)#0
     *      deeper = (mx.messaging.messages::ErrorMessage)#3
     *        body = (Object)#4
     *        clientId = (Null)
     *        code = (Null)
     *        correlationId = ""
     *        destination = ""
     *        details = (Null)
     *        headers = (Object)#5
     *        level = (Null)
     *        message = (Null)
     *        messageId = "14039376-2BBA-0D0E-22A3FFFFFFFF140A"
     *        rootCause = (mx.messaging.messages::ErrorMessage)#3
     *        sequenceId = (Null)
     *        sequencePosition = 0
     *        sequenceSize = 0
     *        timeToLive = 0
     *        timestamp = 0
     *      deeper2 = (Object)#6
     *        deeperStill = (Object)#7
     *          yetDeeper = (Object)#0
     * 
     * // example 3 with Dictionary
     * var point:Point = new Point(100, 100);
     * var point2:Point = new Point(100, 100);
     * var obj:Dictionary = new Dictionary();
     * obj[point] = "point";
     * obj[point2] = "point2";
     * obj["1"] = { name: "one", num: 1};
     * obj["two"] = { name: "2", num: 2};
     * obj[3] = 3;
     * trace(ObjectUtil.toString(obj));
     * 
     * // will output to flashlog.txt
     * (flash.utils::Dictionary)#0
     *   {(flash.geom::Point)#1
     *     length = 141.4213562373095
     *     x = 100
     *     y = 100} = "point2"
     *   {(flash.geom::Point)#2
     *     length = 141.4213562373095
     *     x = 100
     *     y = 100} = "point"
     *   {1} = (Object)#3
     *     name = "one"
     *     num = 1
     *   {3} = 3
     *   {"two"} = (Object)#4
     *     name = "2"
     *     num = 2
     * 
     * </pre>
     */
    public static function toString(value:Object, 
                                    namespaceURIs:Array = null, 
                                    exclude:Array = null):String
    {
        if (exclude == null)
        {
            exclude = defaultToStringExcludes;
        }
        
        refCount = 0;
        return internalToString(value, 0, null, namespaceURIs, exclude);
    }
    
    /**
     *  This method cleans up all of the additional parameters that show up in AsDoc
     *  code hinting tools that developers shouldn't ever see.
     *  @private
     */
    private static function internalToString(value:Object, 
                                             indent:int = 0,
                                             refs:Dictionary= null, 
                                             namespaceURIs:Array = null, 
                                             exclude:Array = null):String
    {
        var str:String;
        var type:String = value == null ? "null" : typeof(value);
        switch (type)
        {
            case "boolean":
            case "number":
            {
                return value.toString();
            }

            case "string":
            {
                return "\"" + value.toString() + "\"";
            }

            case "object":
            {
                if (value is Date)
                {
                    return value.toString();
                }
                else if (value is XMLNode)
                {
                    return value.toString();
                }
                else if (value is Class)
                {
                    return "(" + getQualifiedClassName(value) + ")";
                }
                else
                {
                    var classInfo:Object = getClassInfo(value, exclude,
                        { includeReadOnly: true, uris: namespaceURIs });
                        
                    var properties:Array = classInfo.properties;
                    
                    str = "(" + classInfo.name + ")";
                    
                    // refs help us avoid circular reference infinite recursion.
                    // Each time an object is encoumtered it is pushed onto the
                    // refs stack so that we can determine if we have visited
                    // this object already.
                    if (refs == null)
                        refs = new Dictionary(true);

                    // Check to be sure we haven't processed this object before
                    var id:Object = refs[value];
                    if (id != null)
                    {
                        str += "#" + int(id);
                        return str;
                    }
                    
                    if (value != null)
                    {
                        str += "#" + refCount.toString();
                        refs[value] = refCount;
                        refCount++;
                    }

                    var isArray:Boolean = value is Array;
                    var isDict:Boolean = value is Dictionary;
                    var prop:*;
                    indent += 2;
                    
                    // Print all of the variable values.
                    for (var j:int = 0; j < properties.length; j++)
                    {
                        str = newline(str, indent);
                        prop = properties[j];
                        
                        if (isArray)
                            str += "[";
                        else if (isDict)
                            str += "{";

                    
                        if (isDict)
                        {
                            // in dictionaries, recurse on the key, because it can be a complex object
                            str += internalToString(prop, indent, refs,
                                                    namespaceURIs, exclude);
                        }
                        else
                        {
                            str += prop.toString();
                        }
                        
                        if (isArray)
                            str += "] ";
                        else if (isDict)
                            str += "} = ";
                        else
                            str += " = ";
                        
                        try
                        {
                            // print the value
                            str += internalToString(value[prop], indent, refs,
                                                    namespaceURIs, exclude);
                        }
                        catch(e:Error)
                        {
                            // value[prop] can cause an RTE
                            // for certain properties of certain objects.
                            // For example, accessing the properties
                            //   actionScriptVersion
                            //   childAllowsParent
                            //   frameRate
                            //   height
                            //   loader
                            //   parentAllowsChild
                            //   sameDomain
                            //   swfVersion
                            //   width
                            // of a Stage's loaderInfo causes
                            //   Error #2099: The loading object is not
                            //   sufficiently loaded to provide this information
                            // In this case, we simply output ? for the value.
                            str += "?";
                        }
                    }
                    indent -= 2;
                    return str;
                }
                break;
            }

            case "xml":
            {
                return value.toString();
            }

            default:
            {
                return "(" + type + ")";
            }
        }
        
        return "(unknown)";
    }

    /**
     *  @private
     *  This method will append a newline and the specified number of spaces
     *  to the given string.
     */
    private static function newline(str:String, n:int = 0):String
    {
        var result:String = str;
        result += "\n";
        
        for (var i:int = 0; i < n; i++)
        {
            result += " ";
        }
        return result;
    }
    
    private static function internalCompare(a:Object, b:Object,
                                            currentDepth:int, desiredDepth:int,
                                            refs:Dictionary):int
    {
        if (a == null && b == null)
            return 0;
    
        if (a == null)
            return 1;
    
        if (b == null)
            return -1;
           
        if (a is ObjectProxy)
            a = ObjectProxy(a).object_proxy::object;
            
        if (b is ObjectProxy)
            b = ObjectProxy(b).object_proxy::object;
            
        var typeOfA:String = typeof(a);
        var typeOfB:String = typeof(b);
        
        var result:int = 0;
        
        if (typeOfA == typeOfB)
        {
            switch(typeOfA)
            {
                case "boolean":
                {
                    result = numericCompare(Number(a), Number(b));
                    break;
                }
                
                case "number":
                {
                    result = numericCompare(a as Number, b as Number);
                    break;
                }
                
                case "string":
                {
                    result = stringCompare(a as String, b as String);
                    break;
                }
                
                case "object":
                {
                    var newDepth:int = desiredDepth > 0 ? desiredDepth -1 : desiredDepth;
                    // refs help us avoid circular reference infinite recursion.
                    // Each time an object is encoumtered it is pushed onto the
                    // refs stack so that we can determine if we have visited
                    // this object already.
                    // Here since we are comparing two objects we can short
                    // circuit at the first encounter but have to exhaust all
                    // references found.  A visited reference makes an object 
                    // "greater" than another object, only if both objects
                    // have a visited reference will the result be 0
                    var aRef:Boolean = refs[a];
                    var bRef:Boolean = refs[b];
                    
                    if (aRef && !bRef)
                        return 1;
                    else if (bRef && !aRef)
                        return -1;
                    else if (bRef && aRef)
                        return 0;
                    
                    refs[a] = true;
                    refs[b] = true;
                    
                    if (desiredDepth != -1 && (currentDepth > desiredDepth))
                    {
                        // once we try to go beyond the desired depth we should 
                        // toString() our way out
                        result = stringCompare(a.toString(), b.toString());
                    }
                    else if ((a is Array) && (b is Array))
                    {
                        result = arrayCompare(a as Array, b as Array, currentDepth, desiredDepth, refs);
                    }
                    else if ((a is Date) && (b is Date))
                    {
                        result = dateCompare(a as Date, b as Date);
                    }
                    else if ((a is IList) && (b is IList))
                    {
                        result = listCompare(a as IList, b as IList, currentDepth, desiredDepth, refs);
                    }
                    else if ((a is ByteArray) && (b is ByteArray))
                    {
                        result = byteArrayCompare(a as ByteArray, b as ByteArray);
                    }
                    else if (getQualifiedClassName(a) == getQualifiedClassName(b))
                    {
                        var aProps:Array = getClassInfo(a).properties;
                        var bProps:Array;
                        
                        // if the objects are anonymous they could have different 
                        // # of properties and should be treated on that basis first
                        if (getQualifiedClassName(a) == "Object")
                        {
                            bProps = getClassInfo(b).properties;
                            result = arrayCompare(aProps, bProps, currentDepth, newDepth, refs);
                        }
                        
                        if (result != 0)
                        {
                            return result;
                        }
                        
                        // loop through all of the properties and compare
                        var propName:QName;
                        var aProp:Object;
                        var bProp:Object;
                        for (var i:int = 0; i < aProps.length; i++)
                        {
                            propName = aProps[i];
                            aProp = a[propName];
                            bProp = b[propName];
                            result = internalCompare(aProp, bProp, currentDepth+1, newDepth, refs);
                            if (result != 0)
                            {
                                i = aProps.length;
                            }
                        }
                    }
                    else
                    {
                        // We must be inequal, so return 1
                        return 1;
                    }
                    break;
                }
            }
        }
        else // be consistent with the order we return here
        {
            return stringCompare(typeOfA, typeOfB);
        }

        return result;
    }
    
    /**
     *  Returns information about the class, and properties of the class, for
     *  the specified Object.
     *
     *  @param obj The Object to inspect.
     *
     *  @param exclude Array of Strings specifying the property names that should be 
     *  excluded from the returned result. For example, you could specify 
     *  <code>["currentTarget", "target"]</code> for an Event object since these properties 
     *  can cause the returned result to become large.
     *
     *  @param options An Object containing one or more properties 
     *  that control the information returned by this method. 
     *  The properties include the following:
     *
     *  <ul>
     *    <li><code>includeReadOnly</code>: If <code>false</code>, 
     *      exclude Object properties that are read-only. 
     *      The default value is <code>true</code>.</li>
     *  <li><code>includeTransient</code>: If <code>false</code>, 
     *      exclude Object properties and variables that have <code>[Transient]</code> metadata.
     *      The default value is <code>true</code>.</li>
     *  <li><code>uris</code>: Array of Strings of all namespaces that should be included in the output.
     *      It does allow for a wildcard of "~~". 
     *      By default, it is null, meaning no namespaces should be included. 
     *      For example, you could specify <code>["mx_internal", "mx_object"]</code> 
     *      or <code>["~~"]</code>.</li>
     *  </ul>
     * 
     *  @return An Object containing the following properties:
     *  <ul>
     *    <li><code>name</code>: String containing the name of the class;</li>
     *    <li><code>properties</code>: Sorted list of the property names of the specified object,
     *    or references to the original key if the specified object is a Dictionary</li>
     *  </ul>
    */
    public static function getClassInfo(obj:Object,
                                        excludes:Array = null,
                                        options:Object = null):Object
    {   
        var n:int;
        var i:int;

        if (obj is ObjectProxy)
            obj = ObjectProxy(obj).object_proxy::object;

        if (options == null)
            options = { includeReadOnly: true, uris: null, includeTransient: true };

        var result:Object;
        var propertyNames:Array = [];
        var cacheKey:String;

        var className:String;
        var classAlias:String;
        var properties:XMLList;
        var prop:XML;
        var dynamic:Boolean = false;
        var metadataInfo:Object;

        if (typeof(obj) == "xml")
        {
            className = "XML";
            properties = obj.text();
            if (properties.length())
                propertyNames.push("*");
            properties = obj.attributes();
        }
        else
        {
            var classInfo:XML = DescribeTypeCache.describeType(obj).typeDescription;
            className = classInfo.@name.toString();
            classAlias = classInfo.@alias.toString();
            dynamic = (classInfo.@isDynamic.toString() == "true");

            if (options.includeReadOnly)
                properties = classInfo..accessor.(@access != "writeonly") + classInfo..variable;
            else
                properties = classInfo..accessor.(@access == "readwrite") + classInfo..variable;

            var numericIndex:Boolean = false;
        }

        // If type is not dynamic, check our cache for class info...
        if (!dynamic)
        {
            cacheKey = getCacheKey(obj, excludes, options);
            result = CLASS_INFO_CACHE[cacheKey];
            if (result != null)
                return result;
        }

        result = {};
        result["name"] = className;
        result["alias"] = classAlias;
        result["properties"] = propertyNames;
        result["dynamic"] = dynamic;
        result["metadata"] = metadataInfo = recordMetadata(properties);
        
        var excludeObject:Object = {};
        if (excludes)
        {
            n = excludes.length;
            for (i = 0; i < n; i++)
            {
                excludeObject[excludes[i]] = 1;
            }
        }

        //TODO this seems slightly fragile, why not use the 'is' operator?
        var isArray:Boolean = (className == "Array");
        var isDict:Boolean  = (className == "flash.utils::Dictionary");
        
        if (isDict)
        {
            // dictionaries can have multiple keys of the same type,
            // (they can index by reference rather than QName, String, or number),
            // which cannot be looked up by QName, so use references to the actual key
            for (var key:* in obj)
            {
                propertyNames.push(key);
            }
        }
        else if (dynamic)
        {
            for (var p:String in obj)
            {
                if (excludeObject[p] != 1)
                {
                    if (isArray)
                    {
                         var pi:Number = parseInt(p);
                         if (isNaN(pi))
                            propertyNames.push(new QName("", p));
                         else
                            propertyNames.push(pi);
                    }
                    else
                    {
                        propertyNames.push(new QName("", p));
                    }
                }
            }
            numericIndex = isArray && !isNaN(Number(p));
        }

        if (isArray || isDict || className == "Object")
        {
            // Do nothing since we've already got the dynamic members
        }
        else if (className == "XML")
        {
            n = properties.length();
            for (i = 0; i < n; i++)
            {
                p = properties[i].name();
                if (excludeObject[p] != 1)
                    propertyNames.push(new QName("", "@" + p));
            }
        }
        else
        {
            n = properties.length();
            var uris:Array = options.uris;
            var uri:String;
            var qName:QName;
            for (i = 0; i < n; i++)
            {
                prop = properties[i];
                p = prop.@name.toString();
                uri = prop.@uri.toString();
                
                if (excludeObject[p] == 1)
                    continue;
                    
                if (!options.includeTransient && internalHasMetadata(metadataInfo, p, "Transient"))
                    continue;
                
                if (uris != null)
                {
                    if (uris.length == 1 && uris[0] == "*")
                    {   
                        qName = new QName(uri, p);
                        try
                        {
                            obj[qName]; // access the property to ensure it is supported
                            propertyNames.push();
                        }
                        catch(e:Error)
                        {
                            // don't keep property name 
                        }
                    }
                    else
                    {
                        for (var j:int = 0; j < uris.length; j++)
                        {
                            uri = uris[j];
                            if (prop.@uri.toString() == uri)
                            {
                                qName = new QName(uri, p);
                                try
                                {
                                    obj[qName];
                                    propertyNames.push(qName);
                                }
                                catch(e:Error)
                                {
                                    // don't keep property name 
                                }
                            }
                        }
                    }
                }
                else if (uri.length == 0)
                {
                    qName = new QName(uri, p);
                    try
                    {
                        obj[qName];
                        propertyNames.push(qName);
                    }
                    catch(e:Error)
                    {
                        // don't keep property name 
                    }
                }
            }
        }

        propertyNames.sort(Array.CASEINSENSITIVE |
                           (numericIndex ? Array.NUMERIC : 0));

        // dictionary keys can be indexed by an object reference
        // there's a possibility that two keys will have the same toString()
        // so we don't want to remove dupes
        if (!isDict)
        {
            // for Arrays, etc., on the other hand...
            // remove any duplicates, i.e. any items that can't be distingushed by toString()
            for (i = 0; i < propertyNames.length - 1; i++)
            {
                // the list is sorted so any duplicates should be adjacent
                // two properties are only equal if both the uri and local name are identical
                if (propertyNames[i].toString() == propertyNames[i + 1].toString())
                {
                    propertyNames.splice(i, 1);
                    i--; // back up
                }
            }
        }

        // For normal, non-dynamic classes we cache the class info
        if (!dynamic)
        {
            cacheKey = getCacheKey(obj, excludes, options);
            CLASS_INFO_CACHE[cacheKey] = result;
        }

        return result;
    }

    /**
     * Uses <code>getClassInfo</code> and examines the metadata information to
     * determine whether a property on a given object has the specified 
     * metadata.
     * 
     * @param obj The object holding the property.
     * @param propName The property to check for metadata.
     * @param metadataName The name of the metadata to check on the property.
     * @param excludes If any properties need to be excluded when generating class info. (Optional)
     * @param options If any options flags need to changed when generating class info. (Optional)
     * @return true if the property has the specified metadata.
     */
    public static function hasMetadata(obj:Object, 
                propName:String, 
                metadataName:String, 
                excludes:Array = null,
                options:Object = null):Boolean
    {
        var classInfo:Object = getClassInfo(obj, excludes, options);
        var metadataInfo:Object = classInfo["metadata"];
        return internalHasMetadata(metadataInfo, propName, metadataName);
    }

    /**
     *  @private
     */
    private static function internalHasMetadata(metadataInfo:Object, propName:String, metadataName:String):Boolean
    {
        if (metadataInfo != null)
        {
            var metadata:Object = metadataInfo[propName];
            if (metadata != null)
            {
                if (metadata[metadataName] != null)
                    return true;
            }
        }
        return false;
    }

    /**
     *  @private
     */
    private static function recordMetadata(properties:XMLList):Object
    {
        var result:Object = null;

        try
        {
            for each (var prop:XML in properties)
            {
                var propName:String = prop.attribute("name").toString();
                var metadataList:XMLList = prop.metadata;

                if (metadataList.length() > 0)
                {
                    if (result == null)
                        result = {};

                    var metadata:Object = {};
                    result[propName] = metadata;

                    for each (var md:XML in metadataList)
                    {
                        var mdName:String = md.attribute("name").toString();
                        
                        var argsList:XMLList = md.arg;
                        var value:Object = {};

                        for each (var arg:XML in argsList)
                        {
                            var argKey:String = arg.attribute("key").toString();
                            if (argKey != null)
                            {
                                var argValue:String = arg.attribute("value").toString();
                                value[argKey] = argValue;
                            }
                        }

                        var existing:Object = metadata[mdName];
                        if (existing != null)
                        {
                            var existingArray:Array;
                            if (existing is Array)
                                existingArray = existing as Array;
                            else
                                existingArray = [];
                            existingArray.push(value);
                            existing = existingArray;
                        }
                        else
                        {
                            existing = value;
                        }
                        metadata[mdName] = existing;
                    }
                }
            }
        }
        catch(e:Error)
        {
        }
        
        return result;
    }


    /**
     *  @private
     */
    private static function getCacheKey(o:Object, excludes:Array = null, options:Object = null):String
    {
        var key:String = getQualifiedClassName(o);

        if (excludes != null)
        {
            for (var i:uint = 0; i < excludes.length; i++)
            {
                var excl:String = excludes[i] as String;
                if (excl != null)
                    key += excl;
            }
        }

        if (options != null)
        {
            for (var flag:String in options)
            {
                key += flag;
                var value:String = options[flag] as String;
                if (value != null)
                    key += value;
            }
        }
        return key;
    }

    /**
     *  @private
     */
    private static function arrayCompare(a:Array, b:Array,
                                         currentDepth:int, desiredDepth:int,
                                         refs:Dictionary):int
    {
        var result:int = 0;

        if (a.length != b.length)
        {
            if (a.length < b.length)
                result = -1;
            else
                result = 1;
        }
        else
        {
            var key:Object;
            for (key in a)
            {
                if (b.hasOwnProperty(key))
                {
                    result = internalCompare(a[key], b[key], currentDepth,
                                         desiredDepth, refs);

                    if (result != 0)
                        return result;
                }
                else
                {
                    return -1;
                }
            }

            for (key in b)
            {
                if (!a.hasOwnProperty(key))
                {
                    return 1;
                }
            }
        }

        return result;
    }
    
    /**
     * @private
     */
    private static function byteArrayCompare(a:ByteArray, b:ByteArray):int
    {
        var result:int = 0;
        if (a.length != b.length)
        {
            if (a.length < b.length)
                result = -1;
            else
                result = 1;
        }
        else
        {
            a.position = 0;
            b.position = 0;
            for (var i:int = 0; i < a.length; i++)
            {
                result = numericCompare(a.readByte(), b.readByte());
                if (result != 0)
                {
                    i = a.length;
                }
            }
        }
        return result;
    }

    /**
     *  @private
     */
    private static function listCompare(a:IList, b:IList, currentDepth:int, 
                                        desiredDepth:int, refs:Dictionary):int
    {
        var result:int = 0;

        if (a.length != b.length)
        {
            if (a.length < b.length)
                result = -1;
            else
                result = 1;
        }
        else
        {
            for (var i:int = 0; i < a.length; i++)
            {
                result = internalCompare(a.getItemAt(i), b.getItemAt(i), 
                                         currentDepth+1, desiredDepth, refs);
                if (result != 0)
                {
                    i = a.length;
                }
            }
        }

        return result;
    }
    
    /**
     * @private
     */
    private static var refCount:int = 0;

    /**
     * @private
     */ 
    private static var CLASS_INFO_CACHE:Object = {};
}

}