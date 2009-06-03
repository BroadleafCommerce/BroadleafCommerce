<%@ include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

		<blc:breadcrumb categoryList="${breadcrumbCategories}" />
		<br />
		<h1>${currentProduct.name}</h1>
		<p>${currentProduct.description}</p>

		<h2>SKUs</h2>

		<table border="1">
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Add to Cart</th>
			</tr>
			<c:forEach var="item" items="${currentProduct.skus}" varStatus="status">
				<tr>
					<td><c:out value="${item.name}" /></td>
					<td><c:out value="${item.retailPrice}" /></td>
					<td><a href="<c:url value="/basket/addSku.htm"><c:param name="skuId" value="${item.id}"/></c:url>">Add to Cart</td>
				</tr>
			</c:forEach>

		</table>

	</tiles:putAttribute>
</tiles:insertDefinition>