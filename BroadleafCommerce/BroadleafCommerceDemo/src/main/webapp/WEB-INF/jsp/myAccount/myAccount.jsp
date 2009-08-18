<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<h3 class="pageTitle" ><b>My Account</b></h3>
		<span style="font-size:13px;"> <b>Order Management</b> </span><br/>	
		<a class="noTextUnderline" href="/broadleafdemo/orders/viewOrders.htm" > View Previous Orders </a> <br/>
		<a class="noTextUnderline" href="/broadleafdemo/orders/findOrder.htm" > Find Order </a> <br/><br/>
		<span style="font-size:13px;"> <b>Wishlist Management</b> </span><br/>		
		<a class="noTextUnderline" href="/broadleafdemo/wishlist/showWishlists.htm" > View Wishlists</a><br/>
		<a class="noTextUnderline" href="/broadleafdemo/wishlist/createWishlistName.htm" > Create Wishlist</a>
	</tiles:putAttribute>
</tiles:insertDefinition>