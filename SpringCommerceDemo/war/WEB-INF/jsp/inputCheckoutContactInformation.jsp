<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
		<form:form method="post" commandName="checkout">
				
			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="ccNumber"><b>Primary Phone Number</b></label></td>
					<td>
						<spring:bind path="contactInfo.primaryPhone">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine1">Secondary Phone Number</b></label></td>
					<td>
						<spring:bind path="contactInfo.secondaryPhone">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="addressLine2">Email Address</b></label></td>
					<td>
						<spring:bind path="contactInfo.email">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>

					</td>
	    		</tr>
	    		<tr>
					<td style="text-align:right"><label for="city">Fax Number</b></label></td>
					<td>
						<spring:bind path="contactInfo.fax">						
							<input size="30" class="addressField" type="text" name="${status.expression }" value="${status.value}" />
						</spring:bind>
					</td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
          <input type="Reset">                                       
          <input type="submit" value="Cancel" name="_cancel">			
          <input type="submit" value="Next" name="_target1">     
          </div>
			</form:form>