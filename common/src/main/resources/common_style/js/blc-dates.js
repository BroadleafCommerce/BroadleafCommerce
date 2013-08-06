(function($, BLC) {
    
    var defaultFormats = {
        blcDateFormat : "yy.mm.dd",
        blcTimeFormat : "HH:mm:ss",
        displayDateFormat : 'mm/dd/yy',
        displayTimeFormat : 'HH:mm'
    }
        
    // Add utility functions for dates to the BLCAdmin object
    BLC.dates = {
        
        /**
         * serverDate should be in the Broadleaf datetime format, "yyyy.MM.dd HH:mm:ss" (Java spec)
         * returns the display format, "mm/dd/yy HH:mm" (JavaScript spec)
         */
        getDisplayDate : function(serverDate, formats) {
            if (serverDate) {
                formats = $.extend({}, defaultFormats, formats);
                
                // We have to send the blcTimeFormat twice in this method due to how the library works
                var result = $.datepicker.parseDateTime(formats.blcDateFormat, formats.blcTimeFormat, serverDate, {}, {
                    timeFormat : formats.blcTimeFormat
                });
                
                // Pull the appropriate parts from the result and format them
                if (result != null) {
                    var displayDate = $.datepicker.formatDate(formats.displayDateFormat, result);
                    var displayTime = $.datepicker.formatTime(formats.displayTimeFormat, {
                        hour : result.getHours(),
                        minute : result.getMinutes()
                    });
                    
                    return {
                        displayDate: displayDate,
                        displayTime: displayTime
                    };
                }
            }
            
            return null;
        },
        
        /**
         * displayDate should be in the format "mm/dd/yy HH:mm" (JavaScript spec)
         * returns the server-expected format, "yyyy.MM.dd HH:mm:ss Z" (Java spec)
         */
        getServerDate : function(displayDate, formats) {
            if (displayDate) {
                formats = $.extend({}, defaultFormats, formats);
                
                // First, let's parse the display date into a date object
                var result = $.datepicker.parseDateTime(formats.displayDateFormat, formats.displayTimeFormat, displayDate, {}, {
                    timeFormat : formats.displayTimeFormat
                });
                
                // Now, let's convert it to the server format
                var serverDate = $.datepicker.formatDate(formats.blcDateFormat, result);
                
                var serverTime = $.datepicker.formatTime(formats.blcTimeFormat, {
                    hour : result.getHours(),
                    minute : result.getMinutes(),
                });
                
                return {
                    serverDate: serverDate,
                    serverTime: serverTime
                };
            }
            
            return null;
        }
    };
            
})(jQuery, BLC);