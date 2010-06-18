<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tcs" uri="/container-store"%>

<%@ attribute name="items" required="true" type="java.lang.Object" description="Collection of objects to iterate over." %>
<%@ attribute name="top" required="true" description="Top level to render breadcrumb from." %>

<c:if test="${fn:length(items)<(top+1)}"><c:set var="top" value="0" /></c:if>

<c:forEach items="${items}" begin="${top}" var="category" varStatus="status">
	<c:if test="${status.first}">
		<tcs:catalogUrl category="${category}" var="catUrl" />
		<h2><a href="/${catUrl}">${category.name}</a></h2>
	</c:if>
	<c:choose>
		<c:when test="${!status.last}"><tcs:catalogUrl category="${category}" var="catUrl" /><a class="breadcrumb" href="/${catUrl}">${category.name}</a><span class="sep">&gt;</span></c:when>
		<c:when test="${!status.first}"><h1>${category.name}</h1></c:when>
	</c:choose>
	<c:choose>
		<c:when test="${status.first}">
			<c:set scope="request" var="textOnlyBreadcrumb" value="${category.name}" />
		</c:when>
		<c:otherwise>
			<c:set scope="request" var="textOnlyBreadcrumb" value="${textOnlyBreadcrumb} &gt; ${category.name}" />
		</c:otherwise>
	</c:choose>
</c:forEach>