/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
         * returns the display format, "mm/dd/yy HH:mm:ss" (JavaScript spec)
         */
        getDisplayDate : function(serverDate, formats) {
            try {
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
                            minute : result.getMinutes(),
                            second : result.getSeconds()
                        });
                        
                        return {
                            displayDate: displayDate,
                            displayTime: displayTime
                        };
                    }
                }
            } catch (e) {
                // error from formatting or parsing the date
                console.log(JSON.stringify(e))
                return serverDate;
            }
            
            return null;
        },
        
        /**
         * displayDate should be in the format "mm/dd/yy HH:mm" (JavaScript spec)
         * returns the server-expected format, "yyyy.MM.dd HH:mm:ss Z" (Java spec)
         */
        getServerDate : function(displayDate, formats) {
            try {
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
                        second : result.getSeconds()
                    });
                    
                    return {
                        serverDate: serverDate,
                        serverTime: serverTime
                    };
                }
            } catch (e) {
                // We have an error formatting or parsing the date
                console.log(JSON.stringify(e))
                return displayDate;
                
            }
            
            return null;
        }
    };
            
})(jQuery, BLC);
