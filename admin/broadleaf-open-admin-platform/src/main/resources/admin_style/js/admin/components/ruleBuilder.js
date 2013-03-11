/**
 * Broadleaf Commerce Rule Builder
 * This component initializes any JSON data on the page and converts
 * it into a Conditions Rule Builder
 * @see: conditions-builder-blc.js
 * @author: elbertbautista
 */

//The Conditions Array may have already been initialized on the page by a Rule Builder
var conditionsArray = Object.prototype.toString.call(conditionsArray) == "[object Array]" ? conditionsArray : [];

//Iterate through all the JSON data on the page and create Condition Rule Builders out of them
for (var i = 0; i < conditionsArray.length; i++) {
    var json = conditionsArray[i].json;
    var containerId = conditionsArray[i].containerId;
    $("#"+containerId).conditionsBuilder(json);
    conditionsArray[i].builder = $("#"+containerId).conditionsBuilder("builder");
}

$(".rulebuilder-container").on('click', 'a.add-item-rule', function(){
    var container = $(this).next();
    if (container) {
        var containerId = $(container).attr("id");
        for (var i = 0; i < conditionsArray.length; i++) {
            if (containerId == conditionsArray[i].containerId) {
                var builder = conditionsArray[i].builder;
                builder.buildAddNewItemRule($(container), builder.data);

            }
        }
    }
    return false;
});

$(".rulebuilder-container").on('click', 'a.add-rule', function(){
    var container = $(this).next();
    if (container) {
        var containerId = $(container).attr("id");
        for (var i = 0; i < conditionsArray.length; i++) {
            if (containerId == conditionsArray[i].containerId) {
                var builder = conditionsArray[i].builder;
                builder.buildAddNewRule($(container), builder.data);

            }
        }
    }
    return false;
});

$(".rulebuilder-form").submit(function () {
    for (var i = 0; i < conditionsArray.length; i++) {
        var hiddenId = conditionsArray[i].hiddenId;
        var builder = conditionsArray[i].builder;
        $("#"+hiddenId).val(JSON.stringify(builder.collectData()));
    }
    alert("Debug (ruleBuilder.js): Rulebuilder Fields Updated Successfully!");
    return false;
});
