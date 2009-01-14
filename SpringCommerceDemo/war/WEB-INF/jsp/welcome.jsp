<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<hr/>
	<a href="<c:url value="/createAddress.htm" />">Create Address</a>
	<a href="<c:url value="/listAddress.htm"/>">List Address</a>
	<a href="<c:url value="/passwordChange.htm"/>">Password Change</a>
	<a href="<c:url value="/logout"/>">Logout</a>
</div>
