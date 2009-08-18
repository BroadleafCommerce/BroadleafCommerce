<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<form:form action="createWishlist.htm" modelAttribute="wishlistRequest">
			Wishlist Name: <form:input path="wishlistName" />
			<form:hidden path="addProductId" />
			<form:hidden path="addCategoryId" />
			<form:hidden path="addSkuId" />
			<form:hidden path="quantity"  />
			<input  type="submit" value="Create Wishlist" name="createWishlist" />
		</form:form>
	</tiles:putAttribute>
</tiles:insertDefinition>