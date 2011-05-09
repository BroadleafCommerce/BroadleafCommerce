$(document).ready(function() {

	$("input[helpText]").each(function(){showHelpText(this);});
	$("input[helpText]").blur(function(){showHelpText(this);});
	$("input[helpText]").focus(function(){
		if ($(this).val() == $(this).attr("helpText")) {
			$(this).val("").removeClass("helpTextVisible");
		}
	});
	
});

function showHelpText(elem) {
	if ($(elem).val() == "") {
		$(elem).val($(elem).attr("helpText")).addClass("helpTextVisible");
	}
}