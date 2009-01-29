<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<sc:paginated-list paginationObject="${paginationObject}" 
				objectName="item" numAcross="1" 
				headerJsp="snippets/paginatedAjaxHeader.jsp"
				listId="sellableItemList">
				<!--
			<h2><a href="<c:url value="/createSellableItem.htm"><c:param name="sellableItemId" value="${item.id}"/></c:url>">
				<c:out value="${item.catalogItem.name}"/>
			</a></h2>
			<p><c:out value="${item.catalogItem.description}"/></p>
			<p><b>Price: <c:out value="${item.price}"/></b></p>
			<p>
				<c:forEach items="${item.itemAttributes}" var="attrib">
					<c:out value="${attrib.key}"/>:<c:out value="${attrib.value}"/><br>
				</c:forEach>
			</p>
				<a href="<c:url value="/basket/addSellableItem.htm"><c:param name="sellableItemId" value="${item.id}"/></c:url>">Add to Basket
		</tr>
		-->
		<sc:item sellableItem="${item}" layout="productList" />
</sc:paginated-list>