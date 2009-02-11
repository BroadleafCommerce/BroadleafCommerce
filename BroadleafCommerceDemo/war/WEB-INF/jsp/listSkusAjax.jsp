<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/broadleafcommerce"%>

<sc:paginated-list paginationObject="${paginationObject}"
				objectName="item" numAcross="1"
				headerJsp="snippets/paginatedAjaxHeader.jsp"
				listId="skuList">
			<h2><a href="<c:url value="/createSku.htm"><c:param name="skuId" value="${item.id}"/></c:url>">
				<c:out value="${item.product.name}"/>
			</a></h2>
			<p><c:out value="${item.product.description}"/></p>
			<p><b>Price: <c:out value="${item.price}"/></b></p>
			<p>
				<c:forEach items="${item.itemAttributes}" var="attrib">
					<b><c:out value="${attrib.key}"/></b>:<c:out value="${attrib.value}"/><br>
				</c:forEach>
			</p>
				<a href="<c:url value="/basket/addSku.htm"><c:param name="skuId" value="${item.id}"/></c:url>">Add to Basket
		</tr>
</sc:paginated-list>