<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>Find Order</h1>

	<form:form method="post" action="processFindOrder.htm" commandName="findOrderForm">
		<label> Order Number : </label> <form:input path="orderNumber"/>
		<label> Postal Code: </label> <form:input path="postalCode"/> 	

		<input type="submit" name="findOrder" value="Find Order"/>
	</form:form>
	
	</tiles:putAttribute>
</tiles:insertDefinition>