/* Utility methods provided by Broadleaf Commerce */
var BLC = (function($) {
    
    var redirectUrlDiv = "blc-redirect-url",
        extraDataDiv   = "blc-extra-data",
        preAjaxCallbackHandlers = [];
    
    function addPreAjaxCallbackHandler(fn) {
        preAjaxCallbackHandlers.push(fn);
    }
    
    /**
     * Runs all currently registered pre-ajax-callback handlers. If any such handler returns false,
     * we will stop invocation of additional handlers as well as the callback function.
     */
    function runPreAjaxCallbackHandlers($data) {
        for (var i = 0; i < preAjaxCallbackHandlers.length; i++) {
            if (!preAjaxCallbackHandlers[i]($data)) {
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
    
    function getExtraData($data) {
        if (!($data instanceof jQuery)) {
            return null;
        }
        
    	var extraData;
    	var $extraDataDiv = $data.find('#' + extraDataDiv);
    	if ($extraDataDiv.length > 0) {
	        extraData = $.parseJSON($extraDataDiv.text());
	        $extraDataDiv.remove();
    	}
	    return extraData;
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
            }
        }
        
        options.success = function(data) {
            if (typeof data == "string") {
                data = $($.trim(data));
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
            params = search.replace(new RegExp('[\?]' + key + '[^&]*'), newParam);

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
        redirectIfNecessary : redirectIfNecessary,
        getExtraData : getExtraData,
        ajax : ajax,
        defaultErrorHandler : defaultErrorHandler,
        serializeObject : serializeObject,
        addUrlParam : addUrlParam
    }
})($);
