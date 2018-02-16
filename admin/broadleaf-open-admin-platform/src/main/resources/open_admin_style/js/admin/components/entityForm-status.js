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

    // Properties
    var entityFormChangeMap = {};
    var didConfirmLeave = false;

    BLCAdmin.entityForm.status = {

        /**
         * Returns whether or not the user has confirmed leaving the page or not.
         *
         * @returns {boolean}
         */
        getDidConfirmLeave : function() {
            return didConfirmLeave;
        },

        /**
         * This is a setter function for when the user confirms leaving the page.
         * @param val
         */
        setDidConfirmLeave : function(val) {
            didConfirmLeave = val;
        },

        /**
         * Returns either an object containing the fields original value if a change has occurred or `undefined`
         * if no changes have taken place.
         *
         * @param id
         * @returns {*}
         */
        getEntityFormChangesById : function(id) {
            return entityFormChangeMap[id];
        },

        /**
         * Sets an object containing a fields original value on the `entityFormChangeMap`.
         *
         * @param id
         * @param val
         */
        setChangesForId : function(id, val) {
            entityFormChangeMap[id] = val;
        },

        /**
         * If a changed field is returned to its original value, this function is used to remove its entry
         * from the `entityFormChangeMap`.
         *
         * @param id
         */
        removeChangesForId : function(id) {
            delete entityFormChangeMap[id];
        },

        /**
         * Calling this method will add a property to the `entityFormChangeMap` that during the revert process will trigger
         * a page reload.
         */
        triggerReloadOnRevert : function() {
            entityFormChangeMap['status--reloadOnRevert'] = true;

            this.updateEntityFormActions();
        },

        /**
         * This function, using the `entityFormChangeMap` will revert all changed fields back to their original values.
         */
        revertEntityFormChanges : function(allowReload) {
            for (var key in entityFormChangeMap) {
                // Check if this is a request for page reload
                if (key === 'status--reloadOnRevert' && allowReload) {
                    // Set the property to allow reload
                    BLCAdmin.entityForm.status.setDidConfirmLeave(true);
                    window.location.reload();
                    return;
                }

                var origVal = entityFormChangeMap[key].originalValue;

                // We need to find the actual DOM element from the key in the `entityFormChangeMap`
                var k = key.replace(/'/g, '\\\'');
                var el = $('[id="'+ k + '"]');

                // If no element was found, it is probably a radio button.  To find these elements we need to look
                // by element name.
                if (!el.length) {
                    el = $('[name="'+ k + '"]');
                    if (el.is(':radio')) {
                        // Iterate through all radio options and check the correct one.
                        el.each(function (i, radio) {
                            var origVal = $(radio).attr('data-orig-val');
                            if (origVal === undefined && $(radio).hasClass('toggle')) {
                                origVal = 'AND';
                            }
                            if ($(radio).val() == origVal) {
                                $(radio).prop('checked', true).addClass('checked').trigger('change');
                            } else {
                                $(radio).prop('checked', false).removeClass('checked');
                            }
                        });
                        continue;
                    }
                }
                // Check if this element is a media item.  If so we need to change the image and underlying data back
                // to it's original values
                else if ($(el).hasClass('mediaItem')) {
                    var $mediaImageContainer = $(el).next('.media-image-container');
                    $mediaImageContainer.find('img.thumbnail').attr("src", BLC.servletContext + origVal);
                    $mediaImageContainer.find('img.thumbnail').data("fullurl", BLC.servletContext + origVal);
                    $mediaImageContainer.find('img.thumbnail').parent().attr("href", BLC.servletContext + origVal);
                    $mediaImageContainer.find('img.thumbnail').removeClass('placeholder-image');
                    $mediaImageContainer.find('button.edit-asset-selector, button.clear-asset-selector').show();

                    origVal = '{ "url": "' + origVal + '" }';
                }
                // If this is a to-one lookup we need to update the display value as well
                else if ($(el).closest('.additional-foreign-key-container').length) {
                    if ($(el).hasClass('hidden-display-value')) {
                        var $container = $(el).closest('.additional-foreign-key-container');
                        $container.trigger({ type:"change", revertEntityFormChanges: true });

                        if (origVal != '') {
                            $container.find('.display-value').val(origVal).show();
                            $container.find('.clear-foreign-key').show();
                            $container.find('.display-value-none-selected').hide();
                        } else {
                            $container.find('.display-value').hide();
                            $container.find('.clear-foreign-key').hide();
                            $container.find('.display-value-none-selected').show();
                        }
                    }
                }
                // If this is a redactor field, we have to set its text attribute, not its value
                else if ($(el).prev('.redactor-editor').length) {
                    $(el).redactor('code.set', origVal);
                    $(el).val(origVal);
                    continue;
                }
                // If this is a select field we need to set the original item back through selectize.
                else if (el.is('select')) {
                    if (origVal === '') {
                        el[0].selectize.clear();
                    } else {
                        el[0].selectize.addItem(origVal);
                    }
                    continue;
                }
                // If this is a query builder, find the associated `RuleBuilder` and set the rules back to their
                // original values.
                else if ($(el).closest('.query-builder-rules-container').length && !$(el).hasClass('rules-group-container')) {
                    var $ruleBuilderContainer = $(el).closest('.query-builder-rules-container');

                    // In order to get the new rules on this `RuleBuilder` we need to grab the actual `RuleBuilder`
                    var hiddenId = $ruleBuilderContainer.next('.rule-builder-data').data('hiddenid');
                    var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByHiddenId(hiddenId);
                    var ruleType = $ruleBuilderContainer.parent().find('.rule-builder-required-field').data('ruletype');

                    // Change the original value to JSON
                    var origBuilders = JSON.parse(JSON.parse(origVal));

                    // Clear out the rule builder container
                    ruleBuilder.removeAllQueryBuilders();
                    $ruleBuilderContainer.empty();

                    // Recreate the original rule builder
                    var jsonVal = origBuilders;
                    if (jsonVal.data.length > 0) {
                        for (var i=0; i<jsonVal.data.length; i++) {
                            if (ruleType !== BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                                jsonVal.data[i].quantity = null;
                            }
                            BLCAdmin.ruleBuilders.constructQueryBuilder($ruleBuilderContainer, jsonVal.data[i],
                                ruleBuilder.fields, ruleBuilder);
                        }
                    } else {
                        var qty = null;
                        if (ruleType === BLCAdmin.RuleTypeEnum.RULE_WITH_QUANTITY) {
                            qty = 1;
                        }

                        BLCAdmin.ruleBuilders.constructQueryBuilder($ruleBuilderContainer, BLCAdmin.ruleBuilders.getEmptyRuleData(qty),
                            ruleBuilder.fields, ruleBuilder);
                    }

                    continue;
                }
                // This is a special case for rule builder's quantity match rule
                else if ($(el).hasClass('rules-group-container')) {
                    var $matchValue = $(el).find('input.rules-group-header-item-qty');
                    $matchValue.val(origVal);
                    continue;
                }
                // If this is a color picker, we need to set the color picker widget
                else if ($(el).hasClass('color-picker-value')) {
                    $(el).val(origVal).trigger('blur');
                    $(el).closest('.field-group').find('input.color-picker').spectrum('set', origVal);
                    continue;
                }
                else if ($(el).hasClass('ace-editor-content')) {
                    BLCAdmin.ace.revertRegisteredEditors();
                }

                // If we made it this far, set the fields value.
                $(el).val(origVal).trigger('blur');
            }

            // Finally, we want to clear out all changes in the `entityFormChangeMap` and any data attributes on the fields.
            this.clearEntityFormChanges();
        },

        /**
         * Clears the `entityFormChangeMap`, typically called after a successful save.
         */
        clearEntityFormChanges : function () {
            entityFormChangeMap = {};

            $('[data-orig-val]').each(function(i, el) {
                $(el).removeAttr('data-orig-val');
            });
            this.initializeOriginalValues();
            this.updateEntityFormActions();
        },

        /**
         * This function returns the number of changes the user has made to the entity form
         */
        getEntityFormChangesCount : function() {
            return Object.keys(entityFormChangeMap).length;
        },

        /**
         * This function is called when there is a change on the DOM.  It will check if the field in question is currently
         * in the `entityFormChangeMap`.  If it is not, it will make sure a change actually occurred and add it to the
         * map.  Otherwise, there was a change in the past.  If the fields `originalValue` is the same as the `newVal`
         * the entry is removed from the `entityFormChangeMap`.
         *
         * @param id
         * @param origVal
         * @param newVal
         */
        updateEntityFormChangeMap : function(id, origVal, newVal) {
            var changesFromId = this.getEntityFormChangesById(id);
            var newValJson = BLCAdmin.unescapeString(JSON.stringify(newVal));
            var origValJson = BLCAdmin.unescapeString(JSON.stringify(origVal));

            // Check if the field is in the change map
            if (changesFromId === undefined) {
                // It's not, so double check that the new and old values are actually different
                if (newValJson !== origValJson) {
                    this.setChangesForId(id, {
                        originalValue: origVal
                    });
                }
            }
            // If it is, and the values are the same, remove it
            else {
                var entityOrigValJson = BLCAdmin.unescapeString(JSON.stringify(changesFromId.originalValue));
                if (newValJson == entityOrigValJson) {
                    this.removeChangesForId(id);
                }
            }

            this.updateEntityFormActions();
        },

        /**
         * This function updates the disabled state of the 'Save', 'Promote', and 'Approve' buttons on the entity form.
         * It is called once when the page first loads, and then on every subsequent change.
         */
        updateEntityFormActions : function() {
            var $currModal = BLCAdmin.currentModal();
            if ($currModal && $currModal.has('.modal-add-entity-form').length) {
                // in community, modal submit gets disabled when there is a validation error
                $('.submit-button', $currModal).prop('disabled', !this.getEntityFormChangesCount());
            }
            
            // Grab all buttons we might want to enable/disable
            var $saveBtn = $('.sticky-container').find('.entity-form-actions').find('button.submit-button');
            var $promoteBtn = $('.sandbox-actions').find('.button a:contains("Promote")').parent();
            var $approveBtn = $('.sandbox-actions').find('.button a:contains("Approve")').parent();

            if (this.getEntityFormChangesCount()) {
                // Check to see if there are any changes in the `entityFormChangeMap`
                // If there are, we want to make sure the 'Save' button is active and any workflow buttons are not.
                $saveBtn.prop('disabled', false);
                $promoteBtn.addClass('confirm');
                $approveBtn.addClass('confirm');

                $("#headerChangeBoxContainer").removeClass("hidden");
                $(".change-box-message").html(BLCAdmin.messages.unsavedChangesRevert);
                $('#headerChangeBox').show();
            } else {
                // Otherwise, we don't have any unsaved changes.  So disable the 'Save' button and make sure the
                // workflow buttons are enabled
                $saveBtn.prop('disabled', true);
                $promoteBtn.removeClass('confirm');
                $approveBtn.removeClass('confirm');

                $("#headerChangeBoxContainer").addClass("hidden");
                $('#headerChangeBox').hide();
            }
        },

        /**
         * This function is called whenever there is input triggered on the DOM.  Depending on what type of field
         * was modified we need to look in different places for the value / original value.
         *
         * @param el
         */
        handleEntityFormChanges: function(el) {
            // Check if we should handle the changes
            if (!this.checkIfShouldTrackChanges(el)) { return }

            var id = $(el).attr('id');
            var newVal = $(el).val() || '';
            var origVal = $(el).attr('data-orig-val') || '';

            // If this is a radio input, we want to use the 'name' attribute instead of the 'id'
            if ($(el).is(':radio')) {
                id = $(el).attr('name');
            }
            // If this is a media item input we only care about the url attribute
            else if ($(el).hasClass('mediaItem')) {
                if (!$(el).hasClass('mediaUrl')) {
                    var mediaJson = JSON.parse(newVal || '{}');
                    newVal = mediaJson === null || mediaJson.url === null ? '' : mediaJson.url;
                }
            }
            // If this is a redactor field, we have to get the id from its textarea and it's value from its text.
            else if ($(el).hasClass('redactor-editor')) {
                id = $(el).next('textarea').attr('id');
                newVal = $(el).text() || '';
            }
            // If this is a field within a query builder, grab the id from the builder and set the new value to the id
            // we will treat any click into a `RuleBuilder` as a change
            else if ($(el).closest('.query-builder-rules-container').length && !$(el).hasClass('rules-group-header-item-qty')) {
                if (!$(el).hasClass('query-builder-selectize-input') && $(el).closest('.rule-value-container').length) {
                    $(el).trigger('change');
                }

                var $ruleBuilderContainer = $(el).closest('.query-builder-rules-container');
                id = $ruleBuilderContainer.attr('id');

                // In order to get the new rules on this `RuleBuilder` we need to grab the actual `RuleBuilder`
                var hiddenId = $ruleBuilderContainer.siblings('.rule-builder-data').first().data('hiddenid');
                var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByHiddenId(hiddenId);

                var rules = BLCAdmin.ruleBuilders.getAllRuleBuilderRules(ruleBuilder);
                newVal = JSON.stringify(rules);

                origVal = $ruleBuilderContainer.attr('data-orig-val');

                // If the original value is not set, this is likely being populated for the first time.
                // In this case, we set the original value to the new value.
                if ($ruleBuilderContainer.attr('data-orig-val') === undefined) {
                    $ruleBuilderContainer.attr('data-orig-val', JSON.stringify(newVal));
                    origVal = newVal;
                }

                // Convert the new value to a JSON object
                newVal = JSON.stringify(newVal);
            }
            // This is a special case for rule builders, we need to capture the match quantity value
            else if ($(el).hasClass('rules-group-header-item-qty')) {
                var $ruleGroupContainer = $(el).closest('.rules-group-container');
                id = $ruleGroupContainer.attr('id');
                origVal = $ruleGroupContainer.attr('data-orig-val');
            }

            this.updateEntityFormChangeMap(id, origVal, newVal);
        },

        /**
         * This function is used to initialize any form elements that don't respond to the `focusin` event
         */
        initializeOriginalValues : function () {
            /**
             * Because radio buttons don't respond to `focusin`, we iterate all of them at load time and add their
             * original value as a data field.  This is used for comparison when the field value changes.
             */
            $('input:radio').each(function(i, el) {
                // if this element is in an OMS tab, we don't want to track
                if ($(el).closest('.oms-tab').length) {
                    return false;
                }

                var id = $(el).attr('name');
                var $thisRadio = $('[name="' + id + '"]');
                var $checkedRadio = $('[name="' + id + '"]:checked');

                if (!$checkedRadio.length) {
                    $checkedRadio = $('[name="' + id + '"].checked');
                }
                if ($thisRadio.attr('data-orig-val') === undefined) {
                    $thisRadio.attr('data-orig-val', $checkedRadio.val());
                }
            });

            /**
             * Because media items are hidden inputs and don't respond to `focusin`, we iterate all of them at load time and add
             * their original url as a data field.  This is used for comparison when the field value changes.
             */
            $('input.mediaItem').each(function(i, el) {
                // if this element is in an OMS tab, we don't want to track
                if ($(el).closest('.oms-tab').length) {
                    return false;
                }

                var origVal = $(el).val() || '';
                var mediaUrl;
                if ($(el).hasClass('mediaUrl')) {
                    mediaUrl = origVal;
                } else {
                    var mediaJson = JSON.parse(origVal || '{}');
                    mediaUrl = mediaJson === null || mediaJson.url === null ? '' : mediaJson.url
                }
                $(this).attr('data-orig-val', mediaUrl);
            });

            /**
             * For lookups we need to store the original id value as well as the original display value
             */
            $('.additional-foreign-key-container').each(function(i, el) {
                // if this element is in an OMS tab, we don't want to track
                if ($(el).closest('.oms-tab').length) {
                    return false;
                }

                var $valueContainer = $(el).find('.value');
                var origVal = $valueContainer.val() || '';
                $valueContainer.attr('data-orig-val', origVal);

                var $displayContainer = $(el).find('.hidden-display-value');
                var origVal = $displayContainer.val() || '';
                $displayContainer.attr('data-orig-val', origVal);
            });

            /**
             * For RuleBuilders, we need to set their original value as their current rules.  We set this on the
             * main container.
             */
            $('.rule-builder-required-field').each(function(i, el) {
                // if this element is in an OMS tab, we don't want to track
                if ($(el).closest('.oms-tab').length) {
                    return false;
                }

                var rulesContainer = $($(this)).siblings('.query-builder-rules-container');
                var rulesContainerID = rulesContainer.attr('id');
                var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilder(rulesContainerID);

                // Set the original value on the rule builder once its been completely initialized
                if (ruleBuilder && ruleBuilder.builders.length) {
                    var rules = BLCAdmin.ruleBuilders.getAllRuleBuilderRules(ruleBuilder);
                    var origVal = JSON.stringify(rules);

                    $(rulesContainer).attr('data-orig-val', JSON.stringify(origVal));
                    BLCAdmin.entityForm.status.removeChangesForId($(rulesContainer).attr('id'));
                }
            });
        },

        /**
         * This function checks if there are any unsaved changes to the entity form and if so presents a dialog asking
         * if the user is sure they want to navigate away.  If the user confirms, the window.location.href is modified.
         *
         * @param el
         * @param event
         */
        confirmLeaveEntityForm : function(el, event) {
            // Check if the user has made any unsaved changes
            if (this.getEntityFormChangesCount()) {
                // If there are changes we want to stop the click action
                event.preventDefault();

                // Save the href target for later
                var target = $(el).attr('href');

                // Show the dialog asking if they want to leave
                $.confirm({
                    content: BLCAdmin.messages.unsavedChanges,
                    backgroundDismiss: true,
                    confirm: function () {
                        BLCAdmin.entityForm.status.setDidConfirmLeave(true);
                        location.href = target;
                    }
                });
            }
        },

        /**
         * On a workflow action, we want to check if there are any changes present.  If so we will warn the user that
         * proceeding will revert their changes back to their original values.
         *
         * @param el
         * @param event
         * @returns {boolean}
         */
        confirmWorkflowAction : function(el, event) {
            // Check if the user has made any unsaved changes
            if (BLCAdmin.entityForm.status.getEntityFormChangesCount()) {
                var message;
                // If this is a `Promote` show the promote message
                if ($(el).text() == 'Promote') {
                    message = BLCAdmin.messages.promoteUnsavedChanges;
                }
                // Else, if this is an `Approve` show the approve message
                else if ($(el).text() == 'Approve') {
                    message = BLCAdmin.messages.approveUnsavedChanges;
                }
                // Otherwise, this is another button on the ribbon, continue as normal
                else {
                    return true;
                }

                // Show the dialog asking if they are sure they want to `Promote`/`Approve`
                $.confirm({
                    content: message,
                    backgroundDismiss: true,
                    confirm: function () {
                        BLCAdmin.entityForm.status.revertEntityFormChanges(false);
                        BLCAdmin.workflow.showApproveActionPrompt($(el), false);
                    }
                });
                return false;
            }

            return true;
        },

        /**
         * This function returns true or false depending on if we want to track changes on the current page.
         *
         * @returns {boolean}
         */
        checkIfShouldTrackChanges : function(el) {

            // if this element is in an OMS tab, we don't want to track
            if (el !== undefined && $(el).closest('.oms-tab').length) {
                return false;
            }

            // Don't track if we are on an OMS page, in a modal (except add entity modal) or not on a page with an entity form
            if ((el !== undefined && $('.oms').length) ||
                $(el).closest('.modal:not(:has(.modal-add-entity-form))').length ||
                !$('.entity-form').length) {
                return false;
            }

            // If this is a Selectize Adder or Collection input, we don't want to track as changes are auto-saved
            if (el !== undefined && ($(el).closest('.selectize-adder').length || $(el).closest('.selectize-collection').length)) {
                return false;
            }

            // If this is a boolean-link, it is purely frontend related and not actually changing any values of import
            if (el !== undefined && $(el).closest('.field-group').find('.boolean-link').length) {
                return false;
            }

            return true;
        }
    };
})(jQuery, BLCAdmin);

