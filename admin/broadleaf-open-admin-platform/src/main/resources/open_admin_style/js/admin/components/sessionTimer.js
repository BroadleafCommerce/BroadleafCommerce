/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
/**
 * @author Nick Crum
 */

(function($, BLCAdmin) {

    var pingInterval = 1000;
    var sessionTimeoutInterval = Number.MAX_VALUE; // the actual value for sessionTimeoutInterval get set by way of an ajax request
    var sessionTimeLeft = sessionTimeoutInterval;
    var EXPIRE_MESSAGE_TIME = 60000;
    
    var activityPingInterval = 30000;
    var activityCount = 0;
    
    /*
     * Here we define the path for the `sessionResetTime` cookie to be `/` if this is the root servlet context
     */
    var resetTimeCookiePath = (BLC.servletContext) ? BLC.servletContext : '/';
    
    /*
     * Here we define that key presses to indicate activity by incrementing the activityCount variable.
     */
    $(document).keypress(function(e) {
        activityCount++;
    });


    BLCAdmin.sessionTimer = {

        /*
         * This function is used to reset the session timer on the server and update the page's session time.
         */
        resetTimer : function() {
            /*
             * The session time is temporarily set to a high value to prevent a request that takes an inordinate
             * amount of time from causing the session to expire prematurely.
             */
            
            BLC.get({
                url : BLC.servletContext + "/sessionTimerReset",
                trackAnalytics : false,
                error: function(err) {
                    BLCAdmin.sessionTimer.invalidateSession();
                }
            }, function(data) {
                /*
                 * We deduct one minute from the actual session timeout interval to ensure that the server-side session
                 * doesn't expire before the client session
                 */
                sessionTimeoutInterval = data.serverSessionTimeoutInterval - 60000;
                resetTime = (new Date()).getTime();
                $.cookie("sessionResetTime", resetTime - (resetTime % pingInterval) , { path : resetTimeCookiePath });
                
                BLCAdmin.sessionTimer.updateTimeLeft();
            });
        },

        getTimeLeft : function() { 
            return sessionTimeLeft;
        },
        
        getTimeLeftSeconds : function(){
            return moment.duration(sessionTimeLeft/1000, 'seconds').format('m [min] s [sec]');
        },

        isExpired : function() {
            return sessionTimeLeft <= 0;
        },

        getSessionTimeoutInterval : function() {
            return sessionTimeoutInterval;
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
            return (new Date()).getTime() - $.cookie("sessionResetTime", { path: resetTimeCookiePath });
        },

        updateTimeLeft : function() {
            var exactTimeLeft = (BLCAdmin.sessionTimer.getSessionTimeoutInterval() - BLCAdmin.sessionTimer.timeSinceLastReset());
            exactTimeLeft = exactTimeLeft - (exactTimeLeft % pingInterval);

            sessionTimeLeft = exactTimeLeft;
        },

        invalidateSession : function() {
            $.doTimeout('update-admin-session');
            $.removeCookie('sessionResetTime', { path: resetTimeCookiePath });

            // Disable the entity form status check
            if (BLCAdmin.entityForm.status) {
                BLCAdmin.entityForm.status.setDidConfirmLeave(true);
            }

            BLC.get({
                url : BLC.servletContext + "/adminLogout.htm",
                error: function(err) {
                    window.location.replace(BLC.servletContext + "/login?sessionTimeout=true");
                }
            }, function(data) {
                /*
                 * After the logout occurs, we redirect to the login page with the sessionTimeout parameter being true.
                 * This yield a red banner on the login screen that indicates the session expired to the user.
                 */
                window.location.replace(BLC.servletContext + "/login?sessionTimeout=true");
            });
            return false;
        },
        
        updateTimer : function() {
            
            BLCAdmin.sessionTimer.updateTimeLeft();
            /*
             * If the time left is less than the expire message time, then we know to display the expire message.
             */
            if (BLCAdmin.sessionTimer.getTimeLeft() < BLCAdmin.sessionTimer.getExpireMessageTime()) {

                /*
                 * If the session is expired: invalidate the session, and end the timeout loop by returning false.
                 */
                if (BLCAdmin.sessionTimer.isExpired()) {
                    $("#lightbox").hide();
                    BLCAdmin.sessionTimer.invalidateSession();
                    return false;
                }

                /*
                 * If the session is not expired: update the session expiring text with the current time left, and
                 * display the session expiration message lightbox.
                 */
                $("#expire-text").html(BLCAdmin.messages.sessionCountdown
                                        + BLCAdmin.sessionTimer.getTimeLeftSeconds()
                                        + BLCAdmin.messages.sessionCountdownEnd);

                /*
                 * Here we make sure that the session expiring lightbox is displayed.
                 */
                $("#lightbox").show();
                activityCount = 0;

                return true;
            } else if (BLCAdmin.sessionTimer.getTimeLeft() % BLCAdmin.sessionTimer.getActivityPingInterval() == 0) {
                
                /*
                 * If activityCount is greater than 0, we know that at least one key has been pressed. This means there has
                 * been activity and we should reset the timer.
                 */
                if (activityCount > 0) {
                    BLCAdmin.sessionTimer.resetTimer();
                    activityCount = 0;
                    return true;
                }
            }
            
            /*
             * If our code has reached this point then the session time left is greater than the warning interval and
             * the lightbox should not be showing.
             */
            $("#lightbox").hide();
            return true;
        }

    };
})(jQuery, BLCAdmin);

$(document).ready(function() {
    
    /*
     * We must reset the timer when the page is loaded so we can update the last reset time and session timeout interval.
     */
    BLCAdmin.sessionTimer.resetTimer();

    /*
     * This function provides the proper functionality for the "Stay Logged In" button on the expire message.
     */
    var stayLoggedIn = function() {
        /*
         * This is used to invalidate the old timeout thread
         */
        $.doTimeout('update-admin-session');
        
        $("#lightbox").hide();
        activityCount = 0;
        
        /*
         * We must reset the timer so we can stay logged in.
         */
        BLCAdmin.sessionTimer.resetTimer();
        
        /*
         * This is used to create a new timeout thread
         */
        $.doTimeout('update-admin-session', BLCAdmin.sessionTimer.getPingInterval(), BLCAdmin.sessionTimer.updateTimer);
    };

    $("#stay-logged-in").click(function() {
        stayLoggedIn();
        return false;
    });
    
    var sessionLogout = function() {
        $.doTimeout('update-admin-session');
        $.removeCookie('sessionResetTime', {path: resetTimeCookiePath});
    };
    
    $("#session-logout").click(function() {
        sessionLogout();
        return true;
    });
    
    /*
     * This is used to initiate the timeout thread that tracks the session time and listens for activity.
     */
    $.doTimeout('update-admin-session', BLCAdmin.sessionTimer.getPingInterval(), BLCAdmin.sessionTimer.updateTimer);

});
