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
        
        replaceUrlParameter : function(param, value, state) {
            // If we have a new value (hack around sometimes incorrect update from mCustomScrollbar)
            if (value != undefined) {
                // Parse the current url parameters into an object
                var urlParams = window.location.search.substring(1);
                var paramObj;
                if (urlParams == "") {
                    paramObj = {};
                } else {
                    paramObj = JSON.parse('{"' + decodeURI(urlParams.replace(/&/g, "\",\"").replace(/=/g,"\":\"")) + '"}');
                }
                
                // Update the desired parameter to its new value
                paramObj[param] = value;
                
                // Reassemble the new url
                var indexOfQ = window.location.href.indexOf('?'); 
                var newUrl = indexOfQ > -1 ? window.location.href.substring(0, indexOfQ) : window.location.href;
                newUrl += '?';
                for (i in paramObj) {
                   newUrl += i + '=' + paramObj[i] + '&';
                }
                newUrl = newUrl.substring(0, newUrl.length-1);
                
                // Actually replace the url in the toolbar
                this.replaceUrl(newUrl, state);
            }
        },
    
        popState : function(url, state, event) {
            console.log('popping state ' + url + ' ' + state + ' ' + event);
                    
            if (state) {
                window.location = url;
            }
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

