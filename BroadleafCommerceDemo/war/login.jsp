<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Simple Form with Spring Security</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>" />
</head>
<body>
<div id="menu">
	<div id="side-bar">
	    <a href="<c:url value="/"/>">Home</a>
	    <a href="<c:url value="/registerCustomer.htm" />">New User</a>
	    <a href="<c:url value="/forgotPwd.htm" />">Forgot Password</a>
	</div>
</div>
<div id="content">
    <h1>Login Required</h1>

    <div class="section">
    	<c:if test="${not empty param.login_error}">
    		<p style="color:red;padding:0;margin:0;"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></p>
    	</c:if>
    </div>

    <div class="section">
    	<form name="f" action="<c:url value="/loginProcess" />" method="post">
			<table class="loginInfo">
				<tr>
					<td style="text-align:right"><label for="j_username">Username:</label></td>
					<td><input size="30" class="loginField" type="text" name="j_username" id="j_username" <c:if test="${not empty param.login_error}">value="${sessionScope["SPRING_SECURITY_LAST_USERNAME"]}"</c:if> /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="j_password">Password:</label></td>
					<td><input size="30" class="loginField" type="password" name="j_password" id="j_password" /></td>
	    		</tr>
	    		<tr>
		    		<td>&nbsp;</td>
		    		<td class="loginButton"><input class="loginButton" name="submit" id="submit" type="submit" value="Sign In" /></td>
	            </tr>
    		</table>
    	</form>
    </div>
</div>
<br />
<br />
<h5>Cheat Sheet</h5>
Username/Password/Roles:<br/>
rod/koala/ROLE_USER<br/>
</body>
</html>
