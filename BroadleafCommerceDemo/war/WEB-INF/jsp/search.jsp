<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
<br/>
<div id="searchFilter">
	<form:form method="post" id="refineSearch" commandName="doSearch">
		<blc:skuFilter skus="${skus}" queryString="${queryString}"/>
		<input type="submit" value="Search"/>
	</form:form>
</div>
<div id="searchResults">
	<jsp:include page="searchAjax.jsp"/>
</div>
<div clear="both"> </div>

<script>
	$('.skuFilterCategories li').click(function() {
		$('#searchResults').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
		var postData = $('#refineSearch').serializeArray();
		postData.push({name:'ajax',value:'true'});
		postData.push({name:'categoryId',value:$(this).attr('value')});
		$('#searchResults').load($('#refineSearch').attr('action'), postData);
	} );
	
	$('#skuFilterPrice').bind('slidechange', function(event, ui) {
		$('#searchResults').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
		var postData = $('#refineSearch').serializeArray();
		postData.push({name:'ajax',value:'true'});
		$('#searchResults').load($('#refineSearch').attr('action'), postData);
	});
</script>

	</tiles:putAttribute>
</tiles:insertDefinition>
