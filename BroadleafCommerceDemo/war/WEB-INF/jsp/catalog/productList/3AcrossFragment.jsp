<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:choose>
	<c:when test="${!empty currentProducts}">
	 	<c:forEach var="product" items="${currentProducts}" varStatus="status">
			<div class="span-13 columns productResults">
				<div class="span-2 column productResultsImage" align="center">
					<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
						<img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" width="75"/>
					</a>
				</div>
				<div class="span-6 column productResultsInfo">
					<blc:productLink product="${product}" />  <br/>
					<c:if test="${!(empty product.manufacturer) }" >
						<span> <b>Manufacturer:</b> ${product.manufacturer} </span> <br/>
					</c:if>
					<c:if test="${!(empty product.model) }" >
						<span> <b>Model:</b> ${product.model} </span> <br/>
					</c:if>
				</div>
				<div class="span-3 column productResultsRightCol" style="float:right;text-align:right;">
					<span class="productPrice">
						<c:choose>
							<c:when test="${product.skus[0].salePrice != null}" >
								Sale: <span class="salePrice">$<c:out value="${product.skus[0].salePrice}" /></span>
								<br/><span class="originalPrice">$<c:out value="${product.skus[0].retailPrice}" /></span>
							</c:when>			
							<c:otherwise>
								<span class="salePrice">$<c:out value="${product.skus[0].retailPrice}" /></span>
							</c:otherwise>
						</c:choose>
					</span> <br/><br/>
					<a class="addCartBtn" href="<c:url value="/basket/addItem.htm"> <c:param name="skuId" value="${product.skus[0].id}"/>
						<c:param name="quantity" value="1"/> </c:url>">Add to Cart</a>
				</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
	</c:otherwise>	
</c:choose>
