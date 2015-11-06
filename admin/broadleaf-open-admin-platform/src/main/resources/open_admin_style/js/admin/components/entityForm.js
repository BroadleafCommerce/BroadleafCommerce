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

    BLCAdmin.entityForm = {
        showActionSpinner : function ($actions) {
            $("#headerFlashAlertBoxContainer").addClass("hidden");
            $actions.find('button').prop("disabled",true);
            $actions.find('img.ajax-loader').show();
        },

        /**
         * Should happen after the AJAX request completes
         */
        hideActionSpinner : function () {
            var $actions = $('.entity-form-actions');
            $actions.find('button').prop("disabled",false);
            $actions.find('img.ajax-loader').hide();
        },

        showErrors : function (data, alertMessage) {
            $.each( data.errors , function( idx, error ){
                if (error.errorType == "field") {
                    var fieldGroup = $("#field-" + error.field);

                    if (fieldGroup.length) {
                        // Add an error indicator to the fields tab
                        // this can happen more than once because the indicator is absolute positioning
                        var tabId = '#' + fieldGroup.parents('.entityFormTab').attr("class").substring(0, 4);
                        var tab = $('a[href=' + tabId + ']');
                        tab.prepend('<span class="tab-error-indicator danger"></span>');

                        // Mark the field as an error
                        var fieldError = "<div class='error'";
                        if (fieldGroup.find(".field-help").length !== 0) {
                            fieldError += " style='margin-top:-5px;'";
                        }
                        fieldError += ">" + error.message + "</div>";

                        $(fieldGroup).append(fieldError);
                        $(fieldGroup).addClass("has-error");
                    } else {
                        var error = "<div class='tabError'><span class='error'>" + error.message + "</span></div>";
                        $(".entity-errors").append(error);
                    }
                } else if (error.errorType == 'global'){
                    var globalError = "<div class='tabError'><b>" + BLCAdmin.messages.globalErrors + "</b><span class='error'>"
                        + error.message + "</span></div>";
                    $(".entity-errors").append(globalError);
                }
            });
            BLCAdmin.entityForm.showErrorHeaderAlert(alertMessage);
        },

        showErrorHeaderAlert : function (alertMessage) {
            if (typeof BLCAdmin.currentModal() === 'undefined') {
                $('#headerFlashAlertBoxContainer .alert-box').removeClass('success').addClass('alert');
                $('#headerFlashAlertBoxContainer .alert-box-message').text(alertMessage);
            } else if (BLCAdmin.currentModal().find('#headerFlashAlertBoxContainer .alert-box').length) {
                var $headerFlashAlertBoxContainer = BLCAdmin.currentModal().find('#headerFlashAlertBoxContainer');
                $headerFlashAlertBoxContainer.find('.alert-box').removeClass('success').addClass('alert');
                $headerFlashAlertBoxContainer.find('.alert-box-message').text(alertMessage);
            } else {
                var $modalFooter = BLCAdmin.currentModal().find('.modal-footer');
                var headerFlashAlertBoxContainer = '<div id="headerFlashAlertBoxContainer"><div id="headerFlashAlertBox" class="alert-box">' +
                    '<span class="alert-box-message"></span></div></div>';
                $modalFooter.append(headerFlashAlertBoxContainer);

                var $headerFlashAlertBoxContainer = $modalFooter.find('#headerFlashAlertBoxContainer');
                $headerFlashAlertBoxContainer.find('.alert-box').removeClass('success').addClass('alert');
                $headerFlashAlertBoxContainer.find('.alert-box-message').text(alertMessage);
            }
        },

        swapModalEntityForm : function ($modal, newHtml) {
            $modal.find('.modal-header .tabs-container').replaceWith($(newHtml).find('.modal-body .tabs-container'));
            // Show field-level validation errors
            $modal.find('.modal-body .tabs-content').replaceWith($(newHtml).find('.modal-body .tabs-content'));
            // Show EntityForm error list
            var errorDiv = $(newHtml).find('.modal-body .errors');
            if (errorDiv.length) {
                //since we only replaced the content of the modal body, ensure the error div gets there as well
                var currentErrorDiv = $modal.find('.modal-body .errors');
                if (currentErrorDiv.length) {
                    currentErrorDiv.replaceWith(errorDiv)
                } else {
                    $modal.find('.modal-body').prepend(errorDiv);
                }
            }
        }
    };
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    var $tabs = $('dl.tabs.entity-form');
    var tabs_action=null;
    if ($tabs.length > 0) {
        var $lastTab = $tabs.find('dd:last');
        if ($lastTab.length && $lastTab.width() + $lastTab.position().left + 15 > $tabs.width()) {
            $tabs.mCustomScrollbar({
                theme: 'dark',
                autoHideScrollbar: true,
                horizontalScroll: true
            });
        }
    }

    $('body div.section-tabs:not(.workflow-tabs) li').find('a').click(function(event) {
        var $tab = $(this);
        var $tabBody = $('.' + $tab.attr('href').substring(1) + 'Tab');
        var text = getTabText($tab.find('span'));
        var $form = BLCAdmin.getForm($tab);
        var href = $(this).attr('href').replace('#', '');
        var currentAction = $form.attr('action');
        var tabUrl = encodeURI(currentAction + '/1/' + text);

     	if (tabs_action && tabs_action.indexOf(tabUrl + '++') == -1 && tabs_action.indexOf(tabUrl) >= 0) {
     		tabs_action = tabs_action.replace(tabUrl, tabUrl + '++');
     	} else if (tabs_action && tabs_action.indexOf(tabUrl) == -1) {
     		tabs_action += ' / ' + tabUrl;
     	} else if (tabs_action == null) {
     		tabs_action = tabUrl;
     	}

     	if (tabs_action.indexOf(tabUrl + '++') == -1 && text != 'General') {
            showTabSpinner($tab, $tabBody);

     		BLC.ajax({
     			url: tabUrl,
     			type: "POST",
     			data: $form.serializeArray(),
     			complete: BLCAdmin.entityForm.hideActionSpinner
     		}, function(data) {
     			$('div.' + href + 'Tab .listgrid-container').find('.listgrid-header-wrapper table').each(function() {
     				var tableId = $(this).attr('id').replace('-header', '');
                    var $tableWrapper = data.find('.listgrid-header-wrapper:has(table#' + tableId + ')');
     				BLCAdmin.listGrid.replaceRelatedCollection($tableWrapper);
     			});
     			$('div.' + href + 'Tab .selectize-wrapper').each(function() {
     				var tableId = $(this).attr('id');
                    var $selectizeWrapper = data.find('.selectize-wrapper#' + tableId);
     				BLCAdmin.listGrid.replaceRelatedCollection($selectizeWrapper);
     			});
                $('div.' + href + 'Tab .media-container').each(function() {
                    var tableId = $(this).attr('id');
                    tableId = tableId.replace(".", "\\.");
                    var $container = data.find('#' + tableId);
                    var $assetGrid = $($container).find('.asset-grid-container');
                    var $assetListGrid = data.find('.asset-listgrid');

                    BLCAdmin.assetGrid.initialize($assetGrid);

                    $(this).find('.asset-grid-container').replaceWith($assetGrid);
                    $(this).find('.asset-listgrid').replaceWith($assetListGrid);

                    $(this).find('.listgrid-container').each(function (index, container) {
                        BLCAdmin.listGrid.initialize($(container));
                    });
                });

                hideTabSpinner($tab, $tabBody);
     		});

     		event.preventDefault();

     	}
     });


    // When the delete button is clicked, we can change the desired action for the
    // form and submit it normally (not via AJAX).
    $('body').on('click', 'button.delete-button, a.delete-button', function(event) {
        var $form = BLCAdmin.getForm($(this));

        var currentAction = $form.attr('action');
        var deleteUrl = currentAction + '/delete'

        BLCAdmin.entityForm.showActionSpinner($(this).closest('.entity-form-actions'));
        
        // On success this should redirect, on failure we'll get some JSON back
        BLC.ajax({
            url: deleteUrl,
            type: "POST",
            data: $form.serializeArray(),
            complete: BLCAdmin.entityForm.hideActionSpinner
        }, function (data) {
            $("#headerFlashAlertBoxContainer").removeClass("hidden");
            $(".errors, .error, .tab-error-indicator, .tabError").remove();
            $('.has-error').removeClass('has-error');

            if (!data.errors) {
                $(".alert-box").removeClass("alert").addClass("success");
                $(".alert-box-message").text("Successfully deleted");
            } else {
                BLCAdmin.entityForm.showErrors(data, BLCAdmin.messages.problemDeleting);
            }
            
        });
        
        event.preventDefault();
    });

    $('body').on('click', 'button.submit-button, a.submit-button', function(event) {
        $('body').click(); // Defocus any current elements in case they need to act prior to form submission
        var $form = BLCAdmin.getForm($(this));

        BLCAdmin.entityForm.showActionSpinner($(this).closest('.content-area-title-bar.entity-form-actions'));

        if ($(".blc-admin-ajax-update").length && $form.parents(".modal-body").length == 0) {
            submitFormViaAjax($form);
        } else {
            $form.submit();
        }

        event.preventDefault();
    });

    function getTabText($tab) {
        return $tab.clone()    //clone the element
            .children() //select all the children
            .remove()   //remove all the children
            .end()  //again go back to selected element
            .text()
            .trim();
    }

    function submitFormViaAjax($form) {
        var submit = BLCAdmin.runSubmitHandlers($form);

        if (submit) {
            BLC.ajax({
                url: $form.action,
                dataType: "json",
                type: "POST",
                data: $form.serializeArray(),
                complete: BLCAdmin.entityForm.hideActionSpinner
            }, function (data) {
                $("#headerFlashAlertBoxContainer").removeClass("hidden");
                $(".errors, .error, .tab-error-indicator, .tabError").remove();
                $('.has-error').removeClass('has-error');

                if (!data.errors) {
                    //$(".alert-box").removeClass("alert").addClass("success");
                    //$(".alert-box-message").text("Successfully saved");

                    var alert = {
                        message: BLCAdmin.messages.saved + '!',
                        alertType: 'save-alert',
                        autoClose: 1000,
                        clearOtherAlerts: true
                    };

                    var $alert = $('<div>').addClass('alert-box list-grid-alert').addClass('save-alert');
                    var $closeLink = $('<a>').attr('href', '').addClass('close').html('&times;');

                    $alert.append("Successfully saved");
                    $alert.append($closeLink);

                    $(".alert-box").find('.alert-box-message').html($alert);

                    setTimeout(function() {
                        $closeLink.click();
                    }, 1000);

                } else {
                    BLCAdmin.entityForm.showErrors(data, BLCAdmin.messages.problemSaving);
                }
                
                BLCAdmin.runPostFormSubmitHandlers($form, data);
            });
        }
    }

    function showTabSpinner($tab, $tabBody) {
        $("#headerFlashAlertBoxContainer").addClass("hidden");
        $tabBody.find('button').prop("disabled", true);
        $tab.find('i.icon-spinner').show();
    }

    function hideTabSpinner($tab, $tabBody) {
        $tabBody.find('button').prop("disabled", false);
        $tab.find('i.icon-spinner').hide();
    }

    $('body').on('submit', 'form.entity-form', function(event) {
        var submit = BLCAdmin.runSubmitHandlers($(this));
        return submit;
    });
    
    $('body').on('submit', 'form.modal-add-entity-form', function(event) {
        var submit = BLCAdmin.runSubmitHandlers($(this));
        
        if (submit) {
            BLC.ajax({
                url: this.action,
                type: "POST",
                data: $(this).serialize()
            }, function(data) {
                var $modal = BLCAdmin.currentModal();
                BLCAdmin.entityForm.swapModalEntityForm($modal, data);

                BLCAdmin.initializeFields($('.modal .modal-body .tabs-content'));
                $modal.find('.submit-button').show();
                $modal.find('img.ajax-loader').hide();
            });
        }
        return false;
    });

    $('body').on('click', 'a.media-link', function(event) {
        event.preventDefault();

        var asset = $(this).closest('.asset-item').find('img');
        var link = $(asset).attr('data-link');
        //link += $(this).attr('data-urlpostfix');
        link += $(this).attr('data-queryparams');

        BLCAdmin.showLinkAsModal(link);
    });

    $('body').on('click', 'a.media-grid-remove', function() {
        var $container = $(this).closest('.asset-listgrid-container');

        var asset = $(this).parent().siblings('img');
        var link = $(asset).attr('data-link');
        link += $(this).attr('data-urlpostfix');
        link += $(this).attr('data-queryparams');

        var id = $(asset).data('rowid');
        var $tr = $(this).closest('.select-group').find('.listgrid-container').find('tr[data-rowid=' + id + ']');
        var rowFields = BLCAdmin.listGrid.getRowFields($tr);

        BLC.ajax({
            url: link,
            data: rowFields,
            type: "POST"
        }, function(data) {
            if (data.status == 'error') {
                BLCAdmin.listGrid.showAlert($container, data.message);
            } else {
                var $assetGrid = $(data).find('.asset-grid-container');
                var $assetListGrid = $(data).find('.asset-listgrid');

                BLCAdmin.assetGrid.initialize($assetGrid);

                $container.find('.asset-grid-container').replaceWith($assetGrid);
                $container.find('.asset-listgrid').replaceWith($assetListGrid);

                $container.find('.listgrid-container').each(function (index, container) {
                    BLCAdmin.listGrid.initialize($(container));
                });
            }
        });

        return false;
    });

    $('body').on('click', '.collapser a', function(event) {
        event.preventDefault();

        var $content = $(this).closest('.fieldset-card').find('.fieldset-card-content');
        if ($(this).hasClass('collapsed')) {
            $(this).removeClass('collapsed').addClass('expanded');
            $(this).find('i').removeClass('fa-angle-down').addClass('fa-angle-up');
            $content.removeClass('content-collapsed');
        } else {
            $(this).removeClass('expanded').addClass('collapsed');
            $(this).find('i').removeClass('fa-angle-up').addClass('fa-angle-down');
            $content.addClass('content-collapsed');
        }

        var $fieldSetCard = $(this).closest('.fieldset-card.listgrid-container');
        var $tbody = $fieldSetCard.find('.listgrid-body-wrapper tbody');
        if ($tbody.length) {
            BLCAdmin.listGrid.paginate.updateGridSize($tbody);
        }
    });

    /**
     * Intercepts the enter keypress from the main entity name input and updates the entity title
     */
    $('body').on('keyup', '.main-entity-name input', function(event) {
        $('.mainEntityName').text($(this).val());
    });
});
