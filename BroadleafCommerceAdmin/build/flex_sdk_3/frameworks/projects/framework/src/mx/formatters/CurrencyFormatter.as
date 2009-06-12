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
[ResourceBundle("SharedResources")]

/**
 *  The CurrencyFormatter class formats a valid number as a currency value.
 *  It adjusts the decimal rounding and precision, the thousands separator, 
 *  and the negative sign; it also adds a currency symbol.
 *  You place the currency symbol on either the left or the right side
 *  of the value with the <code>alignSymbol</code> property.
 *  The currency symbol can contain multiple characters,
 *  including blank spaces.
 *  
 *  <p>If an error occurs, an empty String is returned and a String that describes 
 *  the error is saved to the <code>error</code> property. The <code>error</code> 
 *  property can have one of the following values:</p>
 *
 *  <ul>
 *    <li><code>"Invalid value"</code> means an invalid numeric value is passed to 
 *    the <code>format()</code> method. The value should be a valid number in the 
 *    form of a Number or a String.</li>
 *    <li><code>"Invalid format"</code> means one of the parameters contains an unusable setting.</li>
 *  </ul>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:CurrencyFormatter&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:CurrencyFormatter
 *    alignSymbol="left|right" 
 *    currencySymbol="$"
 *    decimalSeparatorFrom="."
 *    decimalSeparatorTo="."
 *    precision="-1"
 *    rounding="none|up|down|nearest"
 *    thousandsSeparatorFrom=","
 *    thousandsSeparatorTo=","
 *    useNegativeSign="true|false"
 *    useThousandsSeparator="true|false"
 *	/>  
 *  </pre>
 *  
 *  @includeExample examples/CurrencyFormatterExample.mxml
 *  
 *  @see mx.formatters.NumberBase
 *  @see mx.formatters.NumberBaseRoundType
 */
