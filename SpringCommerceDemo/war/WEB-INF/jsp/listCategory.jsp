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
				<h1>Manage Categories</h1>

	<table border="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>URL Key</th>
		<th>URL</th>
		<th>Parent Category</th>
	</tr>
	<c:forEach var="item" items="${categoryList}" varStatus="status">
		<tr>
			<td><c:out value="${item.id}"/></td>
			<td><a href="<c:url value="/createCatalogItem.htm"><c:param name="categoryId" value="${item.id}"/></c:url>"><c:out value="${item.name}"/></td>
			<td><c:out value="${item.urlKey}"/></td>
			<td><c:out value="${item.url}"/></td>
			<td>
				<c:if test="${item.parentCategory != null}">
					<c:out value="${item.parentCategory.name}"/>
				</c:if>
			</td>
		</tr>
	</c:forEach>

	</table>

	<a href="<c:url value="/createCategory.htm"/>">Create Category
	<a href="<c:url value="/logout"/>">Logout</a>
</div>