<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ attribute name="itemsPerPage" required="true" description="Number of items displayed per page." %>
<%@ attribute name="totalItems" required="true" description="Total number of items in the collection." %>
<%@ attribute name="baseUrl" required="false" description="The base url for the next, previous and view all links - if needed." %>

<c:set var="productsPageSize" value="${itemsPerPage}" />
<c:set var="productsTotal" value="${totalItems}" />
<fmt:parseNumber var="fullPages" value="${productsTotal / productsPageSize}" type="number" integerOnly="true"/>
<c:set var="productsPages" value="${(productsTotal % productsPageSize) > 0 ? fullPages + 1 : fullPages}" />
<fmt:parseNumber var="parsedPage" value="${param.page}" type="number" integerOnly="true" />
<c:choose>
	<c:when test="${parsedPage < 1 || empty parsedPage}">
		<c:set var="currentPage" value="1" />
	</c:when>
	<c:when test="${parsedPage > productsPages}">
		<c:set var="currentPage" value="${productsPages}" />
	</c:when>
	<c:otherwise>
		<c:set var="currentPage" value="${parsedPage}" />
	</c:otherwise>
</c:choose>
	<c:set scope="request" var="pageBegin" value="${(currentPage-1)*productsPageSize}" />
<c:set var="pageMax" value="${(currentPage*productsPageSize)-1}" />
<c:choose>
	<c:when test="${pageMax > productsTotal}">
		<c:set scope="request" var="pageEnd" value="${productsTotal}" />
	</c:when>
	<c:otherwise>
		<c:set scope="request" var="pageEnd" value="${pageMax}" />
	</c:otherwise>
</c:choose>

<c:url var="previousUrl" value="${baseUrl}">
	<c:param name="page" value="${currentPage-1}"/>
</c:url>
<c:url var="nextUrl" value="${baseUrl}">
	<c:param name="page" value="${currentPage+1}"/>
</c:url>
<c:url var="viewAllUrl" value="${baseUrl}">
	<c:param name="viewAll" value="true"/>
</c:url>

<c:choose>
	<c:when test="${param.viewAll || productsPages < 2}">
		<!-- No Pagination -->
		<c:set scope="request" var="pageBegin" value="0" />
		<c:set scope="request" var="pageEnd" value="${productsTotal}" />
	</c:when>
	<c:when test="${currentPage == 1}">
		<span class="leftNoArrow"></span><span class="currentPage">${currentPage} of ${productsPages}</span><a href="${nextUrl}" class="rightArrow"></a><a href="${viewAllUrl}" class="viewAll">View All</a>
	</c:when>
	<c:when test="${currentPage == productsPages}">
		<a href="${previousUrl}" class="leftArrow"></a><span class="currentPage">${currentPage} of ${productsPages}</span><span class="rightNoArrow"></span><a href="${viewAllUrl}" class="viewAll">View All</a>
	</c:when>
	<c:otherwise>
		<a href="${previousUrl}" class="leftArrow"></a><span class="currentPage">${currentPage} of ${productsPages}</span><a href="${nextUrl}" class="rightArrow"></a><a href="${viewAllUrl}" class="viewAll">View All</a>
	</c:otherwise>
</c:choose>