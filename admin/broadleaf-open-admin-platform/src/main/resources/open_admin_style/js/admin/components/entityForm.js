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

    var excludedEFSectionTabSelectors = [];

    BLCAdmin.entityForm = {
        showActionSpinner : function ($actions) {
            $("#headerFlashAlertBoxContainer").addClass("hidden");
            $actions.find('button').prop("disabled",true);
            $actions.find('img.ajax-loader').show();
        },

        addExcludedEFSectionTabSelector : function(selector) {
            excludedEFSectionTabSelectors.push(selector);
        },

        getExcludedEFSectionTabSelectorString : function() {
            var excludedSelectors = '';
            for (var i=0;i<excludedEFSectionTabSelectors.length;i++){
                excludedSelectors += ', ' + excludedEFSectionTabSelectors[i];
            }
            return excludedSelectors;
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
                        var $tabWithError = $('a[href=' + tabId + ']');
                        if (BLCAdmin.currentModal() !== undefined) {
                            $tabWithError = BLCAdmin.currentModal().find('a[href=' + tabId + ']');
                        }
                        $tabWithError.prepend('<span class="tab-error-indicator danger"></span>');

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
        },

        submitFormViaAjax : function ($form) {
            var submit = BLCAdmin.runSubmitHandlers($form);

            if (submit) {
                BLC.ajax({
                    url: $form.attr('action'),
                    dataType: "json",
                    type: "POST",
                    data: BLCAdmin.serializeArray($form)
                }, function (data) {
                    BLCAdmin.entityForm.hideActionSpinner();

                    $(".errors, .error, .tab-error-indicator, .tabError").remove();
                    $('.has-error').removeClass('has-error');

                    if (!data.errors) {

                        var $titleBar = $form.closest('.main-content').find('.content-area-title-bar');
                        BLCAdmin.alert.showAlert($titleBar, 'Successfully ' + BLCAdmin.messages.saved + '!', {
                            alertType: 'save-alert',
                            autoClose: 2000,
                            clearOtherAlerts: true
                        });

                    } else {
                        BLCAdmin.entityForm.showErrors(data, BLCAdmin.messages.problemSaving);
                    }

                    BLCAdmin.runPostFormSubmitHandlers($form, data);
                });
            }
        },

        makeFieldsReadOnly : function ($form) {
            $form = typeof $form !== 'undefined' ? $form : $('body').find('.main-content .content-yield').find('form');

            var $tabsContent =$form.find('.tabs-content');
            // General input fields
            $tabsContent.find('input').prop('disabled', true).addClass('disabled');

            // Radio buttons
            $tabsContent.find('label.radio-label').prop('disabled', true).addClass('disabled');

            // ListGrid actions
            $tabsContent.find('.listgrid-toolbar a').prop('disabled', true).addClass('disabled');
            $tabsContent.find('.listgrid-row-actions a').prop('disabled', true).addClass('disabled');

            // Selectize
            $tabsContent.find('.selectize-control .selectize-input').addClass('disabled');

            // Rule builder
            var $ruleBuilders = $tabsContent.find('.rule-builder-simple, .rule-builder-simple-time, .rule-builder-with-quantity');
            $ruleBuilders.find('.rules-group-header button').prop('disabled', true).addClass('disabled');
            $ruleBuilders.find('.rules-group-body button').prop('disabled', true).addClass('disabled');
            $ruleBuilders.find('.button.and-button').prop('disabled', true).addClass('disabled');
            $ruleBuilders.find('.toggle-container label').addClass('disabled');
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

    $('body div.section-tabs li').find('a:not(".workflow-tab, .system-property-tab' +
            BLCAdmin.entityForm.getExcludedEFSectionTabSelectorString() + '")').click(function(event) {
        var $tab = $(this);
        var $tabBody = $('.' + $tab.attr('href').substring(1) + 'Tab');
        var tabKey = $tab.find('span').data('tabkey');
        var $form = BLCAdmin.getForm($tab);
        var href = $(this).attr('href').replace('#', '');
        var currentAction = $form.attr('action');
        var tabUrl = encodeURI(currentAction + '/1/' + tabKey);

     	if (tabs_action && tabs_action.indexOf(tabUrl + '++') == -1 && tabs_action.indexOf(tabUrl) >= 0) {
     		tabs_action = tabs_action.replace(tabUrl, tabUrl + '++');
     	} else if (tabs_action && tabs_action.indexOf(tabUrl) == -1) {
     		tabs_action += ' / ' + tabUrl;
     	} else if (tabs_action == null) {
     		tabs_action = tabUrl;
     	}

     	if (tabs_action.indexOf(tabUrl + '++') == -1 && !$tab.hasClass('first-tab')) {
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
                    BLCAdmin.listGrid.updateGridTitleBarSize($(this).closest('.listgrid-container').find('.fieldgroup-listgrid-wrapper-header'));
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
                var $titleBar = $form.closest('.main-content').find('.content-area-title-bar');
                BLCAdmin.alert.showAlert($titleBar, 'Successfully ' + BLCAdmin.messages.deleted + '!', {
                    alertType: 'save-alert',
                    autoClose: 2000,
                    clearOtherAlerts: true
                });
            } else {
                BLCAdmin.entityForm.showErrors(data, BLCAdmin.messages.problemDeleting);
            }

            BLCAdmin.runPostFormSubmitHandlers($form, data);
        });
        
        event.preventDefault();
    });

    $('body').on('click', 'button.submit-button, a.submit-button', function(event) {
        if ($(this).hasClass('disabled') || $(this).is(':disabled')) {
            return;
        }

        $('body').click(); // Defocus any current elements in case they need to act prior to form submission
        var $form = BLCAdmin.getForm($(this));

        BLCAdmin.entityForm.showActionSpinner($(this).closest('.content-area-title-bar.entity-form-actions'));

        if ($(".blc-admin-ajax-update").length && $form.parents(".modal-body").length == 0) {
            BLCAdmin.entityForm.submitFormViaAjax($form);
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

    function showTabSpinner($tab, $tabBody) {
        $("#headerFlashAlertBoxContainer").addClass("hidden");
        $tabBody.find('button').prop("disabled", true);
        $tab.find('i.fa-spinner').show();
    }

    function hideTabSpinner($tab, $tabBody) {
        $tabBody.find('button:not(.row-action)').prop("disabled", false);
        $tab.find('i.fa-spinner').hide();
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
                data: BLCAdmin.serialize($(this))
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

        var link = $(this).attr('data-link');
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

    $('body').on('click', 'a.titlebar', function(event) {
        event.preventDefault();

        var $collapser = $(this).find('.collapser span');
        var $content = $(this).closest('.fieldset-card').find('.fieldset-card-content');
        if ($collapser.hasClass('collapsed')) {
            $collapser.removeClass('collapsed').addClass('expanded');
            $collapser.text("(hide)");
            //$collapser.find('i').removeClass('fa-angle-down').addClass('fa-angle-up');
            $content.removeClass('content-collapsed');

            // update content height
            BLCAdmin.updateContentHeight($(this));
        } else {
            $collapser.removeClass('expanded').addClass('collapsed');
            $collapser.text("(show)");
            //$collapser.find('i').removeClass('fa-angle-up').addClass('fa-angle-down');
            $content.addClass('content-collapsed');
        }

        var $fieldSetCard = $(this).closest('.fieldset-card');
        var $tableBodies = $fieldSetCard.find('.listgrid-body-wrapper tbody');
        $tableBodies.each(function( index, tbody ) {
            BLCAdmin.listGrid.paginate.updateGridSize($(tbody));
        });
    });

    $('body').on('click', 'a.description-link', function(event) {
        event.preventDefault();

        var field = $(this).closest('.description-field');
        // show the textarea or input
        if ($(field).find('textarea').length) {
            $(field).find('textarea').show().focus();
        } else {
            $(field).find('input').show().focus();
        }

        // hide the read-only text
        $(this).parent().hide();
    });

    $('body').on('blur', '.description-field textarea, .description-field input', function(event) {
        event.preventDefault();

        var field = $(this).closest('.description-field');

        // hide the textarea or input
        var descriptionText = $(field).find('textarea').val();
        if (descriptionText !== undefined) {
            $(field).find('textarea').hide();
        } else {
            descriptionText = $(field).find('input').val();
            $(field).find('input').hide();
        }

        // show the read-only text
        if (descriptionText.length) {
            descriptionText += '<a href="" class="description-link">&nbsp;&nbsp;(edit)</a>';
        } else {
            descriptionText = 'No description provided, <a href="" class="description-link">add one</a>';
        }
        $(field).find('.description-text').html(descriptionText);
        $(field).find('.description-text').show();
    });
});
