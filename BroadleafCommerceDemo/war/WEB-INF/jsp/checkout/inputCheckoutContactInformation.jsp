<%@ include file="/WEB-INF/jsp/include.jsp" %>
	<div id="checkoutContactInfo" class="span-11 column checkoutBorder displayNone">
		<div class="checkoutTitle" > Shipping Information </div>
		<span class="small"><b>* Required Fields</b></span> <br/>
		<div>
			<form:errors path="shippingAddress.firstName" cssClass="errorInputText"><br/>
			</form:errors> 
			<form:errors path="shippingAddress.lastName" cssClass="errorInputText"><br/>	
			</form:errors>
			<form:errors path="shippingAddress.primaryPhone" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="shippingAddress.addressLine1" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="shippingAddress.city" cssClass="errorInputText"><br/>
			</form:errors>
			<form:errors path="shippingAddress.postalCode" cssClass="errorInputText" ><br/>
			</form:errors>
			<form:errors path="emailAddress" cssClass="errorInputText"><br/>	
			</form:errors>
		</div>
		<div>
			<br/>
			<label for="contactInfo.firstName"><b>* First Name</b></label>
			<label style="margin-left:85px;" for="contactInfo.lastName"><b>* Last Name</b></label>
		</div>
		<div>
			<form:input path="shippingAddress.firstName"/> 
			<span style="margin-left: 20px;"><form:input path="shippingAddress.lastName"/> </span>
		</div>
		<div>
			<label for="addressLine1">* Address Line 1</b></label> <br/>
			<form:input path="shippingAddress.addressLine1" size="65" />
		</div>
		<div>
			<label for="addressLine2">Address Line2</b></label> <br/>
			<form:input path="shippingAddress.addressLine2"  size="65"/>
		</div>
		<div>
			<label for="city">* City</b></label> <br/>
			<form:input path="shippingAddress.city" />
		</div>
		<div>
			<label for="state">* State</b></label> <br/>
			<form:select path="shippingAddress.state.abbreviation">
				<form:options items="${stateList}" itemValue="abbreviation" itemLabel="name" />
			</form:select>
		</div>
		<div>
			<label for="postalCode">* Postal Code</b></label>
			<label style="margin-left:85px;" for="country">Country</b></label>
		</div>
		<div>
			<form:input path="shippingAddress.postalCode" />
			<span style="margin-left: 20px;" >
				<form:select path="shippingAddress.country.abbreviation">
					<form:options items="${countryList}" itemValue="abbreviation" itemLabel="name" />
				</form:select> 
			</span>
		</div>
		<div>
			<label for="contactInfo.primaryPhone"><b>* Phone Number</b></label><br/>
			<form:input path="shippingAddress.primaryPhone"/>	
		</div>
	</div>