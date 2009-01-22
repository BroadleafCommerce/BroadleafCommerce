<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${paginationObject.showPrevious}">
<div class="prevLink">
	<c:url var="prevLink" value="${paginationObject.dataSource}">
		<c:param name="pageNumber" value="${paginationObject.pageNumber-1}"/>
	</c:url>
	<c:out value="${prevLink}"/>
	<a href='<c:out value="${prevLink}"/>'
	   onClick='$("#<c:out value="${paginationObject.containerId}"/>").load("<c:out value="${prevLink}"/>&ajaxRequest=true"); return false;'>
	   <c:out value="${paginationObject.previousLinkText}" escapeXml="false"/>
   </a>
</div>
</c:if>

<c:if test="${paginationObject.showNext}">
<div class="nextLink">
	<c:url var="nextLink" value="${paginationObject.dataSource}">
		<c:param name="pageNumber" value="${paginationObject.pageNumber+1}"/>
	</c:url>
	<a href='<c:out value="${prevLink}"/>'
	   onClick='$("#<c:out value="${paginationObject.containerId}"/>").load("<c:out value="${nextLink}"/>&ajaxRequest=true"); return false;'>
	   <c:out value="${paginationObject.nextLinkText}" escapeXml="false"/>
	</a> 
</div>
</c:if>