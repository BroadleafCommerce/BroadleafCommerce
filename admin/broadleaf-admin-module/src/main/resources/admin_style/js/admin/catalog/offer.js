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
    
    // Add utility functions for offers to the BLCAdmin object
    BLCAdmin.offer = {

        initValueFieldStyle : function ($form) {
            if (!$form.find("#fields\\'value\\'\\.value").parent().hasClass('input-group')) {
                var inputGroup = $("<div>", {'class' : 'input-group'});
                var inputGroupIcon = $("<i>", {'class' : BLCAdmin.messages.percentIconClass});
                var inputGroupAddon = $("<span>", {'class' : 'input-group-addon'});
                inputGroupAddon.append(inputGroupIcon);
                $form.find("#fields\\'value\\'\\.value").wrap(inputGroup).before(inputGroupAddon);

                // Fix wrapper size for sandbox tags
                var valueField = $form.find("#fields\\'value\\'\\.value");
                var changesWrapper = valueField.closest('.changes');
                var width = inputGroupAddon.outerWidth();
                width += parseInt(changesWrapper.outerWidth()) + 5; // 5px is needed for border offset
                changesWrapper.attr('style', 'width: ' + width + 'px !important');
            }
        },
    
        addOnChangeTriggers : function($form) {
            $form.find('#field-type').on('change', function() {
                BLCAdmin.offer.initializeOfferTypeField($form);
            });

            $form.find('#field-discountType').on('change', function() {
                BLCAdmin.offer.initializeDiscountTypeField($form);
            });
        },
        
        /**
         * Show / hide certain fields on the offer screen based on currently selected values
         */
        initializeOfferFormFields : function($form) {
            this.initializeOfferTypeField($form);
            this.initializeDiscountTypeField($form);
        },

        initializeDiscountTypeField : function($form) {
            var $offerDiscountType = $form.find('#field-discountType');
            var offerDiscountType;
            if ($offerDiscountType.find('select').length > 0) {
                offerDiscountType = $offerDiscountType.find('select').val();
            } else {
                offerDiscountType = $offerDiscountType.find('input[type="radio"]:checked').val();
            }

            $form.find("#fields\\'value\\'\\.value").siblings().find('i').removeClass();
            if (offerDiscountType == "PERCENT_OFF") {
                $form.find("#fields\\'value\\'\\.value").siblings().find('i').addClass(BLCAdmin.messages.percentIconClass);
            } else {
                $form.find("#fields\\'value\\'\\.value").siblings().find('i').addClass(BLCAdmin.messages.currencyIconClass);
            }
        },
        
        initializeOfferTypeField : function($form) {

            var $offerType = $form.find('#field-type');
            var offerTypeValue;
            if ($offerType.find('select').length > 0) {
                offerTypeValue = $offerType.find('select').val();
            } else {
                offerTypeValue = $offerType.find('input[type="radio"]:checked').val();
            }

            var $fgCriteria = $form.find('#field-offerMatchRules---FULFILLMENT-GROUP');
            var $itemTarget = $form.find('#field-targetItemCriteria');

            if (offerTypeValue == "ORDER") {
                $fgCriteria.addClass('hidden');
                $itemTarget.addClass('hidden');
            } else if (offerTypeValue == 'ORDER_ITEM') {
                $fgCriteria.addClass('hidden');
                $itemTarget.removeClass('hidden');
            } else if (offerTypeValue == 'FULFILLMENT_GROUP') {
                $fgCriteria.removeClass('hidden');
                $itemTarget.addClass('hidden');
            } else {
                $fgCriteria.addClass('hidden');
                $itemTarget.addClass('hidden');
            }

        }
        
    };

    BLCAdmin.addExcludedSelectizeSelector(".query-builder-rules-container select");

    BLCAdmin.addInitializationHandler(function($container) {
        var $form = $container.closest('form.offer-form');
        BLCAdmin.offer.addOnChangeTriggers($form);
    });

    BLCAdmin.addFieldInitializationHandler(function($container) {
        var $form = $container.closest('form.offer-form');
        BLCAdmin.offer.initializeOfferFormFields($form);
        BLCAdmin.offer.initValueFieldStyle($form);
    });
    
    $.each(['org.broadleafcommerce.core.offer.domain.Offer'], function(idx, clazz) {

        BLCAdmin.addDependentFieldHandler(
            clazz,
            '#field-showAdvancedVisibilityOptions',
            '#field-offerMatchRules---TIME',
            'true'
        );

        BLCAdmin.addDependentFieldHandler(
            clazz,
            '#field-type',
            '#field-applyToSalePrice',
            'ORDER_ITEM'
        );
        
    });

    function hideOrderFields(compareValue) {
        return (compareValue == 'ORDER_ITEM' || compareValue == 'FULFILLMENT_GROUP');
    }

    function hideItemFields(compareValue) {
        return (compareValue == 'ORDER' || compareValue == 'FULFILLMENT_GROUP');
    }

    function hideShippingFields(compareValue) {
        return (compareValue == 'ORDER_ITEM' || compareValue == 'ORDER');
    }
})(jQuery, BLCAdmin);