$(document).ready(function() {

    // Save a reference to the body so we don't have to use a jQuery selector for every event handler
    var $body = $('body');

    /**
     * This event handler saves a fields original value on the first time it is focused on.
     */
    $body.on('focus', 'input, select, input:radio, textarea, .redactor-editor', function() {
        // We only care about main entity froms.  If we are in a modal, just return
        if (!BLCAdmin.entityForm.status.checkIfShouldTrackChanges(this)) { return }

        if ($(this).attr('data-orig-val') === undefined) {
            var origVal = $(this).val() || '';

            // If this is a selectize, we want the actual selected item
            if ($(this).closest('.selectize-control').length) {
                var $selectizeControl = $(this).closest('.selectize-control');
                origVal = $(this).prev('.item').data('value');
                $selectizeControl.prev('select').attr('data-orig-val', origVal);
                return;
            }
            // Check if this is part of a `RuleBuilder`, if it is we want to get the rules off
            // of the actual `RuleBuilder`
            else if ($(this).closest('.query-builder-rules-container').length && !$(this).hasClass('rules-group-header-item-qty')) {
                var $ruleBuilderContainer = $(this).closest('.query-builder-rules-container');

                if ($ruleBuilderContainer.attr('data-orig-val') === undefined) {
                    // In order to get the new rules on this `RuleBuilder` we need to grab the actual `RuleBuilder`
                    var hiddenId = $ruleBuilderContainer.next('.rule-builder-data').data('hiddenid');
                    var ruleBuilder = BLCAdmin.ruleBuilders.getRuleBuilderByHiddenId(hiddenId);

                    var rules = BLCAdmin.ruleBuilders.getAllRuleBuilderRules(ruleBuilder);
                    var origVal = JSON.stringify(rules);
                    $ruleBuilderContainer.attr('data-orig-val', JSON.stringify(origVal));
                }
                return;
            }
            // This is a special case for rule builders, we need to capture the match quantity value
            else if ($(this).hasClass('rules-group-header-item-qty')) {
                var $ruleGroupContainer = $(this).closest('.rules-group-container');
                $ruleGroupContainer.attr('orig-val', $(this).val());
                return;
            }
            // If this is a redactor field, we have to set its text attribute, not its value
            else if ($(this).hasClass('redactor-editor')) {
                origVal = $(this).html();
            }
            $(this).attr('data-orig-val', origVal);
        }
    });

    /**
     * This event handler is fired for `input` type events.
     * It gets the field's id, original value, and new value to be used in the entity form's change map.
     */
    $body.on('input', 'input[id!="listgrid-search"], textarea, .redactor-editor', function() {
        BLCAdmin.entityForm.status.handleEntityFormChanges(this);
    });

    /**
     * This event handler is fired for `change` type events.
     * It gets the field's id, original value, and new value to be used in the entity form's change map.
     */
    $body.on('change', 'select, input:radio, input.query-builder-selectize-input, input:file', function() {
        BLCAdmin.entityForm.status.handleEntityFormChanges(this);
    });

    /**
     * This event handler intercepts the click event for any link that would cause us to leave the entity form
     * and presents a dialog asking if they are sure they want to leave.
     */
    $body.on('click', 'a.back-button, ul.nav-links a', function(event) {
       BLCAdmin.entityForm.status.confirmLeaveEntityForm(this, event);
    });

    /**
     * This is a click event handler responsible for reverting entity form changes.
     */
    $body.on('click', 'a#revert-changes', function(event) {
        event.preventDefault();
        BLCAdmin.entityForm.status.revertEntityFormChanges(true);
    });

    /**
     * This event handler intercepts the click event for any link that would cause us to leave the entity form
     * and presents a dialog asking if they are sure they want to leave.
     */
    $(window).on('beforeunload', function() {
        if (BLCAdmin.entityForm.status.getEntityFormChangesCount() &&
            !BLCAdmin.entityForm.status.getDidConfirmLeave() &&
            BLCAdmin.entityForm.status.checkIfShouldTrackChanges()) {
            return BLCAdmin.messages.unsavedChangesBrowser;
        }
    });

    /**
     * We want to update the entity form's actions on window load.
     * But only if we are on an actual entity form and not in a modal.
     */
    if (BLCAdmin.entityForm.status.checkIfShouldTrackChanges()) {
        BLCAdmin.entityForm.status.initializeOriginalValues();
        BLCAdmin.entityForm.status.updateEntityFormActions();
    }
});
