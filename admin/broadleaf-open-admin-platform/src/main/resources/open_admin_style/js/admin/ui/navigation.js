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

    $.fn.broadleafNavigation = function(options) {

        var initNav = function() {
            $.each($('.secondary-nav .blc-module'), function(key, value) {
                if ($(value).hasClass('active')) {
                    $(value).show();
                } else {
                    $(value).hide();
                }
            });
        };

        $(document).on('click', '.blc-navigation li a', function(e) {

            e.preventDefault();

            if ($(this).hasClass('active')) {
                $('.secondary-nav').hide();
                $(this).removeClass('active');

            } else {

                // Show the secondary nav
                $('.secondary-nav').show();

                // Remove 'active' class from all links on main nav
                $.each($('.blc-navigation li a'), function (key, value) {
                    $(value).removeClass('active');
                });

                // Add 'active' class to current link on main nav
                $(this).addClass('active');

                // Remove 'active' class from all links on secondary nav
                $.each($('.secondary-nav .blc-module'), function (key, value) {
                    $(value).removeClass('active');
                });

                // Add 'active' class to current link on secondary nav
                var content = $('.secondary-nav .blc-module #' + this.id).closest('div');
                content.addClass('active');

                // Show or hide secondary nav based on 'active' class
                $.each($('.secondary-nav .blc-module'), function (key, value) {
                    if ($(value).hasClass('active')) {
                        $(value).show();
                    } else {
                        $(value).hide();
                    }
                });
            }
        });

        initNav();
    };

})(jQuery, this);
