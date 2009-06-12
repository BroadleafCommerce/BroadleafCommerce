////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.validators
{

/**
 *  The ZipCodeValidatorDomainType class defines the values 
 *  for the <code>domain</code> property of the ZipCodeValidator class,
 *  which you use to specify the type of ZIP code to validate.
 *
 *  @see mx.validators.ZipCodeValidator
 */
public final class ZipCodeValidatorDomainType
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Specifies to validate a United States or Canadian ZIP code.
	 */
	public static const US_OR_CANADA:String = "US or Canada";
	
	/**
	 *  Specifies to validate a United States ZIP code.
	 */
	public static const US_ONLY:String = "US Only";
}

}