<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<a href="<c:url value="/catalog/foo"><c:param name="productId" value="${item.id}"/></c:url>">  
	<img src="<c:out value="${item.itemAttributes.imageUrl}"/>" border="0" alt="<c:out value="${item.name}"/>" title="<c:out value="${item.name}"/>"  height="125">
</a>
<p valign="bottom">
	<a href="<c:url value="/catalog/foo"><c:param name="productId" value="${item.id}"/></c:url>">
		<c:out value="${item.name}"/>
	</a>
</p>
