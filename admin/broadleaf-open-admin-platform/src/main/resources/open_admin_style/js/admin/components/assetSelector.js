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
    
    $('body').on('listGrid-asset-rowSelected', function(event, link, fields, currentUrl) {
        var json = {
            'assetUrl' : fields['cmsUrlPrefix'] + fields['fullUrl'],
            'adminDisplayAssetUrl' : fields['servletContext'] + fields['cmsUrlPrefix'] + fields['fullUrl']
        }
        
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
    		    		   
    		$this.find('img.thumbnail').attr("src", fields['adminDisplayAssetUrl'] + '?largeAdminThumbnail');
    		$this.find('img.thumbnail').data("fullurl", fields['adminDisplayAssetUrl']);
    		$this.find('img.thumbnail').parent().attr("href", fields['adminDisplayAssetUrl']);
    		$this.find('img.thumbnail').removeClass('placeholder-image');
    		
    		var mediaItem = $this.find('input.mediaItem');
    		if (mediaItem.length > 0) {
    		    var mediaJson = mediaItem.val() == "" ? {} : jQuery.parseJSON(mediaItem.val());
        		mediaJson.url = fields['assetUrl'];
        		mediaItem.val(JSON.stringify(mediaJson));
    		} else {
    		    $this.find('input.mediaUrl').val(fields['assetUrl']);
    		}
    		
			BLCAdmin.hideCurrentModal();
    	});
    	
    	BLCAdmin.showLinkAsModal($(this).data('select-url'), function() {
			$('div.asset-selector-container').unbind('assetInfoSelected');
    	});
    	
		return false;
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
    
    // On the asset list view, the upload button triggers this form
    $('body').on('click', 'button.upload-asset', function(event) {    
        $('#assetUploadFile').click();
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
});
