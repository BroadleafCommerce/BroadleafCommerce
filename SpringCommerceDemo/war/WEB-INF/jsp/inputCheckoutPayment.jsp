<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
		<form:form method="post" commandName="checkout">
		<h4>For USPS test server, only certain addresses would work.  Rest will throw an error. Please make sure you check addressVerification.txt file</h4>
				<h4 class="formSectionHeader">Enter CC and Billing Address</h4>
				<spring:hasBindErrors name="checkout">
					  <spring:bind path="checkout.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
            	Please select the billing address or enter a new one below:<br/>
            	
            	<c:forEach var="item" items="${checkout.addressList}" varStatus="status2">
 
						<form:radiobutton id="selecterdBillingAddressId" path="selectedBillingAddressId" value="${item.id}" />
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
					<td style="text-align:right"><label for="ccNumber"><b>CC Number:</b></label></td>
					<td>
						<spring:bind path="orderPayment.referenceNumber">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
					<td>
						<spring:bind path="orderPayment.address.addressLine1">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
					<td>
						<spring:bind path="orderPayment.address.addressLine2">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>

					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="city">City:</b></label></td>
					<td>
						<spring:bind path="orderPayment.address.city">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="state">State:</b></label></td>
					<td>
						<spring:bind path="orderPayment.address.stateCode">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>					
					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
					<td>
						<spring:bind path="orderPayment.address.zipCode">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>					
					</td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
          <input type="Reset">                                       
          <input type="submit" value="Cancel" name="_cancel">			
	      <input type="submit" value="Previous" name="_target1">
          <input type="submit" value="Next" name="_target3">     
          </div>
			</form:form>