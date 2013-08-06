(function($, BLCAdmin) {
    
    var adminFormats = {
        blcDateFormat : "yy.mm.dd",
        blcTimeFormat : "HH:mm:ss",
        displayDateFormat : 'mm/dd/yy',
        displayTimeFormat : 'HH:mm'
    };
    
    // Add utility functions for dates to the BLCAdmin object
    BLCAdmin.dates = {
        
        /**
         * This function should be called for any element that wants to be a rulebuilder
         */
        initialize : function($element) {
            // Set the value of this datepicker to be the appropriately formatted one
            $element.val(this.getDisplayDate($element.val()));
          
            // Make it a date-time picker
            $element.datetimepicker();
        },
        
        /**
         * serverDate should be in the Broadleaf datetime format, "yyyy.MM.dd HH:mm:ss" (Java spec)
         * returns the display format, "mm/dd/yy HH:mm" (JavaScript spec)
         */
        getDisplayDate : function(serverDate) {
            var display = BLC.dates.getDisplayDate(serverDate, adminFormats);
            return display == null ? null : display.serverDate + " " + display.displayTime;
        },
        
        /**
         * displayDate should be in the format "mm/dd/yy HH:mm" (JavaScript spec)
         * returns the server-expected format, "yyyy.MM.dd HH:mm:ss Z" (Java spec)
         */
        getServerDate : function(displayDate) {
            var server = BLC.dates.getServerDate(displayDate, adminFormats);
            return server == null ? null : server.serverDate + " " + server.serverTime;
        }
    };
    
    BLCAdmin.addInitializationHandler(function($container) {
        $container.find('.datepicker').each(function(index, element) {
            BLCAdmin.dates.initialize($(element));
        });
    });
    
    BLCAdmin.addPostValidationSubmitHandler(function($form) {
        $form.find('.datepicker').each(function(index, element) {
            var $hiddenClone = $('<input>', {
                type: 'hidden',
                name: $(this).attr('name'),
                value: BLCAdmin.dates.getServerDate($(this).val())
            });
          
            $(this).removeAttr('name').after($hiddenClone);
        });
    });
            
})(jQuery, BLCAdmin);

$(document).ready(function() {
  
    $('body').on('click', 'div.datepicker-container', function(event) {
        $(this).find('input').datepicker('show');
    });
    
});