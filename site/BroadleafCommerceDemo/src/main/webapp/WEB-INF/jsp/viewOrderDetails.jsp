<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContent container">
        <h3 class="pageTitle" ><b>Order Details</b></h3>
        <jsp:include page="/WEB-INF/jsp/orderSnippet.jsp" />
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>