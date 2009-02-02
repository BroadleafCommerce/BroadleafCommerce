<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
<div id="banner">
	<span id="greeting">Logged in as <b><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></b></span>
	<br/><br/>
	<a href="<c:url value="/createAddress.htm" />">Create Address</a>
	<a href="<c:url value="/listAddress.htm"/>">List Address</a>
	<a href="<c:url value="/createCatalogItem.htm"/>">Create CatalogItem</a>
	<a href="<c:url value="/listCatalogItem.htm"/>">List CatalogItem</a>
	<a href="<c:url value="/createCategory.htm"/>">Create Category</a>
	<a href="<c:url value="/listCategory.htm"/>">List Category</a>
	<a href="<c:url value="/logout"/>">Logout</a>
	<hr/>
	Password change is required.

			<form:form method="post" commandName="user">
				<h1>My Account</h1>
				<br />
				<h4 class="formSectionHeader">Change Password</h4>
			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="j_username">Enter your <b>current password:</b></label></td>
					<td><input size="30" class="loginField" type="password" name="currentPassword" id="currentPassword" value="${user.currentPassword}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="j_password">Choose a <b>new password:</b></label></td>
					<td><input size="30" class="loginField" type="password" name="newPassword" id="newPassword" value="${user.newPassword}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="j_username">Confirm your <b>new password:</b></label></td>
					<td><input size="30" class="loginField" type="password" name="newPasswordConfirm" id="newPasswordConfirm" value="${user.newPasswordConfirm}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>
</div>

	</tiles:putAttribute>
</tiles:insertDefinition>
