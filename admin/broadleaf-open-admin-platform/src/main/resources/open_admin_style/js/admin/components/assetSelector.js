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
    
    var currentRedactor = null;
    
    // Add utility functions for assets
    BLCAdmin.asset = {
        /**
         * Triggered when a user has chosen a file from the file system. Responsible for creating an 
         * iFrame to allow AJAX file upload, triggering the upload, and delegating the response handling
         */
        assetSelected : function($input) {
            var $form = $input.closest('form');
            
            $form.find('button.uploadButton').hide();
            $form.find('div.uploadMessage').show();
            
            var $iframe = $('<iframe>', {
                'name' : 'upload_target',
                'class' : 'upload_target hidden'
            });
            $form.before($iframe);
            
            if (currentRedactor == null) {
                $iframe.load(this.iframeOnLoad);
            } else {
                $iframe.load(this.iframeOnLoadRedactor);
            }

            $form.attr('target', 'upload_target');
            $form.submit();
        },
        
        selectButtonClickedRedactor : function(obj, event, key) {
            currentRedactor = this;
            currentRedactor.selection.save();
            var $redactor = this.$element;
            
            $redactor.on('assetInfoSelected', function(event, fields) {
                currentRedactor.selection.restore();
                var assetUrl =   fields['assetUrl'];
                if (assetUrl.charAt(0) == "/") assetUrl = assetUrl.substr(1);
                var $img = $('<img>', { 'src' : assetUrl });
                currentRedactor.insert.html($img.outerHTML());
                BLCAdmin.hideCurrentModal();
            });

            BLCAdmin.showLinkAsModal($('textarea.redactor').data('select-asset-url'), function() {
                $('textarea.redactor').unbind('assetInfoSelected');
                currentRedactor = null;
            });
        },
        
        /**
         * Handle the response of a form submit for asset
         */
        iframeOnLoad : function() {
            var json = $.parseJSON($(this).contents().text());
            var $container = $(this).closest('div.uploadFileFormContainer');
            $container.find('img.imagePreview').attr('src', json.assetLarge);
            $container.find('div.uploadMessage').hide();
            
            // Note that although we trigger this event on every asset selector container div, only one
            // will have an active event listener for this trigger.
            $('div.asset-selector-container').trigger('assetInfoSelected', json);
        },
        
        iframeOnLoadRedactor : function() {
            var json = $.parseJSON($(this).contents().text());
            $('textarea.redactor').trigger('assetInfoSelected', json);
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'a.show-asset-freeform-url', function(event) {
        event.preventDefault();
        
        var enabled = $(this).data('enabled') == true;
        var $container = $(this).closest('div.asset-selector-container');
        
        if (enabled) {
            $container.find('img.thumbnail').show();
            $container.find('button.show-asset-selector').show();
            $container.find('input.mediaUrl').attr('type', 'hidden');
        } else {
            $container.find('img.thumbnail').hide();
            $container.find('button.show-asset-selector').hide();
            $container.find('input.mediaUrl').attr('type', 'text');
        }
        
        $(this).data('enabled', !enabled);
    });
    
    $('body').on('listGrid-asset-rowSelected', function(event, $target, link, fields, currentUrl) {
        var json = {
            'assetUrl' : fields['cmsUrlPrefix'] + fields['fullUrl'],
            'adminDisplayAssetUrl' : fields['servletContext'] + fields['cmsUrlPrefix'] + fields['fullUrl']
        };
        
        $('div.asset-selector-container').trigger('assetInfoSelected', json);
        $('textarea.redactor').trigger('assetInfoSelected', json);
    });
            
    /**
     * This handler will fire when the choose image button is clicked
     * 
     * It is responsible for binding a assetInfoSelected handler for this field as well as launching
     * a image selection modal that will be used to select the image / media item.
     */
    $('body').on('click', 'button.show-asset-selector', function(event) {
        var $container = $(this).closest('div.asset-selector-container');
        
        $container.on('assetInfoSelected', function(event, fields) {
            var $this = $(this);
                           
            $this.find('img.thumbnail').attr("src", fields['adminDisplayAssetUrl']);
            $this.find('img.thumbnail').data("fullurl", fields['adminDisplayAssetUrl']);
            $this.find('img.thumbnail').parent().attr("href", fields['adminDisplayAssetUrl']);
            $this.find('img.thumbnail').removeClass('placeholder-image');

            var mediaItem = $this.find('input.mediaItem');
            if (mediaItem.length > 0) {
                var mediaUrl = $this.find('input.mediaUrl');
                if (mediaUrl.length > 0) {
                    mediaUrl.val(fields['assetUrl']).trigger('input');
                } else {
                    var mediaJson = mediaItem.val() == "" || mediaItem.val() == "null" ? {} : jQuery.parseJSON(mediaItem.val());
                    mediaJson.url = fields['assetUrl'];
                    mediaItem.val(JSON.stringify(mediaJson)).trigger('input');
                }
            }
            $container.find('button.clear-asset-selector, button.edit-asset-selector').show();
            $container.find('.media-image-container .media-actions').css('display', '');

            $container.find('div.asset-url').html(fields['assetUrl']);

            BLCAdmin.hideCurrentModal();
        });
        
        BLCAdmin.showLinkAsModal($(this).data('select-url'), function() {
            $('div.asset-selector-container').unbind('assetInfoSelected');
        });
        
        return false;
    });

    $('body').on('click', 'button.edit-asset-selector', function() {
        var $modal = BLCAdmin.getModalSkeleton();
        $modal.addClass('primary-media-attrs-modal');
        $modal.find('.modal-header h3').text(BLCAdmin.messages.primaryMediaAttrsFormTitle);
        $modal.find('.modal-body').append(
            "<form id='primary-media-attrs-form'>" +
                "<div class='field-group'>" +
                    "<label for='primary-media-title'>" +
                         "<span>" + BLCAdmin.messages.primaryMediaAttrsTitle + "</span>" +
                    "</label>" +
                    "<div><input id='primary-media-title' type='text'></div>" +
                "</div>" +
                "<div class='field-group'>" +
                    "<label for='primary-media-altText'>" +
                        "<span>" + BLCAdmin.messages.primaryMediaAttrsAltText + "</span>" +
                    "</label>" +
                    "<div><input id='primary-media-altText' type='text'></div>" +
                "</div>" +
                "<div class='field-group'>" +
                    "<label for='primary-media-tags'>" +
                        "<span>" + BLCAdmin.messages.primaryMediaAttrsTags + "</span>" +
                    "</label>" +
                    "<div><input id='primary-media-tags' type='text'></div>" +
                "</div>" +
            "</form>"
        );
        $modal.find('.modal-footer').append(
            "<button form='primary-media-attrs-form' class='button primary large' disabled>" +
                BLCAdmin.messages.primaryMediaAttrsBtnApply +
            "</button>"
        );

        BLCAdmin.showElementAsModal($modal);


        var primaryData = JSON.parse($("#fields\\'defaultSku__skuMedia---primary\\'\\.value").val());

        var $form = $('#primary-media-attrs-form');
        $form.find('#primary-media-title').val(primaryData['title']);
        $form.find('#primary-media-altText').val(primaryData['altText']);
        $form.find('#primary-media-tags').val(primaryData['tags']);
    });

    $('body').on('click', 'button.clear-asset-selector', function(event) {
        //Get the media container
        var $this = $(this);
        var $container = $this.closest('div.asset-selector-container');

        //Set media value to null so that when the request is sent the entry in the map for primary is deleted
        var $mediaUrl = $container.find('input.mediaUrl');
        var $mediaItem = $container.find('input.mediaItem');
        if ($mediaUrl.length > 0) {
            // Fields using mediaUrl require a blank value
            $mediaUrl.val('').trigger('change').trigger('input');
        } else {
            // Other entities require a null value
            $mediaItem.val('null').trigger('change').trigger('input');
        }

        //Set placeholder image and hide clear button since there's nothing to clear
        var src = $container.find('img.placeholder').attr('src');
        var $thumbnail = $container.find('img.thumbnail');
        $thumbnail.removeAttr('data-fullurl');
        $thumbnail.attr('src', src);
        $thumbnail.addClass('placeholder-image');
        $this.hide();
        $container.find('.edit-asset-selector').hide();

        var $mediaImageContainer = $container.find('.media-image-container');
        $mediaImageContainer.find('.media-actions').css('display', 'block');

        $container.find('div.asset-url').html('No media selected.');
    });

    // When we detect that a user has selected a file from his file system, we will trigger an event
    $('body').on('change', 'input.ajaxUploadFile[type="file"]', function() {
        BLCAdmin.asset.assetSelected($(this));
    }); 
    
    // Invisibly proxy a click on our button to the hidden input with type="file" to trigger the 
    // file system browse dialog
    $('body').on('click', 'button.uploadButton', function() {
        $(this).closest('form.uploadFileForm').find('input[type="file"]').click();
        
    });

    // When we detect that a user has selected a file from his file system, we will trigger an event
    $('body').on('change', '#assetUploadFile', function() {
        // TODO: Show a div with "loading" message
        $('#assetUploadForm').submit();
    });
    
    // Workaround for upload button on Internet Explorer < 11
    if(window.navigator.userAgent.indexOf('MSIE ') > 0) {
        $('button.upload-asset').addClass('hidden');
        $('#assetUploadForm').removeClass('hidden');
    }

    // On the asset list view, the upload button triggers this form
       $('body').on('click', 'button.upload-asset', function(event) {
               $('#assetUploadFile').click();
    });

    $('body').on('input', '#primary-media-attrs-form input, #primary-media-attrs-form textarea', function () {
        $("button[form='primary-media-attrs-form']").prop('disabled', false);
    });

    $('body').on('submit', '#primary-media-attrs-form', function () {
        var $this = $(this);

        var $primaryDataField = $("#fields\\'defaultSku__skuMedia---primary\\'\\.value");

        var primaryData = JSON.parse($primaryDataField.val());
        primaryData['title'] = $this.find('#primary-media-title').val();
        primaryData['altText'] = $this.find('#primary-media-altText').val();
        primaryData['tags'] = $this.find('#primary-media-tags').val();

        $primaryDataField.val(JSON.stringify(primaryData));

        $('.submit-button').prop('disabled', false);

        BLCAdmin.hideCurrentModal();

        return false;
    });


});
