<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<h1 style="margin-bottom:10px;">My Account</h1>	

		<a href="/broadleafdemo/orders/viewOrders.htm" > View Previous Orders </a> <br/> <br/>
		<a href="/broadleafdemo/orders/findOrder.htm" > Find Order </a> 
	</tiles:putAttribute>
</tiles:insertDefinition>