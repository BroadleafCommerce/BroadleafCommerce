<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
<script>
    function updateSearchFilterResults() {
        $('#mainContent').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
        var postData = $('#refineSearch').serializeArray();
        postData.push({name:'ajax',value:'true'});
        $('#mainContent').load($('#refineSearch').attr('action'), postData);
    }
</script>
<div class="catalogContainer columns mainContentAreaFull" style="padding:8px 0 8px 8px;">
    <div class="span-5" style="width:170px;margin-left:10px;">
    <form:form method="post" id="refineSearch" commandName="doSearch">
        <blc:searchFilter products="${products}" queryString="${queryString}">
            <blc:searchFilterItem property="defaultCategory.id" propertyDisplay="defaultCategory.name" displayTitle="Categories"/>
            <blc:searchFilterItem property="manufacturer" displayTitle="Manufacturers"/>
            <blc:searchFilterItem property="skus[0].salePrice" displayTitle="Prices" displayType="sliderRange"/>
        </blc:searchFilter>
        <input type="submit" value="Search"/>
    </form:form>
    </div>
    <div class="span-14" id="mainContent" style="padding:15px">
        <jsp:include page="searchAjax.jsp"/>
    </div>
</div>

    </tiles:putAttribute>
</tiles:insertDefinition>
