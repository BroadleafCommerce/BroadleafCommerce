$(document).ready(function() {
	
	/**
	 * Bind a handler to trigger anytime a table row is clicked on any list grid. 
	 * 
	 * After assembling information, this will delegate to the specialized rowSelected
	 * handler for this particular kind of list grid.
	 */
	$('body').on('click', '.list-grid-table tbody tr', function() {
		var $tr = $(this);
		var $table = $tr.closest('table');
		var link = $tr.data('link');
		var listGridType = $table.data('listgridtype');
		var currentUrl = $table.data('currenturl');
		var fields = getRowFields($tr);
		
		$('body').trigger('listGrid-' + listGridType + '-rowSelected', [link, fields, currentUrl]);
	});
	
	/**
	 * The rowSelected handler for the main list grid doesn't do anything by default
	 */
	$('body').on('listGrid-main-rowSelected', function(event, link, fields, currentUrl) {
	});
	
	/**
	 * The rowSelected handler for the inline list grid ...
	 */
	$('body').on('listGrid-inline-rowSelected', function(event, link, fields, currentUrl) {
		var $tr = $('tr[data-link="' + link + '"]');
		$tr.toggleClass("selected");
	});
	
	/**
	 * The rowSelected handler for a toOne list grid needs to trigger the specific valueSelected handler 
	 * for the field that we are performing the to-one lookup on.
	 */
	$('body').on('listGrid-to_one-rowSelected', function(event, link, fields, currentUrl) {
		$('div.additional-foreign-key-container').trigger('valueSelected', fields);
	});
	
	/**
	 * The rowSelected handler for a simpleCollection list grid ...
	 */
	$('body').on('listGrid-basic-rowSelected', function(event, link, fields, currentUrl) {
		var postData = {};
		
		for (var key in fields){
		    if (fields.hasOwnProperty(key)){
		    	postData["fields['" + key + "'].value"] = fields[key];
		    }
		}	
		
		$.post(currentUrl, postData, function(data) {
			replaceListGrid(data);
		})
		.fail(function(data) {
			alert('failed ' + data);
		});
	});
	
	/**
	 * The rowSelected handler for an adornedTarget list grid. This is specific to adorned target
	 * lists that do not have any additional maintained fields. In this case, we can simply
	 * submit the form directly.
	 */
	$('body').on('listGrid-adorned-rowSelected', function(event, link, fields, currentUrl) {
		$(this).find('input#adornedTargetIdProperty').val(fields['id']);
		var $modal = BLCAdmin.currentModal();
		$modal.find('form.modal-form').submit();
	});
	
	/**
	 * The rowSelected handler for an adornedTargetWithForm list grid. Once the user selects an entity,
	 * show the form with the additional maintained fields.
	 */
	$('body').on('listGrid-adorned_with_form-rowSelected', function(event, link, fields, currentUrl) {
		$(this).find('input#adornedTargetIdProperty').val(fields['id']);
		$('a#adornedModalTab2Link').click();
	});
	
	/**
	 * This handler will fire for additional foreign key fields when the find button is clicked.
	 * 
	 * It is responsible for binding a valueSelected handler for this field as well as launching
	 * a list grid modal that will be used to select the to-one entity.
	 * 
	 * Note that we MUST unbind this handler when the modal is hidden as there could be many different
	 * to-one fields on an entity form.
	 */
    $('body').on('click', '.to-one-lookup', function(event) {
    	var $container = $(this).closest('div.additional-foreign-key-container');
    	
    	$container.on('valueSelected', function(event, fields) {
    		var $this = $(this);
    		var displayValueProp = $this.find('input.display-value-property').val();
    		
    		var $valueField = $this.find('input.value');
    		$valueField.val(fields['id']);
    		$this.find('span.display-value').html(fields[displayValueProp]);
    		
    		// To-one fields potentially trigger a dynamicform. We test to see if this field should
    		// trigger a form, and bind the necessary event if it should.
    		var onChangeTrigger = $valueField.data('onchangetrigger');
    		if (onChangeTrigger) {
    			var trigger = onChangeTrigger.split("-");
    			if (trigger[0] == 'dynamicForm') {
	    			var $fieldSet = $("fieldset[data-dynamicpropertyname='" + trigger[1] + "']");
	    			var url = $fieldSet.data('currenturl') + '?propertyTypeId=' + fields['id'];
	    			
					$.get(url, function(data) {
						var dynamicPropertyName = $(data.trim()).find('fieldset').data('dynamicpropertyname');
						var $oldFieldset = $('fieldset[data-dynamicpropertyname="' + dynamicPropertyName + '"]');
						var $newFieldset = $(data.trim()).find('fieldset');
						$newFieldset.find('.redactor').redactor();
						$oldFieldset.replaceWith($newFieldset);
					});
    			}
    		}
    		
			BLCAdmin.hideCurrentModal();
    	});
    	
    	BLCAdmin.showLinkAsModal($(this).data('select-url'), function() {
			$('div.additional-foreign-key-container').unbind('valueSelected');
    	});
    	
		return false;
    });
    
	$('body').on('click', 'a.sub-list-grid-add', function() {
    	BLCAdmin.showLinkAsModal($(this).attr('href'));
		return false;
	});
	
	$('body').on('click', 'a.sub-list-grid-remove', function() {
		var $container = $(this).closest('.listgrid-container');
		var $selectedRows = $container.find('table tr.selected');
		
		var link = $selectedRows.attr('data-link') + '/delete';
		
		$.ajax({
			url: link,
			data: getRowFields($selectedRows),
			type: "POST"
		}).done(function(data) {
			replaceListGrid(data);
		});
		
		return false;
	});
	
	$('body').on('click', 'a.sub-list-grid-update', function() {
		var $container = $(this).closest('.listgrid-container');
		var $selectedRows = $container.find('table tr.selected');
		
		var link = $selectedRows.attr('data-link');
    	BLCAdmin.showLinkAsModal(link);
    	
		return false;
	});
	
	$('body').on('submit', 'form.modal-form', function(event) {
		BLC.ajax({
			url: this.action,
			type: "POST",
			data: $(this).serialize()
		}, function(data) {
			replaceRelatedListGrid(data);
	    });
		return false;
	});
	
	$('body').on('click', 'a.sort', function() {
		//reset any of the currently active sorts on all the fields in the grid
		$(this).closest('thead').find('i.sort-icon').removeClass('listgrid-icon-down').removeClass('listgrid-icon-up');
		$(this).closest('thead').find('input#sort').val('');
		
		//apply the sort to the current field
		var ascending = $(this).hasClass('down');
		var sortValue = (ascending) ? 'ASCENDING' : 'DESCENDING';
		$(this).parents('ul').find('input#sort').val(sortValue);
	    //update the header icon for this field
		var icon = $(this).parents('.listgrid-headerBtn').find('div i');
		icon.toggleClass('listgrid-icon-down', ascending);
		icon.toggleClass('listgrid-icon-up', !ascending);

		//submit the form just for this particular field since this is the only sort that changed
		$(this).closest('ul').find('form.filter-form').submit();
		return false;
	});

	
	$('body').on('submit', 'form.filter-form', function(event) {
		//Serialize all of the filter-forms in this particular list grid since it's possible that criteria could be set
		//for multiple fields at the same time
		var toReplace = $(this).closest('.list-grid-table').find('tbody');
		$(this).closest('ul').removeClass('show-dropdown');
		BLC.ajax({
			url: this.action,
			type: "GET",
			data: $(this).closest('thead').find('form.filter-form').serialize()
		}, function(data) {
			updateListGrid(data, toReplace);
	    });
		return false;
	});
	
	/**
	 * Intercepts the form submission for a top-level entity search. This search is only available on a main entity page
	 * (like Products)
	 */
	$('body').on('submit', 'form.custom-entity-search', function(event) {
		$('body').find('.custom-entity-search a').click();
		return false;
	});
	
	/**
	 * Intercepts the button click for the main entity search. This will look at the first field in the main list grid (of
	 * which there is only 1 on the page) and replace the criteria value for that field with whatever was typed into the
	 * search box.
	 */
	$('body').on('click', '.custom-entity-search a', function(event) {
		//this takes place on the main list grid screen so there should be a single list grid
		var search = $('body').find('input').val();
		var $firstHeader = $('body').find('.list-grid-table th.th1');
		$firstHeader.find('input.listgrid-criteria-input').val(search);
		BLC.ajax({
			url: $(this).closest('form').action,
			type: "GET",
			data: $firstHeader.find('div.filter-fields :input').serialize()
		}, function(data) {
			updateListGrid(data, $('body').find('.list-grid-table tbody'));
	    });
		return false;
	});
	
	var getRowFields = function($tr) {
		var fields = {};
		
		$tr.find('td').each(function() {
			var fieldName = $(this).data('fieldname');
			var value = $(this).data('fieldvalue');
			fields[fieldName] = value;
		});
		
		return fields;
	}
	
	var updateListGrid = function(data, tableToReplace) {
		tableToReplace.replaceWith($(data).find('tbody'))
	}
	
	var replaceRelatedListGrid = function(data) {
		var $table = $(data);
		var tableId = $table.attr('id');
		$('#' + tableId + ' > tbody').replaceWith($table.find('tbody'));
    	BLCAdmin.hideCurrentModal();
	}
	
});

