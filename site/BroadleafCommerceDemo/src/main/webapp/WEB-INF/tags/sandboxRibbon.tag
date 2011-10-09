<%@ tag body-content="empty" description="Displays the sandbox ribbon.  Assumes jQuery is available, and optionally may provide path to the jquery ui datepicker plugin. " %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute rtexprvalue="true" name="jqueryUIDatepicker" description="Path to jquery ui datepicker script to be included only if in sandbox mode.  Default: script will not be included by this tag" %>

<c:if test="${blSandbox ne null}">
	<c:if test="${!empty jqueryUIDatepicker}">
		<script type="text/javascript" src="${jqueryUIDatepicker}"></script>
	</c:if>
	<style type="text/css"> 
		#sandboxRibbon {
			padding:0 10px 5px;
			background-color:#FFCC33; 
			text-align:center; 
			color:#000000;
			border-bottom: 2px solid #000000;
		}
		#sandboxRibbon:after {
			content: ".";
			display: block;
			height: 0;
			clear: both;
			visibility: hidden;
		}		
		
		#sandboxRibbon h3 {
			margin:4px 0 0 0;
		}
		
		#returnToProduction {
			background-color:transparent; 
			text-decoration:underline; 
			border:none; 
			cursor:pointer;
		}
	</style>
	<div id="sandboxRibbon">
		<script type="text/javascript">
		    jQuery(function($){ //on document.ready
		    	if ($.datepicker) {
			        var $sandboxDatePicker = $('#sandboxDate').datepicker();
		    	}
		    });
		</script>

		<form id="sandboxPicker" method="post">
			<h3 style="float:left;padding-left:50px;">
				Sandbox Mode<br/>
				<input id="returnToProduction" type="submit" value="Return to Production" name="blSandboxDateTimeRibbonProduction" style=""/>
			</h3>
			
			<div style="float:right;margin-top:5px;">
			<label>Sandbox Date:</label>
			<input type="text" id="sandboxDate" name="blSandboxDisplayDateTimeDate" size="20" value="${blSandboxDisplayDateTimeDate}"/>
			<label>Time:</label>
			<input type="text" name="blSandboxDisplayDateTimeHours" size="2" value="${blSandboxDisplayDateTimeHours}"/>:
			<input type="text" name="blSandboxDisplayDateTimeMinutes" size="2" value="${blSandboxDisplayDateTimeMinutes}"/>
			<select name="blSandboxDisplayDateTimeAMPM">
				<c:choose>
					<c:when test="${blSandboxDisplayDateTimeAMPM == 0}">
						<option value="AM" selected="selected">am</option>
						<option value="PM">pm</option>
					</c:when>
					<c:otherwise>
						<option value="AM">am</option>
						<option value="PM" selected="selected">pm</option>
					</c:otherwise>
				</c:choose>
			</select>
			<input type="submit" value="Submit" name="blSandboxDateTimeRibbonOverride"/>
			</div>
		</form>
	</div>
</c:if>