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
/* Utility methods provided by Broadleaf Commerce for admin */
var BLCAdmin = (function($) {
    
    // This will keep track of our current active modals so that we are able to overlay them
    var modals = [];
    var preValidationFormSubmitHandlers = [];
    var validationFormSubmitHandlers = [];
    var postValidationFormSubmitHandlers = [];
    var postFormSubmitHandlers = [];
    var dependentFieldFilterHandlers = {};
    var initializationHandlers = [];
    var selectizeInitializationHandlers = [];
    var selectizeUpdateHandlers = [];
    var excludedSelectizeSelectors = [];
    var updateHandlers = [];
    var fieldInitializationHandlers = [];
    var stackedModalOptions = {
        left: 20,
        top: 20
    };

    var fieldSelectors = '>div>input:not([type=hidden]), .custom-checkbox, .foreign-key-value-container, .redactor_box, ' +
                         '.asset-selector-container .media-image, >div>select, div.custom-checkbox, div.small-enum-container, .ace-editor, ' +
                         'textarea:not(.redactor-box textarea), div.radio-container, >.selectize-control>.selectize-input, .redactor-box, .description-field, ' +
                         '.rule-builder-simple-time, .rule-builder-simple, .rule-builder-with-quantity, >div>div>input:not([type=hidden]), .selectize-wrapper';
    
    function showModal($data, onModalHide, onModalHideArgs) {
        // If we already have an active modal, we don't need another backdrop on subsequent modals
        $data.modal({
            backdrop: (modals.length < 1),
            keyboard: false  // disable default keyboard behavior; wasn't intended to work with layered modals
        });

        // If we already have an active modal, we need to modify its z-index so that it will be
        // hidden by the current backdrop
        if (modals.length > 0) {
            modals.last().css('z-index', '1040');

            // We will also offset modals by the given option values
            $data.css('left', $data.position().left + (stackedModalOptions.left * modals.length) + 'px');
            $data.css('top', $data.position().top + (stackedModalOptions.top * modals.length) + 'px');
        }

        // Save our new modal into our stack
        modals.push($data);
        // Bind a callback for the modal hidden event...
        $data.on('hidden', function() {

            // Allow custom callbacks
            if (onModalHide != null) {
                onModalHide(onModalHideArgs);
            }

            // Remove all filterBuilders that were created for this modal that is being removed
            $(this).find('.filter-info .filter-button').each(function() {
                BLCAdmin.filterBuilders.removeFilterBuilderByHiddenId($(this).data('hiddenid'));
            });

            // Remove the modal from the DOM and from our stack
            $(this).remove();
            modals.pop();

            // If this wasn't the only modal, take the last modal and put it above the backdrop
            if (modals.length > 0) {
                var $backdrop = $('.modal-backdrop');
                $backdrop.css('z-index', '1051'); // 1051 was the original z-index for the backdrop

                // Move the last modal to one above the backdrop
                modals.last().css('z-index', parseInt($backdrop.css('z-index')) + 1);
            }

            if (BLCAdmin.currentModal()) {
                BLCAdmin.currentModal().find('.submit-button').show();
                BLCAdmin.currentModal().find('img.ajax-loader').hide();
            }
        });


        // Only initialize all fields if NOT a normal EntityForm in modal
        // Should initialize for lookups
        if (BLCAdmin.currentModal().find('.modal-body>.content-yield .entity-form.modal-form').length === 0) {
            BLCAdmin.initializeFields(BLCAdmin.currentModal());
        } else {
            BLCAdmin.initializeModalTabs($data);
        }

        BLCAdmin.initializeModalButtons($data);
        BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
    }

    function getDependentFieldFilterKey(className, childFieldName) {
        return className + '-' + childFieldName;
    }

    return {
        /**
         * Handlers to run before client-side validation takes place
         */
        addPreValidationSubmitHandler : function(fn) {
            preValidationFormSubmitHandlers.push(fn);
        },

        /**
         * Handlers explicitly designed to validate forms on the client before submitting to the server. If a single handler
         * in the list of client-side validation handlers returns 'false', then the form will not be submitted to the
         * server
         */
        addValidationSubmitHandler : function(fn) {
            validationFormSubmitHandlers.push(fn);
        },
        
        /**
         * Handlers designed to execute after validation has taken place but before the form has been submitted to the server
         */
        addPostValidationSubmitHandler : function(fn) {
            postValidationFormSubmitHandlers.push(fn);
        },
        
        /**
         * Add a form submission handler that runs after the form has been submitted via AJAX. These are designed to execute
         * after errors have already been calculated.
         * 
         * Method signatures should take in 2 arguments:
         *  $form - the JQuery form object that was submitted
         *  data - the data that came back from the server
         */
        addPostFormSubmitHandler : function(fn) {
            postFormSubmitHandlers.push(fn);
        },

        addInitializationHandler : function(fn) {
            initializationHandlers.push(fn);
        },

        addSelectizeInitializationHandler : function(fn) {
            selectizeInitializationHandlers.push(fn);
        },

        addSelectizeUpdateHandler : function(fn) {
            selectizeUpdateHandlers.push(fn);
        },

        addUpdateHandler : function(fn) {
            updateHandlers.push(fn);
        },

        addExcludedSelectizeSelector : function(selector) {
            excludedSelectizeSelectors.push(selector);
        },

        /**
         * Add a field initialization handler that runs before normal field initialization. If any of the handlers return
         * false then all subsequent execution is exited
         *
         * Method signatures should take 1 argument:
         *  $container - the html container that inputs are being initialized for
         */
        addFieldInitializationHandler : function(fn) {
            fieldInitializationHandlers.push(fn);
        },

        runPreValidationSubmitHandlers : function($form) {
            for (var i = 0; i < preValidationFormSubmitHandlers.length; i++) {
                preValidationFormSubmitHandlers[i]($form);
            }
        },

        /**
         * Returns false if a single validation handler returns false. All validation handlers are iterated through before
         * returning. If this method returns false, then the form should not be submitted to the server
         */
        runValidationSubmitHandlers : function($form) {
            var pass = true;
            for (var i = 0; i < validationFormSubmitHandlers.length; i++) {
                pass = pass && validationFormSubmitHandlers[i]($form);
            }
            return pass;
        },
        
        runPostValidationSubmitHandlers : function($form) {
            for (var i = 0; i < postValidationFormSubmitHandlers.length; i++) {
                postValidationFormSubmitHandlers[i]($form);
            }
        },
        
        /**
         * Intended to run after
         */
        runPostFormSubmitHandlers : function($form, data) {
            for (var i = 0; i < postFormSubmitHandlers.length; i++) {
                postFormSubmitHandlers[i]($form, data);
            }
        },
        
        /**
         * Convenience method to submit all pre-validation, validation and post-validation handlers for the form. This will
         * return the result of invoking 'runValidationSubmitHandlers' to denote whether or not the form should actually
         * be submitted to the server
         */
        runSubmitHandlers : function($form) {
            BLCAdmin.runPreValidationSubmitHandlers($form);
            var submit = BLCAdmin.runValidationSubmitHandlers($form);
            BLCAdmin.runPostValidationSubmitHandlers($form);
            return submit;
        },

        /**
         * Runs all of the field initialization handlers. Returns a boolean indicating if normal field initialization
         * should continue or not
         */
        runFieldInitializationHandlers : function($container) {
            for (var i = 0; i < fieldInitializationHandlers.length; i++) {
                var continueInitialization = fieldInitializationHandlers[i]($container);
                if (continueInitialization != undefined && !continueInitialization) {
                    return false;
                }
            }
            return true;
        },

        /**
         * Runs all of the field initialization handlers. Returns a boolean indicating if normal field initialization
         * should continue or not
         */
        runSelectizeUpdateHandlers : function($container) {
            for (var i = 0; i < selectizeUpdateHandlers.length; i++) {
                selectizeUpdateHandlers[i]($container);
            }
        },

        setModalMaxHeight : function($modal) {
            // Resize the modal height to the user's browser
            var availableHeight = $(window).height()
                - $modal.find('.modal-header').outerHeight()
                - $modal.find('.modal-footer').outerHeight()
                - ($(window).height() * 0.1);

            $modal.find('.modal-body').css('max-height', availableHeight);
        },

        getModalSkeleton : function getModalSkeleton() {
            var $modal = $('<div>', { 'class' : 'modal' });

            var $modalHeader = $('<div>', {
                'class' : 'modal-header'
            });
            $modalHeader.append($('<h3>'));
            $modalHeader.append($('<button>', {
                'class' : 'close',
                'data-dismiss' : 'modal',
                'html' : '&times;'
            }));
            $modal.append($modalHeader);

            var $modalBody = $('<div>', {
                'class' : 'modal-body'
            });
            $modal.append($modalBody);

            var $modalFooter = $('<div>', {
                'class' : 'modal-footer'
            });
            $modal.append($modalFooter);

            return $modal;
        },

        initializeModalTabs : function($data) {
            $.fn.broadleafTabs();

            BLCAdmin.currentModal().find('.nav-tabs li.active > a').click();
        },

        initializeModalButtons : function($data) {
            var $buttonDiv = $data.find('div.entity-form-actions');
            if ($buttonDiv.length > 0) {
                var $footer = $data.find('div.modal-footer');
                if (!$footer.length) {
                    $footer = $('<div>', { 'class' : 'modal-footer' });
                    $data.append($footer);
                }
                $buttonDiv.remove().appendTo($footer);
            }

            var $buttonDiv = $data.find('div.listgrid-modal-actions');
            if ($buttonDiv.length > 0) {
                var $footer = $data.find('div.modal-footer');
                if (!$footer.length) {
                    $footer = $('<div>', { 'class' : 'modal-footer' });
                    $data.append($footer);
                }
                $buttonDiv.remove().appendTo($footer);
            }
    	},

        showMessageAsModal : function(header, message) {
            this.showMessageAsModalWithCallback(header, message);
        },

        showMessageAsModalWithCallback : function(header, message, onModalHide, onModalHideArgs) {
            if (BLCAdmin.currentModal() != null && BLCAdmin.currentModal().hasClass('loading-modal')) {
                BLCAdmin.hideCurrentModal();
            }

            var $modal = BLCAdmin.getModalSkeleton();

            $modal.find('.modal-header h3').text(header);
            $modal.find('.modal-body').append(message);
            $modal.find('.modal-body').css('padding-bottom', '20px');

            this.showElementAsModal($modal, onModalHide, onModalHideArgs);
        },

        showElementAsModal : function($element, onModalHide, onModalHideArgs) {
            if (BLCAdmin.currentModal() != null && BLCAdmin.currentModal().hasClass('loading-modal')) {
                BLCAdmin.hideCurrentModal();
            }

            if (!$element.find('.content-yield').length) {
                var content = $('<div>', { 'class': 'content-yield'});
                $element.find('.modal-body').wrapInner(content);
            }
            $('body').append($element);
            showModal($element, onModalHide, onModalHideArgs);
        },

        showLinkAsModal : function(link, onModalHide, onModalHideArgs) {
            // Show a loading message
            var $modal = BLCAdmin.getModalSkeleton();
            $modal.addClass('loading-modal');
            $modal.find('.modal-header h3').text(BLCAdmin.messages.loading);
            $modal.find('.modal-body').append($('<i>', { 'class' : 'fa-pulse fa fa-spinner' }));
            $modal.find('.modal-body').css('text-align', 'center').css('font-size', '24px').css('padding-bottom', '15px');

            BLCAdmin.showElementAsModal($modal, onModalHide, onModalHideArgs);
            
            // Then replace it with the actual requested link
            BLCAdmin.modalNavigateTo(link);
        },

        // Convenience function for hiding the replacing the current modal with the given link
        modalNavigateTo : function(link) {
            if (BLCAdmin.currentModal()) {
                BLCAdmin.currentModal().data('initialized', 'false');
                BLC.ajax({
                    url : link,
                    type : "GET"
                }, function(data) {
                    // Create a modal out of the server response
                    var $data = $(data);

                    // check if the modal has any additional classes
                    var classes = $data.attr('class').split(' ');
                    if (classes.length > 2) {
                        for (var i = 2; i < classes.length; i++) {
                            BLCAdmin.currentModal().addClass(classes[i]);
                        }
                    }

                    $data = $data.children();

                    BLCAdmin.currentModal().empty().append($data);

                    if (!BLCAdmin.currentModal().find('.content-yield').length) {
                        var content = $('<div>', { 'class': 'content-yield'});
                        BLCAdmin.currentModal().find('.modal-body').wrapInner(content);
                    }

                    // Only initialize all fields if NOT a normal EntityForm in modal
                    // Should initialize for lookups
                    if (BLCAdmin.currentModal().find('.modal-body>.content-yield .entity-form.modal-form').length === 0) {
                        BLCAdmin.initializeFields(BLCAdmin.currentModal());
                    } else {
                        BLCAdmin.initializeModalTabs($data);
                    }

                    BLCAdmin.initializeModalButtons(BLCAdmin.currentModal());
                    BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());

                    BLCAdmin.currentModal().removeClass('loading-modal');

                    if (BLCAdmin.currentModal().hasClass('asset-selector')) {
                        var $header = BLCAdmin.currentModal().find('.modal-header');
                        var $closeBtn = $header.find('.close');
                        var $tabSection = BLCAdmin.currentModal().find('.modal-body .section-tabs');

                        $tabSection.append($closeBtn);
                        $header.hide();
                    }

                    // disable submit button if adorned/adornedWith form (must select a listGrid item to enable submission)
                    if (BLCAdmin.currentModal().find('.adorned-select-wrapper').length) {
                        var $submitButton = BLCAdmin.currentModal().find("button[type='submit']");
                        $submitButton.prop('disabled', true);
                    }

                    BLCAdmin.currentModal().trigger('content-loaded');
                });
            } else {
                BLCAdmin.showLinkAsModal(link);
            }
        },

        // Convenience function for returning the current modal
        currentModal : function() {
            return modals.last();
        },

        hideCurrentModal : function() {
            if (BLCAdmin.currentModal()) {
                BLCAdmin.currentModal().modal('hide');
            }
        },

        hideAllModals : function() {
            var currentModal = BLCAdmin.currentModal();
            while (currentModal) {
                currentModal.modal('hide');
                currentModal = BLCAdmin.currentModal();
            }
        },

        focusOnTopModal : function() {
            if (BLCAdmin.currentModal()) {
                BLCAdmin.currentModal().focus();
            }
        },

        getActiveTab : function() {
            var $modal = this.currentModal();
            if ($modal != null) {
                var $tabs = $modal.find('div.section-tabs');

                if ($tabs.length == 0) {
                    return $modal;
                } else {
                    return $modal.find('.entityFormTab.active');
                }

            } else {
                var $body = $('body');
                var $tabs = $body.find('div.section-tabs');

                if ($tabs.length == 0) {
                    return $body;
                } else {
                    return $body.find('.entityFormTab.active');
                }
            }
        },

        initializeFields : function($container) {

            // If there is no container specified, we'll initialize the active tab (or the body if there are no tabs)
            if ($container == null) {
                $container = BLCAdmin.getActiveTab();
            }

            // run field initialization handlers and see if we should continue initializing fields
            var continueInitialization = BLCAdmin.runFieldInitializationHandlers($container);

            // If we've already initialized this container, we'll skip it.
            if ($container.data('initialized') === 'true' || !continueInitialization) {
                if ($container.closest('.oms-tab').length) {
                    return;
                }
                // Update all listgrids sizing on the current tab just in case.
                $container.find('.listgrid-container tbody').each(function (index, element) {
                    BLCAdmin.listGrid.updateGridTitleBarSize($(element).closest('.listgrid-container').find('.fieldgroup-listgrid-wrapper-header'));
                    BLCAdmin.listGrid.paginate.updateGridSize($(element));
                });
                return;
            }

            // Set the blank value for foreign key lookups
            $container.find('.foreign-key-value-container').each(function(index, element) {
                var $displayValue = $(this).find('input.display-value');
                if ($displayValue.val() == '') {
                    $displayValue.val($(this).find('input.display-value-none-selected').val());
                }
            });

            // Show tab error indicators
            if ($container.find('.field-group.has-error').length) {
                var tabId = '#' + $container.attr("class").substring(0, 4);

                var $tabWithError = $('a[href=' + tabId + ']');
                if (BLCAdmin.currentModal() !== undefined) {
                    $tabWithError = BLCAdmin.currentModal().find('a[href=' + tabId + ']');
                }

                if ($tabWithError.length) {
                    $tabWithError.prepend('<span class="tab-error-indicator danger"></span>');
                }
            }

            BLCAdmin.initializeTextAreaFields($container);
            BLCAdmin.initializeColorPickerFields($container);
            BLCAdmin.initializeSelectizeFields($container);
            BLCAdmin.initializeRadioFields($container);
            BLCAdmin.initializeDateFields($container);

            // Run any additionally configured initialization handlers
            for (var i = 0; i < initializationHandlers.length; i++) {
                initializationHandlers[i]($container);
            }

            // Mark this container as initialized
            $container.data('initialized', 'true');

            return false;
        },

        initializeDateFields : function($container) {
            $container.find('.datetimepicker').each(function (index, element) {
                // create a hidden clone, which will contain the actual value
                var $self = $(this);
                var $clone = $self.clone();
                $clone.insertAfter(this);
                $clone.hide();

                // rename the original field, used to contain the display value
                $self.attr('id', $self.attr('id') + '-display');
                $self.attr('name', $self.attr('name') + '-display');

                // create the datetimepicker with the desired display format
                $self.datetimepicker({
                    formatTime: "g:ia",
                    format: "l, F d, Y \@ g:ia",
                    onClose: function(current_time, $input) {
                        if (current_time) {
                            var dateString = '' +
                                current_time.getFullYear() + '.' +
                                ('0' + (current_time.getMonth() + 1).toString()).slice(-2) + '.' +
                                ('0' + current_time.getDate().toString()).slice(-2) + ' ' +
                                ('0' + current_time.getHours().toString()).slice(-2) + ':' +
                                ('0' + current_time.getMinutes().toString()).slice(-2) + ':00';

                            if (dateString.endsWith("23:59:00")) {
                                dateString = dateString.replace("23:59:00", "23:59:59");
                            }
                            // need to escape ids for entity form
                            $clone.attr('value',dateString).trigger('input');
                            $input.trigger('input');
                        }
                    }
                });

                $self.on('input', function() {
                    if ($self.val() === "") {
                        $clone.attr('value',"").trigger('input');
                    }
                });
            });

            $container.find('.timepicker').each(function (index, element) {
                $(this).datetimepicker({
                    datepicker: false,
                    format:'h:i A',
                    formatTime: 'h:i A',
                    step: 15
                });
            });

            // initialize datetimepicker fields
            $container.find("[id$=display].datetimepicker").each(function() {
                if ($(this).val().length) {
                    var d = moment($(this).attr('value'), 'YYYY.MM.DD HH:mm:ss').toDate();
                    $(this).val(d.dateFormat("l, F d, Y \@ g:ia"));
                }
            });

            // initialize datetimepicker fields
            $container.find(".dateFormat").each(function() {
                if ($(this).html().length) {
                    var d = new Date($(this).html());
                    var timezone = d.toString().substring(d.toString().indexOf("("));
                    $(this).html(d.dateFormat("l, F d, Y \@ g:ia") + " " + timezone);
                }
            });

            $container.find('.tooltip').each(function() {
                var windowWidth = $(window).width();
                if ($(this).offset().left > windowWidth / 2 || $(this).closest('.col4').length) {
                    $(this).find('span').addClass('left');
                }
            });

            $container.find('.dropdown-menu-right').each(function() {
                var windowWidth = $(window).width();
                if ($(this).closest('.content-area-title-bar').length === 0
                    && $(this).offset().left < windowWidth / 2) {
                    $(this).removeClass('dropdown-menu-right').addClass('dropdown-menu-left');
                }
            });
        },
        initializeRadioFields : function($container) {
            $container.find('.radio-label').on("click", function(e) {
                if (!$(this).hasClass('disabled')) {
                    e.preventDefault();
                    $(this).prev('input').prop("checked", true).change();
                }
            });
        },
        initializeTextAreaFields : function($container) {
            // Set up rich-text HTML editors
            if($.fn.redactor) {
                $container.find('.redactor').redactor({
                    plugins: ['selectasset', 'fontfamily', 'fontcolor', 'fontsize', 'video', 'table'],
                    replaceDivs : false,
                    buttonSource: true,
                    paragraphize: false,
                    minHeight: 300,
                    tabKey: true,
                    tabsAsSpaces: 4,
                    deniedTags: [],
                    initCallback: function() {
                        // reset the redactor contents to ensure correct rendering
                        this.code.set(this.code.get());
                    }
                });
            }

            $container.find('textarea.autosize').autosize();
        },
        initializeColorPickerFields : function($container) {
            $container.find(".color-picker").spectrum({
                showButtons: false,
                preferredFormat: "hex6",
                change: function(color) {
                    $(this).closest('.field-group').find('input.color-picker-value').val(color).trigger('input');
                },
                move: function(color) {
                    $(this).closest('.field-group').find('input.color-picker-value').val(color).trigger('input');
                }
            });
        },

        initializeSelectizeFields : function($container) {
            var excludedSelectors = '';
            for (var i=0;i<excludedSelectizeSelectors.length;i++){
                excludedSelectors += ', ' + excludedSelectizeSelectors[i];
            }

            $container.find('select:not(".selectize-collection, .selectize-adder' + excludedSelectors + '")')
                .blSelectize({
                    sortField: 'text',
                    closeAfterSelect: true,
                    onItemAdd: function(value, $item) {
                        $item.closest('.selectize-input').find('input').blur();
                    }
                });

            $container.find('.selectize-wrapper').each(function(index, selectizeWrapper) {
                var selectizeAdder = $(selectizeWrapper).find(".selectize-adder");
                var selectizeCollection = $(selectizeWrapper).find(".selectize-collection");

                var selectizeUrl = $(selectizeAdder).data("selectizeurl");
                var selectizeSearchField = $(selectizeAdder).data("selectizesearch");
                var placeholder = 'Add ' + $(selectizeAdder).data("selectizeplaceholder") + ' +';

                var collectionPlaceholder = 'No restrictions';

                var select_adder, $select_adder;
                var select_collection, $select_collection;

                var selectizeAdderOptions = {
                    maxItems: null,
                    persist: false,
                    loadThrottle: 100,
                    preload: 'focus',
                    hideSelected: true,
                    closeAfterSelect: true,
                    placeholder: placeholder,
                    dropdownParent: 'body',
                    selectOnTab: true,
                    onInitialize: function () {
                        var $selectize = this;
                        this.revertSettings.$children.each(function () {
                            $.extend($selectize.options[this.value], $(this).data());
                        });

                        // dirty field check
                        var $wrapper = $(selectizeWrapper);
                        // Run any additionally configured initialization handlers
                        for (var i = 0; i < selectizeInitializationHandlers.length; i++) {
                            selectizeInitializationHandlers[i]($wrapper);
                        }
                    },
                    load: function(query, callback) {
                        var queryData = {};
                        queryData[selectizeSearchField] = query;

                        BLC.ajax({
                            url: selectizeUrl + "/selectize",
                            type: 'GET',
                            data: queryData
                        }, function(data) {
                            $.each(data.options, function (index, value) {
                                if (select_adder.getOption(value.id).length === 0 && select_adder.getItem(value.id).length === 0) {
                                    select_adder.addOption({value: value.id, text: value.name});
                                    if (typeof value.alternateId !== 'undefined') {
                                        select_adder.options[value.name].alternate_id = data.alternateId;
                                    }

                                }
                            });

                            if (typeof $(selectizeCollection).attr("data-target_path") === 'undefined' ||
                                typeof $(selectizeCollection).attr("data-linked_path") === 'undefined' ||
                                typeof $(selectizeCollection).attr("data-linked_id") === 'undefined') {
                                $(selectizeCollection).attr("data-target_path", data.targetObjectPath);
                                $(selectizeCollection).attr("data-linked_path", data.linkedObjectPath);
                                $(selectizeCollection).attr("data-linked_id", data.linkedObjectId);
                            }

                            select_adder.open();
                        });
                    },
                    onItemAdd: function(value, $item) {
                        if (!value.length) return;

                        $item.closest('.selectize-input').find('input').attr("placeholder", placeholder);

                        var data = {"fields['id'].value" : value};

                        var targetPath = $(selectizeCollection).data("target_path");
                        var linkedPath = $(selectizeCollection).data("linked_path");
                        var linkedId = $(selectizeCollection).data("linked_id");
                        if (typeof targetPath !== 'undefined' && typeof linkedPath !== 'undefined'
                            && typeof linkedId !== 'undefined') {
                            data = {};
                            data["fields['" + targetPath + "'].value"] = value;
                            data["fields['" + linkedPath + "'].value"] = linkedId;
                        }

                        BLC.ajax({
                            url : selectizeUrl + "/selectize-add",
                            type : "POST",
                            data : data
                        }, function(data) {
                            BLCAdmin.alert.showAlert($(selectizeCollection), BLCAdmin.messages.saved + '!', {
                                alertType: 'save-alert',
                                autoClose: 3000,
                                clearOtherAlerts: true
                            });

                            select_collection.addOption({value: $item.data('value'), text: $item.html()});
                            select_collection.addItem($item.data('value'));
                            if (typeof data.alternateId !== 'undefined') {
                                select_adder.options[value].alternate_id = data.alternateId;
                            }

                            $item.closest('.selectize-input').find('input').blur();
                            BLCAdmin.runSelectizeUpdateHandlers($(selectizeWrapper));
                        });
                    },
                    onItemRemove: function (value, $item) {
                        select_adder.addOption({value: $item.data('value'), text: $item.html()});
                        $select_adder.siblings('.selectize-control.selectize-adder').find('.selectize-input input').attr('placeholder', placeholder);
                        BLCAdmin.runSelectizeUpdateHandlers($(selectizeWrapper));
                    }
                };

                var selectizeCollectionOptions = {
                    maxItems: null,
                    persist: false,
                    placeholder: collectionPlaceholder,
                    dropdownParent: 'body',
                    hideSelected: true,
                    selectOnTab: true,
                    onInitialize: function () {
                        var $selectize = this;
                        this.revertSettings.$children.each(function () {
                            $.extend($selectize.options[this.value], $(this).data());
                        });
                    },
                    onFocus: function () {
                        this.close();
                    },
                    onType: function () {
                        this.close();
                    },
                    onItemRemove: function(value) {
                        if (!value.length) return;

                        var alternateId = select_adder.options[value].alternate_id;
                        var url;
                        if (typeof alternateId !== 'undefined') {
                            url = selectizeUrl + "/" + value + "/" + alternateId + "/delete";
                        } else {
                            url = selectizeUrl + "/" + value + "/delete";
                        }

                        BLC.ajax({
                            url: url,
                            type: "POST"
                        }, function(data) {
                            BLCAdmin.alert.showAlert($(selectizeCollection), BLCAdmin.messages.saved + '!', {
                                alertType: 'save-alert',
                                autoClose: 3000,
                                clearOtherAlerts: true
                            });

                            select_adder.removeItem(value);
                        });
                    }
                };

                if (!$(selectizeCollection).hasClass('disabled')) {
                    selectizeCollectionOptions['plugins'] = ['remove_button', 'silent_remove'];
                }

                $select_adder = $(selectizeAdder).selectize(selectizeAdderOptions);
                $select_collection = $(selectizeCollection).selectize(selectizeCollectionOptions);

                select_collection  = $select_collection[0].selectize;
                select_adder = $select_adder[0].selectize;

                $select_adder.siblings('.selectize-control.selectize-adder').find('.selectize-input input').attr('placeholder', placeholder);

                $('.selectize-control.selectize-collection input').attr('disabled','disabled');
            });
        },

        updateFields : function($container) {
            for (var i = 0; i < updateHandlers.length; i++) {
                updateHandlers[i]($container);
            }
        },

        updateContentHeight : function($el) {
            var contentYield = $el.closest('.content-yield');
            var wrapper = $(contentYield).find('.row');
            var newHeight = $(wrapper).outerHeight(true);
            if (newHeight >= $(contentYield).height()) {
                $(contentYield).height(newHeight);
            }
        },

        getModals : function() {
            var modalsCopy = [];
            for (var i = 0; i < modals.length; i++) {
                modalsCopy[i] = modals[i];
            }
            return modalsCopy;
        },
 
        getForm : function($element) {
            var $form;

            if ($element.closest('.modal').length > 0) {
                $form = $element.closest('.modal').find('.modal-body form');
            } else {
                $form = $element.closest('form')
            }
    
            if (!$form.length) {
                $form = $('.entity-edit form');
            }

            // If form is still empty, grab the form from the main content
            if (!$form.length) {
                $form = $('.content-yield form');
            }

            return $form;
        },

        getFieldSelectors : function getFieldSelectors() {
            return fieldSelectors.concat();
        },

        extractFieldValue : function extractFieldValue($field) {
            var value = $field.find('input[type="radio"]:checked').val();
            if (value == null) {
                value = $field.find('select').val();
            }
            if (value == null) {
                value = $field.find('input[type="hidden"].value').val();
            }
            if (value == null) {
                value = $field.find('input[type="text"]').val();
            }
            return value;
        },

        setFieldValue : function setFieldValue($field, value) {
            if (value == null) {
                $field.find('input[type="radio"]:checked').removeAttr('checked')
            } else {
                var $radio = $field.find('input[type="radio"][value="' + value + '"]');
                $radio.attr('checked', 'checked');
                $radio.next('label').click();
            }

            $field.find('select').val(value);
            if (!$field.find('.additional-foreign-key-container').length) {
                $field.find('input[type="text"]').val(value);
            }
            
            if (value == null && $field.find('button.clear-foreign-key').length) {
                $field.find('button.clear-foreign-key').click();
            }
            $field.trigger('change');
        },

        /**
         * Adds an initialization handler that is responsible for toggling the visiblity of a child field based on the
         * current value of the associated parent field.
         * 
         * @param className - The class name that this handler should be bound to
         * @param parentFieldSelector - A jQuery selector to use to find the div.field-box for the parent field
         * @param childFieldSelector - A jQuery selector to use to find the div.field-box for the child field
         * @param showIfValue - Either a function that takes one argument (the parentValue) and returns true if the
         *                      child field should be visible or a string to directly match against the parentValue
         * @param options - Additional options:
         *   - clearChildData (boolean) - if true, will null out the data of the child field if the parent field's
         *     value becomes null
         *   - additionalChangeAction (fn) - A function to execute when the value of the parent field changes. The args
         *     passed to the function will be [$parentField, $childField, shouldShow, parentValue]
         *   - additionalChangeAction-runOnInitialization (boolean) - If set to true, will invoke the 
         *     additionalChangeAction on initialization
         */
        addDependentFieldHandler : function addDependentFieldHandler(className, parentFieldSelector, childFieldSelector, 
                showIfValue, options) {
            BLCAdmin.addInitializationHandler(function($container) {
                var $form = $container.find('form').length ? $container.find('form') : $container.closest('form');
                var thisClass = $form.find('input[name="ceilingEntityClassname"]').val();
                if (thisClass != null && thisClass.indexOf(className) >= 0) {
                    var toggleFunction = function(event) {
                        // Get the containers parent in the event a field is on another tab
                        var $containerParent = $container.parent();

                        // Extract the parent and child field DOM elements from the data
                        var $parentField = $containerParent.find(event.data.parentFieldSelector);
                        var $childField = $containerParent.find(event.data.childFieldSelector);
                        var options = event.data.options;
                        var parentValue = BLCAdmin.extractFieldValue($parentField);
                        
                        // Either match the string or execute a function to figure out if the child field should be shown
                        var shouldShow = false;
                        if (typeof showIfValue == "function") {
                            shouldShow = showIfValue(parentValue, event.data.$container);
                        } else {
                            shouldShow = (parentValue == showIfValue);
                        }

                        // Clear the data in the child field if that option was set
                        if (options != null && options['clearChildData'] && !event.initialization && !event.revertEntityFormChanges) {
                            BLCAdmin.setFieldValue($childField, null);
                        }

                        BLCAdmin.entityForm.toggleFieldVisibility($childField, shouldShow);
                        
                        if (options != null 
                                && options['additionalChangeAction'] 
                                && (options['additionalChangeAction-runOnInitialization'] || !event.initialization)) {
                            options['additionalChangeAction']($parentField, $childField, shouldShow, parentValue);
                        }
                    };
                    
                    var $parentField = $container.find(parentFieldSelector);
                    
                    var data = {
                        '$container' : $container,
                        'parentFieldSelector' : parentFieldSelector,
                        'childFieldSelector' : childFieldSelector,
                        'options' : options
                    };
                    
                    // Bind the change event for the parent field
                    $parentField.on('change', data, toggleFunction);

                    // Run the toggleFunction immediately to set initial states appropriately
                    toggleFunction({ data : data, initialization : true });
                }
            })
        },

        /**
         * Adds a dependent field filter handler that will restrict child lookups based on the value of the parent field.
         * 
         * @param className - The class name that this handler should be bound to
         * @param parentFieldSelector - A jQuery selector to use to find the div.field-box for the parent field
         * @param childFieldName - The name of this field (the id value in the containing div.field-box)
         * @param childFieldPropertyName - The name of the back-end field that will receive the filter on the child lookup
         * @param options - Additional options:
         *   parentFieldRequired (boolean) - whether or not to disable the child lookup if the parent field is null
         */
        addDependentFieldFilterHandler : function addDependentFieldFilterHandler(className, parentFieldSelector, 
                childFieldName, childFieldPropertyName, options) {
            // Register the handler so that the lookup knows how to filter itself
            dependentFieldFilterHandlers[getDependentFieldFilterKey(className, childFieldName)] = {
                parentFieldSelector : parentFieldSelector,
                childFieldPropertyName : childFieldPropertyName
            };
            
            // If the parentFieldRequired option is turned on, we need to toggle the behavior of the child field accordingly
            if (options != null && options['parentFieldRequired']) {
                BLCAdmin.addDependentFieldHandler(className, parentFieldSelector, '#' + childFieldName, function(val) {
                    return val != null && val != "";
                }, { 
                    clearChildData : true
                });
            }
        },
        
        getDependentFieldFilterHandler : function getDependentFieldFilterHandler(className, childFieldName) {
            return dependentFieldFilterHandlers[getDependentFieldFilterKey(className, childFieldName)];
        },

        /**
         * Convenience method to show the action spinner
         *
         * @param $actions
         */
        showActionSpinner: function showActionSpinner($actions) {
            $actions.find('button').hide();
            $actions.find('img.ajax-loader').show();
        },

        /**
         * Convenience method to hide the action spinner
         *
         */
        hideActionSpinner : function hideActionSpinner () {
            var $actions = $('.entity-form-actions');
            $actions.find('button').show();
            $actions.find('img.ajax-loader').hide();
        },

        /**
         * Convenience method to show errors
         *
         * @param data
         * @param alertMessage
         */
        showErrors: function showErrors(data, alertMessage) {
            var errorBlock = "<div class='errors'></div>";
            $(errorBlock).insertBefore("form.entity-form div.tabs-container");
            $.each( data.errors , function( idx, error ){
                if (error.errorType == "field") {
                    var fieldLabel = $("#field-" + error.field).find(".field-label");

                    var fieldHtml = "<span class='fieldError error'>SUBSTITUTE</span>";
                    if ($(".tabError:contains(" + error.tab + ")").length) {
                        var labeledError = fieldHtml.replace('SUBSTITUTE', (fieldLabel.length > 0 ? fieldLabel[0].innerHTML + ': ' : '') + error.message);
                        $(".tabError:contains(" + error.tab + ")").append(labeledError);
                    } else {
                        var labeledError = "<div class='tabError'><b>" + error.tab +
                            "</b>" + fieldHtml.replace('SUBSTITUTE', (fieldLabel.length > 0 ? fieldLabel[0].innerHTML + ': ' : '') + error.message) + "</div>";
                        $(".errors").append(labeledError);
                    }

                    var fieldError = "<span class='error'>" + error.message + "</span>";
                    $(fieldError).insertAfter(fieldLabel);
                } else if (error.errorType == 'global'){
                    var globalError = "<div class='tabError'><b>" + BLCAdmin.messages.globalErrors + "</b><span class='error'>"
                        + error.message + "</span></div>";
                    $(".errors").append(globalError);
                }
            });
            $(".alert-box").removeClass("success").addClass("alert");
            $(".alert-box-message").text(alertMessage);
        },

        /**
         * Ensure that disabled fields are included in the serialized form
         * @param {form}
         */
        serialize : function serialize($form) {
            var $disabledFields = $form.find(':disabled').attr('disabled', false);
            var serializedForm = $form.serialize();
            $disabledFields.attr('disabled', true);

            return serializedForm;
        },

        /**
         * Ensure that disabled fields are included in the serialized form
         * @param {form}
         */
        serializeArray : function serializeArray($form) {
            var $disabledFields = $form.find(':disabled').attr('disabled', false);
            var serializedForm = $form.serializeArray();
            $disabledFields.attr('disabled', true);

            return serializedForm;
        },

        /**
         * Splits out a comma-seperated string into a cleaned array
         * @param data
         * @param delimiter
         * @returns {Array}
         */
        stringToArray: function(data, delimiter) {
            delimiter = typeof delimiter !== 'undefined' ? delimiter : ',';
            var dataArray = [];
            $.each(data.split(delimiter), function(index, item) {
                var item = item.replace(/(^\[")|("$)|(^")|("\]$)/g, '');
                item = BLCAdmin.unescapeString(item);
                dataArray.push(item);
            });
            return dataArray;
        },

        unescapeString: function(data) {
            return data.replace(/\\/g, '');
        },

        updateAdminNavigation: function() {
            // var url = window.location.pathname.replace("/admin", '');
            BLC.ajax({
                url: BLC.servletContext + '/update-navigation',
                type: "GET",
                error: function (error) {
                }
            }, function (data) {
                var $nav = $('.secondary-nav').parent();
                $nav.replaceWith($(data));
            });
        },

        confirmProcessBeforeProceeding: function(mustConfirm, confirmMsg, processMethod, methodParams) {
            if (mustConfirm) {
                if (confirmMsg == undefined || !confirmMsg.length) {
                    confirmMsg = BLCAdmin.messages.defaultConfirmMessage;
                }

                var cancel = false;
                $.confirm({
                    content: confirmMsg,
                    confirm: function() {
                        processMethod(methodParams);
                    },
                    cancel: function() {
                        cancel = true;
                    }
                });
                if (cancel) {
                    event.preventDefault();
                    return false;
                }
            } else {
                processMethod(methodParams);
            }
        }
    };

})(jQuery);

