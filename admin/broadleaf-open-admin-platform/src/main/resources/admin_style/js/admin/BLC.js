/* Utility methods provided by Broadleaf Commerce */
var BLC = (function($) {
    
    var redirectUrlDiv = "blc-redirect-url",
        extraDataDiv   = "blc-extra-data";
    
    function redirectIfNecessary($data) {
        if ($data.attr('id') == redirectUrlDiv) {
            var redirectUrl = $data.text();
            if (redirectUrl != null && redirectUrl !== "") {
                window.location = redirectUrl;
                return true;
            }
        }
        return false;
    }
    
    function getExtraData($data) {
        var extraData = $.parseJSON($data.find('#' + extraDataDiv).text());
        $data.find('#' + extraDataDiv).remove();
        return extraData;
    }
    
    function ajax(options, callback) {
        var defaults = {
            success: function(data) {
                if (!redirectIfNecessary($(data))) {
                    var extraData = getExtraData($(data));
                    callback(data, extraData);
                }
            }
        };
        
        $.extend(options, defaults); 
        $.ajax(options);
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
    
    return {
        redirectIfNecessary : redirectIfNecessary,
        getExtraData : getExtraData,
        ajax : ajax,
        serializeObject : serializeObject,
        addUrlParam : addUrlParam
    }
})($);