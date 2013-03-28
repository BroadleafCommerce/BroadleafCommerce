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
	});
	    
});