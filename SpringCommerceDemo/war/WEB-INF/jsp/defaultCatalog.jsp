<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<span id="greeting">Logged in as <b><security:authentication property="principal.username" /></b></span>
	<c:set var="springCommerceRequestState" value="${requestScope['org.springcommerce.web.SpringCommerceRequestState']}" />
	<hr/>
	<h1>Default Category View For: <c:out value="${springCommerceRequestState.category.name}"/></h1>
	<h2>Here is a list of its sub-categories:</h2>

	<sc:breadcrumb/>
	<br/>
	<table border="1">
	<tr>
		<th>Name</th>
	</tr>
	<c:forEach var="item" items="${subCategories}" varStatus="status">
		<tr>
			<td><sc:categoryLink category="${item}" link="true"/></td>
		</tr>
	</c:forEach>
	</table>

	<h2>Items in this category</h2>
	<sc:list objectList="${category.catalogItems}"
		numAcross="4" objectName="item">
		<sc:item catalogItem="${item}" layout="productList" itemName="item" />
	</sc:list>
	
	</tiles:putAttribute>
</tiles:insertDefinition>