#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="mainContent">
	<h2 style="color: rgb(86, 111, 50);">You have successfully registered.</h2>
	<a href='<c:url value="/store"/>'>Continue Shopping</a>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />