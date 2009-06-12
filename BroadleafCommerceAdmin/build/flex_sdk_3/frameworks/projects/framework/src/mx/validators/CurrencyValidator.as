////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.validators
{

import mx.managers.ISystemManager;
import mx.managers.SystemManager;

[ResourceBundle("SharedResources")]
[ResourceBundle("validators")]

/**
 *  The CurrencyValidator class ensures that a String
 *  represents a valid currency expression.
 *  It can make sure the input falls within a given range
 *  (specified by <code>minValue</code> and <code>maxValue</code>),
 *  is non-negative (specified by <code>allowNegative</code>),
 *  and does not exceed the specified <code>precision</code>. The 
 *  CurrencyValidator class correctly validates formatted and unformatted
 *  currency expressions, e.g., "$12,345.00" and "12345".
 *  You can customize the <code>currencySymbol</code>, <code>alignSymbol</code>,
 *  <code>thousandsSeparator</code>, and <code>decimalSeparator</code>
 *  properties for internationalization.
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:CurrencyValidator&gt;</code> tag
 *  inherits all of the tag properties of its superclass,
 *  and adds the following tag properties:</p>
 *
 *  <pre>
 *  &lt;mx:CurrencyValidator
 *    alignSymbol="left|right|any"
 *    allowNegative="true|false"
 *    currencySymbol="$"
 *    currencySymbolError="The currency symbol occurs in an invalid location."
 *    decimalPointCountError="The decimal separator can occur only once."
 *    decimalSeparator="."
 *    exceedsMaxError="The amount entered is too large."
 *    invalidCharError="The input contains invalid characters."
 *    invalidFormatCharsError="One of the formatting parameters is invalid."
 *    lowerThanMinError="The amount entered is too small."
 *    maxValue="NaN"
 *    minValue="NaN"
 *    negativeError="The amount may not be negative."
 *    precision="2"
 *    precisionError="The amount entered has too many digits beyond the decimal point."
 *    separationError="The thousands separator must be followed by three digits."
 *    thousandsSeparator=","
 *  /&gt;
 *  </pre>
 *
 *  @see mx.validators.CurrencyValidatorAlignSymbol
 *
 *  @includeExample examples/CurrencyValidatorExample.mxml
 */
public class CurrencyValidator extends Validator
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Formatting characters for negative values.
	 */
	private static const NEGATIVE_FORMATTING_CHARS:String = "-()";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Convenience method for calling a validator.
	 *  Each of the standard Flex validators has a similar convenience method.
	 *
     *  @param validator The CurrencyValidator instance.
	 *
	 *  @param value The object to validate.
	 *
	 *  @param baseField Text representation of the subfield
	 *  specified in the <code>value</code> parameter.
	 *  For example, if the <code>value</code> parameter specifies value.currency,
	 *  the baseField value is "currency".
	 *
	 *  @return An Array of ValidationResult objects, with one ValidationResult 
	 *  object for each field examined by the validator. 
	 *
	 *  @see mx.validators.ValidationResult
     */
    public static function validateCurrency(validator:CurrencyValidator,
											value:Object,
											baseField:String):Array
    {
		var results:Array = [];
		
		// Resource-backed properties of the validator.
		var alignSymbol:String = validator.alignSymbol;
		var allowNegative:Boolean = validator.allowNegative;		
		var currencySymbol:String = validator.currencySymbol;
		var decimalSeparator:String = validator.decimalSeparator;
		var maxValue:Number = Number(validator.maxValue);
		var minValue:Number = Number(validator.minValue);
		var precision:int = int(validator.precision);
		var thousandsSeparator:String = validator.thousandsSeparator;

        var input:String = String(value);
        var len:int = input.length;
		
		var isNegative:Boolean = false;
		
		var i:int;
		var c:String;

        // Make sure the formatting character parameters are unique,
		// are not digits or negative formatting characters,
		// and that the separators are one character.
        var invalidFormChars:String = DECIMAL_DIGITS + NEGATIVE_FORMATTING_CHARS;

        if (currencySymbol == thousandsSeparator ||
            currencySymbol == decimalSeparator ||
            decimalSeparator == thousandsSeparator ||
            invalidFormChars.indexOf(currencySymbol) != -1 ||
            invalidFormChars.indexOf(decimalSeparator) != -1 ||
            invalidFormChars.indexOf(thousandsSeparator) != -1 ||
            decimalSeparator.length != 1 ||
            thousandsSeparator.length != 1)
        {
			results.push(new ValidationResult(
				true, baseField, "invalidFormatChar",
				validator.invalidFormatCharsError));
			return results;
        }

        // Check for invalid characters in input.
        var validChars:String = 
        	DECIMAL_DIGITS + NEGATIVE_FORMATTING_CHARS +
			currencySymbol + decimalSeparator + thousandsSeparator;
        for (i = 0; i < len; i++)
        {
            c = input.charAt(i);

            if (validChars.indexOf(c) == -1)
            {
                results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
            }
        }

        // Check if the input is negative.
        if (input.charAt(0) == "-")
        {
            // Check if negative input is allowed.
            if (!allowNegative)
            {
                results.push(new ValidationResult(
					true, baseField, "negative",
					validator.negativeError));
				return results;
            }

            // Strip off the negative formatting and update some variables.
            input = input.substring(1);
            len--;
            isNegative = true;
        }

        else if (input.charAt(0) == "(")
        {
            // Make sure the last character is a closed parenthesis.
            if (input.charAt(len - 1) != ")")
            {
                results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
            }

            // Check if negative input is allowed.
            if (!allowNegative)
            {
                results.push(new ValidationResult(
					true, baseField, "negative",
					validator.negativeError));
				return results;
            }

            // Strip off the negative formatting and update some variables.
            input = input.substring(1,len-2);
            len -= 2;
            isNegative = true;
        }

        // Find the currency symbol if it exists,
		// then make sure that it's in the right place
		// and that there is only one.
        if ((input.charAt(0) == currencySymbol &&
			 alignSymbol == CurrencyValidatorAlignSymbol.RIGHT) ||
            (input.charAt(len - 1) == currencySymbol &&
			 alignSymbol == CurrencyValidatorAlignSymbol.LEFT) ||
            (len > 2 &&
			 input.substring(1, len - 1).indexOf(currencySymbol) != -1) ||
            (input.indexOf(currencySymbol) !=
			 input.lastIndexOf(currencySymbol)))
        {
            results.push(new ValidationResult(
				true, baseField, "currencySymbol",
				validator.currencySymbolError));
			return results;
        }

        // Now that we know it's in the right place,
		// strip off the currency symbol if it exists.
        var currencySymbolIndex:int = input.indexOf(currencySymbol);
        if (currencySymbolIndex != -1)
        {
            if (currencySymbolIndex) // if it's at the end
                input = input.substring(0, len - 1);
            else // it's at the beginning
                input = input.substring(1);
            len--;
        }

        // Make sure there is only one decimal point.
        if (input.indexOf(decimalSeparator) !=
			input.lastIndexOf(decimalSeparator))
        {
            results.push(new ValidationResult(
				true, baseField, "decimalPointCount",
				validator.decimalPointCountError));
			return results;
        }

        // Make sure that every character after the decimal point
		// is a digit and that the precision is not exceeded.
        var decimalSeparatorIndex:int = input.indexOf(decimalSeparator);
        var numDigitsAfterDecimal:int = 0;

        // If there is no decimal separator, act like there is one at the end.
        if (decimalSeparatorIndex == -1)
          decimalSeparatorIndex = len;

        for (i = decimalSeparatorIndex + 1; i < len; i++)
        {
			if (DECIMAL_DIGITS.indexOf(input.charAt(i)) == -1)
			{
				results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
			}

			++numDigitsAfterDecimal;

			// Make sure precision is not exceeded.
			if (precision != -1 && numDigitsAfterDecimal > precision)
			{
				results.push(new ValidationResult(
					true, baseField, "precision",
					validator.precisionError));
				return results;
			}
		}

        // Make sure the input begins with a digit or a decimal point.
        if (DECIMAL_DIGITS.indexOf(input.charAt(0)) == -1 &&
			input.charAt(0) != decimalSeparator)
        {
            results.push(new ValidationResult(
				true, baseField, "invalidChar",
				validator.invalidCharError));
			return results;
        }

        // Make sure that every character before the decimal point
		// is a digit or is a thousands separator.
        // If it's a thousands separator, make sure it's followed
		// by three consecutive digits, and then make sure the next character
		// is valid (i.e., either thousands separator, decimal separator,
		// or nothing).
        var validGroupEnder:String = thousandsSeparator + decimalSeparator;
        for (i = 1; i < decimalSeparatorIndex; i++)
        {
            c = input.charAt(i);

            if (c == thousandsSeparator)
            {
                if (input.substring(i + 1, i + 4).length < 3 ||
                    DECIMAL_DIGITS.indexOf(input.charAt(i + 1)) == -1 ||
                    DECIMAL_DIGITS.indexOf(input.charAt(i + 2)) == -1 ||
                    DECIMAL_DIGITS.indexOf(input.charAt(i + 3)) == -1 ||
                    validGroupEnder.indexOf(input.charAt(i + 4)) == -1)
                {
                	results.push(new ValidationResult(
						true, baseField, "separation",
						validator.separationError));
					return results;
                }
            }
            else if (DECIMAL_DIGITS.indexOf(c) == -1)
            {
                results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
            }
        }

        // Make sure the input is within the specified range.
        if (!isNaN(minValue) || !isNaN(maxValue))
        {
            // First strip off the thousands separators.
            for (i = 0; i < decimalSeparatorIndex; i++)
            {
                if (input.charAt(i) == thousandsSeparator)
                {
                    var left:String = input.substring(0, i);
                    var right:String = input.substring(i + 1);
                    input = left + right;
                }
            }

            // Check bounds

			var x:Number = Number(input);

			if (isNegative)
                x = -x;

			if (!isNaN(minValue) && x < minValue)
            {
                results.push(new ValidationResult(
					true, baseField, "lowerThanMin",
					validator.lowerThanMinError));
				return results;
            }

			if (!isNaN(maxValue) && x > maxValue)
            {
                results.push(new ValidationResult(
					true, baseField, "exceedsMax",
					validator.exceedsMaxError));
				return results;
            }
        }

        return results;
    }

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
    public function CurrencyValidator()
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
     *  Specifies the alignment of the <code>currencySymbol</code>
	 *  relative to the rest of the expression.
	 *  Acceptable values in ActionScript are <code>CurrencyValidatorAlignSymbol.LEFT</code>, 
	 *  <code>CurrencyValidatorAlignSymbol.RIGHT</code>, and 
	 *  <code>CurrencyValidatorAlignSymbol.ANY</code>.
	 *  Acceptable values in MXML are <code>"left"</code>, 
	 *  <code>"right"</code>, and 
	 *  <code>"any"</code>.
	 * 
	 *  @default CurrencyValidatorAlignSymbol.LEFT
     *
     *  @see mx.validators.CurrencyValidatorAlignSymbol
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
	//  allowNegative
	//----------------------------------
	
    /**
	 *  @private
	 *  Storage for the allowNegative property.
	 */
	private var _allowNegative:Object;
	
    /**
	 *  @private
	 */
	private var allowNegativeOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Specifies whether negative numbers are permitted.
	 *  Can be <code>true</code> or <code>false</code>.
	 *  
	 *  @default true
     */
	public function get allowNegative():Object
	{
		return _allowNegative;
	}

	/**
	 *  @private
	 */
	public function set allowNegative(value:Object):void
	{
		allowNegativeOverride = value;

		_allowNegative = value != null ?
						 Boolean(value) :
						 resourceManager.getBoolean(
						     "validators", "allowNegative");
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
     *  The single-character String used to specify the currency symbol, 
     *  such as "$" or "&#163;".
	 *  Cannot be a digit and must be distinct from the
     *  <code>thousandsSeparator</code> and the <code>decimalSeparator</code>.
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
	//  decimalSeparator
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the decimalSeparator property.
	 */
	private var _decimalSeparator:String;
	
    /**
	 *  @private
	 */
	private var decimalSeparatorOverride:String;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The character used to separate the whole
	 *  from the fractional part of the number.
	 *  Cannot be a digit and must be distinct from the
     *  <code>currencySymbol</code> and the <code>thousandsSeparator</code>.
	 *  
	 *  @default "."
     */	
	public function get decimalSeparator():String
	{
		return _decimalSeparator;
	}

	/**
	 *  @private
	 */
	public function set decimalSeparator(value:String):void
	{
		decimalSeparatorOverride = value;

		_decimalSeparator = value != null ?
							value :
							resourceManager.getString(
								"validators", "decimalSeparator");
	}

	//----------------------------------
	//  maxValue
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the maxValue property.
	 */
	private var _maxValue:Object;
	
    /**
	 *  @private
	 */
	private var maxValueOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Maximum value for a valid number.
	 *  A value of NaN means it is ignored.
	 *  
	 *  @default NaN
     */
	public function get maxValue():Object
	{
		return _maxValue;
	}

	/**
	 *  @private
	 */
	public function set maxValue(value:Object):void
	{
		maxValueOverride = value;

		_maxValue = value != null ?
					Number(value) :
					resourceManager.getNumber(
						"validators", "maxValue");
	}

	//----------------------------------
	//  minValue
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the minValue property.
	 */
	private var _minValue:Object;
	
    /**
	 *  @private
	 */
	private var minValueOverride:Object;
	
    [Inspectable(category="General", defaultValue="null")]

    /**
     *  Minimum value for a valid number.
	 *  A value of NaN means it is ignored.
	 *  
	 *  @default NaN
     */
	public function get minValue():Object
	{
		return _minValue;
	}

	/**
	 *  @private
	 */
	public function set minValue(value:Object):void
	{
		minValueOverride = value;

		_minValue = value != null ?
					Number(value) :
					resourceManager.getNumber(
						"validators", "minValue");
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
     *  The maximum number of digits allowed to follow the decimal point.
	 *  Can be any non-negative integer.
	 *  Note: Setting to <code>0</code>
     *  has the same effect as setting <code>NumberValidator.domain</code>
	 *  to <code>int</code>.
	 *	Setting it to -1, means it is ignored.
	 * 
	 *  @default 2
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
					     "validators", "currencyValidatorPrecision");
	}

	//----------------------------------
	//  thousandsSeparator
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the thousandsSeparator property.
	 */
	private var _thousandsSeparator:String;
	
    /**
	 *  @private
	 */
	private var thousandsSeparatorOverride:String;
	
    [Inspectable(category="General", defaultValue=",")]

    /**
     *  The character used to separate thousands.
	 *  Cannot be a digit and must be distinct from the
     *  <code>currencySymbol</code> and the <code>decimalSeparator</code>.
	 *  
	 *  @default ","
     */
	public function get thousandsSeparator():String
	{
		return _thousandsSeparator;
	}

	/**
	 *  @private
	 */
	public function set thousandsSeparator(value:String):void
	{
		thousandsSeparatorOverride = value;

		_thousandsSeparator = value != null ?
							  value :
							  resourceManager.getString(
							      "validators", "thousandsSeparator");
	}

	//--------------------------------------------------------------------------
	//
	//  Properties: Errors
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  currencySymbolError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the currencySymbolError property.
	 */
	private var _currencySymbolError:String;
	
    /**
	 *  @private
	 */
	private var currencySymbolErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the currency symbol, defined by <code>currencySymbol</code>,
     *  is in the wrong location.
	 *  
	 *  @default "The currency symbol occurs in an invalid location."
     */
	public function get currencySymbolError():String
	{
		return _currencySymbolError;
	}

	/**
	 *  @private
	 */
	public function set currencySymbolError(value:String):void
	{
		currencySymbolErrorOverride = value;

		_currencySymbolError = value != null ?
							   value :
							   resourceManager.getString(
							       "validators", "currencySymbolError");
	}

	//----------------------------------
	//  decimalPointCountError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the decimalPointCountError property.
	 */
	private var _decimalPointCountError:String;
	
    /**
	 *  @private
	 */
	private var decimalPointCountErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the decimal separator character occurs more than once.
	 *  
	 *  @default "The decimal separator can only occur once."
     */
	public function get decimalPointCountError():String
	{
		return _decimalPointCountError;
	}

	/**
	 *  @private
	 */
	public function set decimalPointCountError(value:String):void
	{
		decimalPointCountErrorOverride = value;

		_decimalPointCountError = value != null ?
								  value :
								  resourceManager.getString(
								      "validators", "decimalPointCountError");
	}

	//----------------------------------
	//  exceedsMaxError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the exceedsMaxError property.
	 */
	private var _exceedsMaxError:String;
	
    /**
	 *  @private
	 */
	private var exceedsMaxErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the value is greater than <code>maxValue</code>.
	 *  
	 *  @default "The amount entered is too large."
     */
	public function get exceedsMaxError():String
	{
		return _exceedsMaxError;
	}

	/**
	 *  @private
	 */
	public function set exceedsMaxError(value:String):void
	{
		exceedsMaxErrorOverride = value;

		_exceedsMaxError = value != null ?
						   value :
						   resourceManager.getString(
						       "validators", "exceedsMaxErrorCV");
	}

	//----------------------------------
	//  invalidCharError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the invalidCharError property.
	 */
	private var _invalidCharError:String;
	
    /**
	 *  @private
	 */
	private var invalidCharErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the currency contains invalid characters.
	 *  
	 *  @default "The input contains invalid characters."
     */	
	public function get invalidCharError():String
	{
		return _invalidCharError;
	}

	/**
	 *  @private
	 */
	public function set invalidCharError(value:String):void
	{
		invalidCharErrorOverride = value;

		_invalidCharError = value != null ?
							value :
							resourceManager.getString(
								"validators", "invalidCharError");
	}

	//----------------------------------
	//  invalidFormatCharsError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the invalidFormatCharsError property.
	 */
	private var _invalidFormatCharsError:String;
	
    /**
	 *  @private
	 */
	private var invalidFormatCharsErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the value contains an invalid formatting character.
	 *  
	 *  @default "One of the formatting parameters is invalid."
     */
	public function get invalidFormatCharsError():String
	{
		return _invalidFormatCharsError;
	}

	/**
	 *  @private
	 */
	public function set invalidFormatCharsError(value:String):void
	{
		invalidFormatCharsErrorOverride = value;

		_invalidFormatCharsError = value != null ?
								   value :
								   resourceManager.getString(
								       "validators", "invalidFormatCharsError");
	}

	//----------------------------------
	//  lowerThanMinError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the lowerThanMinError property.
	 */
	private var _lowerThanMinError:String;
	
    /**
	 *  @private
	 */
	private var lowerThanMinErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the value is less than <code>minValue</code>.
	 *  
	 *  @default "The amount entered is too small."
     */
	public function get lowerThanMinError():String
	{
		return _lowerThanMinError;
	}

	/**
	 *  @private
	 */
	public function set lowerThanMinError(value:String):void
	{
		lowerThanMinErrorOverride = value;

		_lowerThanMinError = value != null ?
							 value :
							 resourceManager.getString(
							     "validators", "lowerThanMinError");
	}

	//----------------------------------
	//  negativeError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the negativeError property.
	 */
	private var _negativeError:String;
	
    /**
	 *  @private
	 */
	private var negativeErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the value is negative and
     *  the <code>allowNegative</code> property is <code>false</code>.
	 *  
	 *  @default "The amount may not be negative."
     */
	public function get negativeError():String
	{
		return _negativeError;
	}

	/**
	 *  @private
	 */
	public function set negativeError(value:String):void
	{
		negativeErrorOverride = value;

		_negativeError = value != null ?
						 value :
						 resourceManager.getString(
						     "validators", "negativeError");
	}

	//----------------------------------
	//  precisionError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the precisionError property.
	 */
	private var _precisionError:String;
	
    /**
	 *  @private
	 */
	private var precisionErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the value has a precision that exceeds the value
     *  defined by the <code>precision</code> property.
	 *  
	 *  @default "The amount entered has too many digits beyond 
	 *  the decimal point."
     */
	public function get precisionError():String
	{
		return _precisionError;
	}

	/**
	 *  @private
	 */
	public function set precisionError(value:String):void
	{
		precisionErrorOverride = value;

		_precisionError = value != null ?
						  value :
						  resourceManager.getString(
						      "validators", "precisionError");
	}

	//----------------------------------
	//  separationError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the separationError property.
	 */
	private var _separationError:String;
	
    /**
	 *  @private
	 */
	private var separationErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the thousands separator is incorrectly placed.
	 *  
	 *  @default "The thousands separator must be followed by three digits."
     */
	public function get separationError():String
	{
		return _separationError;
	}

	/**
	 *  @private
	 */
	public function set separationError(value:String):void
	{
		separationErrorOverride = value;

		_separationError = value != null ?
						   value :
						   resourceManager.getString(
						       "validators", "separationError");
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
		allowNegative = allowNegativeOverride;		
		currencySymbol = currencySymbolOverride;
		decimalSeparator = decimalSeparatorOverride;
		maxValue = maxValueOverride;
		minValue = minValueOverride;
		precision = precisionOverride;
		thousandsSeparator = thousandsSeparatorOverride;
		
		currencySymbolError = currencySymbolErrorOverride;
		decimalPointCountError = decimalPointCountErrorOverride;
		exceedsMaxError = exceedsMaxErrorOverride;
		invalidCharError = invalidCharErrorOverride;
		invalidFormatCharsError = invalidFormatCharsErrorOverride;
		lowerThanMinError = lowerThanMinErrorOverride;
		negativeError = negativeErrorOverride;
		precisionError = precisionErrorOverride;
		separationError = separationErrorOverride;
	}

    /**
     *  Override of the base class <code>doValidation()</code> method
     *  to validate a currency expression.
     *
	 *  <p>You do not call this method directly;
	 *  Flex calls it as part of performing a validation.
	 *  If you create a custom Validator class, you must implement this method. </p>
	 *
     *  @param value Object to validate.
     *
	 *  @return An Array of ValidationResult objects, with one ValidationResult 
	 *  object for each field examined by the validator. 
     */
    override protected function doValidation(value:Object):Array
    {
        var results:Array = super.doValidation(value);
		
		// Return if there are errors
		// or if the required property is set to false and length is 0.
		var val:String = value ? String(value) : "";
		if (results.length > 0 || ((val.length == 0) && !required))
			return results;
		else
		    return CurrencyValidator.validateCurrency(this, value, null);
    }
}

}
