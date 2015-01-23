/**
 * @author Nick Crum
 */

(function($, BLCAdmin) {
	
	var sessionTimeLeft = 0;
	var pingInterval = 30000;
	
	BLCAdmin.sessionTimer = {
		
			resetTimer : function(){
				BLC.get({ url: "/admin/sessionTimerInactiveInterval"}, function(data){
					sessionTimeLeft = data;
				});
			},
			
			getTimeLeft : function(){
				return sessionTimeLeft;
			},
			
			decrement : function(val){
				sessionTimeLeft -= val;
			},
			
			getPingInterval : function(){
				return pingInterval;
			}
			
			
	};
})(jQuery, BLCAdmin);

$(document).ready(function(){
	
	var activityCount = 0;
	
	$(document).keypress(function(e){
		activityCount++;
	});
	
	$(document).click(function(e){
		activityCount++;
	});
	
	$.doTimeout( BLCAdmin.sessionTimer.getPingInterval(), function(){
		BLCAdmin.sessionTimer.decrement(BLCAdmin.sessionTimer.getPingInterval());
		var timeLeft = BLCAdmin.sessionTimer.getTimeLeft();
		
		if(activityCount > 0){
			BLCAdmin.sessionTimer.resetTimer();
			activityCount = 0;
			$("#session-minute").fadeOut("slow");
			return true;
		}
		
		if ( timeLeft <= 0) {
			// session has expired
			$("#session-minute").fadeOut("fast");
			$("#session-expire").fadeIn("slow");
		    $("#session-expire a.close-notify").click(function() {
		        $("#session-expire").fadeOut("slow");
		        window.location.replace("/admin/login"); //Possible BLC redirect
		        return false;
		    });
			return false;
			
		} else if (timeLeft <= 60000){
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