#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
      
      <script language="javascript">
      function centerObj()
      {
          var height = 170;
          var width = 450;
          height = height/2;
          height = String("-" + height + "px");
          width = width/2;
          width = String("-" + width + "px");
       
          var container = document.getElementById('login');
          container.style.marginTop;
          container.style.position="absolute";
          container.style.top = "50%";
          container.style.left="50%";    
          container.style.marginTop=height;/* half elements height*/
          container.style.marginLeft=width;/* half elements width*/
      }
      
      function setSubmitUrl(form)
      {
        var hash = unescape(self.document.location.hash.substring(1));
        form.action = "admin/login_admin_post${symbol_pound}" + hash;
        return true;
      }
      </script>
   </head>
   <body onload="centerObj()">
        <form onSubmit="return setSubmitUrl(this);" method="post">
        <div style="position:absolute; left: 0px; top: 0px; width: 450px; height: 170px; background-image: url(${package}.gwt.mycompanyAdmin/admin/images/admin_login.jpg)" id="login">
            <div style="position: relative; left: 200px; top: 40px">
                    <c:if test="${symbol_dollar}{not empty param.login_error}">
                        <p style="font-family:'Arial', Arial, sans-serif; font-style: normal; font-size: 12px; color: red"><c:out value="${symbol_dollar}{SPRING_SECURITY_LAST_EXCEPTION.message}"/></p>
                    </c:if>
                    <table style="font-family:'Arial', Arial, sans-serif; font-style: normal; font-size: 12px">
                    <tr><td><label for="j_username">Username:</label></td><td><input type="text" name="j_username" id="j_username" style="width: 120px"></td></tr>
                    <tr><td><label for="j_password">Password:</label></td><td><input type="password" name="j_password" id="j_password" style="width: 120px"></td></tr>
                    <tr><td><label for="_spring_security_remember_me">Remember Me:</label></td><td><input type="checkbox" name="_spring_security_remember_me" id="_spring_security_remember_me" /></td></tr>
                    </table>
                    <input type="submit" name="submit" value="Sign In">
            </div>
        </div>
        </form>
   </body>
</html>