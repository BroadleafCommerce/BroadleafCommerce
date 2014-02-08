/*
 * #%L
 * BroadleafCommerce Common Libraries
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
                    second : result.getSeconds()
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
