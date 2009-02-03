<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>Commerce Demo</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<tiles:insertAttribute name="headContent" />
	</head>

	<body>
		<div class="bodyContent">
			<tiles:insertAttribute name="navigation" />
			<tiles:insertAttribute name="mainContent" />
			<div class="clearBoth"></div>
			<tiles:insertAttribute name="footer" />
		</div>
	</body>
</html>