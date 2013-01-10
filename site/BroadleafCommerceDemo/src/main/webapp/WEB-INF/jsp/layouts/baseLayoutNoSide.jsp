<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
    <head>
        <title>Commerce Demo</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <tiles:insertAttribute name="headContent" />
    </head>
    <body>
        <div class="container">
            <tiles:insertAttribute name="navigation" />
            <tiles:insertAttribute name="mainContent" />
            <tiles:insertAttribute name="footer" />
        </div>
        <c:choose>
            <c:when test="${orderComplete}">
                <blc:googleAnalytics webPropertyId="UA-8476611-1" order="${order}" />
            </c:when>
            <c:otherwise>
                <blc:googleAnalytics webPropertyId="UA-8476611-1" />
            </c:otherwise>
        </c:choose>
    </body>
</html>