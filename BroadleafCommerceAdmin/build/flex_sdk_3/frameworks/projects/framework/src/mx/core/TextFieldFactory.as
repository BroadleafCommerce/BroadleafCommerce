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

import flash.text.TextField;
import flash.utils.Dictionary;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  Singleton to create TextFields in the context of various ModuleFactories. 
 *  One module factory will have at most one TextField created for it.
 *  The text fields are only used for measurement;
 *  they are not on the display list.
 */
public class TextFieldFactory implements ITextFieldFactory
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------
    
	/**
	 *  @private
	 * 
	 *  This classes singleton.
	 */
	private static var instance:ITextFieldFactory;

	/**
	 *  @private
	 * 
	 *  Cache of textFields. Limit of one per module factory.
	 */
	private var textFields:Dictionary = new Dictionary(true);
			
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public static function getInstance():ITextFieldFactory
	{
		if (!instance)
			instance = new TextFieldFactory();

		return instance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  Creates a TextField in the context of the specified IFlexModuleFactory.
	 *
	 *  @param moduleFactory The moduleFactory requesting the TextField.
	 *
	 *	@return A text field for a given moduleFactory.
	 */
	public function createTextField(moduleFactory:IFlexModuleFactory):TextField
	{
		// Check to see if we already have a text field for this module factory.
		var textField:TextField = null;
		var textFieldDictionary:Dictionary = textFields[moduleFactory];

		if (textFieldDictionary)
		{
			for (var iter:Object in textFieldDictionary)
			{
				textField = TextField(iter);
				break;
			}
		}
		if (!textField)
		{
			if (moduleFactory)
				textField = TextField(moduleFactory.create("flash.text.TextField"));			
			else 
				textField = new TextField();	
			
			// The dictionary could be empty, but not null because entries in the dictionary
			// could be garbage collected.
			if (!textFieldDictionary)
				textFieldDictionary = new Dictionary(true);
			textFieldDictionary[textField] = 1;
			textFields[moduleFactory] = textFieldDictionary;
		}

		return textField;
	}
}

}
