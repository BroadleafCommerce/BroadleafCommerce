<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
		<form:form method="post" modelAttribute="registerCustomerForm">
				<br />
				<h4 class="formSectionHeader">Register Now! *required fields</h4>
				<spring:hasBindErrors name="registerCustomerForm">
					  <spring:bind path="registerCustomerForm.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="firstName">First Name *</b></label></td>
					<td><form:input path="firstName" size="30" cssClass="userField" cssStyle="background-color: rgb(255, 255, 160);" id="firstName" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="lastName">Last Name *</b></label></td>
					<td><form:input path="lastName" size="30" cssClass="userField" cssStyle="background-color: rgb(255, 255, 160);" id="lastName" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">Password *</b></label></td>
					<td>
						<form:password path="password" size="12" maxlength="12" />
					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">Confirm Password *</b></label></td>
					<td>
						<form:password path="passwordConfirm" size="12" maxlength="12"/>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="challengeQuestion">Challenge Question</b></label></td>
					<td><form:select id="challengeQuestions" path="challengeQuestionId">
          				<form:options items="${challengeQuestions}" itemLabel="question" itemValue="id"/>
       				</form:select> </td>				
	    		</tr>
	    	    <tr>
					<td style="text-align:right"><label for="challengeAnswer">Challenge Answer</b></label></td>
					<td><form:input path="unencodedChallengeAnswer" size="30" cssClass="userField"  id="challengeAnswer" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="emailAddress">Email Address *</b></label></td>
					<td><form:input path="emailAddress" size="30" cssClass="userField" cssStyle="background-color: rgb(255, 255, 160);" id="emailAddress" /></td>
	    		</tr>
			  	<tr>
    	  			<td align="right" colspan="2">
    	    			<a href="/broadleafdemo/store">Cancel</a>
    	    			<div class="formButtonFooter personFormButtons">
							<input type="submit" class="saveButton" value="Save Changes" name="submitAction"/>
						</div>
          			</td>
        		</tr>
    		</table>
		</form:form>
	</tiles:putAttribute>
</tiles:insertDefinition>