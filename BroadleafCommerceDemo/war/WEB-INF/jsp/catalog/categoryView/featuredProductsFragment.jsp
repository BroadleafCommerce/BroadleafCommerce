<%@ include file="/WEB-INF/jsp/include.jsp" %>

Featured Products
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