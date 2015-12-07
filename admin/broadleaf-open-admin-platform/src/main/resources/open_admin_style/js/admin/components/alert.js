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
(function($, BLCAdmin) {
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.alert = {
        showAlert : function($container, message, options) {
            options = options || {};
            var alertType = options.alertType || '';
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
            
            if (options.autoClose) {
                setTimeout(function() {
                    $alert.fadeOut();
                }, options.autoClose);
            }
        }
    };

})(jQuery, BLCAdmin);