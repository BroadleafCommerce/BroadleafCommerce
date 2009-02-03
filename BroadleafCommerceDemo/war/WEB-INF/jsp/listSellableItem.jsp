<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
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

		</tiles:putAttribute>
</tiles:insertDefinition>