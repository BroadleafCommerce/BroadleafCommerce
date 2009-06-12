////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.resources
{

import mx.managers.ISystemManager;

/**
 *  The Locale class can be used to parse a locale String such as <code>"en_US_MAC"</code>
 *  into its three parts: a language code, a country code, and a variant.
 *
 *  <p>The localization APIs in the IResourceManager and IResourceBundle
 *  interfaces use locale Strings rather than Locale instances,
 *  so this class is seldom used in an application.</p>
 *
 *  @see mx.resources.IResourceBundle
 *  @see mx.resources.IResourceManager
 */
public class Locale
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var currentLocale:Locale;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    [Deprecated(replacement="ResourceManager.localeChain", since="3.0")]
    
    /**
     *  Returns a Locale object, if you compiled your application 
     *  for a single locale. Otherwise, it returns <code>null</code>.
     *  
     *  <p>This method has been deprecated because the Flex framework
     *  now supports having resource bundles for multiple locales
     *  in the same application.
     *  You can use the <code>getLocale()</code> method of IResourceManager
     *  to find out which locales the ResourceManager has resource bundles for.
     *  You can use the <code>localeChain</code> property of IResourceManager
     *  to determine which locales the ResourceManager searches for
     *  resources.</p>
     * 
     *  @param sm The current SystemManager.
     *
     *  @return Returns a Locale object.
     */
    public static function getCurrent(sm:ISystemManager):Locale
    {
        if (!currentLocale)
        {
            var compiledLocales:Array = sm.info()["compiledLocales"];
            if (compiledLocales != null && compiledLocales.length == 1)
                currentLocale = new Locale(compiledLocales[0]);
        }

        return currentLocale;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param localeString A 1-, 2-, or 3-part locale String,
     *  such as <code>"en"</code>, <code>"en_US"</code>, or <code>"en_US_MAC"</code>.
     *  The parts are separated by underscore characters.
     *  The first part is a two-letter lowercase language code
     *  as defined by ISO-639, such as <code>"en"</code> for English.
     *  The second part is a two-letter uppercase country code
     *  as defined by ISO-3166, such as <code>"US"</code> for the United States.
     *  The third part is a variant String, which can be used 
     *  to optionally distinguish multiple locales for the same language and country.
     *  It is sometimes used to indicate the operating system
     *  that the locale should be used with, such as <code>"MAC"</code>, <code>"WIN"</code>, or <code>"UNIX"</code>.
     */
    public function Locale(localeString:String)
    {
        super();

        this.localeString = localeString;
        
        var parts:Array = localeString.split("_");
        
        if (parts.length > 0)
            _language = parts[0];
        
        if (parts.length > 1)
            _country = parts[1];
        
        if (parts.length > 2)
            _variant = parts.slice(2).join("_");
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var localeString:String;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  language
    //----------------------------------
            
    /**
     *  @private
     *  Storage for the language property.
     */
    private var _language:String;

    [Inspectable(category="General", defaultValue="null")]
    
    /**
     *  The language code of this Locale instance. [Read-Only]
     *
     *  <pre>
     *  var locale:Locale = new Locale("en_US_MAC");
     *  trace(locale.language); // outputs "en"
     *  </pre>
     */      
    public function get language():String
    {
        return _language;
    }
       
    //----------------------------------
    //  country
    //----------------------------------
            
    /**
     *  @private
     *  Storage for the country property.
     */
    private var _country:String;

    [Inspectable(category="General", defaultValue="null")]

    /**
     *  The country code of this Locale instance. [Read-Only]
     *
     *  <pre>
     *  var locale:Locale = new Locale("en_US_MAC");
     *  trace(locale.country); // outputs "US"
     *  </pre>
     */      
    public function get country():String
    {
        return _country
    }

    //----------------------------------
    //  variant
    //----------------------------------
                
    /**
     *  @private
     *  Storage for the variant property.
     */
    private var _variant:String;

    [Inspectable(category="General", defaultValue="null")]
    
    /**
     *  The variant part of this Locale instance. [Read-Only]
     *
     *  <pre>
     *  var locale:Locale = new Locale("en_US_MAC");
     *  trace(locale.variant); // outputs "MAC"
     *  </pre>
     */      
    public function get variant():String
    {
        return _variant;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the locale String that was used to construct
     *  this Locale instance. For example:
     *
     *  <pre>
     *  var locale:Locale = new Locale("en_US_MAC");
     *  trace(locale.toString()); // outputs "en_US_MAC"
     *  </pre>
     *
     *  @return Returns the locale String that was used to
     *  construct this Locale instance.
     */   
    public function toString():String
    {
        return localeString;
    }
}

}
