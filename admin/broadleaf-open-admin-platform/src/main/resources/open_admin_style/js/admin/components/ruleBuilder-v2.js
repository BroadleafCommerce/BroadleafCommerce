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
/**
 * Broadleaf Commerce Rule Builder v2
 * This component initializes any Ruler Builder JSON data on the page and converts
 * it into a jQuery Query Rule Builder Component
 *
 * The Admin RuleBuilder also provide additional functionality such as handling multiple
 * quantity item rules and question based rules on top of what QueryBuilder provides.
 *
 * @author: elbertbautista
 */
(function($, BLCAdmin) {

    BLCAdmin.RuleTypeEnum = {
        RULE_SIMPLE : "rule-builder-simple",
        RULE_SIMPLE_TIME : "rule-builder-simple-time",
        RULE_WITH_QUANTITY : "rule-builder-with-quantity"
    };

    BLCAdmin.productNameDelimiter = '¬';

    /**
     * An Admin page may contain multiple rule builders of various different types.
     * @type {Array}
     */
    var ruleBuildersArray = [];

    /**
     * Fields (Filters) for the Query Builder may need some pre-initialization (e.g. Selectize) and set-up.
     * All custom handlers that need to be run on this field can be added to this list.
     * @type {Array}
     */
    var preInitQueryBuilderFieldHandlers = [];

    /**
     * Fields (Filters) for the Query Builder may need some post-constructions set up (e.g. Selectize).
     * All custom post handlers that need to be run on the builder can be added to this list.
     * @type {Array}
     */
    var postConstructQueryBuilderFieldHandlers = [];

    BLCAdmin.ruleBuilders = {

        /**
         * Add a {ruleBuilder} to the ruleBuildersArray
         * A single Admin RuleBuilder may contain more than one QueryBuilder - such as in the case of complex
         * item rules (i.e. org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY)
         *
         * @param hiddenId - the ID of the hidden JSON input element where the constructed value is stored
         * @param containerId - the ID of the container <div> element where the query builders are rendered
         * @param fields - field metadata needed for this particular query builder(s)
         * @param data - any rules (data) that need to be populated on the query builder(s)
         * @returns {{hiddenId: *, containerId: *, fields: *, data: *}}
         */
        addRuleBuilder : function(hiddenId, containerId, ruleType, fields, data, modal) {
            var ruleBuilder = {
                modal: modal,
                hiddenId : hiddenId,
                ruleType : ruleType,
                containerId : containerId,
                fields : fields.fields,
                data : data.data,
                builders : [],

                getFieldLabelById : function(fieldId) {
                    for (var i=0; i<this.fields.length; i++) {
                        if (this.fields[i].id === fieldId) {
                            return this.fields[i].label;
                        }
                    }
                    return null;
                },

                getFieldValueById : function(fieldId, value) {
                    for (var i=0; i<this.fields.length; i++) {
                        if (this.fields[i].id === fieldId) {
                            if (this.fields[i].values) {
                                for (var j = 0; j < this.fields[i].values.length; j++) {
                                    var cValue = this.fields[i].values[j];
                                    if (cValue[value] !== undefined && cValue[value].length) {
                                        return cValue[value];
                                    }
                                }
                            }
                        }
                    }
                    return value;
                },

                getOperatorLabelByOperatorType : function(operatorType)  {
                    var operator = this.builders[0].queryBuilder('getOperatorLabelByType', operatorType);
                    if (operator === 'is equal to') operator = 'is';
                    else if (operator === 'is not equal') operator = 'is not';
                    else if (operator === 'is in') operator = 'is';
                    else if (operator === 'is not in') operator = 'is not';
                    return operator;
                },

                addQueryBuilder : function(builder) {
                    var exists = false;
                    for (var i=0; i < ruleBuilder.builders.length; i++) {
                        if (ruleBuilder.builders[i][0].id === $(builder).attr('id')) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        ruleBuilder.builders.push(builder);
                    }
                },

                removeQueryBuilder : function(builder) {
                    var position = 0;
                    for (var i=0; i < ruleBuilder.builders.length; i++) {
                        if (ruleBuilder.builders[i][0].id === $(builder).attr('id')) {
                            position = i;
                            break;
                        }
                    }
                    if (~position) ruleBuilder.builders.splice(position, 1);
                },

                removeAllQueryBuilders : function() {
                    ruleBuilder.builders = [];
                }

            };

            ruleBuildersArray.push(ruleBuilder);
            return ruleBuilder;
        },

        /**
         * Convenience method to retrieve all {ruleBuilder} objects that have been registered on the page
         * @returns {Array}
         */
        getAllRuleBuilders : function() {
            return ruleBuildersArray;
        },

        /**
         * Get a {ruleBuilder} from the ruleBuildersArray by index
         * @param index
         * @returns {*}
         */
        getRuleBuilderByIndex : function(index) {
            return ruleBuildersArray[index];
        },

        /**
         * Get a {ruleBuilder} from the ruleBuildersArray by hiddenId
         * @param hiddenId
         * @returns {*}
         */
        getRuleBuilderByHiddenId : function(hiddenId) {
            for (var i = 0; i < ruleBuildersArray.length; i++) {
                if (hiddenId === ruleBuildersArray[i].hiddenId) {
                    return ruleBuildersArray[i];
                }
            }

            return null;
        },

        /**
         * Get a {ruleBuilder} from the ruleBuildersArray by containerId
         * @param containerId
         * @returns {*}
         */
        getRuleBuilder : function(containerId) {
            if (containerId.indexOf('-modal', containerId.length - '-modal'.length) !== -1) {
                containerId = containerId.slice(0, -('-modal'.length));
            }

            for (var i = 0; i < ruleBuildersArray.length; i++) {
                if (containerId === ruleBuildersArray[i].containerId) {
                    return ruleBuildersArray[i];
                }
            }

            return null;
        },

        /**
         * Get the number of rule builders that have been initialized on the page
         * @returns {Number}
         */
        ruleBuilderCount : function() {
            return ruleBuildersArray.length;
        },

        /**
         * Returns an "And Divider" element used primarily for
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         *
         * @returns {*|jQuery|HTMLElement}
         */
        getAndDivider : function() {
            var andDivider = $("<div>", {'class' : 'and-divider'});
            var andSpan = $("<span>", {'text' : 'AND'});
            andDivider.append(andSpan);
            return andDivider;
        },

        /**
         * Returns an "Add Another Condition Link" element used primarily for
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         *
         * @returns {*|jQuery|HTMLElement}
         */
        getAddAnotherConditionLink : function() {
            //TODO i18n the label
            var outerDiv = $("<div>", {'class' : 'add-and-button' });
            var addMainConditionLink = $("<div>", {
                'class': "button and-button add-main-item-rule",
                'text': "Add Another Condition"
            });
            outerDiv.prepend(addMainConditionLink);
            return outerDiv;
        },

        /**
         * Returns a "Set Rule" button element intended to be used on a rule builder modal
         * @param hiddenId
         * @returns {*|jQuery|HTMLElement}
         */
        getSaveModalRuleLink : function(hiddenId) {
            var saveBtn = $("<button>", {'class' : 'set-modal-rule-builder button primary',
                                         'text' : 'Set Rule',
                                         'data-hiddenId' : hiddenId});
            return saveBtn;
        },

        /**
         * Returns a "Remove Condition Link" element
         * @returns {*|jQuery|HTMLElement}
         */
        getRemoveConditionLink : function() {
            var btn = $("<button>", {'class' : 'button remove-main-item-rule'});
            var icon = $("<i>", {
                "class": "fa fa-minus-circle"
            });

            btn.prepend(icon);
            return btn;
        },

        /**
         * This method is intended to show an existing rule or create a new one if one does not exist.
         *
         * @param $container - the ".query-builder-rules-container" in which to append the builder
         * @param typeToCreate - if there is no existing rule, the method will look for the passed in typeToCreate:
         * - BLCAdmin.RuleTypeEnum.RULE_SIMPLE : associated with org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_SIMPLE
         * - BLCAdmin.RuleTypeEnum.RULE_SIMPLE_TIME : associated with org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_SIMPLE_TIME
         * - BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY : associated with org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         */
        showOrCreateMainRuleBuilder : function($container, typeToCreate) {
            var containerId = $container.attr("id");
            var ruleBuilder = this.getRuleBuilder(containerId);
            if (ruleBuilder != null) {
                var $onOffRadios = $container.closest('.field-group').find('input[type="radio"].radio');
                var setToOff = $onOffRadios.length < 1 ? false : $onOffRadios.filter(function() {
                    return this.id.endsWith('false');
                }).is(':checked');

                if (!$container.hasClass('rule-data-error') && !setToOff) {
                    $container.parent().show();
                }

                //If invoked from a "RADIO" - create new query builder for the container
                if ($container.children().children().length == 0) {
                    if (typeToCreate === BLCAdmin.RuleTypeEnum.RULE_SIMPLE || typeToCreate === BLCAdmin.RuleTypeEnum.RULE_SIMPLE_TIME) {
                        this.addAdditionalQueryBuilder($container, null);
                    } else if (typeToCreate === BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                        this.addAdditionalQueryBuilder($container, 1);
                    }
                }
            }
        },

        /**
         * Hides the rule builder container passed in
         * @param $container
         */
        hideMainRuleBuilder : function($container) {
            var containerId = $container.attr("id");
            $container.hide();
        },

        /**
         * Constructs an empty rule data object
         * @param qty
         * @returns {{pk: null, quantity: *, condition: string, rules: Array}}
         */
        getEmptyRuleData : function(qty) {
            var emptyData = {
                pk: null,
                quantity: qty,
                condition:'AND',
                rules: []
            };
            return emptyData;
        },

        /**
         * Called in order to create a new empty Query Builder
         * Supports:
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_SIMPLE
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_SIMPLE_TIME
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         * @param $container
         * @param qty - if null is passed in, a simple rule builder will be created. Otherwise, an item quantity builder.
         */
        addAdditionalQueryBuilder : function($container, qty) {
            var containerId = $container.attr("id");
            var ruleBuilder = this.getRuleBuilder(containerId);
            this.constructQueryBuilder($container, this.getEmptyRuleData(qty), ruleBuilder.fields, ruleBuilder);
        },

        /**
         * Called in order to remove an Item Quantity Query Builder.
         * This will also remove the associated builder from the RuleBuilder.builders array
         * and any UI divider elements.
         * @param $container
         * @param builder
         */
        removeAdditionalQueryBuilder : function($container, builder) {
            var containerId = $container.attr("id");
            var ruleBuilder = this.getRuleBuilder(containerId);
            ruleBuilder.removeQueryBuilder($(builder));
            $(builder).next('.and-divider').remove();
            $(builder).remove();
        },

        /**
         * Called on initial page load to initialize all rule builders on the page
         * given the passed in container
         *
         * @param $container
         * @param ruleBuilder
         * @param ruleType
         */
        initializeRuleBuilder : function($container, ruleBuilder) {
            var container = $container.find('#' + ruleBuilder.containerId);

            for (var i=0; i<ruleBuilder.data.length; i++) {
                this.constructQueryBuilder(container, ruleBuilder.data[i], ruleBuilder.fields, ruleBuilder);
            }
        },

        /**
         * The main function called to construct the Query Builder associated with the
         * passed in Container and RuleBuilder
         *
         * Depending on the type of rule passed in - it will construct various UI elements that
         * are standard across the Admin.
         *
         * @param container
         * @param ruleData
         * @param fields
         * @param ruleBuilder
         * @param ruleType
         */
        constructQueryBuilder : function(container, ruleData, fields, ruleBuilder) {
            if (ruleBuilder.ruleType === BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                container.find('.add-and-button').remove();
            }

            var builder = $("<div>", {"class": "query-builder-rules"});
            container.append(builder);
            var addRemoveConditionsLink = ruleBuilder.builders.length > 1;
            builder.queryBuilder(this.initializeQueryBuilderConfig(ruleData, fields, addRemoveConditionsLink));

            //run any post-construct handlers
            BLCAdmin.ruleBuilders.runPostConstructQueryBuilderFieldHandler(builder);

            ruleBuilder.addQueryBuilder($(builder));

            if (ruleBuilder.ruleType === BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                container.append(this.getAndDivider());
                container.append(this.getAddAnotherConditionLink());
            }

            if (ruleBuilder.ruleType === BLCAdmin.RuleTypeEnum.RULE_SIMPLE_TIME) {
                var allCondition = $('<span>', {
                    html: ' <strong>ALL</strong>'
                });

                allCondition.insertAfter(container.find('.query-builder-all-any-wrapper').hide());
                container.find('.group-conditions').css('line-height', '40px');
            }

            /****** For Developers: Test JSON Link *******
             var testJsonLink = $("<a>", {"href": "#", "text": "Test"});
             testJsonLink.click(function(e) {
                    e.preventDefault();
                    alert(JSON.stringify($(builder).queryBuilder('getRules'),undefined, 2));
                });
             container.append(testJsonLink);
             *************************************************/
        },

        /**
         * Handlers designed to execute on a field before initializing the query builder
         */
        addPreInitQueryBuilderFieldHandler : function(fn) {
            preInitQueryBuilderFieldHandlers.push(fn);
        },

        /**
         * Invoke all registered query builder field handlers for this field
         * @param field
         */
        runPreInitQueryBuilderFieldHandler : function(field) {
            for (var i = 0; i < preInitQueryBuilderFieldHandlers.length; i++) {
                preInitQueryBuilderFieldHandlers[i](field);
            }
        },

        /**
         * Handlers designed to execute on the query builder after construction
         */
        addPostConstructQueryBuilderFieldHandler : function(fn) {
            postConstructQueryBuilderFieldHandlers.push(fn);
        },

        runPostConstructQueryBuilderFieldHandler : function(builder) {
            for (var i = 0; i < postConstructQueryBuilderFieldHandlers.length; i++) {
                postConstructQueryBuilderFieldHandlers[i](builder);
            }
        },

        /**
         * A custom pre-init query builder field handler to modify the filters object
         * in order to support a Boolean Radio button widget in the Query Builder.
         * @param field
         */
        initBooleanRadioPreInitFieldHandler : function(field) {
            var opRef = field.operators;

            if (opRef && typeof opRef === 'string' && ("blcOperators_Boolean" === opRef)) {
                field.input = 'radio';
                field.values = {
                    'true': 'true',
                    'false': 'false'
                }
            }
        },

        /**
         * A custom pre-init query builder field handler to modify the filters object
         * in order to support a Date Picker widget in the Query Builder.
         * @param field
         */
        initDatePickerPreInitFieldHandler : function(field) {
            var opRef = field.operators;

            if (opRef && typeof opRef === 'string' && ("blcOperators_Date" === opRef)) {
                field.type = 'date';
                field.plugin = 'datetimepicker';
                field.plugin_config = {
                    format: "m/d/Y H:i",
                    onChangeDateTime : function (current_time, $input) {
                        $input.trigger('input');
                    }
                };
            }
        },

        /**
         * A custom pre-init query builder field handler to modify the filters object
         * in order to support the Selectize widget in the Query Builder.
         * @param field
         */
        initSelectizePreInitFieldHandler : function(field) {
            //initialize selectize plugin
            var opRef = field.operators;

            if (opRef && typeof opRef === 'string' && ("blcOperators_Selectize" === opRef || "blcOperators_Selectize_Enumeration" === opRef || "blcOperators_Text_List" === opRef)) {

                //if the options are "pre-defined" as in the case of an enumeration, we'll need to convert
                //this into an actual array since the system may pass that information back as a single string
                var valRef = field.values;
                if (valRef && typeof valRef === 'string' &&
                    valRef.startsWith('[') && valRef.endsWith("]")) {
                    field.values = $.parseJSON(valRef);
                }

                var allowAdd = false;
                if ("blcOperators_Text_List" === opRef) {
                    allowAdd = true;
                }

                var sectionKey = field.selectizeSectionKey;

                field.multiple = true;
                field.plugin = 'selectize';
                field.input = function(rule, name){
                    return "<input type='text' class='query-builder-selectize-input' data-hydrate=''>";
                };
                field.plugin_config = {
                    maxItems: null,
                    persist: false,
                    valueField: "id",
                    labelField: "label",
                    searchField: "label",
                    loadThrottle: 100,
                    preload: true,
                    hideSelected: true,
                    dropdownParent: 'body',
                    closeAfterSelect: !allowAdd,
                    placeholder: field.label + " +",
                    create: allowAdd,
                    createOnBlur: allowAdd,
                    delimiter: BLCAdmin.productNameDelimiter,
                    onInitialize: function () {
                        var $selectize = this;
                        $selectize.sectionKey = sectionKey;
                        $selectize.opRef = opRef;
                        $selectize.enumValues = field.values;
                        this.revertSettings.$children.each(function () {
                            $.extend($selectize.options[this.value], $(this).data());
                        });
                    },
                    onLoad: function() {
                        // Initialize selectize rule data
                        // after the options have been loaded
                        // (Values may contain multiple items and are sent back as a single String array)
                        var $selectize = this;
                        var data = $selectize.$input.attr("data-hydrate");

                        var dataHydrate = BLCAdmin.stringToArray(data, "\",\"");
                        for (var k = 0; k < dataHydrate.length; k++) {
                            var item = dataHydrate[k];
                            if ($selectize.getOption(item).length === 0) {
                                $selectize.addOption({id: item, label: item});
                            }
                            if (!isNaN(item)) {
                                $selectize.addItem(Number(item), false);
                            } else {
                                $selectize.addItem(item, false);
                            }
                        }
                    },
                    load: function(query, callback) {
                        var $selectize = this;
                        var queryData = {};
                        queryData["name"] = query;
                        queryData["criteria"] = "RULE";

                        if ("blcOperators_Selectize_Enumeration" === $selectize.opRef) {
                            var data = {options: []};
                            $.each($selectize.enumValues, function(index, value) {
                                var ob = Object.keys(value);
                                if ($selectize.getOption(ob[0]).length === 0 && $selectize.getItem(ob[0]).length === 0) {
                                    data.options.push({id: ob[0], name: value[ob[0]]});
                                    $selectize.addOption({id: ob[0], label: value[ob[0]]});
                                }
                            });
                            callback(data);
                        } else if ("blcOperators_Selectize" === $selectize.opRef) {
                            BLC.ajax({
                                url: BLC.servletContext + "/" + sectionKey + "/selectize",
                                type: 'GET',
                                data: queryData
                            }, function (data) {
                                $.each(data.options, function (index, value) {
                                    if ($selectize.getOption(value.id).length === 0 && $selectize.getItem(value.id).length === 0) {
                                        $selectize.addOption({id: value.id, label: value.name});
                                        if (typeof value.alternateId !== 'undefined') {
                                            $selectize.options[value.name].alternate_id = data.alternateId;
                                        }
                                    }
                                });
                                callback(data);
                            });
                        } else {
                            callback();
                        }
                    },
                    onItemAdd: function(value, $item) {
                        var $selectize = $(this);
                        if ("blcOperators_Text_List" !== $selectize[0].opRef) {
                            $item.closest('.selectize-input').find('input').blur();
                        }
                    },
                    onItemRemove: function (value, $item) {
                        this.addOption({id: value, label: $item.html()});
                        this.refreshOptions(true);
                    }
                };
                field.valueSetter = function(rule, value) {
                    rule.$el.find('.rule-value-container input.query-builder-selectize-input')[0].selectize.setValue(value);
                    rule.$el.find('.rule-value-container input.query-builder-selectize-input').attr('data-hydrate', value);
                };
                field.valueGetter = function(rule) {
                    var value = rule.$el.find('.rule-value-container input.query-builder-selectize-input').val();
                    value = value.split(BLCAdmin.productNameDelimiter).join("\",\"");
                    if(value.length <= 0) {
                        return "";
                    }
                    return "[\"" + value + "\"]";
                }
            }
        },

        /**
         * A custom post-construct query builder handler to fix the selectize widget upon adding a new rule
         * @param builder
         */
        initSelectizePostConstructFieldHandler : function (builder) {
            $(builder).on('afterCreateRuleInput.queryBuilder', function(e, rule) {
                if (rule.filter.plugin == 'selectize') {
                    rule.$el.find('.rule-value-container').css('min-width', '200px')
                        .find('.selectize-control').removeClass('form-control');
                }
            });
        },

        /**
         * Initializes the configuration object necessary for the jQuery Query Builder
         * to support the BLC Admin Rule Builder use cases (RULE_WITH_QUANTITY, RULE_SIMPLE, and RULE_SIMPLE_TIME)
         * by passing in the fields (filters) and ruleData (rules) for the passed in rule builder
         *
         * Plugin configurations is also performed in order to support third party components
         * such as Selectize
         *
         * @param ruleData
         * @param fields
         * @returns {
         *  {plugins:
         *      {blc-admin-query-builder: {pk: (null|blc-complex-query-builder.pk|*|jQuery),
         *      quantity: (*|blc-complex-query-builder.quantity|ConditionsBuilder.collectDataFromNode.quantity|newField.quantity|jQuery|out.quantity)}},
         *      icons: {add_rule: string, remove_rule: string},
         *      allow_groups: boolean,
         *      filters: *,
         *      rules: *, operators: *}
         * }
         */
        initializeQueryBuilderConfig : function(ruleData, fields, addRemoveConditionsLink) {
            //initialize operators and values on the fields
            for (var i=0; i<fields.length; i++){
                (function(){

                    //run any pre-initialization handlers for this field
                    BLCAdmin.ruleBuilders.runPreInitQueryBuilderFieldHandler(fields[i]);

                    var opRef = fields[i].operators;
                    if (opRef && typeof opRef === 'string') {
                        fields[i].operators = window[opRef];
                    }

                    var valRef = fields[i].values;
                    if (valRef && typeof valRef === 'string') {
                        fields[i].values = window[valRef];
                    }

                })();
            }

            //Parse the rule data if applicable
            for (var k = 0; k < ruleData.rules.length; k++) {
                var ruleValue =  ruleData.rules[k].value;
                var ruleOperator = ruleData.rules[k].operator;
                if (ruleOperator === 'BETWEEN' || ruleOperator === 'BETWEEN_INCLUSIVE') {
                    try {
                        ruleData.rules[k].value = $.parseJSON(ruleValue);
                    } catch(err) {
                        ruleData.rules[k].value = [ruleValue];
                    }
                }
            }

            var removeBtn = addRemoveConditionsLink? this.getRemoveConditionLink() : null;

            var config = {
                plugins: {'blc-admin-query-builder': {
                            pk:ruleData.pk,
                            quantity:ruleData.quantity,
                            removeConditionsLink: removeBtn}},
                icons: {'add_rule':'fa fa-plus-circle',
                        'remove_rule':'fa fa-minus-circle'},
                allow_groups: false,
                inputs_separator: "<span class='rule-val-sep'>,</span>",
                filters: fields,
                rules: ruleData.rules && ruleData.rules.length > 0 ? ruleData : null,
                operators: window['blcOperators'],
                select_placeholder: '~ Choose Attribute'
            };
            return config;
        },

        /**
         * Set the appropriate JSON value on the "hiddenId" input element for the corresponding rule builder.
         *
         * Performs the appropriate data transformations in order to properly bind with the backing
         * org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper
         *
         * @param ruleBuilder
         */
        setJSONValueOnField : function (ruleBuilder) {
            var hiddenId = ruleBuilder.hiddenId;
            var builders = ruleBuilder.builders;

            if (builders != null && ruleBuilder) {
                var collectedData = this.getAllRuleBuilderRules(ruleBuilder);

                $("#"+hiddenId).val(JSON.stringify(collectedData));
                this.setReadableJSONValueOnField(ruleBuilder, collectedData.data);
            }
        },

        /**
         * Returns the JSON value of the current rule builder.
         *
         * NOTE: this will not collect and set every rule builder passed in. It determines whether or not
         * to collect the data based on its state (i.e. if there is a RADIO and it is off, it will not collect data)
         * 
         * @param ruleBuilder
         * @returns {{}}
         */
        getAllRuleBuilderRules : function (ruleBuilder) {
            var $container = $('#'+ruleBuilder.containerId);

            var collectedData = {};
            collectedData.data = [];
            for (var j = 0; j < ruleBuilder.builders.length; j++) {
                var builder = ruleBuilder.builders[j];
                var dataDTO = $(builder).queryBuilder('getRules', { displayErrors : ruleBuilder.displayErrors });
                if (dataDTO.rules) {
                    dataDTO.pk = $(builder).find(".rules-group-header-item-pk").val();
                    dataDTO.quantity = $(builder).find(".rules-group-header-item-qty").val();
                    for (var k = 0; k < dataDTO.rules.length; k++) {
                        if (Array.isArray(dataDTO.rules[k].value)) {
                            dataDTO.rules[k].value =  JSON.stringify(dataDTO.rules[k].value);
                        }
                    }

                    collectedData.data.push(dataDTO);
                }
            }

            // There are two scenarios that we should clear out rule data:
            //   1. The containing field-group has a hidden class, which means this field was explicitly hidden as it
            //      likely depends on the value of some other field and is not currently applicable
            //   2. This field is optional and currently set to off

            var explicitlyHidden = $container.closest('.field-group').hasClass('hidden');
            var $onOffRadios = $container.closest('.field-group').find('input[type="radio"].radio');
            var setToOff = $onOffRadios.length < 1 ? false : $onOffRadios.filter(function() {
                return this.id.endsWith('false');
            }).is(':checked');

            if (explicitlyHidden || setToOff) {
                collectedData.data = [];
            }

            //only send over the error if it hasn't been explicitly turned off
            if (ruleBuilder.data.error != null && !setToOff) {
                collectedData.error = ruleBuilder.data.error;
            }

            return collectedData;
        },
        
        /** Clears hidden field data **/
        clearField : function (ruleBuilder) {
            var hiddenId = ruleBuilder.hiddenId;
            $("#"+hiddenId).val('{"data":[]}');
            this.setReadableJSONValueOnField(ruleBuilder, null);
        },

        /** Set the readable translation of the rule corresponding to the rule builder **/
        setReadableJSONValueOnField : function (ruleBuilder, data) {
            var hiddenId = ruleBuilder.hiddenId;
            var readableElement = $('#'+hiddenId+'-readable');

            //If the element exists set the value
            if (readableElement) {
                //clear out existing content
                $(readableElement).html('');

                // If no data, set "No rules applied"
                if (data == null || data.length == 0) {
                    var noRules = $("<span>", {'class': 'readable-no-rule', 'html' : 'No rules applied yet,'});

                    var addRules = $('<span>', {
                        'data-hiddenId':  hiddenId,
                        'data-ruleType': ruleBuilder.ruleType,
                        'data-ruleTitleId': ruleBuilder.containerId + '-header',
                        'html': '&nbsp;add some',
                        'class': 'launch-modal-rule-builder launch-link'
                    });

                    noRules.append(addRules);
                    $(readableElement).append(noRules);
                    $(readableElement).parent().removeClass('can-edit');
                // else fill in data
                } else {

                    for (var i = 0; i < data.length; i++) {
                        var dataDTO = data[i];
                        var prefix = $("<span>", {
                            'class': 'readable-rule-prefix',
                            'text': dataDTO.quantity ?
                            'Match ' + dataDTO.quantity + ' items where:' :
                                'Rule where:'
                        });

                        $(readableElement).append(prefix);
                        var listElement = $('<ul>');
                        $(readableElement).append(listElement);

                        var allRules = {};
                        for (var k = 0; k < dataDTO.rules.length; k++) {
                            var ruleDTO = dataDTO.rules[k];
                            var condition = dataDTO.condition;

                            var key = ruleBuilder.getFieldLabelById(ruleDTO.id);

                            var valArray;
                            try {
                                valArray = $.parseJSON(ruleDTO.value);
                            } catch(err) {
                                valArray = [ruleDTO.value];
                            }

                            var valueString = undefined;
                            if (valArray != null) {
                                for(var i = 0; i < valArray.length; i++){
                                    var val = ruleBuilder.getFieldValueById(ruleDTO.id, valArray[i]);

                                    if (allRules[key] === undefined) {
                                        allRules[key] = ['<strong>' + val + '</strong>'];
                                    } else {
                                        allRules[key].push('<strong>' + val + '</strong>');
                                    }

                                    var values = allRules[key];
                                    valueString = values.join(' OR ');
                                }
                            }

                            key = '<strong>' + key + '</strong>';
                            if (k != 0) {
                                key = condition + ' ' + key;
                            }

                            var listItem = $('<li>');
                            var name = $("<span>", {
                                'class': 'readable-rule-field',
                                'html': key
                            });
                            var operator = $("<span>", {
                                'class': 'readable-rule-operator',
                                'text': ruleBuilder.getOperatorLabelByOperatorType(ruleDTO.operator)
                            });

                            $(listItem).append(name);
                            $(listItem).append(operator);

                            if (valueString !== undefined) {
                                var value = $("<span>", {
                                    'class': 'readable-rule-value',
                                    'html': valueString
                                });
                                $(listItem).append(value);
                            }

                            $(listElement).append(listItem);
                        }
                    }
                    $(readableElement).parent().addClass('can-edit');

                }
            }

        },

        /** Remove rule builders added by a modal form */
        removeModalRuleBuilders : function ($form) {
            var numRuleBuilders = BLCAdmin.ruleBuilders.ruleBuilderCount();
            var numFormRuleBuilders = $form.find('.rule-builder-simple, .rule-builder-simple-time, .rule-builder-with-quantity').length;
            var startIndex = numRuleBuilders - numFormRuleBuilders; // index from which to start removing rule builders
            BLCAdmin.ruleBuilders.getAllRuleBuilders().splice(startIndex, numFormRuleBuilders);
        }

    };

    /**
     * Initialization handler to find all rule builders on the page and initialize them with
     * the appropriate fields and data (as specified by the container)
     */
    BLCAdmin.addInitializationHandler(function($container) {
        //Add default pre-init and post-construct handlers (e.g. selectize)
        BLCAdmin.ruleBuilders.addPreInitQueryBuilderFieldHandler(BLCAdmin.ruleBuilders.initBooleanRadioPreInitFieldHandler);
        BLCAdmin.ruleBuilders.addPreInitQueryBuilderFieldHandler(BLCAdmin.ruleBuilders.initDatePickerPreInitFieldHandler);
        BLCAdmin.ruleBuilders.addPreInitQueryBuilderFieldHandler(BLCAdmin.ruleBuilders.initSelectizePreInitFieldHandler);
        BLCAdmin.ruleBuilders.addPostConstructQueryBuilderFieldHandler(BLCAdmin.ruleBuilders.initSelectizePostConstructFieldHandler);

        //Initialize all rule builders on the page
        $container.find('.rule-builder-data').each(function(index, element) {
            var $this = $(this),
                hiddenId = $this.data('hiddenid'),
                containerId = $this.data('containerid'),
                fields = $this.data('fields'),
                data = $this.data('data'),
                modal = $this.data('modal'),
                ruleType =  $(this).data('ruletype'),
                ruleBuilder = BLCAdmin.ruleBuilders.addRuleBuilder(hiddenId, containerId, ruleType, fields, data, modal);

            //Create QueryBuilder Instances for all rule builders on the page
            var parent = $this.parent().clone();
            BLCAdmin.ruleBuilders.initializeRuleBuilder(parent, ruleBuilder);


            parent.find('select').each(function(i, el) {
                var el = $(el);
                if (el.hasClass('form-control')) {
                    el.removeClass('form-control').blSelectize({delimiter: BLCAdmin.productNameDelimiter});
                }
            });

            $this.parent().replaceWith(parent);
        });

        //Once all the query builders have been initialized - show or render the component based on its display type
        $container.find('.rule-builder-required-field').each(function(index, element) {
            var ruleType = $(this).data('ruletype');
            var launchModal = $(this).data('modal');
            var rulesContainer = $($(this)).siblings('.query-builder-rules-container');
            var rulesContainerID = rulesContainer.attr('id');
            var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilder(rulesContainerID);
            var data = ruleBuilder.data;

            if (launchModal) {
                BLCAdmin.ruleBuilders.setReadableJSONValueOnField(ruleBuilder, data);
                ruleBuilder.removeAllQueryBuilders();
            } else {
                BLCAdmin.ruleBuilders.showOrCreateMainRuleBuilder(rulesContainer, ruleType);
            }

            if (BLCAdmin.entityForm.status) {
                // Set the original value on the rule builder once its been completely initialized
                if (ruleBuilder.builders.length) {
                    ruleBuilder.displayErrors = false;
                    var rules = BLCAdmin.ruleBuilders.getAllRuleBuilderRules(ruleBuilder);
                    delete ruleBuilder.displayErrors;
                    var origVal = JSON.stringify(rules);
                    $(rulesContainer).attr('data-orig-val', JSON.stringify(origVal));
                    BLCAdmin.entityForm.status.removeChangesForId($(rulesContainer).attr('id'));
                }
            }
        });
    });

    /**
     * Post Validation Handler to aggregate and collect all rule builder data on the page before form submission
     * NOTE: this will only collect non-modal rule builders since modals are responsible for setting their own
     * data and have already set it appropriately.
     */
    BLCAdmin.addPostValidationSubmitHandler(function($form) {
        if ($form.find('.query-builder-rules-container').length) {
            for (var i = 0; i < BLCAdmin.ruleBuilders.ruleBuilderCount(); i++) {
                var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByIndex(i);
                if (!ruleBuilder.modal) {
                    BLCAdmin.ruleBuilders.setJSONValueOnField(ruleBuilder);
                }
            }
        }
    });

})($, BLCAdmin);

