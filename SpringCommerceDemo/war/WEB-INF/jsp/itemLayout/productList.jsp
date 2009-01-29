<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<c:set var="springCommerceRequestState" value="${requestScope['org.springcommerce.web.SpringCommerceRequestState']}" />

<a href="<sc:categoryLink category="${springCommerceRequestState.category}"/>?productId=<c:out value="${item.id}"/>">
	<img src="<c:out value="${item.itemAttributes.imageUrl}"/>" border="0" alt="<c:out value="${item.name}"/>" title="<c:out value="${item.name}"/>"  height="125">
</a>
<p valign="bottom">
	<a href="<sc:categoryLink category="${springCommerceRequestState.category}"/>?productId=<c:out value="${item.id}"/>">
		<c:out value="${item.name}"/>
	</a>
</p>
