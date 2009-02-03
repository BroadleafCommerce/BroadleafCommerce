<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
		<form:form method="post" commandName="category">
				<br />
				<h4 class="formSectionHeader">Create Category</h4>
				<spring:hasBindErrors name="category">
					  <spring:bind path="category.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>

			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="categoryName">Name:</b></label></td>
					<td><input type="text" size="30" class="categoryField" name="name" id="name" value="${category.name}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="categoryUrlKey">UrlKey:</b></label></td>
					<td><input type="text" size="30" class="categoryField" name="urlKey" id="urlKey" value="${category.urlKey}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="categoryUrl">Url:</b></label></td>
					<td><input type="text" size="30" class="categoryField" name="url" id="url" value="${category.url}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="categoryParentId">Parent Category ID:</b></label></td>
					<td><input size="30" class="categoryField" type="text" name="parentId" id="parentId" value="${category.parentId}" /></td>
	    		</tr>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>

	<a href="<c:url value="/logout"/>">Logout</a>

	</tiles:putAttribute>
</tiles:insertDefinition>