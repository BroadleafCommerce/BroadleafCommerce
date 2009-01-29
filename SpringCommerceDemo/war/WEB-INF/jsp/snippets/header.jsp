<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<link rel="stylesheet" type="text/css" href="/scdemo/css/style.css" />

<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<br/><br/>
	<a href="<c:url value="/createAddress.htm" />">Create Address</a>
	<a href="<c:url value="/listAddress.htm"/>">List Address</a>
	<a href="<c:url value="/createCatalogItem.htm"/>">Create CatalogItem</a>
	<a href="<c:url value="/listCatalogItem.htm"/>">List CatalogItem</a>
	<a href="<c:url value="/listSellableItem.htm"/>">List SellableItems</a>
	<a href="<c:url value="/listOrders.htm"/>">List User Orders</a>
	<a href="<c:url value="/basket/listBasket.htm"/>">List Basket</a>
	<a href="<c:url value="/createCategory.htm"/>">Create Category</a>
	<a href="<c:url value="/listCategory.htm"/>">List Category</a>
	<a href="<c:url value="/passwordChange.htm"/>">Password Change</a>
	<a href="<c:url value="/logout"/>">Logout</a>
	<hr/>
</div>
