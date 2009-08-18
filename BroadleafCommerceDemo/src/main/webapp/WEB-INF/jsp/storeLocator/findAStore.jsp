<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	<h3 class="pageTitle">Find a Store Near You</h3>

	<div class="span-24">
		<form:form method="post" action="findStores.htm" commandName="findAStoreForm">
			<div class="columns">
				<div class="orderBorder column span-6" style="margin-top:0px;">
					<div class="orderTitle"><b>Address Information</b></div>
					<table>
						<tr>
							<td>Zip code</td>
							<td><form:input path="postalCode" size="5"/></td>
						</tr>
						<tr>
							<td>Distance (miles)</td>
							<td><form:input path="distance" size="3" /></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td><input type="submit" name="Find" value="Find"/></td>
						</tr>
					</table>
				</div>
				
				<div class="column span-17">
					<c:choose>
						<c:when test="${errorMessage != null}">
							<h3 style="margin:5px 0 10px;color:#6bb3a8;">Store Locations</h3>
							<span>${errorMessage}</span>
						</c:when>
						<c:when test="${!(empty findAStoreForm.storeDistanceMap)}">
							<h3 style="margin:5px 0 10px;color:#6bb3a8;">Store Locations</h3>
							<table class="basicTable">
								<thead>
									<tr>
										<th>Name</th>
										<th>Address</th>
										<th>Driving Distance</th>
									</tr>
								</thead> 
								<c:forEach var="entry" items="${findAStoreForm.storeDistanceMap}" varStatus="status">
									<tr>
										<td>${entry.key.name}</td>
										<td>
											${entry.key.address1}
											<c:if test="${(entry.key.address2 != null) || !(empty entry.key.address2)}" >
												${entry.key.address2 }<br/>
											</c:if>
											${entry.key.city}, ${entry.key.state}, ${entry.key.zip}
										</td>
										<td><fmt:formatNumber value="${entry.value}" maxFractionDigits="2" /> miles</td>
									</tr>
								</c:forEach> 
							</table>
						</c:when>
					</c:choose>
				</div>
				
			</div>
		</form:form>
	</div>
	
	</tiles:putAttribute>
</tiles:insertDefinition>