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

/**
 *  The StringUtil utility class is an all-static class with methods for
 *  working with String objects within Flex.
 *  You do not create instances of StringUtil;
 *  instead you call methods such as 
 *  the <code>StringUtil.substitute()</code> method.  
 */
public class StringUtil
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Removes all whitespace characters from the beginning and end
     *  of the specified string.
     *
     *  @param str The String whose whitespace should be trimmed. 
     *
     *  @return Updated String where whitespace was removed from the 
     *  beginning and end. 
     */
    public static function trim(str:String):String
    {
        if (str == null) return '';
        
        var startIndex:int = 0;
        while (isWhitespace(str.charAt(startIndex)))
            ++startIndex;

        var endIndex:int = str.length - 1;
        while (isWhitespace(str.charAt(endIndex)))
            --endIndex;

        if (endIndex >= startIndex)
            return str.slice(startIndex, endIndex + 1);
        else
            return "";
    }
    
    /**
     *  Removes all whitespace characters from the beginning and end
     *  of each element in an Array, where the Array is stored as a String. 
     *
     *  @param value The String whose whitespace should be trimmed. 
     *
     *  @param separator The String that delimits each Array element in the string.
     *
     *  @return Updated String where whitespace was removed from the 
     *  beginning and end of each element. 
     */
    public static function trimArrayElements(value:String, delimiter:String):String
    {
        if (value != "" && value != null)
        {
            var items:Array = value.split(delimiter);
            
            var len:int = items.length;
            for (var i:int = 0; i < len; i++)
            {
                items[i] = StringUtil.trim(items[i]);
            }
            
            if (len > 0)
            {
                value = items.join(delimiter);
            }
        }
        
        return value;
    }

    /**
     *  Returns <code>true</code> if the specified string is
     *  a single space, tab, carriage return, newline, or formfeed character.
     *
     *  @param str The String that is is being queried. 
     *
     *  @return <code>true</code> if the specified string is
     *  a single space, tab, carriage return, newline, or formfeed character.
     */
    public static function isWhitespace(character:String):Boolean
    {
        switch (character)
        {
            case " ":
            case "\t":
            case "\r":
            case "\n":
            case "\f":
                return true;

            default:
                return false;
        }
    }

    /**
     *  Substitutes "{n}" tokens within the specified string
     *  with the respective arguments passed in.
     *
     *  @param str The string to make substitutions in.
     *  This string can contain special tokens of the form
     *  <code>{n}</code>, where <code>n</code> is a zero based index,
     *  that will be replaced with the additional parameters
     *  found at that index if specified.
     *
     *  @param rest Additional parameters that can be substituted
     *  in the <code>str</code> parameter at each <code>{n}</code>
     *  location, where <code>n</code> is an integer (zero based)
     *  index value into the array of values specified.
     *  If the first parameter is an array this array will be used as
     *  a parameter list.
     *  This allows reuse of this routine in other methods that want to
     *  use the ... rest signature.
     *  For example <pre>
     *     public function myTracer(str:String, ... rest):void
     *     { 
     *         label.text += StringUtil.substitute(str, rest) + "\n";
     *     } </pre>
     *
     *  @return New string with all of the <code>{n}</code> tokens
     *  replaced with the respective arguments specified.
     *
     *  @example
     *
     *  var str:String = "here is some info '{0}' and {1}";
     *  trace(StringUtil.substitute(str, 15.4, true));
     *
     *  // this will output the following string:
     *  // "here is some info '15.4' and true"
     */
    public static function substitute(str:String, ... rest):String
    {
        if (str == null) return '';
        
        // Replace all of the parameters in the msg string.
        var len:uint = rest.length;
        var args:Array;
        if (len == 1 && rest[0] is Array)
        {
            args = rest[0] as Array;
            len = args.length;
        }
        else
        {
            args = rest;
        }
        
        for (var i:int = 0; i < len; i++)
        {
            str = str.replace(new RegExp("\\{"+i+"\\}", "g"), args[i]);
        }

        return str;
    }
}

}
