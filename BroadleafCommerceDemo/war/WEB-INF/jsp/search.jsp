<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
<br/>
<div id="searchFilter">
	<form:form method="post" id="refineSearch" commandName="doSearch">
		<blc:searchFilter products="${products}" queryString="${queryString}">
			<blc:searchFilterItem property="defaultCategory" propertyDisplay="name" propertyValue="id" displayTitle="Categories"/>
		</blc:searchFilter>
		<input type="submit" value="Search"/>
	</form:form>
</div>
<div id="searchResults">
	<jsp:include page="searchAjax.jsp"/>
</div>
<div clear="both"> </div>

<script>
	function updateSearchFilterResults() {
		$('#searchResults').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
		var postData = $('#refineSearch').serializeArray();
		postData.push({name:'ajax',value:'true'});
		$('#searchResults').load($('#refineSearch').attr('action'), postData);
	}
	
	$('#skuFilterPrice').bind('slidechange',  updateSearchFilterResults);
</script>

	</tiles:putAttribute>
</tiles:insertDefinition>
