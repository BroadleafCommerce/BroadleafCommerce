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

    $.fn.broadleafTabs = function(options) {
        $(document).on('click', '.nav-tabs li a:not(.hidden)', function(e) {
            e.preventDefault();

            // Remove 'selected' outline from tab
            $(this).blur();

            var tabs = $(this).closest('.nav-tabs');
            // Remove 'active' class from all tabs
            $.each(tabs.find('li'), function (key, value) {
                $(value).removeClass('active');
            });

            // Add 'active' class to current tab
            $(this).parent().addClass('active');

            // Remove 'active' class from all tab content
            var entityForms = tabs.parent().parent().find('.entityFormTab');
            $.each(entityForms, function (key, value) {
                $(value).removeClass('active');
            });

            // Add 'active' class to current tab content
            var tab = this.href.substring(this.href.indexOf("#") + 1);
            tab = '.' + tab + 'Tab';
            $(tab).addClass('active');

            // Show or hide tab content based on 'active' class
            $.each(entityForms, function(key, value) {
                if ($(value).hasClass('active')) {
                    $(value).show();
                } else {
                    $(value).hide();
                }
            });

            if (!$(this).hasClass('lookup')) {
                BLCAdmin.initializeFields(BLCAdmin.getActiveTab());
                BLCAdmin.updateFields(BLCAdmin.getActiveTab());
            }

            $(tab).trigger('activated');
        });
    };

})(jQuery, this);
