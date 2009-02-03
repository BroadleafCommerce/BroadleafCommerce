<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

		<form:form method="post" commandName="user">
				<br />
				<h4 class="formSectionHeader">Register Now! *required fields</h4>
				<spring:hasBindErrors name="user">
					  <spring:bind path="user.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
				<tr>
						<td style="text-align:right"><label for="firstName">First Name *</b></label></td>
						<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="user" name="firstName" id="firstName" value="${user.firstName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="lastName">Last Name *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="user" name="lastName" id="lastName" value="${user.lastName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="username">User Name *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="user" name="username" id="username" value="${user.username}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">Password *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="password" name="password" id="password" value="${user.password}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">Confirm Password *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="password" name="passwordConfirm" id="passwordConfirm" value="${user.passwordConfirm}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="emailAddress">Email Address *</b></label></td>
					<td><input size="30" class="userField" style="background-color: rgb(255, 255, 160);" type="emailAddress" name="emailAddress" id="emailAddress" value="${user.emailAddress}" /></td>
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
	</tiles:putAttribute>
</tiles:insertDefinition>