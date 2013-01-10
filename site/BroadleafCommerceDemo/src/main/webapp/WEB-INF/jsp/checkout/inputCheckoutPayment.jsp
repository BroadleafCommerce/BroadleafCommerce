<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="span-11 column">
    <div class="orderBorder">
        <div class="orderTitle"><b>Payment Information</b></div>
        <span class="small"><b>* Required Fields</b></span> <br/>
        <div style="margin-top:10px; font-size:13px;">
            <form:checkbox path="isSameAddress" id="sameShippingInfo"  /> 
            <span style="padding-left:4px"> Yes, my <b>Billing</b> and <b>Shipping</b> addresses are the same </span>
        </div>
        <div class="errorInputText" style="margin-top:10px;">
            <form:errors path="billingAddress.firstName" >  
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="billingAddress.lastName" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="billingAddress.primaryPhone" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach>             
            </form:errors>
            <form:errors path="billingAddress.addressLine1" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="billingAddress.addressLine2" > 
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="billingAddress.city" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
             </form:errors>
            <form:errors path="billingAddress.postalCode" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="emailAddress" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
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
            <form:input path="billingAddress.addressLine1" size="50" />
        </div>
        <div>
            <label for="addressLine2">Address Line 2</b></label> <br/>
            <form:input path="billingAddress.addressLine2"  size="50"/>
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
            <label style="margin-left: 70px" for="contactInfo.email">* Email Address</b></label> 
        </div>
        <div>
            <form:input path="billingAddress.primaryPhone"/>    
            <span style="margin-left: 20px" ><form:input path="emailAddress"/></span>
        </div>
    </div>

    <div class="span-10 creditCardPayment orderBorder">
        <div class="orderTitle" > <b> Credit Card Information </b></div>
        <span class="small"><b>* Required Fields</b></span> <br/>
        <div class="errorInputText" style="margin-top:10px;">
            <form:errors path="creditCardNumber" > 
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="creditCardExpMonth" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="creditCardExpYear" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
            <form:errors path="creditCardCvvCode" >
                <c:forEach items="${messages}" var="message"> ${message} <br/> </c:forEach> 
            </form:errors>
        </div>
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