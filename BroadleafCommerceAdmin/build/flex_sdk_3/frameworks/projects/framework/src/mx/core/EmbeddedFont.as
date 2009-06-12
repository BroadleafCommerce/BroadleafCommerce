////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

import flash.text.FontStyle;

[ExcludeClass]

/**
 *  @private
 *  Describes the properties that make an embedded font unique.
 */
public class EmbeddedFont
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Create a new EmbeddedFont object.
	 * 
	 *  @param fontName The name of the font.
	 *
	 *  @param bold true if the font is bold, false otherwise.
	 *
	 *  @param italic true if the fotn is italic, false otherwise,
	 */ 
	public function EmbeddedFont(fontName:String, bold:Boolean, italic:Boolean)
	{
		super();

		_fontName = fontName;
		_fontStyle = EmbeddedFontRegistry.getFontStyle(bold, italic);
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

    //----------------------------------
    //  fontName
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the fontName property.
	 */
	private var _fontName:String;
	
	/**
	 *  The name of the font.
	 */
	public function get fontName():String
	{
		return _fontName;	
	}
	
    //----------------------------------
    //  fontStyle
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the fontStyle property.
	 */
	private var _fontStyle:String;
	
	/**
	 *  The style of the font.
	 *  The value is one of the values in flash.text.FontStyle.
	 */
	public function get fontStyle():String
	{
		return _fontStyle;	
	}
}

}
