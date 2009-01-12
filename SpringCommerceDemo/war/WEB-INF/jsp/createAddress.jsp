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
				<br />
				<h4 class="formSectionHeader">Create Address</h4>
			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="addressName">Address Name:</b></label></td>
					 <form:errors path="addressName" /><br />
					<td><input size="30" class="addressField" type="address" name="addressName" id="addressName" value="${address.addressName}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine1">Address Line1:</b></label></td>
					<form:errors path="addressLine1"/><br />
					<td><input size="30" class="addressField" type="address" name="addressLine1" id="addressLine1" value="${address.addressLine1}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine2">Address Line2:</b></label></td>
					<form:errors path="addressLine2" /><br />
					<td><input size="30" class="addressField" type="address" name="addressLine2" id="addressLine2" value="${address.addressLine2}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="city">City:</b></label></td>
					<form:errors path="city"/><br />
					<td><input size="30" class="addressField" type="address" name="city" id="city" value="${address.city}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="state">State:</b></label></td>
					<form:errors path="state"/><br />
					<td><input size="30" class="addressField" type="address" name="state" id="state" value="${address.state}" /></td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="zipCode">Zip Code:</b></label></td>
					<form:errors path="zipCode"/><br />
					<td><input size="30" class="addressField" type="address" name="zipCode" id="zipCode" value="${address.zipCode}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>

	<a href="<c:url value="/logout"/>">Logout</a>
</div>