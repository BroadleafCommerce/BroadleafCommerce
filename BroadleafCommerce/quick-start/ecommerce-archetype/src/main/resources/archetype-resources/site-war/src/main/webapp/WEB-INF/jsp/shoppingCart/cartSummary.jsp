#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
	<div class="mainContentAreaFull" style="padding:8px;">
		<h3 class="pageTitle">Your Shopping Cart</h3>
		<c:choose>
		    <c:when test="${symbol_dollar}{!empty currentCartOrder.orderItems}">
			    <table class="cartTable">
			 	  <thead>
			          <tr valign="bottom">
			          	<th width="80">&nbsp;</th>
			            <th>&nbsp;</th>
			            <th style="text-align:center;">Quantity</th>
			            <th style="text-align:right;padding-right:12px" width="150">Price</th>
						<th style="text-align:right;padding-right:3px" width="100">Discount</th>
			            <th style="text-align:right;padding-right:14px" width="70">Total</th>
			          </tr>
		          </thead>
		          <tbody>
				  	<form:form modelAttribute="cartSummary" method="POST" name="cartForm">
					<form:errors path="*" cssClass="errorText"/>
					<c:forEach items="${symbol_dollar}{currentCartOrder.orderItems}" var="orderItem" varStatus="status">		
				    	<c:set var="item" value="${symbol_dollar}{orderItem.sku}"/>
						<c:set var="product" value="${symbol_dollar}{item.allParentProducts[0]}"/>
						<c:url var="itemUrl" value="/${symbol_dollar}{product.defaultCategory.generatedUrl}">
							<c:param name="productId" value="${symbol_dollar}{product.id}"/>
						</c:url>
		          		<tr class="product">
		          			<td>&nbsp;</td>
					  		<td class="item">
								<p class="description">
									${symbol_dollar}{item.name}
								</p>
                      		</td>
			  		  		<td style="text-align:center;">
			              		<form:hidden path="rows[${symbol_dollar}{status.index}].orderItem.id"/>
			              		<form:input cssClass="quantityInput" cssStyle="width:30px" maxlength="3" path="rows[${symbol_dollar}{status.index}].quantity" autocomplete="off"/>
			              		<br/>
			              		<input type="submit" name="updateItemQuantity" id="updateQuantity" value="Update" class="cartButton" />
					  			<br />
					  			<c:url var="removeItemUrl" value="/basket/viewCart.htm">
					  				<c:param name="orderItemId" value="${symbol_dollar}{orderItem.id}"/>
					  				<c:param name="removeItemFromCart" value="true"/>
					  			</c:url>
				  	  			<a class="cartLinkBtn" href="${symbol_dollar}{removeItemUrl}">Remove</a>
					  		</td>
			  		  		<td style="text-align:right;">
				    			<span class="price">
	           						<c:choose>
										<c:when test="${symbol_dollar}{item.salePrice.amount != item.retailPrice.amount}">
											<span class="salePrice"><fmt:formatNumber type="currency" value="${symbol_dollar}{item.salePrice.amount}" /></span>
											<br/><span class="originalPrice">reg&nbsp;<fmt:formatNumber type="currency" value="${symbol_dollar}{item.retailPrice.amount}" /></span>
										</c:when>
										<c:otherwise>
											<fmt:formatNumber type="currency" value="${symbol_dollar}{item.retailPrice.amount}" />
										</c:otherwise>
									</c:choose>
					    		</span>
						    </td>
		  					<td style="text-align:right;">
								<c:choose>
									<c:when test="${symbol_dollar}{orderItem.adjustmentValue.amount > 0}" >
										<span class="price" style="color:red;">(<fmt:formatNumber type="currency" value="${symbol_dollar}{orderItem.adjustmentValue.amount}" />)</span>
									</c:when>
									<c:otherwise>
										<span class="price"> -- &nbsp;&nbsp;&nbsp;</span>					
									</c:otherwise>
								</c:choose>								
					  		</td>
					  		<td style="text-align:right;">
					  		  <span class="price"><fmt:formatNumber type="currency" value="${symbol_dollar}{orderItem.price.amount * orderItem.quantity}" /></span>
					  		</td>
			     		</tr>
					</c:forEach>
					<tr class="totals topLine">
						<td colspan="4">&nbsp;</td>
						<td style="text-align:right">Subtotal:</td>
						<td style="text-align:right"><span class="price">${symbol_dollar}${symbol_dollar}{currentCartOrder.subTotal}</span></td>
					</tr>
					<c:if test="${symbol_dollar}{(currentCartOrder.orderAdjustmentsValue != null)}">
						<tr>
							<td colspan="4">&nbsp;</td>
							<td style="text-align:right">Order Discount:</td>
							<td style="text-align:right"><span class="price" style="color:red;">(${symbol_dollar}${symbol_dollar}{currentCartOrder.orderAdjustmentsValue})</span></td>
						</tr>	   
					</c:if>
					<tr class="totals">
						<td colspan="4" style="text-align:right;">Promo Code:
							<form:input maxlength="10" path="promoCode" autocomplete="off"/>
							<input type="submit" name="updatePromo" value="Add To Order" class="cartButton" />
						</td>
						<td style="text-align:right">Tax:</td>
						<td style="text-align:right"><span class="price">${symbol_dollar}${symbol_dollar}{currentCartOrder.totalTax}</span></td>
					</tr>
					<tr class="totals">
						<td colspan="4" style="text-align:right;">Shipping Method:
							<form:select  id="shipping"  cssClass="shipMethod" path="fulfillmentGroup.method">
								<form:options items="${symbol_dollar}{fulfillmentGroups}" itemValue="method" itemLabel="method" />
							</form:select>
							<input type="hidden" name="updateShipping"  id="shippingButton"/>
						</td>
						<td style="text-align:right">Shipping:</td>
						<td style="text-align:right"><span class="price">${symbol_dollar}${symbol_dollar}{currentCartOrder.totalShipping}</span></td>
					</tr>
					<tr class="totals">
						<td colspan="4">&nbsp;</td>
						<td style="text-align:right">Total:</td>
						<td style="text-align:right"><span class="price">${symbol_dollar}${symbol_dollar}{currentCartOrder.total }</span></td>
					</tr>
					<tr class="totals">
						<td colspan="6" style="text-align:right"><a href="<c:url value="/store" />">Continue Shopping</a><button type="submit" name="checkout" id="checkout" value="Proceed to Checkout">Proceed to Checkout &raquo;</button>
						</td>
					</tr>
			</form:form>
				</tbody>
				</table>
				</c:when>
				<c:otherwise>
					<c:url var="storeUrl" value="/store" />
					<div class="alert" style="line-height: 20px; margin-top: 16px;">
	                	<b>Your shopping cart is currently empty</b><br>
						&bull; &nbsp;  <a href="<c:out value="${symbol_dollar}{storeUrl}" />" class="link">Click here</a>
	                    to shop from our selection of fine coffees and brewing equipment.
	                    <c:if test="${symbol_dollar}{customer.firstName eq null}">
		                    <br>&bull; &nbsp; If you are a registered user,
		                    <a href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/registerCustomer/registerCustomer.htm" class="link">sign in</a>
		                    to retrieve any saved items.
	                    </c:if>
	                </div>
				</c:otherwise>
			</c:choose>
	</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />