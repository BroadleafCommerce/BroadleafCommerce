<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:choose>
	<c:when test="${!empty currentProducts}">
	 	<c:forEach var="product" items="${currentProducts}" varStatus="status">
        	<div class="span-4 <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">last</c:if>" align="center">
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
					<img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" />
				</a><br />
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">${product.name}</a>
				<img src="/broadleafdemo/images/addToCart-160x25.png" />
			</div>
			<c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">
				<div class="span-13">&nbsp;</div>
			</c:if>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<h2>This category has no products</h2>
	</c:otherwise>	
</c:choose>
