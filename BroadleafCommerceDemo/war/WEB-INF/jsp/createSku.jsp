<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
		<form:form method="post" commandName="sku">
				<br />
				<h4 class="formSectionHeader">Create sku</h4>
				<spring:hasBindErrors name="sku">
					  <spring:bind path="sku.*">
              			<c:forEach var="error" items="${status.errorMessages}">
                			<tr><td><font color="red"><c:out value="${error}"/></font></td></tr><br />
              			</c:forEach>
              		 </spring:bind>
            	</spring:hasBindErrors>

			<table class="formTable">
				<tr>
					<td style="text-align:right"><label for="productName">Name:</b></label></td>
					<td><c:out value="${sku.product.name}" /></td>
				</tr>
				<tr>
					<td style="text-align:right">Price:</td>
					<td><input type="text" size="10" class="productField" name="price" id="price" value="${sku.price}"/></td>
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