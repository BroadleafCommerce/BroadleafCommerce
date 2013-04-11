/**
 * Custom handlers for frontend catalog functions
 */
$(document).ready(function() {
    
    /* ***************** *
     * PRODUCT FUNCTIONS *
     * ***************** */
    $('body').on('click', 'button.generate-skus', function() {
        var $container = $(this).closest('div.listgrid-container');
        
        $.get($(this).data('actionurl'), function(data) {
            var alertType = data.error ? 'alert' : '';
            
            BLCAdmin.listGrid.showAlert($container, data.message, {
                    alertType: alertType
                }
            );
            
            if (data.skusGenerated > 0) {
                refreshSkusGrid($container, data.listGridUrl);
            }
        });
        
        return false;
    });
    
    function refreshSkusGrid($container, listGridUrl) {
        $.get(listGridUrl, function(data) {
            $container.find('table.list-grid-table').replaceWith($(data.trim()));
            BLCAdmin.listGrid.updateToolbarRowActionButtons($container);
        });
    }
    
    /* *************** *
     * OFFER FUNCTIONS *
     * *************** */
    function addOnChangeTriggers($form) {
        $form.find('#field-type').on('change', function() {
            initializeOfferTypeField($form);
        });
        
        $form.find('#field-deliveryType').on('change', function() {
            initializeDeliveryTypeField($form);
        });
    }
    
    /**
     * Show / hide certain fields on the offer screen based on currently selected values
     */
    function initializeOfferFormFields($form) {
        initializeOfferTypeField($form);
        initializeDeliveryTypeField($form);
    }
    
    function initializeOfferTypeField($form) {
        var $offerType = $form.find('#field-type');
        
        var $fgCriteria = $form.find('#field-offerMatchRules--FULFILLMENT_GROUP');
        var $itemTarget = $form.find('#field-targetItemCriteria');
        var $itemTargetFieldset = $itemTarget.closest('fieldset');
        
        if ($offerType.find('select').val() == "ORDER") {
            $fgCriteria.addClass('hidden');
            $itemTarget.addClass('hidden');
            $itemTargetFieldset.addClass('hidden');
        } else if ($offerType.find('select').val() == 'ORDER_ITEM') {
            $fgCriteria.addClass('hidden');
            $itemTarget.removeClass('hidden');
            $itemTargetFieldset.removeClass('hidden');
        } else if ($offerType.find('select').val() == 'FULFILLMENT_GROUP') {
            $fgCriteria.removeClass('hidden');
            $itemTarget.addClass('hidden');
            $itemTargetFieldset.addClass('hidden');
        } else {
            $fgCriteria.addClass('hidden');
            $itemTarget.addClass('hidden');
            $itemTargetFieldset.addClass('hidden');
        }
    }
    
    function initializeDeliveryTypeField($form) {
        var $deliveryType = $form.find('#field-deliveryType');
        var $offerCode = $form.find('#field-offerCode-offerCode');
        
        if ($deliveryType.find('select').val() == "CODE") {
            $offerCode.removeClass('hidden');
        } else {
            $offerCode.addClass('hidden');
        }
    }
    
    $('form.offer-form').each(function(index, element) {
        var $form = $(element);
        
        addOnChangeTriggers($form);
        initializeOfferFormFields($form);
    });

});
