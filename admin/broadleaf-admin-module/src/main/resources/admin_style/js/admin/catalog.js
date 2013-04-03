/**
 * Custom handlers for frontend catalog functions
 */
$(document).ready(function() {
    
    $('body').on('click', 'button.generate-skus', function() {
        var $container = $(this).closest('div.listgrid-container');
        
        $.get($(this).data('actionurl'), function(data) {
            var alertType = data.error ? 'alert' : '';
            
            BLCAdmin.listGrid.showAlert($container, data.message, {
                    alertType: alertType
                }
            );
            
            if (data.skusGenerated > 0) {
                refreshSkusGrid($container, data.listGridUrl);
            }
        });
        
        return false;
    });
    
    function refreshSkusGrid($container, listGridUrl) {
        $.get(listGridUrl, function(data) {
            $container.find('table.list-grid-table').replaceWith($(data.trim()));
            BLCAdmin.listGrid.updateToolbarRowActionButtons($container);
        });
    }

});
