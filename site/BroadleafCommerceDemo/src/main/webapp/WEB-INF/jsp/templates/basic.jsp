<jsp:root version="2.1" xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:tiles="http://tiles.apache.org/tags-tiles"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:spring="http://www.springframework.org/tags" xmlns:htmlx="/htmlx"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags/layout" xmlns:ent="/ent"
	xmlns:pagination="urn:jsptagdir:/WEB-INF/tags/pagination"
	xmlns:entpage="/entpage" xmlns:util="/util"
	xmlns:security="http://www.springframework.org/security/tags">
	<jsp:directive.page contentType="text/html; charset=UTF-8" />
	<tiles:insertDefinition name="baseLayout">
		<tiles:putAttribute name="breadCrumbs">
			<div id="breadcrumbs"><htmlx:a href="/">Home</htmlx:a> &amp;raquo; ${BLC_PAGE.pageFields.pageTitle.value }</div>
		</tiles:putAttribute>	
		<tiles:putAttribute name="headContentAdditional">
			<c:if test="${BLC_PAGE.metaKeywords ne null}">
				<meta name="keywords" content="${BLC_PAGE.metaKeywords}"/>
			</c:if>
			<c:if test="${BLC_PAGE.metaDescription ne null}">
				<meta name="description" content="${BLC_PAGE.metaDescription}"/>
			</c:if>
		</tiles:putAttribute>
		<tiles:putAttribute name="mainContent" type="string">
			<h1 class="tab-mid">${BLC_PAGE.pageFields.pageTitle.value}</h1>
			<div id="cmsContent">
				<c:if test="${BLC_PAGE ne null}">
					${BLC_PAGE.pageFields.content.value}
				</c:if>
			</div>
		</tiles:putAttribute>
	</tiles:insertDefinition>
</jsp:root>