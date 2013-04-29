$(document).ready(function() {
	
    /**
     * The rowSelected handler for a toOne list grid needs to trigger the specific valueSelected handler 
     * for the field that we are performing the to-one lookup on.
     */
    $('body').on('asset-selected', function(event, link, fields, currentUrl) {
        $('div.asset-selector-container').trigger('assetInfoSelected', fields);
    });
		
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
    		
    		$this.find('input.assetInfo').val(fields['id']);
    		$this.find('input.assetSmallUrl').val(fields['assetSmallUrl']);
    		
			BLCAdmin.currentModal().modal('hide');
    	});
    	
    	BLCAdmin.showLinkAsModal($(this).attr('href'), function() {
			$('div.asset-selector-container').unbind('assetInfoSelected');
    	});
    	
		return false;
    });    
});