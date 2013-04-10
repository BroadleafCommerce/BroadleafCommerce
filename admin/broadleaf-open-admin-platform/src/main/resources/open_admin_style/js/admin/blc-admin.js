/* Utility methods provided by Broadleaf Commerce for admin */
var BLCAdmin = (function($) {
	
	// This will keep track of our current active modals so that we are able to overlay them
	var modals = new Array();
	var stackedModalOptions = {
	    left: 20,
	    top: 20
	}
    
	function showLinkAsModal(link, onModalHide, onModalHideArgs) {
		$.get(link, function(data) {
			// Create a modal out of the server response
			var $data = $(data);
			$data.attr('id', 'modal' + modals.length);
			$('body').append($data);
			
			initializeFields($data);
			
			// If we already have an active modal, we don't need another backdrop on subsequent modals
			$data.modal({
				backdrop: (modals.length < 1)
			});
			
			// If we already have an active modal, we need to modify its z-index so that it will be
			// hidden by the current backdrop
			if (modals.length > 0) {
				modals.last().css('z-index', '1040');
				
				var $backdrop = $('.modal-backdrop');
				$backdrop.css('z-index', parseInt($backdrop.css('z-index')) + 1);
				
				// We will also offset modals by the given option values
				$data.css('left', $data.position().left + (stackedModalOptions.left * modals.length) + 'px');
				$data.css('top', $data.position().top + (stackedModalOptions.top * modals.length) + 'px');
			}
			
			// Save our new modal into our stack
			modals.push($data);
			
			// Bind a callback for the modal hidden event...
			$data.on('hidden', function() {
				
				// Allow custom callbacks
				if (onModalHide != null) {
					onModalHide(onModalHideArgs);
				}
				
				// Remove the modal from the DOM and from our stack
				$(this).remove();
				modals.pop();
				
				// If this wasn't the only modal, take the last modal and put it above the backdrop
				if (modals.length > 0) {
					modals.last().css('z-index', '1050');
				}
			});
		});
	};
	
	// Convenience function for hiding the replacing the current modal with the given link
	function modalNavigateTo(link) {
		if (currentModal()) {
			currentModal().modal('hide');
		}
		showLinkAsModal(link);
	};
	
	// Convenience function for returning the current modal
	function currentModal() {
		return modals.last();
	}
	
	function hideCurrentModal() {
		if (currentModal()) {
			currentModal().modal('hide');
		}
	}
	
	function focusOnTopModal() {
	    if (currentModal()) {
	        currentModal().focus();
	    }
	}
	
	function initializeFields($container) {
	    // Set up the rich-text HTML editor
        $container.find('.redactor').redactor();
        
        // Set up the date-time picker
        $container.find('.datepicker').each(function(index, element) {
            BLCAdmin.dates.onLive($(element));
        });
        
        // Set the blank value for foreign key lookups
        $container.find('.foreign-key-value-container').each(function(index, element) {
            var $displayValue = $(this).find('span.display-value');
            if ($displayValue.text() == '') {
                $displayValue.text($(this).find('span.display-value-none-selected').text());
            }
        });
	}
	
	// The publicly accessible functions provided by this module
    return {
    	showLinkAsModal : showLinkAsModal,
    	modalNavigateTo : modalNavigateTo,
    	currentModal : currentModal,
    	hideCurrentModal : hideCurrentModal,
    	focusOnTopModal : focusOnTopModal,
    	initializeFields : initializeFields
    }
    
})(jQuery);