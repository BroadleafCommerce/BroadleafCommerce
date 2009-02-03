<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

<div id="banner">
	<hr/>
		<form:form method="post" commandName="passwordChange">
				<br />
				<h4 class="formSectionHeader">Please tell us your password challenge answer</h4>
				<spring:hasBindErrors name="passwordChange">
					  <spring:bind path="passwordChange.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
	    		<tr>
					<td style="text-align:right"><label for="challengeQuestion"><c:out value="${passwordChange.challengeQuestion }"/></b></label></td>
					<td><input type="text" size="30" class="userField" style="background-color: rgb(255, 255, 160);" name="challengeAnswer" id="challengeAnswer" value="${passwordChange.challengeAnswer}"/></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
    			<c:set var="email" value="${email}" scope="request" />
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
		</form:form>
</div>

	</tiles:putAttribute>
</tiles:insertDefinition>