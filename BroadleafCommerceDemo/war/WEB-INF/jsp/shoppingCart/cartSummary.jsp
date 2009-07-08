<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">
	
<%@ include file="/WEB-INF/jsp/include.jsp" %>
		<h1 style="margin-bottom:10px;">Your Shopping Cart</h1>
		<c:choose>
		    <c:when test="${!empty currentCartOrder.orderItems}">
			    <p>Checking out is quick, easy and secure. &nbsp; If you have any questions, please call us at 1-888-BROADLEAF.</p>
			 	<table>
			 	  <thead>
			          <tr valign="bottom">
			            <th align="left" valign="bottom">Product</th>
			            <th class="availability">
			            </th>
			            <th align="center" valign="bottom">Quantity</th>
			            <th align="center" valign="bottom">Remove</th>
			            <th align="right" valign="bottom">Price</th>
			            <th align="right" valign="bottom">Total</th>
			          </tr>
		          </thead>
		          <tbody>
				  	<form:form modelAttribute="cartSummary" action="viewCart.htm" method="POST">
					<form:errors path="*" cssClass="errorText"/>
					<c:forEach items="${currentCartOrder.orderItems}" var="orderItem" varStatus="status">		
				    	<c:set var="item" value="${orderItem.sku}"/>
						<c:set var="product" value="${item.allParentProducts[0]}"/>
						<c:url var="itemUrl" value="/${product.defaultCategory.generatedUrl}">
							<c:param name="productId" value="${product.id}"/>
						</c:url>
		          		<tr valign="top">
					  		<td class="item">
								<a href="${itemUrl}">
									<img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" />
								</a>
								<p class="description">
									<a href="${itemUrl}">${item.name}</a>
								</p>
                      		</td>
			  		  		<td class="availability" />
			  		  		<td align="center" style="padding: 8px 0px 0px 8px;">
			              		<form:hidden path="rows[${status.index}].orderItem.id"/>
			              		<form:input cssClass="quantityInput" cssStyle="width:30px" maxlength="3" path="rows[${status.index}].quantity" autocomplete="off"/>
			              		<br/>
			              		<input type="submit" name="updateItemQuantity" id="updateQuantity" value="Update Quantity" />
					  		</td>
					  		<td align="center">
					  			<c:url var="removeItemUrl" value="/basket/viewCart.htm">
					  				<c:param name="orderItemId" value="${orderItem.id}"/>
					  				<c:param name="removeItemFromCart" value="true"/>
					  			</c:url>
				  	  			<a href="${removeItemUrl}">Remove</a>
					  		</td>
			  		  		<td align="right">
				    			<span class="price">
	           						<c:choose>
										<c:when test="${true}">
											<span class="sale"><fmt:formatNumber type="currency" value="${item.salePrice.amount}" />&nbsp;ea</span>
											<br/>reg&nbsp;<fmt:formatNumber type="currency" value="${item.listPrice.amount}" />
										</c:when>
										<c:otherwise>
											<fmt:formatNumber type="currency" value="${item.listPrice.amount}" />&nbsp;ea
										</c:otherwise>
									</c:choose>
					    		</span>
						    </td>
					  		<td align="right">
					  		  <span class="price"><fmt:formatNumber type="currency" value="${orderItem.price.amount * orderItem.quantity}" /></span>
					  		</td>
			     		</tr>
					</c:forEach>
		        	<tr valign="top">
          			  <td id="cartSummary" colspan="7" style="padding:0;">
			     		   <table id="cartTotals" border="0" width="100%" cellspacing="0" cellpadding="0">
		      			     <tr><td colspan="3" style="padding:0"><img src="/images/etc/dot_clear.gif" width="1" height="6"></td></tr>
		      			       <tr valign="top" align="right">
			        		    <td>
			           		    <table border="0" width="290" cellpadding="3" cellspacing="0">
		              			  <tr valign="top" align="right">
		                 		  	<td width="20">&nbsp;</td>
			                	 	<td width="200">Subtotal:</td>
			                	  	<td width="70"><span class="price"><c:out value="${currentCartOrder.subTotal}" /></span></td>
			                     </tr>
								 <tr valign="top" align="right">
				                    <td width="20">&nbsp;</td>
				                    <td width="200">Tax:</td>
				                    <td width="70"><span class="price"><c:out value="${currentCartOrder.totalTax}" /></span></td>
				                 </tr>
						         <tr align="right">
						           <td style="background: #DDF0F6;">
						             <input type="radio" name="isStorePickup" /></td>
						           <td style="background: #DDF0F6;">I would like to <b style="font-size: 14px;">Ship</b> my order</td>
						           <td style="background: #DDF0F6;">
						             <span class="price"><c:out value="${currentCartOrder.totalShipping}" /></span>
						           </td>
						        </tr>
					            <tr align="right">
					              <td colspan="2" style="background: #EBF7DA; border-top: 2px solid #FFFFFF;">
					                Your order is not eligible for <span style="color: #759d40; font-weight: bold; font-size: 14px;">Pickup</span></td>
					              <td style="background: #EBF7DA; color: #CC0000; font-weight: bold; border-top: 2px solid #FFFFFF;">FREE</td>
					              </td>
					            </tr>
        				   	    <tr><td colspan="3"><img src="/images/etc/dot_clear.gif" width="1" height="6"></td></tr>
				                <tr align="right">
				                  <td></td>
				                  <td>Total:</td>
				                  <td>
				                    <span class="price"><c:out value="${currentCartOrder.total}" /></span>
				                  </td>
				                </tr>
			              </table>
			            </td>
			          </tr>
       			  <tr><td colspan="3"><img src="/images/etc/dot_clear.gif" width="1" height="24"></td></tr>
      			  <tr><td colspan="3" style="padding:0;"><img src="/images/etc/dot_clear.gif" width="1" height="24"></td></tr>
   			 </table>

	        <table id="checkout" border="0" width="100%" cellspacing="0" cellpadding="0">
	          <tr valign="top">
	            <td colspan="2" align="right">
	              <table cellpadding="0" cellspacing="0" width="610">
	                <tr valign="middle">

	                  <td align="right" style="vertical-align: middle;">
	                    <a href="<c:url value="/store" />">Continue Shopping</a>
	                  </td>
	                  <td align="right" width="180">
	                    <a href="<c:url value="/checkout/checkout.htm" />">Checkout</a>
	                  </td>
	                </tr>
	              </table>
	            </td>
	          </tr>
	          <tr><td colspan="2"><img src="/images/etc/dot_clear.gif" width="1" height="40"></td></tr>
	        </table>
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
						&bull; &nbsp;  <a href="<c:out value="${storeUrl}" />" class="link">Click here</a>
	                    to shop from our thousands of space- and time-saving solutions.
	                    <c:if test="${customer.firstName eq null}">
		                    <br>&bull; &nbsp; If you are a registered user,
		                    <a href="/profile/login.htm" class="link">sign in</a>
		                    to retrieve any saved items.
	                    </c:if>
	                </div>
				</c:otherwise>
			</c:choose>
	</tiles:putAttribute>
</tiles:insertDefinition>