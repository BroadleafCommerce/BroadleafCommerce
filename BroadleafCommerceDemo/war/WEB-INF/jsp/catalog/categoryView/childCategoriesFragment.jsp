<%@ include file="/WEB-INF/jsp/include.jsp" %>
	Child Categories:<br />
	<c:forEach var="childCategory" items="${currentCategory.childCategories}" varStatus="status">
       	<div class="blueBorder span-4 <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">last</c:if>" align="center">
			<a href="/broadleafdemo/${childCategory.generatedUrl}?productId=${product.id}">
				<img border="0" title="${childCategory.name}" alt="${childCategory.name}" src="/broadleafdemo${product.productImages.small}" />
			</a><br />
			<a href="/broadleafdemo/${childCategory.generatedUrl}?productId=${childCategory.id}">${childCategory.name}</a>
			<c:if test="${product.isFeaturedProduct}"><br />Featured</c:if>
		</div>
		<c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">
			<div class="blueBorder span-13">&nbsp;</div>
		</c:if>
	</c:forEach>