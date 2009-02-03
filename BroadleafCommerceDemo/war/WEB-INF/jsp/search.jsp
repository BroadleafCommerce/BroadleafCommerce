<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

<form:form method="post" commandName="search">

	<table class="formTable">
		<tr>
			<td style="text-align:right"><label for="searchQuery">Search:</b></label></td>
			<td><input type="text" size="30" class="searchQuery" name="queryString" id="queryString" value="${queryString}" /></td>
   		</tr>
  	</table>
  	<div class="formButtonFooter">
		<input type="submit" value="Search"/>
	</div>
</form:form>

<br/>
<h2>
	Search Results
</h2>
<table border="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Price</th>
	</tr>
	<c:forEach var="item" items="${sellableItems}" varStatus="status">
		<tr>
			<td><c:out value="${item.id}"/></td>
			<td><c:out value="${item.name}"/></td>
			<td><c:out value="${item.price}"/></td>
		</tr>
	</c:forEach>
</table>

	</tiles:putAttribute>
</tiles:insertDefinition>
