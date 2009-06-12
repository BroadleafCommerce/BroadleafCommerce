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
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

[ResourceBundle("validators")]

/**
 *  The ZipCodeValidator class validates that a String
 *  has the correct length and format for a five-digit ZIP code,
 *  a five-digit+four-digit United States ZIP code, or Canadian postal code.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ZipCodeValidator&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:ZipCodeValidator
 *    allowedFormatChars=" -" 
 *    domain="US Only | US or Canada"
 *    invalidCharError="The ZIP code contains invalid characters." 
 *    invalidDomainError="The domain parameter is invalid. It must be either 'US Only' or 'US or Canada'." 
 *    wrongCAFormatError="The Canadian postal code must be formatted 'A1B 2C3'." 
 *    wrongLengthError="The ZIP code must be 5 digits or 5+4 digits." 
 *    wrongUSFormatError="The ZIP+4 code must be formatted '12345-6789'." 
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.validators.ZipCodeValidatorDomainType
 * 
 *  @includeExample examples/ZipCodeValidatorExample.mxml
 */
public class ZipCodeValidator extends Validator
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
	private static const DOMAIN_US:uint = 1;
    
    /**
	 *  @private
	 */
	private static const DOMAIN_US_OR_CANADA:uint = 2;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Convenience method for calling a validator.
	 *  Each of the standard Flex validators has a similar convenience method.
	 *
	 *  @param validator The ZipCodeValidator instance.
	 *
	 *  @param value A field to validate.
	 *
	 *  @param baseField Text representation of the subfield
	 *  specified in the <code>value</code> parameter.
	 *  For example, if the <code>value</code> parameter specifies value.zipCode,
	 *  the <code>baseField</code> value is <code>"zipCode"</code>.
     *
	 *  @return An Array of ValidationResult objects, with one ValidationResult 
	 *  object for each field examined by the validator. 
	 *
	 *  @see mx.validators.ValidationResult
	 *
	 */
    public static function validateZipCode(validator:ZipCodeValidator,
										   value:Object,
										   baseField:String):Array
    {
		var results:Array = [];
	
		// Resource-backed properties of the validator.
		var allowedFormatChars:String = validator.allowedFormatChars;
		var domain:String = validator.domain;

		var resourceManager:IResourceManager = ResourceManager.getInstance();

        var zip:String = String(value);
        var len:int = zip.length;
        
		var domainType:uint = DOMAIN_US;
        if (domain == ZipCodeValidatorDomainType.US_OR_CANADA)
		{
            domainType = DOMAIN_US_OR_CANADA;
        }
        else if (domain == ZipCodeValidatorDomainType.US_ONLY)
		{
            domainType = DOMAIN_US;
        }
        else
        {
			results.push(new ValidationResult(
				true, baseField, "invalidDomain",
				validator.invalidDomainError));
            return results;
        }

		var n:int;
		var i:int;
		var c:String;
		
        // Make sure localAllowedFormatChars contains no numbers or letters.
        n = allowedFormatChars.length;
		for (i = 0; i < n; i++)
        {
            c = allowedFormatChars.charAt(i);
            if (DECIMAL_DIGITS.indexOf(c) != -1 ||
                ROMAN_LETTERS.indexOf(c) != -1)
            {
				var message:String = resourceManager.getString(
					"validators", "invalidFormatCharsZCV");
				throw new Error(message);
            }
        }

        // Now start checking the ZIP code.
        // At present, only US and Canadian ZIP codes are supported.
		// As a result, the easiest thing to check first
		// to determine the domain is the length.
		// A length of 5 or 10 means a US ZIP code
		// and a length of 6 or 7 means a Canadian ZIP.
        // If more countries are supported in the future, it may make sense
		// to check other conditions first depending on the domain specified
		// and all the possible ZIP code formats for that domain.
		// For now, this approach makes the most sense.

        // Find out if the ZIP code contains any letters.
        var containsLetters:Boolean = false;
        for (i = 0; i < len; i++)
        {
            if (ROMAN_LETTERS.indexOf(zip.charAt(i)) != -1)
            {
                containsLetters = true;
                break;
            }
        }

        // Make sure there are no invalid characters in the ZIP.
        for (i = 0; i < len; i++)
        {
            c = zip.charAt(i);
            
            if (ROMAN_LETTERS.indexOf(c) == -1 &&
                DECIMAL_DIGITS.indexOf(c) == -1 &&
                allowedFormatChars.indexOf(c) == -1)
            {
				results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
                return results;
            }
        }

        if (len == 5 || len == 9 || len == 10) // US ZIP code
        {
            // Make sure the first 5 characters are all digits.
            for (i = 0; i < 5; i++)
            {
                if (DECIMAL_DIGITS.indexOf(zip.charAt(i)) == -1)
                {
					results.push(new ValidationResult(
						true, baseField, "wrongUSFormat",
						validator.wrongUSFormatError));
                    return results;
                }
            }
            
            if (len == 9 || len == 10)
            {
                if (len == 10)
                {
                    // Make sure the 6th character
					// is an allowed formatting character.
                    if (allowedFormatChars.indexOf(zip.charAt(5)) == -1)
                    {
						results.push(new ValidationResult(
							true, baseField, "wrongUSFormat",
							validator.wrongUSFormatError));
                        return results;
                    }
                    i++;
                }
                
                // Make sure the remaining 4 characters are digits.
                for (; i < len; i++)
                {
                    if (DECIMAL_DIGITS.indexOf(zip.charAt(i)) == -1)
                    {
						results.push(new ValidationResult(
							true, baseField, "wrongUSFormat",
							validator.wrongUSFormatError));
                        return results;
                    }
                }
            }
        }

        else if (domainType == DOMAIN_US_OR_CANADA &&
				 containsLetters &&
				 (len == 6 || len == 7)) // Canadian zip code
        {
            i = 0;

            // Make sure the zip is in the form 'ldlfdld'
			// where l is a letter, d is a digit,
			// and f is an allowed formatting character.
            if (ROMAN_LETTERS.indexOf(zip.charAt(i++)) == -1 ||
                DECIMAL_DIGITS.indexOf(zip.charAt(i++)) == -1 ||
                ROMAN_LETTERS.indexOf(zip.charAt(i++)) == -1)
            {
				results.push(new ValidationResult(
					true, baseField, "wrongCAFormat",
					validator.wrongCAFormatError));
                return results;
            }
            
            if (len == 7 &&
				allowedFormatChars.indexOf(zip.charAt(i++)) == -1)
            {
				results.push(new ValidationResult(
					true, baseField, "wrongCAFormat",
					validator.wrongCAFormatError));
                return results;
            }
            
            if (DECIMAL_DIGITS.indexOf(zip.charAt(i++)) == -1 ||
                ROMAN_LETTERS.indexOf(zip.charAt(i++)) == -1 ||
                DECIMAL_DIGITS.indexOf(zip.charAt(i++)) == -1)
            {
				results.push(new ValidationResult(
					true, baseField, "wrongCAFormat",
					validator.wrongCAFormatError));
                return results;
            }
        }
        
		else
        {
			results.push(new ValidationResult(
				true, baseField, "wrongLength",
				validator.wrongLengthError));
            return results;
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
	public function ZipCodeValidator()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  allowedFormatChars
	//----------------------------------

	/**
	 *  @private
	 */
	private var _allowedFormatChars:String;

    /**
	 *  @private
	 */
	private var allowedFormatCharsOverride:String;
	
	[Inspectable(category="General", defaultValue="null")]

	/** 
     *  The set of formatting characters allowed in the ZIP code.
	 *  This can not have digits or alphabets [a-z A-Z].
	 *
	 *  @default " -". 
	 */
	public function get allowedFormatChars():String
	{
		return _allowedFormatChars;
	}

    /**
	 *  @private
	 */
	public function set allowedFormatChars(value:String):void
	{
		if (value != null)
		{
			for (var i:int = 0; i < value.length; i++)
			{
				var c:String = value.charAt(i);
				if (DECIMAL_DIGITS.indexOf(c) != -1 ||
					ROMAN_LETTERS.indexOf(c) != -1)
				{
					var message:String = resourceManager.getString(
						"validators", "invalidFormatCharsZCV");
					throw new Error(message);
				}
			}
		}

		allowedFormatCharsOverride = value;

		_allowedFormatChars = value != null ?
							  value :
							  resourceManager.getString(
								  "validators",
								  "zipCodeValidatorAllowedFormatChars");
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

    [Inspectable(category="General", defaultValue="null")]

    /** 
     *  Type of ZIP code to check.
     *  In MXML, valid values are <code>"US or Canada"</code> 
     *  and <code>"US Only"</code>.
	 *
	 *  <p>In ActionScript, you can use the following constants to set this property: 
	 *  <code>ZipCodeValidatorDomainType.US_ONLY</code> and 
	 *  <code>ZipCodeValidatorDomainType.US_OR_CANADA</code>.</p>
	 *
	 *  @default ZipCodeValidatorDomainType.US_ONLY
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
				      "validators", "zipCodeValidatorDomain");
	}

	//--------------------------------------------------------------------------
	//
	//  Properties: Errors
	//
	//--------------------------------------------------------------------------

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
     *  Error message when the ZIP code contains invalid characters.
	 *
	 *  @default "The ZIP code contains invalid characters."
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
								"validators", "invalidCharErrorZCV");
	}

	//----------------------------------
	//  invalidDomainError
	//----------------------------------
    
    /**
	 *  @private
	 *  Storage for the invalidDomainError property.
	 */
	private var _invalidDomainError:String;
	
    /**
	 *  @private
	 */
	private var invalidDomainErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /** 
     *  Error message when the <code>domain</code> property contains an invalid value.
	 *
	 *  @default "The domain parameter is invalid. It must be either 'US Only' or 'US or Canada'."
     */
	public function get invalidDomainError():String
	{
		return _invalidDomainError;
	}

	/**
	 *  @private
	 */
	public function set invalidDomainError(value:String):void
	{
		invalidDomainErrorOverride = value;

		_invalidDomainError = value != null ?
							  value :
							  resourceManager.getString(
							      "validators", "invalidDomainErrorZCV");
	}
    
	//----------------------------------
	//  wrongCAFormatError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the wrongCAFormatError property.
	 */
	private var _wrongCAFormatError:String;
	
    /**
	 *  @private
	 */
	private var wrongCAFormatErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /** 
     *  Error message for an invalid Canadian postal code.
	 *
	 *  @default "The Canadian postal code must be formatted 'A1B 2C3'."
     */
	public function get wrongCAFormatError():String
	{
		return _wrongCAFormatError;
	}

	/**
	 *  @private
	 */
	public function set wrongCAFormatError(value:String):void
	{
		wrongCAFormatErrorOverride = value;

		_wrongCAFormatError = value != null ?
							  value :
							  resourceManager.getString(
							      "validators", "wrongCAFormatError");
	}
	
	//----------------------------------
	//  wrongLengthError
	//----------------------------------
    
    /**
	 *  @private
	 *  Storage for the wrongLengthError property.
	 */
	private var _wrongLengthError:String;
	
    /**
	 *  @private
	 */
	private var wrongLengthErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

    /** 
     *  Error message for an invalid US ZIP code.
	 *
	 *  @default "The ZIP code must be 5 digits or 5+4 digits."
     */
	public function get wrongLengthError():String
	{
		return _wrongLengthError;
	}

	/**
	 *  @private
	 */
	public function set wrongLengthError(value:String):void
	{
		wrongLengthErrorOverride = value;

		_wrongLengthError = value != null ?
							value :
							resourceManager.getString(
								"validators", "wrongLengthErrorZCV");
	}
	
 	//----------------------------------
	//  wrongUSFormatError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the wrongUSFormatError property.
	 */
	private var _wrongUSFormatError:String;
	
    /**
	 *  @private
	 */
	private var wrongUSFormatErrorOverride:String;

    [Inspectable(category="Errors", defaultValue="null")]

	/** 
     *  Error message for an incorrectly formatted ZIP code.
	 *
	 *  @default "The ZIP+4 code must be formatted '12345-6789'."
     */
	public function get wrongUSFormatError():String
	{
		return _wrongUSFormatError;
	}

	/**
	 *  @private
	 */
	public function set wrongUSFormatError(value:String):void
	{
		wrongUSFormatErrorOverride = value;

		_wrongUSFormatError = value != null ?
							  value :
							  resourceManager.getString(
							    "validators", "wrongUSFormatError");
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

		allowedFormatChars = allowedFormatCharsOverride;
		domain = domainOverride;

		invalidDomainError = invalidDomainErrorOverride;
		invalidCharError = invalidCharErrorOverride;
		wrongCAFormatError = wrongCAFormatErrorOverride;
		wrongLengthError = wrongLengthErrorOverride;
		wrongUSFormatError = wrongUSFormatErrorOverride;	
	}

    /**
     *  Override of the base class <code>doValidation()</code> method
     *  to validate a ZIP code.
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
		    return ZipCodeValidator.validateZipCode(this, value, null);
    }
}

}
