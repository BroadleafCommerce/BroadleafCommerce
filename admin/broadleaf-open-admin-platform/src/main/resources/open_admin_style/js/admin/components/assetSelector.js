$(document).ready(function() {
			
	/**
	 * This handler will fire when the choose image button is clicked on the RTE control or as
	 * part of a form dialogue.
	 * 
	 * It is responsible for binding a assetInfoSelected handler for this field as well as launching
	 * a image selection modal that will be used to select the image / media item.
	 * 
	 */
    $('body').on('click', 'a.show-asset-selector', function(event) {
    	var $container = $(this).closest('div.asset-selector-container');
    	
    	$container.on('assetInfoSelected', function(event, fields) {
    		var $this = $(this);
    		    		   
    		$this.find('input.assetUrl').val(fields['assetUrl']);
    		$this.find('img.assetSmallUrl').attr("src",fields['assetThumbnail']);
    		
    		var mediaItem = $this.find('input.mediaItem');
    		var mediaJson = jQuery.parseJSON(mediaItem.val());
    		mediaJson.url = fields['assetUrl'];
    		mediaItem.val(JSON.stringify(mediaJson));
    		
			BLCAdmin.currentModal().modal('hide');
    	});
    	
    	BLCAdmin.showLinkAsModal($(this).attr('href'), function() {
			$('div.asset-selector-container').unbind('assetInfoSelected');
    	});
    	
		return false;
    });    
});