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
    
    var typeaheadMatches = {};
    
    BLCAdmin.typeahead = {
            
        setToOneSelectionId : function setToOneSelectionId($input) {
            var $fieldBox = $input.closest('.field-box');
            var $extLink = $fieldBox.find('.external-link-container a');
            var val = $input.val();
            var valId = null;

            if (val != null && val != "") {
                var matches = typeaheadMatches[$fieldBox.attr('id')];
    
                if (typeof matches != "undefined") {
                    for (var i = 0; i < matches.length; i++) {
                        if (matches[i].displayKey.toUpperCase() == val.toUpperCase()) {
                            valId = matches[i].id;
                        }
                    }
                }
            }
            
            $fieldBox.find('input[type="hidden"]').val(valId);
            $extLink.attr('href', $extLink.data('foreign-key-link') + '/' + valId);

            return true;
        },

        initializeForToOne : function initializeForToOne($input) {
            var bh = new Bloodhound({
                datumTokenizer: function(d) { return Bloodhound.tokenizers.whitespace(d.value); },
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                limit: 10,
                remote: {
                    url: $input.data('typeahead-url') + '&query=%QUERY',
                    filter: function(response) {
                        // Store the response locally so we can leverage it later
                        var fieldName = $input.closest('.field-box').attr('id');
                        typeaheadMatches[fieldName] = response;

                        return response;
                    }
                }
            });
               
            bh.initialize();

            $input.typeahead(null, {
                autoselect: false,
                displayKey: 'displayKey',
                source: bh.ttAdapter()
            });
            
            $input.on('blur', function() {
                BLCAdmin.typeahead.setToOneSelectionId($input);
            });
        },
        
    };
    
    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.to-one-typeahead').each(function(index, element) {
            BLCAdmin.typeahead.initializeForToOne($(element));
        });
    });
    
})(jQuery, BLCAdmin);