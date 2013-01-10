<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    
    <div class="splashContainer">
    <div class="dontMiss">
        <a href="/broadleafdemo/store/equipment/espresso?productId=180"><img src="/broadleafdemo/images/promos/mainPromo1.gif" /></a>
    </div>      
    <div class="sidePromoContainer">
        <div class="sidePromo">
            <a href="/broadleafdemo/store/equipment/cups"><img src="/broadleafdemo/images/promos/sidePromo1.jpg" /></a>
        </div>
        <div class="sidePromo">
            <a href="/broadleafdemo/store/coffee/starbucks?productId=123"><img src="/broadleafdemo/images/promos/sidePromo2.jpg" /></a>
        </div>
    </div>

    </tiles:putAttribute>
</tiles:insertDefinition>