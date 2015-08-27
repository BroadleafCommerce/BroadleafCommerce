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
     			complete: hideActionSpinner
     		}, function(data) {
     			$('div.' + href + 'Tab div.listgrid-container div.listgrid-header-wrapper table.list-grid-table').each(function() {
     				var tableId = $(this).attr('id').replace('-header', '');
                    var $tableWrapper = data.find('div.listgrid-header-wrapper:has(table#' + tableId + ')');
     				BLCAdmin.listGrid.replaceRelatedCollection($tableWrapper);
     			});
     			$('div.' + href + 'Tab div.selectize-wrapper').each(function() {
     				var tableId = $(this).attr('id');
                    var $selectizeWrapper = data.find('div.selectize-wrapper#' + tableId);
     				BLCAdmin.listGrid.replaceRelatedCollection($selectizeWrapper);
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

        showActionSpinner($(this).closest('.entity-form-actions'));
        
        // On success this should redirect, on failure we'll get some JSON back
        BLC.ajax({
            url: deleteUrl,
            type: "POST",
            data: $form.serializeArray(),
            complete: hideActionSpinner
        }, function (data) {
            $("#headerFlashAlertBoxContainer").removeClass("hidden");
            $(".errors, .error, .tab-error-indicator, .tabError").remove();
            $('.has-error').removeClass('has-error');

            if (!data.errors) {
                $(".alert-box").removeClass("alert").addClass("success");
                $(".alert-box-message").text("Successfully deleted");
            } else {
                showErrors(data, BLCAdmin.messages.problemDeleting);
            }
            
        });
        
        event.preventDefault();
    });

    $('body').on('click', 'button.submit-button, a.submit-button', function(event) {
        $('body').click(); // Defocus any current elements in case they need to act prior to form submission
        var $form = BLCAdmin.getForm($(this));

        showActionSpinner($(this).closest('.entity-form-actions'));

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
                complete: hideActionSpinner
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
                    showErrors(data, BLCAdmin.messages.problemSaving);
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

    function showActionSpinner($actions) {
        $("#headerFlashAlertBoxContainer").addClass("hidden");
        $actions.find('button').prop("disabled",true);
        $actions.find('img.ajax-loader').show();
    }
    
    /**
     * Should happen after the AJAX request completes
     */
    function hideActionSpinner () {
        var $actions = $('.entity-form-actions');
        $actions.find('button').prop("disabled",false);
        $actions.find('img.ajax-loader').hide();
    }
    
    function showErrors(data, alertMessage) {
        $.each( data.errors , function( idx, error ){
            if (error.errorType == "field") {
                var fieldGroup = $("#field-" + error.field);

                // Add an error indicator to the fields tab
                // this can happen more than once because the indicator is absolute positioning
                var tabId = '#' + fieldGroup.parents('.entityFormTab').attr("class").substring(0,4);
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
            } else if (error.errorType == 'global'){
                var globalError = "<div class='tabError'><b>" + BLCAdmin.messages.globalErrors + "</b><span class='error'>"
                    + error.message + "</span></div>";
                $(".entity-errors").append(globalError);
            }
        });
        $("#headerFlashAlertBoxContainer .alert-box").removeClass("success").addClass("alert");
        $("#headerFlashAlertBoxContainer .alert-box-message").text(alertMessage);
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
                $('.modal .modal-header .tabs-container').replaceWith($(data).find('.modal-body .tabs-container'));
                $('.modal .modal-body .tabs-content').replaceWith($(data).find('.modal-body .tabs-content'));
                var errorDiv = $(data).find('.modal-body .errors');
                if (errorDiv.length) {
                    //since we only replaced the content of the modal body, ensure the error div gets there as well
                    var currentErrorDiv = BLCAdmin.currentModal().find('.modal-body .errors');
                    if (currentErrorDiv.length) {
                        currentErrorDiv.replaceWith(errorDiv)
                    } else {
                        BLCAdmin.currentModal().find('.modal-body').prepend(errorDiv);
                    }
                }
                BLCAdmin.initializeFields($('.modal .modal-body .tabs-content'));
                BLCAdmin.currentModal().find('.submit-button').show();
                BLCAdmin.currentModal().find('img.ajax-loader').hide();
            });
        }
        return false;
    });
});
