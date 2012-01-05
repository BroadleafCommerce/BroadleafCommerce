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
      <title>Broadleaf Commerce - Administrative Application Forgot Username</title>
      
      <script language="javascript">
          function setSubmitUrl(form)
          {
            var hash = encodeURI(self.document.location.hash.substring(1));
            form.action = "${symbol_dollar}{pageContext.request.contextPath}/blcadmin/forgotPassword#" + hash;
            return true;
          }
      </script>
   </head>
   <body id="bg" class="en">
        <c:choose>
            <c:when test="${symbol_dollar}{errorCode == 'invalidUser'}">
                <div id="login-error">No matching username found.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'emailNotFound'}">
                <div id="login-error">The requested username does not have a valid email address.  Please contact an administrator.</div>
            </c:when>
            <c:when test="${symbol_dollar}{errorCode == 'inactiveUser'}">
                <div id="login-error">The username you entered is marked as inactive.   Please contact an administrator to activate your account.</div>
            </c:when>
            <c:when test="${symbol_dollar}{not empty param.errorCode}">
                <div id="login-error">Unknown error.</div>
            </c:when>
        </c:choose>


        <div id="content">
            <div id="logo"></div>
            <form class="login-form" onSubmit="return setSubmitUrl(this);" method="post">
                <table class="login">
                    <tr><td><label for="username_field" class="user">Username:</label><input id="username_field" class="input-username" name="username" type="text"></td></tr>
                    <tr><td><input id="submitButton" type="submit" value="Reset Password"></td></tr>
                </table>
            </form>
        </div>
        <div id="foot" class="forgot">
            <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/blcadmin/forgotUsername">Forgot username</a> -
            <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/blcadmin/login">Return to Login</a>
        </div>
   </body>
</html>
