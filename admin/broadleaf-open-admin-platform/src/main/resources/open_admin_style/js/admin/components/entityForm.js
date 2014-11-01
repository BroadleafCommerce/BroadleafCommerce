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
$(document).ready(function() {
    
	var $tabs = $('dl.tabs.entity-form');
	if ($tabs.length > 0) {
	    var $lastTab = $tabs.find('dd:last');
	    if ($lastTab.length && $lastTab.width() + $lastTab.position().left + 15 > $tabs.width()) {
            $tabs.mCustomScrollbar({
                theme: 'dark',
                autoHideScrollbar: true,
                horizontalScroll: true
            });
	    }
	}
	
	// When the delete button is clicked, we can change the desired action for the
	// form and submit it normally (not via AJAX).
	$('body').on('click', 'button.delete-button', function(event) {
	    var $form = BLCAdmin.getForm($(this));
		var currentAction = $form.attr('action');
		
		$form.attr('action', currentAction + '/delete');
		$form.submit();
	});
	
	$('body').on('click', 'button.submit-button', function(event) {
        $('body').click(); // Defocus any current elements in case they need to act prior to form submission
	    var $form = BLCAdmin.getForm($(this));
	    $form.submit();

	    var $actions = $(this).closest('.entity-form-actions');
	    $actions.find('button').hide();
	    $actions.find('img.ajax-loader').show();

		event.preventDefault();
	});
	
	$('body').on('submit', 'form.entity-form', function(event) {
        var submit = BLCAdmin.runSubmitHandlers($(this));
        return submit;
	});
	
    $('body').on('click', 'button.add-main-entity', function(event) {
    	BLCAdmin.showLinkAsModal($(this).data('url'));
    	return false;
    });
    
    $('body').on('click', 'a.add-main-entity-select-type', function(event) {
    	BLCAdmin.modalNavigateTo($(this).attr('href'));
    	return false;
    });
    
	$('body').on('submit', 'form.modal-add-entity-form', function(event) {
        var submit = BLCAdmin.runSubmitHandlers($(this));
        
        if (submit) {
    		BLC.ajax({
    			url: this.action,
    			type: "POST",
    			data: $(this).serialize()
    		}, function(data) {
    			$('.modal .modal-body .tabs-content').replaceWith($(data).find('.modal-body .tabs-content'));
    			var errorDiv = $(data).find('.modal-body .errors');
    			if (errorDiv.length) {
    			    //since we only replaced the content of the modal body, ensure the error div gets there as well
    			    var currentErrorDiv = BLCAdmin.currentModal().find('.modal-body .errors')
    			    if (currentErrorDiv.length) {
    			        currentErrorDiv.replaceWith(errorDiv)
    			    } else {
    			        BLCAdmin.currentModal().find('.modal-body').prepend(errorDiv);
    			    }
    			}
    			BLCAdmin.initializeFields($('.modal .modal-body .tabs-content'));
        	    BLCAdmin.currentModal().find('.submit-button').show();
        	    BLCAdmin.currentModal().find('img.ajax-loader').hide();
    	    });
        }
		return false;
	});
	    
});
