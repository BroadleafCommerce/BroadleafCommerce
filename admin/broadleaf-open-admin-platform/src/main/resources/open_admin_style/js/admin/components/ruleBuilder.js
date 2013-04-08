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
            
            getCondition : function(index) {
                return conditionsArray[index];
            },
            
            conditionCount : function() {
                return conditionsArray.length;
            }
            
        }
        
    })();
    
})($, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'a.add-item-rule', function(){
        var container = $(this).next();
        if (container) {
            var containerId = $(container).attr("id");
            for (var i = 0; i < BLCAdmin.conditions.conditionCount(); i++) {
                if (containerId == BLCAdmin.conditions.getCondition(i).containerId) {
                    var builder = BLCAdmin.conditions.getCondition(i).builder;
                    builder.buildAddNewItemRule($(container), builder.data);
    
                }
            }
        }
        return false;
    });
    
    $('body').on('click', 'a.add-rule', function(){
        var container = $(this).next();
        if (container) {
            var containerId = $(container).attr("id");
            for (var i = 0; i < BLCAdmin.conditions.conditionCount(); i++) {
                if (containerId == BLCAdmin.conditions.getCondition(i).containerId) {
                    var builder = BLCAdmin.conditions.getCondition(i).builder;
                    builder.buildAddNewRule($(container), builder.data);
    
                }
            }
        }
        return false;
    });
    
    //Intercept the form submission and update all the rule builder hidden fields
    $("form").submit(function () {
        for (var i = 0; i < BLCAdmin.conditions.conditionCount(); i++) {
            var hiddenId = BLCAdmin.conditions.getCondition(i).hiddenId;
            var builder = BLCAdmin.conditions.getCondition(i).builder;
            $("#"+hiddenId).val(JSON.stringify(builder.collectData()));
        }
        return true;
    });
    
});