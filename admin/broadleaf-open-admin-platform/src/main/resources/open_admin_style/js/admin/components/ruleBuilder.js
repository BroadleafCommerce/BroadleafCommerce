/**
 * Broadleaf Commerce Rule Builder
 * This component initializes any JSON data on the page and converts
 * it into a Conditions Rule Builder
 * @see: conditions-builder-blc.js
 * @author: elbertbautista
 */
(function($, BLCAdmin) {
    
    BLCAdmin.conditions = (function() {
        
        var conditionsArray = [];
        
        return {
            
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
                var builder = this.getCondition(containerId).builder;
                
                builder.shouldSubmit = true;
                $container.show();
                
                if ($container.children().children().length == 0) {
                    if (typeToCreate == 'add-main-rule') {
                        builder.buildAddNewRule($container);
                    } else if (typeToCreate == 'add-main-item-rule') {
                        builder.buildAddNewItemRule($container);
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
                var builder = this.getCondition(containerId).builder;
                builder.shouldSubmit = false;
                $container.hide();
            }
            
        }
        
    })();
    
})($, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'a.add-main-condition', function() {
        var $container = $(this).parent();
        BLCAdmin.conditions.addAdditionalMainCondition($container);
        return false;
    });
    
    $('.rule-builder-required-field').each(function(index, element) {
        var $container = $($(this).next().next());
        var ruleType = $(this).data('ruletype');
        BLCAdmin.conditions.showOrCreateMainCondition($container, ruleType);
    });
    
    $('body').on('change', 'input.clear-rules', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());
        $ruleTitle.hide();
        BLCAdmin.conditions.hideMainCondition($container, 'add-main-rule');
    });
    
    $('body').on('change', 'input.add-main-rule, input.add-main-item-rule', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());
        var ruleType = $(this).data('ruletype');
        $ruleTitle.show();
        BLCAdmin.conditions.showOrCreateMainCondition($container, ruleType);
    });
    
    //Intercept the form submission and update all the rule builder hidden fields
    BLCAdmin.addSubmitHandler(function() {
        for (var i = 0; i < BLCAdmin.conditions.conditionCount(); i++) {
            var hiddenId = BLCAdmin.conditions.getConditionByIndex(i).hiddenId;
            
            var builder = BLCAdmin.conditions.getConditionByIndex(i).builder;
            if (!builder.shouldSubmit) {
                builder.element.find('div.conditional-rules').remove();
            }
            
            var collectedData = builder.collectData();
            $("#"+hiddenId).val(JSON.stringify(collectedData));
        }
    });
    
});