$.fn.blSelectize = function (settings_user) {
    if (!this.length) {
        return;
    }

    // Iterate all select elements
    this.each(function(index, el) {
        // make sure the settings are defined
        if (settings_user === undefined) {
            settings_user = {};
        }
        // add default settings here
        settings_user['dropdownParent'] = settings_user['dropdownParent'] || 'body';
        settings_user['hideSelected'] = settings_user['hideSelected'] !== undefined ? settings_user['hideSelected'] : true;
        settings_user['selectOnTab'] = settings_user['selectOnTab'] !== undefined ? settings_user['selectOnTab'] : true;
        settings_user['plugins'] = settings_user['plugins'] || ['clear_on_type', 'enter_key_blur'];
        settings_user['placeholder'] = settings_user['placeholder'] || 'Click here to select ...';
        settings_user['positionDropdown'] = settings_user['positionDropdown'] || 'auto';
        settings_user['onInitialize'] = settings_user['onInitialize'] || function() {
            if (Object.keys(this.options).length <= 1) {
                // Remove the dropdown css
                this.$control.addClass('remove-caret');
            }
        };

        var $select = $(el).selectize(settings_user);
        var selectize = $select[0].selectize;

        // add scroll handler for main content
        $('.main-content').scroll(function () {
            selectize.close();
        });

        // add scroll handler for within a modal
        $('.modal-body').scroll(function () {
            selectize.close();
        });
    });

    return this;
};

