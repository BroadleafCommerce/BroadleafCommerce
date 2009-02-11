<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
		<form:form method="post" commandName="product">
				<br />
				<h4 class="formSectionHeader">Create Product</h4>
				<spring:hasBindErrors name="product">
					  <spring:bind path="product.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>

			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="productName">Name:</b></label></td>
					<td><input type="text" size="30" class="productField" name="name" id="name" value="${product.name}" /></td>
	    		</tr>
				<tr>
					<td style="text-align:right"><label for="productDescription">Description:</b></label></td>
					<td><input size="30" class="productField" type="text" name="description" id="description" value="${product.description}" /></td>
	    		</tr>
	    		<c:forEach items="${sku.itemAttributes}" var="attrib">
	    		<tr>
	    			<td>
						<spring:bind path="sku.itemAttributes[${attrib.key}].name">
						  <input  name="<c:out value="${status.expression}"/>"
						    id="<c:out value="${status.expression}"/>"
						    value="<c:out value="${status.value}"/>" />
						  </spring:bind>
				  	</td>
				  	<td>
						<spring:bind path="sku.itemAttributes[${attrib.key}].value">
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
	
	</tiles:putAttribute>
</tiles:insertDefinition>
