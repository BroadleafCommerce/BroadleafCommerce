<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>Manage Products</h1>

	<div id="listContainer">
		<jsp:include page="listProductsAjax.jsp"/>
	</div>
	
	<a href="<c:url value="/createProduct.htm"/>">Create Product</a>
	<br/>
	<a href="<c:url value="/logout"/>">Logout</a>

	</tiles:putAttribute>
</tiles:insertDefinition>