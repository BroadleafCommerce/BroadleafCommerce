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
            var generatedUrl = $targetField.data('prefix') + BLCAdmin.generatedUrl.convertToUrlFragment($sourceField.val());
            $targetField.val(generatedUrl);
        },
        
        unregisterUrlGenerator : function unregisterUrlGenrator($generatedUrlContainer) {
            var sourceFieldName = $generatedUrlContainer.data('source-field');
            $generatedUrlContainer.closest('form').find('#field-' + sourceFieldName + " input").off('keyup');
        },
        
        convertToUrlFragment : function convertToUrlFragment(val) {
            return val = val.replace(/ /g, BLC.systemProperty.urlFragmentSeparator).replace(/[^\w\s-_]/gi, '').toLowerCase();
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
	    $container.find('input').attr('disabled', 'disabled');
	    $this.text($this.data('disabled-text'));
	    BLCAdmin.generatedUrl.registerUrlGenerator($container);
	} else {
	    $container.find('input').removeAttr('disabled');
	    $this.text($this.data('enabled-text'));
	    BLCAdmin.generatedUrl.unregisterUrlGenerator($container);
	}
	
	$container.closest('form').find('#field-' + $container.data('toggle-field') + ' input').val(!enabled);
	
	$this.data('enabled', !enabled);
});
