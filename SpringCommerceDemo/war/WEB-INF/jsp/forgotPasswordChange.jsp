<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div id="banner">
	<hr/>
			<form:form method="post" commandName="passwordChange">
				<br />
				<h4 class="formSectionHeader">Change Password</h4>
			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="j_password">Choose a <b>new password:</b></label></td>
					<td><input size="30" class="loginField" type="password" name="newPassword" id="newPassword" value="${passwordChange.newPassword}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="j_username">Confirm your <b>new password:</b></label></td>
					<td><input size="30" class="loginField" type="password" name="newPasswordConfirm" id="newPasswordConfirm" value="${passwordChange.newPasswordConfirm}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
    			<input type="hidden" name="email" value="${email}" />
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>
</div>
