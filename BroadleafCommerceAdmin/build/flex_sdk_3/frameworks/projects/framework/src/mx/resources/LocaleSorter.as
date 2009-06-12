////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.resources
{

[ExcludeClass]

/**
 *  @private
 *  The APIs of the LocaleSorter class provides the sorting functionality 
 *  of application locales against system preferences.
 */
public class LocaleSorter
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
     *	Sorts a list of locales using the order specified
     *  by the user preferences.
	 * 
	 * 	@param appLocales An Array of locales supported by the application.
     *
	 * 	@param systemPreferences The locale chain of user-preferred locales.
     *
	 * 	@param ultimateFallbackLocale The ultimate fallback locale
     *  that will be used when no locale from systemPreference matches 
	 * 	a locale from application supported locale list.
     *
	 * 	@param addAll When true, adds all the non-matching locales
     *  at the end of the result list preserving the given order.
	 *
	 *	@return A locale chain that matches user preferences order. 
	 */ 
	public static function sortLocalesByPreference(
                                appLocales:Array, systemPreferences:Array,
                                ultimateFallbackLocale:String = null,
                                addAll:Boolean = false):Array
    {
		var result:Array = [];
		
        var hasLocale:Object = {};
		
		var i:int;
        var j:int;
        var k:int;
        var l:int;
		var locale:String;
		
		var locales:Array = trimAndNormalize(appLocales);
		var preferenceLocales:Array	= trimAndNormalize(systemPreferences);
		
		addUltimateFallbackLocale(preferenceLocales, ultimateFallbackLocale);
		
		// For better performance, save the locales in a lookup table.
		for (j = 0; j < locales.length; j++)
        {
			hasLocale[locales[j]] = j;
		}
		
		function promote(locale:String):void
        {
			if (typeof hasLocale[locale] != "undefined")
            {
				result.push(appLocales[hasLocale[locale]]);
				delete hasLocale[locale];
			}
		}
		
		for (i = 0, l = preferenceLocales.length; i < l; i++)
        {
			var plocale:LocaleID = LocaleID.fromString(preferenceLocales[i]);
			
			// Step 1: Promote the perfect match.
			promote(preferenceLocales[i]);
	
			promote(plocale.toString());
		   
			// Step 2: Promote the parent chain.
			while (plocale.transformToParent())
            {
				promote(plocale.toString());
			}
			
			// Parse it again.
			plocale = LocaleID.fromString(preferenceLocales[i]);
			
            // Step 3: Promote the order of siblings from preferenceLocales.
			for (j = 0; j < l; j++)
            {
				locale = preferenceLocales[j];
				if (plocale.isSiblingOf(LocaleID.fromString(locale)))
					promote(locale);
			}
            				
			// Step 4: Promote all remaining siblings
            // (aka not in preferenceLocales)
			// eg. push en_UK after en_US and en if the user
            // doesn't have en_UK in the preference list
			for (j = 0, k = locales.length; j < k; j++)
            {
				locale = locales[j];
				if (plocale.isSiblingOf(LocaleID.fromString(locale)))
					promote(locale);
			}
		}
		
		if (addAll)
        {
			// Check what locales are not already loaded
            // and push them preserving the order.
			for (j = 0, k = locales.length; j < k; j++)
            {
				promote(locales[j]);
			}
		}

		return result;
	} 

	/**
     *  @private
     */
	private static function trimAndNormalize(list:Array):Array
    {
		var resultList:Array = []; 
		
		for (var i:int = 0; i < list.length; i++)
        {
			resultList.push(normalizeLocale(list[i]));
		}
		
        return resultList;
	}
		
	/**
     *  @private
     */
    private static function normalizeLocale(locale:String):String
    {
		return locale.toLowerCase().replace(/-/g, "_");
	}

	/**
     *  @private
     */
	private static function addUltimateFallbackLocale(
                                preferenceLocales:Array,
                                ultimateFallbackLocale:String):void
    {
		if (ultimateFallbackLocale != null && ultimateFallbackLocale != "")
        {
			var locale:String = normalizeLocale(ultimateFallbackLocale);
			
			if (preferenceLocales.indexOf(locale) == -1)
            {
				// Add the locale to the end of the chain.
				preferenceLocales.push(locale);
			}
		}
	}
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: LocaleID
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 *  The APIs of the internal LocaleID class parse a locale string
 *  according to:
 *  RFC 4646: http://www.ietf.org/rfc/rfc4646.txt
 *  RFC 4647: http://www.ietf.org/rfc/rfc4647.txt
 *  IANA Language Subtag Registry:
 *  http://www.iana.org/assignments/language-subtag-registry
 */
class LocaleID
{
	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
	public static const STATE_PRIMARY_LANGUAGE:int = 0;
	
    /**
     *  @private
     */
    public static const STATE_EXTENDED_LANGUAGES:int = 1;
	
    /**
     *  @private
     */
    public static const STATE_SCRIPT:int = 2;
	
    /**
     *  @private
     */
    public static const STATE_REGION:int = 3;
	
    /**
     *  @private
     */
    public static const STATE_VARIANTS:int = 4;  
	
    /**
     *  @private
     */
    public static const STATE_EXTENSIONS:int = 5;
	
    /**
     *  @private
     */
    public static const STATE_PRIVATES:int = 6;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     */
    public static function fromString(str:String):LocaleID
    {
		var localeID:LocaleID = new LocaleID();
	
		var state:int = STATE_PRIMARY_LANGUAGE;
		var subtags:Array = str.replace(/-/g, '_').split('_');
		
		var last_extension:Array;
	
		for (var i:int = 0, l:int = subtags.length; i < l; i++)
        {
			var subtag:String = subtags[i].toLowerCase();
			
			if (state == STATE_PRIMARY_LANGUAGE)
            {
				if (subtag == "x")
                {
					localeID.privateLangs = true; 
                        // not used in our implementation,
                        // but makes the tag private
				}
                else if (subtag == "i")
                {
					localeID.lang += "i-";
                        // and wait the next subtag
                        // to complete the language name
				}
                else
                {
					localeID.lang += subtag;
					state = STATE_EXTENDED_LANGUAGES;
				}
			}
            else
            {
				// looking for:
                // an extended language - 3 chars
				// a script - 4 chars
				// a region - 2-3 chars
				// a variant - alpha with at least 5 chars
                //             or numeric with at least 4 chars
				// an extension/private singleton - 1 char
				
				var subtag_length:int = subtag.length;
				if (subtag_length == 0)
                    continue; // skip zero-lengthed subtags

				var firstChar:String = subtag.charAt(0).toLowerCase();
				
				if (state <= STATE_EXTENDED_LANGUAGES &&
                    subtag_length == 3)
                {
					localeID.extended_langs.push(subtag);
					if (localeID.extended_langs.length == 3)
                    {
                        // Allow a maximum of 3 extended langs.
						state = STATE_SCRIPT;
					}
				}
                else if (state <= STATE_SCRIPT &&
                         subtag_length == 4)
                {
					localeID.script = subtag;
					state = STATE_REGION;
				}
                else if (state <= STATE_REGION &&
                         (subtag_length == 2 || subtag_length == 3))
                {
					localeID.region = subtag;
					state = STATE_VARIANTS;
				}
                else if (state <= STATE_VARIANTS && 
						 ((firstChar >= "a" && firstChar <= "z" &&
                           subtag_length >= 5) || 
						  (firstChar >= "0" && firstChar <= "9" &&
                           subtag_length >= 4)))
                {
					// variant
					localeID.variants.push(subtag);
					state = STATE_VARIANTS;
				}
                else if (state < STATE_PRIVATES &&
                         subtag_length == 1)
                {
                    // singleton
					if (subtag == "x")
                    {
						state = STATE_PRIVATES;
						last_extension = localeID.privates;
					}
                    else 
                    { 
						state = STATE_EXTENSIONS;
						last_extension = localeID.extensions[subtag] || [];
						localeID.extensions[subtag] = last_extension;
					}
				}
                else if (state >= STATE_EXTENSIONS)
                {
					last_extension.push(subtag);
				}
			}
		}
		
        localeID.canonicalize();
		
        return localeID; 
	}
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
     *  Constructor.
     */
    public function LocaleID()
	{
        super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     */
	private var lang:String = "";
	
	/**
     *  @private
     */
    private var script:String = "";
	
	/**
     *  @private
     */
    private var region:String = "";
	
 	/**
     *  @private
     */
   private var extended_langs:Array = [];
	
 	/**
     *  @private
     */
   private var variants:Array = [];
	
	/**
     *  @private
     */
    private var extensions:Object = {};
	
	/**
     *  @private
     */
    private var privates:Array = [];
	
	/**
     *  @private
     */
    private var privateLangs:Boolean = false; 

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     */
	public function canonicalize():void
    {
		for (var i:String in extensions)
        {
			if (extensions.hasOwnProperty(i))
            {
				// Also clear zero length extensions.
				if (extensions[i].length == 0)
                    delete extensions[i];
				else
                    extensions[i] = extensions[i].sort();
			}
		}

		extended_langs = extended_langs.sort();
		variants = variants.sort();
		privates = privates.sort();
		
        if (script == "")
			script = LocaleRegistry.getScriptByLang(lang);
		
        // Still no script, check the region.
		if (script == "" && region != "")
			script = LocaleRegistry.getScriptByLangAndRegion(lang, region);
		
		if (region == "" && script != "")
        {
			region = LocaleRegistry.getDefaultRegionForLangAndScript(
                                            lang, script);
		}
	}
	
	/**
     *  @private
     */
	public function toString():String
    {
		var stack:Array = [ lang ];
		
        Array.prototype.push.apply(stack, extended_langs);
		
        if (script != "")
            stack.push(script);
		
        if (region != "")
            stack.push(region);
		
        Array.prototype.push.apply(stack, variants);
		
        for (var i:String in extensions)
        {
			if (extensions.hasOwnProperty(i))
            {
				stack.push(i);
				Array.prototype.push.apply(stack, extensions[i]);
			}
		}
		
        if (privates.length > 0)
        {
			stack.push("x");
			Array.prototype.push.apply(stack, privates);
		}

		return stack.join("_");
	} 
	
	/**
     *  @private
     */
	public function equals(locale:LocaleID):Boolean
    {
		return toString() == locale.toString();
	}
	
	/**
     *  @private
     */
	public function isSiblingOf(other:LocaleID):Boolean
    {
		return lang == other.lang && script == other.script;
	}
	
	/**
     *  @private
     */
    public function transformToParent():Boolean
    {
		if (privates.length > 0)
        {
			privates.splice(privates.length - 1, 1);
			return true;
		}
		
		var lastExtensionName:String = null;
		for (var i:String in extensions)
        {
			if (extensions.hasOwnProperty(i))
				lastExtensionName = i;
		}

		if (lastExtensionName)
        {
			var lastExtension:Array = extensions[lastExtensionName];
			if (lastExtension.length == 1)
            {
				delete extensions[lastExtensionName];
				return true;
			}
			lastExtension.splice(lastExtension.length - 1, 1);
			return true;
		}
		
		if (variants.length > 0)
        {
			variants.splice(variants.length - 1, 1);
			return true;
		}

		if (script != "")
        {
			// Check if we can suppress the script.
			if (LocaleRegistry.getScriptByLang(lang) != "")
            {
				script = "";
				return true;
			}
            else if (region == "")
            {
				// Maybe the default region can suppress the script.
				var defaultRegion:String =
                    LocaleRegistry.getDefaultRegionForLangAndScript(
                                        lang, script);
				if (defaultRegion != "")
                {
					region = defaultRegion;
					script = "";
					return true;
				}
			}
		}
		
		if (region != "")
        {
			if (!(script == "" && LocaleRegistry.getScriptByLang(lang) == ""))
            {
				region = "";
				return true;
			}
		}
		
		if (extended_langs.length > 0)
        {
			extended_langs.splice(extended_langs.length - 1, 1);
			return true;
		}
		
		return false;
	}
}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: LocaleRegistry
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class LocaleRegistry
{
	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     *  A list of codes representing writing systems, in arbitrary order.
     *  For example, "latn" represents the Latin alphabet, used for
     *  writing languages such as English, French, Indonesian, and Swahili,
     *  and "arab" represents the Arabic script, used for writing
     *  Arabic, Persian, Pashto, and Urdu.
     */
	private static const SCRIPTS:Array =
    [
        "",     // 0
        "latn", // 1    Latin
        "ethi", // 2    Ethiopian
        "arab", // 3    Arabic
        "beng", // 4    Bengali
        "cyrl", // 5    Cyrillic
        "thaa", // 6    Thaana 
        "tibt", // 7    Tibetan
        "grek", // 8    Greek
        "gujr", // 9    Gujarati
        "hebr", // 10   Hebrew
        "deva", // 11   Devanagari
        "armn", // 12   Armenian
        "jpan", // 13   Japanese
        "geor", // 14   Georgian
        "khmr", // 15   Khmer
        "knda", // 16   Kannada
        "kore", // 17   Korean
        "laoo", // 18   Lao
        "mlym", // 19   Malayalam
        "mymr", // 20   Burmese
        "orya", // 21   Oriya
        "guru", // 22   Punjabi
        "sinh", // 23   Sinhalese
        "taml", // 24   Tamil
        "telu", // 25   Telugu
        "thai", // 26   Thai
        "nkoo", // 27   N'Ko
        "blis", // 28   Bliss
        "hans", // 29   Simplified Chinese
        "hant", // 30   Traditional Chinese
        "mong", // 31   Mongolian
        "syrc"  // 32   Syriac
    ];
	
	/**
     *  @private
     *  The inverse of the SCRIPT Array.
     *  Maps a script code (like "jpan" for Japanese)
     *  to its index in the SCRIPT Array.
     */
    private static const SCRIPT_BY_ID:Object =
    {
        latn: 1,
        ethi: 2,
        arab: 3,
        beng: 4,
        cyrl: 5,
        thaa: 6,
        tibt: 7,
        grek: 8,
        gujr: 9,
        hebr: 10,
        deva: 11,
        armn: 12,
        jpan: 13,
        geor: 14,
        khmr: 15,
        knda: 16,
        kore: 17,
        laoo: 18,
        mlym: 19,
        mymr: 20,
        orya: 21,
        guru: 22,
        sinh: 23,
        taml: 24,
        telu: 25,
        thai: 26,
        nkoo: 27,
        blis: 28,
        hans: 29,
        hant: 30,
        mong: 31,
        syrc: 32
    };
	
	/**
     *  @private
     *  This table maps a language and a script to the most
     *  prominent region where that combination is used.
     *
     *  Note: "is" must be quoted because it is a reserved word.
     */
    private static const DEFAULT_REGION_BY_LANG_AND_SCRIPT:Object =
    {
        bg: { 5: "bg" },            // Bulgarian / Cyrillic -> Bulgaria
        ca: { 1: "es" },            // Catalan / Latin -> Spain
        zh: { 30: "tw", 29: "cn" }, // Chinese / Traditional Chinese -> Taiwan
                                    // Chinese / Simplified Chinese -> China
        cs: { 1: "cz" },            // Czech / Latin -> Czech Republic
        da: { 1: "dk" },            // Danish / Latin -> Denmark
        de: { 1: "de" },            // German / Latin -> Germany
        el: { 8: "gr" },            // Greek / Greek -> Greece
        en: { 1: "us" },            // English / Latin -> United States
        es: { 1: "es" },            // Spanish / Latin -> Spain
        fi: { 1: "fi" },            // Finnish / Latin -> Finland
        fr: { 1: "fr" },            // French / Latin -> France         
        he: { 10: "il" },           // Hebrew / Hebrew -> Israel
        hu: { 1: "hu" },            // Hungarian / Latin -> Hungary
        "is": { 1: "is" },          // Icelandic / Latin -> Iceland
        it: {  1: "it" },           // Italian / Latin -> Italy
        ja: { 13: "jp" },           // Japanese / Japanese -> Japan
        ko: { 17: "kr" },           // Korean / Korean -> Korea
        nl: { 1: "nl" },            // Dutch / Latin -> Netherlands
        nb: { 1: "no" },            // Norwegian Bokmaal / Latin -> Norway
        pl: { 1: "pl" },            // Polish / Latin -> Poland
        pt: { 1: "br" },            // Portuguese / Latin -> Brazil
        ro: { 1: "ro" },            // Romanian / Latin -> Romania
        ru: { 5: "ru" },            // Russian / Cyrillic -> Russia
        hr: { 1: "hr" },            // Croatian / Latin -> Croatia
        sk: { 1: "sk" },            // Slovak / Latin -> Slovakia
        sq: { 1: "al" },            // Albanian / Latin -> Albania
        sv: { 1: "se" },            // Swedish / Latin -> Sweden
        th: { 26: "th" },           // Thai / Thai -> Thailand
        tr: { 1: "tr" },            // Turkish / Latin -> Turkey
        ur: { 3: "pk" },            // Urdu / Arabic -> Pakistan
        id: { 1: "id" },            // Indonesian / Latin -> Indonesia
        uk: { 5: "ua" },            // Ukrainian / Cyrillic -> Ukraine
        be: { 5: "by" },            // Byelorussian / Cyrillic -> Belarus
        sl: { 1: "si" },            // Slovenian / Latin -> Slovenia
        et: { 1: "ee" },            // Estonian / Latin -> Estonia
        lv: { 1: "lv" },            // Latvian / Latin -> Latvia
        lt: { 1: "lt" },            // Lithuanian / Latin -> Lithuania
        fa: { 3: "ir" },            // Persian / Arabic -> Iran
        vi: { 1: "vn" },            // Vietnamese / Latin -> Vietnam
        hy: { 12: "am"},            // Armenian / Armenian -> Armenia
        az: { 1: "az", 5: "az" },   // Azerbaijani / Latin -> Azerbaijan
                                    // Azerbaijani / Cyrillic -> Azerbaijan
        eu: { 1: "es" },            // Basque / Latin -> Spain
        mk: { 5: "mk" },            // Macedonian / Cyrillic -> Macedonia
        af: { 1: "za" },            // Afrikaans / Latin -> South Africa
        ka: { 14: "ge" },           // Georgian / Georgian -> Georgia
        fo: { 1: "fo" },            // Faeroese / Latin -> Faroe Islands
        hi: { 11: "in" },           // Hindi / Devanagari -> India 
        ms: { 1: "my" },            // Malay / Latin -> Malaysia
        kk: { 5: "kz" },            // Kazakh / Cyrillic -> Kazakhstan
        ky: { 5: "kg" },            // Kirghiz / Cyrillic -> Kyrgyzstan
        sw: { 1: "ke" },            // Swahili / Latin -> Kenya
        uz: { 1: "uz", 5: "uz" },   // Uzbek / Latin -> Uzbekistan
                                    // Uzbek / Cyrillic -> Uzbekistan
        tt: { 5: "ru" },            // Tatar / Cyrillic -> Russia
        pa: { 22: "in" },           // Punjabi / Punjabi -> India
        gu: { 9: "in" },            // Gujarati / Gujarati -> India
        ta: { 24: "in" },           // Tamil / Tamil -> India
        te: { 25: "in" },           // Telugu / Telugu -> India
        kn: { 16: "in" },           // Kannada / Kannada -> India
        mr: { 11: "in" },           // Marathi / Devanagari -> India
        sa: { 11: "in" },           // Sanskrit / Devanagari -> India
        mn: { 5: "mn" },            // Mongolian / Cyrillic -> Mongolia
        gl: { 1: "es" },            // Galician / Latin -> Spain
        kok: { 11: "in" },          // Konkani / Devanagari -> India
        syr: { 32: "sy" },          // Syriac / Syriac -> Syria
        dv: { 6: "mv" },            // Dhivehi / Thaana -> Maldives
        nn: { 1: "no" },            // Norwegian Nynorsk / Latin -> Norway
        sr: { 1: "cs", 5: "cs" },   // Serbian / Latin -> Serbia
                                    // Serbian / Cyrillic -> Serbia
        cy: { 1: "gb" },            // Welsh / Latin -> United Kingdom
        mi: { 1: "nz" },            // Maori / Latin -> New Zealand
        mt: { 1: "mt" },            // Maltese / Latin -> Malta
        quz: { 1: "bo" },           // Quechua / Latin -> Bolivia
        tn: { 1: "za" },            // Tswana / Latin -> South Africa
        xh: { 1: "za" },            // Xhosa / Latin -> South Africa
        zu: { 1: "za" },            // Zulu / Latin -> South Africa
        nso: { 1: "za" },           // Northern Sotho / Latin -> South Africa
        se: { 1: "no" },            // Northern Saami / Latin -> Norway
        smj: { 1: "no" },           // Lule Saami / Latin -> Norway
        sma: { 1: "no" },           // Southern Saami / Latin -> Norway
        sms: { 1: "fi" },           // Skolt Saami / Latin -> Finland
        smn: { 1: "fi" },           // Inari Saami / Latin -> Finland
        bs: { 1: "ba" }             // Bosnia / Latin -> Bosnia
    };
	
	/**
     *  @private
     *  This table maps a language to a script.
     *  It was derived from the entries at
     *  http://www.iana.org/assignments/language-subtag-registry
     *  which have a Suppress-Script property.
     *
     *  Note: "as", "in", and "is" must be quoted
     *  because they are reserved words.
     */
    private static const SCRIPT_ID_BY_LANG:Object =
    {
        ab: 5,      // Abkhazian -> Cyrillic
        af: 1,      // Afrikaans -> Latin
        am: 2,      // Amharic -> Ethiopian
        ar: 3,      // Arabic -> Arabic
        "as": 4,    // Assamese -> Bengali
        ay: 1,      // Aymara -> Latin
        be: 5,      // Belarusian -> Cyrillic
        bg: 5,      // Bulgarian -> Cyrillic
        bn: 4,      // Bengali -> Bengali
        bs: 1,      // Bosnian -> Latin
        ca: 1,      // Catalan / Valencian -> Latin
        ch: 1,      // Chamorro -> Latin
        cs: 1,      // Czech -> Latin
        cy: 1,      // Welsh -> Latin
        da: 1,      // Danish -> Latin
        de: 1,      // German -> Latin
        dv: 6,      // Dhivehi / Maldivian -> Thaana
        dz: 7,      // Dzongkha -> Tibetan
        el: 8,      // Modern Greek -> Greek
        en: 1,      // English -> Latin
        eo: 1,      // Esperanto -> Latin
        es: 1,      // Spanish / Castilian -> Latin
        et: 1,      // Estonian -> Latin
        eu: 1,      // Basque -> Latin
        fa: 3,      // Persian -> Arabic
        fi: 1,      // Finnish -> Latin
        fj: 1,      // Fijian -> Latin
        fo: 1,      // Faroese -> Latin
        fr: 1,      // French -> Latin
        frr: 1,     // Northern Frisian -> Latin
        fy: 1,      // Western Frisian -> Latin
        ga: 1,      // Irish -> Latin
        gl: 1,      // Galician -> Latin
        gn: 1,      // Guarani -> Latin
        gu: 9,      // Gujarati -> Gujarati
        gv: 1,      // Manx -> Latin
        he: 10,     // Hebrew -> Hebrew
        hi: 11,     // Hindi -> Devanagari
        hr: 1,      // Croatian -> Latin
        ht: 1,      // Haitian Creole -> Latin
        hu: 1,      // Hungarian -> Latin
        hy: 12,     // Armenian -> Armenian
        id: 1,      // Indonesian -> Latin
        "in": 1,    // Indonesian -> Latin
        "is": 1,    // Icelandic -> Latin
        it: 1,      // Italian -> Latin
        iw: 10,     // Hebrew -> Hebrew
        ja: 13,     // Japanese -> Japanese
        ka: 14,     // Georgian -> Georgian
        kk: 5,      // Kazakh -> Cyrillic
        kl: 1,      // Kalaallisut / Greenlandic -> Latin
        km: 15,     // Central Khmer -> Khmer
        kn: 16,     // Kannada -> Kannada
        ko: 17,     // Korean -> Korean
        la: 1,      // Latin -> Latin
        lb: 1,      // Luxembourgish -> Latin
        ln: 1,      // Lingala -> Latin
        lo: 18,     // Lao -> Lao
        lt: 1,      // Lithuanian -> Latin
        lv: 1,      // Latvian -> Latin
        mg: 1,      // Malagasy -> Latin
        mh: 1,      // Marshallese -> Latin
        mk: 5,      // Macedonian -> Cyrillic
        ml: 19,     // Malayalam -> Malayalam
        mo: 1,      // Moldavian -> Latin
        mr: 11,     // Marathi -> Devanagari
        ms: 1,      // Malay -> Latin
        mt: 1,      // Maltese -> Latin
        my: 20,     // Burmese -> Burmese
        na: 1,      // Nauru -> Latin
        nb: 1,      // Norwegian Bokmaal -> Latin
        nd: 1,      // North Ndebele -> Latin
        ne: 11,     // Nepali -> Devanagari
        nl: 1,      // Dutch / Flemish -> Latin
        nn: 1,      // Norwegian Nynorsk -> Latin
        no: 1,      // Norwegian -> Latin
        nr: 1,      // South Ndebele -> Latin
        ny: 1,      // Chichewa / Chewa / Nyanja -> Latin
        om: 1,      // Oromo -> Latin
        or: 21,     // Oriya -> Oriya
        pa: 22,     // Punjabi -> Punjabi
        pl: 1,      // Polish -> Latin
        ps: 3,      // Pashto -> Arabic
        pt: 1,      // Portuguese -> Latin
        qu: 1,      // Quechua -> Latin
        rn: 1,      // Rundi -> Latin
        ro: 1,      // Romanian -> Latin
        ru: 5,      // Russian -> Cyrillic
        rw: 1,      // Kinyarwanda -> Latin
        sg: 1,      // Sango -> Latin
        si: 23,     // Sinhalese -> Sinhalese
        sk: 1,      // Slovak -> Latin
        sl: 1,      // Slovenian -> Latin
        sm: 1,      // Samoan -> Latin
        so: 1,      // Somali -> Latin
        sq: 1,      // Albanian -> Latin
        ss: 1,      // Swati -> Latin
        st: 1,      // Southern Sotho -> Latin
        sv: 1,      // Swedish -> Latin
        sw: 1,      // Swahili -> Latin
        ta: 24,     // Tamil -> Tamil
        te: 25,     // Telugu -> Telugu
        th: 26,     // Thai -> Thai
        ti: 2,      // Tigrinya -> Ethiopian
        tl: 1,      // Tagalog -> Latin
        tn: 1,      // Tswana -> Latin
        to: 1,      // Tonga -> Latin
        tr: 1,      // Turkish -> Latin
        ts: 1,      // Tsonga -> Latin
        uk: 5,      // Ukrainian -> Cyrillic
        ur: 3,      // Urdu -> Arabic
        ve: 1,      // Venda -> Latin
        vi: 1,      // Vietnamese -> Latin
        wo: 1,      // Wolof -> Latin
        xh: 1,      // Xhosa -> Latin
        yi: 10,     // Yiddish -> Hebrew
        zu: 1,      // Zulu -> Latin
        cpe: 1,     // Creoles and pidgins -> Latin
        dsb: 1,     // Lower Sorbian -> Latin
        frs: 1,     // Eastern Frisian -> Latin
        gsw: 1,     // Swiss German / Alemaanic / Alsatian -> Latin
        hsb: 1,     // Upper Sorbian -> Latin
        kok: 11,    // Konkana -> Devanagari
        mai: 11,    // Maithili -> Devanagari
        men: 1,     // Mende -> Latin
        nds: 1,     // Low German -> Latin
        niu: 1,     // Niuean -> Latin
        nqo: 27,    // N'Ko -> N'Ko
        nso: 1,     // Northern Sotho / Pedi / Sepedi -> Latin
        son: 1,     // Songhai -> Latin
        tem: 1,     // Timne -> Latin
        tkl: 1,     // Tokelau -> Latin
        tmh: 1,     // Tamashek -> Latin
        tpi: 1,     // Tok Pisin -> Latin
        tvl: 1,     // Tuvalu -> Latin
        zbl: 28     // Bliss -> Bliss
    };
	
	/**
     *  @private
     *  This table maps a language-as-spoken-in-a-region
     *  to the script used to write it.
     *
     *  Chinese in China -> Simplified Chinese
     *  Chinese in Singapore -> Simplified Chinese
     *  Chinese in Taiwan -> Traditional Chinese
     *  Chinese in Hong Kong -> Traditional Chinese 
     *  Chinese in Macao -> Traditional Chinese 
     *  Mongolian in China -> Mongolian
     *  Mongolian in Singapore -> Cyrillic
     *  Punjabi in Pakistan -> Arabic
     *  Punjabi in India -> Punjabi
     *  Hausa in Ghana -> Latin
     *  Hausa in Niger -> Latin
     */
    private static const SCRIPT_ID_BY_LANG_AND_REGION:Object =
    {
        zh: { cn: 29, sg: 29, tw: 30, hk: 30, mo: 30 },
        mn: { cn: 31, sg: 5 },
        pa: { pk: 3, "in": 22 }, // "in" is reserved word
        ha: { gh: 1, ne: 1 }
    };

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
     *  @private
     *  Given a language and a region, returns the script system
     *  used to write the language there.
     *
     *  Examples:
     *  lang zh (Chinese), region cn (China) -> hans (Simplified Chinese)
     *  lang zh (Chinese), region tw (Taiwan) -> hast (Traditional Chinese)
     */
    public static function getScriptByLangAndRegion(
                                lang:String, region:String):String
    {
		var langRegions:Object = SCRIPT_ID_BY_LANG_AND_REGION[lang];
		if (langRegions == null)
            return "";
		
        var scriptID:Object = langRegions[region];
		if (scriptID == null)
            return "";

        return SCRIPTS[int(scriptID)].toLowerCase();
	}
	
	/**
     *  @private
     *  Given a language, returns the script generally used to write it.
     *
     *  Examples:
     *  lang bg (Bulgarian) -> cyrl (Cyrillic)
     */
	public static function getScriptByLang(lang:String):String
    {
		var scriptID:Object = SCRIPT_ID_BY_LANG[lang];
        if (scriptID == null)
		    return "";

		return SCRIPTS[int(scriptID)].toLowerCase();
	}
	
	/**
     *  @private
     *  Given a language and a script used for writing it,
     *  returns the most prominent region where that combination is used.
     *
     *  Examples:
     */
	public static function getDefaultRegionForLangAndScript(
                                lang:String, script:String):String
    {
		var langObj:Object = DEFAULT_REGION_BY_LANG_AND_SCRIPT[lang];
		var scriptID:Object = SCRIPT_BY_ID[script];
		if (langObj == null || scriptID == null)
            return "";
		
		return langObj[int(scriptID)] || "";
	} 
}
