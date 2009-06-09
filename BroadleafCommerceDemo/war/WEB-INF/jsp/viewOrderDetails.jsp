<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
	<h1>View Order Details</h1>

	Order Number - <c:out value="${order.orderNumber}" /> <br/>
	Order Placed - <fmt:formatDate type="date" dateStyle="full" value="${order.submitDate}" />

	<div>
		<br/>
		<h2>Billing Information</h2>
		<h3> Billing Address </h3>
		<c:forEach var="info" items="${order.paymentInfos}" varStatus="status">
			<c:out value="${info.address.addressLine1}"/> <br/>
			<c:if test="${info.address.addressLine2 != null}" >		
				<c:out value="${info.address.addressLine2}"/> <br/>
			</c:if>
			<c:out value="${info.address.city}"/>, &nbsp;
			<c:out value="${info.address.state.name}"/>, &nbsp; 
			<c:out value="${info.address.postalCode}"/> <br/>
		</c:forEach>
	</div>
	<br/>
	<h2>Shipping Information</h2>
	<c:forEach var="fg" items="${order.fulfillmentGroups}" varStatus="status">
		<br/>
		<h3>Shipping Address #${status.index+1}</h3>
		<c:out value="${fg.address.addressLine1}"/> <br/>
		<c:if test="${fg.address.addressLine2 != null}" >	
			<c:out value="${fg.address.addressLine2}"/> <br/>
		</c:if>
		<c:out value="${fg.address.city}"/>, &nbsp;
		<c:out value="${fg.address.state.name}"/>, &nbsp;
		<c:out value="${fg.address.postalCode}"/> <br/><br/>
		
	<h3> Shipping Group #${status.index+1} Items </h3>
	
	<table border="1">
		<tr>
			<th>Item Name </th>
			<th>Quantity </th>
			<th>Unit Price</th>
			<th>Total Price</th>
		</tr>
		<c:forEach var="fgi" items="${fg.fulfillmentGroupItems}">
			<tr>
				<td><c:out value="${fgi.orderItem.sku.name}"/></td>
				<td><c:out value="${fgi.orderItem.quantity}"/></td>
				<td><c:out value="${fgi.orderItem.price}"/></td>		
				<td><c:out value="${fgi.orderItem.quantity * fgi.orderItem.price.amount}" /> </td>
			</tr>
		</c:forEach>
		<tr/>
		<tr>
			<td> Subtotal </td> 
			<td/> <td/> 
			<td> <c:out value="${fg.merchandiseTotal}"/> </td>
		</tr>
		<tr>
			<td> Shipping </td> 
			<td/> <td/> 
			<td> <c:out value="${fg.shippingPrice}"/> </td>
		</tr>
		<tr>
			<td> Tax </td> 
			<td/> <td/>
			<td> <c:out value="${fg.totalTax}"/> </td>
		</tr>
		<tr>
			<td> Shipping Group Total </td> 
			<td/> <td/>
			<td> <c:out value="${fg.total}"/> </td>
		</tr>
		
	</table>
	</c:forEach>
	
	<br/>
	<h3> Order Totals </h3>
	<table border="1">
		<tr>
			<th> </th>
			<th>Price</th>
		</tr>
		<tr>
			<td> Subtotal </td>
			<td>${order.subTotal} </td>
		</tr>
		<tr>
			<td> Total Shipping</td>
			<td>${order.totalShipping} </td>
		</tr>
		<tr>
			<td> Total Tax</td>
			<td>${order.totalTax} </td>
		</tr>
		<tr>
			<td> Order Total</td>
			<td>${order.total} </td>
		</tr>

	</table>

	</tiles:putAttribute>
</tiles:insertDefinition>