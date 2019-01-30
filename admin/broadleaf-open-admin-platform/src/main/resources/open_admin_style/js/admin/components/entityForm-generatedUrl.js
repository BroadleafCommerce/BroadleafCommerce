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

                if (typeof generatedPrefix === 'undefined') {
                    generatedPrefix = "";
                }

                if (generatedPrefix != '/') {
                    generatedPrefix += '/';
                }
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

            var finalUrl = generatedPrefix + convertedUrl;
            $targetField.val(finalUrl);
            
            $targetField.trigger('generated-url', [finalUrl]);
        },
        
        unregisterUrlGenerator : function unregisterUrlGenrator($generatedUrlContainer) {
            var sourceFieldName = $generatedUrlContainer.data('source-field');
            $generatedUrlContainer.closest('form').find('#field-' + sourceFieldName + " input").off('keyup');
        },
        
        convertToUrlFragment : function convertToUrlFragment(val, options) {
            var valPostFix = "";
            if (val.toString().indexOf('.') != -1) {
                var valFragments = val.split('.');
                valPostFix = valFragments[valFragments.length - 1];
                val = val.substring(0,val.length - valPostFix.length)
                if(valPostFix){
                    valPostFix="."+valPostFix
                }
            }

            if (options != null && options.allowSlash) {
                return val.replace(/ /g, BLC.systemProperty.urlFragmentSeparator).replace(/[^\w\s-_\/]/gi, '').toLowerCase() + valPostFix;
            } else {
                return val.replace(/ /g, BLC.systemProperty.urlFragmentSeparator).replace(/[^\w\s-_]/gi, '').toLowerCase() + valPostFix;
            }
        }

    };

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
