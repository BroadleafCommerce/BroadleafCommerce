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

import mx.managers.ISystemManager;
import mx.managers.SystemManager;

[ResourceBundle("formatters")]

/**
 *  The ZipCodeFormatter class formats a valid number
 *  into one of the following formats, based on a
 *  user-supplied <code>formatString</code> property.
 *  
 *  <ul>
 *    <li>#####-####</li>
 *    <li>##### ####</li>
 *    <li>#####</li>
 *    <li>### ### (Canadian)</li>
 *  </ul>
 *  
 *  <p>A six-digit number must be supplied for a six-digit mask.
 *  If you use a five-digit or a nine-digit mask, you can use
 *  either a five-digit or a nine-digit number for formatting.</p>
 *
 *  <p>If an error occurs, an empty String is returned and a String that  
 *  describes the error is saved to the <code>error</code> property.  
 *  The <code>error</code> property can have one of the following values:</p>
 *
 *  <ul>
 *    <li><code>"Invalid value"</code> means an invalid numeric value is passed 
 *    to the <code>format()</code> method. The value should be a valid number 
 *    in the form of a Number or a String, except for Canadian postal code, 
 *    which allows alphanumeric values, or the number of digits does not match 
 *    the allowed digits from the <code>formatString</code> property.</li>
 *    <li> <code>"Invalid format"</code> means any of the characters in the 
 *    <code>formatString</code> property do not match the allowed characters 
 *    specified in the <code>validFormatChars</code> property, 
 *    or the number of numeric placeholders does not equal 9, 5, or 6.</li>
 *  </ul>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:ZipCodeFormatter&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:ZipCodeFormatter
 *    formatString="#####|#####-####|### ###"
 *  />
 *  </pre>
 *  
 *  @includeExample examples/ZipCodeFormatterExample.mxml
 *  
 *  @see mx.formatters.SwitchSymbolFormatter
 */
public class ZipCodeFormatter extends Formatter
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private    
     */	
	private static const VALID_LENGTHS:String = "9,5,6";
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function ZipCodeFormatter()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  formatString
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the formatString property.
	 */
	private var _formatString:String;
	
    /**
	 *  @private
	 */
	private var formatStringOverride:String;
	
	[Inspectable(category="General", defaultValue="null")]

	/**
	 *  The mask pattern.
	 *  Possible values are <code>"#####-####"</code>,
	 *  <code>"##### ####"</code>, <code>"#####"</code>,
	 *  <code>"###-###"</code> and <code>"### ###"</code>.
	 *  	 
	 *  @default "#####"
	 */
	public function get formatString():String
	{
		return _formatString;
	}

	/**
	 *  @private
	 */
	public function set formatString(value:String):void
	{
		formatStringOverride = value;

		_formatString = value != null ?
						value :
						resourceManager.getString(
							"formatters", "zipCodeFormat");
	}

	//--------------------------------------------------------------------------
	//
	//  Overidden methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private    
     */
	override protected function resourcesChanged():void
	{
		super.resourcesChanged();

		formatString = formatStringOverride;
	}

 	/**
	 *  Formats the String by using the specified format.
	 *  If the value cannot be formatted, return an empty String 
	 *  and write a description of the error to the <code>error</code> property.
	 *
	 *  @param value Value to format.
	 *
	 *  @return Formatted String. Empty if an error occurs. A description 
	 *  of the error condition is written to the <code>error</code> property.
	 */
	override public function format(value:Object):String
	{
		// Reset any previous errors.
		if (error)
			error = null;
		
		// -- lengths --

		var fStrLen:int;
		var uStrLen:int = String(value).length;

		if (VALID_LENGTHS.indexOf("" + uStrLen) == -1)
		{
			error = defaultInvalidValueError;
			return "";
		}

		if (formatString == "#####-####" || formatString == "##### ####")
		{
			if (uStrLen != 5 && uStrLen != 9)
			{
				error = defaultInvalidValueError;
				return "";
			}
			fStrLen = 9;
		}

		else if (formatString == "#####")
		{
			if (uStrLen != 5 && uStrLen != 9)
			{
				error = defaultInvalidValueError;
				return "";
			}
			fStrLen = 5;
		}

		else if (formatString == "### ###" || formatString == "###-###")
		{
			if (uStrLen != 6)
			{
				error = defaultInvalidValueError;
				return "";
			}
			fStrLen = 6;
		}

		else
		{
			error = defaultInvalidFormatError;
			return "";
		}

		if (fStrLen == 6 && uStrLen != 6)
		{
			error = defaultInvalidValueError;
			return "";
		}

		// -- value --

		if (fStrLen == 6)
		{
			for (var i:int = 0; i < uStrLen; i++)
			{
				if ((value.charCodeAt(i) < 64 || value.charCodeAt(i) > 90) &&
					(value.charCodeAt(i) < 48 || value.charCodeAt(i) > 57))
				{
					error = defaultInvalidValueError;
					return "";
				}
			}
		}
		else
		{
			if (value === null || isNaN(Number(value)))
			{
				error = defaultInvalidValueError;
				return "";
			}
		}

		// --format--

		if (fStrLen == 9 && uStrLen == 5)
			value = String(value).concat("0000");

		if (fStrLen == 5 && uStrLen == 9)
			value = value.substr(0, 5);

		var dataFormatter:SwitchSymbolFormatter = new SwitchSymbolFormatter();

		return dataFormatter.formatValue(formatString, value);
	}
}

}
