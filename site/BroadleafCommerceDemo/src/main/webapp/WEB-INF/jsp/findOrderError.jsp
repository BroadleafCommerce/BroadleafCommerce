<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
    
    <h3 class="pageTitle" ><b>Error</b></h3>

    The order information you specified is incorrect. Please try again. <br/><br/>

    <a href="findOrder.htm"> Back </a>

    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>