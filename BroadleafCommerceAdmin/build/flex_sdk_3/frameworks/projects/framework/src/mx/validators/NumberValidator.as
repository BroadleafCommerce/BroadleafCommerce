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

package mx.validators
{

import mx.managers.ISystemManager;
import mx.managers.SystemManager;

[ResourceBundle("validators")]

/**
 *  The NumberValidator class ensures that a String represents a valid number.
 *  It can ensure that the input falls within a given range
 *  (specified by <code>minValue</code> and <code>maxValue</code>),
 *  is an integer (specified by <code>domain</code>),
 *  is non-negative (specified by <code>allowNegative</code>),
 *  and does not exceed the specified <code>precision</code>.
 *  The validator correctly validates formatted numbers (e.g., "12,345.67")
 *  and you can customize the <code>thousandsSeparator</code> and
 *  <code>decimalSeparator</code> properties for internationalization.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:NumberValidator&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:NumberValidator 
 *    allowNegative="true|false" 
 *    decimalPointCountError="The decimal separator can only occur once." 
 *    decimalSeparator="." 
 *    domain="real|int" 
 *    exceedsMaxError="The number entered is too large." 
 *    integerError="The number must be an integer." 
 *    invalidCharError="The input contains invalid characters." 
 *    invalidFormatCharsError="One of the formatting parameters is invalid." 
 *    lowerThanMinError="The amount entered is too small." 
 *    maxValue="NaN" 
 *    minValue="NaN" 
 *    negativeError="The amount may not be negative." 
 *    precision="-1" 
 *    precisionError="The amount entered has too many digits beyond the decimal point." 
 *    separationError="The thousands separator must be followed by three digits." 
 *    thousandsSeparator="," 
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/NumberValidatorExample.mxml
 */
public class NumberValidator extends Validator
{
	include "../core/Version.as";

    /**
     *  Convenience method for calling a validator
	 *  from within a custom validation function.
	 *  Each of the standard Flex validators has a similar convenience method.
	 *
	 *  @param validator The NumberValidator instance.
	 *
	 *  @param value A field to validate.
	 *
     *  @param baseField Text representation of the subfield
	 *  specified in the <code>value</code> parameter.
	 *  For example, if the <code>value</code> parameter specifies value.number,
	 *  the <code>baseField</code> value is "number".
	 *
	 *  @return An Array of ValidationResult objects, with one ValidationResult 
	 *  object for each field examined by the validator. 
	 *
	 *  @see mx.validators.ValidationResult
     */
    public static function validateNumber(validator:NumberValidator,
										  value:Object,
										  baseField:String):Array
    {
		var results:Array = [];

		// Resource-backed properties of the validator.
		var allowNegative:Boolean = validator.allowNegative;		
		var decimalSeparator:String = validator.decimalSeparator;
		var domain:String = validator.domain;	
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
		// are not digits or the negative sign,
		// and that the separators are one character.
        var invalidFormChars:String = DECIMAL_DIGITS + "-";

        if (decimalSeparator == thousandsSeparator ||
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
        var validChars:String = DECIMAL_DIGITS + "-" +
        						decimalSeparator + thousandsSeparator;
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
			if (len == 1) // we have only '-' char
			{
                results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
			}
			else if (len == 2 && input.charAt(1) == '.') // handle "-."
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

            // Strip off the minus sign, update some variables.
            input = input.substring(1);
            len--;
            isNegative = true;
        }

        // Make sure there's only one decimal point.
        if (input.indexOf(decimalSeparator) !=
			input.lastIndexOf(decimalSeparator))
        {
            results.push(new ValidationResult(
				true, baseField, "decimalPointCount",
				validator.decimalPointCountError));
			return results;
        }

        // Make sure every character after the decimal is a digit,
		// and that there aren't too many digits after the decimal point:
        // if domain is int there should be none,
		// otherwise there should be no more than specified by precision.
        var decimalSeparatorIndex:Number = input.indexOf(decimalSeparator);
        if (decimalSeparatorIndex != -1)
        {
            var numDigitsAfterDecimal:Number = 0;

			if (i == 1 && i == len) // we only have a '.'
			{
            	results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
			}
			
            for (i = decimalSeparatorIndex + 1; i < len; i++)
            {
                // This character must be a digit.
                if (DECIMAL_DIGITS.indexOf(input.charAt(i)) == -1)
                {
                    results.push(new ValidationResult(
						true, baseField, "invalidChar",
						validator.invalidCharError));
					return results;
                }

                ++numDigitsAfterDecimal;

                // There may not be any non-zero digits after the decimal
				// if domain is int.
                if (domain == "int" && input.charAt(i) != "0")
                {
                    results.push(new ValidationResult(
						true, baseField,"integer",
						validator.integerError));
					return results;
                }

                // Make sure precision is not exceeded.
                if (precision != -1 &&
					numDigitsAfterDecimal > precision)
                {
                    results.push(new ValidationResult(
						true, baseField, "precision",
						validator.precisionError));
					return results;
                }
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
        // If it's a thousands separator,
		// make sure it's followed by three consecutive digits.
        var end:int = decimalSeparatorIndex == -1 ?
					  len :
					  decimalSeparatorIndex;
        for (i = 1; i < end; i++)
        {
            c = input.charAt(i);
            if (c == thousandsSeparator)
            {
                if (c == thousandsSeparator)
                {
                    if ((end - i != 4 &&
						 input.charAt(i + 4) != thousandsSeparator) ||
                        DECIMAL_DIGITS.indexOf(input.charAt(i + 1)) == -1 ||
                        DECIMAL_DIGITS.indexOf(input.charAt(i + 2)) == -1 ||
                        DECIMAL_DIGITS.indexOf(input.charAt(i + 3)) == -1)
                    {
                        results.push(new ValidationResult(
							true, baseField, "separation",
							validator.separationError));
						return results;
                    }
                }
            }
            else if (DECIMAL_DIGITS.indexOf(c) == -1)
            {
                results.push(new ValidationResult(
					true, baseField,"invalidChar",
					validator.invalidCharError));
				return results;
            }
        }

        // Make sure the input is within the specified range.
        if (!isNaN(minValue) || !isNaN(maxValue))
        {
            // First strip off the thousands separators.
            for (i = 0; i < end; i++)
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
	public function NumberValidator()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

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
	 *  Valid values are <code>true</code> or <code>false</code>.
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
     *  <code>thousandsSeparator</code>.
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
	//  domain
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the domain property.
	 */
	private var _domain:String;
	
    /**
	 *  @private
	 */
	private var domainOverride:String;

    [Inspectable(category="General", enumeration="int,real", defaultValue="null")]

    /**
     *  Type of number to be validated.
	 *  Permitted values are <code>"real"</code> and <code>"int"</code>.
	 *
	 *  @default "real"
     */
	public function get domain():String
	{
		return _domain;
	}

	/**
	 *  @private
	 */
	public function set domain(value:String):void
	{
		domainOverride = value;

		_domain = value != null ?
				  value :
				  resourceManager.getString(
				      "validators", "numberValidatorDomain");
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
     *  Maximum value for a valid number. A value of NaN means there is no maximum.
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
     *  Minimum value for a valid number. A value of NaN means there is no minimum.
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
	 *  Can be any nonnegative integer. 
	 *  Note: Setting to <code>0</code> has the same effect
	 *  as setting <code>domain</code> to <code>"int"</code>.
	 *  A value of -1 means it is ignored.
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
					     "validators", "numberValidatorPrecision");
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

    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The character used to separate thousands
	 *  in the whole part of the number.
	 *  Cannot be a digit and must be distinct from the
     *  <code>decimalSeparator</code>.
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
	 *  @default "The decimal separator can occur only once."
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
     *  Error message when the value exceeds the <code>maxValue</code> property.
	 *
	 *  @default "The number entered is too large."
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
						       "validators", "exceedsMaxErrorNV");
	}
	
	//----------------------------------
	//  integerError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the integerError property.
	 */
	private var _integerError:String;
	
    /**
	 *  @private
	 */
	private var integerErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /**
     *  Error message when the number must be an integer, as defined 
     * by the <code>domain</code> property.
	 *
	 *  @default "The number must be an integer."
     */
	public function get integerError():String
	{
		return _integerError;
	}

	/**
	 *  @private
	 */
	public function set integerError(value:String):void
	{
		integerErrorOverride = value;

		_integerError = value != null ?
						value :
						resourceManager.getString(
						    "validators", "integerError");
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
     *  Error message when the value contains invalid characters.
	 *
	 *  @default The input contains invalid characters."
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
     *  Error message when the value contains invalid format characters, which means that 
     *  it contains a digit or minus sign (-) as a separator character, 
     *  or it contains two or more consecutive separator characters.
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
     *  Error message when the value is negative and the 
     *  <code>allowNegative</code> property is <code>false</code>.
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
     *  Error message when the value has a precision that exceeds the value defined 
     *  by the precision property.
	 *
	 *  @default "The amount entered has too many digits beyond the decimal point."
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
     *  Error message when the thousands separator is in the wrong location.
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

		allowNegative = allowNegativeOverride;		
		decimalSeparator = decimalSeparatorOverride;
		domain = domainOverride;	
		maxValue = maxValueOverride;
		minValue = minValueOverride;
		precision = precisionOverride;
		thousandsSeparator = thousandsSeparatorOverride;

		decimalPointCountError = decimalPointCountErrorOverride;
		exceedsMaxError = exceedsMaxErrorOverride;
		integerError = integerErrorOverride;
		invalidCharError = invalidCharErrorOverride;
		invalidFormatCharsError = invalidFormatCharsErrorOverride;
		lowerThanMinError = lowerThanMinErrorOverride;
		negativeError = negativeErrorOverride;
		precisionError = precisionErrorOverride;
		separationError = separationErrorOverride;
	}

    /**
     *  Override of the base class <code>doValidation()</code> method 
     *  to validate a number.
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
		// or if the required property is set to <code>false</code> and length is 0.
		var val:String = value ? String(value) : "";
		if (results.length > 0 || ((val.length == 0) && !required))
			return results;
		else
		    return NumberValidator.validateNumber(this, value, null);
    }
}

}
