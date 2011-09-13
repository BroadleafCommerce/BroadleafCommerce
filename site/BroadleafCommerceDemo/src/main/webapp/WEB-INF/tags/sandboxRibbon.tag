<%@ tag body-content="empty" description="Displays the sandbox ribbon.  Assumes jQuery is available, and optionally may provide path to the jquery ui datepicker plugin. " %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ attribute rtexprvalue="true" name="jqueryUIDatepicker" description="Path to jquery ui datepicker script to be included only if in sandbox mode.  Default: script will not be included by this tag" %>


<c:if test="${session['BLC_SANDBOX_TIME'] ne null}">
	<c:if test="${!empty jqueryUIDatepicker}">
		<script type="text/javascript" src="${jqueryUIDatepicker}"></script>
	</c:if>
	<style type="text/css"> 
		#sandboxRibbon {
			padding:10px;
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
			<h3 style="float:left;padding-left:50px;">Sandbox Mode</h3>
			<div style="float:right;">
			<label>Sandbox Date:</label>
			<input type="text" id="sandboxDate" name="sandboxDate" size="20" value="${blSandboxDisplayDateTimeDate}"/>
			<label>Time:</label>
			<input type="text" name="hours" size="2" value="${blSandboxDisplayDateTimeHours}"/>:
			<input type="text" name="minutes" size="2" value="${blSandboxDisplayDateTimeMinutes}"/>
			<select name="ampm">
				<c:choose>
					<c:when test="${blSandboxDisplayDateTimeAMPM == 0}">
						<option value="am" selected="selected">am</option>
						<option value="pm">pm</option>
					</c:when>
					<c:otherwise>
						<option value="am">am</option>
						<option value="pm" selected="selected">pm</option>
					</c:otherwise>
				</c:choose>
			</select>
			<input type="submit" value="Submit" name="blSandboxDateTimeRibbonOverride"/>
			</div>
		</form>
	</div>
</c:if>