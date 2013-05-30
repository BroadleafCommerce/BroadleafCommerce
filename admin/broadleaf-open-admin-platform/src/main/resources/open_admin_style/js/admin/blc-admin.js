/* Utility methods provided by Broadleaf Commerce for admin */
var BLCAdmin = (function($) {
    
	// This will keep track of our current active modals so that we are able to overlay them
	var modals = [];
	var preFormSubmitHandlers = [];
	var initializationHandlers = [];
	var updateHandlers = [];
	var stackedModalOptions = {
	    left: 20,
	    top: 20
	}
	
	/**
	 * Initialize necessary font mappings for Redactor
	 */
	var oFontMap = {
	    arial: ["Arial", "Arial, Helvetica, sans-serif"],
	    arialblack: ["Arial Black", '"Arial Black", Gadget, sans-serif'],
	    comicsans: ["Courier New", '"Courier New", Courier, monospace'],
	    courier: ["Comic Sans", '"Comic Sans MS", cursive, sans-serif'],
	    impact: ["Impact", 'Impact, Charcoal, sans-serif'],
	    lucida: ["Lucida", '"Lucida Sans Unicode", "Lucida Grande", sans-serif'],
	    lucidaconsole: ["Lucida Console", '"Lucida Console", Monaco, monospace'],
	    georgia: ["Georgia", "Georgia, serif"],
	    palatino: ["Palatino Linotype", '"Palatino Linotype", "Book Antiqua", Palatino, serif'],
	    tahoma: ["Tahoma", "Tahoma, Geneva, sans-serif"],
	    times: ["Times New Roman", "Times, serif"],
	    trebuchet: ["Trebuchet", '"Trebuchet MS", Helvetica, sans-serif'],
	    verdana: ["Verdana", "Verdana, Geneva, sans-serif"] 
	};
    var oFontDropdown = {}
    $.each(oFontMap, function(iIndex, oFont){
        var sFontName = oFont[0];
        var sFontFace = oFont[1];
        oFontDropdown[iIndex] = {
            title: "<font face='"+sFontFace+"'>"+sFontName+"</font>",
            callback: function(obj, e, sFont){
                obj.execCommand("fontname", sFontFace);
            }
        }
    });
    
    function getModalSkeleton() {
        var $modal = $('<div>', { 'class' : 'modal' });
        
        var $modalHeader = $('<div>', {
            'class' : 'modal-header'
        });
        $modalHeader.append($('<h3>'));
        $modalHeader.append($('<button>', {
            'class' : 'close',
            'data-dismiss' : 'modal',
            'html' : '&times;'
        }));
        $modal.append($modalHeader);
        
        var $modalBody = $('<div>', { 
            'class' : 'modal-body'
        });
        $modal.append($modalBody);
        
        var $modalFooter = $('<div>', {
            'class' : 'modal-footer'
        });
        $modal.append($modalFooter);
        
        return $modal;
    }
    
	function showModal($data, onModalHide, onModalHideArgs) {
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
		
		BLCAdmin.initializeModalTabs($data);
        BLCAdmin.initializeModalButtons($data);
		BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
		BLCAdmin.initializeFields();
	}
	
	return {
	    addSubmitHandler : function(fn) {
	        preFormSubmitHandlers.push(fn);
	    },
	    
	    addInitializationHandler : function(fn) {
	        initializationHandlers.push(fn);
	    },
	    
	    addUpdateHandler : function(fn) {
	        updateHandlers.push(fn);
	    },
	    
    	runSubmitHandlers : function($form) {
            for (var i = 0; i < preFormSubmitHandlers.length; i++) {
                preFormSubmitHandlers[i]($form);
            }
    	},
    	
    	setModalMaxHeight : function($modal) {
    		// Resize the modal height to the user's browser
    		var availableHeight = $(window).height()
    		    - $modal.find('.modal-header').outerHeight()
    		    - $modal.find('.modal-footer').outerHeight()
    		    - ($(window).height() * .1);
    		
    		$modal.find('.modal-body').css('max-height', availableHeight);
    	},
    	
    	initializeModalTabs : function($data) {
    		var $tabs = $data.find('dl.tabs');
    		if ($tabs.length > 0) {
    		    $tabs.parent().remove().appendTo($data.find('.modal-header'));
    		    
    		    var $lastTab = $tabs.find('dd:last');
    		    if ($lastTab.width() + $lastTab.position().left + 15 > $tabs.width()) {
                    $tabs.mCustomScrollbar({
                        theme: 'dark',
                        autoHideScrollbar: true,
                        horizontalScroll: true
                    });
    		    }
                $data.find('.modal-header').css('border-bottom', 'none');
    		} else {
    		    $data.find('.tabs-container').remove();
    		}
    	},
    	
    	initializeModalButtons : function($data) {
            var $buttonDiv = $data.find('div.entity-form-actions');
            if ($buttonDiv.length > 0) {
                var $footer = $('<div>', { 'class' : 'modal-footer' });
                $buttonDiv.remove().appendTo($footer);
                $data.append($footer);
            }
    	},
    	
    	showMessageAsModal : function(header, message) {
			if (BLCAdmin.currentModal() != null && BLCAdmin.currentModal().hasClass('loading-modal')) {
			    BLCAdmin.hideCurrentModal();
			}
			
    	    var $modal = getModalSkeleton();
    	    
    	    $modal.find('.modal-header h3').text(header);
    	    $modal.find('.modal-body').text(message);
    	    
            this.showElementAsModal($modal);
    	},
    	
    	showElementAsModal : function($element, onModalHide, onModalHideArgs) {
			if (BLCAdmin.currentModal() != null && BLCAdmin.currentModal().hasClass('loading-modal')) {
			    BLCAdmin.hideCurrentModal();
			}
			
			$('body').append($element);
			showModal($element, onModalHide, onModalHideArgs);
    	},
    	
    	showLinkAsModal : function(link, onModalHide, onModalHideArgs) {
    	    // Show a loading message
    	    var $modal = getModalSkeleton();
    	    $modal.addClass('loading-modal');
    	    $modal.find('.modal-header h3').text(BLCAdmin.messages.loading);
    	    $modal.find('.modal-body').append($('<i>', { 'class' : 'icon-spin icon-spinner' }));
    	    $modal.find('.modal-body').css('text-align', 'center').css('font-size', '24px').css('padding-bottom', '15px');
    	    BLCAdmin.showElementAsModal($modal, onModalHide, onModalHideArgs);
            
    	    // Then replace it with the actual requested link
    	    BLCAdmin.modalNavigateTo(link);
    	},
    	
    	// Convenience function for hiding the replacing the current modal with the given link
    	modalNavigateTo : function(link) {
    		if (BLCAdmin.currentModal()) {
    		    BLCAdmin.currentModal().data('initialized', 'false');
    		    BLC.ajax({
    		        url : link,
    		        type : "GET"
    		    }, function(data) {
        			// Create a modal out of the server response
        			var $data = $(data);
        			
        			$data = $data.children();
        		    BLCAdmin.currentModal().empty().append($data);
        		    
        			BLCAdmin.initializeModalTabs(BLCAdmin.currentModal());
        			BLCAdmin.initializeModalButtons(BLCAdmin.currentModal());
        		    BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
        			BLCAdmin.initializeFields();
        			
        			BLCAdmin.currentModal().removeClass('loading-modal');
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
    	
    	getActiveTab : function() {
    	    var $modal = this.currentModal();
    	    if ($modal != null) {
        	    var $tabs = $modal.find('ul.tabs-content');
        	    
        	    if ($tabs.length == 0) {
        	        return $modal;
        	    } else {
        	        return $modal.find('li.active');
        	    }
    	        
    	    } else {
        	    var $body = $('body');
        	    var $tabs = $body.find('ul.tabs-content');
        	    
        	    if ($tabs.length == 0) {
        	        return $body;
        	    } else {
        	        return $tabs.find('li.active');
        	    }
    	    }
    	},
    	
    	initializeFields : function($container) {
    	    // If there is no container specified, we'll initialize the active tab (or the body if there are no tabs)
    	    if ($container == null) {
    	        $container = this.getActiveTab();
    	    }
    	    
    	    // If we've already initialized this container, we'll skip it.
    	    if ($container.data('initialized') == 'true') {
    	        return;
    	    }
    	    
    	    // Set up rich-text HTML editors
            $container.find('.redactor').redactor({
                buttons : ['html', '|', 'formatting', '|', 'bold', 'italic', 'deleted', '|', 
                           'unorderedlist', 'orderedlist', 'outdent', 'indent', '|',
                           'selectAssetButton', 'video', 'file', 'table', 'link', '|',
                           'font', 'fontcolor', 'backcolor', '|', 'alignment', '|', 'horizontalrule'],
                buttonsCustom : {
                    selectAssetButton : {
                        title : BLCAdmin.messages.selectUploadAsset,
                        callback : BLCAdmin.asset.selectButtonClickedRedactor
                    },
                    font : {
                        title: "Advanced Font List",
                        dropdown: oFontDropdown
                    }
                }
            });
            
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
            
            // Mark this container as initialized
    	    $container.data('initialized', 'true');
    	},
    	
    	updateFields : function($container) {
            for (var i = 0; i < updateHandlers.length; i++) {
                updateHandlers[i]($container);
            }
    	}
	};
	
})(jQuery);

// Replace the default AJAX error handler with this custom admin one that relies on the exception
// being set on the model instead of a stack trace page when an error occurs on an AJAX request.
BLC.defaultErrorHandler = function(data) {
    if (data.status == "403") {
        BLCAdmin.showMessageAsModal(BLCAdmin.messages.error, BLCAdmin.messages.forbidden403);
    } else {
        var $data;
        
        if (data.responseText.trim) {
            $data = $(data.responseText.trim());
        } else {
            $data = $(data.responseText);
        }
    
        if ($data.length == 1) {
            BLCAdmin.showElementAsModal($data);
        } else {
            // This shouldn't happen, but it's here as a fallback just in case
            BLCAdmin.showMessageAsModal(BLCAdmin.messages.error, BLCAdmin.messages.errorOccurred);
        }
    }
}

BLC.addPreAjaxCallbackHandler(function($data) {
    if (!($data instanceof jQuery)) {
        return true;
    }
    
    var $loginForm = $data.find('form').filter(function() {
        return $(this).attr('action').indexOf('login_admin_post') >= 0;
    });
    
    if ($loginForm.length > 0) {
        var currentPath = window.location.href;
        if (currentPath.indexOf('?') >= 0) {
            currentPath += '&'
        } else {
            currentPath += '?'
        }
        currentPath += 'sessionTimeout=true';
        
        window.location = currentPath;
        
        return false;
    }
    
    return true;
});

$(document).ready(function() {
    $(window).resize(function() {
        $.doTimeout('resize', 150, function() {
            if (BLCAdmin.currentModal() != null) {
                BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
            }
        });
    });
});
