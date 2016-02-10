/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
                paramObj = JSON.parse('{"'
                    + decodeURI(encodeURI(urlParams.replace(/&/g, "\",\"").replace(/=/g,"\":\""))) + '"}');

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
            var baseUrl = window.location.href;
            var indexOfQ = baseUrl.indexOf('?'); 
            var urlParams = null;
            if (indexOfQ >= 0) {
                urlParams = baseUrl.substring(indexOfQ + 1);
                if (urlParams != null && urlParams != '') {
                    return JSON.parse('{"'
                        + decodeURI(encodeURI(urlParams.replace(/&/g, "\",\"").replace(/=/g,"\":\""))) + '"}');
                }
            }
            return null;
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

