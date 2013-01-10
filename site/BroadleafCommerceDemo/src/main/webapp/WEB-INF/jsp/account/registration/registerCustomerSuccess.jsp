<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
        <div class="mainContent">
            <h2 style="color: rgb(86, 111, 50);">You have successfully registered.</h2>
            <a href='<c:url value="/welcome.htm"/>'>Continue Shopping</a>
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>