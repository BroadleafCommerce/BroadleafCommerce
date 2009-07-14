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
	function updateSearchFilterResults() {
		$('#searchResults').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
		var postData = $('#refineSearch').serializeArray();
		postData.push({name:'ajax',value:'true'});
		$('#searchResults').load($('#refineSearch').attr('action'), postData);
	}
	
	var categoriesChecked = 0;
	
	$('.skuFilterCategories li').click(function() {
		var categoryId = $(this).attr('value');
		var checkbox = $('.skuFilterCategoryCheckbox[value="'+categoryId+'"]');
		if (categoriesChecked == 0) {
			$('.skuFilterCategories li').each(function(){$(this).addClass('disabledCategory')});
			$(this).removeClass('disabledCategory');
			checkbox.attr('checked',true);
			categoriesChecked++;
		} else if (checkbox.attr('checked') == true) {
			$(this).addClass('disabledCategory');
			if (categoriesChecked == 1) {
				// unchecking the only checked category, so reactivate all categories
				$('.skuFilterCategories li').each(function(){$(this).removeClass('disabledCategory')});
			} 
			checkbox.attr('checked',false);
			categoriesChecked--;
		} else {
			$(this).removeClass('disabledCategory');
			checkbox.attr('checked',true);
			categoriesChecked++;
		}
		updateSearchFilterResults();
	} );
	
	$('#skuFilterPrice').bind('slidechange',  updateSearchFilterResults);
</script>

	</tiles:putAttribute>
</tiles:insertDefinition>
