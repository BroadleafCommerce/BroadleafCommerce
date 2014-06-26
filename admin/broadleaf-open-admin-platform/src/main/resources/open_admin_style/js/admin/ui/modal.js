$(document).ready(function() {
	
	$('body').on('click', '.modal-footer button.btn-primary', function() {
		$(this).closest('div.modal').find('form').submit();
	});
	
	$('body').on('shown', '.modal', function () {
		$("html").css({ overflow: 'hidden' });
	});
	
	$('body').on('hide', '.modal', function () {
		$("html").css({ overflow: 'auto' });
	});
	
	$('body').on('click', 'a.modal-view', function() {
    	BLCAdmin.showLinkAsModal($(this).attr('href'));
		return false;
	});
	
});