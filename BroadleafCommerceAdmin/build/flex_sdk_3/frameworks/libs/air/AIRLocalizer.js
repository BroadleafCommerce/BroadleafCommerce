/* AIRLocalizer.js - Revision: 1.0 */
// Copyright 2007-2008. Adobe Systems Incorporated.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//   * Redistributions of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//   * Redistributions in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//   * Neither the name of Adobe Systems Incorporated nor the names of its
//     contributors may be used to endorse or promote products derived from this
//     software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.


// Using the function method to obfuscate inner globals to the main script, so that the 
// "window" remains untouched.
(function(){
	/**
	 * This is automatically replaced while building
	 * @version "1.0"
	 * @private	
	 */
	var version = "1.0";
	
	
	/**
	 * Just to make sure we have the right shortcuts included
	 * @namespace window.air
	 * @private	
	 */
	var air = {
		File : window.runtime.flash.filesystem.File,
	    FileStream : window.runtime.flash.filesystem.FileStream,
	    FileMode : window.runtime.flash.filesystem.FileMode,
	    Capabilities : window.runtime.flash.system.Capabilities
	};
	
	/**
	 * Utility functions
	 * @namespace util
	 * @private	
	 */
	var util = {
		/**
		 * Returns the content of a file
		 * @param file air.File File that should be read
		 * @return string
		 */
		stringFromFile : function stringFromFile(file){
			var fileStream = new air.FileStream();
			fileStream.open(file, air.FileMode.READ);
			var result = fileStream.readUTFBytes(file.size);
			fileStream.close();
			return result;
		}
	};
	
	/**
	 * Used for sorting the locales array
	 * @private
	 */
	
	function LocaleId(){
		this.lang = '';
		this.script = '';
		this.region = '';
		this.extended_langs = [];
		this.variants = [];
		this.extensions = {};
		this.privates = [];
		this.privateLangs = false;
	};
	
	
 	LocaleId.prototype = {
		transformToParent: function(){
			if(this.privates.length>0){
				this.privates.splice(this.privates.length-1, 1);
				return true;
			}
			
			var lastExtensionName = null;
			for(var i in this.extensions){
				if(this.extensions.hasOwnProperty(i)){
					lastExtensionName = i;
				}
			}
			if(lastExtensionName){
				var lastExtension = this.extensions[lastExtensionName];
				if(lastExtension.length==1){
					delete this.extensions[ lastExtensionName ];
					return true;
				}
				lastExtension.splice(lastExtension.length-1, 1);
				return true;
			}
			
			if(this.variants.length>0){
				this.variants.splice(this.variants.length-1, 1);
				return true;
			}

			if(this.script!=''){
				//check if we can surpress the script
			    if(LocaleId.getScriptByLang(this.lang)!=''){
					this.script='';
					return true;
			    }else if(this.region==''){
					//maybe the default region can surpress the script
					var region = LocaleId.getDefaultRegionForLangAndScript(this.lang, this.script);
					if(region!=''){
						this.region = region;
						this.script = '';
						return true;
					}
				}
			}
			
			if(this.region!=''){
				if(!(this.script=='' && LocaleId.getScriptByLang(this.lang) == '')){
					this.region='';
					return true;
				}
			}
			
			
			if(this.extended_langs.length>0){
				this.extended_langs.splice(this.extended_langs.length-1, 1);
				return true;
			}
			
			return false;
		},
		
		isSiblingOf: function(other){
			if(this.lang==other.lang&&this.script==other.script){
				return true;
			}
			return false;
		},
		
		equals: function(locale){
			return this.toString() == locale.toString();
		},
		toString: function(){
			var stack = [ this.lang ];
			Array.prototype.push.apply(stack, this.extended_langs);
			if(this.script!='') stack.push(this.script);
			if(this.region!='') stack.push(this.region);
			Array.prototype.push.apply(stack, this.variants);
			for(var i in this.extensions){
				if(this.extensions.hasOwnProperty(i)){
					stack.push(i);
					Array.prototype.push.apply(stack, this.extensions[i]);
				}
			}
			if(this.privates.length>0){
				stack.push('x');
				Array.prototype.push.apply(stack, this.privates);
			}
			return stack.join('_');
		},
		canonicalize: function(){
			for(var i in this.extensions){
				if(this.extensions.hasOwnProperty(i)){
					//also clear zero length extensions
					if(this.extensions[i].length==0) delete this.extensions[i];
					else this.extensions[i] = this.extensions[i].sort();
				}
			}
			this.extended_langs = this.extended_langs.sort();
			this.variants = this.variants.sort();
			this.privates = this.privates.sort();
			if(this.script == ''){
				this.script = LocaleId.getScriptByLang(this.lang);
			}
			//still no script, check the region
			if(this.script == '' && this.region!=''){
				this.script = LocaleId.getScriptByLangAndRegion(this.lang, this.region);
			}
			
			if(this.region=='' && this.script!=''){
				this.region = LocaleId.getDefaultRegionForLangAndScript(this.lang, this.script);
			}
		}
	}
	
	LocaleId.registry = {
		"scripts": ["cyrl", "latn", "ethi", "arab", "beng", "cyrl", "thaa", "tibt", "grek", "gujr", "hebr", "deva", "armn", "jpan", "geor", "khmr", "knda", "kore", "laoo", "mlym", "mymr", "orya", "guru", "sinh", "taml", "telu", "thai", "nkoo", "blis", "hans", "hant", "mong", "syrc"],
	 	"scriptById": {"cyrl": 5, "latn": 1, "ethi": 2, "arab": 3, "beng": 4, "thaa": 6, "tibt": 7, "grek": 8, "gujr": 9, "hebr": 10, "deva": 11, "armn": 12, "jpan": 13, "geor": 14, "khmr": 15, "knda": 16, "kore": 17, "laoo": 18, "mlym": 19, "mymr": 20, "orya": 21, "guru": 22, "sinh": 23, "taml": 24, "telu": 25, "thai": 26, "nkoo": 27, "blis": 28, "hans": 29, "hant": 30, "mong": 31, "syrc": 32} , 
		"defaultRegionByLangAndScript": {"bg": {5: "bg"}, "ca": {1: "es"}, "zh": {30: "tw", 29: "cn"}, "cs": {1: "cz"}, "da": {1: "dk"}, "de": {1: "de"}, "el": {8: "gr"}, "en": {1: "us"}, "es": {1: "es"}, "fi": {1: "fi"}, "fr": {1: "fr"}, "he": {10: "il"}, "hu": {1: "hu"}, "is": {1: "is"}, "it": {1: "it"}, "ja": {13: "jp"}, "ko": {17: "kr"}, "nl": {1: "nl"}, "nb": {1: "no"}, "pl": {1: "pl"}, "pt": {1: "br"}, "ro": {1: "ro"}, "ru": {5: "ru"}, "hr": {1: "hr"}, "sk": {1: "sk"}, "sq": {1: "al"}, "sv": {1: "se"}, "th": {26: "th"}, "tr": {1: "tr"}, "ur": {3: "pk"}, "id": {1: "id"}, "uk": {5: "ua"}, "be": {5: "by"}, "sl": {1: "si"}, "et": {1: "ee"}, "lv": {1: "lv"}, "lt": {1: "lt"}, "fa": {3: "ir"}, "vi": {1: "vn"}, "hy": {12: "am"}, "az": {1: "az", 5: "az"}, "eu": {1: "es"}, "mk": {5: "mk"}, "af": {1: "za"}, "ka": {14: "ge"}, "fo": {1: "fo"}, "hi": {11: "in"}, "ms": {1: "my"}, "kk": {5: "kz"}, "ky": {5: "kg"}, "sw": {1: "ke"}, "uz": {1: "uz", 5: "uz"}, "tt": {5: "ru"}, "pa": {22: "in"}, "gu": {9: "in"}, "ta": {24: "in"}, "te": {25: "in"}, "kn": {16: "in"}, "mr": {11: "in"}, "sa": {11: "in"}, "mn": {5: "mn"}, "gl": {1: "es"}, "kok": {11: "in"}, "syr": {32: "sy"}, "dv": {6: "mv"}, "nn": {1: "no"}, "sr": {1: "cs", 5: "cs"}, "cy": {1: "gb"}, "mi": {1: "nz"}, "mt": {1: "mt"}, "quz": {1: "bo"}, "tn": {1: "za"}, "xh": {1: "za"}, "zu": {1: "za"}, "nso": {1: "za"}, "se": {1: "no"}, "smj": {1: "no"}, "sma": {1: "no"}, "sms": {1: "fi"}, "smn": {1: "fi"}, "bs": {1: "ba"}},
		"scriptIdByLang": {"ab": 0, "af": 1, "am": 2, "ar": 3, "as": 4, "ay": 1, "be": 5, "bg": 5, "bn": 4, "bs": 1, "ca": 1, "ch": 1, "cs": 1, "cy": 1, "da": 1, "de": 1, "dv": 6, "dz": 7, "el": 8, "en": 1, "eo": 1, "es": 1, "et": 1, "eu": 1, "fa": 3, "fi": 1, "fj": 1, "fo": 1, "fr": 1, "frr": 1, "fy": 1, "ga": 1, "gl": 1, "gn": 1, "gu": 9, "gv": 1, "he": 10, "hi": 11, "hr": 1, "ht": 1, "hu": 1, "hy": 12, "id": 1, "in": 1, "is": 1, "it": 1, "iw": 10, "ja": 13, "ka": 14, "kk": 5, "kl": 1, "km": 15, "kn": 16, "ko": 17, "la": 1, "lb": 1, "ln": 1, "lo": 18, "lt": 1, "lv": 1, "mg": 1, "mh": 1, "mk": 5, "ml": 19, "mo": 1, "mr": 11, "ms": 1, "mt": 1, "my": 20, "na": 1, "nb": 1, "nd": 1, "ne": 11, "nl": 1, "nn": 1, "no": 1, "nr": 1, "ny": 1, "om": 1, "or": 21, "pa": 22, "pl": 1, "ps": 3, "pt": 1, "qu": 1, "rn": 1, "ro": 1, "ru": 5, "rw": 1, "sg": 1, "si": 23, "sk": 1, "sl": 1, "sm": 1, "so": 1, "sq": 1, "ss": 1, "st": 1, "sv": 1, "sw": 1, "ta": 24, "te": 25, "th": 26, "ti": 2, "tl": 1, "tn": 1, "to": 1, "tr": 1, "ts": 1, "uk": 5, "ur": 3, "ve": 1, "vi": 1, "wo": 1, "xh": 1, "yi": 10, "zu": 1, "cpe": 1, "dsb": 1, "frs": 1, "gsw": 1, "hsb": 1, "kok": 11, "mai": 11, "men": 1, "nds": 1, "niu": 1, "nqo": 27, "nso": 1, "son": 1, "tem": 1, "tkl": 1, "tmh": 1, "tpi": 1, "tvl": 1, "zbl": 28}, 
		"scriptIdByLangAndRegion": {"zh": {"cn": 29, "sg": 29, "tw": 30, "hk": 30, "mo": 30}, "mn": {"cn": 31, "sg": 5}, "pa": {"pk": 3, "in": 22}, "ha": {"gh": 1, "ne": 1}}};                                             
	
	
	LocaleId.getScriptByLangAndRegion = function(lang, region){
		var langRegions = LocaleId.registry.scriptIdByLangAndRegion[ lang ];
		if(typeof langRegions=='undefined') return '';
		var scriptId = langRegions[region];
		if(typeof scriptId!='undefined'){
			return LocaleId.registry.scripts[scriptId].toLowerCase();
		}
		return '';
	}
	
	LocaleId.getScriptByLang = function(lang){
		var scriptId = LocaleId.registry.scriptIdByLang[ lang ];
		if(typeof scriptId!='undefined'){
			return LocaleId.registry.scripts[scriptId].toLowerCase();
		}
		return '';
	}

	LocaleId.getDefaultRegionForLangAndScript = function(lang, script){
		var lang = LocaleId.registry.defaultRegionByLangAndScript[ lang ];
		var scriptId = LocaleId.registry.scriptById[ script ];
		if(typeof lang!='undefined' && typeof scriptId !='undefined' ){
			return lang[scriptId] || "";
		}
		return '';
	}

	
	LocaleId.fromString = function(str){
		//states
		{ 
			var PrimaryLanguage = 0,
				ExtendedLanguages = 1,
				Script = 2,
				Region = 3,
				Variants = 5,  
				Extensions = 6,
				Privates = 7;
		}
		var localeId = new LocaleId();

		var state = PrimaryLanguage;
		var subtags = str.replace(/-/g, '_').split('_');
        
		var last_extension;

		for(var i=0, l=subtags.length; i<l ;i++){
			var subtag = subtags[i].toLowerCase();
			
			if(state==PrimaryLanguage){
				if(subtag=='x'){
					localeId.privateLangs = true; //not used in our implementation, but makes the tag private
				}else if(subtag=='i'){
					localeId.lang += 'i-';	//and wait the next subtag to complete the language name
				}else{
					localeId.lang += subtag;
					state ++;
				}
			}else{
				//looging for an extended language 	- 3 chars
				//			   a script 			- 4 chars
				//			   a region				- 2-3 chars
				//			   a variant			- alpha with at least 5 chars or numeric with at least 4 chars
				//			  an extension/private singleton - 1 char
				
				var subtag_length = subtag.length; //store it for faster use later
				if(subtag_length==0) continue; //skip zero-lengthed subtags
				var firstChar = subtag[0].toLowerCase();
				
				if(state<=ExtendedLanguages && subtag_length==3){
				    localeId.extended_langs.push(subtag);
					if(localeId.extended_langs.length==3){ //allow a maximum of 3 extended langs
						state = Script;
					}
				}else if ( state <= Script && subtag_length==4 ){
					localeId.script = subtag;
					state = Region;
				}else if( state <= Region && (subtag_length==2 || subtag_length==3) ){
					localeId.region = subtag;
					state = Variants;
				}else if ( state <= Variants && 
						( 
							( firstChar>='a' && firstChar<='z' && subtag_length>=5 ) 
													|| 
							( firstChar>='0' && firstChar<='9' && subtag_length>=4 ) 
						)
				  ){
					//variant
					localeId.variants.push(subtag);
					state = Variants;
				}else if ( state < Privates && subtag_length==1 ){ //singleton
					if(subtag == 'x'){
						state = Privates;
						last_extension = localeId.privates;
					} else { 
						state = Extensions;
						last_extension = localeId.extensions[subtag] || [];
						localeId.extensions[subtag] = last_extension;
					}
				}else if(state >= Extensions){
					last_extension.push(subtag);
				}
			}
		}
		localeId.canonicalize();
		return localeId;
	}
	
    var LocaleSorter = {
		/**
		 * Promote only that locales from preferenceLocales that have parents in locales
		 * @private
		 * @param locales String[] List of locales to be sorted. 
		 * @param preferenceLocales String[] List of locales in the preference order
		 * @param addAll Adds all the locales at the end, even though no locale is in the preferences list. Default is true
		 * @return String[]
		 */
		sortLocalesUsingPreferences: function(_locales, _preferenceLocales, ultimateFallbackLocale, addAll){
			var result = [];
			var haveLocale = {};
			
			function prepare(list){
				var resultList = []; 
				for(var i=0,l=list.length;i<l;i++) {
					resultList.push (list[i].toLowerCase().replace(/-/g,'_'));
				}
				return resultList;
			}
			
			var locales = prepare(_locales);
			var preferenceLocales = prepare(_preferenceLocales);

			if(ultimateFallbackLocale&&ultimateFallbackLocale!=''){
				var ultimateFallbackLocale = ultimateFallbackLocale.toLowerCase().replace(/-/g,'_');
				var found = false;
				for(var i=0, l=preferenceLocales.length; i<l; i++){
					if(preferenceLocales[i]==ultimateFallbackLocale){
						found = true;
					}
				}
				if(!found){
					preferenceLocales.push(ultimateFallbackLocale);
				}
			}
			
			for(var j=0, k=locales.length; j<k; j++){
				haveLocale[ locales[j] ] = j;
			}
			
			function promote(locale){
				if(typeof haveLocale[locale]!='undefined'){
					result.push( _locales[ haveLocale[locale] ] );
					delete haveLocale[locale];
				}
			}
			

			
			for(var i=0, l=preferenceLocales.length; i<l; i++){
				var plocale = LocaleId.fromString( preferenceLocales[i] );
				
				// step 1: promote the perfect match
				promote(preferenceLocales[i]);

				promote(plocale.toString());
               
 				// step 2: promote the parent chain
				while(plocale.transformToParent()){
					promote(plocale.toString());
				}
				
				//parse it again
			    plocale = LocaleId.fromString( preferenceLocales[i] );
			    // step 3: promote the order of siblings from preferenceLocales
				for(var j=0; j<l; j++){
					var locale = preferenceLocales[j];
					if( plocale.isSiblingOf ( LocaleId.fromString( locale ) ) ){
						promote(locale);
					}
				}				
				// step 4: promote all remaining siblings (aka not in preferenceLocales)
				for(var j=0, k=locales.length; j<k; j++){
					var locale = locales[j];
					if( plocale.isSiblingOf( LocaleId.fromString( locale ) ) ){
						promote( locale );
					}
				}
				
			}
			if(addAll){
				// check what locales are not already loaded and push them. 
				// using the "for" because we want to preserve order
				for(var j=0, k=locales.length; j<k; j++){
					promote(locales[j]);
				}
			}
			return result;
		},
	};
	
	/**
	 * LocalizerEvent
	 * @constructor
	 * @private
	 */
	function LocalizerEvent(name, config){
		for(var prop in config){
			this[prop] = config[prop];
		}
		this.name = name;
	};
	
	LocalizerEvent.prototype = {
		toString: function(){
			return "[LocalizerEvent: "+this.name+"]";
		}
	}
	
	/**
	 * Parses the property files and generates a collection of keys and values
	 * @constructor
	 * @private	
	 */
	function ResourceBundle(bundleFile){  
		this.keys = {};
		try{
			this.parse(util.stringFromFile(bundleFile));
			this.found = true;
		}catch(e){
			// file not found
			// die silently
			this.found = false;
		}
	};
	
	ResourceBundle.prototype = {
		/**
		 * Parses the property file
		 * @param source Content of the property file
		 */
		parse: function(source){
			var source = source.replace(/\r\n/g, "\n").replace(/\r/g, "\n").replace(/^(?:\s*?)(?:!|#)([^\n\r]*)$/gm, ''); 
			var rg = /^(?:[ \t\f]*)((?:\\=|\\:|\\\\|[^=:\s])+)(?:[ \t\f]*)(?:$|(?:[=: \t\f](?:[ \t\f]*)((?:[^\n\r]*(?:\\(?:\r\n|\n|\r))+)*[^\n\r]*)$))/gm;
			var reformat = /\\(?:\r\n|\n|\r)(?:\s*)/g;
			var decoder = /\\(.)/g;
			var decoderCodes = { ':' : ':', '=':'=', 'n': "\n", 'r':"\r", 't':"\t", "\\":"\\" };
			var decoderFn = function(r, a){ return decoderCodes.hasOwnProperty(a) ? decoderCodes[a] : r }
			var line;
			while( line = rg.exec(source) ){
				var nodeName = line[1].replace(/\uFEFF/g, '').replace(decoder, decoderFn);
				var value = line[2] || "";
				this.keys[nodeName] = value.replace(reformat, "").replace(decoder, decoderFn);
			}
		},
		/**
		 * Returns the value of a resource     
		 * @param key string The key name of the resource. This is case sensitvie
		 * @return string
		 */
		get: function(key){
			return this.keys[key] || null;
		}
	};
	
	/**
	 * Holds the content of a localized file
	 * @constructor
	 * @private	
	 */
	function ResourceFile(bundleFile){
		try{
			this.content = util.stringFromFile(bundleFile);
			this.found = true;
		}catch(e){
			// file not found
			// die silently
			this.content = null;
			this.found = false;
		}
	};
	
	ResourceFile.prototype = {
		/**
		 * Returns the content of the file
		 * @return string
		 */
		getContent: function(){
			return this.content;
		}
	};
	
	/**
	 * Takes every node on the DOM and replaces attributes based on a prefix
	 * @param parent LocalizerPrivate The localizer object used to get the resource strings
	 * @constructor
	 * @private
	 */
	function DOMWalker(parent){
		this.parent = parent;
	}
	
	DOMWalker.prototype = {
		/**
		 * Chechs if an attribute is a local attribute
		 * @param attr DOMAttribute The attribute that should be checked 
		 * @return bool 
		 */
		isLocalAttribute: function(attr){
			return attr.nodeName.toLowerCase().substr(0, this.attributePrefixLength)==this.attributePrefix;
		},
		
		/**
		 * Returns the attribute name that should be replace on a local attribute
		 * @param attr DOMAttribute The attribute that should be checked 
		 * @return string
		 */
		getLocalAttribute: function(attr){
			return attr.nodeName.toLowerCase().substr(this.attributePrefixLength);
		},

		/**
		 * Replaces attributes on a single node
		 * @private
		 */
		walkNode: function(node){
			var attributePrefix = this.attributePrefix;
			var attributeInnerPrefix = attributePrefix+'innerhtml';
			if(!node.attributes) return;
			var params = [];
			var setInner = false;
			var innerValue = null;
			for(var i=node.attributes.length-1;i>=0;i--){
				var attr = node.attributes[i];
				if(attr.name.toLowerCase()==attributeInnerPrefix){
					setInner = true;
					innerValue = attr.value;
				}else if(this.isLocalAttribute(attr)){
					params.push([this.getLocalAttribute(attr), attr.value]);
				}
			}
			if(setInner){
				var value = this.getHtmlString(innerValue);
				try{
					node.innerHTML = value||innerValue;
				}catch(e){
					node.textContent = value||innerValue;
				}
			}
			for(var i=params.length-1;i>=0;i--){
				var param = params[i];
				var value = this.getHtmlString(param[1]);
				node.setAttribute(param[0], value||param[1]);
			}
		},
		
		/**
		 * Replaces each node under 'node'
		 * @param node DOMElement
		 * @public
		 */
		run: function (node){
			this.attributePrefix = this.parent.attributePrefix.toLowerCase();
			this.attributePrefixLength = this.attributePrefix.length;
			var treeWalker = document.createTreeWalker(
			    node||document,
			    NodeFilter.SHOW_ELEMENT,
			    { acceptNode: function(node) { return NodeFilter.FILTER_ACCEPT; } },
			    false
			);
			while(treeWalker.nextNode()) this.walkNode(treeWalker.currentNode);
		},
		
		/**
		 * Separates the bundleName and resourceName and returns the string from locale chain
		 * @param resourceName String Default bundle is "default"
		 * @return String
		 */
		getHtmlString: function(resourceName){
			var bundleName;
			var i = resourceName.indexOf('.');
			if(i!=-1){ bundleName = resourceName.substr(0, i); resourceName=resourceName.substr(i+1); }
			else bundleName = "default"
			return this.parent.getStringFromChain(bundleName, resourceName);
		}
	};
 
	
	
	/**
	 * Creates a new object used in Localizer to store loaded bundles, locale chain and bundles path 
	 * @private
	 * @constructor
	 * @v
	 */
	function LocalizerPrivate(parent){
		this.parent = parent;

		// dom walker needed to update nodes
		this.domWalker = new DOMWalker(this);
		
		
		// Locale chain is the internal order that the framework uses to search for locales
		this.localeChain = [];
		
		// When the developer sets the locale chain we should disable the automatic detection
		this.autoLocaleChain = true;
		
		// Loaded locales should be sorted for later use
		this.localeCache = {};
		
		// Default bundle path used to solve the locales directories
		this.bundlePath = new air.File('app:/locale/');
		
		// Store the system capabilities for later use
		this.userPreference = air.Capabilities.languages || [air.Capabilities.language];
		
		// Event listeners
		this.eventListeners = {};
		
		this.attributePrefix = 'local_';

	}                           
	LocalizerPrivate.prototype = {
		
		/**
		 * Creates a new locale object that caches bundles and files
		 * registers the locale for later use
		 * @private
		 * @param locale The localeName to be created
		 * @return LocaleHash
		 */    
		createLocaleCache: function(locale){
			var obj = {
				name : locale, 
				bundles: {},
				files: {}
			};
			this.localeCache[locale] = obj;
			return obj;
		},
		
		/**
		 * Returns the locale cache object. In case it is not already loaded it is created.
		 * @private
		 * @param locale String The localeName to be returned
		 * @return LocaleHash
		 */
		getLocaleCache: function(locale){
			return this.localeCache[locale] || this.createLocaleCache(locale);
		},
		
		
		/**
		 * Removes all the cached locales that are not in the locale chain
		 * @private
		 */
		cleanupCache: function (){
			var isInChain = {};
			for(var i in this.localeChain) isInChain[ this.localeChain[i] ] = true;
			
			for(var locale in this.localeCache ) {
				if( ! isInChain[locale] ){
					this.localeCache [ locale ] = null;
					delete this.localeCache [locale];
				}
			}
		},  
		    
		/**
		 * Removes all the cached locales 
		 * @private
		 */
		clearCache: function (){
			for(var locale in this.localeCache ) {
				this.localeCache [ locale ] = null;
				delete this.localeCache [locale];
			}
		},		
		/**
		 * Creates the bundle object. Also caches it for later use
		 * @private
		 * @param locale String The locale name where it should search the bundle
		 * @param bundlename String The bundle name for the returned bundle
		 * @return ResourceBundle
		 */
		createBundle: function(locale, bundleName){
			var localeCache = this.getLocaleCache(locale);
			var file = this.bundlePath.resolvePath(locale).resolvePath(bundleName+".properties");
			var resourceBundle = new ResourceBundle(file);
			localeCache.bundles[bundleName] = resourceBundle;
			if(!resourceBundle.found){
				setTimeout(function(obj){
					obj.dispatchEvent(new LocalizerEvent(Localizer.BUNDLE_NOT_FOUND, {
						bundleName: bundleName, 
						locale: locale
					}));
				}, 0, this);
			}
			return resourceBundle;
		},
		
		/**
		 * Loads a bundle and returns it. 
		 * @private              		
		 * @param locale String The locale name where it should search the bundle
		 * @param bundleName String The bundle name to be loaded
		 * @param useCache Bool Setting this to false will force the function to reread the bundle. Default is true
		 * @return ResourceBundle
		 */
		loadBundle: function(locale, bundleName, useCache){
			var localeCache = this.getLocaleCache(locale);
			useCache=(typeof useCache=='undefined')?true:useCache;
			return (useCache&&localeCache.bundles[bundleName]) || this.createBundle(locale, bundleName);
		},
		
		/**
		 * Creates a new ResourceFile based on the fileName
		 * @private
		 * @param locale String The locale name where it should search the file
		 * @param fileName String Filename that should be loaded
		 * @return ResourceFile
		 */
		createFile: function(locale, fileName){
			var localeCache = this.getLocaleCache(locale);
			var file = this.bundlePath.resolvePath(locale).resolvePath(fileName);
			var resourceFile = new ResourceFile(file);
			localeCache.files[fileName] = resourceFile;
			if(!resourceFile.found){
				setTimeout(function(obj){
					obj.dispatchEvent(new LocalizerEvent(Localizer.FILE_NOT_FOUND, {
						fileName: fileName, 
						locale: locale
					}));
				}, 0, this);
			}
			return resourceFile;
		},
		
		/**
		 * Loads a file and returns it. 
		 * @private
		 * @param locale String The locale name where it should search the file
		 * @param bundleName String The file name to be loaded
		 * @param useCache Bool Setting this to false will force the function to reread the file. Default is true
		 * @return ResourceFile
		 */
		loadFile: function(locale, fileName, useCache){
			var localeCache = this.getLocaleCache(locale);
			useCache=(typeof useCache=='undefined')?true:useCache;
			return (useCache&&localeCache.files[fileName]) || this.createFile(locale, fileName);
		},
		
		/**
		 * Automatically discovers the available locales using the listing directory of the bundle path, 
		 * returns an array of locales
		 * @private
		 * @return StringArray
		 */
		discoverAndReturnAvailableLocales: function (){
			var folder = this.bundlePath;
			// check if the file exists and is a directory
			if(!(folder.exists&&folder.isDirectory)){
				// just die sillently, because a higher level should take care of that
				return [];
			}
			   
			var localeChain = []
			var localeFolders = folder.getDirectoryListing();
			for(var i=0, l=localeFolders.length; i<l; i++){
				var folder = localeFolders[i];
				// checking if we got a real folder
				if(folder.isDirectory){
					localeChain.push(folder.name);
				}
			}
			return localeChain;
		},
		
		
		
		/**
		 * Same as sortLocalesUsingPreferences, but this time uses the systems pref
		 * @param locales String[] List of locales to be sorted. 
		 * @param addAll Adds all the locales at the end, even though no locale is in the preferences list. Default is true
		 * @return String[]                                                                                                
		 */
		sortLocalesUsingSystemPreferences: function(locales, addAll){
			return LocaleSorter.sortLocalesUsingPreferences( locales, this.userPreference, Localizer.ultimateFallbackLocale, addAll)
		},
		
		/**
		 * Discovers locale chain the bundle path and sets the current locale chain.
		 * The function will fail silently if the locale chain has been set by the user
		 * @param fireEvent bool If true it will fire change events. Default is true
		 */
		autoDiscoverLocaleChain: function(fireEvent){
			if(this.autoLocaleChain){
				// set the new locale chain
				var oldLocaleChain = this.localeChain;
				this.localeChain = this.sortLocalesUsingSystemPreferences( this.discoverAndReturnAvailableLocales(), true);
				// remove from cache not neeed locales
				this.cleanupCache();
				if(typeof fireEvent=='undefined') fireEvent = true;
				if(fireEvent){
					this.diffChainsAndFireEvent(this.localeChain, oldLocaleChain);
				}
			}
		}, 
		
		/**
		 * Diffs two chains and returns true if they are not equal
		 * @param newChain String[]
		 * @param oldChain String[]
		 * @return String[]
		 */
		diffChains: function(newChain, oldChain){
			if(newChain.length!=oldChain.length) return true;
			for(var i=0, l=newChain.length; i<l; i++){
				if(newChain[i]!=oldChain[i]) 
					return true;
			}
			return false;
		},
		
		/**
		 * Clones a chain list
		 * @param chain String[]
		 * @return String[]
		 */   
		cloneChain: function(chain){
			var result = [];
			for(var i=0, l=chain.length; i<l; i++){
				result.push( chain[i] );
			}
			return result;
		},
		
		/**
		 * diffs two locale chains and fires an event if changed
		 * @param newChain String[]
		 * @param oldChain String[]
		 * @private
		 */
		diffChainsAndFireEvent: function(newChain, oldChain){
			if(this.diffChains(newChain, oldChain)){
				this.dispatchEvent(new LocalizerEvent(Localizer.LOCALE_CHANGE, {
					localeChain: this.cloneChain(newChain)
				}));
			}
		},
		
		/**
		 * Sets the locale chain and disables automatic locale chain detection
		 * @param chain String[] 
		 */
		setLocaleChain: function ( chain ){
			if(chain && chain.hasOwnProperty && chain.hasOwnProperty('length') && chain.length>0 ){
				var oldChain = this.localeChain;
				this.localeChain = this.cloneChain(chain);

				// switch off auto locale chain detection
				this.autoLocaleChain = false;
				
				// send some events 
				this.diffChainsAndFireEvent(this.localeChain, oldChain);
			}
		},
		
		/**
		 * Returns the current locale chain
		 * @return String[]
		 */
		getLocaleChain: function(){
			return this.cloneChain(this.localeChain);
		},
		
		/**
		 * Returns the source string, but replaces the "{i}" string with args[i]
		 * @param source String
		 * @param args String[]
		 * @return String
		 */
		template: function(source, args){
			var parser = /{([^}]*)}/g;
			var a = source.split(parser);
			var result = [];
			var d=0;
			for(var i=0,l=a.length;i<l;i++,d=1-d){
				if(d){  
					if(args.hasOwnProperty(a[i]) && (args[a[i]]!=null) && (typeof args[a[i]]!='undefined')){
						result.push(args[a[i]]);
					}else{
						result.push("{"+a[i]+"}");
					}
				}else{ 
					result.push(a[i]);
				}
			}
			return result.join('');
		},
		
		// typical add/remove dispatch listener object
		
		addEventListener: function(eventName, handler){
			var eventListeners = this.eventListeners;
			if(!eventListeners[eventName]){	
				eventListeners[eventName] = [];
			}else{
				// prevent adding the same listener twice
				this.removeEventListener(eventName, handler);	
			}                                                   
			eventListeners[eventName].push( {
				eventName:eventName, 
				handler:handler
			  });
		},
		
		removeEventListener: function(eventName, handler){
			var handlers = this.eventListeners[eventName];
			if(!handlers) return;
			for(var i=handlers.length-1;i>=0;i--){
				var evh = handlers[i];
				if(evh.eventName==eventName&&evh.handler==handler){
					handlers.splice(i, 1);
				}
			}
		},
		
		dispatchEvent: function(ev){
			var handlers = this.eventListeners[ev.name];
			if(!handlers) return;
			for(var i=handlers.length-1;i>=0;i--){
				handlers[i].handler.call(this.parent, ev);
			}
		},
		  
		
		/**
		 * Loads the bundle and returns the resource value
		 * @public		
		 * @param bundleName String
		 * @param resourceName String
		 * @param locale String
		 * @return String
		 */
		getString: function(bundleName, resourceName, locale){
			var bundle = this.loadBundle(locale, bundleName);
			var result = bundle.get(resourceName);
			if(result==null){
				setTimeout(function(obj){
					obj.dispatchEvent(new LocalizerEvent(Localizer.RESOURCE_NOT_FOUND, {
						bundleName: bundleName,
						resourceName: resourceName, 
						locale: locale
					}));
				}, 0, this);
			}
			return result;
		},

		/**
		 * loads the file and returns the contents
		 * @public		
		 * @param fileName String
		 * @param locale String
		 * @return String
		 */		
		getFile: function(fileName, locale){
			var file = this.loadFile(locale, fileName);
			return file.getContent();
		},
		
		/**
		 * Uses the locale chain to get the first defined value 
		 * @public
		 * @param bundleName String
		 * @param resourcename String
		 * @return String
		 */
		getStringFromChain: function(bundleName, resourceName){
			var result;
			var chain = this.localeChain; 
			if(chain){
				for(var i=0, l=chain.length; i<l; i++){
					result = this.getString( bundleName, resourceName, chain[i] );
					if(result!=null) return result;
				}        
			}
			return null;
		},
		
		/**
		 * Uses the locale chain to get the first defined file 
		 * @public
		 * @param fileName
		 * @return String
		 */
		getFileFromChain: function(fileName){
			var result;
			var chain = this.localeChain;
			if(chain){
				for(var i=0, l=chain.length; i<l; i++){
					result = this.getFile( fileName, chain[i] );
					if(result!=null) return result;
				}        
			}
			return null;
		},
		
		/**
		 * Updates the dom
		 * @param domElement DOMElement Optional, default is document
		 * @public
		 */
		update: function(domElement){
			this.domWalker.run( domElement || document );
		}
	};
	
	
	// Localizer constructor, this should be callable by this script only. Use the canCreate trick in order
	// 	to throw an error when it is not set to true. 
	// Also note that the canCreateLocalizer is visible just in this script
	var canCreateLocalizer = false;
	function Localizer(){ 
		// Throw an error when the users wants to create the Localizer using the new keyword. This is a 
		// singleton and there's only one creator for it (air.Localizer.localizer getter)
		if(!canCreateLocalizer){
			throw new Error("Cannot create an air.Localizer instance using the 'new' keyword. Use air.Localizer.localizer instead.");
		}
		this._private = new LocalizerPrivate( this );
		this._private.autoDiscoverLocaleChain(
			   false // do not fire events while loading
			);
	}
	
	Localizer.prototype = {
		
		// * Sets the path to the localization files
		// 
		// * Default Bundle path is “app:/locale/”;
		// 
		// * NOTE: If setLocaleChain hasn’t been called, the directory listing of the bundle path is used to 
		// 		automatically figure out what locales are supported by the application and than call 
		// 		“sortLanguagesByPreference” in order to sort them using the Preferences defined by the user 
		// 		in the “Capabilities.languages” array; If it fails to list the directory it will throw 
		// 		“air.Localizer.BundlePathNotFoundError” exception.
		// 
		// * NOTE: In order to have automatically locales detection the path must point to an existing 
		//		directory, that can be listed using runtime’s File API;

		setBundlesDirectory: function setBundlesDirectory(/* String */ path){
			var file;
			try{
				file = air.File(path);
			}catch(e){
				//coercion failed. this must be a string
				file = air.File.applicationDirectory.resolvePath(path);
				if(!file.exists){ // treating cases like "app:/"
					try{
						file = new air.File(path);
					}catch(e) { ; }
				}
			}
			// Checking that the path points to an existing folder
			if(!file||!(file.exists&&file.isDirectory)){
				throw new Localizer.BundlePathNotFoundError(path);
			}                                 
			
			this._private.clearCache();
			
			this._private.bundlePath = file ;
			// autodiscover will not change the locale chain if the developer already called setLocaleChain
			this._private.autoDiscoverLocaleChain( 
					true // it should fire an event when the chain is changed
				);
		},
		
		// * Walks all the elements in the DOM tree under the specified “parentNode” and applies the localization process:
		// 		* finds all the attributes prefixed with the current prefix (”local_” by default)
		// 		* recreates the attributes by removing the prefix and setting the value to the 
		// 			value defined by the specified key in the resource bundle
		// 		* in one particular case “{prefix}innerHTML” the innerHTML will be changed to the value defined by the key;
		// * Uses the locale chain returned by “getLocaleChain”.
		// * Note: When the “parentNode” is missing the window.document node is used instead.
		// * The key should be in the following format: “{bundleName}.{resourceName}”.
		// 
		// 	E.g. this element:
		// 	<a local_href="default.urlLogin" local_innerHTML="default.cmdLogin"></a> 
		// 	becomes
		// 	<a local_href="default.urlLogin" local_innerHTML="default.cmdLogin" href="app:/login.en.html">Click here to login</a>
			
		update: function update(/* DOMElement, optional */ parentNode /* = document */){
			this._private.update(parentNode);
		},
		
		// * If “locale” argument is provided it is used to return the value of the localization resource 
		// 		called “resourceName” located in the bundle called “bundleName” only for the locale “locale”. 
		// 		Otherwise, the locale chain returned by “getLocaleChain” is used to lookup the first locale 
		// 		that that provides the “resourceName”. For example if the locale chain is [fr_CA, fr] and a 
		// 		particular resource is not found in “fr_CA”, the framework will also search that resource in the “fr”.
		// 		
		// * It will automatically load the bundles if needed.
		// 
		// * Returns null if the resource is not found and fires one of the following events:
		// 		* RESOURCE_NOT_FOUND when the bundle does not contain the specified “resourceName”;
		// 		* BUNDLE_NOT_FOUND when the bundle file is not found.
		// 
		// * If “templateArgs” is provided the function will use it to replace bracketed numbers in the resource 
		// 		with the correspondent values from the “templateArgs” array (only where applicable, meaning 
		// 		that if templateArgs[n] is not defined, “{n}” will not be changed):  	
		// 			* “{0}”, “{1}”, .... “{n}” will be replaced with templateArgs[0], templateArgs[1] ... templateArgs[n].
		// 			* in order to skip replacement for one number, just set that “templateArgs” item to undefined or null;
		
		getString: function getString(/* String */ bundleName, /*String */ resourceName, /* optional String[] */ templateArgs, /* optional, String */ locale ) /*: String*/{
			var result = null;
			if(locale){
				result = this._private.getString(bundleName, resourceName, locale);
			}else{
				result = this._private.getStringFromChain(bundleName, resourceName);
			}
			if(templateArgs&&result!=null){
				result = this._private.template(result, templateArgs);
			}
			return result;
		},
		

		// * If “locale” argument is provided, it returns the contents of the file 
		// 		“{bundlesDirectory}/{locale}/{resourceFileName}”. Otherwise, the locale 
		// 		chain returned by “getLocaleChain” is used to lookup the first locale that 
		// 		that provides the “resourceFileName” file. For example if the locale chain is 
		// 		[fr_FR, fr] the file will be searched using that order: first will search 
		// 		“{bundlesDirectory}/fr_FR/{resourceFileName}” and only if it is not found the 
		// 		framework will continue to search for “{bundlesDirectory}/fr/{resourceFileName}”. 
		// 		
		// * {bundlesPath} is the current bundle path set using “setBundlesDirectory”;
		// 
		// * Returns null if the resource file is not found and fires:
		// 		* FILE_NOT_FOUND event;
		// 
		// * If “templateArgs” is provided the function will use it to replace bracketed numbers in the 
		// 		resource file with the correspondent values from the “templateArgs” array (only where applicable, 
		// 		meaning that if templateArgs[n] is not defined, “{n}” will not be changed):
		// 			* “{0}”, “{1}”, .... “{n}” will be replaced with templateArgs[0], templateArgs[1] ... templateArgs[n].
		// 			* in order to skip replacement for one number, just set that “templateArgs” item to undefined;        
		
		getFile: function getFile(/* String */ resourceFileName, /* optional String[] */ templateArgs, /* String */ locale ) /*: String*/ {
			var result = null;
			if(locale){
				result = this._private.getFile(resourceFileName, locale);
			}else{
				result = this._private.getFileFromChain(resourceFileName);
			}
			if(templateArgs&&result!=null){
				result = this._private.template(result, templateArgs);
			}
			return result;
		},
		
		// * Sets the locale chain and updates the current locale used by all other functions.
		// 
		// * Note: When “chain” argument is missing, is not an array or has zero length the function fails 
		// 		and throws “air.Localizer.IllegalArgumentsError” exception
		// 
		// * Fires LOCALE_CHANGE event, the developer should update the DOM using update function. This event 
		// 		is fired synchronous whenever the locale chain has changed.
		setLocaleChain: function setLocaleChain(/* optional, String[] */ chain){
			if(!(chain&&chain.hasOwnProperty&&chain.hasOwnProperty('length')&&chain.length>0)){
				throw new Localizer.IllegalArgumentsError("Locale chain should be an array.");
			}
			this._private.setLocaleChain(chain);
		},   
		
		// * Returns the locale chain set by the previous call to “setLocaleChain”.
		// 
		// * NOTE: If “setLocaleChain” hasn’t been called the function returns the automatically 
		// 		detected locale chain computed when the Localizer is instantiated (see also the property called “localizer”);
		
		getLocaleChain: function getLocaleChain()/* :String[] */ {
			return this._private.getLocaleChain();
		}, 
		 
		// * Sets the prefix for local attributes used in the “update” function.
		// 
		// * Default prefix is “local_”.
		
		setLocalAttributePrefix: function setLocalAttributePrefix(value){
			this._private.attributePrefix = value+'';
		},
		
		// typical add/remove Event Dispatcher

        addEventListener: function addEventListener(/* String */ eventName, /* callback function */ callback){
			this._private.addEventListener(eventName, callback);
		},                                    
		removeEventListener: function removeEventListener(/* String */ eventName, /* callback function */ callback){
			this._private.removeEventListener(eventName, callback);
		}
		
	};
	
	
	// 	Define the getter function. This will temporararly set canCreateLocalizer to true 
	// 	and create a new Localizer. It writes the newly created localizer to the localizerInstance variable;
	
	var localizerInstance = null;
	Localizer.__defineGetter__("localizer", function(){  
		if(!localizerInstance){
			canCreateLocalizer = true;
			localizerInstance = new Localizer();
			canCreateLocalizer = false;
		}
		return localizerInstance;
	});
	                         
	
	// EVENTS
	
	// Fired by “setLocaleChain” when the current locale is changed (synchronous)
	// Event object strcuture is : { localeChain /* : String [] */ }
	Localizer.LOCALE_CHANGE = "change";
	
	// Fired by “getString” and “update” functions when a resource is not found in the specified bundle (fired asynchronous)
	// Event object structure is : { resourceName /* : String */, bundleName /* : String */ }
	Localizer.RESOURCE_NOT_FOUND = "resourceNotFound";
	
	// Fired by “getFile” when a resource file is not found (fired asynchronous)
	// Event object structure is : { resourceFileName /* : String */ }
	Localizer.FILE_NOT_FOUND = "fileNotFound";
	
	// Fired by “getString” and “update” functions when a bundle file is not found (fired asynchronous)
	// Event object structure is : { bundleName /* : String */ }
	Localizer.BUNDLE_NOT_FOUND = "bundleNotFound";
	         
	// Version number
	Localizer.version = version;
	
	(function(){
		
		// ERROR
		function BundlePathNotFoundError(path){
			this.name = 'air.Localizer.BundlePathNotFoundError';
		   	this.message = "Bundle directory not found "+path;
		}
		BundlePathNotFoundError.prototype = new Error;        
		Localizer.BundlePathNotFoundError = BundlePathNotFoundError;
	                                                        
	
		function IllegalArgumentsError(msg){ 
			this.name = 'air.Localizer.IllegalArgumentsError';
			this.message = msg; 
		}
		IllegalArgumentsError.prototype = new Error;
		Localizer.IllegalArgumentsError = IllegalArgumentsError;

	}());
	
	Localizer.LocaleId = LocaleId;
	
	Localizer.ultimateFallbackLocale = 'en';
	
	
	// * This function is for internal use only.
	// 		
	// 		* Sort a languages array using the order given by the system capabilities languages array.
	// 		
	// 		* Setting removeUnsupported to true removes system unsupported languages. When removeUnsupported is false it makes the system unsupported languages be the last ones preserving order from original array.
	// 		
	// 		* Default value for removeUnsupported is true.
	// 		
	// 		*NOTE: It uses the same approach that the ADOBE AIR application Installer uses:
	// 				* it tries to find the perfect match for locale name;
	// 				* otherwise it fallbacks to finding locales with same parents (eg. “en_US” will fallback to “en” )
	// 				* user preference will be promoted;
	// 		
	// 		* (system supported means they are in the Capabilities.languages array from the runtime)
	// 		
	// 		* eg: If Capabilities.languages = [ “fr_CA”, “en_UK”, “ja” ] and the “languages” argument 
	// 				is [ “en”, “fr_FR”, “zn_ZN” ] the returning array will be [ “fr_FR”, “en” ]
	// 				
	// 		* eg2: When the “languages” argument is [ fr, fr_CA, fr_FR, ro, en, en_US ]:
	// 				* and the Capabilities.languages = [ fr_CA ] the result will be [ fr_CA, fr, fr_FR ]
	// 				* and the Capabilities.languages = [ fr_CA, en ] the result will be [ fr_CA, fr, fr_FR, en, en_US ]

	Localizer.sortLanguagesByPreference = function sortLanguagesByPreference( /* String[] */ appLocales,  /* String[] */ systemPreferences,  /* String, optional */ ultimateFallbackLocale /* = null*/,  /*Boolean, optional */ keepAllLocales /* = true */) /* : String[] */{
		if(!( appLocales && appLocales.hasOwnProperty('length') && systemPreferences&&systemPreferences.hasOwnProperty('length') )){
			throw new Localizer.IllegalArgumentsError("Expected at least two arguments: appLocales and systemPreferences.");
		}
		return LocaleSorter.sortLocalesUsingPreferences( appLocales, systemPreferences, ultimateFallbackLocale, keepAllLocales)		
	};
	
	
	
	if(!window.air){
		window.air = {};
	}
	
	window.air.Localizer = Localizer;
	
}());