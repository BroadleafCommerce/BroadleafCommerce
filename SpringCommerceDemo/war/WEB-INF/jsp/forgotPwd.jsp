<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>

<div id="banner">
	<hr/>
		<form:form method="post" commandName="user">
				<br />
				<h4 class="formSectionHeader">Forgot Password *required fields</h4>
				<spring:hasBindErrors name="user">
					  <spring:bind path="user.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
	    		<tr>
					<td style="text-align:right"><label for="emailAddress">Email Address *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="emailAddress" name="emailAddress" id="emailAddress" value="${user.emailAddress}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>
</div>