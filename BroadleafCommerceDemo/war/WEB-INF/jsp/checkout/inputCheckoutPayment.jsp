<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="span-11 column">
	<div class="checkoutBorder">
		<div class="checkoutTitle" > Payment Information </div>
		<span class="small"><b>* Required Fields</b></span> <br/>
		<div>
			<form:errors path="billingAddress.firstName" cssClass="errorInputText"><br/>
			</form:errors>	
			<form:errors path="billingAddress.lastName" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="billingAddress.primaryPhone" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="creditCardNumber" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="creditCardExpMonth" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="creditCardExpYear" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="creditCardCvvCode" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="billingAddress.addressLine1" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="billingAddress.addressLine2" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="billingAddress.city" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="billingAddress.postalCode" cssClass="errorInputText"><br/> 
			</form:errors>
		</div>
		<div>
			<br/>
			<label for="contactInfo.firstName"><b>* First Name</b></label>
			<label style="margin-left:85px;" for="contactInfo.lastName"><b>* Last Name</b></label>
		</div>
		<div>
			<form:input path="billingAddress.firstName"/> 
			<span style="margin-left: 20px;"><form:input path="billingAddress.lastName"/> </span>
		</div>
		<div>
			<label for="addressLine1">* Address Line 1</b></label> <br/>
			<form:input path="billingAddress.addressLine1" size="65" />
		</div>
		<div>
			<label for="addressLine2">Address Line 2</b></label> <br/>
			<form:input path="billingAddress.addressLine2"  size="65"/>
		</div>
		<div>
			<label for="city">* City</b></label> <br/>
			<form:input path="billingAddress.city" />
		</div>
		<div>
			<label for="state">* State</b></label><br/>
			<form:select path="billingAddress.state.abbreviation">
				<form:options items="${stateList}" itemValue="abbreviation" itemLabel="name" />
			</form:select>
		</div>
		<div>
			<label for="postalCode">* Postal Code</b></label>
			<label style="margin-left:85px;" for="country">Country</b></label>
		</div>
		<div>
			<form:input path="billingAddress.postalCode" />
			<span style="margin-left: 20px;" >
				<form:select path="billingAddress.country.abbreviation">
					<form:options items="${countryList}" itemValue="abbreviation" itemLabel="name" />
				</form:select> 
			</span>
		</div>
		<div>
			<label for="contactInfo.primaryPhone"><b>* Phone Number</b></label>
			<label style="margin-left: 70px" for="contactInfo.email">Email Address</b></label> 
		</div>
		<div>
			<form:input path="billingAddress.primaryPhone"/>	
			<span style="margin-left: 20px" ><form:input path="emailAddress"/></span>
		</div>
		<div>
			<form:checkbox path="isSameAddress" id="sameShippingInfo"  /> 
			<span style="padding-left:4px"> Yes, my <b>Billing</b> and <b>Shipping</b> addresses are the same </span>
		</div>
	</div>

	<div class="span-10 creditCardPayment checkoutBorder">
		<div class="checkoutTitle" > Credit Card Information </div>
		<span class="small"><b>* Required Fields</b></span> <br/>
		<div> 
			<br/>
			<label for="ccNumber"><b>* Card Type</b></label><br/>
			<form:select path="selectedCreditCardType">
				<form:options items="${checkoutForm.approvedCreditCardTypes}" itemValue="type" itemLabel="type" />
			</form:select>
		</div>
		<div>
			<label for="creditCardNumber"><b>* Credit Card Number</b></label>
			<label style="margin-left:50px;" for="ccNumber"><b>* Expiration Date</b></label>
		</div>
		<div>
			<form:input maxlength="16" size="16" path="creditCardNumber" />
			<span style="margin-left:50px;"> 
				<form:input maxlength="2" size="2" path="creditCardExpMonth" /> / <form:input maxlength="4" size="4" path="creditCardExpYear" />
			</span>
		</div>
		<div>
			<label for="cvv"><b>* CVV Code</b></label>
		</div>
		<div>
			<form:input maxlength="4" size="4" path="creditCardCvvCode" />
		</div>
	</div>
</div>