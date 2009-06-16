<%@ include file="/WEB-INF/jsp/include.jsp" %>
	
	<h3 style="margin-bottom:10px;">Your Shopping Cart (<c:out value="${fn:length(checkoutForm.order.orderItems)}" />)</h3>
	<table>
		<thead>
			<tr valign="bottom">
				<th align="left" valign="bottom">Product</th>
	         	<th align="center" valign="bottom">Quantity</th>
	         	<th align="right" valign="bottom">Price</th>
	         	<th align="right" valign="bottom">Total</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${checkoutForm.order.orderItems}" var="orderItem" varStatus="status">
		    <c:set var="item" value="${orderItem.sku}"/>
			<c:url var="itemUrl" value="/${orderItem.category.generatedUrl}">
				<c:param name="productId" value="${orderItem.product.id}"/>
			</c:url>
				<tr valign="top">
					<td class="item">
							<a href="${itemUrl}">
			      				imageUrl
							</a>
							<p class="description">
								<a href="${itemUrl}">${item.name}</a>
							</p>
					</td>
		  		  	<td align="center" style="padding: 8px 0px 0px 8px;">
						<c:out value="${orderItem.quantity}" /><br/>
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
				    <span class="price"><fmt:formatNumber type="currency" value="${orderItem.price.amount}" /></span>
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
		                  <td width="70"><span class="price"><c:out value="${checkoutForm.order.subTotal}" /></span></td>
		                </tr>
						<tr valign="top" align="right">
		                  <td width="20">&nbsp;</td>
		                  <td width="200">Tax:</td>
		                  <td width="70"><span class="price"><c:out value="${checkoutForm.order.totalTax}" /></span></td>
		                </tr>
				        <tr align="right">
				          <td width="20">&nbsp;</td>
		                  <td width="200">Shipping:</td>
				          <td><span class="price"><c:out value="${checkoutForm.order.totalShipping}" /></span></td>
				        </tr>
			           
       				    <tr align="right">
		                  <td></td>
		                  <td>Total:</td>
		                  <td>
		                    <span class="price"><c:out value="${checkoutForm.order.total}" /></span>
		                  </td>

		                </tr>
		              </table>
		            </td>
		          </tr>
      			  <tr><td colspan="3"><img src="/images/etc/dot_clear.gif" width="1" height="24"></td></tr>
     			  <tr><td colspan="3" style="padding:0;"><img src="/images/etc/dot_clear.gif" width="1" height="24"></td></tr>
  			 </table>

            </td>
          </tr>

          <tr><td colspan="2"><img src="/images/etc/dot_clear.gif" width="1" height="40"></td></tr>
        </table>
	</td>
	</tr>
	</tbody>
	</table>
