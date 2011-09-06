<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="headContentAdditional">
		<script language="javascript" src="/broadleafdemo/org.broadleafcommerce.admin.demoAdmin/admin/tinymce/tiny_mce_popup.js"></script>
		<script type="text/javascript" src="/broadleafdemo/org.broadleafcommerce.admin.demoAdmin/admin/tinymce/plugins/preview/jscripts/embed.js"></script>
		<script type="text/javascript">
		tinyMCEPopup.onInit.add(function(ed) {
			var dom = tinyMCEPopup.dom;
		
			// Load editor content_css
		 	//tinymce.each(ed.settings.content_css.split(','), function(u) { 
			//	dom.loadCSS(ed.documentBaseURI.toAbsolute(u));
			//});

			// Place contents inside div container
			dom.setHTML('cmsContent', ed.getContent());
		});
		</script>
	</tiles:putAttribute>
	<tiles:putAttribute name="mainContent" type="string">
	<div class="mainContentAreaFull" style="padding:8px;">
	<div id="cmsContent">
	
	</div>
	</div>
	</tiles:putAttribute>
</tiles:insertDefinition>