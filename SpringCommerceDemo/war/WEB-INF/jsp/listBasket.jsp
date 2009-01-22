<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
				<h1>Your Basket </h1>

	<form:form method="post" action="updateQuantity.htm" commandName="basket">				
	<table border="1">
	<tr>
		<th>Name</th>
		<th>Description</th>
		<th>Price</th>
		<th>Quantity</th>
		<th>Total Line Price</th>
		<th>Actions</th>
	</tr>	
	<c:forEach var="item" items="${basket.items}" varStatus="myRow">
		<tr>
			<td><c:out value="${item.sellableItem.catalogItem.name}"/></td>
			<td><c:out value="${item.sellableItem.catalogItem.description}"/></td>
			<td><c:out value="${item.sellableItem.price}"/></td>
			<td>
				<spring:bind path="basket.items[${myRow.index}].quantity">
			      <input type="text" name="${status.expression}" value="${status.value}"/>
			    </spring:bind>    
			</td>
			<td><c:out value="${item.finalPrice}"/></td>
			<td><a href="<c:url value="/basket/removeItem.htm"><c:param name="sellableItemId" value="${item.id}"/></c:url>">Remove Item(s)</a></td>
		</tr>
	</c:forEach>
	</table>
	<table border="1">
		<tr>
			<th>Order Total</th>
			<th><c:out value="${basket.order.orderTotal}"/>
		</tr>
		<tr>
			<th></th>
			<th><a href="<c:url value="/checkout/checkout.htm"/>">Checkout</a></th>
		</tr>
	</table>
		<input type="submit" class="saveButton" value="Update Quantities" />
	</form:form>	
	<a href="<c:url value="/logout"/>">Logout</a>
</div>