$(document).ready(function() {

    /**
     * Invoked from an "Add Another Condition" button for an Item Rule Builder
     */
    $('body').on('click', 'div.add-main-item-rule', function() {
        var $container = $(this).parent().parent();
        BLCAdmin.ruleBuilders.addAdditionalQueryBuilder($container, 1);
        return false;
    });

    /**
     * Invoked from any additional Item Rule Builder that has been added to the page
     */
    $('body').on('click', 'button.remove-main-item-rule', function() {
        var $container = $(this).closest('.query-builder-rules-container');
        var builder = $(this).closest('.query-builder-rules');
        BLCAdmin.ruleBuilders.removeAdditionalQueryBuilder($container, builder);
        return false;
    });

    /**
     * Invoked from a Rule Builder with display type : "RADIO"
     * e.g. "Build a Time Rule?" (No)
     */
    $('body').on('change', 'input.clear-rules', function(){
        var $groupContainer = $(this).closest('.field-group');

        var $ruleTitle = $($groupContainer.find('.query-builder-rules-header'));
        var $container = $($groupContainer.find('.query-builder-rules-container').parent());
        $ruleTitle.hide();

        //Also hide the error divs if they are shown
        $container.parent().find('.field-label.error').hide();
        $container.parent().find('.query-builder-rules-container-mvel').hide();
        BLCAdmin.ruleBuilders.hideMainRuleBuilder($container);
    });

    /**
     * Invoked from a Rule Builder with display type : "RADIO"
     */
    $('body').on('change', 'input.add-main-rule, input.add-main-item-rule', function(){
        var $groupContainer = $(this).closest('.field-group');

        var $ruleTitle = $($groupContainer.find('.query-builder-rules-header'));
        var $container = $($groupContainer.find('.query-builder-rules-container'));

        $container.parent().show();

        //if we are going to attempt to re-show something, if the error fields are around then re-show those rather
        //than the rule input
        if ($container.parent().find('.field-label.error').length > 0) {
            $container.parent().find('.field-label.error').show();
            $container.parent().find('.query-builder-rules-container-mvel').show();
        } else {
            var ruleType = $(this).data('ruletype');
            $ruleTitle.show();
            BLCAdmin.ruleBuilders.showOrCreateMainRuleBuilder($container, ruleType);
        }
    });

    /**
     * Invoked from a Rule Builder with display type : "MODAL"
     */
    $('body').on('click', '.launch-modal-rule-builder', function() {
        var $container = $($(this)).siblings('.query-builder-rules-container');
        if (!$container.length) {
            $container = $($(this).parents('.rule-modal-zone')).siblings('.query-builder-rules-container');
        }
        var hiddenId = $($(this)).data('hiddenid');
        var ruleType = $($(this)).data('ruletype');
        var ruleTitleId = $($(this)).data('ruletitleid');

        var $modal = BLCAdmin.getModalSkeleton();
        $modal.find('.modal-header h3').text($('#'+ruleTitleId).text());

        var $modalContainer = $container.clone();
        $modalContainer.attr('id', $modalContainer.attr('id') + '-modal');
        $modalContainer.empty();

        var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilder($modalContainer.attr('id'));
        if (ruleBuilder) {
            var jsonVal = $.parseJSON($('#'+hiddenId).val());
            if (jsonVal.data.length > 0) {
                for (var i=0; i<jsonVal.data.length; i++) {
                    if (ruleType !== BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                        jsonVal.data[i].quantity = null;
                    }
                    BLCAdmin.ruleBuilders.constructQueryBuilder($modalContainer, jsonVal.data[i],
                        ruleBuilder.fields, ruleBuilder);
                }
            } else {
                var qty = null;
                if (ruleType === BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                    qty = 1;
                }

                BLCAdmin.ruleBuilders.constructQueryBuilder($modalContainer, BLCAdmin.ruleBuilders.getEmptyRuleData(qty),
                ruleBuilder.fields, ruleBuilder);
            }
        }

        $modalContainer.show();
        $modal.find('.modal-body').append($modalContainer);
        $modal.find('.modal-footer').append(BLCAdmin.ruleBuilders.getSaveModalRuleLink(hiddenId));

        $modal.find('.modal-body').find('select').each(function(i, el) {
            var el = $(el);
            if (el.hasClass('form-control')) {
                el.removeClass('form-control').blSelectize({delimiter: BLCAdmin.productNameDelimiter});
            }
        });

        BLCAdmin.showElementAsModal($modal, function(){
            var modalRuleBuilder = BLCAdmin.ruleBuilders.getRuleBuilder($modalContainer.attr('id'));
            modalRuleBuilder.removeAllQueryBuilders();
        });

        return false;
    });

    /**
     * Invoked from a Rule Builder with display type : "MODAL"
     * Invoked from the "Set Rule" button on a modal rule builder
     */
    $('body').on('click', 'button.set-modal-rule-builder', function() {
        var hiddenId = $($(this)).data('hiddenid');
        var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByHiddenId(hiddenId);
        BLCAdmin.ruleBuilders.setJSONValueOnField(ruleBuilder);
        BLCAdmin.hideCurrentModal();
        if(BLCAdmin.entityForm.status) {
            BLCAdmin.entityForm.status.updateEntityFormChangeMap(hiddenId,'',ruleBuilder.data);
        }
    });

    /**  **/
    $('body').on('click', 'div.clear-rule-builder', function() {
        var hiddenId = $($(this)).data('hiddenid');
        var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByHiddenId(hiddenId);
        BLCAdmin.ruleBuilders.clearField(ruleBuilder);
        if(BLCAdmin.entityForm.status) {
            BLCAdmin.entityForm.status.updateEntityFormChangeMap(hiddenId,ruleBuilder.data,null);
        }
    });

    /**
     * Invoked when a rule was in an error state and the 'Reset Rule' button was hit
     */
    $('body').on('click', 'a.rule-reset', function(){
        var $errorLabelContainer = $(this).parent();
        var $invalidMvelContainer = $($(this).parent().siblings('.query-builder-rules-container-mvel'));
        var $builderContainer = $($(this).parent().siblings('.query-builder-rules-container'));

        //Remove the error containers
        $errorLabelContainer.remove();
        $invalidMvelContainer.remove();
        //Show the 'real' containers
        $builderContainer.removeClass('rule-data-error');
        $builderContainer.show();

        //clear out the original faulty input
        var $fieldContainer = $($builderContainer.parent());
        var $ruleData = $fieldContainer.find('.rule-builder-data');
        var hiddenInput = $fieldContainer.find('input#' + $ruleData.data('hiddenid'));
        hiddenInput.val('');
        //reset the error as now there isn't one
        BLCAdmin.ruleBuilders.getRuleBuilder($ruleData.data('containerid')).data.error = '';
        return false;
    });

    /**
     * Invoked when a query builder's toggle switch has changed.
     * i.e. (All/Any)
     */
    $('.query-builder-all-any-wrapper').on('change', 'input[type="radio"].toggle', function () {
        if (this.checked) {
            $('input[name="' + this.name + '"].checked').removeClass('checked');
            $(this).addClass('checked');
            $('.toggle-container').addClass('force-update').removeClass('force-update');
        }
    });

    /**
     * As selects are created, add necessary class to convert them to our custom appearance
     */
    $(document).on('DOMNodeInserted', '.query-builder-rules-container .rule-filter-container select, ' +
        '                              .query-builder-rules-container .rule-operator-container select,' +
        '                              .query-builder-rules-container .rule-value-container select', function(e) {

        var el = $(e.target);
        if (el.is('select')) {
            if (el.hasClass('form-control')) {
                el.removeClass('form-control').blSelectize({delimiter: BLCAdmin.productNameDelimiter});
            }
        }
    });
});
