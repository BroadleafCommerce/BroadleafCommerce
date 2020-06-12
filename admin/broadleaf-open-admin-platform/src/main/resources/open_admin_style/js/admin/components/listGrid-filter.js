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
(function($, BLCAdmin) {
    
    var dropdownAsToggle = true;
    var activeClass = 'active';

    BLCAdmin.listGrid.filter = {
        // close all dropdowns except for the dropdown passed
        closeDropdowns : function (dropdown) {
            $('.listgrid-headerBtn.dropdown').find('ul').not(dropdown).removeClass('show-dropdown');
        },
        
        // reset all toggle states except for the button passed
        resetToggles : function (button) {
            var buttons = $('.listgrid-headerBtn.dropdown').not(button);
            buttons.add($('> span.' + activeClass, buttons)).removeClass(activeClass);
        },
        
        showOperators : function (criteria) {
            if ( $(criteria).children().length > 1) {
                $(criteria).next().show();
            }   else {
                $(criteria).next().hide();
            }
        },

        clearActiveSorts : function($context) {
            $context.closest('thead').find('i.sort-icon').removeClass('listgrid-icon-down').removeClass('listgrid-icon-up')
                .removeClass('fa-sort-alpha-asc').removeClass('fa-sort-alpha-desc')
                .removeClass('fa-sort-numeric-asc').removeClass('fa-sort-numeric-desc')
                .removeClass('active');
            $context.closest('thead').find('input.sort-direction').removeClass('active').val('');
            $context.closest('thead').find('input.sort-property').removeClass('active');
            //remove the URL parameters that deal with sorts
            BLCAdmin.history.replaceUrlParameter('sortProperty', null);
            BLCAdmin.history.replaceUrlParameter('sortDirection', null);
        },
        
        initialize : function(context) {
            //fill out the criteria and sorts based on the current URL parameters
            var $header = $('#listGrid-main-header');
            if ($header.length == 0) {
                $header = context.find('.listgrid-header-wrapper table');
            }

            var listGridType = $header.data('listgridtype');
            if (listGridType != 'main') {
                return;
            }

            var params = BLCAdmin.history.getUrlParameters();
            if (!params) {
                params = context.find('.listgrid-header-wrapper table').data('currentparams');
                for (key in params) {
                    params[key] = params[key].join('|');
                }
            }
            
            // We'll make a clone of the object here so we don't modify the original data
            params = $.extend({}, params);

            // unbold all headerfields
            $('.listgrid-title').css('font-weight', 'normal');

            if (params) {
                $('i.filter-icon').hide();
                var sortProperty = params['sortProperty'];
                if (sortProperty) {
                    //first enable the clear sorts button
                    var $sortInput = $header.find("input[value='" + sortProperty + "']");
                    var $closestSortHeader = $sortInput.closest('.listgrid-headerBtn');

                    //now ensure that the correct arrow direction is shown (up or down)
                    var ascending = (params['sortDirection'] == 'ASCENDING') ? true : false;
                    var icon = $closestSortHeader.find('div i.sort-icon');
                    icon.addClass('active');
                    icon.toggleClass('listgrid-icon-up', ascending);
                    icon.toggleClass('listgrid-icon-down', !ascending);

                    var isNumeric = $closestSortHeader.find('input.is-numeric').length > 0;
                    if (isNumeric) {
                        icon.toggleClass('fa-sort-numeric-asc', ascending);
                        icon.toggleClass('fa-sort-numeric-desc', !ascending);
                    } else {
                        icon.toggleClass('fa-sort-alpha-asc', ascending);
                        icon.toggleClass('fa-sort-alpha-desc', !ascending);
                    }
                    
                    delete params['sortProperty'];
                    delete params['sortDirection'];
                }

                // iterate through the rest of the parameters
                $.each(params, function(key, value) {
                    var $criteriaInput = $header.find("input[data-name='" + key + "']");
                    // bold the header name
                    $criteriaInput.parent().find('.listgrid-title').css('font-weight', 'bold');
                    //$criteriaInput.parent().find('i.filter-icon').show();
                });
            }
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    /**
     * Handler that fires whenever a sorting link is clicked, sort ascending or descending. This will also modify the
     * sort value input for the closet sort input for this list grid header
     */
    $('body').on('click', '.fa-sort', function() {
        var descending = $(this).hasClass('listgrid-icon-down');
        var ascending = $(this).hasClass('listgrid-icon-up');

        //reset any of the currently active sorts on all the fields in the grid
        BLCAdmin.listGrid.filter.clearActiveSorts($(this));
        
        if ((!descending && !ascending) || ascending) {
            // If we're ascending, we now want to descend
            if (ascending) {
                ascending = false;
                descending = true;
            }

            //apply the sort to the current field
            var sortValue = (descending) ? 'DESCENDING' : 'ASCENDING';
            var $sortType = $(this).closest('.listgrid-headerBtn').find('input.sort-direction');
            $sortType.val(sortValue);
            
            //update the header icon for this field
            var icon = $(this).parents('.listgrid-headerBtn').find('div i.sort-icon');
            icon.toggleClass('listgrid-icon-up', !descending);
            icon.toggleClass('listgrid-icon-down', descending);
            icon.addClass('active');
            
            var isNumeric = $(this).closest('.listgrid-headerBtn').find('input.is-numeric').length > 0;
            if (isNumeric) {
                icon.toggleClass('fa-sort-numeric-asc', !descending);
                icon.toggleClass('fa-sort-numeric-desc', descending);
            } else {
                icon.toggleClass('fa-sort-alpha-asc', !descending);
                icon.toggleClass('fa-sort-alpha-desc', descending);
            }
            
            //also mark these particular sorts as active so they will be serialized
            $sortType.toggleClass('active', true);
            $(this).closest('.listgrid-headerBtn').find('input.sort-property').toggleClass('active', true);
        }

        // submit the form just for this particular field since this is the only sort that changed
        $(this).closest('.listgrid-headerBtn').find('div.filter-fields .listgrid-filter').trigger('click', true);
    });

    /**
     * Intercepts the enter keypress from the listgrid criteria input (since it is not apart of a form) and clicks the
     * closest filter button
     */
    $('body').on('keyup', 'input.listgrid-criteria-input', function(event) {
        if (event.which == 13) {
            $(this).closest('.filter-fields').find('button.listgrid-filter').click();
            return false;
        }
    });
    
    /**
     * Intercepts a filter dropdown being chosen
     */
    $('body').on('change', 'select.listgrid-criteria-input, input.to-one-criteria-input', function(event) {
        var $this = $(this);
        var val = $this.val();
        
        $clearFilterButton = $this.closest('.filter-fields').find('button.listgrid-clear-filter');
        $filterButton = $this.closest('.filter-fields').find('button.listgrid-filter');
        if (val) {
            $filterButton.click();
            $clearFilterButton.removeAttr('disabled');
        } else {
            $clearFilterButton.click();
            $clearFilterButton.attr('disabled', 'disabled');
        }
        
    });
    
    /**
     * Intercepts click events on the 'filter' button for the list grid headers. This will execute an AJAX call after
     * serializing all of the inputs for all of the list grid header fields so that criteria on multiple fields can
     * be sent to the server
     */
    $('body').on('click', 'div.filter-fields button.listgrid-filter', function(event, indirect) {
        event.preventDefault();

        // Dates need their value parsed to the appropriate server value
        $(this).closest('thead').find('div.filter-fields input.datepicker').each(function(i, e) {
            var serverVal = BLCAdmin.dates.getServerDate($(e).val());
            $(e).data('overrideval', serverVal);
        });
        
        // Booleans need their value parsed also
        $(this).closest('thead').find('div.filter-fields .boolean-input-container input.listgrid-criteria-input').each(function(i, e) {
            var $parent = $(e).closest('.boolean-input-container');
            var $yesInput = $parent.find('input.radio-input[value="true"]');
            var $noInput = $parent.find('input.radio-input[value="false"]');
            if ($yesInput.is(':checked')) {
                $(e).val($yesInput.val());
            } else if($noInput.is(':checked')) {
                $(e).val($noInput.val());
            }
        });
            
        // If we're dealing with range inputs, we need to get the appropriate value
        var $rangeInputs = $(this).closest('div.filter-fields').find('.listgrid-criteria-input-range:visible');
        if ($rangeInputs.length > 1) {
            var $rangeLow = $($rangeInputs[0]);
            var $rangeHigh = $($rangeInputs[1]);
            var rangeLowVal = $rangeLow.val();
            var rangeHighVal = $rangeHigh.val();
            
            if ($rangeLow.data('overrideval') != '' && $rangeLow.data('overrideval') != undefined) {
                rangeLowVal = $rangeLow.data('overrideval');
                rangeHighVal = $rangeHigh.data('overrideval');
            }

            var searchVal = rangeLowVal + '|' + rangeHighVal;
            if (searchVal == '|') {
                searchVal = '';
            }

            $(this).closest('div.filter-fields').find('.listgrid-criteria-input').val(searchVal);
        } else if ($rangeInputs.length == 1) {
            var $rangeSpecific = $($rangeInputs[0]);
            var searchVal = $rangeSpecific.val();

            if ($rangeSpecific.data('overrideval') != '' && $rangeSpecific.data('overrideval') != undefined) {
                searchVal = $rangeSpecific.data('overrideval');
            }

            $(this).closest('div.filter-fields').find('.listgrid-criteria-input').val(searchVal);
        }
        
        var $clearFilterButton = $(this).closest('.filter-fields').find('button.listgrid-clear-filter');
        var val = $(this).closest('div.filter-fields').find('.listgrid-criteria-input').val();
        if (val == '' && !indirect) {
            $clearFilterButton.click();
            return false;
        }
        
        $(this).closest('ul').removeClass('show-dropdown');
        
        var $inputs = $(this).closest('thead').find('div.filter-fields .listgrid-criteria-input');
        
        //also grab the sorts and ensure those inputs are also serialized
        var $sorts = $(this).closest('thead').find('input.sort-direction.active, input.sort-property.active');
        $inputs = $inputs.add($.makeArray($sorts));
        
        var nonBlankInputs = [];
        $inputs.each(function(index, input) {
            //since these filter inputs do not have 'real' input names in the DOM, give it here to make serialization easier
            $(input).attr('name', $(input).data('name'));
            
            //only submit fields that have a value set and are not a sort field. Sort fields will be added separately
            if ($(input).val()) {
                if (!$(input).hasClass('sort-direction') && !$(input).hasClass('sort-property')) {
                  //toggle the filter icon for this field as active or not
                    var filterIcon = $(input).parents('.listgrid-headerBtn').find('div i.filter-icon');
                    filterIcon.toggleClass('active', !!$(input).val());
                }
                nonBlankInputs.push(input);
            }
            
        });

        var $tbody = $(this).closest('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');
        if ($tbody.data('listgridtype') == 'asset_grid') {
            var parentId = $('<input>', {
                'name': 'parentId',
                'data-name': 'parentId',
                'value': $(this).closest('.listgrid-container').data('parentid').toString()
            });
            nonBlankInputs.push(parentId[0]);
        }

        $(this).closest('.listgrid-container').find('.mCSB_container').css('top', '0px');
        $(this).closest('.listgrid-container').find('.listgrid-body-wrapper').mCustomScrollbar('update');
        var oldParams = BLCAdmin.history.getUrlParameters();
        if (oldParams == null) {
            oldParams = {};
        }
        oldParams['isLookup'] = $(this).closest('.modal').length > 0;
        var url = BLCAdmin.buildUrlWithParams($(this).closest('.filter-fields').data('action'), oldParams);

        url = getFilteredParams($(this), url);

        BLCAdmin.listGrid.showLoadingSpinner($tbody, $tbody.closest('.mCustomScrollBox').position().top + 3);
        BLC.ajax({
            url: url,
            type: "GET",
            data: $(nonBlankInputs).serialize()
        }, function(data) {
            if ($tbody.data('listgridtype') == 'main') {
                $(nonBlankInputs).each(function(index, input) {
                    BLCAdmin.history.replaceUrlParameter(input.name, input.value);
                });
            }
            BLCAdmin.listGrid.hideLoadingSpinner($tbody);

            if ($tbody.data('listgridtype') == 'asset_grid') {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.asset-listgrid div.listgrid-header-wrapper'), null, {isRefresh: false});
            } else if ($tbody.data('listgridtype') == 'tree') {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.tree-search-wrapper div.listgrid-header-wrapper'), null, {isRefresh: false});
            } else {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.listgrid-header-wrapper'), null, {isRefresh: false});
            }
            $inputs.each(function(index, input) {
                $(input).removeAttr('name');
            });
        });
        
        if (val != undefined && val != '') {
            $clearFilterButton.removeAttr('disabled');
        } else {
            $clearFilterButton.attr('disabled', 'disabled');
        }
        
        return false;
    });
    
    /**
     * Intercepts the form submission for a top-level entity search. This search is only available on a main entity page
     * (like Products)
     */
    $('body').on('submit', 'form.custom-entity-search', function(event) {
        $('body').find('.custom-entity-search button.search-button').click();
        return false;
    });

    $('body').on('search', '#listgrid-search', function(event) {
        $('body').find('.custom-entity-search button.search-button').click();
        return false;
    });

    $('body').on('click', '.custom-entity-search button[type=search]', function(event) {
        $(this).closest('form').find('#listgrid-search').val('');
        $(this).siblings(".search-button").click();
    });

    /**
     * Intercepts the button click for the main entity search. This will look at the first field in the main list grid (of
     * which there is only 1 on the page) and replace the criteria value for that field with whatever was typed into the
     * search box.
     */
    $('body').on('click', '.custom-entity-search button.search-button', function(event) {
        //this takes place on the main list grid screen so there should be a single list grid
        var search = $(this).closest('form').find('input').val();
        var $container = $(this).closest('.listgrid-container');
        var tableId = $container.find('table').last().attr('id');
        var $firstInput = $($container.find('#listGrid-main-header th .listgrid-criteria-input')[0]);

        if ($firstInput.length == 0) {
           // if there wasn't a primary list grid, check for an inline list grid.
           $firstInput = $($container.find('.list-grid-table th .listgrid-criteria-input')[0]);
        }

        $firstInput.val(search);

        var oldParams = BLCAdmin.history.getUrlParameters();
        if (oldParams == null) {
            oldParams = {};
        }

        // Remove startIndex from the request
        delete oldParams["startIndex"];

        if (search.length > 0) {
            oldParams[$firstInput.data('name')] = search;
        } else {
            delete oldParams[$firstInput.data('name')];
        }

        BLC.ajax({
            url: BLCAdmin.buildUrlWithParams($(this).closest('form').attr('action'), oldParams),
            type: "GET"
        }, function(data) {
            if ($(data).find('table').length === 1 && (BLCAdmin.currentModal() === undefined || BLCAdmin.currentModal().length === 0)) {
                BLCAdmin.history.replaceUrlParameter('startIndex');
                for (key in oldParams) {
                    BLCAdmin.history.replaceUrlParameter(key, oldParams[key]);
                }
            }
            var $relatedListGrid;
            if ($(data).find('table').length > 1) {
                $relatedListGrid = $(data).find("div.listgrid-container:has(table#" + tableId +")");
            } else {
                if ($(data).hasClass('listgrid-container')) {
                    $relatedListGrid = $(data);
                } else {
                    $relatedListGrid = $(data).find("div.listgrid-container");
                }
            }

            if (search === "") {
                BLCAdmin.history.replaceUrlParameter($firstInput.data('name'), null);
            }

            BLCAdmin.listGrid.replaceRelatedCollection($relatedListGrid, null, { isRefresh : false});
            $firstInput.trigger('input');

            // update the filter builder
            var hiddenId = $('.filter-button').data('hiddenid');
            var filterBuilder = BLCAdmin.filterBuilders.getFilterBuilderByHiddenId(hiddenId);
            BLCAdmin.filterBuilders.addExistingFilters(filterBuilder);
        });
        return false;
    });

    function getFilteredParams($el, url) {
        var $container = $el.closest('.listgrid-container');
        var $filterButton = $container.find('.filter-button');
        var hiddenId = $filterButton.data('hiddenid');
        if (hiddenId) {
            var filteredValues = BLCAdmin.filterBuilders.getFiltersAsURLParams(hiddenId);
            filteredValues = $.param(filteredValues);
            if (url.indexOf('?') >= 0) {
                url += '&' + filteredValues;
            } else {
                url += '?' + filteredValues;
            }
        }
        return url;
    }
});
