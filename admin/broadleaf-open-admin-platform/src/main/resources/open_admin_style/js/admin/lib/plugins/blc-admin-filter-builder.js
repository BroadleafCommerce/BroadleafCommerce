/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
 * A BLC Admin plugin for the jQuery Query Builder
 * that customizes the component to handle complex rules containing an item quantity
 * as well as simple rules while augmenting the default styles to support
 * Admin UX concepts and styles
 * @author Jon Fleschler (jfleschler)
 */

$.fn.queryBuilder.define('blc-admin-filter-builder', function(options) {

    /**
     * Custom callback to handle displaying rules with multiple value fields
     * (e.g. Order Item - Quantity in between [text] , [text])
     */
    this.on('afterCreateRuleInput.filter', function(h, rule) {
        //rule.$el.find('.rule-operator-container > div > div.selectize-input').width('100px');

        var numElements = rule.$el.find('.rule-value-container span.rule-val-sep').length + 1;
        if (numElements > 1) {
            var dynamicWidth = 180/numElements;
            rule.$el.find('.rule-value-container span.rule-val-sep').parent()
                .find('input').each(function() {
                    var $this = $(this);
                    $this.wrap("<div style='display:inline-block;width:" + dynamicWidth + "px;'></div>");
                });
        }

        if (rule.$el.find('.filter-apply-button').length == 0) {
            // add "Apply" button
            var applyButton = $("<button>", {
                html: "Apply",
                'class': "button primary filter-apply-button"
            });
            rule.$el.find('.rule-header .rule-actions').append(applyButton);
            rule.$el.find('.rule-header .remove-row').css('right', '').css('left', '16px');
            rule.$el.find('.filter-text').css('padding-left', '22px');

            // focus on value input field
            rule.$el.find('.rule-value-container input').focus();
        }
    });

    this.on('afterCreateRuleFilters.filter', function(h, rule) {
        var rules = $(h.target).find('.rules-list');
        rules.find('.no-filters').remove();

        styleInputs(rule);
    });

    this.on('afterCreateRuleOperators.filter', function(h, rule) {
        var el = rule.$el;

        // make rule filter field readonly
        var filterText = el.find('div.rule-filter-container > div > div.selectize-input .item').text();
        var readonlyFilter = $("<span>", {
            html: "<strong>" + filterText + "</strong>",
            'class': "filter-text"
        });
        el.find('div.rule-filter-container').append($(readonlyFilter));
        el.find('div.rule-filter-container > div > div.selectize-input').hide();

        styleInputs(rule);
    });

    this.on('afterUpdateRuleFilter.filter', function(h, rule) {
        styleInputs(rule);
    });

    this.on('afterUpdateRuleOperator.filter', function(h, rule) {
        styleInputs(rule);
    });

    this.on('afterDeleteRule.filter', function(h, rule) {
        // apply the filters
        BLCAdmin.filterBuilders.applyFilters();

        var rules = $(h.target).find('.rules-list');
        if (!rules.children().length) {
            // if the rule is undefined then display "No filters" message
            var noFiltersText = BLCAdmin.filterBuilders.getNoFilterText();
            rules.append(noFiltersText);
        }
    });

    /**
     * Modify Default Templates
     */
    this.on('getGroupTemplate.filter', function(h, level) {
        if (level===1) {
            var $h = $(h.value);
            //Strip-out default Bootstrap styles
            $h.find('.btn-group, .btn, .btn-xs, .btn-success, .btn-primary').each(function(){
                $(this).removeClass('btn-group btn btn-xs btn-success btn-primary');
            });

            //Add a remove condition button - only for
            //org.broadleafcommerce.common.presentation.client.SupportedFieldType.RULE_WITH_QUANTITY
            if (options.removeConditionsLink) {
                $h.find('.group-actions').each(function(){
                    $(this).append(options.removeConditionsLink);
                });
            }

            //Pre-Style the All/Any Toggle Switch Radio Buttons
            $h.find('.group-conditions label').each(function(index){
                var inputName = $(this).find('input[type="radio"]').attr('name');
                inputName += '--' + index;
                $(this).find('input[type="radio"]').attr('id', inputName);
                $(this).attr('for', inputName);
            }).hide();

            //TODO i18n the text
            var filterWhereLabel = $("<label>", {"class": "filter-group-header-label", "text": "Filter where:"});
            $h.find('.group-conditions').append(filterWhereLabel);

            $h.find('.group-actions button').addClass('button primary').text('Add New Filter');

            //$h.find('.rules-group-header').append($("<hr />"));
            h.value = $h.prop('outerHTML');
        }
    });

    this.on('getRuleTemplate.filter', function(h, level) {
        var $h = $(h.value);
        //Strip out Bootstrap styles
        $h.find('.btn-group, .btn, .btn-xs, .btn-success, .btn-primary, .btn-danger').each(function(){
            $(this).removeClass('btn-group btn btn-xs btn-success btn-primary btn-danger');
        });

        $h.find('button').each(function(){
            $(this).addClass('remove-row');
            $(this).contents().filter(function(){return this.nodeType === 3;}).remove();
        });

        h.value = $h.prop('outerHTML');
    });

    function styleInputs(rule) {
        var el = rule.$el;
        rule.$el.append('<div style="clear: both"></div>');

        //el.find('div.rule-filter-container > div > div.selectize-input').width("222px");
        //el.find('div.rule-operator-container > div > div.selectize-input').width("100px");
        el.find('div.rule-value-container').css('width', '223px');

        el.find('label > input[type="radio"]').each(function(){
            $(this).parent().addClass('filter-radio-label');
        });
    }

}, {
    pk: "null",
    quantity: "1",
    removeConditionsLink: {}
});

$.fn.queryBuilder.extend({

    getOperatorLabelByType: function(type) {
        return this.lang.operators[type] || type;
    }

});

