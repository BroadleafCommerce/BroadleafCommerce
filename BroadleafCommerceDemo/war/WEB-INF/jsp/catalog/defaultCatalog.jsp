<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">


	<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	<h1>${currentCategory.name}</h1>
	
	<c:choose>
		<c:when test="${!empty currentCategory.childCategories}">


			<c:forEach var="child" items="${currentCategory.childCategories}" varStatus="status">
				<p><a href="/broadleafdemo/${child.generatedUrl}">${child.name}</a></p>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<h2>This category has no sub-categories</h2>
		</c:otherwise>	
	</c:choose>
	
	<c:if test="${fn:length(currentCategory.featuredProducts) > 0 }" >
		<h3> FEATURED PRODUCTS </h3>
		<table border="0">
			<tr>
				<th>Name</th>
			</tr>
			<c:forEach var="featuredProduct" items="${currentCategory.featuredProducts}" varStatus="status">
				<tr>
					<td>
						<blc:productLink product="${featuredProduct.product}" />
			  			<c:if test="${featuredProduct.product.isFeaturedProduct == true}" > 
							--FEATURED PRODUCT!-- 
						</c:if> 
						Promo Message: ${featuredProduct.promotionMessage}
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	
	<c:choose>
		<c:when test="${!empty currentProducts}">
		 	<tags:grid columnCount="4" collection="${currentProducts}">
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.id}">
					<img border="0" title="${item.name}" alt="${item.name}" src="/broadleafdemo${item.productImages.small}" />
				</a><br />
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.id}">${item.name}</a>
				<c:if test="${product.isFeaturedProduct}"><br />Featured</c:if>
			</tags:grid>
		</c:when>
		<c:otherwise>
			<h2>This category has no products</h2>
		</c:otherwise>	
	</c:choose>
	
		

	</tiles:putAttribute>
</tiles:insertDefinition>