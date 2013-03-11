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
    $('body').on('click', 'a.show-to-one-list-grid', function(event) {
    	var $container = $(this).closest('div.additional-foreign-key-container');
    	
    	$container.on('valueSelected', function(event, fields) {
    		var $this = $(this);
    		var displayValueProp = $this.find('input.display-value-property').val();
    		
    		$this.find('input.value').val(fields['id']);
    		$this.find('input.display-value').val(fields[displayValueProp]);
    		
			BLCAdmin.currentModal().modal('hide');
    	});
    	
    	BLCAdmin.showLinkAsModal($(this).attr('href'), function() {
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
			replaceListGrid(data);
	    });
		return false;
	});
	
	var getRowFields = function($tr) {
		var fields = {};
		
		$tr.find('td').each(function() {
			var fieldName = $(this).data('fieldname');
			var value = $(this).text();
			fields[fieldName] = value;
		});
		
		return fields;
	}
	
	var replaceListGrid = function(data) {
		var $table = $(data);
		var tableId = $table.attr('id');
		$('#' + tableId).replaceWith($table);
    	BLCAdmin.currentModal().modal('hide');
	}
	
});

