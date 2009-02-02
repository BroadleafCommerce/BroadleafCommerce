<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

	<h1>Manage address</h1>
	<table border="1">
	<tr>
		<th>Name</th>
		<th>Address Line 1</th>
		<th>Address Line 2</th>
		<th>City</th>
		<th>State</th>
		<th>ZipCode</th>
	</tr>
	<c:forEach var="item" items="${addressList}" varStatus="status">
		<tr>
			<td><a href="<c:url value="/createAddress.htm"><c:param name="addressId" value="${item.id}"/></c:url>"><c:out value="${item.addressName}"/></td>
			<td><c:out value="${item.addressLine1}"/></td>
			<td><c:out value="${item.addressLine2}"/></td>
			<td><c:out value="${item.city}"/></td>
			<td><c:out value="${item.stateCode}"/></td>
			<td><c:out value="${item.zipCode}"/></td>
		</tr>
	</c:forEach>
	
	</table>
	
	</tiles:putAttribute>
</tiles:insertDefinition>