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
/**
 * Broadleaf Commerce Rule Builder
 * This component initializes any JSON data on the page and converts
 * it into a Conditions Rule Builder
 * @see: blc-conditions-builder.js
 * @author: elbertbautista
 */
(function($, BLCAdmin) {
    
    var conditionsArray = [];
    
    BLCAdmin.conditions = {
        addCondition : function(hiddenId, containerId, fields, data) {
            var condition = {
                hiddenId : hiddenId,
                containerId : containerId,
                fields : fields,
                data : data,
                json : []
            };
            
            condition.json.push(condition.fields);
            condition.json.push(condition.data);
            
            conditionsArray.push(condition)
            
            return condition;
        },
        
        getConditionByIndex : function(index) {
            return conditionsArray[index];
        },
        
        getCondition : function(containerId) {
            for (var i = 0; i < conditionsArray.length; i++) {
                if (containerId == conditionsArray[i].containerId) {
                    return conditionsArray[i];
                }
            }
            
            return null;
        },
        
        conditionCount : function() {
            return conditionsArray.length;
        },
        
        showOrCreateMainCondition : function($container, typeToCreate) {
            var containerId = $container.attr("id");
            var condition = this.getCondition(containerId);
            if (condition != null) {
                var builder = this.getCondition(containerId).builder;

                $container.show();
                if ($container.children().children().length == 0) {
                    //if (typeToCreate == 'add-main-rule') {
                    //    builder.buildAddNewRule($container);
                    //} else if (typeToCreate == 'add-main-item-rule') {
                        builder.buildAddNewItemRule($container);
                    //}
                }
            }
        },
        
        addAdditionalMainCondition : function($container) {
            var containerId = $container.attr("id");
            var builder = this.getCondition(containerId).builder;
            
            builder.buildAddNewItemRule($container, true);
        },
        
        hideMainCondition : function($container) {
            var containerId = $container.attr("id");
            $container.hide();
        },
        
        initializeCondition : function($container, condition) {
            var $builder = $container.find('#' + condition.containerId);
            $builder.conditionsBuilder(condition.json);
            condition.builder = $builder.conditionsBuilder('builder');
        }
        
    };
    
    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.rule-builder-data').each(function(index, element) {
            var $this = $(this),
                hiddenId = $this.data('hiddenid'),
                containerId = $this.data('containerid'),
                fields = $this.data('fields'),
                data = $this.data('data'),
                condition = BLCAdmin.conditions.addCondition(hiddenId, containerId, fields, data);
            
            BLCAdmin.conditions.initializeCondition($this.parent(), condition);
        });
    
        $container.find('.rule-builder-required-field').each(function(index, element) {
            var $container = $($(this).next().next());
            var ruleType = $(this).data('ruletype');
            BLCAdmin.conditions.showOrCreateMainCondition($container, ruleType);
        });
    });
    
    BLCAdmin.addPostValidationSubmitHandler(function($form) {
        for (var i = 0; i < BLCAdmin.conditions.conditionCount(); i++) {
            var condition = BLCAdmin.conditions.getConditionByIndex(i);
            var hiddenId = condition.hiddenId;
            
            var builder = condition.builder;

            if (builder != null) {
                // There are two scenarios that we should clear out rule data:
                //   1. The containing field-box has a hidden class, which means this field was explicitly hidden as it
                //      likely depends on the value of some other field and is not currently applicable
                //   2. This field is optional and currently set to off

                var explicitlyHidden = $(builder.element).closest('.field-group').hasClass('hidden');
                var onOffRadios = $(builder.element).parent().find('input[type="radio"]');
                var setToOff = onOffRadios.length < 1 ? false : onOffRadios.filter(function() {
                    return this.id.endsWith('false');
                }).is(':checked');

                if (explicitlyHidden || setToOff) {
                    builder.element.find('div.conditional-rules').remove();
                }

                var collectedData = builder.collectData();
                //only send over the error if it hasn't been explicitly turned off
                if (condition.data.error != null && !setToOff) {
                    collectedData.error = condition.data.error;
                }
                
                $("#"+hiddenId).val(JSON.stringify(collectedData));
            }
        }
    });
    
})($, BLCAdmin);

$(document).ready(function() {

    $('body').on('click', '.add-main-condition', function() {
        var $container = $(this).parent().parent();
        BLCAdmin.conditions.addAdditionalMainCondition($container);
        return false;
    });
    
    $('body').on('change', 'input.clear-rules', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());
        $ruleTitle.hide();
        
        //Also hide the error divs if they are shown
        $container.parent().find('.field-label.error').hide();
        $container.parent().find('.conditional-rules-container-mvel').hide();
        BLCAdmin.conditions.hideMainCondition($container, 'add-main-rule');
    });
    
    $('body').on('change', 'input.add-main-rule, input.add-main-item-rule', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());
        //if we are going to attempt to re-show something, if the error fields are around then re-show those rather
        //than the rule input
        if ($container.parent().find('.field-label.error').length > 0) {
            $container.parent().find('.field-label.error').show();
            $container.parent().find('.conditional-rules-container-mvel').show();
        } else {
            var ruleType = $(this).data('ruletype');
            $ruleTitle.show();
            BLCAdmin.conditions.showOrCreateMainCondition($container, ruleType);
        }
    });
    
    /**
     * Invoked when a rule was in an error state and the 'Reset Rule' button was hit
     */
    $('body').on('click', 'a.rule-reset', function(){
        var $errorLabelContainer = $(this).parent();
        var $invalidMvelContainer = $($(this).parent().next());
        var $builderContainer = $($errorLabelContainer.prev());
        var $validLabelContainer = $($builderContainer.prev());
        
        //Remove the error containers
        $errorLabelContainer.remove();
        $invalidMvelContainer.remove();
        //Show the 'real' containers
        $validLabelContainer.show();
        $builderContainer.show();
        
        //clear out the original faulty input
        var $fieldContainer = $($builderContainer.parent());
        var $ruleData = $fieldContainer.find('.rule-builder-data')
        var hiddenInput = $fieldContainer.find('input#' + $ruleData.data('hiddenid'));
        hiddenInput.val('');
        //reset the error as now there isn't one
        BLCAdmin.conditions.getCondition($ruleData.data('containerid')).data.error = '';
        return false;
    });
    
});
