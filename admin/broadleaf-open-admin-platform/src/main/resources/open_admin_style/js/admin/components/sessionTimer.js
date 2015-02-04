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
    var activityPingInterval = 30000;
    var pingInterval = 1000;
    var defaultSessionTime = 0;
    var EXPIRE_MESSAGE_TIME = 60000;

    BLCAdmin.sessionTimer = {

        initTimer : function() {
            this.resetTimer();

        },

        resetTimer : function() {
            BLC.get({
                url : BLC.servletContext + "/sessionTimerInactiveInterval"
            }, function(data) {
                sessionTimeLeft = data.maxInterval * 2 / 3;
                defaultSessionTime = sessionTimeLeft;
                $.cookie("sessionResetTime", data.resetTime);
            });
        },

        getTimeLeft : function() {
            return sessionTimeLeft;
        },

        getTimeLeftSeconds : function() {
            return sessionTimeLeft / 1000;
        },

        isExpired : function() {
            return sessionTimeLeft <= 0;
        },

        getDefaultSessionTime : function() {
            return defaultSessionTime;
        },

        decrement : function(val) {
            sessionTimeLeft -= val;
        },

        getActivityPingInterval : function() {
            return activityPingInterval;
        },

        getPingInterval : function() {
            return pingInterval;
        },

        getExpireMessageTime : function() {
            return EXPIRE_MESSAGE_TIME;
        },

        timeSinceLastReset : function() {
            return (new Date()).getTime() - $.cookie("sessionResetTime");
        },

        verifyAndUpdateTimeLeft : function() {
            var exactTimeLeft = (this.getDefaultSessionTime() - this
                    .timeSinceLastReset());
            exactTimeLeft = exactTimeLeft - (exactTimeLeft % pingInterval);

            if (exactTimeLeft > sessionTimeLeft) {
                sessionTimeLeft = exactTimeLeft;
                return true;
            }
            return false;
        },

        invalidateSession : function() {
            BLC.get({
                url : BLC.servletContext + "/adminLogout.htm"
            }, function(data) {
                window.location.replace(BLC.servletContext
                        + "/login?sessionTimeout=true");
            });
        }

    };
})(jQuery, BLCAdmin);

$(document)
        .ready(
                function() {
                    var activityCount = 0;
                    $(document).keypress(function(e) {
                        activityCount++;
                    });

                    var updateTimer = function() {

                        BLCAdmin.sessionTimer.decrement(BLCAdmin.sessionTimer
                                .getPingInterval());

                        if (BLCAdmin.sessionTimer.verifyAndUpdateTimeLeft()) {
                            $("#lightbox").fadeOut("slow");
                            return true;
                        }

                        if (BLCAdmin.sessionTimer.getTimeLeft() < BLCAdmin.sessionTimer
                                .getExpireMessageTime()) {

                            if (BLCAdmin.sessionTimer.isExpired()) {
                                $("#lightbox").fadeOut("slow");
                                BLCAdmin.sessionTimer.invalidateSession();
                                return false;
                            }

                            $("#expire-text")
                                    .html(
                                            BLCAdmin.messages.sessionCountdown
                                                    + BLCAdmin.sessionTimer
                                                            .getTimeLeftSeconds()
                                                    + BLCAdmin.messages.sessionCountdownEnd);

                            $("#lightbox").fadeIn("slow");
                            activityCount = 0;

                            return true;

                        } else if (BLCAdmin.sessionTimer.getTimeLeft()
                                % BLCAdmin.sessionTimer
                                        .getActivityPingInterval() == 0) {
                            if (activityCount > 0) {
                                BLCAdmin.sessionTimer.resetTimer();
                                activityCount = 0;
                                return true;
                            }

                        }

                        return true;
                    };

                    stayLoggedIn = function() {
                        $.doTimeout('update');
                        $("#lightbox").fadeOut("slow");
                        activityCount = 0;
                        BLCAdmin.sessionTimer.resetTimer();
                        $.doTimeout('update', BLCAdmin.sessionTimer
                                .getPingInterval(), updateTimer);
                    }

                    $("#stay-logged-in").click(function() {
                        stayLoggedIn();
                        return false;
                    });

                    BLCAdmin.sessionTimer.resetTimer();
                    $.doTimeout('update', BLCAdmin.sessionTimer
                            .getPingInterval(), updateTimer);

                });