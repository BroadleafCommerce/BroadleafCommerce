<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<sc:paginated-list paginationObject="${paginationObject}" 
				objectName="catItem" numAcross="4" 
				headerJsp="snippets/paginatedAjaxHeader.jsp"
				listId="catalogList">
	<sc:item catalogItem="${catItem}" layout="productList" itemName="item" />
</sc:paginated-list>