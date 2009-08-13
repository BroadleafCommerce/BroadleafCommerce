<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	<h3 class="pageTitle" ><b>Find Order</b></h3>

	<form:form method="post" action="processFindOrder.htm" commandName="findOrderForm">
		<div class="column span-24" style="margin-top:0px;" >
			<div class="column span-3" style="height:150px; line-height:25px;">
				<label>Order Number:</label> <br/><br/>
				<label>Postal Code: </label> <br/> <br/>
				<input type="submit" name="findOrder" value="Find Order"/>
			</div>
			<div class="column span-5" >
				<form:input path="orderNumber"/> <br/><br/>
				<form:input path="postalCode"/> 
			</div>
		</div>
	</form:form>
	</tiles:putAttribute>
</tiles:insertDefinition>