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

import flash.utils.ByteArray;
import flash.utils.Dictionary;

import mx.utils.Base64Encoder;
import mx.utils.Base64Decoder;
import mx.utils.HexEncoder;
import mx.utils.HexDecoder;
import mx.utils.StringUtil;

[ExcludeClass]

/**
 * FIXME: Derivations and restrictions need to be considered in addition
 * to base schema types
 * 
 * @private
 */
public class SchemaMarshaller //implements IXMLTypeMarshaller
{
    public function SchemaMarshaller(constants:SchemaConstants, datatypes:SchemaDatatypes)
    {
        super();
        this.constants = constants;
        this.datatypes = datatypes;

        registerMarshallers();
    }


    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    /**
     * Determines whether this marshaller will throw errors for input that
     * violates the specified format or restrictions for the associated type.
     * Type errors are still thrown for unexpected input types.
     */
    public function get validating():Boolean
    {
        return _validating;
    }

    public function set validating(value:Boolean):void
    {
        _validating = value;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * This function converts an ActionScript value to a String for XML
     * simple content based on a built-in XML Schema type. If a type is not
     * provided, the <code>anyType</code> is assumed.
     */
    public function marshall(value:*, type:QName = null, restriction:XML = null):*
    {
        if (type == null)
            type = datatypes.anyTypeQName;

        var marshaller:Function = marshallers[type.localName];
        var result:*;

        if (marshaller != null)
            result = marshaller(value, type, restriction);
        else
            throw new TypeError("Cannot marshall type '" + type + "' to simple content.");

        return result;
    }

    /**
     * This function converts XML simple content (formatted based on a built-in
     * XML Schema type) to an ActionScript value. If a type is not provided, 
     * the <code>anyType</code> is assumed.
     */
    public function unmarshall(value:*, type:QName = null, restriction:XML = null):*
    {
        // First, get the simple content as a String
        var rawValue:String;

        if (value is XML)
        {
            var xml:XML = value as XML;
            if (xml.nodeKind() == "element")
            {
                var nilAttribute:String = xml.attribute(constants.nilQName).toString();
                if (nilAttribute == "true")
                    rawValue = null;
                else
                    rawValue = xml.text().toString();
            }
            else
            {
                rawValue = xml.toString();
            }
        }
        else if (value is XMLList)
        {
            var list:XMLList = value as XMLList;
            rawValue = list.text().toString();
        }
        else if (value != null)
        {
            rawValue = value.toString();
        }

        if (type == null)
            type = datatypes.anyTypeQName;

        var unmarshaller:Function = unmarshallers[type.localName];
        if (unmarshaller != null)
            var value:* = unmarshaller(rawValue, type, restriction);
        else
            throw new TypeError("Cannot unmarshall type '" + type + "' from XML.");

        return value;
    }

    /**
     * In the case of XML Schema ur-types such as <code>anyType</code> and
     * <code>anySimpleType</code> we try to guess what the equivalent XML Schema
     * simple datatype should be based on the ActionScript type. As a last 
     * resort, the <code>string</code> datatype is used.
     */
    public function marshallAny(value:*, type:QName = null, restriction:XML = null):*
    {
        if (value === undefined)
            return undefined;
        else if (value == null)
            return null;

        var localName:String = guessSimpleType(value);

        if (type != null)
            type = new QName(type.uri, localName);
        else
            type = new QName(constants.xsdURI, localName);

        var marshaller:Function = marshallers[type.localName];
        if (marshaller != null)
        {
            return marshaller(value, type);
        }
        else
        {
            throw new TypeError("Cannot marshall type '" + type + "' to simple content.");
        }
    }

    public function marshallBase64Binary(value:*, type:QName = null, restriction:XML = null):String
    {
        var ba:ByteArray = value as ByteArray;
        var result:String;
        if (ba != null)
        {
            var encoder:Base64Encoder = new Base64Encoder();
            encoder.insertNewLines = false;
            encoder.encodeBytes(ba);
            result = encoder.drain();
        }
        else
        {
            return null;
        }
        return result;
    }

    /**
     * The boolean schema type allows the string values 'true' or
     * '1' for true values and 'false' or '0' for false values. This
     * marshaller, by default, represents values using 'true' or false.
     * If a String value of '1' or '0' is passed, however, it is preserved.
     */
    public function marshallBoolean(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;

        if (value != null)
        {
            if (value is Boolean)
            {
                result = (value as Boolean) ? "true" : "false";
            }
            else if (value is Number)
            {
                result = (value == 1) ? "true" : "false";
            }
            else if (value is Object)
            {
                var s:String = Object(value).toString();
                if (s == "true" || s == "false" || s == "1" || s == "0")
                    result = s;
                else
                    throw new Error("String '" + value + "' is not a Boolean."); 
            }
        }

        return result;
    }

    public function marshallDatetime(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;
        var date:Date;

        if (value is Number)
        {
            date = new Date();
            date.time = value as Number;
            value = date;
        }

        if (value is Date)
        {
            var n:Number;
            date = value as Date;
            result = "";

            if (type == datatypes.dateTimeQName
                || type == datatypes.dateQName)
            {
                // Year
                result = result.concat(date.getUTCFullYear(), "-");

                // Month
                n = date.getUTCMonth() + 1; // ActionScript UTC month starts from 0
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n, "-");

                // Day
                n = date.getUTCDate();
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n);
            }

            if (type == datatypes.dateTimeQName)
            {
                result = result.concat("T");
            }

            // Time
            if (type == datatypes.dateTimeQName
                || type == datatypes.timeQName)
            {
                n = date.getUTCHours();
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n, ":");
    
                n = date.getUTCMinutes();
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n, ":");
    
