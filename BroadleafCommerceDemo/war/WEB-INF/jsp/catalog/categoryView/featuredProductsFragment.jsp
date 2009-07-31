<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:if test="${fn:length(currentCategory.featuredProducts) > 0 }" >
	<c:forEach var="featuredProduct" items="${currentCategory.featuredProducts}" varStatus="status">
		<div class="span-13 columns productResults featuredProduct">
			<div class="span-2 column productResultsImage" align="center">
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
					<img border="0" title="${featuredProduct.product.name}" alt="${featuredProduct.product.name}" src="/broadleafdemo${featuredProduct.product.productImages.small}" width="80"/>
				</a>
			</div>
			<div class="span-6 column productResultsInfo">
				<blc:productLink product="${featuredProduct.product}" /> <br/>
				<c:if test="${!(empty featuredProduct.product.manufacturer) }" >
					<span> <b>Manufacturer:</b> ${featuredProduct.product.manufacturer} </span> <br/>
				</c:if>
				<c:if test="${!(empty featuredProduct.product.model) }" >
					<span> <b>Model:</b> ${featuredProduct.product.model} </span> <br/>
				</c:if>
			</div>
			<div class="span-3 column productResultsRightCol" style="float:right;text-align:right;">
				<span class="productPrice"> 
					<c:choose>
						<c:when test="${featuredProduct.product.skus[0].salePrice != null}" >
							Sale: <span class="salePrice">$<c:out value="${featuredProduct.product.skus[0].salePrice}" /></span>
							<br/><span class="originalPrice">$<c:out value="${featuredProduct.product.skus[0].retailPrice}" /></span>
						</c:when>			
						<c:otherwise>
							<span class="salePrice">$<c:out value="${featuredProduct.product.skus[0].retailPrice}" /></span>
						</c:otherwise>
					</c:choose>
				</span> <br/><br/>
				<a class="addCartBtn" href="<c:url value="/basket/addItem.htm"> <c:param name="skuId" value="${featuredProduct.product.skus[0].id}"/>
					<c:param name="quantity" value="1"/> </c:url>">Add to Cart</a>
			</div>
			<div class="span-13">
				<span class="featuredProductPromo"> <b>${featuredProduct.promotionMessage} </b></span>
			</div>	
		</div>
	</c:forEach>
</c:if>