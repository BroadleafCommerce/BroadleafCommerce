<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	<h3 class="pageTitle" ><b>Order Details</b></h3>

	<jsp:include page="/WEB-INF/jsp/orderSnippet.jsp" />

	</tiles:putAttribute>
</tiles:insertDefinition>