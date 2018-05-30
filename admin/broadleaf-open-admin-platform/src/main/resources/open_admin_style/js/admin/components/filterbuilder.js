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

    /**
     * An Admin page may need to perform some function, using the returned page data, after a filter has been applied.
     * @type {Array}
     */
    var postApplyFilterHandlers = [];

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
         * Handlers designed to execute on the data returned after applying and making a filter request
         */
        addPostApplyFilterHandler : function(fn) {
            postApplyFilterHandlers.push(fn);
        },

        runPostApplyFilterHandlers : function(data) {
            for (var i = 0; i < postApplyFilterHandlers.length; i++) {
                postApplyFilterHandlers[i](data);
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

            filterBuildersArray.push(filterBuilder);
            return filterBuilder;
        },

        /**
         * Remove FilterBuilder from the filterBuildersArray using the containerId
         *
         * @param containerId - the ID of the container <div> element where the query builders are rendered
         */
        removeFilterBuilderByContainerId : function(containerId) {
            filterBuildersArray.splice(filterBuildersArray.indexOf(this.getFilterBuilder(containerId)), 1);
        },

        /**
         * Remove FilterBuilder from the filterBuildersArray using the hiddenId
         *
         * @param hiddenId - the ID of the hidden JSON input element where the constructed value is stored
         */
        removeFilterBuilderByHiddenId : function(hiddenId) {
            filterBuildersArray.splice(filterBuildersArray.indexOf(this.getFilterBuilderByHiddenId(hiddenId)), 1);
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
            };
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
        getCloseModalFilterLink : function(hiddenId) {
            var saveBtn = $("<button>", {'class' : 'set-modal-filter-builder button secondary',
                'text' : 'Close'
            });
            return saveBtn;
        },

        /**
         * Returns a "No filters applied" text element
         * @returns {*|jQuery|HTMLElement}
         */
        getNoFilterText: function() {
            var noFiltersText = $("<li>", {
                html: "No filters applied yet.  Click Add New Filter",
                'class': "rule-container no-filters"
            });
            return noFiltersText;
        },

        /**
         * Returns an "edit" button element
         * @param el
         * @returns {*|jQuery|HTMLElement}
         */
        getEditButton: function(el) {
            var editButton = $("<button>", {
                'class' : 'edit-row',
                'html' : '<i class="fa fa-pencil"></i>'
            });
            el.find('.rule-actions').append(editButton);
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
         * A custom pre-init query builder field handler to modify the filters object
         * in order to support a Boolean Radio button widget in the Query Builder.
         * @param field
         */
        initBooleanRadioPreInitFieldHandler : function(field) {
            var opRef = field.operators;

            if (opRef && typeof opRef === 'string' && ("blcFilterOperators_Boolean" === opRef)) {
                field.input = 'radio';
                field.values = {
                    'true': 'true',
                    'false': 'false'
                }
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

            function updateFilterHeightBasedOnSelectizeHeight($selectize) {
                var $selectizeControl = $selectize.$input.siblings('.selectize-control');
                var inputHeight = $selectizeControl.find('.selectize-input').outerHeight();
                $selectize.$input.closest('.rule-value-container').height(inputHeight);
            }

            if (opRef && typeof opRef === 'string' && "blcFilterOperators_Selectize" === opRef) {
                var sectionKey = field.selectizeSectionKey;

                field.multiple = true;
                field.plugin = 'selectize';
                field.input = function(rule, name){
                    return "<input type='text' class='query-builder-selectize-input' data-hydrate=''>";
                },
                    field.plugin_config = {
                        maxItems: null,
                        persist: false,
                        valueField: "id",
                        labelField: "label",
                        searchField: "label",
                        loadThrottle: 100,
                        preload: true,
                        hideSelected: true,
                        unique: true,
                        placeholder: field.label + " +",
                        dropdownParent: 'body',
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
                            var data = $selectize.$input.attr("data-hydrate");
                            var dataHydrate = BLCAdmin.stringToArray(data);
                            for (var k=0; k<dataHydrate.length; k++) {
                                if (!isNaN(dataHydrate[k])) {
                                    $selectize.addItem(Number(dataHydrate[k]), false);
                                    var name = $selectize.getItem(Number(dataHydrate[k])).text();
                                    var $readonly = this.$input.parent().parent().find('.read-only');
                                    if ($readonly.html() !== undefined) {
                                        $readonly.html($readonly.html().replace(Number(dataHydrate[k]), name));
                                    }
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
                                if ($selectize.$wrapper.is(':visible') && data.options.length) {
                                    $selectize.open();
                                }
                                callback(data);
                            });
                        },
                        onItemAdd: function(value, $item) {
                            updateFilterHeightBasedOnSelectizeHeight(this);
                        },
                        onItemRemove: function(value) {
                            updateFilterHeightBasedOnSelectizeHeight(this);
                        }
                    };
                field.valueSetter = function(rule, value) {
                    rule.$el.find('.rule-value-container input.query-builder-selectize-input')[0].selectize.setValue(value);
                    rule.$el.find('.rule-value-container input.query-builder-selectize-input').attr('data-hydrate', value);
                };
                field.valueGetter = function(rule) {
                    return "["+rule.$el.find('.rule-value-container input.query-builder-selectize-input').val()+"]";
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

                    fields[i].unique = true;

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

                    if (opRef && opRef.indexOf('Numeric') >= 0) {
                        fields[i].type = 'double';
                    }

                    var valRef = fields[i].values;
                    if (valRef && typeof valRef === 'string') {
                        fields[i].values = window[valRef];
                    }
                })();
            }

            var removeBtn = addRemoveConditionsLink? this.getRemoveConditionLink() : null;

            var config = {
                plugins: {
                    //'unique-filter': null,
                    'blc-admin-filter-builder': {
                        pk:filterData.pk,
                        removeConditionsLink: removeBtn
                    }
                },
                icons: {
                    'add_rule':'fa fa-plus-circle',
                    'remove_rule':'fa fa-times'
                },
                allow_groups: false,
                inputs_separator: "<span class='rule-val-sep'>and</span>",
                filters: fields,
                rules: filterData.rules && filterData.rules.length > 0 ? filterData : null,
                operators: window['blcOperators'],
                select_placeholder: '~ Choose Attribute'
            };
            return config;
        },

        applyFilters : function(hiddenId) {
            if (hiddenId == undefined) {
                hiddenId = $("#hidden-id").data('hiddenid');
            }
            var filterBuilder = BLCAdmin.filterBuilders.getFilterBuilderByHiddenId(hiddenId);

            var $filterButton = $($('.filter-button[data-hiddenid=' + hiddenId + ']')[0]);
            var $tbody = $('.list-grid-table[data-hiddenid=' + hiddenId + ']:not([id$=-header])');
            var $filterFields = $tbody.closest('.listgrid-body-wrapper').prev().find('.filter-fields');

            // if the listgrid found is of type 'asset_grid' we want to find the one thats 'asset_grid_folder'
            if (!$tbody.length || $tbody.data('listgridtype') == 'asset_grid') {
                $tbody = $('.list-grid-table[data-listgridtype=asset_grid_folder]:not([id$=-header])');
            }

            // couldn't find filter builder so exit
            if (!filterBuilder) {
                return;
            }

            BLCAdmin.filterBuilders.setJSONValueOnField(filterBuilder);

            // Convert JSON to request params
            var filters = JSON.parse($('#' + hiddenId).val());
            var inputs = BLCAdmin.filterBuilders.getFiltersAsURLParams(hiddenId);

            if (filters.data.length <= 0) {
                var mainContent = $filterButton.closest('.main-content');
                if (mainContent.length) {
                    $(mainContent).find('.sticky-container .filter-text').hide();
                }
            }

            BLC.ajax({
                url: $($filterFields[0]).data('action'),
                type: "GET",
                data: inputs
            }, function(data) {
                if ($tbody.data('listgridtype') == 'main') {
                    // clear all url params
                    $(BLCAdmin.history.getUrlParameters()).each(function(index, input) {
                        for (var key in input) {
                            BLCAdmin.history.replaceUrlParameter(key, null);
                        }
                    });
                    // add back active filters
                    if (inputs.length) {
                        for (var i in inputs) {
                            var input = inputs[i];
                            BLCAdmin.history.replaceUrlParameter(input.name, input.value);
                        }
                    }
                }

                if ($tbody.data('listgridtype') == 'asset_grid_folder') {
                    var $assetGrid = data.find('.asset-grid');
                    var assetGrid = $filterButton.closest('.content-yield').find('.asset-grid').html($assetGrid.html());
                    var $assetListGrid = data.find('.asset-listgrid');
                    var assetListgrid = $filterButton.closest('.content-yield').find('.asset-listgrid').html($assetListGrid.html());

                    var container = assetGrid.closest('.asset-listgrid-container');

                    if (filters.data.length == 0) {
                        // show all breadcrumbs
                        container.find('.breadcrumb-wrapper').show();

                        var parentId = container.find('.select-column').data('parentid');

                        // reload the most recent folder
                        BLCAdmin.assetGrid.loadFolder(parentId, container.find('.select-column'));
                    } else {
                        // hide the folder listgrid
                        container.find('.select-column').hide();

                        // hide all breadcrumbs
                        container.find('.breadcrumb-wrapper').hide();

                        container.find('.asset-title').html("Showing filtered results").show();
                    }

                    BLCAdmin.assetGrid.initialize($(assetGrid).find('.asset-grid-container'));
                    BLCAdmin.listGrid.initialize($(assetListgrid));
                } else {
                    BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.listgrid-header-wrapper'), null, {isRefresh: false});
                }

                BLCAdmin.filterBuilders.runPostApplyFilterHandlers(data);
            });

            $('.error-container').hide();
        },

        clearFilters : function(hiddenId) {
            // clear the filters from the filterbuilder
            var jsonVal = JSON.stringify({ 'data' : [] });
            $('#' + hiddenId).val(jsonVal);

            var $tbody = $('.list-grid-table[data-hiddenid=' + hiddenId + ']:not([id$=-header])');
            if ($tbody.data('listgridtype') == 'main') {
                // remove query string from URL
                $(BLCAdmin.history.getUrlParameters()).each(function (index, input) {
                    for (var key in input) {
                        BLCAdmin.history.replaceUrlParameter(key, null);
                    }
                });
            }
        },

        /**
         * Formats the input from the filter builder for saving
         * @param input
         * @param operator
         * @returns {*}
         */
        formatInput : function(input, operator) {
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
                case "COLLECTION_IN":
                case "COLLECTION_NOT_IN":
                    var array = JSON.parse(input);
                    input = '';
                    for (var i = 0; i < array.length; i++) {
                        input += array[i] + '|'
                    }
                    input = input.substring(0, input.length - 1);
                    break;
                default:
            }
            return input;
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
        },

        addExistingFilters: function(filterBuilder) {
            var hiddenId = filterBuilder.hiddenId;
            // check if there are any existing filters on the page
            var filterData = BLCAdmin.filterBuilders.getEmptyFilterData();
            for (var i=0; i < filterBuilder.fields.length; i++) {
                var field = jQuery.extend({}, filterBuilder.fields[i]);
                if (typeof field.operators === 'string' ) {
                    field.operators = window[field.operators];
                }

                // check for existing rules in the url
                var queryString = BLCAdmin.filterBuilders.getQueryVariable(field.id);

                if (queryString != null) {
                    var numInputs = 1;
                    // is this a 'BETWEEN' filter?
                    if (queryString.indexOf('|') > 0) {
                        if (field.operators.length > 1) {
                            numInputs = 2;
                        }
                        queryString = queryString.split('|');
                    }

                    var newRule = {};
                    newRule.field = field.id;
                    newRule.id = field.id;
                    newRule.input = field.input != null ? field.input : 'text';
                    newRule.operator = numInputs == 1 ? field.operators[0] : field.operators[1];
                    newRule.type = field.type;
                    newRule.value = queryString;

                    filterData.rules.push(newRule);
                }
            }

            // set the rules to the filterbuilder
            var jsonVal = JSON.stringify({ 'data' : [filterData] });
            $('#' + hiddenId).val(jsonVal);

            // if there are active filters, change the filter button to "Edit"
            var $filterButton = $($('.filter-button[data-hiddenid=' + hiddenId + ']')[0]);
            if (filterData.rules.length > 0) {
                if (!$filterButton.closest('.button-group').length) {

                    $filterButton.text(BLCAdmin.messages.editFilter);
                    $filterButton.removeClass('disabled').removeAttr('disabled');

                    var clearButton = $('<button>', {
                        'html': '<i class="fa fa-times" />',
                        'class': 'button dropdown-toggle clear-filters'
                    });

                    var buttonGroup = $('<div>', {
                        'class': 'button-group'
                    });

                    buttonGroup.append($filterButton.clone());
                    buttonGroup.append(clearButton);
                    $(buttonGroup).insertBefore($filterButton.closest('.filter-info:visible').find('.filter-builder-data'));

                    $filterButton.remove();
                }
                $filterButton.closest('.main-content').find('.sticky-container .filter-text').show();
            } else {
                if ($filterButton.text() !== BLCAdmin.messages.filter) {
                    // change "edit filter" button back to "filter"
                    $filterButton.text(BLCAdmin.messages.filter);
                    $filterButton.insertBefore($filterButton.parent());
                    $filterButton.siblings('.button-group:visible').remove();
                    $filterButton.closest('.main-content').find('.sticky-container .filter-text').hide();
                }
            }
        },

        getListGridFiltersAsURLParams: function($listGridContainer) {
            var $filterButton = $listGridContainer.find('.filter-button');
            var hiddenId = $filterButton.data('hiddenid');

            return BLCAdmin.filterBuilders.getFiltersAsURLParams(hiddenId);
        },

        getFiltersAsURLParams: function(hiddenId) {
            // Convert JSON to request params
            var filters = JSON.parse($('#' + hiddenId).val());
            var inputs = [];

            if (filters.data.length > 0) {
                var rules = filters.data[0].rules;
                $(rules).each(function (i, e) {
                    if (e.value != '[]') {
                        var input = {'name': e.id, 'value': BLCAdmin.filterBuilders.formatInput(e.value, e.operator)};
                        inputs.push(input);
                    }
                });
            }

            return inputs;
        }
    };

    /**
     * Initialization handler to find all filter builders on the page and initialize them with
     * the appropriate fields and data (as specified by the container)
     */
    BLCAdmin.addInitializationHandler(function($container) {
        //Add default pre-init and post-construct handlers (e.g. selectize)
        BLCAdmin.filterBuilders.addPreInitQueryBuilderFieldHandler(BLCAdmin.filterBuilders.initBooleanRadioPreInitFieldHandler);
        BLCAdmin.filterBuilders.addPreInitQueryBuilderFieldHandler(BLCAdmin.filterBuilders.initSelectizePreInitFieldHandler);
        BLCAdmin.filterBuilders.addPostConstructQueryBuilderFieldHandler(BLCAdmin.filterBuilders.initSelectizePostConstructFieldHandler);

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

            BLCAdmin.filterBuilders.addExistingFilters(filterBuilder);
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
     * Invoked from the "Apply" button on an individual row
     */
    $('body').on('click', 'button.filter-apply-button', function () {
        // apply the filters
        BLCAdmin.filterBuilders.applyFilters();

        // mark this rule as read-only
        var el = $(this).parent().parent().parent();

        var filterText = el.find('.rule-filter-container .selectize-input .item').text();
        var operatorText = el.find('.rule-operator-container .selectize-input .item').text();
        var valueText = el.find('.rule-value-container .selectize-input .item');

        var valueArray = [];
        $.each(valueText, function(i, val) {
            valueArray.push($(val).text());
        });
        valueText = valueArray.join(", ");

        if (valueText == '') {
            valueText = el.find('.rule-value-container input');
            $.each(valueText, function(i, val) {
                if ($(val).attr('type') === 'radio') {
                    if ($(val).is(':checked')) {
                        valueArray.push($(val).val());
                    }
                } else {
                    valueArray.push($(val).val());
                }
            });
        }
        valueText = valueArray.join(" and ");

        // if no value is set, this is probably an empty row
        if (!valueText || valueText == ' and ') {
            return;
        }

        el.find('.read-only').remove();
        el.find('.filter-text').remove();
        var readonlySpan = $("<div>", {
            html: "<strong>" + filterText + "</strong> " + operatorText + " <strong>" + valueText + "</strong>",
            'class': "read-only"
        });
        el.append($(readonlySpan));

        el.find('.rule-filter-container').hide();
        el.find('.rule-operator-container').hide();
        el.find('.rule-value-container').hide();
        el.find('.rule-value-container input').hide();

        // need to remove the "Apply" button on already applied filters
        el.find('.filter-apply-button').remove();
        el.find('.rule-header .remove-row').css('left', '').css('right', '16px');
        el.find('.filter-text').css('padding-left', '0');

        // add the edit button
        BLCAdmin.filterBuilders.getEditButton(el);

        $('.filter-text').show();
    });

    /**
     * Invoked from a Filter Builder with display type : "MODAL"
     * Invoked from the "Close" button on a modal filter builder
     */
    $('body').on('click', 'button.set-modal-filter-builder', function () {
        BLCAdmin.hideCurrentModal();
    });

    $('body').on('click', '.remove-row', function() {
        // apply the filters
        BLCAdmin.filterBuilders.applyFilters();
    });

    $('body').on('click', '.edit-row', function() {
        var el = $(this).parent().parent().parent();

        el.find('.read-only').remove();
        el.find('.filter-text').remove();

        // make rule filter field readonly
        var filterText = el.find('div.rule-filter-container > div > div.selectize-input .item').text();
        var readonlyFilter = $("<span>", {
            html: "<strong>" + filterText + "</strong>",
            'class': "filter-text"
        });
        el.find('div.rule-filter-container').append($(readonlyFilter));

        el.find('.rule-filter-container').show();
        el.find('.rule-operator-container').show();

        var hasSelectize = el.find('.rule-value-container').find('.selectize-control');
        if (!hasSelectize.length) {
            el.find('.rule-value-container input').show();
        } else {
            //el.find('.rule-value-container .selectize-input').css('width','233px');
        }
        el.find('.rule-value-container').show();

        // add "Apply" button
        var applyButton = $("<button>", {
            html: "Apply",
            'class': "button primary filter-apply-button"
        });
        el.find('.rule-header .rule-actions').append(applyButton);
        el.find('.rule-header .remove-row').css('right', '').css('left', '16px');
        el.find('.filter-text').css('padding-left', '22px');

        // focus on value input field
        el.find('.rule-value-container input').focus();

        // remove edit-button
        el.find('.edit-row').remove();
    });

    /**
     * Invoked from the "X" (Clear Filters) button on the listgrid
     */
    $(document).on('click', '.clear-filters', function (e) {
        // make sure it doesn't submit the form
        e.preventDefault();

        var $filterButton = $($(this)).siblings('.filter-button');
        var hiddenId = $filterButton.data('hiddenid');

        BLCAdmin.filterBuilders.clearFilters(hiddenId);

        // clear the search field
        $filterButton.closest('.listgrid-search').find('.custom-entity-search input').val('');

        // for asset grid filters
        $filterButton.closest('.listgrid-search').find('.custom-asset-search input').val('');
        $filterButton.closest('.listgrid-search').find('.custom-asset-search button.asset-search-button').click();

        // apply the empty filters
        BLCAdmin.filterBuilders.applyFilters(hiddenId);

        // change "edit filter" button back to "filter"
        $filterButton.text(BLCAdmin.messages.filter);
        $filterButton.insertBefore($filterButton.parent());
        $filterButton.siblings('.button-group').remove();

        $filterButton.closest('.main-content').find('.sticky-container .filter-text').hide();
    });

    /**
     * Invoked from the "Filter" button on the listgrid
     */
    $(document).on('click', '.filter-button', function (e) {
        // make sure it doesn't submit the form
        e.preventDefault();

        // show the filter modal
        var $container = $($(this)).siblings('.query-builder-filters-container');
        if (!$container.length) {
            $container = $($(this)).parent().siblings('.query-builder-filters-container');
        }
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

        var hiddenInput = $("<input>", {
            'type' : 'hidden',
            'id' : 'hidden-id',
            'data-hiddenId' : hiddenId
        });

        $modalContainer.find('hr').remove();

        var $modal = BLCAdmin.getModalSkeleton();
        //$modal.addClass('sm');
        $modal.find('.modal-header').find('h3').html('Filters Applied');
        //$modal.find('.modal-header').append(addFilterBtn);
        $modal.find('.modal-body').append(hiddenInput);
        $modal.find('.modal-body').append($modalContainer);
        $modal.find('.modal-footer').append(BLCAdmin.filterBuilders.getCloseModalFilterLink(hiddenId));

        $modal.find('.modal-body').find('select').each(function(i, el) {
            var el = $(el);
            if (el.hasClass('form-control')) {
                el.removeClass('form-control').blSelectize();
            }

            el.parent().parent().find('div.rule-filter-container > div > div.selectize-input').width("244px");
            //el.parent().parent().find('div.rule-operator-container > div > div.selectize-input').width("122px");
            //el.parent().parent().find('div.rule-value-container > div > div.selectize-input').width("245px");
            //el.parent().parent().find('div.rule-value-container').css("display", "inline-block");
        });

        $modal.find('.rule-container').each(function(i, el) {
            var el = $(el);

            var filterText = el.find('.rule-filter-container .selectize-input .item').text();
            var operatorText = el.find('.rule-operator-container .selectize-input .item').text();
            var valueText = el.find('.rule-value-container .selectize-input .item').text();
            if (valueText == '') {
                var valueArray = [];
                valueText = el.find('.rule-value-container input');
                $.each(valueText, function(i, val) {
                    valueArray.push($(val).val());
                });
                valueText = valueArray.join("</strong> and <strong>");
            }

            // check for selectize value
            if (valueText == "</strong> and <strong>") {
                valueText = el.find('.rule-value-container input').data('hydrate');
                valueText = valueText.toString().replace(',', "</strong>, <strong>");
            }

            // if no value is set, this is probably an empty row
            if (!valueText) {
                //el.find('.remove-row').click();
                return;
            }

            el.find('.read-only').remove();
            var readonlySpan = $("<span>", {
                html: "<strong>" + filterText + "</strong> " + operatorText + " <strong>" + valueText + "</strong>",
                'class': "read-only"
            });
            el.append($(readonlySpan));

            el.find('div.rule-filter-container > div > div.selectize-input').hide();
            el.find('.rule-filter-container').hide();
            el.find('.rule-operator-container').hide();
            el.find('.rule-value-container').hide();
            el.find('.rule-value-container input').hide();

            // need to remove the "Apply" button on already applied filters
            el.find('.filter-apply-button').remove();
            el.find('.rule-header .remove-row').css('left', '').css('right', '16px');
            el.find('.filter-text').css('padding-left', '0');

            // add the edit button
            BLCAdmin.filterBuilders.getEditButton(el);
        });

        BLCAdmin.showElementAsModal($modal, function() {
            var modalFilterBuilder = BLCAdmin.filterBuilders.getFilterBuilder($modalContainer.attr('id'));
            var hiddenId = modalFilterBuilder.hiddenId;
            modalFilterBuilder.removeAllQueryBuilders();

            // if we don't have the clear filters button, add it
            var filterData = $.parseJSON($('#' + hiddenId).val());

            // if there are active filters, change the filter button to "Edit"
            var $filterButton = $($('.filter-button[data-hiddenid=' + hiddenId + ']')[0]);
            if (filterData.data.length == 1) {
                if (filterData.data[0].rules.length > 0) {

                    if (!$filterButton.closest('.button-group').length) {

                        $filterButton.text("Edit Filter");
                        $filterButton.removeClass('disabled').removeAttr('disabled');

                        var clearButton = $('<button>', {
                            'html': '<i class="fa fa-times" />',
                            'class': 'button dropdown-toggle clear-filters'
                        });

                        var buttonGroup = $('<div>', {
                            'class': 'button-group'
                        });

                        buttonGroup.append($filterButton.clone());
                        buttonGroup.append(clearButton);
                        $(buttonGroup).insertBefore($filterButton.closest('.filter-info:visible').find('.filter-builder-data'));

                        $filterButton.remove();
                        $filterButton.closest('.main-content').find('.sticky-container .filter-text').show();
                    }
                }
            } else {
                if ($filterButton.text() !== BLCAdmin.messages.filter) {
                    // change "edit filter" button back to "filter"
                    $filterButton.text(BLCAdmin.messages.filter);
                    $filterButton.insertBefore($filterButton.parent());
                    $filterButton.siblings('.button-group:visible').remove();
                    $filterButton.closest('.main-content').find('.sticky-container .filter-text').hide();
                }
            }
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
                var hiddenId = $('.modal-body').find('#hidden-id').data('hiddenid');
                var filterBuilder = BLCAdmin.filterBuilders.getFilterBuilderByHiddenId(hiddenId);
                $(filterBuilder.builders[0]).queryBuilder('updateDisabledFilters');
                el.find('option[disabled]').remove();
                el.removeClass('form-control').blSelectize();
            }

            el.parent().parent().find('div.rule-filter-container > div > div.selectize-input').width("222px");
            //el.parent().parent().find('div.rule-operator-container > div > div.selectize-input').width("100px");
            //el.parent().parent().find('div.rule-value-container > div > div.selectize-input').width("223px");
        }
    });

    /**
     * Trigger "Apply" on enter press
     */
    $('body').on('keyup', '.rule-value-container > input', function(e) {
        if(e.keyCode == 13) {
            $(this).parent().parent().find('.filter-apply-button').click();
        }
    });
});
