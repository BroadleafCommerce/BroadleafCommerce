<%@ include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">


		<blc:breadcrumb categoryList="${breadcrumbCategories}" />

		<br />
		<br />
		<div class="columns">
		<div class="column" style="width: 350px; text-align: center;"><c:choose>
			<c:when test="${!empty currentProduct.productImages.large}">
				<a href="${contextPath}${currentProduct.productImages.large}"
					class="thickbox"><img
					src="/broadleafdemo${currentProduct.productImages.large}"
					width="250" />
				<p>View larger image</p>
				</a>
			</c:when>
			<c:otherwise>
					Image not available
					</c:otherwise>
		</c:choose></div>
		<div class="column" style="width: 400px;"><c:if
			test="${!empty currentProduct.manufacturer}">
			<h1>${currentProduct.manufacturer}</h1>
		</c:if>
		<h2>${currentProduct.name}</h2>
		<c:if test="${currentProduct.isFeaturedProduct}">
			<h3>Featured</h3>
		</c:if>
		<p>${currentProduct.longDescription}</p>
		</div>
		</div>



		<h2>SKUs</h2>

		<table border="1">
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Add to Cart</th>
			</tr>
			<c:forEach var="item" items="${currentProduct.skus}"
				varStatus="status">
				<tr>
					<td>${item.name}</td>
					<td>${item.salePrice}</td>
					<td><a
						href="<c:url value="/basket/addItem.htm">
				<c:param name="skuId" value="${item.id}"/>
				<c:param name="quantity" value="1"/>
				</c:url>">Add
					to Cart</td>
				</tr>
			</c:forEach>

		</table>

		<br />
		<br />

		<h2>Cross Sales</h2>

		<table border="1">
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Add to Cart</th>
			</tr>
			<c:forEach var="item" items="${currentProduct.crossSaleProducts}"
				varStatus="status">
				<tr>
					<td><c:out value="${item.relatedSaleProduct.name}" /></td>
					<td><c:out
						value="${item.relatedSaleProduct.skus[0].salePrice}" /></td>
					<td><a
						href="<c:url value="/basket/addItem.htm">
				<c:param name="skuId" value="${item.relatedSaleProduct.skus[0].id}"/>
				<c:param name="quantity" value="1"/>
				</c:url>">Add
					to Basket</td>
				</tr>
			</c:forEach>

		</table>


		<br />
		<br />

		<h2>Up Sales</h2>

		<table border="1">
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Add to Cart</th>
			</tr>
			<c:forEach var="item" items="${currentProduct.upSaleProducts}"
				varStatus="status">
				<tr>
					<td><c:out value="${item.relatedSaleProduct.name}" /></td>
					<td><c:out
						value="${item.relatedSaleProduct.skus[0].salePrice}" /></td>
					<td><a
						href="<c:url value="/basket/addItem.htm">
				<c:param name="skuId" value="${item.relatedSaleProduct.skus[0].id}"/>
				<c:param name="quantity" value="1"/>
				</c:url>">Add
					to Basket</td>
				</tr>
			</c:forEach>

		</table>

	</tiles:putAttribute>
</tiles:insertDefinition>