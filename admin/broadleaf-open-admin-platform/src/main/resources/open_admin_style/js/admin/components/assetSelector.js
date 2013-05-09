(function($, BLCAdmin) {
    
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
            
            $iframe.load(this.iframeOnLoad);
            
            $form.attr('target', 'upload_target');
            $form.submit();
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
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('listGrid-asset-rowSelected', function(event, link, fields, currentUrl) {
        var json = {
            'assetUrl' : fields['cmsUrlPrefix'] + fields['fullUrl']
        }
        
        $('div.asset-selector-container').trigger('assetInfoSelected', json);
    });
			
	/**
	 * This handler will fire when the choose image button is clicked on the RTE control or as
	 * part of a form dialogue.
	 * 
	 * It is responsible for binding a assetInfoSelected handler for this field as well as launching
	 * a image selection modal that will be used to select the image / media item.
	 */
    $('body').on('click', 'button.show-asset-selector', function(event) {
    	var $container = $(this).closest('div.asset-selector-container');
    	
    	$container.on('assetInfoSelected', function(event, fields) {
    		var $this = $(this);
    		    		   
    		$this.find('img.thumbnail').attr("src", fields['assetUrl'] + '?largeAdminThumbnail');
    		
    		var mediaItem = $this.find('input.mediaItem');
    		if (mediaItem.length > 0) {
        		var mediaJson = jQuery.parseJSON(mediaItem.val());
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
    $('body').on('change', 'input[type="file"]', function() {
        BLCAdmin.asset.assetSelected($(this));
    });
    
    // Invisibly proxy a click on our button to the hidden input with type="file" to trigger the 
    // file system browse dialog
    $('body').on('click', 'button.uploadButton', function() {
        $(this).closest('form.uploadFileForm').find('input[type="file"]').click();
        
    });
});