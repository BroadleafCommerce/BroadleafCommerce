/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

    /**
     * An Admin page may contain multiple rule builders of various different types.
     * @type {Array}
     */
    var ruleBuildersArray = [];

    BLCAdmin.ruleBuilders = {

        /**
         * Add a {ruleBuilder} to the ruleBuildersArray
         * A single Admin RuleBuilder may contain more than one QueryBuilder - such as in the case of complex
         * item rules (i.e. org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY)
         * @param hiddenId
         * @param containerId
         * @param fields
         * @param data
         * @returns {{hiddenId: *, containerId: *, fields: *, data: *}}
         */
        addRuleBuilder : function(hiddenId, containerId, fields, data) {
            var ruleBuilder = {
                hiddenId : hiddenId,
                containerId : containerId,
                fields : fields.fields,
                data : data.data,
                builders : []
            };
            ruleBuildersArray.push(ruleBuilder)
            return ruleBuilder;
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
         * Get a {ruleBuilder} from the ruleBuildersArray by containerId
         * @param containerId
         * @returns {*}
         */
        getRuleBuilder : function(containerId) {
            for (var i = 0; i < ruleBuildersArray.length; i++) {
                if (containerId === ruleBuildersArray[i].containerId) {
                    return ruleBuildersArray[i];
                }
            }

            return null;
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
         * Get the number of rule builders that have been initialized on the page
         * @returns {Number}
         */
        ruleBuilderCount : function() {
            return ruleBuildersArray.length;
        },

        /**
         * Rule Builders in the context of the Admin interface may be driven by a question (radio button).
         * This method is intended to show an existing rule or create a new one if one does not exist.
         *
         * @param $container
         * @param typeToCreate - if there is no existing rule, the method will look for the passed in typeToCreate:
         * - "add-main-item-rule" : associated with org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         * - "add-main-rule" : associated with org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_SIMPLE
         */
        showOrCreateMainRuleBuilder : function($container, typeToCreate) {
            var containerId = $container.attr("id");
            var rule = this.getRuleBuilder(containerId);
            if (rule != null) {
                $container.show();

                if ($container.children().children().length == 0) {
                    if (typeToCreate === 'add-main-rule') {
                        this.addAdditionalItemQueryBuilder($container, null, 'add-main-rule');
                    } else if (typeToCreate === 'add-main-item-rule') {
                        this.addAdditionalItemQueryBuilder($container, 1, 'add-main-item-rule');
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
         * Called in order to create a new empty Item Quantity Query Builder used primarily for
         * org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
         * @param $container
         * @param qty
         * @param ruleType
         */
        addAdditionalItemQueryBuilder : function($container, qty, ruleType) {
            var containerId = $container.attr("id");
            var ruleBuilder = this.getRuleBuilder(containerId);
            var emptyData = {
                pk:null,
                quantity: qty,
                condition:'AND',
                rules: []
            }
            this.constructQueryBuilder($container, emptyData, ruleBuilder.fields, ruleBuilder, ruleType);
        },

        /**
         * Called in order to remove an Item Quantity Query Builder.
         * This will also remove the associated builder from the RuleBuilder.builders array
         * and any UI divider elements.
         * @param $container
         * @param builder
         */
        removeAdditionalItemQueryBuilder : function($container, builder) {
            var containerId = $container.attr("id");
            var ruleBuilder = this.getRuleBuilder(containerId);
            var position = 0;
            for (var i=0; i < ruleBuilder.builders.length; i++) {
                if (ruleBuilder.builders[i][0].id === $(builder).attr('id')) {
                    position = i;
                    break;
                }
            }
            if (~position) ruleBuilder.builders.splice(position, 1);
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
        initializeRuleBuilder : function($container, ruleBuilder, ruleType) {
            var container = $container.find('#' + ruleBuilder.containerId);

            for (var i=0; i<ruleBuilder.data.length; i++) {
                this.constructQueryBuilder(container, ruleBuilder.data[i], ruleBuilder.fields, ruleBuilder, ruleType);
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
        constructQueryBuilder : function(container, ruleData, fields, ruleBuilder, ruleType) {
            if (ruleType === 'add-main-item-rule') {
                container.find('.add-and-button').remove();
            }

            var builder = $("<div>", {"class": "query-builder-rules"});
            container.append(builder);
            var addRemoveConditionsLink = ruleBuilder.builders.length >= 1;
            builder.queryBuilder(this.initializeQueryBuilderConfig(ruleData, fields, addRemoveConditionsLink));

            //fix selectize upon adding a new rule
            $(builder).on('afterCreateRuleInput.queryBuilder', function(e, rule) {
                if (rule.filter.plugin == 'selectize') {
                    rule.$el.find('.rule-value-container').css('min-width', '200px')
                        .find('.selectize-control').removeClass('form-control');
                }
            });
            ruleBuilder.builders.push($(builder));

            if (ruleType === 'add-main-item-rule') {
                container.append(this.getAndDivider());
                container.append(this.getAddAnotherConditionLink());
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
         * Initializes the configuration object necessary for the jQuery Query Builder
         * to support the BLC Admin Rule Builder use cases (both RULE_WITH_QUANTITY and RULE_SIMPLE)
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
                    var opRef = fields[i].operators;
                    if (opRef) {
                        fields[i].operators = window[opRef];

                        //initialize selectize plugin
                        if ("blcOperators_Selectize" === opRef) {
                            var sectionKey = fields[i].selectizeSectionKey;

                            fields[i].multiple = true;
                            fields[i].plugin = 'selectize';
                            fields[i].input = function(rule, name){
                                return "<input type='text' class='query-builder-selectize-input' data-hydrate=''>";
                            },
                            fields[i].plugin_config = {
                                maxItems: null,
                                persist: false,
                                valueField: "id",
                                labelField: "label",
                                searchField: "label",
                                loadThrottle: 100,
                                preload: true,
                                hideSelected: true,
                                placeholder: fields[i].label + " +",
                                onInitialize: function () {
                                    var $selectize = this;
                                    $selectize.sectionKey = sectionKey;
                                    this.revertSettings.$children.each(function () {
                                        $.extend($selectize.options[this.value], $(this).data());
                                    });
                                },
                                onLoad: function() {
                                    // Initialize selectize rule data
                                    // after the options have been loaded
                                    // (Values may contain multiple items and are sent back as a single String array)
                                    var $selectize = this;
                                    var dataHydrate = $.parseJSON($selectize.$input.attr("data-hydrate"));
                                    for (var k=0; k<dataHydrate.length; k++) {
                                        if (!isNaN(dataHydrate[k])) {
                                            $selectize.addItem(Number(dataHydrate[k]), false);
                                        }
                                    }
                                },
                                load: function(query, callback) {
                                    var $selectize = this;
                                    var queryData = {};
                                    queryData["name"] = query;

                                    BLC.ajax({
                                        url: BLC.servletContext + "/" + sectionKey + "/selectize",
                                        type: 'GET',
                                        data: queryData
                                    }, function(data) {
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
                                }
                            };
                            fields[i].valueSetter = function(rule, value) {
                                rule.$el.find('.rule-value-container input.query-builder-selectize-input')[0].selectize.setValue(value);
                                rule.$el.find('.rule-value-container input.query-builder-selectize-input').attr('data-hydrate', value);
                            };
                            fields[i].valueGetter = function(rule) {
                                return "["+rule.$el.find('.rule-value-container input.query-builder-selectize-input').val()+"]";
                            }
                        }
                    }

                    var valRef = fields[i].values;
                    if (valRef) {
                        fields[i].values = window[valRef];
                    }
                })();
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
                rules: ruleData.rules.length > 0 ? ruleData : null,
                operators: window['blcOperators']

            };
            return config;
        }

    };

    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.rule-builder-data').each(function(index, element) {
            var $this = $(this),
                hiddenId = $this.data('hiddenid'),
                containerId = $this.data('containerid'),
                fields = $this.data('fields'),
                data = $this.data('data'),
                ruleType =  $(this).data('ruletype'),
                ruleBuilder = BLCAdmin.ruleBuilders.addRuleBuilder(hiddenId, containerId, fields, data);

            BLCAdmin.ruleBuilders.initializeRuleBuilder($this.parent(), ruleBuilder, ruleType);
        });

        $container.find('.rule-builder-required-field').each(function(index, element) {
            var $container = $($(this).next().next());
            var ruleType = $(this).data('ruletype');
            BLCAdmin.ruleBuilders.showOrCreateMainRuleBuilder($container, ruleType);
        });
    });

    BLCAdmin.addPostValidationSubmitHandler(function($form) {
        for (var i = 0; i < BLCAdmin.ruleBuilders.ruleBuilderCount(); i++) {
            var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByIndex(i);
            var hiddenId = ruleBuilder.hiddenId;
            var container = $('#'+ruleBuilder.containerId);
            var builders = ruleBuilder.builders;

            if (builders != null) {
                var collectedData = {};
                collectedData.data = [];
                for (var j = 0; j < builders.length; j++) {
                    var builder = builders[j];
                    var dataDTO = $(builder).queryBuilder('getRules');
                    if (dataDTO.rules) {
                        dataDTO.pk = $(container).find(".rules-group-header-item-pk").val();
                        dataDTO.quantity = $(container).find(".rules-group-header-item-qty").val();
                        for (var k = 0; k < dataDTO.rules.length; k++) {
                            if (Array.isArray(dataDTO.rules[k].value)) {
                                dataDTO.rules[k].value =  JSON.stringify(dataDTO.rules[k].value);
                            }
                        }

                        collectedData.data.push(dataDTO);
                    }
                }

                // There are two scenarios that we should clear out rule data:
                //   1. The containing field-box has a hidden class, which means this field was explicitly hidden as it
                //      likely depends on the value of some other field and is not currently applicable
                //   2. This field is optional and currently set to off

                var explicitlyHidden = $(container.element).closest('.field-box').hasClass('hidden');
                var onOffRadios = $(container.element).parent().find('input[type="radio"]');
                var setToOff = onOffRadios.length < 1 ? false : onOffRadios.filter(function() {
                    return this.id.endsWith('false');
                }).is(':checked');

                if (explicitlyHidden || setToOff) {
                    container.element.find('div.query-builder-rules').remove();
                }

                //only send over the error if it hasn't been explicitly turned off
                if (ruleBuilder.data.error != null && !setToOff) {
                    collectedData.error = ruleBuilder.data.error;
                }

                $("#"+hiddenId).val(JSON.stringify(collectedData));

            }
        }
    });

})($, BLCAdmin);

$(document).ready(function() {

    $('body').on('click', 'div.add-main-item-rule', function() {
        var $container = $(this).parent().parent();
        BLCAdmin.ruleBuilders.addAdditionalItemQueryBuilder($container, 1, 'add-main-item-rule');
        return false;
    });

    $('body').on('click', 'button.remove-main-item-rule', function() {
        var $container = $(this).closest('.query-builder-rules-container');
        var builder = $(this).closest('.query-builder-rules');
        BLCAdmin.ruleBuilders.removeAdditionalItemQueryBuilder($container, builder);
        return false;
    });

    $('body').on('change', 'input.clear-rules', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());
        $ruleTitle.hide();

        //Also hide the error divs if they are shown
        $container.parent().find('.field-label.error').hide();
        $container.parent().find('.query-builder-rules-container-mvel').hide();
        BLCAdmin.ruleBuilders.hideMainRuleBuilder($container, 'add-main-rule');
    });

    $('body').on('change', 'input.add-main-rule, input.add-main-item-rule', function(){
        var $ruleTitle = $($(this).closest('.rule-builder-checkbox').next());
        var $container = $($ruleTitle.next());

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
        BLCAdmin.ruleBuilders.getRuleBuilder($ruleData.data('containerid')).data.error = '';
        return false;
    });

    $('.query-builder-all-any-wrapper').on('change', 'input[type="radio"].toggle', function () {
        if (this.checked) {
            $('input[name="' + this.name + '"].checked').removeClass('checked');
            $(this).addClass('checked');
            $('.toggle-container').addClass('force-update').removeClass('force-update');
        }
    });

});
