<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<h3 style="margin:8px 0;font-weight:bold;">Wishlists</h3>
		<div class="columns span-16" >
			<div class="column span-5" >
				<ul>
					<c:forEach var="wishlist" items="${wishlists}" >
						<li><a href="viewWishlist.htm?id=${wishlist.id}">${wishlist.name}</a></li>
					</c:forEach>
				</ul>
				<a href="<c:url value="/wishlist/createWishlistName.htm" />">Create New Wishlist</a>
			</div>
			<div class="column span-3" >		
				<c:forEach var="wishlist" items="${wishlists}" >
					<a href="removeWishlist.htm?wishlistName=${wishlist.name}">Remove</a><br/>
				</c:forEach>
			</div>
		</div>
	</tiles:putAttribute>
</tiles:insertDefinition>