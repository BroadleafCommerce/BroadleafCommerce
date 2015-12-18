/**
 * Created by brandon on 11/16/15.
 */
/*
$.fn.dataByPrefix = function( pr ){
    var d=this.data(), r=new RegExp("^"+pr), ob={};
    for(var k in d) if(r.test(k)) ob[k]=d[k];
    return ob;
};

$.fn.reloadSection = function(options) {
    if($(this).length === 0) {
        return false;
    } else if(!$(this).hasClass('ajax-content-placeholder')) {
        console.error("Can't populate content. Element is not an ajax-content-placeholder:");
        console.log($(this));
        return false;
    } else if($(this).data('url') === 'undefined') {
        console.error("Can't populate content. Element does not have a data-url attribute:");
        console.log($(this));
        return false;
    } else {
        var placeholder = this;
        var requestUrl = $(placeholder).data('url');

        var queryParameters = {};
        if(typeof options !== 'undefined' && typeof options['parameters'] !== 'undefined') {
            queryParameters = options['parameters'];
        }

        var dataParams = $(placeholder).dataByPrefix('param');
        for(var param in dataParams) {
            var name = param.substr(5);
            name = name.charAt(0).toLowerCase() + name.slice(1);
            queryParameters[name] = dataParams[param];
        }

        // convert url parameters to string
        var paramsString = '';
        for(var paramName in queryParameters) {
            paramsString += paramName + '=' + queryParameters[paramName] + '&';
        }
        paramsString = paramsString.substr(0, paramsString.length - 1); // remove trailing &
        if(requestUrl.indexOf('?') === -1) {
            paramsString = '?' + paramsString;
        } else if(requestUrl.substr(requestUrl.length - 1, 1) !== '&') {
            paramsString = '&' + paramsString;
        }

        // make request
        $.ajax({
            url: $(placeholder).data('url') + paramsString,
            complete: function(response) {

                // clone sections that shouldn't be changed and then
                // re-apply them after reloading the main section
                var originalStates = $(placeholder).find('.do-not-reload').clone();
                if(originalStates.filter('[data-section-key]').length < originalStates.length) {
                    console.error("Error: 'do-not-reload' element does not have 'data-section-key' attribute");
                    console.log($(this));
                    return false;
                }
                $(placeholder).html(response.responseText);
                var places = $(placeholder).find('.do-not-reload');

                for(var i = 0; i < originalStates.length; i++) {
                    var originalState = $(originalStates[i]);
                    var destination = places.filter('[data-section-key="' + originalState.data('section-key') + '"]')
                    destination.replaceWith(originalState);
                }

                $(placeholder).addClass('populated');

                if(typeof(BLCAdmin) !== 'undefined' && $(placeholder).find('select').length > 0) {
                    BLCAdmin.initializeSelectizeFields($(placeholder));
                }

                // call callback if exists
                if(typeof options !== 'undefined' && typeof options['callback'] !== 'undefined') {
                    options['callback'](placeholder);
                }

                $(placeholder).trigger('content-loaded', response);
            }
        });
    }
};

// initialize ajax content
$('.ajax-content-placeholder').each(function(){$(this).reloadSection()});

$.fn.ajaxSubmit = function(options) {
    var form = $(this);
    $.ajax({
        method: form.attr('method') ? form.attr('method') : 'POST',
        url: form.attr('action') ? form.attr('action') : window.location,
        data: form.serialize(),
        complete: options.callback ? options.callback : undefined
    });
};*/