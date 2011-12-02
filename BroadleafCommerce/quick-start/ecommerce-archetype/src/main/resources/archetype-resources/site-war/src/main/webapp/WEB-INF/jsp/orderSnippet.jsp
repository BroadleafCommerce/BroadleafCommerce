#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
	<div class="orderBorder columns span-9">
		<div class="orderTitle" > <b>Order Summary </b></div>
		<div class="column span-4">
			Order Number: <br/>
			Order Placed: <br/> <br/>
			Subtotal: <br/>
			Total Shipping: <br/>
			Total Tax: <br/>
			<label> Order Total: </label>  
		</div>
		<div class="column ">
			${symbol_dollar}{order.orderNumber} <br/>
			<fmt:formatDate type="date" dateStyle="full" value="${symbol_dollar}{order.submitDate}" /> <br/><br/>
			${symbol_dollar}${symbol_dollar}{order.subTotal} <br/>
			${symbol_dollar}${symbol_dollar}{order.totalShipping} <br/>
			${symbol_dollar}${symbol_dollar}{order.totalTax} <br/>
			<b>${symbol_dollar}${symbol_dollar}{order.total}</b>
		</div>
	</div>

	<div class="orderBorder columns span-12">
		<div class="orderTitle" > <b>Billing Information </b></div>
		<c:forEach var="info" items="${symbol_dollar}{order.paymentInfos}" varStatus="status">
			<div class="column span-7">
				<span class="small"> <b>Billing Address ${symbol_pound}${symbol_dollar}{status.index+1 }: </b> </span>  <br/>
				${symbol_dollar}{info.address.firstName } &nbsp; ${symbol_dollar}{info.address.lastName} <br/>
				${symbol_dollar}{info.address.addressLine1} <br/>
				<c:if test="${symbol_dollar}{info.address.addressLine2 != null && !(empty info.address.addressLine2)}" >		
					${symbol_dollar}{info.address.addressLine2} <br/>
				</c:if>
				${symbol_dollar}{info.address.city}, &nbsp; ${symbol_dollar}{info.address.state.name}, &nbsp; ${symbol_dollar}{info.address.postalCode} <br/>
				${symbol_dollar}{info.address.country.name} <br/>
			</div>
			<div class="column span-5">
				<label> Payment Total: </label> ${symbol_dollar}${symbol_dollar}{info.amount } <br/>
				<c:if test="${symbol_dollar}{info.type.type == 'CREDIT_CARD' }" >
					<label> Payment Type: </label> CREDIT CARD <br/>
				</c:if>
			</div>
		</c:forEach>
	</div>
	
	<c:forEach var="fg" items="${symbol_dollar}{order.fulfillmentGroups}" varStatus="status">
		<div class="orderBorder columns span-12">
			<div class="orderTitle" > <b>Shipment ${symbol_pound}${symbol_dollar}{status.index+1} Information </b></div>
			<div>
				<span class="small"> <b>Ship To:</b> </span>  <br/>
				${symbol_dollar}{fg.address.firstName } &nbsp; ${symbol_dollar}{fg.address.lastName } <br/>
				${symbol_dollar}{fg.address.addressLine1} <br/>
				<c:if test="${symbol_dollar}{fg.address.addressLine2 != null && !(empty fg.address.addressLine2)}" >	
					${symbol_dollar}{fg.address.addressLine2} <br/>
				</c:if>
				${symbol_dollar}{fg.address.city}, &nbsp; ${symbol_dollar}{fg.address.state.name}, &nbsp; ${symbol_dollar}{fg.address.postalCode} <br/>
				${symbol_dollar}{info.address.country.name} <br/>
			</div>
			<table class="cartTable" border="1">
				<tr>
					<th>Item Name </th>
					<th class="alignCenter">Quantity </th>
					<th class="alignCenter">Unit Price</th>
					<th class="alignCenter" >Total Price</th>
				</tr>
				<c:forEach var="fgi" items="${symbol_dollar}{fg.fulfillmentGroupItems}">
					<tr>
						<td><c:out value="${symbol_dollar}{fgi.orderItem.sku.name}"/></td>
						<td class="alignCenter">${symbol_dollar}{fgi.orderItem.quantity}</td>
						<td class="alignCenter">${symbol_dollar}{fgi.orderItem.price}</td>		
						<td class="alignCenter">${symbol_dollar}{fgi.orderItem.quantity * fgi.orderItem.price.amount} </td>
					</tr>
				</c:forEach>
				<tr> <th colspan="4"> </tr>
			</table>
			<div class="columns span-5" style="float:right;">
				<div class="column alignRight" >
					<label>Subtotal:</label> <br/>
					<label> Shipping: </label> <br/>
					<label> Shipment Total: </label> <br/>
				</div>
				<div class="column" >
					${symbol_dollar}${symbol_dollar}{fg.merchandiseTotal} <br/>
					${symbol_dollar}${symbol_dollar}{fg.shippingPrice} <br/>
					${symbol_dollar}${symbol_dollar}{fg.total} <br/>
				</div>
			</div>
		</div>
	</c:forEach>
	<div style="clear:both"> </div>