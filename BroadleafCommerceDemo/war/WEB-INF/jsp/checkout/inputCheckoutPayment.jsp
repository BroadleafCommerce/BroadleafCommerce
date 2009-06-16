<%@ include file="/WEB-INF/jsp/include.jsp" %>
	
	<h4 class="formSectionHeader">Payment Information</h4>
	<spring:hasBindErrors name="checkout">
		  <spring:bind path="checkout.*">
           			<c:forEach var="error" items="${status.errorMessages}">
             			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
           			</c:forEach>
           		 </spring:bind>
         	</spring:hasBindErrors>
     		
	<table class="formTable">
		<tr>
			<td style="text-align:right"><label for="ccNumber"><b>CC Number:</b></label></td>
			<td><form:input maxlength="16" size="16" path="creditCardNumber" /></td>
			<td style="text-align:right"><label for="ccNumber"><b>CC Type:</b></label></td>
			<td><form:select path="selectedCreditCardType">
				<c:forEach var="ccType" items="${checkoutForm.approvedCreditCardTypes}">
					<form:option value="${ccType.type}"><c:out value="${ccType.type}" /></form:option>
				</c:forEach>
			</form:select></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="ccNumber"><b>Expiry Month/Year</b></label></td>
			<td><form:input maxlength="2" size="2" path="creditCardExpMonth" />/<form:input maxlength="4" size="4" path="creditCardExpYear" /></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
			<td><form:input path="billingAddress.addressLine1" /></td>
   		</tr>
		<tr>
			<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
			<td><form:input path="billingAddress.addressLine2" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="city">City:</b></label></td>
			<td><form:input path="billingAddress.city" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="state">State:</b></label></td>
			<td><form:input path="billingAddress.state" /></td>
   		</tr>
   		<tr>
			<td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
			<td><form:input path="billingAddress.postalCode" /></td>
   		</tr>
  	</table>
	