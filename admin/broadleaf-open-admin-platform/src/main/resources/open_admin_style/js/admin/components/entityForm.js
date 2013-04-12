$(document).ready(function() {
	
	// When the delete button is clicked, we can change the desired action for the
	// form and submit it normally (not via AJAX).
	$('body').on('click', 'button.delete-button', function(event) {
		var $form = $(this).closest('form');
		var currentAction = $form.attr('action');
		
		$form.attr('action', currentAction + '/delete');
		$form.submit();
	});
	
	$('body').on('click', 'button.submit-button', function(event) {
		$(this).closest('form').submit();
		event.preventDefault();
	});
	
	$('body').on('submit', 'form.entity-form', function(event) {
        BLCAdmin.runSubmitHandlers($(this));
	});
	
    $('body').on('click', 'a.add-main-entity', function(event) {
    	BLCAdmin.showLinkAsModal($(this).attr('href'));
    	return false;
    });
    
    $('body').on('click', 'a.add-main-entity-select-type', function(event) {
    	BLCAdmin.modalNavigateTo($(this).attr('href'));
    	return false;
    });
    
	$('body').on('submit', 'form.modal-add-entity-form', function(event) {
        BLCAdmin.runSubmitHandlers($(this));
        
		BLC.ajax({
			url: this.action,
			type: "POST",
			data: $(this).serialize()
		}, function(data) {
			// Handle error scenario
	    });
		return false;
	});
	    
});