<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:choose>
	<c:when test="${!empty currentProducts}">
		
	 	<c:forEach var="product" items="${currentProducts}" varStatus="status">
			<c:if test="${(status.first == true) || (status.index + 1) % 3 == 1}">
				<div class="span-13">
			</c:if>      
			<div class="span-4 <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">last</c:if>" align="center">
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
					<img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" />
				</a><br />
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
					${product.name} 
				</a>
				<a href="<c:url value="/basket/addItem.htm"> <c:param name="skuId" value="${product.skus[0].id}"/>
					<c:param name="quantity" value="1"/> </c:url>">
					<img src="/broadleafdemo/images/addToCart-160x25.png"/>
				</a>
			</div>
			<c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">
				</div>
				<div class="span-13">&nbsp;</div>
			</c:if>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<h2>This category has no products</h2>
	</c:otherwise>	
</c:choose>
