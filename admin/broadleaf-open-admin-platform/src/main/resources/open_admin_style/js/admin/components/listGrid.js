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
(function ($, BLCAdmin) {

    var initializationHandlers = [];

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
                $totalRecords.html(totalRecordsText);

                if (totalRecords !== 0 || $listGridContainer.hasClass('filtered')) {
                    $fieldGroupListGridWrapperHeader.removeClass('hidden-body');
                    $fieldGroupListGridWrapperHeader.parent().find('.fieldgroup-listgrid-wrapper').removeClass('hidden');
                    BLCAdmin.listGrid.paginate.updateGridSize($tbody);
                } else {
                    $fieldGroupListGridWrapperHeader.addClass('hidden-body');
                    $fieldGroupListGridWrapperHeader.parent().find('.fieldgroup-listgrid-wrapper').addClass('hidden');
                }
            }

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
                        $containerActions.prop('disabled', true).prop("forceDisabled", true);
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

            var $alert = $('<span>').addClass('alert-box').addClass(alertType);

            $alert.append('&nbsp;&nbsp;' + message);

            if (options.clearOtherAlerts) {
                $container.find('.alert-box').remove();
            }

            var alertTarget = $container.find('.titlebar:first-child .titlebar-title');
            if (!alertTarget.length) {
                alertTarget = $container.find('label span');
            }

            setTimeout(function () {
                alertTarget.append($alert);
                setTimeout(function () {
                    $alert.fadeOut();
                }, autoClose);
            });
        },

        fixHelper: function (e, ui) {
            ui.closest('tbody').find('tr').children().each(function () {
                $(this).width($(this).width());
            });
            return ui;
        },

        showLoadingSpinner: function ($tbody, spinnerOffset) {
            var $spinner = $tbody.closest('.listgrid-container').find('.listgrid-table-spinner-container');

            if (spinnerOffset) {
                $spinner.css('position', 'absolute');
                $spinner.css('top', spinnerOffset + 'px');
            }

            // Subtract 2 to account for left & right boarder lines
            $spinner.css('width',$tbody.innerWidth() - 2);
            $spinner.css('display', 'block');
        },

        hideLoadingSpinner: function ($tbody) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            $spinner.parent().css('display', 'none');
        },

        initialize: function ($container) {
            BLCAdmin.listGrid.updateActionButtons($container);
            BLCAdmin.listGrid.updateGridTitleBarSize($container.find('.fieldgroup-listgrid-wrapper-header'));

            if (BLCAdmin.listGrid.paginate) {
                BLCAdmin.listGrid.paginate.initialize($container);
            }

            if (BLCAdmin.listGrid.filter) {
                BLCAdmin.listGrid.filter.initialize($container);
            }

            // update date fields
            $($.find("[data-fieldname='dateLabel']")).each(function () {
                var day = moment($(this).html());
                if (day.isValid()) {
                    $(this).html(day.fromNow());
                }
            });

            // update duration fields
            $($.find("[data-fieldname='durationLabel']")).each(function () {
                var day = moment.duration(parseInt($(this).html())).format('h[h] m[m] s[s]');
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
        }
    };

    BLCAdmin.addInitializationHandler(function ($container) {
        $container.find('.listgrid-container').each(function (index, element) {
            BLCAdmin.listGrid.initialize($(element));
        });

        $('body').on('click', '.side-nav ul li a', function () {
            $container.find('.listgrid-container').each(function (index, element) {
                $(element).find('tbody').each(function (index, element) {
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
    $('body').on('click', '.list-grid-table tbody tr', function () {
        var $tr = $(this);
        var $table = $tr.closest('table');
        var listGridType = $table.data('listgridtype');
        var listGridSelectType = $table.data('listgridselecttype');

        if (listGridType != 'main' && !$tr.hasClass('clickable')) {
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

    $('body').on({
        mouseenter: function () {
            var $tr = $(this);

            if ($tr.has('a.sub-list-grid-reorder') && $tr.find('a.sub-list-grid-reorder').css('visibility') === 'hidden') {
                $tr.find('a.sub-list-grid-reorder').css({visibility: 'visible'});
            }
            if ($tr.has('.workflow-action') && $tr.find('.workflow-action').css('visibility') === 'hidden') {
                $tr.find('.workflow-action').css({visibility: 'visible'});
            }
        },
        mouseleave: function () {
            $(this).find('a.sub-list-grid-reorder').css({visibility: 'hidden'});
            $(this).find('.workflow-action').css({visibility: 'hidden'});
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
            BLCAdmin.listGrid.replaceRelatedCollection($(data), {
                message: BLCAdmin.messages.saved + '!',
                alertType: 'save-alert',
                autoClose: 3000,
                clearOtherAlerts: true
            });
            BLCAdmin.hideCurrentModal();
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
        var $container = $(this).closest('div.additional-foreign-key-container');

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
            $this.find('span.display-value').html(displayValue);
            $this.find('input.display-value').val(displayValue);
            $this.find('input.hidden-display-value').val(displayValue);

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

            $valueField.trigger('change', fields);
            $valueField.closest('.field-group').trigger('change');
            BLCAdmin.hideCurrentModal();
        });

        var url = $(this).data('select-url');
        var thisClass = $container.closest('form').find('input[name="ceilingEntityClassname"]').val();
        var thisField = $(this).closest('.field-group').attr('id');
        var handler = BLCAdmin.getDependentFieldFilterHandler(thisClass, thisField);
        if (handler != null) {
            var $parentField = $container.closest('form').find(handler['parentFieldSelector']);
            url = url + '&' + handler['childFieldPropertyName'] + '=' + BLCAdmin.extractFieldValue($parentField);
        }
        if ($(this).data('dynamic-field')) {
            url = url + '&dynamicField=true';
        }

        BLCAdmin.showLinkAsModal(url, function () {
            $('div.additional-foreign-key-container').unbind('valueSelected');
        });

        return false;
    });

    $('body').on('click', 'button.sub-list-grid-add, a.sub-list-grid-add', function () {
        BLCAdmin.showLinkAsModal($(this).attr('data-actionurl'));
        return false;
    });

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

                    if (ui.item.closest('table.list-grid-table').length && ui.item.closest('table.list-grid-table').data('listgridtype') === 'tree') {
                        // Expected uri structure: "/admin/{section}/{child-id}/{alternate-id}/sequence"
                        // Desired uri structure: "/admin/{section}/{parent-id}/{collection-name}/{child-id}/{alternate-id}/sequence"
                        // Beginning: "/admin/{section}"
                        // Middle: "/{parent-id}/{collection-name}"
                        // End: "/{child-id}/{alternate-id}/sequence"
                        var parentId = ui.item.closest('.listgrid-container').data('parentid');
                        var collectionName = ui.item.closest('.tree-listgrid-container').data('collectionname');

                        var thirdToLastIndex = url.lastIndexOf('/', url.lastIndexOf('/', url.lastIndexOf('/') - 1) - 1);
                        var beginning = url.substring(0, thirdToLastIndex);
                        var middle = "/" + parentId + "/" + collectionName;
                        var end = url.substring(thirdToLastIndex);
                        url = beginning + middle + end;
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
                            autoClose: 3000
                        });

                        $container = $this.closest('.listgrid-container');
                        if ($container.prev().length) {
                            var $parent = $container.prev().find('tr.selected');
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

        if ($(this).closest('table').length && $(this).closest('table').data('listgridtype') === 'tree' && $(this).closest('.tree-column-wrapper').length) {
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

    $('body').on('submit', 'form.modal-form', function (event) {
        var $form = $(this);
        var submit = BLCAdmin.runSubmitHandlers($form);

        if (submit) {
            BLC.ajax({
                url: this.action,
                type: "POST",
                data: BLCAdmin.serialize($form)
            }, function (data) {
                BLCAdmin.entityForm.hideActionSpinner($form.closest('.modal').find('.entity-form-actions'));

                //if there is a validation error, replace the current form that's there with this new one
                var $newForm = $(data).find('.modal-body form');
                if ($newForm[0]) {
                    //with adorned forms, we have just overwritten the related id property that was selected previously. Ensure
                    //to replace that in the new form
                    var $adornedTargetIdProperty = $(data).find('input#adornedTargetIdProperty');
                    var isAdornedModal = false;
                    if ($adornedTargetIdProperty[0]) {
                        $adornedTargetIdProperty.val($form.find('input#adornedTargetIdProperty').val());
                        isAdornedModal = true;
                    }
                    //we are potentially replacing a form that has tabs. Ensure those are removed from the replacing form so
                    //we don't get double-tabs
                    $newForm.find('.tabs-container').remove();

                    //ensure the active tab fields on the old form is active on the new form
                    var $modal = $form.closest('.modal');
                    $modal.find('dl.tabs dd').each(function (tabIndex, tab) {
                        if ($(tab).hasClass('active')) {
                            var $contentTabs;
                            $contentTabs = $newForm.find('> ul.tabs-content > li');
                            $contentTabs.removeClass('active');
                            $($contentTabs[tabIndex]).addClass('active').css('display', 'block');
                        }
                    });

                    //swap out the forms
                    $form.replaceWith($newForm);

                    //since we just replaced everything, initialize all the fields back. See BLCAdmin.showModal
                    BLCAdmin.initializeModalTabs($(BLCAdmin.currentModal()));
                    BLCAdmin.initializeModalButtons($(BLCAdmin.currentModal()));
                    BLCAdmin.initializeFields();

                    var $actions = BLCAdmin.currentModal().find('.entity-form-actions');
                    $actions.find('button').show();
                    $actions.find('img.ajax-loader').hide();
                } else {

                    var $assetGrid = $(data).find('.asset-grid-container');
                    if ($assetGrid.length) {
                        var $assetListGrid = $(data).find('.asset-listgrid');

                        BLCAdmin.assetGrid.initialize($assetGrid);

                        $('.asset-grid-container').replaceWith($assetGrid);
                        $('.asset-listgrid').replaceWith($assetListGrid);

                        $('.listgrid-container').each(function (index, container) {
                            BLCAdmin.listGrid.initialize($(container));
                        });
                        BLCAdmin.hideCurrentModal();

                    } else {
                        BLCAdmin.hideCurrentModal();

                        BLCAdmin.listGrid.replaceRelatedCollection($(data), {
                            message: BLCAdmin.messages.saved + '!',
                            alertType: 'save-alert',
                            autoClose: 3000
                        });
                    }
                }
            });
        }
        return false;
    });

    /**
     * Clears out a previously-selected foreign key on both a form and listgrid criteria
     */
    $('body').on('click', 'button.clear-foreign-key', function (event) {
        var $container = $(this).closest('div.additional-foreign-key-container');
        var $this = $(this);

        // Remove the current display value
        var emptyInput = $this.closest('.input-group').find('input:not(:visible)');
        $this.closest('.input-group').find('input:visible').val(emptyInput.val());

        if (typeof BLCAdmin.treeListGrid !== 'undefined') {
            BLCAdmin.treeListGrid.removeParentPathJson($container.closest('.modal-add-entity-form.enterprise-tree-add'));
        }

        // Remove the criteria input val
        $container.find('.value').val('').trigger('change');
        $this.toggle();

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
