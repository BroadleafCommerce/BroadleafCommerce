<%@ include file="/WEB-INF/jsp/include.jsp" %>
	<table class="formTable">
		<tr>
			<td style="text-align:right"><label for="contactInfo.name"><b>Name</b></label></td>
			<td><form:input path="customer.firstName"/> <form:input path="customer.lastName"/></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="contactInfo.primaryPhone"><b>Primary Phone Number</b></label></td>
			<td><form:input path="primaryPhoneNumber"/></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine1">Secondary Phone Number</b></label></td>
			<td><form:input path="secondaryPhoneNumber"/></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="contactInfo.email">Email Address</b></label></td>
			<td><form:input path="customer.emailAddress"/></td>
   		</tr>
	</table> 

    	
