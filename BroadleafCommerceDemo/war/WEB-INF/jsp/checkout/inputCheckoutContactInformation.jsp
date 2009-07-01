<%@ include file="/WEB-INF/jsp/include.jsp" %>
	<h4 class="formSectionHeader">Shipping Information</h4>
	<table class="formTable">
			<td style="text-align:right"><label for="contactInfo.name"><b>Name</b></label></td>
			<td><form:input path="firstName"/> <form:input path="lastName"/></td>
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
			<td><form:input path="emailAddress"/></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
			<td><form:input path="shippingAddress.addressLine1" /></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
			<td><form:input path="shippingAddress.addressLine2" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="city">City:</b></label></td>
			<td><form:input path="shippingAddress.city" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="state">State:</b></label></td>
			<td><form:input path="shippingAddress.state" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
			<td><form:input path="shippingAddress.postalCode" /></td>
   		</tr>
	</table> 

    	
