<%@ include file="/WEB-INF/jsp/include.jsp" %>
	<h4 class="formSectionHeader">Shipping Information</h4>
	<table class="formTable">
		<tr>
			<td style="text-align:right"><label for="contactInfo.firstName"><b>First Name</b></label></td>
			<td colspan="2">
				<form:input path="shippingAddress.firstName"/> 
				<form:errors path="shippingAddress.firstName" cssClass="errorInputText"/>
			</td>
		</tr>
		<tr>
			<td style="text-align:right"><label for="contactInfo.lastName"><b>Last Name</b></label></td>
			<td colspan="2">
			 	<form:input path="shippingAddress.lastName"/>
				<form:errors path="shippingAddress.lastName" cssClass="errorInputText"/>
			</td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="contactInfo.primaryPhone"><b>Primary Phone Number</b></label></td>
			<td colspan="2"> 
				<form:input path="shippingAddress.primaryPhone"/>
				<form:errors path="shippingAddress.primaryPhone" cssClass="errorInputText"/>
			</td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine1">Secondary Phone Number</b></label></td>
			<td colspan="2">
				<form:input path="shippingAddress.secondaryPhone"/>
				<form:errors path="shippingAddress.secondaryPhone" cssClass="errorInputText"/>
			</td>
			<td>  </td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="contactInfo.email">Email Address</b></label></td>
			<td colspan="2">
				<form:input path="emailAddress"/>
				<form:errors path="emailAddress" cssClass="errorInputText"/>
			</td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
			<td colspan="2">
				<form:input path="shippingAddress.addressLine1" />
				<form:errors path="shippingAddress.addressLine1" cssClass="errorInputText"/>
			</td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
			<td colspan="2">
				<form:input path="shippingAddress.addressLine2" />
				<form:errors path="shippingAddress.addressLine2" cssClass="errorInputText"/>
			</td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="city">City:</b></label></td>
			<td colspan="2"> 
				<form:input path="shippingAddress.city" />
				<form:errors path="shippingAddress.city" cssClass="errorInputText"/> 
			</td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="state">State:</b></label></td>
			<td>
				<form:select path="shippingAddress.state">
					<form:options items="${stateList}" itemValue="abbreviation" itemLabel="name" />
				</form:select>
			</td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="postalCode">Postal Code:</b></label></td>
			<td colspan="2">
				<form:input path="shippingAddress.postalCode" />
				<form:errors path="shippingAddress.postalCode" cssClass="errorInputText"/> 
			</td>
   		<tr>
			<td style="text-align:right"><label for="country">Country:</b></label></td>
			<td>
				<form:select path="shippingAddress.country">
					<form:options items="${countryList}" itemValue="abbreviation" itemLabel="name" />
				</form:select>
			</td>
   		</tr>
	</table> 