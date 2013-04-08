(function($, BLCAdmin) {
    
    // Add utility functions for dates to the BLCAdmin object
    BLCAdmin.dates = {
        blcDateFormat : "yy.mm.dd",
        blcTimeFormat : "HH:mm:ss z",
        displayDateFormat : 'mm/dd/yy',
        displayTimeFormat : 'HH:mm',
        
        /**
         * This function should be called for any element that wants to be a rulebuilder
         */
        onLive : function($element) {
            // Set the value of this datepicker to be the appropriately formatted one
            $element.val(this.getDisplayDate($element.val()));
          
            // Make it a date-time picker
            $element.datetimepicker();
        },
        
        /**
         * serverDate should be in the Broadleaf datetime format, "yyyy.MM.dd HH:mm:ss Z" (Java spec)
         * returns the display format, "mm/dd/yy HH:mm" (JavaScript spec)
         */
        getDisplayDate : function(serverDate) {
            if (serverDate) {
                // We have to send the blcTimeFormat twice in this method due to how the library works
                var result = $.datepicker.parseDateTime(this.blcDateFormat, this.blcTimeFormat, serverDate, {}, {
                    timeFormat : this.blcTimeFormat
                });
                
                // Pull the appropriate parts from the result and format them
                var displayDate = $.datepicker.formatDate(this.displayDateFormat, result);
                var displayTime = $.datepicker.formatTime(this.displayTimeFormat, {
                    hour : result.getHours(),
                    minute : result.getMinutes()
                });
                
                return displayDate + " " + displayTime;
            }
            
            return null;
        },
        
        /**
         * displayDate should be in the format "mm/dd/yy HH:mm" (JavaScript spec)
         * returns the server-expected format, "yyyy.MM.dd HH:mm:ss Z" (Java spec)
         */
        getServerDate : function(displayDate) {
            if (displayDate) {
                // First, let's parse the display date into a date object
                var result = $.datepicker.parseDateTime(this.displayDateFormat, this.displayTimeFormat, displayDate, {}, {
                    timeFormat : this.displayTimeFormat
                });
                
                // Next, we'll parse the timezone ourselves
                var timezone = result.toString();
                timezone = timezone.substring(timezone.indexOf('GMT') + 3);
                timezone = timezone.substring(0, timezone.indexOf(' '));
                
                // Now, let's convert it to the server format
                var serverDate = $.datepicker.formatDate(this.blcDateFormat, result);
                
                
                var serverTime = $.datepicker.formatTime(this.blcTimeFormat, {
                    hour : result.getHours(),
                    minute : result.getMinutes(),
                    timezone : timezone
                });
                
                return serverDate + " " + serverTime;
            }
            
            return null;
        }
    };
    
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
  $('.datepicker').each(function(index, element) {
      BLCAdmin.dates.onLive($(element));
  });
  
  $('body').on('click', 'div.datepicker-container', function(event) {
      $(this).find('input').datepicker('show');
  });
  
  $('body').on('submit', 'form', function(event) {
      $(this).find('.datepicker').each(function(index, element) {
          var $hiddenClone = $('<input>', {
              type: 'hidden',
              name: $(this).attr('name'),
              value: BLCAdmin.dates.getServerDate($(this).val())
          });
          
          $(this).removeAttr('name').after($hiddenClone);
      });
  });
    
});