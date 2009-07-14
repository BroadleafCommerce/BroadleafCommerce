<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h2>
	Search Results
</h2>
<table border="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Price</th>
	</tr>
	<c:forEach var="item" items="${skus}" varStatus="status">
		<tr>
			<td><c:out value="${item.id}"/></td>
			<td><a href=""><c:out value="${item.name}"/></a></td>
			<td><c:out value="${item.salePrice}"/></td>
		</tr>
	</c:forEach>
</table>