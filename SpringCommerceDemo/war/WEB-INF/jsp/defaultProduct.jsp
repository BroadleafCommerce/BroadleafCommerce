<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<hr/>
	<h1>Default Product View For: <c:out value="${product.catalogItem.name}"/></h1>
	<sc:breadcrumb/>
	<br/>
	<table border="1">
		<tr>
			<th>Name</th>
			<th>Description</th>
		</tr>
		<tr>
			<td><c:out value="${product.catalogItem.name}"/></td>
			<td><c:out value="${product.catalogItem.description}"/></td>
		</tr>

	</table>

	<h2>Sellable Items</h2>

	<table border="1">
		<tr>
			<th>Name</th>
			<th>Price</th>
		</tr>
		<c:forEach var="item" items="${product.sellableItems}" varStatus="status">
			<tr>
				<td><c:out value="${item.name}"/></td>
				<td><c:out value="${item.price}"/></td>
			</tr>
		</c:forEach>

	</table>
</div>