public class CurrencyFormatter extends Formatter
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
	public function CurrencyFormatter()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  alignSymbol
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the alignSymbol property.
	 */
	private var _alignSymbol:String;
	
    /**
	 *  @private
	 */
	private var alignSymbolOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Aligns currency symbol to the left side or the right side
	 *  of the formatted number.
     *  Permitted values are <code>"left"</code> and <code>"right"</code>.
	 *
     *  @default "left"
     */
	public function get alignSymbol():String
	{
		return _alignSymbol;
	}

	/**
	 *  @private
	 */
	public function set alignSymbol(value:String):void
	{
		alignSymbolOverride = value;

		_alignSymbol = value != null ?
					   value :
					   resourceManager.getString(
					       "SharedResources", "alignSymbol");
	}

	//----------------------------------
	//  currencySymbol
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the currencySymbol property.
	 */
	private var _currencySymbol:String;
	
    /**
	 *  @private
	 */
	private var currencySymbolOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Character to use as a currency symbol for a formatted number.
     *  You can use one or more characters to represent the currency 
	 *  symbol; for example, "$" or "YEN".
	 *  You can also use empty spaces to add space between the 
	 *  currency character and the formatted number.
	 *  When the number is a negative value, the currency symbol
	 *  appears between the number and the minus sign or parentheses.
	 *
     *  @default "$"
     */
	public function get currencySymbol():String
	{
		return _currencySymbol;
	}

	/**
	 *  @private
	 */
	public function set currencySymbol(value:String):void
	{
		currencySymbolOverride = value;

		_currencySymbol = value != null ?
						  value :
						  resourceManager.getString(
					          "SharedResources", "currencySymbol");
	}

	//----------------------------------
	//  decimalSeparatorFrom
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the decimalSeparatorFrom property.
	 */
	private var _decimalSeparatorFrom:String;
	
    /**
	 *  @private
	 */
	private var decimalSeparatorFromOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Decimal separator character to use
	 *  when parsing an input string.
	 *
     *  @default "."
     */
	public function get decimalSeparatorFrom():String
	{
		return _decimalSeparatorFrom;
	}

	/**
	 *  @private
	 */
	public function set decimalSeparatorFrom(value:String):void
	{
		decimalSeparatorFromOverride = value;

		_decimalSeparatorFrom = value != null ?
								value :
								resourceManager.getString(
								    "SharedResources", "decimalSeparatorFrom");
	}

	//----------------------------------
	//  decimalSeparatorTo
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the decimalSeparatorTo property.
	 */
	private var _decimalSeparatorTo:String;
	
    /**
	 *  @private
	 */
	private var decimalSeparatorToOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Decimal separator character to use
	 *  when outputting formatted decimal numbers.
	 *
     *  @default "."
     */
	public function get decimalSeparatorTo():String
	{
		return _decimalSeparatorTo;
	}

	/**
	 *  @private
	 */
	public function set decimalSeparatorTo(value:String):void
	{
		decimalSeparatorToOverride = value;

		_decimalSeparatorTo = value != null ?
							  value :
							  resourceManager.getString(
							      "SharedResources", "decimalSeparatorTo");
	}

	//----------------------------------
	//  precision
	//----------------------------------
	
    /**
	 *  @private
	 *  Storage for the precision property.
	 */
	private var _precision:Object;
	
    /**
	 *  @private
	 */
	private var precisionOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Number of decimal places to include in the output String.
	 *  You can disable precision by setting it to <code>-1</code>.
	 *  A value of <code>-1</code> means do not change the precision. For example, 
	 *  if the input value is 1.453 and <code>rounding</code> 
	 *  is set to <code>NumberBaseRoundType.NONE</code>, return 1.453.
	 *  If <code>precision</code> is -1 and you set some form of 
	 *  rounding, return a value based on that rounding type.
	 *
     *  @default -1
     */
	public function get precision():Object
	{
		return _precision;
	}

	/**
	 *  @private
	 */
	public function set precision(value:Object):void
	{
		precisionOverride = value;

		_precision = value != null ?
					 int(value) :
					 resourceManager.getInt(
					     "formatters", "currencyFormatterPrecision");
	}

	//----------------------------------
	//  rounding
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the rounding property.
	 */
	private var _rounding:String;
	
    /**
	 *  @private
	 */
	private var roundingOverride:String;
	
    [Inspectable(category="General", enumeration="none,up,down,nearest", defaultValue="null")]

    /**
     *  How to round the number.
	 *  In ActionScript, the value can be <code>NumberBaseRoundType.NONE</code>, 
	 *  <code>NumberBaseRoundType.UP</code>,
	 *  <code>NumberBaseRoundType.DOWN</code>, or <code>NumberBaseRoundType.NEAREST</code>.
	 *  In MXML, the value can be <code>"none"</code>, 
	 *  <code>"up"</code>, <code>"down"</code>, or <code>"nearest"</code>.
	 *
	 *  @default NumberBaseRoundType.NONE
 	 *
	 *  @see mx.formatters.NumberBaseRoundType
     */
	public function get rounding():String
	{
		return _rounding;
	}

	/**
	 *  @private
	 */
	public function set rounding(value:String):void
	{
		roundingOverride = value;

		_rounding = value != null ?
					value :
					resourceManager.getString(
					    "formatters", "rounding");
	}

	//----------------------------------
	//  thousandsSeparatorFrom
	//----------------------------------
	
    /**
	 *  @private
	 *  Storage for the thousandsSeparatorFrom property.
	 */
	private var _thousandsSeparatorFrom:String;
	
    /**
	 *  @private
	 */
	private var thousandsSeparatorFromOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Character to use as the thousands separator
	 *  in the input String.
	 *
     *  @default ","
     */
	public function get thousandsSeparatorFrom():String
	{
		return _thousandsSeparatorFrom;
	}

	/**
	 *  @private
	 */
	public function set thousandsSeparatorFrom(value:String):void
	{
		thousandsSeparatorFromOverride = value;

		_thousandsSeparatorFrom = value != null ?
								  value :
								  resourceManager.getString(
								      "SharedResources", "thousandsSeparatorFrom");
	}
	
	//----------------------------------
	//  thousandsSeparatorTo
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the thousandsSeparatorTo property.
	 */
	private var _thousandsSeparatorTo:String;
	
    /**
	 *  @private
	 */
	private var thousandsSeparatorToOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Character to use as the thousands separator
	 *  in the output string.
	 *
     *  @default ","
     */
	public function get thousandsSeparatorTo():String
	{
		return _thousandsSeparatorTo;
	}

	/**
	 *  @private
	 */
	public function set thousandsSeparatorTo(value:String):void
	{
		thousandsSeparatorToOverride = value;

		_thousandsSeparatorTo = value != null ?
								value :
								resourceManager.getString(
								    "SharedResources", "thousandsSeparatorTo");
	}

	//----------------------------------
	//  useNegativeSign
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the useNegativeSign property.
	 */
	private var _useNegativeSign:Object;
	
    /**
	 *  @private
	 */
	private var useNegativeSignOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  If <code>true</code>, format a negative number 
	 *  by preceding it with a minus "-" sign.
	 *  If <code>false</code>, format the number
	 *  surrounded by parentheses, for example (400).
	 *
     *  @default true
     */
	public function get useNegativeSign():Object
	{
		return _useNegativeSign;
	}

	/**
	 *  @private
	 */
	public function set useNegativeSign(value:Object):void
	{
		useNegativeSignOverride = value;

		_useNegativeSign = value != null ?
						   Boolean(value) :
						   resourceManager.getBoolean(
						       "formatters", "useNegativeSign");
	}

	//----------------------------------
	//  useThousandsSeparator
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the useThousandsSeparator property.
	 */
	private var _useThousandsSeparator:Object;
	
    /**
	 *  @private
	 */
	private var useThousandsSeparatorOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  If <code>true</code>, split the number into thousands increments
	 *  by using a separator character.
	 *
     *  @default true
     */
	public function get useThousandsSeparator():Object
	{
		return _useThousandsSeparator;
	}

	/**
	 *  @private
	 */
	public function set useThousandsSeparator(value:Object):void
	{
		useThousandsSeparatorOverride = value;

		_useThousandsSeparator = value != null ?
								 Boolean(value) :
								 resourceManager.getBoolean(
								     "formatters", "useThousandsSeparator");
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

		alignSymbol = alignSymbolOverride;
		currencySymbol = currencySymbolOverride;
		decimalSeparatorFrom = decimalSeparatorFromOverride;
		decimalSeparatorTo = decimalSeparatorToOverride;
		precision = precisionOverride;
		rounding = roundingOverride;
		thousandsSeparatorFrom = thousandsSeparatorFromOverride;
		thousandsSeparatorTo = thousandsSeparatorToOverride;
		useNegativeSign = useNegativeSignOverride;
		useThousandsSeparator = useThousandsSeparatorOverride;
	}

    /**
     *  Formats <code>value</code> as currency.
	 *  If <code>value</code> cannot be formatted, return an empty String 
	 *  and write a description of the error to the <code>error</code> property.
	 *
     *  @param value Value to format.
	 *
     *  @return Formatted string. Empty if an error occurs.
     */
    override public function format(value:Object):String
    {
        // Reset any previous errors.
        if (error)
			error = null;

        if (useThousandsSeparator &&
			(decimalSeparatorFrom == thousandsSeparatorFrom ||
			 decimalSeparatorTo == thousandsSeparatorTo))
        {
            error = defaultInvalidFormatError;
            return "";
        }

        if (decimalSeparatorTo == "")
        {
            error = defaultInvalidFormatError;
            return "";
        }

        var dataFormatter:NumberBase = new NumberBase(decimalSeparatorFrom,
													  thousandsSeparatorFrom,
													  decimalSeparatorTo,
													  thousandsSeparatorTo);

        // -- value --

        if (value is String)
            value = dataFormatter.parseNumberString(String(value));

        if (value === null || isNaN(Number(value)))
        {
            error = defaultInvalidValueError;
            return "";
        }

        // -- format --
        
		var isNegative:Boolean = (Number(value) < 0);

        var numStr:String = value.toString();
        var numArrTemp:Array = numStr.split(".");
        var numFraction:int = numArrTemp[1] ? String(numArrTemp[1]).length : 0;

        if (precision <= numFraction)
		{
            if (rounding != NumberBaseRoundType.NONE)
			{
                numStr = dataFormatter.formatRoundingWithPrecision(
					numStr, rounding, int(precision));
			}
		}

        var numValue:Number = Number(numStr);
        if (Math.abs(numValue) >= 1)
        {
            numArrTemp = numStr.split(".");
            var front:String =
				useThousandsSeparator ?
				dataFormatter.formatThousands(String(numArrTemp[0])) :
				String(numArrTemp[0]);
            if (numArrTemp[1] != null && numArrTemp[1] != "")
                numStr = front + decimalSeparatorTo + numArrTemp[1];
            else
                numStr = front;
        }
        else if (Math.abs(numValue) > 0)
        {
        	// if the value is in scientefic notation then the search for '.' 
        	// doesnot give the correct result. Adding one to the value forces 
        	// the value to normal decimal notation. 
        	// As we are dealing with only the decimal portion we need not 
        	// worry about reverting the addition
        	if (numStr.indexOf("e") != -1)
        	{
	        	var temp:Number = Math.abs(numValue) + 1;
	        	numStr = temp.toString();
        	}
            numStr = decimalSeparatorTo +
					 numStr.substring(numStr.indexOf(".") + 1);
        }
        
        numStr = dataFormatter.formatPrecision(numStr, int(precision));

		// If our value is 0, then don't show -0
		if (Number(numStr) == 0)
		{
			isNegative = false;	
		}

        if (isNegative)
            numStr = dataFormatter.formatNegative(numStr, useNegativeSign);

        if (!dataFormatter.isValid)
        {
            error = defaultInvalidFormatError;
            return "";
        }

        // -- currency --

        if (alignSymbol == "left")
		{
            if (isNegative)
			{
                var nSign:String = numStr.charAt(0);
                var baseVal:String = numStr.substr(1, numStr.length - 1);
                numStr = nSign + currencySymbol + baseVal;
            }
			else
			{
                numStr = currencySymbol + numStr;
            }
        } 
		else if (alignSymbol == "right")
		{
            var lastChar:String = numStr.charAt(numStr.length - 1);
            if (isNegative && lastChar == ")")
			{
                baseVal = numStr.substr(0, numStr.length - 1);
                numStr = baseVal + currencySymbol + lastChar;
            }
			else
			{
                numStr = numStr + currencySymbol;
            }
        }
		else
		{
            error = defaultInvalidFormatError;
            return "";
        }

        return numStr;
    }
}

}
