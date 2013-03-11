$(document).ready(function() {
	
	$('body').on('click', '.modal-footer button.btn-primary', function() {
		$(this).closest('div.modal').find('form').submit();
	});
	
	$('body').on('shown', '.modal', function () {
		$("body").css({ overflow: 'hidden' });
	});
	
	$('body').on('hide', '.modal', function () {
		$("body").css({ overflow: 'inherit' });
	});
	
});