Selectize.prototype.positionDropdown = function() {
    var $control = this.$control;
    var offset = this.settings.dropdownParent === 'body' ? $control.offset() : $control.position();
    offset.top += $control.outerHeight(true);
    var controlHeight = $control.outerHeight(true);
    var dropdownHeight = this.$dropdown.outerHeight(true);

    var dropdownBottom = $control.offset().top + controlHeight + dropdownHeight;
    var windowBottom = $(window).height();
    var optDirection = dropdownBottom < windowBottom ? 'down' : 'up';

    if (optDirection === 'down') {
        self.isDropUp = false;
    } else if (optDirection === 'up') {
        offset.top -= dropdownHeight;
        offset.top -= controlHeight;
        self.isDropUp = true;
    }

    this.$dropdown.css({
        width : $control.outerWidth(),
        top   : offset.top,
        left  : offset.left
    });

};

Selectize.define('clear_on_type', function(options) {
    var self = this;

    options.text = options.text || function(option) {
            return option[this.settings.labelField];
        };

    this.onKeyDown = (function() {
        var original = self.onKeyDown;
        return function(e) {
            var index, option;
            if (this.$control_input.val() === '' && !this.$activeItems.length) {
                index = this.caretPos - 1;
                if (index >= 0) {
                    this.previousValue = this.options[this.items[index]];
                }
                if (index >= 0 && index < this.items.length) {
                    if (this.deleteSelection(e)) {
                        this.clear();
                        this.setTextboxValue(String.fromCharCode(e.keyCode));
                        this.refreshOptions(true);
                    }
                    e.preventDefault();
                    return;
                }
            }

            if (e.keyCode === 27 && this.previousValue !== undefined) {
                this.clear();
                this.setTextboxValue(this.previousValue.text);
                this.setActiveItem();
                this.refreshOptions(true);

                return;
            }
            return original.apply(this, arguments);
        };
    })();
});

