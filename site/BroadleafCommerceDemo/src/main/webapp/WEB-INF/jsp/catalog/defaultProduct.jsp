<%@ include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="baseNoSide">
<tiles:putAttribute name="mainContent" type="string">
    <div class="breadcrumb">
        <blc:breadcrumb categoryList="${breadcrumbCategories}" />
    </div>
    <div id="productContainer" class="mainContentArea">
        <h3 class="productName">${currentProduct.name}</h3>
        <jsp:include page="relatedProducts.jsp" />
        <div class="columns">
            <div class="column productImage span-5">
                <c:choose>
                    <c:when test="${!empty currentProduct.productImages.large}">
                        <a href="/broadleafdemo${currentProduct.productImages.large}" class="thickbox">
                            <img src="/broadleafdemo${currentProduct.productImages.large}" width="150" />
                            <p>View larger image</p>
                        </a>
                    </c:when>
                    <c:otherwise>
                        Image not available
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="column productSummary span-11">
                <div class="bottomRule">
                    <c:if test="${currentProduct.featuredProduct}">
                        <img class="featuredSmall" src="/broadleafdemo/images/featuredSmallRed.gif" /><br/>
                    </c:if>
                    <c:if test="${!empty currentProduct.manufacturer}" >
                        <b>Manufacturer:</b> ${currentProduct.manufacturer}<br/>
                    </c:if>
                    <c:if test="${!empty currentProduct.model}" >
                        <b>Model:</b> ${currentProduct.model}<br/>
                    </c:if>
                    <b>SKU:</b> ${currentProduct.skus[0].id}<br/>
                    <c:if test="${!empty currentProduct.longDescription}" >
                        <b>Description:</b> ${currentProduct.longDescription}<br/>      
                    </c:if>
                </div>
                <div class="columns">
                    <div class="productLeftCol column span-7">  
                        <c:if test="${!(empty currentProduct.weight.weight)}" >
                            <span> <b>Weight: </b> ${currentProduct.weight.weight} lb</span> <br/>
                        </c:if>
                        <c:if test="${!(empty currentProduct.dimension.width)}" >
                            <span> <b>Dimensions (WDH): </b> ${currentProduct.dimension.width} X 
                            ${currentProduct.dimension.depth} X ${currentProduct.dimension.height}  </span> <br/>
                        </c:if>
                    </div>
                    <div class="productRightCol">
                        <span class="productPrice"> 
                            <b> Our Price: </b>
                            <c:choose>
                                <c:when test="${currentProduct.skus[0].salePrice != currentProduct.skus[0].retailPrice}" >
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
                        <form:form method="post" modelAttribute="wishlistRequest" action="../../wishlist/addToWishlist.htm">
                            <form:hidden path="addProductId" />
                            <form:hidden path="addCategoryId" />
                            <form:hidden path="addSkuId" />
                            <form:hidden path="quantity"  />
                            <c:choose>
                                <c:when test="${customer.anonymous}">
                                    Logged in users can add items to wishlists
                                </c:when>
                                <c:when test="${(wishlists != null) && !(empty wishlists) }" >
                                    <form:select path="wishlistName">
                                        <form:options items="${wishlists}" itemValue="name" itemLabel="name" />
                                    </form:select>
                                    <button class="wishlistBtn" type="submit" name="addToWishlist">Add To Wishlist</button>     
                                </c:when>
                                <c:otherwise>
                                    <button class="wishlistBtn" type="submit" name="addToWishlist">Add To New Wishlist</button>
                                </c:otherwise>
                            </c:choose>
                        </form:form>
                    </div>
                </div>
                <jsp:include page="ratingsAndReviews.jsp" />
                <div class="socialNetworks">
                    Share this Product: <blc:share />
                </div>
            </div>

        </div>
        

    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>