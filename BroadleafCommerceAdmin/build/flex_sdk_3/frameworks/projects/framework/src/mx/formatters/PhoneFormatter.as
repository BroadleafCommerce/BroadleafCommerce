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
 *  The PhoneFormatter class formats a valid number into a phone number format,
 *  including international configurations.
 *
 *  <p>A shortcut is provided for the United States seven-digit format.
 *  If the <code>areaCode</code> property contains a value
 *  and you use the seven-digit format string, (###-####),
 *  a seven-digit value to format automatically adds the area code
 *  to the returned String.
 *  The default format for the area code is (###). 
 *  You can change this using the <code>areaCodeFormat</code> property. 
 *  You can format the area code any way you want as long as it contains 
 *  three number placeholders.</p>
 *
 *  <p>If an error occurs, an empty String is returned and a String
 *  that describes the error is saved to the <code>error</code> property.
 *  The <code>error</code> property can have one of the following values:</p>
 *
 *  <ul>
 *    <li><code>"Invalid value"</code> means an invalid numeric value is passed 
 *    to the <code>format()</code> method. The value should be a valid number 
 *    in the form of a Number or a String, or the value contains a different 
 *    number of digits than what is specified in the format String.</li>
 *    <li> <code>"Invalid format"</code> means any of the characters in the 
 *    <code>formatString</code> property do not match the allowed characters 
 *    specified in the <code>validPatternChars</code> property, 
 *    or the <code>areaCodeFormat</code> property is specified but does not
 *    contain exactly three numeric placeholders.</li>
 *  </ul>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:PhoneFormatter&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:PhoneFormatter
 *    areaCode="-1"
 *    areaCodeFormat="(###)"
 *    formatString="(###) ###-####"
 *    validPatternChars="+()#-. "
 *  />
 *  </pre>
 *  
 *  @includeExample examples/PhoneFormatterExample.mxml
 *  
 *  @see mx.formatters.SwitchSymbolFormatter
 */
public class PhoneFormatter extends Formatter
{
    include "../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function PhoneFormatter()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  areaCode
    //----------------------------------
    
    /**
	 *  @private
	 *  Storage for the areaCode property.
	 */
	private var _areaCode:Object;
	
    /**
	 *  @private
	 */
	private var areaCodeOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Area code number added to a seven-digit United States
     *  format phone number to form a 10-digit phone number.
     *  A value of <code>-1</code> means do not  
     *  prepend the area code.
     *
     *  @default -1  
     */
	public function get areaCode():Object
	{
		return _areaCode;
	}

	/**
	 *  @private
	 */
	public function set areaCode(value:Object):void
	{
		areaCodeOverride = value;

		_areaCode = value != null ?
					int(value) :
					resourceManager.getInt(
						"formatters", "areaCode");
	}

    //----------------------------------
    //  areaCodeFormat
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the areaCodeFormat property.
	 */
	private var _areaCodeFormat:String;
	
    /**
	 *  @private
	 */
	private var areaCodeFormatOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Default format for the area code when the <code>areacode</code>
     *  property is rendered by a seven-digit format.
     *
     *  @default "(###) "
     */
	public function get areaCodeFormat():String
	{
		return _areaCodeFormat;
	}

	/**
	 *  @private
	 */
	public function set areaCodeFormat(value:String):void
	{
		areaCodeFormatOverride = value;

		_areaCodeFormat = value != null ?
						  value :
						  resourceManager.getString(
						      "formatters", "areaCodeFormat");
	}

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
     *  String that contains mask characters
     *  that represent a specified phone number format.
     *
     *  @default "(###) ###-####"
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
							"formatters", "phoneNumberFormat");
	}

    //----------------------------------
    //  validPatternChars
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the validPatternChars property.
	 */
	private var _validPatternChars:String;
	
    /**
	 *  @private
	 */
	private var validPatternCharsOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  List of valid characters that can be used
     *  in the <code>formatString</code> property.
     *  This property is used during validation
     *  of the <code>formatString</code> property.
     *
     *  @default "+()#- ."
     */
	public function get validPatternChars():String
	{
		return _validPatternChars;
	}

	/**
	 *  @private
	 */
	public function set validPatternChars(value:String):void
	{
		validPatternCharsOverride = value;

		_validPatternChars = value != null ?
							 value :
							 resourceManager.getString(
							     "formatters", "validPatternChars");
	}

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private    
     */
    override protected function resourcesChanged():void
    {
		super.resourcesChanged();

        areaCode = areaCodeOverride;
        areaCodeFormat = areaCodeFormatOverride;
        formatString = formatStringOverride;
        validPatternChars = validPatternCharsOverride;
    }

    /**
     *  Formats the String as a phone number.
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

        // --value--

        if (!value || String(value).length == 0 || isNaN(Number(value)))
        {
            error = defaultInvalidValueError;
            return "";
        }

        // --length--

        var fStrLen:int = 0;
        var letter:String;
        var n:int;
        var i:int;
        
        n = formatString.length;
        for (i = 0; i < n; i++)
        {
            letter = formatString.charAt(i);
            if (letter == "#")
            {
                fStrLen++;
            }
            else if (validPatternChars.indexOf(letter) == -1)
            {
                error = defaultInvalidFormatError;
                return "";
            }
        }

        if (String(value).length != fStrLen)
        {
            error = defaultInvalidValueError;
            return "";
        }

        // --format--

        var fStr:String = formatString;

        if (fStrLen == 7 && areaCode != -1)
        {
            var aCodeLen:int = 0;
            n = areaCodeFormat.length;
            for (i = 0; i < n; i++)
            {
                if (areaCodeFormat.charAt(i) == "#")
                    aCodeLen++;
            }
            if (aCodeLen == 3 && String(areaCode).length == 3)
            {
                fStr = String(areaCodeFormat).concat(fStr);
                value = String(areaCode).concat(value);
            }
        }

        var dataFormatter:SwitchSymbolFormatter = new SwitchSymbolFormatter();

        return dataFormatter.formatValue(fStr, value);
    }
}

}
