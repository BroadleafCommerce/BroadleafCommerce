/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/* Utility methods provided by Broadleaf Commerce for admin */
var BLCAdmin = (function($) {
    
	// This will keep track of our current active modals so that we are able to overlay them
	var modals = [];
	var preValidationFormSubmitHandlers = [];
	var validationFormSubmitHandlers = [];
	var postValidationFormSubmitHandlers = [];
	var initializationHandlers = [];
	var updateHandlers = [];
	var stackedModalOptions = {
	    left: 20,
	    top: 20
	}
	var originalStickyBarOffset = $('.sticky-container').offset().top;
	
    var fieldSelectors = 'input, .custom-checkbox, .foreign-key-value-container span.display-value, .redactor_box, ' + 
                         '.asset-selector-container img, select, div.custom-checkbox, div.small-enum-container, ' + 
                         'textarea';
	
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
			backdrop: (modals.length < 1),
			keyboard: false  // disable default keyboard behavior; wasn't intended to work with layered modals
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
				BLCAdmin.currentModal().find('.submit-button').show();
				BLCAdmin.currentModal().find('img.ajax-loader').hide();
			}
		});
		
		BLCAdmin.initializeModalTabs($data);
        BLCAdmin.initializeModalButtons($data);
		BLCAdmin.setModalMaxHeight(BLCAdmin.currentModal());
		BLCAdmin.initializeFields();
	}
	
	return {
	    /**
	     * Handlers to run before client-side validation takes place
	     */
	    addPreValidationSubmitHandler : function(fn) {
	        preValidationFormSubmitHandlers.push(fn);
	    },
	    
	    /**
	     * Handlers explicitly designed to validate forms on the client before submitting to the server. If a single handler
	     * in the list of client-side validation handlers returns 'false', then the form will not be submitted to the
	     * server
	     */
	    addValidationSubmitHandler : function(fn) {
            validationFormSubmitHandlers.push(fn);
        },
        
        /**
         * Handlers designed to execute after validation has taken place but before the form has been submitted to the server
         */
        addPostValidationSubmitHandler : function(fn) {
            postValidationFormSubmitHandlers.push(fn);
        },
	    
	    addInitializationHandler : function(fn) {
	        initializationHandlers.push(fn);
	    },
	    
	    addUpdateHandler : function(fn) {
	        updateHandlers.push(fn);
	    },
	    
    	runPreValidationSubmitHandlers : function($form) {
            for (var i = 0; i < preValidationFormSubmitHandlers.length; i++) {
                preValidationFormSubmitHandlers[i]($form);
            }
    	},
    	
    	/**
    	 * Returns false if a single validation handler returns false. All validation handlers are iterated through before
    	 * returning. If this method returns false, then the form should not be submitted to the server
    	 */
    	runValidationSubmitHandlers : function($form) {
    	    var pass = true;
            for (var i = 0; i < validationFormSubmitHandlers.length; i++) {
                pass = pass && validationFormSubmitHandlers[i]($form);
            }
            return pass;
        },
        
        runPostValidationSubmitHandlers : function($form) {
            for (var i = 0; i < postValidationFormSubmitHandlers.length; i++) {
                postValidationFormSubmitHandlers[i]($form);
            }
        },
        
        /**
         * Convenience method to submit all pre-validation, validation and post-validation handlers for the form. This will
         * return the result of invoking 'runValidationSubmitHandlers' to denote whether or not the form should actually
         * be submitted to the server
         */
        runSubmitHandlers : function($form) {
            BLCAdmin.runPreValidationSubmitHandlers($form);
            var submit = BLCAdmin.runValidationSubmitHandlers($form);
            BLCAdmin.runPostValidationSubmitHandlers($form);
            return submit;
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
                var $footer = $data.find('div.modal-footer');
                if (!$footer.length) {
                    $footer = $('<div>', { 'class' : 'modal-footer' });
                    $buttonDiv.remove().appendTo($footer);
                    $data.append($footer);
                }
            }
    	},
    	
    	showMessageAsModal : function(header, message) {
			if (BLCAdmin.currentModal() != null && BLCAdmin.currentModal().hasClass('loading-modal')) {
			    BLCAdmin.hideCurrentModal();
			}
			
    	    var $modal = getModalSkeleton();
    	    
    	    $modal.find('.modal-header h3').text(header);
    	    $modal.find('.modal-body').text(message);
    	    $modal.find('.modal-body').css('padding-bottom', '20px');
    	    
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
                buttons : ['html', 'formatting', 'bold', 'italic', 'deleted', 
                           'unorderedlist', 'orderedlist', 'outdent', 'indent',
                           'video', 'file', 'table', 'link',
                           'fontfamily', 'fontcolor', 'alignment', 'horizontalrule'],
                plugins: ['selectasset', 'fontfamily', 'fontcolor', 'fontsize'],
                convertDivs : false,
                xhtml       : true,
                paragraphy  : false,
                minHeight   : 140,
                deniedTags  : []
            });
            
            $container.find('textarea.autosize').autosize();
            
            $container.find(".color-picker").spectrum({
                showButtons: false,
                preferredFormat: "hex6",
                change: function(color) {
                    $(this).closest('.field-box').find('input.color-picker-value').val(color);
                },
                move: function(color) {
                    $(this).closest('.field-box').find('input.color-picker-value').val(color);
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
    	},
    	
    	getModals : function() {
    	    var modalsCopy = [];
    	    for (var i = 0; i < modals.length; i++) {
    	        modalsCopy[i] = modals[i];
    	    }
    	    return modalsCopy;
    	},
 
    	getForm : function($element) {
    	    var $form;
    	    
    	    if ($element.closest('.modal').length > 0) {
    	        $form = $element.closest('.modal').find('.modal-body form');
    	    } else {
    	        $form = $element.closest('form')
    	    }
    
    		if (!$form.length) {
    		    $form = $('.entity-edit form');
    		}
    	    
    		return $form;
    	},
    	
    	getOriginalStickyBarOffset : function() {
    	    return originalStickyBarOffset;
    	},
    	
    	getFieldSelectors : function getFieldSelectors() {
    	    return fieldSelectors.concat();
    	},

        /**
         * Adds an initialization handler that is responsible for toggling the visiblity of a child field based on the
         * current value of the associated parent field.
         * 
         * @param className - The class name that this handler should be bound to
         * @param parentFieldName - A jQuery selector to use to find the div.field-box for the parent field
         * @param childFieldName - A jQuery selector to use to find the div.field-box for the child field
         * @param showIfValue - Either a function that takes one argument (the parentValue) and returns true if the
         *                      child field should be visible or a string to directly match against the parentValue
         */
        addDependentFieldHandler : function addDependentFieldHandler(className, parentFieldName, childFieldName, showIfValue) {
            BLCAdmin.addInitializationHandler(function($container) {
                var thisClass = $container.closest('form').find('input[name="ceilingEntityClassname"]').val();
                if (thisClass != null && thisClass.indexOf(className) >= 0) {
                    var toggleFunction = function(event) {
                        // Extract the parent and child field DOM elements from the data
                        var $parentField = event.data.$parentField;
                        var $childField = event.data.$childField;
                        
                        // Figure out what the current value of the parent field is
                        var parentValue = $parentField.find('input[type="radio"]:checked').val();
                        if (parentValue == null) {
                            parentValue = $parentField.find('select').val();
                        }
                        if (parentValue == null) {
                            parentValue = $parentField.find('input[type="text"]').val();
                        }
                        
                        // Either match the string or execute a function to figure out if the child field should be shown
                        // Additionally, if the parent field is not visible, we'll assume that the child field shouldn't
                        // render either.
                        var shouldShow = false;
                        if ($parentField.is(':visible')) {
                            if (typeof showIfValue == "function") {
                                shouldShow = showIfValue(parentValue);
                            } else {
                                shouldShow = (parentValue == showIfValue);
                            }
                        }
                        
                        // Toggle the visiblity of the child field appropriately
                        $childField.toggle(shouldShow);
                        $childField.trigger('change');
                    };
                    
                    var $parentField = $container.find(parentFieldName);
                    var $childField = $container.find(childFieldName);
                    
                    var data = {
                        '$parentField' : $parentField,
                        '$childField' : $childField
                    };
                    
                    // Bind the change event for the parent field
                    $parentField.on('change', data, toggleFunction);
    
                    // Run the toggleFunction immediately to set initial states appropriately
                    toggleFunction({ data : data });
                }
            })
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
    
    if (window.location.hash) {
        var $listGrid = $('div.listgrid-container' + window.location.hash);
        if ($listGrid.length) {
            var $tab = $listGrid.closest('li.entityFormTab');
            var idx = $tab.index() + 1;
            var $tabLink = $('div.tabs-container dl dd:nth-child(' + idx + ')');
            $.fn.foundationTabs('set_tab', $tabLink);
            $(window).scrollTop($(window.location.hash).offset().top);
        }
    }

    // Ensure that the breadcrumb will render behind the entity form actions
    var $bcc = $('.sticky-container');
    $bcc.find('ul.breadcrumbs').outerWidth($bcc.outerWidth() - $bcc.find('.entity-form-actions').outerWidth() - 30);
});

// Close current modal on escape key
$('body').on('keyup', function(event) {
    if (event.keyCode == 27) {  // if key is escape
        BLCAdmin.hideCurrentModal();
    }
});

$('body').on('click', '.disabled', function(e) {
    e.stopPropagation();
    return false;
});
        
$('body').on('change', 'input.color-picker-value', function() {
    var $this = $(this);
    $this.closest('.field-box').find('input.color-picker').spectrum('set', $this.val());
});

/**
 * Make the sticky bar (breadcrumb) lock at the top of the window when it's scrolled off the page
 */
$(window).on('scroll', function() {
    var $sc = $('.sticky-container');
    var $scp = $('.sticky-container-padding');
       
    if ($(window).scrollTop() < BLCAdmin.getOriginalStickyBarOffset()) {
        $sc.removeClass('sticky-fixed');
        $sc.width('');
        $scp.hide();
    } else {
        $scp.show();
        $sc.addClass('sticky-fixed');
        $sc.outerWidth($('section.main').outerWidth());
        $('.sticky-container-padding').outerHeight($sc.outerHeight());
    }
});

/**
 * Close the workflow confirm action dialog
 */
$('body').on('click', 'a.action-popup-cancel', function() {
    var $this = $(this);
    if ($this.hasClass('no-remove')) {
        $this.closest('div.action-popup').addClass('hidden');
    } else {
        $this.closest('div.action-popup').remove();
    }
    return false;
});
$(document).keyup(function(e){
    if (e.keyCode === 27) {
        var $actionPopup = $('div.action-popup');
        if ($actionPopup) {
            $actionPopup.remove();
        }
    }
});
