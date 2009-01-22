<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
				<h1>Manage Sellable Items</h1>

<style>
	.listItem { 
		float:left;
		background-color:#DDDDDD;
		margin: 5px;
		padding: 5px;
		width: 400px;
		}
</style>
<div id="listContainer">
<jsp:include page="listSellableItemAjax.jsp"/>
</div>

	<a href="<c:url value="/logout"/>">Logout</a>
</div>