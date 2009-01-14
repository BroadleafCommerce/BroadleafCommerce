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
				<h1>Manage address</h1>
				
	<table border="1">
	<tr>
		<th>Name</th>
		<th>Address Line 1</th>
		<th>Address Line 2</th>
		<th>City</th>
		<th>State</th>
		<th>ZipCode</th>
	</tr>
	<c:forEach var="item" items="${addressList}" varStatus="status">
		<tr>
			<td><a href="<c:url value="/createAddress.htm"><c:param name="addressId" value="${item.id}"/></c:url>"><c:out value="${item.addressName}"/></td>
			<td><c:out value="${item.addressLine1}"/></td>
			<td><c:out value="${item.addressLine2}"/></td>
			<td><c:out value="${item.city}"/></td>
			<td><c:out value="${item.stateCode}"/></td>
			<td><c:out value="${item.zipCode}"/></td>
		</tr>
	</c:forEach>
	
	</table>
				
	<a href="<c:url value="/createAddress.htm"/>">Create Address</a>
	<a href="<c:url value="/logout"/>">Logout</a>
</div>