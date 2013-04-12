/* Utility methods provided by Broadleaf Commerce for admin */
var BLCAdmin = (function($) {
	
	// This will keep track of our current active modals so that we are able to overlay them
	var modals = [];
	var preFormSubmitHandlers = [];
	var initializationHandlers = [];
	var stackedModalOptions = {
	    left: 20,
	    top: 20
	}
	
	return {
	    addSubmitHandler : function(fn) {
	        preFormSubmitHandlers.push(fn);
	    },
	    
	    addInitializationHandler : function(fn) {
	        initializationHandlers.push(fn);
	    },
	    
    	runSubmitHandlers : function($form) {
            for (var i = 0; i < preFormSubmitHandlers.length; i++) {
                preFormSubmitHandlers[i]($form);
            }
    	},
    	
    	showLinkAsModal : function(link, onModalHide, onModalHideArgs) {
    	    BLC.ajax({
    	        url : link,
    	        type : "GET"
    	    }, function(data) {
    			// Create a modal out of the server response
    			var $data = $(data);
    			$data.attr('id', 'modal' + modals.length);
    			$('body').append($data);
    			
    			BLCAdmin.initializeFields($data);
    			
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
    	},
    	
    	// Convenience function for hiding the replacing the current modal with the given link
    	modalNavigateTo : function(link) {
    		if (BLCAdmin.currentModal()) {
    		    BLC.ajax({
    		        url : link,
    		        type : "GET"
    		    }, function(data) {
        			// Create a modal out of the server response
        			var $data = $(data);
        			BLCAdmin.initializeFields($data);
        			$data = $data.children();
        		    BLCAdmin.currentModal().empty().append($data);
        		});
    		} else {
    		    showLinkAsModal(link);
    		}
    	},
    	
    	// Convenience function for returning the current modal
    	currentModal : function() {
    		return modals.last();
    	},
    	
    	hideCurrentModal : function() {
    		if (BLCAdmin.currentModal()) {
    			BLCAdmin.currentModal().modal('hide');
    		}
    	},
    	
    	focusOnTopModal : function() {
    	    if (BLCAdmin.currentModal()) {
    	        BLCAdmin.currentModal().focus();
    	    }
    	},
    	
    	initializeFields : function($container) {
    	    // Set up rich-text HTML editors
            $container.find('.redactor').redactor();
            
            // Set the blank value for foreign key lookups
            $container.find('.foreign-key-value-container').each(function(index, element) {
                var $displayValue = $(this).find('span.display-value');
                if ($displayValue.text() == '') {
                    $displayValue.text($(this).find('span.display-value-none-selected').text());
                }
            });
            
            // Run any additionally configured initialization handlers
            for (var i = 0; i < initializationHandlers.length; i++) {
                initializationHandlers[i]($container);
            }
    	}
	};
    
})(jQuery);