Selectize.define('enter_key_blur', function (options) {
    var self = this;

    this.onKeyDown = (function (e) {
        var original = self.onKeyDown;
        return function (e) {

            if (e.keyCode === 13) {
                if (self.settings.selectOnTab && self.isOpen && self.$activeOption) {
                    self.onOptionSelect({currentTarget: self.$activeOption});

                    // Default behaviour is to jump to the next field, we only want this
                    // if the current field doesn't accept any more entries
                    if (!self.isFull()) {
                        e.preventDefault();
                    }
                }
                $(this).blur();
                return;
            }
            return original.apply(this, arguments)
        }
    })()
});

// Replace the default AJAX error handler with this custom admin one that relies on the exception
// being set on the model instead of a stack trace page when an error occurs on an AJAX request.
BLC.defaultErrorHandler = function(data) {
    if (data.status == "403") {
		BLCAdmin.showMessageAsModal(BLCAdmin.messages.error, BLCAdmin.messages.forbidden403);
	} else if (data.status == "409") {
		BLCAdmin.showMessageAsModal(BLCAdmin.messages.error, BLCAdmin.messages.staleContent);
    } else {
        var $data;
        
        if (data.responseText.trim) {
            $data = $(data.responseText.trim());
        } else {
            $data = $(data.responseText);
        }
    
        if ($data.length == 1) {
            BLCAdmin.showElementAsModal($data);
        } else {
            // This shouldn't happen, but it's here as a fallback just in case
            BLCAdmin.showMessageAsModal(BLCAdmin.messages.error, BLCAdmin.messages.errorOccurred);
        }
    }
};

