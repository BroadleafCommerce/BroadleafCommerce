<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<sc:paginated-list paginationObject="${paginationObject}" 
				objectName="catItem" numAcross="4" 
				headerJsp="snippets/paginatedAjaxHeader.jsp"
				listId="catalogList">	
	<h2><a href="<c:url value="/createCatalogItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>"><c:out value="${catItem.name}"/></a></h2>
	<p><c:out value="${catItem.description}"/></p>
	<p><a href="<c:url value="/createSellableItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>">Create Sellable Item </a><br/>
		<a href="<c:url value="/listSellableItem.htm"><c:param name="catalogItemId" value="${catItem.id}"/></c:url>">List Sellable Item </a>
	</p>
</sc:paginated-list>