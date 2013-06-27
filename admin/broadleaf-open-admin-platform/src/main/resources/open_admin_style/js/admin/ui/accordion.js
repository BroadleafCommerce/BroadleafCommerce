;(function($, window, undefined) {
    'use strict';

    $.fn.broadleafAccordion = function(options) {

        var initNav = function() {
            $.each($('.blc-accordion li .content li'), function(key, value) {
                var module = $(value).parents('.blc-module');

                if ($(value).hasClass('active')) {
                    module.addClass('active');
                }
            });

            $.each($('.blc-module'), function(key, value) {
                if ($(value).hasClass('active')) {
                    $(value).children('.content').show();
                } else {
                    $(value).children('.content').hide();
                }
            });
        };

        // DRY up the logic used to determine if the event logic should execute.
        var hasHover = function(accordion) {
            return accordion.hasClass('hover') && !Modernizr.touch
        };

        $(document).on('click.fndtn', '.blc-accordion li .title', function() {
            var li = $(this).closest('li'), p = li.parent(), content = $(this).next();

            if (!hasHover(p)) {
                li.toggleClass('active');
                content.toggle();
            }
        });

        initNav();
    };

})(jQuery, this);
