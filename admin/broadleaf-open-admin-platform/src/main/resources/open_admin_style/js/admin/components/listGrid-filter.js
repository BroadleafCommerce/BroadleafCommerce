/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
                .removeClass('icon-sort-by-alphabet').removeClass('icon-sort-by-alphabet-alt')
                .removeClass('icon-sort-by-order').removeClass('icon-sort-by-order-alt')
                .removeClass('active');
            $context.closest('thead').find('input.sort-direction').removeClass('active').val('');
            $context.closest('thead').find('input.sort-property').removeClass('active');
            //remove the URL parameters that deal with sorts
            BLCAdmin.history.replaceUrlParameter('sortProperty', null);
            BLCAdmin.history.replaceUrlParameter('sortDirection', null);
        },
        
        initialize : function(context) {
            // Positioning the Flyout List
            var normalButtonHeight  = $('.listgrid-headerBtn.dropdown:not(.large):not(.small):not(.tiny)', context).outerHeight() - 1,
                largeButtonHeight   = $('.listgrid-headerBtn.large.dropdown', context).outerHeight() - 1,
                smallButtonHeight   = $('.listgrid-headerBtn.small.dropdown', context).outerHeight() - 1,
                tinyButtonHeight    = $('.listgrid-headerBtn.tiny.dropdown', context).outerHeight() - 1;

            $('.listgrid-headerBtn.dropdown:not(.large):not(.small):not(.tiny) > ul', context).css('top', normalButtonHeight);
            $('.listgrid-headerBtn.dropdown.large > ul', context).css('top', largeButtonHeight);
            $('.listgrid-headerBtn.dropdown.small > ul', context).css('top', smallButtonHeight);
            $('.listgrid-headerBtn.dropdown.tiny > ul', context).css('top', tinyButtonHeight);

            $('.listgrid-headerBtn.dropdown.up:not(.large):not(.small):not(.tiny) > ul', context).css('top', 'auto').css('bottom', normalButtonHeight - 2);
            $('.listgrid-headerBtn.dropdown.up.large > ul', context).css('top', 'auto').css('bottom', largeButtonHeight - 2);
            $('.listgrid-headerBtn.dropdown.up.small > ul', context).css('top', 'auto').css('bottom', smallButtonHeight - 2);
            $('.listgrid-headerBtn.dropdown.up.tiny > ul', context).css('top', 'auto').css('bottom', tinyButtonHeight - 2);
            
            //fill out the criteria and sorts based on the current URL parameters
            var $header = $('#listGrid-main-header');
            if ($header.length == 0) {
                $header = context.find('.listgrid-header-wrapper table');
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

            if (params) {
                $('i.filter-icon').removeClass('active');
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
                        icon.toggleClass('icon-sort-by-order', ascending);
                        icon.toggleClass('icon-sort-by-order-alt', !ascending);
                    } else {
                        icon.toggleClass('icon-sort-by-alphabet', ascending);
                        icon.toggleClass('icon-sort-by-alphabet-alt', !ascending);
                    }
                    
                    delete params['sortProperty'];
                    delete params['sortDirection'];
                }
                
                //iterate through the rest of the parameters and fill out the criteria inputs as necessary
                $.each(params, function(key, value) {
                    var $criteriaInput = $header.find("input[data-name='" + key + "']");
                    
                    if (!$criteriaInput || $criteriaInput.length <= 0) {
                        $criteriaInput = $header.find("select[data-name='" + key + "']");
                    }
                    
                    if ($criteriaInput && $criteriaInput.length > 0) {
                        $criteriaInput.val(decodeURIComponent(value));
                        $criteriaInput.closest('.filter-fields').find('button.listgrid-clear-filter').removeAttr('disabled');
                        //show the active filter icon
                        var filterIcon = $($criteriaInput).parents('.listgrid-headerBtn').find('div i.filter-icon');
                        filterIcon.toggleClass('active', true);
                        
                        var $parent = $criteriaInput.parent();
                        if ($parent.hasClass('additional-foreign-key-container')) {
                            var $ul = $parent.find('ul.active-foreign-key-filters');
                            $ul.empty();
                            
                            if (value.indexOf('|') >= 0) {
                                var vals = value.split('|');
                                var i;
                                for (i = 0; i < vals.length; i++) {
                                    $ul.append($('<li>', { 'data-id' : vals[i] }));
                                }
                            } else {
                                $ul.append($('<li>', { 'data-id' : value }));
                            }
                            
                            var url = $parent.find('button.to-one-lookup').data('select-url');
                            url = url.replace('/select', '/details');
                            BLC.ajax({
                                url: url,
                                type: "GET",
                                data: { ids : value }
                            }, function(data) {
                                $ul.find('li').each(function(i, e) {
                                    var $li = $(e);
                                    $li.append($('<i>', { 'class' : 'icon-remove-sign remove-to-one-filter' }));
                                    $li.append(data[$li.data('id')]);
                                });
                            });
                        } else if ($parent.hasClass('boolean-input-container')) {
                            var $yesInput = $parent.find('input.radio-input[value="true"]');
                            var $noInput = $parent.find('input.radio-input[value="false"]');
                            if (value == 'false') {
                                $noInput.click();
                            } else {
                                $yesInput.click();
                            }
                        } else if ($parent.hasClass('range-input-container')) {
                            var rangeValue = value;
                            // If we're dealing with dates, we need to parse for the display value
                            if ($parent.find('.datepicker').length > 0) {
                                rangeValue = decodeURIComponent(rangeValue);
                                if (rangeValue.indexOf('|') >= 0) {
                                    var vals = rangeValue.split('|');
                                    vals[0] = BLCAdmin.dates.getDisplayDate(vals[0]);
                                    vals[1] = BLCAdmin.dates.getDisplayDate(vals[1]);
                                    rangeValue = vals[0] + '|' + vals[1];
                                } else {
                                    rangeValue = BLCAdmin.dates.getDisplayDate(rangeValue);
                                }
                            }
                            
                            if (rangeValue.indexOf('|') >= 0) {
                                var vals = rangeValue.split('|');

                                $parent.find('.specific-input').addClass('hidden');
                                $parent.find('.range-input').removeClass('hidden');
                                
                                $parent.find('input.range-low').val(vals[0]);
                                $parent.find('input.range-high').val(vals[1]);
                            } else {
                                $parent.find('input.range-single').val(rangeValue);
                            }
                        }
                    }
                });
            }
        }
    };
    
    // Prevent event propagation on disabled buttons
    $(document).on('click.fndtn', '.button.disabled', function (e) {
        e.preventDefault();
    });

    // Prevent event propagation on the dropdown form
    $('.listgrid-headerBtn.dropdown div.filter-fields').click(function (e) {
        if (!$(e.target).is('a') && !$(e.target).is('button') && !$(e.target).is('i')) {
            e.stopPropagation();
        }
    });

    $('.listgrid-headerBtn.dropdown .add-filter').click(function (e) {
        var $el = $(this),
            $form = $el.closest('div.filter-fields'),
            criteria = $form.find('.listgrid-criteria');

        $(criteria).append(BLCAdmin.listGrid.filter.createCriteria);
        BLCAdmin.listGrid.filter.showOperators(criteria);
    });

    // reset other active states
    $(document).on('click.fndtn', '.listgrid-headerBtn.dropdown:not(.split), .listgrid-headerBtn.dropdown.split i.filter-icon', function (e) {
        var $el = $(this),
            button = $el.closest('.listgrid-headerBtn.dropdown'),
            dropdown = $('> ul', button);

        // If the click is registered on an actual link or on button element then do not preventDefault which stops the browser from following the link
        if ($.inArray(e.target.nodeName, ['A', 'BUTTON'])){
            e.preventDefault();
        }

        // close other dropdowns
        setTimeout(function () {
            dropdown.toggleClass('show-dropdown');
            BLCAdmin.listGrid.filter.closeDropdowns(dropdown);
        }, 0);
        
    });

    // close all dropdowns and deactivate all buttons
    $(document).on('click.fndtn', 'body, html', function (e) {
        if (undefined == e.originalEvent) { return; }
        // check original target instead of stopping event propagation to play nice with other events
        if (!$(e.originalEvent.target).parents().is('.listgrid-headerBtn') &&
                !$(e.originalEvent.target).parents().is('#ui-datepicker-div')) {
            BLCAdmin.listGrid.filter.closeDropdowns();
            if (dropdownAsToggle) {
                BLCAdmin.listGrid.filter.resetToggles();
            }
        }
    });
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    /**
     * Handler that fires whenever a sorting link is clicked, sort ascending or descending. This will also modify the
     * sort value input for the closet sort input for this list grid header
     */
    $('body').on('click', '.icon-sort', function() {
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
                icon.toggleClass('icon-sort-by-order', !descending);
                icon.toggleClass('icon-sort-by-order-alt', descending);
            } else {
                icon.toggleClass('icon-sort-by-alphabet', !descending);
                icon.toggleClass('icon-sort-by-alphabet-alt', descending);
            }
            
            //also mark these particular sorts as active so they will be serialized
            $sortType.toggleClass('active', true);
            $(this).closest('.listgrid-headerBtn').find('input.sort-property').toggleClass('active', true);
        }

        //submit the form just for this particular field since this is the only sort that changed
        $(this).closest('.listgrid-headerBtn').find('div.filter-fields .listgrid-filter').trigger('click', true);

        return false;
    });
    
    $('body').on('click', 'button.listgrid-clear-sort', function() {
        BLCAdmin.listGrid.filter.clearActiveSorts($(this));
        $(this).attr('disabled', 'disabled');
        $(this).closest('ul').find('div.filter-fields .listgrid-filter').click();
        return false;
    });
    
    $('body').on('click', 'button.listgrid-clear-filter', function(event) {
        event.preventDefault();
        $(this).closest('.filter-fields').find('.listgrid-criteria-input').val('');
        $(this).closest('.filter-fields').find('.radio-input').attr('checked', false);
        $(this).closest('.filter-fields').find('.listgrid-criteria-input-range').val('');
        //clear out the foreign key display value
        $foreignKeyDisplay = $(this).closest('.filter-fields').find('div.foreign-key-value-container span.display-value');
        if ($foreignKeyDisplay) {
            $foreignKeyDisplay.text('');
        }
        $(this).closest('th').find('.filter-icon').removeClass('active');
        $(this).attr('disabled', 'disabled');
        $(this).closest('ul').find('div.filter-fields .listgrid-filter').trigger('click', true);

        $(this).closest('.filter-fields').find('ul.active-foreign-key-filters').empty();
        
        var $tbody = $(this).closest('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');
        
        if ($tbody.data('listgridtype') == 'main') {
            var name = $(this).closest('.filter-fields').find('.listgrid-criteria-input').data('name');
            BLCAdmin.history.replaceUrlParameter(name, null);
        }
            
        return false;
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
        
        $(this).closest('.listgrid-container').find('.mCSB_container').css('top', '0px');
        $(this).closest('.listgrid-container').find('.listgrid-body-wrapper').mCustomScrollbar('update');
        
        var $tbody = $(this).closest('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');
        BLCAdmin.listGrid.showLoadingSpinner($tbody, $tbody.closest('.mCustomScrollBox').position().top + 3);
        BLC.ajax({
            url: $(this).closest('.filter-fields').data('action'),
            type: "GET",
            data: $(nonBlankInputs).serialize()
        }, function(data) {
            if ($tbody.data('listgridtype') == 'main') {
                
                $(nonBlankInputs).each(function(index, input) {
                    BLCAdmin.history.replaceUrlParameter(input.name, input.value);
                });
            }
            BLCAdmin.listGrid.hideLoadingSpinner($tbody);
            BLCAdmin.listGrid.replaceRelatedListGrid($(data).find('div.listgrid-header-wrapper'), null, { isRefresh : false });
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
        $('body').find('.custom-entity-search a').click();
        return false;
    });
    
    /**
     * Intercepts the button click for the main entity search. This will look at the first field in the main list grid (of
     * which there is only 1 on the page) and replace the criteria value for that field with whatever was typed into the
     * search box.
     */
    $('body').on('click', '.custom-entity-search a.search-button', function(event) {
        //this takes place on the main list grid screen so there should be a single list grid
        var search = $(this).closest('form').find('input').val();
        var $firstInput = $($('body').find('#listGrid-main-header th input.listgrid-criteria-input')[0]);
        
        $firstInput.val(search);

        $(this).closest('form').find('input').val('');
        
        var submitData = {};
        submitData[$firstInput.data('name')] =  $firstInput.val();
        var $tbody = $(this).closest('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');
        BLCAdmin.listGrid.showLoadingSpinner($tbody, $tbody.closest('.mCustomScrollBox').position().top + 3);        
        BLC.ajax({
            url: $(this).closest('form').attr('action'),
            type: "GET",
            data: submitData
        }, function(data) {
            BLCAdmin.history.replaceUrlParameter('startIndex');
            for (key in submitData) {
                BLCAdmin.history.replaceUrlParameter(key, submitData[key]);
            }
            BLCAdmin.listGrid.hideLoadingSpinner($tbody);
            BLCAdmin.listGrid.replaceRelatedListGrid($(data), null, { isRefresh : false});
            $firstInput.trigger('input');
        });

        return false;
    });
    
    $('body').on('change', 'input.to-one-new-selection', function(e) {
        var $this = $(this);
        var $parent = $this.parent();

        var newValue = $this.val();
        var serializedField = $parent.find('.listgrid-criteria-input');
        
        if (serializedField.val() == '') {
            serializedField.val(newValue);
        } else {
            serializedField.val(serializedField.val() + '|' + newValue);
        }
        
        serializedField.trigger('change');
    });

    $('body').on('click', '.remove-to-one-filter', function() {
        var $li = $(this).closest('li');
        var id = $li.data('id');
        
        var $input = $(this).closest('.additional-foreign-key-container').find('input.to-one-criteria-input');
        
        if ($input.val().indexOf('|') >= 0) {
            if ($input.val().indexOf(id) == 0) {
                $input.val($input.val().replace(id + '|', ''));
            } else {
                $input.val($input.val().replace('|' + id, ''));
            }
        } else {
            $li.remove();
            $input.val('');
        }
        
        $input.trigger('change');
    });
    
    $('body').on('click', 'a.numeric-range-toggle', function(e) {
        e.preventDefault();
        
        var $parent = $(this).closest('div.filter-fields');

        $parent.find('.specific-input').toggleClass('hidden');
        $parent.find('.range-input').toggleClass('hidden');
                
        
        return false;
    });

});