BLC.addPreAjaxCallbackHandler(function($data) {
    if (!($data instanceof jQuery)) {
        return true;
    }
    
    var $loginForm = $data.find('form').filter(function() {
        return $(this).attr('action') === undefined ? false : $(this).attr('action').indexOf('login_admin_post') >= 0;
    });
    
    if ($loginForm.length > 0) {
        var currentPath = window.location.href;
        if (currentPath.indexOf('?') >= 0) {
            currentPath += '&'
        } else {
            currentPath += '?'
        }
        currentPath += 'sessionTimeout=true';
        
        window.location = currentPath;
        window.location.reload();
        return false;
    }
    
    return true;
});

var getCurrentHash = function() {
    var baseHash = window.location.hash;
    if(baseHash.indexOf('?') > -1) {
        return baseHash.substr(0, baseHash.indexOf('?'));
    } else {
        return baseHash;
    }
};
var getCurrentHashVal = function() {
    var hash = getCurrentHash();
    return hash.substr(1);
};

$(document).ready(function() {
    // primary entity buttons should be disabled until page is loaded
    $(window).load(function () {
        $('.button.primary.large:not(.submit-button):not(.modify-production-inventory)').prop('disabled', false).removeClass('disabled');
        $('a.show-translations').removeClass('disabled');
    });

    $(window).resize(function() {
        $.doTimeout('resize', 150, function() {
            if (BLCAdmin.currentModal() != null) {
                BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
            }
        });
    });

    // Ensure that the breadcrumb will render behind the entity form actions
    var $bcc = $('.sticky-container');
    $bcc.find('ul.breadcrumbs').outerWidth($bcc.outerWidth() - $bcc.find('.entity-form-actions').outerWidth() - 30);
});

