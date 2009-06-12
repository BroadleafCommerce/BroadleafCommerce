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

[ResourceBundle("validators")]
    
/**
 *  The EmailValidator class validates that a String has a single &#64; sign,
 *  a period in the domain name and that the top-level domain suffix has
 *  two, three, four, or six characters.
 *  IP domain names are valid if they are enclosed in square brackets. 
 *  The validator does not check whether the domain and user name
 *  actually exist.
 *
 *  <p>You can use IP domain names if they are enclosed in square brackets; 
 *  for example, myname&#64;[206.132.22.1].
 *  You can use individual IP numbers from 0 to 255.</p>
 *  
 *  @mxml
 *
 *  <p>The <code>&lt;mx:EmailValidator&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:EmailValidator 
 *    invalidCharError="Your e-mail address contains invalid characters."
 *    invalidDomainError= "The domain in your e-mail address is incorrectly formatted." 
 *    invalidIPDomainError="The IP domain in your e-mail address is incorrectly formatted." 
 *    invalidPeriodsInDomainError="The domain in your e-mail address has consecutive periods." 
 *    missingAtSignError="An at sign (&64;) is missing in your e-mail address."
 *    missingPeriodInDomainError="The domain in your e-mail address is missing a period." 
 *    missingUsernameError="The username in your e-mail address is missing." 
 *    tooManyAtSignsError="Your e-mail address contains too many &64; characters."
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/EmailValidatorExample.mxml
 */
