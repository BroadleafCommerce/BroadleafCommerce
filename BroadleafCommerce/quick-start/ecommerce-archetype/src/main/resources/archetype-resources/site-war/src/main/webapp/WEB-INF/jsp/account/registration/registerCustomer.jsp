#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="mainContentAreaFull clearfix" style="padding:0 0 8px 8px;">
	<form:form method="post" modelAttribute="registerCustomerForm">
		<spring:hasBindErrors name="registerCustomerForm">
			  <spring:bind path="registerCustomerForm.*">
        			<c:forEach var="error" items="${symbol_dollar}{status.errorMessages}">
          				<tr><td><font color="red"><c:out value="${symbol_dollar}{error}"/></font></td></tr><br />
        			</c:forEach>
        	  </spring:bind>
        </spring:hasBindErrors>
		
		<div class="orderBorder span-10" style="margin-top:10px;" >
			<div class="orderTitle" > <b>Register Now! </b></div>
			<table class="formTable">
				<tr>
					<td class="alignCenter" colspan="2">
						 * <b>All fields are required. </b>
					</td>
				<tr>
					<td style="text-align:right"><label for="username">* Username</label></td>
					<td><form:input path="customer.username" size="30" cssClass="userField" id="username" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="firstName">* First Name</label></td>
					<td><form:input path="customer.firstName" size="30" cssClass="userField" id="firstName" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="lastName">* Last Name</label></td>
					<td><form:input path="customer.lastName" size="30" cssClass="userField" id="lastName" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">* Password</label></td>
					<td>
						<form:password path="password" size="12" maxlength="12" />
					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="password">* Confirm Password</label></td>
					<td>
						<form:password path="passwordConfirm" size="12" maxlength="12"/>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="challengeQuestion">* Challenge Question</b></label></td>
					<td><form:select id="challengeQuestions" path="customer.challengeQuestion">
          				<form:options items="${symbol_dollar}{challengeQuestions}" itemLabel="question" itemValue="id"/>
       				</form:select> </td>				
	    		</tr>
	    	    <tr>
					<td style="text-align:right"><label for="challengeAnswer">* Challenge Answer</b></label></td>
					<td><form:input path="customer.unencodedChallengeAnswer" size="30" cssClass="userField"  id="challengeAnswer" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="emailAddress">* Email Address</label></td>
					<td><form:input path="customer.emailAddress" size="30" cssClass="userField" id="emailAddress" /></td>
	    		</tr>
			  	<tr>
    	  			<td class="alignCenter" colspan="2">
						<button type="submit" value="Register">Register</button>
          			</td>
        		</tr>
    		</table>
		</div>
	</form:form>
	<form:form method="post" action="/mycompany/login_post.htm">
		<div class="orderBorder span-10" style="margin-top:10px;" >
			<div class="orderTitle" > <b>Login for Existing users</b></div>
			<table class="formTable">
			<c:if test="${symbol_dollar}{param.error eq 'true'}">
				<tr>
					<td colspan="2"><span style="color:red">Your login credentials were incorrect</span></td>
				</tr>
			</c:if>
				<tr>
					<td style="text-align:right"><label for="username">Username</label></td>
					<td><input size="30" cssClass="userField" id="username" name="j_username" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="password">Password</label></td>
					<td><input size="30" type="password" cssClass="userField" id="password" name="j_password" /></td>
	    		</tr>
	    		<tr>
	    			<td style="text-align:right"><label for="_spring_security_remember_me">Remember Me:</label></td>
	    			<td><input type="checkbox" name="_spring_security_remember_me" id="_spring_security_remember_me" /></td>
	    		</tr>
			  	<tr>
    	  			<td class="alignCenter" colspan="2">
						<button type="submit" value="Login">Login</button>
          			</td>
        		</tr>
    		</table>
		</div>
	</form:form>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />