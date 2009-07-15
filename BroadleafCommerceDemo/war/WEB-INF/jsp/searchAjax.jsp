<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h2>
	Search Results
</h2>
<table border="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
	</tr>
	<c:forEach var="item" items="${products}" varStatus="status">
		<tr>
			<td><c:out value="${item.id}"/></td>
			<td><a href="/broadleafdemo/${item.defaultCategory.generatedUrl}?productId=${item.id}"><c:out value="${item.name}"/></a></td>
		</tr>
	</c:forEach>
</table>