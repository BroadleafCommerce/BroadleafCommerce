////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.formatters
{

/**
 *  The NumberBase class is a utility class that contains
 *  general number formatting capabilities, including rounding,
 *  precision, thousands formatting, and negative sign formatting.
 *  The implementation of the formatter classes use this class.
 *
 *  @see mx.formatters.NumberFormatter
 *  @see mx.formatters.NumberBaseRoundType
 */
public class NumberBase
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 * 
	 *  @param decimalSeparatorFrom Decimal separator to use
	 *  when parsing an input String.
	 *
	 *  @param thousandsSeparatorFrom Character to use
	 *  as the thousands separator in the input String.
	 *
	 *  @param decimalSeparatorTo Decimal separator character to use
	 *  when outputting formatted decimal numbers.
	 *
	 *  @param thousandsSeparatorTo Character to use
	 *  as the thousands separator in the output String.
	 */
	public function NumberBase(decimalSeparatorFrom:String = ".",
							   thousandsSeparatorFrom:String = ",",
							   decimalSeparatorTo:String = ".",
							   thousandsSeparatorTo:String = ",")
	{
		super();

		this.decimalSeparatorFrom = decimalSeparatorFrom;
		this.thousandsSeparatorFrom = thousandsSeparatorFrom;
		this.decimalSeparatorTo = decimalSeparatorTo;
		this.thousandsSeparatorTo = thousandsSeparatorTo;

		isValid = true;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  decimalSeparatorFrom
	//----------------------------------

	/**
     *  Decimal separator character to use
	 *  when parsing an input String.
	 *  
	 *  @default "."
	 */
	public var decimalSeparatorFrom:String;

	//----------------------------------
	//  decimalSeparatorTo
	//----------------------------------

	/**
     *  Decimal separator character to use
	 *  when outputting formatted decimal numbers.
	 *  
	 *  @default "."
	 */
	public var decimalSeparatorTo:String;

	//----------------------------------
	//  isValid
	//----------------------------------

	/**
	 *  If <code>true</code>, the format succeeded,
	 *  otherwise it is <code>false</code>.
	 */
	public var isValid:Boolean = false;

	//----------------------------------
	//  thousandsSeparatorFrom
	//----------------------------------

	/**
     *  Character to use as the thousands separator
	 *  in the input String.
	 *  
	 *  @default ","
	 */
	public var thousandsSeparatorFrom:String;

	//----------------------------------
	//  thousandsSeparatorTo
	//----------------------------------

	/**
     *  Character to use as the thousands separator
	 *  in the output String.
	 *  
	 *  @default ","
	 */
	public var thousandsSeparatorTo:String;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Formats a number by rounding it. 
	 *  The possible rounding types are defined by
	 *  mx.formatters.NumberBaseRoundType.
	 *
	 *  @param value Value to be rounded.
	 *
	 *  @param roundType The type of rounding to perform:
	 *  NumberBaseRoundType.NONE, NumberBaseRoundType.UP,
	 *  NumberBaseRoundType.DOWN, or NumberBaseRoundType.NEAREST.
	 *
	 *  @return Formatted number.
	 *
	 *  @see mx.formatters.NumberBaseRoundType
	 */
	public function formatRounding(value:String, roundType:String):String
	{
		var v:Number = Number(value);
		
		if (roundType != NumberBaseRoundType.NONE)
		{
			if (roundType == NumberBaseRoundType.UP)
			{
				 v = Math.ceil(v);
			}
			else if (roundType == NumberBaseRoundType.DOWN)
			{
				v = Math.floor(v);
			}
			else if (roundType == NumberBaseRoundType.NEAREST)
			{
				v = Math.round(v);
			}
			else
			{
				isValid = false;
				return "";
			}
		}

		return v.toString();
	}

	/**
	 *  Formats a number by rounding it and setting the decimal precision.
	 *  The possible rounding types are defined by
	 *  mx.formatters.NumberBaseRoundType.
	 *
	 *  @param value Value to be rounded.
	 *
	 *  @param roundType The type of rounding to perform:
	 *  NumberBaseRoundType.NONE, NumberBaseRoundType.UP,
	 *  NumberBaseRoundType.DOWN, or NumberBaseRoundType.NEAREST.
	 *
	 *  @param precision int of decimal places to use.
	 *
	 *  @return Formatted number.
	 *
	 *  @see mx.formatters.NumberBaseRoundType
	 */
	public function formatRoundingWithPrecision(value:String, roundType:String,
												precision:int):String
	{
		// precision works differently now. Its default value is -1
		// which means 'do not alter the number's precision'. If a precision
		// value is set, all numbers will contain that precision. Otherwise, there
		// precision will not be changed. 

		var v:Number = Number(value);
		
		// If rounding is not present and precision is NaN,
		// leave value untouched.
		if (roundType == NumberBaseRoundType.NONE) 
		{
			if (precision == -1)
				return v.toString();
		}
		else
		{
			// If rounding is present but precision is less than 0,
			// then do integer rounding.
			if (precision < 0) 
				precision = 0;
			
			// Shift decimal right as Math functions
			// perform only integer ceil/round/floor.
			v = v * Math.pow(10, precision);
			
			// Attempt to get rid of floating point errors
			v = Number(v.toString()); 
			if (roundType == NumberBaseRoundType.UP)
			{
				v = Math.ceil(v);
			}
			else if (roundType == NumberBaseRoundType.DOWN)
			{
				v = Math.floor(v);
			}
			else if (roundType == NumberBaseRoundType.NEAREST)
			{
				v = Math.round(v);
			}
			else
			{
				isValid = false;
				return "";
			}
			
			// Shift decimal left to get back decimal to original point.
			v = v / Math.pow(10, precision); 
		}

		return v.toString();
	}

	/**
	 *  Formats a number by replacing the default decimal separator, ".", 
	 *  with the decimal separator specified by <code>decimalSeparatorTo</code>. 
	 *
	 *  @param value The String value of the Number
	 *  (formatted American style ####.##).
	 *
	 *  @return String representation of the input where "." is replaced
	 *  with the decimal formatting character.
	 */
	public function formatDecimal(value:String):String
	{
	    var parts:Array = value.split(".");
	    return parts.join(decimalSeparatorTo);
	}

	/**
	 *  Formats a number by using 
	 *  the <code>thousandsSeparatorTo</code> property as the thousands separator 
	 *  and the <code>decimalSeparatorTo</code> property as the decimal separator.
	 *
	 *  @param value Value to be formatted.
	 *
	 *  @return Formatted number.
	 */
	public function formatThousands(value:String):String
	{
		var v:Number = Number(value);
		
		var isNegative:Boolean = (v < 0);
		
		var numStr:String = Math.abs(v).toString();
		var numArr:Array =
			numStr.split((numStr.indexOf(decimalSeparatorTo) != -1) ? decimalSeparatorTo : ".");
		var numLen:int = String(numArr[0]).length;

		if (numLen > 3)
		{
			var numSep:int = int(Math.floor(numLen / 3));

			if ((numLen % 3) == 0)
				numSep--;
			
			var b:int = numLen;
			var a:int = b - 3;
			
			var arr:Array = [];
			for (var i:int = 0; i <= numSep; i++)
			{
				arr[i] = numArr[0].slice(a, b);
				a = int(Math.max(a - 3, 0));
				b = int(Math.max(b - 3, 1));
			}
			
			arr.reverse();
			
			numArr[0] = arr.join(thousandsSeparatorTo);
		}
		
		numStr = numArr.join(decimalSeparatorTo);
		
		if (isNegative)
			numStr = "-" + numStr;
		
		return numStr.toString();
	}

	/**
	 *  Formats a number by setting its decimal precision by using 
	 *  the <code>decimalSeparatorTo</code> property as the decimal separator.
	 *
	 *  @param value Value to be formatted.
	 *
	 *  @param precision Number of decimal points to use.
	 *
	 *  @return Formatted number.
	 */
	public function formatPrecision(value:String, precision:int):String
	{
		// precision works differently now. Its default value is -1
		// which stands for 'do not alter the number's precision'. If a precision
		// value is set, all numbers will contain that precision. Otherwise, there
		// precision will not be changed.
		
		if (precision == -1)
			return value;
		
		var numArr:Array = value.split(decimalSeparatorTo);
		
		numArr[0] = numArr[0].length == 0 ? "0" : numArr[0];
		
		if (precision > 0)
		{
			var decimalVal:String = numArr[1] ? String(numArr[1]) : "";
			var fraction:String =
				decimalVal + "000000000000000000000000000000000";
			value = numArr[0] + decimalSeparatorTo + fraction.substr(0, precision);
		}
		else
		{
			value = String(numArr[0]);
		}
		
		return value.toString();
	}

	/**
	 *  Formats a negative number with either a minus sign (-)
	 *  or parentheses ().
	 *
	 *  @param value Value to be formatted.
	 *
	 *  @param useSign If <code>true</code>, use a minus sign (-).
	 *  If <code>false</code>, use parentheses ().
	 *
	 *  @return Formatted number.
	 */
	public function formatNegative(value:String, useSign:Boolean):String
	{
		if (useSign)
		{
			if (value.charAt(0) != "-")
				value = "-" + value;
		}
		else if (!useSign)
		{
			if (value.charAt(0) == "-")
				value = value.substr(1, value.length - 1);
			value = "(" + value + ")";
		}
		else
		{
			isValid = false;
			return "";
		}
		return value;
	}

	/**
	 *  Extracts a number from a formatted String.
	 *  Examines the String from left to right
	 *  and returns the first number sequence.
	 *  Ignores thousands separators and includes the
	 *  decimal and numbers trailing the decimal.
	 *
	 *  @param str String to parse for the numeric value.
	 *
	 *  @return Value, which can be a decimal.
	 */
	public function parseNumberString(str:String):String
	{
		// Check the decimal and thousands formatting for validity.
		var splitDec:Array = str.split(decimalSeparatorFrom);
		if (splitDec.length > 2)
			return null;

		// Attempt to extract the first number sequence from the string.
		var len:int = str.length;
		var count:int = 0;
		var letter:String;
		var num:String;
		var isNegative:Boolean = false;

		while (count < len)
		{
			letter = str.charAt(count);
			count++;

			if (("0" <= letter && letter <= "9") || (letter == decimalSeparatorFrom))
			{
				var lastLetter:String = str.charAt(count - 2);
				if (lastLetter == "-")
					isNegative = true;
				num = "";
				count--;
				
				for (var i:int = count; i < len; i++)
				{
					letter = str.charAt(count);
					count++;
					if ("0" <= letter && letter <= "9")
						num += letter;
					else if (letter == decimalSeparatorFrom)
						num += ".";
					else if (letter != thousandsSeparatorFrom || count >= len)
						break;
				}
			}
		}

		return isNegative ? "-" + num : num;
	}
}

}
