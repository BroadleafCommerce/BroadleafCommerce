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
 * Broadleaf Commerce Filter Builder
 * This component initializes any Filter Builder JSON data on the page and converts
 * it into a jQuery Query Builder Component
 *
 * @author: jfleschler
 */
(function($, BLCAdmin) {

    /**
     * An Admin page may contain multiple filter builders of various different types.
     * @type {Array}
     */
    var filterBuildersArray = [];

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

    BLCAdmin.filterBuilders = {

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
        addFilterBuilder : function(hiddenId, containerId, fields, data, modal) {
            var filterBuilder = {
                modal: modal,
                hiddenId : hiddenId,
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

                getOperatorLabelByOperatorType : function(operatorType)  {
                    return this.builders[0].queryBuilder('getOperatorLabelByType', operatorType);
                },

                addQueryBuilder : function(builder) {
                    var exists = false;
                    for (var i=0; i < filterBuilder.builders.length; i++) {
                        if (filterBuilder.builders[i][0].id === $(builder).attr('id')) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        filterBuilder.builders.push(builder);
                    }
                },

                removeQueryBuilder : function(builder) {
                    var position = 0;
                    for (var i=0; i < filterBuilder.builders.length; i++) {
                        if (filterBuilder.builders[i][0].id === $(builder).attr('id')) {
                            position = i;
                            break;
                        }
                    }
                    if (~position) filterBuilder.builders.splice(position, 1);
                },

                removeAllQueryBuilders : function() {
                    filterBuilder.builders = [];
                }

            };

            filterBuildersArray.push(filterBuilder)
            return filterBuilder;
        },

        /**
         * Called in order to create a new empty Query Builder
         * @param $container
         */
        addAdditionalQueryBuilder : function($container) {
            var containerId = $container.attr("id");
            var filterBuilder = this.getFilterBuilder(containerId);
            this.constructQueryBuilder($container, this.getEmptyFilterData(), filterBuilder.fields, filterBuilder);
        },

        /**
         * Called in order to remove an Item Quantity Query Builder.
         * This will also remove the associated builder from the FilterBuilder.builders array
         * and any UI divider elements.
         * @param $container
         * @param builder
         */
        removeAdditionalQueryBuilder : function($container, builder) {
            var containerId = $container.attr("id");
            var filterBuilder = this.getFilterBuilder(containerId);
            filterBuilder.removeQueryBuilder($(builder));
            $(builder).remove();
        },

        /**
         * Called on initial page load to initialize all filter builders on the page
         * given the passed in container
         *
         * @param $container
         * @param ruleBuilder
         */
        initializeFilterBuilder : function($container, filterBuilder) {
            var container = $container.find('#' + filterBuilder.containerId);

            for (var i=0; i<filterBuilder.data.length; i++) {
                this.constructQueryBuilder(container, filterBuilder.data[i], filterBuilder.fields, filterBuilder);
            }

        },

        /**
         * The main function called to construct the Query Builder associated with the
         * passed in Container and FilterBuilder
         *
         * @param container
         * @param ruleData
         * @param fields
         * @param filterBuilder
         */
        constructQueryBuilder : function(container, ruleData, fields, filterBuilder) {

            var builder = $("<div>", {"class": "query-builder-rules"});
            container.append(builder);
            builder.queryBuilder(this.initializeQueryBuilderConfig(ruleData, fields, false));

            //run any post-construct handlers
            //BLCAdmin.ruleBuilders.runPostConstructQueryBuilderFieldHandler(builder);

            filterBuilder.addQueryBuilder($(builder));

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
         * Constructs an empty filter data object
         * @returns {{pk: null, condition: string, rules: Array}}
         */
        getEmptyFilterData : function() {
            var emptyData = {
                pk: null,
                condition:'AND',
                rules: []
            }
            return emptyData;
        },

        /**
         * Get a {filterBuilder} from the filterBuildersArray by hiddenId
         * @param hiddenId
         * @returns {*}
         */
        getFilterBuilderByHiddenId : function(hiddenId) {
            for (var i = 0; i < filterBuildersArray.length; i++) {
                if (hiddenId === filterBuildersArray[i].hiddenId) {
                    return filterBuildersArray[i];
                }
            }

            return null;
        },

        /**
         * Set the appropriate JSON value on the "hiddenId" input element for the corresponding rule builder.
         *
         * Performs the appropriate data transformations in order to properly bind with the backing
         * org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper
         *
         * NOTE: this will not collect and set every rule builder passed in. It determines whether or not
         * to collect the data based on its state (i.e. if there is a RADIO and it is off, it will not collect data)
         *
         * @param ruleBuilder
         */
        setJSONValueOnField : function (ruleBuilder) {
            var hiddenId = ruleBuilder.hiddenId;
            var container = $('#'+ruleBuilder.containerId);
            var builders = ruleBuilder.builders;

            if (builders != null && ruleBuilder) {
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
                    collectedData.data = [];
                }

                //only send over the error if it hasn't been explicitly turned off
                if (ruleBuilder.data.error != null && !setToOff) {
                    collectedData.error = ruleBuilder.data.error;
                }

                $("#"+hiddenId).val(JSON.stringify(collectedData));
                this.setReadableJSONValueOnField(ruleBuilder, collectedData.data);

            }
        },

        /**
         * Returns a "Apply Filters" button element intended to be used on a filter builder modal
         * @param hiddenId
         * @returns {*|jQuery|HTMLElement}
         */
        getSaveModalFilterLink : function(hiddenId) {
            var saveBtn = $("<button>", {'class' : 'set-modal-filter-builder button primary',
                'text' : 'Apply Filters',
                'data-hiddenId' : hiddenId});
            return saveBtn;
        },

        /**
         * Get a {filterBuilder} from the filterBuildersArray by containerId
         * @param containerId
         * @returns {*}
         */
        getFilterBuilder : function(containerId) {
            if (containerId.indexOf('-modal', containerId.length - '-modal'.length) !== -1) {
                containerId = containerId.slice(0, -('-modal'.length));
            }

            for (var i = 0; i < filterBuildersArray.length; i++) {
                if (containerId === filterBuildersArray[i].containerId) {
                    return filterBuildersArray[i];
                }
            }

            return null;
        },

        /**
         * Gets the query variable value from the url for a specific variable
         * @param variable
         * @returns {*}
         */
        getQueryVariable: function(variable) {
            var query = window.location.search.substring(1);
            var vars = query.split('&');
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split('=');
                if (decodeURIComponent(pair[0]) == variable) {
                    return decodeURIComponent(pair[1]);
                }
            }
            return null;
        },
        /**
         * Initializes the configuration object necessary for the jQuery Query Builder
         * to support the BLC Admin Rule Builder use cases (both RULE_WITH_QUANTITY and RULE_SIMPLE)
         * by passing in the fields (filters) and ruleData (rules) for the passed in rule builder
         *
         * Plugin configurations is also performed in order to support third party components
         * such as Selectize
         *
         * @param filterData
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
        initializeQueryBuilderConfig : function(filterData, fields, addRemoveConditionsLink) {
            //initialize operators and values on the fields
            for (var i=0; i<fields.length; i++){
                (function(){

                    //run any pre-initialization handlers for this field
                    BLCAdmin.filterBuilders.runPreInitQueryBuilderFieldHandler(fields[i]);

                    var opRef = fields[i].operators;
                    if (opRef && typeof opRef === 'string') {
                        fields[i].operators = window[opRef];
                    }

                    if (opRef && opRef.indexOf('Date') >= 0) {
                        fields[i].type = 'date';
                        fields[i].plugin = 'datetimepicker';
                        fields[i].plugin_config = {
                            format: "Y.m.d G:i:s"
                        };
                    }

                    if (opRef && opRef.indexOf('Enumeration') >= 0) {
                        fields[i].input = 'select';
                        fields[i].type = 'string';
                        fields[i].values = $.parseJSON(fields[i].values);
                    }

                    var valRef = fields[i].values;
                    if (valRef && typeof valRef === 'string') {
                        fields[i].values = window[valRef];
                    }


                    // check for existing rules in the url
                    var queryString = BLCAdmin.filterBuilders.getQueryVariable(fields[i].id);

                    if (queryString != null) {
                        var numInputs = 1;
                        // is this a 'BETWEEN' filter?
                        if (queryString.indexOf('|') > 0) {
                            numInputs = 2;
                            queryString = queryString.split('|');
                        }

                        // there is a variable, check if its already in a rule
                        var found = false;
                        for (var r = 0; r < filterData.rules.length; r++) {
                            if (filterData.rules[r].id === fields[i].id) {
                                found = true;

                                filterData.rules[r].value = queryString;
                                filterData.rules[r].operator = numInputs == 1 ? fields[i].operators[0] : fields[i].operators[1];
                                break;
                            }
                        }
                        // check if the rule existed, if not create it
                        if (!found) {
                            var newRule = {};
                            newRule.field = fields[i].id;
                            newRule.id = fields[i].id;
                            newRule.input = fields[i].input != null ? fields[i].input : 'text';
                            newRule.operator = numInputs == 1 ? fields[i].operators[0] : fields[i].operators[1];
                            newRule.type = fields[i].type;
                            newRule.value = queryString;

                            filterData.rules.push(newRule);
                        }
                    }

                })();
            }

            var removeBtn = addRemoveConditionsLink? this.getRemoveConditionLink() : null;

            var config = {
                plugins: {'blc-admin-filter-builder': {
                    pk:filterData.pk,
                    removeConditionsLink: removeBtn}},
                icons: {'add_rule':'fa fa-plus-circle',
                    'remove_rule':'fa fa-minus-circle'},
                allow_groups: false,
                inputs_separator: "<span class='rule-val-sep'>,</span>",
                filters: fields,
                rules: filterData.rules && filterData.rules.length > 0 ? filterData : null,
                operators: window['blcOperators']

            };
            return config;
        },

        /** Set the readable translation of the rule corresponding to the rule builder **/
        setReadableJSONValueOnField : function (ruleBuilder, data) {
            var hiddenId = ruleBuilder.hiddenId;
            var readableElement = $('#'+hiddenId+'-readable');

            //If the element exists set the value
            if (readableElement) {
                //clear out existing content
                $(readableElement).empty();

                // If no data, set "No rules applied"
                if (data == null || data.length == 0) {
                    var noRules = $("<span>", {'class': 'readable-no-rule', 'text' : 'No rules applied yet.'});
                    $(readableElement).append(noRules);
                    // else fill in data
                } else {

                    for (var i = 0; i < data.length; i++) {
                        var dataDTO = data[i];
                        var prefix = $("<span>", {
                            'class': 'readable-rule-prefix',
                            'text': dataDTO.quantity ?
                            'Match ' + dataDTO.quantity + ' items where: ' :
                                'Rule where: '
                        });

                        $(readableElement).append(prefix);
                        var condition = dataDTO.condition;
                        for (var k = 0; k < dataDTO.rules.length; k++) {
                            var ruleDTO = dataDTO.rules[k];

                            var name = $("<span>", {
                                'class': 'readable-rule-field',
                                'text': ruleBuilder.getFieldLabelById(ruleDTO.id)
                            });
                            var operator = $("<span>", {
                                'class': 'readable-rule-operator',
                                'text': ruleBuilder.getOperatorLabelByOperatorType(ruleDTO.operator)
                            });
                            var value = $("<span>", {
                                'class': 'readable-rule-value',
                                'text': ruleDTO.value
                            });

                            $(readableElement).append(name);
                            $(readableElement).append(operator);
                            $(readableElement).append(value);

                            if (k != dataDTO.rules.length - 1) {
                                var additional = $("<span>", {'text': condition});
                                $(readableElement).append(additional);
                            }
                        }

                        if (i != data.length - 1) {
                            var and = $("<span>", {'text': 'and'});
                            $(readableElement).append(and);
                        }
                    }
                }
            }

        }
    };

    /**
     * Initialization handler to find all filter builders on the page and initialize them with
     * the appropriate fields and data (as specified by the container)
     */
    BLCAdmin.addInitializationHandler(function($container) {
        BLCAdmin.addExcludedSelectizeSelector('.query-builder-filters-container *');

        //Initialize all filter builders on the page
        $container.find('.filter-builder-data').each(function(index, element) {
            var $this = $(this),
                hiddenId = $this.data('hiddenid'),
                containerId = $this.data('containerid'),
                fields = $this.data('fields'),
                data = $this.data('data'),
                modal = $this.data('modal'),
                filterBuilder = BLCAdmin.filterBuilders.addFilterBuilder(hiddenId, containerId, fields, data, modal);

            //Create QueryBuilder Instances for all rule builders on the page
            BLCAdmin.filterBuilders.initializeFilterBuilder($this.parent(), filterBuilder);
        });

        ////Once all the query builders have been initialized - show or render the component based on its display type
        //$container.find('.filter-builder-required-field').each(function(index, element) {
        //    var filtersContainer = $($(this)).siblings('.query-builder-rules-container');
        //    BLCAdmin.filterBuilders.showOrCreateMainRuleBuilder(filtersContainer);
        //});
    });
})($, BLCAdmin);

