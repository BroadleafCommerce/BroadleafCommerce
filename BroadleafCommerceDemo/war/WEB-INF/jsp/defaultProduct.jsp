<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

<h1>Default Product View For: <c:out value="${product.catalogItem.name}"/></h1>
<sc:breadcrumb/>
<br/>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><c:out value="${product.catalogItem.name}"/></td>
		<td><c:out value="${product.catalogItem.description}"/></td>
	</tr>

</table>

<h2>Sellable Items</h2>

<table border="1">
	<tr>
		<th>Name</th>
		<th>Price</th>
		<th>Add to Cart</th>
	</tr>
	<c:forEach var="item" items="${product.sellableItems}" varStatus="status">
		<tr>
			<td><c:out value="${item.name}"/></td>
			<td><c:out value="${item.price}"/></td>
			<td><a href="<c:url value="/basket/addSellableItem.htm"><c:param name="sellableItemId" value="${item.id}"/></c:url>">Add to Basket</td>
		</tr>
	</c:forEach>

</table>

	</tiles:putAttribute>
</tiles:insertDefinition>