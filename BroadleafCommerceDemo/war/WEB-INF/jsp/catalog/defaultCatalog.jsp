<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

	<div class="breadcrumb">
		<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	</div>

    <div class="span-5 blueBorder">
      	Search Options
    </div>
    <div class="span-14 blueBorder">
        <jsp:include page="/WEB-INF/jsp/catalog/categoryView/mainContentFragment.jsp" />
    </div>
    <div class="span-4 blueBorder last">
     	<jsp:include page="/WEB-INF/jsp/catalog/promos/categoryPromos.jsp" />
    </div>

	</tiles:putAttribute>
</tiles:insertDefinition>
