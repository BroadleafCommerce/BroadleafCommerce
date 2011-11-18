<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
      <meta http-equiv="Content-Language" content="en-us" />
      <meta http-equiv="Content-Author" content="Broadleaf Commerce" />
      <title>Broadleaf Commerce - Administrative Application</title>

       <style type="text/css">
           #login{
               position: absolute;
               top: 50%;
               left: 50%;
               margin-top: -85px;
               margin-left: -225px;
               background-image: url(org.broadleafcommerce.admin.demoAdmin/admin/images/admin_login.jpg);
               width: 450px;
               height: 170px;
           }

           #error{
               position: absolute;
               top: 50%;
               left: 50%;
               margin-top: -70px;
               margin-left: 230px;
               width: 300px;
               height: 140px;
               font-family:'Arial', Arial, sans-serif;
               font-style: normal;
               font-size: 12px;
               color: #ff0000;
           }

           .definitiveError {
               font-weight: bold;
           }

           #innerForm{
               position: relative;
               left: 200px;
               top: 40px;
           }

           .field {
               width: 120px;
           }

           table {
               font-family:'Arial', Arial, sans-serif;
               font-style: normal;
               font-size: 12px;
           }
       </style>
      
      <script language="javascript">
          function setSubmitUrl(form)
          {
            var hash = unescape(self.document.location.hash.substring(1));
            form.action = "admin/login_admin_post#" + hash;
            return true;
          }
      </script>
   </head>
   <body>
   		<form onSubmit="return setSubmitUrl(this);" method="post">
   		<div id="login">
 			<div id="innerForm">
 					<table>
 					<tr><td><label for="j_username">Username:</label></td><td><input type="text" name="j_username" id="j_username" class="field"></td></tr>
 					<tr><td><label for="j_password">Password:</label></td><td><input type="password" name="j_password" id="j_password" class="field"></td></tr>
 					<tr><td><label for="_spring_security_remember_me">Remember Me:</label></td><td><input type="checkbox" name="_spring_security_remember_me" id="_spring_security_remember_me" /></td></tr>
 					</table>
 					<input type="submit" name="submit" value="Sign In">
 			</div>
 		</div>
        <div id="error">
            <c:if test="${not empty param.login_error}">
                Unable to login:<br><br><span class="definitiveError"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></span>
            </c:if>
        </div>
 		</form>
   </body>
</html>