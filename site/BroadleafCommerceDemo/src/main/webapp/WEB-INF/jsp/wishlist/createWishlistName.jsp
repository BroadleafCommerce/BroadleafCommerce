<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
        <form:form action="createWishlist.htm" modelAttribute="wishlistRequest">
            <form:hidden path="addProductId" />
            <form:hidden path="addCategoryId" />
            <form:hidden path="addSkuId" />
            <form:hidden path="quantity"  />
            <div class="columns">
                <div class="column span-9">
                    <h3 class="pageTitle">Create a Wishlist</h3>
                    <table class="basicTable">
                        <tr>
                            <td nowrap>Wishlist Name</td>
                            <td><form:input path="wishlistName"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><button type="submit" name="createWishlist" value="Create Wishlist">Create Wishlist</button></td>
                        </tr>
                    </table>
                </div>
            </div>
        </form:form>
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>