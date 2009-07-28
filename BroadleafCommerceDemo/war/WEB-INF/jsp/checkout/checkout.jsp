<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<h2 style="margin-bottom:10px;">Checkout</h1>
		<form:form method="post" modelAttribute="checkoutForm">
			<div class="columns"> 
				<jsp:include page="/WEB-INF/jsp/checkout/inputCheckoutPayment.jsp" />
				<jsp:include page="/WEB-INF/jsp/checkout/inputCheckoutContactInformation.jsp" />
			</div>
			<div class="formButtonFooter personFormButtons">
	     		<input type="submit" value="Submit Order">     
	        </div>
		</form:form>	
	</tiles:putAttribute>
</tiles:insertDefinition>