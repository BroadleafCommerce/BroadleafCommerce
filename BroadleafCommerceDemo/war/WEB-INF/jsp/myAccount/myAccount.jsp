<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<h3 style="margin-top: 10px;" ><b>My Account</b></h3>
		<a class="noTextUnderline" href="/broadleafdemo/orders/viewOrders.htm" > View Previous Orders </a> <br/>
		<a class="noTextUnderline" href="/broadleafdemo/orders/findOrder.htm" > Find Order </a> 
	</tiles:putAttribute>
</tiles:insertDefinition>