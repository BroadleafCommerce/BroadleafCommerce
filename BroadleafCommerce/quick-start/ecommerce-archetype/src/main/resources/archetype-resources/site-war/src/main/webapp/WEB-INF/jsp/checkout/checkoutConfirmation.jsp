#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="mainContent">
	<h3 class="pageTitle" ><b>Checkout Confirmation</b></h3>
	<jsp:include page="/WEB-INF/jsp/orderSnippet.jsp" />
</div>
<c:set scope="request" var="orderComplete" value="true" />
<jsp:include page="/WEB-INF/jsp/footer.jsp" />