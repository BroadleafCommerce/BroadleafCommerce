/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/* Utility methods provided by Broadleaf Commerce */
var BLC = (function($) {
    
    var redirectUrlDiv = "blc-redirect-url",
        extraDataDiv   = "blc-extra-data",
        internalDataDiv   = "blc-internal-data",
        preAjaxCallbackHandlers = [],
        internalDataHandlers = [],
        servletContext = "//BLC-SERVLET-CONTEXT",
        siteBaseUrl = "//BLC-SITE-BASEURL";
    
    function addPreAjaxCallbackHandler(fn) {
        preAjaxCallbackHandlers.push(fn);
    }

    function addInternalDataHandler(fn) {
        internalDataHandlers.push(fn);
    }
    
    /**
     * Runs all currently registered pre-ajax-callback handlers. If any such handler returns false,
     * we will stop invocation of additional handlers as well as the callback function.
     */
    function runPreAjaxCallbackHandlers($data) {
        return runGenericHandlers($data, preAjaxCallbackHandlers);
    }

    /**
     * Runs all currently registered internal data handlers. If any such handler returns false,
     * we will stop invocation of additional handlers as well as the callback function.
     */
    function runInternalDataHandlers($data) {
        return runGenericHandlers($data, internalDataHandlers);
    }
    
    function runGenericHandlers($data, handlers) {
        for (var i = 0; i < handlers.length; i++) {
            if (!handlers[i]($data)) {
                return false;
            }
        }
        return true;
    }
    
    function redirectIfNecessary($data) {
        if (!($data instanceof jQuery)) {
            return true;
        }
        
        if ($data.attr('id') == redirectUrlDiv) {
            var redirectUrl = $data.text();
            if (redirectUrl != null && redirectUrl !== "") {
                window.location = redirectUrl;
                return false;
            }
        }
        return true;
    }
    
    function getInternalData($data) {
        var extractedData = extractData($data, internalDataDiv);

        if (($data instanceof jQuery) && ($data.attr('id') == internalDataDiv + "-container")) {
            $data.unwrap();
        }

        return extractedData;
    }

    function getExtraData($data) {
        return extractData($data, extraDataDiv);
    }
    
    function extractData($data, dataDivId) {
        if (!($data instanceof jQuery)) {
            return null;
        }
        
    	var extractedData = null;

	    var $dataDiv = $data.find('#' + dataDivId);
    	if ($dataDiv.length > 0) {
    	    try {
    	        extractedData = $.parseJSON($dataDiv.text());
    	    } catch (e) {
    	        console.log("Could not parse data as JSON: " + $dataDiv.text());
    	    }
	        $dataDiv.remove();
    	}

	    return extractedData;
    }
    
    function ajax(options, callback) {
        if (options.type == null) {
            options.type = 'GET';
        }
        
        if (options.type.toUpperCase() == 'POST') {
            if (typeof options.data == 'string') {
                if (options.data.indexOf('csrfToken') < 0) {
                    var csrfToken = getCsrfToken();
                    if (csrfToken != null) {
                        if (options.data.indexOf('=') > 0) {
                            options.data += "&";
                        }
                        
                        options.data += "csrfToken=" + csrfToken;
                    }
                }
            } else if (typeof options.data == 'object') {
                if (options.data['csrfToken'] == null || options.data['csrfToken'] == '') {
                    var csrfToken = getCsrfToken();
                    if (csrfToken != null) {
                        options.data['csrfToken'] = csrfToken;
                    }
                }
            } else if (!options.data) {
                var csrfToken = getCsrfToken();
                if (csrfToken) {
                    options.data = { 'csrfToken': csrfToken }
                }
            }
        }
        
        options.success = function(data) {
            if (typeof data == "string" && !this.noParse) {
                data = $($.trim(data));
            }
            
            var internalData = getInternalData(data);
            if (internalData != null) {
                runInternalDataHandlers(internalData);
            }
            
            trackAjaxAnalytics(this, data);
            
            if (runPreAjaxCallbackHandlers(data)) {
                var extraData = getExtraData(data);
                callback(data, extraData);
            }
        };
        
        if (!options.error) {
            options.error = function(data) {
                BLC.defaultErrorHandler(data);
            };
        }
        
        return $.ajax(options);
    }
    
    function trackAjaxAnalytics(options, data) {
        if (typeof _gaq == 'undefined') {
            return;
        }

        _gaq.push(['_trackPageview', options.url]);
        console.log('Tracked GA pageview: ' + options.url);
        
        if (options.additionalAnalyticsEvents) {
            for (var i = 0; i < options.additionalAnalyticsEvents.length; i++) {
                _gaq.push(options.additionalAnalyticsEvents[i]);
                console.log('Tracked additional GA event: ' + options.additionalAnalyticsEvents[i]);
            }
        }
    }
        
    function getCsrfToken() {
        var csrfTokenInput = $('input[name="csrfToken"]');
        if (csrfTokenInput.length == 0) {
            return null;
        }
        
        return csrfTokenInput.val();
    }
    
    function defaultErrorHandler(data) {
        if (data.getAllResponseHeaders()) {
            alert("An error occurred while processing your request.");
        }
    }
    
    function serializeObject($object) {
        var o = {};
        var a = $object.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }

    /**
     * Add a URL parameter (or changing it if it already exists)
     * @param {search} string  this is typically document.location.search
     * @param {key}    string  the key to set
     * @param {val}    string  value
     */
    function addUrlParam(search, key, val){
        var newParam = key + '=' + val,
            params = '?' + newParam;

        // If the "search" string exists, then build params from it
        if (search) {
            // Try to replace an existing instance
            params = search.replace(new RegExp('[\?]' + key + '[^&]*'), '?' + newParam);

            // If nothing was replaced, then check if it exists as a trailing param
            if (params === search) {
                params = search.replace(new RegExp('[\&]' + key + '[^&]*'), '&' + newParam);

                // If nothing was replaced and the key is not already present, then add the new param to the end
                if ((params === search) && (search.indexOf(val) == -1) ) {
                    params += '&' + newParam;
                }
            }
        }

        return document.location.search = params;
    };
    
    addPreAjaxCallbackHandler(function($data) {
        return BLC.redirectIfNecessary($data);
    });
    
    return {
        addPreAjaxCallbackHandler : addPreAjaxCallbackHandler,
        addInternalDataHandler : addInternalDataHandler,
        redirectIfNecessary : redirectIfNecessary,
        getExtraData : getExtraData,
        ajax : ajax,
        defaultErrorHandler : defaultErrorHandler,
        serializeObject : serializeObject,
        addUrlParam : addUrlParam,
        servletContext : servletContext,
        siteBaseUrl : siteBaseUrl
    }
})($);
