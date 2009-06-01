<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

<h1>Default Product View For: <c:out value="${productSkus.product.name}"/></h1>

 <blc:breadcrumb categoryList="${breadcrumbCategories}" />
<br />
<br />
<br/>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><c:out value="${currentProduct.name}"/></td>
		<td><c:out value="${currentProduct.description}"/></td>
	</tr>

</table>

<h2>SKUs</h2>

<table border="1">
	<tr>
		<th>Name</th>
		<th>Price</th>
		<th>Add to Cart</th>
	</tr>
	<c:forEach var="item" items="${currentProduct.skus}" varStatus="status">
		<tr>
			<td><c:out value="${item.name}"/></td>
			<td><c:out value="${item.price}"/></td>
			<td><a href="<c:url value="/basket/addSku.htm"><c:param name="skuId" value="${item.id}"/></c:url>">Add to Basket</td>
		</tr>
	</c:forEach>

</table>

	</tiles:putAttribute>
</tiles:insertDefinition>