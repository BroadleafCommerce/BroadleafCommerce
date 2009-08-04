<%@ include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="baseNoSide">
<tiles:putAttribute name="mainContent" type="string">
	<div class="breadcrumb">
		<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	</div>
	<div id="productContainer" class="productContainer">
		<div class="columns">
			<div class="column productImage span-5">
				<c:choose>
					<c:when test="${!empty currentProduct.productImages.large}">
						<a href="${contextPath}${currentProduct.productImages.large}" class="thickbox">
							<img src="/broadleafdemo${currentProduct.productImages.large}" width="150" />
							<p>View larger image</p>
						</a>
					</c:when>
					<c:otherwise>
						Image not available
					</c:otherwise>
				</c:choose>
			</div>
			<div class="column productSummary span-13">
				<div class="productSummaryTop" >
					<c:if test="${currentProduct.isFeaturedProduct == true}">
						<h3 class="productName">${currentProduct.name} -- <b> FEATURED </b> </h3> 	
					</c:if>
					<c:if test="${currentProduct.isFeaturedProduct == false}">
						<h3 class="productName">${currentProduct.name} </h3> 	
					</c:if>
					<c:if test="${!(empty currentProduct.manufacturer) }" >
						<span> <b>Manufacturer:</b> ${currentProduct.manufacturer} </span> <br/>
					</c:if>
					<c:if test="${!(empty currentProduct.model) }" >
						<span> <b>Model:</b> ${currentProduct.model} </span> <br/>
					</c:if>
					<span> <b>SKU:</b> ${currentProduct.skus[0].id} </span> <br/>
					<c:if test="${!(empty currentProduct.longDescription) }" >
						<span> <b>Description:</b> ${currentProduct.longDescription} </span> <br/>		
					</c:if>
				</div>
				<div class="productLeftCol column span-7">	
					<c:if test="${!(empty currentProduct.weight.weight)}" >
						<span> <b>Weight: </b> ${currentProduct.weight.weight} lb</span> <br/>
					</c:if>
					<c:if test="${!(empty currentProduct.dimension.width)}" >
						<span> <b>Dimensions (WDH): </b> ${currentProduct.dimension.width} X 
						${currentProduct.dimension.depth} X ${currentProduct.dimension.height}  </span> <br/>
					</c:if>
				</div>
				<div class="productRightCol column span-5">
					<span class="productPrice"> 
						<b> Our Price: </b>
						<c:choose>
							<c:when test="${currentProduct.skus[0].salePrice != null}" >
								<span class="strikethrough"><c:out value="${currentProduct.skus[0].retailPrice}" /></span>
								<c:out value="${currentProduct.skus[0].salePrice}" />
							</c:when>			
							<c:otherwise>
								<c:out value="${currentProduct.skus[0].retailPrice}" />
							</c:otherwise>
						</c:choose>
					</span>
					<br/><br/>
					<a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
							<c:param name="skuId" value="${currentProduct.skus[0].id}"/>
							<c:param name="quantity" value="1"/>
							</c:url>">Add to Cart</a><br/> <br/>
					<a href="" > Add To Wishlist </a>
				</div>
			</div>
			<div class="relatedProducts column last span-6">
				<div class="productUpSale">
					<c:if test="${(currentProduct.upSaleProducts != null) && !(empty currentProduct.upSaleProducts) }" >
						<h3 class="relatedProd">You may also like</h3>
						<c:forEach var="item" items="${currentProduct.upSaleProducts}" varStatus="status">
							<div class="relatedProd">
								<div class="relatedProdImage">
									<a href="${contextPath}${item.relatedSaleProduct.productImages.small}" class="thickbox">
										<img src="/broadleafdemo${item.relatedSaleProduct.productImages.small}" width="80" />
									</a>
								</div> 
								<div class="relatedProdText">
									<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.relatedSaleProduct.id}">
										${item.relatedSaleProduct.name}
									</a>
									<br/> Our Price: <br/>
									<c:choose>
										<c:when test="${item.relatedSaleProduct.skus[0].salePrice != null}" >
											<span class="strikethrough"><c:out value="${item.relatedSaleProduct.skus[0].retailPrice}" /></span>
											<c:out value="${item.relatedSaleProduct.skus[0].salePrice}" />
										</c:when>			
										<c:otherwise>
											<c:out value="${item.relatedSaleProduct.skus[0].retailPrice}" />
										</c:otherwise>
									</c:choose><br/>
								    <a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
					   					<c:param name="skuId" value="${item.relatedSaleProduct.skus[0].id}"/>
										<c:param name="quantity" value="1"/>
										</c:url>" >Add to Cart</a>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div> 
				<div class="productCrossSale">
					<c:if test="${(currentProduct.crossSaleProducts != null) && !(empty currentProduct.crossSaleProducts) }" >
						<h3 class="relatedProd">Related Products</h3>
						<c:forEach var="item" items="${currentProduct.crossSaleProducts}" varStatus="status">
							<div class="relatedProd">
								<div class="relatedProdImage">
									<a href="${contextPath}${item.relatedSaleProduct.productImages.small}" class="thickbox">
										<img src="/broadleafdemo${item.relatedSaleProduct.productImages.small}" width="80" />
									</a>
								</div> 
								<div class="relatedProdText">
									<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.relatedSaleProduct.id}">
										${item.relatedSaleProduct.name}
									</a>
									<br/> Our Price: <br/>
									<c:choose>
										<c:when test="${item.relatedSaleProduct.skus[0].salePrice != null}" >
											<span class="strikethrough"><c:out value="${item.relatedSaleProduct.skus[0].retailPrice}" /></span>
											<c:out value="${item.relatedSaleProduct.skus[0].salePrice}" />
										</c:when>			
										<c:otherwise>
											<c:out value="${item.relatedSaleProduct.skus[0].retailPrice}" /> 
										</c:otherwise>
									</c:choose>
									 <br/><a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
										<c:param name="skuId" value="${item.relatedSaleProduct.skus[0].id}"/>
										<c:param name="quantity" value="1"/>
										</c:url>">Add to Cart</a>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		<div>
			Share this Product: <blc:share />
		</div>
	</div>
	</tiles:putAttribute>
</tiles:insertDefinition>