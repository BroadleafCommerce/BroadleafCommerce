$(document).ready(function() {
	
    $('body').on('click', 'a.add-main-entity', function(event) {
    	BLCAdmin.showLinkAsModal($(this).attr('href'));
    	return false;
    });
    
    $('body').on('click', 'a.add-main-entity-select-type', function(event) {
    	BLCAdmin.modalNavigateTo($(this).attr('href'));
    	return false;
    });
	
});
