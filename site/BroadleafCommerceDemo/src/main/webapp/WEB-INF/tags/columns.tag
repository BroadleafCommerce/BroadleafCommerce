<%@ attribute name="cssClass" description="Class name that will be applied to columns container element." %>
<div class="columns ${cssClass}">
	<jsp:doBody />
	<div class="clearColumns"></div>
</div>