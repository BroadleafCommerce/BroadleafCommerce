#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="mainContentAreaFull" style="padding:8px;">
<div id="cmsContent">
    <c:if test="${symbol_dollar}{BLC_PAGE_DATA ne null}">
        ${symbol_dollar}{BLC_PAGE_DATA.body}
    </c:if>
</div>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />

