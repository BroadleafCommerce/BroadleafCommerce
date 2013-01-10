<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
        <div class="columns" >
            <div class="column span-5" >
                <h3 class="pageTitle">Wishlists</h3>
                <table class="basicTable">
                <c:forEach var="wishlist" items="${wishlists}" >
                    <tr>
                        <td nowrap><strong><li>${wishlist.name}</li></strong></td>
                        <td><a href="viewWishlist.htm?id=${wishlist.id}">View</a></td>
                        <td><a href="removeWishlist.htm?wishlistName=${wishlist.name}">Remove</a></td>
                    </tr>
                </c:forEach>
                </table>
                <a href="<c:url value="/wishlist/createWishlistName.htm" />">Create New Wishlist</a>
            </div>
        </div>
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>