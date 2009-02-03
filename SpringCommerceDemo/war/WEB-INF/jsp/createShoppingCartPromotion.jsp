<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<jsp:include page="snippets/header.jsp" />


<form:form method="post" commandName="shoppingCartPromotion">
	<br />
	<h4 class="formSectionHeader">Create Shopping Cart Promotion</h4>
	<spring:hasBindErrors name="shoppingCartPromotion">
		<spring:bind path="shoppingCartPromotion.*">
			<c:forEach var="error" items="${status.errorMessages}">
				<tr>
					<td><font color="red"><c:out value="${error}" /></font></td>
				</tr>
				<br />
			</c:forEach>
		</spring:bind>
	</spring:hasBindErrors>

	<table class="formTable">
		
		<tr>
			<td>
				Name:
			</td>
			<td>
				<input type="text" name="name" value="${shoppingCartPromotion.name}" />
			</td>
		</tr>
		
		<tr>
			<td	>
				Description:
			</td>
			<td>
				<input type="text" name="description" value="${shoppingCartPromotion.description}" />
			</td>	
		</tr>
		
		<tr>
			<td>
				Category:
			</td>
			<td>
				<input type="text" name="category" value="${shoppingCartPromotion.category}" />
			</td>
		</tr>
		<tr>
			<td>
				Coupon Code:
			</td>
			<td>
				<input type="text" name="couponCode" value="${shoppingCartPromotion.couponCode}" />
			</td>
		</tr>
		<tr>
			<td>
				Per Coupon: 
			</td>
			<td>
				<input type="text" name="couponRedemptionLimit" value="${shoppingCartPromotion.couponRedemptionLimit}" />
			</td>
		</tr>
		
		<tr>
			<td>
				Per Customer:
			</td>
			<td>
				<input type="text" name="customerRedemptionLimit" value="${shoppingCartPromotion.customerRedemptionLimit}"/>
			</td>
		</tr>
		<tr>
			<td>
				Activation Date:
			</td>
			<td>
				<input type="text" name="activationDate" value="${shoppingCartPromotion.activationDate}" />
			</td>
		</tr>
		<tr>
			<td>
				Expiration Date:
			</td>
			<td>
				<input type="text" name="expirationDate" value="${shoppingCartPromotion.expirationDate}" />
			</td>
		</tr>
		<tr>
			<td>
				Priority:
			</td>
			<td>
				<input type="text" name="priority" value="${shoppingCartPromotion.priority}" />
			</td>
		</tr>
	</table>
	
	<div class="formButtonFooter personFormButtons">
		<input type="submit" class="saveButton" value="Save Changes" />
	</div>
	
</form:form>

<a href="<c:url value="/logout"/>">Logout</a>

