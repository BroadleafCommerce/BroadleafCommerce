<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>



<div id="banner">

	<a href="<c:url value="/createAddress.htm" />">Create Address</a>
	<a href="<c:url value="/listAddress.htm"/>">List Address</a>
	<a href="<c:url value="/createProduct.htm"/>">Create Product</a>
	<a href="<c:url value="/listProducts.htm"/>">List Product</a>
	<a href="<c:url value="/listSkus.htm"/>">List SKUS</a>
	<a href="<c:url value="/listOrders.htm"/>">List User Orders</a>
	<a href="<c:url value="/basket/listBasket.htm"/>">List Basket</a>
	<a href="<c:url value="/createCategory.htm"/>">Create Category</a>
	<a href="<c:url value="/listCategory.htm"/>">List Category</a>
	<a href="<c:url value="/passwordChange.htm"/>">Password Change</a>
	<a href="<c:url value="/search.htm"/>">Search</a>
	<a href="<c:url value="/searchIndex.htm"/>">Build Search Index</a>
	<a href="<c:url value="/logout"/>">Logout</a>
	<hr/>
</div>
