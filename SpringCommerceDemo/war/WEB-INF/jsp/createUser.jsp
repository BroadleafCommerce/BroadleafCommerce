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
				<h4 class="formSectionHeader">Register Now!</h4>
				<spring:hasBindErrors name="user">
					  <spring:bind path="user.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
				<tr>
						<td style="text-align:right"><label for="firstName">First Name:</b></label></td>
						<td><input size="30" class="userField" name="firstName" id="firstName" value="${user.firstName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="lastName">Last Name:</b></label></td>
					<td><input size="30" class="userField" type="user" name="lastName" id="lastName" value="${user.lastName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="username">User Name:</b></label></td>
					<td><input size="30" class="userField" type="user" name="username" id="username" value="${user.username}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">Password:</b></label></td>
					<td><input size="30" class="userField" type="password" name="password" id="password" value="${user.password}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="emailAddress">Email Address:</b></label></td>
					<td><input size="30" class="userField" type="emailAddress" name="emailAddress" id="emailAddress" value="${user.emailAddress}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="challengeQuestion">Challenge Question</b></label></td>
					<td><input size="30" class="userField" type="challengeQuestion" name="challengeQuestion" id="challengeQuestion" value="${user.challengeQuestion}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="challengeAnswer">Challenge Answer</b></label></td>
					<td><input size="30" class="userField" type="challengeAnswer" name="challengeAnswer" id="challengeAnswer" value="${user.challengeAnswer}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>
</div>