// Close current modal on escape key
$('body').on('keyup', function(event) {
    if (event.keyCode == 27) {  // if key is escape
        BLCAdmin.hideCurrentModal();
    }
});

$('body').on('click', '.disabled', function(e) {
    e.stopPropagation();
    return false;
});
        
$('body').on('change', 'input.color-picker-value', function() {
    var $this = $(this);
    $this.closest('.field-group').find('input.color-picker').spectrum('set', $this.val());
});

$('body').on('click', 'button.page-reset', function() {
	var currentUrl = '//' + location.host + location.pathname;
	window.location = currentUrl;
});


$('.boolean-link').each(function() {
    $(this).next().find('input:not(:checked)').click();
});
$('body').on('click', '.boolean-link', function(e) {
    e.preventDefault();
    $(this).next().find('input:not(:checked)').click();

    if ($(this).hasClass('view-options')) {
        $(this).removeClass('view-options').addClass('hide-options');
        BLCAdmin.updateContentHeight($(this));
    } else {
        $(this).addClass('view-options').removeClass('hide-options');
    }
});

/**
 * Close the workflow confirm action dialog
 */
$('body').on('click', 'a.action-popup-cancel', function() {
    var $this = $(this);
    if ($this.hasClass('no-remove')) {
        $this.closest('div.action-popup').addClass('hidden');
    } else {
        $this.closest('div.action-popup').remove();
    }
    return false;
});
$(document).keyup(function(e){
    if (e.keyCode === 27) {
        var $actionPopup = $('div.action-popup');
        if ($actionPopup) {
            $actionPopup.remove();
        }
    }
});