$(document).ready(function() {

    /**
     * Invoked from a Filter Builder with display type : "MODAL"
     * Invoked from the "Apply Filters" button on a modal filter builder
     */
    $('body').on('click', 'button.set-modal-filter-builder', function () {
        var hiddenId = $($(this)).data('hiddenid');
        var filterBuilder = BLCAdmin.filterBuilders.getFilterBuilderByHiddenId(hiddenId);

        // remove any empty rules
        for (var i = 0; i < filterBuilder.builders.length; i++) {
            var builder = filterBuilder.builders[i];

            $(builder).find('.rule-operator-container').each(function(i, e) {
               if (e.innerHTML == '') {
                   $(e).parent().find('button').click()
               }
            });
        }

        BLCAdmin.filterBuilders.setJSONValueOnField(filterBuilder);
        BLCAdmin.hideCurrentModal();

        // Convert JSON to request params
        var filters = JSON.parse($('#' + hiddenId).val());
        var inputs = [];

        if (filters.data.length > 0) {
            var rules = filters.data[0].rules;
            $(rules).each(function (i, e) {
                var input = {'name': e.id, 'value': formatInput(e.value, e.operator)};
                inputs.push(input);
            });
        }

        var $tbody = $('body').find('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');
        //BLCAdmin.listGrid.showLoadingSpinner($tbody, $tbody.closest('.mCustomScrollBox').position().top + 3);
        BLC.ajax({
            url: $(this).closest('.filter-fields').data('action'),
            type: "GET",
            data: $.param(inputs)
        }, function(data) {
            if ($tbody.data('listgridtype') == 'main') {

                if (inputs.length) {
                    $(inputs).each(function (index, input) {
                        BLCAdmin.history.replaceUrlParameter(input.name, input.value);
                    });
                } else {
                    $(BLCAdmin.history.getUrlParameters()).each(function(index, input) {
                        for (var key in input) {
                            BLCAdmin.history.replaceUrlParameter(key, null);
                        }
                    });
                }
            }
            //BLCAdmin.listGrid.hideLoadingSpinner($tbody);
            BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.listgrid-header-wrapper'), null, { isRefresh : false });
        });
    });

    /**
     * Invoked from the "Filter" button on the listgrid
     */
    $(document).on('click', '.filter-button', function (e) {
        // show the filter modal
        var $container = $($(this)).siblings('.query-builder-filters-container');
        var hiddenId = $($(this)).data('hiddenid');

        var $modalContainer = $container.clone();
        $modalContainer.attr('id', $modalContainer.attr('id') + '-modal');
        $modalContainer.empty();

        var filterBuilder = BLCAdmin.filterBuilders.getFilterBuilder($modalContainer.attr('id'));

        if (filterBuilder) {
            var jsonVal = $.parseJSON($('#'+hiddenId).val());
            if (jsonVal.data.length > 0) {
                for (var i=0; i<jsonVal.data.length; i++) {
                    for (var j = 0; j < jsonVal.data[i].rules.length; j++) {
                        if (jsonVal.data[i].rules[j].value[0] === '[') {
                            jsonVal.data[i].rules[j].value = $.parseJSON(jsonVal.data[i].rules[j].value);
                        }
                    }
                    BLCAdmin.filterBuilders.constructQueryBuilder($modalContainer, jsonVal.data[i], filterBuilder.fields, filterBuilder);
                }
            } else {
                BLCAdmin.filterBuilders.constructQueryBuilder($modalContainer, BLCAdmin.filterBuilders.getEmptyFilterData(),
                    filterBuilder.fields, filterBuilder);
            }
        }
        $modalContainer.show();

        var $modal = BLCAdmin.getModalSkeleton();
        $modal.find('.modal-body').append($modalContainer);
        $modal.find('.modal-body').css('overflow', 'visible');
        $modal.find('.modal-footer').append(BLCAdmin.filterBuilders.getSaveModalFilterLink(hiddenId));

        $modal.find('.modal-body').find('select').each(function(i, el) {
            var el = $(el);
            if (el.hasClass('form-control')) {
                el.removeClass('form-control').selectize();
            }

            el.parent().parent().find('div.rule-filter-container > div > div.selectize-input').width("244px");
            el.parent().parent().find('div.rule-operator-container > div > div.selectize-input').width("122px");
            el.parent().parent().find('div.rule-value-container > div > div.selectize-input').width("245px");
            el.parent().parent().find('div.rule-value-container').css("display", "inline-block");
        });

        BLCAdmin.showElementAsModal($modal, function() {
            var modalFilterBuilder = BLCAdmin.filterBuilders.getFilterBuilder($modalContainer.attr('id'));
            modalFilterBuilder.removeAllQueryBuilders();
        });
    });

    /**
     * As selects are created, add necessary class to convert them to our custom appearance
     */
    $(document).on('DOMNodeInserted', '.query-builder-filters-container .rule-filter-container select, ' +
                                      '.query-builder-filters-container .rule-operator-container select, ' +
                                      '.query-builder-filters-container .rule-value-container select', function(e) {
        var el = $(e.target);
        if (el.is('select')) {
            if (el.hasClass('form-control')) {
                el.removeClass('form-control').selectize();
            }

            el.parent().parent().find('div.rule-filter-container > div > div.selectize-input').width("222px");
            el.parent().parent().find('div.rule-operator-container > div > div.selectize-input').width("100px");
            el.parent().parent().find('div.rule-value-container > div > div.selectize-input').width("223px");
        }
    });

    /**
     * Formats the input from the filter builder for saving
     * @param input
     * @param operator
     * @returns {*}
     */
    function formatInput(input, operator) {

        // check if input is a date
        var date = new Date(input);
        if (date != 'Invalid Date') {
            //mm/dd/yy HH:mm
            input = BLCAdmin.dates.getServerDate(input);
        }

        switch(operator) {
            case "IS_NULL":
                input = '\'\'';
                break;
            case "BETWEEN":
                var array = JSON.parse(input);
                input = array[0] + '|' + array[1];
                break;
            default:
        }
        return input;
    }

});
