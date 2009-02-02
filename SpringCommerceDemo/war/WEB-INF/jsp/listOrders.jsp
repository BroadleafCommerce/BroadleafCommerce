<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>Manage Orders </h1>

	<table border="1">
	<tr>
		<th>ID</th>
		<th>Status</th>
		<th>Total</th>
	</tr>
	<c:forEach var="item" items="${orderList}" varStatus="status">
		<tr>
			<td><c:out value="${item.id}"/></td>
			<td><c:out value="${item.orderStatus}"/></td>
			<td><c:out value="${item.orderTotal}"></c:out>
		</tr>
	</c:forEach>

	</table>

	<a href="<c:url value="/createOrder.htm"/>">Create New Order</a>
	<a href="<c:url value="/logout"/>">Logout</a>

	</tiles:putAttribute>
</tiles:insertDefinition>