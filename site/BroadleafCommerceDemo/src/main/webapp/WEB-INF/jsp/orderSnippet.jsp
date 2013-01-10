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
            ${order.orderNumber} <br/>
            <fmt:formatDate type="date" dateStyle="full" value="${order.submitDate}" /> <br/><br/>
            $${order.subTotal} <br/>
            $${order.totalShipping} <br/>
            $${order.totalTax} <br/>
            <b>$${order.total}</b>
        </div>
    </div>

    <div class="orderBorder columns span-12">
        <div class="orderTitle" > <b>Billing Information </b></div>
        <c:forEach var="info" items="${order.paymentInfos}" varStatus="status">
            <div class="column span-7">
                <span class="small"> <b>Billing Address #${status.index+1 }: </b> </span>  <br/>
                ${info.address.firstName } &nbsp; ${info.address.lastName} <br/>
                ${info.address.addressLine1} <br/>
                <c:if test="${info.address.addressLine2 != null && !(empty info.address.addressLine2)}" >       
                    ${info.address.addressLine2} <br/>
                </c:if>
                ${info.address.city}, &nbsp; ${info.address.state.name}, &nbsp; ${info.address.postalCode} <br/>
                ${info.address.country.name} <br/>
            </div>
            <div class="column span-5">
                <label> Payment Total: </label> $${info.amount } <br/>
                <c:if test="${info.type.type == 'CREDIT_CARD' }" >
                    <label> Payment Type: </label> CREDIT CARD <br/>
                </c:if>
            </div>
        </c:forEach>
    </div>
    
    <c:forEach var="fg" items="${order.fulfillmentGroups}" varStatus="status">
        <div class="orderBorder columns span-12">
            <div class="orderTitle" > <b>Shipment #${status.index+1} Information </b></div>
            <div>
                <span class="small"> <b>Ship To:</b> </span>  <br/>
                ${fg.address.firstName } &nbsp; ${fg.address.lastName } <br/>
                ${fg.address.addressLine1} <br/>
                <c:if test="${fg.address.addressLine2 != null && !(empty fg.address.addressLine2)}" >   
                    ${fg.address.addressLine2} <br/>
                </c:if>
                ${fg.address.city}, &nbsp; ${fg.address.state.name}, &nbsp; ${fg.address.postalCode} <br/>
                ${info.address.country.name} <br/>
            </div>
            <table class="cartTable" border="1">
                <tr>
                    <th>Item Name </th>
                    <th class="alignCenter">Quantity </th>
                    <th class="alignCenter">Unit Price</th>
                    <th class="alignCenter" >Total Price</th>
                </tr>
                <c:forEach var="fgi" items="${fg.fulfillmentGroupItems}">
                    <tr>
                        <td><c:out value="${fgi.orderItem.sku.name}"/></td>
                        <td class="alignCenter">${fgi.orderItem.quantity}</td>
                        <td class="alignCenter">${fgi.orderItem.price}</td>     
                        <td class="alignCenter">${fgi.orderItem.quantity * fgi.orderItem.price.amount} </td>
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
                    $${fg.merchandiseTotal} <br/>
                    $${fg.shippingPrice} <br/>
                    $${fg.total} <br/>
                </div>
            </div>
        </div>
    </c:forEach>
    <div style="clear:both"> </div>