<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:0 0 8px 8px;">
        <h3 class="pageTitle">My Account</h3>
        <span style="font-size:13px;"> <b>Order Management</b> </span><br/> 
        <a class="noTextUnderline" href="/broadleafdemo/orders/viewOrders.htm" > View Previous Orders </a> <br/>
        <a class="noTextUnderline" href="/broadleafdemo/orders/findOrder.htm" > Find Order </a> <br/><br/>
        <span style="font-size:13px;"> <b>Wishlist Management</b> </span><br/>      
        <c:choose>
            <c:when test="${customer.anonymous}">
                You must be logged in to create and edit wishlists.
            </c:when>
            <c:otherwise>
                <a class="noTextUnderline" href="/broadleafdemo/wishlist/showWishlists.htm" > View Wishlists</a><br/>
                <a class="noTextUnderline" href="/broadleafdemo/wishlist/createWishlistName.htm" > Create Wishlist</a>
            </c:otherwise>
        </c:choose>
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>