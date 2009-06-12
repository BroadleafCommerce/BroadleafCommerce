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
 *  The PhoneNumberValidator class validates that a string
 *  is a valid phone number.
 *  A valid phone number contains at least 10 digits,
 *  plus additional formatting characters.
 *  The validator does not check if the phone number
 *  is an actual active phone number.
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:PhoneNumberValidator&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:PhoneNumberValidator 
 *    allowedFormatChars="()- .+" 
 *    invalidCharError="Your telephone number contains invalid characters."
 *    wrongLengthError="Your telephone number must contain at least 10 digits."
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/PhoneNumberValidatorExample.mxml
 */
public class PhoneNumberValidator extends Validator
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Convenience method for calling a validator
	 *  from within a custom validation function.
	 *  Each of the standard Flex validators has a similar convenience method.
	 *
	 *  @param validator The PhoneNumberValidator instance.
	 *
	 *  @param value A field to validate.
	 *
	 *  @param baseField Text representation of the subfield
	 *  specified in the <code>value</code> parameter.
	 *  For example, if the <code>value</code> parameter specifies value.phone,
	 *  the <code>baseField</code> value is "phone".
	 *
	 *  @return An Array of ValidationResult objects, with one ValidationResult 
	 *  object for each field examined by the validator. 
	 *
	 *  @see mx.validators.ValidationResult
	 */
	public static function validatePhoneNumber(validator:PhoneNumberValidator,
											   value:Object,
											   baseField:String):Array
	{
		var results:Array = [];
		
		// Resource-backed properties of the validator.
		var allowedFormatChars:String = validator.allowedFormatChars;

		var resourceManager:IResourceManager = ResourceManager.getInstance();

		var valid:String =  DECIMAL_DIGITS + allowedFormatChars;
		var len:int = value.toString().length;
		var digitLen:int = 0;
		var n:int;
		var i:int;
		
		n = allowedFormatChars.length;
		for (i = 0; i < n; i++)
		{
			if (DECIMAL_DIGITS.indexOf(allowedFormatChars.charAt(i)) != -1)
			{
				var message:String = resourceManager.getString(
					"validators", "invalidFormatChars");
				throw new Error(message);
			}
		}

		for (i = 0; i < len; i++)
		{
			var temp:String = "" + value.toString().substring(i, i + 1);
			if (valid.indexOf(temp) == -1)
			{
				results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
			}
			if (valid.indexOf(temp) <= 9)
				digitLen++;
		}

		if (digitLen < 10)
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
	public function PhoneNumberValidator()
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
	 *  Storage for the allowedFormatChars property.
	 */
	private var _allowedFormatChars:String;

    /**
	 *  @private
	 */
	private var allowedFormatCharsOverride:String;
	
	[Inspectable(category="General", defaultValue="null")]

	/** 
	 *  The set of allowable formatting characters.
	 *
	 *  @default "()- .+"
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
			var n:int = value.length;
			for (var i:int = 0; i < n; i++)
			{
				if (DECIMAL_DIGITS.indexOf(value.charAt(i)) != -1)
				{
					var message:String = resourceManager.getString(
						"validators", "invalidFormatChars");
					throw new Error(message);
				}
			}
		}

		allowedFormatCharsOverride = value;

		_allowedFormatChars = value != null ?
							  value :
							  resourceManager.getString(
								  "validators",
								  "phoneNumberValidatorAllowedFormatChars");
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
	 *  Error message when the value contains invalid characters.
	 *
	 *  @default "Your telephone number contains invalid characters."
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
								"validators", "invalidCharErrorPNV");
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
	 *  Error message when the value has fewer than 10 digits.
	 *
	 *  @default "Your telephone number must contain at least 10 digits."
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
								"validators", "wrongLengthErrorPNV");
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
		
		invalidCharError = invalidCharErrorOverride;
		wrongLengthError = wrongLengthErrorOverride;
	}

	/**
     *  Override of the base class <code>doValidation()</code> method
     *  to validate a phone number.
     *
	 *  <p>You do not typically call this method directly;
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
		    return PhoneNumberValidator.validatePhoneNumber(this, value, null);
    }
}

}
