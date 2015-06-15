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
    
    // Add utility functions for products to the BLCAdmin object
    BLCAdmin.product = {
            
        refreshSkusGrid : function($container, listGridUrl) {
            BLC.ajax({
                url : listGridUrl,
                type : "GET"
            }, function(data) {
                BLCAdmin.listGrid.replaceRelatedListGrid($(data));
            });
        }

    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'button.generate-skus', function() {
        var $container = $(this).closest('div.listgrid-container');
        
        BLC.ajax({
            url : $(this).data('actionurl'),
            type : "GET"
        }, function(data) {
            var alertType = data.error ? 'alert' : '';
            
            BLCAdmin.listGrid.showAlert($container, data.message, {
                alertType: alertType,
                clearOtherAlerts: true
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
