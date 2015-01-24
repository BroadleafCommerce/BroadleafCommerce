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
                sessionTimeLeft = data.maxInterval;
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
        }

    };
})(jQuery, BLCAdmin);

$(document).ready(
        function() {

            var activityCount = 0;

            $(document).keypress(function(e) {
                activityCount++;

            });

            $(document).click(function(e) {
                activityCount++;
            });

            $.doTimeout(BLCAdmin.sessionTimer.getPingInterval(), function() {

                BLCAdmin.sessionTimer.decrement(BLCAdmin.sessionTimer
                        .getPingInterval());
                

                if (activityCount > 0) {
                    BLCAdmin.sessionTimer.resetTimer();
                    activityCount = 0;
                    $("#session-minute").fadeOut("slow");
                    return true;
                }

                if (BLCAdmin.sessionTimer.verifyAndUpdateTimeLeft()) {
                    return true;
                }
                
                var timeLeft = BLCAdmin.sessionTimer.getTimeLeft();
                if (timeLeft <= 0) {

                    // session has expired
                    $("#session-minute").fadeOut("fast");
                    $("#session-expire").fadeIn("slow");
                    $("#session-expire a.close-notify").click(function() {
                        $("#session-expire").fadeOut("slow");
                        window.location.replace("/admin/login"); // Possible
                        // BLC
                        // redirect
                        return false;
                    });
                    return false;

                } else if (timeLeft <= 60000) {
                    // session time less than one minute
                    $("#session-minute").fadeIn("slow");
                    $("#session-minute a.close-notify").click(function() {
                        $("#session-minute").fadeOut("slow");
                        BLCAdmin.sessionTimer.resetTimer();
                        return false;
                    });
                }

                return true;
            });

        });