<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>Manage Orders </h1>

	<table border="1">
		<tr>
			<th>View </th>
			<th>Order Number</th>
			<th>Order Date</th>
			<th>Total</th>
			<th>Status</th>
		</tr>
		<c:forEach var="item" items="${orderList}" varStatus="status">
			<tr>
				<td><a href="viewOrderDetails.htm?orderNumber=${item.orderNumber}"> View Order Detail</a>
				<td><c:out value="${item.orderNumber}"/></td>
				<td><c:out value="${item.submitDate}"/></td>
				<td><c:out value="${item.total}"/></td>
				<td><c:out value="${item.status.type}"/></td>
			</tr>
		</c:forEach>
	</table>

	<a href="<c:url value="/logout"/>">Logout</a>

	</tiles:putAttribute>
</tiles:insertDefinition>