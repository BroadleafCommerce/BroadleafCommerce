<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	<h3 class="pageTitle" ><b>Checkout Confirmation</b></h3>

	<jsp:include page="/WEB-INF/jsp/orderSnippet.jsp" />
	<c:set scope="request" var="orderComplete" value="true" />

	</tiles:putAttribute>
</tiles:insertDefinition>