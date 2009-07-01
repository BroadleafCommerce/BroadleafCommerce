<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">


	<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	<h2>${currentCategory.name}</h2>

    <div class="span-6 blueBorder">
      	Search Options
    </div>
    <div class="span-13 blueBorder">
        <jsp:include page="/WEB-INF/jsp/catalog/categoryView/mainContentFragment.jsp" />
    </div>
    <div class="span-4 blueBorder last">
     	<jsp:include page="/WEB-INF/jsp/catalog/promos/categoryPromos.jsp" />
    </div>

	</tiles:putAttribute>
</tiles:insertDefinition>
