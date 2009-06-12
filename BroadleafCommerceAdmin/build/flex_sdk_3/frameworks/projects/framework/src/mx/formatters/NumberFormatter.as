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
 *  The NumberFormatter class formats a valid number
 *  by adjusting the decimal rounding and precision,
 *  the thousands separator, and the negative sign.
 *
 *  <p>If you use both the <code>rounding</code> and <code>precision</code>
 *  properties, rounding is applied first, and then you set the decimal length
 *  by using the specified <code>precision</code> value.
 *  This lets you round a number and still have a trailing decimal;
 *  for example, 303.99 = 304.00.</p>
 *
 *  <p>If an error occurs, an empty String is returned and a String
 *  describing  the error is saved to the <code>error</code> property.
 *  The <code>error</code>  property can have one of the following values:</p>
 *
 *  <ul>
 *    <li><code>"Invalid value"</code> means an invalid numeric value is passed to 
 *    the <code>format()</code> method. The value should be a valid number in the 
 *    form of a Number or a String.</li>
 *    <li><code>"Invalid format"</code> means one of the parameters
 *    contain an unusable setting.</li>
 *  </ul>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:NumberFormatter&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:NumberFormatter
 *    decimalSeparatorFrom="."
 *    decimalSeparatorTo="."
 *    precision="-1"
 *    rounding="none|up|down|nearest"
 *    thousandsSeparatorFrom=","
 *    thousandsSeparatorTo=","
 *    useNegativeSign="true|false"
 *    useThousandsSeparator="true|false"/>  
 *  </pre>
 *  
 *  @includeExample examples/NumberFormatterExample.mxml
 *  
 *  @see mx.formatters.NumberBase
 *  @see mx.formatters.NumberBaseRoundType
 */
public class NumberFormatter extends Formatter
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
	public function NumberFormatter()
	{
		super();
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
	 *  when parsing an input String.
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
	 *  is set to <code>NumberBaseRoundType.NONE</code>, return a value of 1.453.
	 *  If <code>precision</code> is <code>-1</code> and you have set some form of 
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
					     "formatters", "numberFormatterPrecision");
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
    	// !!@ Should enumeration include null?

    /**
     *  Specifies how to round the number.
     *
	 *  <p>In ActionScript, you can use the following constants to set this property: 
	 *  <code>NumberBaseRoundType.NONE</code>, <code>NumberBaseRoundType.UP</code>,
	 *  <code>NumberBaseRoundType.DOWN</code>, or <code>NumberBaseRoundType.NEAREST</code>.
     *  Valid MXML values are "down", "nearest", "up", and "none".</p>
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
								      "SharedResources",
								      "thousandsSeparatorFrom");
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
	 *  in the output String.
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
									"SharedResources",
									"thousandsSeparatorTo");
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
     *  Formats the number as a String.
	 *  If <code>value</code> cannot be formatted, return an empty String 
	 *  and write a description of the error to the <code>error</code> property.
	 *
     *  @param value Value to format.
	 *
     *  @return Formatted String. Empty if an error occurs.
     */
    override public function format(value:Object):String
    {
        // Reset any previous errors.
        if (error)
			error = null;

        if (useThousandsSeparator &&
			((decimalSeparatorFrom == thousandsSeparatorFrom) ||
			 (decimalSeparatorTo == thousandsSeparatorTo)))
        {
            error = defaultInvalidFormatError;
            return "";
        }

        if (decimalSeparatorTo == "" || !isNaN(Number(decimalSeparatorTo)))
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
            var front:String = useThousandsSeparator ?
							   dataFormatter.formatThousands(String(numArrTemp[0])) :
							   String(numArrTemp[0]);
            if (numArrTemp[1] != null && numArrTemp[1] != "")
                numStr = front + decimalSeparatorTo + numArrTemp[1];
            else
                numStr = front;
        }
        else if (Math.abs(numValue) > 0)
        {
        	// Check if the string is in scientific notation
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

        return numStr;
    }
}

}
