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
    var activityCount = 0;

    BLCAdmin.sessionTimer = {

        initTimer : function() {
            this.resetTimer();
            
            $(document).keypress(function(e) {
                activityCount++;
            });
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
                $("#session-minute").fadeOut("slow");
                return true;
            }
            return false;
        },
        
        updateTimer : function() {
            this.decrement(this.getPingInterval());

            if (this.activityCount > 0) {
                this.resetTimer();
                this.activityCount = 0;
                return true;
            }

//            if (BLCAdmin.sessionTimer.verifyAndUpdateTimeLeft()) {
//                return true;
//            }
            
            if (this.getTimeLeft() <= 60000) {
                // session time less than one minute
                $.doTimeOut(1000, this.updateCountdown);
                return false;
            }

            return true;
        },
        
        updateCountdown : function () {
            this.decrement(1000);
            
            if(this.getTimeLeft() <= 0){
                console.log("session expired");
            }
            console.log(this.getTimeLeft());
            return true;
        }

        
    };
})(jQuery, BLCAdmin);

$(document).ready(
        function() {

            $.doTimeout(BLCAdmin.sessionTimer.getPingInterval(), BLCAdmin.sessionTimer.updateTimer);

        });