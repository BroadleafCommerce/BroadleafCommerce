/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
(function($, BLCAdmin) {
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.history = {
            
        pushUrl : function(url, state) {
            
        },
            
        replaceUrl : function(url, state) {
            // Assuming the user is on a browser from the 21st century, update the url
            if (!!(window.history && history.pushState)) {
                history.replaceState(state, '', url);
            }
        },
        
        /**
         * Returns the URL with the given request parameter (param) replaced/added with the given value. Note that
         * both 'param' and 'value' can also be arrays, in which case it is assumed that the arrays are of equal length and
         * matching indices within each array represent a key/value pair
         */
        getUrlWithParameter : function(param, value, state, baseUrl) {
            if (baseUrl == null) {
                baseUrl = window.location.href;
            }
            
            var indexOfQ = baseUrl.indexOf('?'); 
            var urlParams = null;
            if (indexOfQ >= 0) {
                urlParams = baseUrl.substring(indexOfQ + 1);
                baseUrl = baseUrl.substring(0, indexOfQ);
            }
            
            // Parse the current url parameters into an object
            var paramObj = {};
            if (urlParams != null && urlParams != '') {
                // Remove trailing '&'
                if (urlParams.endsWith('&')) {
                    urlParams = urlParams.substring(0, urlParams.length - 1);
                }
                var params = decodeURI(encodeURI(urlParams.replace(/&/g, "\",\"").replace(/=/g,"\":\"")));
                if (params.includes('|')) {
                    params = params.replace(/\|/g, '%7C');
                }
                paramObj = JSON.parse('{"' + params + '"}');
            }
            
            if (value == null || value === "") {
                delete paramObj[param];
            } else {
                // Update the desired parameter to its new value
                if ($.isArray(param)) {
                    $(param).each(function(index, param) {
                        paramObj[param[index]] = value[index];
                    });
                } else {
                    paramObj[param] = value;

                    if (String(value).indexOf('#tab') !== -1) {
                        // Ensure that the parameter doesn't include the tab hash
                        paramObj[param] = String(value).substring(0, String(value).indexOf('#tab'));
                    }
                }
            }
            
            // Reassemble the new url
            var newUrl = baseUrl + '?';
            for (i in paramObj) {
                if (paramObj[i] != null) {
                    newUrl += i + '=' + paramObj[i] + '&';
                }
            }
            newUrl = newUrl.substring(0, newUrl.length-1);
            
            return newUrl;
        },
        
        getUrlParameters : function() {
            return BLC.getUrlParameters();
        },
        
        replaceUrlParameter : function(param, value, state) {
            var newUrl = this.getUrlWithParameter(param, value, state);
            this.replaceUrl(newUrl, state);
        },
    
        popState : function(url, state, event) {
            //console.log('popping state ' + url + ' ' + state + ' ' + event);
                    
            /*
            if (state) {
                window.location = url;
            }
            */
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    // If we have HTML5 history, bind the popstate event
    if (!!(window.history && history.pushState)) {
        window.onpopstate = function(event) {
            BLCAdmin.history.popState(document.location.href, event.state, event);
        }
    }
    
});