                n = date.getUTCSeconds();
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n);

                // Milliseconds are optional so we write them if > 0
                n = date.getUTCMilliseconds();
                if (n > 0)
                {
                    result = result.concat(".");
                    if (n < 10)
                        result = result.concat("00");
                    else if (n < 100)
                        result = result.concat("0");
                    result = result.concat(n);
                }
            }

            // Always send dates, times and dateTimes in UTC from the player.
            result = result.concat("Z");
        }
        else if (value is String)
        {
            result = value as String;
        }

        return result;
    }

    /**
     * FIXME: Handle precision and exponent restrictions.
     */
    public function marshallDecimal(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;
        var number:Number;
        if (value is Number)
        {
            number = value as Number;
        }
        else
        {
            var str:String = whitespaceCollapse(value);
            number = Number(str);
        }

        // Check for Infinity, -Infinity and NaN as these are not valid 
        // values for the XML Schema decimal type.
        result = specialNumber(number);

        if (result != null)
        {
            if (validating)
            {
                throw new Error("Invalid decimal value '" + value + "'.");
            }
            else
            {
                // As a last resort, we just go with the original input
                result = whitespaceCollapse(value);
            }
        }
        else
        {
            // We have a normal number
            result = number.toString();
        }
        return result;
    }

    /**
     * FIXME: Handle precision and exponent restrictions.
     */
    public function marshallDouble(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;
        var number:Number = Number(value);

        // Check for Infinity, -Infinity and NaN
        result = specialNumber(number);

        if (result == null)
            result = number.toString();

        return result;
    }

    public function marshallDuration(value:*, type:QName = null, restriction:XML = null):String
    {
        return whitespaceCollapse(value);
    }

    /**
     * FIXME: Handle precision and exponent restrictions.
     */
    public function marshallFloat(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;

        // First, check that we have a suitable input for Number
        var number:Number;
        if (value is Number)
        {
            number = value as Number;
        }
        else
        {
            var str:String = whitespaceCollapse(value);
            number = Number(str);
        }

        // Check for Infinity, -Infinity and NaN
        result = specialNumber(number);

        if (result == null)
        {
            if (validating)
            {
                if (number > 0)
                {
                    if (number > FLOAT_MAX_VALUE)
                        throw new RangeError("The value '" + value + "' is too large for a single-precision floating point value.");
                    else if (number < FLOAT_MIN_VALUE)
                        throw new RangeError("The value '" + value + "' is too small for a single-precision floating point value.");
                }
                else
                {
                    if (-number > FLOAT_MAX_VALUE)
                        throw new RangeError("The absolute value of '" + value + "' is too large for a single-precision floating point value.");
                    else if (-number < FLOAT_MIN_VALUE)
                        throw new RangeError("The absolute value of '" + value + "' is too small for a single-precision floating point value.");
                }
            }
            result = number.toString();                
        }

        return result;
    }

    public function marshallGregorian(value:*, type:QName = null, restriction:XML = null):String
    {
        var date:Date;
        var n:Number;

        if (value is Date)
            date = value as Date;
        else if (value is Number)
            n = value as Number;
        else
            value = whitespaceCollapse(value);

        var result:String = "";
        var hyphen:int;

        // Year
        if (type == datatypes.gYearMonthQName
            || type == datatypes.gYearQName)
        {
            var year:String;

            if (value is Date)
            {
                n = date.getUTCFullYear();
            }
            else if (value is String)
            {
                year = value as String;
                // We may have CCYY, -CCYY, CCYY-MM or -CCYY-MM
                hyphen = year.indexOf("-", 1);
                if (hyphen > 0)
                    year = year.substring(0, hyphen); 
                n = parseInt(year);
            }

            // Check for NaN or 0000 as these are not valid years.
            if (isNaN(n) || n == 0)
            {
                if (validating)
                {
                    throw new Error("Invalid year supplied for type " + type.localName + " in value " + value);
                }
                else
                {
                    // Abort trying to process this value
                    return whitespaceCollapse(value);
                }
            }
            else
            {
                // Get a String representation of the year, though this value
                // is potentially unbounded so we use Number.toFixed instead
                // of int().
                year = n.toFixed(0);

                // Pad early years with leading 0s up to 000x but
                // we keep in mind that negative years such as -894 are valid.
                if (year.indexOf("-") == 0)
                {
                    while (year.length < 5)
                        year = year.substring(0, 1) + "0" + year.substring(1);
                }
                else
                {
                    while (year.length < 4)
                        year = "0" + year;
                }
                result = result.concat(year);
            }

            if (type != datatypes.gYearQName)
                result = result.concat("-");
        }

        // Month
        if (type == datatypes.gYearMonthQName
            || type == datatypes.gMonthDayQName
            || type == datatypes.gMonthQName)
        {
            if (type != datatypes.gYearMonthQName)
                result = result.concat("--");

            var month:String;

            if (value is Date)
                n = date.getUTCMonth() + 1; // ActionScript UTC month starts at 0
            else
            {
                month = value.toString();
                if (type == datatypes.gMonthDayQName)
                {
                    // We must have --MM-DD
                    hyphen = month.indexOf("--", 0);
                    if (hyphen == 0)
                        month = month.substring(2, 4);
                    else if (validating)
                        throw new Error("Invalid month supplied for " + type.localName + " in value " + value + ". The format must be '--MM-DD'.");
                    else
                    {
                        // Abort trying to process this value
                        return whitespaceCollapse(value);
                    }
                }
                else if (type == datatypes.gYearMonthQName)
                {
                    hyphen = month.indexOf("-", 1);
                    // We must have CCYY-MM or -CCYY-MM
                    if (hyphen > 0)
                        month = month.substring(hyphen + 1, hyphen + 3);
                    else if (validating)
                        throw new Error("Invalid month supplied for " + type.localName + " in value " + value + ". The format must be '--CCYY-MM'.");
                    else
                    {
                        // Abort trying to process this value
                        return whitespaceCollapse(value);
                    }
                }
                else
                {
                    // We may have --MM-- (but we allow just MM too)
                    hyphen = month.indexOf("--", 0);
                    if (hyphen > 0)
                        month = month.substring(2, 4);
                }
                n = parseInt(month);
            }

            // Check for NaN or values not in the range of 1 to 12
            // as these are not valid months
            if (isNaN(n) || n <= 0 || n > 12)
            {
                if (validating)
                    throw new Error("Invalid month supplied for type " + type.localName + " in value " + value);
                else
                {
                    // Abort trying to process this value
                    return whitespaceCollapse(value);
                }
            }
            else
            {
                n = int(n);
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n);
            }

            if (type == datatypes.gMonthDayQName)
            {
                result = result.concat("-");
            }
        }

        // Day          
        if (type == datatypes.gMonthDayQName
            || type == datatypes.gDayQName)
        {
            if (type == datatypes.gDayQName)
                result = result.concat("---");

            if (value is Date)
                n = date.getUTCDate();
            else if (value is String)
            {
                var day:String = value as String;
                if (type == datatypes.gMonthDayQName)
                {
                    //We must have --MM-DD
                    hyphen = day.indexOf("--", 0);
                    if (hyphen == 0)
                        day = day.substring(5, 7);
                    else if (validating)
                        throw new Error("Invalid day supplied for gMonthDay in value " + value + ". The format must be '--MM-DD'.");
                    else
                    {
                        // Abort trying to process this value
                        return whitespaceCollapse(value);
                    }
                }
                else
                {
                    //We might have ---DD (but we allow just DD too)
                    hyphen = day.indexOf("---", 0);
                    if (hyphen == 0)
                        day = day.substring(3, 5);
                }

                n = parseInt(day);
            }

            // Check for NaN or values not in the range of 1 to 31
            // as these are not valid days
            if (isNaN(n) || n <= 0 || n > 31)
            {
                if (validating)
                    throw new Error("Invalid day supplied for type " + type.localName + " in value " + value);
                else
                    // Abort trying to process this value
                    return whitespaceCollapse(value);
            }
            else
            {
                n = int(n);
                if (n < 10)
                    result = result.concat("0");
                result = result.concat(n);
            }
        }

        return result;
    }

    /**
     * The schema type hexBinary represents arbitrary hex-encoded binary data.
     * Each binary octet is encoded as a character tuple consisting of two
     * hexadecimal digits (which is treated case insensitively although
     * capital letters A-F are always used on encoding). These tuples are
     * added to a String to serialize the binary data.
     */
    public function marshallHexBinary(value:*, type:QName = null, restriction:XML = null):String
    {
        var ba:ByteArray = value as ByteArray;
        var valueString:String;
        if (ba != null)
        {
            var encoder:HexEncoder = new HexEncoder();
            encoder.encode(ba);
            valueString = encoder.drain();
        }   
        else
        {
            throw new Error("Cannot marshall value as hex binary.");
        }
        return valueString;
    }

    /**
     * The schema type integer is dervied from the decimal type via restrictions
     * by fixing the value of fractionDigits to be 0 and disallowing the
     * trailing decimal point. The schema types long, int, short, byte are
     * derived from integer by restricting the maxInclusive and minInclusive
     * properties. Other types such as nonPositiveInteger, negativeInteger,
     * nonNegativeInteger, positiveInteger, unsignedLong, unsignedInt,
     * unsignedShort and unsignedByte are also dervied from integer through
     * similar restrictions.
     * 
     * This method first calls parses the <code>value</code> as a Number. It
     * then uses <code>Math.floor()</code> on the number to remove any fraction
     * digits and then checks that the result is within the specified
     * <code>min</code> and <code>max</code> for the type. Note that decimal
     * values are not rounded. This method handles integers longer than 32-bit
     * so ActionScript int or uint types are not used internally.
     */
    public function marshallInteger(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;

        var number:Number;
        if (value is Number)
            number = value as Number
        else
            number = Number(whitespaceCollapse(value));

        var min:Number = -Number.MAX_VALUE;
        var max:Number = Number.MAX_VALUE;

        if (type == datatypes.longQName)
        {
            min = LONG_MIN_VALUE
            max = LONG_MAX_VALUE;
        }
        else if (type == datatypes.intQName)
        {
            min = int.MIN_VALUE;
            max = int.MAX_VALUE;
        }
        else if (type == datatypes.shortQName)
        {
            min = SHORT_MIN_VALUE
            max = SHORT_MAX_VALUE;
        }
        else if (type == datatypes.byteQName)
        {
            min = BYTE_MIN_VALUE
            max = BYTE_MAX_VALUE;
        }
        else if (type == datatypes.unsignedLongQName)
        {
            min = 0;
            max = ULONG_MAX_VALUE;
        }
        else if (type == datatypes.unsignedIntQName)
        {
            min = 0;
            max = uint.MAX_VALUE;
        }
        else if (type == datatypes.unsignedShortQName)
        {
            min = 0;
            max = USHORT_MAX_VALUE;
        }
        else if (type == datatypes.unsignedByteQName)
        {
            min = 0;
            max = UBYTE_MAX_VALUE;
        }
        else if (type == datatypes.positiveIntegerQName)
        {
            min = 1;
            max = Number.MAX_VALUE;
        }
        else if (type == datatypes.nonNegativeIntegerQName)
        {
            min = 0;
            max = Number.MAX_VALUE;
        }
        else if (type == datatypes.negativeIntegerQName)
        {
            min = -Number.MAX_VALUE;
            max = -1;
        }
        else if (type == datatypes.nonPositiveIntegerQName)
        {
            min = -Number.MAX_VALUE;
            max = 0;
        }
        else if (type == datatypes.integerQName)
        {
            min = -Number.MAX_VALUE;
            max = Number.MAX_VALUE;
        }

        var integer:Number = Math.floor(number);
        if (integer >= min)
        {
            if (integer > max)
            {
                if (validating)
                {
                    throw new RangeError("The value '" + value + "' is too large for " + type.localName);
                }
                else
                {
                    // As a last resort, we just go with the original input
                    return whitespaceCollapse(value);
                }
            }
        }
        else
        {
            if (validating)
            {
                throw new RangeError("The value '" + value + "' is too small for " + type.localName);
            }
            else
            {
                // As a last resort, we just go with the original input
                return whitespaceCollapse(value);
            }
        }

        result = integer.toString();
        return result;
    }

    public function marshallString(value:*, type:QName = null, restriction:XML = null):String
    {
        if (value != null && value is Object)
        {
            return Object(value).toString();
        }

        return null;
    }

    public function unmarshallAny(value:*, type:QName = null, restriction:XML = null):*
    {
        if (value === undefined)
            return undefined;
        else if (value == null)
            return null;

        var result:*;
        var unmarshaller:Function;

        var s:String = whitespaceCollapse(value);

        if (s == "")
        {
            unmarshaller = unmarshallers[datatypes.stringQName.localName];
        }
        else if (isNaN(Number(s))
            || (s.charAt(0) == '0')
            || ((s.charAt(0) == '-') && (s.charAt(1) == '0')) 
            || s.charAt(s.length - 1) == 'E')
        {
            var lowerS:String = s.toLowerCase();
            if (lowerS == "true" || lowerS == "false")
            {
                unmarshaller = unmarshallers[datatypes.booleanQName.localName];
            }
            else
            {
                unmarshaller = unmarshallers[datatypes.stringQName.localName];
            }
        }
        else
        {
            unmarshaller = unmarshallers[datatypes.doubleQName.localName];
        }

        result = unmarshaller(value, type, restriction);
        return result;
    }

    public function unmarshallBase64Binary(value:*, type:QName = null, restriction:XML = null):ByteArray
    {
        if (value == null)
            return null;

        var data:String = whitespaceCollapse(value);
        var decoder:Base64Decoder = new Base64Decoder();
        decoder.decode(data);
        return decoder.drain(); 
    }

    public function unmarshallBoolean(value:*, type:QName = null, restriction:XML = null):Boolean
    {
        if (value == null)
            return false;

        var bool:String = whitespaceCollapse(value).toLowerCase();
        if (bool == "true" || bool == "1")
            return true;

        return false;
    }

    public function unmarshallDate(value:*, type:QName = null, restriction:XML = null):Object
    {
        if (value == null)
            return null;

        var date:Date;
        var index:int;
        var datePart:String = whitespaceCollapse(value);

        if (datePart != null)
        {
            // dateLexicalRep ::= yearFrag '-' monthFrag '-' dayFrag timezoneFrag?
            // yearFrag ::= '-'? (([1-9] digit digit digit+)) | ('0' digit digit digit))
            index = datePart.indexOf("-", 1);
            var year:uint = uint(datePart.substring(0, index++));
            // monthFrag ::= ('0' [1-9]) | ('1' [0-2])
            var month:uint = uint(datePart.substring(index, index + 2));
            index += 3;
            // dayFrag ::= (0 [1-9]) | ([12] digit) | ('3' [01])
            var day:uint = uint(datePart.substring(index, index + 2));
            index += 2;
            // timezoneFrag ::= 'Z' | (('+' | '-') ('0' digit | '1' [0-4]) ':' minuteFrag)
            if (datePart.charAt(index) == "Z") // UTC.
            {
                date = new Date(Date.UTC(year, month - 1, day));
            }
            else if (datePart.length > 10) // UTC offset.
            {     
                // Start at UTC.
                date = new Date(Date.UTC(year, month - 1, day));
                // (('+' | '-') ('0' digit | '1' [0-4]) ':' minuteFrag)                
                var offsetDirection:String = datePart.charAt(index++);
                var hours:int = int(datePart.substring(index, index + 2));
                index += 3;
                // minuteFrag ::= [0-5] digit
                var minutes:int = int(datePart.substring(index, index + 2));
                // Done with parse; apply offset.
                var millis:Number = (hours * 3600000) + (minutes * 60000);
                if (offsetDirection == "+")
                {
                    date.time -= millis;
                }
                else // "-"
                {
                    date.time += millis;
                }
            }
            else // Treat as local time.
            {
                date = new Date(year, month - 1, day);
            }
        }        
        
        return date;
    }

    /**
     * Handles dateTime and time types.
     */
    public function unmarshallDatetime(value:*, type:QName = null, restriction:XML = null):Object
    {
        if (value == null)
            return null;

        var date:Date;
        var rawValue:String = whitespaceCollapse(value);
        var datePart:String;
        var timePart:String;
        var utc:Boolean;
        var offset:Boolean;
        var index:int;        

        var sep:int = rawValue.indexOf("T");
        if (sep != -1)
        {
            datePart = rawValue.substring(0, sep);
            timePart = rawValue.substring(sep + 1);
        }
        else // It's a time (no date part).
        {
            timePart = rawValue;
        }

        // Parse the time first to get the timezone/offset. E.g. 24:00:00.000
        // timeLexicalRep ::= ((hourFrag ':' minuteFrag ':' secondFrag) | endOfDayFrag) timezoneFrag?
        // hourFrag ::= ([01] digit) | ('2' [0-3])
        var hours:int = int(timePart.substring(0, 2));
        // minuteFrag ::= [0-5] digit
        var minutes:int = int(timePart.substring(3, 5));
        // secondFrag ::= ([0-5] digit) ('.' digit+)? 
        var millisStart:int = timePart.indexOf(".", 6);
        var seconds:int = int(timePart.substring(6, 8));
        // Handle millis and timezone next.
        // timezoneFrag ::= 'Z' | (('+' | '-') ('0' digit | '1' [0-4]) ':' minuteFrag)
        var tzIndex:int = timePart.indexOf("Z", 8);
        var offsetDirection:int;
        var offsetMillis:Number;
        if (tzIndex == -1)
        {
            if ((tzIndex = timePart.indexOf("+", 8)) != -1)
                offsetDirection = 1; // Positive.    
            else if ((tzIndex = timePart.indexOf("-", 8)) != -1)
                offsetDirection = -1; // Negative.
                
            if (tzIndex != -1)
            {
                index = tzIndex + 1;
                var offsetHours:int = int(timePart.substring(index, index + 2));
                index += 3;
                var offsetMinutes:int = int(timePart.substring(index, index + 2));
                offsetMillis = (offsetHours * 3600000) + (offsetMinutes * 60000); 
                utc = true;
                offset = true;
            }   
        }
        else
        {
            utc = true;
        }
        var millis:int = 0;
        if (millisStart != -1)
        {
            if (utc)
                millis = int(timePart.substring(millisStart + 1, tzIndex));
            else
                millis = int(timePart.substring(millisStart + 1));
        }
        
        // Now parse the datePart if it exists.
        if (datePart != null)
        {
            // dateLexicalRep ::= yearFrag '-' monthFrag '-' dayFrag timezoneFrag?
            // yearFrag ::= '-'? (([1-9] digit digit digit+)) | ('0' digit digit digit))
            index = datePart.indexOf("-", 1);
            var year:uint = uint(datePart.substring(0, index++));
            // monthFrag ::= ('0' [1-9]) | ('1' [0-2])
            var month:uint = uint(datePart.substring(index, index + 2));
            index += 3;
            // dayFrag ::= (0 [1-9]) | ([12] digit) | ('3' [01])
            var day:uint = uint(datePart.substring(index, index + 2));
                        
            if (utc) 
                date = new Date(Date.UTC(year, month - 1, day));
            else
                date = new Date(year, month - 1, day);
            
        }
        else
        {
            if (utc)
            {
                var now:Date = new Date();
                date = new Date(Date.UTC(now.fullYearUTC, now.monthUTC, now.dateUTC));
            }
            else
            {
                date = new Date();
            }
        }
        // Apply the time part.
        if (utc)
        {
            date.setUTCHours(hours, minutes, seconds, millis);
            if (offset)
            {
                if (offsetDirection > 0)
                    date.time -= offsetMillis;
                else
                    date.time += offsetMillis;
            }
        }
        else
        {
            date.setHours(hours, minutes, seconds, millis);
        }
        
        return date;
    }

    public function unmarshallDecimal(value:*, type:QName = null, restriction:XML = null):Number
    {
        return unmarshallDouble(value, type);
    }

    public function unmarshallDouble(value:*, type:QName = null, restriction:XML = null):Number
    {
        var result:Number;

        if (value != null)
        {
            var s:String = whitespaceCollapse(value);
            if (s == "INF")
            {
                result = Number.POSITIVE_INFINITY;
            }
            else if (s == "-INF")
            {
                result = Number.NEGATIVE_INFINITY;
            }
            else
            {
                result = Number(s);
            }
        }

        return result;
    }

    public function unmarshallDuration(value:*, type:QName = null, restriction:XML = null):*
    {
        return whitespaceCollapse(value);
    }

    public function unmarshallFloat(value:*, type:QName = null, restriction:XML = null):Number
    {
        return unmarshallDouble(value, type, restriction);
    }

    // FIXME: Should we always return String for all gregorian types?
    public function unmarshallGregorian(value:*, type:QName = null, restriction:XML = null):*
    {
        var rawValue:String = whitespaceCollapse(value);
        
        var result:* = rawValue;

        // FIXME: Consider timezone, if provided, for gregorian date info
        if (type == datatypes.gYearQName)
        {
            result = uint(rawValue.substring(0, 4));
        }
        /*
        // FIXME: Should we leave YearMonth and MonthDay as String?
        else if (type == datatypes.gYearMonthQName)
        {
            result = rawValue.substring(0, 7);
        }
        else if (type == datatypes.gMonthDayQName)
        {
            result = rawValue.substring(2, 5);
        }
        */
        else if (type == datatypes.gMonthQName)
        {
            result = uint(rawValue.substring(2, 4));
        }
        else if (type == datatypes.gDayQName)
        {
            result = uint(rawValue.substring(3, 5));
        }

        return result;
    }

    public function unmarshallHexBinary(value:*, type:QName = null, restriction:XML = null):ByteArray
    {
        if (value != null)
        {
            var data:String = whitespaceCollapse(value);
            var decoder:HexDecoder = new HexDecoder();
            decoder.decode(data);
            return decoder.drain();
        }

        return null;
    }

    public function unmarshallInteger(value:*, type:QName = null, restriction:XML = null):Number
    {
        return parseInt(value);
    }

    public function unmarshallString(value:*, type:QName = null, restriction:XML = null):String
    {
        var result:String;
        if (value != null)
        {
            result = value.toString();
        }
        return result;
    }

    public static function guessSimpleType(value:*):*
    {
        var localName:String = "string";
        if (value is uint)
        {
            localName = "unsignedInt";
        }
        else if (value is int)
        {
            localName = "int";
        }
        else if (value is Number)
        {
            localName = "double";
        }
        else if (value is Boolean)
        {
            localName = "boolean";
        }
        else if (value is String)
        {
            localName = "string";
        }
        else if (value is ByteArray)
        {
            if (byteArrayAsBase64Binary)
                localName = "base64Binary";
            else
                localName = "hexBinary";
        }
        else if (value is Date)
        {
            localName = "dateTime";
        }

        return localName;
    }

    private static function specialNumber(value:Number):String
    {
        if (value == Number.NEGATIVE_INFINITY)
            return "-INF";
        else if (value == Number.POSITIVE_INFINITY)
            return "INF";
        else if (isNaN(value))
            return "NaN";
        else
            return null;
    }

    /**
     * For simple types with the whitespace restriction <code>collapse</code>
     * all occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return)
     * are replaced with #x20 (space), then consecutive spaces are collapsed
     * to a single space, then finally the leading and trailing spaces are
     * trimmed.
     */ 
    private static function whitespaceCollapse(value:*):String
    {
        if (value == null)
            return null;

        var s:String = value.toString();
        s = s.replace(whitespaceCollapsePattern, " ");
        s = s.replace(whitespaceTrimPattern, "");
        return s;
    }

    /**
     * For simple types with the whitespace restriction <code>replace</code>
     * all occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return)
     * are replaced with #x20 (space).
     */ 
    private static function whitespaceReplace(value:*):String
    {
        if (value == null)
            return null;

        var s:String = value.toString();
        s = s.replace(whitespaceReplacePattern, " ");
        return s;
    }

    private function registerMarshallers():void
    {
        marshallers = {};
        marshallers[datatypes.booleanQName.localName] = marshallBoolean;

        // Special built-in schema datatypes
        marshallers[datatypes.anyTypeQName.localName] = marshallAny;
        marshallers[datatypes.anySimpleTypeQName.localName] = marshallAny;
        marshallers[datatypes.anyAtomicTypeQName.localName] = marshallAny;

        // Primitive datatypes
        marshallers[datatypes.stringQName.localName] = marshallString;
        marshallers[datatypes.booleanQName.localName] = marshallBoolean;
        marshallers[datatypes.decimalQName.localName] = marshallDecimal;
        marshallers[datatypes.precisionDecimal.localName] = marshallDecimal;
        marshallers[datatypes.floatQName.localName] = marshallFloat;
        marshallers[datatypes.doubleQName.localName] = marshallDouble;
        marshallers[datatypes.durationQName.localName] = marshallDuration;
        marshallers[datatypes.dateTimeQName.localName] = marshallDatetime;
        marshallers[datatypes.timeQName.localName] = marshallDatetime;
        marshallers[datatypes.dateQName.localName] = marshallDatetime;
        marshallers[datatypes.gYearMonthQName.localName] = marshallGregorian;
        marshallers[datatypes.gYearQName.localName] = marshallGregorian;
        marshallers[datatypes.gMonthDayQName.localName] = marshallGregorian;
        marshallers[datatypes.gDayQName.localName] = marshallGregorian;
        marshallers[datatypes.gMonthQName.localName] = marshallGregorian;
        marshallers[datatypes.hexBinaryQName.localName] = marshallHexBinary;
        marshallers[datatypes.base64BinaryQName.localName] = marshallBase64Binary;
        marshallers[datatypes.anyURIQName.localName] = marshallString;
        marshallers[datatypes.QNameQName.localName] = marshallString;
        marshallers[datatypes.NOTATIONQName.localName] = marshallString;

        // Other built-in datatypes
        marshallers[datatypes.normalizedStringQName.localName] = marshallString;
        marshallers[datatypes.tokenQName.localName] = marshallString;
        marshallers[datatypes.languageQName.localName] = marshallString;
        marshallers[datatypes.NMTOKENQName.localName] = marshallString;
        marshallers[datatypes.NMTOKENSQName.localName] = marshallString;
        marshallers[datatypes.NameQName.localName] = marshallString;
        marshallers[datatypes.NCNameQName.localName] = marshallString;
        marshallers[datatypes.IDQName.localName] = marshallString;
        marshallers[datatypes.IDREF.localName] = marshallString;
        marshallers[datatypes.IDREFS.localName] = marshallString;
        marshallers[datatypes.ENTITY.localName] = marshallString;
        marshallers[datatypes.ENTITIES.localName] = marshallString;
        marshallers[datatypes.integerQName.localName] = marshallInteger;
        marshallers[datatypes.nonPositiveIntegerQName.localName] = marshallInteger;
        marshallers[datatypes.negativeIntegerQName.localName] = marshallInteger;
        marshallers[datatypes.longQName.localName] = marshallInteger;
        marshallers[datatypes.intQName.localName] = marshallInteger;
        marshallers[datatypes.shortQName.localName] = marshallInteger;
        marshallers[datatypes.byteQName.localName] = marshallInteger;
        marshallers[datatypes.nonNegativeIntegerQName.localName] = marshallInteger;
        marshallers[datatypes.unsignedLongQName.localName] = marshallInteger;
        marshallers[datatypes.unsignedIntQName.localName] = marshallInteger;
        marshallers[datatypes.unsignedShortQName.localName] = marshallInteger;
        marshallers[datatypes.unsignedByteQName.localName] = marshallInteger;
        marshallers[datatypes.positiveIntegerQName.localName] = marshallInteger;
        marshallers[datatypes.yearMonthDurationQName.localName] = marshallDatetime;
        marshallers[datatypes.dayTimeDurationQName.localName] = marshallDatetime;

        // 1999
        if (datatypes.timeInstantQName != null)
            marshallers[datatypes.timeInstantQName.localName] = marshallDatetime;


        unmarshallers = {};
        unmarshallers[datatypes.booleanQName.localName] = unmarshallBoolean;

        // Special built-in schema datatypes
        unmarshallers[datatypes.anyTypeQName.localName] = unmarshallAny;
        unmarshallers[datatypes.anySimpleTypeQName.localName] = unmarshallAny;
        unmarshallers[datatypes.anyAtomicTypeQName.localName] = unmarshallAny;

        // Primitive datatypes
        unmarshallers[datatypes.stringQName.localName] = unmarshallString;
        unmarshallers[datatypes.booleanQName.localName] = unmarshallBoolean;
        unmarshallers[datatypes.decimalQName.localName] = unmarshallDecimal;
        unmarshallers[datatypes.precisionDecimal.localName] = unmarshallDecimal;
        unmarshallers[datatypes.floatQName.localName] = unmarshallFloat;
        unmarshallers[datatypes.doubleQName.localName] = unmarshallDouble;
        unmarshallers[datatypes.durationQName.localName] = unmarshallDuration;
        unmarshallers[datatypes.dateTimeQName.localName] = unmarshallDatetime;
        unmarshallers[datatypes.timeQName.localName] = unmarshallDatetime;
        unmarshallers[datatypes.dateQName.localName] = unmarshallDate;
        unmarshallers[datatypes.gYearMonthQName.localName] = unmarshallGregorian;
        unmarshallers[datatypes.gYearQName.localName] = unmarshallGregorian;
        unmarshallers[datatypes.gMonthDayQName.localName] = unmarshallGregorian;
        unmarshallers[datatypes.gDayQName.localName] = unmarshallGregorian;
        unmarshallers[datatypes.gMonthQName.localName] = unmarshallGregorian;
        unmarshallers[datatypes.hexBinaryQName.localName] = unmarshallHexBinary;
        unmarshallers[datatypes.base64BinaryQName.localName] = unmarshallBase64Binary;
        unmarshallers[datatypes.anyURIQName.localName] = unmarshallString;
        unmarshallers[datatypes.QNameQName.localName] = unmarshallString;
        unmarshallers[datatypes.NOTATIONQName.localName] = unmarshallString;

        // Other built-in datatypes
        unmarshallers[datatypes.normalizedStringQName.localName] = unmarshallString;
        unmarshallers[datatypes.tokenQName.localName] = unmarshallString;
        unmarshallers[datatypes.languageQName.localName] = unmarshallString;
        unmarshallers[datatypes.NMTOKENQName.localName] = unmarshallString;
        unmarshallers[datatypes.NMTOKENSQName.localName] = unmarshallString;
        unmarshallers[datatypes.NameQName.localName] = unmarshallString;
        unmarshallers[datatypes.NCNameQName.localName] = unmarshallString;
        unmarshallers[datatypes.IDQName.localName] = unmarshallString;
        unmarshallers[datatypes.IDREF.localName] = unmarshallString;
        unmarshallers[datatypes.IDREFS.localName] = unmarshallString;
        unmarshallers[datatypes.ENTITY.localName] = unmarshallString;
        unmarshallers[datatypes.ENTITIES.localName] = unmarshallString;
        unmarshallers[datatypes.integerQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.nonPositiveIntegerQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.negativeIntegerQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.longQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.intQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.shortQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.byteQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.nonNegativeIntegerQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.unsignedLongQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.unsignedIntQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.unsignedShortQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.unsignedByteQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.positiveIntegerQName.localName] = unmarshallInteger;
        unmarshallers[datatypes.yearMonthDurationQName.localName] = unmarshallDatetime;
        unmarshallers[datatypes.dayTimeDurationQName.localName] = unmarshallDatetime;

        // 1999
        if (datatypes.timeInstantQName != null)
            unmarshallers[datatypes.timeInstantQName.localName] = unmarshallDatetime;
    }

    /**
     * A Boolean flag to determines whether ActionScript ByteArrays should be
     * serialized as base64Binary or hexBinary when specific XML Schema type
     * information has not been provided. The default is true meaning
     * base64Binary.
     * 
     * @see flash.utils.ByteArray
     */
    public static var byteArrayAsBase64Binary:Boolean = true;

    /**
     * A RegEx pattern to help replace all whitespace characters in the content
     * of certain simple types with #x20 (space) characters. The XML Schema
     * specification defines whitespace as #x9 (tab), #xA (line feed) and
     * #xD (carriage return).
     */
    public static var whitespaceReplacePattern:RegExp = new RegExp("[\t\r\n]", "g");

    /**
     * A RegEx pattern to help collapse all consecutive whitespace characters in
     * the content of certain simple types to a single #x20 (space) character.
     * The XML Schema specification defines whitespace as #x9 (tab),
     * #xA (line feed) and #xD (carriage return).
     */
    public static var whitespaceCollapsePattern:RegExp = new RegExp("[ \t\r\n]+", "g");

    /**
     * A RegEx pattern to help trim all leading and trailing spaces in the
     * content of certain simple types. For whitespace <code>collapse</code>,
     * this RegEx is executed after the whitespaceCollapsePattern RegEx has
     * been executed.
     */
    public static var whitespaceTrimPattern:RegExp = new RegExp("^[ ]+|[ ]+$", "g");

    public static const FLOAT_MAX_VALUE:Number = (Math.pow(2, 24) - 1) * Math.pow(2, 104);
    public static const FLOAT_MIN_VALUE:Number = Math.pow(2, -149);
    public static const LONG_MAX_VALUE:Number = 9223372036854775807;
    public static const LONG_MIN_VALUE:Number = -9223372036854775808;
    public static const SHORT_MAX_VALUE:Number = 32767;
    public static const SHORT_MIN_VALUE:Number = -32768;
    public static const BYTE_MAX_VALUE:Number = 127;
    public static const BYTE_MIN_VALUE:Number = -128;
    public static const ULONG_MAX_VALUE:Number = 18446744073709551615;
    public static const USHORT_MAX_VALUE:Number = 65535;
    public static const UBYTE_MAX_VALUE:Number = 255;

    private var marshallers:Object;
    private var unmarshallers:Object;
    private var constants:SchemaConstants;
    private var datatypes:SchemaDatatypes;    
    private var _validating:Boolean;


}

}