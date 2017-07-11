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
(function ($, BLCAdmin) {

    var initializationHandlers = [];
    var postRowSelectionHandlers = [];

    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.listGrid = {
        replaceRelatedCollection: function ($wrapper, alert, opts) {
            if ($wrapper.hasClass('selectize-wrapper')) {
                BLCAdmin.listGrid.replaceSelectizeCollection($wrapper, alert, opts);
            } else {
                BLCAdmin.listGrid.replaceRelatedListGrid($wrapper, alert, opts);
            }
        },

        findRelatedTable: function ($headerWrapper) {
            var $table = $headerWrapper.find('table');
            var tableId = $table.attr('id');
            var $oldTable = null;

            // Go through the modals from top to bottom looking for the replacement list grid
            var modals = BLCAdmin.getModals();
            if (modals.length > 0) {
                for (var i = modals.length - 1; i >= 0; i--) {
                    $oldTable = $(modals[i]).find('#' + tableId);
                    if ($oldTable != null && $oldTable.length > 0) {
                        break;
                    }
                }
            }

            // If we didn't find it in a modal, use the element from the body
            if ($oldTable == null || $oldTable.length == 0) {
                $oldTable = $('#' + tableId);
            }
            return $oldTable;
        },

        replaceRelatedListGrid: function ($headerWrapper, alert, opts) {
            var $table = $headerWrapper.find('table');
            var $oldTable = BLCAdmin.listGrid.findRelatedTable($headerWrapper);

            var currentIndex = BLCAdmin.listGrid.paginate.getTopVisibleIndex($oldTable.find('tbody'));

            var $oldBodyWrapper = $oldTable.closest('.listgrid-body-wrapper');
            var $oldHeaderWrapper = $oldBodyWrapper.prev();

            $oldHeaderWrapper.find('table.list-grid-table>thead').after($table.find('tbody'));
            $oldBodyWrapper.remove();

            var $listGridContainer = $oldHeaderWrapper.closest('.listgrid-container');

            // We'll update the current params with what we were returned in this request
            $listGridContainer.find('.listgrid-header-wrapper table').data('currentparams', $table.data('currentparams'));

            BLCAdmin.listGrid.initialize($listGridContainer);

            BLCAdmin.listGrid.paginate.scrollToIndex($listGridContainer.find('tbody'), currentIndex);
            $listGridContainer.find('.listgrid-body-wrapper').mCustomScrollbar('update');

            if (alert) {
                BLCAdmin.listGrid.showAlert($listGridContainer, alert.message, alert);
            }

            BLCAdmin.listGrid.paginate.updateTableFooter($listGridContainer.find('tbody'));

            var $fieldGroupListGridWrapperHeader = $listGridContainer.find('.fieldgroup-listgrid-wrapper-header');
            var $tbody = $listGridContainer.find('.listgrid-body-wrapper table tbody');
            var totalRecords = $listGridContainer.find('.listgrid-body-wrapper table tbody').data('totalrecords');
            if ($fieldGroupListGridWrapperHeader.length) {
                var $totalRecords = $fieldGroupListGridWrapperHeader.find('.listgrid-total-records');
                var totalRecordsText = totalRecords == 1 ? '(' + totalRecords + ' Record)' : '(' + totalRecords + ' Records)';

                if (totalRecords != 0 || $totalRecords.html().indexOf('Fetch') < 0) {
                    $totalRecords.html(totalRecordsText);
                }

                if (totalRecords !== 0 || $listGridContainer.hasClass('filtered')) {
                    $fieldGroupListGridWrapperHeader.removeClass('hidden-body');
                    $fieldGroupListGridWrapperHeader.parent().find('.fieldgroup-listgrid-wrapper').removeClass('hidden');
                    BLCAdmin.listGrid.paginate.updateGridSize($tbody);
                } else {
                    $fieldGroupListGridWrapperHeader.addClass('hidden-body');
                    $fieldGroupListGridWrapperHeader.parent().find('.fieldgroup-listgrid-wrapper').addClass('hidden');
                }
            }

            BLCAdmin.listGrid.paginate.initializeHeaderWidths($listGridContainer.find('table.list-grid-table'));

            $listGridContainer.trigger('blc-listgrid-replaced', $listGridContainer);
        },

        replaceSelectizeCollection: function ($newWrapper, alert, opts) {
            var collectionId = $newWrapper.attr('id');
            var $oldWrapper = null;

            // Go through the modals from top to bottom looking for the replacement list grid
            var modals = BLCAdmin.getModals();
            if (modals.length > 1) {
                for (var i = modals.length - 1; i > 0; i--) {
                    $oldWrapper = $(modals[i]).find('.selectize-wrapper#' + collectionId);
                    if ($oldWrapper != null && $oldWrapper.length > 0) {
                        break;
                    }
                }
            }

            // If we didn't find it in a modal, use the element from the body
            if ($oldWrapper == null || $oldWrapper.length == 0) {
                $oldWrapper = $('#' + collectionId);
            }

            var $container = $oldWrapper.parent();

            $oldWrapper.after($newWrapper);
            $oldWrapper.remove();

            BLCAdmin.initializeSelectizeFields($container);

            if (alert) {
                BLCAdmin.alert.showAlert($container, alert.message, alert);
            }

            $container.trigger('blc-listgrid-replaced', $container);
        },


        /**
         * Refreshes the collection to have the latest available data according to the collection's 'actionurl'
         *
         * @param {element} $actionButton - the refresh button
         */
        handleCollectionRefreshAction: function ($actionButton) {
            var $listGridContainer = $actionButton.closest(".listgrid-container");
            var actionUrl = $actionButton.data('actionurl');

            BLCAdmin.listGrid.refreshCollection($listGridContainer, actionUrl);
        },

        /**
         * Refreshes the collection to have the latest available data according to the provided url
         *
         * Note: this is expecting to hit a Spring controller with the given url that returns
         *  a rendered collection using "views/standaloneListGrid"
         *
         * @param {element} $listGridContainer - the ListGrid collection's container element
         * @param {String} url - the url that will return the latest collection data
         */
        refreshCollection: function ($listGridContainer, url) {
            var params = BLCAdmin.filterBuilders.getListGridFiltersAsURLParams($listGridContainer);

            BLC.ajax({
                url: BLC.buildUrlWithParams(url, params),
                type: "GET"
            }, function (data) {
                BLCAdmin.listGrid.replaceRelatedCollection($(data));
            });
        },

        getActionLink: function ($trigger) {
            var $selectedRows;
            if ($trigger.is('a')) {
                $selectedRows = $trigger.closest('tr');
            } else {
                var $container = $trigger.closest('.listgrid-container');
                $selectedRows = $container.find('table tr.selected');
            }
            var link = $selectedRows.attr('data-link');

            if ($trigger.attr('data-urlpostfix')) {
                link += $trigger.attr('data-urlpostfix');
            }
            if ($trigger.attr('data-queryparams')) {
                link += $trigger.attr('data-queryparams');
            }

            return link;
        },

        getRowFields: function ($tr) {
            var fields = {};

            // add the id to the array
            if ($tr.data('rowid')) {
                fields['id'] = $tr.data('rowid');
            }

            $tr.find('td').each(function () {
                var fieldName = $(this).data('fieldname');
                var value = $(this).data('fieldvalue');
                fields[fieldName] = value;
            });

            //add hidden fields to the array
            var hiddenFields = $tr.data('hiddenfields');
            if (hiddenFields) {
                for (j = 0; j < hiddenFields.hiddenFields.length; j++) {
                    var fieldName = hiddenFields.hiddenFields[j].name;
                    var value = hiddenFields.hiddenFields[j].val;
                    fields[fieldName] = value;
                }
            }

            if ($tr.closest('.tree-listgrid-container').length) {
                fields['selectedRow'] = $tr;
            }

            return fields;
        },

        getRows: function ($table) {
            return $table.find('tbody').find('tr:not(.list-grid-no-results):not(.blank-padding)');
        },

        getSelectedRowIds: function ($button) {
            var link = BLC.servletContext + $button.data('urlpostfix');

            var $container = $button.closest('.listgrid-container');
            var $selectedRows = $container.find('table tr.selected');

            var selectedRowIds = [];
            $selectedRows.each(function (index, element) {
                selectedRowIds.push($(element).data('rowid'));
            });

            return selectedRowIds;
        },

        updateActionButtons: function ($listGridContainer) {
            if ($listGridContainer.find('tbody tr.list-grid-no-results, tbody tr.progress').length == 0) {

                $listGridContainer.find('button.row-action.all-capable').removeAttr('disabled');
                $listGridContainer.find('.filter-button').removeClass('disabled').removeAttr('disabled');
            } else {
                var hiddenid = $listGridContainer.find('.filter-button').data('hiddenid');
                var filters = $('#' + hiddenid).val();
                if (filters !== undefined) {
                    var activeFilters = JSON.parse(filters).data;
                    // disable the filter button if the listgrid is not currently filtered
                    if (!activeFilters.length || (activeFilters.length && activeFilters[0].rules.length == 0)) {
                        $listGridContainer.find('.filter-button').addClass('disabled').prop('disabled', true).prop("forceDisabled", true);
                    } else if (activeFilters.length && activeFilters[0].rules.length > 0) {
                        // the listgrid is filtered but may not have any rows, we want to show the "no results"
                        $listGridContainer.addClass('filtered');
                    }
                }
            }
            updateListGridActionsForEmptyContainer($listGridContainer.find('button.non-empty-required'), $listGridContainer.find('tr.clickable').length == 0);
            var listGridId = $listGridContainer.find('.listgrid-header-wrapper table').attr('id');
            var numSelected = $listGridContainer.find('tr.selected').length;
            updateListGridActionsForContainer($listGridContainer.find('button.row-action'), numSelected);

            var $modal = $listGridContainer.closest('.modal');
            if ($modal.length && typeof listGridId !== 'undefined') {
                var $modalActionContainer = $modal.find('.modal-footer .listgrid-modal-actions');
                updateListGridActionsForContainer($modalActionContainer.find("button.row-action"), numSelected);
            }

            function updateListGridActionsForEmptyContainer($containerActions, isEmpty) {
                if (isEmpty) {
                    if (!$containerActions.prop('disabled')) {
                        $containerActions.prop('disabled', true);
                    }
                    if (!$containerActions.prop('forceDisabled')) {
                        $containerActions.prop("forceDisabled", true);
                    }
                } else {
                    if ($containerActions.prop('disabled')) {
                        $containerActions.prop('disabled', false);
                    }
                }
            }

            function updateListGridActionsForContainer($containerActions, numSelected) {
                if (numSelected) {
                    $containerActions.prop('disabled', false);
                } else {
                    $containerActions.prop('disabled', true).prop("forceDisabled", true);
                }

                if (numSelected > 1) {
                    $containerActions.filter('.single-action-only').prop('disabled', true).prop("forceDisabled", true);
                }
            }
        },

        showAlert: function ($container, message, options) {
            options = options || {};
            var alertType = options.alertType || '';
            var autoClose = options.autoClose || 3000;

            if (options.clearOtherAlerts) {
                $container.find('.alert-box').remove();
            }

            var $alert = $('<span>').addClass('alert-box').addClass(alertType);
            $alert.append('&nbsp;&nbsp;' + message);

            var alertTarget = $container.find('.titlebar:first-child .titlebar-title');
            if (!alertTarget.length) {
                alertTarget = $container.find('label span');
            }
            alertTarget.append($alert);

            setTimeout(function () {
                $alert.fadeOut();
            }, autoClose);
        },

        fixHelper: function (e, ui) {
            ui.closest('tbody').find('tr').children().each(function () {
                $(this).width($(this).width());
            });
            return ui;
        },

        showLoadingSpinner: function ($tbody, spinnerOffset) {
            var $spinner = $tbody.closest('.listgrid-container').find('.listgrid-table-spinner-container');

            var spinnerHalfSize = 20;
            var tableHalfWidth = $tbody.innerWidth() / 2;
            var tableHalfHeight = $tbody.closest('.mCustomScrollBox').height() / 2;
            
            $spinner.css('top', (Math.floor(spinnerOffset) + tableHalfHeight - spinnerHalfSize) + 'px');
            $spinner.css('margin-left', tableHalfWidth - spinnerHalfSize + 'px');
            $spinner.css('display', 'block');
            
            var backdrop = $('<div>', {
               'class' : 'spinner-backdrop'
            });
            $spinner.prepend(backdrop);
        },

        hideLoadingSpinner: function ($tbody) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            $spinner.parent().css('display', 'none');
            $spinner.parent().find('.spinner-backdrop').remove();
        },

        isLoading : function($tbody) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            return $spinner.parent().css('display') === 'block';
        },

        initialize: function ($container) {
            BLCAdmin.listGrid.updateActionButtons($container);
            BLCAdmin.adornedEntityForm.updateActionButtons($container);
            BLCAdmin.listGrid.updateGridTitleBarSize($container.find('.fieldgroup-listgrid-wrapper-header'));

            if (BLCAdmin.listGrid.paginate) {
                BLCAdmin.listGrid.paginate.initialize($container);
            }

            if (BLCAdmin.listGrid.filter) {
                BLCAdmin.listGrid.filter.initialize($container);
            }

            // update date fields
            $("[data-fieldname='dateLabel']").find('.column-text').each(function () {
                var day = moment.utc($(this).html()).local();
                if (day.isValid()) {
                    $(this).html(day.fromNow());
                }
            });

            // update duration fields
            $("[data-fieldname='durationLabel']").find('.column-text').each(function () {
                var parsed = parseInt($(this).data("fieldvalue"));
                var day = moment.duration(parsed).format('h[h] m[m] s[s]');
                $(this).html(day);
            });

            // Run any additionally configured initialization handlers
            for (var i = 0; i < initializationHandlers.length; i++) {
                initializationHandlers[i]($container.find('table:not([id$="-header"])'));
            }
        },

        getListGridCount: function ($container) {
            return $container.find('.listgrid-container').length;
        },

        updateGridTitleBarSize: function ($titlebar) {
            var maxWidth = $titlebar.width();
            maxWidth -= $titlebar.find('.listgrid-toolbar').outerWidth();
            maxWidth -= $titlebar.find('.listgrid-total-records').outerWidth();
            maxWidth -= 70;

            $titlebar.find('.listgrid-friendly-name').css('max-width', maxWidth + 'px');
        },

        addInitializationHandler: function (fn) {
            initializationHandlers.push(fn);
        },

        addPostRowSelectionHandler: function (fn) {
            postRowSelectionHandlers.push(fn);
        },

        /**
         * The rowSelected handler for the inline list grid ...
         */
        inlineRowSelected: function (event, $target, link, fields, currentUrl, multi) {
            var $tr = $target !== null ? $target : $('tr[data-link="' + link + '"]');
            var currentlySelected = $tr.hasClass('selected');
            var $listGridContainer = $tr.closest('.listgrid-container');
            var $tbody = $tr.closest("tbody");
            var $listgridHeader = $tbody.closest(".listgrid-body-wrapper").prev();

            if (!multi) {
                $tbody.find('tr').removeClass('selected');
                $tbody.find('tr').find('input[type=checkbox].listgrid-checkbox').prop('checked', false);
            }

            if (!currentlySelected) {
                $tr.addClass("selected");
                $tr.find('input[type=checkbox].listgrid-checkbox').prop('checked', true);
            } else {
                $tr.removeClass("selected");
                $tr.find('input[type=checkbox].listgrid-checkbox').prop('checked', false);
            }

            updateMultiSelectCheckbox($tbody, $listgridHeader);

            BLCAdmin.listGrid.updateActionButtons($listGridContainer);
            BLCAdmin.adornedEntityForm.updateActionButtons($listGridContainer);

            for (var i = 0; i < postRowSelectionHandlers.length; i++) {
                postRowSelectionHandlers[i]($tr);
            }
        }
    };

    BLCAdmin.addInitializationHandler(function ($container) {
        $container.find('.listgrid-container').each(function (index, element) {
            BLCAdmin.listGrid.initialize($(element));
        });

        $('body').on('click', '.side-nav ul li a', function () {
            $container.find('.listgrid-container').each(function (index, element) {
                $(element).find('tbody').each(function (index, element) {
                    if ($(element).closest('.oms-tab').length) {
                        return;
                    }

                    if ($(element).is(':visible')) {
                        BLCAdmin.listGrid.paginate.updateGridSize($(element));
                    } else {
                        $(element).addClass('needsupdate');
                    }
                });
                $(element).find('.fieldgroup-listgrid-wrapper-header').each(function (index, element) {
                    BLCAdmin.listGrid.updateGridTitleBarSize($(element));
                });
            });
        });
    });

})(jQuery, BLCAdmin);

