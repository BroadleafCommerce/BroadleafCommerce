<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    
        <form:form method="post" commandName="checkout">
        <h4>For USPS test server, only certain addresses would work.  Rest will throw an error. Please make sure you check addressVerification.txt file</h4>
                <h4 class="formSectionHeader">Enter Shipping Address</h4>
                <spring:hasBindErrors name="checkout">
                      <spring:bind path="checkout.*">
                        <c:forEach var="error" items="${status.errorMessages}">
                            <tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
                        </c:forEach>
                     </spring:bind>
                </spring:hasBindErrors>
                
            <c:forEach var="item" items="${checkout.addressList}" varStatus="status2">
                    <form:radiobutton id="selectedShippingAddressId" path="selectedShippingAddressId" value="${item.id}" />
                AddressLine1:
                        <c:out value="${item.addressLine1}"/><br/>
                AddressLine2: <c:out value="${item.addressLine2}"/><br/>
                City: <c:out value="${item.city}"/><br/>
                State: <c:out value="${item.stateCode}"/><br/>
                ZipCode: <c:out value="${item.zipCode}"/><br/>
                <br/>
            </c:forEach>     
                
            <table class="formTable">
                <tr>
                    <td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
                    <td>
                        <spring:bind path="orderShipping.address.addressLine1">                     
                            <input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
                        </spring:bind>
                    </td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
                    <td>
                        <spring:bind path="orderShipping.address.addressLine2">                     
                            <input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
                        </spring:bind>

                    </td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="city">City:</b></label></td>
                    <td>
                        <spring:bind path="orderShipping.address.city">                     
                            <input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
                        </spring:bind>
                    </td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="state">State:</b></label></td>
                    <td>
                        <spring:bind path="orderShipping.address.stateCode">                        
                            <input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
                        </spring:bind>                  
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
                    <td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
                    <td>
                        <spring:bind path="orderShipping.address.zipCode">                      
                            <input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
                        </spring:bind>                  
                    </td>
                </tr>
            </table>
            <div class="formButtonFooter personFormButtons">
          <input type="Reset">                                       
          <input type="submit" value="Cancel" name="_cancel">           
          <input type="submit" value="Previous" name="_target0">
          <input type="submit" value="Next" name="_target2">     
          </div>
    </form:form>
    
    </tiles:putAttribute>
</tiles:insertDefinition>