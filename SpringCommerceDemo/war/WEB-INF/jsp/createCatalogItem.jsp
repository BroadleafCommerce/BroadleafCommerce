<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
		<form:form method="post" commandName="catalogItem">
				<br />
				<h4 class="formSectionHeader">Create CatalogItem</h4>
				<spring:hasBindErrors name="catalogItem">
					  <spring:bind path="catalogItem.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>

			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="catalogItemName">Name:</b></label></td>
					<td><input type="text" size="30" class="catalogItemField" name="name" id="name" value="${catalogItem.name}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="catalogItemDescription">Description:</b></label></td>
					<td><input size="30" class="catalogItemField" type="text" name="description" id="description" value="${catalogItem.description}" /></td>
	    		</tr>
	    		<c:forEach items="${sellableItem.itemAttributes}" var="attrib">
	    		<tr>
	    			<td>
						<spring:bind path="sellableItem.itemAttributes[${attrib.key}].name">
						  <input  name="<c:out value="${status.expression}"/>"
						    id="<c:out value="${status.expression}"/>"
						    value="<c:out value="${status.value}"/>" />
						  </spring:bind>
				  	</td>
				  	<td>
						<spring:bind path="sellableItem.itemAttributes[${attrib.key}].value">
						  <input  name="<c:out value="${status.expression}"/>"
						    id="<c:out value="${status.expression}"/>"
						    value="<c:out value="${status.value}"/>" />
						</spring:bind>
				  	</td>
				</tr>
				</c:forEach>
    		</table>
    		<div class="formButtonFooter personFormButtons">
				<input type="submit" class="saveButton" value="Save Changes"/>
			</div>
			</form:form>

	<a href="<c:url value="/logout"/>">Logout</a>
</div>