$('body').on('click', 'a.change-password', function(event) {
    event.preventDefault();
    var $this = $(this);
    BLC.ajax({
        url : $this.attr('href')
    }, function(data) {
        var $modal = BLCAdmin.getModalSkeleton();
        $modal.find('.modal-body').append($(data));
        BLCAdmin.showElementAsModal($modal);

        //$this.closest('div.attached').append(data);
        /*$this.parent().find('div.action-popup').find('div.generated-url-container').each(function(idx, el) {
            if ($(el).data('overridden-url') != true) {
                BLCAdmin.generatedUrl.registerUrlGenerator($(el));
            }
        })
        */
    });
    
});

$('body').on('click', 'button.change-password-confirm', function(event) {
    var $this = $(this);
    var $form = $this.closest('form');

    // show the spinner
    $this.closest('.action-popup').find('img.ajax-loader').show();

    // clear old errors
    $this.closest('.action-popup').find('input[name=oldPassword]').css('border', '1px solid #D8D5D0');
    $this.closest('.action-popup').find('input[name=password]').css('border', '1px solid #D8D5D0');
    $this.closest('.action-popup').find('input[name=confirmPassword]').css('border', '1px solid #D8D5D0');

    BLC.ajax({
        url: $form.attr('action'),
        type: "POST",
        data: BLCAdmin.serialize($form),
        error: function(data) {
            $this.closest('.actions').show();
            $this.closest('.action-popup').find('img.ajax-loader').hide();
            BLC.defaultErrorHandler(data);
        }
    }, function(data) {
        if (data instanceof Object && data.hasOwnProperty('status') && data.status == 'error') {
            //TODO: i18n

            if (data.errorText.indexOf('match') > 0) {
                // confirm password doesnt match
                $this.closest('.action-popup').find('input[name=password]').css('border', '1px solid #890923');
                $this.closest('.action-popup').find('input[name=confirmPassword]').css('border', '1px solid #890923');
            } else if (data.errorText === 'Please enter a valid password.') {
                // new password is not valid
                $this.closest('.action-popup').find('input[name=oldPassword]').css('border', '1px solid #890923');
                $this.closest('.action-popup').find('input[name=password]').css('border', '1px solid #890923');
            }

            $this.closest('div.action-popup')
                .find('span.submit-error')
                    .text(data.errorText)
                    .show();

            // show the actions and hide the spinner
            $this.closest('.actions').show();
            $this.closest('.action-popup').find('img.ajax-loader').hide();
        } else {
            $this.closest('div.action-popup')
                .find('span.submit-error')
                    .text(data.successMessage)
                    .addClass('success')
                    .show();

            $this.closest('.action-popup').find('img.ajax-loader').hide();
            
            setTimeout(function() {
                $this.closest('.modal').find('.close').click();
            }, 2000);
            
            /*
            $ef.find('input[name="fields[\'name\'].value"]').val($form.find('input[name="name"]').val());
            $ef.find('input[name="fields[\'path\'].value"]').val($form.find('input[name="path"]').val());
            $ef.find('input[name="fields[\'overrideGeneratedPath\'].value"]').val($form.find('input[name="overrideGeneratedPath"]').val());
            $ef.append($('<input type="hidden" name="fields[\'saveAsNew\'].value" value="true" />'));
            */
            
            $this.closest('');
            $this.closest('.actions').hide();
            
            //$ef.submit();
        }
    });

    event.preventDefault();
});

$('body').on('click', '.add-main-entity', function (e) {
    if (BLCAdmin.workflow == undefined) {
        e.preventDefault();
        var action = $(this).data('url');

        BLCAdmin.showLinkAsModal(action);
    }
});

// add scroll handler for the body
$('.main-content').scroll(function () {
    var contentWrapper = $(this).find('.content-yield').height();
    var content = $(this).find('.content-yield').find('.row').height();
    var title = $(this).find('.sticky-container').height();
    var tabs = $(this).find('.section-tabs').height();

    var scrollPos = $(this).scrollTop();
    var windowHeight = $(this).height();

    var h = windowHeight + scrollPos;

    if (h > (content + title + tabs) && h < (contentWrapper + title + tabs)) {
        $(this).find('.content-yield').height(h - title - tabs);
    }
});
