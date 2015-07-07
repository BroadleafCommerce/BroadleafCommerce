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
