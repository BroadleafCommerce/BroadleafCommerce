/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * @author Nick Crum
 */

(function($, BLCAdmin) {

    var sessionTimeLeft = 0;
    var pingInterval = 30000;
    var defaultSessionTime = 0;
    

    BLCAdmin.sessionTimer = {

        initTimer : function() {
            this.resetTimer();
            
            
        },

        resetTimer : function() {
            BLC.get({
                url : "/admin/sessionTimerInactiveInterval"
            }, function(data) {
                sessionTimeLeft = data.maxInterval*2/3;
                defaultSessionTime = sessionTimeLeft;
                $.cookie("sessionResetTime", data.resetTime);
            });
        },

        getTimeLeft : function() {
            return sessionTimeLeft;
        },

        getDefaultSessionTime : function() {
            return defaultSessionTime;
        },

        decrement : function(val) {
            sessionTimeLeft -= val;
        },

        getPingInterval : function() {
            return pingInterval;
        },

        timeSinceLastReset : function() {
            return (new Date()).getTime() - $.cookie("sessionResetTime");
        },

        verifyAndUpdateTimeLeft : function() {
            var exactTimeLeft = (this.getDefaultSessionTime() - this
                    .timeSinceLastReset());
            exactTimeLeft = Math.round(exactTimeLeft / 30000) * 30000;

            if (exactTimeLeft > sessionTimeLeft) {
                sessionTimeLeft = exactTimeLeft;
                return true;
            }
            return false;
        },
        
        invalidateSession : function() {
            BLC.get({
                url : "/admin/adminLogout.htm"
            }, function(data) {
                window.location.replace("/admin?sessionTimeout=true");
            });
        }
        
    };
})(jQuery, BLCAdmin);

$(document).ready(
        function() {
            var activityCount = 0;
            $(document).keypress(function(e) {
                activityCount++;
            });
            
            var updateTimer = function() {
                BLCAdmin.sessionTimer.decrement(1000);
                console.log("" + BLCAdmin.sessionTimer.getTimeLeft());
                if (BLCAdmin.sessionTimer.getTimeLeft() <= 60000) {
                    // session time less than one minute
                    if (BLCAdmin.sessionTimer.getTimeLeft() <= 0){
                        $("#lightbox").fadeOut("slow");
                        BLCAdmin.sessionTimer.invalidateSession();
                        return false;
                    }
                    $("#expire-text").html("Your session expires in <span>" + BLCAdmin.sessionTimer.getTimeLeft()/1000 + "</span> seconds");
                    $("#lightbox").fadeIn("slow");
                    activityCount = 0;
                    return true;
                } else if (BLCAdmin.sessionTimer.getTimeLeft() % BLCAdmin.sessionTimer.getPingInterval() == 0) {
                    if (activityCount > 0) {
                        BLCAdmin.sessionTimer.resetTimer();
                        activityCount = 0;
                        return true;
                    }
                
                }
                
                return true;
            };
            
            stayLoggedIn = function () {
                $.doTimeout('update');
                $("#lightbox").fadeOut("slow");
                activityCount=0;
                BLCAdmin.sessionTimer.resetTimer();
                $.doTimeout('update',1000,updateTimer);
            }
            
            

            $.doTimeout('update',1000, updateTimer);

            
        });