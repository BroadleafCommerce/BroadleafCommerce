/**
 * Created by brandon on 11/16/15.
 */
$.fn.dataByPrefix = function( pr ){
    var d=this.data(), r=new RegExp("^"+pr), ob={};
    for(var k in d) if(r.test(k)) ob[k]=d[k];
    return ob;
};

$.fn.reloadSection = function(options) {
    if(!$(this).hasClass('ajax-content-placeholder')) {
        console.error("Can't populate content. Element is not an ajax-content-placeholder:");
        console.log($(this));
    } else if($(this).data('url') === 'undefined') {
        console.error("Can't populate content. Element does not have a data-url attribute:");
        console.log($(this));
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
                if($(placeholder).find('.list-grid-table').length > 0) {
                    $(placeholder).find('tbody').html($(response.responseText).find('tbody').html());
                    $(placeholder).find('.listgrid-table-footer').html($(response.responseText).find('.listgrid-table-footer').html());
                } else {
                    $(placeholder).html(response.responseText);
                }
                $(placeholder).addClass('populated');

                // call callback if exists
                if(typeof options !== 'undefined' && typeof options['callback'] !== 'undefined') {
                    options['callback'](placeholder);
                }
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
};