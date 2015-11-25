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
    
    BLCAdmin.generatedFieldValue = {
            
        registerFieldValueGenerator : function registerFieldValueGenerator($generatedFieldValueContainer) {
            var sourceFieldName = $generatedFieldValueContainer.data('source-field');
            var $sourceField = $generatedFieldValueContainer.closest('form').find('#field-' + sourceFieldName + ' input');

            $sourceField.on('keyup', function() {
                BLCAdmin.generatedFieldValue.setGeneratedFieldValue($generatedFieldValueContainer);
            });

            BLCAdmin.generatedFieldValue.setGeneratedFieldValue($generatedFieldValueContainer);
        },
        
        setGeneratedFieldValue : function setGeneratedFieldValue($generatedFieldValueContainer) {
            var sourceFieldName = $generatedFieldValueContainer.data('source-field');
            var $sourceField = $generatedFieldValueContainer.closest('form').find('#field-' + sourceFieldName + ' input');
            var $targetField = $generatedFieldValueContainer.find('input.target-field');
            var fieldValueFormatConversionHandlerName = $generatedFieldValueContainer.data('field-value-format-conversion-handler');

            var convertedFieldValue = executeFunctionByName(fieldValueFormatConversionHandlerName, window, $sourceField.val());

            $targetField.val(convertedFieldValue);
            
            $targetField.trigger('generated-fieldValue', [convertedFieldValue]);
        },
        
        unregisterFieldValueGenerator : function unregisterFieldValueGenrator($generatedFieldValueContainer) {
            var sourceFieldName = $generatedFieldValueContainer.data('source-field');
            $generatedFieldValueContainer.closest('form').find('#field-' + sourceFieldName + " input").off('keyup');
        }

    }

    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('div.generated-fieldValue-container').each(function(idx, el) {
            if ($(el).data('overridden-fieldValue') != true && !$(el).hasClass('disabledValueGeneration')) {
                BLCAdmin.generatedFieldValue.registerFieldValueGenerator($(el));
            }
        });
    });

})(jQuery, BLCAdmin);

$('body').on('click', 'a.override-generated-fieldValue', function(event) {
    event.preventDefault();
    var $this = $(this);
    
    var enabled = $this.data('enabled') == true;
	var $container = $this.closest('div.generated-fieldValue-container');
	
	if (enabled) {
	    $container.find('input').attr('readonly', 'readonly');
	    $this.text($this.data('disabled-text'));

        if (!$container.hasClass('disabledValueGeneration')) {
            BLCAdmin.generatedFieldValue.registerFieldValueGenerator($container);
        } else {
            BLCAdmin.generatedFieldValue.setGeneratedFieldValue($container);
        }
	} else {
	    $container.find('input').removeAttr('readonly');
	    $this.text($this.data('enabled-text'));
	    BLCAdmin.generatedFieldValue.unregisterFieldValueGenerator($container);
	}
	
	$container.closest('form').find('#field-' + $container.data('toggle-field') + ' input').val(!enabled);
	
	$this.data('enabled', !enabled);
});

function executeFunctionByName(functionName, context /*, args */) {
    var args = [].slice.call(arguments).splice(2);
    var namespaces = functionName.split(".");
    var func = namespaces.pop();
    for(var i = 0; i < namespaces.length; i++) {
        context = context[namespaces[i]];
    }
    return context[func].apply(this, args);
}
