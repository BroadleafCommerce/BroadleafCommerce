////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.validators
{

/**
 *  The ValidationResult class contains the results of a validation. 
 *
 *  <p>The ValidationResultEvent class defines the event object
 *  that is passed to event listeners for the <code>valid</code>
 *  and <code>invalid</code> validator events. 
 *  The class also defines the <code>results</code> property,
 *  which contains an Array of ValidationResult objects,
 *  one for each field examined by the validator.
 *  This lets you access the ValidationResult objects
 *  from within an event listener.</p>
 *
 *  @see mx.events.ValidationResultEvent
 */
public class ValidationResult
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor
	 *
     *  @param isError Pass <code>true</code> if there was a validation error.
     *
     *  @param subField Name of the subfield of the validated Object.
     *
     *  @param errorCode  Validation error code.
     *
     *  @param errorMessage Validation error message.
	 */
	public function ValidationResult(isError:Boolean, subField:String = "",
									 errorCode:String = "",
									 errorMessage:String = "")
	{
		super();

		this.isError = isError;
		this.subField = subField;
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  errorCode
	//----------------------------------

	/**
	 *  The validation error code
	 *  if the value of the <code>isError</code> property is <code>true</code>.
	 */
	public var errorCode:String;

	//----------------------------------
	//  errorMessage
	//----------------------------------

	/**
	 *  The validation error message
	 *  if the value of the <code>isError</code> property is <code>true</code>.
	 */
	public var errorMessage:String;
	
	//----------------------------------
	//  isError
	//----------------------------------

	/**
	 *  Contains <code>true</code> if the field generated a validation failure.
	 */
	public var isError:Boolean;
	
	//----------------------------------
	//  subField
	//----------------------------------

	/**
	 *  The name of the subfield that the result is associated with.
	 *  Some validators, such as CreditCardValidator and DateValidator,
	 *  validate multiple subfields at the same time.
	 */
	public var subField:String;
}

}
