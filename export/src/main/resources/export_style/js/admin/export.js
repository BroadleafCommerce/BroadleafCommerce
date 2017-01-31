/*
 * #%L
 * broadleaf-importexport
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

/**
 * Show the import/export configuration prompt
 */
$('body').on('click', 'a.export-standard-action', function(e) {
    e.preventDefault();
    console.log("hit the export button");
    var $this = $(this);
    var link = $this.data('url');
    var data = {};
    getExportPrompt(link, data);
});

$('body').on('click', 'button.export-standard-action', function(e) {
    e.preventDefault();
    console.log("hit the export button");
    var $this = $(this);
    var link = $this.data('actionurl');
    var data = {};
    getExportPrompt(link, data);
});

function getExportPrompt(link, data) {
    BLC.ajax({
        url: link,
        data: data,
        type: "GET"
    }, function(data) {
        if (data instanceof Object && data.hasOwnProperty('status') && data.status == 'error') {
            if (data.errorCode == 'noItems') {
                BLCAdmin.showMessageAsModal(data.exception, data.extraDetail);
            }
            return false;
        }

        BLCAdmin.showElementAsModal($(data));
    });
}

/**
 * Upload the file
 */
$('body').on('click', '.export-configure-prompt button.export-schedule', function() {

    var $button = $('.export-configure-prompt button.export-schedule');

    // hide buttons and show loading spinner
    $button.hide();
    $('.export-configure-prompt .action-popup-cancel').hide();
    $('.export-configure-prompt .submit-error').hide();
    $('.export-configure-prompt .ajax-loader').show();

    var $form = $(this).closest('form')
    $.ajax({
        type: "POST",
        url: $form.attr("action"),
        data: $form.serialize(),
        success: function() {
            window.location.reload(true);
        }
    });
    return false;
});

