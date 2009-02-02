<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>Manage Catalog Items</h1>

	<div id="listContainer">
		<jsp:include page="listCatalogItemAjax.jsp"/>
	</div>
	
	<a href="<c:url value="/createCatalogItem.htm"/>">Create CatalogItem</a>
	<br/>
	<a href="<c:url value="/logout"/>">Logout</a>

	</tiles:putAttribute>
</tiles:insertDefinition>