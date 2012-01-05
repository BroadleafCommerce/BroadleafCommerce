#set( $symbol_dollar = '$' )
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">


<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
      <link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/admin/admin_login.css" />
      <title>Broadleaf Commerce - Administrative Application Login</title>
   </head>
   <body id="bg" class="en">
        <c:choose>
            <c:when test="${symbol_dollar}{errorCode == 'notFound'}">
                <div id="login-error">No matching user found.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'inactiveUser'}">
                <div id="login-error">The username associated with the email you entered is marked as inactive.   Please contact an administrator to activate the account.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'invalidToken' || errorCode == 'tokenExpired'}">
                <div id="login-error">The token entered is invalid or expired.     To request a new token, choose the "Forgot Password" link below.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'tokenUsed'}">
                <div id="login-error">The token entered has already been processed.   No new updates were made.   To request a new token, choose the "Forgot Password" link below.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'inactiveUser'}">
                <div id="login-error">The username associated with the email you entered is marked as inactive.   Please contact an administrator to activate the account.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'invalidPassword'}">
                <div id="login-error">Please enter a valid password.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'passwordMismatch'}">
                <div id="login-error">The password and confirm password fields did not match.</div>
            </c:when>
            <c:when test="${symbol_dollar}{param.messageCode == 'passwordTokenSent'}">
                <div id="login-message">A reset password email was sent to the address associated with your account.  Enter the token from your email in the form below to reset your password.</div>
            </c:when>
        </c:choose>

        <div id="content">
            <div id="logo"></div>
            <form:form cssClass="login-form" modelAttribute="resetPasswordForm" method="post">
                <table class="login">
                    <tr><td><label for="username_field" class="user">Username:</label><form:input id="username_field" cssClass="input-username" path="username"/></td></tr>
                    <tr><td><label for="token_field" class="user">Token:</label><form:input id="token_field" cssClass="input-token" path="token"/></td></tr>
                    <tr><td><label for="password_field" class="pass">New Password:</label><form:password id="password_field" cssClass="input-pass" path="password"/></td></tr>
                    <tr><td><label for="confirm_password_field" class="pass">Verify New Password:</label><form:password id="confirm_password_field" cssClass="input-pass" path="confirmPassword"/></td></tr>
                    <tr id="LoginBtn"><td><input id="submitButton" type="submit" value="Reset Password"></td></tr>
                </table>
            </form:form>
        </div>

        <div id="foot" class="forgot">
            <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/blcadmin/login">Login</a> -
            <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/blcadmin/forgotUsername">Forgot username</a> -
            <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/blcadmin/forgotPassword">Forgot password</a>
        </div>
   </body>
</html>
