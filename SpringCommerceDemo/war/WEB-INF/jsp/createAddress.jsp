<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div id="banner">
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<hr/>
		<form:form method="post" commandName="address">
		<h4>For USPS test server, only certain addresses would work.  Rest will throw an error. Please make sure you check addressVerification.txt file</h4>
				<h4 class="formSectionHeader">Create Address</h4>
				<spring:hasBindErrors name="address">
					  <spring:bind path="address.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>
				
			<table class="formTable">
				<tr>
						<td style="text-align:right"><label for="addressName">Address Name:</b></label></td>
						<td><input size="30" class="addressField" name="addressName" id="addressName" value="${address.addressName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
					<td><input size="30" class="addressField" type="address" name="addressLine1" id="addressLine1" value="${address.addressLine1}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
					<td><input size="30" class="addressField" type="address" name="addressLine2" id="addressLine2" value="${address.addressLine2}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="city">City:</b></label></td>
					<td><input size="30" class="addressField" type="address" name="city" id="city" value="${address.city}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="state">State:</b></label></td>
					<td><input size="30" class="addressField" type="address" name="stateCode" id="stateCode" value="${address.stateCode}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
					<td><input size="30" class="addressField" type="address" name="zipCode" id="zipCode" value="${address.zipCode}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>
	<a href="<c:url value="/listAddress.htm"/>">List Address</a>
	<a href="<c:url value="/passwordChange.htm"/>">Password Change</a>
	<a href="<c:url value="/logout"/>">Logout</a>
</div>