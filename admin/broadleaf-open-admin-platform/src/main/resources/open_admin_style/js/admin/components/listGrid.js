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
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.listGrid = {
        replaceRelatedListGrid : function($headerWrapper, alert, opts) {
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

            var oldParams = $oldTable.closest('.listgrid-container').find('.listgrid-header-wrapper table').data('currentparams');
            var newParams = $table.data('currentparams');
            var secondaryFetch = new $.Deferred();

            if (oldParams != undefined && oldParams != newParams && (opts == undefined || opts.isRefresh)) {
                var url = $table.data('path');
                for (var param in oldParams) {
                    url = BLCAdmin.history.getUrlWithParameter(param, oldParams[param], null, url);
                }
                
                BLC.ajax({
                    url: url,
                    type: "GET"
                }, function(data) {
                    $table = $(data).find('table');
                    secondaryFetch.resolve("retrieved");
                })
            } else {
                secondaryFetch.resolve("skipping");
            }
            
            $.when(secondaryFetch.promise()).then(function(status) {
                var currentIndex = BLCAdmin.listGrid.paginate.getTopVisibleIndex($oldTable.find('tbody'));
                
                var $oldBodyWrapper = $oldTable.closest('.listgrid-body-wrapper');
                var $oldHeaderWrapper = $oldBodyWrapper.prev();
                
                $oldHeaderWrapper.find('thead').after($table.find('tbody'));
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

                $listGridContainer.trigger('blc-listgrid-replaced', $listGridContainer);
            });
        },
        
        getActionLink : function($trigger) {
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
        
        getRowFields : function($tr) {
            var fields = {};

            // add the id to the array
            if ($tr.data('rowid')) {
                fields['id'] = $tr.data('rowid');
            }
            
            $tr.find('td').each(function() {
                var fieldName = $(this).data('fieldname');
                var value = $(this).data('fieldvalue');
                fields[fieldName] = value;
            });

            //add hidden fields to the array
            var hiddenFields = $tr.data('hiddenfields');
            for (j=0;j<hiddenFields.hiddenFields.length;j++) {
                var fieldName = hiddenFields.hiddenFields[j].name;
                var value = hiddenFields.hiddenFields[j].val;
                fields[fieldName] = value;
            }
            
            
            return fields;
        },
        
        getSelectedRowIds : function($button) {
            var link = BLC.servletContext + $button.data('urlpostfix');
            
            var $container = $button.closest('.listgrid-container');
            var $selectedRows = $container.find('table tr.selected');
            
            var selectedRowIds = [];
            $selectedRows.each(function(index, element) {
                selectedRowIds.push($(element).data('rowid'));
            });
            
            return selectedRowIds;
        },
        
        updateRowActionButtons : function($listGridContainer) {
            var numSelected = $listGridContainer.find('tr.selected').length;
            if (numSelected) {
                $listGridContainer.find('button.row-action').removeAttr('disabled');
            } else {
                $listGridContainer.find('button.row-action').attr('disabled', 'disabled');
            }
            
            if (!$listGridContainer.find('td.list-grid-no-results').length) {
                $listGridContainer.find('button.row-action.all-capable').removeAttr('disabled');
            }
            
            if (numSelected > 1) {
                $listGridContainer.find('button.row-action.single-action-only').attr('disabled', 'disabled');
            }
        },
        
        showAlert : function($container, message, options) {
            options = options || {};
    	    var alertType = options.alertType || '';
    	    
    	    var $alert = $('<li>').addClass('alert-box list-grid-alert').addClass(alertType);
    	    var $closeLink = $('<a>').attr('href', '').addClass('close').html('&times;');
    	    
    	    $alert.append(message);
    	    $alert.append($closeLink);
    	    
    	    if (options.clearOtherAlerts) {
    	        $container.children('.list-grid-alert').find('a.close').click();
    	    }

            $container.find('.listgrid-toolbar ul').append($alert);
    	    
    	    if (options.autoClose) {
    	        setTimeout(function() {
    	            $closeLink.click();
    	        }, options.autoClose);
    	    }
        },
        
        fixHelper : function(e, ui) {
            ui.closest('tbody').find('tr').children().each(function() {
                $(this).width($(this).width());
            });
            return ui;
        },
        
        showLoadingSpinner : function($tbody, spinnerOffset) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            
            if (spinnerOffset) {
                $spinner.css('position', 'absolute').css('top', spinnerOffset + 'px');
            }
            
            $spinner.parent().css('display', 'block');
        },
        
        hideLoadingSpinner : function($tbody) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            $spinner.parent().css('display', 'none');
        },
        
        initialize : function($container) {
            BLCAdmin.listGrid.updateRowActionButtons($container);
            
            if (BLCAdmin.listGrid.paginate) {
                BLCAdmin.listGrid.paginate.initialize($container);
            }
            
            if (BLCAdmin.listGrid.filter) {
                BLCAdmin.listGrid.filter.initialize($container);
            }
        },
        
        getListGridCount : function($container) {
            return $container.find('.listgrid-container').length;
        }
    };
    
    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.listgrid-container').each(function(index, element) {
            BLCAdmin.listGrid.initialize($(element));
        });
    });
    
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    /**
     * Bind a handler to trigger anytime a table row is clicked on any list grid. 
     * 
     * After assembling information, this will delegate to the specialized rowSelected
     * handler for this particular kind of list grid.
     */
    $('body').on('click', '.list-grid-table tbody tr', function() {
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
        
        if ($tr.find('td.list-grid-no-results').length == 0 && !$table.hasClass('reordering')) {

            $('body').trigger('listGrid-' + listGridSelectType + '-rowSelected', [link, fields, currentUrl]);

            // If Adorned or Asset ListGrid, process row click by adding item id to form.
            // Else, wait for confirmation button click.
            if (listGridType === 'adorned_with_form' || listGridType === 'adorned'|| listGridType === 'asset') {
                $('body').trigger('listGrid-' + listGridType + '-rowSelected', [link, fields, currentUrl]);
            }
        }
    });
    
    /**
     * The rowSelected handler for the main list grid doesn't do anything by default
     */
    $('body').on('listGrid-main-rowSelected', function(event, link, fields, currentUrl) {
    });

    /**
     * The rowSelected handler for the inline list grid ...
     */
    function inlineRowSelected(event, link, fields, currentUrl, multi) {
        var $tr = $('tr[data-link="' + link + '"]');
        var currentlySelected = $tr.hasClass('selected');
        var $listGridContainer = $tr.closest('.listgrid-container');
        var $tbody = $tr.closest("tbody");
        var $listgridHeader = $tbody.closest(".listgrid-body-wrapper").prev();
        
        if (!multi) {
            $tr.closest('tbody').find('tr').removeClass('selected');
            $tr.closest('tbody').find('tr').find('input[type=checkbox].listgrid-checkbox').prop('checked', false);
        }
        
        if (!currentlySelected) {
            $tr.addClass("selected");
            $tr.find('input[type=checkbox].listgrid-checkbox').prop('checked', true);
        } else {
            $tr.removeClass("selected");
            $tr.find('input[type=checkbox].listgrid-checkbox').prop('checked', false);
        }

        updateMultiSelectCheckbox($tbody, $listgridHeader)
        
        BLCAdmin.listGrid.updateRowActionButtons($listGridContainer);
    }
    $('body').on('listGrid-single_select-rowSelected', function(event, link, fields, currentUrl) {
        inlineRowSelected(event, link, fields, currentUrl, false);
    });
    $('body').on('listGrid-translation-rowSelected', function(event, link, fields, currentUrl) {
        inlineRowSelected(event, link, fields, currentUrl, false);
    });
    $('body').on('listGrid-multi_select-rowSelected', function(event, link, fields, currentUrl) {
        inlineRowSelected(event, link, fields, currentUrl, true);
    });
    
    /**
     * The rowSelected handler for a toOne list grid needs to trigger the specific valueSelected handler 
     * for the field that we are performing the to-one lookup on.
     */
    $('body').on('listGrid-to_one-rowSelected', function(event, link, fields, currentUrl) {
        $('div.additional-foreign-key-container').trigger('valueSelected', [fields, link, currentUrl]);
    });
    
    /**
     * The rowSelected handler for a simpleCollection list grid ...
     */
    $('body').on('listGrid-basic-rowSelected', function(event, link, fields, currentUrl) {
        var postData = {};
        
        for (var key in fields){
            if (fields.hasOwnProperty(key)){
                postData["fields['" + key + "'].value"] = fields[key];
            }
        }   
        
        BLC.ajax({
            url : currentUrl,
            type : "POST",
            data : postData
        }, function(data) {
            BLCAdmin.listGrid.replaceRelatedListGrid($(data), { 
                message: BLCAdmin.messages.saved + '!', 
                alertType: 'save-alert', 
                autoClose: 1000 
            });
            BLCAdmin.hideCurrentModal();
        })
    });
    
    /**
     * The rowSelected handler for an adornedTarget list grid. This is specific to adorned target
     * lists that do not have any additional maintained fields. In this case, we can simply
     * submit the form directly.
     */
    $('body').on('listGrid-adorned-rowSelected', function(event, link, fields, currentUrl) {
        $(this).find('input#adornedTargetIdProperty').val(fields['id']);
    });
    
    /**
     * The rowSelected handler for an adornedTargetWithForm list grid. Once the user selects an entity,
     * show the form with the additional maintained fields.
     */
    $('body').on('listGrid-adorned_with_form-rowSelected', function(event, link, fields, currentUrl) {
        $(this).find('input#adornedTargetIdProperty').val(fields['id']);
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
    $('body').on('click', '.to-one-lookup', function(event) {
        var $container = $(this).closest('div.additional-foreign-key-container');
        
        $container.on('valueSelected', function(event, fields, link, currentUrl) {
            var $this = $(this);
            var displayValueProp = $this.find('input.display-value-property').val();
            
            var displayValue = fields[displayValueProp];
            var $selectedRow = BLCAdmin.currentModal().find('tr[data-link="' + link + '"]');
            var $displayField = $selectedRow.find('td[data-fieldname=' + displayValueProp + ']');
            if ($displayField.hasClass('derived')) {
                displayValue = $.trim($displayField.text());
            }
            
            var $valueField = $this.find('input.value');
            $valueField.val(fields['id']);
            $this.find('span.display-value').html(displayValue);
            $this.find('input.hidden-display-value').val(displayValue);
            
            // Ensure that the clear button shows up after selecting a value
            $this.find('button.clear-foreign-key').show();
            
            // Ensure that the external link button points to the correct URL
            var $externalLink = $this.find('span.external-link-container a')
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
                        url : url,
                        type : "GET"
                    }, function(data) {
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

        BLCAdmin.showLinkAsModal(url, function() {
            $('div.additional-foreign-key-container').unbind('valueSelected');
        });
        
        return false;
    });
    
    $('body').on('click', 'button.sub-list-grid-add', function() {
        BLCAdmin.showLinkAsModal($(this).attr('data-actionurl'));
        return false;
    });
    
    $('body').on('click', 'button.sub-list-grid-reorder', function() {
        var $container = $(this).closest('.listgrid-container');
        var $table = $container.find('table');
        var $tbody = $table.find('tbody');
        var $trs = $tbody.find('tr');
        var doneReordering = $table.hasClass('reordering');
        
        $table.toggleClass('reordering');
        
        if (doneReordering) {
            $container.find('.listgrid-icon').removeClass('fa fa-arrows-v fa-lg');

            $container.find('.listgrid-toolbar button').removeAttr('disabled');
            $(this).html($('<i>', { 'class' : 'icon-move' }));
            $(this).append(' ' + BLCAdmin.messages.reorder);
            
            BLCAdmin.listGrid.updateRowActionButtons($container);
            
            $trs.removeClass('draggable').addClass('clickable');
            $tbody.sortable("destroy");
        } else {
            $container.find('.listgrid-icon').addClass('fa fa-arrows-v fa-lg');

            $container.find('.listgrid-toolbar button').attr('disabled', 'disabled');
            $(this).removeAttr('disabled').html(BLCAdmin.messages.done);
            
            $trs.removeClass('clickable').addClass('draggable');
            
            $tbody.sortable({
                helper : BLCAdmin.listGrid.fixHelper,
                update : function(event, ui) {
                    BLC.ajax({
                        url : ui.item.data('link') + '/sequence',
                        type : "POST",
                        data : {
                            newSequence : ui.item.index()
                        }
                    }, function(data) {
                        var $container = $('div.listgrid-container#' + data.field);
                        BLCAdmin.listGrid.showAlert($container, BLCAdmin.messages.saved + '!', { 
                            alertType: 'save-alert', 
                            autoClose: 400 
                        });
                    });
                }
            }).disableSelection();
        }
        
        return false;
    });
    
    $('body').on('click', 'a.sub-list-grid-remove, button.sub-list-grid-remove', function() {
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
        }, function(data) {
            if (data.status == 'error') {
                BLCAdmin.listGrid.showAlert($container, data.message);
            } else {
                BLCAdmin.listGrid.replaceRelatedListGrid($(data), { 
                    message: BLCAdmin.messages.saved + '!', 
                    alertType: 'save-alert', 
                    autoClose: 1000 
                });
            }
        });
        
        return false;
    });
    
    $('body').on('click', 'a.sub-list-grid-update, button.sub-list-grid-update', function() {
        var link = BLCAdmin.listGrid.getActionLink($(this));
        
        BLCAdmin.showLinkAsModal(link);
        
        return false;
    });

    $('body').on('click', 'button.sub-list-grid-view', function() {
        var link = BLCAdmin.listGrid.getActionLink($(this));

        BLCAdmin.showLinkAsModal(link);

        return false;
    });

    $('body').on('click', 'button.list-grid-single-select', function() {
        var $container = $(this).closest('.listgrid-container');
        var $table = $container.find('table');
        var $selectedRow = $table.find('tr.selected');
        var listGridType = $table.data('listgridtype');

        var link = $selectedRow.data('link');
        var fields = BLCAdmin.listGrid.getRowFields($selectedRow);
        var currentUrl = $container.find("table").data('currenturl');

        $('body').trigger('listGrid-' + listGridType + '-rowSelected', [link, fields, currentUrl]);
    });
    
    $('body').on('submit', 'form.modal-form', function(event) { 
        var $form = $(this);
        var submit = BLCAdmin.runSubmitHandlers($form);
        
        if (submit) {
            BLC.ajax({
                url: this.action,
                type: "POST",
                data: $(this).serialize()
            }, function(data) {
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
                    $modal.find('dl.tabs dd').each(function(tabIndex, tab) {
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
                    BLCAdmin.listGrid.replaceRelatedListGrid($(data), { 
                        message: BLCAdmin.messages.saved + '!', 
                        alertType: 'save-alert', 
                        autoClose: 1000 
                    });
                    BLCAdmin.hideCurrentModal();
                }
            });
        }
        return false;
    });
        
    /**
     * Clears out a previously-selected foreign key on both a form and listgrid criteria
     */
    $('body').on('click', 'button.clear-foreign-key', function(event) {        
        var $container = $(this).closest('div.additional-foreign-key-container');
        var $this = $(this);
        // Remove the current display value
        $this.prev().html($(this).prev().prev().html());
        
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
    
    $('body').on('click', 'button.clear-asset-selector', function(event) {
    	//Get the media container
        var $container = $(this).closest('div.asset-selector-container');
        var $this = $(this);
        
        //Set media value to null so that when the request is sent the entry in the map for primary is deleted
        $container.find('input.mediaItem').val('null').trigger('change');
        
        //Set placeholder image and hide clear button since there's nothing to clear
        var src = $container.find('img.placeholder').attr('src');
        $container.find('img.thumbnail').attr('src', src);
        $container.find('img.thumbnail').addClass('placeholder-image');
        $this.hide();
        
    });
    
    $('body').on('mouseover', 'td.row-action-selector', function(event) {
        $(this).find('ul.row-actions').show();
    });
    
    $('body').on('mouseout', 'td.row-action-selector', function(event) {
        $(this).find('ul.row-actions').hide();
    });

    $('body').on('click', 'input[type=checkbox].multiselect-checkbox', function(event) {
        var $listgridBody = $(this).closest(".listgrid-header-wrapper").next();
        if ($(this).prop('checked')) {
            $listgridBody.find(".listgrid-checkbox").prop('checked', true);
            $listgridBody.find(".list-grid-table tbody tr:not(.selected)").click();
        } else {
            $listgridBody.find(".listgrid-checkbox").prop('checked', false);
            $listgridBody.find(".list-grid-table tbody tr.selected").click();
        }
    });

    $('body').on('click', 'input[type=checkbox].listgrid-checkbox', function(event) {
        var $listgridHeader = $(this).closest(".listgrid-body-wrapper").prev();
        var $tbody = $(this).closest("tbody");

        updateMultiSelectCheckbox($tbody, $listgridHeader);
    });

    $('body').on('click', 'td.listgrid-row-actions', function(event) {
        return false;
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

    $("input[type=checkbox].listgrid-checkbox").prop('checked', false);
    $("input[type=checkbox].multiselect-checkbox").prop('checked', false);

});

