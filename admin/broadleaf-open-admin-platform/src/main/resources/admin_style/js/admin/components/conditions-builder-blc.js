/**
 * Broadleaf Commerce Conditions Rule Builder
 * Javascript Conditions Builder that handles both simple targeting rules
 * and complex quantitative item rules.
 * @author: elbertbautista
 *
 * Based off the Javascript component "business-rules"
 * @author: chris j. powers
 * https://github.com/chrisjpowers/business-rules
 * Copyright 2013 Chris Powers
 * http://chrisjpowers.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
(function($) {
    $.fn.conditionsBuilder = function(options) {
        if(options == "data") {
            var builder = $(this).eq(0).data("conditionsBuilder");
            return builder.collectData();
        } else if (options == "builder") {
            var builder = $(this).eq(0).data("conditionsBuilder");
            return builder;
        } else {
            return $(this).each(function() {
                var builder = new ConditionsBuilder(this, options);
                $(this).data("conditionsBuilder", builder);
            });
        }
    };

    function ConditionsBuilder(element, options) {
        this.element = $(element);
        this.options = options || {};
        this.init();
    }

    ConditionsBuilder.prototype = {
        init: function() {
            this.fields = this.options[0].fields;
            this.data = this.options[1].data;
            var rules = this.buildRules(this.data);
            this.element.html(rules);
        },

        collectData: function() {
            var elements = this.element.find("> .conditional-rules > .conditional");
            var data = [];
            for (var i=0;i<elements.length;i++) {
                var element = elements[i];
                data.push(this.collectDataFromNode($(element)));
            }
            return data;
        },

        collectDataFromNode: function(element) {
            var klass = null;
            var qty = null
            var _this = this;
            if(element.is(".conditional")) {
                klass = element.find("> .all-any-none-wrapper > .all-any-none").val();
                if ("all" == klass) {klass = "AND";}
                if ("any" == klass) {klass = "OR";}
                if ("none" == klass) {klass = "NOT";}
                qty = element.find("> .all-any-none-wrapper > .conditional-qty").val();
            }

            if(klass) {
                var out = {};
                if (qty) {
                    out.quantity = qty;
                } else {
                    out.quantity = null;
                }
                out.groupOperator = klass;
                out.groups = [];
                element.find("> .conditional-rules > .conditional, > .rule").each(function() {
                    out.groups.push(_this.collectDataFromNode($(this)));
                });
                return out;
            }
            else {
                return {
                    quantity:null,
                    groupOperator:null,
                    groups:null,
                    name: element.find(".field").val(),
                    operator: element.find(".operator").val(),
                    value: element.find(".value").val(),
                    start: element.find(".start").val(),
                    end: element.find(".end").val()
                };
            }
        },

        buildAddNewRule: function(rules) {
            var _this = this;
            var f = _this.fields[0];
            var newField = {quantity:null, groupOperator: "AND", groups: [{name: f.value, operator: f.operators[0], value: null}]};
            var newFieldArray = [];
            newFieldArray.push(newField);
            rules.append(_this.buildConditional(newFieldArray));
        },

        buildAddNewItemRule: function(rules) {
            var _this = this;
            var f = _this.fields[0];
            var newField = {quantity:1, groupOperator: "AND", groups: [{name: f.value, operator: f.operators[0], value: null}]};
            var newFieldArray = [];
            newFieldArray.push(newField);
            rules.append(_this.buildConditional(newFieldArray));
        },


        buildRules: function(ruleDataArray) {
            return this.buildConditional(ruleDataArray) || this.buildRule(ruleDataArray);
        },

        buildConditional: function(ruleDataArray) {
            var output = $("<div>", {"class": "conditional-rules"});
            for (var i=0; i<ruleDataArray.length; i++) {
                var ruleData = ruleDataArray[i];
                var kind;
                if(ruleData.groupOperator == "AND") { kind = "all"; }
                else if(ruleData.groupOperator == "OR") { kind = "any"; }
                else if (ruleData.groupOperator == "NOT") { kind = "none"; }
                if(!kind) { return; }

                var div = $("<div>", {"class": "conditional " + kind});
                var selectWrapper = $("<div>", {"class": "all-any-none-wrapper"});
                selectWrapper.append($("<span>", {text: "Match", "class": "conditional-spacer"}));

                var qty = ruleData.quantity;
                if (qty != null) {
                    var quantity = $("<input>", {"class": "conditional-qty", "type": "text", "value": qty});
                    selectWrapper.append(quantity);
                    selectWrapper.append($("<span>", {text: "of", "class": "conditional-spacer"}));
                }

                var select = $("<select>", {"class": "all-any-none no-custom"});
                select.append($("<option>", {"value": "all", "text": "All", "selected": kind == "all"}));
                select.append($("<option>", {"value": "any", "text": "Any", "selected": kind == "any"}));
                select.append($("<option>", {"value": "none", "text": "None", "selected": kind == "none"}));
                selectWrapper.append(select);
                selectWrapper.append($("<span>", {text: "of the following rules:"}));
                div.append(selectWrapper);

                var addRuleLink = $("<a>", {"href": "#", "class": "add-rule", "text": "Add Rule"});
                var _this = this;
                addRuleLink.click(function(e) {
                    e.preventDefault();
                    var f = _this.fields[0];
                    var newField = {name: f.value, operator: f.operators[0], value: null};
                    var newFieldArray = [];
                    newFieldArray.push(newField);
                    $(this).parent(".conditional").append(_this.buildRule(newFieldArray));
                });
                div.append(addRuleLink);

                var addConditionLink = $("<a>", {"href": "#", "class": "add-condition", "text": "Add Sub-Condition"});
                addConditionLink.click(function(e) {
                    e.preventDefault();
                    var f = _this.fields[0];
                    var newField = {quantity:null, groupOperator: "AND", groups: [{name: f.value, operator: f.operators[0], value: null}]};
                    var newFieldArray = [];
                    newFieldArray.push(newField);
                    $(this).parent(".conditional").append(_this.buildConditional(newFieldArray));
                });
                div.append(addConditionLink);

                var removeLink = $("<a>", {"class": "remove", "href": "#", "text": "Remove This Sub-Condition"});
                removeLink.click(function(e) {
                    e.preventDefault();
                    $(this).parent().parent(".conditional-rules").remove();
                });
                div.append(removeLink);

                var rules = ruleData.groups;
                for(var j=0; j<rules.length; j++) {
                    var ruleArray = [];
                    ruleArray.push(rules[j]);
                    div.append(this.buildRules(ruleArray));
                }
                output.append(div);
            }

            return output;

        },

        buildRule: function(ruleDataArray) {
            var ruleData = ruleDataArray[0];
            var ruleDiv = $("<div>", {"class": "rule"});
            var fieldSelect = getFieldSelect(this.fields, ruleData);
            var operatorSelect = getOperatorSelect();

            fieldSelect.change(onFieldSelectChanged.call(this, operatorSelect, ruleData));

            ruleDiv.append(fieldSelect);
            ruleDiv.append(operatorSelect);
            ruleDiv.append(removeLink());

            fieldSelect.change();
            ruleDiv.find("> .value").val(ruleData.value);
            return ruleDiv;
        },

        operatorsFor: function(fieldName) {
            for(var i=0; i < this.fields.length; i++) {
                var field = this.fields[i];
                if(field.name == fieldName) {
                    return window[field.operators];
                }
            }
        }
    };

    function getFieldSelect(fields, ruleData) {
        var select = $("<select>", {"class": "field no-custom"});
        for(var i=0; i < fields.length; i++) {
            var field = fields[i];
            var option = $("<option>", {
                text: field.label,
                value: field.name,
                selected: ruleData.name == field.name
            });
            option.data("options", window[field.options]);
            select.append(option);
        }
        return select;
    }

    function getOperatorSelect() {
        var select = $("<select>", {"class": "operator no-custom"});
        select.change(onOperatorSelectChange);
        return select;
    }

    function removeLink() {
        var removeLink = $("<a>", {"class": "remove", "href": "#", "text": "Remove"});
        removeLink.click(onRemoveLinkClicked);
        return removeLink;
    }

    function onRemoveLinkClicked(e) {
        e.preventDefault();
        $(this).parent().remove();
    }

    function onFieldSelectChanged(operatorSelect, ruleData) {
        var builder = this;
        return function(e) {
            var operators = builder.operatorsFor($(e.target).val());
            operatorSelect.empty();
            for(var i=0; i < operators.length; i++) {
                var operator = operators[i];
                var option = $("<option>", {
                    text: operator.label || operator.name,
                    value: operator.name,
                    selected: ruleData.operator == operator.name
                });
                option.data("fieldType", operator.fieldType);
                operatorSelect.append(option);
            }
            operatorSelect.change();
        }
    }

    function onOperatorSelectChange(e) {
        var $this = $(this);
        var option = $this.find("> :selected");
        var container = $this.parents(".rule");
        var fieldSelect = container.find(".field");
        var currentValue = container.find(".value");
        var val = currentValue.val();

        switch(option.data("fieldType")) {
            case "NONE":
                $this.after($("<input>", {"type": "hidden", "class": "value"}));
                break;
            case "TEXT":
                $this.after($("<input>", {"type": "text", "class": "value"}));
                break;
            case "RANGE":
                $this.after($("<input>", {"type": "text", "class": "end"}))
                    .after("<span class=\"value conditional-spacer\">and</span>")
                    .after($("<input>", {"type": "text", "class": "start"}));
                break;
            case "SELECT":
                var select = $("<select>", {"class": "value"});
                var options = fieldSelect.find("> :selected").data("options");
                for(var i=0; i < options.length; i++) {
                    var opt = options[i];
                    select.append($("<option>", {"text": opt.label || opt.name, "value": opt.name}));
                }
                $this.after(select);
                break;
        }
        currentValue.remove();
    }

})(jQuery);
