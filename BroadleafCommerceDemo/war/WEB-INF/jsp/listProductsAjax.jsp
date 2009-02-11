<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/broadleafcommerce"%>

<sc:paginated-list paginationObject="${paginationObject}"
				objectName="product" numAcross="4"
				headerJsp="snippets/paginatedAjaxHeader.jsp"
				listId="catalogList">
	<h2><a href="<c:url value="/createProduct.htm"><c:param name="productId" value="${product.id}"/></c:url>"><c:out value="${product.name}"/></a></h2>
	<p><c:out value="${product.description}"/></p>
	<p><a href="<c:url value="/createSku.htm"><c:param name="productId" value="${catItem.id}"/></c:url>">Create SKU </a><br/>
		<a href="<c:url value="/listSku.htm"><c:param name="productId" value="${catItem.id}"/></c:url>">List SKUs </a>
	</p>
</sc:paginated-list>