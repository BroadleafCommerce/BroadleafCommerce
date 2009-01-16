<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<hr/>
				<h1>Manage Sellable Items</h1>

	<table border="1">
	<tr>
		<th>Name</th>
		<th>Description</th>
		<th>Item Attributes</th>
	</tr>
	<c:forEach var="item" items="${sellableItemList}" varStatus="status">
		<tr>
			<td><a href="<c:url value="/createSellableItem.htm"><c:param name="sellableItemId" value="${item.id}"/></c:url>"><c:out value="${item.catalogItem.name}"/></td>
			<td><c:out value="${item.catalogItem.description}"/></td>
			<td>
				<c:forEach var="attribute" items="${item.itemAttributes}">
					<c:out value="${attribute.name}"/>:<c:out value="${attribute.value}"/><br>
				</c:forEach>
			</td>
		</tr>
	</c:forEach>

	</table>

	<a href="<c:url value="/logout"/>">Logout</a>
</div>