public class EmailValidator extends Validator
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
	private static const DISALLOWED_LOCALNAME_CHARS:String =
								"()<>,;:\\\"[] `~!#$%^&*={}|/?'";
	/**
	 *  @private
	 */							
	private static const DISALLOWED_DOMAIN_CHARS:String =
								"()<>,;:\\\"[] `~!#$%^&*+={}|/?'";
	
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
	 *  @param validator The EmailValidator instance.
	 *
	 *  @param value A field to validate.
	 *
	 *  @param baseField Text representation of the subfield
	 *  specified in the value parameter.
	 *  For example, if the <code>value</code> parameter specifies value.email,
	 *  the <code>baseField</code> value is "email".
	 *
	 *  @return An Array of ValidationResult objects, with one
	 *  ValidationResult object for each field examined by the validator. 
	 *
	 *  @see mx.validators.ValidationResult	 
	 */
	public static function validateEmail(validator:EmailValidator,
										 value:Object,
										 baseField:String):Array
	{
		var results:Array = [];
	
		// Validate the domain name
		// If IP domain, then must follow [x.x.x.x] format
		// Can not have continous periods.
		// Must have at least one period.
		// Must end in a top level domain name that has 2, 3, 4, or 6 characters.

		var emailStr:String = String(value);
		var username:String = "";
		var domain:String = "";
		var n:int;
		var i:int;

		// Find the @
		var ampPos:int = emailStr.indexOf("@");
		if (ampPos == -1)
		{
			results.push(new ValidationResult(
				true, baseField, "missingAtSign",
				validator.missingAtSignError));
			return results;
		}
		// Make sure there are no extra @s.
		else if (emailStr.indexOf("@", ampPos + 1) != -1) 
		{ 
			results.push(new ValidationResult(
				true, baseField, "tooManyAtSigns",
				validator.tooManyAtSignsError));
			return results;
		}

		// Separate the address into username and domain.
		username = emailStr.substring(0, ampPos);
		domain = emailStr.substring(ampPos + 1);

		// Validate username has no illegal characters
		// and has at least one character.
		var usernameLen:int = username.length;
		if (usernameLen == 0)
		{
			results.push(new ValidationResult(
				true, baseField, "missingUsername",
				validator.missingUsernameError));
			return results;
		}

		for (i = 0; i < usernameLen; i++)
		{
			if (DISALLOWED_LOCALNAME_CHARS.indexOf(username.charAt(i)) != -1)
			{
				results.push(new ValidationResult(
					true, baseField, "invalidChar",
					validator.invalidCharError));
				return results;
			}
		}
		
		var domainLen:int = domain.length;
		
		// check for IP address
		if ((domain.charAt(0) == "[") && (domain.charAt(domainLen - 1) == "]"))
		{
			// Validate IP address
			if (!isValidIPAddress(domain.substring(1, domainLen - 1)))
			{
				results.push(new ValidationResult(
						true, baseField, "invalidIPDomain",
						validator.invalidIPDomainError));
				return results;
			}
		}
		else
		{
			// Must have at least one period
			var periodPos:int = domain.indexOf(".");
			var nextPeriodPos:int = 0;
			var lastDomain:String = "";
			
			if (periodPos == -1)
			{
				results.push(new ValidationResult(
					true, baseField, "missingPeriodInDomain",
					validator.missingPeriodInDomainError));
				return results;
			}

			while (true)
			{
				nextPeriodPos = domain.indexOf(".", periodPos + 1);
				if (nextPeriodPos == -1)
				{
					lastDomain = domain.substring(periodPos + 1);
					if (lastDomain.length != 3 &&
						lastDomain.length != 2 &&
						lastDomain.length != 4 &&
						lastDomain.length != 6)
					{
						results.push(new ValidationResult(
							true, baseField, "invalidDomain",
							validator.invalidDomainError));
						return results;
					}
					break;
				}
				else if (nextPeriodPos == periodPos + 1)
				{
					results.push(new ValidationResult(
						true, baseField, "invalidPeriodsInDomain",
						validator.invalidPeriodsInDomainError));
					return results;
				}
				periodPos = nextPeriodPos;
			}

			// Check that there are no illegal characters in the domain.
			for (i = 0; i < domainLen; i++)
			{
				if (DISALLOWED_DOMAIN_CHARS.indexOf(domain.charAt(i)) != -1)
				{
					results.push(new ValidationResult(
						true, baseField, "invalidChar",
						validator.invalidCharError));
					return results;
				}
			}
			
			// Check that the character immediately after the @ is not a period.
			if (domain.charAt(0) == ".")
			{
				results.push(new ValidationResult(
					true, baseField, "invalidDomain",
					validator.invalidDomainError));
				return results;
			}
		}

		return results;
	}
	
	/**
	 * Validate a given IP address
	 * 
	 * If IP domain, then must follow [x.x.x.x] format
	 * or for IPv6, then follow [x:x:x:x:x:x:x:x] or [x::x:x:x] or some
	 * IPv4 hybrid, like [::x.x.x.x] or [0:00::192.168.0.1]
	 *
	 * @private
	 */ 
	private static function isValidIPAddress(ipAddr:String):Boolean
	{
		var ipArray:Array = [];
		var pos:int = 0;
		var newpos:int = 0;
		var item:Number;
		var n:int;
		var i:int;
		
		// if you have :, you're in IPv6 mode
		// if you have ., you're in IPv4 mode
		
		if (ipAddr.indexOf(":") != -1)
		{
			// IPv6
			
			// validate by splitting on the colons
			// to make it easier, since :: means zeros, 
			// lets rid ourselves of these wildcards in the beginning
			// and then validate normally
			
			// get rid of unlimited zeros notation so we can parse better
			var hasUnlimitedZeros:Boolean = ipAddr.indexOf("::") != -1;
			if (hasUnlimitedZeros)
			{
				ipAddr = ipAddr.replace(/^::/, "");
				ipAddr = ipAddr.replace(/::/g, ":");
			}
			
			while (true)
			{
				newpos = ipAddr.indexOf(":", pos);
				if (newpos != -1)
				{
					ipArray.push(ipAddr.substring(pos,newpos));
				}
				else
				{
					ipArray.push(ipAddr.substring(pos));
					break;
				}
				pos = newpos + 1;
			}
			
			n = ipArray.length;
			
			const lastIsV4:Boolean = ipArray[n-1].indexOf(".") != -1;
			
			if (lastIsV4)
			{
				// if no wildcards, length must be 7
				// always, never more than 7
				if ((ipArray.length != 7 && !hasUnlimitedZeros) || (ipArray.length > 7))
					return false;
	
				for (i = 0; i < n; i++)
				{
					if (i == n-1)
					{
						// IPv4 part...
						return isValidIPAddress(ipArray[i]);
					}
					
					item = parseInt(ipArray[i], 16);
					
					if (item != 0)
						return false;
				}
			}
			else
			{
			
				// if no wildcards, length must be 8
				// always, never more than 8
				if ((ipArray.length != 8 && !hasUnlimitedZeros) || (ipArray.length > 8))
					return false;
				
				for (i = 0; i < n; i++)
				{
					item = parseInt(ipArray[i], 16);
					
					if (isNaN(item) || item < 0 || item > 0xFFFF)
						return false;
				}
			}
			
			return true;
		}
			
		if (ipAddr.indexOf(".") != -1)
		{
			// IPv4
			
			// validate by splling on the periods
			while (true)
			{
				newpos = ipAddr.indexOf(".", pos);
				if (newpos != -1)
				{
					ipArray.push(ipAddr.substring(pos,newpos));
				}
				else
				{
					ipArray.push(ipAddr.substring(pos));
					break;
				}
				pos = newpos + 1;
			}
			
			if (ipArray.length != 4)
				return false;

			n = ipArray.length;
			for (i = 0; i < n; i++)
			{
				item = Number(ipArray[i]);
				if (isNaN(item) || item < 0 || item > 255)
					return false;
			}
			
			return true;
		}
		
		return false;
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function EmailValidator()
	{
		super();
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
	 *  Error message when there are invalid characters in the e-mail address.
	 *
	 *  @default "Your e-mail address contains invalid characters."
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
								"validators", "invalidCharErrorEV");
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
	 *  Error message when the suffix (the top level domain)
	 *  is not 2, 3, 4 or 6 characters long.
	 *
	 *  @default "The domain in your e-mail address is incorrectly formatted."
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
							      "validators", "invalidDomainErrorEV");
	}

	//----------------------------------
	//  invalidIPDomainError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the invalidIPDomainError property.
	 */
	private var _invalidIPDomainError:String;
	
    /**
	 *  @private
	 */
	private var invalidIPDomainErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when the IP domain is invalid. The IP domain must be enclosed by square brackets.
	 *
	 *  @default "The IP domain in your e-mail address is incorrectly formatted."
     */
	public function get invalidIPDomainError():String
	{
		return _invalidIPDomainError;
	}

	/**
	 *  @private
	 */
	public function set invalidIPDomainError(value:String):void
	{
		invalidIPDomainErrorOverride = value;

		_invalidIPDomainError = value != null ?
								value :
								resourceManager.getString(
									"validators", "invalidIPDomainError");
	}

	//----------------------------------
	//  invalidPeriodsInDomainError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the invalidPeriodsInDomainError property.
	 */
	private var _invalidPeriodsInDomainError:String;
	
    /**
	 *  @private
	 */
	private var invalidPeriodsInDomainErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when there are continuous periods in the domain.
	 *
	 *  @default "The domain in your e-mail address has continous periods."
     */
	public function get invalidPeriodsInDomainError():String
	{
		return _invalidPeriodsInDomainError;
	}

	/**
	 *  @private
	 */
	public function set invalidPeriodsInDomainError(value:String):void
	{
		invalidPeriodsInDomainErrorOverride = value;

		_invalidPeriodsInDomainError = value != null ?
									   value :
									   resourceManager.getString(
									       "validators", "invalidPeriodsInDomainError");
	}

	//----------------------------------
	//  missingAtSignError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the missingAtSignError property.
	 */
	private var _missingAtSignError:String;
	
    /**
	 *  @private
	 */
	private var missingAtSignErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when there is no at sign in the email address.
	 *
	 *  @default "An at sign (&64;) is missing in your e-mail address."
     */
	public function get missingAtSignError():String
	{
		return _missingAtSignError;
	}

	/**
	 *  @private
	 */
	public function set missingAtSignError(value:String):void
	{
		missingAtSignErrorOverride = value;

		_missingAtSignError = value != null ?
							  value :
							  resourceManager.getString(
							      "validators", "missingAtSignError");
	}

	//----------------------------------
	//  missingPeriodInDomainError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the missingPeriodInDomainError property.
	 */
	private var _missingPeriodInDomainError:String;
	
    /**
	 *  @private
	 */
	private var missingPeriodInDomainErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when there is no period in the domain.
	 *
	 *  @default "The domain in your e-mail address is missing a period."
     */
	public function get missingPeriodInDomainError():String
	{
		return _missingPeriodInDomainError;
	}

	/**
	 *  @private
	 */
	public function set missingPeriodInDomainError(value:String):void
	{
		missingPeriodInDomainErrorOverride = value;

		_missingPeriodInDomainError = value != null ?
									  value :
									  resourceManager.getString(
									      "validators", "missingPeriodInDomainError");
	}

	//----------------------------------
	//  missingUsernameError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the missingUsernameError property.
	 */
	private var _missingUsernameError:String;
	
    /**
	 *  @private
	 */
	private var missingUsernameErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when there is no username.
	 *
	 *  @default "The username in your e-mail address is missing."
     */
	public function get missingUsernameError():String
	{
		return _missingUsernameError;
	}

	/**
	 *  @private
	 */
	public function set missingUsernameError(value:String):void
	{
		missingUsernameErrorOverride = value;

		_missingUsernameError = value != null ?
								value :
								resourceManager.getString(
									"validators", "missingUsernameError");
	}

	//----------------------------------
	//  tooManyAtSignsError
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the tooManyAtSignsError property.
	 */
	private var _tooManyAtSignsError:String;
	
    /**
	 *  @private
	 */
	private var tooManyAtSignsErrorOverride:String;
	
	[Inspectable(category="Errors", defaultValue="null")]

    /**
	 *  Error message when there is more than one at sign in the e-mail address.
	 *  This property is optional. 
	 *
	 *  @default "Your e-mail address contains too many &64; characters."
     */
	public function get tooManyAtSignsError():String
	{
		return _tooManyAtSignsError;
	}

	/**
	 *  @private
	 */
	public function set tooManyAtSignsError(value:String):void
	{
		tooManyAtSignsErrorOverride = value;

		_tooManyAtSignsError = value != null ?
							   value :
							   resourceManager.getString(
							       "validators", "tooManyAtSignsError");
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

		invalidCharError = invalidCharErrorOverride;
		invalidDomainError = invalidDomainErrorOverride;
		invalidIPDomainError = invalidIPDomainErrorOverride;
		invalidPeriodsInDomainError = invalidPeriodsInDomainErrorOverride;
		missingAtSignError = missingAtSignErrorOverride;
		missingPeriodInDomainError = missingPeriodInDomainErrorOverride;
		missingUsernameError = missingUsernameErrorOverride;
		tooManyAtSignsError = tooManyAtSignsErrorOverride;
	}

	/**
     *  Override of the base class <code>doValidation()</code> method
     *  to validate an e-mail address.
	 *
	 *  <p>You do not call this method directly;
	 *  Flex calls it as part of performing a validation.
	 *  If you create a custom Validator class, you must implement this method. </p>
	 *
	 *  @param value Either a String or an Object to validate.
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
		    return EmailValidator.validateEmail(this, value, null);
	}
}

}
