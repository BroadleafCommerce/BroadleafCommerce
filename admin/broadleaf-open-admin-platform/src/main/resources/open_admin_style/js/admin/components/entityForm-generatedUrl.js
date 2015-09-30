/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
    
    BLCAdmin.generatedUrl = {
            
        registerUrlGenerator : function registerUrlGenerator($generatedUrlContainer) {
            var sourceFieldName = $generatedUrlContainer.data('source-field');
            var $sourceField = $generatedUrlContainer.closest('form').find('#field-' + sourceFieldName + ' input');
            var $targetField = $generatedUrlContainer.find('input.target-field');

            $sourceField.on('keyup', function() {
                BLCAdmin.generatedUrl.setGeneratedUrl($sourceField, $targetField);
            });

            BLCAdmin.generatedUrl.setGeneratedUrl($sourceField, $targetField);
        },
        
        setGeneratedUrl : function setGeneratedUrl($sourceField, $targetField) {
            var generatedPrefix;
            
            if ($targetField.data('prefix-selector')) {
                var $field = $($targetField.data('prefix-selector'));
                generatedPrefix = $field.find('input.generated-url-prefix').val();
                
                $field.on('change', function() {
                    BLCAdmin.generatedUrl.setGeneratedUrl($sourceField, $targetField);
                });
                
                if (generatedPrefix == null || generatedPrefix == "") {
                    generatedPrefix = $targetField.data('prefix');
                }
                generatedPrefix += '/';
            } else if ($targetField.data('prefix') == "NONE") {
                generatedPrefix = "";
            } else if ($targetField.data('prefix')) {
                generatedPrefix = $targetField.data('prefix');
            } else {
                generatedPrefix = '/';
            }

            var convertedUrl = BLCAdmin.generatedUrl.convertToUrlFragment($sourceField.val(), {
                allowSlash : $targetField.data('allow-slash')
            });

            var finalUrl = generatedPrefix + convertedUrl
            $targetField.val(finalUrl);
            
            $targetField.trigger('generated-url', [finalUrl]);
        },
        
        unregisterUrlGenerator : function unregisterUrlGenrator($generatedUrlContainer) {
            var sourceFieldName = $generatedUrlContainer.data('source-field');
            $generatedUrlContainer.closest('form').find('#field-' + sourceFieldName + " input").off('keyup');
        },
        
        convertToUrlFragment : function convertToUrlFragment(val, options) {
            if (options != null && options.allowSlash) {
                return val = val.replace(/ /g, BLC.systemProperty.urlFragmentSeparator).replace(/[^\w\s-_\/]/gi, '').toLowerCase();
            } else {
                return val = val.replace(/ /g, BLC.systemProperty.urlFragmentSeparator).replace(/[^\w\s-_]/gi, '').toLowerCase();
            }
        }

    }

    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('div.generated-url-container').each(function(idx, el) {
            if ($(el).data('overridden-url') != true) {
                BLCAdmin.generatedUrl.registerUrlGenerator($(el));
            }
        });
    });

})(jQuery, BLCAdmin);

$('body').on('click', 'a.override-generated-url', function(event) {
    event.preventDefault();
    var $this = $(this);
    
    var enabled = $this.data('enabled') == true;
	var $container = $this.closest('div.generated-url-container');
	
	if (enabled) {
	    $container.find('input').attr('readonly', 'readonly');
	    $this.text($this.data('disabled-text'));
	    BLCAdmin.generatedUrl.registerUrlGenerator($container);
	} else {
	    $container.find('input').removeAttr('readonly');
	    $this.text($this.data('enabled-text'));
	    BLCAdmin.generatedUrl.unregisterUrlGenerator($container);
	}
	
	$container.closest('form').find('#field-' + $container.data('toggle-field') + ' input').val(!enabled);
	
	$this.data('enabled', !enabled);
});
