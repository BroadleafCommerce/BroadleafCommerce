<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
<tiles:putAttribute name="mainContent" type="string">
<div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
<%@ include file="/WEB-INF/jsp/include.jsp" %>
    <h3 style="margin:8px 0;font-weight:bold;">Wishlist: ${wishlist.name}</h3>
    <c:choose>
        <c:when test="${!empty wishlist.orderItems}">
            <table class="cartTable">
            <thead>
                  <tr valign="bottom">
                    <th width="80">&nbsp;</th>
                    <th>Item Description</th>
                    <th style="text-align:center;"></th>
                    <th style="text-align:right;padding-right:12px" width="150">Price</th>
                  </tr>
            </thead>
            <tbody>
                <form:form modelAttribute="cartSummary" method="POST" name="cartForm">
                <form:errors path="*" cssClass="errorText"/>
                <c:forEach items="${wishlist.orderItems}" var="orderItem" varStatus="status">       
                    <c:set var="item" value="${orderItem.sku}"/>
                    <c:set var="product" value="${item.allParentProducts[0]}"/>
                    <c:url var="itemUrl" value="/${product.defaultCategory.generatedUrl}">
                        <c:param name="productId" value="${product.id}"/>
                    </c:url>
                    <tr class="product">
                        <td class="thumbnail">
                            <a href="${itemUrl}">
                                <img border="0" title="${product.name}" width="80" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" />
                            </a>
                        </td>
                        <td class="item">
                            <p class="description">
                                <a href="${itemUrl}">${item.name}</a>
                            </p>
                        </td>
                        <td style="text-align:center;">
                            <c:url var="moveToCartUrl" value="/wishlist/moveItemToCart.htm">
                                <c:param name="orderItemId" value="${orderItem.id}"/>
                                <c:param name="wishlistName" value="${wishlist.name}"/>
                            </c:url>
                            <a class="cartLinkBtn" href="${moveToCartUrl}">Move Item To Cart</a><br/><br/>
                            <c:url var="removeItemUrl" value="/wishlist/removeWishlistItem.htm">
                                <c:param name="orderItemId" value="${orderItem.id}"/>
                                <c:param name="orderId" value="${wishlist.id}"/>
                            </c:url>
                            <a class="cartLinkBtn" href="${removeItemUrl}">Remove</a>

                        </td>
                        <td style="text-align:right;">
                            <span class="price">
                                <c:choose>
                                    <c:when test="${item.salePrice.amount != item.retailPrice.amount}">
                                        <span class="salePrice"><fmt:formatNumber type="currency" value="${item.salePrice.amount * orderItem.quantity}" /></span>
                                        <br/><span class="originalPrice">reg&nbsp;<fmt:formatNumber type="currency" value="${item.retailPrice.amount * orderItem.quantity}" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber type="currency" value="${item.retailPrice.amount * orderItem.quantity}" />
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                    </tr> 
                </c:forEach> 
                <tr class="totals">
                    <td colspan="5" style="text-align:right">
                        <c:url var="moveAllItemsToCartUrl" value="/wishlist/moveAllItemsToCart.htm">
                            <c:param name="wishlistName" value="${wishlist.name}"/>
                        </c:url>
                        <a class="cartLinkBtn" href="${moveAllItemsToCartUrl}">Move All Items To Cart</a>
                    </td>
                </tr>
                <tr> <td/> </tr>
                <tr class="totals">
                    <td colspan="5" style="text-align:right">
                        <a href="<c:url value="/store" />">Continue Shopping</a>
                    </td>
                </tr>
                </form:form>
            </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <br/>
            <h4>There's nothing in here!</h4>
            <b> <a href="../store" >Add Something to Your Cart</a></b>
        </c:otherwise>
    </c:choose>
</div>
</tiles:putAttribute>
</tiles:insertDefinition>