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
 *  The CurrencyValidatorAlignSymbol class defines value constants
 *  for specifying currency symbol alignment.
 *  These values are used in the <code>CurrencyValidator.alignSymbol</code>
 *  property.
 *
 *  @see mx.validators.CurrencyValidator
 */
public final class CurrencyValidatorAlignSymbol
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Specifies <code>"any"</code> as the alignment of the currency symbol
	 *  for the CurrencyValidator class.
	 */
	public static const ANY:String = "any";

	/**
	 *  Specifies <code>"left"</code> as the alignment of the currency symbol
	 *  for the CurrencyValidator class.
	 */
	public static const LEFT:String = "left";

	/**
	 *  Specifies <code>"right"</code> as the alignment of the currency symbol
	 *  for the CurrencyValidator class.
	 */
	public static const RIGHT:String = "right";
}

}
