<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sc" uri="/springcommerce"%>

<jsp:include page="snippets/header.jsp"/>
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<c:set var="springCommerceRequestState" value="${requestScope['org.springcommerce.web.SpringCommerceRequestState']}" />
	<hr/>
	<h1>Default Category View For: <c:out value="${springCommerceRequestState.category.name}"/></h1>
	<h2>Here is a list of its sub-categories:</h2>

	<sc:breadcrumb/>
	<br/>
	<table border="1">
	<tr>
		<th>Name</th>
	</tr>
	<c:forEach var="item" items="${subCategories}" varStatus="status">
		<tr>
			<td><sc:categoryLink category="${item}"/></td>
		</tr>
	</c:forEach>

	</table>
