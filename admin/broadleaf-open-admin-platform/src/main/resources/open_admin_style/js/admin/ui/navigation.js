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
