(function($, BLCAdmin) {
    
    // Add utility functions for list grids to the BLCAdmin object
    BLCAdmin.listGrid = {
        replaceRelatedListGrid : function($headerWrapper, alert) {
            var $table = $headerWrapper.find('table');
            var tableId = $table.attr('id');
            var $oldTable = $('#' + tableId);
            
            var currentIndex = BLCAdmin.listGrid.paginate.getTopVisibleIndex($oldTable.find('tbody'));
            
            var $oldBodyWrapper = $oldTable.closest('.listgrid-body-wrapper');
            var $oldHeaderWrapper = $oldBodyWrapper.prev();
            
            $oldHeaderWrapper.find('thead').after($table.find('tbody'));
            $oldBodyWrapper.remove();
            
            var $listGridContainer = $oldHeaderWrapper.closest('.listgrid-container');
            $listGridContainer.find('.listgrid-table-footer').text('');
            
            this.initialize($listGridContainer);
            
            BLCAdmin.listGrid.paginate.scrollToIndex($listGridContainer.find('tbody'), currentIndex);
            //$listGridContainer.find('.listgrid-body-wrapper').mCustomScrollbar('update');
            
            if (alert) {
                this.showAlert($listGridContainer, alert.message, alert);
            }
        },
        
        getButtonLink : function($button) {
            var $container = $button.closest('.listgrid-container');
            var $selectedRows = $container.find('table tr.selected');
            var link = $selectedRows.attr('data-link');
            
            if ($button.attr('data-urlpostfix')) {
                link += $button.attr('data-urlpostfix');
            }
            
            return link;
        },
        
        getRowFields : function($tr) {
            var fields = {};
            
            $tr.find('td').each(function() {
                var fieldName = $(this).data('fieldname');
                var value = $(this).data('fieldvalue');
                fields[fieldName] = value;
            });
            
            return fields;
        },
        
        updateToolbarRowActionButtons : function($listGridContainer) {
            var hasSelected = $listGridContainer.find('tr.selected').length > 0;
            if (hasSelected) {
                $listGridContainer.find('button.row-action').removeAttr('disabled');
            } else {
                $listGridContainer.find('button.row-action').attr('disabled', 'disabled');
            }
        },
        
        updateNecessaryOverflowTooltips : function($container) {
            var $tds = $container.find('td');
            $tds.each(function(index, element) {
                var $element = $(element);
                if ($element.isOverflowed()) {
                    $element.addClass('has-tip tip-top');
                    $element.attr('title', $element.data('fieldvalue'));
                }
            });
        },
        
        showAlert : function($container, message, options) {
            options = options || {};
    	    var alertType = options.alertType || '';
    	    
    	    var $alert = $('<div>').addClass('alert-box list-grid-alert').addClass(alertType);
    	    var $closeLink = $('<a>').attr('href', '').addClass('close').html('&times;');
    	    
    	    $alert.append(message);
    	    $alert.append($closeLink);
    	    
    	    if (options.clearOtherAlerts) {
    	        $container.children('.list-grid-alert').find('a.close').click();
    	    }
    	    
    	    $container.children().first().after($alert);
    	    
    	    if (options.autoClose) {
    	        setTimeout(function() {
    	            $closeLink.click();
    	        }, options.autoClose);
    	    }
        },
        
        fixHelper : function(e, ui) {
            ui.closest('tbody').find('tr').children().each(function() {
                $(this).width($(this).width());
            });
            return ui;
        },
        
        showLoadingSpinner : function($tbody, spinnerOffset) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            
            if (spinnerOffset) {
                $spinner.css('position', 'absolute').css('top', spinnerOffset + 'px');
            }
            
            $spinner.parent().css('display', 'block');
        },
        
        hideLoadingSpinner : function($tbody) {
            var $spinner = $tbody.closest('.listgrid-container').find('i.listgrid-table-spinner');
            $spinner.parent().css('display', 'none');
        },
        
        initialize : function($container) {
            this.updateToolbarRowActionButtons($container);
            
            if (BLCAdmin.listGrid.paginate) {
                BLCAdmin.listGrid.paginate.initialize($container);
            }
            
            this.updateNecessaryOverflowTooltips($container);
        }
    };
    
    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.listgrid-container').each(function(index, element) {
            BLCAdmin.listGrid.initialize($(element));
        });
    });
    
    
})(jQuery, BLCAdmin);

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
        var fields = BLCAdmin.listGrid.getRowFields($tr);
        
        if ($tr.find('td.list-grid-no-results').length == 0 && !$table.hasClass('reordering')) {
            $('body').trigger('listGrid-' + listGridType + '-rowSelected', [link, fields, currentUrl]);
        }
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
        var currentlySelected = $tr.hasClass('selected');
        var $listGridContainer = $tr.closest('.listgrid-container');
        
        $tr.closest('tbody').find('tr').removeClass('selected');
        
        if (!currentlySelected) {
            $tr.addClass("selected");
        }
        
        BLCAdmin.listGrid.updateToolbarRowActionButtons($listGridContainer);
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
        
        BLC.ajax({
            url : currentUrl,
            type : "POST",
            data : postData
        }, function(data) {
            BLCAdmin.listGrid.replaceRelatedListGrid($($(data.trim())[0]), { message: 'Saved!', alertType: 'save-alert', autoClose: 1000 });
            BLCAdmin.hideCurrentModal();
        })
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
            //ensure that the clear button shows up after selecting a value
            $this.find('button.clear-foreign-key').show();
            
            // To-one fields potentially trigger a dynamicform. We test to see if this field should
            // trigger a form, and bind the necessary event if it should.
            var onChangeTrigger = $valueField.data('onchangetrigger');
            if (onChangeTrigger) {
                var trigger = onChangeTrigger.split("-");
                if (trigger[0] == 'dynamicForm') {
                    var $fieldSet = $("fieldset[data-dynamicpropertyname='" + trigger[1] + "']");
                    var url = $fieldSet.data('currenturl') + '?propertyTypeId=' + fields['id'];
                    
                    BLC.ajax({
                        url : url,
                        type : "GET"
                    }, function(data) {
                        var dynamicPropertyName = $(data.trim()).find('fieldset').data('dynamicpropertyname');
                        var $oldFieldset = $('fieldset[data-dynamicpropertyname="' + dynamicPropertyName + '"]');
                        var $newFieldset = $(data.trim()).find('fieldset');
                        
                        BLCAdmin.initializeFields($newFieldset);
                        
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
    
    $('body').on('click', 'button.sub-list-grid-add', function() {
        BLCAdmin.showLinkAsModal($(this).attr('data-actionurl'));
        return false;
    });
    
    $('body').on('click', 'button.sub-list-grid-reorder', function() {
        var $container = $(this).closest('.listgrid-container');
        var $table = $container.find('table');
        var $tbody = $table.find('tbody');
        var $trs = $tbody.find('tr');
        var doneReordering = $table.hasClass('reordering');
        
        $table.toggleClass('reordering');
        
        if (doneReordering) {
            $container.find('.listgrid-toolbar button').removeAttr('disabled');
            $(this).html('Reorder');
            
            BLCAdmin.listGrid.updateToolbarRowActionButtons($container);
            
            $trs.removeClass('draggable').addClass('clickable');
            $tbody.sortable("destroy");
        } else {
            $container.find('.listgrid-toolbar button').attr('disabled', 'disabled');
            $(this).removeAttr('disabled').html('Done');
            
            $trs.removeClass('clickable').addClass('draggable');
            
            $tbody.sortable({
                helper : BLCAdmin.listGrid.fixHelper,
                update : function(event, ui) {
                    BLC.ajax({
                        url : ui.item.data('link') + '/sequence',
                        type : "POST",
                        data : {
                            newSequence : ui.item.index()
                        }
                    }, function(data) {
                        var $container = $('div.listgrid-container#' + data.field);
                        BLCAdmin.listGrid.showAlert($container, 'Saved!', { alertType: 'save-alert', autoClose: 400 });
                        console.log(data);
                    });
                }
            }).disableSelection();
        }
        
        return false;
    });
    
    $('body').on('click', 'button.sub-list-grid-remove', function() {
        var link = BLCAdmin.listGrid.getButtonLink($(this));
        
        var $container = $(this).closest('.listgrid-container');
        var $selectedRows = $container.find('table tr.selected');
        var rowFields = BLCAdmin.listGrid.getRowFields($selectedRows);
        
        BLC.ajax({
            url: link,
            data: rowFields,
            type: "POST"
        }, function(data) {
            BLCAdmin.listGrid.replaceRelatedListGrid($($(data.trim())[0]), { message: 'Saved!', alertType: 'save-alert', autoClose: 1000 });
        });
        
        return false;
    });
    
    $('body').on('click', 'button.sub-list-grid-update', function() {
        var link = BLCAdmin.listGrid.getButtonLink($(this));
        
        BLCAdmin.showLinkAsModal(link);
        
        return false;
    });
    
    $('body').on('submit', 'form.modal-form', function(event) {
        BLCAdmin.runSubmitHandlers($(this));
        
        BLC.ajax({
            url: this.action,
            type: "POST",
            data: $(this).serialize()
        }, function(data) {
            BLCAdmin.listGrid.replaceRelatedListGrid($($(data.trim())[0]), { message: 'Saved!', alertType: 'save-alert', autoClose: 1000 });
            BLCAdmin.hideCurrentModal();
        });
        return false;
    });
        
    /**
     * Clears out a previously-selected foreign key on both a form and listgrid criteria
     */
    $('body').on('click', 'button.clear-foreign-key', function(event) {        
        //remove the current display value
        $(this).prev().html($(this).prev().prev().html());
        
        //remove the criteria input val
        $(this).closest('.additional-foreign-key-container').find('.value').val('');
        $(this).toggle();
        
        //don't follow the link; prevents page jumping
        return false;
    });
    
    $('body').on('mouseover', 'td.row-action-selector', function(event) {
        $(this).find('ul.row-actions').show();
    });
    
    $('body').on('mouseout', 'td.row-action-selector', function(event) {
        $(this).find('ul.row-actions').hide();
    });
    
});

