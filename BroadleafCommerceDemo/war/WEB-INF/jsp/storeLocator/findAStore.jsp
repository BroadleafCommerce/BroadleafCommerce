<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	<h3 class="pageTitle" ><b>Store Locator</b></h3>

	<form:form method="post" action="findStores.htm" commandName="findAStoreForm" >
		<div class="column span-24" style="margin-top:0px;" >
			<div class="column span-3" style="height:150px; line-height:25px;">
				<b>Postal Code:</b> <br/><br/>
				<b>Radius (miles):</b> <br/> <br/>
				<input type="submit" name="Find" value="Find"/>
			</div>
			<div class="column span-5" >
				<form:input path="postalCode" /> <br/><br/>
				<form:input path="distance" /> 
			</div>
		</div>
	</form:form>

	<c:if test="${(findAStoreForm.storeDistanceMap != null) && !(empty findAStoreForm.storeDistanceMap)}" >
		<div>
			<h4 style="margin-top: 10px;" ><b>Search Results </b></h4>
			<table class="cartTable">
				<thead>
					<tr>
						<th class="alignCenter"> Name </th>
						<th class="alignCenter"> Address </th>
						<th class="alignCenter"> Driving Distance </th>
					</tr>
				</thead> 
				<c:forEach var="entry" items="${findAStoreForm.storeDistanceMap}" varStatus="status">
					<tr>
						<td class="alignCenter">${entry.key.name } </td>
						<td class="alignCenter">
							${entry.key.address1}
							<c:if test="${(entry.key.address2 != null) || !(empty entry.key.address2)}" >
								${entry.key.address2 }<br/>
							</c:if>
							${entry.key.city}, ${entry.key.state}, ${entry.key.zip}
						</td>
						<td class="alignCenter"><fmt:formatNumber value="${entry.value}" maxFractionDigits="2" /> miles</td>
					</tr>
				</c:forEach> 
			</table>
		</div>
	</c:if>

	</tiles:putAttribute>

</tiles:insertDefinition>