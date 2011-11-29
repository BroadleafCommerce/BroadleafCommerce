<%@ include file="/WEB-INF/jsp/include.jsp"%>
<c:if test="${!empty currentProduct.upSaleProducts && !empty currentProduct.crossSaleProducts}">
			<div class="relatedProducts">
				<div class="productUpSale">
					<c:if test="${!empty currentProduct.upSaleProducts}" >
						<h3 class="relatedProd">You might also like</h3>
						<c:forEach var="item" items="${currentProduct.upSaleProducts}" varStatus="status">
							<div class="relatedProd clearfix">
								<div class="relatedProdImage">
									<a href="<c:choose><c:when test="${!(fn:startsWith(item.relatedProduct.productMedia.small.url,'http')) && fn:startsWith(item.relatedProduct.productMedia.small.url,'/')}"><c:out value="${pageContext.request.contextPath}"/></c:when></c:choose>${item.relatedProduct.productMedia.small.url}" class="thickbox">
										<img src="<c:choose><c:when test="${!(fn:startsWith(item.relatedProduct.productMedia.small.url,'http')) && fn:startsWith(item.relatedProduct.productMedia.small.url,'/')}"><c:out value="${pageContext.request.contextPath}"/></c:when></c:choose>${item.relatedProduct.productMedia.small.url}" width="80" />
									</a>
								</div> 
								<div class="relatedProdText">
									<p>
										<a href="<c:out value="${pageContext.request.contextPath}"/>/${currentCategory.generatedUrl}?productId=${item.relatedProduct.id}">
											${item.relatedProduct.name}
										</a>
									</p>
									<p>Our Price: <br/>
										<c:choose>
											<c:when test="${item.relatedProduct.sku.salePrice != item.relatedProduct.sku.retailPrice }" >
												<span class="strikethrough"><c:out value="${item.relatedProduct.sku.retailPrice}" /></span>
												<c:out value="${item.relatedProduct.sku.salePrice}" />
											</c:when>			
											<c:otherwise>
												<c:out value="${item.relatedProduct.sku.retailPrice}" />
											</c:otherwise>
										</c:choose>
									</p>
									<p>
								    	<a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
						   					<c:param name="skuId" value="${item.relatedProduct.sku.id}"/>
						   					<c:param name="productId" value="${item.relatedProduct.id}"/>
						   					<c:param name="categoryId" value="${item.relatedProduct.defaultCategory.id}"/>
											<c:param name="quantity" value="1"/>
											</c:url>" >Add to Cart</a>
									</p>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div> 
				<div class="productCrossSale">
					<c:if test="${!empty currentProduct.crossSaleProducts}" >
						<h3 class="relatedProd">Related products</h3>
						<c:forEach var="item" items="${currentProduct.crossSaleProducts}" varStatus="status">
							<div class="relatedProd clearfix">
								<div class="relatedProdImage">
									<a href="<c:choose><c:when test="${!(fn:startsWith(item.relatedProduct.productMedia.small.url,'http')) && fn:startsWith(item.relatedProduct.productMedia.small.url,'/')}"><c:out value="${pageContext.request.contextPath}"/></c:when></c:choose>${item.relatedProduct.productMedia.small.url}" class="thickbox">
										<img src="<c:choose><c:when test="${!(fn:startsWith(item.relatedProduct.productMedia.small.url,'http')) && fn:startsWith(item.relatedProduct.productMedia.small.url,'/')}"><c:out value="${pageContext.request.contextPath}"/></c:when></c:choose>${item.relatedProduct.productMedia.small.url}" width="80" />
									</a>
								</div> 
								<div class="relatedProdText">
									<p>
										<a href="<c:out value="${pageContext.request.contextPath}"/>/${currentCategory.generatedUrl}?productId=${item.relatedProduct.id}">
											${item.relatedProduct.name}
										</a>
									</p>
									<p>Our Price: <br/>
										<c:choose>
											<c:when test="${item.relatedProduct.sku.salePrice != item.relatedProduct.sku.retailPrice }" >
												<span class="strikethrough">${item.relatedProduct.sku.retailPrice}</span>
												${item.relatedProduct.sku.salePrice}
											</c:when>			
											<c:otherwise> ${item.relatedProduct.sku.retailPrice} </c:otherwise>
										</c:choose>
									</p>
									<p>
										<a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
											<c:param name="skuId" value="${item.relatedProduct.sku.id}"/>
											<c:param name="productId" value="${item.relatedProduct.id}" />
											<c:param name="categoryId" value="${item.relatedProduct.defaultCategory.id}"/>
											<c:param name="quantity" value="1"/>
											</c:url>">Add to Cart</a>
									</p>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
</c:if>