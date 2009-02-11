<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${paginationObject.showPrevious}">
<a href='<c:url value="/listProducts.htm"><c:param name="pageNumber" value="${paginationObject.pageNumber-1}"/></c:url>'>&lt;- Previous</a> 
</c:if>

<c:if test="${paginationObject.showNext}">
<a href='<c:url value="/listProducts.htm"><c:param name="pageNumber" value="${paginationObject.pageNumber+1}"/></c:url>'>Next -&gt;</a> 
</c:if>