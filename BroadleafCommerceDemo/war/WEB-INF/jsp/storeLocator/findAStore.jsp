<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">

	<form:form method="post" action="findStores.htm" commandName="findAStoreForm" >
		<h2> Store Locator </h2>
		Postal Code: <form:input path="postalCode" /> <br/>
		<!--  OR <br/>
	    Address 1: <form:input path="addressLine1" /> <br/>
		Address 2: <form:input path="addressLine2" /> <br/>
		City: <form:input path="city" /> <br/>
		State: <form:select path="state.abbreviation">
					<form:options items="${stateList}" itemValue="abbreviation" itemLabel="name" />
		</form:select> <br/>
		Country: <form:select path="country.abbreviation">
			<form:options items="${countryList}" itemValue="abbreviation" itemLabel="name" />
		</form:select> <br/> -->
		Radius: <form:input path="distance" /> <br/>
		<input type="submit" name="Find" value="Find"/>
	</form:form>
	
	<br/><br/>
	<c:if test="${(findAStoreForm.storeDistanceMap != null) && !(empty findAStoreForm.storeDistanceMap)}" >
		<h3> Search Results </h3>
		<table border="0">
			<tr>
				<td> Name </td>
				<td> Address </td>
				<td> Driving Distance </td>
			</tr> 
			<c:forEach var="entry" items="${findAStoreForm.storeDistanceMap}" varStatus="status">
				<tr>
					<td>${entry.key.name } </td>
					<td>
						${entry.key.address1}
						<c:if test="${(entry.key.address2 != null) || !(empty entry.key.address2)}" >
							${entry.key.address2 }<br/>
						</c:if>
						${entry.key.city}, ${entry.key.state},${entry.key.zip}
					</td>
					<td>${entry.value} miles</td>
				</tr>
			</c:forEach> 
		</table>
	</c:if>

	</tiles:putAttribute>

</tiles:insertDefinition>