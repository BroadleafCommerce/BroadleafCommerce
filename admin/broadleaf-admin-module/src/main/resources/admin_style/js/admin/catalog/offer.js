/*
 * #%L
 * BroadleafCommerce Admin Module
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
(function($, BLCAdmin) {
    
    // Add utility functions for offers to the BLCAdmin object
    BLCAdmin.offer = {

        initValueFieldStyle : function ($form) {
            var inputGroup = $("<div>", {'class' : 'input-group'});
            var inputGroupIcon = $("<i>", {'class' : 'blc-icon-percent'});
            var inputGroupAddon = $("<span>", {'class' : 'input-group-addon'});
            inputGroupAddon.append(inputGroupIcon);
            $form.find("#fields\\'value\\'\\.value").wrap(inputGroup).before(inputGroupAddon);
        },
    
        addOnChangeTriggers : function($form) {
            $form.find('#field-type').on('change', function() {
                BLCAdmin.offer.initializeOfferTypeField($form);
            });
            
            $form.find('#field-deliveryType').on('change', function() {
                BLCAdmin.offer.initializeDeliveryTypeField($form);
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
            this.initializeDeliveryTypeField($form);
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
                $form.find("#fields\\'value\\'\\.value").siblings().find('i').addClass("blc-icon-percent");
            } else {
                $form.find("#fields\\'value\\'\\.value").siblings().find('i').addClass("fa fa-dollar");
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
            var $itemTargetFieldset = $itemTarget.closest('fieldset');
            
            if (offerTypeValue == "ORDER") {
                $fgCriteria.addClass('hidden');
                $itemTarget.addClass('hidden');
                $itemTargetFieldset.addClass('hidden');
            } else if (offerTypeValue == 'ORDER_ITEM') {
                $fgCriteria.addClass('hidden');
                $itemTarget.removeClass('hidden');
                $itemTargetFieldset.removeClass('hidden');
            } else if (offerTypeValue == 'FULFILLMENT_GROUP') {
                $fgCriteria.removeClass('hidden');
                $itemTarget.addClass('hidden');
                $itemTargetFieldset.addClass('hidden');
            } else {
                $fgCriteria.addClass('hidden');
                $itemTarget.addClass('hidden');
                $itemTargetFieldset.addClass('hidden');
            }

        },
        
        initializeDeliveryTypeField : function($form) {
            var $deliveryType = $form.find('#field-deliveryType');
            var $offerCode = $form.find('#field-offerCode-offerCode');
            
            if ($deliveryType.find('select').val() == "CODE") {
                $offerCode.removeClass('hidden');
            } else {
                $offerCode.addClass('hidden');
            }
        }
        
    };

    BLCAdmin.addExcludedSelectizeSelector(".query-builder-rules-container select");

    BLCAdmin.addInitializationHandler(function($container) {
        var $form = $container.closest('form.offer-form');
        BLCAdmin.offer.addOnChangeTriggers($form);
        BLCAdmin.offer.initializeOfferFormFields($form);
        BLCAdmin.offer.initValueFieldStyle($form);
    });
    
})(jQuery, BLCAdmin);
