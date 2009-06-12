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

package mx.utils
{

import flash.utils.getQualifiedClassName;
import mx.resources.ResourceBundle;
import mx.utils.StringUtil;

[ExcludeClass]

/**
 *  @private
 */
public class Translator
{
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static const TRANSLATORS:Object = {};
 
    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    [ResourceBundle("messaging")]
    
	/**
	 *  @private
	 */
	private static var messagingBundle:ResourceBundle;    

    [ResourceBundle("rpc")]
    
	/**
	 *  @private
	 */
	private static var rpcBundle:ResourceBundle;    

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Assumes the bundle name is the name of the second package
	 *  (e.g foo in mx.foo).
     */
    public static function getDefaultInstanceFor(source:Class):Translator
    {
        var qualifiedName:String = getQualifiedClassName(source);
        var firstSeparator:int = qualifiedName.indexOf(".");
        var startIndex:int = firstSeparator + 1;
        var secondSeparator:int = qualifiedName.indexOf(".", startIndex);
        if (secondSeparator < 0)
            secondSeparator = qualifiedName.indexOf(":", startIndex);
        var bundleName:String =
            qualifiedName.slice(startIndex, secondSeparator);
        return getInstanceFor(bundleName);
    }

 	/**
	 *  @private
	 */
   public static function getInstanceFor(bundleName:String):Translator
    {
        var result:Translator = TRANSLATORS[bundleName];
        if (!result)
        {
            result = new Translator(bundleName);
            TRANSLATORS[bundleName] = result;
        }
        return result;
    }

	/**
	 *  @private
	 */
    public static function getMessagingInstance():Translator
    {
        return getInstanceFor("messaging");
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

	/**
	 *  Constructor
	 */
    public function Translator(bundleName:String)
    {
        super();

        this.bundleName = bundleName;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    private var bundleName:String;
    
	/**
	 *  @private
	 */
	private var bundle:ResourceBundle = null;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    public function textOf(key:String, ... rest):String
    {
        if (!bundle)
        {
            if (bundleName == "messaging")
                bundle = messagingBundle;
            else if (bundleName == "rpc")
                bundle = rpcBundle;
        }

		// Note: Writing bundle["getString"](key) instead of
		// bundle.getString(key) avoids a deprecation warning.
        return bundle ?
			   StringUtil.substitute(bundle["getString"](key), rest) :
			   "Key " + key + " was not found in resource bundle " + bundleName;
    }
}

}
