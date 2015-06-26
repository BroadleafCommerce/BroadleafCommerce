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
(function($, BLCAdmin) {
    
    BLCAdmin.translations = {
        getProperties : function($container) {
            return {
                ceilingEntity : $container.find('.translation-ceiling').text(),
                entityId      : $container.find('.translation-id').text(),
                propertyName  : $container.find('.translation-property').text(),
                isRte         : $container.find('.translation-rte').text()
            };
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    $('body').on('click', 'a.show-translations', function() {
        if ($(this).data('disabled') != 'disabled') {
            BLCAdmin.showLinkAsModal($(this).attr('href'));
        }
    	return false;
    });
    
    $('body').on('click', 'button.translation-submit-button', function() {
	    var $form = $(this).closest('.modal').find('.modal-body form');
		
		BLC.ajax({
			url: $form.attr('action'),
			type: "POST",
			data: $form.serialize()
		}, function(data) {
			//prevenSubmit is a hidden field whose value is sent from the translations controller, when there are errors
			//"data" contains the new copy of the form, validated 
			var preventSubmit = $(data).find(".modal-body").find("input[name=preventSubmit]").attr("value");
			if (!preventSubmit){
              BLCAdmin.listGrid.replaceRelatedListGrid($(data));
              BLCAdmin.hideCurrentModal();
			}else{
				var errorMapString = $(data).find(".modal-body").find("input[name=jsErrorMapString]").attr("value");
				var errorMap = JSON.parse(errorMapString);
				//clear all previous error spans
				$form.find("span.error").remove();
				errorMap.forEach(function(item){
					for (var name in item){
						var value = item[name];
						//"name" and "value" are the field name and the internationalized error message, respectively
						//now, build the jQuery search string to pinpoint the field's box, and add the error message right after the label
						var searchString = ".field-box[id=field-" + name + "]";
						$form.find(searchString).find(".field-label").append("<span class='error'>" + value + "</span>");
					}
				});
			}
	    });
		
		return false;
    });
    
    $('body').on('click', 'button.translation-grid-add', function() {
        var $container = $(this).closest('.listgrid-container');
        var baseUrl = $container.find('.listgrid-header-wrapper table').data('currenturl');
        var properties = BLCAdmin.translations.getProperties($container);
        
        BLCAdmin.showLinkAsModal(baseUrl + '/add?' + $.param(properties));
        return false;
    });
    
    $('body').on('click', 'button.translation-grid-update', function() {
        var $container = $(this).closest('.listgrid-container');
        var $selectedRows = $container.find('table tr.selected');
        var baseUrl = $container.find('.listgrid-header-wrapper table').data('currenturl');
        var rowFields = BLCAdmin.listGrid.getRowFields($selectedRows);
        var properties = BLCAdmin.translations.getProperties($container);
        
        properties.localeCode = rowFields.localeCode;
        properties.translationId = rowFields.id;
        
        BLCAdmin.showLinkAsModal(baseUrl + '/update?' + $.param(properties));
        return false;
    });
    
    $('body').on('click', 'button.translation-grid-remove', function() {
        var $container = $(this).closest('.listgrid-container');
        var $selectedRows = $container.find('table tr.selected');
        var baseUrl = $container.find('.listgrid-header-wrapper table').data('currenturl');
        var rowFields = BLCAdmin.listGrid.getRowFields($selectedRows);
        var properties = BLCAdmin.translations.getProperties($container);
        
        properties.translationId = rowFields.id;
        
        BLC.ajax({
            url: baseUrl + '/delete',
            data: properties,
            type: "POST"
        }, function(data) {
            BLCAdmin.listGrid.replaceRelatedListGrid($(data));
        });
        
        return false;
    });

});
