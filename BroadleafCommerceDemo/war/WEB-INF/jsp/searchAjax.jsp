<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h2>
	Search Results
</h2>
<c:forEach var="category" items="${categories}" varStatus="status">
	<div class="searchCategory">
		<h3><c:out value="${category.name}"/></h3>
		<c:forEach var="product" items="${categoryGroups[category.id]}" varStatus="status">
			<div class="searchProduct">
				<a href="/broadleafdemo/${category.generatedUrl}?productId=${product.id}"><c:out value="${product.name}"/></a>
			</div>
		</c:forEach>
	</div>
</c:forEach>