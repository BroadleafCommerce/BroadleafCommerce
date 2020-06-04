/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
        // If $data is JSON & ajaxredirect value is valid
        if ($data.constructor === {}.constructor && typeof $data.ajaxredirect !== 'undefined'
            && $data.ajaxredirect != null && $data.ajaxredirect !== "") {
            window.location = $data.ajaxredirect;
            return false;
        }

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
    
    function get(options, callback) {
        if (options == null) {
            options = {};
        }
        options.type = 'GET';
        return BLC.ajax(options, callback);
    }

    function post(options, callback) {
        if (options == null) {
            options = {};
        }
        options.type = 'POST';
        return BLC.ajax(options, callback);
    }
    
    function ajax(options, callback) {
        if (options.type == null) {
            options.type = 'GET';
        }

        var baseUrl = window.location.href;
        if (baseUrl.indexOf('isPostAdd') != -1) {
            if (options.url.indexOf('isPostAdd') < 0) {
                if (options.url.indexOf('?') > 0) {
                    options.url += "&";
                } else {
                    options.url += "?";
                }
                options.url += "isPostAdd=true";
            }
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
                if (options.data.indexOf('stateVersionToken') < 0) {
                    var stateVersionToken = getStateVersionToken();
                    if (stateVersionToken != null) {
                        if (options.data.indexOf('=') > 0) {
                            options.data += "&";
                        }

                        options.data += "stateVersionToken=" + stateVersionToken;
                    }
                }
            } else if (typeof options.data == 'object') {
                if (options.data['csrfToken'] == null || options.data['csrfToken'] == '') {
                    var csrfToken = getCsrfToken();
                    if (csrfToken != null) {
                        options.data['csrfToken'] = csrfToken;
                    }
                }
                if (options.data['stateVersionToken'] == null || options.data['stateVersionToken'] == '') {
                    var stateVersionToken = getStateVersionToken();
                    if (stateVersionToken != null) {
                        options.data['stateVersionToken'] = stateVersionToken;
                    }
                }
            } else if (!options.data) {
                options.data = {};
                var csrfToken = getCsrfToken();
                options.data = {};
                if (csrfToken) {
                    options.data['csrfToken'] = csrfToken;
                }
                var stateVersionToken = getStateVersionToken();
                if (stateVersionToken) {
                    options.data['stateVersionToken'] = stateVersionToken;
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
            
            if (options.trackAnalytics != false) {
                trackAjaxAnalytics(this, data);
            }
                        
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
        try {
            if (window.ga && ga.create) {
                ga('send', 'pageview', options.url);
                console.log('Tracked GA pageview: ' + options.url);
                
                if (options.additionalAnalyticsEvents) {
                    for (var i = 0; i < options.additionalAnalyticsEvents.length; i++) {
                        ga('send', options.additionalAnalyticsEvents[i]);
                        console.log('Tracked additional GA event: ' + options.additionalAnalyticsEvents[i]);
                    }
                }
            }
            
            if (typeof ga != 'undefined') {
                var trackers = ga.getAll();
                for (var i = 0; i < trackers.length; i++) {
                    var tracker = trackers[i];
                    console.log('Tracked GA pageview: ' + options.url + ' for tracker: ' + tracker.get('name'));
                    tracker.send('pageview', options.url);
                    
                    if (options.additionalAnalyticsEvents) {
                        for (var i = 0; i < options.additionalAnalyticsEvents.length; i++) {
                            var event = options.additionalAnalyticsEvents[i];
                            tracker.send(event);
                            console.log('Tracked additional GA event: ' + event + ' for tracker: ' + tracker.get('name'));
                        }
                    }
                }
            }
        } catch (err) {
            console.log(err);
        }
    }
        
    function getCsrfToken() {
        var csrfTokenInput = $('input[name="csrfToken"]');
        if (csrfTokenInput.length == 0) {
            return null;
        }
        
        return csrfTokenInput.val();
    }

    function getStateVersionToken() {
        var stateVersionTokenInput = $('input[name="stateVersionToken"]');
        if (stateVersionTokenInput.length == 0) {
            return null;
        }

        return stateVersionTokenInput.val();
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
    }

    /**
     * Add a URL parameter (or changing it if it already exists)
     * This method does the same as {@link #addUrlParam (search, key, val)},
     * but does not update document.location.search to prevent page reload
     * @param {search} string  this is typically document.location.search
     * @param {key}    string  the key to set
     * @param {val}    string  value
     */
    function addUrlQueryParam(search, key, val){
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

        return params;
    }

    /**
     * Gathers all parameters from the current url
     */
    function getUrlParameters() {
        var baseUrl = window.location.href;
        var indexOfQ = baseUrl.indexOf('?');
        var urlParams = null;
        if (indexOfQ >= 0) {
            urlParams = baseUrl.substring(indexOfQ + 1);
            if (urlParams != null && urlParams != '') {
                var params = decodeURI(encodeURI(urlParams.replace(/&/g, "\",\"").replace(/=/g, "\":\"")));
                if (params.includes('|')) {
                    params = params.replace(/\|/g, '%7C');
                }
                return JSON.parse('{"' + params + '"}');
            }
        }
        return {};
    }

    /**
     * Add URL parameters to an existing url
     * @param {url}     string
     * @param {params}    map of parameter keys to values
     */
    function buildUrlWithParams (url, params) {
        if (url.lastIndexOf("?") > -1) {
            url = url + "&" + $.param(params);
        } else {
            if (!$.isEmptyObject(params)) {
                url = url + "?" + $.param(params);
            }
        }

        return url;
    }
    
    addPreAjaxCallbackHandler(function($data) {
        return BLC.redirectIfNecessary($data);
    });
    
    return {
        addPreAjaxCallbackHandler : addPreAjaxCallbackHandler,
        addInternalDataHandler : addInternalDataHandler,
        redirectIfNecessary : redirectIfNecessary,
        getExtraData : getExtraData,
        get : get,
        post : post,
        ajax : ajax,
        getStateVersionToken : getStateVersionToken,
        defaultErrorHandler : defaultErrorHandler,
        serializeObject : serializeObject,
        getUrlParameters : getUrlParameters,
        addUrlParam : addUrlParam,
        addUrlQueryParam : addUrlQueryParam,
        buildUrlWithParams : buildUrlWithParams,
        servletContext : servletContext,
        siteBaseUrl : siteBaseUrl
    }
})($);
