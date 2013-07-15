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
            $context.closest('thead').find('i.sort-icon').removeClass('listgrid-icon-down').removeClass('listgrid-icon-up');
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
            var params = BLCAdmin.history.getUrlParameters();
            if (params) {
                $('i.filter-icon').removeClass('icon-filter');
                var sortProperty = params['sortProperty'];
                if (sortProperty) {
                    //first enable the clear sorts button
                    var $sortInput = $header.find("input[value='" + sortProperty + "']");
                    var $closestSortHeader = $sortInput.closest('.listgrid-headerBtn');
                    $closestSortHeader.find('button.listgrid-clear-sort').removeAttr('disabled');
                    
                    //now ensure that the correct arrow direction is shown (up or down)
                    var ascending = (params['sortDirection'] == 'ASCENDING') ? true : false;
                    var icon = $closestSortHeader.find('div i.sort-icon');
                    icon.toggleClass('listgrid-icon-up', ascending);
                    icon.toggleClass('listgrid-icon-down', !ascending);
                    
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
                        $criteriaInput.val(value);
                        $criteriaInput.closest('.filter-fields').find('button.listgrid-clear-filter').removeAttr('disabled');
                        //show the active filter icon
                        var filterIcon = $($criteriaInput).parents('.listgrid-headerBtn').find('div i.filter-icon');
                        filterIcon.toggleClass('icon-filter', true);
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
        if (!$(e.target).is('a') && !$(e.target).is('button')) {
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
    $(document).on('click.fndtn', '.listgrid-headerBtn.dropdown:not(.split), .listgrid-headerBtn.dropdown.split span', function (e) {
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
        if (!$(e.originalEvent.target).parents().is('.listgrid-headerBtn')) {
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
    $('body').on('click', '.sort-fields button.listgrid-sort', function() {
        //reset any of the currently active sorts on all the fields in the grid
        BLCAdmin.listGrid.filter.clearActiveSorts($(this));
        
        //apply the sort to the current field
        var ascending = $(this).hasClass('down');
        var sortValue = (ascending) ? 'ASCENDING' : 'DESCENDING';
        var $sortType = $(this).closest('ul').find('input.sort-direction');
        $sortType.val(sortValue);
        
        //update the header icon for this field
        var icon = $(this).parents('.listgrid-headerBtn').find('div i.sort-icon');
        icon.toggleClass('listgrid-icon-up', ascending);
        icon.toggleClass('listgrid-icon-down', !ascending);
        
        //also mark these particular sorts as active so they will be serialized
        $sortType.toggleClass('active', true);
        $(this).closest('ul').find('input.sort-property').toggleClass('active', true);
        
        //submit the form just for this particular field since this is the only sort that changed
        $(this).closest('ul').find('div.filter-fields .listgrid-filter').click();
        
        //enable the clear sort button
        $(this).closest('.sort-fields').find('button.listgrid-clear-sort').removeAttr('disabled');

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
        //clear out the foreign key display value
        $foreignKeyDisplay = $(this).closest('.filter-fields').find('div.foreign-key-value-container span.display-value');
        if ($foreignKeyDisplay) {
            $foreignKeyDisplay.text('');
        }
        $(this).closest('th').find('.filter-icon').removeClass('icon-filter');
        $(this).attr('disabled', 'disabled');
        $(this).closest('ul').find('div.filter-fields .listgrid-filter').click();
        
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
        } else {
            $clearFilterButton = $(this).closest('.filter-fields').find('button.listgrid-clear-filter');
            if ($(this).val()) {
                $clearFilterButton.removeAttr('disabled');
            } else {
                $clearFilterButton.attr('disabled', 'disabled');
            }
        }
    });
    
    /**
     * Intercepts a filter dropdown being chosen
     */
    $('body').on('change', 'select.listgrid-criteria-input', function(event) {
        var $this = $(this);
        var val = $this.val();
        
        $clearFilterButton = $(this).closest('.filter-fields').find('button.listgrid-clear-filter');
        $filterButton = $(this).closest('.filter-fields').find('button.listgrid-filter');
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
    $('body').on('click', 'div.filter-fields button.listgrid-filter', function(event) {
        event.preventDefault();
        $(this).closest('ul').removeClass('show-dropdown');
        
        var $inputs = $(this).closest('thead').find('div.filter-fields .listgrid-criteria-input');
        
        //also grab the sorts and ensure those inputs are also serialized
        var $sorts = $(this).closest('thead').find('input.sort-direction.active, input.sort-property.active');
        $inputs = $inputs.add($.makeArray($sorts));
        
        var nonBlankInputs = [];
        $inputs.each(function(index, input) {
            //since these filter inputs do not have 'real' input names in the DOM, give it here to make serialization easier
            $(input).attr('name', $(input).data('name'));
            
            //convert the datepicker inputs to server-valid ones
            if ($(input).hasClass('datepicker')) {
                input = $('<input>', {
                    type: 'hidden',
                    name: $(input).attr('name'),
                    value: BLCAdmin.dates.getServerDate($(input).val())
                }).addClass('datepicker')[0];
            }
            
            //only submit fields that have a value set and are not a sort field. Sort fields will be added separately
            if ($(input).val()) {
                if (!$(input).hasClass('sort-direction') && !$(input).hasClass('sort-property')) {
                  //toggle the filter icon for this field as active or not
                    var filterIcon = $(input).parents('.listgrid-headerBtn').find('div i.filter-icon');
                    filterIcon.toggleClass('icon-filter', !!$(input).val());
                }
                nonBlankInputs.push(input);
            }
            
        });
        
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
            BLCAdmin.listGrid.replaceRelatedListGrid($(data).find('div.listgrid-header-wrapper'));
            $inputs.each(function(index, input) {
                $(input).removeAttr('name');
            });
        });
        
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
    $('body').on('click', '.custom-entity-search a', function(event) {
        //this takes place on the main list grid screen so there should be a single list grid
        var search = $('body').find('input').val();
        var $firstHeader = $('body').find('#listGrid-main-header th:first-child');
        var $input = $firstHeader.find('input.listgrid-criteria-input');
        
        $input.val(search);
        
        var submitData = {};
        submitData[$input.data('name')] =  $input.val();
        
        BLC.ajax({
            url: $(this).closest('form').attr('action'),
            type: "GET",
            data: submitData
        }, function(data) {
            BLCAdmin.history.replaceUrlParameter('startIndex');
            for (key in submitData) {
                BLCAdmin.history.replaceUrlParameter(key, submitData[key]);
            }
            BLCAdmin.listGrid.replaceRelatedListGrid($(data));
            $input.trigger('input');
        });
        return false;
    });

});
