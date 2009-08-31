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
	
	<div class="breadcrumb">
		<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	</div>
    <div class="span-5 blueBorder">
      	Search Options
  		<form:form method="post" id="refineSearch" commandName="doSearch">
			<blc:searchFilter products="${displayProducts}" queryString="">
				<blc:searchFilterItem property="manufacturer" displayTitle="Manufacturers"/>
				<blc:searchFilterItem property="skus[0].salePrice" displayTitle="Prices" displayType="sliderRange"/>
			</blc:searchFilter>
		</form:form>
    </div>
    <div class="span-14 blueBorder" id="mainContent">
        <jsp:include page="/WEB-INF/jsp/catalog/categoryView/mainContentFragment.jsp" />
    </div>
    <div class="span-4 blueBorder last">
     	<jsp:include page="/WEB-INF/jsp/catalog/promos/categoryPromos.jsp" />
    </div>

	</tiles:putAttribute>
</tiles:insertDefinition>
