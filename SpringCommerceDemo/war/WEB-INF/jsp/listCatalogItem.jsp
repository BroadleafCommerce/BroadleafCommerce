<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="sc" uri="/springcommerce"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<script url="http://ajax.googleapis.com/ajax/libs/jquery/1.3.0/jquery.min.js">
</script>
<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<hr/>
				<h1>Manage Catalog Items</h1>
	
	<style>
		.listItem { 
			float:left;
			background-color:#DDDDDD;
			margin: 5px;
			padding: 5px;
			width: 400px;
			}
	</style>
	<sc:paginated-list paginationObject="${paginationObject}" 
					objectName="catItem" numAcross="2" 
					headerJsp="snippets/paginatedAjaxHeader.jsp"
					listId="catalogList">
		<h2><a href="<c:url value="/createCatalogItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>"><c:out value="${catItem.name}"/></a></h2>
		<p><c:out value="${catItem.description}"/></p>
		<p><a href="<c:url value="/createSellableItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>">Create Sellable Item </a><br/>
			<a href="<c:url value="/listSellableItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>">List Sellable Item </a>
		</p>
	</sc:paginated-list>

	<a href="<c:url value="/createCatalogItem.htm"/>">Create CatalogItem
	<br/>
	<a href="<c:url value="/logout"/>">Logout</a>
</div>