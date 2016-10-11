/*
 * #%L
 * BroadleafCommerce Admin Module
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
    
    // Add utility functions for products to the BLCAdmin object
    BLCAdmin.product = {
            
        refreshSkusGrid : function($container, listGridUrl) {
            BLC.ajax({
                url : listGridUrl,
                type : "GET"
            }, function(data) {
                BLCAdmin.listGrid.replaceRelatedCollection($(data));
            });
        }

    };

    $.each(['org.broadleafcommerce.core.catalog.domain.Product'], function(idx, clazz) {

        BLCAdmin.addDependentFieldHandler(
            clazz,
            '#field-hasPromotionMessageOverrides',
            '.listgrid-container#promotionMessageOverrides',
            'true'
        );
    });
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'button.generate-skus', function() {
        var $container = $(this).closest('div.listgrid-container');
        
        BLC.ajax({
            url : $(this).data('actionurl'),
            type : "GET"
        }, function(data) {
            var alertType = data.error ? 'error-alert' : data.skusGenerated > 0 ? 'save-alert' : 'error-alert';
            
            BLCAdmin.listGrid.showAlert($container, data.message, {
                alertType: alertType,
                clearOtherAlerts: true,
                autoClose: 5000
            });
            
            if (data.skusGenerated > 0) {
                BLCAdmin.product.refreshSkusGrid($container, data.listGridUrl);
            }
        });
        
        return false;
    });

    $('body').on('change', "input[name=\"fields['defaultCategory'].value\"]", function(event, fields) {
        var $fieldBox = $(event.target).closest('.field-group');
        var $prefix = $fieldBox.find('input.generated-url-prefix');

        if (!$prefix.length) {
            $prefix = $fieldBox.append($('<input>', {
                'type'  : "hidden",
                'class' : "generated-url-prefix"
            })).find('input.generated-url-prefix');
        }

        $prefix.val(fields['url']);
    });
});
