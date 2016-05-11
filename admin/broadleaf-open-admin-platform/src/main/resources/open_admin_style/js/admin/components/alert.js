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
(function($, BLCAdmin) {
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.alert = {
        showAlert : function($container, message, options) {
            options = options || {};
            var alertType = options.alertType || '';
            var autoClose = options.autoClose || 3000;
            var $fieldGroup = $container.closest('.field-group');
            
            var $alert = $('<span>').addClass('alert-box').addClass(alertType);
            
            $alert.append(message);
            
            if (options.clearOtherAlerts) {
                if ($fieldGroup.length) {
                    $fieldGroup.find('.alert-wrapper').find('.alert-box').remove();
                } else {
                    $container.find('.alert-box').remove();
                }
            }

            if ($fieldGroup.length) {
                $fieldGroup.find('.alert-wrapper').append($alert);
            } else {
                $container.append($alert);
            }

            setTimeout(function() {
                $alert.fadeOut();
            }, autoClose);
        }
    };

    // Fade out any header flashes that are shown after a redirect (i.e. "Successfully deleted" notification)
    setTimeout(function() {
        $('#headerFlashAlertBoxContainer').find('.alert-box').fadeOut();
        BLCAdmin.history.replaceUrlParameter('headerFlash');
    }, 3000);

})(jQuery, BLCAdmin);
