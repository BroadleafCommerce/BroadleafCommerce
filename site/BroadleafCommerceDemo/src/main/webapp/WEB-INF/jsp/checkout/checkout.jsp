<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
        <div class="mainContent">
        <h3 style="margin:8px 0;font-weight:bold;">Checkout</h3>
        <form:form method="post" modelAttribute="checkoutForm">
            <div class="columns"> 
                <jsp:include page="/WEB-INF/jsp/checkout/inputCheckoutPayment.jsp" />
                <jsp:include page="/WEB-INF/jsp/checkout/inputCheckoutContactInformation.jsp" />
            </div>
            <div class="formButtonFooter personFormButtons">
                <button type="submit" id="checkout" name="checkout">Submit Order</button>     
            </div>
        </form:form>    
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>