$(document).ready(function () {
    var isMouseDown = false;

    $('body').mousedown(function () {
        isMouseDown = true;
    }).mouseup(function () {
        isMouseDown = false;
    });

    /**
     * Bind a handler to trigger anytime a table row is clicked on any list grid.
     *
     * After assembling information, this will delegate to the specialized rowSelected
     * handler for this particular kind of list grid.
     */
    $('body').on('click', '.list-grid-table tbody tr', function (event) {
        var $tr = $(this);
        var $table = $tr.closest('table');
        var listGridType = $table.data('listgridtype');
        var listGridSelectType = $table.data('listgridselecttype');

        if (listGridType != 'main' && !$tr.hasClass('clickable') && !isExternalLink(event)) {
            return false;
        }

        var link = $tr.data('link');
        var currentUrl = $table.data('currenturl');
        var fields = BLCAdmin.listGrid.getRowFields($tr);

        if ($tr.find('tr.list-grid-no-results').length == 0 && !$table.hasClass('reordering')) {

            // Avoid rebuilding "next" columns if row is already selected
            if ((listGridType === 'tree' || listGridType === 'asset_grid_folder') && !$tr.hasClass('selected')) {
                $('body').trigger('listGrid-' + listGridType + '-rowSelected', [$tr, link, fields, currentUrl]);
            }

            // If this record is locked, don't allow clicks
            if ($tr.find('.fa-lock').length) {
                return;
            }

            // Select row based on select type
            if (listGridType !== 'tree' || !$tr.hasClass('selected')) {
                $('body').trigger('listGrid-' + listGridSelectType + '-rowSelected', [$tr, link, fields, currentUrl]);
            }

            // If Adorned or Asset ListGrid, process row click by adding item id to form.
            // Else, wait for confirmation button click.
            if (listGridType === 'adorned_with_form' ||
                listGridType === 'adorned' ||
                listGridType === 'asset' ||
                listGridType === 'productionInventorySkuSelection') {
                $('body').trigger('listGrid-' + listGridType + '-rowSelected', [$tr, link, fields, currentUrl]);
            }

            // Always trigger row selection for asset grid types
            if (listGridType === 'asset_grid') {
                $('body').trigger('listGrid-' + listGridType + '-rowSelected', [$tr, link, fields, currentUrl]);
            }
        }
    });

    function isExternalLink(event) {
        return $(event.target).is('a.external-link');
    }

    var ACTION_PADDING = 35;

    function modifyTextColumnWidth($workflowAction, removeSpacing) {
        var $td = $workflowAction.closest('td');
        var $columnText = $td.find('.column-text');
        if (removeSpacing) {
            // Add 'hovered' class as it effects the selectors width
            $columnText.addClass('hovered');
            var textWidth = $columnText.width();
            var actionWidth = $workflowAction.width() + ACTION_PADDING;

            textWidth -= actionWidth;
            $columnText.width(textWidth + "px");
        } else {
            // Remove 'hovered' class and reset selectors width
            $columnText.removeClass('hovered');
            $columnText.width("88%");
        }
    }

    $('body').on({
        mouseenter: function () {
            var $tr = $(this);

            if ($tr.has('a.sub-list-grid-reorder') && $tr.find('a.sub-list-grid-reorder').css('visibility') === 'hidden') {
                $tr.find('a.sub-list-grid-reorder').css({visibility: 'visible'});
            }

            var $listgridRowAction = $tr.find('.listgrid-row-action');
            if ($listgridRowAction.length && $listgridRowAction.css('visibility') === 'hidden') {
                $listgridRowAction.css({visibility: 'visible'});

                modifyTextColumnWidth($listgridRowAction, true);
            }
        },
        mouseleave: function () {
            $(this).find('a.sub-list-grid-reorder').css({visibility: 'hidden'});
            $(this).find('.listgrid-row-action').css({visibility: 'hidden'});

            var $tr = $(this);
            var $listgridRowAction = $tr.find('.listgrid-row-action');
            if ($listgridRowAction.length) {
                modifyTextColumnWidth($listgridRowAction, false);
            }
        }
    }, '.list-grid-table tbody tr');

    /**
     * The rowSelected handler for the main list grid doesn't do anything by default
     */
    $('body').on('listGrid-main-rowSelected', function (event, $target, link, fields, currentUrl) {
    });

    $('body').on('listGrid-single_select-rowSelected', function (event, $target, link, fields, currentUrl) {
        BLCAdmin.listGrid.inlineRowSelected(event, $target, link, fields, currentUrl, false);
    });
    $('body').on('listGrid-translation-rowSelected', function (event, $target, link, fields, currentUrl) {
        BLCAdmin.listGrid.inlineRowSelected(event, $target, link, fields, currentUrl, false);
    });
    $('body').on('listGrid-multi_select-rowSelected', function (event, $target, link, fields, currentUrl) {
        BLCAdmin.listGrid.inlineRowSelected(event, $target, link, fields, currentUrl, true);
    });
    $('body').on('listGrid-selectize-rowSelected', function (event, $target, link, fields, currentUrl) {
        BLCAdmin.listGrid.inlineRowSelected(event, $target, link, fields, currentUrl, false);
    });

    /**
     * The rowSelected handler for a toOne list grid needs to trigger the specific valueSelected handler
     * for the field that we are performing the to-one lookup on.
     */
    $('body').on('listGrid-to_one-rowSelected', function (event, $target, link, fields, currentUrl) {
        $('div.additional-foreign-key-container').trigger('valueSelected', [$target, fields, link, currentUrl]);
    });

    /**
     * The rowSelected handler for a simpleCollection list grid ...
     */
    $('body').on('listGrid-basic-rowSelected', function (event, $target, link, fields, currentUrl) {
        var postData = {};

        for (var key in fields) {
            if (fields.hasOwnProperty(key)) {
                postData["fields['" + key + "'].value"] = fields[key];
            }
        }

        BLC.ajax({
            url: currentUrl,
            type: "POST",
            data: postData
        }, function (data) {
            BLCAdmin.hideCurrentModal();

            BLCAdmin.listGrid.replaceRelatedCollection($(data), {
                message: BLCAdmin.messages.saved + '!',
                alertType: 'save-alert',
                autoClose: 3000,
                clearOtherAlerts: true
            });
        });
    });

    /**
     * The rowSelected handler for an adornedTarget list grid. This is specific to adorned target
     * lists that do not have any additional maintained fields. In this case, we can simply
     * submit the form directly. Also need to enable or disable the submit
     * button based on whether the row is being de/selected.
     */
    $('body').on('listGrid-adorned-rowSelected', function (event, $target, link, fields, currentUrl) {
        var $adornedTargetId = $(this).find('input#adornedTargetIdProperty');
        if ($adornedTargetId.val() == fields['id']) {
            $adornedTargetId.val('');
        } else {
            $adornedTargetId.val(fields['id']);
        }
        // if selecting a row -> enable submit button, else deselecting -> disable submit button
        if ($target.hasClass('selected')) {
            $('button[type="submit"]').prop('disabled', false);
        } else {
            $('button[type="submit"]').prop('disabled', true);
        }
    });

    /**
     * The rowSelected handler for an adornedTargetWithForm list grid. Once the user selects an entity,
     * show the form with the additional maintained fields. Also need to enable or disable the submit
     * button based on whether the row is being de/selected.
     */
    $('body').on('listGrid-adorned_with_form-rowSelected', function (event, $target, link, fields, currentUrl) {
        var $adornedTargetId = $(this).find('input#adornedTargetIdProperty');
        if ($adornedTargetId.val() == fields['id']) {
            $adornedTargetId.val('');
        } else {
            $adornedTargetId.val(fields['id']);
        }
        // if selecting a row -> enable submit button, else deselecting -> disable submit button
        if ($target.hasClass('selected')) {
            $('button[type="submit"]').prop('disabled', false);
            var $a = $('a#adornedModalTab2Link');
            BLC.ajax({
                url: link + '/verify',
                type: "POST"
            }, function (data) {
                var autoSubmit = false;
                for (prop in data) {
                    if (data.hasOwnProperty(prop)) {
                        var fixedKey = prop.replace(".", "__");
                        var value = data[prop];
                        if (prop == 'autoSubmit' && value == 'true') {
                            autoSubmit = true;
                        } else {
                            var inputField = $('input[name="fields[\'' + fixedKey + '\'].value"]');
                            inputField.val(value);
                        }
                    }
                }
                $a.removeClass('disabled');
                $a.click();
                $a.addClass('disabled');
                if (autoSubmit) {
                    BLCAdmin.currentModal().find('.submit-button').click();
                }
            });
        } else {
            $('button[type="submit"]').prop('disabled', true);
        }
    });

    /**
     * This handler will fire for additional foreign key fields when the find button is clicked.
     *
     * It is responsible for binding a valueSelected handler for this field as well as launching
     * a list grid modal that will be used to select the to-one entity.
     *
     * Note that we MUST unbind this handler when the modal is hidden as there could be many different
     * to-one fields on an entity form.
     */
    $('body').on('click', '.to-one-lookup', function (event) {
        var $lookupButton = $(this);
        var fkValue = $lookupButton.closest('div.additional-foreign-key-container').find('.value');
        var fkValueFound = fkValue != undefined && fkValue.val().length > 0;
        var mustConfirm = $lookupButton.data('confirm') && fkValueFound;
        var confirmMsg = $lookupButton.data('confirm-text');

        BLCAdmin.confirmProcessBeforeProceeding(mustConfirm, confirmMsg, processToOneLookupCall, [$lookupButton]);

        function processToOneLookupCall(params) {
            var $toOneLookup = params[0];

            var $container = $toOneLookup.closest('div.additional-foreign-key-container');
            $container.on('valueSelected', function (event, $target, fields, link, currentUrl) {
                var $this = $(this);
                var displayValueProp = $this.find('input.display-value-property').val();
                var displayValue = fields[displayValueProp];
                var $selectedRow = BLCAdmin.currentModal().find('tr[data-link="' + link + '"]');
                var $displayField = $selectedRow.find('td[data-fieldname=' + displayValueProp.split(".").join("\\.") + ']');
                if ($displayField.hasClass('derived')) {
                    displayValue = $.trim($displayField.text());
                }
                if (typeof BLCAdmin.treeListGrid !== 'undefined' && $this.closest('.modal-add-entity-form.enterprise-tree-add').length) {
                    BLCAdmin.treeListGrid.buildParentPathJson($this.closest('.modal-add-entity-form.enterprise-tree-add'), $selectedRow);
                }
                var $valueField = $this.find('input.value');
                $valueField.val(fields['id']);
                $this.find('.display-value-none-selected').hide();
                $this.find('span.display-value').html(displayValue);
                $this.find('input.display-value').val(displayValue).show();
                $this.find('input.hidden-display-value').val(displayValue).trigger('input');
                // Ensure that the clear button shows up after selecting a value
                $this.find('button.clear-foreign-key').show();
                // Ensure that the external link button points to the correct URL
                var $externalLink = $this.find('span.external-link-container a');
                $externalLink.attr('href', $externalLink.data('foreign-key-link') + '/' + fields['id']);
                $externalLink.parent().show();
                // To-one fields potentially trigger a dynamicform. We test to see if this field should
                // trigger a form, and bind the necessary event if it should.
                var onChangeTrigger = $valueField.data('onchangetrigger');
                if (onChangeTrigger) {
                    var trigger = onChangeTrigger.split("-");
                    if (trigger[0] == 'dynamicForm') {
                        var $dynamicContainer = $("div.dynamic-form-container[data-dynamicpropertyname='" + trigger[1] + "']");
                        var url = $dynamicContainer.data('currenturl') + '?propertyTypeId=' + fields['id'];
                        BLC.ajax({
                            url: url,
                            type: "GET"
                        }, function (data) {
                            var dynamicPropertyName = data.find('div.dynamic-form-container').data('dynamicpropertyname');
                            var $oldDynamicContainer = $('div.dynamic-form-container[data-dynamicpropertyname="' + dynamicPropertyName + '"]');
                            var $newDynamicContainer = data.find('div.dynamic-form-container');
                            BLCAdmin.initializeFields($newDynamicContainer);
                            $oldDynamicContainer.replaceWith($newDynamicContainer);
                        });
                    }
                }
                $valueField.trigger('change', fields).trigger('input');
                $valueField.closest('.field-group').trigger('change');
                BLCAdmin.hideCurrentModal();
            });
            var url = $toOneLookup.data('select-url');
            var thisClass = $container.closest('form').find('input[name="ceilingEntityClassname"]').val();
            var thisField = $toOneLookup.closest('.field-group').attr('id');
            var handler = BLCAdmin.getDependentFieldFilterHandler(thisClass, thisField);
            if (handler != null) {
                var $parentField = $container.closest('form').find(handler['parentFieldSelector']);
                url = url + '&' + handler['childFieldPropertyName'] + '=' + BLCAdmin.extractFieldValue($parentField);
            }
            if ($toOneLookup.data('dynamic-field')) {
                url = url + '&dynamicField=true';
            }
            BLCAdmin.showLinkAsModal(url, function () {
                $('div.additional-foreign-key-container').unbind('valueSelected');
            });
            return false;
        }
    });

    /**
     * Handle "add" clicks on listgrids
     */
    $('body').on('click', 'button.sub-list-grid-add, a.sub-list-grid-add', function () {
        BLCAdmin.showLinkAsModal($(this).attr('data-actionurl'));
        return false;
    });

    /**
     * Handle empty "add" clicks on listgrids
     */
    $('body').on('click', 'button.sub-list-grid-add-empty, a.sub-list-grid-add-empty', function () {
        var link = $(this).attr('data-actionurl');
        link = link.substring(0, link.indexOf("/add")) + "/addEmpty" + link.substring(link.indexOf("/add") + 4, link.length);
        BLC.ajax({
            url: link,
            type: "POST"
        }, function (data) {
            link = link.substring(0, link.indexOf("/addEmpty")) + "/" + data.id + link.substring(link.indexOf("/addEmpty") + 9, link.length);
            if (link.indexOf("?") < 0) {
                link += "?isPostAdd=true";
            } else {
                link += "&isPostAdd=true";
            }
            BLCAdmin.showLinkAsModal(link);
        });
        return false;
    });

    /**
     * Handle "fetch" click on listgrids
     */
    $('body').on('click', 'button.sub-list-grid-fetch', function () {
        var $button = $(this);
        var url = $button.attr('data-actionurl');
        var $tbody = $button.closest('.listgrid-container').find('.listgrid-body-wrapper .list-grid-table');

        // Create the loading icon
        var loadingSpinner = $('<i>', {
            "class" : 'fa-pulse fa fa-spinner',
            "style" : 'float: none; width: ' + $button.width() + 'px;'
        });
        $button.html(loadingSpinner);

        BLC.ajax({
            url: url,
            type: "GET"
        }, function(data) {
            $button.html("Refresh");
            if ($tbody.data('listgridtype') == 'asset_grid') {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.asset-listgrid div.listgrid-header-wrapper'), null, {isRefresh: false});
            } else if ($tbody.data('listgridtype') == 'tree') {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.tree-search-wrapper div.listgrid-header-wrapper'), null, {isRefresh: false});
            } else {
                BLCAdmin.listGrid.replaceRelatedCollection($(data).find('div.listgrid-header-wrapper'), null, {isRefresh: false});
            }
        });
        return false;
    });

    /**
     * Handle the Re-ordering of ListGrid items
     */
    $('body').on({
        mouseenter: function () {
            var $this = $(this);
            var $container = $this.closest('.listgrid-container');
            var $table = $container.find('table');
            var $tbody = $table.find('tbody');
            var $trs = $tbody.find('tr');
            var parentId = typeof $container.data('parentid') === 'undefined' ? null : $container.data('parentid');

            $table.addClass('reordering');

            $trs.removeClass('clickable').addClass('draggable');

            function isReorderToFirst(prevDisplayOrder, nextDisplayOrder) {
                return typeof prevDisplayOrder === 'undefined' && typeof nextDisplayOrder !== 'undefined';
            }

            $tbody.sortable({
                helper : BLCAdmin.listGrid.fixHelper,
                change: function( event, ui ) {
                    var prevDisplayOrder = ui.placeholder.prev().data('displayorder');
                    var nextDisplayOrder = ui.placeholder.next().data('displayorder');
                    if ((!$.isNumeric(prevDisplayOrder) || prevDisplayOrder == nextDisplayOrder) && !isReorderToFirst(prevDisplayOrder, nextDisplayOrder)) {
                        ui.placeholder.hide();
                    } else {
                        ui.placeholder.show();
                    }
                },
                beforeStop: function(ev, ui) {
                    var prevDisplayOrder = ui.placeholder.prev().prev().data('displayorder');
                    var nextDisplayOrder = ui.placeholder.next().data('displayorder');
                    if ((!$.isNumeric(prevDisplayOrder) || prevDisplayOrder == nextDisplayOrder) && !isReorderToFirst(prevDisplayOrder, nextDisplayOrder)) {
                        $(this).sortable("cancel");
                    }
                },
                update : function(event, ui) {
                    var url = ui.item.data('link') + '/sequence';

                    if (BLCAdmin.treeListGrid !== undefined) {
                        url = BLCAdmin.treeListGrid.updateSequenceUrl(ui, url);
                    }

                    BLC.ajax({
                        url : url,
                        type : "POST",
                        data : {
                            newSequence : BLCAdmin.listGrid.paginate.getActualRowIndex(ui.item),
                            parentId : parentId
                        }
                    }, function (data) {
                        var $container = $('div.listgrid-container#' + data.field);

                        BLCAdmin.listGrid.showAlert($container, BLCAdmin.messages.saved + '!', {
                            alertType: 'save-alert',
                            clearOtherAlerts: true,
                            autoClose: 3000
                        });
                        $container = $this.closest('.listgrid-container');
                        if ($container.prev().length) {
                            var $parent =  ui.item;
                            if (!$parent.hasClass('dirty')) {
                                $parent.addClass('dirty');
                                var changeIcon = '<a class="blc-icon-triangle-right has-tip hover-cursor workflow-icon" data-width="200" ' +
                                    'title="This record has been modified in the current sandbox"></a>';

                                if ($parent.find('.workflow-change-icon').length) {
                                    $parent.find('.workflow-change-icon').html(changeIcon);
                                } else {
                                    var contents = $parent.find('td:first').html();
                                    $parent.find('td:first').html(changeIcon + contents);
                                }
                            }
                        }

                        ui.item.data('displayorder', data.newDisplayOrder);
                    });
                }
            }).disableSelection();
        },
        mouseleave: function () {
            if (isMouseDown) {
                return false;
            }

            var $container = $(this).closest('.listgrid-container');
            var $table = $container.find('table');
            var $tbody = $table.find('tbody');
            var $trs = $tbody.find('tr');

            $table.removeClass('reordering');

            $trs.removeClass('draggable').addClass('clickable');
            $tbody.sortable("destroy");
        }
    }, 'a.sub-list-grid-reorder');

    /**
     * Handle the removing of ListGridItems from the ListGrid
     */
    $('body').on('click', 'a.sub-list-grid-remove, button.sub-list-grid-remove', function () {
        var link = BLCAdmin.listGrid.getActionLink($(this));

        var $selectedRows;
        if ($(this).is('a')) {
            $selectedRows = $(this).closest('tr');
        } else {
            var $container = $(this).closest('.listgrid-container');
            $selectedRows = $container.find('table tr.selected');
        }
        var rowFields = BLCAdmin.listGrid.getRowFields($selectedRows);

        BLC.ajax({
            url: link,
            data: rowFields,
            type: "POST"
        }, function (data) {
            if (data.status == 'error') {
                BLCAdmin.listGrid.showAlert($container, data.message);
            } else {
                BLCAdmin.listGrid.replaceRelatedCollection($(data), {
                    message: BLCAdmin.messages.saved + '!',
                    alertType: 'save-alert',
                    autoClose: 3000,
                    clearOtherAlerts: true
                });
            }
        });

        return false;
    });

    $('body').on('click', 'a.sub-list-grid-update, button.sub-list-grid-update', function () {
        var link = BLCAdmin.listGrid.getActionLink($(this));

        BLCAdmin.showLinkAsModal(link);

        return false;
    });

    $('body').on('click', 'button.sub-list-grid-view', function () {
        var link = BLCAdmin.listGrid.getActionLink($(this));

        BLCAdmin.showLinkAsModal(link);

        return false;
    });

    $('body').on('click', 'a.sub-list-grid-edit, button.sub-list-grid-edit', function () {
        var link = BLCAdmin.listGrid.getActionLink($(this));

        if ($(this).closest('table').length && $(this).closest('table').data('listgridtype') === 'tree'
            && $(this).closest('.select-column[data-parentid]').length) {
            // Expected uri structure: "/admin/{section}/{id}/{alternate-id}"
            // Desired uri structure: "/admin/{section}/{id}"
            link = link.substring(0, link.lastIndexOf('/'));
        }

        window.location.assign(link);
        return false;
    });

    $('body').on('click', 'button.list-grid-single-select', function () {
        var $modal = $(this).closest('.modal');
        var $container = $modal.find('.listgrid-container');
        var $table = $container.find('table');
        var $selectedRow = $table.find('tr.selected');
        var listGridType = $table.data('listgridtype');

        var link = $selectedRow.data('link');
        var fields = BLCAdmin.listGrid.getRowFields($selectedRow);
        var currentUrl = $container.find("table").data('currenturl');

        $('body').trigger('listGrid-' + listGridType + '-rowSelected', [$(this), link, fields, currentUrl]);
    });

    /**
     * Clears out a previously-selected foreign key on both a form and listgrid criteria
     */
    $('body').on('click', 'button.clear-foreign-key', function (event) {
        var $this = $(this);
        var $container = $this.closest('div.additional-foreign-key-container');

        // Update the current display value
        $container.find('.display-value').hide();
        $container.find('.clear-foreign-key').hide();
        $container.find('.display-value-none-selected').show();

        if (typeof BLCAdmin.treeListGrid !== 'undefined') {
            BLCAdmin.treeListGrid.removeParentPathJson($container.closest('.modal-add-entity-form.enterprise-tree-add'));
        }

        // Remove the criteria input val
        $container.find('.value').val('').trigger('change').trigger('input');
        $container.find('.hidden-display-value').val('').trigger('change').trigger('input');

        $container.find('.external-link-container').hide();

        // Clear out the dynamic form that this field drives, if any
        var onChangeTrigger = $container.find('input.value').data('onchangetrigger');
        if (onChangeTrigger) {
            var trigger = onChangeTrigger.split("-");
            if (trigger[0] == 'dynamicForm') {
                $("div.dynamic-form-container[data-dynamicpropertyname='" + trigger[1] + "'] fieldset").remove();
            }
        }

        // Don't follow the link; prevents page jumping
        return false;
    });

    $('body').on('mouseover', 'td.row-action-selector', function (event) {
        $(this).find('ul.row-actions').show();
    });

    $('body').on('mouseout', 'td.row-action-selector', function (event) {
        $(this).find('ul.row-actions').hide();
    });

    $('body').on('click', 'input[type=checkbox].multiselect-checkbox', function (event) {
        var $listgridBody = $(this).closest(".listgrid-header-wrapper").next();
        if ($(this).prop('checked')) {
            $listgridBody.find(".listgrid-checkbox").prop('checked', true);
            BLCAdmin.listGrid.inlineRowSelected(null, $listgridBody.find(".list-grid-table tbody tr:not(.selected)"), null, null, null, true);
        } else {
            $listgridBody.find(".listgrid-checkbox").prop('checked', false);
            BLCAdmin.listGrid.inlineRowSelected(null, $listgridBody.find(".list-grid-table tbody tr.selected"), null, null, null, true);
        }
    });

    $('body').on('click', 'input[type=checkbox].listgrid-checkbox', function (event) {
        var $listgridHeader = $(this).closest(".listgrid-body-wrapper").prev();
        var $tbody = $(this).closest("tbody");

        updateMultiSelectCheckbox($tbody, $listgridHeader);
    });

    $('body').on('click', 'td.listgrid-row-actions span', function (event) {
        return false;
    });

    $('body').on('click', '.collection-refresh', function (e) {
        var $this = $(this);
        var $contents = $this.html();
        $this.html('<i class="fa-pulse fa fa-spinner"/>');
        $this.removeClass('collection-refresh').addClass('disabled');

        BLCAdmin.listGrid.handleCollectionRefreshAction($this);

        $this.html($contents);
        $this.addClass('collection-refresh').removeClass('disabled');
    });

    $("input[type=checkbox].listgrid-checkbox").prop('checked', false);
    $("input[type=checkbox].multiselect-checkbox").prop('checked', false);
});

function updateMultiSelectCheckbox($tbody, $listgridHeader) {
    var numRows = $tbody.find("input[type=checkbox].listgrid-checkbox").length;
    var numCheckedRows = $tbody.find("input[type=checkbox].listgrid-checkbox:checked").length;

    if (numRows === numCheckedRows) {
        $listgridHeader.find("input[type=checkbox].multiselect-checkbox").prop('checked', true);
    } else {
        $listgridHeader.find("input[type=checkbox].multiselect-checkbox").prop('checked', false);
    }
}
